package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.blogs.comments;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_StandAloneBlog_Comments extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };
	
	private APIBlogsHandler blogsAPIUser1, blogsAPIUser2;
	private BaseBlog baseBlog;
	private BaseBlogComment baseBlogCommentUser1;
	private BaseBlogPost baseBlogPost;
	private Blog standaloneBlog;
	private BlogComment blogCommentUser1;
	private BlogPost blogPost;
	private boolean likeTestCompleted;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		blogsAPIUser2 = initialiseAPIBlogsHandlerUser(testUser2);
		
		// User 1 will now create a blog
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlog);
		
		// Set all other relevant global test components to null
		blogPost = null;
		blogCommentUser1 = null;
		likeTestCompleted = false;
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Remove the blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> test_CreateBlogComment_Standalone()</li>
	 * <li><B>Step:</B> testUser1 log in to Blogs</li>
	 * <li><B>Step:</B> testUser1 open a standalone blog that has an existing blog entry</li>
	 * <li><B>Step:</B> testUser1 create a comment on a blog entry</li>
	 * <li><B>Step:</B> testUser2 log in to Homepage</li>
	 * <li><B>Step:</B> testUser2 go to Home \ Updates \ Discover \ All & Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.comment.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/57F10280AC5B948E852578760079E74F">TTT - DISC - BLOGS - 00040 - BLOG.COMMENT.CREATED - STANDALONE BLOG</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_CreateBlogComment_Standalone(){

		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createBlogCommentIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);

		// Create the news story to be verified
		String commentOnEntryEvent = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		// Verify that the comment on activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_LikeComment_Standalone()</li>
	 * <li><B>Step:</B> testUser1 log in to Blogs</li>
	 * <li><B>Step:</B> testUser1 open a standalone blog that has an existing blog entry and add a comment</li>
	 * <li><B>Step:</B> testUser1 recommend this comment</li>
	 * <li><B>Step:</B> testUser2 log in to Homepage</li>
	 * <li><B>Step:</B> testUser2 go to Home \ Updates \ Discover \ All & Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.comment.recommended is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/73B183B0692CB337852578760079E75B">TTT - DISC - BLOGS - 00070 - BLOG.COMMENT.RECOMMENDED - STANDALONE BLOG</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeComment_Standalone(){

		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createBlogCommentIfRequired();
		
		// User 1 will now like / recommend the comment on the blog entry
		BlogEvents.likeBlogPostComment(testUser1, blogsAPIUser1, blogCommentUser1);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);

		// Create the news story to be verified
		String likeCommentOnEntryEvent = BlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		// Verify that the comment recommended on activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent()}, TEST_FILTERS, true);
		
		likeTestCompleted = true;
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_deleteCommentToEntry_StandaloneBlog()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Blogs</B></li>
	*<li><B>Step: testUser 1 creates a new blog</B></li>
	*<li><B>Step: testUser 1 create a new blog entry</B></li>
	*<li><B>Step: testUser 1 add a comment to the blog</B></li>	
	*<li><B>Step: testUser 2 login to Homepage</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \Discover Filter by Blogs</B></li>
	*<li><B>Step: testUser 2 click on entry link to open the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 add a comment to the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: testUser 2 filter by Blogs - validation point #1</B></li>	
	*<li><B>Step: testUser 2 now deletes their comment on the blog entry - validation point #2</B></li>
	*<li><B>Step: testUser 1 log in to Blogs again</B></li>
	*<li><B>Step: testUser 1 deletes the blog comment that they created</B></li>
	*<li><B>Step: testUser 2 logs in to Home and goes to  \ Activity Stream \ Discover - validation point #3</B></li>
	*<li><B>Verify: #1 - testUser 2 sees two comments on the blog entry, one created by themselves, the other created by User 1</B></li>
	*<li><B>Verify: #2 - testUser 2 sees one comment on the blog entry, the comment author is testUser 1</B></li>
	*<li><B>Verify: #3 - testUser 2 sees no comments on the blog entry</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/21A3FDA2735E41CE85257B4E00528C6D">TTT - Disc - Blogs - 00100 - blog.comment.deleted - standalone blog</a></li>	
	* @author Patrick Doherty
	*/	
	@Test(groups = {"fvtonprem"}, priority = 3)
	public void test_deleteCommentToEntry_StandaloneBlog(){
		
		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createBlogCommentIfRequired();
		
		// User 2 will now comment on User 1's blog entry
		BaseBlogComment baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogCommentUser2 = BlogEvents.createBlogPostComment(testUser2, blogsAPIUser2, blogPost, baseBlogCommentUser2);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String twoCommentsOnEntryEvent = BlogNewsStories.getCommentOnTheEntryNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on the blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent()}, TEST_FILTERS, true);

		// User 2 deletes their comment
		blogsAPIUser2.deleteComment(blogCommentUser2);
		
		// Create the news story to be verified
		String commentOnEntryEvent;
		if(likeTestCompleted) {
			// Since the "like comment" test was the last completed test case - the "like comment" event will now appear in the UI
			commentOnEntryEvent = BlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		} else {
			// Since the "like comment" test did not complete successfully - the "comment on entry" event will now appear in the UI
			commentOnEntryEvent = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		}
		
		for(String filter : TEST_FILTERS) {
			// Verify that the correct comment event and User 1's comment are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent()}, filter, true);
			
			// Verify that the two comments on entry event, the correct comment event and User 2's comment are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnEntryEvent, commentOnEntryEvent, baseBlogCommentUser2.getContent()}, null, false);
		}
		// User 1 deletes their comment
		blogsAPIUser1.deleteComment(blogCommentUser1);
		
		// Create the news story to be verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create blog entry event is displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the two comments on entry event, the correct comment event, like comment on entry event, and User 1 and User 2's comments are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnEntryEvent, commentOnEntryEvent, baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent()}, null, false);
		}
		ui.endTest();
	}
	
	private void createBlogPostIfRequired() {
		if(blogPost == null) {
			// User 1 will now create an entry in the blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogPost = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
		}
	}
	
	private void createBlogCommentIfRequired() {
		if(blogCommentUser1 == null) {
			// User 1 will now post a comment to the blog entry
			baseBlogCommentUser1 = BlogBaseBuilder.buildBaseBlogComment();
			blogCommentUser1 = BlogEvents.createBlogPostComment(testUser1, blogsAPIUser1, blogPost, baseBlogCommentUser1);
		}
	}
}