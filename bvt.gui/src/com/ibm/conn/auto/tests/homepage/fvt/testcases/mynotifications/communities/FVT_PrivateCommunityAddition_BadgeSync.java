package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

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

public class FVT_PrivateCommunityAddition_BadgeSync extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String user1MentionsUpdate1Id, user2StatusUpdate1, user2StatusUpdate2, user2StatusUpdate1Id, user2StatusUpdate2Id;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 2 will now post their first status update
		user2StatusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user2StatusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate1);
		
		// User 2 will now post their second status update
		user2StatusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user2StatusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate2);
		
		// Initialise the ID's for the mentions status updates as null
		user1MentionsUpdate1Id = null;
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status updates posted by User 2
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdate1Id);
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdate2Id);
		
		// Delete the status updates with mentions posted by User 1 (only deletes the updates which have been successfully posted)
		if(user1MentionsUpdate1Id != null) {
			profilesAPIUser1.deleteBoardMessage(user1MentionsUpdate1Id);
		}
	}

	/**
	* test_NotificationBadging_ClearingBadgeValues() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 adds some Notification events for User 2 such as Comments and Likes on User 2's content and mentions directed at User 2 from different Apps</B></li>
	*<li><B>Step: User 2 logs into Homepage and Opens their Notifications Center Flyout - Verification point 1</B></li>
	*<li><B>Step: User 1 adds some more Notification events for User 2</B></li>
	*<li><B>Step: User 2 Opens their My Notifications homepage view from the left Nav. link - Verification point 2</B></li>
	*<li><B>Verification Point 1: As well as the numbered badge being removed from the Header, it is also removed from My Notifications in the left nav after a short transition</B></li>
	*<li><B>Verification Point 2: As well as the numbered badge being removed from the My Notifications in the left nav, it is also removed from the Header  after a short transition</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E081BF215AC1EA3E85257E6F004F0722">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00100 - OPENING MY NOTIFICATIONS CLEARS NOTIFICATIONS CENTER BADGE AND VICE VERSA</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_NotificationBadging_ClearingBadgeValues() {

		ui.startTest();
		
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Navigate to the I'm Following view - this will reset the My Notifications badge values to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 1 will now comment on User 2's first status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdate1Id, user1Comment1, profilesAPIUser1);
		
		// User 1 will now like User 2's first status update
		ProfileEvents.likeStatusUpdate(profilesAPIUser1, user2StatusUpdate1Id);
		
		// User 1 will now post the first status update with mentions to User 2
		Mentions mentions1 = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		user1MentionsUpdate1Id = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser1, mentions1);
		
		// Refresh the I'm Following view - this will update the My Notifications badge value to 3
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have been updated to 3
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 3);
		
		// User 2 will now open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		/**
		 * When the MN and NC badges are reset in the UI - the values can take a few seconds to update dynamically behind the scenes.
		 * Due to this, we need to ask Selenium to wait around until the values have been updated dynamically to their expected values.
		 * 
		 * Therefore Selenium will just click at position (0, 0) in the UI up to a maximum of 3 times.
		 * This will give the UI the time it needs to update the badge values dynamically (ie. we cannot change views).
		 * Usually the values are updated successfully after 2 iterations of the below loop.
		 */
		int numberOfTries = 0;
		while(numberOfTries < 3 && UIEvents.getMyNotificationsBadgeValue(driver) > 0) {
			// Reset Selenium back to the top of the AS
			UIEvents.resetASToTop(ui);
			numberOfTries ++;
		}
		// Verify that the My Notifications and Notifications Center badge values have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
					
		// User 1 will now comment on User 2's second status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdate2Id, user1Comment2, profilesAPIUser1);
				
		// User 1 will now like User 2's second status update
		ProfileEvents.likeStatusUpdate(profilesAPIUser1, user2StatusUpdate2Id);
				
		// Refresh the I'm Following view - this will update the My Notifications badge value to 2
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have been updated to 2
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 2);
		
		// User 2 will now navigate to their My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		ui.endTest();
	}
}