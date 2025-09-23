package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.files.fileoverlay;

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
/* Copyright IBM Corp. 2015, 2016                                	 */
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

public class FVT_Discover_PublicFileCommentEdit_FileOverlay extends SetUpMethodsFVT{

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
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
	* fileCommentEdit_FileOverlay() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 uploads a public file</B></li>
	*<li><B>Step: User 1 comments on the file</B></li>
	*<li><B>Step: User 1 edits the comment on the file</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / Discover / All & Files, selects the story</B></li>
	*<li><B>Verify: File Detail Overlay launches</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8510E57080264D8E85257E90004733F7">TTT - DISC - FILES - 00110 - File Details overlay</a></li>
	*</ul>
	*@author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileCommentEdit_FileOverlay() {
		
		ui.startTest();
		
		// User 1 will now add a comment to the public file
		String fileComment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addAndEditFileComment(testUser1, filesAPIUser1, publicFile, fileComment, updatedComment);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be used to open the File Details Overlay
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the current filter
			UIEvents.filterBy(ui, filter);
			
			// Open the file details overlay for the comment on file event in the current view
			FileEvents.openFileOverlay(ui, commentOnFileEvent);
			
			// Verify that the file details overlay is displayed
			HomepageValid.verifyFileDetailsOverlayIsDisplayed(ui, baseFile);
		}
		ui.endTest();
	}
}