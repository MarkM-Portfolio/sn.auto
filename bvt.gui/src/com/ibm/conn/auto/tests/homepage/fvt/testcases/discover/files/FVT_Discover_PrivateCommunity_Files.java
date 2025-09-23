package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016	                                     	 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * This is a functional test for the Homepage Activity Stream (Discover/files) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 2/2016
 */

public class FVT_Discover_PrivateCommunity_Files extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;	
	private APIProfilesHandler profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private BaseFile baseFile1, baseFile2;
	private boolean deletedPublicFile;
	private Community restrictedCommunity;
	private FileEntry publicFile1, publicFile2;
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
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a restricted community for use in the tests - User 3 will be added as a member to this community in order for mentions to work correctly
		baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);	
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser3);
		
		// User 1 will now create their first public file for use in the tests
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFile(baseFile1, testUser1, filesAPIUser1);
		
		// User 1 will now create their second public file for use in the tests
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFile(baseFile2, testUser1, filesAPIUser1);
		deletedPublicFile = false;
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete all of the files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		
		if(deletedPublicFile == false) {
			filesAPIUser1.deleteFile(publicFile2);
		}
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileUpload_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Upload a file to the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.created is not seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/91C2E527EA5CAD6E852578760079E7BA">TTT -  Disc - Files - 00014 - files.file.created - private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_fileupload_PrivateCommunity() {

		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);		
		CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile);
		}
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileUpdated_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Upload a new version of a file to the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.updated is not seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E6EE9831911F4DA852578760079E7BF">TTT -  Disc - Files - 00024 - files.file.updated - private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_fileupdated_PrivateCommunity() {

		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);	
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);		
		CommunityFileEvents.addAndUpdateFileVersion(restrictedCommunity, baseFile, baseFileNewVersion, testUser1, filesAPIUser1);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileUpdatedEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());		
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file edited event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, fileUpdatedEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileSharedwith_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Public access</B></li>
	*<li><B>Step: testUser 1 Share this file with a Private community</B></li>		
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.shared is not seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AB29E4BB9148E939852578760079E7C9">TTT - Disc - Files - 00044 - files.file.shared - public file shared with private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filesharedwithCommunity_PrivateCommunity() {
	
		ui.startTest();
		
		// Share a public file with the community
		CommunityFileEvents.shareFileWithCommunity(restrictedCommunity, publicFile1, Role.READER, testUser1, filesAPIUser1);		

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile1);
		}
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_Filecomment_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>	
	*<li><B>Step: testUser 1 Add a comment to a file in the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.comment.created is not seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0344DF2D85C39065852578760079E7CE">TTT - Disc - Files - 00054 - files.file.comment.created - private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecomment_PrivateCommunity() {

		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);

		// Comment on the file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(restrictedCommunity, publicFile, commentOnFile, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FilecommentUpdate_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>	
	*<li><B>Step: testUser 1 Update an existing comment to a file in the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.comment.updated is not seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/718F399E999CFEFA852579BC005B2A11">TTT -Disc - Files - 00059 - files.file.comment.updated - private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecommentUpdate_PrivateCommunity() {
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);

		// Comment on the file and then update the comment
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		String updatedCommentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(restrictedCommunity, publicFile, commentOnFile, updatedCommentOnFile, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updatedFileEvent = CommunityFileNewsStories.getUpdateFileNewsStory(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updatedFileEvent, baseFile);
			
			// Verify that the original comment and updated comment posted by User 1 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile, updatedCommentOnFile}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: disc_files_FileLike_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a recommendation to a file in the community</B></li>		
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.recommend.created is not seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2BF804A76A8DFB1E852578760079E7D3">TTT - Disc - Files - 00064 - files.file.recommend.created - private community (neg)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_FileLike_PrivateCommunity() {
			
		ui.startTest();

		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);	
		FileEntry publicFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);
		
		// Like / recommend the file
		CommunityFileEvents.likeFile(restrictedCommunity, publicFile, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified	
		String likeFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		ui.endTest();	
	}		
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_ Remove file shared with Folder_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Ensure there is a file shared with a folder that has given users edit access</B></li>	
	*<li><B>Step: testUser 1 log into the Private communities</B></li>
	*<li><B>Step: testUser 1 remove the file from the folder</B></li>			
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify that the news story of the file shared with the folder and given edit access is NOT seen in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E1F2CA44F40BD6FA8525798A00507BBE">TTT - Disc - files - 00097 - Remove file shared with folder that had given members edit access</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_RemoveFilesharedwithfolder_PrivateCommunity() {
				
		ui.startTest();
		
		// Create a folder in the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry communityFolder = CommunityFileEvents.addFolder(restrictedCommunity, baseFolder, testUser1, filesAPIUser1);
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);	
		
		// Add the file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, publicFile, communityFolder);
		
		// Remove the file from the community folder
		CommunityFileEvents.removeFileFromFolder(testUser1, filesAPIUser1, publicFile, communityFolder);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Outline event to be verified	
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());	
				
		// Verify that the event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);

		ui.endTest();			
	}	
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_ Remove file shared with community_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Ensure there is a file shared with a Private community</B></li>	
	*<li><B>Step: testUser 1 log into the Private communities</B></li>
	*<li><B>Step: testUser 1 remove the file from the community</B></li>			
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify:Verify that the news story of the file shared with the community is not seen in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C9B8F0FB6CC56C698525798A00507A9C">TTT - Disc - files - 00096 - Remove file shared with priavte community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_RemoveFileSharedWithCommunity_PrivateCommunity() {
				
		ui.startTest();
	
		// Share the file with the community
		CommunityFileEvents.shareFileWithCommunity(restrictedCommunity, publicFile2, Role.OWNER, testUser1, filesAPIUser1);
		
		// Delete the file to remove it from the community
		deletedPublicFile = filesAPIUser1.deleteFile(publicFile2);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile2);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: Discover_files_fileCommentMention_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Add a comment to a file in the community mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the mentions event DOES NOT appear in all the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9DF3AC5B6D748D6D85257C6D003E0FF3">TTT - Disc - Files- 00105 - Mentioning a user in a public file comment - private community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecommentMention_PrivateCommunity() {
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file8, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);		

		// Add comment with mentions to the community file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(restrictedCommunity, publicFile, mentions, testUser1, filesAPIUser1);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified			
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment with mentions is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, false);
		}
		ui.endTest();			
	}
}