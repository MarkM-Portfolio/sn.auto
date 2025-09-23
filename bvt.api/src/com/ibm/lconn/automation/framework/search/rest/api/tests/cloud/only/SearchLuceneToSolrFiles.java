package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrFiles extends SearchTest {
	/**
	 * query page pageSize scope files queryLang sortOrder SortKey
	 * date,relevance,title constraint category-tag,category-source-files, field-tag
	 * nonconstraint category-tag highlight
	 */
	String test_tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH);
	String[] file_category = { "Source", "files" };

	@Test
	public void testsScopeParameterFiles() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testsScopeParameterFiles");
		getSearchLuceneToSolrHelper().testScopeParameter(Scope.files);
	}

	@Test
	public void testsQueryTag() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testsQueryTag");
		getSearchLuceneToSolrHelper().testsQueryTag(Scope.files);
	}

	@Test
	public void testsQueryDescription() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testsQueryDescription");
		getSearchLuceneToSolrHelper().testsQueryDescription(Scope.files);
	}

	@Test
	public void testPageParameterFiles() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testPageParameterFiles");
		getSearchLuceneToSolrHelper().testPageParameter(Scope.files);
	}

	@Test
	public void testsSortOrderParameterDateRelevance() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testsSortOrderParameterDateRelevance");
		SearchResponse sortDateDesc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.files).setQueryAsExecId().setSortKey(SortKey.date).setSortOrder(SortOrder.desc)
				.buildAndExecute();
		Verifier.validateSortKeyResult(sortDateDesc, SortKey.date, SortOrder.desc);

		SearchResponse sortDateAsc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.files).setSortKey(SortKey.date).setSortOrder(SortOrder.asc).buildAndExecute();
		Verifier.validateSortKeyResult(sortDateAsc, SortKey.date, SortOrder.asc);

		SearchResponse sortRelevanceDesc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.files).setSortKey(SortKey.relevance).setSortOrder(SortOrder.desc).buildAndExecute();
		Verifier.validateSortKeyResult(sortRelevanceDesc, SortKey.relevance, SortOrder.desc);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterTitle() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testsSortOrderParameterTitle");
		getSearchLuceneToSolrHelper().testsSortOrderParameterTitle(Scope.files);
	}

	@Test(groups = { "smartcloud" })
	public void searchFilesWithCategorySourceAndTagConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#searchFilesWithCategoryTagConstraintTest");

		CategoryConstraint categoryConstraintSource = new CategoryConstraint(new String[][] { file_category });

		CategoryConstraint[] values = new CategoryConstraint[] { categoryConstraintSource };

		SearchResponse searchResponseSourceFiles = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.files).setCategoryConstraints(values).buildAndExecute();

		Verifier.verifyUnifyActivityScope(searchResponseSourceFiles, Scope.files);

		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", test_tag } });
		CategoryConstraint[] CategoryConstraintTag = new CategoryConstraint[] { categoryConstraint };
		SearchResponse searchResponseWithTag = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.files).setCategoryConstraints(CategoryConstraintTag).buildAndExecute();
		assertTrue("Tag  not found " + test_tag + " in respond:" + searchResponseWithTag.toString(),
				Verifier.verifyTagByName(searchResponseWithTag, test_tag));
		Verifier.verifyEntriesScope(searchResponseWithTag, Scope.files);
	}

	@Test(groups = { "smartcloud" })
	public void searchFilesWithFieldConstraintsTagTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#searchFilesWithFieldConstraintsTagTest");
		getSearchLuceneToSolrHelper().testSearchWithFieldConstraintsTag(Scope.files);
	}

	@Test(groups = { "smartcloud" })
	public void searchFilesWithNotConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#searchFilesWithNotConstraintTest");
		getSearchLuceneToSolrHelper().searchFilesWithNotConstraintTest(Scope.files);
	}

	@Test
	public void testHighlight() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrFiles#testHighlight");
		getSearchLuceneToSolrHelper().testHighlight(Scope.files);

	}

	@Test
	public void testSocialParameterUserEmail() throws Exception {
		LOGGER.fine("SearchLuceneToSolrFiles#testSocialParameterUserEmail");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserEmail(Scope.files);
	}

	@Test
	public void testSocialParameterUserId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrFiles#testSocialParameterUserId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserId(Scope.files);
	}

	@Test
	public void testSocialParameterCommunityId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrFiles#testSocialParameterCommunityId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterCommunityId(Scope.files);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
