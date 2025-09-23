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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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
 * 	Author:			Anthony Cox
 * 	Date:			22nd September 2015
 */

public class FVT_Saved_Public_Files extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
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
	* test_PublicFile_SavingFileStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Files</B></li>
	*<li><B>2. User 1 start a public file - User 2 follow this file</B></li>
	*<li><B>3. User 1 go to Homepage / Discover / Files</B></li>
	*<li><B>4. User 1 go to the story of the file uploaded and mark it as Saved</B></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>5: User 1 go to Homepage / Saved / Files</b></li>
	*<li><b>Verify: User 1 should see the file uploaded in Homepage / Saved / Files</b></li>
	*<li><b>6: User 2 log into Connections</b></li>
	*<li><b>7: User 2 go to Homepage / I'm Following / Files</b></li>
	*<li><b>8: User 2 go to the story of the new version of the file uploaded and mark it as Saved</b></li>
	*<li><b>Verify: User 2 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 2 go to Homepage / Saved / Files</b></li>
	*<li><B>Verify: User 2 should see edited file story in Homepage / Saved / Files</B></li>
	*<li><b>10: User 2 go to the File uploaded by User 1</b></li>
	*<li><b>11: User 2 comment on the file</b></li>
	*<li><b>12: User 1 go to Homepage / My Notifications / For Me / Files</b></li>
	*<li><b>13: User 1 go to the story of User 2 commenting on the file and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>14: User 1 go to Homepage / Saved / Files</b></li>
	*<li><b>Verify: User 1 should see the comment on the file in Homepage / Saved / Files</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/69A6D2ADEB723DF485257936004E451A">TTT: AS - Saved - 00011 - Marking File Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PublicFile_SavingFileStories() {
		
		ui.startTest();
		
		// User 1 will now upload a public file with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFileWithOneFollower(baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// Create the news story which appears in the Discover view and is to be saved via the UI for Cloud runs
		String discoverViewUploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, discoverViewUploadFileEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the upload file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{discoverViewUploadFileEvent}, TEST_FILTERS, true);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// User 1 will now upload a new version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.updateFileVersion(publicFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Create the news story which appears in the Discover view and is to be saved via the UI for Cloud runs
		String imFollowingViewEditFileEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
			
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, imFollowingViewEditFileEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the edit file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{imFollowingViewEditFileEvent}, TEST_FILTERS, true);
		
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
		
		ui.endTest();
	}
}