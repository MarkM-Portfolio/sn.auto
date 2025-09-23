package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.FilesUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015, 2016                              */
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

public class FVT_StatusUpdate_FileAttached extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS_STATUS_UPDATES = { HomepageUIConstants.FilterAll, HomepageUIConstants.MyUpdates };
	private final String[] TEST_FILTERS_IM_FOLLOWING = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private FilesUI uiFi;
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiFi = FilesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		uiFi = FilesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	* addStatusUpdate_TextFileAttached() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Add a status update and select "attach a file"</B></li>
	*<li><B>Step: Select "My Computer"</B></li>
	*<li><B>Step: Browse to a text file from your computer and click "OK"</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the status and file attachment has been added to the relevant view and that the file details are shown correctly</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/467D4B643C6A82CD85257944005D7CBD">TTT - Activity Stream Sharebox - 00021 - User should be able to attach a text file from their computer</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_TextFileAttached(){
		
		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a status update with file attachment using the UI
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file15, ".jpg", ShareLevel.EVERYONE);
		boolean fileUploaded = ProfileEvents.addStatusUpdateWithFileAttachmentUsingUI(ui, driver, uiFi, testUser1, baseFile, user1StatusUpdate);
		
		// Verify that the file uploaded successfully with all verifications completing successfully
		HomepageValid.verifyBooleanValuesAreEqual(fileUploaded, true);
		
		// Verify that the status update and file name are displayed in all necessary filters in the Status Updates view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user1StatusUpdate, baseFile.getName()}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the status update and file name are displayed in all necessary filters in the I'm Following view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user1StatusUpdate, baseFile.getName()}, TEST_FILTERS_IM_FOLLOWING, true);
		
		ui.endTest();
	}
}