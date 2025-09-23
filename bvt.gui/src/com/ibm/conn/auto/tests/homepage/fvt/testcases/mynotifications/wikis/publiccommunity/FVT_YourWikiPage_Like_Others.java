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

public class FVT_YourWikiPage_Like_Others extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3, wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8;
	private Wiki communityWiki;

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
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
		wikisAPIUser4 = initialiseAPIWikisHandlerUser(testUser4);
		wikisAPIUser5 = initialiseAPIWikisHandlerUser(testUser5);
		wikisAPIUser6 = initialiseAPIWikisHandlerUser(testUser6);
		wikisAPIUser7 = initialiseAPIWikisHandlerUser(testUser7);
		wikisAPIUser8 = initialiseAPIWikisHandlerUser(testUser8);
		
		// User 1 create a public community and add the Wikis widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki for use in the test
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove the community created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_YourWikiPage_Like_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a Wiki and add a wiki page</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 like the wiki page User 1 added</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 8 like the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your wiki page {wikiPageName} in the {wikiName} wiki."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your wiki page {wikiPageName} in the {wikiName} wiki."</B></li>
	*<li><B>Verify: Verify the event shows "{user 8} and 6 others liked your wiki page {wikiPageName} in the {wikiName} wiki."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1F8A24A86F21FA4A85257DEA004B6EF0">TTT - MY NOTIFICATIONS - WIKIS - 00040 - LIKE A WIKI PAGE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourWikiPage_Like_Rollup() {

		String testName = ui.startTest();

		// User 1 create a wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage wikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// User 2 like the wiki page
		CommunityWikiEvents.likeWikiPage(wikiPage, testUser2, wikisAPIUser2);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_User(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the like wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);

		// User 3 like the wiki page
		CommunityWikiEvents.likeWikiPage(wikiPage, testUser3, wikisAPIUser3);
		
		// Create the news story to be verified
		likeWikiPageEvent = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseWikiPage.getName(), baseCommunity.getName());

		// Verify the like wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		// Users 4 to 8 like the wiki page
		User[] usersLikingWikiPage = { testUser4, testUser5, testUser6, testUser7, testUser8 };
		APIWikisHandler[] apiUsersLikingWikiPage = { wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8 };
		
		for(int index = 0; index < usersLikingWikiPage.length; index ++){
			CommunityWikiEvents.likeWikiPage(wikiPage, usersLikingWikiPage[index], apiUsersLikingWikiPage[index]);
		}
		
		// Create the news story to be verified
		likeWikiPageEvent = CommunityWikiNewsStories.getLikeYourWikiPageNewsStory_UserAndMany(ui, testUser8.getDisplayName(), "6", baseWikiPage.getName(), baseCommunity.getName());

		// Verify the like wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);

		ui.endTest();		
	}
}