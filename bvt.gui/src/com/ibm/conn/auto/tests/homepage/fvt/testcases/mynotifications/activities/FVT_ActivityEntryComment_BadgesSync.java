package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.activities;

import org.testng.annotations.BeforeClass;
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
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;

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
 * [FVT Automation for Story 154601] Scynchronize realtime updates with badging
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/156301
 * @author Patrick Doherty
 */

public class FVT_ActivityEntryComment_BadgesSync extends SetUpMethodsFVT {
	
	private Activity privateActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private BaseActivity baseActivity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		// User 1 will now create a private activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created for the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}

	/**
	* test_ActivityEntry_Comment_BadgesSync() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 start an entry</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view and the Notification Center</B></li>
	*<li><B>Verify: Verify that both badges show the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9BB65C07CE6E4AE685257E6F003035AD">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00010 - ACTIVITIES EVENTS MY NOTIFICATIONS BADGE SYNCED WITH HEADER</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_ActivityEntry_Comment_BadgesSync() {

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
		
		ui.endTest();
	}
}