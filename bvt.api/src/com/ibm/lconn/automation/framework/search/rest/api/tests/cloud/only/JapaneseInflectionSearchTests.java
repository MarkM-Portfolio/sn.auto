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

public class JapaneseInflectionSearchTests extends SearchTest {

	// Index "buying" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX1 = "\u8CB7\u3044\u53D6\u308A";
	// Index "not buy" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX2 = "\u8CB7\u308F\u306A\u3044";
	// Index "bought" in Japanese
	public static String ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX3 = "\u304B\u3063\u305F";
	// search for "buy" (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH = "\u8CFC\u5165";

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
	public void searchJapaneseInflectionTextFound1() throws IOException, Exception {
		LOGGER.fine("Test JapaneseInflectionSearchTests#searchJapaneseInflectionTextFound1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX1);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseInflectionTextFound2() throws IOException, Exception {
		LOGGER.fine("Test JapaneseInflectionSearchTests#searchJapaneseInflectionTextFound2");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX2);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseInflectionTextFound3() throws IOException, Exception {
		LOGGER.fine("Test JapaneseInflectionSearchTests#searchJapaneseInflectionTextFound3");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_INFLECTION_INDEX3);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}
}
