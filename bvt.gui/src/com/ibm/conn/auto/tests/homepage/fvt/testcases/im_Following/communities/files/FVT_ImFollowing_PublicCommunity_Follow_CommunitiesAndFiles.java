package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities.files;

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
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/** 
 * @author 	Anthony Cox
 * Date:	10th March 2016
 */

public class FVT_ImFollowing_PublicCommunity_Follow_CommunitiesAndFiles extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser3;
	private BaseCommunity baseCommunityWithFollower, baseCommunity;
	private BaseFile baseFile1;
	private Community publicCommunityWithFollower, publicCommunity;
	private FileEntry standaloneFile1;
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
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 create the first public community with User 2 as a follower
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityWithFollower = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 1 create a public community with no followers
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a standalone file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		standaloneFile1 = FileEvents.addFile(baseFile1, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunityWithFollower);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// Delete the standalone files created during the test
		filesAPIUser1.deleteFile(standaloneFile1);
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_FileUpload_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Communities</B></li>
	*<li><B>Step: testUser 1 create a new community with public access</B></li>
	*<li><B>Step: testUser 2 FOLLOW this community</B></li>
	*<li><B>Step: testUser 1 create a file with public access</B></li>
	*<li><B>Step: testUser 2 login to Connections</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the files.file.created is displayed with the timestamp to the right of the file icon</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/93EFBF12BC3D6577852578F7005174A1">TTT - AS - Follow - Files - 00013 - files.file.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileUpload_PublicCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunityWithFollower, baseFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String shareFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_FileUpdate_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a public community that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the community</B></li>
	*<li><B>Step: testUser 1 Upload a new version of a public file that is in that community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the files.file.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/395F73E57FC9CF59852578F7005316AD">TTT -  AS - Follow - Files - 00023 - files.file.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileUpdate_PublicCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community and update the version of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addAndUpdateFileVersion(publicCommunityWithFollower, baseFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories
		String shareFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String updateFileEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the share file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, shareFileEvent, baseFile);
			
			// Verify that the update file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_fileShared_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to files</B></li>
	*<li><B>Step: testUser 1 Open one of your files that has public access</B></li>
	*<li><B>Step: testUser 1 Share this file with a public community</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user that is following the community </B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify that files.file.shared appears in the files & Community filter</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/412C7B6273C56EB9852578F700556801">TTT - AS - Follow - Files - 00043 - files.file.shared - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_fileShared_PublicCommunity() {
		
		ui.startTest();
		
		// User 1 sharing the file with the community
		CommunityFileEvents.shareFileWithCommunity(publicCommunityWithFollower, standaloneFile1, Role.OWNER, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story
		String shareFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
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
	*<li><B>Name: followFiles_FileComment_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a public community that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a comment to a public file that is in that community</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the file</B></li>	
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: 1. Verify the files.file.comment.created is displayed in all filters with the timestamp to the right of the file icon and the comment under</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FA011BE1049ED98D852578F8002AA66A">TTT - AS - Follow - Files - 00053 - files.file.comment.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileComment_publicCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(publicCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now add a comment to the community file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(publicCommunity, communityFile, commentOnFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
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
	*<li><B>Name: followFiles_FileLike_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 go to a public community you own</B></li>
	*<li><B>Step: testUser 1 upload a file in the community</B></li>
	*<li><B>Step: testUser 2 Login to Home follow the file ONLY</B></li>
	*<li><B>Step: testUser 1 Recommend a file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ED3B926577ED75BF852578F8002B42A7">TTT - AS - Follow - Files - 00063 - files.file.recommend.created - PUBLIC COMMUNITY (NEG IN MARCH)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileLike_publicCommunity(){
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(publicCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(publicCommunity, communityFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories
		String likeFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String shareFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the share file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, shareFileEvent, baseFile);
			
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}
		ui.endTest();
	}		
	
	/**
	*<ul>
	*<li><B>Name: followFiles_UpdateFileComment_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a public community that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a comment to a public file that is in that community</B></li>
	*<li><B>Step: testUser 1 Update the Comment on this file</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & People</B></li>
	*<li><B>Verify: Verify the files.file.comment.updated is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBAEFE5783CFBA4E852579BB00645D2D">TTT - AS - Follow - Files - 00093 - files.file.comment.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_UpdateFileComment_publicCommunity(){
		
		ui.startTest();
		
		// User 1 create a public file for the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunityWithFollower, baseFile, testUser1, filesAPIUser1);
		
		// User 1 add and update a comment posted to the community file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(publicCommunityWithFollower, communityFile, commentOnFile, updatedComment, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updateCommentEvent = CommunityFileNewsStories.getUpdateFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the file update event is NOT displayed in any of the view
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateCommentEvent, baseFile);
			
			// Verify that the updated comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedComment}, null, true);
			
			// Verify that the original comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_fileCommentMention_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a public community that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: testUser 1 Comment on a public file in the community as User 1 which mentions User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify the mentions event appears in all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19A69CB655FAD92785257C6D00419606">TTT - As - follow - files - 00097 - mentioning a user in a file comment - public community</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_fileCommentMention_publicCommunity(){
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(publicCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now add a comment with mentions to User 3 to the community file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(publicCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story and mentions comment text
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);
		}
		ui.endTest();
	}	
}