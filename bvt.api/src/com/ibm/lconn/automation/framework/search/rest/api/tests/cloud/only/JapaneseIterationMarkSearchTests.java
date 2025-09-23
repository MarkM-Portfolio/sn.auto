package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class JapaneseIterationMarkSearchTests extends SearchTest {

	// Index "sometimes" with iteration mark in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX1 = "\u6642\u3005";
	// Index "people" with iteration mark in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX2 = "\u4EBA\u3005";

	// search for "sometimes" without iteration mark but with duplicated letters
	// (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_SEARCH1 = "\u6642\u6642";
	// search for "people" without iteration mark but with duplicated letters
	// (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_SEARCH2 = "\u4EBA\u4EBA";

	public int SEARCH_RESULTS;

	@BeforeClass
	public void beforeClass() {
		SEARCH_RESULTS = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.activity, Purpose.SEARCH);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

	@Test
	public void searchJapaneseIterationMarkTextFound1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseIterationMarkSearchTests#searchJapaneseIterationMarkTextFound1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_SEARCH1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseIterationMarkTextFound2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseIterationMarkSearchTests#searchJapaneseIterationMarkTextFound2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_SEARCH2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_ITERATION_MARK_INDEX2);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}
}