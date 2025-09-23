package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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
 * This is a functional test for the Homepage Activity Stream (I'm Following/Community) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 01/2016
 */

public class FVT_ImFollowing_CommunityFolder_PublicCommunity extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1;
	private BaseCommunity baseCommunity;
	private BaseFile baseFile1, baseFile2;
	private Community publicCommunity;
	private FileEntry communityFile1, communityFile2;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);			
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);	
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
				
		// User 1 will now create a public community with User 2 added as a follower
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollower(baseCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 1 will now share the first file with the community (this will be added to a community folder during a test)
		baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		communityFile1 = CommunityFileEvents.addFile(publicCommunity, baseFile1, testUser1, filesAPIUser1);
		
		// User 1 will now share the second file with the community (this will be added to a community folder during a test)
		baseFile2 = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		communityFile2 = CommunityFileEvents.addFile(publicCommunity, baseFile2, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community now that the test has completed
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_follow Community_Community Folder Created_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 follow the public community</B></li>
	*<li><B>Step: testUser 1 create a folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify the community folder created event Not appears in the AS</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7616A6A1ACC72D1185257BD40038D2E8">TTT - AS - Follow Community - 00240 - Community Folder Created - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void foldercreation_PublicCommunity(){
		
		ui.startTest();
		
		// User 1 will now add a folder to the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String folderAddedToCommunityEvent = CommunityFileNewsStories.getCreateFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
		
		// Verify that the folder added to community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{folderAddedToCommunityEvent}, TEST_FILTERS, false);
		
		ui.endTest();		
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_communityfollow_Community Folder File Added Created_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 who is follow the Public community</B></li>
	*<li><B>Step: testUser 1 add a file to the folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities</B></li>
	*<li><B>Verify: Verify the community folder file added event appears in the AS</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/07DA0A4794E684D585257BD40038D2EB">TTT - AS - Follow Community - 00250 - Community Folder File Added - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileaddtofolder_PublicCommunity(){
		
ui.startTest();
		
		// User 1 will now add a folder to the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry communityFolder = CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
				
		// User 1 will now add a file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, communityFile1, communityFolder);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
				
		// Verify that the file added to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		ui.endTest();		
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_FollowCommunity_Community Folder File Removed Created_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 who is follow the public community</B></li>
	*<li><B>Step: testUser 1 remove a file to the folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities</B></li>
	*<li><B>Verify: Verify the community folder file added event has been removed from the stream</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/44D469CFABAA6C7785257BD30048D779">TTT -AS - Follow Community - 00255 - Community Folder File Removed - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void communityfolderfileremoved_PublicCommunity(){
			
		ui.startTest();
		
		// User 1 will now add a folder to the community
		BaseFile baseFolder = FileBaseBuilder.buildBaseFile(Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry communityFolder = CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
				
		// User 1 will now add a file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, communityFile2, communityFolder);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
				
		// Verify that the file added to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		// User 1 will now remove the file from the community folder
		CommunityFileEvents.removeFileFromFolder(testUser1, filesAPIUser1, communityFile2, communityFolder);
		
		// Verify that the file added to folder event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);
		
		ui.endTest();	
	}	
}