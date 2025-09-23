package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.general;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
 *	Date:		14th January 2016
 */

public class FVT_ActionRequired_General_BadgeTotalInView extends SetUpMethodsFVT {
	
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private ArrayList<Activity> listOfActivities = new ArrayList<Activity>();
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
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activities created during the test
		for(Activity activity : listOfActivities) {
			activitiesAPIUser1.deleteActivity(activity);
		}
	}
	
	/**
	* test_ActionRequired_BadgeNumberShowTotalWhileInActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Activities</B></li>
	*<li><B>2. User 1 start a public activity adding User 2 as a member</B></li>
	*<li><b>3. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>4. User 2 log into Homepage AS</b></li>
	*<li><b>5. User 2 check the Action Required view</b></li>
	*<li><b>Verify: Verify that there is there is a badge displaying the number of entries (in this case 1)</b></li>
	*<li><b>6. User 1 log into Activities in a different browser</b></li>
	*<li><b>7. User 1 start a private activity adding User 2 as a member</b></li>
	*<li><b>8. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>9. User 1 start a public activity adding User 2 as a member</b></li>
	*<li><b>10. User 1 create a todo in the activity and assign to User 2</b></li>
	*<li><b>11. User 2 who is still in the Action Required view, change filter, then return to Filter By -> All</b></li>
	*<li><b>Verify: Verify that when the user switches filter back to All the badge updates to the amount of entries (in this case 3)</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EA64D732AA6A1AD0852579A00033F0F5">TTT: AS - Action Required - 00014 - Badge Number Show Total When User If Item Added While User Is In Action Required View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_BadgeNumberShowTotalWhileInActionRequired() {
		
		String testName = ui.startTest();
		
		// Log in as User 2 and go to the Action Required
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Retrieve the Action Required badge value before any events have been added to the view
		int badgeValueBeforeEvents = UIEvents.getActionRequiredBadgeValue(driver);
				
		// User 1 will now create their first public activity with User 2 added as a member
		BaseActivity basePublicActivity1 = ActivityBaseBuilder.buildBaseActivity(testName + Helper.genStrongRand(), false);
		Activity publicActivity1 = ActivityEvents.createActivityWithOneMember(basePublicActivity1, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
		listOfActivities.add(publicActivity1);
		
		// User 1 will now create a to-do item in the first public activity and will assign the to-do item to User 2
		BaseActivityToDo basePublicActivityTodo1 = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity1, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity1, basePublicActivityTodo1, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Refresh the Action Required badge value by refreshing the UI
		UIEvents.refreshPage(driver);
		
		// Retrieve the Action Required badge value now that new events have been added to the view
		int badgeValueAfterEvents = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required badge value has incremented by 1
		HomepageValid.verifyIntValuesAreEqual(badgeValueAfterEvents - badgeValueBeforeEvents, 1);
		
		// User 1 will now create a private activity with User 2 added as a member
		BaseActivity basePrivateActivity = ActivityBaseBuilder.buildBaseActivity(testName + Helper.genStrongRand(), true);
		Activity privateActivity = ActivityEvents.createActivityWithOneMember(basePrivateActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
		listOfActivities.add(privateActivity);
		
		// User 1 will now create a to-do item in the private activity and will assign the to-do item to User 2
		BaseActivityToDo basePrivateActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(privateActivity, basePrivateActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
						
		// User 1 will now create their second public activity with User 2 added as a member
		BaseActivity basePublicActivity2 = ActivityBaseBuilder.buildBaseActivity(testName + Helper.genStrongRand(), false);
		Activity publicActivity2 = ActivityEvents.createActivityWithOneMember(basePublicActivity2, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
		listOfActivities.add(publicActivity2);
		
		// User 1 will now create a to-do item in the second public activity and will assign the to-do item to User 2
		BaseActivityToDo basePublicActivityTodo2 = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity2, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity2, basePublicActivityTodo2, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Refresh the Action Required badge value by switching the Action Required filters and returning to the 'All' filter
		UIEvents.filterBy(ui, HomepageUIConstants.FilterActivities);
		UIEvents.filterBy(ui, HomepageUIConstants.FilterAll);
		
		// Retrieve the Action Required badge value now that new events have been added to the view
		badgeValueAfterEvents = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required badge value has now incremented by 3
		HomepageValid.verifyIntValuesAreEqual(badgeValueAfterEvents - badgeValueBeforeEvents, 3);
		
		ui.endTest();
	}
}