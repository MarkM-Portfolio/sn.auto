package com.ibm.conn.auto.tests.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.ShareUICnx8;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.ShareUIContants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class BVT_Cnx8_UI_ShareInConnections extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8_UI_ShareInConnections.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private CommonUICnx8 commonUI;
	private HomepageUI homepageUI;
	private ShareUICnx8 shareUI;
	private User targetUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private CommunitiesUI communitiesUI;
	private ForumsUI forumUI;
	private Forum standaloneForum;
	private BaseForum baseForum;
	private APIForumsHandler forumsAPIUser;
	private WikisUI wikiUI;
	private BlogsUI blogUI;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		targetUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		shareUI = new ShareUICnx8(driver);
		communitiesUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		homepageUI.addOnLoginScript(homepageUI.getCloseTourScript());
		commonUI = new CommonUICnx8(driver);
		forumUI = ForumsUI.getGui(cfg.getProductName(), driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that basic share functionality is
	 * working as expected</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type in target user name and message to share</li>
	 * <li><b>Step:</b>Click on Share button</li>
	 * <li>Verify share post confirmation message is shown</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and toggle
	 * the new UI</li>
	 * <li>Verify new post appears on status updates page</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T537</li>
	 * </ul>
	 */
	//TODO Cnx8ui: Disabled this test case for MT because of defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11513
	@Test(groups = { "cnx8ui-cplevel2", "mt-exclude", "cnx8ui-level2" }, enabled = true)
	public void verifyShareConfirmation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String message = "share test " + System.currentTimeMillis();

		homepageUI.startTest();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click on Share Button to display Share Dialog");
		shareUI.openShareInConnectionsDialog();

		logger.strongStep("Type in user name");
		log.info("INFO: Type in user name");
		shareUI.typeWithDelayWd(targetUser.getLastName(), By.cssSelector(ShareUIContants.searchTypeAhead));

		logger.strongStep("Type in the message to share");
		log.info("INFO: Type in the message to share");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeheadResult), 3);
		shareUI.typeWithDelayWd(message, By.cssSelector(ShareUIContants.shareMessage));

		logger.strongStep("Click on Post button");
		log.info("INFO: Click on Post button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareButton), 3, "Share message");

		logger.strongStep("Verify share post confirmation message is shown");
		log.info("INFO: Verify share post confirmation message is shown");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.confirmationMsg), 5),
				"Confirmation is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		logger.strongStep(
				"Load homepage, login as " + targetUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + targetUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.loginAndToggleUI(targetUser, cfg.getUseNewUI());

		logger.strongStep("Verify new post appears on status updages page");
		log.info("INFO: Verify new post appears on status updages page");
		cnxAssert.assertTrue(
				shareUI.isElementVisibleWd(By.xpath(ShareUIContants.sharePost.replace("PLACEHOLDER", message)), 5),
				"Post message is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		homepageUI.endTest();

	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that share with community functionality
	 * is working as expected</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Select community to enable share with community</li>
	 * <li><b>Step:</b>Type in target community name and message to share</li>
	 * <li><b>Step:</b>Click on Share button</li>
	 * <li>Verify share post confirmation message is shown</li>
	 * <li><b>Step:</b>Redirect to target Community and toggle the new UI</li>
	 * <li>Verify new post appears on status updates page</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T537</li>
	 * </ul>
	 */
	//TODO Cnx8ui: Disabled this test case for MT because of defect https://jira.cwp.pnp-hcl.com/browse/CNXTOOL-748
	//TODO Cnx8UI: Disabled it for cpbvt too because now there is not dropdown for people and community as part of https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11431
	@Test(groups = { "cnx8ui-cplevel2", "mt-exclude" }, enabled = false)
	public void verifyShareConfirmationCommunities() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String message = "share test " + System.currentTimeMillis();

		homepageUI.startTest();

		List<Member> listMembers = new ArrayList<Member>();
		listMembers.add(new Member(CommunityRole.MEMBERS, testUser));

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
				.addMembers(listMembers).tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.description(Data.getData().commonDescription).build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click on Share Button to display Share Dialog");
		shareUI.openShareInConnectionsDialog();

		logger.strongStep("Select community to share message in Communities");
		log.info("INFO: Select community to share message in Communities");
		shareUI.selectFromDropdownWithValue(ShareUIContants.communitySelect, "community");

		logger.strongStep("Type in community name");
		log.info("INFO: Type in community name");
		shareUI.typeWithDelayWd(community.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));

		logger.strongStep("Type in the message to share");
		log.info("INFO: Type in the message to share");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeheadResult), 3);
		shareUI.typeWithDelayWd(message, By.cssSelector(ShareUIContants.shareMessage));

		logger.strongStep("Click on Post button");
		log.info("INFO: Click on Post button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareButton), 3, "Share message");

		logger.strongStep("Verify share post confirmation message is shown");
		log.info("INFO: Verify share post confirmation message is shown");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.confirmationMsg), 5),
				"Confirmation is visible");

		logger.strongStep("Load community");
		log.info("INFO: Load community");
		communitiesUI.loadComponent(Data.getData().ComponentCommunities, true);

		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(communitiesUI);

		logger.strongStep("Select Recent Updates");
		log.info("INFO: Select Recent Updates");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.recetUpdates), 3, "Select recent updates");

		logger.strongStep("Verify new post appears on status updages page");
		log.info("INFO: Verify new post appears on status updages page");
		cnxAssert.assertTrue(
				shareUI.isElementVisibleWd(By.xpath(ShareUIContants.sharePost.replace("PLACEHOLDER", message)), 5),
				"Post message is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		homepageUI.endTest();

	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that share icon is working on different
	 * CNX pages</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and toggle
	 * the new UI</li>
	 * <li><b>Step:</b>Click on different Connection Pages e.g: Communities, Files,
	 * Blogs, Wikis etc.</li>
	 * <li><b>Step:</b>Click on Share Button on these pages to display Share
	 * Dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is opened</li>
	 * <li><b>Step:</b>Click on Cross icon in Share dialog to close the dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is closed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T532</li>
	 * </ul>
	 */
	//TODO Cnx8ui :removed "FORUMS" from below list due to an issue of share window. Defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11475 is raised for the same
	//Removed "NOTIFICATIONS" from below list due to an issue https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11878
	//Removed "ACTIVITIES" due to 404 error. https://jira.cwp.pnp-hcl.com/browse/CNXTOOL-766 is already open for the same.
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyShareOnDiffCNXPages() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		commonUI.startTest();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		String cnxPages[] = { "COMMUNITIES","ACTIVITIES", "BOARDS", "PEOPLE", "FILES", "PROFILE", "BLOGS",
				"BOOKMARKS", "WIKIS" };
				
		String excludeForMT[] = {"BLOGS", "FORUMS", "WIKIS", "BOOKMARKS"};

		for (String app : cnxPages) {
			if (app.equalsIgnoreCase("BOARDS") && !(cfg.getIsKudosboardEnabled())) {
				log.warn("Kudosboard is disabled, skipping Boards test.");
				continue;
			}
			if (Arrays.asList(excludeForMT).contains(app) && cfg.getTestConfig().serverIsMT()) {
				log.warn("Skipping " + app + "test for MT environment");
				continue;
			}
			
			logger.strongStep("Select " + app + " in nav menu");
			log.info("INFO: Select " + app + " in nav menu");
			AppNavCnx8 appNav = AppNavCnx8.valueOf(app);
			appNav.select(commonUI);
			commonUI.waitForElementVisibleWd(By.xpath(appNav.getAppMenuLocator()
					.replace(AppNavCnx8.SUBNAV.getAppMenuLocator(), AppNavCnx8.NAVBAR.getAppMenuLocator())), 3);

			logger.strongStep("Verify " + app + " is selected in nav");
			cnxAssert.assertTrue(appNav.isAppSelected(commonUI), app + " is selected in navigation");

			logger.strongStep("Click on Share Button to display Share Dialog");
			shareUI.openShareInConnectionsDialog();
			cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 3),
					"Share Dialog is visible");

			logger.strongStep("Click on Close icon to close Share Dialog");
			log.info("INFO: Click on Close icon to close Share Dialog");
			shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.closeShareIcon), 2);
			cnxAssert.assertFalse(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 3),
					"Share Dialog is disabled");
		}
		
		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		commonUI.logout();

		commonUI.endTest();

	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that cancel button is working in Share
	 * dialog box</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and toggle
	 * the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is opened</li>
	 * <li><b>Step:</b>Click on Cancel button in Share dialog to close the
	 * dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is closed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T533</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyCancelButtonInShareDialog() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		commonUI.startTest();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click on Share Button to display Share Dialog");
		shareUI.openShareInConnectionsDialog();
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 3),
				"Share Dialog is visible");

		logger.strongStep("Click on Cancel Button to close Share Dialog");
		log.info("INFO: Click on Cancel Button to close Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.cancelShareButton), 5);
		shareUI.waitForElementInvisibleWd(By.cssSelector(ShareUIContants.shareDialog), 6);
		cnxAssert.assertFalse(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 6),
				"Share Dialog is closed");
		
		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		commonUI.logout();

		commonUI.endTest();

	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify various share dialog elements</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and toggle
	 * the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is opened</li>
	 * <li><b>Step:</b>Verify user icon is present inside the dialog</li>
	 * <li><b>Step:</b>Click on text area</li>
	 * <li><b>Step:</b>Verify text area is highlighted</li>
	 * <li><b>Step:</b>Verify preview area title</li>
	 * <li><b>Step:</b>Verify preview area text</li>
	 * <li><b>Step:</b>Click on Cancel button in Share dialog to close the
	 * dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is closed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T534</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T535</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T536</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyShareDialogElements() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
			
		commonUI.startTest();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click on Share Button to display Share Dialog");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Verify user icon is available");
		log.info("INFO: Verify user icon is available");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.userIcon), 2),
				"User icon is visible");		
		
		logger.strongStep("Click on text area");
		log.info("INFO: Click on text area");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareMessage), 1);
		
		logger.strongStep("Verify that Text Area is highlighted");
		log.info("INFO: Verify that Text Area is highlighted");
		WebElement textarea = commonUI.findElement(By.cssSelector(ShareUIContants.shareMessage)).findElement(By.xpath("./.."));	
		cnxAssert.assertTrue(textarea.getAttribute("class").toLowerCase().contains("mui-focused"),"Text Area is highlighted");
		
		logger.strongStep("Verify content in preview area");
		log.info("INFO: Verify content in preview area");
		
		WebElement previewTitle = commonUI.findElement(By.cssSelector(ShareUIContants.previewTitle));
		WebElement previewText = commonUI.findElement(By.cssSelector(ShareUIContants.previewText));
		cnxAssert.assertEquals(previewTitle.getAttribute("innerHTML"), driver.getTitle(), "Preview area title confirmed");
		cnxAssert.assertEquals(previewText.getAttribute("innerHTML"), driver.getCurrentUrl(), "Preview area text confirmed");
		
		logger.strongStep("Click on Cancel Button to close Share Dialog");
		log.info("INFO: Click on Cancel Button to close Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.cancelShareButton), 2);
		shareUI.waitForElementInvisibleWd(By.cssSelector(ShareUIContants.shareDialog), 6);
		cnxAssert.assertFalse(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 5),
				"Share Dialog is closed");
		
		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		commonUI.logout();

		commonUI.endTest();

	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that share functionality is working as
	 * expected for forum topics</li>
	 * <li><b>Step:</b>Create Forums topic using API</li>
	 * <li><b>Step:</b>Login to Forums component</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Select Forums topic's page</li>
	 * <li><b>Step:</b>Click on Share Link against forum topic to display Share
	 * Dialog</li>
	 * <li><b>Step:</b>Type in target user name and message to share</li>
	 * <li><b>Step:</b>Click on Share button</li>
	 * <li>Verify share post confirmation message is shown</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and toggle
	 * the new UI</li>
	 * <li>Verify new post appears on status updates page</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T577</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2","mt-exclude" }, enabled = true)
	public void verifyShareOnForumTopic() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String message = "Forum topic share test " + System.currentTimeMillis();

		String testName = forumUI.startTest();
		
		//Create forum using API
		forumsAPIUser = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum = ForumEvents.createForum(testUser, forumsAPIUser, baseForum);

		// Create Forums topic using API
		logger.strongStep("Create Forums topic using API");
		log.info("Create Forums topic using API");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(),
				standaloneForum);
		ForumEvents.createForumTopic(testUser, forumsAPIUser, baseForumTopic);
		log.info("FORUM TOPIC ID --  " + baseForumTopic.getUUID());

		// Login to Forums component
		logger.strongStep("Load Forum Component, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("Load Forum Component, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		forumUI.loadComponent(Data.getData().ComponentForums);
		forumUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		// Select Forums topic's page
		logger.strongStep("Select Forums topic's page");
		log.info("Select Forums topic's page");
		forumUI.clickLinkWait(forumUI.getTopicSelector(baseForumTopic));

		logger.strongStep("Click on Share Link against forum topic to display Share Dialog");
		log.info("INFO: Click on Share Link against forum topic to display Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ForumsUIConstants.shareForumTopic), 5);
		
		List<WebElement> shareOptions = shareUI.findElements(By.xpath(ShareUIContants.shareInConnectionsDropDownOption));
		for(WebElement shareInConnection : shareOptions) {
			if (shareInConnection.isDisplayed()) {
				shareInConnection.click();
			}
		}
		
		logger.strongStep("Verify Share Dialog is opened");
		log.info("INFO: Verify Share Dialog is opened");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 5),
				"Share Dialog is visible");

		logger.strongStep("Verify content in preview area");
		log.info("INFO: Verify content in preview area");
		WebElement previewTitle = commonUI.findElement(By.cssSelector(ShareUIContants.previewTitle));
		WebElement previewText = commonUI.findElement(By.cssSelector(ShareUIContants.previewText));
		cnxAssert.assertEquals(previewTitle.getAttribute("innerHTML"), driver.getTitle(),
				"Preview area title confirmed");
		log.info("Preview Text  " + previewText.getText());
		cnxAssert.assertEquals(previewText.getText(), driver.getCurrentUrl(), "Preview area text confirmed");

		logger.strongStep("Type in user name");
		log.info("INFO: Type in user name");
		shareUI.typeWithDelayWd(targetUser.getLastName(), By.cssSelector(ShareUIContants.searchTypeAhead));

		logger.strongStep("Type in the message to share");
		log.info("INFO: Type in the message to share");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeheadResult), 3);
		shareUI.typeWithDelayWd(message, By.cssSelector(ShareUIContants.shareMessage));

		logger.strongStep("Click on Post button");
		log.info("INFO: Click on Post button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareButton), 3, "Share message");

		logger.strongStep("Verify share post confirmation message is shown");
		log.info("INFO: Verify share post confirmation message is shown");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.confirmationMsg), 5),
				"Confirmation is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		forumUI.logout();
		
		logger.strongStep("Load homepage, login as " + targetUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + targetUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.loginAndToggleUI(targetUser, cfg.getUseNewUI());
		driver.navigate().to(serverURL+"/"+Data.getData().HomepageImFollowing);

		logger.strongStep("Verify new post appears on status updages page");
		log.info("INFO: Verify new post appears on status updates page");
		cnxAssert.assertTrue(
				shareUI.isElementVisibleWd(By.xpath(ShareUIContants.sharePost.replace("PLACEHOLDER", message)), 5),
				"Post message is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		forumUI.endTest();

	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify Share functionality for Wiki entry is working as expected</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Load Wiki Page</li>
	 * <li>Verify Share icon should be present against each wiki entry next to 'like'.</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Select community to enable share with community</li>
	 * <li><b>Step:</b>Type in target community name and message to share</li>
	 * <li><b>Step:</b>Click on Share button</li>
	 * <li>Verify share post confirmation message is shown</li>
	 * <li><b>Step:</b>Redirect to target Community and toggle the new UI</li>
	 * <li>Verify new post appears on status updates page</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T568</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyShareInConnectionsOnWiki() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName ="Test Wiki" +Data.getData().commonName + Helper.genDateBasedRand();
		
		BaseWiki wiki = new BaseWiki.Builder(testName  + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand())
				.description("Description for test " + testName)
				.build();
		
		String message = "share test " + System.currentTimeMillis();

		homepageUI.startTest();

		List<Member> listMembers = new ArrayList<Member>();
		listMembers.add(new Member(CommunityRole.OWNERS, testUser));

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
				.addMembers(listMembers).tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.description(Data.getData().commonDescription).build();
		
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		logger.strongStep("Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Wikipage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO:Load Wikipage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		wikiUI.loadComponent(Data.getData().ComponentWikis);
		wikiUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		wiki.create(wikiUI);

		logger.strongStep("Validate that the Share button is present");
		log.info("INFO: Validate that the Share button is present");

		homepageUI.waitForPageLoaded(driver);
		homepageUI.waitForElementVisibleWd(By.cssSelector(ShareUIContants.shareInConnectionsInWiki), 20);
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareInConnectionsInWiki), 20),
				"Share Dialog is visible");
		
		logger.strongStep("Click on Share Button to display Share Dialog");
		log.info("INFO: Click on Share Button to display Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareInConnectionsInWiki), 2);
		
		List<WebElement> shareOptions = shareUI.findElements(By.xpath(ShareUIContants.shareInConnectionsDropDownOption));
		for(WebElement shareInConnection : shareOptions) {
			if (shareInConnection.isDisplayed()) {
				shareInConnection.click();
			}
		}

		logger.strongStep("Type in community name");
		log.info("INFO: Type in community name");
		homepageUI.waitForElementVisibleWd(By.cssSelector(ShareUIContants.searchTypeAhead), 8);
		shareUI.typeWithDelayWd(community.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));

		logger.strongStep("Type in the message to share");
		log.info("INFO: Type in the message to share");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeheadResult), 3);
		shareUI.typeWithDelayWd(message, By.cssSelector(ShareUIContants.shareMessage));

		logger.strongStep("Click on Post button");
		log.info("INFO: Click on Post button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareButton), 3, "Share message");

		logger.strongStep("Verify share post confirmation message is shown");
		log.info("INFO: Verify share post confirmation message is shown");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.confirmationMsg), 5),
				"Confirmation is visible");

		logger.strongStep("Load community");
		log.info("INFO: Load community");
		communitiesUI.loadComponent(Data.getData().ComponentCommunities, true);

		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(communitiesUI);

		logger.strongStep("Select Recent Updates");
		log.info("INFO: Select Recent Updates");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.recetUpdates), 3, "Select recent updates");

		logger.strongStep("Verify new post appears on status updages page");
		log.info("INFO: Verify new post appears on status updages page");
		cnxAssert.assertTrue(
				shareUI.isElementVisibleWd(By.xpath(ShareUIContants.sharePost.replace("PLACEHOLDER", message)), 5),
				"Post message is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		homepageUI.endTest();
	}
	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that share functionality is working as
	 * expected for blogs entries</li>
	 * <li><b>Step:</b>Create Community using API</li>
	 * <li><b>Step:</b>Login to Blogs component</li>
	 * <li><b>Step:</b>Create a new blog</li>
	 * <li><b>Step:</b>Click on the Add Entry link</li>
	 * <li><b>Step:</b>Add a new Entry</li>
	 * <li><b>Step:</b>Navigate to on Public Blog Page</li>
	 * <li><b>Step:</b>Get entry id based on entry's title</li>
	 * <li><b>Step:</b>Click on Share button based on blog post entry id</li>
	 * <li>Verify share post confirmation message is shown</li>
	 * <li><b>Step:</b>Redirect to target Community</li>
	 * <li>Verify new post appears on status updates page</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T567</li>
	 * </ul>
	 */
	//https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11773
	@Test(groups = { "cnx8ui-cplevel2", "mt-exclude" }, enabled = false)
	public void verifyShareInConnectionsOnBlog() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String message = "share test " + System.currentTimeMillis();
		String testName = "Test Blog" + Data.getData().commonName + Helper.genDateBasedRand();
		String randval = Helper.genDateBasedRandVal();
		String blogAddress = Data.getData().BlogsAddress1 + randval;

		homepageUI.startTest();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, blogAddress).tags("Tag for " + testName + randval).description("Test description for testcase " + testName).timeZone(Time_Zone.Europe_London).theme(Theme.Blog_with_Bookmarks)
				.build();
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntryForShareFunctionality" + Helper.genDateBasedRand()).blogParent(blog).tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName).build();

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand()).tags(Data.getData().commonTag + Helper.genDateBasedRand()).description(Data.getData().commonDescription).build();

		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		

		logger.strongStep("Open Blogs and login: " + testUser.getDisplayName());
		log.info("INFO: Load blogs component and login");
		blogUI.loadComponent(Data.getData().ComponentBlogs);
		blogUI.login(testUser);

		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(blogUI);

		logger.strongStep("Click on the Add Entry link");
		log.info("INFO: Click on the Add Entry link");
		shareUI.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));

		logger.strongStep("Add a new Entry");
		log.info("INFO: Add the new entry");
		blogEntry.create(blogUI);

		logger.strongStep("Navigate to on Public Blog Page");
		log.info("INFO: Navigate to on Public Blog Page");
		blogUI.clickLinkWait(BlogsUIConstants.PublicBlogs);

		logger.strongStep("Get entry id based on entry's title");
		log.info("INFO: Get entry id based on entry's title");
		String blogPostXpath = ShareUIContants.blogPostEntryDiv.replace("PLACEHOLDER", blogEntry.getTitle());
		shareUI.waitForElementVisibleWd(By.xpath(blogPostXpath),3);
		WebElement blogPostElement = shareUI.findElement(By.xpath(blogPostXpath));
		String entryid = blogPostElement.getAttribute("id").replace("entry-", "").replace(":link:entries", "");

		logger.strongStep("Click on Share Button to display Share Dialog");
		log.info("INFO: Click on Share Button to display Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.blogShareLink.replace("PLACEHOLDER", "shareWrapper_" + entryid)), 2);
		
		logger.strongStep("Type in community name");
		log.info("INFO: Type in community name");
		shareUI.typeWithDelayWd(community.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));

		logger.strongStep("Type in the message to share");
		log.info("INFO: Type in the message to share");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeheadResult), 3);
		shareUI.typeWithDelayWd(message, By.cssSelector(ShareUIContants.shareMessage));

		logger.strongStep("Click on Post button");
		log.info("INFO: Click on Post button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.shareButton), 0, "Share message");

		logger.strongStep("Verify share post confirmation message is shown");
		log.info("INFO: Verify share post confirmation message is shown");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.confirmationMsg), 5), "Confirmation is visible");

		logger.strongStep("Load community");
		log.info("INFO: Load community");
		communitiesUI.loadComponent(Data.getData().ComponentCommunities, true);

		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(communitiesUI);

		logger.strongStep("Select Recent Updates");
		log.info("INFO: Select Recent Updates");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.recetUpdates), 3, "Select recent updates");

		logger.strongStep("Verify new post appears on status updages page");
		log.info("INFO: Verify new post appears on status updages page");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.xpath(ShareUIContants.sharePost.replace("PLACEHOLDER", message)), 5), "Post message is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		shareUI.logout();

		shareUI.endTest();

	}

}
