
package com.ibm.lconn.automation.framework.services.search.request.quickresults;



public class QuickResultsForumViewedPostRequest extends QuickResultsWithContainerPostRequest{


	public final static String QUICK_RESULTS_SOURCE_FORUM = "FORUMS";
	public final static String QUICK_RESULTS_ITYPE_FORUM = "FORUM";

	/* 
	 * Request Params Example:
	 * 
	 * contentId:13b4dd36-225a-411e-9f28-6bce66f13d44
	 * itemType:FORUM
	 * source:FORUMS
	 * forum:b540a07f-7e25-4bbe-a101-36057879a621
	 * contentTitle:test for activity
	 * contentLink:https://icstage.swg.usma.ibm.com/ic4/forums/html/forum?id=13b4dd36-225a-411e-9f28-6bce66f13d44	
 	 * 
	 */ 

	public QuickResultsForumViewedPostRequest(String forumId, String forumTitle,
			String forumCreatorId, String forumCreateTs,
			String forumLink, String forumContainerId, String forumContainerTitle,
			String forumContainerLink) {
		super(forumId, QUICK_RESULTS_SOURCE_FORUM, forumTitle, forumCreatorId, forumCreateTs,
				forumLink, QUICK_RESULTS_ITYPE_FORUM, forumContainerId, forumContainerTitle,
				forumContainerLink);
	}

	@Override
	protected String getContentPath() {
		return "/forums/html/forum?id="+contentId;
	}
	

	@Override
	public String toString() {
		return "QuickResultsStandaloneForumViewedPostRequest [toString()="
				+ super.toString() + "]";
	}

}
