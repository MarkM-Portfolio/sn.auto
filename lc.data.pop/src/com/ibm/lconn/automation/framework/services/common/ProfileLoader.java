package com.ibm.lconn.automation.framework.services.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;


public class ProfileLoader {

	protected static Properties _profileProperties; 
	private static String PROPERTIES_FILE_PATH = "/resources/ProfileData_lc30linux3.properties"; 
	private static String GROUP_NAME;
	private static String ORGID;
	private static String ORGADMINGK;
	private static boolean QUICK_RESULTS_ENABLED;

	private static boolean _dataLoaded = false;
	private static boolean _dataset = false;
	
	public static ArrayList<ProfileData> PROFILES_ARRAY;
	
	// Loads profile data from properties file	
	private static  void loadData() throws FileNotFoundException, IOException{
		loadProfileProperties();
		String[] profileNames = _profileProperties.getProperty("profilesToLoad").split(",");
		PROFILES_ARRAY = new ArrayList<ProfileData> ();
		for(String profile : profileNames){
			ProfileData profilesData = createProfileData(profile);
			PROFILES_ARRAY.add(profilesData);
						
		}
		
		_dataLoaded = true;
		}

	protected static void loadProfileProperties() throws IOException,
			FileNotFoundException {
		_profileProperties = new Properties();
				
		String servername = URLConstants.SERVER_URL.substring(URLConstants.SERVER_URL.indexOf("://")+3,URLConstants.SERVER_URL.indexOf('.'));
		
		// For server names starting with 'apps' and a '.' where the dot is not part of the domain.
		// TJB 1/12/14 because we now test na.collabservtest and na.collabservtest.lotus (E1 and C1)
		// we now have to check for a third token.  At this point, this code probably needs to be
		// rewritten to accommodate any server name.
		if (servername.equalsIgnoreCase("apps")){
			String srvrName;
			String[] tokens = URLConstants.SERVER_URL.split("\\.");
			if (tokens[1].equalsIgnoreCase("ce") || tokens[1].equalsIgnoreCase("ap") || tokens[1].equalsIgnoreCase("na")){
				if (tokens[3].equalsIgnoreCase("lotus")) {
					srvrName = tokens[0] + "." + tokens[1] + "." +tokens[2] + "." + tokens[3];
				} else {
					srvrName = tokens[0] + "." + tokens[1] + "." +tokens[2];
				}
			} else {
				srvrName = tokens[0] + "." + tokens[1];
			}
			
			servername = srvrName.substring(srvrName.indexOf("://")+3,srvrName.length());
			// For SC sandbox servers
			if (servername.contains("sandbox")) {
				servername = "sandbox_pool";
			}
		// For on-premise server pool and BVT servers
		} else if (servername.startsWith("lcauto") || servername.startsWith("icbvt") || servername.startsWith("bvt") ) {
			servername =  "lcauto_pool";
		} 
		
		PROPERTIES_FILE_PATH = "/resources/ProfileData_" + servername + ".properties";

		InputStream in = ProfileData.class.getResourceAsStream(PROPERTIES_FILE_PATH);
		if (in != null){
			_profileProperties.load(in);
		}
		else {    // read properties from outside jar, under ./resources
			_profileProperties.load(new FileInputStream("."+PROPERTIES_FILE_PATH));
		}
		
		GROUP_NAME = _profileProperties.getProperty("groupname");
		ORGID = _profileProperties.getProperty("orgid");
		ORGADMINGK = _profileProperties.getProperty("orgadminGK");
		
		// using "true".equalsIgnoreCase for null safety
		QUICK_RESULTS_ENABLED = "true".equalsIgnoreCase(_profileProperties.getProperty("quickResultsEnabled"));
		
	}
	protected static ProfileData createProfileData(String profile) {
		String nameIndex = profile + ".";
		String username = _profileProperties
				.getProperty(nameIndex + "username");
		String userpassword = _profileProperties.getProperty(nameIndex
				+ "userpassword");
		String useremail = _profileProperties.getProperty(nameIndex
				+ "useremail");
		String userid = _profileProperties.getProperty(nameIndex + "userid");
		String userkey = _profileProperties.getProperty(nameIndex + "userkey");
		String realName = _profileProperties
				.getProperty(nameIndex + "realname");
		boolean isAdmin = Boolean.valueOf(_profileProperties
				.getProperty(nameIndex + "isadmin"));
		boolean isCurrentUser = Boolean.valueOf(_profileProperties
				.getProperty(nameIndex + "iscurrentuser"));
		boolean isModerator = Boolean.valueOf(_profileProperties
				.getProperty(nameIndex + "ismoderator"));
		boolean isConnectionsAdmin = Boolean.valueOf(_profileProperties
				.getProperty(nameIndex + "isconnectionsadmin"));
		String orgName = _profileProperties.getProperty(nameIndex + "orgname");
		
		ProfileData profilesData = new ProfileData(username, userpassword,
				useremail, userid, userkey, realName, true, isAdmin,
				isCurrentUser, isModerator, isConnectionsAdmin, orgName);
		return profilesData;
	}
	//returns a random ProfileData from the list of loaded profile data
	public static ProfileData getProfile() throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		int randIndex = (int)(Math.random()*(PROFILES_ARRAY.size()));
		return PROFILES_ARRAY.get(randIndex);
	}
	
	//returns the ProfileData for the Current Profile from the list of loaded profile data
	public static ProfileData getCurrentProfile() throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		ProfileData currProfile = null;
		for(ProfileData p : PROFILES_ARRAY){
			if(p.isCurrentUser()){
				currProfile = p;
				break;
			}
		}
		if(currProfile != null){
			return currProfile;
		}
		else{
			return null;
		}
	}
	
	//returns the first admin ProfileData from the list of loaded profile data
	public static ProfileData getAdminProfile() throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		ProfileData admin = null;
		for(ProfileData p : PROFILES_ARRAY){
			if(p.isAdmin()){
				admin = p;
				break;
			}
		}
		if(admin != null){
			return admin;
		}
		else{
			return null;
		}
	}
	
	//returns the first moderator ProfileData from the list of loaded profile data
	public static ProfileData getModeratorProfile() throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		ProfileData moderator = null;
		for(ProfileData p : PROFILES_ARRAY){
			if(p.isModerator()){
				moderator = p;
				break;
			}
		}
		if(moderator != null){
			return moderator;
		}
		else{
			return null;
		}
	}
	
	//returns the first ConnectionsAdmin ProfileData from the list of loaded profile data
	public static ProfileData getConnectionsAdminProfile() throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		ProfileData connectionsAdmin = null;
		for(ProfileData p : PROFILES_ARRAY){
			if(p.isConnectionsAdmin()){
				connectionsAdmin = p;
				break;
			}
		}
		if(connectionsAdmin != null){
			return connectionsAdmin;
		}
		else{
			return null;
		}
	}	
	
	//returns the ProfileData specified by an index
	public static ProfileData getProfile(int index) throws FileNotFoundException, IOException{
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		return PROFILES_ARRAY.get(index);
	}
	
	//returns the Profile group name
	public static String getGroupName(){
			return GROUP_NAME;
	}
	
	public static String getORGID() {
		return ORGID;
	}
	
	public static String getORGADMINGK() {
		return ORGADMINGK;
	}

	public static boolean getQuickResultsEnabled() {
		return QUICK_RESULTS_ENABLED;
	}
	
	// reset the default value in StringConstants
	public static void getProfiles() throws FileNotFoundException, IOException {
		if (_dataset){
			return;
		}
		
		if(!_dataLoaded){
			ProfileLoader.loadData();
		}
		// get the profiles for testing
		ProfileData admin, login_user, moderator, user1, user2, connectionsAdmin, inactive_user, external_user;
		admin = ProfileLoader.getProfile(0);
		login_user = ProfileLoader.getProfile(2);		
		moderator = ProfileLoader.getProfile(3);
		user1 = ProfileLoader.getProfile(5);
		user2 = ProfileLoader.getProfile(12);
		connectionsAdmin = ProfileLoader.getProfile(13);
		inactive_user = ProfileLoader.getProfile(14);
		external_user = ProfileLoader.getProfile(15);	
		
		// set the StringConstants
		StringConstants.GROUP_NAME = ProfileLoader.getGroupName();	
		StringConstants.ORGID = ProfileLoader.getORGID();
		StringConstants.ORGADMINGK = ProfileLoader.getORGADMINGK();
		StringConstants.QUICK_RESULTS_ENABLED = ProfileLoader.getQuickResultsEnabled();
		
		StringConstants.USER_NAME = login_user.getUserName();
		StringConstants.USER_PASSWORD = login_user.getPassword();
		StringConstants.USER_REALNAME = login_user.getRealName();
		StringConstants.USER_EMAIL = login_user.getEmail();
		
		StringConstants.ADMIN_USER_NAME = admin.getUserName();
		StringConstants.ADMIN_USER_PASSWORD = admin.getPassword();
		StringConstants.ADMIN_USER_REALNAME = admin.getRealName();
		StringConstants.ADMIN_USER_EMAIL = admin.getEmail();
		
		StringConstants.MODERATOR_USER_NAME = moderator.getUserName();
		StringConstants.MODERATOR_USER_PASSWORD = moderator.getPassword();
		StringConstants.MODERATOR_USER_REALNAME = moderator.getRealName();
		StringConstants.MODERATOR_USER_EMAIL = moderator.getEmail();
		
		StringConstants.RANDOM1_USER_NAME = user1.getUserName();
		StringConstants.RANDOM1_USER_PASSWORD = user1.getPassword();
		StringConstants.RANDOM1_USER_REALNAME = user1.getRealName();
		StringConstants.RANDOM1_USER_EMAIL = user1.getEmail();
		
		StringConstants.RANDOM2_USER_NAME = user2.getUserName();
		StringConstants.RANDOM2_USER_PASSWORD = user2.getPassword();
		StringConstants.RANDOM2_USER_REALNAME = user2.getRealName();
		StringConstants.RANDOM2_USER_EMAIL = user2.getEmail();
		
		StringConstants.INACTIVE_USER_NAME = inactive_user.getUserName();
		StringConstants.INACTIVE_USER_REALNAME = inactive_user.getRealName();
		StringConstants.INACTIVE_USER_EMAIL = inactive_user.getEmail();
		
		StringConstants.CONNECTIONS_ADMIN_USER_NAME = connectionsAdmin.getUserName();
		StringConstants.CONNECTIONS_ADMIN_USER_PASSWORD = connectionsAdmin.getPassword();
		
		StringConstants.EXTERNAL_USER_NAME = external_user.getUserName();
		StringConstants.EXTERNAL_USER_PASSWORD = external_user.getPassword();
		StringConstants.EXTERNAL_USER_REALNAME = external_user.getRealName();
		StringConstants.EXTERNAL_USER_EMAIL = external_user.getEmail();
		
		_dataset =true;
	
		
	}
	
	
}
