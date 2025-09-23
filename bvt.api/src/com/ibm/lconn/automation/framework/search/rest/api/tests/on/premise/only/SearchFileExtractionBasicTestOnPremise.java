package com.ibm.lconn.automation.framework.search.rest.api.tests.on.premise.only;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchFileExtractionBasicTestOnPremise extends SearchTest {

	@Test
	public void testSearchFileTxtContent() throws Exception {
		LOGGER.fine("SearchFileExtractionBasicTestOnPremise#testSearchFileTxtContent");
		SearchResponse searchResponse = prepareAndExecuteQuery(Scope.files);
		Verifier.verifyEntriesScope(searchResponse, Scope.files);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, Scope.files);

	}

	@Test
	public void testSearchActivityAttachmentTxtContent() throws Exception {
		LOGGER.fine("SearchFileExtractionBasicTestOnPremise#testSearchActivityAttachmentTxtContent");
		SearchResponse searchResponse = prepareAndExecuteQueryAttachment(Scope.activities);
		Verifier.verifyEntriesScope(searchResponse, Scope.activities);
		Verifier.verifyNumberOfReceivedEntries(searchResponse, 1);

	}

	private SearchResponse prepareAndExecuteQuery(Scope scope)
			throws Exception, IOException {
		LOGGER.fine("Test SearchScopeParameterTest#executeQuery, scope: "
				+ scope);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils
				.contentForSearchExtracted(Purpose.SEARCH));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(scope);

		return executeQuery(searchRequest);

	}
	private SearchResponse prepareAndExecuteQueryAttachment(Scope scope)
			throws Exception, IOException {
		LOGGER.fine("Test SearchScopeParameterTest#executeQuery, scope: "
				+ scope);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(SearchRestAPIUtils
				.contentForSearchExtracted(Purpose.SEARCH_SCOPE));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(scope);

		return executeQuery(searchRequest);

	}
	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}

}
