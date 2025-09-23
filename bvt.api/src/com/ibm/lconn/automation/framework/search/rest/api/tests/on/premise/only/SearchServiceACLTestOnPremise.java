package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceACLTestOnPremise extends SearchTest {

	@Test
	public void testSearchWithAnonymousUser() throws Exception {
		LOGGER.fine("Test SearchACLTest#testSearchWithAnonymousUser");
		SearchResponse response = prepareAndExecuteQuery();
		Verifier.verifyReceivedEntriesAreOnlyPublic(response);
	}

	private SearchResponse prepareAndExecuteQuery() throws Exception, IOException {
		LOGGER.fine("Test SearchACLTest#executeQuery");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		return executeQuery(searchRequest);
	}

	public SearchResponse executeQuery(SearchRequest searchRequest) throws Exception {
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = getSearchService().searchAllPublic(searchRequest);
		LOGGER.fine("Search response: " + response);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		return response;
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
