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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
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
/* Copyright IBM Corp. 2016		                                     */
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

public class FVT_ImFollowing_StandalonePublic_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };

	private Activity activity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
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
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 will now create a public activity with User 2 added as a member and a follower
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false); 
		activity = ActivityEvents.createActivityWithOneMemberAndOneFollower(baseActivity, testUser1, activitiesAPIUser1, testUser2, activitiesAPIUser2, isOnPremise);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(activity);
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.created is not seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/463EE780A440C898852578760079E8AC">TTT - AS - FOLLOW - ACTIVITY - 00014 - ACTIVITY.CREATED - STANDALONE ACTIVITY (NEG)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CreatePublicActivity_Standalone() {
		
		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create activity event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntry_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9F798133D99E26BD852578760079E8B1">TTT - AS - FOLLOW - ACTIVITY - 00026 - ACTIVITY.ENTRY.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	 * @author Hugh Caren 
	 */	
	@Test (groups =  {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateEntry_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a public activity entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateEntry_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity entry as user 1 but with private visibility</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is not seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3ECEC6B52DF3B8BA852578760079E8B0">TTT - AS - FOLLOW - ACTIVITY - 00027 - ACTIVITY.ENTRY.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreatePrivateEntry_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a private entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, true);
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, activity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryComment_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a comment on an activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7F2C29A21333FAE0852578760079E8B8">TTT -  AS - FOLLOW - ACTIVITY - 00036 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateEntryComment_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a public activity entry and add a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, comment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription().trim(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryPrivateComment_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a comment on an activity entry as user 1 - mark the comment private</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is not seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/85D0B5822EBE5974852578760079E8B7">TTT - AS - FOLLOW - ACTIVITY - 00037 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateEntryPrivateComment_PublicActivity_Standalone() {

		String testName = ui.startTest();
		
		// User 1 create a public activity entry and add a private comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, comment, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B32E957F2E301F16852578760079E8C3">TTT - AS - FOLLOW - ACTIVITY - 00046 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, false);
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
	 *<li><B>Name: test_CreateAssignedTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity todo as user 1 and assign it to user 2</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C45615F938E9D350852578760079E8C4">TTT - AS - FOLLOW - ACTIVITY - 00047 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY / ASSIGNED</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateAssignedTodo_PublicActivity_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 create a to-do item and assign it to User 2
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(activity, baseTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified - because the API creates and assigns the to-do in separate steps, an update event will be shown here
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity todo as user 1 and mark the todo as private</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is not seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3FC18A5D797F9B0852578760079E8C5">TTT - AS - FOLLOW - ACTIVITY - 00048 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreatePrivateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a private to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, true);
		ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseTodo, activity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the to-do item creation event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CompleteTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Mark the todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.completed is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E339C99871F578FC852578760079E8CB">TTT -  AS - FOLLOW - ACTIVITY - 00056 - ACTIVITY.TO-DO.COMPLETED - STANDALONE PUBLIC ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CompleteTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a to-do item and complete it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, false);
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
	 *<li><B>Name: test_CompletePrivateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Activities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new private todo as user 1</b></li>
	 *<li><b>Step: Mark the private todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.completed is NOT seen in the Activities view</B></li>
	 *<li><b>No TTT link for this test case - it is the same as the above test case except for it is using a private to-do item instead of a public to-do item</b></li> 
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CompletePrivateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a private to-do item and complete it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, true);
		ActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseTodo, activity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><B>Step: User 1 go to a todo in the activity</B></li>
	 *<li><b>Step: User 1 update the todo in the activity</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.updated story does appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AE0D1B54466F388085257A630036927D">TTT - AS - FOLLOW - ACTIVITY - 00086 - ACTIVITY.TO-DO.UPDATED - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a to-do item and edit it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, false);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, activity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><B>Step: User 1 go to a private todo in the activity</B></li>
	 *<li><b>Step: User 1 update the private todo in the activity</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D0CDA167CE97E28A85257A6300369520">TTT - AS - FOLLOW - ACTIVITY - 00088 - ACTIVITY.TO-DO.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdatePrivateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a private to-do item and edit it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, true);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, activity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><B>Step: User 1 go to a public entry in the activity</B></li>
	 *<li><b>Step: User 1 update the entry</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the story of the activity.entry.updated does NOT appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C9B83F649E87333685257A620051F346">TTT - AS - FOLLOW - ACTIVITY - 00075 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateEntry_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a public activity entry and edit it
		String editDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, activity, editDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String nonEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the update entry event and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent, editDescription}, null, false);
		
		ui.endTest();	
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateEntry_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>	
	 *<li><B>Step: User 1 go to a private entry in the activity</B></li>
	 *<li><b>Step: User 1 update the private entry</b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the story of the activity.entry.updated does NOT appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D9CBC7863D388F9085257A620051F531">TTT - AS - FOLLOW - ACTIVITY - 00076 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdatePrivateEntry_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a private activity entry and edit it
		String editDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, true);
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, activity, editDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String updateEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the update entry event and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEvent, editDescription}, null, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdateReply_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Actities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.reply.updated is NOT seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC05E9C7A72AA325852579BB005B132B">TTT - AS - FOLLOW - ACTIVITY - 00066 - ACTIVITY.REPLY.UPDATED - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateReply_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a public activity entry, add a comment and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, comment, false, commentEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign events to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String commentUpdateEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and edited comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, commentEdit}, filter, true);
			
			// Verify that the update event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentUpdateEvent, comment}, null, false);
		}
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateReply_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Actities as user 1</B></li>
	 *<li><B>Step: Create a new activity as user 1, mark it public and add user 2 as a member</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this activity</B></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1, but with private visibility</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #1</b></li>
	 *<li><B>Verify: Verify that the news story for activity.reply.updated is NOT seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/946633F78A6D7ECF852579BB005B5305">TTT - AS - FOLLOW - ACTIVITY - 00067 - ACTIVITY.REPLY.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG SC NOV)</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdatePrivateReply_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a public activity entry, add a private comment and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, activity, comment, true, commentEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign events to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String commentUpdateEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event, update comment event, original comment and edited comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, comment, commentUpdateEvent, commentEdit}, TEST_FILTERS, false);
		
		ui.endTest();		
	}

	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the public activity</B></li>	
	 *<li><B>Step: User 1 go to a public completed todo in the activity</B></li>
	 *<li><b>Step: User 1 reopen the public completed todo </b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.reopened story appears in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3FED530E0C6BD7D385257A63002F696F">TTT - AS - FOLLOW - ACTIVITY - 00096 - ACTIVITY.TO-DO.REOPENED - STANDALONE PUBLIC ACTIVITY</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_ReopenTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a to-do item, complete it and then reopen it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, false);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, activity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_ReopenPrivateTodo_PublicActivity_Standalone()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public activity you own</B></li>
	 *<li><B>Step: User 2 ensure you are following the public activity</B></li>	
	 *<li><B>Step: User 1 go to a private completed todo in the activity</B></li>
	 *<li><b>Step: User 1 reopen the private completed todo </b></li>
	 *<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Activities - verification point</b></li>
	 *<li><B>Verify: Verify that the activity.todo.reopened story does NOT appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/999F4A252F38D2CE85257A63002F6D52">TTT - AS - FOLLOW - ACTIVITY - 00098 - ACTIVITY.TO-DO.REOPENED - STANDALONE PUBLIC ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	 * @author Hugh Caren hughcare@ie.ibm.com
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_ReopenPrivateTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();

		// User 1 create a to-do item, complete it and then reopen it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), activity, true);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, activity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
}