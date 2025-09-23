
package com.ibm.lconn.automation.framework.services.search.request.quickresults;

import java.util.Map;



public abstract class QuickResultsWithContainerPostRequest extends QuickResultsPostRequest{

	protected String contentContainerId;
	protected String contentContainerTitle;
	protected String contentContainerLink;
	
	public QuickResultsWithContainerPostRequest(String contentId,
			String source, String contentTitle, String contentCreatorId,
			String contentCreateTs, String contentLink, String itemType,
			String contentContainerId, String contentContainerTitle,
			String contentContainerLink) {
		super(contentId, source, contentTitle, contentCreatorId,
				contentCreateTs, contentLink, itemType);
		this.contentContainerId = contentContainerId;
		this.contentContainerTitle = contentContainerTitle;
		this.contentContainerLink = contentContainerLink;
	}


	public String getContentContainerId() {
		return contentContainerId;
	}
	public String getContentContainerTitle() {
		return contentContainerTitle;
	}
	public String getContentContainerLink() {
		return contentContainerLink;
	}

	@Override
	public String toString() {
		return "QuickResultsWithContainerPostRequest [contentContainerId="
				+ contentContainerId + ", contentContainerTitle="
				+ contentContainerTitle + ", contentContainerLink="
				+ contentContainerLink + ", toString()=" + super.toString()
				+ "]";
	}
	
	protected void addToParams(Map<String,String> paramsMap){
		super.addToParams(paramsMap);
		if (contentContainerId != null){
			paramsMap.put("contentContainerId", contentContainerId);
		}
		if (contentContainerTitle != null){
			paramsMap.put("contentContainerTitle", contentContainerTitle);
		}
		if (contentContainerLink != null){
			paramsMap.put("contentContainerLink", contentContainerLink);
		}
	}
	
}
