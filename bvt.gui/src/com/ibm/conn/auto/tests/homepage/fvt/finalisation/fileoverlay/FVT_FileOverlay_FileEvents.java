package com.ibm.conn.auto.tests.homepage.fvt.finalisation.fileoverlay;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.FilesUI;
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
 * Date:	14th October 2016
 */

public class FVT_FileOverlay_FileEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3;
	private BaseFile baseFile1, baseFile2, baseFile3;
	private FileEntry publicFile1, publicFile2, publicFile3;
	private FilesUI uiFi;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiFi = FilesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testUser3 = cfg.getUserAllocator().getUser(this);
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser2 = new APIFileHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		filesAPIUser3 = new APIFileHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		// User 1 will now create a public standalone file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFile(baseFile1, testUser1, filesAPIUser1);
		
		// User 2 will now create a public standalone file
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFile(baseFile2, testUser2, filesAPIUser2);
		
		// User 3 will now create a public standalone file
		baseFile3 = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		publicFile3 = FileEvents.addFile(baseFile3, testUser3, filesAPIUser3);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiFi = FilesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		filesAPIUser2.deleteFile(publicFile2);
		filesAPIUser3.deleteFile(publicFile3);
	}
	
	/**
	*	test_FileDetailsOverlay_IsDisplayed() 
	*<ul>
	*<li><B>1: User 1 create a public file</b></li>
	*<li><B>2: Login as User 1 and navigate to the Discover view</b></li>
	*<li><B>3: Open the file details overlay for the public file uploaded news story - verification point</b></li>
	*<li><B>Verify: Verify that the file details overlay opens correctly with all relevant data displayed</b></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FileDetailsOverlay_IsDisplayed() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be used to open the file details overlay
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileUploadedEvent);
		
		// Verify that the file details overlay is displayed
		HomepageValid.verifyFileDetailsOverlayIsDisplayed(ui, baseFile1);
		
		ui.endTest();
	}
	
	/**
	* test_FileDetailsOverlay_Like() 
	*<ul>
	*<li><B>1: Log in as User 1</b></li>
	*<li><B>2: User 1 post a public file</B></li>
	*<li><B>3: Navigate to the Discover view</B></li>
	*<li><B>4: Open the file details overlay for the file upload news story</B></li>
	*<li><b>5: Click 'like' in the file details overlay to like / recommend the file - verification point 1</B></li>
	*<li><b>6: Refresh the Discover view - verification point 2</b></li>
	*<li><B>Verification Point 1: Verify that the 'like' link has now changed to an 'unlike' link in the file details overlay</B></li>
	*<li><b>Verification Point 2: Verify that the like file event is now displayed in the AS</B></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FileDetailsOverlay_Like() {
		
		/**
		 * To prevent duplicate news stories in the AS, User 2 is used as User 1 in this test case.
		 */
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be used to open the file details overlay
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser2.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileUploadedEvent);
		
		// Like the file using the file details overlay
		FileEvents.likeFileUsingUI(ui, driver, testUser2);
		
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Refresh the Discover view by navigating to it again
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_You(ui);
		
		// Verify that the like file event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeFileEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_FileDetailsOverlay_Comment() 
	*<ul>
	*<li><B>1: Log in as User 1</b></li>
	*<li><B>2: User 1 post a public file</B></li>
	*<li><B>3: Navigate to the Discover view</B></li>
	*<li><B>4: Open the file details overlay for the file upload news story</B></li>
	*<li><b>5: Post a comment to the file using the file details overlay - verification point 1</B></li>
	*<li><b>6: Refresh the Discover view - verification point 2</b></li>
	*<li><B>Verification Point 1: Verify that the comment is displayed in the file details overlay after posting the comment</B></li>
	*<li><b>Verification Point 2: Verify that the comment on file event and the comment are now displayed in the AS</B></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FileDetailsOverlay_Comment() {
		
		/**
		 * To prevent duplicate news stories in the AS, User 3 is used as User 1 in this test case.
		 */
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be used to open the file details overlay
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser3.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileUploadedEvent);
		
		// Post a comment to the file using the file details overlay
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, uiFi, testUser3, user1Comment);
		
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Refresh the Discover view by navigating to it again
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Verify that the comment on file event and comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}