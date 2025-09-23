package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.microblogs;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	29th September 2016
 */

public class FVT_Microblogs_StatusUpdate_WithVideoAttachment extends SetUpMethods2 {
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseFile baseVideoFile;
	private FileEntry publicVideoFile;
	private HomepageUI ui;
	private String serverURL, user1StatusUpdateWithVideoFile, user1StatusUpdateWithVideoFileId, user2Comment;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now upload a public video file
		baseVideoFile = FileBaseBuilder.buildBaseFile(Data.getData().file4, ".mp4", ShareLevel.EVERYONE);
		publicVideoFile = FileEvents.addFile(baseVideoFile, testUser1, filesAPIUser1);
		
		// User 1 will now post a status update with the video file attached		
		user1StatusUpdateWithVideoFile = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdateWithVideoFileId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, user1StatusUpdateWithVideoFile, publicVideoFile);
		
		// User 2 will now comment on User 1's status update with video file attachment
		user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user1StatusUpdateWithVideoFileId, user2Comment, profilesAPIUser2);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the files created during the test
		filesAPIUser1.deleteFile(publicVideoFile);
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateWithVideoFileId);
	}
	
	/**
	 *  test_PostStatusUpdate_WithVideoAttachment_FromLocalMachine() 
	 *<ul>
	 *<li><B>1: User 1 log into Homepage</B></li>
	 *<li><B>2: User 1 add a status update adding a .mp4 file from there computer</B></li>
	 *<li><B>3: User 1 post the update</B></li>
	 *<li><B>4: User 2 log into Homepage / Updates / Discover</B></li>
	 *<li><B>5: User 2 comment on the update</B></li>
	 *<li><B>6: User 1 go to Homepage / My Notifications / For Me / All</B></li>
	 *<li><B>Verify: Verify the comment event appears correctly with the video</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1BD7B4B3B36CA98085257D27003C826B">AS - VIDEO PLAYER - 00060 - STATUS UPDATE WITH A VIDEO FILE ATTACHED COMMENTED ON</a></li>
	 *</ul>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PostStatusUpdate_WithVideoAttachment_FromLocalMachine() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentedOnYourMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser2.getDisplayName());
		
		// Verify that the commented on your message event, the status update and User 2's comment are displayed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentedOnYourMessageEvent, user1StatusUpdateWithVideoFile, user2Comment}, null, true);
		
		// Set the title to be verified - in some environments, the video title is displayed but is not displayed in others
		String videoTitle = "";
		if(HomepageValid.isTextDisplayed(driver, baseVideoFile.getRename() + baseVideoFile.getExtension())) {
			videoTitle = baseVideoFile.getRename() + baseVideoFile.getExtension();
		}
		
		// Verify that the video preview is displayed with a thumbnail, the expected title and the expected content 
		HomepageValid.verifyVideoFileAttachmentWithStatusUpdate(ui, driver, commentedOnYourMessageEvent, videoTitle, testUser1.getDisplayName());
		
		ui.endTest();
	}
}