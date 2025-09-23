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

public class JapaneseKanjiSearchTests extends SearchTest {

	/** Kanji tests **/
	// Index "Matsushita Electric Industrial Co., Ltd. mission-critical information
	// technology division regular meeting minutes" in Japanese
	// (since there is kanji text here and it identifies as Chinese, I added
	// katakana letters to the text in order to make it identified as Japanese)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_INDEX = "\u677E\u4E0B\u96FB\u5668\u7523\u696D\u682A\u5F0F\u4F1A\u793E\u57FA\u5E79\u696D\u52D9\u60C5\u5831\u5316\u4E8B\u696D\u90E8\u5B9A\u4F8B\u4F1A\u8B70\u8B70\u4E8B\u9332\u3092\u767A\u58F2\u3057\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC\u30B9\u30FC\u30D1\u30FC";
	// search for kanji text: "company" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH1 = "\u4F1A\u793E";
	// search for kanji text: "Society" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH2 = "\u793E\u4F1A";
	// search for kanji text: "Executive secretary" in Japanese (should NOT be
	// found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH3 = "\u5E79\u4E8B";
	// search for kanji text: "conference" in Japanese (should be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH4 = "\u4F1A\u8B70";
	// search for kanji text: "Parliament" in Japanese (should NOT be found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH5 = "\u8B70\u4F1A";
	// search for kanji text: "conference division" in Japanese (should NOT be
	// found)
	public static String ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH6 = "\u4F1A\u8B70\u4E8B\u696D\u90E8";

	public int SEARCH_RESULTS;

	@BeforeClass
	public void beforeClass() {
		SEARCH_RESULTS = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.activity, Purpose.SEARCH);

	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

	// @Test
	public void searchJapaneseKanjiTextSearch1Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch1");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH1, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiTextSearch2NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch2");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH2, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiTextSearch3NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch3");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH3, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// @Test
	public void searchJapaneseKanjiTextSearch4Found() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch4");

		int SEARCH_RESULTS = 1;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH4, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyUnifyActivityContent(searchResponse, ACTIVITY_LANG_WORD_JAPANESE_KANJI_INDEX);
		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiTextSearch5NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch5");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH5, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	@Test
	public void searchJapaneseKanjiTextSearch6NotFound() throws IOException, Exception {
		LOGGER.fine("Test JapaneseKanjiSearchTests#searchJapaneseKanjiTextSearch6");

		int SEARCH_RESULTS = 0;

		SearchRequest searchRequest = SearchActivitiesUtils
				.prepareAndExecuteSearchLangQueryTitle(ACTIVITY_LANG_WORD_JAPANESE_KANJI_SEARCH6, "ja");
		SearchResponse searchResponse = executeQuery(searchRequest);

		Verifier.verifyNumberOfReceivedPrivateEntries(searchResponse, SEARCH_RESULTS);

		assertEquals(searchResponse.getSearchRequestEngine(), SearchRequestEngine.Solr);
	}

	// // Build search query over solr. query = <text> <randomNumer>
	// // At index time (population) we index the content as <text> and the title as
	// <randomNumer> <text>
	// // So this query should return results on the content
	// private SearchResponse prepareAndExecuteSearchLangQuery(String text, String
	// lang)
	// throws Exception, IOException {
	//
	// SearchRequest searchRequest = new SearchRequest();
	// searchRequest.setQuery(URLEncoder.encode(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.SEARCH))
	// + " " + text, "UTF-8"));
	// searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
	// searchRequest.setSortKey(SortKey.date);
	// searchRequest.setScope(Scope.activities);
	// searchRequest.setQueryLang(lang);
	// searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
	// return executeQuery(searchRequest);
	// }
	//
	// // Build search query over solr. query = <randomNumer> <text>
	// // At index time (population) we index the content as <text> and the title as
	// <randomNumer> <text>
	// // So this query should return results on the title
	// private SearchResponse prepareAndExecuteSearchLangQueryTitle(String text,
	// String lang)
	// throws Exception, IOException {
	//
	// SearchRequest searchRequest = new SearchRequest();
	// searchRequest.setQuery(URLEncoder.encode(SearchRestAPIUtils.getExecId(Purpose.SEARCH)
	// + " " + text, "UTF-8"));
	// searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
	// searchRequest.setSortKey(SortKey.date);
	// searchRequest.setScope(Scope.activities);
	// searchRequest.setQueryLang(lang);
	// searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
	// return executeQuery(searchRequest);
	// }
}
