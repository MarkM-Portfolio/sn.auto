package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs.privatecommunity;

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
/* Copyright IBM Corp. 2015, 2017	                                 */
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

public class FVT_YourIdea_Others_Private extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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
		
		// User 1 will now create a restricted community with Users 2 through to User 6 added as members and with the Ideation Blogs widget added
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_YourIdea_LikeComment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Private Community adding the Ideation Blogs widget and user 2 to user 6 as members</B></li>
	*<li><B>Step: User 1 create an idea in the Ideation Blog</B></li>
	*<li><B>Step: User 1 comment on the idea</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 6 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your comment on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your comment on your {ideaName} idea in the{ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 6} and 4 others liked your comment {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0ED216283C3DA58485257DE8004A1C85">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00080 - LIKE ON COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourIdea_LikeComment_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(restrictedCommunity, baseIdea, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now post a comment to the idea
		BaseBlogComment user1Comment = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment user1BlogComment = CommunityBlogEvents.createComment(idea, user1Comment, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now like / recommend the comment posted by User 1
		CommunityBlogEvents.likeComment(user1BlogComment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_User(ui, baseIdea.getTitle(), testUser2.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseIdea.getContent(), user1Comment.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the comment posted by User 1
		CommunityBlogEvents.likeComment(user1BlogComment, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseIdea.getTitle(), testUser3.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseIdea.getContent(), user1Comment.getContent()}, TEST_FILTERS, true);
		
		// Users 4 through to User 6 will now like / recommend the comment posted by User 1
		CommunityBlogEvents.likeComment(user1BlogComment, testUser4, communityBlogsAPIUser4);
		CommunityBlogEvents.likeComment(user1BlogComment, testUser5, communityBlogsAPIUser5);
		CommunityBlogEvents.likeComment(user1BlogComment, testUser6, communityBlogsAPIUser6);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_UserAndMany(ui, testUser6.getDisplayName(), "4", baseIdea.getTitle());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseIdea.getContent(), user1Comment.getContent()}, TEST_FILTERS, true);
						
		ui.endTest();
	}
}