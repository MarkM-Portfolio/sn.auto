package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.statusupdates;

import java.util.HashMap;
import java.util.Set;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/* *******************************************************************/
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* *******************************************************************/
/**
 *	Author:		Anthony Cox
 *	Date:		28th September 2015
 */

public class FVT_Saved_StatusUpdates extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private FileEntry publicFile;
	private HashMap<String, APIProfilesHandler> statusUpdatesToBeDeleted = new HashMap<String, APIProfilesHandler>();
	private User testUser1, testUser2, testUser3, testUser4;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configurations
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		// User 1 will now create a public file - this is required to be attached to a status update in a later test
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		
		// User 3 will now follow User 4 - this is required for a later test
		ProfileEvents.followUser(profilesAPIUser4, profilesAPIUser3);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the tests
		Set<String> setOfStatusUpdateIds = statusUpdatesToBeDeleted.keySet();
		
		for(String statusUpdateId : setOfStatusUpdateIds) {
			statusUpdatesToBeDeleted.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
		
		// Have User 3 unfollow User 4 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser4, profilesAPIUser3);
	}
	
	/**
	* test_StatusUpdates_SaveStatusUpdateStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 1 update their status</B></li>
	*<li><B>3. User 1 go to Homepage / Discover / Status Updates</B></li>
	*<li><B>4. User 1 go to the status update and mark it as Saved</B></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>5: User 1 go to Homepage / Saved / Status Updates </b></li>
	*<li><b>Verify: User 1 should see the message posted in Homepage / Saved / Status Updates</b></li>
	*<li><b>6: User 2 comment on User  status update</b></li>
	*<li><b>7: User 1 go to Homepage / My Notifications / For Me / All</b></li>
	*<li><b>8: User 1 go to the story of the comment of User 1 and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 1 go to Homepage / Saved / Status Updates</b></li>
	*<li><B>Verify: User 1 should see the message posted in Homepage / Saved / Status Updates</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/F9AB4E2C4CFC680A8525793B003483C9">TTT: AS - Saved - 00011 - Marking Status Update Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SaveStatusUpdateStories() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// User 1 will now save the status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, statusUpdate);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the status update news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{statusUpdate}, TEST_FILTERS, true);
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment, profilesAPIUser2);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Create the news story to be saved
		String commentOnStatusUpdateEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser2.getDisplayName());
		
		// User 1 will now save the comment on status update event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnStatusUpdateEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
				
		// Verify that the comment on status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusUpdateEvent, statusUpdate, user2Comment}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_RemoveSavedStatusUpdateStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 2 add 2 status updates</B></li>
	*<li><B>3. User 1 comment on one of the status updates</B></li>
	*<li><B>4. User 1 save the status update that has NOT been commented</B></li>
	*<li><b>5: User 1 go to Homepage / Saved / Status Updates </b></li>
	*<li><b>6: User 1 click the 'X' in the story related to the Status Update that was saved by User 1</b></li>
	*<li><b>Verify: User 1 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>7: Go to Homepage / Discover / Status Updates</b></li>
	*<li><b>Verify: User 1 - The story in Homepage / Discover / Status Updates should have a clickable "Save this" link again</b></li>
	*<li><b>8: User 2 log into Connections</b></li>
	*<li><b>9: User 2 save the commented story</b></li>
	*<li><b>10: User 2 go to Homepage / Saved / Status Updates</b></li>
	*<li><b>11: User 2 click the 'X' in the story related to the Status Update that was saved by User 2</b></li>
	*<li><b>Verify: User 2 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>12: User 2 go to Homepage / I'm Following / Status Updates</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / I'm Following / Status Updates should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/8449D96424206C938525793B0035ECC6">TTT: AS - Saved - 00012 - Removing Status Update Stories From Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_RemoveSavedStatusUpdateStories() {
		
		ui.startTest();
		
		// User 2 will now create a status update
		String user2StatusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate1);
		statusUpdatesToBeDeleted.put(user2StatusUpdate1Id, profilesAPIUser2);
		
		// User 2 will now create a second status update
		String user2StatusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate2);
		statusUpdatesToBeDeleted.put(user2StatusUpdate2Id, profilesAPIUser2);
		
		// User 1 will now post a comment to the second status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdate2Id, user1Comment, profilesAPIUser1);
		
		// Create the news story to be saved
		String commentOnStatusUpdateEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser1.getDisplayName());
		
		if(isOnPremise) {
			// User 1 will now save the status update without comment news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, user2StatusUpdate1, true);
			
			// Create the Entry representation of the news story to be saved by User 2
			String commentOnStatusUpdateEntry = ProfileNewsStories.getCommentedOnAnotherUsersMessageNewsStory_User(ui, testUser2.getDisplayName(), testUser1.getDisplayName());
			
			// User 2 will now save the status update with comment news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser2, commentOnStatusUpdateEntry, true);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
			// User 1 will now save the status update without comment news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, user2StatusUpdate1);
			
			// Log out of Connections
			LoginEvents.logout(ui);
			
			// Log in as User 2 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
			
			// User 2 will now save the status update with comment news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, commentOnStatusUpdateEvent);
			
			// Log out of Connections
			LoginEvents.logout(ui);
		}
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, !isOnPremise);
		
		// User 1 will now remove the status update without comment news story from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, user2StatusUpdate1);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the 'Save This' link for the status update without comment news story is displayed
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, user2StatusUpdate1);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser2, true);
		
		// User 2 will now remove the status update with comment news story from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, commentOnStatusUpdateEvent);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
				
		// Verify that the 'Save This' link for the status update with comment news story is displayed
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnStatusUpdateEvent);
		
		// Perform clean up now that the test has completed
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdate1Id);
		statusUpdatesToBeDeleted.remove(user2StatusUpdate1Id);
		
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdate2Id);
		statusUpdatesToBeDeleted.remove(user2StatusUpdate2Id);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_VerifySavedStatusUpdate()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage</B></li>
	*<li><B>2. Update your status</B></li>
	*<li><B>3. Go to Homepage / Status Updates / My Updates</B></li>
	*<li><B>4. Click "Save this"</B></li>
	*<li><b>5: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the status update</b></li>
	*<li><b>6: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the status update</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/A6E517BD39D8528D852579B2003C4A34">TTT: AS - Saved - 00020 - Status Update Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_VerifySavedStatusUpdate() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		if(isOnPremise) {
			// User 1 will now save the status update news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, statusUpdate, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the Status Updates view
			LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
			
			// User 1 will now save the status update news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, statusUpdate);
			
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		
		// Verify that the status update news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{statusUpdate}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_SavedStatusUpdateWithFileAttachment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage</B></li>
	*<li><B>2. Update your status and add a file attachment</B></li>
	*<li><B>3. Go to Homepage / Status Updates / All</B></li>
	*<li><B>4. Click "Save this"</B></li>
	*<li><b>5: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the status update with the file attached details</b></li>
	*<li><b>6: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the status update with the file attached details</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/E0251389EFBC1B9F852579B2003C7203">TTT: AS - Saved - 00021 - Status Update With File Attachment Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SavedStatusUpdateWithFileAttachment() {
		
		ui.startTest();
		
		// User 1 will now post a status update with file attachment
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, statusUpdate, publicFile);
		
		if(isOnPremise) {
			// User 1 will now save the status update with file attachment news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, statusUpdate, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
			
			// User 1 will now save the status update with file attachment news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, statusUpdate);
			
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		
		// Verify that the status update with file attachment news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{statusUpdate, publicFile.getTitle()}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_SavedStatusUpdateWithComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage</B></li>
	*<li><B>2. Update your status</B></li>
	*<li><B>3. Go to Homepage / Status Updates / All</B></li>
	*<li><B>4. Comment on the status update</B></li>
	*<li><b>5: Comment on the status update again</b></li>
	*<li><b>6: Go to Homepage / Status Updates / My Updates</b></li>
	*<li><b>7: Click 'Save this'</b></li>
	*<li><b>8: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the comment from point 5 on the status update with the comment inline</b></li>
	*<li><b>9: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the comment from point 5 on the status update with the comment inline</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/1A1A01D2109880A6852579B2003CDD28">TTT: AS - Saved - 00022 - Status Update With A Comment Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SavedStatusUpdateWithComment() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// User 1 will now post a first comment to the status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment1, profilesAPIUser1);
		
		// User 1 will now post a second comment to the status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment2, profilesAPIUser1);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be saved
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		// User 1 will now save the comment on status update event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnMessageEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String commentOnOwnMessageEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status update event, the status update and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnOwnMessageEvent, statusUpdate, user1Comment2}, filter, true);
			
			// Verify that User 1's first comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_SaveLikedStatusUpdate()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage</B></li>
	*<li><B>2. Update your status</B></li>
	*<li><B>3. Go to Homepage / Status Updates / All</B></li>
	*<li><B>4. Open the EE of the status update story and like the status update</B></li>
	*<li><b>5: Go to Homepage / Discover / Status Updates</b></li>
	*<li><b>6: Click 'Save this'</b></li>
	*<li><b>7: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the recommendation of the status update</b></li>
	*<li><b>8: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the recommendation of the status update</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/6FF2C6E198819021852579B2003CDFB7">TTT: AS - Saved - 00024 - Status Update With A Recommendation Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SaveLikedStatusUpdate() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now open the EE for the status update news story
		UIEvents.openEE(ui, statusUpdate);
		
		// User 1 will now like / recommend the status update using the EE
		UIEvents.clickLikeInEEUsingUI(ui);
		
		// Return focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be saved
		String youLikedYourMessageEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
		
		// User 1 will now save the liked / recommended status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, youLikedYourMessageEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String likedTheirOwnMessageEvent = ProfileNewsStories.getLikedTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the liked / recommended status update news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likedTheirOwnMessageEvent, statusUpdate}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_SaveLikedStatusUpdateComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Homepage</B></li>
	*<li><B>2: Add a status update with a comment</B></li>
	*<li><B>3: Like the comment</B></li>
	*<li><b>4: Go to Status Updates / My Updates</b></li>
	*<li><b>5: Save the story of the comment being liked</b></li>
	*<li><B>6: Go to Homepage / Saved / All & Status Updates</B></li>
	*<li><b>Verify: Verify that the profiles.wall.comment.recommended.added story appears</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/BA8599D66FA1FD0C85257A8B00454A3A">TTT: AS - Saved - 00030 - Profiles Wall Comment Recommended Added Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SaveLikedStatusUpdateComment() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// User 1 will now post a comment to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentId = ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment, profilesAPIUser1);
		
		// User 1 will now like / recommend the comment
		ProfileEvents.likeComment(profilesAPIUser1, user1CommentId);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be saved
		String youLikedYourCommentEvent = ProfileNewsStories.getLikedYourCommentOnAMessageNewsStory_You(ui);
		
		// User 1 will now save the liked / recommended comment news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, youLikedYourCommentEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
				
		// Create the news story to be verified
		String userLikedTheirOwnCommentEvent = ProfileNewsStories.getLikedTheirOwnCommentNewsStory(ui, testUser1.getDisplayName());
				
		// Verify that the liked / recommended comment news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{userLikedTheirOwnCommentEvent, statusUpdate, user1Comment}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_PostStatusUpdateComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: User 1 log into Homepage</B></li>
	*<li><B>2: User 1 follow User 2</B></li>
	*<li><B>3: User 2 log into Connections</B></li>
	*<li><b>4: User 2 add a Status Update</b></li>
	*<li><b>5: User 1 go to Homepage / I'm Following / Status Updates</b></li>
	*<li><B>6: User 1 mark the story of User 2 status Update as Saved</B></li>
	*<li><b>7: User 1 go to Homepage / Saved / Status Updates</b></li>
	*<li><b>8: User 1 open the EE for the story and click "Comment"</b></li>
	*<li><b>Verify: Verify when the user click comment that an input field is displayed under the line of actions with a "Post" button and "cancel" link</b></li>
	*<li><b>9: User 1 enter in comment click "Post"</b></li>
	*<li><b>Verify: Verify that when "Post" is clicked that the comment is displayed</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/18FC176CFA9D79288525794400404649">TTT: AS - Saved - 00011 - Comment On Status Updates</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_PostStatusUpdateComment() {
		
		/**
		 * Since all other tests use User 1 and User 2 and this is one of only two tests which require that User 1 follows User 2, this test
		 * case will use User 3 (as User 1) and User 4 (as User 2) with User 3, then, following User 4.
		 */
		ui.startTest();
		
		// User 2 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser4, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser4);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// User 1 will now save User 2's status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, statusUpdate);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// User 1 will now open the EE for the status update news story and will post a comment to the news story using the EE
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		UIEvents.addEECommentUsingUI(ui, testUser3, statusUpdate, user1Comment);
				
		// Perform clean up now that the test has completed
		profilesAPIUser4.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);		
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_ResetStatusUpdateComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: User 1 log into Homepage</B></li>
	*<li><B>2: User 1 follow User 2</B></li>
	*<li><B>3: User 2 log into Connections</B></li>
	*<li><b>4: User 2 add a Status Update</b></li>
	*<li><b>5: User 1 go to Homepage / I'm Following / Status Updates</b></li>
	*<li><B>6: User 1 mark the story of User 2 status Update as Saved</B></li>
	*<li><b>7: User 1 go to Homepage / Saved / Status Updates</b></li>
	*<li><b>8: User 1 open the EE for the story and click "Comment"</b></li>
	*<li><b>Verify: Verify when the user click comment that an input field is displayed under the line of actions with a "Post" button and "cancel" link</b></li>
	*<li><b>9: User 1 enter in comment click "Cancel"</b></li>
	*<li><b>Verify: Verify that when "Cancel" is clicked that the comment is removed from the comment field</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/95AE86A85DDE5FA4852579440040A1D5">TTT: AS - Saved - 00012 - Reset On Comment On Status Updates</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_ResetStatusUpdateComment() {
		
		/**
		 * Since all other tests use User 1 and User 2 and this is one of only two tests which require that User 1 follows User 2, this test
		 * case will use User 3 (as User 1) and User 4 (as User 2) with User 3, then, following User 4.
		 */
		ui.startTest();
		
		// User 2 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser4, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser4);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// User 1 will now save User 2's status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, statusUpdate);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// User 1 will now open the EE for the status update news story and will post a comment to the news story using the EE
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		UIEvents.addEECommentAndCancelCommentUsingUI(ui, driver, testUser1, statusUpdate, user1Comment);
				
		// Perform clean up now that the test has completed
		profilesAPIUser4.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);		
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_BeforeCommentingSaveLikedStatusUpdate()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Homepage</B></li>
	*<li><B>2: Update your status</B></li>
	*<li><B>3: Open the EE for the status update and like the status update</B></li>
	*<li><b>4: Go to Homepage / Status Updates / All</b></li>
	*<li><b>5: Click "Save this" on the story of the status update being recommended</b></li>
	*<li><B>6: Go to Homepage / Saved / Status Updates</B></li>
	*<li><b>Verify: Verify that the story of the status update recommended appears in Saved view</b></li>
	*<li><b>7: Go to Homepage / I'm Following / Status Updates</b></li>
	*<li><b>8: Go to the story of the status update being recommended and comment on the status update</b></li>
	*<li><b>9: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that after the comment is added the story in the saved view is still of the status update</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/06D84A8B1BC1932E852579B2003EC6BF">TTT: AS - Saved - 00026 - Status Update Already Saved Commented On After Being Recommended</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_BeforeCommentingSaveLikedStatusUpdate() {
		
		ui.startTest();
		
		// User 1 will now create a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		statusUpdatesToBeDeleted.put(statusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
				
		// User 1 will now open the EE for the status update news story
		UIEvents.openEE(ui, statusUpdate);
				
		// User 1 will now like / recommend the status update using the EE
		UIEvents.clickLikeInEEUsingUI(ui);
				
		// Return focus back to the top frame
		UIEvents.switchToTopFrame(ui);
				
		// Filter by 'All' to refresh the view
		UIEvents.filterBy(ui, HomepageUIConstants.FilterAll);
				
		// Create the news story to be saved
		String youLikedYourMessageEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
				
		// User 1 will now save the liked / recommended status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, youLikedYourMessageEvent);
				
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
				
		// Create the news story to be verified
		String likedTheirOwnMessageEvent = ProfileNewsStories.getLikedTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
				
		// Verify that the liked / recommended status update news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likedTheirOwnMessageEvent, statusUpdate}, TEST_FILTERS, true);
		
		// User 1 will now post a comment on the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user1Comment, profilesAPIUser1);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the liked / recommended status update news story is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likedTheirOwnMessageEvent, statusUpdate}, filter, true);
			
			// Verify that the comment posted to the status update is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		// Perform clean up now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		statusUpdatesToBeDeleted.remove(statusUpdateId);
		ui.endTest();
	}
}