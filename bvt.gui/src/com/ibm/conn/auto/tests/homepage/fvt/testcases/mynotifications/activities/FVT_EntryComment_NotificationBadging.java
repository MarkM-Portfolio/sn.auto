package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.activities;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;

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

public class FVT_EntryComment_NotificationBadging extends SetUpMethodsFVT {
	
	private Activity privateActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2, activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6;
	private BaseActivity baseActivity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(6);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		activitiesAPIUser3 = initialiseAPIActivitiesHandlerUser(testUser3);
		activitiesAPIUser4 = initialiseAPIActivitiesHandlerUser(testUser4);
		activitiesAPIUser5 = initialiseAPIActivitiesHandlerUser(testUser5);
		activitiesAPIUser6 = initialiseAPIActivitiesHandlerUser(testUser6);
		
		// User 1 will now create a private activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// User 1 will now add Users 2 to 6 to the activity as members
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6 };
		ActivityEvents.addMemberMultipleUsers(privateActivity, testUser1, activitiesAPIUser1, membersList);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created for the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}

	/**
	* test_ActivityEntry_CommentEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 start an entry</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 3 and User 4 comment on the entry</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>Step: User 5 and User 6 comment on the entry</B></li>
	*<li><B>Step: User 4 delete their comment from the entry</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/68B09FB388F21DC185257E12006BFDDD">TTT - NOTIFICATIONS BADGING - ACTIVITIES - 00010 - ENTRY COMMENT EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_ActivityEntry_CommentEvents() {

		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view to reset the My Notifications counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now post a comment to the activity entry
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// Refresh the page by reloading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 3 will now post a comment to the activity entry
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user3Comment, testUser3, activitiesAPIUser3, false);
		
		// User 4 will now post a comment to the activity entry
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		Reply user4Reply = ActivityEvents.createComment(privateActivity, activityEntry, null, user4Comment, testUser4, activitiesAPIUser4, false);
		
		// Refresh the page by reloading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 5 will now post a comment to the activity entry
		String user5Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user5Comment, testUser5, activitiesAPIUser5, false);
		
		// User 6 will now post a comment to the activity entry
		String user6Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user6Comment, testUser6, activitiesAPIUser6, false);
		
		// User 4 will now delete their comment on the activity entry
		ActivityEvents.deleteReply(user4Reply, testUser4, activitiesAPIUser4);
		
		// Refresh the page by reloading the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
				
		ui.endTest();
	}
}