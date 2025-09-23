package com.ibm.lconn.automation.framework.services.catalog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;


public class CatalogListRequest{
	
	private static final String QUERY_LANG = "queryLang";
	private static final String QUERY = "query";
	private static final String RESULTS = "results";
	private static final String SORT_KEY = "sortKey";
	private static final String START = "start";
	private static final String SORT_ORDER = "sortOrder";
	private static final String FORMAT = "format";
	
	
	private String queryLang = null;
	private String query = null;
	private Integer results = 10;
	private SortKey sortKey = SortKey.update_date;
	private Integer start = null;
	private Locale locale = null;
	private SortOrder sortOrder = SortOrder.desc;
	private Format format = null;
	private List<ConstraintParameter> constraints = null;
	private List<FacetParameter> facets = null;
	
	public enum SortOrder{asc,desc};
	public enum SortKey{title,update_date,FIELD_COMMUNITY_MEMBER_COUNT,FIELD_LAST_VISITED_DATE};
	public enum Format{XML,atom};
	
	
	private final static Logger API_LOGGER = Logger.getLogger("APIEndPoint");

	public CatalogListRequest() {
	}
	
	public String getQueryLang() {
		return queryLang;
	}

	public void setQueryLang(String queryLang) {
		this.queryLang = queryLang;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public Integer getResults() {
		return results;
	}

	public void setResults(Integer results) {
		this.results = results;
	}

	public SortKey getSortKey() {
		return sortKey;
	}

	public void setSortKey(SortKey sortKey) {
		this.sortKey = sortKey;
	}
	
	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}
	
	public List<ConstraintParameter> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<ConstraintParameter> constraints) {
		this.constraints = constraints;
	}
	
	public List<FacetParameter> getFactes() {
		return facets;
	}

	public void setFactes(List<FacetParameter> facets) {
		this.facets = facets;
	}

	public static String buildPath(String url,
			CatalogListRequest catalogListRequest) {
		StringBuffer catalogPath = new StringBuffer();
		catalogPath.append(url);
		
		if(catalogPath.toString().lastIndexOf("?") == -1){
			catalogPath.append("?");
		}else{
			catalogPath.append("&");
		}
		addQueryLang(catalogPath, catalogListRequest.getQueryLang());
		addQuery(catalogPath, catalogListRequest.getQuery());
		addResults(catalogPath, catalogListRequest.getResults());
		addSortKey(catalogPath, catalogListRequest.getSortKey());
		addStart(catalogPath, catalogListRequest.getStart());
		addSortOrder(catalogPath, catalogListRequest.getSortOrder());
		addFormat(catalogPath, catalogListRequest.getFormat());
		addLocale(catalogPath ,catalogListRequest.getLocale());
		addConstraints(catalogPath, catalogListRequest.getConstraints());
		addFactes(catalogPath, catalogListRequest.getFactes());
		catalogPath.deleteCharAt(catalogPath.length() - 1);
		return catalogPath.toString();
	}
	
	private static void addQueryLang(StringBuffer catalogPath ,String queryLang){
		addParameter(catalogPath, QUERY_LANG, queryLang);
	}
	
	private static void addQuery(StringBuffer catalogPath ,String query){
		addParameter(catalogPath, QUERY, query);
	}
	
	private static void addResults(StringBuffer catalogPath ,Integer results){
		addParameter(catalogPath, RESULTS, results);
	}
	
	private static void addSortKey(StringBuffer catalogPath ,SortKey sortKey){
		addParameter(catalogPath, SORT_KEY, sortKey);
	}
	
	private static void addStart(StringBuffer catalogPath ,Integer start){
		addParameter(catalogPath, START, start);
	}
	
	private static void addSortOrder(StringBuffer catalogPath ,SortOrder sortOrder){
		addParameter(catalogPath, SORT_ORDER, sortOrder);
	}
	
	private static void addFormat(StringBuffer catalogPath ,Format format){
		addParameter(catalogPath, FORMAT, format);
	}
	
	private static void addLocale(StringBuffer catalogPath ,Locale locale){
		if(locale != null){
			catalogPath.append("locale=");
			catalogPath.append(locale);
			catalogPath.append("&");
		}
	}
	
	private static void addConstraints(StringBuffer catalogPath ,List<ConstraintParameter> constraints){
		if(constraints != null){
			String constraintStr = null;
			for(ConstraintParameter constraint : constraints){
				try {
					constraintStr = URLEncoder.encode(constraint.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					API_LOGGER.finer(e.getMessage());
				}
				catalogPath.append("constraint=");
				catalogPath.append(constraintStr);
				catalogPath.append("&");
			}
		}
	}
	
	private static void addFactes(StringBuffer catalogPath ,List<FacetParameter> facets){
		if(facets != null){
			String facetStr = null;
			for(FacetParameter facet : facets){
				try {
					facetStr = URLEncoder.encode(facet.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					API_LOGGER.finer(e.getMessage());
				}
				catalogPath.append("facet=");
				catalogPath.append(facetStr);
				catalogPath.append("&");
			}
		}
	}
	
	private static void addParameter(StringBuffer catalogPath ,String paramName, Object paramValue){
		if(paramValue != null){
			catalogPath.append(paramName + "=");
			catalogPath.append(paramValue);
			catalogPath.append("&");
		}	
	}

}

