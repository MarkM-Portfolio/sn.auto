package com.ibm.conn.auto.tests.homepage;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hcl.lconn.automation.framework.services.AdminBannerService;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;

import io.restassured.response.Response;

public class BVT_Cnx8UI_Homepage_CustomizedSearch extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_CustomizedSearch.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;	
	private User testUser;
	private CustomizerUI cusUI;
	private GlobalSearchCnx8 globalSearchUI;
	private String appRegAppName = "Customized Search Display";;

	
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
		cusUI = new CustomizerUI(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Customized Search Display Options via App Registry extension.</li>
	*<li><B>Step:</B>Load customizer component and login into Application</li>
	*<li><B>Step:</B>Delete Custom Search Display App, if already created</li>
	*<li><B>Step:</B>Create the Custom Search Display App from App Reg</li>
	*<li><B>Verify:</B>Verify Custom Search Display app successfully enabled on App Reg UI</li>
	*<li><B>Step:</B>Load Home Page and Toggle to new UI</li>
	*<li><B>Step:</B>Type text in Quick Search textbox</li>
	*<li><B>Verify:</B>Verify the options in Quick Search Dropdown</li>
	*<li><B>Verify:</B>Click on "In Friends" option and verify the landing page</li>
	*<li><B>Verify:</B>Click on "All Connections" option and verify the landing page</li>
	*<li><B>Verify:</B>Navigate to AppReg page anddDelete the Custom Search Display App</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T688</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true) 
	public void verifyCustomSearchDisplayViaAppReg() throws IOException {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		cusUI.startTest();
		String text ="testing";
		String friendsUrl = "https://search.yahoo.com/?q="+text;
		String allConnectionUrl = "https://www.google.com/search?q="+text;
		
		logger.strongStep("Load Customizer and login: " +testUser.getDisplayName());
		log.info("Info: Load Customizer and login: " +testUser.getDisplayName());
		cusUI.loadComponent(Data.getData().HomepageImFollowing);
		cusUI.login(testUser);
		cusUI.loadComponent(Data.getData().ComponentCustomizer,true);


		logger.strongStep("Delete Custom Search Display  App, if already created");
		log.info("Info: Delete Custom Search Display App, if already created");
		if(cusUI.isElementVisibleWd(By.xpath(cusUI.cardLayout.replace("PLACEHOLDER",
				appRegAppName)),3)) { cusUI.deleteAppFromView(appRegAppName, appRegAppName);
		}

		logger.strongStep("Create Customized Search Display App");
		log.info("Info: Create Customized Search Display App");
		cusUI.createAppViaAppReg(appRegAppName);

		logger.strongStep("Verify Customized Search Display App is successfully displayed on App Reg UI.");
		log.info("Info: Verify Customized Search Display App is successfully displayed on App Reg UI.");
		cusUI.waitForElementsVisibleWd(By.xpath(cusUI.cardLayout.replace("PLACEHOLDER", appRegAppName)), 5);
		cnxAssert.assertTrue(cusUI.getAppByTitle(appRegAppName).getWebElement().isDisplayed(),"Custom Search Display App is created successfully.");		
		
		logger.strongStep("Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage and toggle to New UI as "+ cfg.getUseNewUI());
		cusUI.loadComponentAndToggleUI(Data.getData().HomepageImFollowing,true,cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
		String parentWindowHandle = ((WebDriver)driver.getBackingObject()).getWindowHandle();
		
		logger.strongStep("Type "+text+" in Global Search textBox");
		log.info("INFO : Type "+text+" in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Click on "+ text + " - in Friends option from suggestion list");
		log.info("INFO: Click on "+ text + " - in Friends option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.customizedInFriendsSuggestion.replace("PLACEHOLDER", text)), 4,
				" - in Friends option");	
		
		logger.strongStep("Switch to Friends page and verify the title of Friends page");
		log.info("INFO: Switch to Friends page and verify the title of Friends page");
		globalSearchUI.switchToNextWindowWd(friendsUrl);
		cnxAssert.assertTrue(((WebDriver)driver.getBackingObject()).getTitle().equals("Yahoo Search - Web Search"),"Verify the Title of in friends page");
		
		logger.strongStep("Close Friends window and navigate to homepage window");
		log.info("INFO: Close Friends window and navigate to homepage window");
		globalSearchUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowHandle);
		
		logger.strongStep("Type "+text+" in Global Search textBox");
		log.info("INFO : Type "+text+" in Global Search textBox");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 4);
		globalSearchUI.clearTexWithJavascriptWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox));
		globalSearchUI.typeWithDelayWd(text, By.cssSelector(GlobalSearchUIConstants.searchTextBox));
		
		logger.strongStep("Click on "+ text + " - in All Connetion option from suggestion list");
		log.info("INFO: Click on "+ text + " - in All Connetion option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.customizedInAllConnectionSuggestion.replace("PLACEHOLDER", text)), 4,
				" - in All Connetions option");	
		
		logger.strongStep("Switch to All Connection page and verify the title of All Connection page");
		log.info("INFO: Switch to All Connection page and verify the title of All Connection page");
		globalSearchUI.switchToNextWindowWd(allConnectionUrl);
		cnxAssert.assertTrue(((WebDriver)driver.getBackingObject()).getTitle().equals(text+" - Google Search"),"Verify the Title  of Al Connection page");
		
		logger.strongStep("Close All Connection window and navigate to homepage window");
		log.info("INFO: Close All Connection window and navigate to homepage window");
		globalSearchUI.closeCurrentWindowAndMoveToParentWindowWd(parentWindowHandle);
		
		logger.strongStep("Deleting Admin Banner App");
		log.info("Info: Deleting Admin Banner App");
		cusUI.loadComponent(Data.getData().ComponentCustomizer, true);
		cusUI.deleteAppFromView(appRegAppName, appRegAppName);		

		cusUI.endTest();
	}
	
	@AfterMethod(alwaysRun=true)
	public void deleteAppReg(ITestResult result)
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
