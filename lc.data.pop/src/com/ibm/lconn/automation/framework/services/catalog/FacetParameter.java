package com.ibm.lconn.automation.framework.services.catalog;

public class FacetParameter{
	
	private String facetId;
	private int facetCount;
	private int facetDepth;
	
	public FacetParameter(String facetId, int facetCount, int facetDepth) {
		this.facetId = facetId;
		this.facetCount = facetCount;
		this.facetDepth = facetDepth;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"id\":").append("\"").append(facetId).append("\"").append(", ");
		sb.append("\"count\":").append(facetCount).append(", ");
		sb.append("\"depth\": ").append(facetDepth);
		sb.append("}");
		return sb.toString();
	}


}

