package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.blogs;

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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:		Anthony Cox
 * Date:		27th August 2015
 */

public class FVT_Saved_Public_StandaloneBlogs extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };
	
	private APIBlogsHandler blogsAPIUser1, blogsAPIUser2, blogsAPIUser3, blogsAPIUser4;
	private APIProfilesHandler profilesAPIUser3, profilesAPIUser4;
	private BaseBlog baseBlogUser1, baseBlogUser3;
	private Blog publicBlogUser1, publicBlogUser3;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
			
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		blogsAPIUser2 = initialiseAPIBlogsHandlerUser(testUser2);
		blogsAPIUser3 = initialiseAPIBlogsHandlerUser(testUser3);
		blogsAPIUser4 = initialiseAPIBlogsHandlerUser(testUser4);
		
		// User 1 will now create a standalone public blog with User 2 added as a follower
		baseBlogUser1 = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		publicBlogUser1 = BlogEvents.createBlogWithFollower(testUser1, blogsAPIUser1, baseBlogUser1, testUser2, blogsAPIUser2);
		
		// User 3 will now create a standalone public blog with User 4 added as a follower
		baseBlogUser3 = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		publicBlogUser3 = BlogEvents.createBlogWithFollower(testUser3, blogsAPIUser3, baseBlogUser3, testUser4, blogsAPIUser4);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the blog used in the test
		blogsAPIUser1.deleteBlog(publicBlogUser1);
		blogsAPIUser3.deleteBlog(publicBlogUser3);
	}
	
	/**
	* test_PublicStandaloneBlog_MarkingBlogStoriesAsSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Blogs</B></li>
	*<li><B>2. User 1 start a public blog - User 2 follow this blog</B></li>
	*<li><B>3. User 1 add an entry to the blog</B></li>
	*<li><B>4. User 1 go to Homepage / Discover / Blogs</B></li>
	*<li><B>5. User 1 go to the story of the blog creation and mark it as Saved</B></li>
	*<li><B>Verify: User 1 - Once the 'Save this' link has been clicked it should be blackened and unclickable</B></li>
	*<li><b>6: User 1 go to Homepage / Saved / Blogs</b></li>
	*<li><B>Verify: User 1 should see the blog creation in Homepage / Saved / Blogs</B></li>
	*<li><b>7: User 2 go to Homepage / I'm Following / Blogs</b></li>
	*<li><b>8: User 2 go to the story of the entry being added to the blog and mark it as Saved</b></li>
	*<li><b>Verify: User 2 - Once the 'Save this' link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 2 go to Homepage / Updates / Saved / Blogs</b></li>
	*<li><b>Verify: User 2 should see the blog entry added in Homepage / Saved / Blogs</b></li>
	*<li><b>10: User 2 comment on the blog entry</b></li>
	*<li><b>11: User 1 go to Homepage / My Notifications / For Me / Blogs</b></li>
	*<li><b>12: User 1 go to the story of the blog entry being commented on and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the 'Save this' link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>13: User 1 go to Homepage / Saved / Blogs</b></li>
	*<li><b>Verify: User 1 should see the blog entry commented on added in Homepage / Saved / Blogs</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/E0EB63CC73159A8C85257936004E413F">TTT: AS - Saved - 00011 - Marking Blog Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicStandaloneBlog_MarkingBlogStoriesAsSaved() {
		
		String testName = ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved and verified
		String createBlogEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlogUser1.getName(), testUser1.getDisplayName());
		
		// Save the create blog event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createBlogEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 1 will now create a blog entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, publicBlogUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the news story to be saved and verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlogUser1.getName(), testUser1.getDisplayName());
		
		// Save the create blog entry event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the blog entry
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser2, blogsAPIUser2, blogEntry, user2Comment);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news story to be saved and verified
		String commentOnEntryEvent = BlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseBlogUser1.getName(), testUser2.getDisplayName());
		
		// Save the comment on blog entry event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnEntryEvent);
				
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
				
		// Verify that the comment on blog entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);		
		
		ui.endTest();
	}
	
	/**
	* test_PublicStandaloneBlog_RemovingBlogStoriesFromSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. Go to Homepage / Saved / Blogs</B></li>
	*<li><B>3. Click the "X" in the story related to the Blog that had been created by User 1</B></li>
	*<li><B>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</B></li>
	*<li><B>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</B></LI>
	*<li><b>5: Go to Homepage / Discover / Blogs</b></li>
	*<LI><B>Verify: User 1 - The story in Homepage / Discover / Blogs should have a clickable "Save this" link again</B></LI>
	*<li><b>6: User 2 log into Connections</b></li>
	*<li><b>7: Go to Homepage / Saved / Blogs</b></li>
	*<LI><B>8: Click the "X" in the story related to the Blog entry that was created by User 1</B></LI>
	*<li><B>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</B></li>
	*<li><B>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</B></LI>
	*<li><b>9: Go to Homepage / I'm Following / Blogs</b></li>
	*<li><b>User 2 - The story in Homepage / I'm Following / Blogs should have a clickable "Save this" link again</b></li>
	*<li><b>10: User 1 go to Homepage / Saved  / Blogs</b></li>
	*<li><b>11: Click the "X" in the story related to the Blog entry commented on that was created by User 2</b></li>
	*<li><B>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</B></li>
	*<li><B>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</B></LI>
	*<li><b>12: Go to Homepage / My Notifications / For Me / Blogs</b></li>
	*<li><b>Verify: User 1 - The story in Homepage / My Notifications / For Me / Blogs should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/4F9A826D2A2B96DD8525793B0035E3B5">TTT: AS - Saved - 00012 - Removing Blog Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicStandaloneBlog_RemovingBlogStoriesFromSaved() {
		
		/**
		 * In order to prevent the saving and removal of identical stories in the AS - this test case will use User 3 (as User 1) and User 4 (as User 2)
		 */
		String testName = ui.startTest();
		
		// Create the news story to be saved and verified
		String createBlogEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlogUser3.getName(), testUser3.getDisplayName());
		
		// User 1 will now save the create blog event news story using the API
		ProfileEvents.saveNewsStory(profilesAPIUser3, createBlogEvent, true);
		
		// User 1 will now create a blog entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = BlogEvents.createBlogPost(testUser3, blogsAPIUser3, baseBlogPost, publicBlogUser3);
		
		// Create the news story to be saved and verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlogUser3.getName(), testUser3.getDisplayName());
				
		// User 2 will now save the create blog entry event news story using the API
		ProfileEvents.saveNewsStory(profilesAPIUser4, createEntryEvent, false);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser3, false);
		
		// Verify that the create blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		// Remove the create blog event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createBlogEvent);
		
		// Verify that the create blog entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, false);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the create blog event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createBlogEvent);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser4, true);
		
		// Verify that the create blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		// Remove the create blog entry event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createEntryEvent);
		
		// Verify that the create blog entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the create blog entry event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createEntryEvent);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the blog entry
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser4, blogsAPIUser4, blogEntry, user2Comment);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser3, true);
				
		// Create the news story to be saved and verified
		String commentOnEntryEvent = BlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseBlogUser3.getName(), testUser4.getDisplayName());
				
		// Save the comment on blog entry event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnEntryEvent);
						
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
						
		// Verify that the comment on blog entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// Remove the create blog entry event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, commentOnEntryEvent);
		
		// Verify that the comment on blog entry event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment.getContent()}, TEST_FILTERS, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the comment on blog entry event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnEntryEvent);
				
		ui.endTest();
	}
	
	/**
	* test_PublicStandaloneBlog_SaveABlogCommentedOnStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Blogs</B></li>
	*<li><B>2. Start a blog</B></li>
	*<li><B>3. Add an entry to the blog</B></li>
	*<li><b>4: Comment on the entry</b></li>
	*<li><b>5: Go to Homepage / Discover / Blogs</b></li>
	*<li><b>6: Go to the story of the blog entry commented on and 'Save This'</b></li>
	*<LI><B>7: Go to Homepage / Saved / Blogs</B></LI>
	*<li><B>Verify: Verify that the story of the blog commented on and the comment is in the saved view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/0BD5F3324DC98E15852579B2003EBC4A">TTT: AS - Saved - 00040 - Save a blog commented on story</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicStandaloneBlog_SaveABlogCommentedOnStory() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, publicBlogUser1);
		
		// User 1 will now post a comment to the blog entry
		BaseBlogComment user1Comment = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser1, blogsAPIUser1, blogEntry, user1Comment);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String commentOnEntryEvent = BlogNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseBlogPost.getTitle(), baseBlogUser1.getName());
		
		// Save the comment on blog entry event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		commentOnEntryEvent = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlogUser1.getName(), testUser1.getDisplayName());
								
		// Verify that the comment on blog entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user1Comment.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	* test_PublicStandaloneBlog_BlogEntryAlreadySavedRecommendedAfterCommentedOn()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Blogs</B></li>
	*<li><B>2. Start a blog</B></li>
	*<li><B>3. Add an entry to the blog</B></li>
	*<li><b>4: Comment on the entry</b></li>
	*<li><b>5: Go to Homepage / Discover / Blogs</b></li>
	*<li><b>6: Go to the story of the blog entry commented on and 'Save This'</b></li>
	*<LI><B>7: Go to Homepage / Saved / Blogs</B></LI>
	*<li><B>Verify: Verify that the story of the blog commented on and the comment is in the saved view</B></li>
	*<LI><B>8: Go to the blog entry and recommend the blog entry</B></LI>
	*<LI><B>9: Go to Homepage / Discover / Blogs</B></LI>
	*<LI><B>Verify: Verify that the story in discover is of the blog entry recommended</B></LI>
	*<LI><B>10 Go to Homepage / Saved / Blogs</B></LI>
	*<LI><B>Verify: Verify that the story of the blog commented on and the comment is still in the saved view</B></LI>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/087AA3C0AEFC1C0B852579B2003EC4DC">TTT: AS - Saved - 00041 - Blog entry already saved recommended after commented on</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicStandaloneBlog_BlogEntryAlreadySavedRecommendedAfterCommentedOn() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, publicBlogUser1);
		
		// User 1 will now post a comment to the blog entry
		BaseBlogComment user1Comment = BlogBaseBuilder.buildBaseBlogComment();
		BlogEvents.createBlogPostComment(testUser1, blogsAPIUser1, blogEntry, user1Comment);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String discoverCommentOnEntryEvent = BlogNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseBlogPost.getTitle(), baseBlogUser1.getName());
		
		// Save the comment on blog entry event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, discoverCommentOnEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedCommentOnEntryEvent = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlogUser1.getName(), testUser1.getDisplayName());
								
		// Verify that the comment on blog entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedCommentOnEntryEvent, baseBlogPost.getContent(), user1Comment.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now like / recommend the blog entry
		BlogEvents.likeBlogPost(testUser1, blogsAPIUser1, blogEntry);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String discoverLikeEntryEvent = BlogNewsStories.getLikeYourEntryNewsStory_You(ui, baseBlogPost.getTitle(), baseBlogUser1.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the like blog entry event and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{discoverLikeEntryEvent, baseBlogPost.getContent(), user1Comment.getContent()}, filter, true);
			
			// Verify that the comment on blog entry event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{discoverCommentOnEntryEvent}, null, false);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedLikeEntryEvent = BlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlogUser1.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on blog entry event and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{savedCommentOnEntryEvent, baseBlogPost.getContent(), user1Comment.getContent()}, filter, true);
			
			// Verify that the like blog entry event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{savedLikeEntryEvent}, null, false);
		}
		ui.endTest();
	}
}