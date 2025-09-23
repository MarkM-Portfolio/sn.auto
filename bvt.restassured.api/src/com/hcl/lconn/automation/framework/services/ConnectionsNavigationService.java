package com.hcl.lconn.automation.framework.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class ConnectionsNavigationService extends BaseAPI {
	
	private static Logger log = LoggerFactory.getLogger(ConnectionsNavigationService.class);
	
	private static String contextroot = "appregistry/api/v3/services/Connections";
	
	public ConnectionsNavigationService(String serverHostName,String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}
	
	public Response getConnectionsNavConfigAppReg() {
		log.info("Get Connection Navigation Config: ");
		String endPoint = "/extensions";
		
		String uri = String.format("%s%s%s", serverHost, contextroot, endPoint);
		
		addQueryParam("type", "com.hcl.connections.nav");
		
		return get(uri);
	}
	
	public Response getConnectionsCustomStyleConfigAppReg() {
		log.info("Get Connection Navigation Style Config: ");
		String endPoint = "/extensions";
		
		String uri = String.format("%s%s%s", serverHost, contextroot, endPoint);
		
		addQueryParam("type", "com.hcl.connections.custom.style");
		
		return get(uri);
	}	
}
