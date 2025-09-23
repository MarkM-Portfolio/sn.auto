package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.mynotifications;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	27th September 2016
 */

public class FVT_Microblogs_NotificationBadging_LikeEvents extends SetUpMethods2 {

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private HomepageUI ui;
	private String serverURL, user1StatusUpdate1Id, user1StatusUpdate2Id;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		profilesAPIUser4 = new APIProfilesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		// User 1 will now post the first status update
		String user1StatusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate1);
		
		// User 1 will now post the second status update
		String user1StatusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate2);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdate1Id);
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdate2Id);
	}
	
	/**
	* test_NotificationBadging_LikeEvents() 
	*<ul>
	*<li><B>1: User 1 log into Homepage</B></li>
	*<li><B>2: User 1 add 2 status updates</B></li>
	*<li><B>3: User 2 like the first update</B></li>
	*<li><B>4: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>5: User 1 look at the badge number of the My Notifications view - verification point 1</B></li>
	*<li><B>6: User 3 like both updates</B></li>
	*<li><B>7: User 4 like the first update</B></li>
	*<li><B>8: User 1 refresh the I'm Following view and look at the badge again - verification point 2</B></li>
	*<li><B>9: User 3 unlike the second update</B></li>
	*<li><B>10: User 1 refresh the I'm Following view again</B></li>
	*<li><B>11: User 1 check the badge - verification point 3</B></li>
	*<li><B>Verification Point 1: The badge shows the number '1'</B></li>
	*<li><B>Verification Point 2: The badge shows the number '2'</B></li>
	*<li><B>Verification Point 3: The badge shows the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/066688F9D38E4B5185257E18003FA7BD">NOTIFICATIONS BADGING - MICROBLOGGING - 00014 - DIFERENT MICROBLOG LIKE EVENTS</a></li>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_NotificationBadging_Microblog_LikeEvents() {
		
		ui.startTest();
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view to reset the My Notifications counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter have been reset to 0
		verifyMyNotificationsAndNotificationsCenterBadgeValues(0);
				
		// User 2 will now like the first status update posted by User 1
		ProfileEvents.likeStatusUpdate(profilesAPIUser2, user1StatusUpdate1Id);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		if(UIEvents.getMyNotificationsBadgeValue(driver) != 1) {
			// Log out to give the server more time to update the My Notifications badges
			LoginEvents.logout(ui);
			
			// Log in as User 1 and navigate to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		}
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		verifyMyNotificationsAndNotificationsCenterBadgeValues(1);
		
		// User 3 will now like both the first and second status updates posted by User 1
		ProfileEvents.likeStatusUpdate(profilesAPIUser3, user1StatusUpdate1Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser3, user1StatusUpdate2Id);
		
		// User 4 will now like the first status update posted by User 1
		ProfileEvents.likeStatusUpdate(profilesAPIUser4, user1StatusUpdate1Id);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		if(UIEvents.getMyNotificationsBadgeValue(driver) != 2) {
			// Log out to give the server more time to update the My Notifications badges
			LoginEvents.logout(ui);
			
			// Log in as User 1 and navigate to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		}
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 2
		verifyMyNotificationsAndNotificationsCenterBadgeValues(2);
		
		// User 3 will now unlike the second status update posted by User 1
		ProfileEvents.unlikeStatusUpdate(profilesAPIUser3, user1StatusUpdate2Id);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		if(UIEvents.getMyNotificationsBadgeValue(driver) != 1) {
			// Log out to give the server more time to update the My Notifications badges
			LoginEvents.logout(ui);
			
			// Log in as User 1 and navigate to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		}
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		verifyMyNotificationsAndNotificationsCenterBadgeValues(1);
		
		ui.endTest();
	}
	
	/**
	 * Verifies that the My Notifications and Notification Center badges match the specified value
	 * 
	 * @param badgeValueToBeVerified - The Integer value to be verified
	 */
	private void verifyMyNotificationsAndNotificationsCenterBadgeValues(int badgeValueToBeVerified) {
		
		// Retrieve the badge values for both the My Notifications and Notification Center badges
		int myNotificationsCounter = UIEvents.getMyNotificationsBadgeValue(driver);
		int notificationCenterCounter = UIEvents.getNotificationCenterBadgeValue(driver);
		
		// Verify that the My Notifications badge counter and Notification Center badge counters match the required value
		HomepageValid.verifyIntValuesAreEqual(myNotificationsCounter, badgeValueToBeVerified);
		HomepageValid.verifyIntValuesAreEqual(notificationCenterCounter, badgeValueToBeVerified);
	}
}