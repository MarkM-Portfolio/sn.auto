package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
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

public class SearchServicePageSizeParameterTest extends SearchTest {

	@Test
	public void testPageParameter() throws Exception {

		LOGGER.fine("Test SearchPageSizeParameterTest#testPageParameter");
		List<SearchResult> entries = getAllEntries();

		for (int i = 1; i <= entries.size(); i++) {
			executeQueryAndVerify(entries, i);
		}
	}

	private void executeQueryAndVerify(List<SearchResult> entries, int pageSizeValue) throws Exception, IOException {
		LOGGER.fine("Test SearchPageSizeParameterTest#executeQueryAndVerify: pageSize: " + pageSizeValue);
		SearchResponse response;

		for (int i = 0; i < entries.size() / pageSizeValue; i++) {
			response = prepareAndExecuteQuery(i + 1, pageSizeValue);
			List<SearchResult> expectedEntries = new ArrayList<SearchResult>();
			for (int y = 0; y < pageSizeValue; y++) {
				expectedEntries.add(entries.get(y + i * pageSizeValue));
			}

			assertEquals("The number of retrieved entries does not fit expected ", pageSizeValue,
					response.getResults().size());

			for (int k = 0; k < pageSizeValue; k++) {
				Verifier.verifyDiffBetweenEntries(expectedEntries.get(k), response.getResults().get(k));
			}
		}
	}

	private List<SearchResult> getAllEntries() throws Exception, IOException {
		SearchResponse response = prepareAndExecuteQuery(1, StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		List<SearchResult> entries = response.getResults();
		return entries;
	}

	private SearchResponse prepareAndExecuteQuery(int page, int pageSize) throws Exception, IOException {
		LOGGER.fine("Test SearchPageSizeParameterTest#executeQuery, page: " + page + ", pageSize: " + pageSize);
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
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
