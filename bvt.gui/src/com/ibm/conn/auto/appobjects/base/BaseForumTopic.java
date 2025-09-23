package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;

import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;


public class BaseForumTopic {

	public enum forumTopicField{
		TITLE,
		TAGS,
		DESCRIPTION;	
	}
	
	private String title;
	private boolean markAsQuestion;
	private String tags;
	private String description;
	private BaseCommunity partOfCommunity;
	private Set<forumTopicField> edit_track = new HashSet<forumTopicField>();
	private Forum parentForum_API;
	private String attachment; 
	private String UUID;
	private String tinyEditorFunctionalitytoRun;
	private boolean tinyEditorEnabled;
	
	
	public static class Builder {
		private String title;
		private boolean markAsQuestion = false;
		private String tags;
		private String description;
		private BaseCommunity partOfCommunity;
		private Forum parentForum_API;
		private String attachment = "";
		private String tinyEditorFunctionalitytoRun;
		private boolean tinyEditorEnabled;
		
		public Builder tinyEditorFunctionalitytoRun(String functionality)
		{
			this.tinyEditorFunctionalitytoRun= functionality;
			return this;
		}
		
		public Builder tinyEditorEnabled(boolean tinyEditorEnabled)
		{
			this.tinyEditorEnabled= tinyEditorEnabled;
			return this;
		}
		public Builder(String title){
			this.title = title;
		}
	
		public Builder markAsQuestion(boolean markAsQuestion){
			this.markAsQuestion = markAsQuestion;
			return this;
		}
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}

		public Builder description(String description){
			this.description = description;
			return this;
		}

		public Builder partOfCommunity(BaseCommunity partOfCommunity){
			this.partOfCommunity = partOfCommunity;
			return this;
		}
		public Builder parentForum(Forum parent){
			
			this.parentForum_API = parent;
			return this;
			
		}
		
		public Builder addAttachment(String fileName){
			this.attachment = fileName;
			return this;
		}
		
		public BaseForumTopic build() {
			return new BaseForumTopic(this);
		}
		

		
		
	}
	
	private BaseForumTopic(Builder b) {
		this.setTitle(b.title);
		this.setMarkAsQuestion(b.markAsQuestion);
		this.setTags(b.tags);		
		this.setDescription(b.description);
		this.setPartOfCommunity(b.partOfCommunity);
		this.setParentForum_API(b.parentForum_API);
		this.setAttachment(b.attachment);
		this.setTinyEditorFunctionalitytoRun(b.tinyEditorFunctionalitytoRun);
		this.setTinyEditorEnabled(b.tinyEditorEnabled);
	}
	
	public void setTinyEditorEnabled(boolean tinyEditorEnabled) {
		this.tinyEditorEnabled = tinyEditorEnabled;
		
	}

	public void setTinyEditorFunctionalitytoRun(String functionality)
	{
		this.tinyEditorFunctionalitytoRun = functionality;
	}
	
	public void setTitle(String title) {
		this.title = title;	
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setMarkAsQuestion(boolean markAsQuestion) {
		this.markAsQuestion = markAsQuestion;	
	}
	
	public boolean getMarkAsQuestion() {
		return markAsQuestion;
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
	
	public String getTinyEditorFunctionalitytoRun() {
		return tinyEditorFunctionalitytoRun;
	}
	
	public boolean getTinyEditorEnabled() {
		return tinyEditorEnabled;
	}
	
	public void setPartOfCommunity(BaseCommunity partOfCommunity) {
		this.partOfCommunity = partOfCommunity;	
	}
	
	public void setParentForum_API(Forum parent){
		
		this.parentForum_API = parent;
	}	
	
	public Forum getParentForum_API(){
		
		return parentForum_API;
	}
	
	public BaseCommunity getPartOfCommunity() {
		return partOfCommunity;
	}
	
	public String getAttachment(){
		return attachment;
	}
	
	public void setAttachment(String fileName) {
		this.attachment = fileName;
	}
	
	public String getUUID() {
		return UUID;
	}
	
	public void setUUID(String UUID) {
		this.UUID = UUID;
	}
	
	public void create(ForumsUI ui) {
		ui.createTopic(this);
	}
	public ForumTopic createAPI(APIForumsHandler forumOwner){
		
		ForumTopic topic = forumOwner.createForumTopic(this);
		return topic;
		
	}

	public Set<forumTopicField> getEdits(){
		return edit_track;
	}
	
	public String verifyTinyEditor(ForumsUI ui)  {
		String TEText = ui.verifyTinyEditor(this);
		return TEText;
	}


}
