package com.ibm.conn.auto.appobjects.base;

import org.testng.Assert;

import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;



public class BaseForum {
	
	public enum forumFields {
		NAME,
		TAGS,
		DESCRIPTION;	
	}
	
	private String name;
	private String tags;
	private String description;
	private boolean newTopicNotify;
	
	public static class Builder {
		private String name;
		private String tags;
		private String description;
		private boolean newTopicNotify = true;
		
		public Builder(String name){
			this.name = name;
		}
	
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}

		public Builder description(String description){
			this.description = description;
			return this;
		}
		
		public Builder newTopicNotify(boolean newTopicNotify){
			this.newTopicNotify = newTopicNotify;
			return this;
		}
		
		public BaseForum build() {
			return new BaseForum(this);
		}
	}
	
	private BaseForum(Builder b) {
		this.setName(b.name);
		this.setTags(b.tags);		
		this.setDescription(b.description);
		this.setNewTopicNotify(b.newTopicNotify);
	}
	
	
	public void setName(String name) {
		this.name = name;	
	}
	
	public String getName() {
		return name;
	}
	
	public void setTags(String tags) {
		this.tags = tags;	
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setDescription(String description) {
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setNewTopicNotify(boolean newTopicNotify) {
		this.newTopicNotify = newTopicNotify;	
	}
	
	public boolean getNewTopicNotify() {
		return newTopicNotify;
	}
	
	public void create(ForumsUI ui) {
		ui.create(this);
	}
	
	public void delete(ForumsUI ui) {
		ui.delete(this);
	}
	
	public Forum createAPI(APIForumsHandler apiOwner) {
		Forum forum = apiOwner.createForum(this);
		Assert.assertTrue(forum != null, "Failed to add forum using API.");		
		return forum;
	}
}
