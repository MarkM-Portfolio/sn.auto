
package com.ibm.lconn.automation.framework.services.search.request;


public class QuickResultsRequest {

	private String query;
	private Integer pageSize = 10;
	private Integer page = 1;
	private Boolean highlight = true;
	
	public QuickResultsRequest(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	
	public Boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(Boolean highlight) {
		this.highlight = highlight;
	}
	



	@Override
		public String toString() {
		StringBuilder params = new StringBuilder();
		params.append("query").append("=").append(query);
		if ( pageSize!=null ){
			params.append("&").append("pageSize").append("=").append(pageSize);
		}
		if ( page!=null ){
			params.append("&").append("page").append("=").append(page);
		}
		if(highlight!=null){
			params.append("&").append("highlight").append("=").append(highlight);
		}
		return params.toString();
	}
}