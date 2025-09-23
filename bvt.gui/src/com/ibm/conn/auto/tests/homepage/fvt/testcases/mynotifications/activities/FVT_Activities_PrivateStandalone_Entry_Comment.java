package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                          		     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_Activities_PrivateStandalone_Entry_Comment extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = {HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities};
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2, activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8, activitiesAPIUser9;
	private BaseActivity baseActivity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(9);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		activitiesAPIUser3 = initialiseAPIActivitiesHandlerUser(testUser3);
		activitiesAPIUser4 = initialiseAPIActivitiesHandlerUser(testUser4);
		activitiesAPIUser5 = initialiseAPIActivitiesHandlerUser(testUser5);
		activitiesAPIUser6 = initialiseAPIActivitiesHandlerUser(testUser6);
		activitiesAPIUser7 = initialiseAPIActivitiesHandlerUser(testUser7);
		activitiesAPIUser8 = initialiseAPIActivitiesHandlerUser(testUser8);
		activitiesAPIUser9 = initialiseAPIActivitiesHandlerUser(testUser9);

		// User 1 will now create a public activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// User 1 will now add Users 2 through to 9 to the activity as members
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9 };
		ActivityEvents.addMemberMultipleUsers(publicActivity, testUser1, activitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_YourEntry_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an entry in the Activity</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 4, User 5, User 6 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {entry} entry thread in the{activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 6} and 4 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EFF0B43F79D9C81285257DE7005B137E">TTT - MY NOTIFICATIONS - ACTIVITIES - 00010 - COMMENT ON ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Comment_Rollup(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity);
		
		// User 2 will now post a comment to the activity entry
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, activityEntry, null, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the activity entry
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, activityEntry, null, user3Comment, testUser3, activitiesAPIUser3, false);
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseActivityEntry.getTitle(), baseActivity.getName(), testUser3.getDisplayName());
		
		// Verify that the comment on entry event and User 2's and User 3's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment, user3Comment}, TEST_FILTERS, true);
		
		// Users 4, 5 and 6 will now post a comment to the activity entry
		User[] usersCommenting = { testUser4, testUser5, testUser6 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6 };
		String[] userComments = new String[usersCommenting.length];
				
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createComment(publicActivity, activityEntry, null, userComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, "4", baseActivityEntry.getTitle(), baseActivity.getName(), testUser6.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 5's and User 6's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), userComments[1], userComments[2]}, filter, true);
			
			// Verify that the comments posted by Users 2 through to User 4 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, userComments[0]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourEntry_CommentUpdate_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an entry in the Activity</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the entry and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 4, User 5, User 6 comment on the entry and then User 4 update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {entry} entry thread in the{activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 6} and 4 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E2572B5AF21A485F85257DE7005C42C3">TTT - MY NOTIFICATIONS - ACTIVITIES - 00020 - UPDATED COMMENT ON ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourEntry_CommentUpdate_Rollup(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity);
		
		// User 2 will now post a comment to the activity entry and will edit / update the comment
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, activityEntry, null, user2Comment, user2EditedComment, testUser2, activitiesAPIUser2, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2EditedComment}, filter, true);
			
			// Verify that User 2's original comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, false);
		}
		
		// User 3 will now post a comment to the activity entry  and will edit / update the comment
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user3EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, activityEntry, null, user3Comment, user3EditedComment, testUser3, activitiesAPIUser3, false);
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseActivityEntry.getTitle(), baseActivity.getName(), testUser3.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's and User 3's updated comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2EditedComment, user3EditedComment}, filter, true);
			
			// Verify that User 2's and User 3's original comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment}, null, false);
		}
		
		// Users 4, 5 and 6 will now post a comment to the activity entry
		User[] usersCommenting = { testUser4, testUser5, testUser6 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6 };
		String[] userComments = new String[usersCommenting.length];
		Reply[] userReplies = new Reply[usersCommenting.length];
		
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			userReplies[index] = ActivityEvents.createComment(publicActivity, activityEntry, null, userComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
		
		// User 4 will now edit their reply
		String user4EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.editComment(testUser4, activitiesAPIUser4, userReplies[0], user4EditedComment);
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, "4", baseActivityEntry.getTitle(), baseActivity.getName(), testUser6.getDisplayName());
				
		for(String filter: TEST_FILTERS) {
			// Verify that the comment on entry event and User 5's and User 4's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user4EditedComment, userComments[2]}, filter, true);
			
			// Verify that none of the other comments are displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user2EditedComment, user3Comment, user3EditedComment, userComments[0], userComments[1]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourToDo_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an to do item in the Activity</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 4, User 5, User 6, User 7 and User 8 comment on the to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {entry} entry thread in the{activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 8} and 6 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A525CC01C41A384D85257DE7005BE7DC">TTT - MY NOTIFICATIONS - ACTIVITIES - 00030 - COMMENT ON A TO-DO ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourToDo_Comment_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// User 2 will now post a comment to the to-do item
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, null, todo, user2Comment, testUser2, activitiesAPIUser2, false);
				
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
				
		// Verify that the comment on to-do event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2Comment}, TEST_FILTERS, true);
				
		// User 3 will now post a comment to the to-do item
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, null, todo, user3Comment, testUser3, activitiesAPIUser3, false);
				
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseActivityTodo.getTitle(), baseActivity.getName(), testUser3.getDisplayName());
				
		// Verify that the comment on to-do event and User 2's and User 3's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2Comment, user3Comment}, TEST_FILTERS, true);
				
		// Users 4 through to 8 will now post a comment to the to-do item
		User[] usersCommenting = { testUser4, testUser5, testUser6, testUser7, testUser8 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8 };
		String[] userComments = new String[usersCommenting.length];
				
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createComment(publicActivity, null, todo, userComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
				
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, "6", baseActivityTodo.getTitle(), baseActivity.getName(), testUser8.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on to-do event and User 7's and User 8's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), userComments[3], userComments[4]}, filter, true);
			
			// Verify that the comments posted by all other users are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, userComments[0], userComments[1], userComments[2]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourToDo_CommentUpdate_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an to-do item in the Activity</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the to-do item and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the to-do item and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 4, User 5, User 6, User 7, User 8, User 9 comment on the todo and all edit their comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your {entry} entry thread in the{activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "{user 9} and 7 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3623CFD7A5D7586785257DE7005BE95E">TTT - MY NOTIFICATIONS - ACTIVITIES - 00040 - UPDATED COMMENT ON A TO-DO ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourToDo_CommentUpdate_Rollup(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, baseActivityTodo, publicActivity);
		
		// User 2 will now post a comment to the to-do item and will edit / update the comment
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, null, todo, user2Comment, user2EditedComment, testUser2, activitiesAPIUser2, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on to-do item event and User 2's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2EditedComment}, filter, true);
			
			// Verify that User 2's original comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, false);
		}
		
		// User 3 will now post a comment to the to-do item and will edit / update the comment
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user3EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, null, todo, user3Comment, user3EditedComment, testUser3, activitiesAPIUser3, false);
		
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseActivityTodo.getTitle(), baseActivity.getName(), testUser3.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on to-do item event and User 2's and User 3's updated comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user2EditedComment, user3EditedComment}, filter, true);
			
			// Verify that User 2's and User 3's original comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment}, null, false);
		}
		
		// Users 4 through to 9 will now post a comment to the to-do item and will edit / update the comment
		User[] usersCommenting = { testUser4, testUser5, testUser6, testUser7, testUser8, testUser9 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8, activitiesAPIUser9 };
		String[] userComments = new String[usersCommenting.length];
		String[] userEditedComments = new String[usersCommenting.length];
		Reply[] userReplies = new Reply[usersCommenting.length];
		
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			userEditedComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			userReplies[index] = ActivityEvents.createCommentAndEditComment(publicActivity, null, todo, userComments[index], userEditedComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
		
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, "7", baseActivityTodo.getTitle(), baseActivity.getName(), testUser9.getDisplayName());
				
		// Verify that the comment on to-do item event and User 8's and User 9's updated comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), userEditedComments[4], userEditedComments[5]}, TEST_FILTERS, true);
					
		ui.endTest();
	}	
}