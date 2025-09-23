package com.ibm.lconn.automation.framework.services.search.request;

import java.util.HashMap;
import java.util.Map;

import com.ibm.lconn.automation.framework.services.search.data.MetricsItemType;
import com.ibm.lconn.automation.framework.services.search.data.MetricsSource;


public class EventTrackerConsumerPostRequest {
	
	private String contentId;
	private MetricsItemType itemType;
	private MetricsSource source;
	private Boolean forwardToEventTrackerConsumerBVTServlet;
	private String scope;
	private String community;
	private String contentCorrelationId;
	private String contentContainerId;
	private String contentContainerTitle; 
	private String contentTitle;
	private String contentLink;
	private String contentContainterLink;
	private String contentCreatorId;
	private String contentCreateTs;

	public EventTrackerConsumerPostRequest(String contentId, MetricsItemType itemType,
			MetricsSource source, Boolean forwardToEventTrackerConsumerBVTServlet) {
		this.contentId = contentId;
		this.itemType = itemType;
		this.source = source;
		this.forwardToEventTrackerConsumerBVTServlet = forwardToEventTrackerConsumerBVTServlet;
	}
	
	public String getContentId(){
		return contentId;
	}
		
	public MetricsItemType getItemType(){
		return itemType;
	}
	
	public MetricsSource getSource(){
		return source;
	}
	
	public Boolean getForwardToEventTrackerConsumerBVTServlet(){
		return forwardToEventTrackerConsumerBVTServlet;
	}
	
	public String getScope(){
		return scope;
	}
	
	public String getCommunity(){
		return community;
	}
	
	public String getContentCorrelationId(){
		return contentCorrelationId;
	}
	
	public String getContentContainerId(){
		return contentContainerId;
	}
	
	public String getContentContainerTitle(){
		return contentContainerTitle;
	}
	
	public String getContentTitle(){
		return contentTitle;
	}
	
	public String getContentLink(){
		return contentLink;
	}
	
	public String getContentContainterLink(){
		return contentContainterLink;
	}
	
	public String getContentCreatorId(){
		return contentCreatorId;
	}
	
	public String getContentCreateTs(){
		return contentCreateTs;
	}
	
	public void setScope(String scope){
		this.scope = scope;
	}
	
	public void setCommunity(String community){
		this.community = community;
	}
	
	public void setContentCorrelationId(String ContentCorrelationId){
		this.contentCorrelationId = ContentCorrelationId;
	}
	
	public void setContentContainerId(String contentContainerId){
		this.contentContainerId = contentContainerId;
	}
	
	public void setContentContainerTitle(String contentContainerTitle){
		this.contentContainerTitle = contentContainerTitle;
	}
	
	public void setContentTitle(String contentTitle){
		this.contentTitle = contentTitle;
	}
	
	public void setContentLink(String contentLink){
		this.contentLink = contentLink;
	}
	
	public void setContentContainterLink(String contentContainterLink){
		this.contentContainterLink = contentContainterLink;
	}
	
	public void setContentCreatorId(String contentCreatorId){
		this.contentCreatorId = contentCreatorId;
	}
	
	public void setContentCreateTs(String contentCreateTs){
		this.contentCreateTs = contentCreateTs;
	}
	
	@Override
	public String toString() {
		return "EventTrackerConsumerPostRequest [contentId=" + contentId 
				+ ", itemType=" + itemType 
				+ ", source= " + source 
				+ ", forwardToEventTrackerConsumerBVTServlet= " + forwardToEventTrackerConsumerBVTServlet 
				+ ", scope= " + scope 
				+ ", community= " + community 
				+ ", contentCorrelationId= " + contentCorrelationId 
				+ ", contentContainerId= " + contentContainerId 
				+ ", contentContainerTitle= " + contentContainerTitle 
				+ ", contentTitle= " + contentTitle 
				+ ", contentLink= " + contentLink 
				+ ", contentContainterLink= " +contentContainterLink 
				+ ", contentCreatorId= " + contentCreatorId 
				+ ", contentCreateTs= " + contentCreateTs + "]";
	}

	
	public Map<String,String> getParams(){
		Map<String, String> paramsMap = new HashMap<String, String>();
		addToParams(paramsMap);
		return paramsMap;
	}
	
	protected void addToParams(Map<String,String> paramsMap){
		if (contentId != null){
			paramsMap.put("contentId", contentId);
		}
		if (itemType != null){
			paramsMap.put("itemType", itemType.toString());
		}
		if (source != null){
			paramsMap.put("source", source.toString());
		}
		if (forwardToEventTrackerConsumerBVTServlet){
			paramsMap.put("forwardToEventTrackerConsumerBVTServlet", "true");
		}
		if (scope != null){
			paramsMap.put("scope", scope);
		}
		if (community != null){
			paramsMap.put("community", community);
		}
		if (contentCorrelationId != null){
			paramsMap.put("contentCorrelationId", contentCorrelationId);
		}
		if (contentContainerId != null){
			paramsMap.put("contentContainerId", contentContainerId);
		}
		if (contentContainerTitle != null){
			paramsMap.put("contentContainerTitle", contentContainerTitle);
		}
		if (contentTitle != null){
			paramsMap.put("contentTitle", contentTitle);
		}
		if (contentLink != null){
			paramsMap.put("contentLink", contentLink);
		}
		if (contentContainterLink != null){
			paramsMap.put("contentContainterLink", contentContainterLink);
		}
		if (contentCreatorId != null){
			paramsMap.put("contentCreatorId", contentCreatorId);
		}
		if (contentCreateTs != null){
			paramsMap.put("contentCreateTs", contentCreateTs);
		}
	}
	
}