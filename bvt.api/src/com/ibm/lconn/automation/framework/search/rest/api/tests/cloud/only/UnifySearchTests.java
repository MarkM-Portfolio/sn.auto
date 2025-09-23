package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class UnifySearchTests extends SearchTest {

	public static final int START_UNIFY = 1;

	@BeforeClass
	public void beforeClass() {
		Populator.UNIFY_RESULTS = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.activity,
				Purpose.UNIFY);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

	@Test(groups = { "smartcloud" })
	public void searchRequestEngineParamIsSolrInResultTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchRequestEngineParamIsSolrInResultTest");
		LOGGER.fine("count:" + Populator.UNIFY_RESULTS);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		SearchResponse searchResponse = executeQuery(searchRequest);

		assertEquals(SearchRequestEngine.Solr, searchResponse.getSearchRequestEngine());
	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithNotConstraintTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchUnifyActivitiesWithNotConstraintTest");
		int ALL_UNIFY_RESULTS = Populator.UNIFY_RESULTS + Populator.UNIFY_ENTRIES_NUMBER
				+ Populator.UNIFY_REPLIES_NUMBER + Populator.UNIFY_SECTIONS_NUMBER + Populator.UNIFY_TODOS_NUMBER;
		String query = SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", Populator.INACTIVE_TAG_FOR_UNIFY } });
		CategoryConstraint[] valuesTag = new CategoryConstraint[] { categoryConstraint };
		SearchResponse searchResponseTag = prepareAndExecuteSearchUnifyQueryWithConstraint(query, valuesTag);

		assertTrue("Tag not found " + Populator.INACTIVE_TAG_FOR_UNIFY,
				Verifier.verifyTagByName(searchResponseTag, Populator.INACTIVE_TAG_FOR_UNIFY));

		SearchResponse searchResponseNotTag = prepareAndExecuteSearchUnifyQueryWithNotConstraint(categoryConstraint);

		assertTrue("Tag  found " + Populator.INACTIVE_TAG_FOR_UNIFY + " in respond:" + searchResponseNotTag.toString(),
				Verifier.verifyNoTagByName(searchResponseNotTag, Populator.INACTIVE_TAG_FOR_UNIFY));
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseNotTag, ALL_UNIFY_RESULTS - 1);

		Verifier.verifyEntriesScope(searchResponseNotTag, Scope.activities);

		assertEquals(SearchRequestEngine.Solr, searchResponseNotTag.getSearchRequestEngine());
	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithFieldConstraintsTagTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchUnifyActivitiesWithFieldConstraintsTagTest");

		FieldConstraint[] fieldConstraintsTag = new FieldConstraint[] {
				new FieldConstraint("tag", new String[] { Populator.INACTIVE_TAG_FOR_UNIFY }, true) };

		SearchResponse searchResponseTag = prepareAndExecuteSearchUnifyQueryWithFieldConstraint(fieldConstraintsTag);
		Verifier.verifyUnifyActivityScope(searchResponseTag, Scope.activities);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseTag, 1);
		assertTrue("Tag " + Populator.INACTIVE_TAG_FOR_UNIFY + " not found in respond:" + searchResponseTag.toString(),
				Verifier.verifyTagByName(searchResponseTag, Populator.INACTIVE_TAG_FOR_UNIFY));

		assertEquals(SearchRequestEngine.Solr, searchResponseTag.getSearchRequestEngine());

	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithConstraintsEntryTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchUnifyActivitiesWithConstraintsEntryTest");

		CategoryConstraint categoryConstraintEntry = new CategoryConstraint(
				new String[][] { Populator.UNIFY_ACTIVITY_ENTRY_CATEGORY });

		CategoryConstraint[] valuesEntry = new CategoryConstraint[] { categoryConstraintEntry };
		String query = SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponseEntry = prepareAndExecuteSearchUnifyQueryWithConstraint(query, valuesEntry);
		Verifier.verifyUnifyActivityScope(searchResponseEntry, Scope.activities_entry);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseEntry, Populator.UNIFY_ENTRIES_NUMBER);

		assertEquals(SearchRequestEngine.Solr, searchResponseEntry.getSearchRequestEngine());

	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithConstraintsReplyTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchUnifyActivitiesWithConstraintsReplyTest");

		CategoryConstraint categoryConstraintReply = new CategoryConstraint(
				new String[][] { Populator.UNIFY_ACTIVITY_REPLY_CATEGORY });

		CategoryConstraint[] valuesReply = new CategoryConstraint[] { categoryConstraintReply };
		String query = SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		SearchResponse searchResponseReply = prepareAndExecuteSearchUnifyQueryWithConstraint(query, valuesReply);
		Verifier.verifyUnifyActivityScope(searchResponseReply, Scope.activities_reply);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseReply, Populator.UNIFY_REPLIES_NUMBER);

		assertEquals(SearchRequestEngine.Solr, searchResponseReply.getSearchRequestEngine());

	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesWithConstraintsAllTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchUnifyActivitiesWithConstraintsAllTest");
		int ALL_UNIFY_RESULTS = Populator.UNIFY_RESULTS + Populator.UNIFY_ENTRIES_NUMBER
				+ Populator.UNIFY_REPLIES_NUMBER;

		CategoryConstraint categoryConstraintAll = new CategoryConstraint(
				new String[][] { Populator.UNIFY_ACTIVITY_REPLY_CATEGORY, Populator.UNIFY_ACTIVITY_ENTRY_CATEGORY,
						Populator.UNIFY_ACTIVITY_CATEGORY });
		String query = SearchRestAPIUtils.getExecId(Purpose.UNIFY);
		CategoryConstraint[] valuesAll = new CategoryConstraint[] { categoryConstraintAll };

		SearchResponse searchResponseAll = prepareAndExecuteSearchUnifyQueryWithConstraint(query, valuesAll);
		Verifier.verifyEntriesScope(searchResponseAll, Scope.activities);
		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponseAll, ALL_UNIFY_RESULTS);

		assertEquals(SearchRequestEngine.Solr, searchResponseAll.getSearchRequestEngine());

	}

	@Test(groups = { "smartcloud" })
	public void searchAllUnifyActivitiesTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchAllUnifyActivitiesTest");
		int ALL_UNIFY_RESULTS = Populator.UNIFY_RESULTS + Populator.UNIFY_ENTRIES_NUMBER
				+ Populator.UNIFY_REPLIES_NUMBER + Populator.UNIFY_SECTIONS_NUMBER + Populator.UNIFY_TODOS_NUMBER;

		SearchResponse searchResponse = prepareAndExecuteSearchAllUnifyQuery();

		Verifier.verifyNumberOfReceivedEntries(searchResponse, ALL_UNIFY_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.activities, Permissions.PRIVATE,
				Purpose.UNIFY);

		assertEquals(SearchRequestEngine.Solr, searchResponse.getSearchRequestEngine());
	}

	@Test(groups = { "smartcloud" })
	public void searchUnifyActivitiesPagesTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#testsPageStartParameter");
		int ALL_UNIFY_RESULTS = Populator.UNIFY_RESULTS + Populator.UNIFY_ENTRIES_NUMBER
				+ Populator.UNIFY_REPLIES_NUMBER + Populator.UNIFY_SECTIONS_NUMBER + Populator.UNIFY_TODOS_NUMBER;
		int PAGE_UNIFY_SIZE = Math.round(ALL_UNIFY_RESULTS / 2) + 1;
		SearchResponse searchResponse = prepareAndExecuteSearchAllUnifyQuery();
		Verifier.verifyNumberOfReceivedEntries(searchResponse, ALL_UNIFY_RESULTS);
		assertEquals(SearchRequestEngine.Solr, searchResponse.getSearchRequestEngine());

		SearchResponse searchResponsePage1 = prepareAndExecuteSearchPageUnifyQuery(1, PAGE_UNIFY_SIZE);
		Verifier.verifyNumberOfReceivedEntries(searchResponsePage1, PAGE_UNIFY_SIZE);
		assertEquals(searchResponsePage1.getSearchRequestEngine(), SearchRequestEngine.Solr);

		SearchResponse searchResponsePage2 = prepareAndExecuteSearchPageUnifyQuery(2, PAGE_UNIFY_SIZE);
		Verifier.verifyNumberOfReceivedEntries(searchResponsePage2, ALL_UNIFY_RESULTS - PAGE_UNIFY_SIZE);
		assertEquals(searchResponsePage2.getSearchRequestEngine(), SearchRequestEngine.Solr);

		SearchResponse searchResponsePage3 = prepareAndExecuteSearchStartUnifyQuery();
		Verifier.verifyNumberOfReceivedEntries(searchResponsePage3, ALL_UNIFY_RESULTS - START_UNIFY);

		assertEquals(SearchRequestEngine.Solr, searchResponsePage3.getSearchRequestEngine());
	}

	@Test(groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestThink() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestThink");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQuery(Populator.UNIFY_ACTIVITY_LANG_WORD, "en");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test(groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestBrought() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestBrought");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQuery(Populator.UNIFY_ACTIVITY_LANG_WORD_TWO,
				"en");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD_TWO[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test(groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestShort() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestShort");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQuery(Populator.UNIFY_ACTIVITY_LANG_WORD_THREE,
				"en");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD_THREE[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test(groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestUnicodeAnd2Words() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestUnicode");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQuery(Populator.UNIFY_ACTIVITY_LANG_WORD_FOUR,
				"en");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);

		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD_FOUR[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test (groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestGermanOne() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestGermanOne");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQuery(Populator.UNIFY_ACTIVITY_LANG_WORD_GERMAN,
				"de");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD_GERMAN[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test (groups = { "smartcloud" })
	public void searchLangUnifyActivitiesTestGermanSec() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchLangUnifyActivitiesTestGermanSec");
		int UNIFY_LANG_RESULTS = 1;

		SearchResponse searchResponse = prepareAndExecuteSearchLangUnifyQueryTitle(
				Populator.UNIFY_ACTIVITY_LANG_WORD_GERMAN_SEC, "de");

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, UNIFY_LANG_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, Populator.UNIFY_ACTIVITY_LANG_WORD_GERMAN_SEC[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	private SearchResponse prepareAndExecuteSearchAllUnifyQuery() throws Exception, IOException {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#executeQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);

		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchPageUnifyQuery(int page, int pageSize) throws Exception, IOException {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#executePageQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setPageSize(pageSize);
		searchRequest.setPage(page);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);

		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchUnifyQueryWithNotConstraint(CategoryConstraint categoryConstraint)
			throws Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#prepareAndExecuteSearchUnifyQueryWithNotConstraint");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setCategoryNotConstraints(new CategoryConstraint[] { categoryConstraint });
		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchUnifyQueryWithConstraint(String query,
			CategoryConstraint[] categoryConstraintValues) throws Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#prepareAndExecuteSearchUnifyQueryWithConstraint");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(query);
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setCategoryConstraints(categoryConstraintValues);
		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchUnifyQueryWithFieldConstraint(FieldConstraint[] fieldConstraints)
			throws Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#prepareAndExecuteSearchUnifyQueryWithFieldConstraint");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setFieldConstraints(fieldConstraints);
		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchStartUnifyQuery() throws Exception, IOException {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#executeStartQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.UNIFY));
		searchRequest.setStart(START_UNIFY);
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);

		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchLangUnifyQuery(String[] langTestWord, String lang)
			throws Exception, IOException {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#prepareAndExecuteSearchLangUnifyQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(URLEncoder.encode(
				StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.UNIFY)) + " " + langTestWord[1], "UTF-8"));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setQueryLang(lang);
		searchRequest.setHighlight(new String[0]);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		return executeQuery(searchRequest);
	}

	private SearchResponse prepareAndExecuteSearchLangUnifyQueryTitle(String[] langTestWord, String lang)
			throws Exception, IOException {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#prepareAndExecuteSearchLangUnifyQuery");

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(
				URLEncoder.encode(SearchRestAPIUtils.getExecId(Purpose.UNIFY) + " " + langTestWord[1], "UTF-8"));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setQueryLang(lang);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		return executeQuery(searchRequest);
	}
}
