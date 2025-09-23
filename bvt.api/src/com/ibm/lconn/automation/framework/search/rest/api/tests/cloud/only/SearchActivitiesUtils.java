package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.tests.SearchTest;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;

public class SearchActivitiesUtils extends SearchTest{

	@Override
	public Logger setLogger() {
		return SearchRestAPILoggerUtil.getInstance().getSearchServiceLogger();
	}
	
	// Build search query over solr. query = <text> <randomNumer> 
	// At index time (population) we index the content as <text> and the title as <randomNumer> <text>
	// So this query should return results on the content
	public static SearchRequest prepareAndExecuteSearchLangQuery(String text, String lang)
			throws Exception, IOException {

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(URLEncoder.encode(StringUtils.reverse(SearchRestAPIUtils.getExecId(Purpose.JAPANESE)) + " " + text, "UTF-8"));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setQueryLang(lang);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		return searchRequest;
	}
	
	// Build search query over solr. query = <randomNumer> <text> 
	// At index time (population) we index the content as <text> and the title as <randomNumer> <text>
	// So this query should return results on the title
	public static SearchRequest prepareAndExecuteSearchLangQueryTitle(String text, String lang)
			throws Exception, IOException {

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(URLEncoder.encode(SearchRestAPIUtils.getExecId(Purpose.JAPANESE) + " " + text, "UTF-8"));
		searchRequest.setPageSize(StringConstants.SEARCH_API_PAGE_SIZE_DEFAULT);
		searchRequest.setSortKey(SortKey.date);
		searchRequest.setScope(Scope.activities);
		searchRequest.setQueryLang(lang);
		searchRequest.setSearchRequestEngine(SearchRequestEngine.Solr);
		searchRequest.setHighlight(new String[0]);
		return searchRequest;
	}
}
