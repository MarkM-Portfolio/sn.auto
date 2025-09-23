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

public class JapaneseEndVowelSearchTests extends SearchTest {

	// Index "manager" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX1 = "\u30DE\u30CD\u30FC\u30B8\u30E3\u30FC";
	// Index "number1" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX2 = "\u30CA\u30F3\u30D0\u30FC1C";
	// Index "cake" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX3 = "\u30B1\u30FC\u30AD";

	// search for "manager" with end vowel1 (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_1 = "\u30DE\u30CD\u30FC\u30B8\u30E3";
	// search for "manager" with end vowel2 (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_2 = "\u30DE\u30CD\u30B8\u30E3";
	// search for "manager" with end vowel3 (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_3 = "\u30DE\u30CD\u30B8\u30E3\u30FC";
	// search for "number1" with end vowel (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH2 = "\u30CA\u30F3\u30D01";
	// search for "cake" with end vowel (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH3 = "\u30B1\u30AD";

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
	public void searchJapaneseEndVowelTextFound1_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseEndVowelSearchTests#searchJapaneseEndVowelTextFound1_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseEndVowelTextFound1_2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseEndVowelSearchTests#searchJapaneseEndVowelTextFound1_2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseEndVowelTextFound1_3() throws IOException, Exception {
		LOGGER.fine("Test JapaneseEndVowelSearchTests#searchJapaneseEndVowelTextFound1_3");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH_1_3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseEndVowelTextFound2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseEndVowelSearchTests#searchJapaneseEndVowelTextFound2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX2);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseEndVowelTextFound3() throws IOException, Exception {
		LOGGER.fine("Test JapaneseEndVowelSearchTests#searchJapaneseEndVowelTextFound3");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_SEARCH3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_END_VOWEL_INDEX3);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}