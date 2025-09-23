
package com.ibm.lconn.automation.framework.services.search.request.quickresults;




public class QuickResultsActivityEntryViewedPostRequest extends QuickResultsWithContainerPostRequest{


	public final static String QUICK_RESULTS_SOURCE_ACTIVITY = "ACTIVITIES";
	public final static String QUICK_RESULTS_ITYPE_ACTIVITY_ENTRY = "ENTRY";

	/* 
	 * Request Params Example:
	 *
	 * contentId:73449370-c9a7-4142-b19b-88c5659361a9
	 * itemType:ENTRY
	 * source:ACTIVITIES
	 * i:1
	 * contentTitle:Noga create test
	 * contentCreatorId:9a20f540-06b4-102c-8335-b3e03759c05f
	 * contentCreateTs:Wed Oct 22 2014 14:45:03 GMT+0300 (Jerusalem Daylight Time)
	 * contentLink:https://icstage.swg.usma.ibm.com/ic4/activities/service/html/activity/recent?activityUuid=73449370-c9a7-4142-b19b-88c5659361a9
	 * contentContainerId:73449370-c9a7-4142-b19b-88c5659361a9
	 * 
	 */ 

	public QuickResultsActivityEntryViewedPostRequest(String activityEntryId,
			String activityEntryTitle, String activityCreatorId, String activityCreateTs,
			String activityLink, String activityContainerId, String activityContainerTitle,
			String activityContainerLink) {
		super(activityEntryId, QUICK_RESULTS_SOURCE_ACTIVITY, activityEntryTitle, activityCreatorId, activityCreateTs,
				activityLink, QUICK_RESULTS_ITYPE_ACTIVITY_ENTRY, activityContainerId, activityContainerTitle,
				activityContainerLink);
	}

	@Override
	protected String getContentPath() {
		return "/activities/service/html/mainpage#activitypage,"+contentId;
	}
	

	@Override
	public String toString() {
		return "QuickResultsActivityViewedPostRequest [toString()="
				+ super.toString() + "]";
	}

	
}
