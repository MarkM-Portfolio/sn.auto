package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;

public class SearchLuceneToSolrFilesScopes extends SearchTest {
	/**
	 * scope=personalOnly scope=files scope=communities:files scope=stand-alone
	 * scope=communities scope=communities:content
	 */

	@Test
	public void testsScopeParameterFilesStandAlone() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFilesScopes#testsScopeParameterFilesStandAlone");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.files, Scope.stand_alone,
				Scope.files, Purpose.SEARCH, true);
	}

	@Test
	public void testsScopepersonalOnly() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFilesScopes#testsScopepersonalOnly");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopePersonalOnly(Scope.files);
	}

	@Test
	public void testsScopeParameterFilesCommunities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFilesScopes#testsScopeParameterFilesCommunities");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameterComponentCommunities(Scope.files);
	}

	@Test
	public void testsScopeParameterFilesInCommunity() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFilesScopes#testsScopeParameterFilesInCommunity");
		getSearchLuceneToSolrHelper().getSearchScopesHelper().testsScopeParameter(Scope.communities_files, null,
				Scope.files, Purpose.SEARCH_SCOPE);
	}

	@Test
	public void testsScopeParameterFilesCommunitiesContent() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFilesScopes#testsScopeParameterFilesCommunitiesContent");
		getSearchLuceneToSolrHelper().getSearchScopesHelper()
				.testsScopeParameterComponentCommunitiesContent(Scope.files);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
