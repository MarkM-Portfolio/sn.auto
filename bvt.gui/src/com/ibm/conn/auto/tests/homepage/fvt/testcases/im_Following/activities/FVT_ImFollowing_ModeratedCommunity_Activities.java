package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.activities;

import java.util.HashMap;
import java.util.Set;

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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_ModeratedCommunity_Activities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };

	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		// User 1 will now create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now add an activity to the community with User 2 as a follower
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivityWithOneFollower(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, activitiesAPIUser2, moderatedCommunity);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		// Delete any additional communities created during the test
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		
		for(Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this community</B></li>
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5583E03E811C89F0852578760079E8AF">TTT - AS - FOLLOW - ACTIVITY - 00011 - ACTIVITY.CREATED - MODERATED COMMUNITY ACTIVITY </a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivity_ModeratedCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create a moderated community with User 2 as a follower (this is the only test case where User 2 must follow the community)
		BaseCommunity testBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);
		Community testModeratedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(testBaseCommunity, testUser2, communitiesAPIUser2, BaseWidget.ACTIVITIES, testUser1, communitiesAPIUser1, isOnPremise);
		communitiesForDeletion.put(testModeratedCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity testBaseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), testBaseCommunity);
		CommunityActivityEvents.createCommunityActivity(testBaseActivity, testBaseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, testModeratedCommunity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, testBaseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, testBaseActivity.getGoal()}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		communitiesAPIUser1.deleteCommunity(testModeratedCommunity);
		communitiesForDeletion.remove(testModeratedCommunity);
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityEntry_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1</B></li>	
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CA6A7596AC3AFE52852578760079E8B4">TTT - AS - FOLLOW - ACTIVITY - 00021 - ACTIVITY.ENTRY.CREATED - MODERATED COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivityEntry_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityPrivateEntry_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1</B></li>	
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is NOT seen in the Activities view</B></li>
	 *<li><b>No TTT link for this test - it is similar to the test, above, except for it is using a private activity entry instead of a public activity entry</b></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivityPrivateEntry_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a private entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityEntryComment_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a comment to activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.comment.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F69FB5D9A9D0664E852578760079E8BD">TTT - AS - FOLLOW - ACTIVITY - 00031 - ACTIVITY.ENTRY.COMMENT.CREATED - MODERATED COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateActivityEntryComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will comment on that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryPrivateComment_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>	
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
	public void test_CreateEntryPrivateComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will post a private comment to that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.todo.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li> 
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7FBEFD3CDBAE5B8C852578760079E8BE">TTT - AS - FOLLOW - ACTIVITY - 00041 - ACTIVITY.TO-DO.CREATED - MODERATED COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateActivityTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>	
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
	public void test_CreatePrivateTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CompleteTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Mark the todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify that the news story for activity.todo.completed is seen in the Communities view</b></li>
	 *<li><B>Verify that the news story for activity.todo.completed is seen in the Activities view</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8E91780B6986F993852578760079E8C6">TTT - AS - FOLLOW - ACTIVITY - 00051 - ACTIVITY.TO-DO.COMPLETED - MODERATED COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CompleteTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will mark the to-do item as completed
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateActivityEntryComment_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & Communities</b></li>
	 *<li><b>Verify that the news story for activity.reply.updated is NOT seen in any of the views</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6FB74ABD0FDA8E74852579BB00537A7F">TTT - AS - FOLLOW - ACTIVITY - 00061 - ACTIVITY.REPLY.UPDATED - MODERATED COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateActivityEntryComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity, will comment on the entry and will then edit the comment on the entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false, replyEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as this news story should still appear in the Activity Stream
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on activity entry event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdatePrivateEntryComment_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with moderated access as user 1, add user 2 as a member</B></li>
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
	public void test_UpdatePrivateEntryComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity, will post a private comment on the entry and will then edit the private comment on the entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, true, replyEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as this news story should still appear in the Activity Stream
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment events and comments posted / updated are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, comment, replyEdit}, TEST_FILTERS, false);
			
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateActivityEntry_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to an entry in the activity</B></li>	
	 *<li><B>Step: User 1 update the entry</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of activity.entry.updated does NOT appear in the views</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8435C34ADEA9763B85257A62004BBF71">TTT -  AS - FOLLOW - ACTIVITY - 00071 - ACTIVITY.ENTRY.UPDATED - MODERATED COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateActivityEntry_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will update the description of the entry
		String entryDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryDescriptionEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, entryDescriptionEdit}, null, false);
		}
		ui.endTest();			
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PrivateEntry_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
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
	public void test_UpdateEntry_PrivateEntry_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a private entry in the activity and will update the description of the entry
		String entryDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryDescriptionEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create and update events for the private entry are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, updateEntryEvent, baseActivityEntry.getDescription(), entryDescriptionEdit}, TEST_FILTERS, false);
		
		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a todo in the activity</B></li>	
	 *<li><B>Step: User 1 update the todo in the activity</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of the activity.todo.updated appears in the views</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/14D25843682003BA85257A6300368CA0">TTT -  AS - FOLLOW - ACTIVITY - 00081 - ACTIVITY.TO-DO.UPDATED - MODERATE COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will create a to-do item in the activity and will edit the description of that to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, communityActivity, newDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update event for the to-do item is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_EditPrivateTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
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
	public void test_EditPrivateTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will create a private to-do item in the activity and will edit the description of that to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, communityActivity, newDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update event for the to-do item is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a completed todo in the community</B></li>	
	 *<li><B>Step: User 1 reopen the completed todo</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the activity.todo.reopened story appears in the views</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5B78F61BFFAF4D9885257A63002F6052">TTT -  AS - FOLLOW - ACTIVITY - 00091 - ACTIVITY.TO-DO.REOPENED - MODERATE COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_ReopenTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, will mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}	
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_PrivateTodo_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a moderated community you are the owner of that has an activity</B></li>
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
	public void test_ReopenTodo_PrivateTodo_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity, will mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}