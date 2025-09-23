package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.stacking.StackingRetrievalRequest;

public class ActivityStreamStackingService extends SearchService {
	public static String BASIC_AUTH_CONTEXT_PATH = "/basic/as/stacking";
	private Logger LOGGER = getApiLogger();
	
	public ActivityStreamStackingService(AbderaClient client, ServiceEntry searchServiceEntry) {
		super(client, searchServiceEntry);
	}
	
	public ClientResponse retrieve(StackingRetrievalRequest stackingRetrievalRequest) throws UnsupportedEncodingException {
		LOGGER.debug("Send Activity Stream Stackingk request:  " + stackingRetrievalRequest.toString());	
		String requestUrl = service.getServiceURLString() + BASIC_AUTH_CONTEXT_PATH + "?" + stackingRetrievalRequest.toString();
		getApiLogger().debug("full request : " + requestUrl);
		return doSearch(requestUrl);
	}
}
