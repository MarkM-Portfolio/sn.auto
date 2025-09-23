package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_ActivityEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
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
	* test_EntryTodo_FinalTwoComments() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an activity</B></li>
	*<li><B>Step: User 1 add an entry and a todo</B></li>
	*<li><B>Step: User 2 add 2 comments on each</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add a private comment to a todo</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify it is still the same 2 comments and the private one is not shown</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/216C4B44FD90028585257E2F0036A45B">TTT - INLINE COMMENTS - 00010 - ACTIVITY EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_EntryTodo_FinalTwoComments() {

		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// User 1 will now create a to-do in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, privateActivity);
		
		// User 2 will now post their first comment to the activity entry
		String user2EntryComment1 = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user2EntryComment1, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post their second comment to the activity entry
		String user2EntryComment2 = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user2EntryComment2, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post their first comment to the to-do item
		String user2TodoComment1 = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, null, todo, user2TodoComment1, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post their second comment to the to-do item
		String user2TodoComment2 = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, null, todo, user2TodoComment2, testUser2, activitiesAPIUser2, false);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		String commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on activity entry event and User 2's first and second comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2EntryComment1, user2EntryComment2}, filter, true);
			
			// Verify that the comment on to-do item event and User 2's first and second comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2TodoComment1, user2TodoComment2}, null, true);
		}
		
		// User 2 will now post their third comment (a private comment) to the to-do item
		String user2TodoComment3 = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, null, todo, user2TodoComment3, testUser2, activitiesAPIUser2, true);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on activity entry event and User 2's first and second comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2EntryComment1, user2EntryComment2}, filter, true);
			
			// Verify that the comment on to-do item event and User 2's first and second comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2TodoComment1, user2TodoComment2}, null, true);
			
			// Verify that the User 2's private comment posted to the to-do item is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2TodoComment3}, null, false);
		}		
		ui.endTest();
	}
}