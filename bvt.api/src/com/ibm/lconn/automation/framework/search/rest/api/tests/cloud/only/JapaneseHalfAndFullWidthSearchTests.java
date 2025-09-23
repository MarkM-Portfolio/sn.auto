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

public class JapaneseHalfAndFullWidthSearchTests extends SearchTest {

	/** Full Width/Half Width tests **/
	// Index Japanese katakana full width
	public static String ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_INDEX = "\u30AF\u30FC\u30E9\u30FC\u30DC\u30C3\u30AF\u30B9\u3001\u30C4\u30FC\u30D0\u30FC\u30CA\u30FC";
	// search for half width Japanese katakana text
	public static String ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_KATAKANA_SEARCH_FOUND = "\uFF78\uFF70\uFF97\uFF70\uFF8E\uFF9E\uFF6F\uFF78\uFF7D\uFF64\uFF82\uFF70\uFF8A\uFF9E\uFF70\uFF85\uFF70";

	// Index Japanese katakana half width
	public static String ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_KATAKANA_INDEX = "\u5168\u89D2\uFF76\uFF80\uFF76\uFF85";
	// search for full width Japanese katakana text
	public static String ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_SEARCH_KATAKANA_FOUND = "\u5168\u89D2\u30AB\u30BF\u30AB";

	// Index Japanese katakana and Latin full width
	public static String ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_AND_LATIN_INDEX = "\u30E9\u30F3\u30BF\u30F3\u30B9\u30BF\u30F3\u30C9\u3001\u30B3\u30FC\u30D2\u30FC\u30E1\u30FC\u30AB\u30FC\u3001\uFF22\uFF22\uFF31\u30B0\u30EA\u30EB";
	// search for half width Japanese Latin text
	public static String ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_SEARCH_LATIN_FOUND = "BBQ";
	// search for half width Katakana Japanese Latin text
	public static String ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_SEARCH_KATAKANA_FOUND = "\uFF97\uFF9D\uFF80\uFF9D\uFF7D\uFF80\uFF9D\uFF84\uFF9E";

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
	public void searchJapaneseKatakanaHalfWidthSearchFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHalfAndFullWidthSearchTests#searchJapaneseKatakanaHalfWidthSearchFound");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_KATAKANA_SEARCH_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKatakanaFullWidthSearchFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHalfAndFullWidthSearchTests#searchJapaneseKatakanaFullWidthSearchFound");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_SEARCH_KATAKANA_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_KATAKANA_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseLatinHalfWidthSearchFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHalfAndFullWidthSearchTests#searchJapaneseLatinHalfWidthSearchFound");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_SEARCH_LATIN_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse,
				ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_AND_LATIN_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKatakanaAndLatinHalfWidthSearchFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHalfAndFullWidthSearchTests#searchJapaneseKatakanaAndLatinHalfWidthSearchFound");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils.prepareAndExecuteSearchLangQueryTitle(
				ACTIVITY_LANG_WORD_JAPANESE_HALF_WIDTH_SEARCH_KATAKANA_FOUND, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse,
				ACTIVITY_LANG_WORD_JAPANESE_FULL_WIDTH_KATAKANA_AND_LATIN_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
