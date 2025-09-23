package com.ibm.atmn.waffle.extensions.user;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.utils.Utils;

public class UserGroup {

	private static final Logger log = LoggerFactory.getLogger(UserGroup.class);

	private String groupName;

	private boolean checkOut = true;

	private long timeout = 10000;

	private volatile ArrayList<User> pool;

	UserGroup(ArrayList<User> pool, String groupName, boolean checkOut, long timeout) {

		this.groupName = groupName;
		this.pool = pool;
		this.checkOut = checkOut;
		this.timeout = timeout;
	}

	synchronized User getAvailableUserFromPool(Object checkOutToken) {

		// assert that the pool has at least one entry
		assertPoolNotEmpty();

		// Get random user in list, if not available, get first available user in list.
		// If no available users, wait up to timeout for user to become available.

		// Return an integer in the range 0-(pool.size()-1) inclusive
		int rand = new Random().nextInt(pool.size());

		User user = this.pool.get(rand);

		// Check that the random user does not have their hashcode set. If so, wait for available user.
		if (user.isCheckedOut()) {
			user = getFirstAvailableUser(this.pool, this.groupName, this.timeout);
		}

		// set the hashcode on the user and return the user object
		if (this.checkOut) {
			user.checkOut(checkOutToken.hashCode());
		}

		// return the user object
		return user;
	}

	synchronized private User getFirstAvailableUser(ArrayList<User> pool, String poolDescription, long timeout) {

		long deadline = System.currentTimeMillis() + timeout;
		User user = null;

		for(int count = 0; user == null && (System.currentTimeMillis() < deadline || count == 0); count++) {

			for (int i = 0; i < pool.size() && user == null; i++) {
				if (!pool.get(i).isCheckedOut()) {
					user = pool.get(i);
				}
			}
			if (user == null)
				log.warn("Starvation warning: The user pool '" + poolDescription + "' has no available users. This will delay your test. " + (deadline - System.currentTimeMillis()) + " millis remaining.");
			Utils.milliSleep(new Random().nextInt(5000));
		}

		if (user == null) {
			log.error("Starvation: Your test starved to death waiting for available user of pool '" + poolDescription + "'. You need more users!");
			throw new RuntimeException("Starvation: Your test starved to death waiting for available user of pool '" + poolDescription + "'. You need more users!");
		} else {
			return user;
		}
	}

	private void assertPoolNotEmpty() {

		if (this.pool.isEmpty()) {
			log.error("Error: You have requested a user from the empty user group '" + groupName + "'.");
			throw new InvalidParameterException("Error: You have requested a user from the empty user group '" + groupName + "'.");
		}

	}

	synchronized ArrayList<User> queryUsers(boolean withRemoval, String attribute, ArrayList<String> indentifiers) {

		ArrayList<User> newPool = new ArrayList<User>();

		// loop through every user in the fromPool, and if it a user matches criteria, add it to the returned list. Remove reference from fromPool if option set.
		Iterator<User> iterator = this.pool.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (indentifiers.contains(user.getAttribute(attribute))) {
				newPool.add(user);
				if (withRemoval) {
					iterator.remove();
				}
			}
		}
		return newPool;
	}

	private String generateQueryDescription(String groupName, String attribute, String... identifiers) {

		StringBuilder builder = new StringBuilder();
		builder.append(groupName);
		builder.append("[Query{attribute=");
		builder.append(attribute + ",values=");
		for (int i = 0; i < identifiers.length; i++) {
			builder.append(identifiers[i] + ",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}]");
		return builder.toString();
	}

	/**
	 * Gets the specified user from map.
	 * 
	 * @param map
	 *            the user map
	 * @param identifiers
	 *            the user of interest
	 * @return the specified user from map
	 */
	User getSpecifiedUserFromPool(String attribute, String value, Object checkOutToken) {

		log.info("Requested the user from pool " + this.groupName + " with value '" + value + "' for attribute '" + attribute + "'.");

		assertPoolNotEmpty();

		ArrayList<String> indentifiers = new ArrayList<String>(Arrays.asList(value));

		ArrayList<User> results = queryUsers(false, attribute, indentifiers);

		User user = null;
		String poolDescription = generateQueryDescription(this.groupName, attribute, value);

		if (results.size() > 1) {
			log.error(results.size() + " users returned for " + poolDescription + ", indentifier must be unique.");
			throw new InvalidParameterException(results.size() + " users returned for " + poolDescription + ", indentifier must be unique.");
		} else if (results.size() < 1) {
			log.error("No users returned for the query " + poolDescription + ".");
			throw new InvalidParameterException("No users returned for the query " + poolDescription + ".");
		} else {// results.size() == 1
			user = getFirstAvailableUser(results, poolDescription, timeout);
			if (this.checkOut) {
				user.checkOut(checkOutToken.hashCode());
			}
		}

		return user;
	}

	/**
	 * Clear user map.
	 * 
	 * Checks the user map for users 'checked out' with the class instance identifier
	 * 
	 * @param map
	 *            the map of users
	 * @param checkOutToken
	 *            the instance of the calling class
	 */
	void clearAllUsers(Object checkOutToken, boolean forceCheckIn) {

		int count = 0;

		// Iterate through the map and check for the class instance set
		// Set the hashcode id of the users to 0 if the hash matches or forceCheckIn is true
		for (User user : this.pool) {

			// check if the hashcode has been set
			// Get the hashcode of the class instance and compare.
			// reset the hashcode to 0 if hashes match or forceCheckIn is true
			if (user.isCheckedOut() && (user.isCheckOutToken(checkOutToken) || forceCheckIn)) {
				user.checkIn();
				count++;
			}
		}
		log.info("The current executing class " + checkOutToken.toString() + " checked back in " + count + " users.");

	}

	/**
	 * Clear a specified list of users from the map
	 */
	void clearSpecificUsersFromPool(Object checkOutToken, boolean forceCheckIn, String attribute, String... identifiers) {
		
		int count = 0;
		String poolDescription = generateQueryDescription(this.groupName, attribute, identifiers);
		//query the users
		ArrayList<String> indentifiers = new ArrayList<String>(Arrays.asList(identifiers));
		ArrayList<User> results = queryUsers(false, attribute, indentifiers);

		// Iterate through the map and check for the class instance set
		// Set the hashcode id of the users to 0 if the hash matches or forceCheckIn is true
		for (User user : results) {

			// check if the hashcode has been set
			// Ensure that the object trying to unlock the user is the object that requested the user initially
			// Get the hashcode of the class instance and compare.
			// reset the hashcode to 0 if hashes match or forceCheckIn is true
			if (user.isCheckedOut() && (user.isCheckOutToken(checkOutToken) || forceCheckIn)) {
				user.checkIn();
				count++;
			}
		}
		log.info("The current executing class " + checkOutToken.toString() + " checked back in " + count + " users that matched pool "+poolDescription+".");
	}
	
	int size(){
		
		return this.pool.size();
	}
}
