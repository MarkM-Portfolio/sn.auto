package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.imfollowing;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	4th October 2016
 */

public class FVT_PublicFile_FollowFileEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private BaseFile baseFile1;
	private FileEntry publicFile1, publicFile2;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser2 = new APIFileHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create the first public standalone file with User 2 following the file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFileWithOneFollower(baseFile1, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now create the second public standalone file with User 2 following the file
		BaseFile baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFileWithOneFollower(baseFile2, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		filesAPIUser1.deleteFile(publicFile2);
	}
	
	/**
	* test_FollowFile_UpdateFile() 
	*<ul>
	*<li><B>1: Log in to Files as User 1</B></li>
	*<li><B>2: Update a public file already there</B></li>
	*<li><B>3: Log in to Home as User 2 who is following the file</B></li>	
	*<li><B>4: Go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the story for files.file.updated appears with the timestamp to the right of the file icon and the size of the file</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A5D142713519379D852578F700528C52">AS - Follow - Files - 00021 - files.file.updated - PUBLIC FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FollowFile_UpdateFile() {
		
		ui.startTest();
		
		// User 1 will now update the file
		FileEvents.updateFileVersion(publicFile1, baseFile1, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story and timestamp element to be verified
		String updateFileEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		String timeStampCSSSelector = HomepageUIConstants.NewsStoryTimeLink.replaceAll("PLACEHOLDER", updateFileEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the update file event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateFileEvent}, filter, true);
			
			// verify that the timestamp is displayed with the news story in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{timeStampCSSSelector}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* test_FollowFile_FileComment
	*<ul>
	*<li><B>Step: Log in to Files</B></li>
	*<li><B>Step: Open one of your files that has public access</B></li>
	*<li><B>Step: Comment on this file</B></li>
	*<li><B>Step: Log in to Home as user who is following the file</B></li>	
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All, People & Files</B></li>
	*<li><B>Verify: Verify that files.file.comment.created displays with the timestamp to the right of the file icon, the size of the file and the comment under</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/26108A7F00CFD2AE852578F70056B592">AS - Follow - Files - 00051 - files.file.comment.created - PUBLIC FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FollowFile_FileComment() {
		
		ui.startTest();
		
		// User 1 will now post a comment on the file
		String commentPostedToFile = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFile2, commentPostedToFile);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story and timestamp element to be verified
		String commentedFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String timeStampCSSSelector = HomepageUIConstants.NewsStoryTimeLink.replaceAll("PLACEHOLDER", commentedFileEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on file event and User 1's comment are displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentedFileEvent, commentPostedToFile}, filter, true);
			
			// Verify that the timestamp is displayed with the news story in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{timeStampCSSSelector}, null, true);
		}
		ui.endTest();
	}
}