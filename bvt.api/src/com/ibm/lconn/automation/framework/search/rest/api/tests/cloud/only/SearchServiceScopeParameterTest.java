package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchServiceScopeParameterTest extends SearchTest {

	@Test
	public void testsScopeParameterAllConnections() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsAllConnectionsScopeParameter");
		SearchResponse allConnectionsResponse = prepareAndExecuteQuery(Scope.allconnections);
		// Purpose.Search is used for request query. Verification need to
		// exclude unify-related entries, which has another purpose
		// Purpose.UNIFY
		Verifier.verifyEntriesComparedToPopulation(allConnectionsResponse, Scope.allconnections);
	}

	@Test
	public void testsScopeParameterBlogs() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterBlogs");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.blogs);
		Verifier.verifyEntriesScope(searchResponse, Scope.blogs);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.blogs);
	}

	@Test
	public void testsScopeParameterCommunities() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterCommunities");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.communities);
		Verifier.verifyEntriesScope(searchResponse, Scope.communities);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.communities);
	}

	@Test
	public void testsScopeParameterCommunitiesEntry() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterCommunitiesEntry");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.communities_entry);
		Verifier.verifyEntriesScope(searchResponse, Scope.communities_entry);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.communities_entry);
	}

	@Test
	public void testsScopeParameterDogear() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterDogear");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.dogear);
		Verifier.verifyEntriesScope(searchResponse, Scope.dogear);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.dogear);
	}

	@Test
	public void testsScopeParameterForums() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterForums");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.forums);
		Verifier.verifyEntriesScope(searchResponse, Scope.forums);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.forums);
	}

	@Test
	public void testsScopeParameterStatusUpdates() throws Exception {
		LOGGER.fine("Test SearchScopeParameterTest#testsScopeParameterStatusUpdates");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.status_updates);
		Verifier.verifyEntriesScope(searchResponse, Scope.status_updates);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.status_updates);
	}

	private SearchResponse prepareAndExecuteQuery(Scope scope) throws Exception, IOException {
		LOGGER.fine("Test SearchScopeParameterTest#executeQuery, scope: " + scope);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(scope);

		return executeQuery(searchRequest);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
