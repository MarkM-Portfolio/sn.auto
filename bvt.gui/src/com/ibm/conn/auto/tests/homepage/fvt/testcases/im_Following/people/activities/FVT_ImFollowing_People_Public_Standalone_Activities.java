package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.activities;

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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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

public class FVT_ImFollowing_People_Public_Standalone_Activities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	
	private Activity publicActivity, publicActivityWithMember;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity, baseActivityWithMember;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);

		// User 2 will follow User 1 through the API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[3];
			TEST_FILTERS[2] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[2];
		}
		
		// Add the commonly used filters to the TEST_FILTERS array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterActivities;
		
		// User 1 creates a public activity with no members
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// User 1 creates a public activity with User 2 added as a member
		baseActivityWithMember = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivityWithMember = ActivityEvents.createActivityWithOneMember(baseActivityWithMember, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// Remove all of the activities created during the tests
		activitiesAPIUser1.deleteActivity(publicActivity);
		activitiesAPIUser1.deleteActivity(publicActivityWithMember);
	}
	
	/**
	*<ul>
	*<li><B>Name: createEntry_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a public activity</B></li>
	*<li><B>Step: Log in as User 2 and Follow User 1</B></li>
	*<li><B>Step: Log in as User 1</B></li>
	*<li><B>Step: Add an entry to the Activity already created</B></li>
	*<li><b>Step: Log in to Home as user 2 who is following User 1</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story activity.entry.created is displayed in the Filters People and Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2E61386E6CDCA79F852578FB002C7CD8">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00021 - activity.entry.created - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createEntry_PublicActivity(){
		
		String testName = ui.startTest();

		// User 1 create a public activity entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createPrivateEntry_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, mark it public and add User 2 as a member</B></li>
	*<li><B>Step: Have User 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a new activity entry as User 1, but with private visibility</B></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story activity.entry.created is not displayed in the Filters People or Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/202A8B68E0DD7BAD852578FB002C85F9">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00023 - activity.entry.created - STANDALONE PUBLIC ACTIVITY - PRIVATE ENTRY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createPrivateEntry_PublicActivity() {

		String testName = ui.startTest();

		// User 1 create a private activity entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, true);
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String createActivityEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createComment_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, mark it public and add User 2 as a member</B></li>
	*<li><B>Step: Have User 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a comment on an activity entry as User 1</B></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story for activity.entry.comment.created is displayed in the People and activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2A774252108E1E07852578FB00304784">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00031 - activity.entry.comment.created - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and add a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, comment, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommentEvent, baseActivityEntry.getDescription().trim(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createPrivateComment_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, mark it public and add User 2 as a member</B></li>
	*<li><B>Step: Have User 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a comment on an activity entry as User 1 - mark the comment private</B></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story for activity.entry.comment.created is NOT displayed in the People or Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/97C738E7A5CA62BF852578FB003048F3">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00032 - activity.entry.comment.created - STANDALONE PUBLIC ACTIVITY - PRIVATE COMMENT</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createPrivateComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and add a private comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, comment, true);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommentEvent, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createTodo_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, mark it public and add User 2 as a member</B></li>
	*<li><B>Step: Have User 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a todo with the activity entry as User 1</B></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify that the story activity.todo.created is displayed in People and Activities views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5CBCE3957A9F1BD8852578FB0036F3B3">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00041 - activity.todo.created - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createPrivateTodo_PublicActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, mark it public and add User 2 as a member</B></li>
	*<li><B>Step: Have User 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a todo with the activity entry as User 1 - mark the todo as private</B></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify that the story activity.todo.created is NOT displayed in the People and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E45D5C8000D9706A852578FB0036F672">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00043 - activity.todo.created - STANDALONE PUBLIC ACTIVITY - PRIVATE Todo</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createPrivateTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a private to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, true);
		ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* createTodo_Assigned
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new activity as user 1, mark it public and add user 2 as a member</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a todo within the activity entry and assign User 2 to the todo as User 1</B></li>
	* <li><b>5. Log in to Home as User 2</b></li>
	* <li><b>6. Go to Homepage \ All Updates \ People</b></li>
	* <li><b>7. Go to Homepage \ All Updates \ Activities</b></li>
	* <li><b>8. Go to Homepage \ Action Required \ Activities</b></li>
	* <li><b>Verify: Verify that the story activity.todo.created is displayed in the people and activities view</b></li>
	* <li><b>Verify: Verify that the story activity.todo.created is displayed in the action required view</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/139223C76C432F04852578FB0036F525">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00042 - activity.todo.created - STANDALONE PUBLIC ACTIVITY - ASSIGNED</a></li>
	* </ul>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void createTodo_Assigned() {

		String testName = ui.startTest();
		
		/**
		 * User 1 create a to-do item and assign it to testUser2
		 * This will be done via the UI since creating and assigning the to-do item simultaneously is not possible using the API methods
		 */
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivityWithMember, false);
		ActivityEvents.loginAndNavigateToActivityAndCreateAssignedTodo(ui, driver, publicActivityWithMember, baseActivityTodo, testUser1, testUser2, false);
		
		// Navigate to Home and log out
		LoginEvents.gotoHomeAndLogout(ui);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Create the news story to be verified
		String assignedTodoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());
		
		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		// Navigate to the Action Required view
		UIEvents.gotoActionRequired(ui);
		
		// Verify the news story does appear in all views
		String ACTION_REQUIRED_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{assignedTodoEvent, baseActivityTodo.getDescription()}, ACTION_REQUIRED_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	* completeTodo_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new activity as user 1, mark it public and add user 2 as a member</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a new activity todo as User 1</B></li>
	* <li><b>5. Mark the todo as complete as User 1</b></li>
	* <li><b>6. Log in to Home as user 2</b></li>
	* <li><b>7. Go to Homepage \ All Updates \ People</b></li>
	* <li><b>8. Go to Homepage \ All Updates \ Activities</b></li>
	* <li><b>Verify: Verify that the story for activity.todo.completed is displayed in the people and activities view</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/20A01981BE66DD57852578FB0038FFC7">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00051 - activity.todo.completed - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void completeTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item and complete it
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(),baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* updateComment_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new activity as user 1, mark it public and add user 2 as a member</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Update an existing comment on an activity entry as User 1</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the story for activity.reply.updated is NOT displayed in any view</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4FD5DEF1A0EC2ECE852579BF00437C49">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00071 - activity.reply.updated - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updateComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry, add a comment and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, comment, false, commentEdit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, commentEdit}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* updatePrivateComment_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new activity as user 1, mark it public and add user 2 as a member</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Update an existing comment on an activity entry as User 1 - mark the comment private</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the story for activity.reply.updated is NOT displayed in any view</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E569E3024C5B45FA852579BF0043C72A">TTT -   AS - FOLLOW - PERSON - ACTIVITY - 00072 - activity.reply.updated - STANDALONE PUBLIC ACTIVITY - PRIVATE COMMENT (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatePrivateComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry, add a private comment and edit the private comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, comment, true, commentEdit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the comment event, update comment event, original comment and updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, comment, commentEdit}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* updateEntry_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 go to a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to an entry in the activity</B></li>
	* <li><B>4. User 1 update the entry</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All, Activities & People</b></li>
	* <li><b>Verify: Verify that the activity.entry.updated story does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CFB6D2CEEADD8DA985257A630032EC50">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00086 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updateEntry_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* updatePrivateEntry_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 go to a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to a private entry in the activity</B></li>
	* <li><B>4. User 1 update the private entry</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All, Activities & People</b></li>
	* <li><b>Verify: Verify that the activity.entry.updated story does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB65C5592FD82AEF85257A630032EDBF">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00087 - ACTIVITY.ENTRY.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY (NEG) (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatePrivateEntry_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a private activity entry and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, true);
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create entry event, update entry event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, updateEntryEvent, baseActivityEntry.getDescription(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* updateTodo_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 go to a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to a todo in the activity</B></li>
	* <li><B>4. User 1 update the todo</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All, Activities & People</b></li>
	* <li><b>Verify: Verify that the activity.todo.updated story does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0BEB7FFD45EA290285257A6400335090">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00096 - ACTIVITY.Todo.UPDATED - STANDALONE PUBLIC ACTIVITY (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updateTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create to-do item event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, filter, true);
			
			// Verify that the update to-do item event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTodoEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* updatePrivateTodo_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 go to a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to a private todo in the activity</B></li>
	* <li><B>4. User 1 update the private todo</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All, Activities & People</b></li>
	* <li><b>Verify: Verify that the activity.todo.updated story does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CEE60016AD8A0F4785257A64003352AD">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00098 - ACTIVITY.Todo.UPDATED - STANDALONE PUBLIC ACTIVITY / PRIVATE Todo (NEG) (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatePrivateTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, true);
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do item event, update to-do item event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, updateTodoEvent, baseActivityTodo.getDescription(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	* reopenTodo_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 log into a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to a completed todo in the activity</B></li>
	* <li><B>4. User 1 reopen the todo</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All - verification point</b></li>
	* <li><b>6. User 2 log into Home / I'm Following / Activities - verification point</b></li>
	* <li><b>7. User 2 log into Home / I'm Following / People - verification point</b></li>
	* <li><b>Verify: Verify that the activity.todo.reopened story appears in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5824194B6F010E0385257A640039BEC5">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00106 - ACTIVITY.Todo.REOPENED - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void reopenTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item, complete it and then reopen it
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* reopenPrivateTodo_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. User 1 log into a public activity you own</B></li>
	* <li><B>2. User 2 ensure you are following User 1</B></li>
	* <li><B>3. User 1 go to a completed private todo in the activity</B></li>
	* <li><B>4. User 1 reopen the private todo</B></li>
	* <li><b>5. User 2 log into Home / I'm Following / All - verification point</b></li>
	* <li><b>6. User 2 log into Home / I'm Following / Activities - verification point</b></li>
	* <li><b>7. User 2 log into Home / I'm Following / People - verification point</b></li>
	* <li><b>Verify: Verify that the activity.todo.reopened story does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C7A22C332D410CEB85257A640039C2AA">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00108 - ACTIVITY.Todo.REOPENED - STANDALONE PUBLIC ACTIVITY / PRIVATE Todo (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void reopenPrivateTodo_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a private to-do item, complete it and then reopen it
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, true);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* mentionsEntryComment_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new public activity as User 1</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a comment to activity entry as User 1 mentioning User 3</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the mentions event does appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/35CF8F421D18A5DF85257C6D005A09A6">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00121 - Mentions added in Entry Comment - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentionsEntryComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and add a comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityEntry, publicActivity, mention, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* mentionsEntryPrivateComment_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new public activity as User 1</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a comment to activity entry as User 1 mentioning User 3 - marking the comment private</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the mentions event does NOT appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AB355D5940C65CF985257C6D005A09A8">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00122 - Mentions added in Entry Comment - STANDALONE PUBLIC ACTIVITY - PRIVATE COMMENT</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentionsEntryPrivateComment_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and add a private comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityEntry, publicActivity, mention, true);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* todoCommentMention_PublicActivity
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new public activity as User 1</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a comment to activity todo as User 1 mentioning User 3</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the mentions event does appear in the views</b></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C57E500A6A1CD4D885257C6D005B2FBD">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00131 - Mentions added in ToDo Comment - STANDALONE PUBLIC ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void todoCommentMention_PublicActivity(){

		String testName = ui.startTest();

		// User 1 create a to-do item and add a comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityTodo, publicActivity, mention, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify the news story does appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityTodo.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
		
	}
	
	/**
	* todoPrivateCommentMention
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>1. Log in to Activities as User 1</B></li>
	* <li><B>2. Create a new public activity as User 1</B></li>
	* <li><B>3. Have user 2 FOLLOW User 1</B></li>
	* <li><B>4. Create a comment to activity todo as User 1 mentioning User 3 - marking the comment private</B></li>
	* <li><b>5. Log in to Home as user 2</b></li>
	* <li><b>6. Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	* <li><b>Verify: Verify the mentions event does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D54D8B178DE3F56285257C6D005B2FBF">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00132 - Mentions added in ToDo Comment - STANDALONE PUBLIC ACTIVITY - PRIVATE COMMENT</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void todoPrivateCommentMention(){

		String testName = ui.startTest();

		// User 1 create a to-do item and add a private comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityTodo, publicActivity, mention, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityTodo.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
}