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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016, 2017	                             */
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

public class FVT_YourIdea_You_Others extends SetUpMethodsFVT {

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
	* test_YourIdea_Vote_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Ideation Blogs widget</B></li>
	*<li><B>Step: User 1 create an idea in the Blog</B></li>
	*<li><B>Step: User 1 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 create another idea in the Blog</B></li>
	*<li><B>Step: User 2 to User 4 vote for the idea</B></li>
	*<li><B>Step: User 1 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you voted for your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others voted for your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C3880E59C36363F785257DE8004A1C86">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00071 - VOTE YOUR OWN IDEA ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_YourIdea_Vote_You_Others_Rollup(){

		String testName = ui.startTest();

		// User 1 post an idea and like the idea
		BaseBlogPost baseBlogPost1 = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea1 = CommunityBlogEvents.createIdeaAndVoteForIdea(publicCommunity, baseBlogPost1, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);

		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_You(ui, baseBlogPost1.getTitle(), baseCommunity.getName());

		// Verify the like idea event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEvent, baseBlogPost1.getContent()}, TEST_FILTERS, false);
		
		// User 2 like the first idea
		CommunityBlogEvents.likeOrVote(idea1, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		likeEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_UserAndYou(ui, baseBlogPost1.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the like idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEvent, baseBlogPost1.getContent()}, TEST_FILTERS, true);

		// User 1 post another idea
		BaseBlogPost baseBlogPost2 = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea2 = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost2, testUser1, communityBlogsAPIUser1);
		
		// Users 2 to 4 will now like the second idea and then User 1 will like the idea
		User[] testUsers = { testUser2, testUser3, testUser4, testUser1 };
		APICommunityBlogsHandler[] users = { communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser1 };
		
		for(int index = 0; index < testUsers.length; index ++){
			CommunityBlogEvents.likeOrVote(idea2, testUsers[index], users[index]);
		}
		// Create the news story to be verified
		likeEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_YouAndMany(ui, "3", baseBlogPost2.getTitle(), baseCommunity.getName());

		// Verify the like idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEvent, baseBlogPost2.getContent()}, TEST_FILTERS, true);

		ui.endTest();
	}

	/**
	* test_YourIdea_LikeComment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Private Community adding the Ideation Blogs widget and user 2 to user 6 as members</B></li>
	*<li><B>Step: User 1 create an idea in the Ideation Blog</B></li>
	*<li><B>Step: User 1 comment on the idea</B></li>
	*<li><B>Step: User 1 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 8 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your comment on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 7 others liked your comment {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/79F3763614BBB1D985257DE8004A1C87">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00081 - LIKE YOUR OWN COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_YourIdea_LikeComment_You_Others_Rollup(){
		
		String testName = ui.startTest();

		// User 1 create an idea
		BaseBlogPost baseBlogPost1 = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost1, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now add a comment and like the comment
		BaseBlogComment baseBlogComment1 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment1 = CommunityBlogEvents.createCommentAndLikeComment(idea, baseBlogComment1, testUser1, communityBlogsAPIUser1);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_You(ui, baseBlogPost1.getTitle());

		// Verify the like comment is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost1.getContent(), baseBlogComment1.getContent()}, TEST_FILTERS, false);
		
		// User 2 like the comment
		CommunityBlogEvents.likeComment(blogComment1, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_UserAndYou(ui, baseBlogPost1.getTitle(), testUser2.getDisplayName());

		// Verify the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost1.getContent(), baseBlogComment1.getContent()}, TEST_FILTERS, true);
		
		// Users 3 to 8 will now like the comment
		User[] testUsers = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8 };
		APICommunityBlogsHandler[] users = { communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8 };
		
		for(int index = 0; index < testUsers.length; index ++){
			CommunityBlogEvents.likeComment(blogComment1, testUsers[index], users[index]);
		}
		// Create the news story to be verified
		likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnYourIdeaNewsStory_YouAndMany(ui, "7", baseBlogPost1.getTitle());

		// Verify the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost1.getContent(), baseBlogComment1.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
}