package com.ibm.conn.auto.cleanup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Users {
	
	private static Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	private ArrayList<User> _users;
	
	// Constants used for loading properties files
	private static final String PROFILES_TO_LOAD_KEY = "profilesToLoad";
	private static final String USER_NAME_KEY = "username";
	private static final String USER_PASSWORD_KEY = "userpassword";
	private static final String USER_EMAIL_KEY = "useremail";
	private static final String USER_REALNAME_KEY = "realname";
	private static final String IS_ADMIN_KEY = "isadmin";
	private static final String IS_CONNECTIONS_ADMIN_KEY = "isConnectionsAdmin";
	
	public ArrayList<User> getUsers(){
		return _users;
	}
	
	/**
	 * Given the filePatht to the users to load, attempt to load the users. This
	 * handles both property files used by api testing and csv files used by
	 * gui testing.
	 * @param filePath
	 * @throws IOException 
	 */
	public void loadUsers(String filePath) throws IOException{
		_users = new ArrayList<User>();
		
		if(filePath.toLowerCase().endsWith(".csv")){
			loadCSVFile(filePath);
		} else if(filePath.toLowerCase().endsWith(".properties")){
			loadPropertiesFile(filePath);
		} else {
			LOGGER.error("The file: " + filePath + " is not a recognized file type");
			LOGGER.error("Only .properties and .csv files are able to be loaded");
			System.exit(1);
		}
	}
	
	private void loadCSVFile(String filePath) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String headerLine = "";
		String[] header = null;
	    String line;
	    String[] userComponents;
	    while ((line = reader.readLine()) != null) {
	    	// The first line will be the header specified in the csv file.
	    	if(line.equalsIgnoreCase("") || line.startsWith("#")){
	    		continue;
	    	} else if(header == null){
	    		headerLine = line;
	    		header = line.split(",");
	    	} else {
	    		userComponents = line.split(",");
	    		if(userComponents.length != header.length){
	    			LOGGER.error("Error reading the user csv file: " + filePath);
	    			LOGGER.error("The file was malformated. The header line and the current user have a different field count.");
	    			LOGGER.error("Header Line: " + headerLine);
	    			LOGGER.error("Current User Line: " + line);
	    			System.exit(1);
	    		}
	    		
	    		HashMap<String, String> userData = new HashMap<String, String>();
	    		for(int i = 0; i < userComponents.length; i++){
	    			userData.put(header[i], userComponents[i]);
	    		}
	    		
	    		// Skip over the users that are members of the admin or search
	    		// groups
	    		if( userData.get("members") != null &&
	    			userData.get("members").equalsIgnoreCase("admin") ||
	    			userData.get("members").equalsIgnoreCase("search")) {
	    				continue;
    			}
	    		
	    		_users.add(new User(userData));
	    	}
	    	
	    }
	}
	
	private void loadPropertiesFile(String filePath) throws IOException{
		Properties userProperties = new Properties();
		FileInputStream fs = new FileInputStream(filePath);
		userProperties.load(fs);
		fs.close();
		
		String rawProfiles = userProperties.getProperty(PROFILES_TO_LOAD_KEY, "");
		for(String profile : rawProfiles.split(",")){
			profile = profile.trim();
			
			// Check if the user is an admin, if so skip over this user  why?  Admins can create things too.
			//if( userProperties.getProperty(profile + '.' + IS_ADMIN_KEY, "false").equalsIgnoreCase("true") ||
			//	userProperties.getProperty(profile + '.' + IS_CONNECTIONS_ADMIN_KEY, "false").equalsIgnoreCase("true")){
			//	continue;
			//}
			
			HashMap<String, String> userData = new HashMap<String, String>();
			
			// TJB 8/19/15 If the password is blank for some reason, the user will not be able to log in.  Skip that user
			if (! userProperties.getProperty(profile + '.' + USER_PASSWORD_KEY).equalsIgnoreCase("")) {
				userData.put("uid", userProperties.getProperty(profile + '.' + USER_NAME_KEY));
				userData.put("email", userProperties.getProperty(profile + '.' + USER_EMAIL_KEY));
				userData.put("password", userProperties.getProperty(profile + '.' + USER_PASSWORD_KEY));
				userData.put("name", userProperties.getProperty(profile + '.' + USER_REALNAME_KEY));
			
				_users.add(new User(userData));
			}
		}
		
	}
}
