package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs.modcommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourEntry_You_Others_Mod extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(6);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);
		communityBlogsAPIUser4 = initialiseAPICommunityBlogsHandlerUser(testUser4);
		communityBlogsAPIUser5 = initialiseAPICommunityBlogsHandlerUser(testUser5);
		communityBlogsAPIUser6 = initialiseAPICommunityBlogsHandlerUser(testUser6);
		
		// User 1 will now create a moderated community with the blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_YourEntry_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Moderated Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 1 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 6 comment on the entry</B></li>
	*<li><B>Step: User 1 comment on the entry again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 5 others commented on your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9077AA7705A6A1FC85257DE8003BD7F9">TTT - MY NOTIFICATIONS - BLOGS - 00011 - COMMENT ON YOUR OWN ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Comment_You_Others_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 1 will now post their first comment to the entry
		BaseBlogComment user1Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user1Comment1, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());
		
		// Verify that the comment on entry event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user1Comment1.getContent()}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the entry
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry event, User 2's comment and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user1Comment1.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the entry
		BaseBlogComment user3Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user3Comment, testUser3, communityBlogsAPIUser3);
		
		// User 4 will now post a comment to the entry
		BaseBlogComment user4Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user4Comment, testUser4, communityBlogsAPIUser4);
		
		// User 5 will now post a comment to the entry
		BaseBlogComment user5Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user5Comment, testUser5, communityBlogsAPIUser5);
		
		// User 6 will now post a comment to the entry
		BaseBlogComment user6Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user6Comment, testUser6, communityBlogsAPIUser6);
		
		// User 1 will now post their second comment to the entry
		BaseBlogComment user1Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user1Comment2, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, "5", baseBlogPost.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event, User 6's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user6Comment.getContent(), user1Comment2.getContent()}, filter, true);
			
			// Verify that User 1's first comment and all comments posted by User 2 through to User 5 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1.getContent(), user2Comment.getContent(), user3Comment.getContent(), user4Comment.getContent(), user5Comment.getContent()}, null, false);
		}
		ui.endTest();
	}	
}