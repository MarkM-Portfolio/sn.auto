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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;

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
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourFile_Public_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3, filesAPIUser4, filesAPIUser5, filesAPIUser6, filesAPIUser7, filesAPIUser8, filesAPIUser9;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFile1, baseFile2;
	private FileEntry publicFile1, publicFile2;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9;
			   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(9);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		filesAPIUser4 = initialiseAPIFileHandlerUser(testUser4);
		filesAPIUser5 = initialiseAPIFileHandlerUser(testUser5);
		filesAPIUser6 = initialiseAPIFileHandlerUser(testUser6);
		filesAPIUser7 = initialiseAPIFileHandlerUser(testUser7);
		filesAPIUser8 = initialiseAPIFileHandlerUser(testUser8);
		filesAPIUser9 = initialiseAPIFileHandlerUser(testUser9);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a public file for use in the test
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFile(baseFile1, testUser1, filesAPIUser1);
		
		// User 1 will now create a second public file for use in the test
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFile(baseFile2, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		filesAPIUser1.deleteFile(publicFile2);
	}

	/**
	* test_YourFile_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Files</B></li>
	*<li><B>Step: User 1 upload a File</B></li>
	*<li><B>Step: User 2 log into Files</B></li>
	*<li><B>Step: User 2 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 2</B></li>
	*<li><B>Step: User 4 to User 9 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your file."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your file."</B></li>
	*<li><B>Verify: Verify the event shows "{user 9} and 7 others commented on your file."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8CFEAC1930B58A3885257DE800522FB5">TTT - MY NOTIFICATIONS - FILES - 00010 - COMMENT ON FILE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourFile_Comment_Rollup(){
		
		ui.startTest();
		
		// User 2 will now post a comment to the file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, publicFile1, user2Comment, profilesAPIUser1);
		
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
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, true);
		}
		// User 3 will now post a comment to the file
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser3, filesAPIUser3, publicFile1, user3Comment, profilesAPIUser1);
		
		// Create the news story to be verified
		commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 2's comment and User 3's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment}, null, true);
		}
		// Users 4 through to 9 will now post a comment to the file
		User[] usersPostingComments = { testUser4, testUser5, testUser6, testUser7, testUser8, testUser9 };
		APIFileHandler[] apiUsersPostingComments = { filesAPIUser4, filesAPIUser5, filesAPIUser6, filesAPIUser7, filesAPIUser8, filesAPIUser9 };
		String userComments[] = new String[usersPostingComments.length];
		
		int index = 0;
		while(index < usersPostingComments.length) {
			// Set the comment to be posted by the current user
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			// Post the comment to the file as the current user
			FileEvents.addFileCommentOtherUser(usersPostingComments[index], apiUsersPostingComments[index], publicFile1, userComments[index], profilesAPIUser1);
			
			index ++;
		}
		// Create the news story to be verified
		commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_UserAndMany(ui, testUser9.getDisplayName(), "7");
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 8's comment and User 9's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{userComments[4], userComments[5]}, null, true);
			
			// Verify that the comments posted by Users 2 through to 7 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, userComments[0], userComments[1], userComments[2], userComments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourFile_LikeFile_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Files</B></li>
	*<li><B>Step: User 1 upload a File</B></li>
	*<li><B>Step: User 2 log into Files</B></li>
	*<li><B>Step: User 2 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 1</B></li>
	*<li><B>Step: User 3 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 2</B></li>
	*<li><B>Step: User 4 to User 9 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your file."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your file."</B></li>
	*<li><B>Verify: Verify the event shows "{user 9} and 7 others liked your file."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1D302D0CF356761585257DE800522FB9">TTT - MY NOTIFICATIONS - FILES - 00030 - LIKE A FILE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourFile_LikeFile_Rollup(){
		
		ui.startTest();
		
		// User 2 will now like / recommend the file
		FileEvents.likeFileOtherUser(publicFile2, testUser2, filesAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_User(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile2);
		}
		// User 3 will now like / recommend the file
		FileEvents.likeFileOtherUser(publicFile2, testUser3, filesAPIUser3);
		
		// Create the news story to be verified
		likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile2);
		}
		// Users 4 through to 9 will now like / recommend the file
		User[] usersLikingFile = { testUser4, testUser5, testUser6, testUser7, testUser8, testUser9 };
		APIFileHandler[] apiUsersLikingFile = { filesAPIUser4, filesAPIUser5, filesAPIUser6, filesAPIUser7, filesAPIUser8, filesAPIUser9 };
		int index = 0;
		while(index < usersLikingFile.length) {
			// Like / recommend the file as the current user
			FileEvents.likeFileOtherUser(publicFile2, usersLikingFile[index], apiUsersLikingFile[index]);
			index ++;
		}
		// Create the news story to be verified
		likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_UserAndMany(ui, testUser9.getDisplayName(), "7");
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile2);
		}		
		ui.endTest();
	}
}