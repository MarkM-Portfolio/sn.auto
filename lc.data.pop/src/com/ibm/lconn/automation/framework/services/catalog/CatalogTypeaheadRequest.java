package com.ibm.lconn.automation.framework.services.catalog;


public class CatalogTypeaheadRequest{
	
	private String prefix= null;
	private String limit= null;
	
	public CatalogTypeaheadRequest() {
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	

}

