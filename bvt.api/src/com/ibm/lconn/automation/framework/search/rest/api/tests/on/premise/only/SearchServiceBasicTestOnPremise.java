package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceBasicTestOnPremise extends SearchTest {

	@Test
	public void testBasicSearchServiceAvailability_atom_search() throws Exception {
		LOGGER.fine("Test SearchBasicTest#testBasicSearchServiceAvailability_atom_search");
		SearchRequest searchRequest = new SearchRequest();
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = getSearchService().searchAllPublic(searchRequest);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

	}

	@Test
	public void testBasicSearchServiceAvailability_atomfba_search() throws Exception {
		LOGGER.fine("Test SearchBasicTest#testBasicSearchServiceAvailability_atomfba_search");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContextPath(ContextPath.atomfba);
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = getSearchService().searchAllPublic(searchRequest);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
