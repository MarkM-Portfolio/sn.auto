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

public class JapaneseBasicSearchTests extends SearchTest {

	// ACTIVITY_LANG_WORD_JAPANESE = { "I like cats", "like" };
	// Index "I like cats" in Japanese and search for "like" in Japanese (should be
	// found)
	public static String[] ACTIVITY_LANG_WORD_JAPANESE_BASIC_1_FOUND = { "\u79C1\u306F\u732B\u304C\u597D\u304D",
			"\u304C\u597D\u304D" };
	// ACTIVITY_LANG_WORD_JAPANESE_NOT_FOUND = { "", "out" };
	// Index "" in Japanese and search for "out" in Japanese (should not be found)
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String[] ACTIVITY_LANG_WORD_JAPANESE_BASIC_1_NOT_FOUND = { "", "\u79C1\u304D" };

	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_INDEX = "\u3042\u3044\u3046\u611B\u4E0A\u5CA1\u559C\u4E45\u3048\u304A\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";

	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_1_FOUND = "\u5CA1\u559C";
	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_2_NOT_FOUND = "\u3042\u3044\u3046\u3048";

	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_3_INDEX = "\u611B\u4E0A\u5CA1\u559C\u4E45\u6BDB\u500B\u5DEE\u5E02\u7D20\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_3_1_FOUND = "\u611B\u4E0A";

	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_4_INDEX = "\u30A2\u30A4\u30A6\u30A8\u30AA\u30AB\u30AD\u30AF\u30B1\u30B3\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_4_1_FOUND = "\u30A2\u30A4\u30A6\u30A8";

	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_5_INDEX = "\u30A2\u30A4\u30A6\u30A8\u30AA\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	public static String ACTIVITY_LANG_WORD_JAPANESE_BASIC_5_1_FOUND = "\u30A2\u30A4";

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
	public void searchJapaneseBasicTextFound_1_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_1_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_1_FOUND[1], "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_BASIC_1_FOUND[0]);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseBasicTextNotFound_1_2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_1_2");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQuery(ACTIVITY_LANG_WORD_JAPANESE_BASIC_1_NOT_FOUND[1], "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseBasicTextFound_2_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_2_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_1_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);

	}

	@Test
	public void searchJapaneseBasicTextFound_2_2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_2_2");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_2_2_NOT_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseBasicTextFound_3_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_3_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_3_1_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_BASIC_3_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);

	}

	@Test
	public void searchJapaneseBasicTextFound_4_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_4_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_4_1_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_BASIC_4_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);

	}

	@Test
	public void searchJapaneseBasicTextFound_5_1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseBasicSearchTests#searchJapaneseBasicTextFound_5_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_BASIC_5_1_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_BASIC_5_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);

	}

}
