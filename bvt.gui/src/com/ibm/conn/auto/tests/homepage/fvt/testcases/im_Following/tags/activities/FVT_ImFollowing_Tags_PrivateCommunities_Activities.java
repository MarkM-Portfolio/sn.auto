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

public class FVT_ImFollowing_Tags_PrivateCommunities_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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
		
		// User 2 create a restricted community and add the Activities widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 create an activity in the restricted community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, restrictedCommunity);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* <li><B>Name:</B> test_CreateActivity_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 start a Private community</B></li>
	*<li><B>Step: Create an activity within the community and add the tag that User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the activity.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF5DC2435102AB5F852578FC00450E57">TTT - AS - FOLLOW - TAG - ACTIVITY - 00015 - activity.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_CreateActivity_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create an activity with the tag that User 1 is following
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivityWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, restrictedCommunity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the create activity event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* <li><B>Name:</B> test_tags_CreateActivity_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with private access</B></li>
	*<li><B>Step: testUser 2 got to the activity in the community and add an entry with the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the activity.entry.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AE23766152CF9DE1852578FC00484198">TTT - AS - FOLLOW - TAG - ACTIVITY - 00027 - activity.entry.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tags_CreateActivityEntry_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 2 create an activity entry with the tag that User 1 is following
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
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
	* <li><B>Name:</B> test_CreateActivityTodo_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are the owner of with private access</B></li>
	*<li><B>Step: testUser 2 go to the activity within the community and add a todo with the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the activity.todo.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Activities</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1A1C19A2733F66FE852578FC004AEA01">TTT - AS - FOLLOW - TAG - ACTIVITY - 00038 - activity.todo.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	@author Srinivas Vechha
	*/
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_CreateActivityTodo_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the communityActivity with the tag that User 1 is following
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
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
	* <li><B>Name:</B> test_Tags_CompleteActivityTodo_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private community activity and for a to-do that contains the tag that User 1 is following, mark the to-do as complete</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the activity.todo.completed story does not appear in (1) the top level, (2) theTags filter, and (3) the specific <tag> subfilter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E9AADD19073FC3D852579AC004A9E60">TTT - AS - FOLLOW - TAG - ACTIVITY - 00065 - activity.todo.completed - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	@author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tags_CompleteActivityTodo_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the communityActivity with the tag that User 1 is following
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser2, activitiesAPIUser2, baseTodo, communityActivity);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the complete to-do item event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Tag_Update_Activity_Entry_PrivateCommunity()</li>
	 * <li><B>Step: testUser 2 log into a Private community that has an activity you own </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 * <li><B>Step: testUser 2 go to a private activity entry that you have tagged with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 2 update the entry</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.entry.updated story does NOT appear in the view.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D6683ECF7B04062E85257A64003FF1CB">TTT - AS - Follow - Tag - Activity - 00082 - activity.entry.updated - private community activity (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_Update_Activity_Entry_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create an activity entry with the tag that User 1 is following and edit the description of that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntryWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
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
	 * <li><B>Name:</B> test_Tag_UpdateActivityTodo_PrivateCommunity()</li>
	 *<li><B>Step: testUser 2 log into a private community that has an activity you own </B></li>
	 *<li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 *<li><B>Step: testUser 2 go to a activity todo that you have tagged with the tag User 1 is following</B></li>
	 *<li><B>Step: testUser 2 update the todo</B></li>
	 *<li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.updated story does NOT appear in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CD7557C6CDB3BDE785257A64003FFAD5">TTT - AS - Follow - Tag - Activity - 00092 - activity.todo.updated - private community activity (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tags_UpdateActivityTodo_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the activity with the tag that User 1 is following and edit the description of that to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
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
	 * <li><B>Name:</B> test_Tag_ReopenTodo_PrivateCommunity()</li>
	 * <li><B>Step: testUser 2 log into a private community that has an activity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 1 go to an activity to-do that you have tagged with the tag User 2 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the to-do </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the activity.todo.reopened story does NOT appear in the views.
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C0D6FC575C63FC3185257A6400400802">TTT - AS - Follow - Tag - Activity - 00102 - activity.todo.reopened - private community activity (neg)</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tags_ReopenTodo_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create a to-do item in the activity with the tag that User 1 is following, mark it as completed and then re-open the to-do item again
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDoWithCustomTag(testName + Helper.genStrongRand(), communityActivity, tagToFollow, false);
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