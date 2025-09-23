package com.ibm.conn.auto.tests.homepage;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.payload.AdminBannerResponse;
import com.hcl.lconn.automation.framework.services.AdminBannerService;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BVT_Cnx8UI_Homepage_AdminBanner extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_AdminBanner.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;	
	private User testUser;
	private CustomizerUI uiCnx7;
	private String appRegAppName = "Connections Banner";;

	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();

		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser("app_admin_users");
		} else {
			testUser = cfg.getUserAllocator().getAdminUser();
		}
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		uiCnx7 = new CustomizerUI(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create, Disable and Enable admin banner app via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Admin Banner App, if already created</li>
	*<li><B>Step:</B>Create the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner Config via GET api call</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully disabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app not displayed on Homepage New UI</li>
	*<li><B>Step:</B>Enable the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T580</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T571</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T573</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCreateDisableEnableAdminBannerViaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Load Customizer and login: " +testUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +testUser.getDisplayName());
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		uiCnx7.login(testUser);
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Delete Admin Banner App, if already created");
		log.info("Info: Delete Admin Banner App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Create Admin Banner App");
		log.info("Info: Create Admin Banner App");
		uiCnx7.createAppViaAppReg(appRegAppName);
				
		logger.strongStep("Invoke a GET API for Admin banner app config");
		log.info("Info: Calling GET API for Admin banner app config");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.getAdminBannerConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Admin banner app");
		log.info("Info: Verify api status code and response of Admin banner app");
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Admin Banner Config Status Code");

		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-banner","Verify value for name key");
		cnxAssert.assertEquals(json.get("items[0].payload.open"), true,"Verify value for name open");
		cnxAssert.assertEquals(json.get("items[0].payload.message"), Arrays.asList("This is the HCL Connections Banner"),"Verify value for name message");
		cnxAssert.assertEquals(json.get("items[0].payload.severity"), "success","Verify value for name severity");

		logger.strongStep("Verify Admin Banner App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Admin Banner App is successfully displayed on App Reg UI.");
		uiCnx7.waitForElementsVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(uiCnx7.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Banner is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled on UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");

		logger.strongStep("Disable the Admin Banner App");
		log.info("Info: Disable the Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage");
		log.info("Info: Load component Homepage");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing, true);
		
		logger.strongStep("Verify Admin Banner App is successfully disabled");
		log.info("Info: Verify Admin Banner App is successfully disabled");
		cnxAssert.assertFalse(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");

		logger.strongStep("Load Customizer component");
		log.info("Info: Load Customizer component");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		
		logger.strongStep("Enable the Admin Banner App");
		log.info("Info: Enable the Admin Banner App");
		uiCnx7.enableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled on App Reg UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on  App Reg UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");

		logger.strongStep("Deleting Admin Banner App");
		log.info("Info: Deleting Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		

		uiCnx7.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create, Disable and Enable admin banner app via App Registry extension with cache Expiration.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Admin Banner App, if already created</li>
	*<li><B>Step:</B>Create the Admin Banner App from App Reg with Cache</li>
	*<li><B>Verify:</B>Verify Admin Banner Config via GET api call</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Update the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Updated Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Disable the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully disabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app not displayed on Homepage New UI</li>
	*<li><B>Step:</B>Enable the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully enabled on App Reg UI</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T659</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyAdminBannerViaAppRegWithCache() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Load Hompeage I am following and login: " +testUser.getDisplayName());
		log.info("Info: Load Hompeage I am following and login: " +testUser.getDisplayName());
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		uiCnx7.login(testUser);
		logger.strongStep("Load Customizer");
		log.info("Info: Load Customizer");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);
	
		logger.strongStep("Delete Admin Banner App, if already created");
		log.info("Info: Delete Admin Banner App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Create Admin Banner App");
		log.info("Info: Create Admin Banner App");
		uiCnx7.createAppViaAppReg(appRegAppName+" Cache");
				
		logger.strongStep("Invoke a GET API for Admin banner app config");
		log.info("Info: Calling GET API for Admin banner app config");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.getAdminBannerConfigAppReg();
		
		logger.strongStep("Verify api status code and response of Admin banner app");
		log.info("Info: Verify api status code and response of Admin banner app");
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Verify Admin Banner Config Status Code");

		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-banner","Verify value for name key");
		cnxAssert.assertEquals(json.get("items[0].payload.open"), true,"Verify value for name open");
		cnxAssert.assertEquals(json.get("items[0].payload.message"), Arrays.asList("This is the HCL Connections Banner"),"Verify value for name message");
		cnxAssert.assertEquals(json.get("items[0].payload.severity"), "success","Verify value for name severity");
		cnxAssert.assertEquals(json.get("items[0].payload.cacheExpiration"), 2000,"Verify value for name cacheExpiration");


		logger.strongStep("Verify Admin Banner App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Admin Banner App is successfully displayed on App Reg UI.");
		uiCnx7.waitForElementsVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(uiCnx7.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Connections Banner is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled on UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");
		
		logger.strongStep("Load Customizer");
		log.info("Info: Load Customizer");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Click on Admin Banner App");		
		log.info("Info: Click on Admin Banner App");
		uiCnx7.clickLinkWaitWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),5, "Click and open connection banner app");
		
		String jsonString = uiCnx7.updateAdminBannerAppJsonWithCache();
		String successMsg = "Saved updated changes to application '" + appRegAppName + "'";
		logger.strongStep("Prepare and import the json file.");		
		log.info("Info: Prepare and import the json file.");		
		uiCnx7.importJsonFileAndSaveApp(jsonString, successMsg);
				
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully updated on UI");
		log.info("Info: Verify Admin Banner App is successfully updated on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "Please make sure to update your profile soon.")), 10), "Updated Admin banner text is verified");
				
		logger.strongStep("Load Customizer and then disable the Admin Banner App");
		log.info("Info: Load Customizer and then disable the Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.disableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage");
		log.info("Info: Load component Homepage");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing, true);
		
		logger.strongStep("Verify Admin Banner App is successfully disabled");
		log.info("Info: Verify Admin Banner App is successfully disabled");
		cnxAssert.assertFalse(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "Please make sure to update your profile soon.")), 10), "Admin banner text is verified");

		logger.strongStep("Load Customizer component");
		log.info("Info: Load Customizer component");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		
		logger.strongStep("Enable the Admin Banner App");
		log.info("Info: Enable the Admin Banner App");
		uiCnx7.enableAppFromAppReg(appRegAppName);
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing, true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled on App Reg UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on  App Reg UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "Please make sure to update your profile soon.")), 10), "Admin banner text is verified");

		logger.strongStep("Deleting Admin Banner App");
		log.info("Info: Deleting Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		

		uiCnx7.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Update admin banner app via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Create Admin Banner app, if not displayed</li>
	*<li><B>Step:</B>Update the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner Config via GET api call</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T580</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyUpdateAdminBannerviaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String jsonString = uiCnx7.updateAdminBannerAppJson();
		String successMsg = "Saved changes to application '" + appRegAppName + "'";
		uiCnx7.startTest();
		
		logger.strongStep("Load Customizer and login: " +testUser.getDisplayName());	
		log.info("Info: Load Customizer and login: " +testUser.getDisplayName());
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		uiCnx7.login(testUser);
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Create Admin Banner app, if not displayed");	
		log.info("Info: Create Admin Banner app, if not displayed");
		if(uiCnx7.getAppByTitle(appRegAppName) == null) {
			uiCnx7.createAppViaAppReg(appRegAppName);	
			uiCnx7.waitForElementsVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		} 
		
		logger.strongStep("Click on Admin Banner App");		
		log.info("Info: Click on Admin Banner App");
		uiCnx7.clickLinkWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)), "Click and open connection banner app");
		
		logger.strongStep("Prepare and import the json file.");		
		log.info("Info: Prepare and import the json file.");		
		uiCnx7.importJsonFileAndSaveApp(jsonString, successMsg);
				
		logger.strongStep("Invoke GET API for Admin banner app");
		log.info("Info: Invoke GET API for Admin banner app");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.getAdminBannerConfigAppReg();
		
		logger.strongStep("Verify Status Code of Admin banner app response");
		log.info("Info: Verify Status Code of Admin banner app response");
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Get Admin Banner Config Status Code");
				
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		JsonPath json = new JsonPath(resp.asString());
		cnxAssert.assertEquals(json.get("items[0].name"), "connections-banner","Verify value for name key");
		cnxAssert.assertEquals(json.get("items[0].payload.open"), true,"Verify value for name open");
		cnxAssert.assertEquals(json.get("items[0].payload.message"), Arrays.asList("Please make sure to update your profile soon."),"Verify value for name message");
		cnxAssert.assertEquals(json.get("items[0].payload.severity"), "info","Verify value for name severity");
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully updated on UI");
		log.info("Info: Verify Admin Banner App is successfully updated on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "Please make sure to update your profile soon.")), 10), "Updated Admin banner text is verified");
		
		logger.strongStep("Deleting Admin Banner App");
		log.info("Info: Deleting Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);
		
		uiCnx7.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify GET API for Admin Banner app.</li>
	*<li><B>Step:</B>Load homepage component and login into Application</li>
	*<li><B>Step:</B>Invoke GET API for Admin banner app</li>
	*<li><B>Verify:</B>Verify GET API response of Admin banner app</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T570</li>
	*/	
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2"},enabled=true) 
	public void verifyGetAdminBannerViaAPI() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Load HomePage and Login as : "+ testUser.getDisplayName());
		log.info("Info: Load HomePage and Login as : "+ testUser.getDisplayName());
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		uiCnx7.login(testUser);
		
		logger.strongStep("Invoke GET API for Admin banner app");
		log.info("Info: Invoke GET API for Admin banner app");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.getAdminBannerConfig();
		
		logger.strongStep("Verify Status Code of Admin banner app response");
		log.info("Info: Verify Status Code of Admin banner app response");		
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Get Admin Banner Config Status Code");
		
		logger.weakStep("Converting Response to POJO class");
		log.info("Info: Converting Response to POJO class");		
		AdminBannerResponse adminBannerResponse=resp.as(AdminBannerResponse.class);
		
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		cnxAssert.assertTrue((!adminBannerResponse.getSeverity().isEmpty()), "verify severify for Admin Banner");
		cnxAssert.assertTrue((!adminBannerResponse.getMessage().isEmpty()), "verify message for Admin Banner");
		
		logger.strongStep("Invoke PUT API to disable Admin banner if already enabled");
		log.info("Info: Invoke PUT API to disable Admin banner if already enabled");		
		if(adminBannerResponse.isOpen()) {
			adminBannerService.disableEnableAdminBanner("disable");
		}
		
		uiCnx7.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Enable Admin Banner via PUT API</li>
	*<li><B>Step:</B>Load homepage component and login into Application</li>
	*<li><B>Step:</B>Invoke PUT API for Admin banner app</li>
	*<li><B>Verify:</B>Verify PUT API response of Admin banner app</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T570</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T571</li>
	*/		
	//TODO CNX8UI: disabled for MTBVT server, waiting for https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11509 ticket code to merge
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2","mt-exclude"}) 
	public void verifyEnableAdminBannerViaAPI() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Invoke PUT API to enable Admin banner");
		log.info("Info: Invoke PUT API to enable Admin banner");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.disableEnableAdminBanner("enable");
		
		logger.strongStep("Verify Status Code of Admin banner response");
		log.info("Info: Verify Status Code after got response of Admin banner");				
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Admin Banner is enabled");
		
		logger.strongStep("Verify api response for enable Admin banner");
		log.info("Info: Verify api response for enable Admin banner");
		cnxAssert.assertEquals(resp.asString(), "\"Admin Banner enabled\"" ,"verify response for enabled Admin banner");
		
		logger.strongStep("Load component Homepage");
		log.info("Info: Load component Homepage");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Info: Login and toggle to new UI as "+ cfg.getUseNewUI());
		uiCnx7.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.weakStep("Verify Admin Banner App is successfully enabled on UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.cssSelector(HomepageUIConstants.AdminBanner), 10),"Admin banner is enabled");
				
		logger.strongStep("Invoke PUT API to disable Admin banner");
		log.info("Info: Invoke PUT API to disable Admin banner");
		adminBannerService.disableEnableAdminBanner("disable");
		
		uiCnx7.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Disable Admin Banner via PUT API</li>
	*<li><B>Step:</B>Load homepage component and login into Application</li>
	*<li><B>Step:</B>Invoke PUT API for Admin banner app</li>
	*<li><B>Verify:</B>Verify PUT API response of Admin banner app</li>
	*<li><B>Verify:</B>Verify Admin Banner app not displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T570</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T573</li>
	*/	
	//TODO CNX8UI: disabled for MTBVT server, waiting for https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11509 ticket code to merge
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2","mt-exclude"}) 
	public void verifyDisableAdminBannerViaAPI() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
			
		logger.strongStep("Invoke PUT API to disable Admin banner");
		log.info("Info: Invoke PUT API to disable Admin banner");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		Response resp = adminBannerService.disableEnableAdminBanner("disable");
		
		logger.strongStep("Verify Status Code after got response of Admin banner");
		log.info("Info: Verify Status Code after got response of Admin banner");	
		adminBannerService.assertStatusCode(resp, HttpStatus.SC_OK, "Admin Banner is disabled");
		
		logger.strongStep("Verify api response for disable Admin banner");
		log.info("Info: Verify api response for disable Admin banner");
		cnxAssert.assertEquals(resp.asString(), "\"Admin Banner disabled\"" ,"verify response for disabled Admin banner");
	
		logger.strongStep("Load component Homepage");
		log.info("Info: Load component Homepage");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Info: Login and toggle to new UI as "+ cfg.getUseNewUI());
		uiCnx7.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully disabled");
		log.info("Info: Verify Admin Banner App is successfully disabled");
		cnxAssert.assertFalse(uiCnx7.isElementVisibleWd(By.cssSelector(HomepageUIConstants.AdminBanner), 3),"Admin banner is disabled");
		
		uiCnx7.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Update Admin Banner via PUT API</li>
	*<li><B>Step:</B>Load homepage component and login into Application</li>
	*<li><B>Step:</B>Update admin banner for 'success' severity via PUT API</li>
	*<li><B>Verify:</B>Verify PUT API response for 'success' severity</li>
	*<li><B>Verify:</B>Verify Admin Banner app for 'success' severity is successfully displayed on Homepage New UI</li>
	*<B>Step:</B>Update admin banner for 'warning' severity via PUT API</li>
	*<li><B>Verify:</B>Verify PUT API response for 'warning' severity</li>
	*<li><B>Verify:</B>Verify Admin Banner app for 'warning' severity is successfully displayed on Homepage New UI</li>
	*<B>Step:</B>Update admin banner for 'error' severity via PUT API</li>
	*<li><B>Verify:</B>Verify PUT API response for 'error' severity</li>
	*<li><B>Verify:</B>Verify Admin Banner app for 'error' severity is successfully displayed on Homepage New UI</li>
	*<B>Step:</B>Update admin banner for 'info' severity via PUT API</li>
	*<li><B>Verify:</B>Verify PUT API response for 'info' severity</li>
	*<li><B>Verify:</B>Verify Admin Banner app for 'info' severity is successfully displayed on Homepage New UI</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T570</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T571</li>
	*/	
	//TODO CNX8UI: disabled for MTBVT server, waiting for https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11509 ticket code to merge
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2","mt-exclude"}) 
	public void verifyUpdateAdminBannerViaAPI() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Update admin banner for 'success' severity via PUT API");
		log.info("Info: Update admin banner for 'success' severity via PUT API");
		AdminBannerResponse adminBannerResponse = uiCnx7.updateAdminBannerViaApi(logger, testUser, "This is a successful banner", "success", true);
		
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		cnxAssert.assertEquals(adminBannerResponse.isOpen(), true, "verify open status for Admin Banner");
		cnxAssert.assertEquals(adminBannerResponse.getSeverity(), "success", "verify severity for Admin Banner");	
		
		logger.strongStep("Load component Homepage");
		log.info("Info: Load component Homepage");
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
				
		logger.strongStep("Login and toggle to new UI as "+cfg.getUseNewUI());
		log.info("Info: Login and toggle to new UI as "+cfg.getUseNewUI());
		uiCnx7.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled");
		log.info("Info: Verify Admin Banner App is successfully enabled");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is a successful banner")), 10), "Admin banner text is verified for 'success'");
		
		logger.strongStep("Testing admin banner for 'warning' severity");
		log.info("Info: Testing admin banner for 'warning' severity");
		adminBannerResponse = uiCnx7.updateAdminBannerViaApi(logger, testUser, "This is a warning banner", "warning", true);
		
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		cnxAssert.assertEquals(adminBannerResponse.isOpen(), true, "verify open status for Admin Banner");
		cnxAssert.assertEquals(adminBannerResponse.getSeverity(), "warning", "verify severity for Admin Banner");
		
		logger.strongStep("Refresh Homepage");
		log.info("Info: Refresh Homepage");
		uiCnx7.refreshPage();
		
		logger.strongStep("Verify Admin Banner App is successfully enabled");
		log.info("Info: Verify Admin Banner App is successfully enabled");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is a warning banner")), 10), "Admin banner text is verified for 'warning'");
		
		logger.strongStep("Testing admin banner for 'error' severity");
		log.info("Info: Testing admin banner for 'error' severity");
		adminBannerResponse = uiCnx7.updateAdminBannerViaApi(logger, testUser, "This is an error banner", "error", true);
		
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		cnxAssert.assertEquals(adminBannerResponse.isOpen(), true, "verify open status for Admin Banner");
		cnxAssert.assertEquals(adminBannerResponse.getSeverity(), "error", "verify severity for Admin Banner");
		
		logger.strongStep("Refresh Homepage");
		log.info("Info: Refresh Homepage");
		uiCnx7.refreshPage();
		
		logger.strongStep("Verify Admin Banner App is successfully enabled");
		log.info("Info: Verify Admin Banner App is successfully enabled");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is an error banner")), 10), "Admin banner text is verified for 'error'");
		
		logger.strongStep("Testing admin banner for 'info' severity");
		log.info("Info: Testing admin banner for 'info' severity");
		adminBannerResponse = uiCnx7.updateAdminBannerViaApi(logger, testUser, "This is an info banner", "info", true);
		
		logger.strongStep("Verify api response of Admin banner app");
		log.info("Info: Verify api response of Admin banner app");
		cnxAssert.assertEquals(adminBannerResponse.isOpen(), true, "verify open status for Admin Banner");
		cnxAssert.assertEquals(adminBannerResponse.getSeverity(), "info", "verify severity for Admin Banner");
		
		logger.strongStep("Refresh Homepage");
		log.info("Info: Refresh Homepage");
		uiCnx7.refreshPage();
		
		logger.strongStep("Verify Admin Banner App is successfully enabled");
		log.info("Info: Verify Admin Banner App is successfully enabled");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is an info banner")), 10), "Admin banner text is verified for 'info'");
		
		logger.strongStep("Testcase passed. Calling PUT API to disable Admin banner");
		log.info("Info: Testcase passed. Calling PUT API to disable Admin banner");
		AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
		adminBannerService.disableEnableAdminBanner("disable");		

		uiCnx7.endTest();	
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Admin Banner on all component pages.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Admin Banner App, if already created</li>
	*<li><B>Step:</B>Create the Admin Banner App from App Reg</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Homepage New UI</li>
	*<li><B>Step:</B>Navigate to Communities page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Communities New UI</li>
	*<li><B>Step:</B>Navigate to People page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on People New UI</li>
	*<li><B>Step:</B>Navigate to Files page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Files New UI</li>
	**<li><B>Step:</B>Navigate to Profiles page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Profiles New UI</li>
	*<li><B>Step:</B>Navigate to Blogs page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Blogs New UI</li>
	*<li><B>Step:</B>Navigate to Bookmarks page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Bookmarks New UI</li>
	*<li><B>Step:</B>Navigate to Wikis page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Wikis New UI</li>
	*<li><B>Step:</B>Navigate to Activities page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Activities New UI</li>
	*<li><B>Step:</B>Navigate to Forums page</li>
	*<li><B>Verify:</B>Verify Admin Banner app successfully displayed on Forums New UI</li>
	*<li><B>Step:</B>Navigate to customizer UI and Delete Admin banner AppReg</li>
	*<li><B>Defect Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14133</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyAdminBannerOnAllPages() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		uiCnx7.startTest();
		
		logger.strongStep("Load Customizer and login: " +testUser.getDisplayName()+"and toggle new UI as "+ cfg.getUseNewUI());
		log.info("Info : Load Customizer and login: " +testUser.getDisplayName()+"and toggle new UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponent(Data.getData().HomepageImFollowing);
		uiCnx7.login(testUser);
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Delete Admin Banner App, if already created");
		log.info("Info: Delete Admin Banner App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		logger.strongStep("Create Admin Banner App");
		log.info("Info: Create Admin Banner App");
		uiCnx7.createAppViaAppReg(appRegAppName);

		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		uiCnx7.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());
		
		logger.strongStep("Verify Admin Banner App is successfully enabled on UI");
		log.info("Info: Verify Admin Banner App is successfully enabled on UI");
		cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");		
		
		String cnxPages[] = { "COMMUNITIES" , "PEOPLE", "FILES", "PROFILE", "BLOGS",
				"BOOKMARKS", "WIKIS","ACTIVITIES", "FORUMS"};
				
		for (String cnxPage : cnxPages) {
			if (cnxPage.equalsIgnoreCase("ACTIVITIES") && cfg.getIsKudosboardEnabled()) {
				log.warn("Kudosboard is enabled, skipping classic Activities test.");
				continue;
			}

			logger.strongStep("Select " + cnxPage + " in nav menu");
			log.info("INFO: Select " + cnxPage + " in nav menu");
			AppNavCnx8 appNav = AppNavCnx8.valueOf(cnxPage);
			appNav.select(uiCnx7);
			
			logger.strongStep("Verify Admin Banner App is successfully enabled on UI");
			log.info("Info: Verify Admin Banner App is successfully enabled on UI");
			cnxAssert.assertTrue(uiCnx7.isElementVisibleWd(By.xpath(HomepageUIConstants.AdminBannerText.replace("PLACEHOLDER", "This is the HCL Connections Banner")), 10), "Admin banner text is verified");
			
		}
		
		logger.strongStep("Load Customizer component");
		log.info("Info: Load Customizer component");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		
		logger.strongStep("Deleting Admin Banner App");
		log.info("Info: Deleting Admin Banner App");
		uiCnx7.loadComponent(Data.getData().ComponentCustomizer, true);
		uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		

		uiCnx7.endTest();
	}
	
	@AfterMethod(alwaysRun=true)
	public void deleteAppReg(ITestResult result)
	{
		if(!result.getName().contains("ViaAPI"))
		{
			log.info("Info: Invoke GET AppReg API to get the auth token");
			AdminBannerService adminBannerService = new AdminBannerService(cfg.getServerURL(), testUser.getEmail(), testUser.getPassword());
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
}
