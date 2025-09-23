package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

public class FVT_Saved_Public_Files_LikeBeforeComment extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a public file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}

	/**
	* test_PublicFile_LikeFileStorySavedBeforeCommentAdded()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Files</B></li>
	*<li><B>2. Upload a file</B></li>
	*<li><B>3. Recommend the file</B></li>
	*<li><B>4. Go to Homepage / Discover / Files</B></li>
	*<li><b>5: Go to the story of the file recommended and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Files</b></li>
	*<li><b>Verify: Verify that the story saved is of the file recommended</b></li>
	*<li><b>7: Go back to the file and comment on the file</b></li>
	*<li><b>8: Go to Homepage / Discover / Files</b></li>
	*<li><b>Verify: Verify that the story in discover is now of the file commented on</b></li>
	*<li><b>9: Go to Homepage / Saved / Files</b></li>
	*<li><b>Verify: Verify that it is still the story of the file recommended in saved</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/72FF3376420B4F94852579B300348B62">TTT: AS - Saved - 00061 - File Commented On After A Recommendation Story Is Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PublicFile_LikeFileStorySavedBeforeCommentAdded() {
		
		ui.startTest();
		
		// User 1 will now like / recommend the public file
		FileEvents.likeFile(testUser1, filesAPIUser1, publicFile);
		
		// Create the news story which appears in the Discover view and is to be saved via the UI for Cloud runs
		String discoverViewLikeFileEvent = FileNewsStories.getLikeYourFileNewsStory_You(ui);
					
		if(isOnPremise) {
			// Create the news story to be saved via the API
			String likeFileEvent = FileNewsStories.getLikedTheFileNewsStory_User(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
			
			// Save the news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, likeFileEvent, true);
			
			// Log in to Homepage as User 1
			LoginEvents.loginToHomepage(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
			// Save the news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, discoverViewLikeFileEvent);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String likeFileEvent = FileNewsStories.getLikedAFileNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the like file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeFileEvent}, TEST_FILTERS, true);
		
		// User 1 will now post a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFile, user1Comment);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on file event and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFileEvent, user1Comment}, filter, true);
			
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{discoverViewLikeFileEvent}, null, false);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeFileEvent}, filter, true);
			
			// Verify that the comment on file event and User 1's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFileEvent, user1Comment}, null, false);
		}
		ui.endTest();
	}
}