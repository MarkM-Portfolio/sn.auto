package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrActivitiesScopes extends SearchTest {
	/**
	 * including activity in community
	 * 
	 * scope=personalOnly scope=activities:activity scope=activities:section
	 * scope=activities:entry scope=activities:task scope=activities:bookmark
	 * scope=stand-alone scope=communities scope=communities:content
	 */

	@Test
	public void testsScopeParameterActivitiesTask() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesTask");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.activities_task, null,
				Scope.activities, Purpose.UNIFY);
	}

	@Test
	public void testsScopeParameterActivitiesSection() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesSection");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.activities_section, null,
				Scope.activities, Purpose.UNIFY);
	}

	@Test
	public void testsScopeParameterActivitiesEntries() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesEntries");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.activities_entry, null,
				Scope.activities, Purpose.SEARCH);
	}

	@Test
	public void testsScopeParameterActivitiesOnly() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesOnly");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.activities_activity, null,
				Scope.activities, Purpose.SEARCH_SCOPE);
	}

	@Test
	public void testsScopeParameterActivitiesStandAlone() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesStandAlone");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.activities_activity,
				Scope.stand_alone, Scope.activities, Purpose.UNIFY, true);
	}

	@Test
	public void testsScopepersonalOnly() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopepersonalOnly");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopePersonalOnly(Scope.activities);
	}

	@Test
	public void testsScopeParameterActivitiesCommunities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesCommunities");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameterComponentCommunities(Scope.activities);
	}

	@Test
	public void testsScopeParameterActivitiesCommunitiesContent() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsScopeParameterActivitiesCommunitiesContent");
		getSearchLuceneToSolrHelper().getSearchScopesHelper()
				.testsScopeParameterComponentCommunitiesContent(Scope.activities);
	}

	@Test
	public void testsQueryattachFileScopeActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivitiesScopes#testsQueryattachFileScopeActivities");
		String query = SearchRestAPIUtils.getExecId(Purpose.SEARCH_SCOPE) + "+attach";
		SearchResponse searchResponse = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setQuery(query).setScope(Scope.activities).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, 1);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
