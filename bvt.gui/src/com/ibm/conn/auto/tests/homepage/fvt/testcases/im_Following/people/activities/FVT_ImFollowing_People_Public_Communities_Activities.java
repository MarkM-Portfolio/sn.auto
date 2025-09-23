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
 * @author Ileana Belfiore - belfilie@ie.ibm.com
 */

public class FVT_ImFollowing_People_Public_Communities_Activities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];

	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
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
		
		// User 1 create a public community with the activities widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 will now create an activity in the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, publicCommunity);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// Remove all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* createActivity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.created is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B11AB85785FBAFBB852578FB002BB6D8">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00012 - activity.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void createActivity(){

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
	* createEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D785A50F38812492852578FB002C874B">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00024 - activity.entry.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createEntry(){

		String testName = ui.startTest();

		// User 1 will now add an activity entry to the community
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
	* createPrivateEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new private entry to the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/90D04F3F46343DCB852578FB002C8896">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00025 - activity.entry.created - PUBLIC COMMUNITY ACTIVITY - PRIVATE ENTRY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createPrivateEntry(){

		String testName = ui.startTest();

		// User 1 will now add a private activity entry to the community
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
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
	* addCommentToEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser1 creates a comment to activity entry</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/591A356F3B7374B4852578FB00304B58">AS - FOLLOW - PERSON - ACTIVITY - 00034 - activity.entry.comment.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void addCommentToEntry(){

		String testName = ui.startTest();

		// User 1 will now add an activity entry to the community and will post a comment to that entry
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
	* addPrivateCommentToEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new entry to the activity</B></li>
	*<li><B>Step: testUser1 creates a private comment to activity entry</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF7E7A09DBA8A6BD852578FB0030503E">AS - FOLLOW - PERSON - ACTIVITY - 00035 - activity.entry.comment.created - PUBLIC COMMUNITY ACTIVITY - PRIVATE COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void addPrivateCommentToEntry(){

		String testName = ui.startTest();

		// User 1 will now add an activity entry to the community and will post a private comment to that entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, true);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* createToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new to do within the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/24C858EDC445AF6A852578FB0036F8D8">AS - FOLLOW - PERSON - ACTIVITY - 00045 - activity.todo.created - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createToDo(){

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
	* createPrivateToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new private to do within the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A866B545D404AE5A852578FB0036FA15">AS - FOLLOW - PERSON - ACTIVITY - 00046 - activity.todo.created - PUBLIC COMMUNITY ACTIVITY - PRIVATE TO DO</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void createPrivateToDo(){

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
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
	* completeToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 creates a new to do within the activity</B></li>
	*<li><B>Step: testUser1 marks the todo as complete</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the story for activity.todo.completed is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E77BE9615ECA16DF852578FB00390262">AS - FOLLOW - PERSON - ACTIVITY - 00053 - activity.todo.completed - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void completeToDo(){

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
	* updateComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates an existing comment to activity entry</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1C9E568B3121B42B852579BF00441EA4">AS - FOLLOW - PERSON - ACTIVITY - 00074 - activity.reply.updated - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateComment(){

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
	* updatePrivateComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates an existing PRIVATE comment to activity entry</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AEA0AFB2D8B7470E852579BF004463AF">AS - FOLLOW - PERSON - ACTIVITY - 00075 - activity.reply.updated - PUBLIC COMMUNITY ACTIVITY - PRIVATE COMMENT (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatePrivateComment(){

		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity, post a private comment to that entry and then update the private comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, true, replyEdit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event, update event, original comment and updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, updateCommentEvent, comment, replyEdit}, TEST_FILTERS, false);
			
		ui.endTest();
	}
	
	/**
	* updateEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates an entry in the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9AFDCEEFE3ECC23285257A630032E7EB">AS - FOLLOW - PERSON - ACTIVITY - 00083 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateEntry(){

		String testName = ui.startTest();

		// User 1 will now add an activity entry to the community and will update the description of the entry
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
	* updatePrivateEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates a private entry in the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B1726D7471B387E485257A630032E949">AS - FOLLOW - PERSON - ACTIVITY - 00084 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatePrivateEntry(){

		String testName = ui.startTest();
		
		// User 1 will now add a private activity entry to the community and will update the description of the private entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
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
	* updateToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates a to do in the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/351CE9E7F920206D85257A64003347A1">AS - FOLLOW - PERSON - ACTIVITY - 00093 - ACTIVITY.TO DO.UPDATED - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateToDo(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will update the description of the to-do item
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
	* updatePrivateToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 updates a private to do in the activity</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.updated is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9D782D6FC8708E1A85257A64003349BA">AS - FOLLOW - PERSON - ACTIVITY - 00094 - ACTIVITY.TO DO.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRVATE ENTRY (NEG) (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatePrivateToDo(){

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity and will update the description of the private to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
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
	* reopenToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 go to a completed to do in the activity</B></li>
	*<li><B>Step: testUser1 reopens the todo</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.reopened is displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/645144E6BECD1C2C85257A640039B943">AS - FOLLOW - PERSON - ACTIVITY - 00103 - ACTIVITY.TO DO.REOPENED - PUBLIC COMMUNITY ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void reopenToDo(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, mark the to-do item as completed and then re-open the to-do item
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
	* reopenPrivateToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a new community with public access</B></li>
	*<li><B>Step: testUser2 follows testUser1</B></li>
	*<li><B>Step: testUser1 creates a new activity within this community</B></li>
	*<li><B>Step: testUser1 go to a completed private to do in the activity</B></li>
	*<li><B>Step: testUser1 reopens the private todo</B></li>
	*<li><B>Step: testUser2 login to Homepage / All Updates / Communities & People & Activities</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.reopened is NOT displayed in the Communities, People and Activities view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1E7ACA249FA6F5A685257A640039BB3B">AS - FOLLOW - PERSON - ACTIVITY - 00104 - ACTIVITY.TO DO.REOPENED - PUBLIC COMMUNITY ACTIVITY / PRIVATE TO DO (NEG)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void reopenPrivateToDo(){

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity, mark the private to-do item as completed and then re-open the private to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
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
	*<li><B>Name: test_Person_Mentions added in Entry Comment_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access add User 2 as a member</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity entry as User 1 mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F828EB99791EEE4485257C6D005A09AC">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00124 - Mentions added in Entry Comment - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha	
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void MentionsaddedinEntryComment(){
		
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
	*<li><B>Name: test_Person_Mentions added in Entry Comment_publicCommunity_Private Comment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity entry as User 1 mentioning User 3- mark the comment private </B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does NOT appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AED24261133B955185257C6D005A09AB">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00125 - Mentions added in Entry Comment - PUBLIC COMMUNITY ACTIVITY - PRIVATE COMMENT</a></li>
	* @author Srinivas Vechha	
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void MentionsaddedinEntryComment_PrivComment(){
		
		String testName = ui.startTest();

		// User 1 will now create an activity entry in the activity and will post a private comment with mentions to User 3 to that entry
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, mention, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();

		// Verify that the comment event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);
				
		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_ Mentions added in ToDo Comment_publicCommunityActivity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity todo as User 1 mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/40799D0B2109476885257C6D005B2FC3">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00134 - Mentions added in ToDo Comment - PUBLIC COMMUNITY ACTIVITY</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)		
	public void MentionsaddedinTODOComment(){
			
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
	
	/**
	*<ul>
	*<li><B>Name: test_Person_ Mentions added in ToDo Comment_publicCommunityActivity_Priv Comment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Create a new activity within this community </B></li>
	*<li><B>Step: testUser 1 Create a comment to activity todo as User 1 mentioning User 3- mark the comment private </B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Activities, Communities & People</B></li>
	*<li><B>Verify: Verify the mentions event does Not appear in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/206066C88F63815485257C6D005B2FC2">TTT - AS - FOLLOW - PERSON - ACTIVITY - 00135 - Mentions added in ToDo Comment - PUBLIC COMMUNITY ACTIVITY - PRIVATE COMMENT</a></li>
	* @author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)		
	public void MentionsaddedinTODOPrivComment(){
			
		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will post a private comment with mentions to User 3 to that to-do item
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityTodo, communityActivity, mention, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + mention.getUserToMention().getDisplayName() + " " + mention.getAfterMentionText();
		
		// Verify that the comment event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);
				
		ui.endTest();
	}	
}