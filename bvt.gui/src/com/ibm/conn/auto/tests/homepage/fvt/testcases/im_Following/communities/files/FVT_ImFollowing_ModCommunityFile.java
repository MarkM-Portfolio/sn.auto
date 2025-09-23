package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities.files;

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

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                	 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_ImFollowing_ModCommunityFile extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithFollower;
	private Community moderatedCommunity, moderatedCommunityWithFollower;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);	
		
		// User 1 will now create a moderated community with User 2 added as a follower
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunityWithFollower = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
									   
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunityWithFollower);
	}

	/**
	* moderatedCommunityFileUpload() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 upload a public file to the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file created event does appear in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E67AD6DAC60880A9852578F70051763C">TTT - AS - Follow - Files - 00014 - files.file.created - MODERATE COMMUNITY</a></li>
	*</ul>
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void moderatedCommunityFileUpload() {
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(moderatedCommunityWithFollower, baseFile, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String uploadFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, uploadFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	* moderatedCommunityFileUpdate
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Upload a new version of a moderate file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Homepage / I'm Following / All, Files & Communities</B></li>
	*<li><B>Verify: Verify the files.file.updated story is displayed in all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE9B5ECFA0235344852578F70053185B">TTT - AS - Follow - Files - 00024 - files.file.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul> 	
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void moderatedCommunityFileUpdate() {
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now update the version of the file
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.updateFileVersion(moderatedCommunity, communityFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
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
	* moderatedCommunityFileComment
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a moderate file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Home / All Updates / Communities - verification point #1</B></li>
	*<li><b>Step: Go to Home / All Updates / Files - verification point #2</b></li>
	*<li><B>Verify: Verify the files.file.comment.created is displayed in communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12AF2818B9A8A98B852578F8002AA805">TTT - AS - Follow - Files - 00054 - files.file.comment.created - MODERATE COMMUNITY</a></li>
	*</ul> 	
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void moderatedCommunityFileComment(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now post a comment to the community file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(moderatedCommunity, communityFile, user1Comment, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* moderatedCommunityFileCommentUpdate
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a moderate community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a file that is in that community</B></li>
	*<li><B>Step: Update the Comment on this file</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Homepage / I'm Following / All, Files & Communities</B></li>
	*<li><B>Verify: Verify the files.file.comment.updated is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7E0B82109CAD5AFC852579BB0064E442">TTT - AS - Follow - Files - 00094 - files.file.comment.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul> 	
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void moderatedCommunityFileCommentUpdate(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file5, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now post a comment to the community file and will update the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(moderatedCommunity, communityFile, user1Comment, user1EditedComment, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String updateFileEvent = CommunityFileNewsStories.getUpdateFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the update file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateFileEvent, baseFile);
						
			// Verify that the updated comment posted by User 1 is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1EditedComment}, null, true);
			
			// Verify that the original comment posted by User 1 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* moderatedCommunityFileLike
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a moderate community you own</B></li>
	*<li><B>Step: User 1 upload a file in the community</B></li>
	*<li><B>Step: User 2 follow the file ONLY</B></li>
	*<li><B>Step: User 1 like the file</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4B303DBD9D7BC594852578F8002B43AC">AS - Follow - Files - 00064 - files.file.recommend.created - MODERATE COMMUNITY (NEG IN MARCH)</a></li>
	*</ul> 	
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void moderatedCommunityFileLike(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file6, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(moderatedCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(moderatedCommunity, communityFile, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String likeFileEvent = CommunityFileNewsStories.getLikeTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
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
}