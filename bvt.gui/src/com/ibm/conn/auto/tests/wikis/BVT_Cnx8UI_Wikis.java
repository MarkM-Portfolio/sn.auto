
package com.ibm.conn.auto.tests.wikis;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.DogearUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.cnx8.WikisUICnx8;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_Cnx8UI_Wikis extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Wikis.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private DogearUICnx8 ui;
	private SearchAdminService adminService;
	private User testUser,testUserAddedToITM,searchAdmin;
	private APIProfilesHandler profilesAPIUser;
	private String serverURL;
	private ItmNavCnx8 itmNavCnx8;
	private WikisUI WikiUI;
	private APIWikisHandler apiWikisOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		itmNavCnx8 = new ItmNavCnx8(driver);
		apiWikisOwner = new APIWikisHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		profilesAPIUser = new APIProfilesHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		WikiUI = WikisUI.getGui(cfg.getProductName(), driver);
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new DogearUICnx8(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify clicking on the filter icon of a person on the ITM bar from wikis should show wikis belonging to that user </li>
	 *<li><B>Prereq:</B>[API] testUserAddedToITM create wikis </li>
	 *<li><B>Step:</B> Login to wikis with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user navigates to page with URL Server_URL/wikis/home/search?uid=${USER_ID}</li>
	 *<li><B>Verify:</B> Verify that wikis belonging to the testUserAddedToITM whose filter icon is clicked should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyClickingPersonFilterFromWiki() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String uid = profilesAPIUser.getUUID();
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + testName).build();

		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Create a Wiki via API");
		log.info("INFO: Create Wiki via API");
		Wiki wikiAPI = wiki.createAPI(apiWikisOwner);
		
		logger.strongStep("Run Search indexer for wikis");
		log.info("INFO: Run Search indexer for wikis");
		adminService.indexNow("wikis", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
       
		//Load the component
		logger.strongStep("Load wikis, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		// Adding user to ITM if it is already not added
		logger.strongStep("Add user in ITM and Click on filter icon associated with user in ITM");
		itmNavCnx8.addUserToITMAndClickFilterIcon(testUserAddedToITM);
			
		log.info("INFO: Verify that "+testUserAddedToITM.getDisplayName() +" wiki page is opened");
		logger.strongStep("Verify that "+testUserAddedToITM.getDisplayName() +" wiki page is opened");	
		String expectedUrl = Data.getData().userWikiUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid);
		ui.waitForTitleIsPresentWd("Search", 5);
		cnxAssert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase()),"User navigates to "+expectedUrl);
		
		log.info("Verify that wikis belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		logger.strongStep("Verify that wikis belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		ui.clickLinkWaitWd(By.xpath(ForumsUIConstants.dateSortDesc), 5, "Click Date filter");
		ui.waitForElementInvisibleWd(By.xpath(ForumsUIConstants.dateSortDesc), 5);
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(WikisUICnx8.getWiki(wiki))),"Wiki is displayed");
		
		log.info("INFO: Delete wiki");
		apiWikisOwner.deleteWiki(wikiAPI);
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to verify Community wiki page elements</li>
	 *<li><B>Step:</B> Create a Community via API</li>
	 *<li><B>Step:</B> Login to Wikis and Toggle to the new UI</li>
	 *<li><B>Step:</B> Select 'I'm an Owner' view</li>
	 *<li><B>Step:</B> Open created community wiki</li>
	 *<li><B>Verify:</B> Verify Community Header is displayed</li>
	 *<li><B>Verify:</B> Verify Action buttons are displayed on Community Wiki page</li>
	 *<li><B>Verify:</B> Verify Tag section is displayed</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130553</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyCommunityWikiPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.access(Access.PUBLIC)
				.tags(Data.getData().commonTag)
				.description("Test Community for " + testName)
				.build();
		
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Creating a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		logger.strongStep("Load wikis, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Select 'I'm an Owner' view");
		log.info("INFO: Select 'I'm an Owner' view");
		Wiki_LeftNav_Menu.OWNER.select(WikiUI);
		
		logger.strongStep("Open the Community Wiki created via API");
		log.info("INFO: Open the Community Wiki created via API");
		ui.clickLinkWaitWd(By.cssSelector(WikisUIConstants.wikisLink.replace("PLACEHOLDER", community.getName())), 5, "Click Date filter");
		
		logger.strongStep("Verify Community Header is displayed");
		log.info("INFO: Verify Community Header is displayed");
		ui.waitForElementVisibleWd(By.xpath(WikisUIConstants.communityWikiHeader.replace("PLACEHOLDER", community.getName())), 7);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.communityWikiHeader.replace("PLACEHOLDER", community.getName()))), "Community Header is displayed");
		
		logger.strongStep("Verify Action Buttons 'Following Actions', 'Wiki Actions' and 'Community Actions' are displayed");
		log.info("INFO: Verify Action Buttons 'Following Actions', 'Wiki Actions' and 'Community Actions' are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.communityWikiActionsBtn.replace("PLACEHOLDER", "Following Actions"))), "Following Actions is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.communityWikiActionsBtn.replace("PLACEHOLDER", "Wiki Actions"))), "Wiki Actions is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.communityWikiActionsBtn.replace("PLACEHOLDER", "Community Actions"))), "Community Actions is displayed");
		
		logger.strongStep("Verify Tag Section is displayed");
		log.info("INFO: Verify Tag Section is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.communityWikiTags)), "Tag section is displayed");
		
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify CNX8UI navigation bar at Wikis Page</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI </li>
	 *<li><B>Step:</B> Create a new Wiki</li>
	 *<li><B>Verify:</B> Verify 'Wikis' link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Arrow' link is displayed before Wikis link</li>
	 *<li><B>Verify:</B> Verify 'Index' link is displayed on top navigation bar</li>
	 *<li><B>Verify:</B> Verify 'Members' link is displayed on top navigation bar</li>
	 *<li><B>Verify:</B> Verify 'Trash' link is displayed on top navigation bar</li>
	 *<li><B>Verify:</B> Verify 'PDF' link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Full-Screen Toggle' icon is displayed</li>
	 *<li><B>Step:</B> Click on 'Full-Screen Toggle' icon to maximize the screen</li>
	 *<li><B>Verify:</B> Verify 'Minimize Full-Screen Toggle' icon is displayed</li>
	 *<li><B>Step:</B> Click on 'Minimize Full-Screen Toggle' icon to navigate back to default screen size</li>
	 *<li><B>Verify:</B> Verify 'Following Actions' Section is displayed</li>
	 *<li><B>Verify:</B> Verify 'Following this page' Link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Stop Following this Wiki' Link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Wiki Actions' Section is displayed</li>
	 *<li><B>Verify:</B> Verify 'Edit Wiki' Link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Delete Wiki' Link is displayed</li>
	 *<li><B>Verify:</B> Verify Tag Section is displayed</li>
	 *<li><B>Verify:</B> Verify Member Section is displayed</li>
	 *<li><B>Step:</B> Click on 'Following this page' Link</li>
	 *<li><B>Verify:</B> Verify 'You are following this page.' message is displayed</li>
	 *<li><B>Step:</B> Click on 'Edit Wiki' Link</li>
	 *<li><B>Verify:</B> Verify on 'Edit Wiki' page title is displayed</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T749</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T750</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T751</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyNavigationBarAndLinksAtWikiPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + testName).build();

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		wiki.create(WikiUI);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify 'Wikis' link is displayed");
		log.info("INFO: Verify 'Wikis' link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.wikisLinkNewUI)), "Wikis", "'Wikis' title is Displayed");
		
		logger.strongStep("Verify 'Arrow' link is displayed before Wikis link");
		log.info("INFO: Verify 'Arrow' link is displayed before Wikis link");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.wikisArrowLink)), "'Arrow' link is displayed");
		
		logger.strongStep("Verify 'Index' link is displayed on top navigation bar");
		log.info("INFO: Verify 'Index' link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.indexLinkTopNav)), "Index", "'Index' link is Displayed");
		
		logger.strongStep("Verify 'Members' link is displayed on top navigation bar");
		log.info("INFO: Verify 'Members' link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.membersLinkTopNav)), "Members", "'Members' link is Displayed");
		
		logger.strongStep("Verify 'Trash' link is displayed on top navigation bar");
		log.info("INFO: Verify 'Trash' link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.trashLinkTopNav)), "Trash", "'Trash' link is Displayed");
		
		logger.strongStep("Verify 'PDF' link is displayed");
		log.info("INFO: Verify 'PDF' link is displayed");
		ui.fluentWaitPresentWithRefresh(WikisUIConstants.exportPDFLink);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.exportPDFLink)), "'Export as PDF' link is Displayed");
		
		logger.strongStep("Verify 'Full-Screen Toggle' icon is displayed");
		log.info("INFO: Verify 'Full-Screen Toggle' icon is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.maximizefullScreenToggleLink)), "'Full Screen Toggle' link is displayed");
		
		logger.strongStep("Click on 'Full-Screen Toggle' icon to maximize the screen");
		log.info("INFO: Click on 'Full-Screen Toggle' icon to maximize the screen");
		ui.clickLinkWd(By.xpath(WikisUIConstants.maximizefullScreenToggleLink),"Maximize 'Full-Screen Toggle' icon is Clicked");	
		
		logger.strongStep("Verify 'Minimize Full-Screen Toggle' icon is displayed");
		log.info("INFO: Verify 'Minimize Full-Screen Toggle' icon is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.minimizefullScreenToggleLink)), "'Minimize Full Screen Toggle' link is displayed");
		
		logger.strongStep("Click on 'Minimize Full-Screen Toggle' icon to navigate back to default screen size");
		log.info("INFO: Click on 'Minimize Full-Screen Toggle' icon to navigate back to default screen size");
		ui.clickLinkWd(By.xpath(WikisUIConstants.minimizefullScreenToggleLink),"Navigate back to default screen size");
		
		logger.strongStep("Verify 'Following Actions' Section is displayed");
		log.info("INFO: Verify 'Following Actions' Section is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.followingActionsDropDownnewUI)), "Following Actions", "'Following Actions' title is Displayed");
		
		logger.strongStep("Verify 'Following this page' Link is displayed");
		log.info("INFO: Verify 'Follow this Page' Link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.followThisPageLinknewUI)), "Follow this Page", "'Follow this Page' Link is Displayed");
		
		logger.strongStep("Verify 'Stop Following this Wiki' Link is displayed");
		log.info("INFO: Verify 'Stop Following this Wiki' Link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.stopFollowThisWikiLinknewUI)), "Stop Following this Wiki", "'Stop Following this Wiki' Link is Displayed");
		
		logger.strongStep("Verify 'Wiki Actions' Section is displayed");
		log.info("INFO: Verify 'Wiki Actions' Section is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.wikiActionsDropDownnewUI)), "Wiki Actions", "'Wiki Actions' title is Displayed");
		
		logger.strongStep("Verify 'Edit Wiki' Link is displayed");
		log.info("INFO: Verify 'Edit Wiki' Link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.editWikiLinknewUI)), "Edit Wiki", "'Edit Wiki' Link is Displayed");
		
		logger.strongStep("Verify 'Delete Wiki' Link is displayed");
		log.info("INFO: Verify 'Delete Wiki' Link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.deleteWikiLinknewUI)), "Delete Wiki", "'Delete Wiki' Link is Displayed");
		
		logger.strongStep("Verify Tag Section is displayed");
		log.info("INFO: Verify Tag Section is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.communityWikiTags)), "Tag section is displayed");
		
		logger.strongStep("Verify Member Section is displayed");
		log.info("INFO: Verify Member Section is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.memberDropDownnewUI)), "Member section is displayed");
		
		logger.strongStep("Click on 'Following this page' Link ");
		log.info("INFO: Click on 'Follow this Page' Link ");
		ui.clickLinkWd(By.xpath(WikisUIConstants.followThisPageLinknewUI),"'Following this page' Link is Clicked");
		
		logger.strongStep("Verify 'You are following this page.' message is displayed");
		log.info("INFO: Verify 'You are following this page.' message is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.followingThisPageMessagenewUI)), "You are following this page.", "'You are following this page.' message is Displayed");
		
		logger.strongStep("Click on 'Edit Wiki' Link");
		log.info("INFO: Click on 'Edit Wiki' Link ");
		ui.clickLinkWd(By.xpath(WikisUIConstants.editWikiLinknewUI),"'Edit Wiki' Link is Clicked");
		
		logger.strongStep("Verify on 'Edit Wiki' page title is displayed");
		log.info("INFO: Verify on 'Edit Wiki' page title is displayed ");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(WikisUIConstants.editWikiTitleTextnewUI)), "Edit Wiki", "'Edit Wiki' page title is Displayed");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Public Tags, Most Visited, and Most Liked wikis links are displayed</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI</li>
	 *<li><B>Step:</B> Select Public Wikis view</li>
	 *<li><B>Verify:</B> Verify 'Public Wikis' is selected in top nav</li>
	 *<li><B>Verify:</B> Verify 'Most Liked' link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Most Visits' link is displayed</li>
	 *<li><B>Verify:</B> Verify 'Public Tags' is displayed</li>
	 *<li><B>Verify:</B> Verify Summary and Details view links are displayed</li>
	 *<li><B>Verify:</B> Verify Pagination is displayed</li>
	 *<li><B>Verify:</B> Verify 'Feed for public wikis' link is displayed</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130246</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPublicWikiPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		ui.startTest();

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Select 'Public Wikis' view");
		log.info("INFO: Select 'Public Wikis' view");
		Wiki_LeftNav_Menu.PUBLICWIKIS.select(WikiUI);
		ui.waitForPageLoaded(driver);
		ui.waitForElementVisibleWd(By.xpath(WikisUIConstants.publicTags), 8);
		
		logger.strongStep("Verify 'Public Wikis' is selected in top nav");
		log.info("INFO: Verify 'Public Wikis' is selected in top nav");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.selected_ternaryNav)), "Public Wikis", "'Public Wikis' is Selected");
		
		logger.strongStep("Verify 'Most Liked' link is displayed");
		log.info("INFO: Verify 'Most Liked' link is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.mostLiked)), "'Most Liked' link is displayed");
		
		logger.strongStep("Verify 'Most Visits' link is displayed");
		log.info("INFO: Verify 'Most Visits' link is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.mostVisits)), "'Most Visits' link is displayed");
		
		logger.strongStep("Verify 'Public Tags' is displayed");
		log.info("INFO: Verify 'Public Tags' is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.publicTags)), "'Public Tags' Section is displayed");
		
		logger.strongStep("Verify Summary and Details view links are displayed");
		log.info("INFO: Verify Summary and Details view links are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Summary)), "Summary view link is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Details)), "Details view link is displayed");
		
		logger.strongStep("Verify Pagination is displayed");
		log.info("INFO: Verify Pagination is displayed");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(WikisUIConstants.wikis_pagination));
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikis_pagination)), "Pagination section is displayed");
		
		logger.strongStep("Verify 'Feed for public wikis' link is displayed");
		log.info("INFO: Verify 'Feed for public wikis' link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.wikisFeed)), "Feed for public wikis", "Link is displayed");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Public Tags, Most Visited, and Most Liked wikis links are displayed</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI</li>
	 *<li><B>Step:</B> Select 'I'm Following' view</li>
	 *<li><B>Verify:</B> Verify various elements on 'I'm Following' Page</li>
	 *<li><B>Step:</B> Select 'I'm an Editor' view</li>
	 *<li><B>Verify:</B> Verify various elements on 'I'm an Editor' Page</li>
	 *<li><B>Step:</B> Select 'I'm a Reader' view</li>
	 *<li><B>Verify:</B> Verify various elements on 'I'm a Reader' Page</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130246</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyImFollowingImAnEditorAndImAReaderPages(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
						.tags("tag" + Helper.genDateBasedRand())
						.description("Description for test " + testName)
						.build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Select 'I'm Following' view");
		log.info("INFO: Select 'I'm Following' view");
		Wiki_LeftNav_Menu.FOLLOWING.select(WikiUI);
		ui.waitForTitleIsPresentWd("Wikis I'm Following", 9);
		
		String wikiFeed_Following = "Feed for wikis you are following";
		
		logger.strongStep("Verify various elements on 'I'm Following' Page");
		log.info("INFO: Verify various elements on 'I'm Following' Page");
		verifyPageElement(wiki.getName(), "I'm Following", wikiFeed_Following);
		
		logger.strongStep("Select 'I'm an Editor' view");
		log.info("INFO: Select 'I'm an Editor' view");
		Wiki_LeftNav_Menu.EDITOR.select(WikiUI);
		
		String wikiFeed_Editor = "Feed for wikis you can edit";
		
		logger.strongStep("Verify various elements on 'I'm an Editor' Page");
		log.info("INFO: Verify various elements on 'I'm an Editor' Page");
		verifyPageElement(wiki.getName(), "I'm an Editor", wikiFeed_Editor);
		
		logger.strongStep("Select 'I'm a Reader' view");
		log.info("INFO: Select 'I'm a Reader' view");
		Wiki_LeftNav_Menu.READER.select(WikiUI);
		
		String wikiFeed_Reader = "Feed for wikis you can read";
		
		logger.strongStep("Verify various elements on 'I'm a Reader' Page");
		log.info("INFO: Verify various elements on 'I'm a Reader' Page");
		verifyPageElement(wiki.getName(), "I'm a Reader", wikiFeed_Reader);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify CNX8UI Links on Wikis index Page</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI</li>
	 *<li><B>Step:</B> Open Wiki created via API</li>
	 *<li><B>Step:</B> Click on 'Index' Tab from Top Nav</li>
	 *<li><B>Verify:</B> Verify 'Index' is selected in Top Nav</li>
	 *<li><B>Verify:</B> Verify links are displayed in left and right side of panel</li>
	 *<li><B>Verify:</B> Verify Created Wiki and Pagination are displayed</li>
	 *<li><B>Verify:</B> Verify Summary and Details view links are displayed</li>
	 *<li><B>Verify:</B> Verify Tags, Members, Dates and Pages section on right side panel</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130246</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyWikisIndexPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		User testUser = cfg.getUserAllocator().getUser();
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
						.tags("tag" + Helper.genDateBasedRand())
						.description("Description for test " + testName)
						.build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Index' Tab from Top Nav");
		log.info("INFO: Click on 'Index' Tab from Top Nav");
		ui.clickLinkWithJavascript(WikisUIConstants.indexLinkTopNav);
		ui.waitForElementVisibleWd(By.cssSelector(WikisUIConstants.wikiMembers), 7);
		
		logger.strongStep("Verify 'Index' is selected in Top Nav");
		log.info("INFO: Verify 'Index' is selected in Top Nav");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.selectedTab_TopNavBar)), "Index", "Index is Selected");
		
		logger.strongStep("Verify links are displayed in left and right side of panel");
		log.info("INFO: Verify links are displayed in left and right side of panel");
		verifyWikiPageElement(wiki.getName());
		
		logger.strongStep("Verify Created Wiki and Pagination are displayed");
		log.info("INFO: Verify Created Wiki and Pagination are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.wikiList.replace("PLACEHOLDER", wiki.getName()))), "Create wiki is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikis_pagination)), "Pagination is Visible");
		
		logger.strongStep("Verify Summary and Details view links are displayed");
		log.info("INFO: Verify Summary and Details view links are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Summary)), "Summary view link is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Details)), "Details view link is visible");
		
		logger.strongStep("Verify Tags, Members, Dates and Pages section are displayed on right side panel");
		log.info("INFO: Verify Tags, Members, Dates and Pages section are displayed on right side panel");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiTags)), "Tags section is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiMembers)), "Members section is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiDates)), "Dates section is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiPages)), "Pages section is visible");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify CNX8UI Links on Wikis Members Page</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI</li>
	 *<li><B>Step:</B> Open Wiki created via API</li>
	 *<li><B>Step:</B> Click on 'Members' Tab from Top Nav</li>
	 *<li><B>Verify:</B> Verify 'Members' is selected in Top Nav</li>
	 *<li><B>Verify:</B> Verify links are displayed in left and right side of panel</li>
	 *<li><B>Verify:</B> Verify Add Members, Remove Members and Manage Access Action buttons are displayed</li>
	 *<li><B>Verify:</B> Verify Member list and Pagination are displayed</li>
	 *<li><B>Verify:</B> Verify Summary and Details view links are displayed</li>
	 *<li><B>Verify:</B> Verify Role and Kind section are displayed on right side panel</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130246</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyWikisMembersPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		User testUser = cfg.getUserAllocator().getUser();
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
						.tags("tag" + Helper.genDateBasedRand())
						.description("Description for test " + testName)
						.build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Members' Tab from Top Nav");
		log.info("INFO: Click on 'Members' Tab from Top Nav");
		ui.clickLinkWithJavascript(WikisUIConstants.membersLinkTopNav);
		ui.waitForElementVisibleWd(By.cssSelector(WikisUIConstants.wikiRole), 7);
		
		logger.strongStep("Verify Members is selected in top nav");
		log.info("INFO: Verify Members is selected in top nav");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.selectedTab_TopNavBar)), "Members", "Members is Selected");
		
		logger.strongStep("Verify links are displayed in left and right side of panel");
		log.info("INFO: Verify links are displayed in left and right side of panel");
		verifyWikiPageElement(wiki.getName());
		
		logger.strongStep("Verify Add Members, Remove Members and Manage Access Action buttons are displayed");
		log.info("INFO: Verify Add Members, Remove Members and Manage Access Action buttons are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.actionBtn.replace("PLACEHOLDER", "Add Members"))), "Add Members btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.actionBtn.replace("PLACEHOLDER", "Remove Members"))), "Remove Members btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.actionBtn.replace("PLACEHOLDER", "Manage Access"))), "Manage Acces btn is visible");
		
		logger.strongStep("Verify Member list and Pagination are displayed");
		log.info("INFO: Verify Member list and Pagination are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.memberList.replace("PLACEHOLDER", testUser.getDisplayName()))), "Member is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikis_pagination)), "Pagination is visible");
		
		logger.strongStep("Verify Summary and Details view links are displayed");
		log.info("INFO: Verify Summary and Details view links are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Summary)), "Summary view link is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Details)), "Details view link is displayed");
		
		logger.strongStep("Verify Role and Kind section are displayed on right side panel");
		log.info("INFO: Verify Role and Kind section are displayed on right side panel");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiRole)), "Role section is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiKind)), "Kind section is visible");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify CNX8UI Links on Wikis Trash Page</li>
	 *<li><B>Step:</B> Load wikis, Log in and Toggle to new UI</li>
	 *<li><B>Step:</B> Open Wiki created via API</li>
	 *<li><B>Step:</B> Click on 'Trash' Tab from Top Nav</li>
	 *<li><B>Verify:</B> Verify 'Trash' is selected in Top Nav</li>
	 *<li><B>Verify:</B> Verify links are displayed in left and right side of panel</li>
	 *<li><B>Verify:</B> Verify Restore, Delete and Enpty Trash action buttons are displayed</li>
	 *<li><B>Verify:</B> Verify Pagination is displayed</li>
	 *<li><B>JIRA:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130246</li>
	 *</ul> 
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyWikisTrashPage(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		User testUser = cfg.getUserAllocator().getUser();
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
						.tags("tag" + Helper.genDateBasedRand())
						.description("Description for test " + testName)
						.build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);

		logger.strongStep("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		log.info("Load wikis, Log in and Toggle to new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Trash' Link from Top Nav");
		log.info("INFO: Click on 'Trash' Link from Top Nav");
		ui.clickLinkWithJavascript(WikisUIConstants.trashLinkTopNav);
		ui.waitForElementVisibleWd(By.cssSelector(WikisUIConstants.wikiMembers), 7);
		
		logger.strongStep("Verify Index is selected in top nav");
		log.info("INFO: Verify Index is selected in top nav");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.selectedTab_TopNavBar)), "Trash", "Trash is Selected");
		
		logger.strongStep("Verify links are displayed in left and right side of panel");
		log.info("INFO: Verify links are displayed in left and right side of panel");
		verifyWikiPageElement(wiki.getName());
		
		logger.strongStep("Verify Restore, Delete and Enpty Trash action buttons are displayed");
		log.info("INFO: Verify Restore, Delete and Enpty Trash action buttons are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.trashActionBtn.replace("PLACEHOLDER", "Restore"))), "Restore btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.trashActionBtn.replace("PLACEHOLDER", "Delete"))), "Delete btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.trashActionBtn.replace("PLACEHOLDER", "Empty Trash"))), "Empty Trash btn is visible");
		
		logger.strongStep("Verify Members section is displayed");
		log.info("INFO: Verify Members section is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikiMembers)), "Members is Visible");
		
		ui.endTest();
	}
	
	/**
	 * This method will validate visibility of links in left panel, back to wikis link,
	 * Wikis and following action links in right side panel on different tabs of wikis pages
	 * @param wikiName
	 */
	private void verifyWikiPageElement(String wikiName) {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Wikis link, New page button and create wiki page in left panel are displayed");
		log.info("INFO: Verify Wikis link, New page button and create wiki page in left panel are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.wikisLinkNewUI)), "Wikis link is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.newPageBtn)), "New Page btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.sideNavWikiName.replace("PLACEHOLDER", wikiName))), "Wiki page is visible");
		
		logger.strongStep("Verify 'Back to Wiki' link is displayed");
		log.info("INFO: Verify 'Back to Wiki' link is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.backToWikiLink.replace("PLACEHOLDER", wikiName))), "Back to Wiki link is visible");
		
		logger.strongStep("Verify Following actions and Wiki action links are displayed");
		log.info("INFO: Verify Following actions and Wiki action links are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.followingActionsLink)), "Following Action link is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(WikisUIConstants.wikiActionsLink)), "Wiki Action link is visible");
		
	}


	/**
	 * This method will verify selected nav item, Created Wiki, Tag section, Different Views,Pagination
	 * and feed links displayed on the pages
	 * @param wikiName
	 * @param navTitle
	 * @param wikiFeed
	 */
	private void verifyPageElement(String wikiName, String navTitle, String wikiFeed) {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		ui.waitForPageLoaded(driver);
		ui.waitForElementVisibleWd(By.cssSelector(WikisUIConstants.wikisName.replace("PLACEHOLDER", wikiName)), 7);
		
		logger.strongStep("Verify " + navTitle + " is selected in top nav");
		log.info("INFO: Verify " + navTitle + " is selected in top nav");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.selected_ternaryNav)), navTitle, navTitle + " is Selected");
		
		logger.strongStep("Verify " + wikiName + " Wiki is displayed");
		log.info("INFO: Verify " + wikiName + " Wiki is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikisName.replace("PLACEHOLDER", wikiName))), wikiName + " is displayed");
		
		// Check if 'I'm an editor' or 'I'm a Reader' is selected in top nav
		if(navTitle.contains("Editor") || navTitle.contains("Reader")) {
			logger.strongStep("Verify Tag section is displayed");
			log.info("INFO: Verify Tag section is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.tag_Section)), "Tag Section is displayed");
			
			logger.strongStep("Verify Summary and Details view links are displayed");
			log.info("INFO: Verify Summary and Details view links are displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Summary)), "Summary view link is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.view_Details)), "Details view link is displayed");
		}
		
		logger.strongStep("Verify Pagination is displayed");
		log.info("INFO: Verify Pagination is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(WikisUIConstants.wikis_pagination)), "Pagination is displayed");
		
		logger.strongStep("Verify " + wikiFeed + " link is displayed");
		log.info("INFO: Verify " + wikiFeed + " link is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(WikisUIConstants.wikisFeed)), wikiFeed, "Feed link is displayed");
	}
}
