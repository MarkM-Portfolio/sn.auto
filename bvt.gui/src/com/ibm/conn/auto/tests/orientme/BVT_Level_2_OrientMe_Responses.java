package com.ibm.conn.auto.tests.orientme;

import java.util.HashMap;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class BVT_Level_2_OrientMe_Responses extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Responses.class);
	private TestConfigCustom cfg;
	private OrientMeUI ui;
	private User testUserA, testUserB;
	private String serverUrl;
	private APIActivitiesHandler apiActTestUserA, apiActTestUserB;
	private APICommunitiesHandler apiCommTestUserA;
	private APIForumsHandler apiForumsTestUserA, apiForumsTestUserB;
	private APIWikisHandler apiWikiTestUserA, apiWikiTestUserB;
	private APIFileHandler apiFileTestUserA, apiFileTestUserB;
	private APIProfilesHandler apiProfileTestUserA, apiProfileTestUserB;

	Activity activity;
	Community community;
	FileEntry sharedFile;
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		ui = OrientMeUI.getGui(cfg.getProductName(), driver);
		
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiActTestUserA = new APIActivitiesHandler(cfg.getProductName(), serverUrl,
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiActTestUserB = new APIActivitiesHandler(cfg.getProductName(), serverUrl,
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiCommTestUserA = new APICommunitiesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiForumsTestUserA = new APIForumsHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiForumsTestUserB = new APIForumsHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiWikiTestUserA = new APIWikisHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiWikiTestUserB = new APIWikisHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiWikiTestUserA = new APIWikisHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiWikiTestUserB = new APIWikisHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiFileTestUserA = new APIFileHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiFileTestUserB = new APIFileHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiProfileTestUserA = new APIProfilesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiProfileTestUserB = new APIProfilesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Count in icon reflects new responses since last time the page was viewed.</li>
	*<li><B>Step:</B>(API) Create an Activity and a Community as UserA, add UserB as member.</li>
	*<li><B>Step:</B>(API) As UserA, create a community forum topic and wiki page.</li>
	*<li><B>Step:</B>(API) As UserA, upload a file</li>
	*<li><B>Step:</B>(API) As UserA, create an activity entry.</li>
	*<li><B>Step:</B>(API) As UserB, add a comment to the activity entry.  Add comment to the file.</li>
	*<li><B>Step:</B>(API) As UserB, add a comment to the community forum topic.  Add comment to community wiki.</li>
	*<li><B>Step:</B>As UserA, go to OrientMe.</li>
	*<li><B>Verify:</B>'Responses' button displays a count. It should show the number of responses since last visit.</li>
	*<li><B>Step:</B>Toggle between the 'Responses' and 'All Updates' button. Click the 'Responses' icon again.</li>
	*<li><B>Verify:</B>Count no longer appears in the Response icon.</li>
	*<li><B>Verify:</B>The responses added by UserB appear.</li>
	*<li><B>Step:</B>Click 'X' to the right of Responses in the 'Add filter...' field.</li>
	*<li><B>Verify:</B>Verify the Latest Updates tab is selected.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*</ul>
	 */
	@Test(groups = {"level2", "cplevel2"})
	public void responseCount() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		int expectedCount = 4;
		
		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (cfg.getTestConfig().serverIsMT())  {
			expectedCount = 3;
		}
		
		// load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		ui.goToOrientMe(testUserA, false);
				
		// Check if there is already new responses. If so, clear it.
		ui.resetResponsesCount();
		
		// Seed resources using the API
		Map<String, String> resourceStrings = responsesTestDataPop();
		
		// Check Response count
		logger.strongStep(testUserA.getDisplayName() + "goes to OrientMe to check Response count to be " + expectedCount);
		driver.navigate().refresh();
		String count = ui.getResponseCount();
		Assert.assertTrue(count != null && !count.equals("0") && !count.isEmpty(), "Response count is found");
		Assert.assertEquals(Integer.parseInt(count), expectedCount, "Expected Responses count found");
		
		// Toggle between the Responses and All Updates buttons and check count again.
		log.info("Toggle between the Responses and All Updates buttons. Verify count is reset.");
		logger.strongStep("Toggle between the Responses and All Updates buttons. Verify count is reset.");
		ui.clickLink(OrientMeUIConstants.responseIcon);
		ui.clickLink(OrientMeUIConstants.allUpdatesIcon);
		count = ui.getResponseCount();
		Assert.assertTrue(count == null || count.equals("0") || count.isEmpty(), "Response count is not found.");
		
		// Click the Response icon and look for the entry comment in filtered results
		ui.clickLink(OrientMeUIConstants.responseIcon);
		String title = testUserB.getDisplayName() + " commented on your " + resourceStrings.get("activity_entry") + " entry thread in the " +
				resourceStrings.get("activity") + " activity.";
		logger.strongStep("Verify entry comment is found.");
		ui.fluentWaitTextPresent(title);
		
		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (!cfg.getTestConfig().serverIsMT())  {
			title = testUserB.getDisplayName() + " commented on your wiki page " + resourceStrings.get("community_wiki") + " in the " + resourceStrings.get("community") + " wiki."; 
			logger.strongStep("Verify community wiki comment is found.");
			ui.fluentWaitTextPresent(title);
		}
		
		title = testUserB.getDisplayName() + " replied to your " + resourceStrings.get("community_forum_topic") + " topic thread in the " +  resourceStrings.get("community_forum") + " forum.";
		logger.strongStep("Verify community forum reply is found.");
		ui.fluentWaitTextPresent(title);
		
		title = testUserB.getDisplayName() + " commented on your file.";
		logger.strongStep("Verify file comment response entry is found.");
		ui.fluentWaitTextPresent(title);
		
		log.info("Dismiss the Responses filter and verify Latest Updates tab is selected.");
		logger.strongStep("Dismiss the Responses filter and verify Latest Updates tab is selected.");
		ui.clickRemoveFilter(OrientMeUIConstants.responseFilter);
		ui.waitForPageLoaded(driver);
		Element latestUpdate = ui.getFirstVisibleElement(OrientMeUIConstants.latestUpdate);
		Assert.assertTrue(ui.isTabSelected(latestUpdate), "Latest Updates tab is selected");

		ui.endTest();
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		if (activity != null) {
			apiActTestUserA.deleteActivity(activity);
		}
		if (community != null) {
			apiCommTestUserA.deleteCommunity(community);
		}
		if (sharedFile != null) {
			apiFileTestUserA.deleteFile(sharedFile);
		}
	}
	

	
	/**
	 *<ul>
	 *<li><B>Data population for Responses test</B></li>
	 *<li>UserA upload a file and share with UserB.  UserB creates a comment to the file.</li>
	 *<li>UserA creates an activity with UserB as member, creates an entry. UserB comments on the entry.</li>
	 *<li>UserA creates a community with UserB as member. </li>
	 *<li>UserA creates a community forum topic and (if MT) wiki page.</li>
	 *<li>UserB creates a comment to the community forum topic and (if MT) wiki page.</li>
	 *</ul>
	 *@return map of resource created
	 */
	private Map<String, String> responsesTestDataPop() {
		Map<String, String> resources = new HashMap<String, String>();

		// Activities data population
		String logMsg = "(API) Create an activity as " + testUserA.getDisplayName() + " with " + testUserB.getDisplayName() + " as member";
		logMessage(logMsg);
		BaseActivity baseActivity = ActivityBaseBuilder.buildBaseActivity(
				getClass().getSimpleName() + Helper.genStrongRand(), false);
		activity = ActivityEvents.createActivityWithOneMember(baseActivity, testUserA, 
				apiActTestUserA, testUserB, true);
		resources.put("activity", activity.getTitle());

		logMsg = "(API) Create an activity entry as " + testUserA.getDisplayName();
		logMessage(logMsg);
		BaseActivityEntry baseActivityEntry = BaseActivityEntry.builder(
				getClass().getSimpleName() + Helper.genStrongRand())
				.description(Data.getData().commonDescription + Helper.genStrongRand())
				.build();
		ActivityEntry entry = ActivityEvents.createActivityEntry(testUserA, apiActTestUserA, baseActivityEntry, activity);
		resources.put("activity_entry", entry.getTitle());
		
		logMsg = "(API) Create a comment to the activity entry as " + testUserB.getDisplayName();
		logMessage(logMsg);
		ActivityEvents.createComment(activity, entry, null, "activity comment", testUserB, apiActTestUserB, false);
		
		// Community Forum data population
		logMsg = "(API) Create a community as " + testUserA.getDisplayName() + " with member " + testUserB.getDisplayName();
		logMessage(logMsg);	
		BaseCommunity baseCommunity = new BaseCommunity.Builder(getClass().getSimpleName() + Helper.genStrongRand())
				.access(Access.RESTRICTED)
				.addMember(new Member(CommunityRole.MEMBERS,testUserB)).shareOutside(false)
				.approvalRequired(false)
				.build();		
		community = baseCommunity.createAPI(apiCommTestUserA);

		resources.put("community", community.getTitle());
		
		logMsg = "(API) Create a community forum topic as " + testUserA.getDisplayName();
		logMessage(logMsg);
		BaseForum baseCommForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		Forum commForum = CommunityForumEvents.createForum(community, serverUrl, testUserA, apiCommTestUserA, apiForumsTestUserA, baseCommForum);
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(
				getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, commForum);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicInSpecifiedForum(testUserA, apiForumsTestUserA, baseForumTopic);
		resources.put("community_forum", commForum.getTitle());
		resources.put("community_forum_topic", forumTopic.getTitle());
		

		logMsg = "(API) Create a forum reply as " + testUserB.getDisplayName();
		logMessage(logMsg);
		CommunityForumEvents.createForumTopicReply(testUserB, apiForumsTestUserB, forumTopic, Data.getData().commonComment + Helper.genStrongRand());
				
		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (!cfg.getTestConfig().serverIsMT())  {
			logMsg = "(API) Create a community wiki page as " + testUserA.getDisplayName();
			logMessage(logMsg);
			if (apiCommTestUserA.getWidgetID(community.getUuid(), "Wiki").isEmpty()) {
				if (apiCommTestUserA.getWidgetID(community.getUuid(), "Wiki").isEmpty()) {
					CommunityEvents.addCommunityWidget(community, BaseWidget.WIKI, testUserA, apiCommTestUserA, true);
				}
			}
			Wiki communityWiki = CommunityWikiEvents.getCommunityWiki(community, apiWikiTestUserA);
			BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
			WikiPage wikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUserA, apiWikiTestUserA);
			resources.put("community_wiki", wikiPage.getTitle());

			logMsg = "(API) Create a comment on the wiki page as " + testUserB.getDisplayName();
			logMessage(logMsg);
			CommunityWikiEvents.addCommentToWikiPage(wikiPage, testUserB, apiWikiTestUserB, Data.getData().commonComment+ Helper.genStrongRand());
		}

		logMsg = "(API) Upload a file as " + testUserA.getDisplayName() + " and share with " + testUserB.getDisplayName();
		logMessage(logMsg);		
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE);
		sharedFile = FileEvents.addFile(baseFile, testUserA, apiFileTestUserA);
		FileEvents.shareFileWithUser(sharedFile, testUserA, apiFileTestUserA, apiProfileTestUserB);
		
		logMsg = "(API) Add file comment as " + testUserB.getDisplayName();
		logMessage(logMsg);		
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUserB, apiFileTestUserB, sharedFile, user2Comment, apiProfileTestUserA);
		
		return resources;
	}

	private void logMessage(String logMsg)  {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		log.info(logMsg);
		logger.strongStep(logMsg);
	}
	
}
