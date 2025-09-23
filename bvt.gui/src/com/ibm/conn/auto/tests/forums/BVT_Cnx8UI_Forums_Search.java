package com.ibm.conn.auto.tests.forums;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.ForumsUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class BVT_Cnx8UI_Forums_Search extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Forums_Search.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private ForumsUICnx8 fUI;
	GlobalSearchCnx8 globalSearchUI;
	CommonUICnx8 commonUI;
	private User testUser;
	private String serverURL;
	private SearchAdminService adminService;
	private User searchAdmin;
	APIForumsHandler forumsAPIOwner;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		forumsAPIOwner = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
	}


	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		fUI = new ForumsUICnx8(driver);
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Visited Results for Forums</li>
	 *<li><B>Step:</B>Create Forum via API</li>
	 *<li><B>Step:</B>Login to Forum page and Toggle to new UI</li>
	 *<li><B>Step:</B>Navigate to created forum</li>
	 *<li><B>Verify:</B>Load Forums component</li>
	 *<li><B>Step:</B>Click on search text box</li>
	 *<li><B>Verify:</B>Verify the created forum is displayed under Recently visited section of Search Dropdown</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T649</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2","cp-only"})
	public void verifyRecentlyVisitedForum() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		fUI.startTest();
		
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		Forum forum = ForumEvents.createForum(testUser, forumsAPIOwner, baseForum);
	
		logger.strongStep("Load Forums, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Forums, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		fUI.loadComponent(Data.getData().ComponentForums);
		fUI.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Click on created forum");
		log.info("INFO: Click on created forum");
		fUI.clickLinkWaitWd(globalSearchUI.createByFromSizzle(ForumsUIConstants.Forum_Tab),5);
		fUI.clickLinkWait("link=" + forum.getTitle());
		fUI.waitForPageLoaded(driver);
		
		logger.strongStep("Run Search indexer for forums");
		log.info("INFO: Run Search indexer for forums");
		adminService.indexNow("forums", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		WebElement searchBox = globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);

		logger.strongStep("Click on created forum listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on created forum listed under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlyVisited(forum.getTitle());	
		
		logger.strongStep("Verify the page naviagted to created forum");
		log.info("INFO : Verify the page naviagted to created forum");
		globalSearchUI.waitForElementsVisibleWd(globalSearchUI.createByFromSizzle(ForumsUIConstants.forumNameLink),4);
		cnxAssert.assertTrue(globalSearchUI.findElement(globalSearchUI.createByFromSizzle(ForumsUIConstants.forumNameLink)).getText().equals(forum.getTitle()), 
				"Verify New Entry button is displayed");
		globalSearchUI.endTest();
	}
}
