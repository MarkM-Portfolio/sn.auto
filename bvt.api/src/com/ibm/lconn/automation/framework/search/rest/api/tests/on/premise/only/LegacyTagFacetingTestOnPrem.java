package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class LegacyTagFacetingTestOnPrem extends SearchTest {
	private final static int NUM_OF_TAGS_TO_RETRIEVE = 20;
	private final static String SORT_ORDER_LEGACY = "asc";
	private final static String TAG_FIELD_NAME_LEGACY = "Tag";
	
	private final static HashSet<String> expectedTags = new HashSet<String>(Arrays.asList(
			"search",
			"entry",
			SearchRestAPIUtils.generateTagValue(Purpose.SEARCH)));
	
	@Test
	public void testExpectedTagFacetsLegacy() throws Exception {
		LOGGER.fine("LegacyTagFacetingTestOnCloud#testExpectedTagFacetsLegacy");
		SearchResponse responseLegacy = executeSearchWithTagFacet(SearchRequestEngine.Legacy);
		verifyTags(responseLegacy.getTagFacets());
	}

	private void verifyTags(HashMap<String, Float> tagCountByTagName) {
		Set<String> tagNames = tagCountByTagName.keySet();
		
		assertTrue(tagNames.equals(expectedTags));
		assertEquals(tagCountByTagName.get("search"), new Integer(1));
		assertEquals(tagCountByTagName.get("entry"), new Integer(1));
	}

	private SearchResponse executeSearchWithTagFacet(SearchRequestEngine searchRequestEngine) 
			throws Exception {
		SearchRequest request = new SearchRequest();
		request.setQuery(SearchRestAPIUtils.getExecId(Purpose.SEARCH));
		request.setFacets(new Facet[] { buildTagFacet(searchRequestEngine) });
		request.setSearchRequestEngine(searchRequestEngine);
		return executeQuery(request);
	}
	
	private Facet buildTagFacet(SearchRequestEngine searchRequestEngine) {
		String tagFieldName = TAG_FIELD_NAME_LEGACY;
		String sortOrder = SORT_ORDER_LEGACY;
		return new Facet(tagFieldName, NUM_OF_TAGS_TO_RETRIEVE, null, sortOrder);
	}
	
	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
}
