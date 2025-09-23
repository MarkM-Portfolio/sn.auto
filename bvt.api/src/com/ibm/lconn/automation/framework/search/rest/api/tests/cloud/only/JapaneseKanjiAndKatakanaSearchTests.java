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

public class JapaneseKanjiAndKatakanaSearchTests extends SearchTest {

	/** Japanese text that includes Kanji + Katakana tests **/
	// Index "I spilled coffee in my car" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_INDEX1 = "\u79C1\u306F\u81EA\u5206\u306E\u8ECA\u306E\u4E2D\u3067\u30B3\u30FC\u30D2\u30FC\u3092\u3053\u307C\u3057\u307E\u3057\u305F";
	// search for text: "Coffee" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_1 = "\u30B3\u30FC\u30D2\u30FC";
	// search for text: "my car" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_2 = "\u81EA\u5206\u306E\u8ECA";
	// search for text: "car my" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_3 = "\u306E\u8ECA\u81EA\u5206";
	// search for text: "car spilled" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_4 = "\u306E\u8ECA\u3092\u3053\u307C\u3057\u307E\u3057";
	// search for text: "spilled car" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_5 = "\u3092\u3053\u307C\u3057\u307E\u3057\u306E\u8ECA";

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
	public void searchJapaneseKanjiAndKatakanaText1Found() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndKatakanaTests#searchJapaneseKanjiAndKatakanaText_1Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndKatakanaText2Found() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndKatakanaTests#searchJapaneseKanjiAndKatakanaText_2Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndKatakanaText_3NotFound() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndKatakanaTests#searchJapaneseKanjiAndKatakanaText_3NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndKatakanaText4Found() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndKatakanaTests#searchJapaneseKanjiAndKatakanaText_4Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_4, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndKatakanaText_5NotFound() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndKatakanaTests#searchJapaneseKanjiAndKatakanaText_5NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_KATAKANA_SEARCH_5, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
