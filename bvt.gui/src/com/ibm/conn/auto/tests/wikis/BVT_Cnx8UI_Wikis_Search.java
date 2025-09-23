
package com.ibm.conn.auto.tests.wikis;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;
import com.ibm.conn.auto.webui.cnx8.WikisUICnx8;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;

public class BVT_Cnx8UI_Wikis_Search extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Wikis_Search.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private WikisUICnx8 wUI;
	GlobalSearchCnx8 globalSearchUI;
	CommonUICnx8 commonUI;
	private User testUser;
	private String serverURL;
	private SearchAdminService adminService;
	private User searchAdmin;
	APIWikisHandler wikiApiOwner;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		wikiApiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
	}


	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		wUI = new WikisUICnx8(driver);
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Visited Results for Wikis</li>
	 *<li><B>Step:</B>Create Wiki via API</li>
	 *<li><B>Step:</B>Login to Wiki page and Toggle to new UI</li>
	 *<li><B>Step:</B>Navigate to created wiki</li>
	 *<li><B>Verify:</B>Load Wiki component</li>
	 *<li><B>Step:</B>Click on search text box</li>
	 *<li><B>Verify:</B>Verify the created wiki is displayed under Recently visited section of Search Dropdown</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T649</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2","cp-only"})
	public void verifyRecentlyVisitedWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = wUI.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(wikiApiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentWikis);
		wUI.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		wUI.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		wUI.waitForElementVisibleWd(wUI.createByFromSizzle(WikisUIConstants.wikiPageHeader), 5);
		
		logger.strongStep("Run Search indexer for wikis");
		log.info("INFO: Run Search indexer for wikis");
		adminService.indexNow("wikis", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		WebElement searchBox = globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);

		logger.strongStep("Click on created wiki listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on created wiki listed under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlyVisited(wiki.getName());	
		
		logger.strongStep("Verify the page naviagted to created Wiki page");
		log.info("INFO : Verify the page naviagted to created Wiki page");
		globalSearchUI.waitForElementsVisibleWd(globalSearchUI.createByFromSizzle(WikisUIConstants.wikiNameInBreadcrumb),8);
		cnxAssert.assertTrue(globalSearchUI.findElement(globalSearchUI.createByFromSizzle(WikisUIConstants.wikiPageHeader)).getText()
				.equals("Welcome to " +wiki.getName()),
				"Verify user is navigated to created Wiki page");
		
		globalSearchUI.endTest();
	}
}
