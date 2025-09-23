package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceACLTest extends SearchTest {

	@Test
	public void testSearchWithAuthenticatedUser() throws Exception {
		LOGGER.fine("Test SearchACLTest#testSearchWithAuthenticatedUser");
		SearchResponse response = prepareAndExecuteQuery();
		Verifier.verifyReceivedEntries(response, Permissions.PUBLIC);
		Verifier.verifyReceivedEntries(response, Permissions.PRIVATE);
	}

	private SearchResponse prepareAndExecuteQuery() throws Exception, IOException {
		LOGGER.fine("Test SearchACLTest#executeQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		return executeQuery(searchRequest);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
