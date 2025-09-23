package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.communities.files;

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
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	8th March 2016
 */

public class FVT_ImFollowing_Tags_PublicCommunityFile extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APIFileHandler filesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private String tagToFollow;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);		
		
		// Log in with User 1 and follow the tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser2, communitiesAPIUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(publicCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_publicCommunityFileUpload</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are owner of and has public access</B></li>
	*<li><B>Step: testUser 2 upload a file and add the tag that User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Files & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the files.file.created story is displayed in Homepage / All Updates filtered by Communities, Tags and Files</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3F605427795EE3ED852578FC00537772">TTT -AS - FOLLOW - TAG - FILES - 00133 - files.file.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileUpload_PublicCommunity() {			
		
		ui.startTest();
		
		// User 2 add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFileWithCustomTag(Data.getData().file1, ".jpg", ShareLevel.EVERYONE, tagToFollow);
		CommunityFileEvents.addFile(publicCommunity, baseFile, testUser2, filesAPIUser2);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified in all views
		String shareFileEvent = CommunityFileNewsStories.getUploadFileNewsStory(ui, testUser2.getDisplayName());
		
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
	*<li><B>Name: test_Tags_publicCommunityFileUpdate</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are owner of and has public access</B></li>
	*<li><B>Step: testUser 2 update a file and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1  go to Homepage / Updates / I'm Following / All, Communities & Files</B></li>
	*<li><B>Verify: Verify that the files.file.updated story is displayed in Homepage / All Updates filtered by Communities, Tags and Files</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/90AEB5686B1142DE852578FC005380F3">TTT - AS - FOLLOW - TAG - FILES - 00143 - files.file.updated - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileUpdate_PublicCommunity() {
		
		ui.startTest();
		
		// User 2 add a file to the community and update the file
		BaseFile baseFile = FileBaseBuilder.buildBaseFileWithCustomTag(Data.getData().file2, ".jpg", ShareLevel.EVERYONE, tagToFollow);
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", null);
		CommunityFileEvents.addAndUpdateFileVersion(publicCommunity, baseFile, baseFileNewVersion, testUser2, filesAPIUser2);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified in all views
		String updateFileEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser2.getDisplayName());
		
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
}