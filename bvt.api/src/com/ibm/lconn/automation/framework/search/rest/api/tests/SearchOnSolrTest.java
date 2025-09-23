package com.ibm.lconn.automation.framework.search.rest.api.tests;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public abstract class SearchOnSolrTest extends SearchTest{
	
	
	public SearchRequest getSearchAllOnSolrRequest(String searchString,
			Purpose purpose, int start, int pageSize, SortKey sortKey,
			Scope scope, CategoryConstraint[] categoryConstraint,
			CategoryConstraint[] categoryNotConstraint,
			FieldConstraint[] fieldConstraints) {
		SearchRequest searchRequest = new SearchRequest();
		//TODO change it back
		String execId = StringUtils
				.strip(SearchRestAPIUtils.getExecId(purpose));
		//String execId = "1453913727845";
		String searchStringWithId = (execId != null ? execId + " "
				+ searchString : searchString);		
		searchRequest.setQuery(StringUtils.strip(searchStringWithId));
		searchRequest.setStart(start);
		searchRequest.setPageSize(pageSize);
		searchRequest.setSortKey(sortKey);
		searchRequest.setScope(scope);
		if(ArrayUtils.isNotEmpty(categoryConstraint)){
		searchRequest.setCategoryConstraints(categoryConstraint);
		}
		if(ArrayUtils.isNotEmpty(categoryNotConstraint)){
		searchRequest.setCategoryNotConstraints(categoryNotConstraint);
		}
		if(ArrayUtils.isNotEmpty(fieldConstraints)){
		searchRequest.setFieldConstraints(fieldConstraints);
		}
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		return searchRequest;
	}
	
	public SearchRequest getSearchAllOnSolrRequest(String searchString, Purpose purpose, int start, int pageSize, SortKey sortKey, Scope scope){
		return getSearchAllOnSolrRequest("", purpose, 0, StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT, SortKey.date, scope, null, null, null);
	}
	
	public SearchRequest getSearchAllOnSolrRequest(Purpose purpose, Scope scope) throws Exception, IOException {
		return getSearchAllOnSolrRequest("", purpose, 0, StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT, SortKey.date, scope);
	}

}
