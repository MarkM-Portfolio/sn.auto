package com.hcl.lconn.automation.framework.services;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.lconn.automation.framework.payload.AdminBannerRequest;
import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class AdminBannerService extends BaseAPI {
	
	private static Logger log = LoggerFactory.getLogger(AdminBannerService.class);
	
	private static String contextroot = "ic360/ui/api/admin-banner";
	
	public AdminBannerService(String serverHostName,String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}
	
	public Response getAppReg()
	{
		String root = "appreg/apps/";
		log.info("Get App Reg: ");
		String uri = String.format("%s%s", serverHost, root);
		return get(uri);
		
	}
	
	public Response getAdminBannerConfigAppReg() {
		String root = "appregistry/api/v3/services/Connections";
		log.info("Get Admin Banner Config: ");
		String endPoint = "/extensions";
		
		String uri = String.format("%s%s%s", serverHost, root, endPoint);
		
		addQueryParam("type", "com.hcl.connections.banner");
		
		return get(uri);
	}
	
	public Response getAdminBannerConfig() {
		
		log.info("Get Admin Banner Config: ");
		String endPoint = "/config.json";
		String uri = String.format("%s%s%s", serverHost, contextroot, endPoint);
		
		return get(uri);
	}

	public Response disableEnableAdminBanner(String enableDisableText) {
		log.info("Admin Banner is: "+enableDisableText);
		String endPoint = "/"+enableDisableText;
		String uri = String.format("%s%s%s", serverHost, contextroot, endPoint);

		return put(uri, null);
	}

	public Response updateAdminBanner(String message, String severity, boolean isEnabled) {
		log.info("Updating Admin Banner: message: "+ message +" severity: "+ severity + " isEnabled: "+ isEnabled);
		String endPoint = "/config.json";
		
		List<String> msgList = new ArrayList<String>();
		msgList.add(message);
		
		String uri = String.format("%s%s%s", serverHost, contextroot, endPoint);
		AdminBannerRequest adminBannerRequest = new AdminBannerRequest();
		adminBannerRequest.setMessage(msgList);
		adminBannerRequest.setSeverity(severity);
		adminBannerRequest.setOpen(isEnabled);
		
		return put(uri, adminBannerRequest);
	}

	public Response deleteAppRegistry(String entry,String authToken)
	{
		String root = "appregistry/api/v3/applications/"+entry;	
		this.setHeader("appreg-ui-request", "true");
        this.setHeader("authorization", authToken);
		String uri = String.format("%s%s", serverHost, root);
		log.info("Delete Admin Banner url is : "+ uri);

		return delete(uri);
	}
}
