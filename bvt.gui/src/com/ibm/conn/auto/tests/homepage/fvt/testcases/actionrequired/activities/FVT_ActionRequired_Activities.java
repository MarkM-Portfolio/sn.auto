package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.activities;

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
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                             		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	Author:		Anthony Cox
 * 	Date:		15th October 2015
 */

public class FVT_ActionRequired_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity publicActivity, publicActivityForDeletion;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity, baseActivityForDeletion;
	private boolean deletedActivity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		// User 1 will now create a public activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
		
		// User 1 will now create a public activity which will be used for a test case and then deleted before the test uite has completed
		baseActivityForDeletion = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivityForDeletion = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivityForDeletion, isOnPremise);
		deletedActivity = false;
	}	
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activities created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
		
		if(deletedActivity == false) {
			activitiesAPIUser1.deleteActivity(publicActivityForDeletion);
		}
	}
	
	/**
	* test_ActionRequired_ActivityToDoItemsIntoActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Activities</B></li>
	*<li><B>2. User 1 start a public activity - User 2 is a member</B></li>
	*<li><B>3. User 1 adds a to-do to the activity, assigned to User 2</B></li>
	*<li><B>4. User 2 go to Homepage / Updates / Action Required</B></li>
	*<li><b>Verify: User 2 should see a story about being assigned the to-do</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D1D6D80C2BAE903A8525792F0051A357">TTT: AS - Action Required - 00011 - Activity To-Do items into Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityToDoItemsIntoActionRequired() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to User 2
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_RemovingActivityToDoStoriesFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. Go to Homepage / Updates / Action Required / Activities</B></li>
	*<li><B>3. As User 1 go to the to-do story in the Homepage / Updates / Action Required / Activities UI and select the X icon to remove the story</B></li>
	*<li><b>Verify: Verify in each case that the story is removed from the todo list and the counter is decremented</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BEAC50AF07A736AC8525792F004F0F0B">TTT: AS - Action Required - 00012 - Removing Activity To-Do Stories From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_RemovingActivityToDoStoriesFromActionRequired() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Retrieve the badge counter value for Action Required before any news stories are removed from the view
		int badgeValueBeforeStoryRemoval = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		// Remove the assigned to-do item news story from the Action Required view using the UI
		UIEvents.removeNewsStoryFromActionRequiredViewUsingUI(ui, assignedTodoEvent);
		
		// Retrieve the badge counter value for Action Required now that a news story has been removed from the view
		int badgeValueAfterStoryRemoval = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		// Verify that the action required badge value has decremented as expected after story removal
		HomepageValid.verifyIntValuesAreEqual(badgeValueAfterStoryRemoval, (badgeValueBeforeStoryRemoval - 1));
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_ActivityToDoDeletionAutoRemovesStoryFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Activities</B></li>
	*<li><B>2. Start an Activity</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>5. Go back to the activity and delete the todo</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A7762E90143A06A8852579A600550A29">TTT: AS - Action Required - 00013 - Activity To-Do Deletion Auto-Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityToDoDeletionAutoRemovesStoryFromActionRequired() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
				
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
				
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		// User 1 will now delete the to-do item from the activity
		ActivityEvents.deleteTodoItem(todo, testUser1, activitiesAPIUser1);
				
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_ActivityToDoCompletionAutoRemovesStoryFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Activities</B></li>
	*<li><B>2. Start an Activity</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>5. Go back to the activity and complete the todo</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/29059853CCD823E5852579D60042F37F">TTT: AS - Action Required - 00014 - Activity To-Do Completion Auto-Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityToDoCompletionAutoRemovesStoryFromActionRequired() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
				
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
				
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		// User 1 will now mark the to-do item as completed
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, todo, true);
				
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_ActivityDeletionAutoRemovesStoryFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Activities</B></li>
	*<li><B>2. Start an Activity</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>5. Go back to the activity and delete the entire activity</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE3E022E4A064948852579ED004976E5">TTT: AS - Action Required - 00015 - Activity Deletion Auto-Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityDeletionAutoRemovesStoryFromActionRequired() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivityForDeletion, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
				
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivityForDeletion.getName(), testUser1.getDisplayName());
				
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		// User 1 will now delete the public activity
		ActivityEvents.deleteActivity(publicActivityForDeletion, testUser1, activitiesAPIUser1);
		deletedActivity = true;
				
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_ActivityToDoMarkedIncompleteRestoresStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Activities</B></li>
	*<li><B>2. Start an Activity</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>5. Go back to the activity and mark the to-do complete</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><b>7. Go back to the activity and mark the to-do incomplete (uncheck the box beside the to-do)</b></li>
	*<li><b>8. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is restored in Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8E4C394F82AEBA66852579ED004A63CA">TTT: AS - Action Required - 00016 - Activity To-Do Marked Incomplete Restores Story To Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityToDoMarkedIncompleteRestoresStory() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
				
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
				
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		// User 1 will now mark the to-do item as completed
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, todo, true);
				
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		// User 1 will now re-open the to-do item
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, todo, false);
		
		// Create the news story to be verified
		String reopenedTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the reopened todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_ActivityToDoEditedAndReassignedUpdatesStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Activities as User 1</B></li>
	*<li><B>2. Start an Activity and User 2 as a member</B></li>
	*<li><B>3. Create a todo and assign it to yourself</B></li>
	*<li><b>4. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><b>5. Go back to the activity and assign to User 2</b></li>
	*<li><b>6. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is deleted from Homepage / Action Required / Activities</b></li>
	*<li><b>7. Log in as User 2</b></li>
	*<li><b>8. Go to Homepage / Action Required / Activities</b></li>
	*<li><b>Verify: Verify that the todo story is displayed in Homepage / Action Required / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E9E2D697D2A887E8852579ED004AE72D">TTT: AS - Action Required - 00017 - Activity To-Do Edited And Reassigned Updates Story In Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActivityToDoEditedAndReassignedUpdatesStory() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will assign the todo item to themselves
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser1, activitiesAPIUser1, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
				
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
				
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now assign the to-do item to User 2
		ActivityEvents.assignTodoItemToUser(todo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// Verify that the assigned todo item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, true);
		
		// Verify that the assigned todo item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}