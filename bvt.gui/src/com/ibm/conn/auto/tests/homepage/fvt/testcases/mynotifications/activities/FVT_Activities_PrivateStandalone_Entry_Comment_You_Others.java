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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
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

public class FVT_Activities_PrivateStandalone_Entry_Comment_You_Others extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2, activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8, activitiesAPIUser9, activitiesAPIUser10, activitiesAPIUser11, activitiesAPIUser12, activitiesAPIUser13, activitiesAPIUser14, activitiesAPIUser15, activitiesAPIUser16, activitiesAPIUser17, activitiesAPIUser18, activitiesAPIUser19, activitiesAPIUser20;
	private BaseActivity baseActivity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18, testUser19, testUser20;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(20);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		testUser10 = listOfStandardUsers.get(9);
		testUser11 = listOfStandardUsers.get(10);
		testUser12 = listOfStandardUsers.get(11);
		testUser13 = listOfStandardUsers.get(12);
		testUser14 = listOfStandardUsers.get(13);
		testUser15 = listOfStandardUsers.get(14);
		testUser16 = listOfStandardUsers.get(15);
		testUser17 = listOfStandardUsers.get(16);
		testUser18 = listOfStandardUsers.get(17);
		testUser19 = listOfStandardUsers.get(18);
		testUser20 = listOfStandardUsers.get(19);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		activitiesAPIUser3 = initialiseAPIActivitiesHandlerUser(testUser3);
		activitiesAPIUser4 = initialiseAPIActivitiesHandlerUser(testUser4);
		activitiesAPIUser5 = initialiseAPIActivitiesHandlerUser(testUser5);
		activitiesAPIUser6 = initialiseAPIActivitiesHandlerUser(testUser6);
		activitiesAPIUser7 = initialiseAPIActivitiesHandlerUser(testUser7);
		activitiesAPIUser8 = initialiseAPIActivitiesHandlerUser(testUser8);
		activitiesAPIUser9 = initialiseAPIActivitiesHandlerUser(testUser9);
		activitiesAPIUser10 = initialiseAPIActivitiesHandlerUser(testUser10);
		activitiesAPIUser11 = initialiseAPIActivitiesHandlerUser(testUser11);
		activitiesAPIUser12 = initialiseAPIActivitiesHandlerUser(testUser12);
		activitiesAPIUser13 = initialiseAPIActivitiesHandlerUser(testUser13);
		activitiesAPIUser14 = initialiseAPIActivitiesHandlerUser(testUser14);
		activitiesAPIUser15 = initialiseAPIActivitiesHandlerUser(testUser15);
		activitiesAPIUser16 = initialiseAPIActivitiesHandlerUser(testUser16);
		activitiesAPIUser17 = initialiseAPIActivitiesHandlerUser(testUser17);
		activitiesAPIUser18 = initialiseAPIActivitiesHandlerUser(testUser18);
		activitiesAPIUser19 = initialiseAPIActivitiesHandlerUser(testUser19);
		activitiesAPIUser20 = initialiseAPIActivitiesHandlerUser(testUser20);

		// User 1 will now create a public activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// User 1 will now add Users 2 through to 20 to the activity as members
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18, testUser19, testUser20 };
		ActivityEvents.addMemberMultipleUsers(publicActivity, testUser1, activitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the activity now that the test has completed
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	* test_YourEntry_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an entry in the Activity</B></li>
	*<li><B>Step: User 1 comment on your own entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 12 comment on the entry</B></li>
	*<li><B>Step: User 1 comment on the entry again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "You and 11 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CDE43612A5819AD085257DE7005C42C4">TTT - MY NOTIFICATIONS - ACTIVITIES - 00011 - COMMENT ON YOUR OWN ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourEntry_Comment_You_Others_Rollup(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will post a comment to the entry
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, user1Comment1, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseActivityEntry.getTitle(), baseActivity.getName());
		
		// Verify that the comment on entry event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user1Comment1}, TEST_FILTERS, false);

		// User 2 will now post a comment to the activity entry
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, activityEntry, null, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());

		// Verify that the comment on entry event and User 2's and User 1's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment, user1Comment1}, TEST_FILTERS, true);
		
		// Users 3 through to 12 will now post a comment on the activity entry with User 1 then posting a second comment to the entry
		User[] usersCommenting = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser1 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8, activitiesAPIUser9, activitiesAPIUser10, activitiesAPIUser11, activitiesAPIUser12, activitiesAPIUser1 };
		String[] userComments = new String[usersCommenting.length];
		
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createComment(publicActivity, activityEntry, null, userComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, "11", baseActivityEntry.getTitle(), baseActivity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event, User 12's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), userComments[9], userComments[10]}, filter, true);
			
			// Verify that the comments posted by Users 2 through to User 11 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, userComments[0], userComments[1], userComments[2], userComments[3], userComments[4], userComments[5], userComments[6], userComments[7], userComments[8]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourEntry_CommentUpdate_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an entry in the Activity</B></li>
	*<li><B>Step: User 1 comment on the entry and then edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the entry and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 5 comment on the entry and then edit the comment</B></li>
	*<li><B>Step: User 1 comment on the entry again and then edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you updated your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "You and 4 others updated your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3289D75CA9B36BA285257DE7005C42C5">TTT - MY NOTIFICATIONS - ACTIVITIES - 00021 - UPDATE COMMENT ON YOUR OWN ENTRY ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourEntry_CommentUpdate_You_Others_Rollup(){
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the activity, post a comment to the entry and will edit the comment
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, user1Comment1, false, user1EditedComment1);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseActivityEntry.getTitle(), baseActivity.getName());
		
		// Verify that the comment on entry event, User 1's original comment and User 1's updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user1Comment1, user1EditedComment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the activity entry and will update the comment
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, activityEntry, null, user2Comment, user2EditedComment, testUser2, activitiesAPIUser2, false);
		
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 1's and User 2's updated comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), user1EditedComment1, user2EditedComment}, filter, true);
			
			// Verify that USer 1's original comment and User 2's original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment}, null, false);
		}
		
		// Users 3 through to 5 will now post an updated comment on the activity entry with User 1 then posting a second updated comment to the entry
		User[] usersCommenting = { testUser3, testUser4, testUser5, testUser1 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser1 };
		String[] userComments = new String[usersCommenting.length];
		String[] userEditedComments = new String[usersCommenting.length];
				
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			userEditedComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createCommentAndEditComment(publicActivity, activityEntry, null, userComments[index], userEditedComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
				
		// Create the news story to be verified
		commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, "4", baseActivityEntry.getTitle(), baseActivity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event, User 5's updated comment and User 1's second updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), userEditedComments[2], userEditedComments[3]}, filter, true);
			
			// Verify that User 1's to User 5's original comments, User 1's first updated comment and User 2's to User 4's updated comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user1EditedComment1, user2Comment, user2EditedComment, userComments[0], userEditedComments[0], userComments[1], userEditedComments[1], userComments[2], userComments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourToDo_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an to do item in the Activity</B></li>
	*<li><B>Step: User 1 comment on your own to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 4 comment on the to do item</B></li>
	*<li><B>Step: User 1 comment again on the to do item</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others commented on your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D44D12553F6E25B085257DE8003B22D9">TTT - MY NOTIFICATIONS - ACTIVITIES - 00031 - COMMENT ON YOUR OWN TO-DO ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourToDo_Comment_You_Others_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a to-do item in the activity and will post a comment to the to-do item
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAddComment(publicActivity, baseActivityTodo, user1Comment1, testUser1, activitiesAPIUser1, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
			
		// Create the news story to be verified
		String commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseActivityTodo.getTitle(), baseActivity.getName());
		
		// Verify that the comment on to-do event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the to-do item
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(publicActivity, null, todo, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on to-do event, User 1's comment and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user1Comment1, user2Comment}, TEST_FILTERS, true);
		
		// Users 3 and 4 will now post a comment on the activity entry with User 1 then posting a second comment to the entry
		User[] usersCommenting = { testUser3, testUser4, testUser1 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser1 };
		String[] userComments = new String[usersCommenting.length];
				
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createComment(publicActivity, null, todo, userComments[index], usersCommenting[index], apiUsersCommenting[index], false);
		}
				
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, "3", baseActivityTodo.getTitle(), baseActivity.getName());
				
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event, User 12's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), userComments[1], userComments[2]}, filter, true);
			
			// Verify that User 1's first comment and the comments posted by Users 2 and 3 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment, userComments[0]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourToDo_CommentUpdate_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 create a public Activity</B></li>
	*<li><B>Step: User 1 create an to-do item in the Activity</B></li>
	*<li><B>Step: User 1 comment on your own to-do item and then edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Activities and go to the Activity User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the to-do item and then update the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 20 comment on the todo and all edit their comments</B></li>
	*<li><B>Step: User 1 comment again on the to-do item and then edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Activities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you updated your {entry} entry thread in the {activity} activity."</B></li>
	*<li><B>Verify: Verify the event shows "You and 19 others updated your {entry} entry thread in the {activity} activity."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A65CBFD3F0D6C10F85257DE8003B22DA">TTT - MY NOTIFICATIONS - ACTIVITIES - 00041 - UPDATE COMMENT ON YOUR OWN TO-DO ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourToDo_CommentUpdate_You_Others_Rollup(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, post a comment to the to-do item and will edit the comment
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo todo = ActivityEvents.createTodoAndAddCommentAndEditComment(publicActivity, baseActivityTodo, user1Comment1, user1EditedComment1, testUser1, activitiesAPIUser1, false);
		
		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseActivityTodo.getTitle(), baseActivity.getName());
		
		// Verify that the comment on to-do item event, User 1's original comment and User 1's updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user1Comment1, user1EditedComment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the to-do item and will update the comment
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createCommentAndEditComment(publicActivity, null, todo, user2Comment, user2EditedComment, testUser2, activitiesAPIUser2, false);
		
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on to-do item event and User 1's and User 2's updated comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), user1EditedComment1, user2EditedComment}, filter, true);
			
			// Verify that USer 1's original comment and User 2's original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment}, null, false);
		}
		
		// Users 3 through to 20 will now post an updated comment on the activity entry with User 1 then posting a second updated comment to the entry
		User[] usersCommenting = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18, testUser19, testUser20, testUser1 };
		APIActivitiesHandler[] apiUsersCommenting = { activitiesAPIUser3, activitiesAPIUser4, activitiesAPIUser5, activitiesAPIUser6, activitiesAPIUser7, activitiesAPIUser8, activitiesAPIUser9, activitiesAPIUser10, activitiesAPIUser11, activitiesAPIUser12, activitiesAPIUser13, activitiesAPIUser14, activitiesAPIUser15, activitiesAPIUser16, activitiesAPIUser17, activitiesAPIUser18, activitiesAPIUser19, activitiesAPIUser20, activitiesAPIUser1 };
		String[] userComments = new String[usersCommenting.length];
		String[] userEditedComments = new String[usersCommenting.length];
				
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			userEditedComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			ActivityEvents.createCommentAndEditComment(publicActivity, null, todo, userComments[index], userEditedComments[index], usersCommenting[index], apiUsersCommenting[index], false);
			
			// This if statement provides a pause to make the test more robust - without this all comments fail to post correctly
			if(index % 4 == 0){
				UIEvents.gotoMyNotifications(ui);
			}
		}
		
		// Refresh the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Create the news story to be verified
		commentOnTodoEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, "19", baseActivityTodo.getTitle(), baseActivity.getName());
		
		// Verify that the comment on to-do item event, User 20's updated comment and User 1's second updated comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTodoEvent, baseActivityTodo.getDescription(), userEditedComments[17], userEditedComments[18]}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}