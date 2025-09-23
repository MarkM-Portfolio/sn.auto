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

public class SearchServiceSortOrderParameterTest extends SearchTest {

	@Test
	public void testsSortOrderParameter() throws Exception {
		LOGGER.fine("Test SearchSortOrderParameterTest#testsSortOrderParameter");

		SearchResponse sortDateDescParser = prepareAndExecuteQuery(SortKey.date, SortOrder.desc);
		Verifier.validateSortKeyResult(sortDateDescParser, SortKey.date, SortOrder.desc);

		SearchResponse sortDateAscRelevance = prepareAndExecuteQuery(SortKey.date, SortOrder.asc);
		Verifier.validateSortKeyResult(sortDateAscRelevance, SortKey.date, SortOrder.asc);

		SearchResponse sortRelevanceDescParser = prepareAndExecuteQuery(SortKey.relevance, SortOrder.desc);
		Verifier.validateSortKeyResult(sortRelevanceDescParser, SortKey.relevance, SortOrder.desc);

	}

	private SearchResponse prepareAndExecuteQuery(SortKey sortKeyValue, SortOrder sortOrderValue)
			throws Exception, IOException {

		LOGGER.fine("Test SearchSortOrderParameterTest#executeQuery, sortOrder: " + sortOrderValue);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(sortKeyValue);
		searchRequest.setSortOrder(sortOrderValue);
		return executeQuery(searchRequest);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
