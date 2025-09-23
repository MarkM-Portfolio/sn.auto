package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;

public class SearchLuceneToSolrForumsScopes extends SearchTest {

	/**
	 * scope=personalOnly scope=forums:forum scope=forums:topic
	 * scope=communities:forums scope=communities scope=communities:content
	 * scope=stand-alone tbd scope=forums:file
	 * social={%22type%22%3A%22community%22%2C%22id%22%3A%2232f643fc-a611-4461-a1ad-89f7a045b400%22}
	 */

	@Test
	public void testsScopePersonalOnly() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForumsScopes#testsScopePersonalOnly");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopePersonalOnly(Scope.forums);
	}

	// @Test
	// public void testsScopeParameterForumsCommunities() throws Exception {
	// LOGGER.fine("Test
	// SearchLuceneToSolrForumsScopes#testsScopeParameterForumsCommunities");
	// getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameterComponentCommunities(Scope.forums);
	// }
	//
	// @Test
	// public void testsScopeParameterForumsCommunitiesContent() throws Exception {
	// LOGGER.fine("Test
	// SearchLuceneToSolrForumsScopes#testsScopeParameterForumsCommunitiesContent");
	// getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameterComponentCommunitiesContent(Scope.forums);
	// }
	//
	// @Test
	// public void testsScopeParameterForumsOnly() throws Exception {
	// LOGGER.fine("Test
	// SearchLuceneToSolrForumsScopes#testsScopeParameterForumsOnly");
	// getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.forums_forum,
	// null, Scope.forums, Purpose.SEARCH_SCOPE);
	// }
	//
	// @Test
	// public void testsScopeParameterForumsTopic() throws Exception {
	// LOGGER.fine("Test
	// SearchLuceneToSolrForumsScopes#testsScopeParameterActivitiesOnly");
	// getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.forums_topic,
	// null, Scope.forums, Purpose.SEARCH_SCOPE);
	// }

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
