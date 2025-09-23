package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.filepreview;

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
/* Copyright IBM Corp. 2010, 2014, 2016                              */
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

public class FVT_NonImage_FilePreview_AS extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;	
	private BaseFile baseFileJpg, baseFileOdp, baseFilePdf;
	private FileEntry publicFileJpg, publicFileOdp, publicFilePdf;
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will create a public jpg file
		baseFileJpg = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		publicFileJpg = FileEvents.addFile(baseFileJpg, testUser1, filesAPIUser1);
		
		// User 1 will create a public pdf file
		baseFilePdf = FileBaseBuilder.buildBaseFile(Data.getData().file19, ".pdf", ShareLevel.EVERYONE);
		publicFilePdf = FileEvents.addFile(baseFilePdf, testUser1, filesAPIUser1);
		
		// User 1 will create a public odp file
		baseFileOdp = FileBaseBuilder.buildBaseFile(Data.getData().file17, ".odp", ShareLevel.EVERYONE);
		publicFileOdp = FileEvents.addFile(baseFileOdp, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFileJpg);
		filesAPIUser1.deleteFile(publicFileOdp);
		filesAPIUser1.deleteFile(publicFilePdf);
	}
	
	/**
	* filePreview_jpgFile_uploaded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a new .jpg file with public access</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Step: testUser1 look at the story for the file uploaded</B></li>
	*<li><B>Verify: Verify there is no Details/Description box beneath the image preview</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/23E462A3C3C0054685257D0F00556EF1">TTT - FILE PREVIEW - 00024 - FILE CREATED - PUBLIC IMAGE FILE - NO DETAILS BOX DISPLAYED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void filePreview_jpgFile_uploaded(){

		ui.startTest();

		// Log into Connections as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be verified
		String fileUploadEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());

		// Create the file details to be verified
		String fileDetails = baseFileJpg.getRename() + baseFileJpg.getExtension();
		String nameDetails = testUser1.getDisplayName() + profilesAPIUser1.getUUID();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Verify that the file uploaded news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileUploadEvent, baseFileJpg);
			
			// Verify that the file details box is NOT displayed in any of the views
			HomepageValid.verifyFileDetailsBox(ui, driver, baseFileJpg, fileDetails, nameDetails, null, false);
		}	
		ui.endTest();
	}
	
	/**
	* filePreview_pdfFile_updated() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a new .pdf file with public access</B></li>
	*<li><B>Step: testUser1, after the file is uploaded, upload a newer version</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Step: testUser1 look at the story for the file updated</B></li>
	*<li><B>Verify: Verify the preview image of the first page of the file appears in the story, with a detail box beneath with file-name and file owner name as clickable links, plus any tags for the file</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3BCB56331780BECC85257D1000484DF6">TTT - FILE PREVIEW - 00025 - FILE UPDATED - COMPATIBLE PUBLIC FILE - EVENT APPEARS CORRECTLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void filePreview_pdfFile_updated(){

		ui.startTest();

		// User 1 will now update the file version of the public file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file19, ".pdf", ShareLevel.EVERYONE);
		FileEvents.updateFileVersion(publicFilePdf, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log into Connections as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be verified
		String fileEditedEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());

		// Create the file details to be verified
		String fileDetails = baseFilePdf.getRename() + baseFilePdf.getExtension();
		String nameDetails = testUser1.getDisplayName() + profilesAPIUser1.getUUID();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Verify that the file edited news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileEditedEvent, baseFilePdf);
			
			// Verify that the file details box is displayed in all views
			HomepageValid.verifyFileDetailsBox(ui, driver, baseFilePdf, fileDetails, nameDetails, null, true);
		}	
		ui.endTest();
	}

	/**
	* filePreview_odpFile_commented() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to Files</B></li>
	*<li><B>Step: testUser1 upload a public .odp file</B></li>
	*<li><B>Step: testUser1, after the file is uploaded, comment on the file</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Step: testUser1 Look at the event of the file comment added</B></li>
	*<li><B>Verify: Verify the preview image of the first page of the file appears in the story, with a detail box beneath with file-name and file owner name as clickable links, plus any tags for the file</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1BC063F15E0E7A4485257D100048C838">TTT - FILE PREVIEW - 00026 - FILE COMMENT ADDED - COMPATIBLE PUBLIC FILE - EVENT APPEARS CORRECTLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void filePreview_odpFile_commented() {

		ui.startTest();

		// User 1 will add a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFileOdp, user1Comment);
		
		// Log into Connections as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Create the file details to be verified
		String fileDetails = baseFileOdp.getRename() + baseFileOdp.getExtension();
		String nameDetails = testUser1.getDisplayName() + profilesAPIUser1.getUUID();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Verify that the file edited news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFileOdp);
			
			// Verify that the file details box is displayed in all views
			HomepageValid.verifyFileDetailsBox(ui, driver, baseFileOdp, fileDetails, nameDetails, null, true);
			
			// Verify that User 1's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, true);
		}	
		ui.endTest();	
	}	
}