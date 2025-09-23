package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.files;

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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_StandaloneFileEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser2;
	private BaseFile privateBaseFile, publicBaseFile;
	private FileEntry privateFile, publicFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 will now create a public standalone file
		publicBaseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(publicBaseFile, testUser1, filesAPIUser1);
		
		// User 1 will now create a private standalone file which is shared with User 2
		privateBaseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		privateFile = FileEvents.addFile(privateBaseFile, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile);
		filesAPIUser1.deleteFile(privateFile);
	}
	
	/**
	* test_FileComment_PublicFile() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 2 comment on the file and like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 6 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the reply is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 replies are shown inline in the view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/293681B5428E8B9C85257E2F0036A45F">TTT - INLINE COMMENTS - 00050 - FILE EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_PublicFile() {

		ui.startTest();
		
		// User 2 will now comment on the file
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, publicBaseFile);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1}, null, true);
		}
		// User 2 will now add 6 more comments to the file
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment2);
		
		String user2Comment3 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment3);
		
		String user2Comment4 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment4);
		
		String user2Comment5 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment5);

		String user2Comment6 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment6);
		
		String user2Comment7 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, publicFile, user2Comment7);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, publicBaseFile);
			
			// Verify that the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment6, user2Comment7}, null, true);
			
			// Verify that User 2's first five comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1, user2Comment2, user2Comment3, user2Comment4, user2Comment5}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_FileComment_PrivateFile() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 2 comment on the file and like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 6 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the reply is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 replies are shown inline in the view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/293681B5428E8B9C85257E2F0036A45F">TTT - INLINE COMMENTS - 00050 - FILE EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_PrivateFile() {

		ui.startTest();
		
		// User 2 will now comment on the file
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, privateBaseFile);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1}, null, true);
		}
		// User 2 will now add 6 more comments to the file
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment2);
		
		String user2Comment3 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment3);
		
		String user2Comment4 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment4);
		
		String user2Comment5 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment5);

		String user2Comment6 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment6);
		
		String user2Comment7 = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addCommentToFile(testUser2, filesAPIUser2, privateFile, user2Comment7);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, privateBaseFile);
			
			// Verify that the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment6, user2Comment7}, null, true);
			
			// Verify that User 2's first five comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1, user2Comment2, user2Comment3, user2Comment4, user2Comment5}, null, false);
		}
		ui.endTest();	
	}	
}