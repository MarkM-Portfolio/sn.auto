package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.facets.SolrLegacyFacetingConstants;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class SolrLegacyPeopleFacetingTests extends SearchTest {
	public static String PERSON;
	public static HashSet<String> expectedPeople = null;

	@BeforeClass
	public void init() throws FileNotFoundException, IOException {
		ProfileData profileData = ProfileLoader.getProfile(3);
		PERSON = profileData.getRealName();
		expectedPeople = new HashSet<String>();
		expectedPeople.add(PERSON);
	}

	@Test
	public void testExpectedPersonFacetsWithNoScopeUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongAllUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(7));
	}

	@Test
	public void testExpectedPersonFacetsAmongAllUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongAllUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.allconnections);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(7));
	}

	@Test
	public void testExpectedPersonFacetsAmongActivitiesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongActivitiesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.activities);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(6));
	}

	@Test
	public void testExpectedPersonFacetsAmongFilesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongFilesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.files);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(1));
	}

	@Ignore
	public void testExpectedPersonFacetsAmongAllUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongAllUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Legacy);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(28.397758));
	}

	@Ignore
	public void testExpectedPersonFacetsAmongActivitiesUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongActivitiesUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Legacy);
		searchRequest.setScope(Scope.activities);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(44.948524));
	}

	@Ignore
	public void testExpectedPersonFacetsAmongFilesUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedPersonFacetsAmongFilesUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Legacy);
		searchRequest.setScope(Scope.files);
		SearchResponse responseSolr = executeSearchWithPeopleFacet(searchRequest);

		HashMap<String, Float> peopleFacets = responseSolr.getPeopleFacets();
		Set<String> personNames = peopleFacets.keySet();
		assertTrue(personNames.equals(expectedPeople));
		assertEquals(peopleFacets.get(PERSON), new Float(4.663233));
	}

	private SearchResponse executeSearchWithPeopleFacet(SearchRequest searchRequest) throws Exception {
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.FACETING));
		searchRequest.setFacets(new Facet[] { buildPeopleFacet(searchRequest.getSearchRequestEngine()) });
		SearchResponse response = executeQuery(searchRequest);
		return response;
	}

	private Facet buildPeopleFacet(SearchRequestEngine searchRequestEngine) {
		String personFieldName = SolrLegacyFacetingConstants.PEOPLE_FIELD;
		String sortOrder = SolrLegacyFacetingConstants.SORT_ORDER;
		;
		return new Facet(personFieldName, SolrLegacyFacetingConstants.NUM_OF_FACETS, null, sortOrder);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}