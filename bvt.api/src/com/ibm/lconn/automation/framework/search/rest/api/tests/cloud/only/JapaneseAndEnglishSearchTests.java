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

public class JapaneseAndEnglishSearchTests extends SearchTest {

	/** Japanese and English tests **/
	// Index "Apple in 2008 released the MacBook Air" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1 = "2008\u5E74\u306BApple\u304CMacBook Air\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	// search for text: "2008" (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_1 = "2008";
	// search for text: "Apple" (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_2 = "Apple";
	// search for text: "MacBook Air" (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_3 = "2008";
	// search for text: "2008 year" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_4 = "2008\u5E74";
	// search for text: "2008 a year" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_5 = "2008\u5E74\u306B";
	// search for text: "2008 Apple Released" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_6 = "2008Apple\u3092\u767A\u58F2\u3057";
	// search for text: "Apple but" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_7 = "Apple\u304C";
	// search for text: "MacBook Air The" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_8 = "MacBook Air\u3092";
	// search for text: "Origination" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_9 = "\u3092\u767A";
	// search for text: "released" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_10 = "\u3092\u767A\u58F2\u3057";

	// Index "Z1091115-S2 J-Win Secretariat share information field - Business
	// Support" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX2 = "Z1091115-S2 J-Win\u4E8B\u52D9\u5C40\u60C5\u5831\u5171\u6709\u306E\u5834 - \u4F01\u696D\u652F\u63F4\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	// search for text: "Business Support" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_2_1 = "\u4F01\u696D\u652F\u63F4";
	// search for text: "Z1091115-S2 J-Win Secretariat share information field -
	// Business Support" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_2_2 = "Z1091115-S2 J-Win\u4E8B\u52D9\u5C40\u60C5\u5831\u5171\u6709\u306E\u5834 - \u4F01\u696D\u652F\u63F4";

	// Index "Request Corp it" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX3 = "\u8981\u8ACBcorp it\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	// search for text: "Request Corp it"(should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_1 = "\u8981\u8ACBcorp it";
	// search for text: "request" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_2 = "\u8981\u8ACB";
	// search for text: "it" text (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_3 = "it";

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
	public void searchJapaneseAndEnglishText1_1Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_2Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_3Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_3");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_4Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_4");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_4, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_5Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_5");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_5, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_6NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_6");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_6, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_7Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_7");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_7, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText1_8Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_8");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_8, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test
	public void searchJapaneseAndEnglishText1_9Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_9");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_9, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test
	public void searchJapaneseAndEnglishText1_10Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText1_10");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_1_10, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText2_1Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText2_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_2_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX2);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test
	public void searchJapaneseAndEnglishText2_2Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText2_2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_2_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX2);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText3_1Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText3_1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX3);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText3_2Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText3_2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX3);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseAndEnglishText3_3Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseAndEnglishSearchTests#searchJapaneseAndEnglishText3_3");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_SEARCH_3_3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_AND_ENGLISH_INDEX3);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
