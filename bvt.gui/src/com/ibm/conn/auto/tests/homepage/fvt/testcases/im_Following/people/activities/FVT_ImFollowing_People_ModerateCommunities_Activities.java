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

public class FVT_ImFollowing_People_ModerateCommunities_Activities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];

	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community with the activities widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, moderatedCommunity);		
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateActivity_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.created is displayed in the Communities, People and Activities views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFC38944D5666B2D852578FB002BB7FB">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00013 - activity.created - MODERATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_People_CreateActivity_ModerateCommunity(){
		
		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, baseActivity.getGoal()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is displayed in the Communities, People and Activities views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFD00BB4AFA534BB852578FB002C89B9">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00026 - activity.entry.created - MODERATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CreateEntry_ModerateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
			
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_addCommentToEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>	
	*<li><B>Step: testUser 1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser 1 creates a comment to activity entry</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is displayed in the Communities, People and Activities views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/98B6C5E421BBC89C852578FB0030517F"> AS - FOLLOW - PERSON - ACTIVITY - 00036 - activity.entry.comment.created - MODERATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_addCommentToEntry_ModerateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will comment on the entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateToDo_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>	
	*<li><B>Step: testUser 1 Create a todo within the activity entry as User 1</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9F892CDFA1CCA7F9852578FB0036FB5F"> AS - FOLLOW - PERSON - ACTIVITY - 00047 - activity.todo.created - MODERATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CreateToDo_ModerateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTodoEvent = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the create to-do event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CompleteToDo_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 creates a new to do within the activity</B></li>
	*<li><B>Step: testUser 1 marks the todo as complete</B></li>
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the story for activity.todo.completed is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/40B80612D43CD64C852578FB0039038B"> AS - FOLLOW - PERSON - ACTIVITY - 00054 - activity.todo.completed - MODERATE COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_CompleteToDo_ModerateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will mark the to-do item as completed
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String completeTodoEvent = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the complete to-do event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
			
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateComment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with Moderate access, add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 follows testUser1</B></li>
	*<li><B>Step: testUser 1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser 1 Update an existing comment to activity entry as User 1</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is NOT displayed in the any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8EE2D782349148E9852579BF00448DC9"> AS - FOLLOW - PERSON - ACTIVITY - 00076 - activity.reply.updated - MODERATE COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateComment_ModerateCommunity(){
		
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
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 go to a moderate community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to an entry in the activity</B></li>
	*<li><B>Step: testUser 1 update the entry</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.entry.updated story does NOT appear in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D03807FE5CA903585257A630032E514"> AS - Follow - Person - activity - 00081 - activity.entry.updated - moderate community activity (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateEntry_ModerateCommunity(){
		
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
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, null, false);
		}	
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_updateToDo_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into a moderate community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to the activity in the community</B></li>
	*<li><B>Step: testUser 1 go to a todo in the activity</B></li>	
	*<li><B>Step: testUser 1 update the todo</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E6ED564794A5AB9A85257A640033432B"> AS - Follow - Person - activity - 00091 - activity.todo.updated - moderate community activity (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_updateToDo_ModerateCommunity(){
		
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

		for(String filter : TEST_FILTERS) {
			// Verify that the create to-do event and original description are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTodoEvent, baseActivityTodo.getDescription()}, filter, true);
			
			// Verify that the update to-do event and updated description are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTodoEvent, newDescription}, null, false);
		}	
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_reopenToDo_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into a moderate community you own with an activity</B></li>
	*<li><B>Step: testUser 2 ensure you are following User 1</B></li>
	*<li><B>Step: testUser 1 go to the activity in the community</B></li>
	*<li><B>Step: testUser 1 go to a completed todo in the activity</B></li>	
	*<li><B>Step: testUser 1 reopen the todo</B></li>	
	*<li><B>Step: testUser 2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the activity.todo.reopened story appears in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81D797D14B1808C885257A640039B5CA"> AS - Follow - Person - activity - 00101 - activity.todo.reopened - moderate community activity</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_reopenToDo_ModerateCommunity(){
		
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, will mark it as completed and will re-open the to-do item again
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String reopenTodoEvent = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify that the reopen to-do event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, baseActivityTodo.getDescription()}, TEST_FILTERS, true);
						
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_Mentions added in Entry Comment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with Moderate access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity entry as User 1 mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/674F67ADB533C2C085257C6D005A09AA">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00126 - Mentions added in Entry Comment - MODERATE COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha	
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_MentionsaddedinEntryComment(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will post a comment with mentions to User 3 to that entry
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, mention, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify that the comment event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), mentionsText}, TEST_FILTERS, true);
				
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_ Mentions added in ToDo Comment_ModerateCommunityActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with Moderate access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity todo as User 1 mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/50743855401E4C9685257C6D005B2FC1">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00136 - Mentions added in ToDo Comment - MODERATE COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)		
	public void test_People_MentionsaddedinTODOComment(){
			
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will post a comment with mentions to User 3 to that to-do item
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity, mention, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();
		
		// Verify that the comment event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseActivityTodo.getDescription(), mentionsText}, TEST_FILTERS, true);
				
		ui.endTest();		
	}
}