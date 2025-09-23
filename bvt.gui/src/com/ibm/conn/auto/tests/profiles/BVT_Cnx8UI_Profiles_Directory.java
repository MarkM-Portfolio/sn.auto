package com.ibm.conn.auto.tests.profiles;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.UnsupportedEncodingException;
import java.util.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;

public class BVT_Cnx8UI_Profiles_Directory  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Profiles_Directory.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, searchUser,adminUser;
	private ProfilesUICnx8 profilesUICnx8;
	private SearchAdminService adminService;
	private ProfilesUI ui;
	private String serverURL; 
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		searchUser = cfg.getUserAllocator().getUser();
		adminUser=cfg.getUserAllocator().getAdminUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser.getEmail(), testUser.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, searchUser.getEmail(), searchUser.getPassword());

		try {
			String components = "evidence,graph,manageremployees,tags,taggedby,communitymembership"; //
			adminService.sandIndexNow(components, adminUser.getAttribute(cfg.getLoginPreference()),adminUser.getPassword());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		profilesUICnx8 = new ProfilesUICnx8(driver);
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify directory page for people directory</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left nav panel</li>
	 * <li><B>Step:</B> Select 'Directory' tab</li>
	 * <li><B>Verify:</B> Verify People you may know widget is displayed</li>
	 * <li><B>Verify:</B> Verify Organization tag section is displayed</li>
	 * <li><B>Verify:</B> Verify that 'Directory' page is displayed</li>
	 * <li><B>Step:</B> Enter user name in Search directory and validate correct users are displayed</li>
	 * <li><B>Step:</B> Click on 'Invite to Connect' button</li>
	 * <li><B>Verify:</B> Verify 'Invite To My Network' pop up is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128920</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyDirectoryPage() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		logger.strongStep("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(adminUser,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'Directory' tab");
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		log.info("INFO: Verify People you may know widget is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.cssSelector(ProfilesUIConstants.PYMKwidget), 4),"People you may know widget is displayed");
		
		log.info("INFO: Verify Organization tag section is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.cssSelector(ProfilesUIConstants.orgnizationTagsSection), 4),"Organization tag section is displayed");
				
		logger.strongStep("INFO: Verify that 'Directory' page is displayed");
		cnxAssert.assertTrue(profilesUICnx8.waitForTitleIsPresentWd("Directory - Profiles", 4),"Directoty page is displayed");
				
		log.info("INFO: Enter user name in Search directory and validate correct users are displayed");
		cnxAssert.assertTrue(ui.isDirectorySearchResultsMatching(testUser),"Matching users are returned in search directory");

		log.info("INFO: Click on 'Invite to Connect' button");
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 4);
		profilesUICnx8.scrollToElementWithJavaScriptWd(ui.findElement(By.xpath(ProfilesUIConstants.doyouknowActions)));
		profilesUICnx8.clickLinkWaitWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 3,"Click Invite to connect button");

		log.info("INFO: Verify 'Invite To My Network' pop up is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ProfilesUIConstants.InviteToMyNetworkDailogueBox), 4),"Invite to Network dialogue box is displayed");
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify directory full search</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left nav panel</li>
	 * <li><B>Step:</B> Select 'Directory' tab</li>
	 * <li><B>Verify:</B> Select 'Display full search options'</li>
	 * <li><B>Step:</B> Verify directory full search options</li>
	 * <li><B>Step:</B> Enter user name to be searched for in Display Name</li>
	 * <li><B>Verify:</B> Verify that directory full search returns matching names in result</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128921</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyDirectoryFullSearch() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		logger.strongStep("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'Directory' tab");
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		logger.strongStep("INFO: Select 'Display full search options'");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.directoryFullSearch),4, "Click directory full search");
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.hideFullSearchLink), 4);
		
		logger.strongStep("INFO: Verify directory full search options");

		List<String> expOptionsList = new ArrayList<>(Arrays.asList("Keyword:", "Display Name:", "Given Name:", "Family Name:", "Tags:", "Job Title:",
				"About Me:", "Background:", "Organization or Company:", "City:", "State:", "Country:", "Email address:",
				"Phone Number:"));

		List<WebElement> directoryFullSearchOptions = ui.findElements(By.cssSelector(ProfilesUIConstants.directoryFullSearchOptions));
		List<String> actualOptionsList = new ArrayList<>();

		for (WebElement option : directoryFullSearchOptions) {
			String label = option.getText();
			actualOptionsList.add(label);
		}
		// Verify list of actual options on full search is equals to the list of expected options
		cnxAssert.assertTrue(actualOptionsList.equals(expOptionsList),"All options are present for full directory search");
		
		logger.strongStep("INFO: Enter user name to be searched for in Display Name");
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.fullSearchOptionDisName),4, "Click directory tab");
		WebElement fullSearchOptionDisName = ui.findElement(By.cssSelector(ProfilesUIConstants.fullSearchOptionDisName));
		fullSearchOptionDisName.sendKeys(searchUser.getDisplayName());
		
		logger.strongStep("INFO: Verify directory full search returns matching names in result");
		isDirFullSearchResultsMatching(searchUser.getDisplayName());
		profilesUICnx8.endTest();
	}

	/**
	 * Checking if full directory search returns matching results
	 * @param userToBeSearched
	 * @return boolean
	 */
	private boolean isDirFullSearchResultsMatching(String userToBeSearched) {

		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.button_Search));
		profilesUICnx8.clickLinkWd(By.cssSelector(ProfilesUIConstants.button_Search), "Click search button");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.profileSearchResultHeader), 3);
		List<WebElement> fullSearchResult = ui.findElements(By.xpath(ProfilesUIConstants.directoryFullsearchResult));
		boolean check = true;
		for (WebElement result : fullSearchResult) {
			String userName = result.getText();
			if (!(userName.equals(userToBeSearched))) {
				check = false;
				break;
			}
		}
		return check;

	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify directory full search result page layout and pagination</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left nav panel</li>
	 * <li><B>Step:</B> Select 'Directory' tab</li>
	 * <li><B>Step:</B> Select 'Display full search options'</li>
	 * <li><B>Step:</B> Enter user name to be searched for in Display Name</li>
	 * <li><B>Step:</B> Click search button</li>
	 * <li><B>Verify:</B> Verify that directory full search returns matching names in result</li>
	 * <li><B>Verify:</B> Verify header text "Profile search results for Display Name: 'searched user's name here'" should be displayed</li>
	 * <li><B>Verify:</B> Verify 'Directory' tab is selected</li>
	 * <li><B>Verify:</B> Verify 'Sort by:', 'Relevance','Display name','Family name' is present on search result page</li>
	 * <li><B>Verify:</B> Verify pagination is displayed at the bottom of search result page</li>
	 * <li><B>Verify:</B> Verify 'Result per page' dropdown along with default value '10' is displayed on page</li>
	 * <li><B>Step:</B> Click last page icon '>|'</li>
	 * <li><B>Verify:</B> Verify pagination is changed from first page to last page</li>
	 * <li><B>Step:</B> Click first page icon '|<'</li>
	 * <li><B>Verify:</B> Verify pagination is changed from last page to first page</li>
	 * <li><B>Step:</B> Click next page icon '>'</li>
	 * <li><B>Verify:</B> Verify pagination is changed from first page to second page</li>
	 * <li><B>Step:</B> Click previous page icon '<'</li>
	 * <li><B>Verify:</B> Verify pagination is changed from second page to first page</li>
	 * <li><B>Step:</B> Select drop down 'Result per page'</li>
	 * <li><B>Verify:</B> Verify pagination is changed from second page to first page</li>
	 * <li><B>Step:</B> Select any random value from 'Result per page' drop down</li>
	 * <li><B>Verify:</B> Verify that number of records on search result page should be similar to the selected value</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128921</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2","cnx8ui-level2" })
	public void dirFullSearchResultPageWithPagination()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Select 'People' from left nav panel");
		logger.strongStep("Select3 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		log.info("INFO: Select 'Directory' tab");
		logger.strongStep("Select 'Directory' tab");
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab), 8);
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		log.info("INFO: Select 'Display full search options'");
		logger.strongStep("Select 'Display full search options'");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.directoryFullSearch), 8);
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(By.xpath(ProfilesUIConstants.directoryFullSearch)));
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.hideFullSearchLink), 4);
		
		log.info("INFO: Enter user name to be searched for in Display Name");
		logger.strongStep("Enter user name to be searched for in Display Name");
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.fullSearchOptionDisName),4, "Click directory tab");
		profilesUICnx8.typeWithDelayWd(searchUser.getFirstName(), By.cssSelector(ProfilesUIConstants.fullSearchOptionDisName));
		
		log.info("INFO: Click search button");
		logger.strongStep("Click search button");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.button_Search));
		profilesUICnx8.clickLinkWd(By.cssSelector(ProfilesUIConstants.button_Search), "Click search button");
		
		log.info("INFO: Verify header text 'Profile search results for Display Name: "+searchUser.getFirstName()+" is displayed");
		logger.strongStep("Verify header 'Profile search results for Display Name: "+searchUser.getFirstName()+" is displayed");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.profileSearchResultHeader), 4);
		WebElement searchInfo = ui.findElement(By.xpath(ProfilesUIConstants.profileSearchResultHeader));
		cnxAssert.assertEquals(searchInfo.getText(), "Profile search results for Display Name: "+searchUser.getFirstName(),"Profile search results for Display Name: "+searchUser.getFirstName());
		
		log.info("INFO: Verify 'Directory' tab is selected");
		logger.strongStep("Verify 'Directory' tab is selected");
		cnxAssert.assertTrue(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)).getAttribute("aria-label").contains("selected"), "'Directory' tab is selected");
	
		log.info("INFO: Verify 'Sort by:', 'Relevance','Display name','Family name' is present on search result page");
		logger.strongStep("Verify 'Sort by:', 'Relevance','Display name','Family name' is present on search result page");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.sortBy)),"'Sort by:' is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.relevance)),"'Relevance'is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.displayName)),"'Display name' is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.familyName)),"'Family name' is present");
		
		log.info("INFO: Verify pagination is displayed at the bottom of search result page");
		logger.strongStep("Verify pagination is displayed at the bottom on search result page");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.pagination));
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.cssSelector(ProfilesUIConstants.pagination)),"Pagination is displayed");
		
		log.info("INFO: Verify 'Result per page' dropdown along with default value '10' is displayed on page");
		logger.strongStep("Verify 'Result per page' dropdown along with default value '10' is displayed on page");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.cssSelector(ProfilesUIConstants.resultPerPage)),"'Result per page' dropdown is displayed on page");
		Select resultPerPage = new Select(profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.resultPerPageDropdown)));
		cnxAssert.assertTrue(resultPerPage.getFirstSelectedOption().getText().equals("10"),"'Dropdown default value '10' is displayed on page");
		
		log.info("INFO: Click last page icon '>|'");
		logger.strongStep("Click last page icon '>|'");
		List<WebElement> linkToPages = ui.findElements(By.cssSelector(ProfilesUIConstants.pullCenter));
		int pages = linkToPages.size();
		String lastPageNumber = linkToPages.get(pages-1).getText();
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.lastPageIcon),4,"click");
		profilesUICnx8.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from first page to last page");
		logger.strongStep(" Verify pagination is changed from first page to last page");
		cnxAssert.assertTrue(ui.findElement(By.xpath(ProfilesUI.getPageNumberAt(Integer.parseInt(lastPageNumber)))).getAttribute("aria-disabled").contains("true"),"Pagination is changed from first page to last page");
		
		log.info("INFO: Click first page icon '|<'");
		logger.strongStep("Click first page icon '|<'");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.pagination));
		profilesUICnx8.waitForClickableElementWd(By.cssSelector(ProfilesUIConstants.firstPageIcon), 5);
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.firstPageIcon),4,"click");
		profilesUICnx8.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from last page to first page");
		logger.strongStep(" Verify pagination is changed from last page to first page");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.pagination));
		cnxAssert.assertTrue(ui.findElement(By.xpath(ProfilesUI.getPageNumberAt(1))).getAttribute("aria-disabled").contains("true"),"Pagination is changed from last page to first page");
		
		log.info("INFO: Click next page icon '>'");
		logger.strongStep("Click next page icon '>'");
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.nextPageIcon),4,"click");
		profilesUICnx8.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from first page to second page");
		logger.strongStep(" Verify pagination is changed from first page to second page");
		cnxAssert.assertTrue(ui.findElement(By.xpath(ProfilesUI.getPageNumberAt(2))).getAttribute("aria-disabled").contains("true"),"Pagination is changed from first page to second page");
		
		log.info("INFO: Click previous page icon '<'");
		logger.strongStep("Click previous page icon '<'");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.pagination));
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.prePageIcon),4,"click");
		profilesUICnx8.waitForPageLoaded(driver);
		
		log.info("INFO: Verify pagination is changed from second page to first page");
		logger.strongStep(" Verify pagination is changed from second page to first page");
		cnxAssert.assertTrue(ui.findElement(By.xpath(ProfilesUI.getPageNumberAt(1))).getAttribute("aria-disabled").contains("true"),"Pagination is changed from second page to first page");
		
		log.info("INFO: Select dropdown 'Result per page'");
		logger.strongStep("Select dropdown 'Result per page'");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.pagination));
		
		logger.strongStep("Select any random value from 'Result per page' dropdown");
		log.info("INFO: Select value '50' from 'Result per page' dropdown");
		resultPerPage = new Select(profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.resultPerPageDropdown)));
		resultPerPage.selectByIndex(2);
		
		logger.strongStep("Verify that number of records on search result page should be similar to the selected value");
		log.info("INFO: Verify that number of records on search result page should be similar to the selected value i.e. 50");
		cnxAssert.assertTrue(profilesUICnx8.waitForNumberOfElementsToBe(By.cssSelector(ProfilesUIConstants.searchResult),50, 10),"Expected number of search records are displayed on result page");
		
		profilesUICnx8.endTest();		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the changes on directory page for People - Another user overflow</li>
	 * <li><B>Step:</B> [API] User 1 will follow User 2</li>
	 * <li><B>Step:</B> Load Profiles and login with User 1</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Select Directory tab</li>
	 * <li><B>Step:</B> Search user in directory</li>
	 * <li><B>Step:</B> Click on searched user from result</li>
	 * <li><B>Verify:</B> Verify that it should displays the user information page of that user</li>
	 * <li><B>Step:</B> Click on chat icon </li>
	 * <li><B>Verify:</B> It should redirect to chatting application like MS teams </li>
	 * <li><B>Step:</B> Close current window </li>
	 * <li><B>Step:</B> Click on three dots icon(more action) </li>
	 * <li><B>Verify:</B> It should display more action popup window </li>
	 * <li><B>Verify:</B> Popup window should displays 'Stop Following' , 'Share a File' , 'Download vCard' options on it</li> 
	 * <li><B>Step:</B> Click on 'Invite to Connect' button</li>
	 * <li><B>Verify:</B> Verify 'Invite To My Network' pop up is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T629/li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyAnotherUserProfileFromDirectory() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		 
		// User 1 will now follow User 2
		log.info("INFO: " + testUser.getDisplayName() + " follow " + searchUser.getDisplayName());
		logger.strongStep(testUser.getDisplayName() + " follow " + searchUser.getDisplayName());
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);
	
		log.info("INFO: Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		profilesUICnx8.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'Directory' tab");
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		log.info("INFO: Search user "+searchUser.getDisplayName()+" in directory");
		logger.strongStep("Search user in directory");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.directorSearchBox), 4);
		profilesUICnx8.typeWithDelayWd(searchUser.getDisplayName(), By.xpath(ProfilesUIConstants.directorSearchBox));
		
		log.info("INFO: Click on "+searchUser.getDisplayName()+" displayed in My Network list");
		logger.strongStep("Click on "+searchUser.getDisplayName()+"  displayed in My Network list");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserLinkFromDirSearchResult(searchUser.getDisplayName())), 4);
		profilesUICnx8.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserLinkFromDirSearchResult(searchUser.getDisplayName()))));
		
		log.info("INFO: Verify that it should displays the user information page of that user");
		logger.strongStep("Verify that it should displays the user information page of that user");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		cnxAssert.assertTrue(driver.getTitle().equals(searchUser.getDisplayName()+" Profile"), "It should displays the user information page of "+searchUser.getDisplayName());
		
		log.info("INFO: Click on chat icon ");
		logger.strongStep("Click on chat icon ");
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.chatIconOnProfile), 4);
		profilesUICnx8.clickLinkWithJavaScriptWd(profilesUICnx8.findElement(By.cssSelector(ProfilesUIConstants.chatIconOnProfile)));
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();
		profilesUICnx8.switchToNextWindowWd("teams");
		
		String currentUrl = driver.getCurrentUrl();
		String msLoginUrl = "microsoft";
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		profilesUICnx8.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);
		
		log.info("INFO: Click on three dots icon(more action)");
		logger.strongStep("Click on three dots icon(more action)");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click ");
		
		log.info("INFO: It should display more action popup window.");
		logger.strongStep("It should display more action popup window.");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.anotherUserProfActionMenuTable)), "It should display more action popup window");

		List<String> expActionMenu = new ArrayList<>(Arrays.asList("Stop Following ", "Share a File ", "Download vCard "));
		List<String> actualActionMenu = new ArrayList<>();

		List<WebElement> actionMenus = ui.findElements(By.xpath(ProfilesUIConstants.anotherUserProfActionMenu));
		for (WebElement menu : actionMenus) {
			String menuLabel = menu.getAttribute("aria-label");
			actualActionMenu.add(menuLabel);
		}

		log.info("INFO: Popup window should display 'Stop Following', 'Share a File' , 'Download vCard' options on it");
		logger.strongStep("Popup window should display 'Stop Following', 'Share a File' , 'Download vCard' options on it");
		cnxAssert.assertTrue(actualActionMenu.equals(expActionMenu), "Popup window should display all expected options");
		
		log.info("INFO: Click on 'Invite to Connect' button");
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.inviteToConnectIcon), 4);
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.inviteToConnectIcon), 3,"Click Invite to connect button");

		log.info("INFO: Verify 'Invite To My Network' pop up is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ProfilesUIConstants.InviteToMyNetworkDailogueBox), 4),"Invite to Network dialogue box is displayed");
				
		profilesUICnx8.endTest();
	}
	
}
