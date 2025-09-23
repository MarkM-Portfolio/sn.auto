package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

/**
 * 
 * @author reuven
 * 
 */
public class SearchServiceBasicTest extends SearchTest {

	public SearchServiceBasicTest() throws IOException {
		super();
	}

	@Test
	public void testBasicSearchServiceAvailabilityTest_atom_mysearch() throws Exception {
		LOGGER.fine("Test SearchBasicTest#testBasicSearchServiceAvailabilityTest_atom_mysearch");
		SearchRequest searchRequest = new SearchRequest();
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse searchResponse = getSearchService().searchAllPublicPrivate(searchRequest);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, searchResponse);
		getTestUser().getAbderaClient().clearCookies();
	}

	@Test
	public void testBasicSearchServiceAvailability_atomfba_mysearch() throws Exception {
		LOGGER.fine("Test SearchBasicTest#testBasicSearchServiceAvailability_atomfba_mysearch");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setContextPath(ContextPath.atomfba);
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse searchResponse = getSearchService().searchAllPublicPrivate(searchRequest);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, searchResponse);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
