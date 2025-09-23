package com.ibm.conn.auto.tests.homepage.fvt.finalisation.ee;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	13th October 2016
 */

public class FVT_EE_ForumEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunityPublic, baseCommunityRestricted;
	private BaseForum baseForumPublic, baseForumRestricted;
	private CommunitiesUI uiCo;
	private Community publicCommunity, restrictedCommunity;
	private Forum publicCommunityForum, restrictedCommunityForum;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		do {
			testUser2 = cfg.getUserAllocator().getUser(this);
		} while(testUser2.getDisplayName().equals(testUser1.getDisplayName()));
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		forumsAPIUser1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// User 1 will now create a public community
		baseCommunityPublic = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunityPublic, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community
		baseCommunityRestricted = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunityRestricted, testUser1, communitiesAPIUser1);
		
		// User 1 will now log in to the public community - ensures the API calls to create the forum and forum topic will work consistently against G2, G3 etc.
		CommunityEvents.loginAndNavigateToCommunity(publicCommunity, baseCommunityPublic, ui, uiCo, testUser1, communitiesAPIUser1, false);
		
		// User 1 will now navigate to the restricted community - again ensures all API calls will work correctly
		CommunityEvents.navigateToCommunity(baseCommunityRestricted, communitiesAPIUser1, restrictedCommunity, uiCo);
		
		// Log out as User 1
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Close the current browser instance
		UIEvents.closeCurrentBrowserWindow(ui);
		
		// User 1 will now create a forum in the public community
		baseForumPublic = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		publicCommunityForum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForumPublic);
		
		// User 1 will now create a forum in the restricted community
		baseForumRestricted = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		restrictedCommunityForum = CommunityForumEvents.createForum(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForumRestricted);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* test_PublicCommunity_ForumTopic_EE_Reply() 
	*<ul>
	*<li><B>1: Log in as User 1</b></li>
	*<li><B>2: User 1 create a public community</B></li>
	*<li><B>3: Add a forum to the community</B></li>
	*<li><b>4: Add a topic to the community forum</b></li>
	*<li><b>5: Navigate to the Discover view</b></li>
	*<li><B>6: Open the EE for the forum topic created news story</B></li>
	*<li><b>7: Post a reply to the forum topic using the EE - verification point 1</B></li>
	*<li><b>8: Look again at the Discover view - verification point 2</b></li>
	*<li><B>Verification Point 1: Verify that the reply is displayed in the EE after posting the reply</B></li>
	*<li><b>Verification Point 2: Verify that the reply to forum topic event and the reply are now displayed in the AS</B></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicCommunity_ForumTopic_EE_Reply() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunityPublic, publicCommunityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createForumTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumPublic.getName(), testUser1.getDisplayName());
		
		// Open the EE and post a reply to the forum topic using the EE
		String forumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.openEEAndPostForumTopicReplyUsingUI(ui, testUser1, createForumTopicEvent, forumTopicReply);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String replyToForumTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseForumPublic.getName());
		
		// Verify that the reply to topic event, the topic description and the reply are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToForumTopicEvent, baseForumTopic.getDescription(), forumTopicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_RestrictedCommunity_ForumTopic_EE_Mentions_NonMember() 
	*<ul>
	*<li><B>1: Log into a private community ensure User 2 is NOT a member</b></li>
	*<li><B>2: Create a forum topic</B></li>
	*<li><B>3: Go to Homepage Activity Stream</B></li>
	*<li><b>4: Go to the event of the topic</b></li>
	*<li><b>5: Open the EE for the story</b></li>
	*<li><B>6: Start to add a reply to the entry with and add User 2 as a mentions - verification point 1</B></li>
	*<li><b>7: User 2 go to Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verification Point 1: Verify the '@' is dropped and a message appears saying the user will not get the event</B></li>
	*<li><b>Verification Point 2: Verify the mentions event is NOT  there</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/13B3270EDD6E86BA85257CA70041289D">@Mentions - EE - ForumReply - 00015 - Adding a mentions in a private community - Non Member</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_RestrictedCommunity_ForumTopic_EE_Mentions_NonMember() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunityRestricted, restrictedCommunityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createForumTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumRestricted.getName(), testUser1.getDisplayName());
		
		// Open the EE and post a reply with mentions to a non-member using the EE
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityForumEvents.openEEAndPostForumTopicReplyWithMentionsUsingUI(ui, driver, testUser1, createForumTopicEvent, mentions, false);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
				
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the news stories to be verified
		String mentionedYouEvent = CommunityForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), baseForumRestricted.getName(), testUser1.getDisplayName());
		String mentionsTextValid = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		String mentionsTextInvalid = mentions.getBeforeMentionText() + " " + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event, forum topic description and both possible mentions links are NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, baseForumTopic.getDescription(), mentionsTextValid, mentionsTextInvalid}, null, false);
		
		ui.endTest();
	}
}