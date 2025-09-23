package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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

public class FVT_Microblogs_StatusUpdate_WithFileAttachment extends SetUpMethods2 {
	
	private final String[] TEST_FILTERS_IF_VIEW = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	private final String[] TEST_FILTERS_SU_VIEW = { HomepageUIConstants.FilterAll, HomepageUIConstants.MyUpdates };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseTextFile;
	private FileEntry publicTextFile;
	private HomepageUI ui;
	private String serverURL, user1StatusUpdateWithTextFile,user1StatusUpdateWithTextFileId;
	private TestConfigCustom cfg;	
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now upload a public text file
		baseTextFile = FileBaseBuilder.buildBaseFile(Data.getData().file15, ".txt", ShareLevel.EVERYONE);
		publicTextFile = FileEvents.addFile(baseTextFile, testUser1, filesAPIUser1);
		
		// User 1 will now post a status update with the text file attached
		user1StatusUpdateWithTextFile = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdateWithTextFileId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, user1StatusUpdateWithTextFile, publicTextFile);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicTextFile);
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateWithTextFileId);
	}
	
	/**
	 * test_PostStatusUpdate_WithFileAttachment_FromLocalMachine() 
	 *<ul>
	 *<li><B>1: Log into Connections</B></li>
	 *<li><B>2: Go to Homepage / Status Updates / All Updates</B></li>
	 *<li><B>3: Add a status update and select "attach a file"</B></li>
	 *<li><B>4: Select "My Computer"</B></li>
	 *<li><B>5: Browse to a text file from your computer and click "OK"</B></li>
	 *<li><B>6: Click "Post"</B></li>
	 *<li><B>Verify: Verify that the status and file attachment has been added to the relevant view and that the file details are shown correctly</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/467D4B643C6A82CD85257944005D7CBD">Activity Stream Sharebox - 00021 - User should be able to attach a text file from their computer</a></li>
	 *</ul>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PostStatusUpdate_WithFileAttachment_FromLocalMachine() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Verify that the status update and file name are displayed in all necessary filters in the Status Updates view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user1StatusUpdateWithTextFile, baseTextFile.getRename() + baseTextFile.getExtension()}, TEST_FILTERS_SU_VIEW, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the status update and file name are displayed in all necessary filters in the I'm Following view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user1StatusUpdateWithTextFile, baseTextFile.getRename() + baseTextFile.getExtension()}, TEST_FILTERS_IF_VIEW, true);
		
		ui.endTest();
	}
}