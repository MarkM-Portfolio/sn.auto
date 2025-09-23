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

public class JapaneseKanjiAndHiraganaAndKatakanaSearchTests extends SearchTest {

	/** Japanese text that includes Kanji + Hiragana + katakana tests **/
	// Index "eraser" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX1 = "\u6D88\u3057\u30B4\u30E0\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_1_1 = "\u3057\u30B4";
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_1_2 = "\u6D88\u30E0";

	// Index "ninja" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX2 = "\u304F\u30CE\u4E00\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_2_1 = "\u30CE\u4E00";
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_2_2 = "\u304F\u4E00";

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
	public void searchJapaneseKanjiAndHiraganaAndKatakanaText1_1Found() throws IOException, Exception {
		LOGGER.fine(
				"Test searchJapaneseKanjiAndHiraganaAndKatakanaTests#searchJapaneseKanjiAndHiraganaAndKatakanaText1_1Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_1_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse,
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaAndKatakanaText1_2NotFound() throws IOException, Exception {
		LOGGER.fine(
				"Test searchJapaneseKanjiAndHiraganaAndKatakanaTests#searchJapaneseKanjiAndHiraganaAndKatakanaText1_2NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_1_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test
	public void searchJapaneseKanjiAndHiraganaAndKatakanaText2_1Found() throws IOException, Exception {
		LOGGER.fine(
				"Test searchJapaneseKanjiAndHiraganaAndKatakanaTests#searchJapaneseKanjiAndHiraganaAndKatakanaText2_1Found");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_2_1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse,
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiAndHiraganaAndKatakanaText2_2NotFound() throws IOException, Exception {
		LOGGER.fine(
				"Test searchJapaneseKanjiAndHiraganaAndKatakanaTests#searchJapaneseKanjiAndHiraganaAndKatakanaText2_2NotFound");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_KANJI_AND_HIRAGANA_AND_KATAKANA_SEARCH_2_2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
