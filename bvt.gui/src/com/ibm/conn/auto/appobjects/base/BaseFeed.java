package com.ibm.conn.auto.appobjects.base;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;

public class BaseFeed implements BaseStateObject {

	public enum communityFields {
		TITLE,
		TAGS,
		DESCRIPTION;	
	}
	
	private String title;
	private String feed;
	private String tags;
	private String description;
	
	public static class Builder {
		private String title;
		private String feed;
		private String tags;
		private String description;

		
		public Builder(String title, String feed){
			this.title = title;
			this.feed = feed;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}		

		public BaseFeed build() {
			return new BaseFeed(this);
		}

	}
	
	private BaseFeed(Builder b) {
		this.setTitle(b.title);
		this.setFeed(b.feed);
		this.setTags(b.tags);
		this.setDescription(b.description);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}
	
	public String getFeed() {
		return feed;
	}

	public void setFeed(String feed) {
		this.feed = feed;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FeedLink createAPI(APICommunitiesHandler user, Community community){	
		FeedLink feedLink = user.createFeed(community, this);		
		Assert.assertTrue(feedLink != null, "Failed to add feed using API.");	
		return feedLink;
		
	}



}