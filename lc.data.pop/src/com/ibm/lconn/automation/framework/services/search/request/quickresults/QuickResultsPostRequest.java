package com.ibm.lconn.automation.framework.services.search.request.quickresults;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ibm.lconn.automation.framework.services.common.URLConstants;

public abstract class QuickResultsPostRequest {

	private static final String DATE_FORMAT = "E MMM dd yyyy H:mm:ss zz";
	
	protected String contentId;
	protected String source;
	protected String contentTitle;
	protected String contentCreatorId;
	protected String contentCreateTs;
	protected String contentLink;
	protected String itemType;

	public QuickResultsPostRequest(String contentId, String source,
			String contentTitle, String contentCreatorId,
			String contentCreateTs, String contentLink, String itemType) {
		super();
		this.contentId = contentId;
		this.source = source;
		this.contentTitle = contentTitle;
		this.contentCreatorId = contentCreatorId;
		this.contentCreateTs = contentCreateTs != null ? contentCreateTs : dateFormatted();
		this.contentLink = contentLink != null? contentLink : URLConstants.SERVER_URL + getContentPath();
		this.itemType = itemType;
	}
	
	protected abstract String getContentPath();

	protected static String dateFormatted() {
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				DATE_FORMAT, Locale.getDefault());
		return dateFormat.format(date);

	}

	public String getContentId() {
		return contentId;
	}

	public String getSource() {
		return source;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public String getContentCreatorId() {
		return contentCreatorId;
	}

	public String getContentCreateTs() {
		return contentCreateTs;
	}

	public String getContentLink() {
		return contentLink;
	}

	public String getItemType() {
		return itemType;
	}

	@Override
	public String toString() {
		return "QuickResultsPostRequest [contentId=" + contentId + ", source="
				+ source + ", contentTitle=" + contentTitle
				+ ", contentCreatorId=" + contentCreatorId
				+ ", contentCreateTs=" + contentCreateTs + ", contentLink="
				+ contentLink + ", itemType=" + itemType + "]";
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
		if (source != null){
			paramsMap.put("source", source);
		}
		if (contentTitle != null){
			paramsMap.put("contentTitle", contentTitle);
		}
		if (contentCreatorId != null){
			paramsMap.put("contentCreatorId", contentCreatorId);
		}
		if (contentCreateTs != null){
			paramsMap.put("contentCreateTs", contentCreateTs);
		}
		if (contentLink != null){
			paramsMap.put("contentLink", contentLink);
		}
		if (itemType != null){
			paramsMap.put("itemType", itemType);
		}
	}
	
}