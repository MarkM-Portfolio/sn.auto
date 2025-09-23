package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_FileEvents_VisitorAdded extends SetUpMethods2 {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APIFileHandler filesAPIUser1, filesAPIUser3;
	private APIProfilesHandler profilesAPIUser1;
	private BaseCommunity baseCommunityUser1, baseCommunityUser3;
	private CommunitiesUI uiCo;
	private Community visitorModelCommunityUser1, visitorModelCommunityUser3;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser3 = new APIFileHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now create the restricted community with both User 2 and User 3 added as members and User 3 following the community
		User[] user1MembersToAdd = { testUser2, testUser3 };
		baseCommunityUser1 = CommunityBaseBuilder.buildVisitorModelBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand());
		visitorModelCommunityUser1 = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollower(baseCommunityUser1, testUser1, communitiesAPIUser1, user1MembersToAdd, testUser3, communitiesAPIUser3);
	
		// User 3 (acting as User 1) will now create the restricted community with both User 1 and User 2 added as members and User 1 following the community
		User[] user3MembersToAdd = { testUser1, testUser2 };
		baseCommunityUser3 = CommunityBaseBuilder.buildVisitorModelBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand());
		visitorModelCommunityUser3 = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollower(baseCommunityUser3, testUser3, communitiesAPIUser3, user3MembersToAdd, testUser1, communitiesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(visitorModelCommunityUser1);
		communitiesAPIUser3.deleteCommunity(visitorModelCommunityUser3);
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a file to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A30DF3237BB7CE5A85257C8A0043D7E0">TTT - VISITORS - ACTIVITY STREAM - 00067 - SHARED EXTERNALLY HEADER - FILE EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileAdded() {
		
		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(visitorModelCommunityUser1, baseFile, testUser1, filesAPIUser1);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityUser1.getName(), testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is displayed with the file shared event in all views
		HomepageValid.verifySharedExternallyHeader(ui, driver, fileSharedEvent, null, null, null, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a file to this community</B></li>
	*<li><B>Step: testUser1 add a comment to the file</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A30DF3237BB7CE5A85257C8A0043D7E0">TTT - VISITORS - ACTIVITY STREAM - 00067 - SHARED EXTERNALLY HEADER - FILE EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileComment() {
		
		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(visitorModelCommunityUser1, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the file
		String commentOnFile = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(visitorModelCommunityUser1, communityFile, commentOnFile, testUser1, filesAPIUser1);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String fileCommentEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is displayed with the comment on file event in all views
		HomepageValid.verifySharedExternallyHeader(ui, driver, fileCommentEvent, commentOnFile, null, null, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileComment_mention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a file to this community</B></li>
	*<li><B>Step: testUser1 add a comment to the file mentioning another user in the same org</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A30DF3237BB7CE5A85257C8A0043D7E0">TTT - VISITORS - ACTIVITY STREAM - 00067 - SHARED EXTERNALLY HEADER - FILE EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_fileComment_mention() {
		
		/**
		 * In order to avoid duplicate news stories appearing in the news feed - this test case will use User 3 (as User 1) and User 1 (as User 3)
		 * ie. the user assignments are reversed between User 1 and User 3 since both belong to the same organisation
		 */
		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(visitorModelCommunityUser3, baseFile, testUser3, filesAPIUser3);
		
		// Create the mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 3 will now log in to the community / files screen - critical step for the mentions to User 3 to work correctly
		CommunityFileEvents.loginAndNavigateToCommunityFiles(visitorModelCommunityUser3, baseCommunityUser3, ui, uiCo, testUser1, communitiesAPIUser1, false);
		
		// User 1 will now comment on the file with mentions to User 3
		CommunityFileEvents.addCommentWithMentions(visitorModelCommunityUser3, communityFile, mentions, testUser3, filesAPIUser3);
		
		// Return to Home and then navigate to I'm Following
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// Create the news story to be verified
		String fileCommentEvent = CommunityFileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser3.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + testUser1.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the 'Shared Externally' header is displayed with the comment on file event in all views
		HomepageValid.verifySharedExternallyHeader(ui, driver, fileCommentEvent, mentionsComment, null, null, TEST_FILTERS, true);
		
		ui.endTest();
	}
}