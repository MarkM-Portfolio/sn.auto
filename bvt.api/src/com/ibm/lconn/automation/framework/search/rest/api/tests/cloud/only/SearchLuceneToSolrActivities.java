package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrActivities extends SearchTest {
	/**
	 * query page pageSize scope activities queryLang sortOrder SortKey
	 * date,relevance,title,dueDate constraint
	 * category-tag,category-entry,category-reply, field-tag nonconstraint
	 * category-tag highlight social query content query tag query reply (comment)
	 * query section query todo
	 */
	String test_tag = Populator.INACTIVE_TAG_FOR_UNIFY;

	@Test
	public void testsScopeParameterActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsScopeParameterActivities");
		getSearchLuceneToSolrHelper().searchFilesWithNotConstraintTest(Scope.activities);
	}

	@Test
	public void testsQueryContentActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsQueryContentActivities");
		getSearchLuceneToSolrHelper().testsQueryDescription(Scope.activities);
	}

	@Test
	public void testsQueryTagActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsQueryTagActivities");
		getSearchLuceneToSolrHelper().testsQueryTag(Scope.activities);
	}

	@Test
	public void testsQueryTodoActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsQueryTodoActivities");
		String query = "Todo+" + SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponse = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, 1);
	}

	@Test
	public void testsQuerySectionActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsQuerySectionActivities");
		String query = "Section+" + SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponse = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, 1);
	}

	@Test
	public void testsQueryReplyActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsQueryReplyActivities");
		String query = "Reply+" + SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponse = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		assertEquals("One entry is expected but actual number: " + searchResponse.getResults().size(), 1,
				searchResponse.getResults().size());
	}

	@Test
	public void testPageParameterActivities() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testPageParameterActivities");
		getSearchLuceneToSolrHelper().testPageParameter(Scope.activities);
	}

	@Test
	public void testsSortOrderParameterDateRelevance() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsSortOrderParameterDateRelevance");
		SearchResponse sortDateDesc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQueryAsExecId().setSortKey(SortKey.date).setSortOrder(SortOrder.desc)
				.buildAndExecute();
		Verifier.validateSortKeyResult(sortDateDesc, SortKey.date, SortOrder.desc);

		SearchResponse sortDateAsc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQueryAsExecId().setSortKey(SortKey.date).setSortOrder(SortOrder.asc)
				.buildAndExecute();
		Verifier.validateSortKeyResult(sortDateAsc, SortKey.date, SortOrder.asc);

		SearchResponse sortRelevanceDesc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQueryAsExecId().setSortKey(SortKey.relevance)
				.setSortOrder(SortOrder.desc).buildAndExecute();
		Verifier.validateSortKeyResult(sortRelevanceDesc, SortKey.relevance, SortOrder.desc);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterTitle() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsSortOrderParameterTitle");
		getSearchLuceneToSolrHelper().testsSortOrderParameterTitle(Scope.activities);
	}

	@Test(groups = { "smartcloud" })
	public void testsSortOrderParameterDueDate() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testsSortOrderParameterDueDate");

		SearchResponse sortDueDateAsc = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQueryAsExecId().setSortKey(SortKey.due_date).setSortOrder(SortOrder.asc)
				.buildAndExecute();

		assertFalse("Search result for sort by DueDate  is empty", sortDueDateAsc.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortDueDateAsc, SortKey.due_date, SortOrder.asc);
	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithCategoryTagEntryReplyConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#searchUnifyActivitiesWithCategoryTagEntryReplyConstraintTest");

		CategoryConstraint categoryConstraintReply = new CategoryConstraint(
				new String[][] { Populator.UNIFY_ACTIVITY_REPLY_CATEGORY });

		CategoryConstraint[] valuesReply = new CategoryConstraint[] { categoryConstraintReply };

		String query = SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponseReply = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).setCategoryConstraints(valuesReply).buildAndExecute();

		Verifier.verifyUnifyActivityScope(searchResponseReply, Scope.activities_reply);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseReply, 1);

		CategoryConstraint categoryConstraintEntry = new CategoryConstraint(
				new String[][] { Populator.UNIFY_ACTIVITY_ENTRY_CATEGORY });

		CategoryConstraint[] valuesEntry = new CategoryConstraint[] { categoryConstraintEntry };

		SearchResponse searchResponseEntry = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).setCategoryConstraints(valuesEntry).buildAndExecute();

		Verifier.verifyUnifyActivityScope(searchResponseEntry, Scope.activities_entry);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseEntry, Populator.UNIFY_ENTRIES_NUMBER);

		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", test_tag } });
		CategoryConstraint[] CategoryConstraintTag = new CategoryConstraint[] { categoryConstraint };

		SearchResponse searchResponseWithTag = getSearchLuceneToSolrHelper().getSearchRequestHelper().getBuilder()
				.setScope(Scope.activities).setQuery(query).setCategoryConstraints(CategoryConstraintTag)
				.buildAndExecute();

		assertTrue("Tag  not found " + test_tag + " in respond:" + searchResponseWithTag.toString(),
				Verifier.verifyTagByName(searchResponseWithTag, test_tag));

		Verifier.verifyEntriesScope(searchResponseWithTag, Scope.activities);

	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithFieldConstraintsTagTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#searchUnifyActivitiesWithFieldConstraintsTagTest");
		getSearchLuceneToSolrHelper().testSearchWithFieldConstraintsTag(Scope.activities, test_tag, Purpose.UNIFY);
	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithNotConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#searchUnifyActivitiesWithNotConstraintTest");
		getSearchLuceneToSolrHelper().searchFilesWithNotConstraintTest(Scope.activities, test_tag);
	}

	@Test
	public void testHighlight() throws Exception {
		LOGGER.fine("Test SearchLuceneToSolrActivities#testHighlight");
		getSearchLuceneToSolrHelper().testHighlight(Scope.activities);
	}

	@Test
	public void testSocialParameterUserEmail() throws Exception {
		LOGGER.fine("SearchLuceneToSolrActivities#testSocialParameterUserEmail");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserEmail(Scope.activities);
	}

	@Test
	public void testSocialParameterUserId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrActivities#testSocialParameterUserId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterUserId(Scope.activities);
	}

	@Test
	public void testSocialParameterCommunityId() throws Exception {
		LOGGER.fine("SearchLuceneToSolrActivities#testSocialParameterCommunityId");
		getSearchLuceneToSolrHelper().getSocialHelper().testSocialParameterCommunityId(Scope.activities);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
