package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.files;

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
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
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
 * [Notification/Mentions Badges] FVT UI Automation for Story 146307
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/147242
 * @author Patrick Doherty
 */

public class FVT_FileComment_NotificationBadging extends SetUpMethodsFVT {
	
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3, filesAPIUser4;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseFile basePrivateFile, basePublicFile;
	private FileEntry privateFile, publicFile;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
	
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		filesAPIUser4 = initialiseAPIFileHandlerUser(testUser4);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public file
		basePublicFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(basePublicFile, testUser1, filesAPIUser1);
		
		// User 1 will now create a private file which is shared with User 2
		basePrivateFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		privateFile = FileEvents.addFile(basePrivateFile, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile);
		filesAPIUser2.deleteFile(privateFile);
	}

	/**
	* test_File_CommentEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a public and private file</B></li>
	*<li><B>Step: User 1 share the private file with User 2</B></li>
	*<li><B>Step: User 2 comment on both the files</B></li>
	*<li><B>Step: User 3 comment on the public file</B></li>
	*<li><B>Step: User 4 comment on the public file</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 4 delete their comment</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '2'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BABDE022DA31FFBB85257E18003BBC24">TTT - NOTIFICATIONS BADGING - FILES - 00012 - DIFFERENT FILE COMMENT EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_File_CommentEvents(){

		ui.startTest();
		
		// Login and navigate to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view - this will reset the My Notifications badge counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
				
		// User 2 will now post a comment to the public file
		String user2PublicFileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, publicFile, user2PublicFileComment, profilesAPIUser1);
		
		// User 2 will now post a comment to the private file
		String user2PrivateFileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, privateFile, user2PrivateFileComment, profilesAPIUser1);
		
		// User 3 will now post a comment to the public file
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser3, filesAPIUser3, publicFile, user3Comment, profilesAPIUser1);
		
		// User 4 will now post a comment to the public file
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileComment user4FileComment = FileEvents.addFileCommentOtherUser(testUser4, filesAPIUser4, publicFile, user4Comment, profilesAPIUser1);
		
		// Refresh the badges by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the My Notifications and Notification Center badges have now been set to 2
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 2);
		
		// User 4 will now delete their comment posted to the public file
		FileEvents.deleteFileComment(user4FileComment, testUser4, filesAPIUser4);
		
		// Refresh the badges by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the My Notifications and Notification Center badges are still set to 2
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 2);
				
		ui.endTest();
	}
}