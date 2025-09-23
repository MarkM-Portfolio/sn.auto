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

public class FVT_YourFile_Public_You_Others extends SetUpMethodsFVT {
	
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
	* test_YourFile_You_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Files</B></li>
	*<li><B>Step: User 1 upload a File</B></li>
	*<li><B>Step: User 1 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 1</B></li>
	*<li><B>Step: User 2 log into Files</B></li>
	*<li><B>Step: User 2 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 2</B></li>
	*<li><B>Step: User 3 to User 5 comment on the file</B></li>
	*<li><B>Step: User 1 comment on the file again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your file."</B></li>
	*<li><B>Verify: Verify the event shows "You and 4 others commented on your file."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2973C7CAF191973D85257DE800522FB6">TTT - MY NOTIFICATIONS - FILES - 00011 - COMMENT ON YOUR OWN FILE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourFile_You_Comment_Rollup(){
		
		ui.startTest();
		
		// User 1 will now post a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFile1, user1Comment);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		// User 2 will now post a comment to the file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, publicFile1, user2Comment, profilesAPIUser1);
		
		// Create the news story to be verified
		commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_UserAndYou(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 1's comment and User 2's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment, user2Comment}, null, true);
		}
		// Users 3 through to 5 will now post a comment to the file with User 1 then posting a second comment to the file
		User[] usersPostingComments = { testUser3, testUser4, testUser5, testUser1 };
		APIFileHandler[] apiUsersPostingComments = { filesAPIUser3, filesAPIUser4, filesAPIUser5, filesAPIUser1 };
		String userComments[] = new String[usersPostingComments.length];
				
		int index = 0;
		while(index < usersPostingComments.length) {
			// Set the comment to be posted by the current user
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			if(usersPostingComments[index].getDisplayName().equals(testUser1.getDisplayName())) {
				// Post the comment to the file as User 1
				FileEvents.addFileComment(usersPostingComments[index], apiUsersPostingComments[index], publicFile1, userComments[index]);
			} else {
				// Post the comment to the file as the current user
				FileEvents.addFileCommentOtherUser(usersPostingComments[index], apiUsersPostingComments[index], publicFile1, userComments[index], profilesAPIUser1);
			}
			index ++;
		}
		// Create the news story to be verified
		commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_YouAndMany(ui, "4");
			
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile1);
			
			// Verify that User 5's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{userComments[2], userComments[3]}, null, true);
			
			// Verify that the first comment posted by User 1 and the comments posted by Users 2 through to 4 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment, user2Comment, userComments[0], userComments[1]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourFile_You_LikeFile_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Files</B></li>
	*<li><B>Step: User 1 upload a File</B></li>
	*<li><B>Step: User 1 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 1</B></li>
	*<li><B>Step: User 2 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 2</B></li>
	*<li><B>Step: User 3 to User 9 like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Files - verification point 3</B></li>
	*<li><B>Verify: Verify no events appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your file."</B></li>
	*<li><B>Verify: Verify the event shows "You and 8 others liked your file."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2DB90A5BE2FB23D085257DE800522FBA">TTT - MY NOTIFICATIONS - FILES - 00031 - LIKE YOUR OWN FILE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourFile_You_LikeFile_Rollup(){
		
		ui.startTest();
		
		// User 1 will now like / recommend the file
		FileEvents.likeFile(testUser1, filesAPIUser1, publicFile2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile2);
		}		
		// User 2 will now like / recommend the file
		FileEvents.likeFileOtherUser(publicFile2, testUser2, filesAPIUser2);
		
		// Create the news story to be verified
		likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_UserAndYou(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile2);
		}	
		// Users 3 through to 9 will now like / recommend the file
		User[] usersLikingFile = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9 };
		APIFileHandler[] apiUsersLikingFile = { filesAPIUser3, filesAPIUser4, filesAPIUser5, filesAPIUser6, filesAPIUser7, filesAPIUser8, filesAPIUser9 };
		int index = 0;
		while(index < usersLikingFile.length) {
			// Like / recommend the file as the current user
			FileEvents.likeFileOtherUser(publicFile2, usersLikingFile[index], apiUsersLikingFile[index]);
			index ++;
		}
		// Create the news story to be verified
		likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_YouAndMany(ui, "8");
		
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