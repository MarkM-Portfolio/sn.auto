package com.hcl.lconn.automation.framework.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class ForumService extends BaseAPI {
	
	private static Logger log = LoggerFactory.getLogger(ForumService.class);
	
	private static String contextroot = "forums/atom/forums";
	
	public ForumService(String serverHostName,String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}
	
	public Response getForums() {
		
		log.info("Get Forums: ");
		String uri = String.format("%s%s", serverHost, contextroot);
		
		return get(uri);
	}

}
