package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
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
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	Author:		Anthony Cox
 * 	Date:		14th January 2016
 */

public class FVT_Saved_Public_Files_RemoveFromSaved extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private FileEntry publicFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		publicFile = null;
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		if(publicFile != null) {
			filesAPIUser1.deleteFile(publicFile);
		}
	}
	
	/**
	* test_PublicFile_RemovingSavedFileStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. Go to Homepage / Updates / Saved / Files</B></li>
	*<li><B>3. Click the 'X' in the story related to the File being uploaded by User 1</B></li>
	*<li><b>Verify: User 1 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><B>4. Go to Homepage / Discover / Files</B></li>
	*<li><b>Verify: User 1 - The story in Homepage / Discover / Files should have a clickable "Save this" link again</b></li>
	*<li><b>5: Click the "X" in the story related to the comment on the file that was created by User 1</b></li>
	*<li><b>Verify: User 1 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>6: Go to Homepage / My Notifications / For Me / Files</b></li>
	*<li><b>Verify: User 1 - The story in Homepage / My Notifications / For Me should have a clickable "Save this" link again</b></li>
	*<li><b>7: User 2 log into Connections</b></li>
	*<li><b>8: Go to Homepage / Saved / Files</b></li>
	*<li><b>9: Click the "X" in the story related to the new version of the file being uploaded</b></li>
	*<li><B>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</B></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>10: Go to Homepage / I'm Following / Files</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / I'm Following / Files should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/BF09049B00B8686E8525793B0035E886">TTT: AS - Saved - 00012 - Removing File Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PublicFile_RemovingSavedFileStories() {
		
		/**
		 * PLEASE NOTE: The order of events in this test case have been re-ordered to suit the order in which the news stories will appear in the UI
		 */
		ui.startTest();
		
		// User 1 will now upload a public file with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFileWithOneFollower(baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// Create the news story which appears in the Discover view and is to be saved via the UI for Cloud runs
		String discoverViewUploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		if(isOnPremise) {
			// Create the news story to be saved via the API
			String uploadFileEvent = FileNewsStories.getUploadFileNewsStory_WithFileName(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
			
			// Save the news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, uploadFileEvent, true);
			
			// Log in to Homepage as User 1
			LoginEvents.loginToHomepage(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
			// Save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, discoverViewUploadFileEvent);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the upload file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{discoverViewUploadFileEvent}, TEST_FILTERS, true);
		
		// Remove the upload file event from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, discoverViewUploadFileEvent);
		
		// Verify that the upload file event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{discoverViewUploadFileEvent}, TEST_FILTERS, false);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the 'Save This' link is displayed for the upload file event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, discoverViewUploadFileEvent);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// User 1 will now upload a new version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.updateFileVersion(publicFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Create the news story which appears in the Discover view and is to be saved via the UI for Cloud runs
		String imFollowingViewEditFileEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		if(isOnPremise) {
			// Create the news story to be saved via the API
			String editFileEvent = FileNewsStories.getEditFileNewsStory_WithFileName(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
			
			// Save the news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser2, editFileEvent, false);
			
			// Log in to Homepage as User 2
			LoginEvents.loginToHomepage(ui, testUser2, true);
		} else {
			// Log in as User 2 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
			
			// Save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, imFollowingViewEditFileEvent);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the edit file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{imFollowingViewEditFileEvent}, TEST_FILTERS, true);
		
		// Remove the edit file event from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, imFollowingViewEditFileEvent);
		
		// Verify that the edit file event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{imFollowingViewEditFileEvent}, TEST_FILTERS, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the 'Save This' link is displayed for the edit file event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, imFollowingViewEditFileEvent);
		
		// User 2 will now post a comment to the file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news story to be saved and verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser2.getDisplayName());
		
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnFileEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the comment on file event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user2Comment}, TEST_FILTERS, true);
		
		// Remove the comment on file event from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, commentOnFileEvent);
		
		// Verify that the comment on file event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user2Comment}, TEST_FILTERS, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the 'Save This' link is displayed for the comment on file event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnFileEvent);
			
		ui.endTest();
	}
}