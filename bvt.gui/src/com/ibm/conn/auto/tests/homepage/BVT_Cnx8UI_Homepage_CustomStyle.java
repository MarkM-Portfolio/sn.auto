package com.ibm.conn.auto.tests.homepage;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.services.AdminBannerService;
import com.hcl.lconn.automation.framework.services.ConnectionsNavigationService;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BVT_Cnx8UI_Homepage_CustomStyle extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_CustomStyle.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, adminUser;
	private HomepageUI ui;
	private CustomizerUI customizerUI;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		if (cfg.getTestConfig().serverIsMT())  {
			adminUser = cfg.getUserAllocator().getGroupUser("app_admin_users");
		} else {
			adminUser = cfg.getUserAllocator().getAdminUser();
		}
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		customizerUI = new CustomizerUI(driver);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create, Disable and Enable Connections Custom Style - Fiesta app via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Fiesta App, if already created</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Dark App, if already created</li>
	*<li><B>Step:</B>Create the Connections Custom Style - Fiesta App from App Reg</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the Connections Custom Style - Fiesta App from App Reg</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully disabled on App Reg UI</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Fiesta App</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully removed on App Reg UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T658</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCreateEnableDisableConnectionsCustomStyleFiestaViaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String appRegAppName = "Connections Custom Style - Fiesta";
		customizerUI.startTest();
		
		logger.strongStep("Load Customizer and login: " +adminUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().HomepageImFollowing);
		customizerUI.login(adminUser);
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Delete Connections Custom Style - Fiesta App, if already created");
		log.info("Info: Delete Connections Custom Style - Fiesta App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Delete Connections Custom Style - Dark App, if already created");
		log.info("Info: Delete Connections Custom Style - Dark App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", "Connections Custom Style - Dark")),3)) {			
			customizerUI.deleteAppFromView("Connections Custom Style - Dark", "Connections Custom Style - Dark");		
		}
		
		logger.strongStep("Create Connections Custom Style - Fiesta App");
		log.info("Info: Create Connections Custom Style - Fiesta App");
		customizerUI.createAppViaAppReg(appRegAppName);
				
		logger.strongStep("Invoke a GET API for Connections Custom Style - Fiesta app config");
		log.info("Info: Calling GET API for Connections Custom Style - Fiesta app config");
		ConnectionsNavigationService connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Fiesta app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Fiesta app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Fiesta Config Status Code");

		logger.strongStep("Verify item name via api");
		log.info("Info: Verify item name via api");
		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-custom-style-fiesta","Verify Connections Custom Style - Fiesta App via api");
		
		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully displayed on App Reg UI.");
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(customizerUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Custom Style - Fiesta is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully enabled on UI");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully enabled on UI");
		customizerUI.waitForPageLoaded(driver);
		customizerUI.waitForElementVisibleWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition), 4);
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition)), "Top Navigation logo is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarPosition)), "Top Navigation Search Bar is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionTextPosition)), "Top Navigation Text is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionActionPosition)), "Top Navigation Actions is verified");
		
		logger.strongStep("Disable the Connections Custom Style - Fiesta App");
		log.info("Info: Disable the Connections Custom Style - Fiesta App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Invoke a GET API for Connections Custom Style - Fiesta app config");
		log.info("Info: Calling GET API for Connections Custom Style - Fiesta app config");
		connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Fiesta app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Fiesta app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Fiesta Config Status Code");

		logger.strongStep("Verify item name via api after disabled app");
		log.info("Info: Verify item name via api after disabled app");
		json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), null, "Verify Connections Custom Style - Fiesta App via api after disabled app");
				
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully disabled");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully disabled");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition)), "Top Navigation logo is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarPosition)), "Top Navigation Search Bar is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionTextPosition)), "Top Navigation Text is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionActionPosition)), "Top Navigation Actions is verified");
		
		logger.strongStep("Deleting Connections Custom Style - Fiesta App");
		log.info("Info: Deleting Connections Custom Style - Fiesta App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		

		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully removed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully removed on App Reg UI.");
		cnxAssert.assertFalse(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3),"Connections Custom Style - Fiesta is removed successfully.");		
		
		customizerUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create, Disable and Enable Connections Custom Style - Dark app via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Dark App, if already created</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Fiesta App, if already created</li>
	*<li><B>Step:</B>Create the Connections Custom Style - Dark App from App Reg</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the Connections Custom Style - Dark App from App Reg</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark app successfully disabled on App Reg UI</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Dark App</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Dark app successfully removed on App Reg UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T657</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCreateEnableDisableConnectionsCustomStyleDarkViaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String appRegAppName = "Connections Custom Style - Dark";
		customizerUI.startTest();
		
		logger.strongStep("Load Customizer and login: " +adminUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().HomepageImFollowing);
		customizerUI.login(adminUser);
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Delete Connections Custom Style - Dark App, if already created");
		log.info("Info: Delete Connections Custom Style - Dark App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Delete Connections Custom Style - Fiesta App, if already created");
		log.info("Info: Delete Connections Custom Style - Fiesta App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", "Connections Custom Style - Fiesta")),3)) {			
			customizerUI.deleteAppFromView("Connections Custom Style - Fiesta", "Connections Custom Style - Fiesta");		
		}
		
		logger.strongStep("Create Connections Custom Style - Dark App");
		log.info("Info: Create Connections Custom Style - Dark App");
		customizerUI.createAppViaAppReg(appRegAppName);
				
		logger.strongStep("Invoke a GET API for Connections Custom Style - Dark app config");
		log.info("Info: Calling GET API for Connections Custom Style - Dark app config");
		ConnectionsNavigationService connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Dark app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Dark app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Dark Config Status Code");

		logger.strongStep("Verify item name via api");
		log.info("Info: Verify item name via api");
		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-custom-style-dark", "Verify Connections Custom Style - Dark App via api");
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully displayed on App Reg UI.");
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(customizerUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(), "Connections Custom Style - Dark is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		customizerUI.waitForPageLoaded(driver);
		customizerUI.waitForElementVisibleWd(customizerUI.findElement(By.xpath(HomepageUIConstants.wrapperStyleHomepage)), 10);
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully enabled on UI");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully enabled on UI");
		String innerText = customizerUI.findElement(By.xpath(HomepageUIConstants.cnx8CustomStyles)).getAttribute("innerText");
		cnxAssert.assertTrue(innerText!=null && !(innerText.isEmpty()) , "Connections Custom Style - Dark text is verified");
		
		logger.strongStep("Disable the Connections Custom Style - Dark App");
		log.info("Info: Disable the Connections Custom Style - Dark App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Invoke a GET API for Connections Custom Style - Dark app config");
		log.info("Info: Calling GET API for Connections Custom Style - Dark app config");
		connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Dark app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Dark app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Dark Config Status Code");

		logger.strongStep("Verify item name via api after disabled app");
		log.info("Info: Verify item name via api after disabled app");		
		json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), null,"Verify Connections Custom Style - Dark App via api after disbabled");		
				
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		customizerUI.waitForPageLoaded(driver);
		customizerUI.waitForElementVisibleWd(customizerUI.findElement(By.xpath(HomepageUIConstants.wrapperStyleHomepage)), 10);
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully disabled");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully disabled");
		innerText = customizerUI.findElement(By.xpath(HomepageUIConstants.cnx8CustomStyles)).getAttribute("innerText");
		log.info("text " + innerText);
		cnxAssert.assertFalse(innerText!=null && !(innerText.isEmpty()) , "Connections Navigation is disabled");

		logger.strongStep("Deleting Connections Custom Style - Dark App");
		log.info("Info: Deleting Connections Custom Style - Dark App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully removed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully removed on App Reg UI.");
		cnxAssert.assertFalse(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 3), "Connections Custom Style - Dark is removed successfully.");

		customizerUI.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Create,Update  Connections Custom Style - Fiesta app via App Registry extension with cache Expiration.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Fiesta App, if already created</li>
	*<li><B>Step:</B>Delete Connections Connections Custom Style - Dark, if already created</li>
	*<li><B>Step:</B>Create the Connections Custom Style - Fiesta App from App Reg with cache</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Update the Connections Custom Style - Fiesta App from App Reg with new cache</li>
	*<li><B>Verify:</B>Verify Updated Connections Custom Style - Fiesta app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the Connections Custom Style - Fiesta App from App Reg</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta Config via GET api call</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully disabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully disabled on Homepage UI</li>
	*<li><B>Step:</B>Delete Connections Custom Style - Fiesta App</li>
	*<li><B>Verify:</B>Verify Connections Custom Style - Fiesta app successfully removed on App Reg UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T661</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyConnectionsCustomStyleFiestaViaAppRegWithCacheExpiration() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String appRegAppName = "Connections Custom Style - Fiesta";
		customizerUI.startTest();
		
		logger.strongStep("Load Customizer and login: " +adminUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().HomepageImFollowing);
		customizerUI.login(adminUser);
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);

		logger.strongStep("Delete Connections Custom Style - Fiesta App, if already created");
		log.info("Info: Delete Connections Custom Style - Fiesta App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Delete Connections Connections Custom Style - Dark, if already created");
		log.info("Info: Delete Connections Connections Custom Style - Dark, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", "Connections Custom Style - Dark")),3)) {			
			customizerUI.deleteAppFromView("Connections Custom Style - Dark", "Connections Custom Style - Dark");		
		}
		
		logger.strongStep("Create Connections Custom Style - Fiesta App with Cache Expiration");
		log.info("Info: Create Connections Custom Style - Fiesta App with Cache Expiration");
		customizerUI.createAppViaAppReg(appRegAppName+" Cache");
				
		logger.strongStep("Invoke a GET API for Connections Custom Style - Fiesta app config");
		log.info("Info: Calling GET API for Connections Custom Style - Fiesta app config");
		ConnectionsNavigationService connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Fiesta app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Fiesta app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Fiesta Config Status Code");

		logger.strongStep("Verify item name via api");
		log.info("Info: Verify item name via api");
		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-custom-style-fiesta","Verify Connections Custom Style - Fiesta App via api");
		
		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully displayed on App Reg UI.");
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(customizerUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Custom Style - Fiesta is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully enabled on UI");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully enabled on UI");
		customizerUI.waitForPageLoaded(driver);
		customizerUI.waitForElementVisibleWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition), 4);
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition)), "Top Navigation logo is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarPosition)), "Top Navigation Search Bar is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionTextPosition)), "Top Navigation Text is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionActionPosition)), "Top Navigation Actions is verified");
		
		logger.strongStep("Load Customizer");
		log.info("Info: Load Customizer");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Click on Connection Navigation App");		
		log.info("Info: Click on Connection Navigation App");
		customizerUI.clickLinkWaitWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),5, "Click and open connection navigation app");
		
		String jsonString = customizerUI.updateConnectionsCustomStyleFiestaAppJsonWithCache();
		String successMsg = "Saved updated changes to application '" + appRegAppName + "'";
		logger.strongStep("Prepare and import updated json file.");		
		log.info("Info: Prepare and import updated json file.");		
		customizerUI.importJsonFileAndSaveApp(jsonString, successMsg);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully enabled and updated on UI");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully enabled and updated on UI");
		customizerUI.waitForPageLoaded(driver);
		customizerUI.waitForElementVisibleWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarRightPosition), 4);
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionLogoLeftPosition)), "Top Navigation logo is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarRightPosition)), "Top Navigation Search Bar is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionTextLeftPosition)), "Top Navigation Text is verified");
		cnxAssert.assertTrue(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionActionCenterPosition)), "Top Navigation Actions is verified");
			
		logger.strongStep("Disable the Connections Custom Style - Fiesta App");
		log.info("Info: Disable the Connections Custom Style - Fiesta App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Invoke a GET API for Connections Custom Style - Fiesta app config");
		log.info("Info: Calling GET API for Connections Custom Style - Fiesta app config");
		connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		resp = connectionsNavigationService.getConnectionsCustomStyleConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Custom Style - Fiesta app");
		log.info("Info: Verify api status code and response of Connections Custom Style - Fiesta app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Custom Style - Fiesta Config Status Code");

		logger.strongStep("Verify item name via api after disabled app");
		log.info("Info: Verify item name via api after disabled app");
		json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), null, "Verify Connections Custom Style - Fiesta App via api after disabled app");
				
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Custom Style - Dark App is successfully disabled");
		log.info("Info: Verify Connections Custom Style - Dark App is successfully disabled");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionLogoPosition)), "Top Navigation logo is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionSearchBarPosition)), "Top Navigation Search Bar is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionTextPosition)), "Top Navigation Text is verified");
		cnxAssert.assertFalse(customizerUI.isElementPresentWd(By.xpath(HomepageUIConstants.topNaviagtionActionPosition)), "Top Navigation Actions is verified");
				
		logger.strongStep("Deleting Connections Custom Style - Fiesta App");
		log.info("Info: Deleting Connections Custom Style - Fiesta App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		

		logger.strongStep("Verify Connections Custom Style - Fiesta App is successfully removed on App Reg UI.");
		log.info("Info: Verify Connections Custom Style - Fiesta App is successfully removed on App Reg UI.");
		cnxAssert.assertFalse(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3),"Connections Custom Style - Fiesta is removed successfully.");		
		
		customizerUI.endTest();
	}
	
	@AfterMethod(alwaysRun=true)
	public void deleteAppReg(ITestResult result)
	{

		String appRegAppName="" ;
		if(result.getName().contains("Fiesta"))
		{
			appRegAppName = "Connections Custom Style - Fiesta";;
		}
		else if(result.getName().contains("Dark"))
		{
			appRegAppName = "Connections Custom Style - Dark";;

		}

		log.info("Info: Invoke GET AppReg API to get the auth token");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), adminUser.getEmail(), adminUser.getPassword());
		Response resp = adminBannerService.getAppReg();

		log.info("Info: Invoke DELETE AppReg API to delete the App Registry");
		resp = adminBannerService.deleteAppRegistry(appRegAppName,resp.getHeader("authorization"));

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
