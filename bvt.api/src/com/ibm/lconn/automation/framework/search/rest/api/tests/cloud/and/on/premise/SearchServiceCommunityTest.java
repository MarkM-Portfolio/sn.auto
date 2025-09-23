package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

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

public class SearchServiceCommunityTest extends SearchTest {

	public SearchServiceCommunityTest() throws IOException {
		super();
	}

	@Test
	public void testFirstBasicSearch() throws Exception {
		LOGGER.fine("Test SearchCommunityTest#testFirstBasicSearch");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = getSearchService().searchAllPublicPrivate(searchRequest);
		LOGGER.fine("Search response: " + response);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		Verifier.verifyCommunity(response);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
