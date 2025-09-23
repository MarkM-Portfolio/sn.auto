package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016, 2017                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout] FVT UI Automation for Story 140633
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143012
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_EventLink extends SetUpMethodsFVT {
	
	private Activity privateActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
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
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a private activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}

	/**
	* test_NotificationCenter_EventLink() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 add an activity entry and notify User 2 of the entry</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 open the notification center flyout</B></li>
	*<li><B>Step: User 2 look at the activity entry name in the event in the flyout - verification point 1</B></li>
	*<li><B>Step: User 2 click on the activity entry name - verification point 2</B></li>
	*<li><B>Verify: Verify the activity entry name is a clickable link</B></li>
	*<li><B>Verify: Verify that it opens the activity entry in a new tab / window</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BCE94A83710C6F1985257DCC00564A20">TTT - NOTIFICATION CENTER FLYOUT - 00033 - EVENT HAS A CLICKABLE LINK TO TAKE USER TO APPLICATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_EventLink(){

		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the private activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		boolean preserveInstance = false;
		if(isOnPremise) {
			// User 1 will now notify User 2 about the newly created activity entry using the API
			ActivityEvents.notifyUserAboutActivityEntry(privateActivity, activityEntry, testUser1, activitiesAPIUser1, profilesAPIUser2);
		} else {
			// User 1 will now notify User 2 about the newly created activity entry using the UI (the API does NOT work for Smart Cloud)
			ActivityEvents.loginAndNotifyUserAboutActivityEntryUsingUI(ui, privateActivity, activityEntry, testUser1, profilesAPIUser2, preserveInstance);
			
			// Return to the Home screen and log out
			LoginEvents.gotoHomeAndLogout(ui);
			preserveInstance = true;
		}
		// User 2 will now log in to Homepage
		LoginEvents.loginToHomepage(ui, testUser2, preserveInstance);
		
		// User 2 will now open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		// Create the news story which contains the activity entry link
		String notifiedAboutEntryEvent = ActivityNewsStories.getNotifiedYouAboutTheActivityEntryItemNewsStory(ui, baseActivityEntry.getTitle(), testUser1.getDisplayName());
		
		// Retrieve the main browser window handle before clicking on the link
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 2 will now click on the activity entry link in the notification news story in the Notification Center flyout
		UIEvents.clickLinkInNotificationCenterFlyout(ui, notifiedAboutEntryEvent, baseActivityEntry.getTitle());
		
		// Switch focus to the newly opened 'Activities' browser window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the activity and activity entry are displayed in Activities UI
		HomepageValid.verifyActivityEntryIsDisplayedInActivitiesUI(ui, driver, privateActivity, activityEntry, false);
		
		// Close the Activities UI browser window and switch focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}