package com.hcl.lconn.automation.framework.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hcl.lconn.automation.framework.utils.BaseAPI;

import io.restassured.response.Response;

public class PDFExportService extends BaseAPI {

	private static Logger log = LoggerFactory.getLogger(CommunityTemplateService.class);

	private static String contextroot = "ic360/ui/api/mod/pdfexport/";
	
	public PDFExportService(String serverHostName, String username, String password) {
		super(username, password);
		serverHost = serverHostName;
	}

	public Response managePDFExportAccess(boolean access, String comUUID)
	{
		log.info("Set PDF Export access for community: " + comUUID);
		
		String endPoint="access/communities/";
		String contextLink = "communities/service/html/communityedit?communityUuid=";
		String uri = String.format("%s%s%s%s", serverHost, contextroot, endPoint, comUUID);
		String context = String.format("%s%s%s", serverHost, contextLink, comUUID);
		
		addQueryParam("access", access);
		addQueryParam("context", context);
		return put(uri, null);
	}
	
}
