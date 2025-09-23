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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following/files) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_files_PublicFiles_followFiles extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseFile baseFile1, baseFile2, baseFile3, baseFile5, baseFile6, baseFile7, baseFile8;
	private FileEntry publicFile1, publicFile2, publicFile3, publicFile5, publicFile6, publicFile7, publicFile8;
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
		
		// Initialise all required APIProfilesHandler users
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create the first public file with User 2 following the file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFileWithOneFollower(baseFile1, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
			
		// User 1 will now create the second public file with User 2 following the file
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFileWithOneFollower(baseFile2, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now create a public file with no followers
		baseFile3 = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		publicFile3 = FileEvents.addFile(baseFile3, testUser1, filesAPIUser1);
		
		// User 1 will now create the third public file with User 2 following the file
		baseFile5 = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		publicFile5 = FileEvents.addFileWithOneFollower(baseFile5, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now create the fourth public file with User 2 following the file
		baseFile6 = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		publicFile6 = FileEvents.addFileWithOneFollower(baseFile6, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now create a public file with User 2 following the file
		baseFile7 = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		publicFile7 = FileEvents.addFileWithOneFollower(baseFile7, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now create a public file with User 2 following the file
		baseFile8 = FileBaseBuilder.buildBaseFile(Data.getData().file8, ".jpg", ShareLevel.EVERYONE);
		publicFile8 = FileEvents.addFileWithOneFollower(baseFile8, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now share a public file with User 3 so that the mentions comment will work correctly
		FileEvents.shareFileWithUser(publicFile8, testUser1, filesAPIUser1, profilesAPIUser3);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		filesAPIUser1.deleteFile(publicFile2);
		filesAPIUser1.deleteFile(publicFile3);
		filesAPIUser1.deleteFile(publicFile5);
		filesAPIUser1.deleteFile(publicFile6);
		filesAPIUser1.deleteFile(publicFile7);
		filesAPIUser1.deleteFile(publicFile8);
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_createdPublicFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Upload a new file with public access</B></li>
	*<li><B>Step: testUser 2 who is following this file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the file.file.created is not shown in files</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7C1565ABC983A757852578F7005095A0">TTT - AS - Follow - Files - 00011 - files.file.created - PUBLIC FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_createdPublicFile() {
		
		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String uploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the upload file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, uploadFileEvent, baseFile1);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_updatePublicFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Update a existing public file</B></li>
	*<li><B>Step: testUser 2 Login to Home who is following the file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the story for files.file.updated appears with the timestamp to the right of the file icon and the size of the file</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A5D142713519379D852578F700528C52">TTT - AS - FOLLOW - PERSON - FILES - 00211 - files.file.updated - PUBLIC FILE (NEG SC NOV)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_updatePublicFile() {
		
		ui.startTest();
		
		// User 1 will now update the version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEvents.updateFileVersion(publicFile2, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
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
	*<li><B>Name: followFiles_sharedPublicFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has public access</B></li>
	*<li><B>Step: testUser 1 Share this file with a user 2</B></li>
	*<li><B>Step: testUser 2 Login to Home </B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify that files.file.shared appears in the files filter</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1353B11747E7F656852578F700556573">TTT - AS - Follow - Files - 00041 - files.file.shared - PUBLIC FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_sharedPublicFile() {
		
		ui.startTest();
		
		// User 1 will now share a public file with User 2
		FileEvents.shareFileWithUser(publicFile3, testUser1, filesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String shareFileEvent = FileNewsStories.getSharedAFileWithYouNewsStory(ui, testUser1.getDisplayName());
				
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile3);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_fileComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has public access</B></li>
	*<li><B>Step: testUser 1 Comment on a public file</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, People & Files</B></li>
	*<li><B>Verify: Verify that files.file.comment.created displays with the timestamp to the right of the file icon, the size of the file and the comment under</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/26108A7F00CFD2AE852578F70056B592">TTT - AS - Follow - Files - 00051 - files.file.comment.created - PUBLIC FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_fileComment() {
		
		ui.startTest();
		
		// User 1 will now comment on a public file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, publicFile5, user1Comment);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
						
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile5);
						
			// Verify that User 1's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, true);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_likePublicFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to file</B></li>
	*<li><B>Step: testUser 1 upload a public file</B></li>	
	*<li><B>Step: testUser 2 Log in to Home who is following  the file</B></li>	
	*<li><B>Step: testUser 1 Recommend a public file</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created does not appear in the I'm Following view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9064B89D9457C9E8852578F8002C9144">TTT - AS - Follow - Files - 00061 - files.file.recommend.created - PUBLIC FILE (NEG IN MARCH)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_likePublicFile() {
		
		ui.startTest();
		
		// User 1 will now like / recommend the file
		FileEvents.likeFile(testUser1, filesAPIUser1, publicFile6);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeFileEvent = FileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
						
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile6);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: followFiles_updatedfileComment()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has public access</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a public file</B></li>
	*<li><B>Step: testUser 2 Login to Home who is following the File</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, People & Files</B></li>
	*<li><B>Verify: Verify that files.file.comment.updated does NOT display with the timestamp to the right of the file icon, the size of the file and the comment under</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B50A61E96B4F6847852579BB0063E138">TTT - AS - Follow - Files - 00091 - files.file.comment.updated - PUBLIC FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_updatedfileComment(){
		
		ui.startTest();
		
		// User 1 will now comment on a public file and edit the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addAndEditFileComment(testUser1, filesAPIUser1, publicFile7, user1Comment, user1EditedComment);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story and timestamp element to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updateCommentOnFileEvent = FileNewsStories.getUpdateCommentNewsStory(ui, baseFile7.getRename() + baseFile7.getExtension(), testUser1.getDisplayName());
				
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
						
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile7);
						
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateCommentOnFileEvent, baseFile7);
						
			// Verify that User 1's updated comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1EditedComment}, null, true);
			
			// Verify that User 1's original comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_publicfileCommentMention()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 Log in to Files and Upload a Public file</B></li>
	*<li><B>Step: User 2 follow User 1's Public file</B></li>
	*<li><B>Step: User 1 Open the Public file that user 2 is following and add a comment mentioning User 3</B></li>
	*<li><B>Step: Log in to Home as user 2</B></li>
	*<li><B>Step: User 2 Go to Homepage / Updates/I'm Following / Files & All</B></li>
	*<li><B>Verify: Verify the mentions event appears in all the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2882E2FF03DC48D985257C6D003F56E9">TTT - AS - Follow - Files - 00095 - Mentioning a user in a public file comment</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_publicfileCommentMention(){
		
		ui.startTest();
		
		// User 1 will now comment on the file with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, publicFile8, mentions);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story and mentions text to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
						
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile8);
						
			// Verify that mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);
		}
		ui.endTest();
    }
}