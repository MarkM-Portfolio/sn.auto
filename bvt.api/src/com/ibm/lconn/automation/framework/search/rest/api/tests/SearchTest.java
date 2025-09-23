package com.ibm.lconn.automation.framework.search.rest.api.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.testng.annotations.BeforeMethod;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers.SearchLuceneToSolrHelper;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.service.SandService;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

public abstract class SearchTest {

	protected RestAPIUser _restApiUser;

	private String _searchServiceUri;

	private SearchService _searchService = null;
	
	private SandService _sandService = null;
	
	private SearchLuceneToSolrHelper searchLuceneToSolrHelper = null;
	
	public Logger LOGGER;

	public SearchTest() {
		LOGGER = setLogger();
	}

	@BeforeMethod
	public void setUp() throws Exception {
		LOGGER.fine("Start Initializing RestAPISearchTest setUp");
		_restApiUser = getRestAPIUser();
		ServiceEntry search = _restApiUser.getService("search");
		
		assert (search != null);
		_searchServiceUri = search.getServiceURLString();
		_searchService = new SearchService(_restApiUser.getAbderaClient(),
				search);
		_sandService = new SandService(_restApiUser.getAbderaClient(),
				search);
		
		searchLuceneToSolrHelper = new SearchLuceneToSolrHelper(_searchService, 
				_searchServiceUri, 
				_sandService, 
				_restApiUser);
	}

	protected RestAPIUser getRestAPIUser() throws FileNotFoundException, IOException {
		return new RestAPIUser(UserType.LOGIN);
	}

	public SearchService getSearchService() {
		return _searchService;
	}
	public SandService getSandService() {
		return _sandService;
	}
	public String getSearchServiceURI() {
		return _searchServiceUri;
	}

	public RestAPIUser getTestUser() {
		return _restApiUser;
	}
	
	public SearchLuceneToSolrHelper getSearchLuceneToSolrHelper() {
		return searchLuceneToSolrHelper;
	}

	public abstract Logger setLogger();

	public SearchResponse executeQuery(SearchRequest searchRequest)
			throws Exception {
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = getSearchService().searchAllPublicPrivate(
				searchRequest);
		LOGGER.fine("Search response: " + response);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
				response);
		return response;
	}
}
