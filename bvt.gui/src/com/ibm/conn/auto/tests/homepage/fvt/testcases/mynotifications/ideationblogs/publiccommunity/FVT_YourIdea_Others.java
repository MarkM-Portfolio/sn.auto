package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs.publiccommunity;

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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016, 2017                              */
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

public class FVT_YourIdea_Others extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
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

		// User 1 create a public community and add the ideation blog widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_YourIdea_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Ideation Blogs widget</B></li>
	*<li><B>Step: User 1 create an idea in the Blog</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 7 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {ideaName} idea in the{ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 7} and 5 others commented on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/750BEFD4C10F3F4885257DE8004A1C80">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00050 - COMMENT ON IDEA ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourIdea_Comment_Rollup() {

		String testName = ui.startTest();

		// User 1 post an idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);

		// User 2 add a comment
		BaseBlogComment baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(idea, baseBlogCommentUser2, testUser2, communityBlogsAPIUser2);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), baseBlogCommentUser2.getContent()}, TEST_FILTERS, true);

		// User 3 add a comment
		BaseBlogComment baseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(idea, baseBlogCommentUser3, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), baseBlogCommentUser2.getContent(), baseBlogCommentUser3.getContent()}, TEST_FILTERS, true);
		
		// Users 4 to 7 add comments to the idea
		BaseBlogComment[] baseBlogComments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		User[] usersPostingComments = { testUser4, testUser5, testUser6, testUser7 };
		APICommunityBlogsHandler[] apiUsersPostingComments = { communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7 };
		
		for(int index = 0; index < baseBlogComments.length; index ++){
			CommunityBlogEvents.createComment(idea, baseBlogComments[index], usersPostingComments[index], apiUsersPostingComments[index]);
		}
		// Create the news story to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_UserAndMany(ui, testUser7.getDisplayName(), "5", baseBlogPost.getTitle(), baseCommunity.getName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and comments posted by User 6 and User 7 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), baseBlogComments[2].getContent(), baseBlogComments[3].getContent()}, filter, true);
			
			// Verify that none of the other comments are displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogComments[0].getContent(), baseBlogComments[1].getContent(), baseBlogCommentUser2.getContent(), baseBlogCommentUser3.getContent()}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourIdea_CommentUpdate_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Ideation Blogs widget</B></li>
	*<li><B>Step: User 1 create an idea in the Blog</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the idea and edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the idea and edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 8 comment on the idea and edit the comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} updated a comment on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} updated a comment on your {ideaName} idea in the{ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 8} and 6 others updated a comment on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/530A42878287922885257DE8004A1C82">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00060 - UPDATE COMMENT ON IDEA ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourIdea_CommentUpdate_Rollup(){

		String testName = ui.startTest();

		// User 1 post an idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);

		// User 2 add a comment and edit the comment
		BaseBlogComment baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(idea, baseBlogCommentUser2, updatedBaseBlogCommentUser2, testUser2, communityBlogsAPIUser2);

		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);

		// Create the news stories to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		String updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and User 2's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), updatedBaseBlogCommentUser2.getContent()}, filter, true);
			
			// Verify that the update comment event and User 2's original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogCommentUser2.getContent()}, null, false);
		}
		// User 3 add a comment and edit the comment
		BaseBlogComment baseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(idea, baseBlogCommentUser3, updatedBaseBlogCommentUser3, testUser3, communityBlogsAPIUser3);

		// Create the news stories to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event, User 2's updated comment and User 3's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), updatedBaseBlogCommentUser2.getContent(), updatedBaseBlogCommentUser3.getContent()}, filter, true);
			
			// Verify that the comment event and User 3's original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogCommentUser3.getContent()}, null, false);
		}

		// Users 4 to 8 add comments to the idea and edit those comments
		BaseBlogComment[] baseBlogComments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		BaseBlogComment[] updatedBaseBlogComments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		User[] usersPostingComments = { testUser4, testUser5, testUser6, testUser7, testUser8 };
		APICommunityBlogsHandler[] apiUsersPostingComments = { communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8 };
		
		for(int index = 0; index < baseBlogComments.length; index ++){
			CommunityBlogEvents.createCommentAndEditComment(idea, baseBlogComments[index], updatedBaseBlogComments[index], usersPostingComments[index], apiUsersPostingComments[index]);
		}
		// Create the news stories to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_UserAndMany(ui, testUser8.getDisplayName(), "6", baseBlogPost.getTitle(), baseCommunity.getName());
		updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser8.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event, User 7's updated comment and User 8's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), updatedBaseBlogComments[3].getContent(), updatedBaseBlogComments[4].getContent()}, filter, true);
			
			// Verify that the update event, User 7 and User 8's original comments, Users 4 to 6's original and updated comments and Users 2 and 3's updated comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogComments[0].getContent(), baseBlogComments[1].getContent(), baseBlogComments[2].getContent(), baseBlogComments[3].getContent(), baseBlogComments[4].getContent(), updatedBaseBlogComments[0].getContent()}, null, false);
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedBaseBlogComments[1].getContent(), updatedBaseBlogComments[2].getContent(), updatedBaseBlogCommentUser2.getContent(), updatedBaseBlogCommentUser3.getContent()}, null, false);
		}
		ui.endTest();	
	}
}