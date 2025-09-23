package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.files.folder;

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
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
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
/* Copyright IBM Corp. 2016 	                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following/people) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 02/2016
 */

public class FVT_ImFollowing_Person_PublicCommunity_Folder extends SetUpMethodsFVT {
	
private String[] TEST_FILTERS;
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private BaseFile baseFolder;
	private Community publicCommunity;
	private FileEntry communityFolder;
	private User testUser1, testUser2;	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);			
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
					
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		
		// Add the filters to the filters array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterFiles;
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a folder in the community
		baseFolder = FileBaseBuilder.buildBaseFile(getClass().getSimpleName() + Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		communityFolder = CommunityFileEvents.addFolder(publicCommunity, baseFolder, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// User 2 will now unfollow User 1 now that the tests have completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: foldercreation_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 create a folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & People</B></li>
	*<li><B>Verify: Verify the community folder created event does NOT appear in the AS - This event has been removed</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7A7E8CDCC9C66F8B85257BD4003C2ACE">TTT - AS - Follow - Person - Community - 00240 - Community Folder Created - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void foldercreation_PublicCommunity() {
		
		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createFolderEvent = CommunityFileNewsStories.getCreateFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
		
		// Verify that the create folder event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createFolderEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: fileaddtofolder_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 add a file to the folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & People</B></li>
	*<li><B>Verify: Verify the community folder file added event appears in the AS</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0D97B00D0908568A85257BD4003C2AD4">TTT - AS - Follow - Person - Community - 00205 - Community Folder File Added - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void fileaddtofolder_PublicCommunity() {
		
		ui.startTest();
		
		// User 1 will now create a community file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);	
		
		// User 1 will now add the file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, communityFile, communityFolder);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseFolder.getName(), testUser1.getDisplayName());
		
		// Verify that the add file to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: communityfolderfileremoved_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 start a public community</B></li>
	*<li><B>Step: testUser 2 who is following User 1 log into Home</B></li>
	*<li><B>Step: testUser 1 remove a file to the folder in the community</B></li>
	*<li><B>Step: testUser 2 go to Homepage / Updates / I'm Following / All, Communities & People</B></li>
	*<li><B>Verify: Verify the community folder file added event is removed from the AS</B></li>
	* <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6170D9485020B64D85257BD4003C2AD1">TTT -  AS - Follow - Person - Communities - 00210 - Community Folder File Removed - Public Community</a></li>
	* @author Srinivas Vechha
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void communityfolderfileremoved_PublicCommunity() {
			
		String testName = ui.startTest();
		
		// User 1 will now create a folder in the community - a new folder is created in this test case in order to prevent duplicate news stories appearing in the AS
		BaseFile baseCommunityFolder = FileBaseBuilder.buildBaseFile(testName + Helper.genStrongRand(), "", ShareLevel.EVERYONE);
		FileEntry testFolder = CommunityFileEvents.addFolder(publicCommunity, baseCommunityFolder, testUser1, filesAPIUser1);
				
		// User 1 will now create a community file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);	
		
		// User 1 will now add the file to the community folder
		CommunityFileEvents.addFileToFolder(testUser1, filesAPIUser1, communityFile, testFolder);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String fileAddedToFolderEvent = CommunityFileNewsStories.getAddFileToFolderNewsStory(ui, baseCommunityFolder.getName(), testUser1.getDisplayName());
		
		// Verify that the add file to folder event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, true);
		
		// User 1 will now remove the file from the community folder
		CommunityFileEvents.removeFileFromFolder(testUser1, filesAPIUser1, communityFile, testFolder);
		
		// Verify that the add file to folder event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileAddedToFolderEvent}, TEST_FILTERS, false);
		
		ui.endTest();	
	}		
}