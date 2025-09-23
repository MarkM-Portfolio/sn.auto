package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.general;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
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
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

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
/**
 * 	@author 	Anthony Cox
 *	Date:		16th January 2016
 */

public class FVT_ActionRequired_General_BadgeDecrements extends SetUpMethodsFVT {
	
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
		
		// Delete the activities created during the test
		activitiesAPIUser1.deleteActivity(privateActivity);
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_ActionRequired_BadgeNumberWillDecrement()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Activities</B></li>
	*<li><B>2. User 1 start a private activity adding User 2 as a member</B></li>
	*<li><b>3. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>4. User 1 start a public activity adding User 2 as a member</b></li>
	*<li><b>5. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>6. User 2 log into Homepage / Action Required / Activities</b></li>
	*<li><b>7. User 2 remove a story from the Action Required view</b></li>
	*<li><b>Verify: Verify that the badge number has now updated to '1' dynamically</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C93ADFD5BC944A220525798400485559">TTT: AS - Action Required - 00012 - Badge Number Will Decrement If User Removes An Item</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_BadgeNumberWillDecrement() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the private activity and will assign it to User 2
		BaseActivityToDo basePrivateActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(privateActivity, basePrivateActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a to-do item in the public activity and will assign it to User 2
		BaseActivityToDo basePublicActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, basePublicActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Retrieve the Action Required badge value before any news stories are removed
		int badgeValueBeforeNewsStoryRemoval = UIEvents.getActionRequiredBadgeValue(driver);
		
		// User 1 will now remove a story from the Action Required view by marking one of the to-do items as complete
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, todo, true);
		
		// Refresh the Action Required view
		UIEvents.gotoImFollowing(ui);
		UIEvents.gotoActionRequired(ui);
		
		// Retrieve the Action Required badge value now that a news story has been removed
		int badgeValueAfterNewsStoryRemoval = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the badge value after removal is les than the badge value before removal
		HomepageValid.verifyBooleanValuesAreEqual(badgeValueAfterNewsStoryRemoval < badgeValueBeforeNewsStoryRemoval, true);
		
		// Verify that the badge value decremented by 1 after removing the news story
		HomepageValid.verifyIntValuesAreEqual((badgeValueBeforeNewsStoryRemoval - badgeValueAfterNewsStoryRemoval), 1);
		
		ui.endTest();
	}
}