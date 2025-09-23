package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.service.SandService;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

public class SearchLuceneToSolrHelper {
	
	private static String CLASS_NAME = SearchLuceneToSolrHelper.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	String TEST_TAG =  SearchRestAPIUtils.generateTagValue(Purpose.SEARCH);
	
	private SearchRequestHelper SearchRequestHelper = null;
	private SearchLuceneToSolrSocialHelper socialHelper = null;
	private SearchLuceneToSolrScopesHelper scopesHelper = null;

	public SearchLuceneToSolrHelper(SearchService searchService,
			String searchServiceUri,
			SandService sandService,
			RestAPIUser testUser
			){

		this.SearchRequestHelper = new SearchRequestHelper(searchService);
		this.socialHelper = new SearchLuceneToSolrSocialHelper(SearchRequestHelper, testUser);	
		this.scopesHelper =  new SearchLuceneToSolrScopesHelper(SearchRequestHelper); 
	}
	
	public SearchLuceneToSolrSocialHelper getSocialHelper(){
		return socialHelper;
	}
	
	public SearchRequestHelper getSearchRequestHelper(){
		return SearchRequestHelper;
	}
	
	public SearchLuceneToSolrScopesHelper getSearchScopesHelper(){
		return scopesHelper;
	}
	
	public void testScopeParameter(Scope scope) throws Exception {
		final String methodName = "testScopeParameter";
		LOGGER.entering(CLASS_NAME, methodName, scope);		
		SearchResponse searchResponse = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).buildAndExecute();
		Verifier.verifyEntriesScope(searchResponse, scope);
		Verifier.verifyEntriesComparedToPopulation(searchResponse, scope);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testPageParameter(Scope scope) throws Exception {
		final String methodName = "testPageParameter";
		LOGGER.entering(CLASS_NAME, methodName, scope);		
		SearchResponse responseStart = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setStart(1)
				.setPageSize(1).buildAndExecute();
		
		SearchResponse responsePage = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setPage(2).setPageSize(1).buildAndExecute();
		
		Boolean isEqual = Verifier.IsResultsEqualByTitles(responseStart, responsePage);
		assertTrue("The  entries are not equal:" + responseStart.toString() + " and: " + responsePage.toString(), isEqual);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testsSortOrderParameterDateRelevance(Scope scope) throws Exception {
		final String methodName = "testsSortOrderParameterDateRelevance";
		LOGGER.entering(CLASS_NAME, methodName, scope);

		SearchResponse sortDateDesc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.date).setSortOrder(SortOrder.desc).buildAndExecute();
		Verifier.validateSortKeyResult(sortDateDesc, SortKey.date, SortOrder.desc);

		SearchResponse sortDateAsc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.date).setSortOrder(SortOrder.asc).buildAndExecute();
		Verifier.validateSortKeyResult(sortDateAsc, SortKey.date, SortOrder.asc);

		SearchResponse sortRelevanceDesc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.relevance).setSortOrder(SortOrder.desc).buildAndExecute();
		Verifier.validateSortKeyResult(sortRelevanceDesc,
				SortKey.relevance, SortOrder.desc);

		SearchResponse sortRelevanceAsc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.relevance).setSortOrder(SortOrder.asc).buildAndExecute();
		Verifier.validateSortKeyResult(sortRelevanceAsc, SortKey.relevance, SortOrder.desc);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testsSortOrderParameterTitle(Scope scope) throws Exception {
		final String methodName = "testsSortOrderParameterTitle";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		SearchResponse sortTitleDesc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.title).setSortOrder(SortOrder.desc).buildAndExecute();
		assertFalse ("Search result for sort by title  is empty",sortTitleDesc.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortTitleDesc, SortKey.title, SortOrder.desc);

		SearchResponse sortTitleAsc = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setSortKey(SortKey.title).setSortOrder(SortOrder.asc).buildAndExecute();
		assertFalse ("Search result for sort by title  is empty",sortTitleAsc.getResults().isEmpty());
		Verifier.validateSortKeyResult(sortTitleAsc, SortKey.title, SortOrder.asc);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testHighlight(Scope scope) throws Exception {
		final String methodName = "testHighlight";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		SearchResponse ActivitiesResponse =  SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).buildAndExecute();
		Verifier.verifyQueryInTileHighlighted(ActivitiesResponse,SearchRestAPIUtils.getExecId(Purpose.SEARCH),true);
		
		String[] highlightDisabled = new String[0];
		SearchResponse ActivitiesResponseNotHighlighted =  SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setHighlight(highlightDisabled).buildAndExecute();
		Verifier.verifyQueryInTileHighlighted(ActivitiesResponseNotHighlighted,SearchRestAPIUtils.getExecId(Purpose.SEARCH),false);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testsQueryTag(Scope scope) throws Exception {
		final String methodName = "testsQueryTag";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		String query=SearchRestAPIUtils.generateTagValue(Purpose.SEARCH);
		SearchResponse tagResponse = SearchRequestHelper.getBuilder().setScope(scope).setQuery(query).buildAndExecute();
		Verifier.verifyEntriesComparedToPopulation(tagResponse, scope);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	
	public void testsQueryDescription(Scope scope) throws Exception {
		final String methodName = "testsQueryDescription";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		String query=SearchRestAPIUtils.generateTagValue(Purpose.SEARCH)+ "description";
		SearchResponse contentResponse = SearchRequestHelper.getBuilder().setScope(scope).setQuery(query).buildAndExecute();
		Verifier.verifyEntriesScope(contentResponse, scope);
		Verifier.verifyEntriesComparedToPopulation(contentResponse, scope);
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void testSearchWithFieldConstraintsTag(Scope scope) throws IOException, Exception {
		 testSearchWithFieldConstraintsTag(scope, TEST_TAG, Purpose.SEARCH);
	}
	
	public void testSearchWithFieldConstraintsTag(Scope scope, String testTag, Purpose purpose) throws IOException, Exception {
		final String methodName = "testSearchWithFieldConstraintsTag";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		FieldConstraint[] fieldConstraintsTag = new FieldConstraint[] { new FieldConstraint(
				"tag", new String[] {testTag  }, true) };

		SearchResponse searchResponseTag = SearchRequestHelper.getBuilder().setQueryAsExecId(purpose).setScope(scope).setFieldConstraints(fieldConstraintsTag).buildAndExecute();		
		Verifier.verifyUnifyActivityScope(searchResponseTag, scope);		
		assertTrue("Tag " + testTag + " not found in respond:"
				+ searchResponseTag.toString(), Verifier.verifyTagByName(
				searchResponseTag, testTag));
		LOGGER.exiting(CLASS_NAME, methodName);
	}
	
	public void searchFilesWithNotConstraintTest(Scope scope)
			throws IOException, Exception {
		searchFilesWithNotConstraintTest(scope, TEST_TAG);
	}

	public void searchFilesWithNotConstraintTest(Scope scope, String testTag)
			throws IOException, Exception {
		final String methodName = "searchFilesWithNotConstraintTest";
		LOGGER.entering(CLASS_NAME, methodName, scope);
		
		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", testTag } });
		CategoryConstraint[] nonConstraintTag= new CategoryConstraint[] { categoryConstraint };

		SearchResponse searchResponseNotTag = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setCategoryNotConstraints(nonConstraintTag).buildAndExecute();
		assertTrue("Tag  found " + testTag  + " in respond:"
				+ searchResponseNotTag.toString(), Verifier.verifyNoTagByName(
				searchResponseNotTag, testTag));
		

		Verifier.verifyEntriesScope(searchResponseNotTag, scope);
		LOGGER.exiting(CLASS_NAME, methodName);
		
	}
	
	public void testSearchWithCategorySourceAndTagConstraint(Scope scope, String[] componentCategory, String[] pageCategory)
			throws IOException, Exception {
		final String methodName = "testSearchWithCategorySourceAndTagConstraint";
		LOGGER.entering(CLASS_NAME, methodName, new Object[]{scope, componentCategory});

		CategoryConstraint categoryConstraintSource = new CategoryConstraint(
				new String[][] { componentCategory });

		CategoryConstraint[] values = new CategoryConstraint[] { categoryConstraintSource };

		SearchResponse searchResponseSourceComponent = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setCategoryConstraints(values).buildAndExecute();
		
		Verifier.verifyUnifyActivityScope(searchResponseSourceComponent,
				scope);
		Verifier.verifyEntriesComparedToPopulation(searchResponseSourceComponent,
				scope);

		CategoryConstraint categoryConstraintSourceComponentPage = new CategoryConstraint(
				new String[][] { pageCategory });

		CategoryConstraint[] values2 = new CategoryConstraint[] { categoryConstraintSourceComponentPage };

		SearchResponse searchResponseSourceWikiPages = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setCategoryConstraints(values2).buildAndExecute();
		
		Verifier.verifyUnifyActivityScope(searchResponseSourceWikiPages,
				scope);
		Verifier.verifyEntriesComparedToPopulation(searchResponseSourceWikiPages,
				scope);
		
		CategoryConstraint categoryConstraint = new CategoryConstraint(
				new String[][] { new String[] { "Tag", TEST_TAG } });
		CategoryConstraint[] CategoryConstraintTag= new CategoryConstraint[] { categoryConstraint };

		SearchResponse searchResponseWithTag = SearchRequestHelper.getBuilder().setQueryAsExecId().setScope(scope).setCategoryConstraints(CategoryConstraintTag).buildAndExecute();

		assertTrue("Tag  not found " + TEST_TAG  + " in respond:"
				+ searchResponseWithTag.toString(), Verifier.verifyTagByName(
						searchResponseWithTag, TEST_TAG));
		

		Verifier.verifyEntriesScope(searchResponseWithTag, scope);
		LOGGER.exiting(CLASS_NAME, methodName);

	}
}
