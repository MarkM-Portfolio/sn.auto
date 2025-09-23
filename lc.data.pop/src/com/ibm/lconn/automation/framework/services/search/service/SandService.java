package com.ibm.lconn.automation.framework.services.search.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.ConnectionType;
import com.ibm.lconn.automation.framework.services.search.data.FeedWithCacheControlHeader;
import com.ibm.lconn.automation.framework.services.search.data.Person;
import com.ibm.lconn.automation.framework.services.search.data.SocialNetworkType;
import com.ibm.lconn.automation.framework.services.search.nodes.SearchResult;
import com.ibm.lconn.automation.framework.services.search.request.SandSocialNetworkRequest;
import com.ibm.lconn.automation.framework.services.search.request.SandSocialPathRequest;
import com.ibm.lconn.automation.framework.services.search.request.SocialRecommendationRequest;
import com.ibm.lconn.automation.framework.services.search.response.SearchResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse;
import com.ibm.lconn.automation.framework.services.search.response.SearchSandResponse.RecommendationsCacheAccessStatus;

public class SandService extends SearchService {
	
	private static final String UTF_8_ENCODING_TYPE = "UTF-8";
	
	private static final String DIVERSITY_BOOST = "diversityboost";
	private static final String RANDOMIZE = "randomize"; 
	private static final String DATE_BOOST = "dateboost";
	private static final String TARGET = "target";
	private static final String SOURCE = "source";
	private static final String MAX_LENGTH = "maxLength";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String TYPE = "type";
	
	public SandService(AbderaClient client, ServiceEntry service) {
		super(client, service);
		
		if(service != null)
			this.setFoundService(true);
	}
	
	//social recommendations
	public SearchResponse searchSocialRecommendations(SocialRecommendationRequest socialRecommendationRequest) {
		return searchSocialRecommend(service.getServiceURLString() + socialRecommendationRequest.getContextPathString() + URLConstants.SEARCH_SOCIAL_RECOMMENDATIONS, socialRecommendationRequest);
	}
	public SearchSandResponse searchSocialRecommendationsWithCache(SocialRecommendationRequest socialRecommendationRequest) {
		return searchSocialRecommendWithCache(service.getServiceURLString() + socialRecommendationRequest.getContextPathString() + URLConstants.SEARCH_SOCIAL_RECOMMENDATIONS, socialRecommendationRequest);
	}
	//social graph
	public ExtensibleElement searchSocialGraph(SandSocialPathRequest sandSocialPathRequest) {
		return getSocialGraphFeed(service.getServiceURLString() + sandSocialPathRequest.getContextPathString() + URLConstants.SEARCH_SOCIAL_GRAPH, sandSocialPathRequest);
	}
	
	//social network
	public ExtensibleElement searchSocialNetwork(SandSocialNetworkRequest sandSocialNetworkRequest) {
		return getSocialNetworkFeed(service.getServiceURLString() + sandSocialNetworkRequest.getContextPathString() + URLConstants.SEARCH_SOCIAL_NETWORK, sandSocialNetworkRequest);
	}
	
	private ExtensibleElement getSocialGraphFeed(String url, SandSocialPathRequest sandSocialPathRequest){
		String socialGraphPath = buildSocialGraphPath(url, sandSocialPathRequest);
		ExtensibleElement socialGraphFeed = getFeed(socialGraphPath.toString());
		return socialGraphFeed;
	}
	
	private ExtensibleElement getSocialNetworkFeed(String url, SandSocialNetworkRequest sandSocialNetworkRequest){
		String socialNetworkPath = buildSocialNetworkPath(url, sandSocialNetworkRequest);
		ExtensibleElement socialNetworkFeed = getFeed(socialNetworkPath.toString());
		return socialNetworkFeed;
	}
	
	
	public SearchResponse searchSocialRecommend(String sourceURL, SocialRecommendationRequest socialRecommendationRequest){
		StringBuffer searchPath = new StringBuffer();
		searchPath.append(sourceURL);
		
		if(searchPath.toString().lastIndexOf("?") == -1){
			searchPath.append("?");
		}else{
			searchPath.append("&");
		}
		addPage(searchPath ,socialRecommendationRequest.getPage());
		addPageSize(searchPath ,socialRecommendationRequest.getPageSize());
		addLocale(searchPath ,socialRecommendationRequest.getLocale());
		addStart(searchPath ,socialRecommendationRequest.getStart());
		addEvidence(searchPath ,socialRecommendationRequest.getEvidence());
		addDiversityboost (searchPath ,socialRecommendationRequest.getDiversityboost());
		addRandomize(searchPath ,socialRecommendationRequest.isRandomize());
		addDateboost(searchPath ,socialRecommendationRequest.getDateboost());
		addCategoryConstraints(searchPath ,socialRecommendationRequest.getCategoryConstraints());
		
		searchPath.deleteCharAt(searchPath.length() - 1);
		
		SearchResponse searchResponse = calculateSearchResults(searchPath.toString());
		
		return searchResponse;
	}
	public SearchSandResponse searchSocialRecommendWithCache(String sourceURL, SocialRecommendationRequest socialRecommendationRequest){
		StringBuffer searchPath = new StringBuffer();
		searchPath.append(sourceURL);
		
		if(searchPath.toString().lastIndexOf("?") == -1){
			searchPath.append("?");
		}else{
			searchPath.append("&");
		}
		addPage(searchPath ,socialRecommendationRequest.getPage());
		addPageSize(searchPath ,socialRecommendationRequest.getPageSize());
		addLocale(searchPath ,socialRecommendationRequest.getLocale());
		addStart(searchPath ,socialRecommendationRequest.getStart());
		addEvidence(searchPath ,socialRecommendationRequest.getEvidence());
		addDiversityboost (searchPath ,socialRecommendationRequest.getDiversityboost());
		addRandomize(searchPath ,socialRecommendationRequest.isRandomize());
		addDateboost(searchPath ,socialRecommendationRequest.getDateboost());
		addCategoryConstraints(searchPath ,socialRecommendationRequest.getCategoryConstraints());
		
		searchPath.deleteCharAt(searchPath.length() - 1);
		
		SearchSandResponse searchResponse = calculateSearchSandResults(searchPath.toString());
		
		return searchResponse;
	}
	
	protected SearchSandResponse calculateSearchSandResults(String searchPath){
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		Feed searchFeed = null;

		FeedWithCacheControlHeader feedWithCacheControl = getFeedWithCacheControlHeader(searchPath);
		ExtensibleElement feed = feedWithCacheControl.getFeed();
		
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){
			if (feed instanceof Feed){
				searchFeed = (Feed)feed;
			}
		}else{
			getApiLogger().debug(feed.getAttributeValue(StringConstants.API_ERROR)+" error in feed:"+ feed.toString());
			return null;
		}
		if (searchFeed != null) {
			for(Entry entry : searchFeed.getEntries()) {
				SearchResult result = new SearchResult(entry);
				results.add(result);
			}
		}
		
		RecommendationsCacheAccessStatus recommendationsCacheAccessStatus = getRecommendationsCacheAccessStatus(searchFeed);
		return new SearchSandResponse(results,getRespType(),getRespStatus(),recommendationsCacheAccessStatus, searchFeed, 
				feedWithCacheControl.getCacheControl());
	}
	
	private RecommendationsCacheAccessStatus getRecommendationsCacheAccessStatus(Feed searchFeed) {
		RecommendationsCacheAccessStatus recommendationsCacheAccessStatus = RecommendationsCacheAccessStatus.None;
		try {
			if (searchFeed != null) {
				String recommendationsCacheAccessStatusString = searchFeed.getSimpleExtension(IBM_XML_NAMESPACE, "recommendationsCacheAccessStatus", "ibmsc");
				if (RecommendationsCacheAccessStatus.Hit.name().equalsIgnoreCase(recommendationsCacheAccessStatusString)) {
					recommendationsCacheAccessStatus = RecommendationsCacheAccessStatus.Hit; 
				} else if (RecommendationsCacheAccessStatus.Miss.name().equalsIgnoreCase(recommendationsCacheAccessStatusString)){
					recommendationsCacheAccessStatus = RecommendationsCacheAccessStatus.Miss;
					
				}				
			}
		} catch (Throwable e) {
			logger.warning("failed to fetch RecommendationsCacheAccessStatus. Default value None returned . exception: "+ e.toString());
		}
		return recommendationsCacheAccessStatus;
	}
	
	private String buildSocialGraphPath(String url, SandSocialPathRequest sandSocialPathRequest){
		StringBuffer socialGraphPath = new StringBuffer();
		socialGraphPath.append(url);
		
		if(socialGraphPath.toString().lastIndexOf("?") == -1){
			socialGraphPath.append("?");
		}else{
			socialGraphPath.append("&");
		}
		addEvidence(socialGraphPath ,sandSocialPathRequest.getEvidence());
		addTargetPerson(socialGraphPath ,sandSocialPathRequest.getTargetPerson());
		addSourcePerson(socialGraphPath ,sandSocialPathRequest.getSourcePerson());
		addMaxLenght(socialGraphPath ,sandSocialPathRequest.getMaxLenght());
		addConnectionType(socialGraphPath, sandSocialPathRequest.getConnectionType());
		socialGraphPath.deleteCharAt(socialGraphPath.length() - 1);
		return socialGraphPath.toString();
	}
	
	private String buildSocialNetworkPath(String url, SandSocialNetworkRequest sandSocialNetworkRequest){
		StringBuffer socialNetworkPath = new StringBuffer();
		socialNetworkPath.append(url);
		
		if(socialNetworkPath.toString().lastIndexOf("?") == -1){
			socialNetworkPath.append("?");
		}else{
			socialNetworkPath.append("&");
		}
		addEvidence(socialNetworkPath ,sandSocialNetworkRequest.getEvidence());
//		addTargetPerson(socialNetworkPath ,sandSocialNetworkRequest.getTargetPerson());
		addSourcePerson(socialNetworkPath ,sandSocialNetworkRequest.getSourcePerson());
//		addMaxLenght(socialNetworkPath ,sandSocialNetworkRequest.getMaxLenght());
		addPage(socialNetworkPath, sandSocialNetworkRequest.getPage());
		addPageSize(socialNetworkPath, sandSocialNetworkRequest.getPageSize());
		addConnectionType(socialNetworkPath, sandSocialNetworkRequest.getConnectionType());
		addType(socialNetworkPath, sandSocialNetworkRequest.getType());
		socialNetworkPath.deleteCharAt(socialNetworkPath.length() - 1);
		return socialNetworkPath.toString();
	}
	
	
	public ArrayList<SearchResult> search(String sourceURL){
		String searchPath = sourceURL;
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		Feed searchFeed = null;
		ExtensibleElement feed = getFeed(searchPath);
		
		if(feed.getAttributeValue(StringConstants.API_ERROR) == null){
			searchFeed = (Feed)getFeed(searchPath);
		}
		
		if (searchFeed != null) {
			for(Entry entry:  searchFeed.getEntries()) {
				SearchResult result = new SearchResult(entry);
				results.add(result);
			}
		}
		return results;
	}

	private void addTargetPerson(StringBuffer searchPath ,Person targetPerson){
		addPerson(searchPath, TARGET, targetPerson);
	}
	
	private void addSourcePerson(StringBuffer searchPath ,Person sourcePerson){
		addPerson(searchPath, SOURCE, sourcePerson);
	}
	
	private void addDiversityboost(StringBuffer searchPath ,Float diversityboost){
		addParameter(searchPath, DIVERSITY_BOOST, diversityboost);
	}
	
	private void addRandomize(StringBuffer searchPath ,Boolean randomize){
		addParameter(searchPath, RANDOMIZE, randomize);
	}
	
	private void addDateboost(StringBuffer searchPath ,Float dateBoost){
		addParameter(searchPath, DATE_BOOST, dateBoost);
	}
	
	private void addMaxLenght(StringBuffer searchPath ,Integer maxLenght){
		addParameter(searchPath, MAX_LENGTH, maxLenght);
	}
	
	private void addConnectionType(StringBuffer searchPath ,ConnectionType connectionType){
		addParameter(searchPath, CONNECTION_TYPE, connectionType);
	}
	
	private void addType(StringBuffer searchPath ,SocialNetworkType type){
		addParameter(searchPath, TYPE, type);
	}
	
	private void addPerson(StringBuffer searchPath ,String paramName, Person personValue){
		String personStr = null;
		if(personValue != null){
			try {
				personStr = URLEncoder.encode(personValue.toString(), UTF_8_ENCODING_TYPE);
			} catch (UnsupportedEncodingException e) {
				getApiLogger().debug(e.getMessage());
			}
			searchPath.append(paramName + "=");
			searchPath.append(personStr);
			searchPath.append("&");
		}	
	}
	
}

