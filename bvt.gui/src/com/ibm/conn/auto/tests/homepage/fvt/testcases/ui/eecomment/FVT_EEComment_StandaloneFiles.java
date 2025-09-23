package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */
public class FVT_EEComment_StandaloneFiles extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };

	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private FilesUI filesUI;
	private FileEntry privateFile, publicFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public file
		BaseFile basePublicFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(basePublicFile, testUser1, filesAPIUser1);
		
		// User 1 will now create a private shareable file
		BaseFile basePrivateFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE);
		privateFile = FileEvents.addFile(basePrivateFile, testUser1, filesAPIUser1);
		
		// User 1 will now share the private file with User 2
		FileEvents.shareFileWithUser(privateFile, testUser1, filesAPIUser1, profilesAPIUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the tests
		filesAPIUser1.deleteFile(publicFile);
		filesAPIUser1.deleteFile(privateFile);
	}

	/**
	* fileOverlayComment_PublicFile() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a public file</B></li>
	*<li><B>Step: Comment on the file</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Files</B></li>
	*<li><B>Step: Open the file overlay for the public file commented news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the file overlay</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayComment_PublicFile(){

		ui.startTest();
		
		// User 1 will now comment on their own file so as the file event will be displayed in the I'm Following view
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFile, user1Comment1);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the file details overlay
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, commentOnFileEvent);
				
		// Post a comment to the file using the file details overlay
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, filesUI, testUser1, user1Comment2);
				
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user1Comment1, user1Comment2}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* fileOverlayComment_SharedFile() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: User 1 create a private file</B></li>
	*<li><B>Step: User 1 share the file with User 2</B></li>
	*<li><B>Step: User 1 post a comment</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / Files</B></li>
	*<li><B>Step: User 2 open the file overlay for the public file commented news story</B></li>
	*<li><B>Step: User 2 add a comment</B></li>
	*<li><B>Step: User 2 click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the file overlay</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayComment_SharedFile(){

		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be used to open the file details overlay
		String sharedFileEvent = FileNewsStories.getSharedAFileWithYouNewsStory(ui, testUser1.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, sharedFileEvent);
						
		// Post a comment to the file using the file details overlay
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, filesUI, testUser2, user2Comment);
						
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
				
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnAFileNewsStory_You(ui);
				
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user2Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}