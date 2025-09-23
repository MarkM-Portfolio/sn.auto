package com.hcl.lconn.automation.framework.services;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hcl.lconn.automation.framework.utils.BaseAPI;
import com.hcl.lconn.automation.framework.utils.TestAPIConfigCustom;
import com.ibm.atmn.waffle.extensions.user.User;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ProfileService extends BaseAPI {
	
	private static Logger log = LoggerFactory.getLogger(ProfileService.class);
	private TestAPIConfigCustom cfg;
	
	public ProfileService(String serverHostName,String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}
	
	public Response getProfileDetails(String userEmailID) {
		
		log.info("Get Profile UUID: ");

		String endpoint = "profiles/json/profile.do";

		String uri = String.format("%s%s", serverHost, endpoint);
		
		addQueryParam("email", userEmailID);
		
		return get(uri);
		
	}
	
	public String getProfileUUID(User testUser)
	{
		log.debug("the gertuid: "+ testUser.getUid());
		log.debug("the getpass: "+ testUser.getPassword());

		ProfileService profileService = new ProfileService(serverHost, testUser.getUid(), testUser.getPassword());     
		Response resp = profileService.getProfileDetails(testUser.getEmail());  
		
		profileService.assertStatusCode(resp, HttpStatus.SC_OK, "Jsonapi Get profile UUID status code");                                                                                                           

		JsonPath jsonpath = new JsonPath(resp.asString());
		
		String profileUUID = jsonpath.getString("X_lconn_userid");
		
		return profileUUID;
	}

}
