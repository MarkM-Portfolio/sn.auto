package com.hcl.lconn.automation.framework.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.lconn.automation.framework.payload.CommunityTemplateRequest;
import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class CommunityTemplateService extends BaseAPI {
	
	private static Logger log = LoggerFactory.getLogger(CommunityTemplateService.class);
	
	private static String contextroot = "comm-template/";
	
	public CommunityTemplateService(String serverHostName, String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}
	
	public Response createCommunityFromTemplate(String templateID, 
			String newCommHandle, String newCommName) {
		
		log.info("Create community from template: " + templateID);
		String endpoint = "/createcommunity";
		String uri = String.format("%s%s%s%s", serverHost, contextroot, templateID, endpoint);
		
		CommunityTemplateRequest commTemplateRequestPayload = new CommunityTemplateRequest();
		commTemplateRequestPayload.setHandle(newCommHandle).setName(newCommName);
		
		return put(uri, commTemplateRequestPayload);
	}
	
	public Response getCommunityTemplate(String... templateID) {
		
		String uri=null;
		log.info("Get template details for : " + templateID);
		if(templateID.length == 0)
		{
			uri = String.format("%s%s", serverHost, contextroot);
		}
		else
			uri = String.format("%s%s%s", serverHost, contextroot, templateID[0]);
				
		return get(uri);
	}
	
	public Response deleteCommunityTemplate(String templateID) {
		
		log.info("Delete community template: " + templateID);
		String uri = String.format("%s%s%s", serverHost, contextroot, templateID);
		return delete(uri);
	}
}
