package com.ibm.conn.auto.tests.communities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;

public class BVT_Cnx8UI_Communities_Search  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Communities_Search.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	CommonUICnx8 commonUI;
	GlobalSearchCnx8 globalSearchUI;
	private CommunitiesUI cUI;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private SearchAdminService adminService;
	private User searchAdmin;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
	}

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
	}


	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities page</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the text in Search Communities Suggestion at the top</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T608</li>
	 *</ul>
	 */

	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"},enabled=true)
	public void verifySearchSuggestionOrderAndDefaultAttributesFromCommunity()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in Communities", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		globalSearchUI.startTest();

		logger.strongStep("Load Communities, login to Connections and toggle to new UI");
		log.info("INFO : Load Communities, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify that "+ text + " in Communities Search Suggestion is displayed and Communities Search Suggestion is at the top");
		log.info("INFO : "+ "Verify that "+ text + " in Communities Search Suggestion is displayed and Communities Search Suggestion is at the top");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.communitiesInSearchSuggestion.replace("PLACEHOLDER", text))).getText(),
				globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.searchedSuggestionList)).get(0).getText(),
				text + "in Communities Search Suggestion is displayed and Communities Search Suggestion is at the top");

		logger.strongStep("Verify the order of Suggestions in Search");
		log.info("INFO : Verify the order of Suggestions in Search");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");

		globalSearchUI.endTest();

	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Wikis page</li>
	 *<li><B>Step:</B> Create Community via API and Wikis to it, if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Wikis link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Wikis page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T611</li>
	 *</ul>
	 */
	//Failing as Community wiki navigates to cnx7 instead of cnx8 page(which is temporary solution for a bug https://jira.cwp.pnp-hcl.com/browse/CNXSERV-15103 ) so need to disable this until new implementation is available
	@Test(groups = {"cnx8ui-cplevel2"},enabled=false)
	public void verifySearchSuggestionOrderFromCommunityWikis() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Wiki",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Wikis widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Wikis widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wikis widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Wikis link in the Secondary Nav");
		log.info("INFO: Select Wikis link in the Secondary Nav");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on My Communities  page</li>
	 *<li><B>Step:</B> Create Community via API</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on My Community page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T607</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"},enabled=true)
	public void verifySearchSuggestionOrderFromMyCommunity() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Blogs page</li>
	 *<li><B>Step:</B> Create Community via API and Blogs to it, if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Blogs link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Blogs page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T618</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityBlog() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Blog",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Blogs widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Blogs widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.BLOG)) {
			log.info("INFO: Add the Blogs widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Blogs link in the Secondary Nav");
		log.info("INFO: Select Blogs link in the Secondary Nav");
		Community_TabbedNav_Menu.BLOG.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Forum page</li>
	 *<li><B>Step:</B> Create Community via API and Forum to it , if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Forum link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Forum page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T619</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityForum() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Forum",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add the Forum widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Forum link in the Secondary Nav");
		log.info("INFO: Select Forum link in the Secondary Nav");
		Community_TabbedNav_Menu.FORUMS.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Status Update page</li>
	 *<li><B>Step:</B> Create Community via API</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Status Update link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Forum page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T620</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityStatusUpdate() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in Status Updates",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Status Update in the Secondary Nav");
		log.info("INFO: Select Status Update link in the Secondary Nav");
		Community_TabbedNav_Menu.STATUSUPDATES.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Files page</li>
	 *<li><B>Step:</B> Create Community via API and Files to it , if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Files link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Files page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T621</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityFiles() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this File",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Files widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Files widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FILES)) {
			log.info("INFO: Add the Files widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FILES);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Files link in the Secondary Nav");
		log.info("INFO: Select Files link in the Secondary Nav");
		Community_TabbedNav_Menu.FILES.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Bookmark page</li>
	 *<li><B>Step:</B> Create Community via API and Bookmark to it , if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Bookmark link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Bookmark page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T622</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityBookmark() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Bookmark",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Bookmark widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Bookmark widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.BOOKMARKS)) {
			log.info("INFO: Add the Files widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BOOKMARKS);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Bookmark link in the Secondary Nav");
		log.info("INFO: Select Files Bookmark in the Secondary Nav");
		Community_TabbedNav_Menu.BOOKMARK.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Activity page</li>
	 *<li><B>Step:</B> Create Community via API and Activity to it , if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Activity link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Activity page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T623</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityActivity() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Activity",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Activity widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Activity widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.ACTIVITIES)) {
			log.info("INFO: Add the Files widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
		}

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Activity link in the Secondary Nav");
		log.info("INFO: Select Files Activity in the Secondary Nav");
		Community_TabbedNav_Menu.ACTIVITIES.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Communities Ideation Blog page</li>
	 *<li><B>Step:</B> Create Community via API and Ideation Blog to it, if not present</li>
	 *<li><B>Step:</B> Login to Communities Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to created Community and click on Ideation Blog link</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order on Community Ideation Blog page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T624</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderFromCommunityIdeationBlog() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in this Ideation Blog",text+" - in this Community", text+" - in All Content", text+" - in People", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Add the Ideation Blog widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);	

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on the Ideation Blog link in the Secondary Nav");
		log.info("INFO: Select Ideation Blog link in the Secondary Nav");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(cUI,2);

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);

		logger.strongStep("Verify the order of Suggestions in Search dropdown");
		log.info("INFO : Verify the order of Suggestions in Search dropdown");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Search Result for Communities</li>
	 *<li><B>Step:</B> Load Communities component, login and Toggle to new UI</li>	
	 *<li><B>Step:</B> Create Community via UI</li>
	 *<li><B>Step:</B>Load Homepage component</li>	
	 *<li><B>Verify:</B>Verify that Search TextBox is displayedt</li>
	 *<li><B>Step:</B> Type created community name in search text box</li>
	 *<li><B>Verify:</B>Verify the created community is displayed under community typograph</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T650</li>
	 *</ul>
	 * @throws Exception 
	 */
	
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifySearchResultForCommunities() throws Exception
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("test" + Helper.genDateBasedRand())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Run Search indexer for communities");
		log.info("INFO: Run Search indexer for communities");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Load Communities component, login to Connections and toggle to new UI as" + cfg.getUseNewUI());
		log.info("INFO : Load Home page, login to Connections and toggle to new UI as" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		commonUI.loadComponent(Data.getData().ComponentCommunities,true);

		logger.strongStep("Type created community name in Search textBox");
		log.info("INFO : Type created community in Search textBox");
		globalSearchUI.waitForClickableElementWd( By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
		globalSearchUI.typeWithDelayWd(community.getName(), By.cssSelector(GlobalSearchUIConstants.searchTextBox));
		
		logger.strongStep("Verify the created community is displayed under community typography");
		log.info("INFO : Verify the created community is displayed under community typography");
		cnxAssert.assertTrue(globalSearchUI.isSearchedCommunityDisplayInCommunititesTypography(community.getName()),
				"Verify the order of Suggestions in Search");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Visited Results for Communities</li>
	 *<li><B>Step:</B>Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B>Create Community via UI</li>
	 *<li><B>Verify:</B>Load Communities component</li>
	 *<li><B>Step:</B>Click on search text box</li>
	  *<li><B>Step:</B>Click on View All link of Recently Visited</li>
	 *<li><B>Verify:</B>Verify that Recent Search section is not displayed</li>
 	 *<li><B>Verify:</B>Verify that Recently Visited item is displayed</li>
	 *<li><B>Step:</B>Click on Back button</li>
	 *<li><B>Verify:</B>Verify that Recent Search section is displayed again</li>
	 *<li><B>Verify:</B>Click on the created community under Recently visited section of Search Dropdown</li>
	 *<li><B>Verify:</B>Verify that User is naviagted to created community page</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T649</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T707</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2","cp-only"})
	public void verifyRecentlyVisitedCommunity() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Change default landing page of community, if its not already set");
		log.info("INFO: Change default landing page of community, if its not already set");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Community using UUID");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		commonUI.waitForPageLoaded(driver);

		logger.strongStep("Run Search indexer for communities");
		log.info("INFO: Run Search indexer for communities");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Load Communities component");
		log.info("INFO :  Load Communities component");
		commonUI.loadComponent(Data.getData().ComponentCommunities,true);
		
		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		commonUI.waitForPageLoaded(driver);
		WebElement searchBox = globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);
		
		logger.strongStep("Click on View All link of Recently Visited");
		log.info("INFO : Click on View All link of Recently Visited");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.recentVisitedViewAllLinkInSearchDropdown),5
				,"Click on Recently Visited View all link");
		
		logger.strongStep("Verify Recent Search section is not displayed");
		log.info("INFO : Verify Recent Search section is not displayed");	
		cnxAssert.assertTrue(globalSearchUI.waitForElementInvisibleWd(By.xpath(GlobalSearchUIConstants.recentSearchViewAllLinkInSearchDropdown), 4),
		"Verify Recent Search View all link is not displayed");
		
		logger.strongStep("Verify Recently visited item is displayed in Recently Visted View all section");
		log.info("INFO : Verify Recently visited item is displayed in Recently Visted View all section");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.recentlyVisitedItemsInSearchDropdown
				.replace("PLACEHOLDER", comAPI.getTitle())),4);
		
		logger.strongStep("Click on Back button");
		log.info("INFO : Click on Back button");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.recentlyVisitedBackButtonInDropdown)
				, 5, "click on Back button");
		
		logger.strongStep("Verify Recent Search section is displayed");
		log.info("INFO : Verify Recent Search section is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.xpath(GlobalSearchUIConstants.recentSearchViewAllLinkInSearchDropdown),4),
				"Verify Recent Search View all link is not displayed");

		logger.strongStep("Click on created community listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on created community listed under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlyVisited(comAPI.getTitle());	
		globalSearchUI.waitForElementVisibleWd(globalSearchUI.createByFromSizzle(CommunitiesUIConstants.communityOverview), 8);

		logger.strongStep("Get the URL of navigated community");
		log.info("INFO : Get the URL of navigated community");
		String communityUUID = driver.getCurrentUrl().substring(driver.getCurrentUrl().lastIndexOf("?communityUuid=")+1);
		
		logger.strongStep("Verify the page naviagted to created community");
		log.info("INFO : Verify the page naviagted to created community");
		cnxAssert.assertEquals(community.getCommunityUUID(), communityUUID,"Verify communityUUID");
		
		globalSearchUI.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Search Results for Communities</li>
	 *<li><B>Step:</B>Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B>Create Community via UI</li>
	 *<li><B>Stpe:</B>Load Communities component</li>
	 *<li><B>Step:</B>Type created community name in search text box and click on it</li>
	 *<li><B>Verify:</B>Verify that search result page for communities gets opened</li>
	 *<li><B>Step:</B>Load Communities component again</li>
	 *<li><B>Step:</B>Click on search text box and click on it</li>
	 *<li><B>Step:</B>Click on last navigated community under Recently Search section of Search Dropdown</li>
	 *<li><B>Verify:</B>Verify that search result page for communities gets opened</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T652</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyRecentlySearchCommunity() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("test" + Helper.genDateBasedRand())
				.access(defaultAccess)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Run Search indexer for communities");
		log.info("INFO: Run Search indexer for communities");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Load Communities component, login to Connections and toggle to new UI as" + cfg.getUseNewUI());
		log.info("INFO : Load Home page, login to Connections and toggle to new UI as" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		commonUI.waitForPageLoaded(driver);
		commonUI.loadComponent(Data.getData().ComponentCommunities,true);

		logger.strongStep("Type created community name in Search textBox");
		log.info("INFO : Type created community in Search textBox");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),3);
		globalSearchUI.typeWithDelayWd(community.getName(), By.cssSelector(GlobalSearchUIConstants.searchTextBox));

		logger.strongStep("Click on community name which is appended with in Coomunities in Search textBox");
		log.info("INFO : Click on community name which is appended with in Coomunities in Search textBox");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.communitiesTypographyInSearchSuggestion), 15);
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.communitiesInSearchSuggestion.replace("PLACEHOLDER", community.getName())),6);
		
		logger.strongStep("Verify that User is navigated to Search Result page with last searched community");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched community");
		globalSearchUI.waitForPageLoaded(driver);
		cnxAssert.assertTrue(globalSearchUI.isResultListContainsSearchedCommunity(community.getName()),"Verify searched community on Search Result page");
		
		logger.strongStep("Load homepage and Click on Communities from left nav");
		log.info("INFO : Load homepage and Click on Communities from left nav");
		commonUI.loadComponent(Data.getData().ComponentHomepage,true);
		AppNavCnx8.COMMUNITIES.select(commonUI);
		globalSearchUI.waitForPageLoaded(driver);
		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		WebElement searchBox = globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);
		
		logger.strongStep("Click on last searched community listed under Recently Search section of Global search dropdown");
		log.info("INFO : Click on last searched community listed under Recently Search section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlySearch(community.getName());	
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that User is navigated to Search Result page with last searched community");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched community");
		cnxAssert.assertTrue(globalSearchUI.isResultListContainsSearchedCommunity(community.getName()),"Verify searched community on Search Result page");

		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify User is able to see community results when select "in communities" option in the quick search and
	 * user navigated to Community page when click on a community in search result</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type Community name in global search box</li>
	 *<li><B>Step:</B> Select Search 'in Communities' option from dropdown</li>
	 *<li><B>Verify:</B> Verify Communities filter button is selected by default in Top filters</li>
	 *<li><B>Verify:</B> Verify Search result contains Community name</li>
	 *<li><B>Step:</B> Click on community name in the search result</li>
	 *<li><B>Verify:</B> Verify user navigated to the community page</li>
	 *<li><B>Step:</B>Delete the community via API</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T693</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/129924</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyClickingCommunityNameRedirectToCommunityPage() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = globalSearchUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription)
													.build();
		
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		String communityTitle = comAPI.getTitle();
		
		// Run indexer for communities
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type Community name in Global Search textBox");
		log.info("INFO: Type Community name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(community.getName());
		
		logger.strongStep("Click on "+ communityTitle + " - in Communities option from suggestion list");
		log.info("INFO: Click on "+ communityTitle + " - in Communities option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.communitiesInSearchSuggestion.replace("PLACEHOLDER", community.getName())), 4,
				" - in Communities option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);
		
		logger.strongStep("Verify 'Communities' filter button is selected in top filters");
		log.info("INFO : Verify 'Communities' filter button is selected in top filters");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "Communities", "'Communities' is selected");
		
		logger.strongStep("Verify Community name is visible in the search result");
		log.info("INFO : Verify Community name is visible in the search result");
		cnxAssert.assertTrue(globalSearchUI.isResultListContainsSearchedCommunity(communityTitle),
				"Community is visible");
		
		logger.strongStep("Click on created community name");
		log.info("INFO : Click on created community name");
		globalSearchUI.mouseHoverAndClickWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultForCommunity)));
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Verify User is redirected to the Community page");
		log.info("INFO : Verify User is redirected to the Community page");
		globalSearchUI.waitForElementVisibleWd(globalSearchUI.createByFromSizzle(CommunitiesUIConstants.communityHighlightTab), 5);
		cnxAssert.assertTrue(globalSearchUI.isElementPresentWd(globalSearchUI.createByFromSizzle(CommunitiesUIConstants.communityHighlightTab)), "Community page is displayed");
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();

	}
}
