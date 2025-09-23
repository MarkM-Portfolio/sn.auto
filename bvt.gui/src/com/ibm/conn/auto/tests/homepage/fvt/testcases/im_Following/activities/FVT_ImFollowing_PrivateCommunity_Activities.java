package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.webui.CommunitiesUI;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_PrivateCommunity_Activities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };
	
	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		// User 1 will now create a restricted community, User 2 will be added to this community as a member and the Activities widget added to the community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a new activity in the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
				
		// Log in as User 2 and follow the activity using the UI - restricted community activities can NOT be followed using the API
		CommunityActivityEvents.loginAndFollowCommunityActivity(ui, uiCo, restrictedCommunity, baseCommunity, communityActivity, testUser2, communitiesAPIUser2, false);
		
		// Log out of Connections
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Close the currently open browser window
		UIEvents.closeCurrentBrowserWindow(ui);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityEntry_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>	
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4D2E3D26517855B6852578760079E8B5">TTT - AS - FOLLOW - ACTIVITY - 00022 - ACTIVITY.ENTRY.CREATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivityEntry_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create activity entry event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateActivityEntry_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new private activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is NOT seen in the Activities view</B></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private activity entry instead of a public activity entry</b></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreatePrivateActivityEntry_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a private entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create private activity entry event is not displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryComment_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a comment to activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.comment.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6AD1F2CAC6F9E58A852578760079E8BC">TTT - AS - FOLLOW - ACTIVITY - 00032 - ACTIVITY.ENTRY.COMMENT.CREATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateEntryComment_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity and comment on the entry
		String entryComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryComment, false);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create comment event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommentEvent, baseActivityEntry.getDescription().trim(), entryComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateEntryComment_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>	
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a private comment to activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.comment.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is NOT seen in the Activities view</B></li> 
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private comment instead of a public comment</b></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreatePrivateEntryComment_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity and comment on the entry
		String entryComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryComment, true);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String createCommentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			
			// Verify that the create activity entry event is displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, filter, true);
			
			// Verify that the create private comment event is not displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createCommentEvent, entryComment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.todo.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D4610786947B7756852578760079E8BF">TTT - AS - FOLLOW - ACTIVITY - 00042 - ACTIVITY.TO-DO.CREATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateTodo_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>	
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new private activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.todo.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is NOT seen in the Activities view</B></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private to-do item instead of a public to-do item</b></li> 
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreatePrivateTodo_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create to-do item event is not displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CompleteTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Mark the todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify that the news story for activity.todo.completed is seen in the Communities view</b></li>
	 *<li><B>Verify that the news story for activity.todo.completed is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/536F447F26A5BE36852578760079E8C7">TTT - AS - FOLLOW - ACTIVITY - 00052 - ACTIVITY.TO-DO.COMPLETED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CompleteTodo_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will mark it as completed
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the complete to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntryComment_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & Communities</b></li>
	 *<li><b>Verify that the news story for activity.reply.updated is NOT seen in any of the views</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EA24AC76524F03FA852579BB0057A14B">TTT -  AS - FOLLOW - ACTIVITY - 00062 - ACTIVITY.REPLY.UPDATED- PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateEntryComment_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity and comment on the entry
		String entryComment = Data.getData().commonComment + Helper.genStrongRand();
		String editedEntryComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryComment, false, editedEntryComment);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create comment event and the updated comment are displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createCommentEvent, baseActivityEntry.getDescription().trim(), editedEntryComment}, filter, true);
			
			// Verify that the update comment event and unedited comment are not displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, entryComment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateEntryComment_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with private access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new private comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & Communities</b></li>
	 *<li><b>Verify that the news story for activity.reply.updated is NOT seen in any of the views</b></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private comment instead of a public comment</b></li> 
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdatePrivateEntryComment_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity and comment on the entry
		String entryComment = Data.getData().commonComment + Helper.genStrongRand();
		String editedEntryComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryComment, true, editedEntryComment);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String createCommentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create activity entry event is displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, filter, true);
			
			// Verify that the create comment event, the update comment event, the unedited comment and the updated comment are not displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createCommentEvent, updateCommentEvent, entryComment, editedEntryComment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateActivityEntry_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to an entry in the activity</B></li>	
	 *<li><B>Step: User 1 update the entry</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of activity.entry.updated does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C2DA4096D026B9EC85257A62004BC1E0">TTT - AS - FOLLOW - ACTIVITY - 00072 - ACTIVITY.ENTRY.UPDATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren 
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateActivityEntry_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity
		String editedEntryContent = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createEntryAndEditDescription(communityActivity, baseActivityEntry, editedEntryContent, testUser1, activitiesAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription().trim()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedEntryContent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateActivityEntry_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a private entry in the activity</B></li>	
	 *<li><B>Step: User 1 update the entry</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of activity.entry.updated does NOT appear in the views</b></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private entry instead of a public entry</b></li> 
	 * @author Hugh Caren 
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdatePrivateActivityEntry_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the activity
		String editedEntryContent = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createEntryAndEditDescription(communityActivity, baseActivityEntry, editedEntryContent, testUser1, activitiesAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create activity event, the original description and the update event are not displayed in any of the filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, updateEntryEvent, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a todo in the activity</B></li>	
	 *<li><B>Step: User 1 update the todo in the activity</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of the activity.todo.updated appears in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9C40E574F992D52A85257A6300368DF9">TTT - AS - FOLLOW - ACTIVITY - 00082 - ACTIVITY.TO-DO.UPDATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTodo_PrivateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity
		String editedTodoDescription = Data.getData().commonDescription + Helper.genStrongRand(); 
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity, editedTodoDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String updatedTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the update to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updatedTodoEvent, editedTodoDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a private todo in the activity</B></li>	
	 *<li><B>Step: User 1 update the todo in the activity</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of the activity.todo.updated does NOT appear in the views</b></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private to-do item instead of a public to-do item</b></li> 
	 * @author Hugh Caren
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_UpdatePrivateTodo_PrivateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity
		String editedTodoDescription = Data.getData().commonDescription + Helper.genStrongRand(); 
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity, editedTodoDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String updatedTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the update to-do item event is not displayed in any of the filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updatedTodoEvent, editedTodoDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a completed todo in the community</B></li>	
	 *<li><B>Step: User 1 reopen the completed todo</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the activity.todo.reopened story appears in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6B286A36606F55DF85257A63002F62CC">TTT - AS - FOLLOW - ACTIVITY - 00092 - ACTIVITY.TO-DO.REOPENED - PRIVATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_ReopenTodo_PrivateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the communityActivity, will mark it as completed and then re-open it again
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String reopenedTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the re-opened to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenedTodoEvent, baseActivityTodo.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenPrivateTodo_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a private community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a completed private todo in the community</B></li>	
	 *<li><B>Step: User 1 reopen the completed todo</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the activity.todo.reopened story does NOT appear in the views</b></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private to-do item instead of a public to-do item</b></li> 
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_ReopenPrivateTodo_PrivateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the communityActivity, will mark it as completed and then re-open it again
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String reopenedTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the re-opened to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenedTodoEvent, baseActivityTodo.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}