package com.ibm.lconn.automation.framework.services.search.response;

import java.util.ArrayList;

import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;

import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;

public class SearchSandResponse {
	
	public enum RecommendationsCacheAccessStatus {
		Miss, Hit,None
	}
	
	RecommendationsCacheAccessStatus recommendationsCacheAccessStatus = null;
	ArrayList<SearchResult> results;
	ResponseType type;
	int status;
	Feed feed;
	String cacheControl;
	
	public SearchSandResponse(ArrayList<SearchResult> results, 
			ResponseType responseType, 
			int responseStatus ) {
		super();
		this.results = results;
		this.type = responseType;
		this.status = responseStatus;
		this.recommendationsCacheAccessStatus = RecommendationsCacheAccessStatus.None;
	}
	
	public SearchSandResponse(ArrayList<SearchResult> results, 
			ResponseType responseType, 
			int responseStatus, RecommendationsCacheAccessStatus recommendationsCacheAccessStatus ) {
		super();
		this.results = results;
		this.type = responseType;
		this.status = responseStatus;
		this.recommendationsCacheAccessStatus = recommendationsCacheAccessStatus;
	}
	
	public SearchSandResponse(ArrayList<SearchResult> results, 
			ResponseType responseType, 
			int responseStatus, 
			RecommendationsCacheAccessStatus recommendationsCacheAccessStatus,
			Feed feed, 
			String cacheControl) {
		this(results, responseType, responseStatus, recommendationsCacheAccessStatus);
		this.feed = feed;
		this.cacheControl = cacheControl;
	}
	
	public Feed getFeed() {
		return feed;
	}

	public String getCacheControl(){
		return cacheControl;
	}
	
	public ArrayList<SearchResult> getResults() {
		return results;
	}

	public void setResults(ArrayList<SearchResult> results) {
		this.results = results;
	}
	
	public int getStatus() {
		return status;
	}

	public ResponseType getType() {
		return type;
	}
	
	public RecommendationsCacheAccessStatus getRecommendationsCacheAccessStatus() {
		return recommendationsCacheAccessStatus;
	}
	
}
