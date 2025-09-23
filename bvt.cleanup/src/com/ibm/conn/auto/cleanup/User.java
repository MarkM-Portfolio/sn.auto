package com.ibm.conn.auto.cleanup;

import java.io.IOException;
import java.util.HashMap;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;

public class User {

	private HashMap<String, String> userData;
	private final String PASSWORD_FIELD = "password";
	private final String UID_FIELD = "uid";
	private final String EMAIL_FIELD = "email";
	private String USERNAME_FIELD;
	
	public User(HashMap<String, String> userData){
		this.userData = userData;
		USERNAME_FIELD = StringConstants.DEPLOYMENT_TYPE == StringConstants.DeploymentType.ON_PREMISE
						 ? UID_FIELD : EMAIL_FIELD;
	}
	
	public String getUserField(String fieldName){
		return userData.get(fieldName);
	}
	
	public String getUID(){
		return getUserField(UID_FIELD);
	}
	
	public String getEmail(){
		return getUserField(EMAIL_FIELD);
	}
	
	/**
	 * Get the username for this user to log in with. Note that this will return
	 * either the email or uid depending upon if the server is a smart cloud
	 * installation or on prem installation.
	 */
	public String getUsername(){
		return getUserField(USERNAME_FIELD);
	}
	
	public String getPassword(){
		return getUserField(PASSWORD_FIELD);
	}
	
	/**
	 * Helper method to log into a user for a particular component
	 * @param component The application component to log into
	 * @return The user perspective for this particular user
	 * @throws IOException
	 * @throws LCServiceException
	 */
	public UserPerspective getUserPerspective(String component) throws IOException, LCServiceException{
			return new UserPerspective(getUsername(), getPassword(), component, true);
	}
}
