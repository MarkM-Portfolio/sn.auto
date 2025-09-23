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

public class JapaneseHiraganaSearchTests extends SearchTest {

	/** Hiragana tests **/
	// Index "To formulate an action plan to promote the efficiency of the
	// line-of-business" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_INDEX = "\u57FA\u5E79\u696D\u52D9\u306E\u52B9\u7387\u5316\u3092\u63A8\u9032\u3059\u308B\u305F\u3081\u306E\u884C\u52D5\u8A08\u753B\u3092\u7ACB\u6848\u3059\u308B";
	// search for hiragana text: "animation" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH1 = "\u52D5\u753B";
	// search for hiragana text: "promote the efficiency" in Japanese (should NOT be
	// found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH2 = "\u63A8\u9032\u3059\u308B\u305F\u3081\u306E\u52B9\u7387\u5316\u3092";
	// search for hiragana text: "Dried squid" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH3 = "\u3059\u308B\u3081";
	// search for hiragana text: "the efficiency" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH4 = "\u52B9\u7387\u5316\u3092";

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
	public void searchJapaneseHiraganaTextSearch1NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHiraganaSearchTests#searchJapaneseHiraganaTextSearch1");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseHiraganaTextSearch2NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHiraganaSearchTests#searchJapaneseHiraganaTextSearch2");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseHiraganaTextSearch3NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHiraganaSearchTests#searchJapaneseHiraganaTextSearch3");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseHiraganaTextSearch4Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseHiraganaSearchTests#searchJapaneseHiraganaTextSearch4");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_SEARCH4, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_HIRAGANA_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
