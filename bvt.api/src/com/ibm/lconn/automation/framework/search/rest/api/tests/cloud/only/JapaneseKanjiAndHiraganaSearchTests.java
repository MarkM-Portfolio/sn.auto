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

public class JapaneseKanjiAndHiraganaSearchTests extends SearchTest {

	/** Japanese text that includes Kanji + Hiragana tests **/
	// Index "I was engrossed in a novel and forgot I had arranged to meet my
	// friend" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_INDEX1 = "\u5C0F\u8AAC\u306B\u5922\u4E2D\u306B\u306A\u3063\u3066\u53CB\u9054\u3068\u306E\u5F85\u3061\u5408\u308F\u305B\u3092\u5FD8\u308C\u307E\u3057\u305F";
	// search for text: "In novel" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_1 = "\u5C0F\u8AAC\u306B";
	// search for text: "novel in" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_2 = "\u306B\u5C0F\u8AAC";
	// search for text: "meetup friend" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_3 = "\u3068\u306E\u5F85\u3061\u5408\u308F\u305B\u53CB\u9054";
	// search for text: "forgot friend" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_4 = "\u3092\u5FD8\u308C\u307E\u3057\u305F\u53CB\u9054";
	// search for text: "friend" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_5 = "\u53CB\u9054";

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
	public void searchJapaneseKanjiAndHiraganaText1Found() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndHiraganaTests#searchJapaneseKanjiAndHiraganaText_1Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaText_2NotFound() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndHiraganaTests#searchJapaneseKanjiAndHiraganaText_2NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaText_3NotFound() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndHiraganaTests#searchJapaneseKanjiAndHiraganaText_3NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaText_4NotFound() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndHiraganaTests#searchJapaneseKanjiAndHiraganaText_4NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_4, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaText5Found() throws IOException, Exception {
		LOGGER.fine("Test searchJapaneseKanjiAndHiraganaTests#searchJapaneseKanjiAndHiraganaText_5Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_SEARCH_5, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
