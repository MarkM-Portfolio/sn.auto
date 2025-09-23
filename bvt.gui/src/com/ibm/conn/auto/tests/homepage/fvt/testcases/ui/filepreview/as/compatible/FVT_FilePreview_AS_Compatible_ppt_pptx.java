package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.filepreview.as.compatible;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016, 2017			                         */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Preview non image file types in AS] FVT Automation for Story 122132
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/128417
 * @author Patrick Doherty
 */

public class FVT_FilePreview_AS_Compatible_ppt_pptx extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFilePpt, baseFilePptx;
	private FileEntry publicFilePpt, publicFilePptx;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will create a public ppt file
		baseFilePpt = FileBaseBuilder.buildBaseFile(Data.getData().file20, ".ppt", ShareLevel.EVERYONE);
		publicFilePpt = FileEvents.addFile(baseFilePpt, testUser1, filesAPIUser1);
		
		// User 1 will create a public pptx file
		baseFilePptx = FileBaseBuilder.buildBaseFile(Data.getData().file21, ".pptx", ShareLevel.EVERYONE);
		publicFilePptx = FileEvents.addFile(baseFilePptx, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFilePpt);
		filesAPIUser1.deleteFile(publicFilePptx);
	}

	/**
	* filePreview_pptFile_uploaded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a new .ppt file with public access</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Step: testUser2 look at the story for the file uploaded</B></li>
	*<li><B>Verify: Verify the file uploaded event displays a preview image of the first page of the file added as the first item, with a details/description box underneath it</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B912DEDAF63B194A85257D0E00508BDA">TTT - FILE PREVIEW - 00010 - File Created - Compatible Public File - Preview image appears</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void filePreview_pptFile_uploaded(){

		ui.startTest();

		// Log into Connections as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String fileUploadEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		// Create the file details to be verified
		String fileDetails = baseFilePpt.getRename() + baseFilePpt.getExtension();
		String nameDetails = testUser1.getDisplayName() + profilesAPIUser1.getUUID();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Verify that the file uploaded news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileUploadEvent, baseFilePpt);
			
			// Verify that the file details box is displayed in all views
			HomepageValid.verifyFileDetailsBox(ui, driver, baseFilePpt, fileDetails, nameDetails, null, true);
		}		
		ui.endTest();	
	}

	/**
	* filePreview_pptxFile_uploaded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a new .pptx file with public access</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Step: testUser2 look at the story for the file uploaded</B></li>
	*<li><B>Verify: Verify the file uploaded event displays a preview image of the first page of the file added as the first item, with a details/description box underneath it</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B912DEDAF63B194A85257D0E00508BDA">TTT - FILE PREVIEW - 00010 - File Created - Compatible Public File - Preview image appears</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void filePreview_pptxFile_uploaded(){

		ui.startTest();

		// Log into Connections as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String fileUploadEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		// Create the file details to be verified
		String fileDetails = baseFilePptx.getRename() + baseFilePptx.getExtension();
		String nameDetails = testUser1.getDisplayName() + profilesAPIUser1.getUUID();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Verify that the file uploaded news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileUploadEvent, baseFilePptx);
			
			// Verify that the file details box is displayed in all views
			HomepageValid.verifyFileDetailsBox(ui, driver, baseFilePptx, fileDetails, nameDetails, null, true);
		}		
		ui.endTest();	
	}
}