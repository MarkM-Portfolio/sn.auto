package com.ibm.conn.auto.tests.homepage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.blogs.BVT_Cnx8UI_Blogs;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;

public class BVT_Cnx8UI_Homepage_Search  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Blogs.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, testUser2, searchAdmin;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private Member member;
	private SearchAdminService adminService;
	private CommonUICnx8 commonUI;
	private GlobalSearchCnx8 globalSearchUI;
	private CommunitiesUI ui;
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		member = new Member(CommunityRole.MEMBERS, testUser2);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		adminService = new SearchAdminService();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
	}
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Homepage</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion options</li>
	 *<li><B>Verify:</B> Verify the text Search in All Content Suggestion at the top</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T609</li>
	 *</ul>
	 */
	
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderAndDefaultAttributes()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in All Content", text+" - in Communities", text+" - in People",text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		globalSearchUI.startTest();

		logger.strongStep("Load Home page, login to Connections and toggle to new UI");
		log.info("INFO : Load Home page, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);
		
		logger.strongStep("Verify that "+ text + " in All Content Search Suggestion is displayed and All Content Search Suggestion is at the top");
		log.info("INFO : "+ "Verify that "+ text + " in All Content Search Suggestion is displayed and All Content Search Suggestion is at the top" );
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", text))).getText(),
				globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.searchedSuggestionList)).get(0).getText(),
				text + "in All Content Search Suggestion is displayed and All Content Search Suggestion is at the top");

		logger.strongStep("Verify the order of Suggestions in Search");
		log.info("INFO : Verify the order of Suggestions in Search");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify user is able to click on 'Communities' Filter button in Search Result</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Navigate to My Communities page</li>
	 *<li><B>Step:</B> Type Community name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Verify:</B> Verify search result are displayed as per searched community name</li>
	 *<li><B>Step:</B> Click on Communities Filter button</li>
	 *<li><B>Verify:</B> Verify the expected result is visible when community filter is clicked</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T674</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"} )
	public void verifyCommunitiesFilterLink() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = globalSearchUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.addMember(member)
													.description(Data.getData().commonDescription)
													.build();
		
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		String communityTitle = comAPI.getTitle();
		
		// Run indexer for communities
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Select Communities in nav menu");
        log.info("INFO: Select Communities in nav menu");       
        AppNavCnx8.COMMUNITIES.select(globalSearchUI);

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type Created community name in Search textBox");
		log.info("INFO: Type Created community name in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(community.getName());
		
		logger.strongStep("Click on "+ communityTitle + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ communityTitle + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", community.getName())), 3,
				" - in All content option");
		
		logger.strongStep("Select Communities filter button and verify result");
		log.info("INFO: Select Communities filter button and verify result");
		clickAndVerifyCommunityFilter(communityTitle);
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Pagination navigation is not displayed when search result is less than results per page/li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Verify:</B> Verify Result are displayed matching with searched keyword</li>
	 *<li><B>Step:</B> Click on 'Filter By' option and change value to 'My Content'</li>
	 *<li><B>Step:</B> Change 'Results Per Page' value to maximum number</li>
	 *<li><B>Verify:</B> Verify Pagination navigation is not displayed at the end of page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T682</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyPaginationNavigationBarVisibilty(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Verify Search result data is displayed as per Searched People name");
		log.info("INFO : Verify Search result data is displayed as per Searched People name");
		cnxAssert.assertTrue(globalSearchUI.isResultListMatchesWithSearchedPeople(testUser.getFirstName()),
				"All Results are matching with searched string");
		
		logger.strongStep("Verify Pagination navigation appears in the end of the page");
		log.info("INFO : Verify Pagination navigation appears in the end of the page");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.paginationBarInSearchResult), 5),
				"Pagination bar is displayed");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on 'Filter By' Button and select 'My Content' option");
		log.info("INFO: Click on 'Filter By' Button and select 'My Content' option");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.filterByDropdownIcon), 7, "FilterBy Dropdown icon");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.filterByMyContentInSearchResult)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Change filter 'Results per page' value to 100");
		log.info("INFO: Change filter 'Results per page' value to 100");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult+"~svg"), 5, "'Results per page' filter");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.perPageValue_100), 5, "Data Value '100'");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		// Scroll up to the bottom of page
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);

		logger.strongStep("Verify Pagination navigation is not visible in the end of the page");
		log.info("INFO : Verify Pagination navigation is not visible in the end of the page");
		cnxAssert.assertFalse(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.paginationBarInSearchResult), 5),
				"Pagination bar is not displayed");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Clicking on Reset All Filter Link, All Applied filters should come to their original state/li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from drop down</li>
	 *<li><B>Step:</B> Apply different filters like People, Forums, Blogs, Filter By, Sort By and Results Per Page</li>
	 *<li><B>Step:</B> Click on Reset All Filter Link</li>
	 *<li><B>Verify:</B> Verify all filters displayed as default state </li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T679</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyResetAllFilterLink(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		// Fetch Default values of multiple filters
		int selectedFiltersDefaultCount= globalSearchUI.findElements(By.cssSelector(GlobalSearchUIConstants.selectedFilterCounts)).size();
		String filterByDefaultValue= globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult)).getText();
		String sortByDefaultValue= globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.sortByButtonInSearchResult)).getText();
		String resultPerPageDefaultValue= globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult)).getText();
		
		logger.strongStep("Filter results by Clicking on People, Blogs and Forums Filter buttons");
		log.info("INFO: Filter results by Clicking on People, Blogs and Forums Filter buttons");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.blogsFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.forumFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on 'Filter By' Button and select 'My Content' option");
		log.info("INFO: Click on 'Filter By' Button and select 'My Content' option");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.filterByDropdownIcon), 5, "FilterBy Dropdown icon");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.filterByMyContentInSearchResult)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.waitForTextToBePresentInElementWd(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult), "My Content", 5);
		
		logger.strongStep("Click on 'Sort By' Button and select 'Date' option");
		log.info("INFO: Click on 'Sort By' Button and select 'Date' option");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.sortByDropdownIcon), 10);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.sortByDropdownIcon), 5, "Sort By Dropdown icon");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.sortByDateInSearchResult), 10, "Date option in dropdown");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on 'Results Per Page' Link and select value '100'");
		log.info("INFO: Click on 'Results Per Page' Link and select value '100'");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult+"~svg"), 10);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult+"~svg"), 10, "Results Per page Link");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.perPageValue_100), 10, "Data value '100'");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on 'Reset All Filter' Link");
		log.info("INFO: Click on 'Reset All Filter' Link");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.resetAllFilterButtonInSearchResult)));
		globalSearchUI.waitForTextToBePresentInElementWd(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult), filterByDefaultValue, 5);
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		int selectedFiltersActualCount= globalSearchUI.findElements(By.cssSelector(GlobalSearchUIConstants.selectedFilterCounts)).size();
		
		logger.strongStep("Validate that filters 'Selected filters count', 'Filter By', 'Sort By','Results per page' come to the default state");
		log.info("INFO : Validate that filters 'Selected filters count', 'Filter By', 'Sort By','Results per page' come to the default state");
		cnxAssert.assertEquals(selectedFiltersActualCount, selectedFiltersDefaultCount, "Filters count matches with original count");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult)).getText(),
				filterByDefaultValue, "Filter By matches with default value");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.sortByButtonInSearchResult)).getText(),
				sortByDefaultValue, "Sort By matches with default value");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult)).getText(),
				resultPerPageDefaultValue, "Result per page matches with default value");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify user is able to change filter from 'All results' to 'My content'/li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Click on 'Filter By' option and change value to 'My Content'</li>
	 *<li><B>Verify:</B> Verify Username is displayed in result</li>
	 *<li><B>Verify:</B> Verify 'My Content' is visible in 'Filter By' option</li>
	 *<li><B>Step:</B> Clear Searchbox, type 123123 and click on Search icon</li>
	 *<li><B>Verify:</B> Verify No Search result found text is displayed</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T676</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14060</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyFilterByOptionAndNoSearchResultText(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Waiting for Search results to be visible");
		log.info("INFO: Waiting for Search results to be visible");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		
		logger.strongStep("Click on 'Filter By' Button and select 'My Content' option");
		log.info("INFO: Click on 'Filter By' Button and select 'My Content' option");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.filterByDropdownIcon), 5, "FilterBy Dropdown icon");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.filterByMyContentInSearchResult)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.waitForTextToBePresentInElementWd(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult), "My Content", 5);
		
		logger.strongStep("Verify Username "+ testUser.getDisplayName() +" is displayed in search result");
		log.info("INFO : Verify Username "+ testUser.getDisplayName() +" is displayed in search result");
		cnxAssert.assertTrue(globalSearchUI.isSearchPeopleDisplayInResultList(testUser.getDisplayName()), "People is displayed in result");
		
		logger.strongStep("Verify option 'My Content' is selected in 'Filter By' option");
		log.info("INFO : Verify option 'My Content' is selected in 'Filter By' option");
		String filterByActualValue= globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.filterByButtonInSearchResult)).getText();
		cnxAssert.assertEquals(filterByActualValue, "My Content", "'My Content' option is selected");
		
		logger.strongStep("Type some number in searchbox andverify that no search result found text is displayed");
		log.info("INFO : Type some number in searchbox andverify that no search result found text is displayed");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).click();
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).clear();
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys("123123");
		globalSearchUI.clickLinkWd(By.cssSelector(GlobalSearchUIConstants.searchButton), "Search button icon");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.noSearchResultFound), 5);
		cnxAssert.assertTrue(globalSearchUI.isElementDisplayedWd(By.xpath(GlobalSearchUIConstants.noSearchResultFound)),
				"No search Result found text is displayed");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify user is able to change 'Results per page' filter/li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Click on 'Results per page' option and change value to '25'</li>
	 *<li><B>Verify:</B> Verify Results per page value has been changed to 25</li>
	 *<li><B>Verify:</B> Verify Results count is less than or equals to 25</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T678</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14060</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyResultsPerPageFilter(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Waiting for Search results to be visible");
		log.info("INFO: Waiting for Search results to be visible");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 8);
		
		logger.strongStep("Click on 'Results Per Page' Link and select value '25'");
		log.info("INFO: Click on 'Results Per Page' Link and select value '25'");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult), 7);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult)).click();
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.perPageValue_25), 7);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.perPageValue_25)).click();
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Results per Page value is changed to '25'");
		log.info("INFO : Verify Results per Page value is changed to '25'");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.resultPerPageFilterInSearchResult)).getText(),
				"25", "value '25' is selected in Result per page");
		
		logger.strongStep("Verify Search results are less than or equals to the selected Per page value '25'");
		log.info("INFO : Verify Search results are less than or equals to the selected Per page value '25'");
		cnxAssert.assertTrue(globalSearchUI.isCountOfResultsLessThanOrEqualsToPerPageValueForSearchPeople(25), "Result counts are there as expected");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify user is able to change 'Sort By' filter option from Relevance to Date /li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Click on 'Sort By' option and change value to 'Date'</li>
	 *<li><B>Verify:</B> Verify Value 'Date' is selected in 'Sort By' option</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T677</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifySortByFilter(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Waiting for Search results to be visible");
		log.info("INFO: Waiting for Search results to be visible");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		
		logger.strongStep("Click on 'Sort By' Button and select 'Date' option");
		log.info("INFO: Click on 'Sort By' Button and select 'Date' option");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.sortByButtonInSearchResult), 5, "Sort By button");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.sortByDateInSearchResult), 5, "Date option in dropdown");
		
		logger.strongStep("Verify value 'Date' is selected in 'Sort By' filter");
		log.info("INFO : Verify value 'Date' is selected in 'Sort By' filter");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.sortByButtonInSearchResult)).getText(),
				"Date", "'Date' is visible in 'Sort By' filter");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to validate that pagination can be change from first page to last page and vice versa /li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page and wait for pagination to be visible</li>
	 *<li><B>Step:</B> Click on 'Go to last page' icon</li>
	 *<li><B>Verify:</B> Verify pagination is changed from first page to last page</li>
	 *<li><B>Step:</B> Click on 'Go to first page' icon</li>
	 *<li><B>Verify:</B> Verify pagination is changed from last page to first page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T684</li>
	 *</ul>
	 */
	// Fixed for Non-component server but not enabling for bvt server due to defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-13722
	// TO DO- Enable the test for non-component server bvtdb2 once defect gets fixed
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyNavigationToFirstPageAndLastPageFromPagination(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		verifyPagination();
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to validate that pagination can be change from first page to next page and vice versa /li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first  name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page and wait for pagination to be visible</li>
	 *<li><B>Step:</B> Click on 'Go to next page' icon</li>
	 *<li><B>Verify:</B> Verify pagination is changed from first page to next page</li>
	 *<li><B>Step:</B> Click on 'Go to previous page' icon</li>
	 *<li><B>Verify:</B> Verify pagination is changed from second page to first page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T683</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyNavigationToNextPageAndPreviousPageFromPagination(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first  name in Global Search textBox");
		log.info("INFO: Type User's first  name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Go to bottom of the page and wait for pagination to be visible");
		log.info("INFO: Go to bottom of the page and wait for pagination to be visible");
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.paginationBarInSearchResult), 10);
		
		logger.strongStep("Click on 'Go to next page' icon in pagination navigation");
		log.info("INFO: Click on 'Go to next page' icon in pagination navigation");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.pagination_nextPage), 5, "'Next page' icon");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify second page is selected in pagination navigation");
		log.info("INFO : Verify second page is selected in pagination navigation");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.pagination_selectedNumber)).getText(),
				"2", "Second page is selected");
		
		logger.strongStep("Click on 'Go to previous page' icon in pagination navigation");
		log.info("INFO: Click on 'Go to previous page' icon in pagination navigation");
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.pagination_previousPage), 5, "'Previous page' icon");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify first page is selected in pagination navigation");
		log.info("INFO : Verify first page is selected in pagination navigation");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.pagination_selectedNumber)).getText(),
				"1", "First page is selected");
		
		globalSearchUI.endTest();

	}			
		

	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Search Results for All Content</li>
	 *<li><B>Step:</B>Login to Homepage page and Toggle to new UI</li>
	 *<li><B>Step:</B>Type valid User display name in search text box and click on it</li>
	 *<li><B>Verify:</B>Verify that search result page for searched display name gets opened</li>
	 *<li><B>Step:</B>Load Homepage component again</li>
	 *<li><B>Step:</B>Hover on search text box and click on it</li>
	 *<li><B>Step:</B>Click on View All link of Recent Search</li>
	 *<li><B>Verify:</B>Verify that Recently Visited section is not displayed</li>
 	 *<li><B>Verify:</B>Verify that Recent Searched item is displayed</li>
	 *<li><B>Step:</B>Click on Back button</li>
	 *<li><B>Verify:</B>Verify that Recently Visited section is displayed again</li>
	 *<li><B>Step:</B>Click on last searched Username under Recently Search section of Search Dropdown</li>
	 *<li><B>Verify:</B>Verify that search result page for searched Username gets opened</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T652</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T707</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyRecentlySearchInAllContent() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = globalSearchUI.startTest();
		User UserA = cfg.getUserAllocator().getUser();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
								.tags(Data.getData().commonTag + Helper.genDateBasedRand())
								.description(Data.getData().commonDescription)
								.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Communities, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		commonUI.waitForPageLoaded(driver);
		
		log.info("INFO: Navigate to the Community using UUID and re-navigate to Homepage");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		AppNavCnx8.HOMEPAGE.select(commonUI);

		logger.strongStep("Type User name in Search textBox");
		log.info("INFO : Type User name in Search textBox");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),7);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(UserA.getDisplayName());
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.peopleTypographyInSearchSuggestion), 5);

		logger.strongStep("Click on Searched User name appended with in People in Search textBox");
		log.info("INFO : Click on Searched User name appended with in People in Search textBox");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", UserA.getDisplayName())),6);

		logger.strongStep("Verify that User is navigated to Search Result page with last searched User");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched User");
		cnxAssert.assertTrue(globalSearchUI.isResultsContainSearchedPeople(UserA.getDisplayName()),"Verify searched People on Search Result page");

		logger.strongStep("Click on searched User from the displayed list of User");
		log.info("INFO : Click on searched User from the displayed list of User");
		globalSearchUI.mouseHoverAndClickWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultForPeople.replace("PLACEHOLDER1", UserA.getFirstName()).replace("PLACEHOLDER2", UserA.getLastName()))));
		globalSearchUI.waitForPageLoaded(driver);
		globalSearchUI.fluentWaitElementVisible(ProfilesUIConstants.RecentupdateComment_iFrame);
		globalSearchUI.fluentWaitTextPresent(UserA.getDisplayName());

		logger.strongStep("Load Homepage component and click on Community from Nav bar");
		log.info("INFO : Load Homepage component and click on Community from Nav bar");
		commonUI.loadComponent(Data.getData().ComponentHomepage,true);

		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		commonUI.waitForPageLoaded(driver);
		WebElement searchBox = globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);
		
		logger.strongStep("Click on View All link of Recent Search");
		log.info("INFO : Click on View All link of Recent Search");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.recentSearchViewAllLinkInSearchDropdown),5
				,"Click on Recent Search View all link");
		
		logger.strongStep("Verify Recently Visited section is not displayed");
		log.info("INFO : Verify Recently Visited section is not displayed");	
		cnxAssert.assertTrue(globalSearchUI.waitForElementInvisibleWd(By.xpath(GlobalSearchUIConstants.recentVisitedViewAllLinkInSearchDropdown), 4),
		"Verify Recently Visited View all link is not displayed");
		
		logger.strongStep("Verify Recent Searched item is displayed in Recent Search View all section");
		log.info("INFO : Verify Recent Searched item is displayed in Recent Search View all section");
		globalSearchUI.waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.recentSearchInSearchDropdown
				.replace("PLACEHOLDER", UserA.getDisplayName())),4);
		
		logger.strongStep("Click on Back button");
		log.info("INFO : Click on Back button");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.recentSearchBackButtonInDropdown)
				, 5, "click on Back button");
		
		logger.strongStep("Verify Recently Visited section is displayed");
		log.info("INFO : Verify Recently Visited section is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.xpath(GlobalSearchUIConstants.recentVisitedViewAllLinkInSearchDropdown),7),
				"Verify Recently Visited View all link is displayed");
		
		logger.strongStep("Click on last searched User listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on last searched User listed under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlySearch(UserA.getDisplayName());	
		globalSearchUI.waitForPageLoaded(driver);

		logger.strongStep("Verify that User is navigated to Search Result page with last searched community");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched communitye");
		cnxAssert.assertTrue(globalSearchUI.isResultsContainSearchedPeople(UserA.getDisplayName()),"Verify searched community on Search Result page");	
		
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify user is able to switch tag view from cloud to list/li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type random text in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Click on Cloud link in tags view</li>
	 *<li><B>Verify:</B> Verify cloud view of Tags is displayed</li>
	 *<li><B>Step:</B> Click on List link in tags view</li>
	 *<li><B>Verify:</B> Verify List view of Tags is displayed</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T680</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifySwitchingTagsFromCloudViewToListView(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String text = "test";

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type text in Global Search textBox");
		log.info("INFO: Type text in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);
		
		logger.strongStep("Click on "+ text + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ text + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", text)), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on Cloud link in tags");
		log.info("INFO: Click on Cloud link in tags");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.tags_cloudLink)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify cloud view of Tags is displayed");
		log.info("INFO : Verify cloud view of Tags is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementDisplayedWd(By.xpath(GlobalSearchUIConstants.tags_cloudView)), "Cloud view is visible");
		
		logger.strongStep("Click on List link in tags");
		log.info("INFO: Click on List link in tags");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.tags_listLink)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify List view of Tags is displayed");
		log.info("INFO : Verify List view of Tags is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementDisplayedWd(By.xpath(GlobalSearchUIConstants.tags_listView)), "List view is visible");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify User should be able to select multiple filters except All Contents filter</li>
	 *<li><B>Step:</B> Login to Communities and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from drop down</li>
	 *<li><B>Step:</B> Apply different filters like People, Forums, Blogs</li>
	 *<li><B>Verify:</B> Verify People, Forums and Blogs Filters are selected</li>
	 *<li><B>Verify:</B> Verify 'All content' filter is not selected</li>
	 *<li><B>Step:</B> Click 'All Content' filter button</li>
	 *<li><B>Verify:</B> Verify 'All Content' is the only selected filters </li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T675</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyMultipleFiltersSelectedExceptAllContentFilter(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();

		logger.strongStep("Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());

		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);

		logger.strongStep("Filter results by Clicking on People, Blogs and Forums Filter buttons");
		log.info("INFO: Filter results by Clicking on People, Blogs and Forums Filter buttons");
		globalSearchUI.waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.peopleFilterButton), 4);
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.forumFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.blogsFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		List<String> expectedFiltersNameList=new ArrayList<String>();
		expectedFiltersNameList.add(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleFilterButton)).getText());
		expectedFiltersNameList.add(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.forumFilterButton)).getText());
		expectedFiltersNameList.add(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.blogsFilterButton)).getText());

		List<WebElement> selectedFilters = globalSearchUI.findElements(By.cssSelector(GlobalSearchUIConstants.selectedFilters));
		List<String> selectedFiltersNameList = new ArrayList<String>();
		for (WebElement webElement : selectedFilters) {
			selectedFiltersNameList.add(webElement.getText());
		}

		logger.strongStep("Verify People, Forum and Blogs filters are selected");
		log.info("INFO : Verify People, Forum and Blogs filters are selected");
		cnxAssert.assertEquals(selectedFiltersNameList, expectedFiltersNameList, "Expected Filters are selected");

		logger.strongStep("Verify 'All Content' filter is not selected");
		log.info("INFO : Verify 'All Content' filter is not selected");
		cnxAssert.assertFalse(selectedFiltersNameList.contains("All Content"), "'All Content' is not selected");

		logger.strongStep("Select All Content filter button");
		log.info("INFO: Select All Content filter button");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.allContentFilterButton)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);

		logger.strongStep("Verify only All Content filter is selected");
		log.info("INFO : Verify only All Content filter is selected");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "All Content", "'All Content' is selected");

		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify user is able to select multiple tags under Tags section/li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type random text in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Click on Cloud link in tags view</li>
	 *<li><B>Step:</B> Select multiple Tag from the tag view</li>
	 *<li><B>Verify:</B> Verify expected tags are selected in cloud view</li>
	 *<li><B>Step:</B> Click on List link in tags view</li>
	 *<li><B>Verify:</B> Verify same tags are selected in List view</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T681</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyMultipleTagsSelection() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String tagName = "testtag";
		String testName = globalSearchUI.startTest();
		String entryName = testName + Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(entryName)
													.tags("testtag1, testtag2, testtag3")
													.description(Data.getData().commonDescription)
													.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryName)
												.tags(tagName + "2")
												.content("Test description for Blog")
												.build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(entryName)
		   		 								.tags(tagName + "3")
		   		 								.description("Test description for ForumTopic")
		   		 								.partOfCommunity(community)
		   		 								.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);

		logger.strongStep("create blog entry and Forum entry using API");
		log.info("INFO: create blog entry and Forum entry using API");
		apiOwner.createBlogEntry(blogEntry, comAPI);
		apiOwner.CreateForumTopic(comAPI, topic);
		
		log.info("INFO: Run Search indexer for communities, blogs and forums");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		adminService.indexNow("forums", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type text in Global Search textBox");
		log.info("INFO: Type text in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(entryName);
		
		logger.strongStep("Click on "+ entryName + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ entryName + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", entryName)), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Click on Cloud link in tags");
		log.info("INFO: Click on Cloud link in tags");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.tags_cloudLink)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Select multiple tags based on search result");
		log.info("INFO: Select multiple tags based on search result");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.tagLink.replace("PLACEHOLDER", blogEntry.getTags())), 5, "1st tag link");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.tagLink.replace("PLACEHOLDER", topic.getTags())), 5, "2nd tag link");
		
		List<String> expectedTagList=new ArrayList<String>();
		expectedTagList.add(blogEntry.getTags());
		expectedTagList.add(topic.getTags());

		List<WebElement> selectedTagCloudElement = globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.selectedTags));
		List<String> selectedCloudTagName = new ArrayList<String>();
		for (WebElement webElement : selectedTagCloudElement) {
			selectedCloudTagName.add(webElement.getText());
		}

		logger.strongStep("Verify both tags are selected in cloud view");
		log.info("INFO : Verify both tags are selected in cloud view");
		cnxAssert.assertEquals(selectedCloudTagName, expectedTagList, "Expected tags are selected");
		
		logger.strongStep("Click on List link in tags");
		log.info("INFO: Click on List link in tags");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.tags_listLink)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		List<WebElement> selectedTagListElemt = globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.selectedTags));
		List<String> selectedListTagName = new ArrayList<String>();
		for (WebElement webElement : selectedTagListElemt) {
			selectedListTagName.add(webElement.getText());
		}

		logger.strongStep("Verify same tags are selected in List view");
		log.info("INFO : Verify same tags are selected in List view");
		cnxAssert.assertEquals(selectedListTagName, expectedTagList, "Expected tags are selected");
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify 'In All Content' selection and user is able to clear all selected tag using Clear selection link</li>
	 *<li><B>Step:</B> Create a Community with tag using API</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Type Community name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Verify:</B> Verify All Content filter is selected in top filters</li>
	 *<li><B>Step:</B> Select a Tag under Tag section</li>
	 *<li><B>Verify:</B> Verify tag is selected</li>
	 *<li><B>Step:</B> Click on Clear selection link to remove the selection</li>
	 *<li><B>Verify:</B> Verify no tag is selected</li>
	 *<li><B>Step:</B> Delete Community using API</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T697</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyClearSelectionLink() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = globalSearchUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags("tag1")
													.description(Data.getData().commonDescription)
													.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Run Search indexer for communities");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type Community name in Global Search textBox");
		log.info("INFO: Type Community name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(community.getName());
		
		logger.strongStep("Click on "+ community.getName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ community.getName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", community.getName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify 'All Content' filter button is selected in top filters");
		log.info("INFO : Verify 'All Content' filter button is selected in top filters");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "All Content", "'All Content' is selected");
		
		logger.strongStep("Select a tag inside Tags");
		log.info("INFO: Select a tag inside Tags");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.tagLink.replace("PLACEHOLDER", community.getTags())), 5, "Tag link");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);

		logger.strongStep("Validate that the Tag is selected");
		log.info("INFO : Validate that the Tag is selected");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.selectedTags)).getText(), community.getTags(), "Expected tag is selected");
		
		logger.strongStep("Click on Clear selection");
		log.info("INFO: Click on Clear selection");
		globalSearchUI.clickLinkWithJavaScriptWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.clearSelectionBtn)));
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);

		logger.strongStep("Verify no tags are selected");
		log.info("INFO : Verify no tags are selected");
		cnxAssert.assertEquals(globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.selectedTags)).size(), 0, "Expected tags are selected");
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Error Message when 'To Month' is less than 'From Month' when searching in same year.</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first  name in global search box</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page to find the Dates filter</li>
	 *<li><B>Step:</B> Click on Dates dropdown and select date range option</li>
	 *<li><B>Step:</B> Type month value in 'To Month' less than value in 'From Month'</li>
	 *<li><B>Step:</B> Type same year value in 'From Year' and 'To Year' both input field</li>
	 *<li><B>Verify:</B> Verify Error message is displayed correctly</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T703</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyErrorMsgWhenToMonthLessThanFromMonthInSameYears(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String expectedErrorMsg = "From Month should be less than or equal to To Month, when searching in same year.";
		int year = Calendar.getInstance().get(Calendar.YEAR);

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first  name in Global Search textBox");
		log.info("INFO: Type User's first  name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Waiting for Search results to be visible");
		log.info("INFO: Waiting for Search results to be visible");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		
		logger.strongStep("Scroll down to the bottom of the page");
		log.info("INFO: Scroll down to the bottom of the page");
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		
		logger.strongStep("Click on Dates dropdown and select date range option");
		log.info("INFO: Click on Dates dropdown and select date range option");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 6);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 7, "Dates Dropdown");	
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.dateOptions_dateRange), 5);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.dateOptions_dateRange), 7, "Date Range Option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Type month value in 'To Month' less than value in 'From Month'");
		log.info("INFO: Type month value in 'To Month' less than value in 'From Month'");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromMonth)).sendKeys("12");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toMonth)).sendKeys("1");
		
		logger.strongStep("Type same year in 'From Year' and 'To Year' both input field");
		log.info("INFO: Type same year in 'From Year' and 'To Year' both input field");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromYear)).sendKeys(Integer.toString(year));
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toYear)).sendKeys(Integer.toString(year));
		
		logger.strongStep("Verify error message displayed while 'To Month' is less than 'From Month' when years are same");
		log.info("INFO : Verify error message displayed while 'To Month' is less than 'From Month' when years are same");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.errorMsg)).getText(), expectedErrorMsg, "Error message is displayed");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Error Message when 'To Year' value is less than 'From Year' value.</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI.</li>
	 *<li><B>Step:</B> Type user's first  name in global search box.</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown.</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page to find the Dates filter.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and select date range option.</li>
	 *<li><B>Step:</B> Type same month value in 'To Month' and 'From Month' both.</li>
	 *<li><B>Step:</B> Type Year value in 'To Year' less than value in 'From Year'.</li>
	 *<li><B>Verify:</B> Verify Error message is displayed.</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T702</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyErrorMsgWhenToYearLessThanFromYear(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String expectedErrorMsg = "From Year should be less than or equal to To Year";
		int year = Calendar.getInstance().get(Calendar.YEAR);

		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first  name in Global Search textBox");
		log.info("INFO: Type User's first  name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Waiting for Search results to be visible");
		log.info("INFO: Waiting for Search results to be visible");
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForPeople), 5);
		
		logger.strongStep("Scroll down to the bottom of the page");
		log.info("INFO: Scroll down to the bottom of the page");
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		
		logger.strongStep("Click on Dates dropdown and select date range option");
		log.info("INFO: Click on Dates dropdown and select date range option");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 6);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.dateOptions), 7, "Dates Dropdown");	
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.dateOptions_dateRange), 5);
		globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.dateOptions_dateRange), 7, "Date Range Option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Type same month value in 'To Month' and 'From Month' both input field");
		log.info("INFO: Type same month value in 'To Month' and 'From Month' both input field");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromMonth)).sendKeys("12");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toMonth)).sendKeys("12");
		
		logger.strongStep("Type Year value in 'To Year' less than value in 'From Year'");
		log.info("INFO: Type Year value in 'To Year' less than value in 'From Year'");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromYear)).sendKeys(Integer.toString(year));
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toYear)).sendKeys(Integer.toString(year-1));
		
		logger.strongStep("Verify error message displayed while 'To Year' value is less than 'From Year' value.");
		log.info("INFO : Verify error message displayed while 'To Year' value is less than 'From Year' value.");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.errorMsg)).getText(), expectedErrorMsg, "Error message is displayed");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Results when Date range is selected in Dates filter.</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI.</li>
	 *<li><B>Step:</B> Type a text in global search box.</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown.</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page to find the Dates filter.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and 'Date Range' option.</li>
	 *<li><B>Step:</B> Enter current month in 'From Month' and 'To Month' both input field</li>
	 *<li><B>Step:</B> Enter current year in 'From Year' and 'To Year' both input field</li>
	 *<li><B>Verify:</B> Verify results are displayed for current date.</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T701</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyDateRangeOptionInDatesFilter() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		SimpleDateFormat DateFormat = new SimpleDateFormat("MMM d, yyyy");	
		
		// set UTC time zone by using SimpleDateFormat class 
		DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));				
		String currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String currentMonth= Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);

		String testName = globalSearchUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription)
													.build();
		
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		String comCreationDate = DateFormat.format(comAPI.getPublished());
		String communityTitle = comAPI.getTitle();
		
		// Run indexer for communities
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type text "+ communityTitle +" in Global Search textBox");
		log.info("INFO: Type text "+ communityTitle +" in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(community.getName());
		
		logger.strongStep("Click on "+ communityTitle + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ communityTitle + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", community.getName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		log.info("INFO: Scroll down to the bottom of the page");
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Click on Dates dropdown and select 'Date Range' option");
		log.info("INFO: Click on Dates dropdown and select 'Date Range' option");	
		globalSearchUI.selectDatesFilterOption(GlobalSearchUIConstants.dateOptions_dateRange, "'Date Range' option");
		
		logger.strongStep("Type current month value in 'To Month' and 'From Month' field");
		log.info("INFO: Type current month value in 'To Month' and 'From Month' field");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromMonth)).sendKeys(currentMonth);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toMonth)).sendKeys(currentMonth);
		
		logger.strongStep("Type current year in 'From Year' and 'To Year' both input field");
		log.info("INFO: Type current year in 'From Year' and 'To Year' both input field");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromYear)).sendKeys(currentYear);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toYear)).sendKeys(currentYear);
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("arguments[0].scrollIntoView(true);", driver.getElements(GlobalSearchUIConstants.allContentFilterButton).get(0).getWebElement());
		
		logger.strongStep("Verify results displayed for current date");
		log.info("INFO : Verify results displayed for current date");
		cnxAssert.assertTrue(globalSearchUI.isSearchListContainsCurrentDate(comCreationDate), "Current date visible for results");
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Results when 'Last 7 days' selected in Dates filter on Search result page.</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI.</li>
	 *<li><B>Step:</B> Type a text in global search box.</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown.</li>
	 *<li><B>Verify:</B> Verify results associated with searched text.</li>
	 *<li><B>Step:</B> Scroll down to the bottom of page to find the Dates filter.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and 'Last 7 days' option.</li>
	 *<li><B>Verify:</B> Verify latest results are displayed.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and select date range option.</li>
	 *<li><B>Verify:</B> Verify From & To values should be associated with current date.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and 'Last 30 days' option.</li>
	 *<li><B>Verify:</B> Verify latest results are displayed.</li>
	 *<li><B>Step:</B> Click on Dates dropdown and select date range option.</li>
	 *<li><B>Verify:</B> Verify From & To values should be associated with current date.</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T699</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T704</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T700</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T705</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyLast7DaysAndLast30DaysOptionsInDatesFilter() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		Date date = new Date();
		SimpleDateFormat DateFormat = new SimpleDateFormat("MMM d, yyyy");
		SimpleDateFormat DateFormat2 = new SimpleDateFormat("MM");
		
		// set UTC time zone by using SimpleDateFormat class 
		DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		DateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String fromMonth = DateFormat2.format(date);
		String toMonth = fromMonth;
		int currentDayOfMonth = (Calendar.getInstance().get(Calendar.DATE));

		String testName = globalSearchUI.startTest();	
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription)
													.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		String commCreationDate = DateFormat.format(comAPI.getPublished());
		String communityTitle = comAPI.getTitle();

		// Run indexer for communities
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type text "+ communityTitle +" in Global Search textBox");
		log.info("INFO: Type text "+ communityTitle +" in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).click();
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(community.getName());
		
		logger.strongStep("Click on "+ communityTitle + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ communityTitle + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", community.getName())), 4,
				" - in All content option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify Results are displayed as per searched text");
		log.info("INFO : Verify Results are displayed as per searched text");
		cnxAssert.assertTrue(globalSearchUI.isResultListContainsSearchedCommunity(community.getName()), "Results matches with searched text");
		
		logger.strongStep("Scroll down to the bottom of the page");
		log.info("INFO: Scroll down to the bottom of the page");
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
		
		logger.strongStep("Click on Dates dropdown and select 'Last 7 days' option");
		log.info("INFO: Click on Dates dropdown and select 'Last 7 days' option");	
		globalSearchUI.selectDatesFilterOption(GlobalSearchUIConstants.dateOptions_last7Days, "'Last 7 days' option");
		
		logger.strongStep("Verify 'Last 7 days' is selected in Dates filter");
		log.info("INFO : Verify 'Last 7 days' is selected in Dates filter");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateOptions)).getText(), "Last 7 days", "Option selected");
		
		if(currentDayOfMonth <= 7) {
			int i = Integer.parseInt(fromMonth) - 1;
			fromMonth = String.format("%02d", i);
		}
		verifyResultAndDateRangeOption(fromMonth, toMonth, currentYear, commCreationDate);
		
		logger.strongStep("Click on Dates dropdown and select 'Last 30 days' option");
		log.info("INFO: Click on Dates dropdown and select 'Last 30 days' option");	
		globalSearchUI.selectDatesFilterOption(GlobalSearchUIConstants.dateOptions_last30Days, "'Last 30 days' option");
		
		logger.strongStep("Verify 'Last 30 days' is selected in Dates filter");
		log.info("INFO : Verify 'Last 30 days' is selected in Dates filter");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateOptions)).getText(), "Last 30 days", "Option selected");
		
		fromMonth = DateFormat2.format(date);
		if(!(currentDayOfMonth >= 30)) {
			int i = Integer.parseInt(fromMonth) - 1;
			fromMonth = String.format("%02d", i);
		}
		verifyResultAndDateRangeOption(fromMonth, toMonth, currentYear, commCreationDate);
		
		globalSearchUI.endTest();

	}
	
	private void verifyResultAndDateRangeOption(String fromMonth, String toMonth, String currentYear, String currentDate) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("arguments[0].scrollIntoView(true);", driver.getElements(GlobalSearchUIConstants.allContentFilterButton).get(0).getWebElement());
		
		logger.strongStep("Verify results displayed as per current date");
		log.info("INFO : Verify results displayed as per current date");
		cnxAssert.assertTrue(globalSearchUI.isSearchListContainsCurrentDate(currentDate), "Current date visible for results");
		
		logger.strongStep("Click on Dates dropdown and select date range option");
		log.info("INFO: Click on Dates dropdown and select date range option");	
		globalSearchUI.selectDatesFilterOption(GlobalSearchUIConstants.dateOptions_dateRange, "Date Range option");
		
		logger.strongStep("Verify current month displayed in 'From Month' and 'To Month' & current year displayed in 'From Year' and 'To Year'");
		log.info("INFO : Verify current month displayed in 'From Month' and 'To Month' & current year displayed in 'From Year' and 'To Year'");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromMonth)).getAttribute("value"), fromMonth, "From Month value is correct");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_fromYear)).getAttribute("value"), currentYear, "From Year value is correct");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toMonth)).getAttribute("value"), toMonth, "To Month value is correct");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.dateRange_toYear)).getAttribute("value"), currentYear, "To Year value is correct");
	}
	
	private void clickAndVerifyCommunityFilter(String communityTitle) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		if(!globalSearchUI.isComponentPackInstalled()) {
			logger.strongStep("Click on Community filter button");
			log.info("INFO: Click on Community filter button");
			globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.communitiesFilter_oldSearchPage), 3,
					"Communities filter button");
			globalSearchUI.waitForElementInvisibleWd(By.xpath(GlobalSearchUIConstants.loadingIcon_oldSearchPage), 6);
			
			logger.strongStep("Verify the created community is displayed in search result");
			log.info("INFO : Verify the created community is displayed in search result");
			globalSearchUI.waitForPageLoaded(driver);
			String actualCommName = globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.communitiesResult_oldSearchPage)).getText();
			cnxAssert.assertEquals(actualCommName, communityTitle, "Community Name is displayed");
		}else {
			logger.strongStep("Verify Results are displayed as per searched community");
			log.info("INFO : Verify Results are displayed as per searched community");
			cnxAssert.assertTrue(globalSearchUI.isResultListContainsSearchedCommunity(communityTitle),
					"Community is visible in search result");
			
			logger.strongStep("Click on Community filter button");
			log.info("INFO: Click on Community filter button");
			globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.communitiesFilterButton), 3,
					"Communities filter button");
			
			logger.strongStep("Verify the created community is displayed in search result");
			log.info("INFO : Verify the created community is displayed in search result");
			globalSearchUI.waitForPageLoaded(driver);
			String actualCommName = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultForCommunity)).getText();
			cnxAssert.assertEquals(actualCommName, communityTitle, "Community Name is displayed");
		}
		
	}
	
	private void verifyPagination() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		if(!globalSearchUI.isComponentPackInstalled()) {
			// Getting last page number from pagination navigation
			List<WebElement> paginationElementsCount = globalSearchUI.findElements(By.cssSelector(GlobalSearchUIConstants.pagination_elementCount_oldSearchPage));
			String lastPageNumPosition = Integer.toString(paginationElementsCount.size() - 1);
			String lastPageNum = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.pageNum_oldSearchPage.replace("PLACEHOLDER", lastPageNumPosition))).getText();
			
			logger.strongStep("Select last page number in pagination navigation");
			log.info("INFO: Select last page number in pagination navigation");
			globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.pageNum_oldSearchPage.replace("PLACEHOLDER", lastPageNumPosition)), 5 ,"Last Page");
			globalSearchUI.waitForElementInvisibleWd(By.xpath(GlobalSearchUIConstants.loadingIcon_oldSearchPage), 6);
			
			logger.strongStep("Verify last page number:"+ lastPageNum + " is selected in pagination navigation");
			log.info("INFO : Verify last page number:"+ lastPageNum + " is selected in pagination navigation");
			cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.selectedPage_oldSearchPage)).getText(),
					lastPageNum, "Last page number:"+ lastPageNum + " is selected");
			
			logger.strongStep("Select first page in pagination navigation");
			log.info("INFO: Select first page in pagination navigation");
			globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.pageNum_oldSearchPage.replace("PLACEHOLDER", "2")), 5 ,"First Page");
			globalSearchUI.waitForElementInvisibleWd(By.xpath(GlobalSearchUIConstants.loadingIcon_oldSearchPage), 6);
			
			logger.strongStep("Verify first page number is selected in pagination navigation");
			log.info("INFO : Verify first page number is selected in pagination navigation");
			cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.selectedPage_oldSearchPage)).getText(),
					"1", "First page number is selected");			
		}else {
			logger.strongStep("Go to bottom of page and wait for pagination to be visible");
			log.info("INFO: Go to bottom of page and wait for pagination to be visible");
			globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.footerAboutLink));
			globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.paginationBarInSearchResult), 10);
			
			logger.strongStep("Click on 'Go to last page' icon in pagination navigation");
			log.info("INFO: Click on 'Go to last page' icon in pagination navigation");
			globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.pagination_lastPage), 5, "'Last page' icon");
			globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
			
			logger.strongStep("Get the last page number");
			log.info("INFO: Get the last page number");
			List<WebElement> paginationElementsCount = globalSearchUI.findElements(By.cssSelector(GlobalSearchUIConstants.pagination_elementCount));
			String pagesCount = Integer.toString(paginationElementsCount.size() - 2);
			String lastPageNum = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.pagination_thirdLastNum.replace("PLACEHOLDER", pagesCount))).getText();
			
			logger.strongStep("Verify last page number:"+ lastPageNum + " is selected in pagination navigation");
			log.info("INFO : Verify last page number:"+ lastPageNum + " is selected in pagination navigation");
			cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.pagination_selectedNumber)).getText(),
					lastPageNum, "Last page number:"+ lastPageNum + " is selected");
			
			logger.strongStep("Click on 'Go to first page' icon in pagination navigation");
			log.info("INFO: Click on 'Go to first page' icon in pagination navigation");
			globalSearchUI.clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.pagination_firstPage), 5, "'First page' icon");
			globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
			
			logger.strongStep("Verify first page number is selected in pagination navigation");
			log.info("INFO : Verify first page number is selected in pagination navigation");
			cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.pagination_selectedNumber)).getText(),
					"1", "First page number is selected");
		}		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> User should be able to slide using People slider and see people search results..</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI.</li>
	 *<li><B>Step:</B> Type a text in global search box.</li>
	 *<li><B>Step:</B> Select Search in all content option from dropdown.</li>
	 *<li><B>Step:</B> Slide through People slider.</li>
	 *<li><B>Step:</B> Increase the People slider</li>
	 *<li><B>Verify:</B> Verify list of people should be increased to filter </li>
	 *<li><B>Step:</B> Decrease the People slider </li>
	 *<li><B>Verify:</B> Verify list of people should be decreased to filter </li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/129670</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPeopleSlider(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		globalSearchUI.startTest();
			
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in All content option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in All content option");
		
		logger.strongStep("Click on in All content option from suggestion list");
		log.info("INFO: Click on in All content option from suggestion list");
		globalSearchUI.waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.tags), 5);
		globalSearchUI.scrollToElementWithJavaScriptWd(By.xpath(GlobalSearchUIConstants.tags));
		
		// Get list of people before increasing slider
		int listOfPeopleBeforeSlidingForward = getListOfPeopleFilterOnSlider();
		log.info("INFO: List of people before increasing slider :"+listOfPeopleBeforeSlidingForward);
		
		logger.strongStep(" Increase the People slider");
		log.info("INFO:  Increase the People slider");
		Actions act = new Actions((WebDriver) driver.getBackingObject());
		act.dragAndDropBy(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleSlider)), 100, 125).perform();
		WebDriverWait wait = globalSearchUI.getWebDriverWait(10);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(GlobalSearchUIConstants.peopleSlider),listOfPeopleBeforeSlidingForward));
		 
		// Get list of people after increasing slider
		int listOfPeopleAfterSlidingForward = getListOfPeopleFilterOnSlider();
		log.info("INFO: list of people after sliding forward: "+listOfPeopleAfterSlidingForward);
		
		logger.strongStep("Verify list of people should be increased to filter");
		log.info("INFO: Verify list of people should be increased to filter");
		cnxAssert.assertTrue(listOfPeopleBeforeSlidingForward<listOfPeopleAfterSlidingForward, "List of people filter is increased");
		
		// Get list of people before decreasing slider
		int listOfPeopleBeforeSlidingBackward = getListOfPeopleFilterOnSlider();
		log.info("INFO: list of people before sliding backward: "+listOfPeopleBeforeSlidingBackward);
		
		logger.strongStep(" Decrease the People slider");
		log.info("INFO:  Decrease the People slider");
		act.dragAndDropBy(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleSlider)), -70, 125).perform();
		wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.xpath(GlobalSearchUIConstants.peopleSlider), listOfPeopleBeforeSlidingBackward));

		//  Get list of people after decreasing slider
		int listOfPeopleAfterSlidingBackward = getListOfPeopleFilterOnSlider();
		log.info("INFO: list of people after sliding backward: "+listOfPeopleAfterSlidingBackward);
		
		logger.strongStep("Verify list of people should be decreased to filter");
		log.info("INFO: Verify list of people should be decreased to filter");
		cnxAssert.assertTrue(listOfPeopleBeforeSlidingBackward>listOfPeopleAfterSlidingBackward, "List of people filter is decreased");

		globalSearchUI.endTest();

	}

	/**
	 * Get list of people filter
	 * 
	 * @return
	 */
	private int getListOfPeopleFilterOnSlider() {
		int listOfPeopleAftrUsingslider;
		List<WebElement> list = globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.peopleFilters));
		listOfPeopleAftrUsingslider = list.size();
		return listOfPeopleAftrUsingslider;
	}

}
