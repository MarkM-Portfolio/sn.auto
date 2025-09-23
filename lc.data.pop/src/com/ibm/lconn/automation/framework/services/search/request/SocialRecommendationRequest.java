package com.ibm.lconn.automation.framework.services.search.request;




public class SocialRecommendationRequest extends BaseSearchRequest{
	
	private Float diversityboost = null;
	private Boolean randomize;
	private Float dateboost = null; 
	
	public SocialRecommendationRequest()  {
		super();
	}
	
	public Float getDiversityboost() {
		return diversityboost;
	}

	public void setDiversityboost(Float diversityboost) {
		this.diversityboost = diversityboost;
	}

	public Boolean isRandomize() {
		return randomize;
	}

	public void setRandomize(Boolean randomize) {
		this.randomize = randomize;
	}

	public Float getDateboost() {
		return dateboost;
	}

	public void setDateboost(Float dateboost) {
		this.dateboost = dateboost;
	}

	@Override
	public String toString() {
		return "SocialRecommendationRequest [diversityboost=" + diversityboost
				+ ", randomize=" + randomize + ", dateboost=" + dateboost
				+ ", contextPath=" + getContextPath() + "]";
	}

	public String getContextPathString() {
		return "/" + getContextPath().toString();
	}
	
	
}

