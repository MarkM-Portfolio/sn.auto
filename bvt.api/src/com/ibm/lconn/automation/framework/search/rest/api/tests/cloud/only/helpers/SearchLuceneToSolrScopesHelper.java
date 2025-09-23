package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers;

import java.util.logging.Logger;


import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers.SearchRequestHelper.SearchRequestBuilder;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;

public class SearchLuceneToSolrScopesHelper {

	private static String CLASS_NAME = SearchLuceneToSolrScopesHelper.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	private SearchRequestHelper SearchRequestHelper;
	
	public SearchLuceneToSolrScopesHelper(SearchRequestHelper SearchRequestHelper) {
		this.SearchRequestHelper = SearchRequestHelper;
	}
	
	public void testsScopePersonalOnly(Scope componentScope) throws Exception {
		final String methodName = "testsScopePersonalOnly";
		LOGGER.entering(CLASS_NAME, methodName, componentScope);
		SearchResponse searchResponse = SearchRequestHelper.getBuilder()
				.setQueryAsExecId()
				.setScope(componentScope)
				.setScope_secondary(Scope.personalOnly).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, componentScope);
		Verifier.verifyEntriesComparedToPopulation(searchResponse,
				componentScope,null,Purpose.SEARCH);
		LOGGER.exiting(CLASS_NAME, methodName);
	}	
	
	public void testsScopeParameterComponentCommunities(Scope componentScope) throws Exception {
		final String methodName = "testsScopeParameterComponentCommunities";
		LOGGER.entering(CLASS_NAME, methodName, componentScope);
		SearchResponse searchResponse = SearchRequestHelper.getBuilder().setQueryAsExecId(Purpose.SEARCH_SCOPE)
				.setScope(componentScope)
				.setScope_secondary(Scope.communities).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, componentScope);
		Verifier.verifyNumberOfReceivedEntries(searchResponse,1);
	}
	
	public void testsScopeParameterComponentCommunitiesContent(Scope componentScope) throws Exception {
		final String methodName = "testsScopeParameterComponentCommunitiesContent";
		LOGGER.entering(CLASS_NAME, methodName, componentScope);
		SearchResponse searchResponse = SearchRequestHelper.getBuilder().setQueryAsExecId(Purpose.SEARCH_SCOPE)
				.setScope(componentScope)
				.setScope_secondary(Scope.communities_content).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, componentScope);
		Verifier.verifyNumberOfReceivedEntries(searchResponse,1);
	}
	
	public void testsScopeParameter(Scope scope, Scope secondaryScope, Scope responseScope, Purpose purpose) throws Exception {
		final String methodName = "testsScopeParameter";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		testsScopeParameter(scope, secondaryScope, responseScope, purpose, false);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testsScopeParameter(Scope scope, Scope secondaryScope, Scope responseScope, Purpose purpose, boolean verifyEntriesComparedToPopulation) throws Exception{
		final String methodName = "testsScopeParameter";
		LOGGER.entering(CLASS_NAME, methodName, new Object[]{scope, secondaryScope, responseScope, purpose, verifyEntriesComparedToPopulation});
		SearchRequestBuilder searchRequestBuilder = SearchRequestHelper.getBuilder().setQueryAsExecId(purpose).setScope(scope);
		if(secondaryScope!=null){
			searchRequestBuilder.setScope_secondary(secondaryScope);
		}	
		SearchResponse searchResponse = searchRequestBuilder.buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, responseScope);
		if(verifyEntriesComparedToPopulation){
			Verifier.verifyEntriesComparedToPopulation(searchResponse, responseScope, null, purpose);
		} else {
		Verifier.verifyNumberOfReceivedEntries(searchResponse,1);	
		}
		LOGGER.exiting(CLASS_NAME, methodName);		
	}
	
}
