package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.conn.auto.lcapi.APIFileHandler;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	9th March 2016
 */

public class FVT_ImFollowing_Tags_PublicFile extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles, HomepageUIConstants.FilterTags };
	
	private APIFileHandler filesAPIUser2;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private String tagToFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);		
		
		// Log in with User 1 and follow the tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 uploading a public file with the tag followed by User 1
		baseFile = FileBaseBuilder.buildBaseFileWithCustomTag(Data.getData().file1, ".jpg", ShareLevel.EVERYONE, tagToFollow);
		publicFile = FileEvents.addFile(baseFile, testUser2, filesAPIUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser2.deleteFile(publicFile);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_publicFileUpload</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 upload a public file and add a tag to the file</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All,Files & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the files.file.created story is displayed in Homepage / All Updates filtered by Tags and Files</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B816906AC2148A1A852578FC00537407">TTT -AS - FOLLOW - TAG - FILES - 00131 - files.file.created - PUBLIC FILE</a></li>
	*</ul>	  
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileUpload_PublicFile() {			
		
		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified in all views
		String uploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the upload file news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, uploadFileEvent, baseFile);
		}
		ui.endTest();
	}		
}	