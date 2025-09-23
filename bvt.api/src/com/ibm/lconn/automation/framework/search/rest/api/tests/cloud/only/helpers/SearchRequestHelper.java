package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only.helpers;

import java.util.Locale;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.services.search.data.CategoryConstraint;
import com.ibm.lconn.automation.framework.services.search.data.ContextPath;
import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.RangeConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.request.SearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

public class SearchRequestHelper {

	private static String CLASS_NAME = SearchRequestHelper.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASS_NAME);

	private SearchService searchService = null;

	public SearchRequestHelper(SearchService searchService) {
		this.searchService = searchService;
	}

	public SearchResponse executeQuery(SearchRequest searchRequest) throws Exception {
		LOGGER.fine("Search request: " + searchRequest);
		SearchResponse response = searchService.searchAllPublicPrivate(searchRequest);
		LOGGER.fine("Search response: " + response);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);
		return response;
	}

	public SearchRequestBuilder getBuilder() {
		return new SearchRequestBuilder();
	}

	public class SearchRequestBuilder {

		private final String EN_LANG = "en";
		private SearchRequest searchRequest = null;

		private SearchRequestBuilder() {
			this.searchRequest = new SearchRequest();
			this.searchRequest.setQueryLang(EN_LANG);
		}

		public SearchRequestBuilder setContextPath(ContextPath contextPath) {
			searchRequest.setContextPath(contextPath);
			return this;
		}

		public SearchRequestBuilder setPage(Integer page) {
			searchRequest.setPage(page);
			return this;
		}

		public SearchRequestBuilder setPageSize(Integer pageSize) {
			searchRequest.setPageSize(pageSize);
			return this;
		}

		public SearchRequestBuilder setStart(Integer start) {
			searchRequest.setStart(start);
			return this;
		}

		public SearchRequestBuilder setLocale(Locale locale) {
			searchRequest.setLocale(locale);
			return this;
		}

		public SearchRequestBuilder setCategoryConstraints(CategoryConstraint[] categoryConstraints) {
			searchRequest.setCategoryConstraints(categoryConstraints);
			return this;
		}

		public SearchRequestBuilder setCategoryNotConstraints(CategoryConstraint[] categoryNotConstraints) {
			searchRequest.setCategoryNotConstraints(categoryNotConstraints);
			return this;
		}

		public SearchRequestBuilder setEvidence(Boolean evidence) {
			searchRequest.setEvidence(evidence);
			return this;
		}

		public SearchRequestBuilder setComponent(String component) {
			searchRequest.setComponent(component);
			return this;
		}

		public SearchRequestBuilder setComponents(String components) {
			searchRequest.setComponents(components);
			return this;
		}

		public SearchRequestBuilder setEmail(String email) {
			searchRequest.setEmail(email);
			return this;
		}

		public SearchRequestBuilder setQueryLang(String queryLang) {
			searchRequest.setLang(queryLang);
			return this;
		}

		public SearchRequestBuilder setLang(String lang) {
			searchRequest.setLang(lang);
			return this;
		}

		public SearchRequestBuilder setQuery(String query) {
			searchRequest.setQuery(query);
			return this;
		}

		public SearchRequestBuilder setQueryAsExecId() {
			setQueryAsExecId(Purpose.SEARCH);
			return this;
		}

		public SearchRequestBuilder setQueryAsExecId(Purpose purpose) {
			searchRequest.setQuery(SearchRestAPIUtils.getExecId(purpose));
			return this;
		}

		public SearchRequestBuilder setTag(String tag) {
			searchRequest.setTag(tag);
			return this;
		}

		public SearchRequestBuilder setUserid(String userid) {
			searchRequest.setUserid(userid);
			return this;
		}

		public SearchRequestBuilder setScope(Scope scope) {
			searchRequest.setScope(scope);
			return this;
		}

		public SearchRequestBuilder setSortKey(SortKey sortKey) {
			searchRequest.setSortKey(sortKey);
			return this;
		}

		public SearchRequestBuilder setSortOrder(SortOrder sortOrder) {
			searchRequest.setSortOrder(sortOrder);
			return this;
		}

		public SearchRequestBuilder setIncludeField(String includeField) {
			searchRequest.setIncludeField(includeField);
			return this;
		}

		public SearchRequestBuilder setFieldConstraints(FieldConstraint[] fieldConstraints) {
			searchRequest.setFieldConstraints(fieldConstraints);
			return this;
		}

		public SearchRequestBuilder setFieldNotConstraints(FieldConstraint[] fieldNotConstraints) {
			searchRequest.setFieldNotConstraints(fieldNotConstraints);
			return this;
		}

		public SearchRequestBuilder setRangeConstraints(RangeConstraint[] rangeConstraints) {
			searchRequest.setRangeConstraints(rangeConstraints);
			return this;
		}

		public SearchRequestBuilder setRangeNotConstraints(RangeConstraint[] rangeNotConstraints) {
			searchRequest.setRangeNotConstraints(rangeNotConstraints);
			return this;
		}

		public SearchRequestBuilder setFacets(Facet[] facets) {
			searchRequest.setFacets(facets);
			return this;
		}

		public SearchRequestBuilder setSocialConstraints(SocialConstraint[] socialConstraints) {
			searchRequest.setSocialConstraints(socialConstraints);
			return this;
		}

		public SearchRequestBuilder setSocialConstraint(SocialConstraint socialConstraint) {
			SocialConstraint[] personSocialConstraint = new SocialConstraint[1];
			personSocialConstraint[0] = socialConstraint;
			searchRequest.setSocialConstraints(personSocialConstraint);
			return this;
		}

		public SearchRequestBuilder setHighlight(String[] highlight) {
			searchRequest.setHighlight(highlight);
			return this;
		}

		public SearchRequestBuilder setSearchRequestEngine(SearchRequestEngine searchRequestEngine) {
			searchRequest.setSearchRequestEngine(searchRequestEngine);
			return this;
		}

		public SearchRequestBuilder setScope_secondary(Scope scope_secondary) {
			searchRequest.setScope_secondary(scope_secondary);
			return this;
		}

		public SearchResponse buildAndExecute() throws Exception {
			SearchRequest searchRequest = build();
			return executeQuery(searchRequest);
		}

		public SearchRequest build() {
			return searchRequest;
		}
	}
}
