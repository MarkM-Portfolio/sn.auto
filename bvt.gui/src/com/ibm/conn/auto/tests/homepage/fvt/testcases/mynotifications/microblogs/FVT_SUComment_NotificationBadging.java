package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.microblogs;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                                    */
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

public class FVT_SUComment_NotificationBadging extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private String statusUpdate, statusUpdateId;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now post a status update
		statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
	}

	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}

	/**
	* test_StatusUpdate_CommentEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 comment on the update</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 2 comment on the update</B></li>
	*<li><B>Step: User 3 comment on the update 3 times</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>Step: User 2 delete their comments</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/88DCFF1F060A7B5385257E18003FA7B9">TTT - NOTIFICATIONS BADGING - MICROBLOGGING - 00010 - MICROBLOG COMMENT EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_StatusUpdate_CommentEvents() {

		ui.startTest();
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view - this will reset the My Notifications and Notification Badge counters to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Badge counters have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now post their first comment to User 1's status update
		String user2Comment1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2Comment1Id = ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment1, profilesAPIUser2);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Badge counters have now been updated to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 2 will now post their second comment to User 1's status update
		String user2Comment2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2Comment2Id = ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment2, profilesAPIUser2);
		
		// User 3 will now post 3 comments to User 1's status update
		String user3Comments[] = { Data.getData().commonStatusUpdate + Helper.genStrongRand(), Data.getData().commonStatusUpdate + Helper.genStrongRand(), Data.getData().commonStatusUpdate + Helper.genStrongRand() };
		String user3CommentIds[] = new String[user3Comments.length];
		for(int index = 0; index < user3Comments.length; index ++) {
			user3CommentIds[index] = ProfileEvents.addStatusUpdateComment(statusUpdateId, user3Comments[index], profilesAPIUser3);
		}
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Badge counters are still set to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 2 will now delete both of their comments posted to User 1's status update
		ProfileEvents.deleteComment(statusUpdateId, user2Comment1Id, profilesAPIUser2);
		ProfileEvents.deleteComment(statusUpdateId, user2Comment2Id, profilesAPIUser2);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Badge counters are still set to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}