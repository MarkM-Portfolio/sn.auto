package  com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.activities;

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

public class FVT_ImFollowing_People_Private_Standalone_Activities extends SetUpMethodsFVT {
	
	private String TEST_FILTERS[];
	
	private Activity privateActivity, privateActivityToBeMadePublic, privateActivityWithMember;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity, baseActivityToBeMadePublic, baseActivityWithMember;
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
		
		// User 1 creates a private activity with no members
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// User 1 creates a private activity which will later be made public
		baseActivityToBeMadePublic = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivityToBeMadePublic = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivityToBeMadePublic, isOnPremise);
		
		// User 1 creates a private activity with User 2 added as a member
		baseActivityWithMember = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivityWithMember = ActivityEvents.createActivityWithOneMember(baseActivityWithMember, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// Remove all of the activities created during the tests
		activitiesAPIUser1.deleteActivity(privateActivity);
		activitiesAPIUser1.deleteActivity(privateActivityToBeMadePublic);
		activitiesAPIUser1.deleteActivity(privateActivityWithMember);
	}
	
	/**
	*<ul>
	*<li><B>Name: createActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into connection and Follow User 2</B></li>
	*<li><B>Step: User 2 that is being followed log into Connections</B></li>
	*<li><B>Step: User 2 create an activity</B></li>
	*<li><B>Step: User 1 go to Homepage / I'm Following / People</B></li>
	*<li><B>Step: User 1 go to Homepage / I'm Following / Activities</B></li>
	*<li><B>Verify: Verify that the story activity.created is NOT displayed in the People filter</B></li>
	*<li><b>Verify: Verify that the story activity.created is NOT displayed in the Activities filter</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAA97FCF3EB18910852578FB002BB542">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00011 - activity.created - STANDALONE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void createActivity(){

		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createEntry()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, add User 2 as a member</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a new activity entry as User 1</B></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story activity.entry.created is NOT displayed in the People or Activities view</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D98A6FADC85BB408852578FB002C84A9">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00022 - activity.entry.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createEntry(){

		String testName = ui.startTest();

		// User 1 create a private entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivityWithMember, false);
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivityWithMember);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, add User 2 as a member</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a comment on an activity entry as User 1</B></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify the story for activity.entry.comment.created is NOT displayed in the People or Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/94717AE53E45029C852578FB00304A3A">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00033 - activity.entry.comment.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createComment(){

		String testName = ui.startTest();

		// User 1 create an activity entry and add a comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivityWithMember, false);
		ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivityWithMember, comment, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommentEvent, baseActivityEntry.getDescription().trim(), comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createTodo()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, add User 2 as a member</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a todo within the activity entry as User 1</B></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify that the story activity.todo.created is NOT displayed within People or Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E1E1ACC3D75AA4E9852578FB0036F7A3">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00044 - activity.todo.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createTodo(){

		String testName = ui.startTest();

		// User 1 create a to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivityWithMember, false);
		ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, privateActivityWithMember);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: createTodo()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, add User 2 as a member</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a new activity todo as User 1</B></li>
	*<li><b>Step: Mark the todo as complete as User 1</b></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><b>Step: Go to Homepage \ All Updates \ People</b></li>
	*<li><b>Step: Go to Homepage \ All Updates \ Activities</b></li>
	*<li><B>Verify: Verify that the story activity.todo.completed is NOT displayed in the people and activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0BB377252F03AC17852578FB00390136">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00052 - activity.todo.completed - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void completeTodo(){

		String testName = ui.startTest();

		// User 1 create a to-do item and complete it
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivityWithMember, false);
		ActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, privateActivityWithMember);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: changeAccess()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Connections</B></li>
	*<li><B>Step: User 1 follow User 2</B></li>
	*<li><B>Step: User 2 that is being followed log into Connections</B></li>
	*<li><B>Step: User 2 make a private activity (that is already there) public</B></li>
	*<li><b>Step: User 1 go to Homepage \ Updates \ I'm Following \ All, People & Activities</b></li>
	*<li><B>Verify: Verify that the activity.access.public story is displayed in All, People and Activities views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E21555E43D3FF099852578FB003905CC">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00061 - activity.access.public - STANDALONE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void changeAccess(){

		ui.startTest();
		
		// User 1 will now mark the activity as public using the UI
		ActivityEvents.loginAndNavigateToActivityAndMakeActivityPublic(ui, privateActivityToBeMadePublic, testUser1, isOnPremise, false);
		
		// User 1 return to Home and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Create the news story to be verified
		String makePublicEvent = ActivityNewsStories.getMakeActivityPublicNewsStory(ui, baseActivityToBeMadePublic.getName(), testUser1.getDisplayName());
		
		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{makePublicEvent, baseActivityToBeMadePublic.getGoal()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: updateComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as user 1</B></li>
	*<li><B>Step: Create a new activity as User 1, add User 2 as a member</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Update an existing comment on an activity entry as User 1</B></li>
	*<li><B>Step: Log in to Home as User 2</B></li>
	*<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & People</b></li>
	*<li><B>Verify: Verify the story for activity.reply.updated is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/96A0F417352AB2C7852579BF0043F591">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00073 - activity.reply.updated - STANDALONE PRIVATE ACTIVITY (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateComment(){

		String testName = ui.startTest();

		// User 1 create an activity entry, add a comment and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivityWithMember, false);
		ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivityWithMember, comment, false, commentEdit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());
		String updateCommentEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivityWithMember.getName(), testUser1.getDisplayName());

		// Verify that the comment event, update comment event, original comment and updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, comment, commentEdit}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: updateEntry()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private activity you own</B></li>
	*<li><B>Step: User 2 ensure you are following User 1</B></li>
	*<li><B>Step: User 1 go to an entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry</B></li>
	*<li><b>Step: User 2 log into Home / I'm Following / All, Activities & People</b></li>
	*<li><B>Verify: Verify that the activity.entry.updated story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1A62453C7028DC1085257A630032EAB8">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00085 - ACTIVITY.ENTRY.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG) (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateEntry(){

		String testName = ui.startTest();

		// User 1 create a public activity entry and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity, editedDescription);

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
	*<ul>
	*<li><B>Name: updateTodo()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private activity you own</B></li>
	*<li><B>Step: User 2 ensure you are following User 1</B></li>
	*<li><B>Step: User 1 go to a todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><b>Step: User 2 log into Home / I'm Following / All, Activities & People</b></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/848D7D356631D63E85257A6400334C1F">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00095 - ACTIVITY.Todo.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG) (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateTodo(){

		String testName = ui.startTest();

		// User 1 create a to-do item and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, privateActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: reopenTodo()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private activity you own</B></li>
	*<li><B>Step: User 2 ensure you are following User 1</B></li>
	*<li><B>Step: User 1 go to a completed todo in the activity</B></li>
	*<li><B>Step: User 1 reopen the todo</B></li>
	*<li><b>Step: User 2 log into Home / I'm Following / All - verification point</b></li>
	*<li><b>Step: User 2 log into Home / I'm Following / Activities - verification point</b></li>
	*<li><b>Step: User 2 log into Home / I'm Following / People - verification point</b></li>
	*<li><B>Verify: Verify that the activity.todo.reopened story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B3F9DA8B84F9EFF685257A640039BCF5">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00105 - ACTIVITY.Todo.REOPENED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void reopenTodo(){

		String testName = ui.startTest();

		// User 1 create a to-do item, complete it and then reopen it
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, privateActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: entryMentionsComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as User 1</B></li>
	*<li><B>Step: Create a new private activity as User 1</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a comment to activity entry as User 1 mentioning User 3</B></li>
	*<li><b>Step: Log in to Home as User 2</b></li>
	*<li><b>Step: Go to Homepage \ Updates \ I'm Following \ All, Activities & People</b></li>
	*<li><B>Verify: Verify the mentions event does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/082ED19B19268C0685257C6D005A09A7">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00123 - Mentions added in Entry Comment - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void entryMentionsComment(){

		String testName = ui.startTest();

		// User 1 create an activity entry and add a comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityEntry, privateActivity, mention, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: todoMentionsComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities as User 1</B></li>
	*<li><B>Step: Create a new private activity as User 1</B></li>
	*<li><B>Step: Have user 2 FOLLOW User 1</B></li>
	*<li><B>Step: Create a comment to activity todo as User 1 mentioning User 3</B></li>
	*<li><b>Step: Log in to Home as User 2</b></li>
	*<li><b>Step: Go to Homepage \ Updates \ I'm Following \ All, Activities & People</b></li>
	*<li><B>Verify: Verify the mentions event does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/10FEB586B46D741185257C6D005B2FBE">TTT -  AS - FOLLOW - PERSON - ACTIVITY - 00133 - Mentions added in ToDo Comment - STANDALONE PRIVATE ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void todoMentionsComment(){

		String testName = ui.startTest();

		// User 1 create a to-do item and add a comment with mentions to User 3
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), privateActivity, false);
		ActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser2, activitiesAPIUser1, baseActivityTodo, privateActivity, mention, false);
		
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