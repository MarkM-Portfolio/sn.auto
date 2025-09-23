package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceStartParameterTest extends SearchTest {

	private final int pageSize100 = 100;

	private final int pageSize1 = 1;

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	// @Test
	public void testStartParameterIncrement() throws Exception {
		LOGGER.fine("Test SearchStartParameterTest#testStartParameterIncrement");

		int numOfEntriesWithoutUnify = PopulatedData.getInstance().getNumOfEntries();
		for (int i = 0; i < numOfEntriesWithoutUnify; i++) {
			SearchResponse response = prepareAndExecuteQuery(i, null, pageSize1);
			int expected = 1;
			assertEquals("The number of retrieved entries does not fit the expected for i=" + i, expected,
					response.getResults().size());
		}
	}

	// @Test
	public void testStartParameter1000Page0() throws Exception {
		LOGGER.fine("Test SearchStartParameterTest#testStartParameter1000Page0");
		SearchResponse response = prepareAndExecuteQuery(1000, 0, pageSize100);
		int expected = PopulatedData.getInstance().getNumOfEntries();
		assertEquals("The number of retrieved entries does not fit the expected ", expected,
				response.getResults().size());
	}

	// @Test
	public void testStartParameter1000Page1000() throws Exception {
		LOGGER.fine("Test SearchStartParameterTest#testStartParameter1000Page0");
		SearchResponse response = prepareAndExecuteQuery(1000, 1000, pageSize100);
		assertEquals("The number of retrieved entries does not fit the expected ", 0, response.getResults().size());
	}

	private SearchResponse prepareAndExecuteQuery(int startValue, Integer pageParamVal, int pageSize)
			throws Exception, IOException {
		LOGGER.fine("Test SearchStartParameterTest#executeQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(pageSize);
		searchRequest.setPage(pageParamVal);
		searchRequest.setStart(startValue);
		searchRequest.setSortKey(SortKey.date);

		return executeQuery(searchRequest);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
