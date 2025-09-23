package com.ibm.conn.auto.util.baseBuilder;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseBlog objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class BlogBaseBuilder {
	
	/**
	 * 
	 * @param blogName - The name of the blog.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseBlog - A BaseBlog object
	 */
	public static BaseBlog buildBaseBlog(String blogName) {
		
		BaseBlog baseBlog = new BaseBlog.Builder(blogName, blogName + Helper.genStrongRand())
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.build();
		
		return baseBlog;
	}
	
	/**
	 * 
	 * @param blogName - The name of the blog.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param customTag - The custom tag for this blog.  Ideally this should be created with
	 * a unique identifier, e.g. Helper.genStrongRand();
	 * @return baseBlog - A BaseBlog object
	 */
	public static BaseBlog buildBaseBlogWithCustomTag(String blogName, String customTag){

		BaseBlog baseBlog = buildBaseBlog(blogName);
		baseBlog.setTags(customTag);
		
		return baseBlog;		
	}

	
	/**
	 * This method can be used for blog entries, or Ideation Blog ideas
	 * 
	 * @param postName - The name of the blog entry / idea .  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseBlogPost - A BaseBlogPost object
	 */
	public static BaseBlogPost buildBaseBlogPost(String postName) {

		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder(postName)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.content(Data.getData().commonDescription + Helper.genStrongRand())
													.allowComments(true)
													.numDaysCommentsAllowed(5)
													.complete(true)
													.build();
		
		return baseBlogPost;		
	}
	
	/**
	 * This method can be used for blog entries, or Ideation Blog ideas
	 * 
	 * @param postName - The name of the blog entry / idea .  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param customTag - The custom tag for this blog post.  Ideally this should be created with a unique identifier, e.g. Helper.genStrongRand();
	 * @return baseBlogPost - A BaseBlogPost object
	 */
	public static BaseBlogPost buildBaseBlogPostWithCustomTag(String postName, String customTag) {

		BaseBlogPost baseBlogPost = buildBaseBlogPost(postName);
		baseBlogPost.setTags(customTag);
		
		return baseBlogPost;		
	}
	
	/**
	 * Creates the BaseBlogComment instance of a blog comment
	 * 
	 * @return - The BaseBlogComment instance of the blog comment
	 */
	public static BaseBlogComment buildBaseBlogComment() {
		
		BaseBlogComment baseBlogComment = new BaseBlogComment.Builder(Data.getData().commonComment + Helper.genStrongRand())
	     													 .build();
		return baseBlogComment;
	}
}