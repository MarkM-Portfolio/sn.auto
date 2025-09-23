package com.hcl.lconn.automation.framework.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.lconn.automation.framework.payload.BasePayload;
import com.hcl.lconn.automation.framework.payload.ProfileUpdateRequest;
import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class MiddlewareJsonapiService extends BaseAPI  {
	
	public MiddlewareJsonapiService(String serverHostName, String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}

	private static Logger log = LoggerFactory.getLogger(MiddlewareJsonapiService.class);
	
	private static String contextroot = "jsonapi/";
	
	public Response getProfile(String userId)  {
		log.info("Get profile: " + userId);
		
		String endpoint = "profiles/atom/profileEntry.do";
		String uri = String.format("%s%s%s", serverHost, contextroot, endpoint);
		
		addQueryParam("userid", userId);

		Response resp =  get(uri);
				
		return resp;	
	}
	
	public <T extends BasePayload> Response updateProfile(String userId, T body)  {
		log.info("Get profile: " + userId);
		
		String endpoint = "profiles/atom/profileEntry.do";
		String uri = String.format("%s%s%s", serverHost, contextroot, endpoint);
		
		addQueryParam("userid", userId);
		
		Response resp =  put(uri, body);
		
		return resp;
	}
	
	public Response inviteProfile(String email)  {
		log.info("As user " + username + ", invite profile " + email + " to connect.");
		
		String endpoint = "profiles/atom/connections.do";
		String uri = String.format("%s%s%s", serverHost, contextroot, endpoint);
		
		addQueryParam("email", email);
		
		Response resp =  post(uri, null);
		
		return resp;
	}
	
	public Response getSearch(String searchText)  {
		log.info("Get Search: " + searchText);
		
		String endpoint = "search";
		String uri = String.format("%s%s%s", serverHost, contextroot, endpoint);
		
		addQueryParam("query", searchText);

		Response resp =  get(uri);
				
		return resp;
	}
	
	public ProfileUpdateRequest createProfileUpdateRequestPayload(String jobTitle, String officeNumber, String workPhone,
			String cellPhone, String faxNumber, String description, String experience) {
		
		log.info("Create profile update request payload");
		
		ProfileUpdateRequest updatePayload = new ProfileUpdateRequest();
		updatePayload.setJobTitle(jobTitle);
		updatePayload.setOfficeNumber(officeNumber);
		updatePayload.setWorkPhone(workPhone);
		updatePayload.setCellPhone(cellPhone);
		updatePayload.setFaxNumber(faxNumber);
		updatePayload.setDescription(description);
		updatePayload.setExperience(experience);
		
		return updatePayload;
	}
}
