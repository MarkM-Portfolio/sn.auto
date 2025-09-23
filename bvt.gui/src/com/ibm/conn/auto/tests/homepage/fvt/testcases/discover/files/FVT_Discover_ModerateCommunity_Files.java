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

public class FVT_Discover_ModerateCommunity_Files extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;	
	private APIProfilesHandler profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private BaseFile baseFile1, baseFile2;
	private boolean deletedPublicFile;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community for use in the tests
		baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);	
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
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
		
		// Remove all of the public standalone files created during the test
		filesAPIUser1.deleteFile(publicFile1);
		
		if(deletedPublicFile == false) {
			filesAPIUser1.deleteFile(publicFile2);
		}
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileUpload_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Upload a file to the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.created is seen with the timestamp to the right of the file icon and the size of the file</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/296A62517F06E436852578760079E7BD">TTT - Disc - Files - 00013 - files.file.created - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_fileupload_ModerateCommunity() {		
			
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);		
		CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileUpdated_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Upload a new version of a file to the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.updated is seen with the timestamp to the right of the file icon and the size of the file</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8342DE7248F6D1FD852578760079E7C1">TTT - Disc - Files - 00023 - files.file.updated - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_fileupdated_ModerateCommunity() {
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);	
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addAndUpdateFileVersion(moderatedCommunity, baseFile, baseFileNewVersion, testUser1, filesAPIUser1);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileUpdatedEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());		
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file edited event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileUpdatedEvent, baseFile);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FileSharedwith_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has Moderate access</B></li>
	*<li><B>Step: testUser 1 Share this file with a Moderate community</B></li>		
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.shared is seen with the timestamp to the right of the file icon, the size of the file and the tag added</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D8C012FA4F0E77D1852578760079E7CA">TTT - Disc - Files - 00043 - files.file.shared - public file shared with moderated community</a></li>
	* @author Srinivas Vechha
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filesharedwith_ModerateCommunity() {
	
		ui.startTest();
		
		// Share a public file with the community
		CommunityFileEvents.shareFileWithCommunity(moderatedCommunity, publicFile1, Role.READER, testUser1, filesAPIUser1);		

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile1);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_Filecomment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>	
	*<li><B>Step: testUser 1 Add a comment to a file in the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.comment.created is seen with the timestamp to the right of the file icon, the size of the file, the tag added and the comment under</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/79021F4083FF33A4852578760079E7CF">TTT - Disc - Files - 00053 - files.file.comment.created - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecomment_ModerateCommunity() {
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);

		// Comment on the file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(moderatedCommunity, publicFile, commentOnFile, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, true);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_FilecommentUpdate_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>	
	*<li><B>Step: testUser 1 Update an existing comment to a file in the community</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.comment.created is seen with the timestamp to the right of the file icon, the size of the file, the tag added and the comment</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F6248D6F8CAA3A77852579BC005AFD18">TTT -Disc - Files - 00058 - files.file.comment.updated - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecommentUpdate_ModerateCommunity() {
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);

		// Comment on the file and then update the comment
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		String updatedCommentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(moderatedCommunity, publicFile, commentOnFile, updatedCommentOnFile, testUser1, filesAPIUser1);
		
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
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updatedFileEvent, baseFile);
			
			// Verify that the updated comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedCommentOnFile}, null, true);
			
			// Verify that the original comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: disc_files_FileLike_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a recommendation to a file in the community</B></li>		
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story for files.file.recommend.created is seen with the timestamp to the right of the file icon, the size of the file, number of likes, the tag added and the comment under</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B2900218D91CB6AD852578760079E7D4">TTT - Disc - Files - 00063 - files.file.recommend.created - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_FileLike_ModerateCommunity() {
			
		ui.startTest();

		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);	
		FileEntry publicFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);
		
		// Like / recommend the file
		CommunityFileEvents.likeFile(moderatedCommunity, publicFile, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified	
		String likeFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the like file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		ui.endTest();	
	}	
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_ Remove file shared with community Folder_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Ensure there is a file shared with a Moderate community</B></li>	
	*<li><B>Step: testUser 1 log into the Moderate communities</B></li>
	*<li><B>Step: testUser 1 remove the file from the Community Folder</B></li>			
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story of the file shared with the Folder is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1298EBA6E40C524D8525798A00507968">TTT - Disc - files - 00095 - Remove file shared with Moderated Community folder that had given members edit access</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_RemoveFilesharedwithfolder_ModerateCommunity(){
				
		ui.startTest();
	
		// Create a folder in the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry communityFolder = CommunityFileEvents.addFolder(moderatedCommunity, baseFolder, testUser1, filesAPIUser1);
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);	
		
		// Add the file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, publicFile, communityFolder);

		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());		
		
		// Verify that the event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		// Remove the file from the community folder
		CommunityFileEvents.removeFileFromFolder(testUser1, filesAPIUser1, publicFile, communityFolder);
		
		// Verify that the event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);

		ui.endTest();		
	}	
	
	/**
	*<ul>
	*<li><B>Name: Discover_Files_ Remove file shared with Community_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Ensure there is a file shared with a moderate community</B></li>	
	*<li><B>Step: testUser 1 log into the Moderate communities</B></li>
	*<li><B>Step: testUser 1 remove the file from the Community</B></li>			
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the news story of the file shared with the community is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/30186AF1D700AB418525798A0050780B">TTT - Disc - files - 00094 - Remove file shared with moderate community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_RemoveFileSharedwithCommunity_ModerateCommunity(){
		
		ui.startTest();
	
		// Share the file with the community
		CommunityFileEvents.shareFileWithCommunity(moderatedCommunity, publicFile2, Role.OWNER, testUser1, filesAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Outline event to be verified	
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click on the 'Show More' link to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile2);
		}
		// Delete the file to remove it from the community
		deletedPublicFile = filesAPIUser1.deleteFile(publicFile2);

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
	*<li><B>Name: Discover_files_fileCommentMention_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the files widget within this community if not already present</B></li>
	*<li><B>Step: testUser 1 Add a comment to a file in the community mentioning User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the mentions event appears in all the views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB13223B91DD0BEA85257C6D003D350B">TTT - Disc - File - 00104 - Mentioning a user in a file comment - moderated community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void disc_files_filecommentMention_ModerateCommunity(){
		
		ui.startTest();
		
		// Creating the BaseFile with all setup steps completed.
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file8, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);		

		// Add comment with mentions to the community file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(moderatedCommunity, publicFile, mentions, testUser1, filesAPIUser1);

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
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment with mentions is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);
		}
		ui.endTest();		
	}
}	