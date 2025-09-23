package com.ibm.conn.auto.tests.homepage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8_TopUpdates;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Cnx8UI_Homepage_TopUpdates extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_TopUpdates.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private CommonUICnx8 commonUI;
	private HomepageUI ui;
	private HomepageUICnx8 homepageCnx8ui;
	private HomepageUICnx8_TopUpdates homepageCnx8TopUpdates;
	private ProfilesUICnx8 profilesUiCnx8;
	private APICommunitiesHandler apiOwner;
	private String serverURL;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		commonUI = new CommonUICnx8(driver);
		homepageCnx8ui = new HomepageUICnx8(driver);
		homepageCnx8TopUpdates = new HomepageUICnx8_TopUpdates(driver);
		cnxAssert = new Assert(log);
		profilesUiCnx8 = new ProfilesUICnx8(driver);
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
    
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Top Updates link is present in CNX8 home page</li>
	 * <li><B>Step:</B> Load Home Page, Login to Connections and Toggle to the new UI</li>
	 * <li><B>Verify:</B> Verify the Top Updates link is visible</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Verify:</B> Verify the Top Updates page is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T540</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyTopUpdatesLinkIsPresentAndClickable() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		commonUI.startTest();

		logger.strongStep("Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify the Top Updates link is visible");
		log.info("INFO: Verify the Top Updates link is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3), "Top Updates link is visible");

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");

		logger.strongStep("Verify Top Updates page is displayed");
		log.info("INFO: Verify Top Updates page is displayed");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesPage), 3), "Verify Top Updates page is displayed");

		commonUI.endTest();
	}
    
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify no content found when the filtered text is not available in content</li>
	 * <li><B>Step:</B> Load Home Page, Login to Connections and Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Click filter icon</li>
	 * <li><B>Step:</B> Type text in filter textBox</li>
	 * <li><B>Step:</B> Wait for filter suggestion</li>
	 * <li><B>Verify:</B> Verify no results are found</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T562</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyFilterByNoContentFound() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		commonUI.startTest();

		logger.strongStep("Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");

		logger.strongStep("Click filter icon");
		log.info("INFO: Click filter icon");
		WebElement filterIcon = homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterIcon), 3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(filterIcon);

		logger.strongStep("Type text in filter textBox");
		log.info("INFO: Type text in filter textBox");
		homepageCnx8ui.typeWithDelayWd("asdfgasdasdacacasascacascascascsacascsasa", By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox));

		logger.strongStep("Wait for filter suggestion");
		log.info("INFO: Wait for filter suggestion");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.filterbarTypeaheadContainer), 3);

		logger.strongStep("Verify no results are found");
		log.info("INFO: Verify no results are found");
		WebElement filterContentHeader = homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.filterContentHeader), 3);
		cnxAssert.assertTrue(filterContentHeader.getText().contains("No search results found"), "Verify no search results are found");

		commonUI.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify filtering by people is working</li>
	 * <li><B>Step:</B> Load Home Page, Login to Connections and Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Click filter icon</li>
	 * <li><B>Step:</B> Type test user name in filter textBox</li>
	 * <li><B>Step:</B> Wait for people filter suggestion</li>
	 * <li><B>Verify:</B> Verify the filtered user is present in the filter result dropdown list</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T575</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyFilterByPeople() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		commonUI.startTest();

		logger.strongStep("Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");

		logger.strongStep("Click filter icon");
		log.info("INFO: Click filter icon");
		WebElement filterIcon = homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterIcon), 3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(filterIcon);

		logger.strongStep("Type @ followed by " + testUser.getFirstName()+ " in filter textBox");
		log.info("INFO: Type @"+ testUser.getFirstName() +" in filter textBox");
		homepageCnx8ui.typeWithDelayWd("@" + testUser.getFirstName(), By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox));

		logger.strongStep("Wait for people filter suggestion");
		log.info("INFO: Wait for people filter suggestion");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.filterbarTypeaheadContainer), 3);

		logger.strongStep("Verify the filtered user is present in the filter result dropdown list");
		log.info("INFO: Verify the filtered user is present in the filter result dropdown list");
		List<WebElement> filterContentList = homepageCnx8ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.filterContentList), 3);
		cnxAssert.assertTrue(filterContentList.get(0).getText().contains(testUser.getFirstName()),
				"Verify the filtered user is present in the filter result dropdown list");

		commonUI.endTest();
	}
    
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify filtering by contents is working</li>
	 * <li><B>Step:</B> Load Home Page, Login to Connections and Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Click filter icon</li>
	 * <li><B>Step:</B> Create Community using API</li>
	 * <li><B>Step:</B> Type the community name in filter textBox</li>
	 * <li><B>Step:</B> Wait for filter suggestion</li>
	 * <li><B>Verify:</B> Verify the community name is present in the filtered list</li>
	 * <li><B>Step:</B> Click the search icon to apply the filter</li>
	 * <li><B>Verify:</B> Verify the filtered text is present in chips</li>
	 * <li><B>Verify:</B> Verify the filtered text is present in content</li>
	 * <li><B>Step:</B> Click filter icon</li>
	 * <li><B>Step:</B> Type text in filter textBox</li>
	 * <li><B>Step:</B> Click the search icon to apply the filter</li>
	 * <li><B>Step:</B> Wait for the filter suggestion</li>
	 * <li><B>Verify:</B> Verify the filtered text is present in chips</li>
	 * <li><B>Verify:</B> Verify the filtered text is present in content</li>
	 * <li><B>Step:</B> Cancel the 1st filter</li>
	 * <li><B>Verify:</B> Verify the filtered text is not present in chips</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T561</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T576</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T563</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T564</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyFilterByCommunity() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		commonUI.startTest();

		String filterText = "Pink Panther";
		BaseCommunity community = new BaseCommunity.Builder("The Pink Panther" + Helper.genDateBasedRandVal()).tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("The Pink Panther has a pink coat. It belongs to the cat family.").build();

		logger.strongStep("Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Home Page, Login to Connections and Toggle to the new UI as " + cfg.getUseNewUI());
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 5, "Click Top Updates link in secondary nav menu");

		logger.strongStep("Click filter icon");
		log.info("INFO: Click filter icon");
		WebElement filterIcon = homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterIcon), 3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(filterIcon);

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Type the " + community.getName() + " in filter textBox");
		log.info("INFO: Type " + community.getName() + " in filter textBox");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox), 3);
		homepageCnx8ui.typeWithDelayWd(community.getName(), By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox));

		logger.strongStep("Wait for filter suggestion");
		log.info("INFO: Wait for filter suggestion");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.filterbarTypeaheadContainer), 3);

		logger.strongStep("Verify the " + community.getName() + " is present in the filtered list");
		log.info("INFO: Verify the " + community.getName() + " is present in the filtered list");
		List<WebElement> filterContentList = homepageCnx8ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.filterContentList), 3);
		cnxAssert.assertTrue(filterContentList.get(0).getText().contains(community.getName()), "Verify the " + community.getName() + " is present in the filtered list");

		// Starting https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T563 Apply 2 filters and expect the content to be filtered
		logger.strongStep("Click the search icon to apply the filter");
		log.info("INFO: Click the search icon to apply the filter");
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.filterIconToClickToApplyFilter), 3, "Click the search icon to apply the filter");

		logger.strongStep("Verify the " + community.getName() + " text is present in chips");
		log.info("INFO: Verify the searched text is present in chips");
		List<WebElement> filterChipsLables = homepageCnx8ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.filterChipsLables), 3);
		cnxAssert.assertTrue(filterChipsLables.get(0).getText().contains(community.getName()), "Verify the searched text is present in chips");

		logger.strongStep("Verify the " + community.getName() + " text is present in content");
		log.info("INFO: Verify the " + community.getName() + " text is present in content");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.filteredArticleTitles.replace("PLACEHOLDER", community.getName())),5),"Verify the " + community.getName() + " text is present in content");

		logger.strongStep("Click filter icon");
		log.info("INFO: Click filter icon");
		WebElement filterIcon2 = homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterIcon), 3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(filterIcon2);

		logger.strongStep("Type " + filterText + " in filter textBox");
		log.info("INFO: Type " + filterText + " in filter textBox");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox), 3);
		homepageCnx8ui.typeWithDelayWd(filterText, By.cssSelector(HomepageUIConstants.topUpdatesFilterSearchBox));

		logger.strongStep("Wait for " + filterText + " filter suggestion");
		log.info("INFO: Wait for " + filterText + " filter suggestion");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.filterbarTypeaheadContainer), 3);

		logger.strongStep("Click the search icon to apply the filter");
		log.info("INFO: Click the search icon to apply the filter");
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.filterIconToClickToApplyFilter), 3, "Click the search icon to apply the filter");

		logger.strongStep("Verify the " + filterText + " text is present in chips");
		log.info("INFO: Verify the " + filterText + " text is present in chips");
		List<WebElement> filterChipsLables2 = homepageCnx8ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.filterChipsLables), 3);
		cnxAssert.assertTrue(filterChipsLables2.get(1).getText().contains(filterText), "Verify the " + filterText + " text is present in chips");

		logger.strongStep("Verify the " + filterText + " text is present in content");
		log.info("INFO: Verify the " + filterText + " text is present in content");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.filteredArticleTitles.replace("PLACEHOLDER", filterText)),3),"Verify the " + filterText + " text is present in content");

		// Starting https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T564 remove a filter and expect it to be removed from chips
		logger.strongStep("Cancel the " +community.getName()+ " filter");
		log.info("INFO: Cancel the " +community.getName()+ " filter");
		homepageCnx8ui.clickLinkWd(homepageCnx8ui.findElements(By.cssSelector(HomepageUIConstants.filterChipsDeleteIcons)).get(0), "Cancel the " +community.getName()+ " filter");

		logger.strongStep("Verify the " +community.getName()+ " text is not present in chips");
		log.info("INFO: INFO: Verify the " +community.getName()+ " text is not present in chips");
		List<WebElement> filterChipsLables3 = homepageCnx8ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.filterChipsLables), 3);
		cnxAssert.assertFalse(filterChipsLables3.get(0).getText().contains(community.getName()), "Verify the " +community.getName()+ " text is not present in chips");

		commonUI.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Mentions category in Top Updates</li>
	 * <li><B>Step:</B> Login to Homepage</li>
	 * <li><B>Step:</B> Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Share the Post for mentions </li>
	 * <li><B>Step:</B> Click on categories dropdown </li>
	 * <li><B>Step:</B> Click on mentions category </li>
	 * <li><B>Verify:</B> Verify the shared post on the mentions category</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T548</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyTopUpdatesMentions() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());        
		String testName = ui.startTest();
		String message = testName + " message " + Helper.genDateBasedRand();

		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);

		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");

		log.info("INFO: wait for Top Updates page ready to interact");
		homepageCnx8TopUpdates.waitForTopUpdatesInteractable();
		ui.closeGuidedTourDialog();
		
		logger.strongStep("Share the Post for mentions ");
		log.info("INFO: Share the Post for mentions");        
		homepageCnx8TopUpdates.postAtMention(testUser, message);

		logger.strongStep("Click on categories dropdown");
		log.info("INFO: Click on categories dropdown");
		WebElement topUpdatesCategoriesDropdown = homepageCnx8ui.waitForClickableElementWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesDropdown),5);
		homepageCnx8ui.clickLinkWd(topUpdatesCategoriesDropdown,"Click on categorie dropdown");

		logger.strongStep("Click on mentions category");
		log.info("INFO: Click on mentions category");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesMentions), 3, "Select the mentions category");

		logger.strongStep("Verify the shared post on the mentions category");
		log.info("INFO: Verify the shared post on the mentions category");
		cnxAssert.assertTrue(homepageCnx8TopUpdates.verifyMentionsPost(testUser, message), "Verify the mentions post");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Comments functionality in Top Updates</li>
	 * <li><B>Step:</B> Login to Homepage</li>
	 * <li><B>Step:</B> Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Share the Post for mentions </li>
	 * <li><B>Step:</B> Click on categories dropdown </li>
	 * <li><B>Step:</B> Click on mentions category </li>
	 * <li><B>Step:</B> Click on comment icon and adding comment </li>
	 * <li><B>Verify:</B> Verify comment Message is displayed as expected</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T543</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyTopUpdatesPostComment() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());        
		String testName = ui.startTest();
		String message = " " + testName + " message " + Helper.genDateBasedRand();
		String commentMessage="This is test Comment to post"; 
		String commentTextElement = HomepageUIConstants.topUpdatesCommentText.replace("PLACEHOLDER", commentMessage);

		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);

		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");
		
		// observed that focus got taken away if clicking the Share Something box before 
		// page has finished loading so make best effort to wait until after
		log.info("INFO: wait for Top Updates page ready to interact");
		homepageCnx8TopUpdates.waitForTopUpdatesInteractable();
		ui.closeGuidedTourDialog();

		logger.strongStep("Share the Post for mentions ");
		log.info("INFO: Share the Post for mentions");        
		homepageCnx8TopUpdates.postAtMention(testUser, message);

		logger.strongStep("Click on categories dropdown");
		log.info("INFO: Click on categories dropdown");
		WebElement topUpdatesCategoriesDropdown = homepageCnx8ui.waitForClickableElementWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesDropdown),3);
		homepageCnx8ui.clickLinkWd(topUpdatesCategoriesDropdown,"Click on categorie dropdown");

		logger.strongStep("Click on mentions category");
		log.info("INFO: Click on mentions category");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesMentions), 3, "Select the mentions category");

		logger.strongStep("Click on comment icon");
		log.info("INFO: Click on comment icon");
		WebElement topUpdatesComment = homepageCnx8ui.waitForClickableElementWd(By.xpath(HomepageUIConstants.topUpdatesComment.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2", message)), 5);
		homepageCnx8ui.scrollToElementWithJavaScriptWd(topUpdatesComment);
		homepageCnx8ui.clickLinkWd(topUpdatesComment, "Click on comment icon");

		logger.strongStep("Adding comment on post");
		log.info("INFO: Adding comment on post");
		homepageCnx8TopUpdates.commentOnTopUpdates(commentMessage, message);

		logger.strongStep("Verify comment Message is displayed as expected");
		log.info("INFO: Verify comment Message is displayed as expected Message: "+commentMessage);
		WebElement commentMessageElement = homepageCnx8ui.waitForElementVisibleWd(By.xpath(commentTextElement), 3);
		cnxAssert.assertEquals(commentMessageElement.getText(), commentMessage, "Comment Message"+commentMessage+ "is displaying");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Like and Unlike options in Top Updates</li>
	 * <li><B>Step:</B> Login to Homepage</li>
	 * <li><B>Step:</B> Toggle to the new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Share the Post for mentions </li>
	 * <li><B>Step:</B> Click on categories dropdown </li>
	 * <li><B>Step:</B> Click on mentions category </li>
	 * <li><B>Step:</B> Click on like icon </li>
	 * <li><B>Verify:</B> Verify post is liked</li>
	 * * <li><B>Step:</B> Click on unlike icon </li>
	 * <li><B>Verify:</B> Verify post is unliked</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T542</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyTopUpdatesPostLikeUnlike() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());        
		String testName = ui.startTest();
		String message = testName + " message " + Helper.genDateBasedRand();

		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);

		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");
		
		// observed that focus got taken away if clicking the Share Something box before 
		// page has finished loading so make best effort to wait until after
		log.info("INFO: wait for Top Updates page ready to interact");
		homepageCnx8TopUpdates.waitForTopUpdatesInteractable();
		ui.closeGuidedTourDialog();

		logger.strongStep("Share the Post for mentions ");
		log.info("INFO: Share the Post for mentions");        
		homepageCnx8TopUpdates.postAtMention(testUser, message);

		logger.strongStep("Click on categories dropdown");
		log.info("INFO: Click on categories dropdown");
		WebElement topUpdatesCategoriesDropdown = homepageCnx8ui.waitForClickableElementWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesDropdown),3);
		homepageCnx8ui.clickLinkWd(topUpdatesCategoriesDropdown,"Click on categorie dropdown");

		logger.strongStep("Click on mentions category");
		log.info("INFO: Click on mentions category");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesMentions), 3, "Select the mentions category");

		logger.strongStep("Click on like icon");
		log.info("INFO: Click on like icon");  
		String topUpdatesLikeLocator =  HomepageUIConstants.topUpdatesLike.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2", message);
		homepageCnx8ui.waitForClickableElementWd(By.xpath(topUpdatesLikeLocator), 3);
		homepageCnx8ui.clickLinkWd(By.xpath(topUpdatesLikeLocator), "Click on like button");

		logger.strongStep("Verify post is liked");
		log.info("INFO: Verify post is liked");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.topUpdatesUnlike.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2", message)), 3), "Verify Post is liked");

		logger.strongStep("Click on unlike icon");
		log.info("INFO: Click on unlike icon");
		String topUpdatesUnLikeLocator =  HomepageUIConstants.topUpdatesUnlike.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2", message);
		homepageCnx8ui.scrollToElementWithJavaScriptWd(homepageCnx8ui.findElement(By.xpath(topUpdatesUnLikeLocator)));
		homepageCnx8ui.waitForClickableElementWd(By.xpath(topUpdatesUnLikeLocator), 3);
		homepageCnx8ui.clickLinkWd(By.xpath(topUpdatesUnLikeLocator), "Click on like button");

		logger.strongStep("Verify post is unliked");
		log.info("INFO: Verify post is unliked");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.topUpdatesLike.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2", message)), 3), "Verify Post is unliked");

		ui.endTest();
	}
    
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Responses category in Top Updates</li>
	 *<li><B>Step:</B> Login to Profiles with UserA</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Directory tab and Search for userB</li>
	 *<li><B>Step:</B> Send Invite to UserB</li>
     * <li><B>Verify:</B> Verify disabled userIcon is not displayed in UserB bizcard</li>
	 *<li><B>Step:</B> Login with UserB</li>
	 *<li><B>Step:</B> Toggle to new UI</li>
	 * <li><B>Step:</B> Click Top Updates link in secondary nav menu</li>
	 * <li><B>Step:</B> Click on categories dropdown </li>
	 * <li><B>Step:</B> Click on responses category </li>
	 * <li><B>Verify:</B> Verify invitation text on responses category</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T549</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2692 
	 * (no userIcon displayed before invite sent and after invite sent but not yet accepted)</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyTopUpdatesResponses() { 
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		ui.startTest();
		User testUser = cfg.getUserAllocator().getUser();
		User testUser1 = cfg.getUserAllocator().getUser();

		logger.strongStep("INFO : Delete "+testUser1.getEmail()+ " from " + testUser.getEmail() + " network");
		log.info("INFO : Delete "+testUser1.getEmail()+ " from " + testUser.getEmail()+ " network");
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), testUser1.getEmail(), testUser1.getPassword());
		new APIProfilesHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), testUser.getEmail(), testUser.getPassword()).deleteUserFromNetworkConnections(testUser1Profile);
		
		// Load the component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		AppNavCnx8.PEOPLE.select(commonUI);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab), 3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		log.info("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUser1),"Profile Search result is displayed");

		logger.strongStep("Hover on " + testUser1.getLastName() + "and Verify Business card");
		log.info("Hover on " + testUser1.getLastName() + "and Verify Business card");
		Element userName =homepageCnx8ui.getFirstVisibleElement(HomepageUIConstants.userNamelink+"[@title='PLACEHOLDER1']".replace("PLACEHOLDER1",testUser1.getDisplayName())) ; //driver.getFirstElement(HomepageUIConstants.userSurNamelink);
		userName.hover();
		
		logger.strongStep("Verify that disabled userIcon on Bizcard is not displayed" );
		log.info("INFO: Verify that disabled userIcon on Bizcard is not displayed");
		cnxAssert.assertFalse(profilesUiCnx8.isElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardDisabledInvite),5),
				"Verify disabled userIcon is not displayed");	

		logger.strongStep("INFO :Click on User icon at top right of bizCard popup and click on Send invitation");
		log.info("INFO : Click on User icon at top right of bizCard popup and click on Send invitation");
		profilesUiCnx8.sendInvite();
		
		logger.strongStep("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		log.info("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		AppNavCnx8.PEOPLE.select(commonUI);
		homepageCnx8ui.clickLinkWaitWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),4);
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUser1),"Profile Search result is displayed");

		logger.strongStep("Hover on " + testUser1.getLastName() + "and Verify Business card");
		log.info("Hover on " + testUser1.getLastName() + "and Verify Business card");
		userName =homepageCnx8ui.getFirstVisibleElement(HomepageUIConstants.userNamelink+"[@title='PLACEHOLDER1']".replace("PLACEHOLDER1",testUser1.getDisplayName())) ; //driver.getFirstElement(HomepageUIConstants.userSurNamelink);
		userName.hover();
		
		logger.strongStep("Verify that disabled userIcon on Bizcard is not displayed" );
		log.info("INFO: Verify that disabled userIcon on Bizcard is not displayed");
		cnxAssert.assertFalse(profilesUiCnx8.isElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardDisabledInvite),5),
				"Verify disabled userIcon is not displayed");	

		log.info("Logout as " + testUser.getDisplayName() + " Load My Profiles and Log In as " + testUser1.getDisplayName());
		logger.strongStep("Logout as " + testUser.getDisplayName() + " Load My Profiles and Log In as " + testUser1.getDisplayName());
		ui.logout();

		logger.strongStep("Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 3, "Click Top Updates link in secondary nav menu");

		logger.strongStep("Click on categories dropdown");
		log.info("INFO: Click on categories dropdown");
		homepageCnx8ui.waitForClickableElementWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesDropdown),3);
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesDropdown), 3, "Click on categorie dropdown");

		logger.strongStep("Click on responses category");
		log.info("INFO: Click on responses category");      
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.topUpdatesCategoriesResponses), 3, "Select the responses category");

		logger.strongStep("Verify invitation text on responses category");
		log.info("INFO: Verify invitation text on responses category");        
		cnxAssert.assertTrue(driver.isTextPresent(Data.INVITE_TEXT_RESPONSES.replace("User", testUser.getDisplayName())), "Verify the invited text" + Data.INVITE_TEXT_RESPONSES.replace("User", testUser.getDisplayName()));

		ui.endTest();
	}

}
