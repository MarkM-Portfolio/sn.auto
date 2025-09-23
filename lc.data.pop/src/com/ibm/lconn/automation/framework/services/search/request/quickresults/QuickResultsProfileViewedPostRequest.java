
package com.ibm.lconn.automation.framework.services.search.request.quickresults;



public class QuickResultsProfileViewedPostRequest extends QuickResultsPostRequest {

	public final static String QUICK_RESULTS_SOURCE_PROFILE = "PROFILES";
	public final static String QUICK_RESULTS_ITYPE_PROFILE = "ProfilesView";
	
	/* 
	 * Request Params Example:
	 * 
	 * contentId:9a9f43c0-8f0a-1028-8f2e-db07163b51b2
	 * itemType:ProfilesView
	 * source:PROFILES
	 * contentLink:https://icstage.swg.usma.ibm.com/ic4/profiles/html/profileView.do?userid=9a9f43c0-8f0a-1028-8f2e-db07163b51b2&lang=en_us
	 * contentTitle:Ina Schmatchenko
	 * 
	 */
		
	public QuickResultsProfileViewedPostRequest(String profileId,
			String profileTitle, String profileCreatorId, String profileCreateTs,
			String profileLink) {
		super(profileId, QUICK_RESULTS_SOURCE_PROFILE, profileTitle, profileCreatorId,
				profileCreateTs, profileLink, QUICK_RESULTS_ITYPE_PROFILE);
	}
	
	@Override
	protected String getContentPath() {
		return "/profiles/html/profileView.do?userid=" + contentId + "&lang=en_us";
	}
	
	@Override
	public String toString() {
		return "QuickResultsProfileViewedPostRequest [toString()="
				+ super.toString() + "]";
	}
	
}