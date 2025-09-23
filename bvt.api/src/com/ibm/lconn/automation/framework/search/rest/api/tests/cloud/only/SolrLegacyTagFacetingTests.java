package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
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

public class SolrLegacyTagFacetingTests extends SearchTest {

	@Test
	public void testExpectedTagFacetsWithNoScopeUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongAllUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(2));
	}

	@Test
	public void testExpectedTagFacetsWithNoScopeUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsWithNoScopeUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(2));
	}

	@Test
	public void testExpectedTagFacetsAmongAllUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongAllUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.allconnections);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(2));
	}

	@Test
	public void testExpectedTagFacetsAmongActivitiesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongActivitiesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.activities);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(1));
	}

	@Test
	public void testExpectedTagFacetsAmongFilesUsingSolr() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongFilesUsingSolr");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setScope(Scope.files);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		Set<String> expectedtagNames = new HashSet<String>();
		expectedtagNames.add(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING)));
		assertTrue(tagNames.equals(expectedtagNames));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(1));
	}

	@Test
	public void testExpectedTagFacetsAmongAllUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongAllUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setScope(Scope.allconnections);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(2));
	}

	@Test
	public void testExpectedTagFacetsAmongActivitiesUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongActivitiesUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setScope(Scope.activities);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		assertTrue(tagNames.equals(SolrLegacyFacetingConstants.expectedTags));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[0]), new Float(3));
		assertEquals(tagCountByTagName.get(SolrLegacyFacetingConstants.TAGS[1]), new Float(2));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(1));
	}

	@Test
	public void testExpectedTagFacetsAmongFilesUsingLegacy() throws Exception {
		LOGGER.fine("SolrTagFacetingTestOnCloud#testExpectedTagFacetsAmongFilesUsingLegacy");
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setScope(Scope.files);
		SearchResponse responseSolr = executeSearchWithTagFacet(searchRequest);

		HashMap<String, Float> tagCountByTagName = responseSolr.getTagFacets();
		Set<String> tagNames = tagCountByTagName.keySet();
		Set<String> expectedtagNames = new HashSet<String>();
		expectedtagNames.add(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING)));
		assertTrue(tagNames.equals(expectedtagNames));
		assertEquals(tagCountByTagName.get(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.FACETING))),
				new Float(1));
	}

	private SearchResponse executeSearchWithTagFacet(SearchRequest searchRequest) throws Exception {
		searchRequest.setQuery(SearchRestAPIUtils.getExecId(Purpose.FACETING));
		searchRequest.setFacets(new Facet[] { buildTagFacet(searchRequest.getSearchRequestEngine()) });
		SearchResponse response = executeQuery(searchRequest);
		return response;
	}

	private Facet buildTagFacet(SearchRequestEngine searchRequestEngine) {
		String tagFieldName = SolrLegacyFacetingConstants.TAG_FIELD_NAME;
		String sortOrder = SolrLegacyFacetingConstants.SORT_ORDER;
		return new Facet(tagFieldName, SolrLegacyFacetingConstants.NUM_OF_FACETS, null, sortOrder);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
