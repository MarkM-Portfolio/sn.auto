package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsSearchRequest;

public class QuickResultsService extends SearchService {
	public enum UserType {EMPLOYEE, VISITOR};
	public static String BASIC_AUTH_CONTEXT_PATH = "/basic/quickresults/typeahead";
	public static String FORM_BASE_AUTH_CONTEXT_PATH = "/fba/quickresults/typeahead";
	public static String ANONYMOUS_AUTH_CONTEXT_PATH = "/anonymous/quickresults/typeahead";
	private Logger LOGGER = getApiLogger();
	
	
	public QuickResultsService(AbderaClient client, ServiceEntry searchServiceEntry) {
		super(client, searchServiceEntry);
	}
	
	public ClientResponse typeAhead(QuickResultsSearchRequest request) throws UnsupportedEncodingException {
		return typeAhead(BASIC_AUTH_CONTEXT_PATH, request);
	}
	
	public ClientResponse typeAhead(String contentPath ,QuickResultsSearchRequest request) throws UnsupportedEncodingException {
		LOGGER.debug("Send quick results request:  " + request.toString());	
		String requestUrl = service.getServiceURLString() + contentPath + "?" + request.toString();
		getApiLogger().debug("full request : " + requestUrl);
		return doSearch(requestUrl);
	}
}
