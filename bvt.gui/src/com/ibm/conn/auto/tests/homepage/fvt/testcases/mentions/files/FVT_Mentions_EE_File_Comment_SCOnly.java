package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files;

import java.util.HashMap;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Mentions_EE_File_Comment_SCOnly extends SetUpMethodsFVT {

	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private FilesUI uiFiles;
	private User testUser1;
				
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will upload a new public file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* mentions_ee_fileComment_threeCharacters_profilePhoto() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Click to add a comment with @xxx in the embedded sharebox</B></li>
	*<li><B>Verify: Verify that the user profile photo appears in the typeahead</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/058A77D0744C2ED885257C6D00554E36">TTT - @Mentions - EE - File Comment - 00023 - When popup dialog is open profile photo appears - SC only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void mentions_ee_fileComment_threeCharacters_profilePhoto() {
		
		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a partial mention to the comments input field and retrieve the list of users and their photos from the typeahead
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		HashMap<Element, Element> mapOfItemsWithPhotos = FileEvents.openFileOverlayAndTypePartialMentionAndGetTypeaheadMenuItemsAndPhotos(ui, driver, uiFiles, fileUploadedEvent, mentions, 3);
		
		// Verify that all menu item elements had a photo element attached
		Set<Element> menuItemElements = mapOfItemsWithPhotos.keySet();
		for(Element menuItemElement : menuItemElements) {
			Element menuItemPhoto = mapOfItemsWithPhotos.get(menuItemElement);
			
			// Verify that the photo element has a value (ie. a null photo is a photo that did not exist in the UI)
			HomepageValid.verifyOneElementIsNotNull(menuItemPhoto);
		}
		ui.endTest();
	}
}
