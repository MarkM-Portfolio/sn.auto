package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.activities;

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
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                               	 */
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

public class FVT_Discover_PrivateCommunity_Activities extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };

	private Activity communityActivity;
	private ActivityEntry privateEntry, publicEntry;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseActivity baseActivity;
	private BaseActivityEntry basePrivateEntry, basePublicEntry;
	private BaseActivityToDo basePrivateTodo, basePublicTodo;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);

		// User 1 will now create a restricted community with the Activities widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now add an activity to the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
	
		// Set all other relevant global test components to null
		privateEntry = null;
		publicEntry = null;
		privateTodo = null;
		publicTodo = null;
		privateReply = null;
		publicReply = null;
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* test_CreateActivity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Create a new community with private access</B></li>
	*<li><B>Step: Create a new activity within this community</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/50DDC1C5C74E2828852578760079E720">TTT - DISC - ACTIVITY - 00010 - ACTIVITY.CREATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CreateActivity_PrivateCommunity() {

		ui.startTest();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create activity event is is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateActivityEntry_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is not seen - negative test</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/82FB16635B1FB66E852578760079E727">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateActivityEntry_PrivateCommunity() {

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEntryEvent, basePublicEntry.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateActivityPrivateEntry_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new private entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is NOT seen</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_CreateActivityEntry_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_CreateActivityPrivateEntry_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a private entry to the community activity
		createPrivateEntryIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, basePrivateEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEntryEvent, basePrivateEntry.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateActivityTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new todo in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4EB9832C8045D372852578760079E735">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 4)
	public void test_CreateActivityTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePublicTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();		
	}
	
	/**
	* Test_CreateActivityPrivateTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new private todo in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is NOT seen</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_CreateActivityTodo_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void Test_CreateActivityPrivateTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a private to-do item to the community
		createPrivateTodoIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePrivateTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();		
	}
	
	/**
	* test_UpdateActivityEntry_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a private community you own that has an activity</B></li>
	*<li><B>Step: User 1 go to a public entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that there is no story in the views of the activity.entry.updated</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ACBFC968D359809F85257A6200458299">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 6)
	public void test_UpdateActivityEntry_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
		
		// User 1 will now edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicEntry = CommunityActivityEvents.editEntryDescription(testUser1, activitiesAPIUser1, publicEntry, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateActivityEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the update activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateActivityEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateActivityEntry_PrivateEntry_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a private community you own that has an activity</B></li>
	*<li><B>Step: User 1 go to a private entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.entry.updated story is NOT shown in the views</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_UpdateActivityEntry_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 7)
	public void test_UpdateActivityEntry_PrivateEntry_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a private entry to the community activity
		createPrivateEntryIfRequired();
				
		// User 1 will now edit the description of the private entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		privateEntry = CommunityActivityEvents.editEntryDescription(testUser1, activitiesAPIUser1, privateEntry, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateActivityEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, basePrivateEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
				
		// Verify that the update activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateActivityEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_EditActivityTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a private community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9A5041EBE0451B9685257A630039E508">TTT - DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - PRIVATE COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 8)
	public void test_EditActivityTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now edit the description of the public to-do item
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicTodo = CommunityActivityEvents.editTodoDescription(testUser1, activitiesAPIUser1, publicTodo, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_EditPrivateTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a private community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a private todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in any of the views</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_EditActivityTodo_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 9)
	public void test_EditPrivateTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();

		// User 1 will now edit the description of the private to-do item
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		privateTodo = CommunityActivityEvents.editTodoDescription(testUser1, activitiesAPIUser1, privateTodo, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateActivityEntryPrivateComment_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a private comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is NOT seen</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_CreateActivityEntryComment_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 10)
	public void test_CreateActivityEntryPrivateComment_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a private comment to the public entry
		createPrivateReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnActivityEntryEvent, privateReply.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_UpdateActivityEntryComment_PrivateEntry_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Update an existing private comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is NOT seen</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_UpdateActivityEntryReply_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 11)
	public void test_UpdateActivityEntryComment_PrivateReply_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a private comment to the public entry
		createPrivateReplyIfRequired();
		
		// User 1 will now edit the private comment on the public entry
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.editComment(testUser1, activitiesAPIUser1, privateReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateCommentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateCommentOnActivityEntryEvent, publicEntry.getContent().trim(), user1EditedComment}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateActivityEntryComment_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A40FE9837FA45C53852578760079E72D">TTT - DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 12)
	public void test_CreateActivityEntryComment_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the comment on activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnActivityEntryEvent, publicEntry.getContent().trim(), publicReply.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateActivityEntryReply_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Update an existing comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/05420527E4E688EE852579BC005721D0">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 13)
	public void test_UpdateActivityEntryReply_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
						
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
				
		// User 1 will now edit the public comment on the public entry
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.editComment(testUser1, activitiesAPIUser1, publicReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateCommentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the update comment on activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateCommentOnActivityEntryEvent, publicEntry.getContent().trim(), user1EditedComment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CompleteActivityTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a private community that contains an activity that you are a member of and contains todo items in this activity</B></li>
	*<li><B>Step: Mark the todo as completed</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.completed is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B61AFFDD1D218DC852578760079E73B">TTT - DISC - ACTIVITY - 00050 - ACTIVITY.TO-DO.COMPLETED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
 	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 14)
	public void test_CompleteActivityTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now mark the public to-do item as completed
		publicTodo = CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ReopenActivityTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a private community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a completed public todo in the activity</B></li>
	*<li><B>Step: User 1 reopen the complete public todo in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All, Activities & Communities</b></li>
	*<li><B>Verify: Verify that there is no story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A8247DDC2BC886F785257A62004A3A1D">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - PRIVATE COMMUNITY ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 15)
	public void test_ReopenActivityTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		if(publicTodo.isComplete() == false) {
			// User 1 will now mark the public to-do item as completed
			publicTodo = CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		}
		// User 1 will now re-open the public to-do item again
		CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, false);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the re-open to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ReopenActivityTodo_PrivateTodo_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a private community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a completed private todo in the activity</B></li>
	*<li><B>Step: User 1 reopen the complete private todo in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All, Activities & Communities</b></li>
	*<li><B>Verify: Verify that there is NOT a story of the activity.todo.reopened in the views</B></li>
	*<li>There is no TTT link for this test - it is a private version of the test_ReopenActivityTodo_PrivateCommunity() test case, above</li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 16)
	public void test_ReopenActivityTodo_PrivateTodo_PrivateCommunity(){

		ui.startTest();

		// User 1 will now add a private to-do item to the activity
		createPrivateTodoIfRequired();
				
		// User 1 will now mark the private to-do item as completed
		CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, privateTodo, true);
		
		// User 1 will now re-open the private to-do item again
		CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, privateTodo, false);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String reopenTodoEvent  = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the re-open to-do item event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, privateTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	private void createPublicEntryIfRequired() {
		if(publicEntry == null) {
			// User 1 will now add a public entry to the community activity
			basePublicEntry = ActivityBaseBuilder.buildBaseActivityEntry(getClass().getSimpleName() + Helper.genStrongRand(), communityActivity, false);
			publicEntry = CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, basePublicEntry, communityActivity);
		}
	}
	
	private void createPrivateEntryIfRequired() {
		if(privateEntry == null) {
			// User 1 will now add a private entry to the community activity
			basePrivateEntry = ActivityBaseBuilder.buildBaseActivityEntry(getClass().getSimpleName() + Helper.genStrongRand(), communityActivity, true);
			privateEntry = CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, basePrivateEntry, communityActivity);
		}
	}
	
	private void createPublicTodoIfRequired() {
		if(publicTodo == null) {
			// User 1 will now add a public to-do item to the activity
			basePublicTodo = ActivityBaseBuilder.buildBaseActivityToDo(getClass().getSimpleName() + Helper.genStrongRand(), communityActivity, false);
			publicTodo = CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, basePublicTodo, communityActivity);
		}
	}
	
	private void createPrivateTodoIfRequired() {
		if(privateTodo == null) {
			// User 1 will now add a private to-do item to the community
			basePrivateTodo = ActivityBaseBuilder.buildBaseActivityToDo(getClass().getSimpleName() + Helper.genStrongRand(), communityActivity, true);
			privateTodo = CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, basePrivateTodo, communityActivity);
		}
	}
	
	private void createPublicReplyIfRequired() {
		if(publicReply == null) {
			// User 1 will now add a public comment to the public entry
			String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
			publicReply = CommunityActivityEvents.createComment(communityActivity, publicEntry, null, user1Comment, testUser1, activitiesAPIUser1, false);
		}
	}
	
	private void createPrivateReplyIfRequired() {
		if(privateReply == null) {
			// User 1 will now add a private comment to the public entry
			String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
			privateReply = CommunityActivityEvents.createComment(communityActivity, publicEntry, null, user1Comment, testUser1, activitiesAPIUser1, true);
		}	
	}
}