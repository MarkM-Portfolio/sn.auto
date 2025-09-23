package com.ibm.conn.auto.tests.share;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.services.AdminBannerService;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.ShareUICnx8;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.ShareUIContants;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

import io.restassured.response.Response;

public class BVT_Cnx8_UI_ShareInTeams extends SetUpMethods2 {

	private String hclShareExtensionType = "com.hcl.share.extension";
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8_UI_ShareInTeams.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, adminUser;
	private HomepageUI homepageUI;
	private ShareUICnx8 shareUI;
	private CustomizerUI uiCnx7;
	private String msTeamShareAppName = "MS Teams Share";
	private String serverURL;
	private CommonUICnx8 commonUI;
	private ForumsUI forumUI;
	private BlogsUI blogUI;
	private CustomizerUI customizerUI;
	private APIBlogsHandler apiOwner;
	private String msLoginUrl = "microsoft";
	private WikisUI wikiUI;
	//create API handlers
    private	APIWikisHandler wikiApiOwner;



	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		if (cfg.getTestConfig().serverIsMT())  {
			adminUser = cfg.getUserAllocator().getGroupUser("app_admin_users");
		} else {
			adminUser = cfg.getUserAllocator().getAdminUser();
		}
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		wikiApiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		shareUI = new ShareUICnx8(driver);
		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		homepageUI.addOnLoginScript(homepageUI.getCloseTourScript());
		uiCnx7 = new CustomizerUI(driver);
		commonUI = new CommonUICnx8(driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		commonUI.addOnLoginScript(commonUI.getCloseTourScript());
		forumUI = ForumsUI.getGui(cfg.getProductName(), driver);
		blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
		customizerUI = new CustomizerUI(driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Disable Share extension and verify Share extension is not
	 * visible</li>
	 * <li><B>Step:</B>Disable Share Team Extension in App registry</li>
	 * <li><B>Step:</B>Load homepage component and login into Application</li>
	 * <li><B>Verify:</B>Verify Share to Microsoft Teams is not seen in Dialog
	 * box</li>
	 * <li><B>JIRA Link:</B>
	 * https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T565</li>
	 * </ul>
	 * @throws IOException 
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyMSTeamShareDisabled() throws IOException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageUI.startTest();
		logger.strongStep("Load homepage ");
		log.info("INFO:Load homepage ");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login as " + adminUser.getEmail());
		log.info("INFO: Login as " + adminUser.getEmail() );
		uiCnx7.login(adminUser);
		
		logger.strongStep(" Load app Reg");
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO: Disable Share to Microsoft Team Extension");
		disableShareTeamExtension();

		logger.strongStep("Load homepage and Logout of application");
		log.info("INFO:Load homepage and Logout of application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();

		logger.strongStep("Load Homepage and Login as " + testUser.getEmail());
		log.info("INFO: Load Homepage and Login as " + testUser.getEmail() );
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click on Share Button to display Share Dialog");
		log.info("INFO: Click on Share Button to display Share Dialog");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.share), 2);
		
		// Check if Share dropdown options displayed
		if(shareUI.isElementDisplayedWd(By.xpath(ShareUIContants.shareInConnectionsDropDownOption))) {
			logger.strongStep("Click on Share In Connection option from dropdown");
			log.info("INFO: Click on Share In Connection option from dropdown");
			shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.shareInConnectionsDropDownOption), 2);
		}

		log.info("INFO:Verify Share Dialog visible");
		logger.strongStep("Verify Share Dialog visible");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.shareDialog), 3),
				"Share Dialog is visible");

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		logger.strongStep("End Test case");
		log.info("INFO: End Test case");

		homepageUI.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Enable Share extension and verify Share is working</li>
	 * <li><B>Step:</B>Enable Share Team Extension in App registry</li>
	 * <li><B>Step:</B>Load homepage component and login into Application</li>
	 * <li><B>Verify:</B>Verify Share to Microsoft Teams can be seen in Dialog
	 * box</li>
	 * <li><B>Verify:</B>A Share to Microsoft Teams pop up appears</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T566</li>
	 * <li><B>JIRA Link:</B>
	 * https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T569</li>
	 * </ul>
	 * @throws IOException 
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyMSTeamShareEnabled() throws IOException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Start Test");
		log.info("INFO: Start Test");

		homepageUI.startTest();
		logger.strongStep("Load homepage ");
		log.info("INFO:Load homepage ");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login as " + adminUser.getEmail());
		log.info("INFO: Login as " + adminUser.getEmail() );
		uiCnx7.login(adminUser);
		
		logger.strongStep(" Load app Reg");
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Enable Share to Microsoft Team Extension");
		log.info("INFO: Enable Share to Microsoft Team Extension ");
		enableShareTeamExtension();

		logger.strongStep("Load homepage, Logout of the application");
		log.info("INFO:Load homepage,   Logout of the application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();

		logger.strongStep(" login as " + testUser.getEmail() );
		log.info("INFO: login as " + testUser.getEmail() );
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Share Button to be visible");
		log.info("INFO: Wait for Share Button to be visible");
		shareUI.waitForElementsVisibleWd(By.xpath(ShareUIContants.shareDialogButtonWhenMSTeamsEnabled), 10);

		logger.strongStep("Click on Share Button to display Share Dialog");
		log.info("INFO: Click on Share Button to display Share Dialog");
		shareUI.clickLinkWd(shareUI.findElement(By.xpath(ShareUIContants.shareDialogButtonWhenMSTeamsEnabled)));

		logger.strongStep("Click on MS Team Share");
		log.info("INFO:Click on MS Team Share");
		shareUI.waitForElementVisibleWd(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption), 10);
		shareUI.scrollToElementWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));

		shareUI.clickLinkWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();

		logger.strongStep("Switch to new window for Microsoft authentication");
		log.info("INFO:Switch to new window for Microsoft authentication");
		shareUI.fluentWaitNumberOfWindowsEqual(2);
		shareUI.switchToNextWindowWd("teams");

		String currentUrl = ((WebDriver) driver.getBackingObject()).getCurrentUrl();
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		shareUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();

		logger.strongStep("Load Homepage , Login as " + adminUser.getEmail() );
		log.info("INFO: Load Homepage , login as " + adminUser.getEmail());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		uiCnx7.login(adminUser);

		logger.strongStep("Load app Reg" );
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true );

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO:Disable Share to Microsoft Team Extension ");
		disableShareTeamExtension();

		logger.strongStep("Load homepage, Logout of the application");
		log.info("INFO: Load homepage,  Logout of the application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();
		logger.strongStep("End Test Case");
		log.info("INFO: End Test Case ");

		homepageUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Enable Share extension and verify share option is visible on
	 * different connections pages e.g - Homepage, Communities, Profiles, Files,
	 * Blogs, Wikis etc.</li>
	 * <li><B>Step:</B>Enable Share Team Extension in App registry</li>
	 * <li><B>Step:</B>Load homepage component and login into Application</li>
	 * <li><b>Step:</b>Click on Share Button on these pages to display Share
	 * Dialog</li>
	 * <li><B>Verify:</B>Verify Share to Microsoft Teams can be seen in Dialog box
	 * on different connections pages e.g - Homepage, Communities, Profiles, Files,
	 * Blogs, Wikis etc.</li>
	 * <li><b>Step:</b>Click on Cross icon in Share dialog to close the dialog</li>
	 * <li><b>Step:</b>Verify Share dialog box is closed</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T560</li>
	 * </ul>
	 * @throws IOException 
	 */
	//TODO Cnx8ui :removed "FORUMS" from below list due to an issue of share window. Defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11475 is raised for the same
	//Removed "NOTIFICATIONS" from below list due to an issue https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11878
	//Removed "ACTIVITIES" due to 404 error. https://jira.cwp.pnp-hcl.com/browse/CNXTOOL-766 is already open for the same.
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyShareOnDiffCNXPages() throws IOException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		commonUI.startTest();

		logger.strongStep("Load homepage ");
		log.info("INFO:Load homepage ");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login as " + adminUser.getEmail());
		log.info("INFO: Login as " + adminUser.getEmail() );
		uiCnx7.login(adminUser);
		
		logger.strongStep(" Load app Reg");
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);


		logger.strongStep("Enable Share Team Extension");
		log.info("INFO: Enable Share Team Extension");
		enableShareTeamExtension();

		logger.strongStep("Load Homepage and Logout of the application");
		log.info("INFO: Load Homepage and  Logout of the application");
		commonUI.loadComponent(Data.getData().HomepageImFollowing, true);
		commonUI.logout();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing,true);
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
			log.info("INFO:  Verify " + app + " is selected in nav");
			cnxAssert.assertTrue(appNav.isAppSelected(commonUI), app + " is selected in navigation");

			logger.strongStep("Click on Share Button to display Share Dialog");
			log.info("INFO: Click on Share Button to display Share Dialog");
			shareUI.waitForElementsVisibleWd(By.xpath(ShareUIContants.shareDialogButtonWhenMSTeamsEnabled), 10);

			shareUI.clickLinkWd(shareUI.findElement(By.xpath(ShareUIContants.shareDialogButtonWhenMSTeamsEnabled)));

			logger.strongStep("Click on MS Team Share");
			log.info("INFO:Click on MS Team Share");
			shareUI.waitForElementVisibleWd(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption), 10);
			shareUI.scrollToElementWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));

			shareUI.clickLinkWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));
			String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();

			logger.strongStep("Switch to new window for Microsoft authentication");
			log.info("INFO:Switch to new window for Microsoft authentication");
			shareUI.fluentWaitNumberOfWindowsEqual(2);
			shareUI.switchToNextWindowWd("teams");

			logger.strongStep("Verify Redirected to MS Teams URL");
			log.info("INFO:  Verify Redirected to MS Teams URL");
			
			String currentUrl = ((WebDriver) driver.getBackingObject()).getCurrentUrl();
			cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

			logger.strongStep("Close Current Window ");
			log.info("INFO:Close Current Window");

			shareUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);
		}

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		commonUI.logout();

		uiCnx7.loadComponent(Data.getData().HomepageImFollowing, true);
		logger.strongStep("Login as " + adminUser.getEmail() );
		log.info("INFO: Login as " + adminUser.getEmail());
		uiCnx7.login(adminUser);

		logger.strongStep("Load app Reg" );
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true );

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO:Disable Share to Microsoft Team Extension ");

		disableShareTeamExtension();

		logger.strongStep("Load homepage and logout ");
		log.info("INFO: Load homepage and logout ");
		commonUI.loadComponent(Data.getData().HomepageImFollowing, true);
		commonUI.logout();

		logger.strongStep("End Test Case");
		log.info("INFO: End Test Case ");

		commonUI.endTest();

	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that MS Teams Share is working as
	 * expected for forum topics</li>
	 * <li><b>Step:</b>Create Forums topic using API</li>
	 * <li><b>Step:</b>Login to App Registry to enable Share extension</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><b>Step:</b>Login to Forums component</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Navigate to Forums topic page</li>
	 * <li><b>Step:</b>Click on Share Link against forum topic to display Share
	 * drop down</li>
	 * <li><b>Step:</b>Click on MS Teams Share from the drop down</li> 
	 * <li>Switch to new window for Microsoft authentication</li>
	 * <li>Verify Redirected to MS Teams URL</li>
	 * <li><b>Step:</b>Close current window and logout of the application</li>
	 * <li><b>Step:</b>Login to App Registry to disable Share extension</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T631</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2", "mt-exclude"}, enabled = true)
	public void verifyMSTeamShareOnForumsTopic() throws IOException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = forumUI.startTest();
		
		//Create forum using API
		APIForumsHandler forumsAPIUser = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		Forum standaloneForum = ForumEvents.createForum(testUser, forumsAPIUser, baseForum);

		// Create Forums topic using API
		logger.strongStep("Create Forums topic using API");
		log.info("Create Forums topic using API");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(),
				standaloneForum);
		ForumEvents.createForumTopic(testUser, forumsAPIUser, baseForumTopic);
		log.info("FORUM TOPIC ID --  " + baseForumTopic.getUUID());
		
		logger.strongStep("Load homepage ");
		log.info("INFO:Load homepage ");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login as " + adminUser.getEmail());
		log.info("INFO: Login as " + adminUser.getEmail() );
		uiCnx7.login(adminUser);
		
		logger.strongStep("Load App Registry");
		log.info("INFO: Load App Registry");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Enable Share to Microsoft Team Extension");
		log.info("INFO: Enable Share to Microsoft Team Extension ");
		enableShareTeamExtension();

		logger.strongStep("Load homepage, Logout of the application");
		log.info("INFO:Load homepage,   Logout of the application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();
		
		// Login to Forums component
		logger.strongStep("Load Forum Component, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("Load Forum Component, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		
		//TODO Cnx8UI : Added workaround to go to homepage and than load forum page due to defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11873
		forumUI.loadComponent(Data.getData().HomepageImFollowing,true);
		forumUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		forumUI.loadComponent(Data.getData().ComponentForums,true);

		// Select Forums topic's page
		logger.strongStep("Select Forums topic's page");
		log.info("Select Forums topic's page");
		forumUI.clickLinkWait(forumUI.getTopicSelector(baseForumTopic));

		logger.strongStep("Click on Share Link against forum topic to display Share dropdown");
		log.info("INFO: Click on Share Link against forum topic to display Share dropdown");
		shareUI.waitForElementVisibleWd(By.cssSelector(ForumsUIConstants.shareForumTopic), 10);
		shareUI.clickLinkWaitWd(By.cssSelector(ForumsUIConstants.shareForumTopic), 5);

		logger.strongStep("Click on MS Team Share");
		log.info("INFO:Click on MS Team Share");
		shareUI.waitForElementVisibleWd(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption), 10);
		shareUI.scrollToElementWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));

		shareUI.clickLinkWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();

		logger.strongStep("Switch to new window for Microsoft authentication");
		log.info("INFO:Switch to new window for Microsoft authentication");
		shareUI.fluentWaitNumberOfWindowsEqual(2);
		shareUI.switchToNextWindowWd("teams");

		String currentUrl = ((WebDriver) driver.getBackingObject()).getCurrentUrl();
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		shareUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		forumUI.logout();

		logger.strongStep("Load Homepage , Login as " + adminUser.getEmail() );
		log.info("INFO: Load Homepage , login as " + adminUser.getEmail());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		uiCnx7.login(adminUser);

		logger.strongStep("Load App Registry" );
		log.info("INFO: Load App Registry");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true );

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO:Disable Share to Microsoft Team Extension ");
		disableShareTeamExtension();

		logger.strongStep("Load homepage, Logout of the application");
		log.info("INFO: Load homepage,  Logout of the application");
		commonUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();
		logger.strongStep("End Test Case");
		log.info("INFO: End Test Case ");

		homepageUI.endTest();

	}

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that MS Teams Share is working as expected for blogs</li>
	 * <li><b>Step:</b>Create Blog Entry topic using API</li>
	 * <li><b>Step:</b>Login to App Registry to enable Share extension</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><b>Step:</b>Login to Blogs component</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Navigate to Public Blog Page</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Click on MS Teams Share from the drop down</li> 
	 * <li>Switch to new window for Microsoft authentication</li>
	 * <li>Verify Redirected to MS Teams URL</li>
	 * <li><b>Step:</b>Close current window and logout of the application</li>
	 * <li><b>Step:</b>Login to App Registry to disable Share extension</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T631</li>
	 * </ul>
	 */
	// TODO : Cnx8UI - Disabled the test case due to https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11773
	@Test(groups = { "cnx8ui-cplevel2", "mt-exclude"}, enabled = false)
	public void verifyMSTeamShareOnBlog() throws IOException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = "Test Blog" + Data.getData().commonName + Helper.genDateBasedRand();
		String randval = Helper.genDateBasedRandVal();
		String blogAddress = Data.getData().BlogsAddress1 + randval;

		blogUI.startTest();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, blogAddress).tags("Tag for " + testName + randval).description("Test description for testcase " + testName).timeZone(Time_Zone.Europe_London).theme(Theme.Blog_with_Bookmarks)
				.build();
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntryForShareFunctionality" + Helper.genDateBasedRand()).blogParent(blog).tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName).build();

		
		logger.strongStep("Login as Connections Admin and go to the Customizer: " + adminUser.getDisplayName());
		log.info("INFO: Login as Connections Admin and go to the Customizer: " + adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().ComponentCustomizer);
		customizerUI.login(adminUser);	
		
		logger.strongStep("Enable Share to Microsoft Team Extension");
		log.info("INFO: Enable Share to Microsoft Team Extension ");
		enableShareTeamExtension();

		log.info("There is no logout link so just clear cookies");
		logger.strongStep("There is no logout link so just clear cookies");
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();

		logger.strongStep("Create the first blog using API");
		log.info("INFO: Create first blog using API");
		Blog blogAPI = blog.createAPI(apiOwner);
		
		logger.strongStep("Create a blog post for this blog");
		log.info("INFO: Create a blog post for blog 1");
		blogEntry.createAPI(apiOwner, blogAPI);
		
		logger.strongStep("Open Blogs and login: " + testUser.getDisplayName());
		log.info("INFO: Load blogs component and login");
		blogUI.loadComponent(Data.getData().ComponentBlogs, true);
		blogUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to on Public Blog Page");
		log.info("INFO: Navigate to on Public Blog Page");
		blogUI.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Get entry id based on entry's title");
		log.info("INFO: Get entry id based on entry's title");
		String blogPostXpath = ShareUIContants.blogPostEntryDiv.replace("PLACEHOLDER", blogEntry.getTitle());
		shareUI.waitForElementVisibleWd(By.xpath(blogPostXpath),3);
		WebElement blogPostElement = shareUI.findElement(By.xpath(blogPostXpath));
		String entryid = blogPostElement.getAttribute("id").replace("entry-", "").replace(":link:entries", "");
		
		logger.strongStep("Click on Share Button");
		log.info("INFO: Click on Share Button");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.blogShareLink.replace("PLACEHOLDER", "shareWrapper_" + entryid)), 2);

		logger.strongStep("Click on MS Team Share");
		log.info("INFO:Click on MS Team Share");
		shareUI.waitForElementVisibleWd(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption), 10);
		shareUI.scrollToElementWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));

		shareUI.clickLinkWithJavaScriptWd(shareUI.findElement(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)));
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();

		logger.strongStep("Switch to new window for Microsoft authentication");
		log.info("INFO:Switch to new window for Microsoft authentication");
		shareUI.fluentWaitNumberOfWindowsEqual(2);
		shareUI.switchToNextWindowWd("teams");

		String currentUrl = ((WebDriver) driver.getBackingObject()).getCurrentUrl();
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		shareUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		blogUI.logout();

		logger.strongStep("Login as Connections Admin and go to the Customizer: " + adminUser.getDisplayName());
		log.info("INFO: Login as Connections Admin and go to the Customizer: " + adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.login(adminUser);

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO:Disable Share to Microsoft Team Extension ");
		customizerUI.disableAppFromAppReg(msTeamShareAppName);
		
		
		log.info("There is no logout link so just clear cookies");
		logger.strongStep("There is no logout link so just clear cookies");
		wd.manage().deleteAllCookies();
		logger.strongStep("End Test Case");
		log.info("INFO: End Test Case ");

		homepageUI.endTest();

	}
	
	/**
	* Enable Share extension 
	 * @throws IOException 
	*/
	private void enableShareTeamExtension() throws IOException {

		if (!isMSTeamShareEnabled()) {
			uiCnx7.enableAppFromAppReg(msTeamShareAppName);
		}

	}

	/**
	* Disable Share extension 
	 * @throws IOException 
	*/
	private void disableShareTeamExtension() throws IOException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Login as admin");
		log.info("INFO: Login as admin");

		if (isMSTeamShareEnabled()) {
			logger.strongStep("Step MS Team share Enabled in app reg");
			log.info("INFO: Verify MS Team share Enabled in app reg");

			uiCnx7.disableAppFromAppReg(msTeamShareAppName);
		}
	}

	/**
	 * Verify if MS Team extension is enabled
	 * @return true if MS Team extension is enabled, false if MS Team extension is disabled
	 * @throws IOException 
	 **/
	private boolean isMSTeamShareEnabled() throws IOException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Verify MS Team share configured in app reg");
		log.info("INFO: Verify MS Team share configured in app reg");

		boolean isMSTeamConfigured = uiCnx7
				.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", msTeamShareAppName)), 3);

		if (!isMSTeamConfigured) {
			logger.strongStep("Configure MS Team share in app reg");
			log.info("INFO: Configure MS Team share in app reg");

			try {
				configureMSTeamShare();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			logger.strongStep("Verify MS Teams Share extension Type is "+hclShareExtensionType);
			log.info("INFO: Verify MS Teams Share extension Type is "+hclShareExtensionType);
			if(!getExtensionType().equals(hclShareExtensionType)){
				logger.strongStep("Configure MS Team share in app reg");
				log.info("INFO: Configure MS Team share in app reg");
				configureMSTeamShare();
			}
			logger.strongStep("Verify MS Team share Enabled in app reg");
			log.info("INFO: Verify MS Team share Enabled in app reg");
		}

		uiCnx7.waitForClickableElementWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", msTeamShareAppName)), 10);

		if (uiCnx7.isAppEnabled(msTeamShareAppName)) 
			return true;
		else
			return false;
	}

	/**
	 * Configure MS Teams Share extension
	 * @throws IOException 
	 */

	private void configureMSTeamShare() throws IOException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		logger.strongStep("Create Admin Banner app, if not displayed");
		log.info("Info: Create Admin Banner app, if not displayed");
		if (uiCnx7.getAppByTitle(msTeamShareAppName) == null) {
			uiCnx7.createMSTeamShareApp(logger, msTeamShareAppName);
			uiCnx7.waitForElementsVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", msTeamShareAppName)), 5);
		}
		logger.strongStep("Verify MS Team Share is successfully updated on UI");
		log.info("Info: Verify  MS Team Share  is successfully updated on UI");
		cnxAssert.assertTrue(
				uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", msTeamShareAppName)), 3),
				"Configured MS Team Share verified");

	}

	

	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify MS Teams Share functionality for Wiki entry is working as expected</li>
	 * <li><b>Step:</b>Enable MS Teams Share</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Load Wiki Page</li>
	 * <li><b>Step:</b>Click on Share Button on wiki to display Share  Dialog</li>
	 * <li><b>Step:</b>Click on MS Teams Share  button from Share Dialog</li>
	 * <li>Verify New window for Microsoft Teams authentication should open up.</li>
	 * <li><b>Step:</b>Disable MS Teams Share</li>
	 * <li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T630</li>
	 * </ul>
	 * @throws IOException 
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyMSTeamsShareInWiki() throws IOException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName ="Test Wiki" +Data.getData().commonName + Helper.genDateBasedRand();
		BaseWiki wiki = new BaseWiki.Builder(testName  + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand())
				.description("Description for test " + testName)
				.build();
		
		homepageUI.startTest();
		logger.strongStep("Load homepage ");
		log.info("INFO:Load homepage ");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login as " + adminUser.getEmail());
		log.info("INFO: Login as " + adminUser.getEmail() );
		uiCnx7.login(adminUser);
		
		logger.strongStep(" Load app Reg");
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Enable Share Team Extension");
		log.info("INFO: Enable Share Team Extension");
		enableShareTeamExtension();

		logger.strongStep("Load Homepage and Logout of the application");
		log.info("INFO: Load Homepage and  Logout of the application");
		commonUI.loadComponent(Data.getData().HomepageImFollowing, true);
		commonUI.logout();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(wikiApiOwner);
		
		logger.strongStep("Load Wikipage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO:Load Wikipage, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		wikiUI.loadComponent(Data.getData().ComponentWikis, true);
		wikiUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on the Wiki created via API");
		log.info("INFO: Click on the Wiki created via API");
		wikiUI.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		wikiUI.waitForPageLoaded(driver);

		logger.strongStep("Wait for Share Button to be visible");
		log.info("INFO: Wait for Share Button to be visible");
		shareUI.waitForElementsVisibleWd(By.xpath(ShareUIContants.shareInMSTeamsInWiki), 5);

		logger.strongStep("Click on Share Button to display Share Dialog");
		log.info("INFO: Click on Share Button to display Share Dialog");
		shareUI.clickLinkWd(shareUI.findElement(By.xpath(ShareUIContants.shareInMSTeamsInWiki)));

		logger.strongStep("Click on MS Team Share");
		log.info("INFO:Click on MS Team Share");
		shareUI.findElements(By.xpath(ShareUIContants.shareWithMSTeamDropDownOption)).get(0).click();
		
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();

		logger.strongStep("Switch to new window for Microsoft authentication");
		log.info("INFO:Switch to new window for Microsoft authentication");
		shareUI.fluentWaitNumberOfWindowsEqual(2);
		shareUI.switchToNextWindowWd("teams");

		String currentUrl = ((WebDriver) driver.getBackingObject()).getCurrentUrl();
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		shareUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);

		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		logger.strongStep("Load Homepage , Login as " + adminUser.getEmail() );
		log.info("INFO: Load Homepage , login as " + adminUser.getEmail());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		uiCnx7.login(adminUser);

		logger.strongStep("Load app Reg" );
		log.info("INFO: Load app Reg");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true );

		logger.strongStep("Disable Share to Microsoft Team Extension");
		log.info("INFO:Disable Share to Microsoft Team Extension ");
		disableShareTeamExtension();

		logger.strongStep("Load homepage, Logout of the application");
		log.info("INFO: Load homepage,  Logout of the application");
		homepageUI.loadComponent(Data.getData().HomepageImFollowing, true);
		homepageUI.logout();
		
		logger.strongStep("End Test Case");
		log.info("INFO: End Test Case ");
		homepageUI.endTest();
	}


	
	/**
	 * Get the type of extension
	 * @return extType
	 * @throws IOException 
	 */
	public String getExtensionType() throws IOException {
		String extensionType="";
		uiCnx7.clickLinkWd(uiCnx7.findElement(By.xpath(ShareUIContants.msTeamsShareInAppReg)));
		uiCnx7.clickLinkWd(uiCnx7.findElement(By.xpath(ShareUIContants.extension)));
		extensionType=uiCnx7.getFirstVisibleElement(ShareUIContants.extensionType).getText();
		uiCnx7.clickLinkWd(uiCnx7.findElement(By.xpath(ShareUIContants.backToAppsButton)));
			
		return extensionType;
	}
	
	// CNXSERV-14338 This method is not used because the appreg is now created during deployment so we will keep it around
	public void deleteAppReg(ITestResult result)
	{
		log.info("Info: Invoke GET AppReg API to get the auth token");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), adminUser.getEmail(), adminUser.getPassword());
		Response resp = adminBannerService.getAppReg();

		log.info("Info: Invoke DELETE AppReg API to delete the App Registry");
		resp = adminBannerService.deleteAppRegistry("MS Teams Share Extension", resp.getHeader("authorization"));

		log.info("Info: Verify Status Code for Delete AppReg entry");	
		if(resp.getStatusCode()==HttpStatus.SC_NO_CONTENT)
		{
			log.info("AppReg entry is successfully deleted in AfterMethod");
		}
		else if(resp.getStatusCode()==HttpStatus.SC_NOT_FOUND)
		{
			log.info("AppReg entry is already deleted in Testcase");
		}
		else
		{
			log.warn("Status code of Delete api is " + resp.getStatusCode());
		}
	
	}
}
