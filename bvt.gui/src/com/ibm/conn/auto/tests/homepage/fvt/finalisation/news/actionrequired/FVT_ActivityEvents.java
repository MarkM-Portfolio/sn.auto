package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.actionrequired;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	4th October 2016
 */

public class FVT_ActivityEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseActivity baseActivity;
	private boolean isOnPremise;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
		activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a public activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_ActivityToDo_Completion_AutoRemovesStory()
	*<ul>
	*<li><B>1. Log into Activities</B></li>
	*<li><B>2. Start an Activity</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities - verification point #1</b></li>
	*<li><b>5. Go back to the activity and complete the todo</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities - verification point #2</b></li>
	*<li><b>Verification Point 1: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>Verification Point 2: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/29059853CCD823E5852579D60042F37F">AS - Action Required - 00014 - Activity To-Do Completion Auto-Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_ActivityToDo_Completion_AutoRemovesStory() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the to-do item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		Todo activityTodo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		/**
		 * Regularly, when this test is run on the Dashboard against G2, the views are NOT being updated in time to include the assigned to-do item event.
		 * 
		 * Therefore we are going to attempt up to 3 logins - each time we log out (if we have to) will give the server
		 * some additional time that it needs to pick up the assigned to-do item event and update the Action Required view.
		 */
		boolean preserveInstance = false;
		boolean assignedToDoEventInView = false;
		int numberOfTries = 0;
		do {
			// Log in as User 1 and go to the Action Required view
			LoginEvents.loginAndGotoActionRequired(ui, testUser1, preserveInstance);
			
			if(preserveInstance == false) {
				// Set the boolean as true now that the user has logged in at least once
				preserveInstance = true;
			}
			if(HomepageValid.isTextDisplayed(driver, assignedTodoEvent) == false) {
				// Navigate to the I'm Following view
				UIEvents.gotoImFollowing(ui);
				
				// Log out from Connections
				LoginEvents.logout(ui);
			} else {
				assignedToDoEventInView = true;
			}
			numberOfTries ++;
		} while(numberOfTries < 3 && assignedToDoEventInView == false);	
		
		// Verify that the assigned to-do item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now mark the to-do item as complete
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, activityTodo, true);
		
		// Verify that the assigned to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}