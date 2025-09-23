package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.general;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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
 * 	@author 	Anthony Cox
 *	Date:		6th November 2015
 */

public class FVT_ActionRequired_General_OnPremOnly extends SetUpMethodsFVT {
	
	private Activity privateActivity, publicActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseActivity basePrivateActivity, basePublicActivity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
				
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
				
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		// User 1 will now create a private activity with User 2 added as a member
		basePrivateActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivityWithOneMember(basePrivateActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
				
		// User 1 will now create a public activity with User 2 added as a member
		basePublicActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(basePublicActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}
			
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(privateActivity);
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
			
	/**
	* test_ActionRequired_BadgeVisibleFromWidgetsAndGettingStarted()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Activities</B></li>
	*<li><B>2. User 1 start a private activity adding User 2 as a member</B></li>
	*<li><b>3. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>4. User 1 start a public activity adding User 2 as a member</b></li>
	*<li><b>5. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>6. User 2 log into Homepage / Getting Started</b></li>
	*<li><b>7. User 2 log into Homepage / Widgets</b></li>
	*<li><b>Verify: Verify that when the User is in views from 6 & 7 that the Action Required has a badge beside it (in this case with 2)</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D70689E3D7C4E093052579840049A60B">TTT: AS - Action Required - 00015 - Badge Visible from Getting Started and Widgets page</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_ActionRequired_BadgeVisibleFromWidgetsAndGettingStarted() {
		
		String testName = ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Retrieve the Action Required badge value before any events have been added to the view
		int badgeValueBeforeEvents = UIEvents.getActionRequiredBadgeValue(driver);
		
		// User 1 will now create a to-do item in the private activity and will assign the to-do item to User 2
		BaseActivityToDo basePrivateActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(privateActivity, basePrivateActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a to-do item in the public activity and will assign the to-do item to User 2
		BaseActivityToDo basePublicActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, basePublicActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Refresh the Action Required badge value by refreshing the UI
		UIEvents.refreshPage(driver);
		
		// Retrieve the Action Required badge value now that two new events have been added to the view
		int badgeValueAfterEvents = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required badge value has incremented by 2
		HomepageValid.verifyIntValuesAreEqual(badgeValueAfterEvents - badgeValueBeforeEvents, 2);
		
		// Navigate to the My Page view
		UIEvents.gotoMyPage(ui);
		
		// Retrieve the Action Required badge value from the My Page view
		int badgeValueFromMyPageView = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required badge value has the correct value beside it
		HomepageValid.verifyIntValuesAreEqual(badgeValueFromMyPageView, badgeValueAfterEvents);
		
		// Navigate to the Getting Started view
		UIEvents.gotoGettingStarted(ui);
		
		// Retrieve the Action Required badge value from the Getting Started view
		int badgeValueFromGettingStartedView = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required badge value has the correct value beside it
		HomepageValid.verifyIntValuesAreEqual(badgeValueFromGettingStartedView, badgeValueAfterEvents);
		
		ui.endTest();
	}
}
