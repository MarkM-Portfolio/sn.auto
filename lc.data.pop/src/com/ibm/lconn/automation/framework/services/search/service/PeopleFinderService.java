package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
//import java.util.logging.Logger;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.PeopleFinderRequest;

public class PeopleFinderService extends SearchService {
	public enum UserType {EMPLOYEE, VISITOR};
	public static String BASIC_AUTH_CONTEXT_PATH = "/basic/people/typeahead";
	public static String FORM_BASE_AUTH_CONTEXT_PATH = "/fba/people/typeahead";
	public static String ANONYMOUS_AUTH_CONTEXT_PATH = "/anonymous/people/typeahead";
	private Logger LOGGER = getApiLogger();
	public PeopleFinderService(AbderaClient client, ServiceEntry searchServiceEntry) {
		super(client, searchServiceEntry);
	}
	
	public ClientResponse typeAhead(PeopleFinderRequest request) throws UnsupportedEncodingException {
		LOGGER.debug("Send people finder request:  " + request.toString());	
		return typeAhead(BASIC_AUTH_CONTEXT_PATH, request);
	}
	
	public ClientResponse typeAhead(String contentPath ,PeopleFinderRequest request) throws UnsupportedEncodingException {
		LOGGER.debug("Send people finder request:  " + request.toString());	
		String requestUrl = service.getServiceURLString() + contentPath + "?" + request.toString();
		getApiLogger().debug("Request is: " + requestUrl);
		ClientResponse response1 = doSearch(requestUrl);
		if (response1.getStatus()== 429){
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			response1 = doSearch(requestUrl);
			if (response1.getStatus()== 429){
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					getApiLogger().debug(e.getMessage());
				}
			return doSearch(requestUrl);
			}
		}
		if (response1.getStatus()== 503){
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				getApiLogger().debug(e.getMessage());
			}
			response1 = doSearch(requestUrl);
			
		}
		return response1;
	}
}
