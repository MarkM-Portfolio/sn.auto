package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_StandaloneBlogEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };

	private APIBlogsHandler blogsAPIUser1, blogsAPIUser2;
	private BaseBlog baseBlog;
	private Blog standaloneBlog;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		blogsAPIUser2 = initialiseAPIBlogsHandlerUser(testUser2);
		
		// User 1 will now create a standalone blog
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlog);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
	}

	/**
	* test_Entry_FinalTwoComments() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a Blog</B></li>
	*<li><B>Step: User 1 add an entry</B></li>
	*<li><B>Step: User 2 add 3 comments on the entry</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last comment</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify the second last comment only is shown inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7866BC26D04FC5885257E2F0036A45C">TTT - INLINE COMMENTS - 00020 - BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups = {"fvtonprem"})
	public void test_Entry_FinalTwoComments(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
		
		// User 2 will now add their first comment to the blog entry
		BaseBlogComment user2Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser2, blogsAPIUser2, blogEntry, user2Comment1);
		
		// User 2 will now add their second comment to the blog entry
		BaseBlogComment user2Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser2, blogsAPIUser2, blogEntry, user2Comment2);
		
		// User 2 will now add their third comment to the blog entry
		BaseBlogComment user2Comment3 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment user2BlogComment3 = BlogEvents.createBlogPostComment(testUser2, blogsAPIUser2, blogEntry, user2Comment3);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = BlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second and third comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent(), user2Comment3.getContent()}, filter, true);
			
			// Verify that the first comment posted by User 2 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent()}, null, false);
		}
		
		// User 2 will now delete their third comment from the community entry
		BlogEvents.deleteComment(user2BlogComment3, testUser2, blogsAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent()}, filter, true);
			
			// Verify that the first and third comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent(), user2Comment3.getContent()}, null, false);
		}		
		ui.endTest();
	}	
}