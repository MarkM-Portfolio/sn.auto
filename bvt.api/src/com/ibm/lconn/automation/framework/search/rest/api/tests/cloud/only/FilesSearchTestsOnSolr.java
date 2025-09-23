package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchOnSolrTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class FilesSearchTestsOnSolr extends SearchOnSolrTest {

	public int FILES_RESULTS;

	@BeforeClass
	public void beforeClass() {
		FILES_RESULTS = PopulatedData.getInstance().getExpectedNumOfEntriesByApp(Application.file,
				Purpose.SEARCH_SOLR_ENGINE);
	}

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

	@Test(groups = { "smartcloud" })
	public void searchAllFilesOnSolrTest() throws IOException, Exception {
		LOGGER.fine("Test SearchServiceUnifyBasicTest#searchAllUnifyActivitiesTest");
		SearchRequest searchRequest = getSearchAllOnSolrRequest(Purpose.SEARCH_SOLR_ENGINE, Scope.files);
		SearchResponse searchResponse = executeQuery(searchRequest);
		Verifier.verifyEntriesScope(searchResponse, Scope.files);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.files, Permissions.PRIVATE,
				Purpose.SEARCH_SOLR_ENGINE);

		assertEquals(SearchRequestEngine.Solr, searchResponse.getSearchRequestEngine());
	}

}
