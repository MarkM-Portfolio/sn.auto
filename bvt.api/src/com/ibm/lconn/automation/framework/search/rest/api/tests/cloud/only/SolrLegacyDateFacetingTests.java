package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.facets.SolrLegacyFacetingConstants;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class SolrLegacyDateFacetingTests extends SearchTest {

	@Test
	public void testExpectedDateFacetsWithNoScopeUsingSolr() throws Exception {
		LOGGER.fine("SolrLegacyDateFacetingTests#testExpectedDateFacetsWithNoScopeUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		SearchResponse responseSolr = executeSearchWithDateFacet(searchRequest);

		HashMap<String, Float> dateCountByTagName = responseSolr.getDateFacets();
		Set<String> dates = dateCountByTagName.keySet();
		assertTrue(dates.equals(SolrLegacyFacetingConstants.expectedDate));
		assertEquals(dateCountByTagName.get(SolrLegacyFacetingConstants.DATE[0]), new Float(7));
	}

	@Test
	public void testExpectedDateFacetsAmongAllUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedDateFacetsAmongAllUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.allconnections);
		SearchResponse responseSolr = executeSearchWithDateFacet(searchRequest);

		HashMap<String, Float> dateCountByTagName = responseSolr.getDateFacets();
		Set<String> dates = dateCountByTagName.keySet();
		assertTrue(dates.equals(SolrLegacyFacetingConstants.expectedDate));
		assertEquals(dateCountByTagName.get(SolrLegacyFacetingConstants.DATE[0]), new Float(7));
	}

	@Test
	public void testExpectedDateFacetsAmongActivitiesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedDateFacetsAmongActivitiesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.activities);
		SearchResponse responseSolr = executeSearchWithDateFacet(searchRequest);

		HashMap<String, Float> dateCountByTagName = responseSolr.getDateFacets();
		Set<String> dates = dateCountByTagName.keySet();
		assertTrue(dates.equals(SolrLegacyFacetingConstants.expectedDate));
		assertEquals(dateCountByTagName.get(SolrLegacyFacetingConstants.DATE[0]), new Float(6));
	}

	@Test
	public void testExpectedDateFacetsAmongFilesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedDateFacetsAmongFilesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.files);
		SearchResponse responseSolr = executeSearchWithDateFacet(searchRequest);

		HashMap<String, Float> dateCountByTagName = responseSolr.getDateFacets();
		Set<String> dates = dateCountByTagName.keySet();
		assertTrue(dates.equals(SolrLegacyFacetingConstants.expectedDate));
		assertEquals(dateCountByTagName.get(SolrLegacyFacetingConstants.DATE[0]), new Float(1));
	}

	private SearchResponse executeSearchWithDateFacet(SearchRequest searchRequest) throws Exception {
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.FACETING));
		searchRequest.setFacets(new Facet[] { buildDateFacet(searchRequest.getSearchRequestEngine()) });
		SearchResponse response = executeQuery(searchRequest);
		return response;
	}

	private Facet buildDateFacet(SearchRequestEngine searchRequestEngine) {
		String tagFieldName = SolrLegacyFacetingConstants.DATE_FIELD;
		String sortOrder = SolrLegacyFacetingConstants.SORT_ORDER;
		return new Facet(tagFieldName, SolrLegacyFacetingConstants.NUM_OF_FACETS, null, sortOrder);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
