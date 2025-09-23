package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.saved;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
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
 * Date:	4th October 2016
 */

public class FVT_Saved_ForumEvents extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseCommunity baseCommunity;
	private BaseForum baseForum;
	private boolean isOnPremise;
	private CommunitiesUI uiCo;
	private Community publicCommunity;
	private Forum forum;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		forumsAPIUser1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
			baseCommunity = null;
			publicCommunity = null;
			
			// User 1 will now create a public standalone forum
			baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
			forum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
		} else {
			isOnPremise = false;
			
			// User 1 will now create a public community
			baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
			publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
			
			// User 1 will now log in to the community - ensures the API calls will work consistently against G2, G3 etc.
			CommunityEvents.loginAndNavigateToCommunity(publicCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, false);
			
			// Log out as User 1
			LoginEvents.gotoHomeAndLogout(ui);
			
			// Close the current browser instance
			UIEvents.closeCurrentBrowserWindow(ui);
			
			// User 1 will now create a forum in the community
			baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
			forum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		}
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		if(isOnPremise) {
			// Delete the forum created during the test
			forumsAPIUser1.deleteForum(forum);
		} else {
			// Delete the community created during the test
			communitiesAPIUser1.deleteCommunity(publicCommunity);
		}
	}
	
	/**
	* test_PublicForum_SaveAStoryOfAForumTopic()
	*<ul>
	*<li><B>1. Log into Forums (For Cloud environment got to Forums in a Public Community you own)</B></li>
	*<li><B>2. Start a forum</B></li>
	*<li><B>3. Start a topic in the forum</B></li>
	*<li><B>4. Go to Homepage / Discover / Forums</B></li>
	*<li><b>5: Go to the story of the topic in the forum and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story saved is of the topic in the forum</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/C6E6E3BFF6E925B3852579B300348D86">TTT: AS - Saved - 00070 - Save A Story Of A Forum Topic</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicForum_SaveAStoryOfAForumTopic() {
		
		String testName = ui.startTest();
		
		BaseForumTopic baseForumTopic;
		if(isOnPremise) {
			// User 1 will now add a topic to the standalone forum
			baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
			ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		} else {
			// User 1 will now add a topic to the community forum
			baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, forum);
			CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		}
				
		// Create the news stories to be saved
		String createTopicEvent;
		if(isOnPremise) {
			createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
			
			// User 1 will now save the news story of the create forum topic event using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, createTopicEvent, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
			
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
			// Save the news story of the create forum topic event using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createTopicEvent);
			
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		// Verify that the create forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicForum_ForumTopicRepliedToAfterTopicStartedIsSaved()
	*<ul>
	*<li><B>1. Log into Forums (For Cloud environment got to Forums in a Public Community you own)</B></li>
	*<li><B>2. Start a forum</B></li>
	*<li><B>3. Start a topic in the forum</B></li>
	*<li><B>4. Go to Homepage / Discover / Forums</B></li>
	*<li><b>5: Go to the story of the topic in the forum and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story saved is of the topic in the forum</b></li>
	*<li><b>7: Go back to the forum topic and reply to the topic</b></li>
	*<li><b>8: Go to Homepage / Discover / Forums</b></li>
	*<li><b>Verify: Verify that the story in discover is now of the reply to the topic</b></li>
	*<li><b>9: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story still saved is of the topic in the forum</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/EEBB0C79ECCFC4BB852579B300348EDC">TTT: AS - Saved - 00071 - Forum Topic Replied To After A Topic Started Story Is Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicForum_ForumTopicRepliedToAfterTopicStartedIsSaved() {
		
		String testName = ui.startTest();
		
		BaseForumTopic baseForumTopic;
		ForumTopic forumTopic;
		if(isOnPremise) {
			// User 1 will now add a topic to the standalone forum
			baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
			forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		} else {
			// User 1 will now add a topic to the community forum
			baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, forum);
			forumTopic = CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		}
				
		// Create the news stories to be saved
		String createTopicEvent;
		if(isOnPremise) {
			createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
			
			// User 1 will now save the news story of the create forum topic event using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, createTopicEvent, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
			
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
			
			// Save the news story of the create forum topic event using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createTopicEvent);
			
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		// Verify that the create forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now post a comment to the forum topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, forumTopic, topicReply);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String commentOnTopicEvent;
		if(isOnPremise) {
			commentOnTopicEvent = ForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseForum.getName());
		} else {
			commentOnTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseForum.getName());
		}
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTopicEvent, baseForumTopic.getDescription(), topicReply}, filter, true);
			
			// Verify that the create forum topic event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent},  null, false);
		}
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
						
			// Verify that the comment on forum topic event and User 1's comment are NOT displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTopicEvent, topicReply}, null, false);
		}
		ui.endTest();
	}
}