package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServicePageParameterTest extends SearchTest {

	@Test
	public void testPageParameter() throws Exception {

		LOGGER.fine("Test SearchPageParameterTest#testPageParameter");

		SearchResponse response = prepareAndExecuteQuery(0, 0, StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		List<SearchResult> expectedEntries = response.getResults();
		LOGGER.fine("Search response: " + response);
		int pagesNumber = (expectedEntries.size() > 5) ? 5 : expectedEntries.size();
		for (int i = 0; i < pagesNumber; i++) {
			response = prepareAndExecuteQuery(0, i + 1, 1);

			SearchResult expectedEntry = expectedEntries.get(i);
			assertEquals("The number of retrieved entries does not fit the expected ", 1, response.getResults().size());
			Verifier.verifyDiffBetweenEntries(expectedEntry, response.getResults().get(0));
		}
	}

	private SearchResponse prepareAndExecuteQuery(int startValue, int page, int pageSize)
			throws Exception, IOException {
		LOGGER.fine("Test SearchPageParameterTest#executeQuery");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setStart(startValue);
		searchRequest.setPage(page);
		searchRequest.setPageSize(pageSize);
		searchRequest.setSortKey(SortKey.date);

		return executeQuery(searchRequest);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
