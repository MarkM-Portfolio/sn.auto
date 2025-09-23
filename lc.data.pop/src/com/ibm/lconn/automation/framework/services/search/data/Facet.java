package com.ibm.lconn.automation.framework.services.search.data;

public class Facet{
	
	private String facetId;
	private int facetCount;
	private Integer facetDepth;
	private String facetSortOrder;
	
	public Facet(String facetId, int facetCount,
			Integer facetDepth, String facetSortOrder) {
		this.facetId = facetId;
		this.facetCount = facetCount;
		this.facetDepth = facetDepth;
		this.facetSortOrder = facetSortOrder;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		appendIfNotNull(sb, facetId, "{\"id\":" + "\"" + facetId + "\"" + ", ");
		appendIfNotNull(sb, facetCount, "\"count\":" + facetCount + ", ");
		appendIfNotNull(sb, facetDepth, "\"depth\": " + facetDepth + ", ");
		appendIfNotNull(sb, facetSortOrder, "\"sortOrder\": " + "\"" + facetSortOrder + "\"");
		if (!sb.toString().equals("")){
			sb.append("}");
		}
		return sb.toString();
	}
	
	private void appendIfNotNull(StringBuilder sb, Object o, String str) {
		if (o != null) {
			sb.append(str);
		}
	}
}

