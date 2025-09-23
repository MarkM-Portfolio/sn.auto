package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_Standalone_Activities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity activity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private BaseActivity baseActivity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);

		// User 1 will now create a private activity with User 2 added as a member and follower
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		activity = ActivityEvents.createActivityWithOneMemberAndOneFollower(baseActivity, testUser1, activitiesAPIUser1, testUser2, activitiesAPIUser2, isOnPremise);		
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(activity);
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntry_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, add user 2 as a member</B></li>	
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8CAFD7FC1056C04C852578760079E8B2">TTT - AS - FOLLOW - ACTIVITY - 00025 - ACTIVITY.ENTRY.CREATED - STANDALONE PRIVATE ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_CreateEntry_PrivateActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryComment_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, add user 2 as a member</B></li>	
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a comment on an activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0816D908403F135E852578760079E8B9">TTT -  AS - FOLLOW - ACTIVITY - 00035 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PRIVATE ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateEntryComment_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will post a comment to that entry
		String entryComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, entryComment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription().trim(), entryComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateTodo_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, add user 2 as a member</B></li>	
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ACFE9F64A1F69906852578760079E8C2">TTT - AS - FOLLOW - ACTIVITY - 00045 - ACTIVITY.TO-DO.CREATED - STANDALONE PRIVATE ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create a to-do in the activity
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseTodo, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CompleteTodo_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, add user 2 as a member</B></li>	
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Mark the todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.completed is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD58729EB79B762D852578760079E8CA">TTT - AS - FOLLOW - ACTIVITY - 00055 - ACTIVITY.TO-DO.COMPLETED - STANDALONE PRIVATE ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CompleteTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create a to-do in the activity and will mark the to-do as completed
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseTodo, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTodo_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><b>Step: User 1 go to a todo in the activity</b></li>
	 *<li><b>Step: User 1 update the todo in the activity</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.updated story appears in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B9DEF4398B9F6EFF85257A6300369151">TTT - AS - FOLLOW - ACTIVITY - 00085 - ACTIVITY.TO-DO.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will update the description of the to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, activity, newDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><b>Step: User 1 go to an entry in the activity </b></li>
	 *<li><b>Step: User 1 update the entry</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the story of the activity.entry.updated does NOT appear in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A24D6A42B594CA5A85257A620051F667">TTT - AS - FOLLOW - ACTIVITY - 00077 - ACTIVITY.ENTRY.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateEntry_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will update the description of the entry
		String newContent = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, activity, newContent);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the update event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, newContent}, null, false);
		}
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateReply_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, add user 2 as a member</B></li>	
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.reply.updated is NOT seen in any of the I'm Following view's filters</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9097919A593ABB55852579BB005AD165">TTT -  AS - FOLLOW - ACTIVITY - 00065 - ACTIVITY.REPLY.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateReply_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity, post a comment to the entry and will then edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, comment, false, commentEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription().trim(), commentEdit}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PrivateActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><b>Step: User 1 go to a completed todo in the activity</b></li>
	 *<li><b>Step: User 1 reopen the completed todo </b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.reopened story appears in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E8EA24594D5934A285257A63002F67F5">TTT - AS - FOLLOW - ACTIVITY - 00095 - ACTIVITY.TO-DO.REOPENED - STANDALONE PRIVATE ACTIVITY</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ReopenTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 1 will now create a to-do in the activity, mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}