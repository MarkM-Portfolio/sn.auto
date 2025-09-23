package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertFalse;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class SearchServiceSortOrderParameterTestForSolr extends SearchTest {

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterDate() throws Exception {
		LOGGER.fine("SearchServiceSortOrderParameterTestForSolr#testsSortOrderParameterDate");

		SearchResponse sortDateDescParser = prepareAndExecuteQuery(SortKey.date, SortOrder.desc);
		assertFalse("Search result for sort by date  is empty", sortDateDescParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortDateDescParser, SortKey.date, SortOrder.desc);

		SearchResponse sortDateAscParser = prepareAndExecuteQuery(SortKey.date, SortOrder.asc);
		assertFalse("Search result for sort by date  is empty", sortDateAscParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortDateAscParser, SortKey.date, SortOrder.asc);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterRelevance() throws Exception {
		LOGGER.fine("SearchServiceSortOrderParameterTestForSolr#testsSortOrderParameterRelevance");
		SearchResponse sortRelevanceDescParser = prepareAndExecuteQuery(SortKey.relevance, SortOrder.desc);
		assertFalse("Search result for sort by relevance  is empty", sortRelevanceDescParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortRelevanceDescParser, SortKey.relevance, SortOrder.desc);

		SearchResponse sortRelevanceAscParser = prepareAndExecuteQuery(SortKey.relevance, SortOrder.asc);
		assertFalse("Search result for sort by relevance  is empty", sortRelevanceAscParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortRelevanceAscParser, SortKey.relevance, SortOrder.asc);
	}

	// @Test (groups = { "smartcloud" })
	public void testsSortOrderParameterTitle() throws Exception {
		LOGGER.fine("SearchServiceSortOrderParameterTestForSolr#testsSortOrderParameterTitle");
		SearchResponse sortTitleDescParser = prepareAndExecuteQuery(SortKey.title, SortOrder.desc);
		assertFalse("Search result for sort by title  is empty", sortTitleDescParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortTitleDescParser, SortKey.title, SortOrder.desc);

		SearchResponse sortTitleAscParser = prepareAndExecuteQuery(SortKey.title, SortOrder.asc);
		assertFalse("Search result for sort by title  is empty", sortTitleAscParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortTitleAscParser, SortKey.title, SortOrder.asc);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterDueDate() throws Exception {
		LOGGER.fine("SearchServiceSortOrderParameterTestForSolr#testsSortOrderParameterDueDate");

		SearchResponse sortDueDateAscParser = prepareAndExecuteQuery(SortKey.due_date, SortOrder.asc);
		assertFalse("Search result for sort by DueDate  is empty", sortDueDateAscParser.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortDueDateAscParser, SortKey.due_date, SortOrder.asc);
	}

	private SearchResponse prepareAndExecuteQuery(SortKey sortKeyValue, SortOrder sortOrderValue)
			throws Exception, IOException {

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.activities);
		searchRequest.setPageSize(10);
		searchRequest.setSortKey(sortKeyValue);
		searchRequest.setSortOrder(sortOrderValue);
		searchRequest.setHighlight(new String[0]);
		return executeQuery(searchRequest);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
