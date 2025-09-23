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

public class BVT_Cnx8UI_Homepage_Navigation extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_Navigation.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, adminUser;
	private HomepageUI ui;
	private CustomizerUI customizerUI;
	String appRegAppName = "Connections Navigation";

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
	*<li><B>Info:</B>Create, Disable and Enable navigation bar app via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete navigation bar App, if already created</li>
	*<li><B>Step:</B>Create the navigation bar App from App Reg</li>
	*<li><B>Verify:</B>Verify navigation bar Config via GET api call</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the navigation bar App from App Reg</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully disabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify default navigation bar app on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T651</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCreateEnableDisableConnectionsNavigationViaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		customizerUI.startTest();
		
		logger.strongStep("Load Customizer and login: " +adminUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().HomepageImFollowing);
		customizerUI.login(adminUser);
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);

		
		logger.strongStep("Delete Connections Navigation App, if already created");
		log.info("Info: Delete Connections Navigation App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Create Connections Navigation App");
		log.info("Info: Create Connections Navigation App");
		customizerUI.createAppViaAppReg(appRegAppName);
				
		logger.strongStep("Invoke a GET API for Connections Navigation app config");
		log.info("Info: Calling GET API for Connections Navigation app config");
		ConnectionsNavigationService connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = connectionsNavigationService.getConnectionsNavConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Navigation app");
		log.info("Info: Verify api status code and response of Connections Navigation app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Navigation Config Status Code");

		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-nav","Verify Connections Navigation App via api");
		
		logger.strongStep("Verify Connections Navigation App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Connections Navigation App is successfully displayed on App Reg UI.");
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(customizerUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Navigation is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Navigation App is successfully enabled on UI");
		log.info("Info: Verify Connections Navigation App is successfully enabled on UI");
		cnxAssert.assertTrue(customizerUI.isElementVisibleWd(By.xpath(HomepageUIConstants.navigationBarText.replace("PLACEHOLDER", "Customer Intranet")), 10), "Connections Navigation text is verified");
		
		logger.strongStep("Disable the Connections Navigation App");
		log.info("Info: Disable the Connections Navigation App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Navigation App is successfully disabled");
		log.info("Info: Verify Connections Navigation App is successfully disabled");
		cnxAssert.assertFalse(customizerUI.isElementVisibleWd(By.xpath(HomepageUIConstants.navigationBarText), 3),"Connections Navigation is disabled");
				
		logger.strongStep("Deleting Connections Navigation App");
		log.info("Info: Deleting Connections Navigation App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}	

		customizerUI.endTest();
	}
		
	/**
	*<ul>
	*<li><B>Info:</B>Create, Disable and Enable navigation bar app via App Registry extension With cache Expiration.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete navigation bar App, if already created</li>
	*<li><B>Step:</B>Create the navigation bar App from App Reg</li>
	*<li><B>Verify:</B>Verify navigation bar Config via GET api call</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Update the navigation bar App from App Reg</li>
	*<li><B>Verify:</B>Verify updated navigation bar app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the navigation bar App from App Reg</li>
	*<li><B>Verify:</B>Verify navigation bar app successfully disabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify default navigation bar app on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T660</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCreateEnableDisableConnectionsNavigationViaAppRegWithCacheExpiration() throws IOException, InterruptedException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		customizerUI.startTest();
		
		logger.strongStep("Load Hompeage I am following and login: " +adminUser.getDisplayName());
		log.info("Info: Load Hompeage I am following and login: " +adminUser.getDisplayName());
		customizerUI.loadComponent(Data.getData().HomepageImFollowing);
		customizerUI.login(adminUser);
		logger.strongStep("Load Customizer");
		log.info("Info: Load Customizer");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);
	
		logger.strongStep("Delete Connections Navigation App, if already created");
		log.info("Info: Delete Connections Navigation App, if already created");
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Create Connections Navigation App with Cache Expiration");
		log.info("Info: Create Connections Navigation App with Cache Expiration");
		customizerUI.createAppViaAppReg(appRegAppName+" Cache");
				
		logger.strongStep("Invoke a GET API for Connections Navigation app config");
		log.info("Info: Calling GET API for Connections Navigation app config");
		ConnectionsNavigationService connectionsNavigationService = new ConnectionsNavigationService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = connectionsNavigationService.getConnectionsNavConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Connections Navigation app");
		log.info("Info: Verify api status code and response of Connections Navigation app");
		connectionsNavigationService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Connections Navigation Config Status Code");

		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-nav","Verify Connections Navigation App via api");
		
		logger.strongStep("Verify Connections Navigation App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Connections Navigation App is successfully displayed on App Reg UI.");
		customizerUI.waitForElementsVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(customizerUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Navigation is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Navigation App is successfully enabled on UI");
		log.info("Info: Verify Connections Navigation App is successfully enabled on UI");
		cnxAssert.assertTrue(customizerUI.isElementVisibleWd(By.xpath(HomepageUIConstants.navigationBarText.replace("PLACEHOLDER", "Customer Intranet")), 10), "Connections Navigation text is verified");
		
		logger.strongStep("Load Customizer");
		log.info("Info: Load Customizer");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Click on Connection Navigation App");		
		log.info("Info: Click on Connection Navigation App");
		customizerUI.clickLinkWaitWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),5, "Click and open connection navigation app");
		
		String jsonString = customizerUI.updateNavigationBarAppJsonWithCache();
		String successMsg = "Saved updated changes to application '" + appRegAppName + "'";
		logger.strongStep("Prepare and import updated json file.");		
		log.info("Info: Prepare and import updated json file.");		
		customizerUI.importJsonFileAndSaveApp(jsonString, successMsg);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
			
		logger.strongStep("Verify Connections Navigation App is successfully updated and enabled on UI");
		log.info("Info: Verify Connections Navigation App is successfully updated and enabled on UI");
		cnxAssert.assertTrue(customizerUI.isElementVisibleWd(By.xpath(HomepageUIConstants.navigationBarText.replace("PLACEHOLDER", "Customer Intranet1")), 10), "Updated Connections Navigation text is verified");
		
		logger.strongStep("Load Customizer and then disable the Connections Navigation App");
		log.info("Info: Load Customizer and then disable the Connections Navigation App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		customizerUI.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		customizerUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Connections Navigation App is successfully disabled");
		log.info("Info: Verify Connections Navigation App is successfully disabled");
		cnxAssert.assertFalse(customizerUI.isElementVisibleWd(By.xpath(HomepageUIConstants.navigationBarText), 3),"Connections Navigation is disabled");
				
		logger.strongStep("Deleting Connections Navigation App");
		log.info("Info: Deleting Connections Navigation App");
		customizerUI.loadComponent(Data.getData().ComponentCustomizer, true);
		if(customizerUI.isElementVisibleWd(By.xpath(customizerUI.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			customizerUI.deleteAppFromView(appRegAppName, appRegAppName);		
		}	

		customizerUI.endTest();
	}
	
	@AfterMethod(alwaysRun=true)
	public void deleteAppReg(ITestResult result)
	{
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
