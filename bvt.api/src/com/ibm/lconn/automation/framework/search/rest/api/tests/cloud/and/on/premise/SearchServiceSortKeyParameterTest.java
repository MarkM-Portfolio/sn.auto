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
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceSortKeyParameterTest extends SearchTest {

	@Test
	public void testsSortKeyParameter() throws Exception {
		LOGGER.fine("Test SearchSortKeyParameterTest#testsSortKeyParameter");

		SearchResponse sortKeyDateParser = prepareAndExecuteQuery(SortKey.date);
		Verifier.validateSortKeyResult(sortKeyDateParser, SortKey.date, SortOrder.desc);

		SearchResponse sortKeyRelevance = prepareAndExecuteQuery(SortKey.relevance);
		Verifier.validateSortKeyResult(sortKeyRelevance, SortKey.relevance, SortOrder.desc);

		SearchResponse sortKeyNone = prepareAndExecuteQuery(null);
		Verifier.validateSortKeyResult(sortKeyNone, null, SortOrder.desc);
	}

	private SearchResponse prepareAndExecuteQuery(SortKey sortKey) throws Exception, IOException {
		LOGGER.fine("Test SearchSortKeyParameterTest#executeQuery, sortKey: " + sortKey);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(sortKey);

		return executeQuery(searchRequest);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
