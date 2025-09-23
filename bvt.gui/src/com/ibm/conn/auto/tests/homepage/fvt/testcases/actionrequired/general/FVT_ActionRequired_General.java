package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.general;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
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

public class FVT_ActionRequired_General extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseActivity baseActivity;
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
		
		// User 1 will now create a public activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_ActionRequired_ConfirmPromptAndMessageWhenRemovingAnItem()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 1 start an activity and add User 2</B></li>
	*<li><b>3. User 1 create a todo in the Activity</b></li>
	*<li><b>4. User 1 assign the todo to User 2</b></li>
	*<li><b>5. User 2 log into Homepage / Action Required / All Updates</b></li>
	*<li><b>6. Go to the story of the activity todo assigned and click the 'X' to remove the story</b></li>
	*<li><b>Verify: Confirm that User 2 is prompted with a message to remove the Action Required</b></li>
	*<li><b>Verify: Verify that there is a message on the view that the Action Required was successfully removed from the view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/630AC8224D23D9BD8525797B003C134C">TTT: AS - Action Required - Confirm Prompt And Message When Removing An Item From Action Required View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ConfirmPromptAndMessageWhenRemovingAnItem() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the to-do item to User 2
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Create the news story to be verified and deleted
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the assigned to-do item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		// Remove the assigned to-do item news story using the UI
		UIEvents.removeNewsStoryFromActionRequiredViewUsingUI(ui, assignedTodoEvent);
		
		// Verify that the assigned to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}
