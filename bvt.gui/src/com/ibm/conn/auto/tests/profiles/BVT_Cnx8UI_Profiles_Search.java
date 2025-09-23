package com.ibm.conn.auto.tests.profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.blogs.BVT_Cnx8UI_Blogs;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;

public class BVT_Cnx8UI_Profiles_Search  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Blogs.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	CommonUICnx8 commonUI;
	GlobalSearchCnx8 globalSearchUI;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
	}
	

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
	}
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a profile page</li>
	 *<li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the text in Search People Suggestion at the top</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T610</li>
	 *</ul>
	 */
	
	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"},enabled=true)
	public void verifySearchSuggestionOrderAndDefaultAttributes()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in People", text+" - in All Content", text+" - in Communities", text+" - in Files"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		globalSearchUI.startTest();

		logger.strongStep("Load Profiles page, login to Connections and toggle to new UI");
		log.info("INFO : Load Profiles page, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentProfiles);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);
		
		logger.strongStep("Verify that "+ text + " in People Search Suggestion is displayed and People Search Suggestion is at the top");
		log.info("INFO : "+ "Verify that "+ text + " in People Search Suggestion is displayed and People Search Suggestion is at the top");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleInSearchSuggestion.replace("PLACEHOLDER", text))).getText(),
				globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.searchedSuggestionList)).get(0).getText(),
				text + "in People Search Suggestion is displayed and People Search Suggestion is at the top");

		logger.strongStep("Verify the order of Suggestions in Search");
		log.info("INFO : Verify the order of Suggestions in Search");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
		
		globalSearchUI.endTest();

	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Search Result for Profiles</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type random user name in search text box</li>
	 *<li><B>Verify:</B>Verify that search user is displayed under People typography</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T650</li>
	 *</ul>
	 */
	
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifySearchResultForPeople()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		User testUser1 = cfg.getUserAllocator().getUser();

		globalSearchUI.startTest();

		logger.strongStep("Load Home page, login to Connections and toggle to new UI");
		log.info("INFO : Load Home page, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),12),
				"Search TextBox is displayed");

		logger.strongStep("Type a User1 display name in Search textBox");
		log.info("INFO : Type a User1 display name in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser1.getDisplayName());
		
		logger.strongStep("Verify the searched User is displayed under People typography");
		log.info("INFO : Verify the searched User is displayed under People typography");
		cnxAssert.assertTrue(globalSearchUI.isSearchedPeopleDisplayInPeopleTypography(testUser1.getDisplayName()),
				"Verify Searvch people is displayed in typography");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Quick Search - Verify Recently Search Results for Profiles</li>
	 *<li><B>Stpe:</B>Load Profile component</li>
	 *<li><B>Step:</B>Login to Profiles page and Toggle to new UI</li>
	 *<li><B>Step:</B>Type valid User display name in search text box and click on it</li>
	 *<li><B>Verify:</B>Verify that search result page for profiles gets opened</li>
	 *<li><B>Step:</B>Load Profiles component again</li>
	 *<li><B>Step:</B>Click on search text box and click on it</li>
	 *<li><B>Step:</B>Click on last searched User display name under Recently Search section of Search Dropdown</li>
	 *<li><B>Verify:</B>Verify that search result page for profiles gets opened</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T652</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyRecentlySearchPeople() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();

		User UserA = cfg.getUserAllocator().getUser();

		logger.strongStep("Load Profiles, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		log.info("INFO : Load Profiles, login to Connections and toggle to new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentProfiles);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		commonUI.waitForPageLoaded(driver);

		logger.strongStep("Type User name in Search textBox");
		log.info("INFO : Type User name in Search textBox");
		globalSearchUI.waitForClickableElementWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),7);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(UserA.getDisplayName());
		globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.peopleTypographyInSearchSuggestion), 5);

		logger.strongStep("Click on Searched User name appended with in People in Search textBox");
		log.info("INFO : Click on Searched User name appended with in People in Search textBox");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.peopleInSearchSuggestion.replace("PLACEHOLDER", UserA.getDisplayName())),6);

		logger.strongStep("Verify that User is navigated to Search Result page with last searched User");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched User");
		cnxAssert.assertTrue(globalSearchUI.isResultsContainSearchedPeople(UserA.getDisplayName()),"Verify searched People on Search Result page");
		
		logger.strongStep("Click on searched User from the displayed list of User");
		log.info("INFO : Click on searched User from the displayed list of User");
		globalSearchUI.mouseHoverAndClickWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultForPeople.replace("PLACEHOLDER1", UserA.getFirstName()).replace("PLACEHOLDER2", UserA.getLastName()))));
		globalSearchUI.waitForElementVisibleWd(globalSearchUI.createByFromSizzle(ProfilesUIConstants.ProfilePhoto),7);

		logger.strongStep("Load Profiles component");
		log.info("INFO : Load Profiles component");
		AppNavCnx8.PROFILE.select(commonUI);

		logger.strongStep("Click on Search textBox");
		log.info("INFO : Click on Search textBox");
		commonUI.waitForPageLoaded(driver);
		WebElement searchBox = globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 8);
		globalSearchUI.scrollToElementWithJavaScriptWd(searchBox);
		globalSearchUI.mouseHoverAndClickWd(searchBox);
		
		logger.strongStep("Click on last searched User display name listed under Recently visited section of Global search dropdown");
		log.info("INFO : Click on last searched User display name under Recently visited section of Global search dropdown");
		globalSearchUI.clickItemUnderRecentlySearch(UserA.getDisplayName());	
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that User is navigated to Search Result page with last searched User display name");
		log.info("INFO : Verify that User is navigated to Search Result page with last searched User display name");
		cnxAssert.assertTrue(globalSearchUI.isResultsContainSearchedPeople(UserA.getDisplayName()),"Verify searched community on Search Result page");

		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify User is able to see people list when select "in people" option in the quick search 
	 *	and Rank my content higher option on search result page is unchecked by default/li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user's first name in global search box</li>
	 *<li><B>Step:</B> Select Search 'in People' option from dropdown</li>
	 *<li><B>Verify:</B> Verify People filter button is selected by default</li>
	 *<li><B>Verify:</B> Verify Search result contains People name list</li>
	 *<li><B>Verify:</B> Verify 'Rank my content higher' is unchecked by default on search result page</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/129920</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T692</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyPeopleSearchResultAndRankMyContentHigherOption(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's first name in Global Search textBox");
		log.info("INFO: Type User's first name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getFirstName());
		
		logger.strongStep("Click on "+ testUser.getFirstName() + " - in People option from suggestion list");
		log.info("INFO: Click on "+ testUser.getFirstName() + " - in People option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.peopleInSearchSuggestion.replace("PLACEHOLDER", testUser.getFirstName())), 4,
				" - in People option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);
		
		logger.strongStep("Verify 'People' filter button is selected");
		log.info("INFO : Verify 'People' filter button is selected");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "People", "'People' is selected");
		
		logger.strongStep("Verify results matches with searched people name");
		log.info("INFO : Verify results matches with searched people name");
		cnxAssert.assertTrue(globalSearchUI.isResultListMatchesWithSearchedPeople(testUser.getFirstName()),
				"All Results are matching with searched string");
		
		logger.strongStep("Verify 'Rank my content higher' is uncheck by default");
		log.info("INFO : Verify 'Rank my content higher' is uncheck by default");
		cnxAssert.assertFalse(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.rankMyContentHigherCheckbox)).isSelected(), "'Rank my content higher' is uncheck");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify User is able to see business card while hovering on a person in the list </li>
	 *<li><B>Step:</B> Login to Communities page and Toggle to new UI</li>
	 *<li><B>Step:</B> Type user name in global search box</li>
	 *<li><B>Step:</B> Select Search 'in People' option from dropdown</li>
	 *<li><B>Verify:</B> Verify People filter button is selected by default</li>
	 *<li><B>Step:</B> Mouse hover over the person in the list</li>
	 *<li><B>Verify:</B> Verify Business card is visible</li>
	 *<li><B>Verify:</B> Verify Business card is displayed for expected person</li>
	 *<li><B>Step:</B> Click on person name in the list</li>
	 *<li><B>Verify:</B> Verify User's Profile page is displayed</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/129925</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T694</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"} )
	public void verifyUserBusinessCardAndProfilePage(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		globalSearchUI.startTest();
			
		logger.strongStep("Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		log.info("INFO: Load Communities, login to Connections and toggle to new UI" + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().ComponentCommunities);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);

		logger.strongStep("Type User's name in Global Search textBox");
		log.info("INFO: Type User's name in Global Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(testUser.getDisplayName());
		
		logger.strongStep("Click on "+ testUser.getDisplayName() + " - in People option from suggestion list");
		log.info("INFO: Click on "+ testUser.getDisplayName() + " - in People option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.peopleInSearchSuggestion.replace("PLACEHOLDER", testUser.getDisplayName())), 4,
				" - in People option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);
		
		logger.strongStep("Verify selected filter and open Business card for user");
		log.info("INFO: Verify selected filter and open Business card for user");
		String userName = OpenBusinessCard();
		
		logger.strongStep("Verify business card should appear");
		log.info("INFO: Verify business card should appear");		
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.bizCard), 5);
		WebElement bizCard = globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.bizCard));
		cnxAssert.assertTrue(bizCard.isDisplayed(), "Business card is visible");
		
		logger.strongStep("Verify Business card displayed for expected person");
		log.info("INFO : Verify Business card displayed for expected person");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.bizCardTitle)).getText(), userName, "Biz card visible for expected user");
		
		logger.strongStep("Click on Person name in Search result list");
		log.info("INFO :Click on Person name in Search result list");
		if(!globalSearchUI.isComponentPackInstalled()) {
			globalSearchUI.mouseHoverAndClickWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleResults_oldSearchPage)));
		}else {
			WebElement userNameElement = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultForPeople.replace("PLACEHOLDER1", testUser.getFirstName()).replace("PLACEHOLDER2", testUser.getLastName())));
			globalSearchUI.mouseHoverAndClickWd(userNameElement);
		}	
		globalSearchUI.waitForPageLoaded(driver);
		
		logger.strongStep("Verify User is redirected to the person's My profile page");
		log.info("INFO : Verify User is redirected to the person's My profile page");
		globalSearchUI.waitForElementVisibleWd(globalSearchUI.createByFromSizzle(ProfilesUIConstants.EditMyProfileBtn), 5);
		cnxAssert.assertTrue(globalSearchUI.isElementPresentWd(globalSearchUI.createByFromSizzle(ProfilesUIConstants.EditMyProfileBtn)), "User's profile page is displayed");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify No search page visible on empty search</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	 *<li><B>Step:</B> Click on Search button in global search</li>
	 *<li><B>Verify:</B> Verify Homepage is displayed</li>
	 *<li><B>Verify:</B> Verify Search result page is not displayed</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"} )
	public void verifyEmptyGlobalSearch(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		globalSearchUI.startTest();
		
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO: Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Wait for Global Search TextBox to be visible");
		log.info("INFO : Wait for Global Search TextBox to be visible");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
		
		logger.strongStep("Click on Search Icon without entring any text in search text box");
		log.info("INFO : Click on Search Icon without entring any text in search text box");
		globalSearchUI.clickLinkWd(By.cssSelector(GlobalSearchUIConstants.searchButton), "Search button icon");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 7);
		
		logger.strongStep("Verify Homepage is visible and Search result page is not displayed");
		log.info("INFO : Verify Homepage is visible and Search result page is not displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementDisplayedWd(By.cssSelector(HomepageUIConstants.topUpdatesLink)), "Homepage is visible");
		cnxAssert.assertFalse(globalSearchUI.isTextPresentWd("No search result found for given search term"), "Result page is not visible");
		
		globalSearchUI.endTest();
	}
	
	private String OpenBusinessCard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String userName;
		
		if(!globalSearchUI.isComponentPackInstalled()) {
			logger.strongStep("Verify 'Profiles' is selected in filters");
			log.info("INFO : Verify 'Profiles' is selected in filters");
			globalSearchUI.waitForPageLoaded(driver);
			cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilter_oldSearchPage)).getText(), "Profiles", "'Profiles' is selected");

			logger.strongStep("Mouse hover over Person name in Search result list");
			log.info("INFO : Mouse hover over Person name in Search result list");
			globalSearchUI.mouseHoverWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleResults_oldSearchPage)));
			userName = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.peopleResults_oldSearchPage)).getText();		
		}else {
			logger.strongStep("Verify 'People' is selected in top filters label");
			log.info("INFO : Verify 'People' is selected in top filters label");
			globalSearchUI.waitForPageLoaded(driver);
			cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "People", "'People' is selected");

			logger.strongStep("Mouse hover over Person name in Search result list");
			log.info("INFO : Mouse hover over Person name in Search result list");
			globalSearchUI.waitForElementsVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultForPeople.replace("PLACEHOLDER1", testUser.getFirstName()).replace("PLACEHOLDER2", testUser.getLastName())), 5);
			globalSearchUI.mouseHoverWd(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultsForPeople)));
			userName = globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultsForPeople)).getText();
		}
		return userName;
	}
}
