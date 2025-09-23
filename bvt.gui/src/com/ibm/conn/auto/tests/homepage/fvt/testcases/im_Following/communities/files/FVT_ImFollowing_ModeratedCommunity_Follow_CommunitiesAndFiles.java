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
 * Date:	15th March 2016
 */

public class FVT_ImFollowing_ModeratedCommunity_Follow_CommunitiesAndFiles extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser3;
	private BaseCommunity baseCommunity, baseCommunityFollowed;
	private BaseFile baseFile1, baseFile2;
	private Community moderatedCommunity, moderatedCommunityFollowed;
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
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a moderated community with no followers
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community with User 2 added as a follower
		baseCommunityFollowed = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunityFollowed = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityFollowed, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
	
		// User 1 will now upload their first public file
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile1 = FileEvents.addFile(baseFile1, testUser1, filesAPIUser1);
		
		// User 1 will now upload their second public file
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		publicFile2 = FileEvents.addFile(baseFile2, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void discardAllObjects() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile1);
		filesAPIUser1.deleteFile(publicFile2);
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunityFollowed);
	}
	
	/**
	*<ul>
	*<li><B>Name: followCommunity_FileCreate_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as user 1</B></li>
	*<li><B>Step: Create a new community with moderate access as user 1</B></li>
	*<li><B>Step: Have User 2 FOLLOW this community</B></li>
	*<li><B>Step: Upload a file to the community as User 1</B></li>
	*<li><B>Step: Log in to Home as user 2</B></li>
	*<li><B>Step: Go to Home / I'm Following / All </B></li>
	*<li><b>Step: Go to Home / I'm Following / Communities</b></li>
	*<li><b>Step: Go to Home / I'm Following / Files</b></li>
	*<li><B>Verify: Verify the files.file.created is displayed with the timestamp to the right of the file icon</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E67AD6DAC60880A9852578F70051763C">TTT - AS - Follow - Community - 00014 - files.file.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followCommunity_FileCreate_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(moderatedCommunityFollowed, baseFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityFollowed.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_FileUpdate_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Upload a new version of a moderate file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Homepage / I'm Following / All, Files & Communities</B></li>
	*<li><B>Verify: Verify the files.file.updated story is displayed in all views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE9B5ECFA0235344852578F70053185B">TTT - AS - Follow - Files - 00024 - files.file.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileUpdate_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a shared file for the community with User 2 as a follower
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now upload a new version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.updateFileVersion(moderatedCommunity, communityFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String fileUpdateEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the file update event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileUpdateEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followCommunity_fileShared_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Files</B></li>
	*<li><B>Step: Open one of your files that has moderate access</B></li>
	*<li><B>Step: Share this file with a moderate community</B></li>
	*<li><B>Step: Log in to Home as a different user that is following the community</B></li>	
	*<li><B>Step: Go to Home / All Updates / Communities - verification point #1</B></li>
	*<li><b>Step: Go to Home / All Updates / Files - verification point #2</b></li>
	*<li><B>Verify: Verify that files.file.shared does appear in the Communities with the timestamp to the right of the file icon</B></li>
	*<li><b>Verify: Verify that files.file.shared does appear in the Files Filter</b></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9A4F63070BD0F566852578F700556927">TTT - AS - Follow - Community - 00044 - files.file.shared - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followCommunity_fileShared_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 sharing the file with the community with Read access rights
		CommunityFileEvents.shareFileWithCommunity(moderatedCommunityFollowed, publicFile1, Role.READER, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityFollowed.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedEvent, baseFile1);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followCommunity_fileaddedEditaccess_ModerateCommunity - Moderate COMMUNITY AND GIVE USERS EDIT ACCESS()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Files</B></li>
	*<li><B>Step: Open one of your files that has public access</B></li>
	*<li><B>Step: Share this file with a moderate community as editor</B></li>
	*<li><B>Step: Log in to Home as a different user that is following the community</B></li>	
	*<li><B>Step: Go to Home / All Updates / Communities</B></li>
	*<li><B>Verify: Verify that there is a news story that is "<userName> shared a file with the community <communityName>"</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/68903E1B9993719C8525798A004E6BB3">TTT - AS - Follow - Community - 00047 - files.collection.file.added - MODERATE COMMUNITY AND GIVE USERS EDIT ACCESS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followCommunity_fileaddedEditaccess_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 sharing the file with the community with Edit access rights
		CommunityFileEvents.shareFileWithCommunity(moderatedCommunityFollowed, publicFile2, Role.OWNER, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityFollowed.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedEvent, baseFile2);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: followFiles_FileComment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a moderate file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Home / All Updates / Communities - verification point #1</B></li>
	*<li><b>Step: Go to Home / All Updates / Files - verification point #2</b></li>
	*<li><B>Verify: 1. Verify the files.file.comment.created is displayed in communities</B></li>
	*<li><B>Verify:	2. Verify the file.file..comment.created is displayed in files with the timestamp to the right of the file icon, the size of the file, the tag added and the comment under</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12AF2818B9A8A98B852578F8002AA805">TTT -AS - Follow - Files - 00054 - files.file.comment.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileComment_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now add a comment to the community file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(moderatedCommunity, communityFile, commentOnFile, testUser1, filesAPIUser1);
		
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
	*<li><B>Name: followFiles_FileLike_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a moderate community you own</B></li>
	*<li><B>Step: User 1 upload a file in the community</B></li>
	*<li><B>Step: User 2 follow the file ONLY</B></li>
	*<li><B>Step: User 1 like the file</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4B303DBD9D7BC594852578F8002B43AC">TTT - AS - Follow - Files - 00064 - files.file.recommend.created - MODERATE COMMUNITY (NEG IN MARCH)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_FileLike_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(moderatedCommunity, communityFile, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories
		String fileLikeEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
						
			// Verify that the like file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, fileLikeEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: followFiles_UpdateFileComment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a file that is in that community</B></li>
	*<li><B>Step: Update the Comment on this file</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Homepage / I'm Following / All, Files & Communities</B></li>
	*<li><B>Verify: Verify the files.file.comment.updated is NOT displayed in any view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7E0B82109CAD5AFC852579BB0064E442">TTT - AS - Follow - Files - 00094 - files.file.comment.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_UpdateFileComment_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community with User 2 as a follower
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 add and update a comment posted to the community file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(moderatedCommunity, communityFile, commentOnFile, updatedComment, testUser1, filesAPIUser1);
		
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
			
			// Verify that the update comment on file event is NOT displayed in any of the views
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
	*<li><B>Name: followFiles_fileCommentMention_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderated community that you have owner access to</B></li>
	*<li><B>Step: User 1 add a comment to the same file mentioning User 3</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Home / Updates / All & Communities/ Files </B></li>
	*<li><B>Verify: Verify the mentions event appears in all views</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5526E5413434832185257C6D00436BFE">As - follow - files - 00098 - mentioning a user in a file comment - moderated community</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void followFiles_fileCommentMention_ModerateCommunity() {
		
		ui.startTest();
		
		// User 1 create a public file for the community which is to be followed by User 2
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now add a comment with mentions to User 3 to the community file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(moderatedCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
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