package com.ibm.lconn.automation.framework.services.search.data;

import org.apache.abdera.model.ExtensibleElement;

public class FeedWithCacheControlHeader{
	private String cacheControl;
	private ExtensibleElement feed;

	public FeedWithCacheControlHeader(String cacheControl, ExtensibleElement feed){
		this.cacheControl = cacheControl;
		this.feed = feed;
	}
	
	public String getCacheControl() {
		return cacheControl;
	}

	public ExtensibleElement getFeed() {
		return feed;
	}
}
