package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.modcommunity;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [FVT Automation for Story 154601] Scynchronize realtime updates with badging
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/156301
 * @author Patrick Doherty
 */

public class FVT_CommunitySUComment_BadgeSync extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create a moderated community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_SUComment_Like_BadgeSync() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a moderate community you own adding User 2 as a members</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 comment on the update</B></li>
	*<li><B>Step: User 1 like the comment</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 2 look at the badge number of the My Notifications view and the Notification Center</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7ED7C5DD2609EACE85257E6F00446C5E">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00030 - COMMUNITIES EVENTS MY NOTIFICATIONS BADGE SYNCED WITH HEADER</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_SUComment_Like_BadgeSync() {

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
		
		// Refresh the My Notifications and Notification Center badge values by re-loading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have the expected value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}