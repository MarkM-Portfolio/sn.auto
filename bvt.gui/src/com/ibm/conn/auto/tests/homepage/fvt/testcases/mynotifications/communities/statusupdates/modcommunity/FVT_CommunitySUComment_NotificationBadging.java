package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.modcommunity;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016  		                             */
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

public class FVT_CommunitySUComment_NotificationBadging extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		profilesAPIUser4 = new APIProfilesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		// User 1 will now create a moderated community with Users 2, 3 and 4 added as members
		User[] usersToAddAsMembers = { testUser2, testUser3, testUser4 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, usersToAddAsMembers);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_SUComment_LikeEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a moderate community you own adding User 2, 3, 4 as members</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 comment on the update</B></li>
	*<li><B>Step: User 1 and User 3 like the comment</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 2 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 3 remove their like</B></li>
	*<li><B>Step: User 2 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>Step: User 4 like the comment</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B1FA0D502D99EA4285257D8600457635">TTT - NOTIFICATIONS BADGING - COMMUNITIES - 00014 - COMMUNITY STATUS UPDATE LIKE COMMENT EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_SUComment_LikeEvents() {

		ui.startTest();
		
		// Log in as User 2 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Navigate to the I'm Following view to reset the My Notifications badges
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user2CommentId = CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment);
		
		// User 1 will now like / recommend User 2's comment
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser1, user2CommentId);
		
		// User 3 will now like / recommend User 2's comment
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser3, user2CommentId);
		
		// Refresh the My Notifications and Notification Center badge values by re-loading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have the expected value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 3 will now unlike User 2's comment again
		CommunityEvents.unlikeStatusUpdateComment(profilesAPIUser3, user2CommentId);
		
		// Refresh the My Notifications and Notification Center badge values by re-loading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have the expected value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 4 will now like / recommend User 2's comment
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser4, user2CommentId);
		
		// Refresh the My Notifications and Notification Center badge values by re-loading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have the expected value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}