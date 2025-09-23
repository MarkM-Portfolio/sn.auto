package com.ibm.conn.auto.tests.orientme;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8_TopUpdates;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class BVT_Level_2_OrientMe_Filter extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Filter.class);
	private TestConfigCustom cfg;
	private OrientMeUI ui;
	private ItmNavCnx8 itmNavCnx8;
	private String serverUrl;
	private SearchAdminService adminService;
	private User testUserA, testUserB, searchAdmin;
	private APICommunitiesHandler apiCommTestUserB;
	private APIWikisHandler apiWikiTestUserA, apiWikiTestUserB;
	private APIProfilesHandler apiProfilesTestUserA, apiProfilesTestUserB;
	private Community community;
	private boolean isPrereqDone = false;

	Map<String, String> resourceStrings = new HashMap<String, String>();

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
		cfg = TestConfigCustom.getInstance();
		ui = OrientMeUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);

		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();

		apiCommTestUserB = new APICommunitiesHandler(serverUrl,
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiWikiTestUserA = new APIWikisHandler(serverUrl,
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiWikiTestUserB = new APIWikisHandler(serverUrl,
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiProfilesTestUserA = new APIProfilesHandler(serverUrl,
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiProfilesTestUserB = new APIProfilesHandler(serverUrl,
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());

		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();

		// populate test data using API
		resourceStrings = filterTestDataPop();
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpMethod() throws Exception {
		
		ui.goToOrientMe(testUserA, false);
		
		// Pre-req: Log in as UserA, add UserB to the Important to Me list
		if (!isPrereqDone)  {
			// observed that focus got taken away if clicking the Share Something box before 
			// page has finished loading so make best effort to wait until after
			log.info("INFO: wait for Top Updates page ready to interact");
			HomepageUICnx8_TopUpdates homepageCnx8TopUpdates = new HomepageUICnx8_TopUpdates(driver);
			homepageCnx8TopUpdates.waitForTopUpdatesInteractable();
			HomepageUI ui = HomepageUI.getGui(cfg.getProductName(), driver);
			ui.closeGuidedTourDialog();
			
			WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserB.getDisplayName(), false);
			if (user == null)  {
				log.info("Add " + testUserB.getDisplayName() + " to the Important to Me list.");
				itmNavCnx8.addImportantItem(testUserB.getDisplayName(), true);
			} else {
				log.info(testUserB.getDisplayName() + " is already in the Important to Me list.");
			}
			isPrereqDone = true;
		}
	}


	/**
	*<ul>
	*<li><B>Info:</B>People filter in OrientMe</li>
	*<li><B>Pre-req:</B>Log in as UserA, add UserB to the Important to Me list.</li>
	*<li><B>Step:</B>As UserB, create a new community and add UserA as member.</li>
	*<li><B>Step:</B>Kick off the search index.</li>
	*<li><B>Step:</B>Click on 'Filter' and search on UserB. Select UserB in typeahead.</li>
	*<li><B>Verify:</B>Community created by UserB displays along with other entries associated with the user.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void testFilterPeopleSearch() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		ui.loadComponent(Data.getData().ComponentOrientMe, true);
		logger.strongStep("Click on 'Filter' and search on " + testUserB.getDisplayName() + ". Select user from typeahead results.");
		ui.filterUpdates(testUserB.getDisplayName(), true);
		ui.waitForPageLoaded(driver);

		// Search for the community in filtered results
		String title = testUserB.getDisplayName() + " added you to the " + resourceStrings.get("community") + " community";
		logger.strongStep("Verify community entry is found: " + title);
		ui.fluentWaitTextPresent(title);

		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (!cfg.getTestConfig().serverIsMT())  {
			title = testUserB.getDisplayName() + " commented on your wiki page " + resourceStrings.get("community_wiki") + " in the " + resourceStrings.get("community") + " wiki.";
			logger.strongStep("Verify community wiki comment is found.");
			ui.fluentWaitTextPresent(title);
		}

		title = testUserB.getDisplayName() + " mentioned you in a message posted to the " + resourceStrings.get("community");
		logger.strongStep("Verify the community mention is found.");
		ui.fluentWaitTextPresent(resourceStrings.get("community_mention"));

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>Tag filter in OrientMe</li>
	*<li><B>Pre-req:</B>Log in as UserA, add UserB to the Important to Me list.</li>
	*<li><B>Step:</B>As UserA, create a status update with a hashtag #orientme</li>
	*<li><B>Step:</B>Kick off the search index.</li>
	*<li><B>Step:</B>Click on 'Filter' and search on the hashtag.  Search the hashtag.</li>
	*<li><B>Verify:</B>The status entry containing the hashtag displays.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*/
	@Test(groups = {"regression"})
	public void testFilterTagSearch() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		ui.loadComponent(Data.getData().ComponentOrientMe, true);
		logger.strongStep("Click on 'Filter' and search on the hashtag orientme.");
		ui.filterUpdates(resourceStrings.get("status_update_hashtag").replace("#", ""), false);

		logger.strongStep("Verify the status update is found.");
		ui.fluentWaitTextPresent(resourceStrings.get("status_update"));

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>Community filter in OrientMe</li>
	*<li><B>Pre-req:</B>Log in as UserA, add UserB to the Important to Me list.</li>
	*<li><B>Step:</B>As UserB, create a new community and add UserA as member.</li>
	*<li><B>Step:</B>Kick off the search index.</li>
	*<li><B>Step:</B>Click on 'Filter' and search on a word of the community.</li>
	*<li><B>Verify:</B>The community displays.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*/
	@Test(groups = {"regression"})
	public void testFilterCommunitySearch() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		ui.loadComponent(Data.getData().ComponentOrientMe, true);
		String communityPartialName = resourceStrings.get("community-rand");
		logger.strongStep("Click on 'Filter' and search on a word of the community: " + communityPartialName);
		ui.filterUpdates(communityPartialName, false);

		logger.strongStep("Verify the community is found.");
		ui.fluentWaitTextPresent(resourceStrings.get("community"));				

		ui.endTest();
	}

	/**
	*<li><B>Info:</B>Global Search in OrientMe</li>
	*<li><B>Pre-req:</B>Log in as UserB, who is in UserA's Important to Me list.</li>
	*<li><B>Step:</B>Click on the Search icon, enter a keyword and click All Content.</li>
	*<li><B>Verify:</B>All Content search results page displays with matching results form various apps.</li>
	 */
	@Test(groups = {"regression"})
	public void testGlobalSearchInOM() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		log.info("Load OrientMe and Log In as: " + testUserB.getDisplayName());
		ui.logout();
		ui.goToOrientMe(testUserB, true);

		log.info("Search for " + resourceStrings.get("community-rand") + " in Global Search (All Content).");
		logger.strongStep("Use global search (All Content) to search for " + resourceStrings.get("community-rand"));
		ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		ui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);
		ui.typeTextWithDelay(GlobalsearchUI.TextAreaInPanel, resourceStrings.get("community-rand"));
		ui.clickLink(GlobalsearchUI.AllContentScope);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Verify the community status update with mention is found.");
		ui.fluentWaitTextPresent(resourceStrings.get("community_mention"));

		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (!cfg.getTestConfig().serverIsMT())  {
			logger.strongStep("Verify the community wiki is found.");
			ui.fluentWaitTextPresent(resourceStrings.get("community_wiki"));

			logger.strongStep("Verify the community wiki comment is found.");
			ui.fluentWaitTextPresent(resourceStrings.get("community_wiki_comment"));
		}

		logger.strongStep("Click View All in Latest Status Update.");
		log.info("Click View All in Latest Status Update.");
		ui.clickLink(GlobalsearchUI.LatestStatusUpdateViewAll);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Verify the status update is found.");
		ui.fluentWaitTextPresent(resourceStrings.get("status_update"));
	}

	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		if (community != null) {
			apiCommTestUserB.deleteCommunity(community);
		}
	}

	/**
	 *<ul>
	 *<li><B>Data population for Responses test</B></li>
	 *<li>As UserA, post a status update with hashtag #orientme</li>
	 *<li>Kick off the search index</li>
	 *<li>UserB creates a community and add UserA as a member</li>
	 *<li>UserB posts a status update to the community and mentions UserA</li>
	 *<li>(If not MT) UserA creates a community wiki page.</li>
	 *<li>(If not MT) UserB creates a comment to the community wiki page.</li>
	 *</ul>
	 *@return map of resource created
	 * @throws Exception
	 */
	private Map<String, String> filterTestDataPop() throws Exception {
		Map<String, String> resources = new HashMap<String, String>();
		String randomString = Helper.genStrongRand();

		log.info("(API) Create a status update with #OrientMe as " + testUserA.getDisplayName());
		String statusUpdateHashtag = "#orientme" + randomString;
		// adding the randomString at the end again so that it's searchable
		apiProfilesTestUserA.postStatusUpdate(statusUpdateHashtag + " status " + randomString);
		resources.put("status_update_hashtag",statusUpdateHashtag);
		resources.put("status_update", statusUpdateHashtag + " status " + randomString);

		log.info("(API) Create a community as " + testUserB.getDisplayName() + " with member " + testUserA.getDisplayName());
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				getClass().getSimpleName() + " " + randomString, Access.RESTRICTED);
		community = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUserB, apiCommTestUserB, testUserA);
		resources.put("community", community.getTitle());
		resources.put("community-rand", randomString);
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		log.info("(API) Post a status update to the community as " + testUserB.getDisplayName() + " and mention " + testUserA.getDisplayName());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, apiProfilesTestUserA, serverUrl, testUserB.getDisplayName(), randomString);
		CommunityEvents.addCommStatusUpdateWithMentions(community, apiCommTestUserB, apiProfilesTestUserB, mentions);
		resources.put("community_mention", testUserB.getDisplayName() + " @" + testUserA.getDisplayName() + " " + randomString);
		adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		// not testing Wiki for MT due to intermittent 403 issue when creating resources (CNXTOOL-783)
		if (!cfg.getTestConfig().serverIsMT())  {
			log.info("(API) Create a community wiki page as " + testUserA.getDisplayName());
			if (apiCommTestUserB.getWidgetID(community.getUuid(), "Wiki").isEmpty()) {
				CommunityEvents.addCommunityWidget(community, BaseWidget.WIKI, testUserB, apiCommTestUserB, true);
			}
			Wiki communityWiki = CommunityWikiEvents.getCommunityWiki(community, apiWikiTestUserB);
			BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + " " + randomString);
			WikiPage wikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUserA, apiWikiTestUserA);
			resources.put("community_wiki", wikiPage.getTitle());

			log.info("(API) Create a comment on the wiki page as " + testUserB.getDisplayName());
			String wiki_comment = Data.getData().commonComment + " " + randomString;
			CommunityWikiEvents.addCommentToWikiPage(wikiPage, testUserB, apiWikiTestUserB, wiki_comment);
			resources.put("community_wiki_comment", wiki_comment);
			adminService.indexNow("wikis", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		}

		return resources;
	}
}
