package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.activities;

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
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

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

public class FVT_Discover_StandalonePublic_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };

	private Activity publicActivity;
	private ActivityEntry privateEntry, publicEntry;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseActivityEntry basePrivateEntry, basePublicEntry;
	private BaseActivityToDo basePrivateTodo, basePublicTodo;
	private Reply privateReply, publicReply;
	private Todo privateTodo, publicTodo;
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

		// User 1 creates a public activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// Set all other relevant global test components to null
		privateEntry = null;
		publicEntry = null;
		privateTodo = null;
		publicTodo = null;
		privateReply = null;
		publicReply = null;
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_CreatePublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Create a new public activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity created is not seen - negative test</B></li>
	* <li>There is no TTT link for this test - it is a public activity version of the following private activity test case: "Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2ECE6CF25C594636852578760079E71F"</li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CreatePublicActivity_Standalone() {

		ui.startTest();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent}, TEST_FILTERS, false);
		
		ui.endTest();	
	}

	/**
	* test_CreateEntry_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a new entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E5DFE45B8F89651B852578760079E723">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateEntry_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, basePublicEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}

	/**
	* test_CreatePrivateEntry_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a new entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E89B06695DFA3B41852578760079E728">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_CreatePrivateEntry_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private entry to the activity
		createPrivateEntryIfRequired();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createPrivateEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, basePrivateEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createPrivateEntryEvent, basePrivateEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a new todo in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D728BEA3AAAD3487852578760079E731">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void test_CreateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePublicTodo.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	* test_CreatePrivateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a new todo in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E10FB0E28E3ADB86852578760079E736">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void test_CreatePrivateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);

		// Create the event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePrivateTodo.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_UpdateEntry_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 go to a public activity you own</B></li>
	*<li><B>Step: User 1 go to a public entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All - verification point</b></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that the activity.entry.updated story is shown in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/315DFB1ECCA2BA2B85257A620044402B">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 6)
	public void test_UpdateEntry_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicEntry = ActivityEvents.editEntryDescription(testUser1, activitiesAPIUser1, publicEntry, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();	
	}

	/**
	* test_UpdatePrivateEntry_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 go to a public activity you own</B></li>
	*<li><B>Step: User 1 go to a private entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All - verification point</b></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that there is no story in the views of the activity.entry.updated</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0AF6587EAC3547DF85257A62004441DB">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY/PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 7)
	public void test_UpdatePrivateEntry_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private entry to the activity
		createPrivateEntryIfRequired();
				
		// User 1 will now edit the description of the private entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		privateEntry = ActivityEvents.editEntryDescription(testUser1, activitiesAPIUser1, privateEntry, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, basePrivateEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public activity you own</B></li>
	*<li><B>Step: User 1 go to a todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All - verification point</B></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story appears in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F5FB1F87FCADF23485257A630039DBDE">TTT - DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 8)
	public void test_UpdateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now edit the description of the public to-do item
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicTodo = ActivityEvents.editTodoDescription(testUser1, activitiesAPIUser1, publicTodo, editedDescription);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_UpdatePrivateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public activity you own</B></li>
	*<li><B>Step: User 1 go to a private todo in the activity</B></li>
	*<li><B>Step: User 1 update the private todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All - verification point</B></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F7765F79579E68B585257A630039DF12">TTT - DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 9)
	public void test_UpdatePrivateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();

		// User 1 will now edit the description of the private to-do item
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		privateTodo = ActivityEvents.editTodoDescription(testUser1, activitiesAPIUser1, privateTodo, editedDescription);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateEntryPrivateComment_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a comment on an entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5464005A725E9EC6852578760079E72F">TTT - DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PUBLIC ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 10)
	public void test_CreateEntryPrivateComment_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a private comment to the public entry
		createPrivateReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, privateReply.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_UpdatePrivateReply_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Update an existing comment on an entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><b>Step: Go to Home \ Activity Stream \ Discover</b></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FA73A1471A77AD8A852579BC005815FE">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 11)
	public void test_UpdatePrivateReply_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a private comment to the public entry
		createPrivateReplyIfRequired();
				
		// User 1 will now edit the private comment on the public entry
		String originalReplyContent = privateReply.getContent().trim();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.editComment(testUser1, activitiesAPIUser1, privateReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateEntryCommentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryCommentEvent, originalReplyContent, user1EditedComment}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateEntryComment_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A14700E1CDD67414852578760079E729">TTT - DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 12)
	public void test_CreateEntryComment_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, publicEntry.getContent().trim(), publicReply.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_UpdateReply_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Update an existing comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><b>Step: Go to Home \ Activity Stream \ Discover</b></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/01163649AA353E6E852579BC0057EB4B">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 13)
	public void test_UpdateReply_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
						
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
		
		// User 1 will now edit the public comment on the public entry
		String originalReplyContent = publicReply.getContent().trim();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.editComment(testUser1, activitiesAPIUser1, publicReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentOnEntryEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, publicEntry.getContent().trim(), user1EditedComment}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentOnEntryEvent, originalReplyContent}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_CompleteTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of and contains todo items in this activity</B></li>
	*<li><B>Step: Mark the todo as completed</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.completed is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/67FC815B73AD4B42852578760079E739">TTT - DISC - ACTIVITY - 00050 - ACTIVITY.TO-DO.COMPLETED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 14)
	public void test_CompleteTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now mark the public to-do item as completed
		publicTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	* test_ReopenTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activity</B></li>
	*<li><B>Step: User 1 go to a public activity </B></li>
	*<li><B>Step: User 1 go to a completed public todo</B></li>
	*<li><B>Step: User 1 reopen the complete public todo</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All - verification point</b></li>
	*<li><B>Step: User 2 log into Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that there is a story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C61889620A210B0185257A620049367F">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 15)
	public void test_ReopenTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		if(publicTodo.isComplete() == false) {
			// User 1 will now mark the public to-do item as completed
			publicTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		}
		// User 1 will now re-open the public to-do item again
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, false);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_CompletePrivateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of and contains private todo items in this activity</B></li>
	*<li><B>Step: Mark the private todo as completed</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.completed is not seen - negative test</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_CompleteTodo_PublicActivity_Standalone() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 16)
	public void test_CompletePrivateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();
				
		// User 1 will now mark the private to-do item as completed
		privateTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, privateTodo, true);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, privateTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ReopenPrivateTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activity</B></li>
	*<li><B>Step: User 1 go to a public activity </B></li>
	*<li><B>Step: User 1 go to a completed private todo</B></li>
	*<li><B>Step: User 1 reopen the complete private todo</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All - verification point</b></li>
	*<li><B>Step: User 2 log into Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that there is no story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/694D06AE458A2FC785257A6200493830">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - STANDALONE PUBLIC ACTIVITY / PRIAVTE TO-DO (NEG)</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 17)
	public void test_ReopenPrivateTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();
		
		if(privateTodo.isComplete() == false) {
			// User 1 will now mark the private to-do item as completed
			privateTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, privateTodo, true);
		}
		// User 1 will now re-open the private to-do item again
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, privateTodo, false);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, privateTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateAssignedTodo_PublicActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a public activity that you are a member of</B></li>
	*<li><B>Step: Create a new todo in this activity and assign this to a member of the activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Homepage / Discover / Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/17DB674CB6E4FF14852578760079E738">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - STANDALONE PUBLIC ACTIVITY / ASSIGNED</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 18)
	public void test_CreateAssignedTodo_PublicActivity_Standalone() {

		ui.startTest();
		
		// User 1 will now add User 2 to the activity as a member
		ActivityEvents.addMemberSingleUser(publicActivity, testUser1, activitiesAPIUser1, testUser2);
		
		// User 1 will now assign User 2 to the public to-do item
		ActivityEvents.assignTodoItemToUser(publicTodo, testUser1, activitiesAPIUser1, profilesAPIUser2);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	private void createPublicEntryIfRequired() {
		if(publicEntry == null) {
			// User 1 will now add a public entry to the activity
			basePublicEntry = ActivityBaseBuilder.buildBaseActivityEntry(getClass().getSimpleName() + Helper.genStrongRand(), publicActivity, false);
			publicEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, basePublicEntry, publicActivity);
		}
	}
	
	private void createPrivateEntryIfRequired() {
		if(privateEntry == null) {
			// User 1 will now add a private entry to the activity
			basePrivateEntry = ActivityBaseBuilder.buildBaseActivityEntry(getClass().getSimpleName() + Helper.genStrongRand(), publicActivity, true);
			privateEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, basePrivateEntry, publicActivity);
		}
	}
	
	private void createPublicTodoIfRequired() {
		if(publicTodo == null) {
			// User 1 will now add a public to-do item to the activity
			basePublicTodo = ActivityBaseBuilder.buildBaseActivityToDo(getClass().getSimpleName() + Helper.genStrongRand(), publicActivity, false);
			publicTodo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, basePublicTodo, publicActivity);
		}
	}
	
	private void createPrivateTodoIfRequired() {
		if(privateTodo == null) {
			// User 1 will now add a private to-do item to the community
			basePrivateTodo = ActivityBaseBuilder.buildBaseActivityToDo(getClass().getSimpleName() + Helper.genStrongRand(), publicActivity, true);
			privateTodo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, basePrivateTodo, publicActivity);
		}
	}
	
	private void createPublicReplyIfRequired() {
		if(publicReply == null) {
			// User 1 will now add a public comment to the public entry
			String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
			publicReply = ActivityEvents.createComment(publicActivity, publicEntry, null, user1Comment, testUser1, activitiesAPIUser1, false);
		}
	}
	
	private void createPrivateReplyIfRequired() {
		if(privateReply == null) {
			// User 1 will now add a private comment to the public entry
			String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
			privateReply = ActivityEvents.createComment(publicActivity, publicEntry, null, user1Comment, testUser1, activitiesAPIUser1, true);
		}	
	}
}