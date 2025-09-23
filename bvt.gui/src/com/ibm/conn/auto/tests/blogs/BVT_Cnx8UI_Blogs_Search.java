
package com.ibm.conn.auto.tests.blogs;

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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.BlogsUICnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;

public class BVT_Cnx8UI_Blogs_Search extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Blogs_Search.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private BlogsUICnx8 bUI;
	GlobalSearchCnx8 globalSearchUI;
	CommonUICnx8 commonUI;
	private User testUser;
	private APIBlogsHandler apiOwner;
	private String serverURL;
	private SearchAdminService adminService;
	private User searchAdmin;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
	}


	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		bUI = new BlogsUICnx8(driver);
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Visited Results for Blogs</li>
	 *<li><B>Step:</B>Create Blog via API</li>
	 *<li><B>Step:</B>Login to Blogs page and Toggle to new UI</li>
	 *<li><B>Step:</B>Navigate to created blog</li>
	 *<li><B>Verify:</B>Load Blogs component</li>
	 *<li><B>Step:</B>Click on search text box</li>
	 *<li><B>Verify:</B>Verify the created blog is displayed under Recently visited section of Search Dropdown</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T649</li>
	 *</ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2","cp-only" })
	public void verifyRecentlyVisitedBlog() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = bUI.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.description("Blogs Test description for testcase " + testName).build();

		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser.getDisplayName());
		blog.createAPI(apiOwner);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		bUI.loadComponent(Data.getData().ComponentBlogs);
		bUI.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Open the blog");
		log.info("INFO: Open blog " + blog.getName());
		bUI.clickLinkWait("link=" + blog.getName());
		bUI.waitForPageLoaded(driver);
		
		logger.strongStep("Load Blogs component");
		log.info("Load Blogs component");
		bUI.loadComponent(Data.getData().ComponentBlogs,true);
		
		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		WebElement searchBox = globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);

		logger.strongStep("Click on created blog listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on created blog listed under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlyVisited(blog.getName());	
		globalSearchUI.waitForElementsVisibleWd(globalSearchUI.createByFromSizzle(BlogsUIConstants.BlogsNewEntryButton.replace("PLACEHOLDER", blog.getName())),6);
		
		logger.strongStep("Verify the page naviagted to created blog");
		log.info("INFO : Verify the page naviagted to created blog");
		cnxAssert.assertTrue(globalSearchUI.isElementDisplayedWd(globalSearchUI.createByFromSizzle(BlogsUIConstants.BlogsNewEntryButton.replace("PLACEHOLDER", blog.getName()))), 
				"Verify New Entry button is displayed");
		globalSearchUI.endTest();
	}
}
