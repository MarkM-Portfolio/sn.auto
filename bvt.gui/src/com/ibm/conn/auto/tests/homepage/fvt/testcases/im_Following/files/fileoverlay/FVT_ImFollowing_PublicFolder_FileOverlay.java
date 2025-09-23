package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.files.fileoverlay;

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
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                			 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [FiDO replacing Files EE in Activity Streams] FVT UI Automation for Story 154776
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/165016
 * @author Patrick Doherty
 */

public class FVT_ImFollowing_PublicFolder_FileOverlay extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };

	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private BaseFile baseFile, baseFolder;
	private FileEntry publicFile, publicFolder;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);		
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);

		// User 1 will now create a public file 
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now create a public folder
		baseFolder = FileBaseBuilder.buildBaseFile(getClass().getSimpleName() + Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		publicFolder = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		
		// User 2 will now follow the public folder
		FileEvents.followFolder(publicFolder, testUser2, filesAPIUser2);
		
		// User 1 will now add the public file to the public folder
		FileEvents.addFileToFolder(testUser1, filesAPIUser1, publicFile, publicFolder);
	}
	   
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the folder created during the test
		filesAPIUser1.deleteFolder(publicFolder);
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}

	/**
	* fileOverlayOpens_imFollowing_PublicFolder() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 creates a public folder</B></li>
	*<li><B>Step: User 2 follows the public folder</B></li>
	*<li><B>Step: User 1 adds a public file to the public folder</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All & Files, selects the story</B></li>
	*<li><B>Verify: File Detail Overlay launches</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/C07DF89F1C990C3A8525722C004A1BD4/0421F75360D921AC85257E91003B1DCC">TTT - AS - Follow - FILES - 00100 - File Details overlay</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayOpens_imFollowing_PublicFolder() {

		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be used to open the file details overlay
		String addFileToFolderEvent = FileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Ensure that the required filter has been set in the UI
			UIEvents.filterBy(ui, filter);
			
			// Open the file details overlay for the add file to folder event news story
			FileEvents.openFileOverlay(ui, addFileToFolderEvent);
			
			// Verify that the file details overlay is correctly displayed in the UI
			HomepageValid.verifyFileDetailsOverlayIsDisplayed(ui, baseFile);
		}
		ui.endTest();
	}
}