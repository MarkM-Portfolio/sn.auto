
package com.ibm.lconn.automation.framework.services.search.request.quickresults;



public class QuickResultsBlogViewedPostRequest extends QuickResultsWithContainerPostRequest{


	private String blogPath = null;
	
	public final static String QUICK_RESULTS_SOURCE_BLOG = "blogs";
	public final static String QUICK_RESULTS_ITYPE_BLOG = "BLOG";

	/* 
	 * Request Params Example:
	 *
	 * contentId:f15ed843-ce66-46a7-9b90-6c99a83efe7d
	 * itemType:BLOG
	 * source:blogs
	 * contentLink:https://icstage.swg.usma.ibm.com/ic4/blogs/noga-qr/?lang=en_us
	 * contentTitle:Noga Quick Results Testing Blogs
	 */ 

	public QuickResultsBlogViewedPostRequest(String blogId, String blogTitle,
			String blogCreatorId, String blogCreateTs,
			String blogLink, String blogContainerId, String blogContainerTitle,
			String blogContainerLink, String blogPath) {
		super(blogId, QUICK_RESULTS_SOURCE_BLOG, blogTitle, blogCreatorId,
				blogCreateTs, blogLink, QUICK_RESULTS_ITYPE_BLOG, blogContainerId,
				blogContainerTitle, blogContainerLink);
		this.blogPath = blogPath;
	}
	

	@Override
	protected String getContentPath() {
		return "/blogs/"+ blogPath + "/?lang=en_us";
	}


	@Override
	public String toString() {
		return "QuickResultsStandaloneBlogViewedPostRequest [toString()="
				+ super.toString() + "]";
	}
	
	
}
