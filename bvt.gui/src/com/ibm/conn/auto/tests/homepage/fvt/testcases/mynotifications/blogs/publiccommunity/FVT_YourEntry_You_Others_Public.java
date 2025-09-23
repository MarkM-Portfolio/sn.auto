package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs.publiccommunity;

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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
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
public class FVT_YourEntry_You_Others_Public extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(8);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);
		communityBlogsAPIUser4 = initialiseAPICommunityBlogsHandlerUser(testUser4);
		communityBlogsAPIUser5 = initialiseAPICommunityBlogsHandlerUser(testUser5);
		communityBlogsAPIUser6 = initialiseAPICommunityBlogsHandlerUser(testUser6);
		communityBlogsAPIUser7 = initialiseAPICommunityBlogsHandlerUser(testUser7);
		communityBlogsAPIUser8 = initialiseAPICommunityBlogsHandlerUser(testUser8);
		
		// User 1 will now create a public community with the blogs widget added to it
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourEntry_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 1 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 2 to User 4 like the entry</B></li>
	*<li><B>Step: User 1 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others liked your {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC12334ED9D3598985257DE8004064F9">TTT - MY NOTIFICATIONS - BLOGS - 00031 - LIKE YOUR OWN ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Like_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community blog and will like / recommend the entry
		BaseBlogPost baseBlogPost1 = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogEntry1 = CommunityBlogEvents.createBlogPostAndLikeBlogPost(publicCommunity, baseBlogPost1, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_You(ui, baseBlogPost1.getTitle(), baseCommunity.getName());
		
		// Verify that the like blog entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost1.getContent()}, TEST_FILTERS, false);
		
		// User 2 will now like the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogEntry1, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_UserAndYou(ui, baseBlogPost1.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost1.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now create another blog entry in the community blog
		BaseBlogPost baseBlogPost2 = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogEntry2 = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost2, publicCommunity);
		
		// Users 2, 3 and 4 will now like / recommend the second community blog entry with User 1 then liking / recommending the entry
		CommunityBlogEvents.likeOrVote(communityBlogEntry2, testUser2, communityBlogsAPIUser2);
		CommunityBlogEvents.likeOrVote(communityBlogEntry2, testUser3, communityBlogsAPIUser3);
		CommunityBlogEvents.likeOrVote(communityBlogEntry2, testUser4, communityBlogsAPIUser4);
		CommunityBlogEvents.likeOrVote(communityBlogEntry2, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		likeBlogEntryEvent = CommunityBlogNewsStories.getLikeYourEntryNewsStory_YouAndMany(ui, "3", baseBlogPost2.getTitle(), baseCommunity.getName());
		
		// Verify that the like blog entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeBlogEntryEvent, baseBlogPost2.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_YourEntry_LikeComment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 1 comment on the entry</B></li>
	*<li><B>Step: User 1 like their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 add another comment</B></li>
	*<li><B>Step: User 4 to User 8 like the comment</B></li>
	*<li><B>Step: User 1 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your comment on {entryName} blog entry in the {blogName} Blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 5 others liked your comment on {entryName} blog entry in the {blogName} blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/003CCC020CE6E6A585257DE8004064FA">TTT - MY NOTIFICATIONS - BLOGS - 00041 - LIKE YOUR OWN COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEntry_LikeComment_You_Others_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the community blog 
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		// User 1 will now comment on the entry and like / recommend the comment
		BaseBlogComment baseBlogComment1 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment1 = CommunityBlogEvents.createCommentAndLikeComment(communityBlogEntry, baseBlogComment1, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_You(ui, baseBlogPost.getTitle());
		
		// Verify that the like comment event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), baseBlogComment1.getContent()}, TEST_FILTERS, false);
		
		// User 2 will now like User 1's comment posted to the community blog entry
		CommunityBlogEvents.likeComment(blogComment1, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), testUser2.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), baseBlogComment1.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now add another comment to the community blog entry
		BaseBlogComment baseBlogComment2 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment2 = CommunityBlogEvents.createComment(communityBlogEntry, baseBlogComment2, testUser1, communityBlogsAPIUser1);
		
		// Users 4 through to 8 will now like / recommend the second comment posted by User 1 with User 1 then liking / recommending the comment
		CommunityBlogEvents.likeComment(blogComment2, testUser4, communityBlogsAPIUser4);
		CommunityBlogEvents.likeComment(blogComment2, testUser5, communityBlogsAPIUser5);
		CommunityBlogEvents.likeComment(blogComment2, testUser6, communityBlogsAPIUser6);
		CommunityBlogEvents.likeComment(blogComment2, testUser7, communityBlogsAPIUser7);
		CommunityBlogEvents.likeComment(blogComment2, testUser8, communityBlogsAPIUser8);
		CommunityBlogEvents.likeComment(blogComment2, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourEntryNewsStory_YouAndMany(ui, "5", baseBlogPost.getTitle());
		
		// Verify that the like comment event and both of User 1's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), baseBlogComment1.getContent(), baseBlogComment2.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}	
}