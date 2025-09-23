package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.activities;

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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following / People) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 06/2015
 */

public class FVT_ImFollowing_People_PrivateComm_Activities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];

	private Activity communityActivity, communityActivityWithMembers;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity, baseActivityWithMembers;
	private BaseCommunity baseCommunity, baseCommunityWithMembers;
	private Community restrictedCommunity, restrictedCommunityWithMembers;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);

		// User 2 will follow User 1 through the API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		
		// Add the commonly used filters to the TEST_FILTERS array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterActivities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterCommunities;
		
		// User 1 create a private community with no members and add the Activities widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a private community with multiple members
		User membersToAdd[] = { testUser2, testUser3 };
		baseCommunityWithMembers = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunityWithMembers = CommunityEvents.createNewCommunityWithMultipleMembersAndAddWidget(baseCommunityWithMembers, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1, membersToAdd);
				
		// User 1 will now create an activity in the community with no members
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
		
		// User 1 will now create an activity in the community with multiple members
		baseActivityWithMembers = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunityWithMembers);
		communityActivityWithMembers = CommunityActivityEvents.createCommunityActivity(baseActivityWithMembers, baseCommunityWithMembers, testUser1, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunityWithMembers);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// Remove all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunityWithMembers);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateActivity_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.created is NOT displayed in the Communities, People and Acticities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CF4ACEB602D93D39852578FB002BB945">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00014 - activity.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_People_CreateActivity_PrivateCommunity(){
		
		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is NOT displayed in the Communities, People and Activity views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D75B3B85617B4B8D852578FB002C8B0F">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00027 - activity.entry.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CreateEntry_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, false);
			
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_People_addCommentToEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>	
	*<li><B>Step: testUser 1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser 1 creates a comment to activity entry</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is not displayed in the Communities, People and Activities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/342C402B63B6F013852578FB003052C5"> AS - FOLLOW - PERSON - ACTIVITY - 00037 - activity.entry.comment.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_addCommentToEntry_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will comment on the entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_People_CreateToDo_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>	
	*<li><B>Step: testUser 1 Create a todo within the activity entry as User 1</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8ECBF5CA7E684CC8852578FB00382A86"> AS - FOLLOW - PERSON - ACTIVITY - 00048 - activity.todo.created - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CreateToDo_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CompleteToDo_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 creates a new to do within the activity</B></li>
	*<li><B>Step: testUser 1 marks the todo as complete</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the story for activity.todo.completed is not displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/175FA6FF2D35D7C0852578FB003904AC"> AS - FOLLOW - PERSON - ACTIVITY - 00055 - activity.todo.completed - PRIVATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CompleteToDo_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will mark the to-do item as completed
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete to-do event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
			
		ui.endTest();
	}		
	
	/**
	*<ul>
	*<li><B>Name: test_People_PrivateCompleteToDo_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 Create a new activity todo - mark this todo as private</B></li>
	*<li><B>Step: testUser 1 Mark the todo as complete</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the story for activity.todo.completed is not displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CA424024FAAD2641852578FB003A1DF0"> AS - FOLLOW - PERSON - ACTIVITY - 00056 - activity.todo.completed - PRIVATE COMMUNITY ACTIVITY - PRIVATE TO DO</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_PrivateCompleteToDo_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity and will mark the private to-do item as completed
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete private to-do event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
			
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateComment_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Private access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 Update an existing comment to activity entry as User 1</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is NOT displayed in the any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F30C1A2E37286739852579BF0044BF03"> AS - FOLLOW - PERSON - ACTIVITY - 00077 - activity.reply.updated - PRIVATE COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateComment_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity, post a comment to that entry and then update the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false, replyEdit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event, update comment event, original comment and updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, updateCommentEvent, comment, replyEdit}, TEST_FILTERS, false);
					
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 go to a Private community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to an entry in the activity</B></li>
	*<li><B>Step: testUser 1 update the entry</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.entry.updated story does NOT appear in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/20E087F75998614B85257A630032E6A0"> AS - Follow - Person - activity - 00082 - activity.entry.updated - private community activity (neg) (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateEntry_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will update the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create entry event, update entry event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, updateEntryEvent, baseActivityEntry.getDescription(), editedDescription}, TEST_FILTERS, false);
			
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateToDo_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into a Private community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to the activity in the community</B></li>
	*<li><B>Step: testUser 1 go to a todo in the activity</B></li>	
	*<li><B>Step: testUser 1 update the todo</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FDCAB9D3C96EF44585257A64003345B9">  AS -Follow - Person - activity - 00092 - activity.todo.updated - private community activity (neg) (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateToDo_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will update the to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity, newDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do event, update to-do event, original description and updated description are NOT displayed in any of the views 
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, updateTodoEvent, baseActivityTodo.getDescription(), newDescription}, TEST_FILTERS, false);
			
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_reopenToDo_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into a Private community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to the activity in the community</B></li>
	*<li><B>Step: testUser 1 go to a completed todo in the activity</B></li>	
	*<li><B>Step: testUser 1 reopen the todo</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.todo.reopened story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B0E99D0073E6925485257A640039B765"> AS - Follow - Person - activity - 00102 - activity.todo.reopened - private community activity (neg)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_reopenToDo_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, will mark it as completed and will re-open the to-do item again
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the reopen to-do event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, false);
						
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_Mentions added in Entry Comment_Private Community()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with private access,</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity entry as User 1 mentioning User 3 </B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does NOT appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/79DE12C95AE9DDA285257C6D005A09A9">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00125 - Mentions added in Entry Comment - PUBLIC COMMUNITY ACTIVITY - PRIVATE COMMENT</a></li>
	* @author Srinivas Vechha	
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void MentionsaddedinEntryComment_PrivCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will post a comment with mentions to User 3 to that entry
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivityWithMembers, false);
		ActivityEntry newEntry = CommunityActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivityWithMembers, mention, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, newEntry.getTitle(), baseActivityWithMembers.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify that the comment event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), mentionsText}, TEST_FILTERS, false);
				
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_ Mentions added in ToDo Comment_privateCommunityActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with private access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity todo as User 1 mentioning User 3 </B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does Not appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8FABC8088AD3FBC985257C6D005B2FC0">TTT -AS - FOLLOW - PERSON - ACTIVITY - 00137 - Mentions added in ToDo Comment - PRIVATE COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)		
	public void MentionsaddedinTODOPPrivCommunity(){
			
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will post a comment with mentions to User 3 to that to-do item
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivityWithMembers, false);
		CommunityActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivityWithMembers, mention, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityTodo.getTitle(), baseActivityWithMembers.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();
		
		// Verify that the comment event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityTodo.getDescription(), mentionsText}, TEST_FILTERS, false);
				
		ui.endTest();
	}	
}