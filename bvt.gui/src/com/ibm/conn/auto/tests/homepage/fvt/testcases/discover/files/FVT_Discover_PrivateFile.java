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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
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
/* Copyright IBM Corp. 2017	                                     	 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_PrivateFile extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1;
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
	}	
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the files and folders created during the test
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
	 * <li><B>Name:</B> privateFileUpload()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 upload a new file with private access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.created is not seen - negative test</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E2144E38D6E2AEAF852578760079E7BB">TTT - DISC - FILES - 00011 - FILES.FILE.CREATED - PRIVATE FILE (NEG)</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFileUpload() {

		ui.startTest();
		
		// User 1 create a file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
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
			
			// Verify that the upload file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, uploadFileEvent, baseFile);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> privateFileLike()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 add a recommendation to a file with private access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.recommend.created is not seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/10BFC307C66E6E83852578760079E7D7">TTT - DISC - FILES - 00061 - FILES.FILE.RECOMMEND.CREATED - PRIVATE FILE (NEG)</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFileLike() {

		ui.startTest();
		
		// User 1 create a file and like / recommend the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.NO_ONE);
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
			
			// Verify the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> privateFilePin()()</li>
	 * <li><B>How:</B> Use the API to create a private file owned by testUser1, pin the file </li>
	 * <li><B>Purpose:</B> Test that Story shows correctly in the Discover view of testUser2</li>
	 * <li><B>Verify:</B> Verify that the file story does NOT appear in testUser2's Discover/All and Discover/Files views</li>
	 * <li>There is no TTT link for this test case</li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFilePin() {

		ui.startTest();
		
		// User 1 create a file and like it
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.NO_ONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will now pin the file
		FileEvents.pinFile(testUser1, filesAPIUser1, fileEntry);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String pinFileEvent = FileNewsStories.getPinnedAFileNewsStory_User(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify the pin file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, pinFileEvent, baseFile);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> privateFileComment()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 add a comment to a file with private access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.comment.created is not seen - negative test</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E41B115BF1931855852578760079E7D2">TTT - DISC - FILES - 00051 - FILES.FILE.COMMENT.CREATED - PRIVATE FILE (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFileComment() {

		ui.startTest();

		// User 1 create a file and comment on it
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.NO_ONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will now add a comment to the file
		String fileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileComment(testUser2, filesAPIUser1, fileEntry, fileComment);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileComment}, null, false);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> privateFileCommentEdit()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 update an existing comment to a file that has private access</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.file.comment.updated is not seen - negative test</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AFF71003FC9BD92C852579BC005A9180">TTT - DISC - FILES - 00056 - FILES.FILE.COMMENT.UPDATED - PRIVATE FILE (NEG)</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFileCommentEdit() {
		
		ui.startTest();
		
		// User 1 create a file, comment on it and edit the comment
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.NO_ONE);
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
			
			// Verify the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updatedCommentEvent, baseFile);
						
			// Verify that the original comment and updated comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileComment, updatedFileComment}, null, false);
		}
		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> privateFolderCreate()</li>
	 * <li><B>How:</B> Use the API to create a private folder owned by testUser1 </li>
	 * <li><B>Purpose:</B> Test that Story shows correctly in the Discover view of testUser2</li>
	 * <li><B>Verify:</B> Verify that the file story is NOT displayed in testUser2's Discover/All and Discover/Files views</li>
	 * <li>There is no TTT link for this test case</li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFolderCreate() {

		String testName = ui.startTest();		
		
		// User 1 create a folder
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), "", ShareLevel.NO_ONE);	
		FileEntry folderEntry = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		foldersForDeletion.put(folderEntry, filesAPIUser1);	

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String folderAddedEvent = FileNewsStories.getCreateFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{folderAddedEvent}, TEST_FILTERS, false);

		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(folderEntry);
		foldersForDeletion.remove(folderEntry);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> privateFolderFileAdded()</li>
	 * <li><B>Step: User 1 log into Files</B></li>
	 * <li><B>Step: User 1 create a folder that is shared with individual users</B></li>
	 * <li><B>Step: User 1 add a file to the folder</B></li>
	 * <li><B>Step: User 2 go to Home \ Updates \ Discover \ All & Files</B></li>
	 * <li><B>Verify: Verify that the news story for files.collection.file.added is not seen - negative test</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8837DC0E02813370852578760079E7D9">TTT - DISC - FILES - 00071 - FILES.COLLECTION.FILE.ADDED - PRIVATE SHARED FOLDER (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateFolderFileAdded() {

		String testName = ui.startTest();
		
		// User 1 create a folder
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), "", ShareLevel.NO_ONE);		
		FileEntry folderEntry = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
		foldersForDeletion.put(folderEntry, filesAPIUser1);
		
		// User 1 create a public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.NO_ONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);

		// User 1 will now add the file to the folder
		FileEvents.addFileToFolder(testUser1, filesAPIUser1, fileEntry, folderEntry);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileAddedToFolderEvent = FileNewsStories.getAddFileToFolderNewsStory(ui, folderEntry.getTitle(), testUser1.getDisplayName());

		// Verify the file added to folder event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);

		// Perform clean up now that the test has completed
		filesAPIUser1.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		filesAPIUser1.deleteFile(folderEntry);
		foldersForDeletion.remove(folderEntry);	
		ui.endTest();
	}
}