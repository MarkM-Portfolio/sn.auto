package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;

/* *******************************************************************/
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
/* *******************************************************************/
/*
 * Author:		Anthony Cox
 * Date:		18th August 2015
 */

public class FVT_Saved_Private_StandaloneActivities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity privateActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a private standalone activity with User 2 added as a member and both User 1 and User 2 as followers
		User[] membersList = { testUser2 };
		APIActivitiesHandler[] followersList = { activitiesAPIUser1, activitiesAPIUser2 };
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivityWithMultipleMembersAndMultipleFollowers(testUser1, activitiesAPIUser1, baseActivity, membersList, followersList, isOnPremise);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created for the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}
	
	/**
	* test_PrivateStandaloneActivity_MarkActivityStoriesAsSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: User 1 log into Connections - Go to Activities</B></li>
	*<li><B>2: User 1 start a public activity - User 2 follow this activity</B></li>
	*<li><B>3: User 1 add an entry to the activity</B></li>
	*<li><B>4: User 1 go to Homepage / Discover / Activities</B></li>
	*<li><B>5: User 1 go to the story of the activity creation and mark it as Saved</B></li>
	*<li><B>Verify: User 1 - Once the 'Save this' link has been clicked it should be blackened and unclickable</B></li>
	*<li><b>6: User 1 go to Homepage / Saved / Activities</b></li>
	*<li><B>Verify: User 1 should see the activity created in Homepage / Saved / Activities</B></li>
	*<li><b>7: User 2 log into connections go to Homepage / I'm Following / Activities</b></li>
	*<li><b>8: User 2 go to the story of User 1 creating the entry to the activity and mark it as Saved</b></li>
	*<li><b>Verify: User 2 - Once the 'Save this' link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 2 go to Homepage / Saved / Activities</b></li>
	*<li><b>Verify: User 2 should see the activity entry added in Homepage / Saved / Activities</b></li>
	*<li><b>10: User 2 comment on the activity entry</b></li>
	*<li><b>11: User 1 go to Homepage / My Notifications / For Me / Activities</b></li>
	*<li><b>12: User 1 go to the story of the activity entry being commented on and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the 'Save this' link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>13: User 1 go to Homepage / Saved / Activities</b></li>
	*<li><b>Verify: User 1 should see the activity entry commented on added in Homepage / Saved / Activities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/DEB960DF2491C2D48525794300421622">TTT: AS - Saved - 00011 - Marking Activity Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PrivateStandaloneActivity_MarkActivityStoriesAsSaved() {
		
		/**
		 * PLEASE NOTE:	Step 5 and its subsequent verification are impossible in this case since the create private
		 * 				standalone activity news story does NOT appear in the news feed and therefore cannot be saved.
		 */
		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be saved
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// User 2 will now save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the activity entry
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news story to be saved
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// User 1 will now save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedCommentOnEntryEvent = ActivityNewsStories.getCommentOnTheEntryAddedByYouNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on activity entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedCommentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	* test_PrivateStandaloneActivity_RemovingActivityStoriesFromSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: User 1 log into Connections</B></li>
	*<li><B>2: Go to Homepage / Saved / Activities</B></li>
	*<li><B>3: Click the "X" in the story related to the Activity that had been created by User 1</B></li>
	*<li><b>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a greeen message to confirm it has been removed</b></li>
	*<li><B>4: Go to Homepage / Discover / Activities</B></li>
	*<li><b>Verify: The story in Homepage / Discover / Activities should have a clickable "Save this" link again</b></li>
	*<li><B>5: User 2 log into Connections</B></li>
	*<li><b>6: Go to Homepage / Saved / Activities</b></li>
	*<li><b>7: Click the "X" in the story related to the Activity entry that was created by User 1</b></li>
	*<li><b>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a greeen message to confirm it has been removed</b></li>
	*<li><b>8: Go to Homepage / I'm Following / Activities</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / Discover / Activities should have a clickable "Save this" link again</b></li>
	*<li><b>9: User 1 go to Homepage / Saved / Activities</b></li>
	*<li><b>10: Click the "X" in the story related to the Activity entry commented on that was created by User 2</b></li>
	*<li><b>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a greeen message to confirm it has been removed</b></li>
	*<li><b>11: Go to Homepage / My Notifications / For Me / Activities</b></li>
	*<li><b>Verify: User 1 - The story in Homepage / For Me / Activities should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/021D2579D0068775852579430047A50D">TTT: AS - Saved - 00012 - Removing Activity Stories From Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PrivateStandaloneActivity_RemovingActivityStoriesFromSaved() {
		
		/**
		 * PLEASE NOTE:	Steps 3 and 4 and all subsequent verifications relating to these steps are impossible in this case since the create private
		 * 				standalone activity news story does NOT appear in the news feed and therefore cannot be saved.
		 */
		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// Create the news story to be saved
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());		
				
		if(isOnPremise) {
			// Save the create activity entry event using the API
			ProfileEvents.saveNewsStory(profilesAPIUser2, createEntryEvent, false);
			
			// Log in as User 2
			LoginEvents.loginToHomepage(ui, testUser2, false);
		} else {
			// Log in as User 2 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
			
			// User 2 will now save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createEntryEvent);
		}
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
				
		// Remove the create activity entry event from the Saved view using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createEntryEvent);
		
		// Verify that the create activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the 'Save This' link is displayed on the create activity entry event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createEntryEvent);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the activity entry
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user2Comment, testUser2, activitiesAPIUser2, false);
		
		// Create the news story to be saved
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
			
		// User 1 will now save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnEntryEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedCommentOnEntryEvent = ActivityNewsStories.getCommentOnTheEntryAddedByYouNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
				
		// Verify that the comment on activity entry event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedCommentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment}, TEST_FILTERS, true);
		
		// Remove the comment on activity entry event using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, savedCommentOnEntryEvent);
		
		// Verify that the comment on activity entry event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedCommentOnEntryEvent, baseActivityEntry.getDescription(), user2Comment}, TEST_FILTERS, false);
			
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
				
		// Verify that the 'Save This' link is displayed on the comment on activity entry event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnEntryEvent);
		
		ui.endTest();
	}
	
	/**
	* test_PrivateStandaloneActivity_SavingAnActivityEntry()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Activities</B></li>
	*<li><B>2: Start a public activity</B></li>
	*<li><B>3: Add an entry</B></li>
	*<li><b>4: Go to Homepage / Discover / Activities</B></li>
	*<li><B>5: Go to the story of the entry and click "Save this"</B></li>
	*<li><b>6: Go to Homepage / Saved / Activities</b></li>
	*<li><b>Verify: Verify that the story is saved of the entry created</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/AFFE10F610E4BF27852579B2003EA670">TTT: AS - Saved - 00030 - Saving an Activity Entry</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PrivateStandaloneActivity_SavingAnActivityEntry() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// Create the news story to be saved
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());		
				
		if(isOnPremise) {
			// Save the create activity entry event using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, createEntryEvent, false);
			
			// Log in as User 1
			LoginEvents.loginToHomepage(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
			
			// User 1 will now save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createEntryEvent);
		}
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PrivateStandaloneActivity_StoryCommentedOnAfterSaving()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Activities</B></li>
	*<li><B>2: Start a public activity</B></li>
	*<li><B>3: Add an entry</B></li>
	*<li><b>4: Go to Homepage / Discover / Activities</B></li>
	*<li><B>5: Go to the story of the entry and click "Save this"</B></li>
	*<li><b>6: Go to Homepage / Saved / Activities</b></li>
	*<li><b>Verify: Verify that the story saved is of the entry in the activity</b></li>
	*<li><b>7: Go back to the entry in the activity and comment on the entry</b></li>
	*<li><b>8: Go to Homepage / Discover / Activities</b></li>
	*<li><b>Verify: Verify that the story in discover is now of the comment on the entry</b></li>
	*<li><b>9: Go to Homepage / Saved / Activities</b></li>
	*<li><b>Verify: Verify that the story saved is still the entry in the activity</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/F0BE8E88438847F1852579B2003EBAC5">TTT: AS - Saved - 00031 - Activity story commented on after the entry story was saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PrivateStandaloneActivity_StoryCommentedOnAfterSaving() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an activity entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, privateActivity);
		
		// Create the news story to be saved
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());		
				
		if(isOnPremise) {
			// Save the create activity entry event using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, createEntryEvent, false);
			
			// Log in as User 1
			LoginEvents.loginToHomepage(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
			
			// User 1 will now save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createEntryEvent);
		}
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create activity entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now post a comment to the activity entry
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.createComment(privateActivity, activityEntry, null, user1Comment, testUser1, activitiesAPIUser1, false);
		
		// Create the news story to be verified
		String commentOnYourEntryEvent = ActivityNewsStories.getCommentOnYourEntryNewsStory_You(ui, baseActivityEntry.getTitle(), baseActivity.getName());
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the comment on activity event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnYourEntryEvent, baseActivityEntry.getDescription(), user1Comment}, TEST_FILTERS, true);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create activity entry event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the comment on entry event and User 1's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnYourEntryEvent, user1Comment}, null, false);
		}
		ui.endTest();
	}
}