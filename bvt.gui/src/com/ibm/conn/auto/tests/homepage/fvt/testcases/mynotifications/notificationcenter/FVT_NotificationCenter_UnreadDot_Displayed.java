package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Read/Unread Marker in AS Events] FVT UI Automation for Story 139476
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139568
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_UnreadDot_Displayed extends SetUpMethodsFVT {
	
	private Activity privateActivity;
	private APIActivitiesHandler activitiesAPIUser1;
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
		
		// User 1 will now create a private activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}
	
	/**
	* test_NotificationCenter_UnreadDot_displayed() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an activity adding User 2 as a member</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 open the notification center</B></li>
	*<li><B>Step: User 2 look at the unread notification of being added to the activity</B></li>
	*<li><B>Verify: In the top right corner there is a persisted blue dot</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7779E90C6123BEC385257E2F0043FC37">TTT - INDIVIDUAL NOTIFICATION - 00010 - PERSISTED BLUE DOT FOR UNREAD</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_UnreadDot_displayed() {

		ui.startTest();
		
		// User 2 will now log in to Homepage
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Create the news story to be verified
		String addedYouAsAMemberEvent = ActivityNewsStories.getNotifiedYouThatYouWereAddedToTheActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		/**
		 * Verify that the news story is displayed with a blue dot (ie. marked as unread) in the Notification Center
		 * The success of this method call will prove that the blue dot is present and correct in the UI and that the story is marked as unread
		 */
		UIEvents.openNotificationCenterAndVerifyNewsStoryIsUnread(ui, driver, addedYouAsAMemberEvent);
		
		ui.endTest();
	}
}