package com.ibm.conn.auto.appobjects.base;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;

import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

public class BaseBlogPost {
	
	public enum blogEntryFields {
		TITLE,
		TAGS,
		CONTENT;	
	}
	
	private String title;
	private String content;
	private String tags;
	private boolean allowComments;
	private int numDaysCommentsAllowed;
	private BaseBlog blogParent;
	private boolean advanced;
	private boolean announcement;
	private boolean delay;
	private boolean preExistingBlog;
	private boolean enableEmoticons;
	private boolean complete;
	private String useUploadedImage;
	private Set<blogEntryFields> edit_track = new HashSet<blogEntryFields>();

	public static class Builder {
		
		private BaseBlog blogParent = null;
		private String title;
		private String content="";
		private String tags ="";
		private boolean allowComments = true;
		private int numDaysCommentsAllowed = 4;
		private boolean advanced = false;
		private boolean announcement = false;
		private boolean delay = false;
		private boolean preExistingBlog = false;
		private boolean enableEmoticons = false;
		private boolean complete = true;
		private String useUploadedImage="";
		
		
		public Builder(String title){
			this.title = title;
		}
		
		public Builder preExistingBlog (boolean preExistingBlog) {
			this.preExistingBlog=preExistingBlog;
			return this;
		}
		
		public Builder blogParent(BaseBlog blogParent){
			this.blogParent=blogParent;
			return this;
		}
		
		public Builder content(String content){
			this.content = content;
			return this;
		}
		
		
		public Builder tags(String tags){
			this.tags = tags;
			return this;
		}
		
		public Builder allowComments(boolean allowComments){
			this.allowComments = allowComments;
			return this;
		}
		
		public Builder numDaysCommentsAllowed(int numDaysCommentsAllowed){
			this.numDaysCommentsAllowed = numDaysCommentsAllowed;
			return this;
		}
		
		public Builder advanced(boolean advanced){
			this.advanced = advanced;
			return this;
		}		
		
		public Builder announcement(boolean announcement){
			this.announcement = announcement;
			return this;
		}	
		
		public Builder delay(boolean delay){
			this.delay = delay;
			return this;
		}
		
		public Builder useUploadedImage(String imageFileName){
			this.useUploadedImage = imageFileName;
			return this;
		}
		
		public Builder complete(boolean complete){
			this.complete = complete;
			return this;
			
		}
		
		public Builder enableEmoticons(boolean enableEmoticons){
			this.enableEmoticons = enableEmoticons;
			return this;
		}
		
		public BaseBlogPost build() {
			return new BaseBlogPost(this);
		}
		
		
	}

    private BaseBlogPost(Builder builder) {
		this.setTitle(builder.title);
		this.setContent(builder.content);			
		this.setTags(builder.tags);
		this.setAllowComments(builder.allowComments);
		this.setNumDaysCommentsAllowed(builder.numDaysCommentsAllowed);
		this.setBlogParent(builder.blogParent);
		this.setAdvanced(builder.advanced);
		this.setAnnouncement(builder.announcement);
		this.setDelay(builder.delay);
		this.setPreExistingBlog(builder.preExistingBlog);
		this.setEnableEmoticons(builder.enableEmoticons);
		this.setImage(builder.useUploadedImage);
		this.setComplete(builder.complete);

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;		
		this.edit_track.add(blogEntryFields.TITLE);
	}
	
	public boolean getPreExistingBlog(){
		return preExistingBlog;
	}
	
	public void setPreExistingBlog(boolean preExistingBlog) {
		this.preExistingBlog = preExistingBlog;
	}		
	
	public BaseBlog getBlogParent() {
		return blogParent;
	}

	public void setBlogParent(BaseBlog blogParent) {
		this.blogParent = blogParent;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.edit_track.add(blogEntryFields.CONTENT);
	}
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
		this.edit_track.add(blogEntryFields.TAGS);
	}
	
	public boolean getAllowComments() {
		return allowComments;
	}

	public void setAllowComments(boolean allowComments) {
		this.allowComments = allowComments;
	}
	
	public int getNumDaysCommentsAllowed() {
		return numDaysCommentsAllowed;
	}

	public void setNumDaysCommentsAllowed(int numDaysCommentsAllowed) {
		this.numDaysCommentsAllowed = numDaysCommentsAllowed;
	}
	
	public boolean getAdvanced(){
		return advanced;
	}
	
	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}
	
	public boolean getAnnouncement(){
		return announcement;
	}
	
	public void setAnnouncement(boolean announcement) {
		this.announcement = announcement;
	}
	
	public boolean getDelay(){
		return delay;
	}
	
	public void setDelay(boolean delay) {
		this.delay = delay;
	}
	
	public boolean getEnableEmoticons(){
		return enableEmoticons;
	}
	
	public void setEnableEmoticons(boolean enableEmoticons) {
		this.enableEmoticons = enableEmoticons;
	}
	
	public String getImage(){
		return useUploadedImage;
	}
	
	public void setImage(String imageFileName) {
		this.useUploadedImage = imageFileName;
	}

	public boolean getComplete(){
		return complete;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	// this method is called many times in other place, so not change the method signature to avoid massive changes
	public void create(BlogsUI ui) {
		ui.createBlogEntry(this, false);
		
		//after creating blog entry reset edits
		this.edit_track.clear();		
	}
	
	public void create(BlogsUI ui, boolean useUnifyInsertImage) {
		ui.createBlogEntry(this, useUnifyInsertImage);
		
		//after creating blog entry reset edits
		this.edit_track.clear();		
	}

	public void newBlogEntry(BlogsUI ui, boolean unifyInsertImageDlg) {
		ui.newBlogEntry(this, unifyInsertImageDlg);
		
		//after creating blog entry reset edits
		this.edit_track.clear();		
	}

	public void postBlogEntry(BlogsUI ui) {
		ui.postBlogEntry(this);
		
		//after creating blog entry reset edits
		this.edit_track.clear();		
	}

	/**
	 * Add a blog post to an existing blog via API 
	 * @param APIBlogHandler apiOwner
	 * @return BlogPost API object
	 */
	
	public BlogPost createAPI(APIBlogsHandler user1, Blog blog) {
		BlogPost blogPost = null;
		blogPost = user1.createBlogEntry(this, blog);
		Assert.assertTrue(blogPost != null, "Failed to add blog post using API.");				
	
		//after creating blog entry reset edits
		this.edit_track.clear();	
		
	return blogPost;
		
	}
	
	public boolean deleteBlogPostAPI(APIBlogsHandler blogsAPI, BlogPost blogPost){
		return blogsAPI.deleteBlogPost(blogPost);
	}
}
