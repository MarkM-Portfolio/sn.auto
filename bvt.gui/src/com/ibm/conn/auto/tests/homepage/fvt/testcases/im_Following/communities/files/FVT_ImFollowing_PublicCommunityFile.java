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

public class FVT_ImFollowing_PublicCommunityFile extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithFollower;
	private Community publicCommunity, publicCommunityWithFollower;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		// User 1 will now create a public community with User 2 added as a follower
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityWithFollower = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
									   
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunityWithFollower);
	}

	/**
	* publicCommunityFileUpload() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 upload a public file to the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file created event does appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/93EFBF12BC3D6577852578F7005174A1">TTT - AS - Follow - Files - 00013 - files.file.created - PUBLIC COMMUNITY</a></li>
	*</ul> 	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void publicCommunityFileUpload(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunityWithFollower, baseFile, testUser1, filesAPIUser1);
		
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
	* publicCommunityFileUpdate
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 upload a public file to the community</B></li>
	*<li><B>Step: testUser1 upload a new version of the file to the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file updated event does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/395F73E57FC9CF59852578F7005316AD">TTT - AS - Follow - Files - 00023 - files.file.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul> 	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void publicCommunityFileUpdate(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community and will then upload a new version of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addAndUpdateFileVersion(publicCommunityWithFollower, baseFile, baseFileNewVersion, testUser1, filesAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String uploadFileEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String updateFileEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the file shared with community event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, uploadFileEvent, baseFile);
			
			// Verify that the update file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, updateFileEvent, baseFile);
		}
		ui.endTest();
	}
	
	/**
	* publicCommunityFileComment
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities as User 1</B></li>
	*<li><B>Step: Open a public community that you have owner access to</B></li>
	*<li><B>Step: Add a comment to a public file that is in that community</B></li>
	*<li><B>Step: Log in to Home as User 2 who is following the file</B></li>
	*<li><B>Step: Go to Home / All Updates / All & Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / All Updates / Files - verification point #1</B></li>
	*<li><B>Verify: Verify the files.file.comment.created is displayed in all filters with the timestamp to the right of the file icon and the comment under</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FA011BE1049ED98D852578F8002AA66A">TTT - AS - Follow - Files - 00053 - files.file.comment.created - PUBLIC COMMUNITY</a></li>
	*</ul> 	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void publicCommunityFileComment(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(publicCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now post a comment to the community file
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(publicCommunity, communityFile, user1Comment, testUser1, filesAPIUser1);
		
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
	* publicCommunityFileCommentUpdate
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 upload a public file to the community</B></li>
	*<li><B>Step: testUser1 add a comment to the file in the community</B></li>
	*<li><B>Step: testUser1 update the comment</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the file commented event does appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBAEFE5783CFBA4E852579BB00645D2D">TTT - AS - Follow - Files - 00093 - files.file.comment.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul> 	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void publicCommunityFileCommentUpdate(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file3, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunityWithFollower, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now post a comment to the community file and will update the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addCommentAndUpdateComment(publicCommunityWithFollower, communityFile, user1Comment, user1EditedComment, testUser1, filesAPIUser1);
		
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
	* publicCommunityFileLike
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 1 upload a file in the community</B></li>
	*<li><B>Step: User 2 follow the file ONLY</B></li>
	*<li><B>Step: User 1 like the file</B></li>
	*<li><b>Step: User 2 go to Homepage / Updates / I'm Following / All, Files</B></li>
	*<li><B>Verify: Verify the files.file.recommend.created is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ED3B926577ED75BF852578F8002B42A7">AS - Follow - Files - 00063 - files.file.recommend.created - PUBLIC COMMUNITY (NEG IN MARCH)</a></li>
	*</ul> 	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void publicCommunityFileLike(){
		
		ui.startTest();
		
		// User 1 will now upload a public file to the community with User 2 added as a follower of the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFileWithOneFollower(publicCommunity, baseFile, testUser1, filesAPIUser1, testUser2, filesAPIUser2);
		
		// User 1 will now like / recommend the file
		CommunityFileEvents.likeFile(publicCommunity, communityFile, testUser1, filesAPIUser1);
		
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