package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.files;

import java.util.ArrayList;

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
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following/People) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 08/2015
 */

public class FVT_ImFollowing_Person_PrivateFile extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private ArrayList<FileEntry> filesForDeletion = new ArrayList<FileEntry>();
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
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
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[3];
			TEST_FILTERS[2] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[2];
		}
		
		// Set the common filters to be added to the array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterFiles;
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		for(FileEntry fileEntry : filesForDeletion) {
			filesAPIUser1.deleteFile(fileEntry);
		}
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdatePrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Update a private file</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, people & Files</B></li>
	*<li><B>Verify: Verify that the files.file.updated story is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9DE0BFD4B57E9E2C852578FB0053270F">TTT -  AS - FOLLOW - PERSON - FILES - 00212 - files.file.updated - PRIVATE FILE (NEG SC NOV)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatePrivateFile(){
		
		ui.startTest();
		
		// User 1 will now create a new private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.add(privateFile);
		
		// User 1 will now update the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
		FileEvents.updateFileVersion(privateFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String updateFileEvent = FileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the update file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_Createdfilecomment_PrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Comment on a private file</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, people & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.created is not displayed in the People and Files view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E215897D1F77C692852578FC002B41DA">TTT - AS - FOLLOW - PERSON - FILES - 00242 - files.file.comment.created - PRIVATE FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileComment_Privatefile(){
		
		ui.startTest();
		
		// User 1 will now create a new private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.add(privateFile);
		
		// User 1 will now comment on the file
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, privateFile, comment);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentEvent, baseFile);
			
			// Verify that the comment posted to the file is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{comment}, null, false);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Person_Updatedfilecomment_PrivateFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a private file</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, people & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.updated is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3C184ACB3F54CBC4852579BF0047E483">TTT - AS - FOLLOW - PERSON - FILES - 00247 - files.file.comment.updated - PRIVATE FILE (NEG SC NOV)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatedfileComment(){
		
		ui.startTest();
		
		// User 1 will now create a new private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.add(privateFile);
		
		// User 1 will now comment on the file and will then edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addAndEditFileComment(testUser1, filesAPIUser1, privateFile, comment, updatedComment);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news stories to be verified
		String commentEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updateCommentEvent = FileNewsStories.getUpdateCommentNewsStory(ui, baseFile.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentEvent, baseFile);
			
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateCommentEvent, baseFile);
			
			// Verify that the original comment and updated comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{comment, updatedComment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_Likeprivatefile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to file</B></li>
	*<li><B>Step: testUser 1 Recommend a private file</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, people & Files</B></li>
	*<li><B>Verify: Verify that the files.file.recommend.created is not displayed within the People and Files view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E6CB5535E93491E4852578FC002CECCE">TTT - AS - FOLLOW - PERSON - FILES - 00252 - files.file.recommend.created - PRIVATE FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void likePrivateFile(){
		
		ui.startTest();
		
		// User 1 will now create a new private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.add(privateFile);
		
		// User 1 will now like the file
		FileEvents.likeFile(testUser1, filesAPIUser1, privateFile);
		
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
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		ui.endTest();	
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Person_fileCommentMention_privatefile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 upload a public file</B></li>
	*<li><B>Step: testUser 1 comment on the ptivate file mentioning User 3</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, people & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.mentions is NOT  displayed in the People and Files view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/20B1D608B1F4A73485257C6D004675A6">TTT - AS - FOLLOW - PERSON - FILES - 00264 - files.file.comment.mentions - PRIVATE FILE</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privatefileCommentMention(){
		
		ui.startTest();
		
		// User 1 will now create a new private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.add(privateFile);
		
		// User 1 will now post a comment with mentions to User 3 to the file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, privateFile, mentions);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String commentEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentEvent, baseFile);
			
			// Verify that the mentions text is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, false);
		}
		ui.endTest();		
    }
}