package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

public class FVT_ImFollowing_Tags_PublicCommunities_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private String tagToFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);

		// Create the tag to be followed and have User 1 follow that tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 create a public community and add the Activities widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 create an activity in the moderated community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, publicCommunity);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(publicCommunity);
	}
	
	/**
	* <li><B>Name:</B> test_CreateActivity_PublicCommunity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 start a public community</B></li>
	* <li><B>Step: Create an activity within the community and add the tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify: Verify that the activity.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/50BF2E37FAB9BC11852578FC00450BD0">TTT - AS - FOLLOW - TAG - ACTIVITY - 00013 - activity.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	* </ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateActivity_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create an activity with the tag that User 1 is following
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivityWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, publicCommunity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create activity event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_CreateActivityEntry_PublicCommunity()</li>
	 * <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 * <li><B>Step: testUser 1 log into Connections</B></li>
	 * <li><B>Step: testUser 1 follow a tag</B></li>
	 * <li><B>Step: testUser 2 log into Connections</B></li	 
	 * <li><B>Step: testUser 2 go to a community you are the owner of with public access</B></li>
	 * <li><B>Step: testUser 2 got to the activity in the community and add an entry with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.entry.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6751226B8E62046B852578FC0047CD67">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00024 - activity.entry.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateActivityEntry_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 2 create an activity entry with the tag that User 1 is following
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		CommunityActivityEvents.createActivityEntry(testUser2, activitiesAPIUser2, baseActivityEntry, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
    * <li><B>Name:</B> test_Tag_CreateActivityPrivateEntry_PublicCommunity()</li>
    * <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a community you are the owner of with public access</B></li>
	* <li><B>Step: testUser 2 got to the activity in the community and add a private entry with the tag User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Updates,Activities, Communities & Tags (All Tags / {TagName}</B></li>
    * <li><B>Verify:</B> Verify that the activity.entry.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Activities
    * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/72B1A538B5ECF306852578FC0047CE86">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00025 - activity.entry.created - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY</a></li>
    * @author Srinivas Vechha
    */
   @Test(groups = {"fvtonprem", "fvtcloud"})
   public void test_Tag_CreatePrivateEntry_PublicCommunity(){

	   String testName = ui.startTest();
		
		// User 2 create a private activity entry with the tag that User 1 is following
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, true);
		CommunityActivityEvents.createActivityEntry(testUser2, activitiesAPIUser2, baseActivityEntry, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_Activity_CreateTodo_PublicCommunity()</li>
	 * <li><B>Step: testUser 1 log into Connections</B></li>
	 * <li><B>Step: testUser 1 follow a tag</B></li>
	 * <li><B>Step: testUser 2 log into Connections</B></li>
	 * <li><B>Step: testUser 2 go to a community you are the owner of with public access</B></li>
	 * <li><B>Step: testUser 2 go to the activity within the community and add a to-do with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Updates,Activities, Communities & Tags (All Tags / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/75A7D54C785D428F852578FC004AE63B">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00035 - activity.todo.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the communityActivity with the tag that User 1 is following
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		CommunityActivityEvents.createActivityTodo(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create to-do item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
    }

	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_CreatePrivateTodo_PublicCommunity()</li>
	 * <li><B>Step: testUser 1 log into Connections</B></li>
	 * <li><B>Step: testUser 1 follow a tag</B></li>
	 * <li><B>Step: testUser 2 log into Connections</B></li>
	 * <li><B>Step: testUser 2 go to a community you are the owner of with public access</B></li>
	 * <li><B>Step: testUser 2 go to the activity within the community and add a private todo with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Updates,Activities, Communities & Tags (All Tags / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Activities.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6B0D89B45A9A16EC852578FC004AE76A">TTT -   AS - FOLLOW - TAG - ACTIVITY - 00036 - activity.todo.created - PUBLIC COMMUNITY ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tag_CreatePrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a private to-do item in the communityActivity with the tag that User 1 is following
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, true);
		CommunityActivityEvents.createActivityTodo(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* <li><B>Name:</B> test_Tag_CompleteTodo_PublicCommunity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a public community activity and for a todo that contains the tag that User 1 is following, mark the todo as complete</B></li>
	* <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify:</B> Verify that the activity.todo.completed story appears in (1) the top level, (2) theTags filter, and (3) the specific <tag> subfilter
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D59BF20ED7182B44852579AC004A9BCB">TTT - AS - FOLLOW - TAG - ACTIVITY - 00063 - activity.todo.completed - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tag_CompleteTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the communityActivity with the tag that User 1 is following
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the complete to-do item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_Update_Activity_Entry_PublicCommunity()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 * <li><B>Step: testUser 2 go to a activity entry that you have tagged with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 2 update the entry</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.entry.updated story appears in the views
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/529A90E8FCAF480785257A64003FF3A1">TTT - AS - Follow - Tag - Activity - 00083 - activity.entry.updated - public community activity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_Update_Activity_Entry_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create an activity entry with the tag that User 1 is following and edit the description of that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		String entryEdit = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityActivityEvents.createEntryAndEditDescription(testUser2, activitiesAPIUser2, baseActivityEntry, communityActivity, entryEdit);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the update entry event and updated description are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, entryEdit}, TEST_FILTERS, true);
		
		ui.endTest();
   }	
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_UpdateEntry_PrivateEntry_PublicCommunity()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 * <li><B>Step: testUser 2 go to a private activity entry that you have tagged with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 2 update the private entry</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Private Entry Comment update event does not appear in the homepage AS (Im Following) filtered by communities and activities views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F8B68C10F15571D185257A64003FF55F">TTT - AS - Follow - Tag - Activity - 00084 - activity.entry.updated - public community activity / private entry (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_Update_Activity_Entry_PrivateEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a private activity entry with the tag that User 1 is following and edit the description of that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, true);
		String entryEdit = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityActivityEvents.createEntryAndEditDescription(testUser2, activitiesAPIUser2, baseActivityEntry, communityActivity, entryEdit);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the update entry event and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, entryEdit}, TEST_FILTERS, false);
		
		ui.endTest();
	}			
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_UpdateTodo_PublicCommunity()</li>
	 *<li><B>Step: testUser 2 log into a public community that has an activity you own </B></li>
	 *<li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 *<li><B>Step: testUser 2 go to a activity todo that you have tagged with the tag User 1 is following</B></li>
	 *<li><B>Step: testUser 2 update the todo</B></li>
	 *<li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.updated story appears in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A7EDD5CBBAA4930C85257A64003FFC92">TTT - AS - Follow - Tag - Activity - 00093 - activity.todo.updated - public community activity</a></li>
	 * @author Srinivas Vechha
	 */
   @Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_UpdateTodo_PublicCommunity(){

	   	String testName = ui.startTest();

		// User 2 create a to-do item in the activity with the tag that User 1 is following and edit the description of that to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		String newDescription = Helper.genStrongRand();
		CommunityActivityEvents.createTodoAndEditDescription(testUser2, activitiesAPIUser2, baseTodo, communityActivity, newDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the update to-do item event and updated description are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_UpdatePrivateTodo_PublicCommunity()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 * <li><B>Step: testUser 2 go to a private activity todo that you have tagged with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 2 update the private todo</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.updated story does NOT appear in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/35D1E4D3492ABB2485257A6400400417">TTT - AS - Follow - Tag - Activity - 00094 - activity.todo.updated - public community activity / private todo (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_UpdatePrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a private to-do item in the activity with the tag that User 1 is following and edit the description of that to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, true);
		String newDescription = Helper.genStrongRand();
		CommunityActivityEvents.createTodoAndEditDescription(testUser2, activitiesAPIUser2, baseTodo, communityActivity, newDescription);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the update to-do item event and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PublicCommunity()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to an activity todo that you have tagged with the tag User 1 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the todo </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story appears in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CE2511E915C5946885257A64004009D6">TTT - AS - Follow - Tag - Activity - 00103 - activity.todo.reopened - public community activity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_ReopenTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the activity with the tag that User 1 is following, mark it as completed and then re-open the to-do item again
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the re-open to-do item event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PrivateTodo_PublicCommunity()</li>
	 * <li><B>Step: testUser 2 log into a public community that has an activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to a private activity todo that you have tagged with the tag User 2 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the private todo </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story does NOT appear in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/469B97EBDB87F8C585257A6400400C34">TTT - AS - Follow - Tag - Activity - 00104 - activity.todo.reopened - public community activity / private todo (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_ReopenTodo_PrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 2 create a private to-do item in the activity with the tag that User 1 is following, mark it as completed and then re-open the private to-do item again
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, true);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the re-open to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}	