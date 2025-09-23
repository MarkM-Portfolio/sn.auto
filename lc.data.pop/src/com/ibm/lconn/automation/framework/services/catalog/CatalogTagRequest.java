package com.ibm.lconn.automation.framework.services.catalog;

import java.util.Locale;


public class CatalogTagRequest{

	private static final String LIMIT = "limit";
	private static final String SORT_KEY = "sortKey";
	private static final String START = "start";
	private static final String SORT_ORDER = "sortOrder";
	private static final String LOCALE = "locale";

	private Integer limit = 30; 
	private SortKey sortKey = SortKey.weight;
	private SortOrder sortOrder = SortOrder.desc;
	private Integer start = null;
	private Locale locale = null;
	
	public enum SortOrder{asc, desc};
	public enum SortKey{weight, name};
	
	public CatalogTagRequest() {
	}
	
	
	public Integer getLimit() {
		return limit;
	}


	public void setLimit(Integer limit) {
		this.limit = limit;
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


	public static String buildPath(String url, CatalogTagRequest catalogTagRequest) {
		StringBuffer catalogTagPath = new StringBuffer();
		catalogTagPath.append(url);
		
		if(catalogTagPath.toString().lastIndexOf("?") == -1){
			catalogTagPath.append("?");
		}else{
			catalogTagPath.append("&");
		}
		addLimit(catalogTagPath, catalogTagRequest.getLimit());
		addTagSortKey(catalogTagPath, catalogTagRequest.getSortKey());
		addStart(catalogTagPath, catalogTagRequest.getStart());
		addTagSortOrder(catalogTagPath, catalogTagRequest.getSortOrder());
		addLocale(catalogTagPath ,catalogTagRequest.getLocale());
		catalogTagPath.deleteCharAt(catalogTagPath.length() - 1);
		return catalogTagPath.toString();
	}
	
	private static void addLocale(StringBuffer catalogTagPath, Locale locale) {
		addParameter(catalogTagPath, LOCALE, locale);
	}


	private static void addTagSortOrder(StringBuffer catalogTagPath,
			SortOrder sortOrder) {
		addParameter(catalogTagPath, SORT_ORDER, sortOrder);
	}


	private static void addStart(StringBuffer catalogTagPath, Integer start) {
		addParameter(catalogTagPath, START, start);
	}


	private static void addTagSortKey(StringBuffer catalogTagPath,
			SortKey sortKey) {
		addParameter(catalogTagPath, SORT_KEY, sortKey);
	}


	private static void addLimit(StringBuffer catalogTagPath, Integer limit) {
		addParameter(catalogTagPath, LIMIT, limit);
	}


	private static void addParameter(StringBuffer catalogPath ,String paramName, Object paramValue){
		if(paramValue != null){
			catalogPath.append(paramName + "=");
			catalogPath.append(paramValue);
			catalogPath.append("&");
		}	
	}

}

