package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.and.on.premise;

import static com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.BoostTestsPopulator.FIELD_BOOST_TEST_QUERY_POSTFIX;
import static com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.BoostTestsPopulator.PROXIMITY_BOOST_TITLES;
import static com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.BoostTestsPopulator.PROXIMITY_FOX_QUICK_TITLE;
import static com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.BoostTestsPopulator.PROXIMITY_QUICK_BROWN_FOX_TITLE;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.abdera.model.Category;
import org.apache.commons.lang3.StringUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class SearchBoostTests extends SearchTest{

	private static final String CLASS_NAME = SearchBoostTests.class.getName();
	
	private static final String PROXIMITY_ENABLED_EXPECTED_RESULTS = PROXIMITY_FOX_QUICK_TITLE + PROXIMITY_QUICK_BROWN_FOX_TITLE;
	private static final String PROXIMITY_DISABLED_EXPECTED_RESULTS = PROXIMITY_QUICK_BROWN_FOX_TITLE + PROXIMITY_FOX_QUICK_TITLE;
	
	public enum BoostType {PROXIMITY, FIELD, RECENCY};
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyProximityBoost_sortByRelevance_checkRanking_devEnabled() throws IOException, Exception {
		final String methodName = "searchUnifyProximityBoost_sortByRelevance_checkRanking_devEnabled";
		LOGGER.entering(CLASS_NAME, methodName);
		StringBuilder actualResultBuilder = new StringBuilder();
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick -devenableproximityboost", SortKey.relevance);
		List<SearchResult> entries = searchResponse.getResults();
		for(SearchResult entry : entries){
			String title = entry.getTitle();
			if (title != null){
				String StringTitleWithoutPrefix = removeExecIdPrefix(title);
				if (PROXIMITY_BOOST_TITLES.contains(StringTitleWithoutPrefix)) {
					actualResultBuilder.append(StringTitleWithoutPrefix);
				}			
			}
		}
		String actualResult = actualResultBuilder.toString();
		assertEquals(PROXIMITY_ENABLED_EXPECTED_RESULTS,actualResult);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyProximityBoost_sortByRelevance_checkRanking_devDisabled() throws IOException, Exception {
		final String methodName = "searchUnifyProximityBoost_sortByRelevance_checkRanking_devDisabled";
		LOGGER.entering(CLASS_NAME, methodName);
		StringBuilder actualResultBuilder = new StringBuilder();
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick -devdisableproximityboost", SortKey.relevance);
		List<SearchResult> entries = searchResponse.getResults();
		for(SearchResult entry : entries){
			String title = entry.getTitle();
			if (title != null){
				String StringTitleWithoutPrefix = removeExecIdPrefix(title);
				if (PROXIMITY_BOOST_TITLES.contains(StringTitleWithoutPrefix)) {
					actualResultBuilder.append(StringTitleWithoutPrefix);
				}			
			}
		}
		String actualResult = actualResultBuilder.toString();
		assertEquals(PROXIMITY_DISABLED_EXPECTED_RESULTS,actualResult);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyProximityBoostTest_checkRanking_featureEnabledIfGKTrue() throws IOException, Exception {
		final String methodName = "searchUnifyProximityBoostTest_checkRanking_featureEnabledIfGKTrue";
		LOGGER.entering(CLASS_NAME, methodName);
		StringBuilder actualResultBuilder = new StringBuilder();
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick", SortKey.relevance);
		List<SearchResult> entries = searchResponse.getResults();
		for(SearchResult entry : entries){
			String title = entry.getTitle();
			if (title != null){
				String StringTitleWithoutPrefix = removeExecIdPrefix(title);
				if (PROXIMITY_BOOST_TITLES.contains(StringTitleWithoutPrefix)) {
					actualResultBuilder.append(StringTitleWithoutPrefix);
				}			
			}
		}
		String actualResult = actualResultBuilder.toString();
		AssertJUnit.assertEquals(PROXIMITY_ENABLED_EXPECTED_RESULTS,actualResult);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyProximityBoostTest_checkScore_featureEnabledIfGKTrue() throws IOException, Exception {
		final String methodName = "searchUnifyProximityBoostTest_checkScore_featureEnabledIfGKTrue";
		LOGGER.entering(CLASS_NAME, methodName);
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick", SortKey.relevance);
		Double relevanceDefault = getScoreFromResult(searchResponse, PROXIMITY_FOX_QUICK_TITLE, BoostType.PROXIMITY);
		assertTrue(relevanceDefault!=null);
		searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick -devenableproximityboost", SortKey.relevance);
		Double relevanceAfterProximityBoost = getScoreFromResult(searchResponse, PROXIMITY_FOX_QUICK_TITLE, BoostType.PROXIMITY);	
		assertEquals(relevanceDefault,relevanceAfterProximityBoost);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyProximityBoostTest_checkScore_devOptions() throws IOException, Exception {
		final String methodName = "searchUnifyProximityBoostTest_checkScore_devOptions";
		LOGGER.entering(CLASS_NAME, methodName);
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick -devenableproximityboost", SortKey.relevance);
		Double relevanceBoostedByProximity = getScoreFromResult(searchResponse, PROXIMITY_FOX_QUICK_TITLE, BoostType.PROXIMITY);
		assertTrue(relevanceBoostedByProximity!=null);
		searchResponse = prepareAndExecuteUnifyBoostQuery("fox quick -devdisableproximityboost", SortKey.relevance);
		Double relevanceNoProximityBoost = getScoreFromResult(searchResponse, PROXIMITY_FOX_QUICK_TITLE, BoostType.PROXIMITY);
		assertTrue(relevanceBoostedByProximity!=null);		
		assertTrue(relevanceBoostedByProximity>relevanceNoProximityBoost);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyRecencyBoostTest_checkScore_devOptions() throws IOException, Exception {
		final String methodName = "searchUnifyRecencyBoostTest_checkScore_devOptions";
		LOGGER.entering(CLASS_NAME, methodName);
		String exId = getExecId(Purpose.UNIFY);
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("-devenablerecencyboost", SortKey.relevance);
		Double relevanceBoostedByDate = getScoreFromResult(searchResponse, exId, BoostType.RECENCY); 
		assertTrue(relevanceBoostedByDate!=null);
		searchResponse = prepareAndExecuteUnifyBoostQuery("-devdisablerecencyboost", SortKey.relevance);
		Double relevanceNoRecencyBoost = getScoreFromResult(searchResponse, exId, BoostType.RECENCY); 
		assertTrue(relevanceBoostedByDate!=null);		
		assertTrue(relevanceBoostedByDate>relevanceNoRecencyBoost);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyRecencyBoostTest_checkScore() throws IOException, Exception {
		final String methodName = "searchUnifyRecencyBoostTest_checkScore";
		LOGGER.entering(CLASS_NAME, methodName);
		String exId = getExecId(Purpose.UNIFY);
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery("", SortKey.relevance);
		Double relevanceBoostedByDate = getScoreFromResult(searchResponse, exId, BoostType.RECENCY); 
		assertTrue(relevanceBoostedByDate!=null);
		searchResponse = prepareAndExecuteUnifyBoostQuery("-devdisablerecencyboost", SortKey.relevance);
		Double relevanceNoRecencyBoost = getScoreFromResult(searchResponse, exId, BoostType.RECENCY); 
		assertTrue(relevanceBoostedByDate!=null);		
		assertTrue(relevanceBoostedByDate>relevanceNoRecencyBoost);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyFieldBoostTest_checkRanking_devDisabled() throws IOException, Exception {
		final String methodName = "searchUnifyFieldBoostTest_checkRanking_featureEnabledIfGKTrue";
		LOGGER.entering(CLASS_NAME, methodName);
		String query = getQueryForFieldBoostTest() + " -devdisablefieldboost";
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery(query, SortKey.relevance, false);
		List<SearchResult> entries = searchResponse.getResults();
		int numberResults = entries.size();
		assertTrue("Number of results returned was " + numberResults, numberResults>1);
		SearchResult entry = entries.get(1);
		String expectedSummary = getQueryForFieldBoostTest();
		String actualSummary = entry.getSummary();
		assertEquals(expectedSummary, actualSummary);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyFieldBoost_checkRanking_devEnabled() throws IOException, Exception {
		final String methodName = "searchUnifyFieldBoost_sortByRelevance_checkRanking_devEnabled";
		LOGGER.entering(CLASS_NAME, methodName);
		String query = getQueryForFieldBoostTest() + " -devenablefieldboost";
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery(query, SortKey.relevance, false);
		List<SearchResult> entries = searchResponse.getResults();
		int numberResults = entries.size();
		assertTrue("Number of results returned was " + numberResults, numberResults>1);
		
		SearchResult entry = entries.get(1);
		List<Category> tags = entry.getTags();
		String expectedTag = getQueryForFieldBoostTest();
		String actualTag = (tags.size()==1 ? tags.get(0).getTerm() : "");
		assertEquals(expectedTag, actualTag);
		
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	//@Test(groups = { "smartcloud" })
	public void searchUnifyFieldBoostTest_checkRanking_featureEnabledIfGKTrue() throws IOException, Exception {
		final String methodName = "searchUnifyFieldBoostTest_checkRanking_featureEnabledIfGKTrue";
		LOGGER.entering(CLASS_NAME, methodName);
		String query = getQueryForFieldBoostTest();
		SearchResponse searchResponse = prepareAndExecuteUnifyBoostQuery(query, SortKey.relevance, false);
		List<SearchResult> entries = searchResponse.getResults();
		int numberResults = entries.size();
		assertTrue("Number of results returned was " + numberResults, numberResults>1);
		
		SearchResult entry = entries.get(1);
		List<Category> tags = entry.getTags();
		String expectedTag = getQueryForFieldBoostTest();
		String actualTag = (tags.size()==1 ? tags.get(0).getTerm() : "");
		assertEquals(expectedTag, actualTag);
		
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	private SearchResponse prepareAndExecuteUnifyBoostQuery(String queryString, SortKey sortKey) throws IOException, Exception{
		return prepareAndExecuteUnifyBoostQuery(queryString, sortKey, true);
	}
	
	
	private SearchResponse prepareAndExecuteUnifyBoostQuery(String queryString, SortKey sortKey, boolean addExIdToQuery)
			throws Exception, IOException {
		final String methodName = "prepareAndExecuteUnifyBoostQuery";
		LOGGER.entering(CLASS_NAME, methodName, new Object[]{queryString, sortKey.toString()});
		SearchRequest searchRequest = new SearchRequest();
		String query = (addExIdToQuery ? addExIdToQuery(queryString) : queryString);		
		//String query = "1449156405631" + " " + queryString;
		String normalizedQuery = StringUtils.stripToEmpty(query);
		normalizedQuery = StringUtils.replace(normalizedQuery, " ", "%20");
		searchRequest.setQuery(normalizedQuery);
		searchRequest.setStart(0);
		searchRequest.setSortKey(sortKey);
		searchRequest.setScope(Scope.activities);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		return executeQuery(searchRequest);
	}
	
	private String addExIdToQuery(String queryString){
		return getExecId(Purpose.UNIFY) + " " + queryString;
	}
	
	private String getQueryForFieldBoostTest(){
		return getExecId(Purpose.UNIFY) + FIELD_BOOST_TEST_QUERY_POSTFIX;
	}
	
	private Double getScoreFromResult(SearchResponse searchResponse, String title, BoostType boostType){
		Double score = null;
		List<SearchResult> entries = searchResponse.getResults();
		for(SearchResult entry : entries){
			String entryTitle = entry.getTitle();
			if(boostType == BoostType.PROXIMITY){
				entryTitle = removeExecIdPrefix(entryTitle);
			}
			if(StringUtils.equalsIgnoreCase(title, entryTitle)){
				score = entry.getRelevenceScore();
				break;
			}
		}
		return score;
	}
	
	private String removeExecIdPrefix(String text){
	 	return StringUtils.substringAfter(text, " ");
	}
	
	private String getExecId(Purpose purpose) {
		return "1449583597497";
		//return SearchRestAPIUtils.getExecId(purpose);
	}
	

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
	
	
}
