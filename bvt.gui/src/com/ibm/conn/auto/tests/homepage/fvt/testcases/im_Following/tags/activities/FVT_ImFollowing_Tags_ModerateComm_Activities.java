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

public class FVT_ImFollowing_Tags_ModerateComm_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		
		// User 2 create a moderated community and add the Activities widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 create an activity in the moderated community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, moderatedCommunity);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
	}
	
	/**
	* <li><B>Name:</B> test_tags_CreateActivity_ModerateCommunity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 start a moderate moderatedCommunity</B></li>
	* <li><B>Step: Create an communityActivity within the moderatedCommunity and add the tag that User 1 is following</B></li>
	* <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, activities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify: Verify that the communityActivity.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/24AE29C74CC2270A852578FC00450D12">TTT - AS - FOLLOW - TAG - ACTIVITY - 00014 - communityActivity.created - MODERATE COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateActivity_ModerateComm() {

		String testName = ui.startTest();

		// User 2 create an activity with the tag that User 1 is following
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivityWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser2, activitiesAPIUser2, communitiesAPIUser2, moderatedCommunity);

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
	 * <li><B>Name:</B> test_Tag_CreateActivityEntry_ModerateCommunity()</li>
	 * <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 * <li><B>Step: testUser 1 log into Connections</B></li>
	 * <li><B>Step: testUser 1 follow a tag</B></li>
	 * <li><B>Step: testUser 2 log into Connections</B></li	 
	 * <li><B>Step: testUser 2 go to a moderatedCommunity you are the owner of with Moderate access</B></li>
	 * <li><B>Step: testUser 2 got to the communityActivity in the moderatedCommunity and add an entry with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the communityActivity.entry.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9478EDAC16955531852578FC0047CFAC">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00026 - communityActivity.entry.created - MODERATE COMMUNITY ACTIVITY</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateActivityEntry_ModeratedCommunity() {

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
	 * <ul>
	 * <li><B>Name:</B> test_Tag_Activity_CreateTodo_ModerateCommunity()</li>
	 * <li><B>Step: testUser 1 log into Connections</B></li>
	 * <li><B>Step: testUser 1 follow a tag</B></li>
	 * <li><B>Step: testUser 2 log into Connections</B></li>
	 * <li><B>Step: testUser 2 go to a moderatedCommunity you are the owner of with Moderate access</B></li>
	 * <li><B>Step: testUser 2 go to the communityActivity within the moderatedCommunity and add a to-do with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Updates,Activities, Communities & Tags (All Tags / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the communityActivity.todo.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Activities
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D44320C368A78253852578FC004AE8D8">TTT -  AS - FOLLOW - TAG - ACTIVITY - 00037 - communityActivity.todo.created - MODERATE COMMUNITY ACTIVITY</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_CreateTodo_ModeratedCommunity() {

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
	* <li><B>Name:</B> test_Tag_CompleteTodo_ModeratedCommunity()</li>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: testUser 1 log into Connections</B></li>
	* <li><B>Step: testUser 1 follow a tag</B></li>
	* <li><B>Step: testUser 2 log into Connections</B></li>
	* <li><B>Step: testUser 2 go to a Moderated moderatedCommunity communityActivity and for a todo that contains the tag that User 1 is following, mark the todo as complete</B></li>
	* <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	* <li><B>Verify:</B> Verify that the communityActivity.todo.completed story appears in (1) the top level, (2) theTags filter, and (3) the specific <tag> subfilter
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/040F0040F146D62B852579AC004A9CEA">TTT - AS - FOLLOW - TAG - ACTIVITY - 00064 - communityActivity.todo.completed - MODERATED COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Tag_CompleteTodo_ModeratedCommunity() {

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
	 * <li><B>Name:</B> test_Tag_UpdateActivityEntry_MODERATEDCommunity()</li>
	 * <li><B>Step: testUser 2 log into a MODERATED moderatedCommunity that has an communityActivity you own </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 * <li><B>Step: testUser 2 go to a communityActivity entry that you have tagged with the tag User 1 is following</B></li>
	 * <li><B>Step: testUser 2 update the entry</B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the communityActivity.entry.updated story appears in the view
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A0E3699BBE95AD0D85257A64003FEFB6">TTT - AS - Follow - Tag - Activity - 00081 - communityActivity.entry.updated - moderate moderatedCommunity communityActivity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_Update_Activity_Entry_ModeratedCommunity() {

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
	 * <li><B>Name:</B> test_Tag_UpdateTodo_ModeratedCommunity()</li>
	 *<li><B>Step: testUser 2 log into a Moderated moderatedCommunity that has an communityActivity you own </B></li>
	 *<li><B>Step: testUser 1 ensure they are following the tag User 2 used</B></li>
	 *<li><B>Step: testUser 2 go to a communityActivity todo that you have tagged with the tag User 1 is following</B></li>
	 *<li><B>Step: testUser 2 update the todo</B></li>
	 *<li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li>
	 * <li><B>Verify:</B> Verify that the communityActivity.todo.updated story appears in the views
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/651D99354BF5CF4E85257A64003FF71C">TTT - AS - Follow - Tag - Activity - 00091 - communityActivity.todo.updated - moderate moderatedCommunity communityActivity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_UpdateTodo_ModeratedCommunity() {

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
	 * <li><B>Name:</B> test_Tag_ReopenTodo_ModeratedCommunity()</li>
	 * <li><B>Step: testUser 2 log into a Moderated moderatedCommunity that has an communityActivity you own  </B></li>
	 * <li><B>Step: testUser 1 ensure they are following the tag User 2 used </B></li>
	 * <li><B>Step: testUser 2 go to an communityActivity todo that you have tagged with the tag User 1 is following </B></li>
	 * <li><B>Step: testUser 2 reopen the todo </B></li>
	 * <li><B>Step: testUser 1 log into Homepage / All Updates / I'm Following / All, Communities & Tags (All Tags,  Tags filtered by <User 1 tag> / {TagName}</B></li> </B></li>
	 * <li><B>Verify:</B> Verify that the communityActivity.todo.reopened story appears in the views
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/35EB25EB04048DEB85257A6400400642">TTT - AS - Follow - Tag - Activity - 00101 - communityActivity.todo.reopened - moderate moderatedCommunity communityActivity</a></li>
	 * @author Srinivas Vechha
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_Tag_ReopenTodo_ModeratedCommunity(){

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
}