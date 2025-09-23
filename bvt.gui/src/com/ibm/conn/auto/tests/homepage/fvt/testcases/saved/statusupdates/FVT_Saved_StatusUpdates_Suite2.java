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
 *	Date:		5th January 2016
 *
 *	PLEASE NOTE: 	This suite of tests must be kept separate from the other Saved Status Updates tests
 *					in this package.
 */

public class FVT_Saved_StatusUpdates_Suite2 extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private HashMap<String, APIProfilesHandler> statusUpdatesForDeletion = new HashMap<String, APIProfilesHandler>();
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configurations
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		// User 1 will now upload a public file which can be attached to any status update
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		Set<String> statusUpdateIdsForDeletion = statusUpdatesForDeletion.keySet();
		for(String statusUpdateId : statusUpdateIdsForDeletion) {
			statusUpdatesForDeletion.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
		
		// Delete the public file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* test_StatusUpdates_SaveCommentedStatusUpdateWithFileAttachment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage</B></li>
	*<li><B>2. Update your status and add a file attachment</B></li>
	*<li><B>3. Go to Homepage / Status Updates / All</B></li>
	*<li><B>4. Comment on the status update</B></li>
	*<li><b>5: Comment on the status update again</b></li>
	*<li><b>6: Go to Homepage / Status Updates / My Updates</b></li>
	*<li><b>7: Click 'Save this'</b></li>
	*<li><b>8: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the comment from point 5 on the status update with the comment inline</b></li>
	*<li><b>9: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the comment from point 5 on the status update with the comment inline</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/87E76BCD6FC200D0852579B2003CDE64">TTT: AS - Saved - 00023 - Status Update With File Attachment And A Comment Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SaveCommentedStatusUpdateWithFileAttachment() {
		
		ui.startTest();
		
		// User 1 will now post a status update with a file attachment
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, user1StatusUpdate, publicFile);
		statusUpdatesForDeletion.put(user1StatusUpdateId, profilesAPIUser1);
		
		// User 1 will now post their first comment to the status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user1StatusUpdateId, user1Comment1, profilesAPIUser1);
		
		// User 1 will now post their second comment to the status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user1StatusUpdateId, user1Comment2, profilesAPIUser1);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be saved
		String suCommentOnStatusUpdateEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		// Save the comment on status update event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, suCommentOnStatusUpdateEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedCommentOnStatusUpdateEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status update event, the status update, the title of the public file and the second of User 1's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{savedCommentOnStatusUpdateEvent, user1StatusUpdate, baseFile.getRename() + baseFile.getExtension(), user1Comment2}, filter, true);
			
			// Verify that the first of User 1's comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_SaveLikedStatusUpdateWithFileAttachment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Homepage</B></li>
	*<li><B>2: Update your status and add a file attachment</B></li>
	*<li><B>3: Go to Homepage / Status Updates / All</B></li>
	*<li><b>4: Open the EE of the status update story and like the status update</b></li>
	*<li><b>5: Go to Homepage / Status Updates / My Updates</b></li>
	*<li><B>6: Click "Save this"</B></li>
	*<li><b>7: Go to Homepage / Saved / All</b></li>
	*<li><b>Verify: Verify that the saved story is of the recommendation of the status update with the file details</b></li>
	*<li><b>8: Go to Homepage / Saved / Status Updates</b></li>
	*<li><b>Verify: Verify that the saved story is of the recommendation of the status update with the file details</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/A65E8521BFEDF650852579B2003CE0D8">TTT: AS - Saved - 00025 - Status Update With File Attachment And A Recommendation Saved Appears Correctly In Saved View</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_SaveLikedStatusUpdateWithFileAttachment() {
		
		ui.startTest();
		
		// User 1 will now post a status update with a file attachment
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, user1StatusUpdate, publicFile);
		statusUpdatesForDeletion.put(user1StatusUpdateId, profilesAPIUser1);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now open the EE for the status update news story
		UIEvents.openEE(ui, user1StatusUpdate);
		
		// User 1 will now like / recommend the status update using the EE
		UIEvents.clickLikeInEEUsingUI(ui);
		
		// Return focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be saved
		String youLikedYourMessageEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
		
		// User 1 will now save the liked / recommended status update news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, youLikedYourMessageEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String likedTheirOwnMessageEvent = ProfileNewsStories.getLikedTheirOwnMessageNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the liked / recommended status update news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likedTheirOwnMessageEvent, user1StatusUpdate, baseFile.getRename() + baseFile.getExtension()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	* test_StatusUpdates_RemoveSavedLikedStatusUpdateComment()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Homepage</B></li>
	*<li><B>2: Add a status update with a comment</B></li>
	*<li><B>3: Like the comment</B></li>
	*<li><b>4: Go to Status Updates / My Updates</b></li>
	*<li><b>5: Save the story of the comment being liked</b></li>
	*<li><B>6: Go to Homepage / Saved / All & Status Updates</B></li>
	*<li><b>7: Go to the story of the liked comment and hover over the story</b></li>
	*<li><b>8: Click the 'X' to remove from the saved view</b></li>
	*<li><b>Verify: Verify that the profiles.wall.comment.recommended.added story is removed from the saved view</b></li>
	*<li><b>9: Go back to the story in Status Updates / My Updates</b></li>
	*<li><b>10: Hover over the story</b></li>
	*<li><b>Verify: Verify when the user hovers over the story the link says "Save this" again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/09A7D657FDB668F485257A8B00454C77">TTT: AS - Saved - 00031 - Profiles Wall Comment Recommended Added Unsaved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdates_RemoveSavedLikedStatusUpdateComment() {
		
		/**
		 * To avoid duplicate news stories in the news feed - this test case will use User 2 as User 1
		 */
		ui.startTest();
		
		// User 1 will now post a status update
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user1StatusUpdate);
		statusUpdatesForDeletion.put(user1StatusUpdateId, profilesAPIUser2);
		
		// User 1 will now post a comment to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentId = ProfileEvents.addStatusUpdateComment(user1StatusUpdateId, user1Comment, profilesAPIUser2);
		
		// User 1 will now like / recommend the comment
		ProfileEvents.likeComment(profilesAPIUser2, user1CommentId);
				
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser2, false);
			
		// Create the news story to be saved
		String suLikeCommentEvent = ProfileNewsStories.getLikedYourCommentOnAMessageNewsStory_You(ui);
		
		// Save the like comment event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, suLikeCommentEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String savedLikeCommentEvent = ProfileNewsStories.getLikedTheirOwnCommentNewsStory(ui, testUser2.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedLikeCommentEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, true);
		
		// Remove the like comment event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, savedLikeCommentEvent);
		
		// Verify that the like comment event and User 1's comment are NOT displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{savedLikeCommentEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, false);
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the like comment event now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, suLikeCommentEvent);
		
		ui.endTest();
	}
}