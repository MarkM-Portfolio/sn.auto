package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.files;

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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author	Anthony Cox
 * Date:	11th March 2016
 */

public class FVT_ImFollowing_files_PrivateFile_followFiles extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseFile baseFile1, baseFile2, baseFile3, baseFile5, baseFile6;
	private FileEntry sharedFile1, sharedFile2, sharedFile3, sharedFile5, sharedFile6;
	private User testUser1, testUser2, testUser3;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);		
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will create a private file, share it with User 2 and User 2 will follow the file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile1 = FileEvents.addFileWithOneFollower(baseFile1, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will create a private file, share it with User 2 and User 2 will follow the file
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile2 = FileEvents.addFileWithOneFollower(baseFile2, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will create a private file, share it with User 2 and User 2 will follow the file
		baseFile3 = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile3 = FileEvents.addFileWithOneFollower(baseFile3, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will create a private file, share it with User 2 and User 2 will follow the file
		baseFile5 = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile5 = FileEvents.addFileWithOneFollower(baseFile5, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
				
		// User 1 will create a private file, share it with User 2 and User 2 will follow the file
		baseFile6 = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile6 = FileEvents.addFileWithOneFollower(baseFile6, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will also share sharedFile6 with User 3 so as they can be mentioned in the file
		FileEvents.shareFileWithUser(sharedFile6, testUser1, filesAPIUser1, profilesAPIUser3);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(sharedFile1);
		filesAPIUser1.deleteFile(sharedFile2);
		filesAPIUser1.deleteFile(sharedFile3);
		filesAPIUser1.deleteFile(sharedFile5);
		filesAPIUser1.deleteFile(sharedFile6);
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_createdPrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Upload a new file with Private access</B></li>
	*<li><B>Step: testUser 1 Share the file with User 2</B></li>
	*<li><B>Step: testUser 2 who is following this file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the file.file.created is not shown in files</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0E777660B91AC545852578F700509820">TTT - AS - Follow - Files - 00012 - files.file.created - PRIVATE FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void followFiles_createdPrivateFile() {
		
		ui.startTest();
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String shareFileEvent = FileNewsStories.getSharedAFileWithYouNewsStory(ui, testUser1.getDisplayName());
		String uploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile1);
			
			// Verify that the upload file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, uploadFileEvent, baseFile1);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_updatePrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Update a existing Private file</B></li>
	*<li><B>Step: testUser 2 Login to Home who is following the file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the story for files.file.updated appears with the timestamp to the right of the file icon</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BF5080388FF99CAC852578F700528DD1">TTT - AS - FOLLOW - PERSON - FILES - 00022 - files.file.updated - Private FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void followFiles_updatePrivateFile() {
		
		ui.startTest();
		
		// User 1 will now update the private file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", null);
		FileEvents.updateFileVersion(sharedFile2, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String editFileEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the edit file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, editFileEvent, baseFile2);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_sharedPrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Private access</B></li>
	*<li><B>Step: testUser 1 Share this file with a user 2</B></li>
	*<li><B>Step: testUser 2 Login to Home </B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the story for files.file.shared appears with the timestamp to the right of the file icon</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D3F6F808740057A3852578F7005566E2">TTT - AS - Follow - Files - 00042 - files.file.shared - Private FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void followFiles_sharedPrivateFile() {
		
		ui.startTest();
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story and timestamp element
		String shareFileEvent = FileNewsStories.getSharedAFileWithYouNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile1);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_fileComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Private access</B></li>
	*<li><B>Step: testUser 1 Comment on a Private file</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following  the file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, People & Files</B></li>
	*<li><B>Verify: Verify that files.file.comment.created displays with the timestamp to the right of the file icon, the size of the file and the comment under</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/878107E3FB09BBCB852578F70056B736">TTT - AS - Follow - Files - 00052 - files.file.comment.created - Private FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void followFiles_fileComment() {
		
		ui.startTest();
		
		// User 1 will comment on the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, sharedFile3, user1Comment);
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story and timestamp element
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile3);
						
			// Verify that the comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, true);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_likePrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to file</B></li>
	*<li><B>Step: testUser 1 upload a Private file</B></li>	
	*<li><B>Step: testUser 2 Log in to Home who is following  the file</B></li>	
	*<li><B>Step: testUser 1 Recommend a Private file</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created does not appear in the I'm Following view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BA34025677A2F320852578F8002B415F">TTT - AS - Follow - Files - 00062 - files.file.recommend.created - Private FILE (NEG IN MARCH)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void followFiles_likePrivateFile() {
		
		ui.startTest();
		
		// User 1 will now like / recommend the file
		FileEvents.likeFile(testUser1, filesAPIUser1, sharedFile1);
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String shareFileEvent = FileNewsStories.getSharedAFileWithYouNewsStory(ui, testUser1.getDisplayName());
		String likeFileEvent = FileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile1);
						
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile1);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: followFiles_updatedfileComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Private access</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a Private file</B></li>
	*<li><B>Step: testUser 2 Login to Home who is following the File</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, People & Files</B></li>
	*<li><B>Verify: Verify that files.file.comment.created displays with the timestamp to the right of the file icon, the size of the file and the updated comment under</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8EA5B539782A0973852579BB00642898">TTT - AS - Follow - Files - 00092 - files.file.comment.updated - Private FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void followFiles_updatedfileComment() {
		
		ui.startTest();
		
		// User 1 will now add a comment to the file and then update the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addAndEditFileComment(testUser1, filesAPIUser1, sharedFile5, user1Comment, user1EditedComment);
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories and elements to be verified in the UI
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updateCommentOnFileEvent = FileNewsStories.getUpdateCommentNewsStory(ui, baseFile5.getRename() + baseFile5.getExtension(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile5);
			
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateCommentOnFileEvent, baseFile5);
						
			// Verify that the updated comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1EditedComment}, null, true);
			
			// Verify that the original comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_PrivatefileCommentMention()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Private access</B></li>
	*<li><b>Step: testUser 2 follow the file
	*<li><B>Step: testUser 1 log back in and comment on the file mentioning User 3</B></li>
	*<li><B>Step: Log in to Home as testUser2</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify the mentions event appears in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4A9738E28F18CE7E85257C6D0040D28B">TTT - AS - Follow - Files - 00096 - Mentioning a user in a Private file comment</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void followFiles_PrivatefileCommentMention() {
		
		ui.startTest();
		
		// User 1 will now post a comment with mentions to User 3 to the private file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, sharedFile6, mentions);
		
		// Log into Connections as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified in the UI
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile6);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);
		}
		ui.endTest();
    }
}