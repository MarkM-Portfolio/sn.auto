package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.wikis.modcommunity;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016	                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification/Mentions Badges] FVT UI Automation for Story 146307
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/147242
 * @author Patrick Doherty
 */

public class FVT_WikiPageLikes_NotificationBadging extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
		
		// User 1 will now create a moderated community with Users 2 and 3 added as members and with the Wiki widget added
		User[] membersList = { testUser2, testUser3 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1, membersList);
	
		// Retrieve the community wiki for use in the test
		communityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_WikiPage_LikeEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a moderate community you own adding the Wikis widget</B></li>
	*<li><B>Step: User 1 create a new wiki page</B></li>
	*<li><B>Step: User 2 like the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 3 like the wiki page</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>Step: User 3 unlike the wiki page</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EA0F97226BFBA6CE85257E18003BBE9E">TTT - NOTIFICATIONS BADGING - WIKIS - 00011 - WIKI PAGE LIKE EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_WikiPage_LikeEvents() {

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view to reset the My Notifications counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now like the wiki page
		CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser2, wikisAPIUser2);
		
		// Refresh the page by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 3 will now like the wiki page
		String user3LikeWikiPageURL = CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser3, wikisAPIUser3);
		
		// Refresh the page by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 3 will now unlike the wiki page again
		CommunityWikiEvents.unlikeWikiPage(communityWikiPage, user3LikeWikiPageURL, testUser3, wikisAPIUser3);
		
		// Refresh the page by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}