package com.ibm.lconn.automation.framework.services.search.request;

import java.util.Arrays;

import com.ibm.lconn.automation.framework.services.search.data.Facet;
import com.ibm.lconn.automation.framework.services.search.data.FieldConstraint;
import com.ibm.lconn.automation.framework.services.search.data.RangeConstraint;
import com.ibm.lconn.automation.framework.services.search.data.Scope;
import com.ibm.lconn.automation.framework.services.search.data.SocialConstraint;
import com.ibm.lconn.automation.framework.services.search.data.SortKey;
import com.ibm.lconn.automation.framework.services.search.data.SortOrder;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse.SearchRequestEngine;


public class SearchRequest extends BaseSearchRequest{
	
	private String component= null;
	private String components = null;
	private String email = null; 
	private String queryLang = null;
	private String lang = null;
	private String query = null;
	private String tag = null;
	private String userid = null;
	private Scope scope = null;
	private Scope scope_secondary = null;
	


	private SortKey sortKey = null;
	private SortOrder sortOrder = null;
	private String includeField = null;
	private FieldConstraint[] fieldConstraints = null;
	private RangeConstraint[] rangeConstraints = null;
	private FieldConstraint[] fieldNotConstraints = null;
	private RangeConstraint[] rangeNotConstraints = null;
	private Facet[] facets = null;
	private SocialConstraint[] socialConstraints = null;
	private String[] highlight = null;
	private SearchRequestEngine searchRequestEngine = null;
	
	public SearchRequest() {
		super();
		this.setPageSize(25);
	}
	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getQueryLang() {
		return queryLang;
	}
	public String getLang() {
		return lang;
	}
	public void setQueryLang(String queryLang) {
		this.queryLang = queryLang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public SortKey getSortKey() {
		return sortKey;
	}

	public void setSortKey(SortKey sortKey) {
		this.sortKey = sortKey;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getIncludeField() {
		return includeField;
	}

	public void setIncludeField(String includeField) {
		this.includeField = includeField;
	}

	public FieldConstraint[] getFieldConstraints() {
		return fieldConstraints;
	}

	public void setFieldConstraints(FieldConstraint[] fieldConstraints) {
		this.fieldConstraints = fieldConstraints;
	}
	
	public FieldConstraint[] getFieldNotConstraints() {
		return fieldNotConstraints;
	}

	public void setFieldNotConstraints(FieldConstraint[] fieldNotConstraints) {
		this.fieldNotConstraints = fieldNotConstraints;
	}

	public RangeConstraint[] getRangeConstraints() {
		return rangeConstraints;
	}

	public void setRangeConstraints(RangeConstraint[] rangeConstraints) {
		this.rangeConstraints = rangeConstraints;
	}
	
	public RangeConstraint[] getRangeNotConstraints() {
		return rangeNotConstraints;
	}

	public void setRangeNotConstraints(RangeConstraint[] rangeNotConstraints) {
		this.rangeNotConstraints = rangeNotConstraints;
	}
	
	public Facet[] getFacets() {
		return facets;
	}

	public void setFacets(Facet[] facets) {
		this.facets = facets;
	}
	
	public SocialConstraint[] getSocialConstraints() {
		return socialConstraints;
	}

	public void setSocialConstraints(SocialConstraint[] socialConstraints) {
		this.socialConstraints = socialConstraints;
	}
	
	public String[] getHighlight() {
		return highlight;
	}

	public void setHighlight(String[] highlight) {
		this.highlight = highlight;
	}

	public SearchRequestEngine getSearchRequestEngine() {
		return searchRequestEngine;
	}
	public void setSearchRequestEngine(SearchRequestEngine searchRequestEngine) {
		this.searchRequestEngine = searchRequestEngine;
	}
	public Scope getScope_secondary() {
		return scope_secondary;
	}
	public void setScope_secondary(Scope scope_secondary) {
		this.scope_secondary = scope_secondary;
	}
	
	@Override
	public String toString() {
		return "SearchRequest [component=" + component + ", components="
				+ components + ", email=" + email + ", queryLang=" + queryLang
				+ ", lang=" + lang + ", query=" + query + ", tag=" + tag + ", userid=" + userid
				+ ", scope=" + scope + ", sortKey=" + sortKey + ", sortOrder="
				+ sortOrder + ", includeField=" + includeField+ ", scope_secondary="+scope_secondary
				+ ", fieldConstraints=" + Arrays.toString(fieldConstraints)
				+ ", categoryConstraints=" + Arrays.toString(getCategoryConstraints())
				+ ", categoryNotConstraints=" + Arrays.toString(getCategoryNotConstraints())
				+ ", rangeConstraints=" + Arrays.toString(rangeConstraints)
				+ ", fieldNotConstraints="
				+ Arrays.toString(fieldNotConstraints)
				+ ", rangeNotConstraints="
				+ Arrays.toString(rangeNotConstraints) + ", facets="
				+ Arrays.toString(facets) + ", socialConstraints="
				+ Arrays.toString(socialConstraints) + ", highlight="
				+ Arrays.toString(highlight) + ", contextPath=" + getContextPath()
				+ ", searchRequestEngine=" + searchRequestEngine
				+ "]";
	}
	

	public String getContextPathString() {
		return "/" + getContextPath().toString();
	}

	

	
}

