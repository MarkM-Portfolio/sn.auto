package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.files;

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
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	Author:		Anthony Cox
 * 	Date:		14th January 2016
 */

public class FVT_Saved_Public_Files_SaveCommented extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
	private FileEntry publicFile;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		// User 1 will now upload a public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* test_PublicFile_SaveCommentedFileStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Files</B></li>
	*<li><B>2. Upload a file</B></li>
	*<li><B>3. Comment on the file</B></li>
	*<li><B>4. Go to Homepage / Discover / Files</B></li>
	*<li><b>5: Go to the story of the file commented on and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Files</b></li>
	*<li><b>Verify: Verify that the story saved is of the file commented on with the file details and the comment is inline</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/67B16954229A3016852579B300348999">TTT: AS - Saved - 00060 - Save A Story Of A File Commented On</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_PublicFile_SaveCommentedFileStory() {
		
		ui.startTest();
		
		// User 1 will now post a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser1, filesAPIUser1, publicFile, user1Comment);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String youCommentedOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Save the comment on file event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, youCommentedOnFileEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String userCommentedOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{userCommentedOnFileEvent, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}
