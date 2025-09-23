package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;

public class SearchLuceneToSolrWikis extends SearchTest {
	/**
	 * query page pageSize scope wikis queryLang sortOrder SortKey
	 * date,relevance,title constraint category-tag,category-source-wikis,
	 * field-tag,category-page nonconstraint category-tag highlight social - userId,
	 * userEmail, communityId
	 */

	String test_tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH);
	String[] wikis_category = { "Source", "wikis" };
	String[] wikis_page_category = { "Source", "wikis", "page" };

	@Test
	public void testsQueryTag() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testsQueryTag");
		getSearchLuceneToSolrHelper().testsQueryTag(Scope.wikis);
	}

	@Test
	public void testsQueryDescription() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testsQueryDescription");
		getSearchLuceneToSolrHelper().testsQueryDescription(Scope.wikis);
	}

	@Test
	public void testsScopeParameterWikis() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testsScopeParameterWikis");
		getSearchLuceneToSolrHelper().testScopeParameter(Scope.wikis);
	}

	@Test
	public void testPageParameterWikis() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testPageParameterWikis");
		getSearchLuceneToSolrHelper().testPageParameter(Scope.wikis);
	}

	@Test
	public void testsSortOrderParameterDateRelevance() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testsSortOrderParameterDateRelevance");
		getSearchLuceneToSolrHelper().testsSortOrderParameterDateRelevance(Scope.wikis);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterTitle() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testsSortOrderParameterTitle");
		getSearchLuceneToSolrHelper().testsSortOrderParameterTitle(Scope.wikis);
	}

	@Test(groups = { "smartcloud" })
	public void searchWikisWithCategorySourceAndTagConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#searchFilesWithCategoryTagConstraintTest");
		getSearchLuceneToSolrHelper().testSearchWithCategorySourceAndTagConstraint(Scope.wikis, wikis_category,
				wikis_page_category);
	}

	@Test(groups = { "smartcloud" })
	public void searchWikisWithFieldConstraintsTagTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#searchWikisWithFieldConstraintsTagTest");
		getSearchLuceneToSolrHelper().testSearchWithFieldConstraintsTag(Scope.wikis);
	}

	@Test(groups = { "smartcloud" })
	public void searchFilesWithNotConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#searchWikisWithNotConstraintTest");
		getSearchLuceneToSolrHelper().searchFilesWithNotConstraintTest(Scope.wikis);
	}

	@Test
	public void testHighlight() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrWikis#testHighlight");
		getSearchLuceneToSolrHelper().testHighlight(Scope.wikis);
	}

	@Test
	public void testSocialParameterUserEmail() throws Exception {
		LOGGER.fine("SearchLuceneToSolrWikis#testSocialParameterUserEmail");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserEmail(Scope.wikis, Purpose.SEARCH);
	}

	@Test
	public void testSocialParameterUserId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrWikis#testSocialParameterUserId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserId(Scope.wikis, Purpose.SEARCH);
	}

	@Test
	public void testSocialParameterCommunityId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrWikis#testSocialParameterCommunityId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterCommunityId(Scope.wikis, Purpose.SEARCH);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
