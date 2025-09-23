package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs.publiccommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
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

public class FVT_YourEntry_Others_Public extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8, communityBlogsAPIUser9, communityBlogsAPIUser10, communityBlogsAPIUser11, communityBlogsAPIUser12, communityBlogsAPIUser13, communityBlogsAPIUser14;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(14);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		testUser10 = listOfStandardUsers.get(9);
		testUser11 = listOfStandardUsers.get(10);
		testUser12 = listOfStandardUsers.get(11);
		testUser13 = listOfStandardUsers.get(12);
		testUser14 = listOfStandardUsers.get(13);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);
		communityBlogsAPIUser4 = initialiseAPICommunityBlogsHandlerUser(testUser4);
		communityBlogsAPIUser5 = initialiseAPICommunityBlogsHandlerUser(testUser5);
		communityBlogsAPIUser6 = initialiseAPICommunityBlogsHandlerUser(testUser6);
		communityBlogsAPIUser7 = initialiseAPICommunityBlogsHandlerUser(testUser7);
		communityBlogsAPIUser8 = initialiseAPICommunityBlogsHandlerUser(testUser8);
		communityBlogsAPIUser9 = initialiseAPICommunityBlogsHandlerUser(testUser9);
		communityBlogsAPIUser10 = initialiseAPICommunityBlogsHandlerUser(testUser10);
		communityBlogsAPIUser11 = initialiseAPICommunityBlogsHandlerUser(testUser11);
		communityBlogsAPIUser12 = initialiseAPICommunityBlogsHandlerUser(testUser12);
		communityBlogsAPIUser13 = initialiseAPICommunityBlogsHandlerUser(testUser13);
		communityBlogsAPIUser14 = initialiseAPICommunityBlogsHandlerUser(testUser14);
		
		// User 1 will now create a public community with the Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourEntry_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 5 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {entryName} blog entry in the{blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 5} and 3 others commented on your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C4BF93C72F6BA8FC85257DE8003BCD81">TTT - MY NOTIFICATIONS - BLOGS - 00010 - COMMENT ON ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Comment_Rollup(){

		String testName = ui.startTest();
		
		// User 1 post a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		// User 2 will now post a comment to the entry
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the entry
		BaseBlogComment user3Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user3Comment, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the comment on entry event, User 2's comment and User 3's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment.getContent(), user3Comment.getContent()}, TEST_FILTERS, true);
		
		// User 4 will now post a comment to the entry
		BaseBlogComment user4Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user4Comment, testUser4, communityBlogsAPIUser4);
		
		// User 5 will now post a comment to the entry
		BaseBlogComment user5Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user5Comment, testUser5, communityBlogsAPIUser5);
		
		// Create the news story to be verified
		commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, testUser5.getDisplayName(), "3", baseBlogPost.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event, User 4's comment and User 5's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user4Comment.getContent(), user5Comment.getContent()}, filter, true);
			
			// Verify that the comments posted by User 2 and User 3 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment.getContent(), user3Comment.getContent()}, null, false);
		}
		ui.endTest();
	}	

	
	/**
	* test_YourEntry_Like_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 14 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your {entryName} blog entry in the{blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 14} and 12 others liked your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/493B9B4C56E1F5AE85257DE8003EE939">TTT - MY NOTIFICATIONS - BLOGS - 00030 - LIKE ON ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Like_Rollup(){

		String testName = ui.startTest();
		
		// User 1 post a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		// User 2 will now like / recommend the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogPost, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogPost, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		User[] listOfUsersToLikeEntry = { testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14 };
		APICommunityBlogsHandler[] listOfAPIsToLikeEntry = { communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8, communityBlogsAPIUser9, communityBlogsAPIUser10, communityBlogsAPIUser11, communityBlogsAPIUser12, communityBlogsAPIUser13, communityBlogsAPIUser14 };
		int index = 0;
		while(index < listOfUsersToLikeEntry.length) {
			// Like / recommend the community blog entry as the current user
			CommunityBlogEvents.likeOrVote(communityBlogPost, listOfUsersToLikeEntry[index], listOfAPIsToLikeEntry[index]);
			index ++;
		}
		// Create the news story to be verified
		likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_UserAndMany(ui, testUser14.getDisplayName(), "12", baseBlogPost.getTitle(), baseCommunity.getName());
		
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_YourEntry_LikeComment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 1 comment on the entry</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 6 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your comment on {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your comment on {entryName} blog entry in the{blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 6} and 4 others liked your comment on {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AECFA54873F1C0B385257DE8003EE93A">TTT - MY NOTIFICATIONS - BLOGS - 00040 - LIKE ON COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_LikeComment_Rollup() {

		String testName = ui.startTest();
		
		// User 1 post a blog entry and post a comment to the blog entry
		BaseBlogComment user1BaseComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment user1BlogComment = CommunityBlogEvents.createBlogPostAndAddComment(publicCommunity, baseBlogPost, user1BaseComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now like / recommend User 1's comment
		CommunityBlogEvents.likeComment(user1BlogComment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), testUser2.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), user1BaseComment.getContent()}, TEST_FILTERS, true);
				
		// User 3 will now like / recommend User 1's comment
		CommunityBlogEvents.likeComment(user1BlogComment, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseBlogPost.getTitle(), testUser3.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), user1BaseComment.getContent()}, TEST_FILTERS, true);
		
		// User 4 will now like / recommend User 1's comment
		CommunityBlogEvents.likeComment(user1BlogComment, testUser4, communityBlogsAPIUser4);
		
		// User 5 will now like / recommend User 1's comment
		CommunityBlogEvents.likeComment(user1BlogComment, testUser5, communityBlogsAPIUser5);
		
		// User 6 will now like / recommend User 1's comment
		CommunityBlogEvents.likeComment(user1BlogComment, testUser6, communityBlogsAPIUser6);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_UserAndMany(ui, testUser6.getDisplayName(), "4", baseBlogPost.getTitle());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), user1BaseComment.getContent()}, TEST_FILTERS, true);
						
		ui.endTest();
	}
}