package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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
* This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
* Created By: Srinivas Vechha.
* Date: 08/2015
*/

public class FVT_ImFollowing_Person_PublicCommunity_Files extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
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
			
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
			
		// Add the commonly used filters to the TEST_FILTERS array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterFiles;
		TEST_FILTERS[2] = HomepageUIConstants.FilterCommunities;
		
		// User 2 will now follow User 1 through API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
		
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// User 2 will unfollow User 1 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}

	/**
	*<ul>
	*<li><B>Name: test_Person_FileUpload_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 Upload a file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.created story is displayed within the Communities, People and Files view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6DC9F1A38485CC13852578FB005341EB">TTT - AS - FOLLOW - PERSON - FILES - 00203 - files.file.created - PUBLIC COMMUNITY</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_FileUpload_PublicCommunity(){
		
		ui.startTest();
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, fileSharedWithCommEvent, baseFile);
		}
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_FileUpdate_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 Update a file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ED0BD79F2944823A852578FB005325E3">TTT -  AS - FOLLOW - PERSON - FILES - 00213 - files.file.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_FileUpdate_PublicCommunity(){
		
		ui.startTest();

		// User 1 will now add a file to the community and will then update the file version of the community file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);	
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addAndUpdateFileVersion(publicCommunity, baseFile, baseFileNewVersion, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String fileSharedWithCommEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());	
		String fileUpdatedEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser3.getDisplayName());		

		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views while the file edited event is NOT displayed in any of the views
			HomepageValid.verifyFileNewsStoriesAsPresentAndAbsentInAS(ui, driver, fileSharedWithCommEvent, fileUpdatedEvent, baseFile);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_FileComment_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 Comment on a public file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.created is displayed within the Communities, People and Files view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BDDE612FCAD25626852578FC002B42E9">TTT - AS - FOLLOW - PERSON - FILES - 00243 - files.file.comment.created - PUBLIC COMMUNITY</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_FileComment_publicCommunity(){
		
		ui.startTest();

		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the community file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(publicCommunity, publicFile, commentOnFile, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified	
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file news story is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment posted to the file is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, true);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateFileComment_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a public file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.updated is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F3F606D53E6D8F4852579BF00480FDE">TTT - AS - FOLLOW - PERSON - FILES - 00248 - files.file.comment.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_UpdateFileComment_publicCommunity(){
		
		ui.startTest();
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);		
		FileEntry publicFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);

		// User 1 will now comment on the file and then update the comment
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		String updatedCommentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(publicCommunity, publicFile, commentOnFile, updatedCommentOnFile, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news stories to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updatedFileEvent = CommunityFileNewsStories.getUpdateFileNewsStory(ui, testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file comment event is displayed in all views while the update comment event is NOT displayed in any of the views
			HomepageValid.verifyFileNewsStoriesAsPresentAndAbsentInAS(ui, driver, commentOnFileEvent, updatedFileEvent, baseFile);
			
			// Verify that the updated comment is displayed in all of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updatedCommentOnFile}, filter, true);
			
			// Verify that the original comment is NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFile}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_FileLike_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Recommend a file in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.recommend.created story is displayed within the Communities, People and Files view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0453AE01A8D97AC0852578FC002CEDF4">TTT - AS - FOLLOW - PERSON - FILES - 00253 - files.file.recommend.created - PUBLIC COMMUNITY</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_FileLike_publicCommunity(){
		
		ui.startTest();

		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);	
		FileEntry publicFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(publicCommunity, publicFile, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified	
		String likeFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the news story is displayed in all of the views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, likeFileEvent, baseFile);
		}	
		ui.endTest();	
	}		
	
	/**
	*<ul>
	*<li><B>Name: test_Person_fileCommentMention_publicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1, log into Home</B></li>
	*<li><B>Step: testUser 1 Comment on a public file in the community as User 1 which mentions User 3</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities, People & Files</B></li>
	*<li><B>Verify: Verify that the files.file.comment.mentioned is displayed within the Communities, People and Files view</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/72E317CF0CCABD4285257C6D0046CB43">TTT - AS - FOLLOW - PERSON - FILES - 00265 - files.file.comment.mentions - PUBLIC COMMUNITY</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Person_fileCommentMention_publicCommunity(){
		
		ui.startTest();
		
		// User 1 will now upload a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file7, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);		

		// User 1 will now add comment with mentions to User 3 to the community file
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(publicCommunity, publicFile, mentions, testUser1, filesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified			
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