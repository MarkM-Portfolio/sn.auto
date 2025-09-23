package com.ibm.conn.auto.tests.icec.xcc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

public class BVT_Level_2_Icec_AdminDashboard extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Icec_AdminDashboard.class);
	private IcecUI ui;
	private User testUser, testUserNew;
	private TestConfigCustom cfg;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getAdminUser();
		testUserNew = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user:" + testUser.getDisplayName());
	
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

	    // initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Global Settings page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Global Settings.
	*<li><B>Verify:</B> Check all labels, checkboxes and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashGlobalSettings() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);

		String icecAdminUrl = cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin;
		ui.loadUrlWithHttps(icecAdminUrl);
		if (!driver.getCurrentUrl().contains(icecAdminUrl)){
			ui.loadUrlWithHttps(icecAdminUrl);
		}
		
		checkLabelAndCheckboxPresent(IcecUI.cachingForAdminsLabel, IcecUI.cachingForAdminsCheckbox, "Caching for Admins");
		checkLabelAndCheckboxPresent(IcecUI.openCommLinksInNewTabLabel, IcecUI.openCommLinksInNewTabCheckbox, "Open Community-Links");
		checkLabelAndCheckboxPresent(IcecUI.allowUrlDebuggingLabel, IcecUI.allowUrlDebuggingCheckbox, "Allow URL Debugging");
		checkLabelAndCheckboxPresent(IcecUI.enableNewIcecStylesLabel, IcecUI.enableNewIcecStylesCheckbox, "Enable new ICEC styles");
		
		logger.strongStep("Check Global Settings labels and buttons are present");
		log.info("INFO: Check Global Settings labels and buttons are present");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.externalImagesLabel),
				"External Images setting not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.enforceHttpsBtn),
				"Enforce Https button not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.useProxyBtn),
				"Use Proxy button not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.allowHttpBtn),
				"Allow Http button not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");	
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Context Roots page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Context Roots.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashContextRoots() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#ContextRoots");
		
		String[] componentLabels = {"activities", "blogs", "bookmarks", "common", "communities", "files",
				"forums", "help", "homepage", "metrics", "mobile", "mobileadmin", "moderation", "news",
				"profiles", "proxy", "search", "widgetscontainer", "wikis"};
		
		checkAdminDashLabelsPresent(componentLabels);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Connections Environment page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Connections Environment.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashConnectionsEnv() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#ConnectionsEnvironment");
		
		String[] settingsLabels = {"customizationpath", "ltpaTokenName"};
		
		checkAdminDashLabelsPresent(settingsLabels);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Anonymous Access page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Anonymous Access.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashAnonymousAccess() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#Anonymous");
		
		String[] labels = {"runasuser", "password"};
		checkAdminDashLabelsPresent(labels);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Backup page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Backup.
	*<li><B>Verify:</B> Check all labels, checkboxes and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashBackup() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#Backup");
		
		checkLabelAndCheckboxPresent(IcecUI.getAdminDashLabel("enablebackup"), IcecUI.enableBackupCheckbox, "Enable Backup");
		
		logger.strongStep("Check Backup label and dropdown are present");
		log.info("INFO: Check Backup label and dropdown are present");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getAdminDashLabel("backupinterval")),
				"Backup Interval label not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.backupIntervalDropdown),
				"Backup Interval dropdown not found");
		
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Custom Properties page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Custom Properties.
	*<li><B>Step:</B> Click Add Custom Property.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashCustomProperties() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#TenantProperties");
		
		logger.strongStep("Click Add Custom Property and check labels");
		log.info("INFO: Click Add Custom Property and check labels");
		ui.clickLink(IcecUI.addCustomPropertyBtn);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.customPropertiesKeyLabel),
				"Key label not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.customPropertiesValueLabel),
				"Value not found");
		
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Language Files page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Language Files.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashLanguageFiles() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#LanguageFiles");
		
		logger.strongStep("Check Language Files labels and buttons");
		log.info("INFO: Check Language Files labels and buttons");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.languageFilesList),
				"Language Files list is not found or empty");		
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.reInitLanguageFilesBtn),
				"ReInit Language Files button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Integrations page is as expected.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to Admin Dashboard Integrations.
	*<li><B>Verify:</B> Check all labels and buttons are displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateDashIntegrations() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the Homepage component, login and load the ICEC component
		logger.strongStep("Login to ICEC via the Homepage and load Admin Dashboard");
		log.info("INFO: Logging in the ICEC via the Homepage with user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcecAdmin + "#Integrations");
		
		String[] labels = {"allowIntegrations"};
		checkAdminDashLabelsPresent(labels);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.allowIntegrationsSlider),
				"Allow Integrations Slider not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.saveBtn),
				"Save button not found");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate Admin Dashboard Connections Environment page has error if not logged in as the admin user.
	*<li><B>Step:</B> Load the ICEC Admin Dashboard without logging in.
	*<li><B>Verify:</B> The error page appears when the URL 'xcc/admin' is loaded without logging in.
	*<li><B>Step:</B> Click on the 'Back to homepage' link.
	*<li><B>Verify:</B> The login page for Homepage is visible.
	*<li><B>Step:</B> Login to the Homepage component as a user that is not the admin user.
	*<li><B>Step:</B> Load the ICEC Admin Dashboard.
	*<li><B>Verify:</B> The error page is displayed when the URL 'xcc/admin' is loaded for a user that is not the admin user.
	*<li><B>Step:</B> Click on the 'Back to homepage' link.
	*<li><B>Verify:</B> The Homepage is loaded.
	*<li><B>Step:</B> Logout and login to the Homepage component as the admin user.
	*<li><B>Step:</B> Load the ICEC Admin Dashboard.
	*<li><B>Verify:</B> The Admin Dashboard loads properly for the admin user.
	*<li><B>Verify:</B> The title of the page does not reflect the error.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void validateErrorMessage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load the ICEC Admin Dashboard without logging in
		logger.strongStep("Load the ICEC Admin Dashboard without logging in");
		log.info("INFO: Load the ICEC Admin Dashboard without logging in");
		ui.loadComponent(Data.getData().ComponentIcecAdmin);
		
		logger.strongStep("Verify that the error page appears when the URL 'xcc/admin' is loaded without logging in");
		log.info("INFO: Verify that the error page appears when the URL 'xcc/admin' page is loaded without logging in");
		Assert.assertTrue(ui.isCECErrorPresent(),
				"Error page is displayed when 'xcc/admin' page is loaded without logging in");
		
		logger.strongStep("Click on the 'Back to homepage' link");
		log.info("INFO: Click on the 'Back to homepage' link");
		ui.clickLinkWait(IcecUI.backToHomepage);
		
		logger.strongStep("Verify that the login page for Homepage is visible");
		log.info("INFO: Verify that the login page for Homepage is visible");
		Assert.assertTrue(driver.getCurrentUrl().contains("/homepage/login"));
		Assert.assertTrue(ui.fluentWaitElementVisible(BaseUIConstants.USERNAME_FIELD) &&
				ui.fluentWaitElementVisible(BaseUIConstants.Password_FIELD));
		
		ui.close(cfg);
		
		logger.strongStep("Login to the Homepage component as a user that is not the admin user");
		log.info("INFO: Login to the Homepage component as a user that is not the admin user");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUserNew);
		ui.fluentWaitElementVisible(HomepageUIConstants.leftNavMenu);

		logger.strongStep("Load the ICEC Admin Dashboard");
		log.info("INFO: Load the ICEC Admin Dashboard");
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);

		logger.strongStep("Verify that the error page is displayed when the URL 'xcc/admin' is loaded for a user that is not the admin user");
		log.info("INFO: Verify that the error page is displayed when the URL 'xcc/admin' is loaded for a user that is not the admin user");
		Assert.assertTrue(ui.isCECErrorPresent(),
				"Error page is displayed when the URL 'xcc/admin' is loaded for a user that is not the admin user");
		Assert.assertEquals(driver.getTitle(), "Error occurred - NotEntitledException",
				"The title of the 'xcc/admin' page is not 'Error occurred - NotEntitledException'");

		logger.strongStep("Click on the 'Back to homepage' link");
		log.info("INFO: Click on the 'Back to homepage' link");
		ui.clickLinkWait(IcecUI.backToHomepage);

		logger.strongStep("Verify that the Homepage is loaded");
		log.info("INFO: Verify that the Homepage is loaded");
		Assert.assertTrue(driver.getTitle().equals("HCL Connections Home Page - Updates") &&
				ui.fluentWaitElementVisible(HomepageUIConstants.leftNavMenu),
				"The Homepage loaded after clicking on the 'Back to homepage' link");

		logger.strongStep("Logout as the user: " + testUserNew.getDisplayName());
		log.info("INFO: Logout as the user: " + testUserNew.getDisplayName());
		ui.logout();
		
		logger.strongStep("Login to the Homepage component as the admin user");
		log.info("INFO: Login to the Homepage component as the admin user");
		ui.login(testUser);
		ui.fluentWaitElementVisible(HomepageUIConstants.leftNavMenu);

		logger.strongStep("Load the ICEC Admin Dashboard");
		log.info("INFO: Load the ICEC Admin Dashboard");
		ui.loadComponent(Data.getData().ComponentIcecAdmin, true);

		logger.strongStep("Verify that the Admin Dashboard loads properly for the admin user");
		log.info("INFO: Verify that the Admin Dashboard loads properly for the admin user");
		checkLabelAndCheckboxPresent(IcecUI.cachingForAdminsLabel, IcecUI.cachingForAdminsCheckbox, "Caching for Admins");
		checkLabelAndCheckboxPresent(IcecUI.openCommLinksInNewTabLabel, IcecUI.openCommLinksInNewTabCheckbox, "Open Community-Links");
		checkLabelAndCheckboxPresent(IcecUI.allowUrlDebuggingLabel, IcecUI.allowUrlDebuggingCheckbox, "Allow URL Debugging");
		checkLabelAndCheckboxPresent(IcecUI.enableNewIcecStylesLabel, IcecUI.enableNewIcecStylesCheckbox, "Enable new ICEC styles");

		logger.strongStep("Verify that the title of the page does not reflect the error");
		log.info("INFO: Verify that the title of the page does not reflect the error");
		Assert.assertEquals(driver.getTitle(), "Highlights - Admin Dashboard",
				"The title of the 'xcc/admin' page is not 'Highlights - Admin Dashboard'");
		
		ui.endTest();
		
	}
	
	private void checkLabelAndCheckboxPresent(String labelSelector, String checkboxSelector, String labelText) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Check label " + labelSelector + " and checkbox " + checkboxSelector + " are present");
		log.info("INFO: Check label " + labelSelector + " and checkbox " + checkboxSelector + " are present");
		Assert.assertTrue(ui.fluentWaitPresent(labelSelector),
				labelText + " label not found");
		Assert.assertTrue(ui.fluentWaitPresent(checkboxSelector),
				labelText + " checkbox not found");
	}
	
	private void checkAdminDashLabelsPresent(String[] labels) {
		for (String label : labels) {
			String selector = IcecUI.getAdminDashLabel(label);
			DefectLogger logger = dlog.get(Thread.currentThread().getId());
			logger.strongStep("Check label " + label + " is present");
			log.info("INFO: Check label " + label + " is present");
			Assert.assertTrue(ui.fluentWaitPresent(selector),
					label + " label not found");
		}	
	}	
}
