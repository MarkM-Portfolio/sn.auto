package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;

public class SearchLuceneToSolrForums extends SearchTest {
	/**
	 * query page pageSize scope Forums queryLang sortOrder SortKey
	 * date,relevance,title constraint category-tag,category-source-Forums,
	 * field-tag,category-page nonconstraint category-tag highlight social - userId,
	 * userEmail, communityId
	 */

	@Test
	public void testsQueryTag() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testsQueryTag");
		getSearchLuceneToSolrHelper().testsQueryTag(Scope.forums);
	}

	@Test
	public void testsQueryDescription() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testsQueryDescription");
		getSearchLuceneToSolrHelper().testsQueryDescription(Scope.forums);
	}

	@Test
	public void testsScopeParameterForums() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testsScopeParameterForums");
		getSearchLuceneToSolrHelper().testScopeParameter(Scope.forums);
	}

	@Test
	public void testPageParameterForums() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testPageParameterForums");
		getSearchLuceneToSolrHelper().testPageParameter(Scope.forums);
	}

	@Test
	public void testsSortOrderParameterDateRelevance() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testsSortOrderParameterDateRelevance");
		getSearchLuceneToSolrHelper().testsSortOrderParameterDateRelevance(Scope.forums);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterTitle() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testsSortOrderParameterTitle");
		getSearchLuceneToSolrHelper().testsSortOrderParameterTitle(Scope.forums);
	}

	@Test(groups = { "smartcloud" })
	public void searchFilesWithNotConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#searchForumsWithNotConstraintTest");
		getSearchLuceneToSolrHelper().searchFilesWithNotConstraintTest(Scope.forums);
	}

	@Test
	public void testHighlight() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrForums#testHighlight");
		getSearchLuceneToSolrHelper().testHighlight(Scope.forums);
	}

	@Test
	public void testSocialParameterUserEmail() throws Exception {
		LOGGER.fine("SearchLuceneToSolrForums#testSocialParameterUserEmail");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserEmail(Scope.forums, Purpose.SEARCH);
	}

	@Test
	public void testSocialParameterUserId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrForums#testSocialParameterUserId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserId(Scope.forums, Purpose.SEARCH);
	}

	@Test
	public void testSocialParameterCommunityId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrForums#testSocialParameterCommunityId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterCommunityId(Scope.forums, Purpose.SEARCH);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
