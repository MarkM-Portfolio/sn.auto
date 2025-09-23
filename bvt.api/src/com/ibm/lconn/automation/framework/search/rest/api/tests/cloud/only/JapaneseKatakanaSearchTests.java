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

public class JapaneseKatakanaSearchTests extends SearchTest {

	/** Katakana tests **/
	// Index "To participate in the manager training" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_INDEX = "\u30DE\u30CD\u30FC\u30B8\u30E3\u30FC\u7814\u4FEE\u306B\u53C2\u52A0\u3059\u308B";
	// search for katakana text: "training" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_SEARCH1 = "\u7814\u4FEE";
	// search for katakana text: "training manager" in Japanese (should NOT be
	// found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_SEARCH2 = "\u7814\u4FEE\u30DE\u30CD\u30FC\u30B8\u30E3\u30FC";

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
	public void searchJapaneseKatakanaText1Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKatakanaSearchTests#searchJapaneseKatakanaText1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_SEARCH1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKatakanaText2NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKatakanaSearchTests#searchJapaneseKatakanaText2");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KATAKANA_SEARCH2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

}
