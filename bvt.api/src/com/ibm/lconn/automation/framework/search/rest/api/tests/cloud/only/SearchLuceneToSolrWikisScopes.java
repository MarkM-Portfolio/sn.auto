package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrWikisScopes extends SearchTest {
	/**
	 * scope=personalOnly scope=wikis:wiki scope=wikis:page *
	 * scope=communities:wikis scope=communities scope=communities:content tbd
	 * scope=wikis:file
	 * social={"type":"community","id":"76f7adf9-0d84-40ee-825d-ba49658c557c"}
	 * 
	 */

	@Test
	public void testsScopepersonalOnly() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopepersonalOnly");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopePersonalOnly(Scope.wikis);
	}

	@Test
	public void testsScopeParameterWikisCommunities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopeParameterWikisCommunities");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH);
		SearchResponse searchResponse = prepareAndExecuteQueryParameter(query, Scope.wikis, Scope.communities);
		Verifier.verifyEntriesScope(searchResponse, Scope.wikis);
		int expectedCommunitiesNumber = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.community,
				Purpose.SEARCH);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, expectedCommunitiesNumber * 2);
	}

	@Test
	public void testsScopeParameterWikisInCommunity() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopeParameterWikisInCommunity");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH);
		SearchResponse searchResponse = prepareAndExecuteQueryParameter(query, Scope.communities_wikis, null);
		Verifier.verifyEntriesScope(searchResponse, Scope.wikis);
		int expectedCommunitiesNumber = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.community,
				Purpose.SEARCH);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, expectedCommunitiesNumber * 2);
	}

	@Test
	public void testsScopeParameterWikisCommunitiesContent() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopeParameterWikisCommunitiesContent");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH);
		SearchResponse searchResponse = prepareAndExecuteQueryParameter(query, Scope.wikis, Scope.communities_content);
		Verifier.verifyEntriesScope(searchResponse, Scope.wikis);
		int expectedCommunitiesNumber = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.community,
				Purpose.SEARCH);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, expectedCommunitiesNumber * 2);
	}

	@Test
	public void testsScopeParameterWikisPage() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopeParameterWikisPage");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH);
		SearchResponse searchResponse = prepareAndExecuteQueryParameter(query, Scope.wikis_page, null);
		Verifier.verifyEntriesScope(searchResponse, Scope.wikis);
		int expectedCommunitiesNumber = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.community,
				Purpose.SEARCH);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, expectedCommunitiesNumber);
	}

	@Test
	public void testsScopeParameterWikisWiki() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikisScopes#testsScopeParameterWikisWiki");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH);
		SearchResponse searchResponse = prepareAndExecuteQueryParameter(query, Scope.wikis_wiki, null);
		int expectedCommunitiesNumber = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.community,
				Purpose.SEARCH);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, expectedCommunitiesNumber);
	}

	private SearchResponse prepareAndExecuteQueryParameter(String query, Scope scope, Scope scope_secondary)
			throws Exception, IOException {

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(query);
		searchRequest.setScope(scope);
		searchRequest.setScope_secondary(scope_secondary);
		return executeQuery(searchRequest);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
