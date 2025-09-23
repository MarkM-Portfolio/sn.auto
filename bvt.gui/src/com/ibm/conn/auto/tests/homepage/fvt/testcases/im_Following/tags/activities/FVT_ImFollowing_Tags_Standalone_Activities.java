package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.activities;

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
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 04/2015
 */

public class FVT_ImFollowing_Tags_Standalone_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterTags };

	private Activity privateActivity, privateActivityWithTag;
	private APIActivitiesHandler activitiesAPIUser2;
	private BaseActivity baseActivity, baseActivityWithTag;
	private String tagToFollow;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		// Create the tag to be followed by User 1
		tagToFollow = Data.getData().commonTag + Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 creates a private activity without using any custom tag
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivity(testUser2, activitiesAPIUser2, baseActivity, isOnPremise);
		
		// User 2 creates a private activity with the tag being followed by User 1
		baseActivityWithTag = ActivityBaseBuilder.buildBaseActivityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), tagToFollow, true);
		privateActivityWithTag = ActivityEvents.createActivity(testUser2, activitiesAPIUser2, baseActivityWithTag, isOnPremise);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the activity created during the test
		activitiesAPIUser2.deleteActivity(privateActivity);
		activitiesAPIUser2.deleteActivity(privateActivityWithTag);
	}
	
	/**
	* <li><B>Name:</B> test_Tags_CreatePrivateActivity_Standalone Activity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to Activities</B></li>
	* <li><B>Step: testUser 2 start a private Activity and add the Tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify: Verify that the activity.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7CC7DADD855416428525793D004C455A">TTT - AS - FOLLOW - TAG - ACTIVITY - 00012 - activity.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* </ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_Tags_CreateActivity_StandaloneActivity(){

		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivityWithTag.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivityWithTag.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
				
	/**
	* <li><B>Name:</B> test_Tags_CreateEntry_PrivateActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a private activity</B></li>
	* <li><B>Step: testUser 2 add an entry with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B31D34708EC4C016852578FC0047CB41">TTT - AS - FOLLOW - TAG - ACTIVITY - 00022 - activity.entry.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* </ul>@author Srinivas Vechha
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreateEntry_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create an activity entry with the tag being followed by User 1
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		ActivityEvents.createActivityEntry(testUser2, activitiesAPIUser2, baseActEntry, privateActivity);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		//outline event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}	

	/**
	* <li><B>Name:</B> test_Tags_CreateTodo_PrivateActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to Activities</B></li>
	* <li><B>Step: testUser 2 go to the private activity and add a todo with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/29EEC5BC88DFD591852578FC004AE520">TTT - AS - FOLLOW - TAG - ACTIVITY - 00034 - activity.todo.created - STANDALONE PRIVATE ACTIVITY</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create a to-do item with the tag being followed by User 1
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		ActivityEvents.createTodo(testUser2, activitiesAPIUser2, baseTodo, privateActivity);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
				
	/**
	* <li><B>Name:</B> test_Tags_CompleteTodo_PrivateActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a private activity and for a to-do that contains the tag that User 1 is following, mark the to-do as complete</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.completed story does not appear in (1) the top level, (2) theTags filter, and (3) the specific <tag> subfilter</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A805A43649DCD3EA852579AC004A9A43">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00062 - activity.todo.completed - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CompleteTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create a to-do item with the tag being followed by User 1 and mark it as complete
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		ActivityEvents.createTodoAndMarkAsCompleted(testUser2, activitiesAPIUser2, baseTodo, privateActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();									
	}		
				
	/**
	* <li><B>Name:</B> test_Tags_Update_ActivityEntry_PrivateActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a private activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 1 used</B></li>
	* <li><B>Step: testUser 2 go to an activity entry that you have tagged with the tag User 2 is following</B></li>
	* <li><B>Step: testUser 2 update the entry</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DDC805EB71076A2B85257A640049A2A9">TTT -  AS - Follow - Tag - Activity - 00085 - activity.entry.updated - standalone private activity (neg)</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_Update_ActivityEntry_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create an activity entry with the tag being followed by User 1 and edit the entry
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createEntryAndEditDescription(testUser2, activitiesAPIUser2, baseActEntry, privateActivity, editedDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
				
	/**
	* <li><B>Name:</B> test_Tags_UpdateToDo_PrivateActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a private activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to an activity todo that you have tagged with the tag User 2 is following</B></li>
	* <li><B>Step: testUser 2 update the to-do</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD7E96AEEB77797785257A640049B5A5">TTT - AS - Follow - Tag - Activity - 00095 - activity.todo.updated - standalone private activity (neg)</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_UpdateToDo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create a to-do item with the tag being followed by User 1 and edit the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createTodoAndEditDescription(testUser2, activitiesAPIUser2, baseTodo, privateActivity, editedDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
				
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PrivateActivity_Standalone()</li>
	 * <li><B>Step: testUser 2 log into a private activity you own</B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 1 used</B></li>
	 * <li><B>Step: testUser 2 go to a completed to-do that you have tagged with the tag User 2 is following</B></li>
	 * <li><B>Step: testUser 2 reopen the to-do </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story does NOT appear in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/99CA50243E4FDDF885257A640049C60A">TTT - AS - Follow - Tag - Activity - 00105 - activity.todo.reopened - standalone private activity (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tag_ReopenTodo_PrivateActivity_Standalone(){

		String testName = ui.startTest();

		// User 2 create a to-do item with the tag being followed by User 1, mark it as complete and reopen it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), privateActivity, tagToFollow, false);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser2, activitiesAPIUser2, baseTodo, privateActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}			
}