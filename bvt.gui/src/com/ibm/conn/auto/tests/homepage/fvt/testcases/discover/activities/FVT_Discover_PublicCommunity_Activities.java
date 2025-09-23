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

public class FVT_Discover_PublicCommunity_Activities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };

	private Activity communityActivity;
	private ActivityEntry privateEntry, publicEntry;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseActivity baseActivity;
	private BaseActivityEntry basePrivateEntry, basePublicEntry;
	private BaseActivityToDo basePrivateTodo, basePublicTodo;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
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

		// User 1 will now create a public community with the Activities widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now add an activity to the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, publicCommunity);
		
		// Set all other relevant global test components to null
		privateEntry = null;
		publicEntry = null;
		privateTodo = null;
		publicTodo = null;
		privateReply = null;
		publicReply = null;
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_CreateActivity_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Create a new community with public access</B></li>
	*<li><B>Step: Create a new activity within this community</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DFD4A5B7058F8F02852578760079E721">TTT - DISC - ACTIVITY - 00010 - ACTIVITY.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CreateActivity_PublicCommunity() {

		ui.startTest();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_CreateActivityEntry_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/03F07535FB26F752852578760079E725">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateActivityEntry_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity entry event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEntryEvent, basePublicEntry.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_CreateActivityPrivateEntry_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is not seen - negative test</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8A7F70B488023F73852578760079E72E">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_CreateActivityPrivateEntry_PublicCommunity() {

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
	* test_CreateActivityTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new todo in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46E8CDFCF008C28A852578760079E733">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 4)
	public void test_CreateActivityTodo_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do item event is displayed in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePublicTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	* Test_CreateActivityPrivateTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a new todo in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8C23EAD81E6F24B5852578760079E737">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void Test_CreateActivityPrivateTodo_PublicCommunity() {

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
	* test_UpdateActivityEntry_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a public community you own that has an activity</B></li>
	*<li><B>Step: User 1 go to a public entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.entry.updated story is shown in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5AD41C7E522762EE85257A62004445C9">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 6)
	public void test_UpdateActivityEntry_publicCommunity() {

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
		
		// Verify that the update activity entry event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateActivityEntryEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();		
	}
	
	/**
	* test_UpdateActivityEntry_PrivateEntry_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a public community you own that has an activity</B></li>
	*<li><B>Step: User 1 go to a private entry in the activity</B></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that there is no story in the views of the activity.entry.updated</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6421F2BAFBE5F0BF85257A6200444700">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY/PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 7)
	public void test_UpdateActivityEntry_PrivateEntry_publicCommunity() {

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
	* test_EditActivityTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story appears in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B35CBE87FE9CA51085257A630039E157">TTT - DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 8)
	public void test_EditActivityTodo_PublicCommunity() {

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

		// Verify that the update to-do item event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_EditPrivateTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a private todo in the activity</B></li>
	*<li><B>Step: User 1 update the todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All, Activities & Communities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in any of the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D6E0B7F9FF4A65C585257A630039E286">TTT -  DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 9)
	public void test_EditPrivateTodo_PublicCommunity() {

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
	* test_CreateActivityEntryPrivateComment_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a comment on an entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is not seen - negative test</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBE83347F9F21A99852578760079E730">TTT - DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 10)
	public void test_CreateActivityEntryPrivateComment_PublicCommunity() {

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
	* test_UpdateActivityEntryReply_PrivateEntry_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Update an existing comment on an entry in this activity and select the checkbox "Mark this entry private"</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7007F25AAC830325852579BC005789A5">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 11)
	public void test_UpdateActivityEntryReply_PrivateReply_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a private comment to the public entry
		createPrivateReplyIfRequired();
				
		// User 1 will now edit the private comment on the public entry
		String originalReplyContent = privateReply.getContent().trim();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.editComment(testUser1, activitiesAPIUser1, privateReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnActivityEntryEvent, updateCommentEvent, originalReplyContent, user1EditedComment}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateActivityEntryComment_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Create a comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E001E0E5AE178A49852578760079E72B">TTT -  DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 12)
	public void test_CreateActivityEntryComment_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnActivityEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the comment on activity entry event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnActivityEntryEvent, publicEntry.getContent().trim(), publicReply.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateActivityReply_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that you are a member of and already has an activity</B></li>
	*<li><B>Step: Update an existing comment on an entry in this activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/984202F88CEEBAAB852579BC00575710">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 13)
	public void test_UpdateActivityEntryReply_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public entry to the community activity
		createPublicEntryIfRequired();
						
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
				
		// User 1 will now edit the public comment on the public entry
		String originalReplyContent = publicReply.getContent().trim();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.editComment(testUser1, activitiesAPIUser1, publicReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, publicEntry.getContent().trim(), user1EditedComment}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, originalReplyContent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* test_CompleteActivityTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a public community that contains an activity that you are a member of and contains todo items in this activity</B></li>
	*<li><B>Step: Mark the todo as completed</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.completed is seen</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/664452F2B75A6A00852578760079E73C">TTT - DISC - ACTIVITY - 00050 - ACTIVITY.TO-DO.COMPLETED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 14)
	public void test_CompleteActivityTodo_PublicCommunity() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now mark the public to-do item as completed
		publicTodo = CommunityActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete to-do item event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_ReopenActivityTodo_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a public community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a completed public todo in the activity</B></li>
	*<li><B>Step: User 1 reopen the complete public todo in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All, Activities & Communities</b></li>
	*<li><B>Verify: Verify that there is a story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/88CD95BA221DF7A885257A6200493BF1">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 15)
	public void test_ReopenActivityTodo_PublicCommunity() {

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

		// Verify that the re-open to-do item event is seen in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();	
	}	
	
	/**
	* test_ReopenActivityTodo_PrivateEntry_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a public community you own with an activity</B></li>
	*<li><B>Step: User 1 go to a completed private todo in the activity</B></li>
	*<li><B>Step: User 1 reopen the complete private todo in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All, Activities & Communities</b></li>
	*<li><B>Verify: Verify that there is no story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87101DD1821D881885257A6200494070">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - PUBLIC COMMUNITY ACTIVITY / PRIAVTE TO-DO (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 16)
	public void test_ReopenActivityTodo_PrivateEntry_PublicCommunity() {

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
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, basePrivateTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

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