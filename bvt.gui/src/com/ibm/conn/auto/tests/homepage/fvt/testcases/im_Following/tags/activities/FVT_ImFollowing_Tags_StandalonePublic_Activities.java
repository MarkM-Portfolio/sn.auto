package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
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
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 03/2015
 */

public class FVT_ImFollowing_Tags_StandalonePublic_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterTags };

	private Activity publicActivity, publicActivityWithTag;
	private APIActivitiesHandler activitiesAPIUser2;
	private ActivitiesUI uiAct;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity, baseActivityWithTag;
	private String tagToFollow;
	private User testUser1, testUser2, testUser3;

	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiAct = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// Create the tag to be followed and have User 1 follow the tag
		tagToFollow = Data.getData().commonTag + Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 creates a public activity without using any custom tags but with User 3 added as a member (so as the to-do item can be assigned to User 3 later)
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser2, activitiesAPIUser2, testUser3, isOnPremise);
		
		// User 2 creates a public activity with the tag being followed by User 1 but not including any members
		baseActivityWithTag = ActivityBaseBuilder.buildBaseActivityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), tagToFollow, false);
		publicActivityWithTag = ActivityEvents.createActivity(testUser2, activitiesAPIUser2, baseActivityWithTag, isOnPremise);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest(){
	
		uiAct = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the activity created during the test
		activitiesAPIUser2.deleteActivity(publicActivity);
		activitiesAPIUser2.deleteActivity(publicActivityWithTag);
	}
	
	/**
	* <li><B>Name:</B> test_Tags_CreateActivity_Standalone Activity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 start a public Activity and add the Tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify: Verify that the activity.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A9185002C81AD741852578FC00450A35">TTT - AS - FOLLOW - TAG - ACTIVITY - 00011 - activity.created - STANDALONE ACTIVITY</a></li>
	* </ul>
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_Tags_CreateActivity_StandaloneActivity() {

		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news stories to be verified
		String makeActivityPublicEvent = ActivityNewsStories.getMakeActivityPublicNewsStory(ui, baseActivityWithTag.getName(), testUser2.getDisplayName());
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivityWithTag.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the activity made public event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{makeActivityPublicEvent, baseActivityWithTag.getGoal()}, filter, true);
			
			// verify that the activity creation event is not displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createActivityEvent}, null, false);
		}
		ui.endTest();
	}
		
	/**
	* <li><B>Name:</B> test_Tags_CreateEntry_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a public activity and add an entry with the tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.created story is displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4F8B2870D8460CB0852578FC00451007 ">TTT - AS - FOLLOW - TAG - ACTIVITY - 00021 - activity.entry.created - STANDALONE PUBLIC ACTIVITY</a></li>
	* </ul>@author Srinivas Vechha
	*/		
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreateEntry_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a public activity entry with the tag followed by User 1
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createActivityEntry(testUser2, activitiesAPIUser2, baseActEntry, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}		
		
	/**
	* <li><B>Name:</B> test_Tags_CreatePrivateEntry_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a public activity and add a private entry with the tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EFFA93B77D96249C852578FC0047CC6A">TTT - AS - FOLLOW - TAG - ACTIVITY - 00023 - activity.entry.created - STANDALONE PUBLIC ACTIVITY / PRIVATE ENTRY</a></li>
	* </ul>@author Srinivas Vechha
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreatePrivateEntry_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a private activity entry with the tag followed by User 1
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, true);
		ActivityEvents.createActivityEntry(testUser2, activitiesAPIUser2, baseActEntry, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
			
	/**
	* <li><B>Name:</B> test_Tags_CreateTodo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to Activities</B></li>
	* <li><B>Step: testUser 2 go to the public activity and add a to-do with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.created story is displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/907D0723C96C000B852578FC004AE12C">TTT - AS - FOLLOW - TAG - ACTIVITY - 00031 - activity.todo.created - STANDALONE PUBLIC ACTIVITY</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreateTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createTodo(testUser2, activitiesAPIUser2, baseTodo, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
    }
	
	/**
	* <li><B>Name:</B> test_Tags_CreatePrivateTodo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to Activities</B></li>
	* <li><B>Step: testUser 2 go to the public activity and add a private todo with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.created story is not displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6E4FB5199024FC91852578FC004AE3EB">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00033 - activity.todo.created - STANDALONE PUBLIC ACTIVITY / PRIVATE TO-DO</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreatePrivateTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a private to-do item with the tag followed by User 1
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, true);
		ActivityEvents.createTodo(testUser2, activitiesAPIUser2, baseTodo, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();			
	}	
		
	/**
	* <li><B>Name:</B> test_Tags_CompleteTodo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a public activity and for a to-do that contains the tag that User 1 is following, mark the to-do as complete</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.completed story appears in (1) the top level, (2) theTags filter, and (3) the specific <tag> subfilter</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8105C4CABE7565E3852579AC004A98E8">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00061 - activity.todo.completed - STANDALONE PUBLIC ACTIVITY</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CompleteTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1 and complete it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createTodoAndMarkAsCompleted(testUser2, activitiesAPIUser2, baseTodo, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();								
	}		

	/**
	* <li><B>Name:</B> test_Tags_Update_ActivityEntry_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a public activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to an activity entry that you have tagged with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 2 update the entry</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.updated story appears in the views<tag> subfilter</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9AE993F66A73AE6485257A640049A82A">TTT -  AS - Follow - Tag - Activity - 00086 - activity.entry.updated - standalone public activity</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_Update_ActivityEntry_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a public activity entry with the tag followed by User 1 and edit it
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createEntryAndEditDescription(testUser2, activitiesAPIUser2, baseActEntry, publicActivity, editedDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* <li><B>Name:</B> test_Tags_Update_ActivityPrivateEntry_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a public activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to a private activity entry that you have tagged with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 update the private entry</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.entry.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/281FB6A4151F4B5F85257A640049AC28">TTT -  AS - Follow - Tag - Activity - 00087 - activity.entry.updated - standalone public activity / private entry (neg)</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_Update_ActivityPrivateEntry_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a private activity entry with the tag followed by User 1 and edit it
		BaseActivityEntry baseActEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, true);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createEntryAndEditDescription(testUser2, activitiesAPIUser2, baseActEntry, publicActivity, editedDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, baseActEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
		
	/**
	* <li><B>Name:</B> test_Tags_UpdateActivityToDo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a public activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to an activity todo that you have tagged with the tag User 2 is following</B></li>
	* <li><B>Step: testUser 2 update the to-do</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.updated story appears in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1D5E6E5D1873D0DF85257A640049B737">TTT -  AS - Follow - Tag - Activity - 00096 - activity.todo.updated - standalone public activity</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_UpdateActivityToDo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1 and edit it
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createTodoAndEditDescription(testUser2, activitiesAPIUser2, baseTodo, publicActivity, editedDescription);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();	
	}	
		
	/**
	* <li><B>Name:</B> test_Tags_UpdatePrivateToDo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a public activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to a private activity to-do that you have tagged with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 2 update the private to-do</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1BD1FB701EDFBDDF85257A640049C461">TTT -  AS - Follow - Tag - Activity - 00098 - activity.todo.updated - standalone public activity / private entry (neg)</a></li>
	* </ul>@author Srinivas Vechha
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_UpdatePrivateToDo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a private to-do item with the tag followed by User 1 and edit it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, true);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ActivityEvents.createTodoAndEditDescription(testUser2, activitiesAPIUser2, baseTodo, publicActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PublicActivity_Standalone</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to a completed todo that you have tagged with the tag User 2 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the todo </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story appears in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D5E446FF3B8A4B2985257A640049C773">TTT - AS - Follow - Tag - Activity - 00106 - activity.todo.reopened - standalone public activity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tag_ReopenTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1, complete it and then reopen it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser2, activitiesAPIUser2, baseTodo, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();	
   }
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PrivateTodo_Standalone()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to a Completed private todo that you have tagged with the tag User 1 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the private todo </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story does NOT appear in the views.
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF8FF948AF60FFE485257A640049D25B">TTT -  AS - Follow - Tag - Activity - 00108 - activity.todo.reopened - standalone public activity / private entry (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tag_ReopenTodo_PrivateTodo_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a private to-do item with the tag followed by User 1, complete it and then reopen it
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, true);
		ActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser2, activitiesAPIUser2, baseTodo, publicActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();		
	}
		
	/**
	* <li><B>Name:</B> test_Tags_CreateAssignedTodo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to Activities</B></li>
	* <li><B>Step: testUser 2 go to the public activity and add a to-do with the tag User 1 is following and assign it to another User</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.created story is displayed in Homepage / All Updates filtered by Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C54BBECBA809FC9852578FC004AE2B7">TTT - AS - FOLLOW - TAG - ACTIVITY - 00032 - activity.todo.created - STANDALONE PUBLIC ACTIVITY / ASSIGNED</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_CreateAssignedTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1 and assign the to-do item to User 3
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseTodo, testUser2, activitiesAPIUser2, profilesAPIUser3);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		/**
		 * Assign event to be verified - since we are creating and assigning the to-do item in separate steps using the API,
		 * we will instead see the "update" event for the assigned to-do and NOT the "create" event.
		 */
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();			
	}
		
	/**
	* <li><B>Name:</B> test_Tags_UpdateAssignedTodo_PublicActivity_Standalone()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 2 log into a public activity you own</B></li>
	* <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	* <li><B>Step: testUser 2 go to an activity todo that you have tagged with the tag User 1 is following and is assigned to you</B></li>
	* <li><B>Step: testUser 2 update the todo by assigning to a different activity member (Select All)</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify that the activity.todo.updated story appears in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/82A2D9ECDFFE133B85257A640049B901">TTT - AS - Follow - Tag - Activity - 00097 - activity.todo.updated - standalone public activity / assigned</a></li>
	* </ul>@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tags_UpdatedAssignedTodo_PublicActivity_Standalone(){

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1 and assign the to-do item to themselves
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		Todo newTodo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseTodo, testUser2, activitiesAPIUser2, profilesAPIUser2);
		
		// User 2 will now update the assigned to-do and re-assign it to anyone (ie. no specific user)
		ActivityEvents.assignTodoToAnyone(testUser2, activitiesAPIUser2, newTodo);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Assign event to be verified						
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PublicActivity_Standalone_Assigned</li>
	 * <li><B>Step: testUser 2 log into a public activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to a completed to-do that you have tagged with the tag User 1 is following and is assigned to you </B></li>
	 * <li><B>Step: testUser 2 reopen the to-do </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B>Verify that the activity.todo.reopened story appears in the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B5657BF71CDDE78B85257A640049D05D">TTT -  AS - Follow - Tag - Activity - 00107 - activity.todo.reopened - standalone public activity / assigned</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_Tag_Assigned_ReopenTodo_PublicActivity_Standalone() {

		String testName = ui.startTest();
		
		// User 2 create a to-do item with the tag followed by User 1, will assign the to-do item to themselves and will complete the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), publicActivity, tagToFollow, false);
		Todo newTodo = ActivityEvents.createTodoAndAssignTodoItemToUserAndMarkAsCompleted(publicActivity, baseTodo, testUser2, activitiesAPIUser2, profilesAPIUser2);
		
		// User 2 log in and re-open the to-do item - critical step since the API generates an "update" event and NOT the "re-open" event if used to perform this action
		ActivityEvents.loginAndNavigateToActivityAndReopenToDoItem(ui, uiAct, publicActivity, newTodo, testUser2, false);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		
		// Assign event to be verified					
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());			

		// Verify the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
						
		ui.endTest();	
	}	
}