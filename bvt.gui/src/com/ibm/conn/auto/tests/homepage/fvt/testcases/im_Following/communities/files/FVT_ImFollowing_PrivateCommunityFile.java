package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;

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

public class FVT_ImFollowing_PrivateCommunityFile extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		
		// User 1 will now create a restricted community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}
									   
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* privateCommunityFileUpload() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as user 1</B></li>
	*<li><B>Step: Create a new community with private access as user 1</B></li>
	*<li><B>Step: Create a file with private access as User 1</B></li>
	*<li><b>Step: Share the file with the community</b></li>
	*<li><b>Step: Have user 2 FOLLOW this file</b></li>
	*<li><b>Step: Log in to Home as user 2</b></li>
	*<li><b>Step: Go to Home / I'm Following / All</b></li>
	*<li><b>Step: Go to Home / I'm Following / Communities</b></li>
	*<li><b>Step: Go to Home / I'm Following / Files</b></li>
	*<li><B>Verify: Verify the files.file.shared is NOT displayed in communities</B></li>
	*<li><b>No TTT for this test - it is a negative test for the community files test found at: Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5943B87A0B77AC6C852578F70051775D</b></li>	
	* @author Patrick Doherty
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunityFileUpload() {
		
		ui.startTest();
		
		// User 1 will now upload a private file to the community and have User 2 follow the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
		CommunityFileEvents.addFileWithOneFollower(restrictedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String sharedFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the shared file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, sharedFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	* privateCommunityFileUpdate
	* <ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a private community that you have owner access to</B></li>
	*<li><B>Step: Upload a new version of a private file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Homepage / I'm Following / All, Files & Communities</B></li>
	*<li><B>Verify: Verify the files.file.updated story is displayed in all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/00A51C40C81EF8FF852578F70053199B">TTT - AS - Follow - Files - 00025 - files.file.updated - PRIVATE COMMUNITY (NEG SC NOV)</a></li>	
	* @author Patrick Doherty
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunityFileUpdate(){
		
		ui.startTest();
		
		// User 1 will now upload a private file to the community with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateCommunityFile = CommunityFileEvents.addFileWithOneFollower(restrictedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now update the file version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.NO_ONE);
		CommunityFileEvents.updateFileVersion(restrictedCommunity, privateCommunityFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String updateFileEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the update file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, updateFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	* privateCommunityFileComment
	* <ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a private community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a private file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Home / All Updates / Communities</B></li>
	*<li><b>Step: Go to Home / All Updates / Files</b></li>
	*<li><B>Verify: Verify the file.file.comment.created is displayed in files with the timestamp to the right of the file icon, the size of the file, the tag added and the comment under</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E4825726DD76783E852578F8002AA934">TTT - AS - Follow - Files - 00055 - files.file.comment.created - PRIVATE COMMUNITY</a></li> 	
	* @author Patrick Doherty
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunityFileComment(){
		
		ui.startTest();
		
		// User 1 will now upload a private file to the community with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateCommunityFile = CommunityFileEvents.addFileWithOneFollower(restrictedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now post a comment to the file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(restrictedCommunity, privateCommunityFile, user1Comment, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String commentedFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentedFileEvent, baseFile);
			
			// Verify that the comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* privateCommunityFileCommentUpdate
	* <ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community and add testUser2 as a member</B></li>
	*<li><B>Step: testUser1 upload a private file to the community</B></li>
	*<li><b>Step: testUser2 follow this file</b></ui>
	*<li><B>Step: testUser1 add a comment to the file in the community</B></li>
	*<li><B>Step: testUser1 update the comment</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file comment updated event does NOT appear in the views</B></li>	
	* @author Patrick Doherty
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunityFileCommentUpdate(){
		
		ui.startTest();
		
		// User 1 will now upload a private file to the community with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateCommunityFile = CommunityFileEvents.addFileWithOneFollower(restrictedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now post a comment to the file and then update that comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedUser1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(restrictedCommunity, privateCommunityFile, user1Comment, updatedUser1Comment, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String commentedFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updatedFileEvent = CommunityFileNewsStories.getUpdateFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentedFileEvent, baseFile);
			
			// Verify that the update comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updatedFileEvent, baseFile);
			
			// Verify that the updated comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedUser1Comment}, null, true);
			
			// Verify that the original comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* privateCommunityFileLike
	* <ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community and add testUser2 as a member</B></li>
	*<li><B>Step: testUser1 upload a private file to the community</B></li>
	*<li><b>Step: testUser2 follow this file</b></ui>
	*<li><B>Step: testUser1 like the file in the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file recommended event does NOT appear in any of the views</B></li>	
	* @author Patrick Doherty
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunityFileLike(){
		
		ui.startTest();
		
		// User 1 will now upload a private file to the community with User 2 following the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.NO_ONE);
		FileEntry privateCommunityFile = CommunityFileEvents.addFileWithOneFollower(restrictedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(restrictedCommunity, privateCommunityFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String likedFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likedFileEvent, baseFile);
		}
		ui.endTest();
	}
}