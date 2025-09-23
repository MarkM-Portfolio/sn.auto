package com.ibm.conn.auto.appobjects.base;

import org.testng.Assert;

import com.ibm.conn.auto.appobjects.BaseStateObject;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;


public class BaseBlogComment implements BaseStateObject{

	private String content;
	
	
	public static class Builder {
		private String content;
		
	
		public Builder (String content){
			this.content = content;
		}

		public BaseBlogComment build() {
			return new BaseBlogComment(this);
		}
	}
	
	private BaseBlogComment(Builder builder) {
			this.setContent(builder.content);	
	 }
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void create(BlogsUI ui){
		ui.createBlogComment(this);
	}
	
	/**
	 * Edit a comment to a blog
	 * @param BlogsUI 
	 * @param commentNumber to edit
	*/
	public void edit(BlogsUI ui,int commentNumber){
		ui.editBlogComment(this,commentNumber);
	}
	
	/**
	 * Add a comment to a blog post in an existing blog via API 
	 * @param APIBlogHandler apiOwner, BlogPost blogPost
	 * @return BlogComment API object
	*/
	public BlogComment createAPI(APIBlogsHandler APIOwner, BlogPost blogPost) {
		BlogComment blogComment = null;
		blogComment = APIOwner.createBlogComment(this.getContent(), blogPost);
		
		Assert.assertTrue(blogComment != null, "Failed to add blog comment using API.");
		return blogComment;
	}
}
