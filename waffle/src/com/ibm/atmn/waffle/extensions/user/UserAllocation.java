package com.ibm.atmn.waffle.extensions.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.utils.CSVHandler;
import com.ibm.atmn.waffle.utils.FileIOHandler;

/**
 * The Class UserAllocation.
 */
public class UserAllocation {

	private static final Logger log = LoggerFactory.getLogger(UserAllocation.class);

	private static final String DEFAULT_PROPERTIES_FILE_PATH = "test_config/extensions/user/default_users.properties";
	
	/** The User map. */
	private static final String STANDARD_USER_GROUP = "standard_users";

	/** The Admin map. */
	private static final String ADMIN_USER_GROUP = "admin_users";
	
	/** The Guest map. */
	private static final String GUEST_USER_GROUP = "guest_users";
	
	/** The Moderator map. */
	private static final String GLOBAL_MODS_GROUP = "global_mods";
	
	/** The externalUserModerator map. */
	private static final String GUEST_MODS_GROUP = "guest_mods";

	private Properties usersProperties;

	private Map<String, UserGroup> userGroups = new HashMap<String, UserGroup>();

	private static volatile Map<String, UserAllocation> allocators = Collections.synchronizedMap(new HashMap<String, UserAllocation>());

	/**
	 * Returns a UserAllocation for a user properties file. If the path has not been used before a new UserAllocator will be created. If their is an existing UserAllocator for this
	 * properties file, it will be returned.
	 * 
	 * @return UserAllocation
	 */
	public static synchronized UserAllocation getUserAllocation(String filePath) {

		if (!new File(filePath).exists()) {
			log.error(filePath + " is not a valid file!");
			throw new RuntimeException(filePath + " does not exist!");
		}
		UserAllocation userAllocator;
		if (allocators.containsKey(filePath)) {
			userAllocator = allocators.get(filePath);
		} else {
			userAllocator = new UserAllocation(filePath);
			allocators.put(filePath, userAllocator);
		}
		return userAllocator;
	}
	
	public static UserAllocation getUserAllocation() {
		
		return getUserAllocation(DEFAULT_PROPERTIES_FILE_PATH);
	}

	private UserAllocation(String filePath) {

		usersProperties = FileIOHandler.loadExternalProperties(filePath);

		ArrayList<HashMap<String, String>> userData = CSVHandler.loadCSV(usersProperties.getProperty("user_data_file_path"), usersProperties.getProperty("csv_delimiter"), true);

		if (userData.size() == 0) {
			throw new RuntimeException("There was no data loaded from the CSV file. Verify your file and check the log for warnings.");
		} else {
			log.info("Constructing UserAllocation from pool of " + userData.size() + " users.");
		}

		ArrayList<User> userPool = createUserPoolFromData(userData);

		this.userGroups = splitPoolToGroups(userPool);

		log.info(userGroups.size() + " user groups have been loaded (including the standard/default group).");
		for(String key : this.userGroups.keySet()){
			log.info("Group name: "+key+". Size: "+this.userGroups.get(key).size());
		}
	}

	private synchronized Map<String, UserGroup> splitPoolToGroups(ArrayList<User> userPool) {

		Map<String, UserGroup> userGroups = new HashMap<String, UserGroup>();
		String delimiter = usersProperties.getProperty("config_delimiter", ";");

		// Get the names of all the user groups to be formed from the user pool.
		String[] groupNames = usersProperties.getProperty("special_user_groups").split(delimiter);

		// Add all users in the main user pool to the group called 'standard_users'. Remove users for all other groups if .ringfence property is true, otherwise user is shared.
		userGroups.put(STANDARD_USER_GROUP, new UserGroup(userPool, STANDARD_USER_GROUP, Boolean.parseBoolean(usersProperties.getProperty(STANDARD_USER_GROUP + ".check_out",
				"true")), Long.parseLong(usersProperties.getProperty(STANDARD_USER_GROUP + ".timeout", "10000"))));
		
		// Identify the users that belong to each group and add to map of user groups
		for (int i = 0; i < groupNames.length; i++) {

			String groupName = groupNames[i];

			// get config properties for this group
			String groupAttribute = usersProperties.getProperty(groupName + ".attribute");
			ArrayList<String> indentifiers = new ArrayList<String>(Arrays.asList(usersProperties.getProperty(groupName + ".identifiers").split(delimiter)));
			boolean checkOut = Boolean.parseBoolean(usersProperties.getProperty(groupName + ".check_out", "true"));
			long timeout = Long.parseLong(usersProperties.getProperty(groupName + ".timeout", "10000"));
			boolean ringfence = Boolean.parseBoolean(usersProperties.getProperty(groupName + ".ringfence", "true"));

			// Move users from main pool to the group pool
			ArrayList<User> groupPool = userGroups.get(STANDARD_USER_GROUP).queryUsers(ringfence, groupAttribute, indentifiers);

			if (groupPool.isEmpty())
				log.warn("User pool '" + groupName + "' is empty.");

			// wrap group pool as UserGroup instance with group configuration.
			UserGroup group = new UserGroup(groupPool, groupName, checkOut, timeout);
			userGroups.put(groupName, group);
		}

		return userGroups;
	}

	private ArrayList<User> createUserPoolFromData(ArrayList<HashMap<String, String>> userData) {

		ArrayList<User> userPool = new ArrayList<User>();

		for (HashMap<String, String> map : userData) {
			userPool.add(new User(map));
		}

		return userPool;
	}

	private UserGroup getUserGroup(String name) {

		UserGroup group = this.userGroups.get(name);
		if(group == null){
			log.error("The user group '" + name + "' was requested but has not been loaded.");
			throw new RuntimeException("The user group '" + name + "' was requested but has not been loaded.");
		}
		return group;
	}

	/**
	 * Gets a standard user without a token. If checkout is enabled this user would have to be checked back in manually.
	 * 
	 * @return User object representing a non-special/admin user.
	 */
	public User getUser() {

		return getUser(this);
	}

	public User getUser(Object checkOutToken) {

		return getUserGroup(STANDARD_USER_GROUP).getAvailableUserFromPool(checkOutToken);
	}

	/**
	 * Gets an admin user from the admin_users pool without a token. If checkout is enabled this user would have to be checked back in manually.
	 * 
	 * @return User object representing an admin user.
	 */
	public User getAdminUser() {

		return getAdminUser(this);
	}

	public User getGuestUser() {

		return getGuestUser(this);
	}

	public User getAdminUser(Object checkOutToken) {

		return getUserGroup(ADMIN_USER_GROUP).getAvailableUserFromPool(checkOutToken);
	}
	
	public User getGuestUser(Object checkOutToken) {

		return getUserGroup(GUEST_USER_GROUP).getAvailableUserFromPool(checkOutToken);
	}
	
	public User getGuestModUser()
	{
		return getGuestModUser(this);
	}
	
	public User getModUser() {

		return getModUser(this);
	}

	public User getModUser(Object checkOutToken) {

		return getUserGroup(GLOBAL_MODS_GROUP).getAvailableUserFromPool(checkOutToken);
	}
	
	public User getGuestModUser(Object checkOutToken) {

		return getUserGroup(GUEST_MODS_GROUP).getAvailableUserFromPool(checkOutToken);
	}
	
	public User getGroupUser(String groupName) {

		return getGroupUser(groupName, this);
	}

	public User getGroupUser(String groupName, Object checkOutToken) {

		return getUserGroup(groupName).getAvailableUserFromPool(checkOutToken);
	}

	public void checkInAllUsers() {

		getUserGroup(STANDARD_USER_GROUP).clearAllUsers(this, true);
	}

	public void checkInAllUsersWithToken(Object checkOutToken) {

		getUserGroup(STANDARD_USER_GROUP).clearAllUsers(checkOutToken, false);
	}
	
	public void checkInAllAdminUsers() {

		getUserGroup(ADMIN_USER_GROUP).clearAllUsers(this, true);
	}

	public void checkInAllAdminUsersWithToken(Object checkOutToken) {

		getUserGroup(ADMIN_USER_GROUP).clearAllUsers(checkOutToken, false);
	}
	
	public void checkInAllModUsers() {

		getUserGroup(GLOBAL_MODS_GROUP).clearAllUsers(this, true);
	}

	public void checkInAllModUsersWithToken(Object checkOutToken) {

		getUserGroup(GLOBAL_MODS_GROUP).clearAllUsers(checkOutToken, false);
	}

	public void checkInAllGroupUsers(String groupName) {

		getUserGroup(groupName).clearAllUsers(this, true);
	}

	public void checkInAllGroupUsersWithToken(String groupName, Object checkOutToken) {

		getUserGroup(groupName).clearAllUsers(checkOutToken, false);
	}
	
	public boolean isUserGroupEmpty(String groupName)  {
		return getUserGroup(groupName).size() == 0;
	}

}
