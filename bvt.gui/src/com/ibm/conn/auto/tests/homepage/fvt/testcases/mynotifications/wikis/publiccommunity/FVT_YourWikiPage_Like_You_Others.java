package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.wikis.publiccommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016 		                             */
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

public class FVT_YourWikiPage_Like_You_Others extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3, wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8, wikisAPIUser9, wikisAPIUser10, wikisAPIUser11, wikisAPIUser12;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(12);
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

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
		wikisAPIUser4 = initialiseAPIWikisHandlerUser(testUser4);
		wikisAPIUser5 = initialiseAPIWikisHandlerUser(testUser5);
		wikisAPIUser6 = initialiseAPIWikisHandlerUser(testUser6);
		wikisAPIUser7 = initialiseAPIWikisHandlerUser(testUser7);
		wikisAPIUser8 = initialiseAPIWikisHandlerUser(testUser8);
		wikisAPIUser9 = initialiseAPIWikisHandlerUser(testUser9);
		wikisAPIUser10 = initialiseAPIWikisHandlerUser(testUser10);
		wikisAPIUser11 = initialiseAPIWikisHandlerUser(testUser11);
		wikisAPIUser12 = initialiseAPIWikisHandlerUser(testUser12);

		// User 1 create a public community and add the Wikis widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
				
		// Retrieve the community wiki for use in the test
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_YourWikiPage_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a Wiki and add a wiki page</B></li>
	*<li><B>Step: User 1 like the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 like the wiki page User 1 added</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 add another wiki page</B></li>
	*<li><B>Step: User 3 to User 12 like the wiki page</B></li>
	*<li><B>Step: User 1 like the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your wiki page {wikiPageName} in the {wikiName} wiki."</B></li>
	*<li><B>Verify: Verify the event shows "You and 10 others liked your wiki page {wikiPageName} in the {wikiName} wiki."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4F1C0C24EA52538185257DEA004B6EF1">TTT - MY NOTIFICATIONS - WIKIS - 00041 - LIKE YOUR OWN WIKI PAGE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourWikiPage_Like_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 create the first wiki page
		BaseWikiPage baseWikiPage1 = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage wikiPage1 = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage1, testUser1, wikisAPIUser1);
		
		// User 1 like the first wiki page
		CommunityWikiEvents.likeWikiPage(wikiPage1, testUser1, wikisAPIUser1);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeWikiPage1Event = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_You(ui, baseWikiPage1.getName(), baseCommunity.getName());

		// Verify the like wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPage1Event, baseWikiPage1.getDescription()}, TEST_FILTERS, false);

		// User 2 like the first wiki page
		CommunityWikiEvents.likeWikiPage(wikiPage1, testUser2, wikisAPIUser2);

		// Create the news story to be verified
		likeWikiPage1Event = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_UserAndYou(ui, baseWikiPage1.getName(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify the like wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPage1Event, baseWikiPage1.getDescription()}, TEST_FILTERS, true);

		// User 1 create a second wiki page
		BaseWikiPage baseWikiPage2 = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage wikiPage2 = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage2, testUser1, wikisAPIUser1);

		// Users 3 to 12 and User 1 like the second wiki page
		User[] usersToLike = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser1 };
		APIWikisHandler[] apiUsersToLike = { wikisAPIUser3, wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8, wikisAPIUser9, wikisAPIUser10, wikisAPIUser11, wikisAPIUser12, wikisAPIUser1 };
		
		for(int i = 0; i < usersToLike.length; i++){
			CommunityWikiEvents.likeWikiPage(wikiPage2, usersToLike[i], apiUsersToLike[i]);
		}
		
		// Create the news story to be verified
		String likeWikiPage2Event = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_YouAndMany(ui, "10", baseWikiPage2.getName(), baseCommunity.getName());

		// Verify the like wiki page events for liking both the first and second wiki page are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPage2Event, baseWikiPage2.getDescription(), likeWikiPage1Event, baseWikiPage1.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();		
	}	
}