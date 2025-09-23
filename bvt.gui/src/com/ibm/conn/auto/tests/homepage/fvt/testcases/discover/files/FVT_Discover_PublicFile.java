package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.files;

import java.util.HashMap;
import java.util.Set;

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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016 	                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_PublicFile extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };

	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private HashMap<FileEntry, APIFileHandler> filesForDeletion = new HashMap<FileEntry, APIFileHandler>();
	private HashMap<FileEntry, APIFileHandler> foldersForDeletion = new HashMap<FileEntry, APIFileHandler>();
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration		
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
	}	
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files created during the test
		Set<FileEntry> filesToDelete = filesForDeletion.keySet();
		Set<FileEntry> foldersToDelete = foldersForDeletion.keySet();
		
		for(FileEntry fileEntry : filesToDelete) {
			filesForDeletion.get(fileEntry).deleteFile(fileEntry);
		}

		for(FileEntry folderEntry : foldersToDelete) {
			foldersForDeletion.get(folderEntry).deleteFolder(folderEntry);
		}
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFileLike()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 add a recommendation to a file with public access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify:</B> Verify that the news story for files.file.recommend.created is seen with the timestamp to the right of the file icon, the size of the file, number of likes, the tag added and the comment under</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/23BE4B2E79F6E8F5852578760079E7D6">TTT - DISC - FILES - 00060 - FILES.FILE.RECOMMEND.CREATED - PUBLIC FILE</a></li>
	 * @author Naomi Pakenham	 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFileLike() {
		
		ui.startTest();
		
		// User 1 create a file and like it
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addAndLikeFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String likeFileEvent = FileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file news story and all components are displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFilecreated()()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 upload a new file with public access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify:</B> Verify that the news story for files.file.created is seen with the timestamp to the right of the file icon and the size of the file</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F88F44030BA4C36852578760079E7BE">TTT - DISC - FILES - 00010 - FILES.FILE.CREATED - PUBLIC FILE</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFileUpload() {
		
		ui.startTest();
		
		// User 1 create a file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// Log into Connections as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story
		String uploadFileEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the upload file news story and all components are displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, uploadFileEvent, baseFile);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFileComment()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 add a comment to a file with public access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.comment.created is seen with the timestamp to the right of the file icon, the size of the file, the tag added and the comment under</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63575BDE1A435565852578760079E7D1">TTT - DISC - FILES - 00050 - FILES.FILE.COMMENT.CREATED - PUBLIC FILE</a></li>
	 * @author Naomi Pakenham	 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFileComment() {
		
		ui.startTest();

		// User 1 create a file and comment on it
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will now add a comment to the file
		String fileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser1, filesAPIUser1, fileEntry, fileComment);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file news story and all components are displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify the comment posted to the file by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileComment}, null, true);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();			
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFileCommentEdit()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 update an existing comment to a file that has public access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.comment.created is seen with the timestamp to the right of the file icon, the size of the file, the tag added and the comment under</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B868D8A8909F6FFC852579BC005A5B3C">TTT - DISC - FILES - 00055 - FILES.FILE.COMMENT.UPDATED - PUBLIC FILE</a></li>
	 * @author Naomi Pakenham	 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFileCommentEdit() {
		
		ui.startTest();
		
		// User 1 create a file, comment on it and edit the comment
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will now comment on the file and then update the comment
		String fileComment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedFileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addAndEditFileComment(testUser1, filesAPIUser1, fileEntry, fileComment, updatedFileComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updatedCommentEvent = FileNewsStories.getUpdateCommentNewsStory(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file news story and all components are displayed in all views while the update comment event is NOT displayed
			HomepageValid.verifyFileNewsStoriesAsPresentAndAbsentInAS(ui, driver, commentOnFileEvent, updatedCommentEvent, baseFile);
			
			// Verify that the updated comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedFileComment}, null, true);
						
			// Verify that the original comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileComment}, null, false);
						
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFolderCreate()</li>
	 * <li><B>How:</B> Use the API to create a public folder owned by testUser1 </li>
	 * <li><B>Purpose:</B> Test that Story shows correctly in the Discover view of testUser2</li>
	 * <li><B>Verify:</B> Verify that the folder creation story does NOT show in testUser2's Discover/All and Discover/Files views</li>
	 * <li>There is no TTT link for this test case</li>
	 * @author Naomi Pakenham	    
	 */	 
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFolderCreate() {
		
		String testName = ui.startTest();		
		
		// User 1 create a folder
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), "", ShareLevel.EVERYONE);	
		FileEntry folderEntry = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		foldersForDeletion.put(folderEntry, filesAPIUser1);	

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String folderAddedEvent = FileNewsStories.getCreateFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());

		// Verify the folder added event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{folderAddedEvent}, TEST_FILTERS, false);

		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(folderEntry);
		foldersForDeletion.remove(folderEntry);
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> publicFolderFileAdded()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 create a folder that is shared public</B></li>
	 * <li><B>Step: User 1 add a file to the folder</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that there is a news story that is "<userName> shared <fileName> with <folderName>"</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BBE5747BC98C7C17852578760079E7D8">TTT - DISC - FILES - 00070 - FILES.COLLECTION.FILE.ADDED - PUBLIC SHARED FOLDER</a></li>
	 * @author Naomi Pakenham	
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_publicFolderFileAdded() {
		
		String testName = ui.startTest();
		
		// User 1 create a folder
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), ".jpg", ShareLevel.EVERYONE);		
		FileEntry folderEntry = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		foldersForDeletion.put(folderEntry, filesAPIUser1);
		
		// User 1 create a public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);

		// User 1 will now add the file to the folder
		FileEvents.addFileToFolder(testUser1, filesAPIUser1, fileEntry, folderEntry);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileAddedToFolderEvent = FileNewsStories.getAddFileToFolderNewsStory(ui, folderEntry.getTitle(), testUser1.getDisplayName());

		// Verify the file added to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);

		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		filesAPIUser1.deleteFile(folderEntry);
		foldersForDeletion.remove(folderEntry);	
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> removeFileFromPublicFolder()()</li>
	 * <li><B>How:</B> Use the API to create a public folder owned by testUser1, add a file and then remove the file </li>
	 * <li><B>Purpose:</B> Test that Story shows correctly in the Discover view of testUser2</li>
	 * <li><B>Verify:</B> Verify that the file story shows correctly in testUser2's Discover/All and Discover/Files views</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/519090348A66ED4D8525798A004F39FC">TTT - DISC - FILES - 00090 - REMOVE FILE SHARED WITH PUBLIC FOLDER</a></li>
	 * @author Naomi Pakenham	 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_removeFileFromPublicFolder() {
		
		String testName = ui.startTest();
		
		// User 1 create a folder
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), ".jpg", ShareLevel.EVERYONE);		
		FileEntry folderEntry = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		foldersForDeletion.put(folderEntry, filesAPIUser1);
		
		// User 1 create a file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will now add the file to the folder
		FileEvents.addFileToFolder(testUser1, filesAPIUser1, fileEntry, folderEntry);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileAddedToFolderEvent = FileNewsStories.getAddFileToFolderNewsStory(ui, folderEntry.getTitle(), testUser1.getDisplayName());

		// Verify the file added to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		// Remove the file from the folder
		FileEvents.removeFileFromFolder(testUser1, filesAPIUser1, fileEntry, folderEntry);
		
		// Verify the file added to folder event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);

		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		filesAPIUser1.deleteFile(folderEntry);
		foldersForDeletion.remove(folderEntry);	
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_files_fileCommentMention_PublicFile()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Files</B></li>
	*<li><B>Step: testUser 1 upload a public file</B></li>
	*<li><B>Step: testUser 1 add a comment mentioning User 2</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Verify: Verify the mentions event appears in the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/50F8E81588FEE83A85257C6D00394030">TTT - Disc - File - 00101 - Mentioning a user in a public file comment</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecommentMention_PublicFile() {
		
		ui.startTest();

		// User 1 create a file and add a comment containing a mentions to it
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file8, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);

		// User 1 will now comment on the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, fileEntry, mentions);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file news story and all components are displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();		
	}
}