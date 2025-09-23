package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs.moderatedcommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
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

public class FVT_YourIdea_Others_Mod extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8, communityBlogsAPIUser9, communityBlogsAPIUser10, communityBlogsAPIUser11, communityBlogsAPIUser12, communityBlogsAPIUser13, communityBlogsAPIUser14;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community with the Ideation Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_YourIdea_Vote_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Moderated Community adding the Ideation Blogs widget</B></li>
	*<li><B>Step: User 1 create an idea in the Blog</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 14 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} voted for your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} voted for your {ideaName} idea in the{ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "{user 14} and 12 others voted for your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6B16CC6552C72CE985257DE8004A1C84">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00070 - VOTE ON IDEA ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourIdea_Vote_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(moderatedCommunity, baseIdea, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now vote for the idea
		CommunityBlogEvents.likeOrVote(idea, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_User(ui, baseIdea.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeIdeaEvent, baseIdea.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now vote for the idea
		CommunityBlogEvents.likeOrVote(idea, testUser3, communityBlogsAPIUser3);
		
		// Create the news story to be verified
		likeIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseIdea.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the like idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeIdeaEvent, baseIdea.getContent()}, TEST_FILTERS, true);
		
		// Users 4 through to User 14 will now like / recommend the idea
		User[] usersVotingForIdea = { testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14 };
		APICommunityBlogsHandler[] apiUsersVotingForIdea = { communityBlogsAPIUser4, communityBlogsAPIUser5, communityBlogsAPIUser6, communityBlogsAPIUser7, communityBlogsAPIUser8, communityBlogsAPIUser9, communityBlogsAPIUser10, communityBlogsAPIUser11, communityBlogsAPIUser12, communityBlogsAPIUser13, communityBlogsAPIUser14 };
		int index = 0;
		while(index < usersVotingForIdea.length) {
			// Have the specified user vote for the idea
			CommunityBlogEvents.likeOrVote(idea, usersVotingForIdea[index], apiUsersVotingForIdea[index]);
			index ++;
		}
		// Create the news story to be verified
		likeIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_UserAndMany(ui, testUser14.getDisplayName(), "12", baseIdea.getTitle(), baseCommunity.getName());
		
		// Verify that the like idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeIdeaEvent, baseIdea.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}