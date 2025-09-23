
package com.ibm.lconn.automation.framework.services.search.request.quickresults;

import java.util.Map;



public class QuickResultsCommunityViewedPostRequest extends QuickResultsPostRequest {

	public final static String QUICK_RESULTS_SOURCE_COMMUNITY = "COMMUNITIES";
	public final static String QUICK_RESULTS_ITYPE_COMMUNITY = "COMMUNITY";
	
	private String community;

	/* 
	 * Request Params Example:
	 * contentId:93a83572-1a0b-485d-a8b9-a911612d8d8d
	 * itemType:COMMUNITY
	 * source:COMMUNITIES
	 * community:93a83572-1a0b-485d-a8b9-a911612d8d8d
	 * contentTitle:All About Whales
	 * contentLink:https://icstage.swg.usma.ibm.com/ic4/communities/service/html/communitystart?communityUuid=93a83572-1a0b-485d-a8b9-a911612d8d8d
	 */
		
	public QuickResultsCommunityViewedPostRequest(String communityId,
			String communityTitle, String communityCreatorId, String communityCreateTs,
			String communityLink) {
		super(communityId, QUICK_RESULTS_SOURCE_COMMUNITY, communityTitle, communityCreatorId,
				communityCreateTs, communityLink, QUICK_RESULTS_ITYPE_COMMUNITY);
		this.community = communityId;
	}

	public String getCommunity() {
		return community;
	}
	
	@Override
	protected String getContentPath() {
		return "/communities/service/html/communitystart?communityUuid="+contentId;
	}
	

	protected void addToParams(Map<String,String> paramsMap){
		super.addToParams(paramsMap);
		if (community != null){
			paramsMap.put("community", community);
		}
	}

	@Override
	public String toString() {
		return "QuickResultsCommunityViewedPostRequest [community=" + community
				+ ", toString()=" + super.toString() + "]";
	}
	
	
}