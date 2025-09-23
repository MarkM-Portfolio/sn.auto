package com.ibm.conn.auto.tests.homepage.regression;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.atmn.waffle.utils.Assert;

public class LanguageSelector extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(LanguageSelector.class);
	private Assert cnxAssert;
	private User testUser;
	private HomepageUI ui;
	private ProfilesUI profileUI;
	private TestConfigCustom cfg;
	private HomepageUICnx8 homepageCnx8ui;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private CommunitiesUI commUI;
	private BlogsUI blogUI;
	private WikisUI wikiUI;
	private ForumsUI forumsUI;
	private DogearUI bookmarksUI;
	private ActivitiesUI activitiesUI;
	private FilesUI filesUI;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		       //initialize the configuration
				cfg = TestConfigCustom.getInstance();
				testUser = cfg.getUserAllocator().getUser();
				ui = HomepageUI.getGui(cfg.getProductName(), driver);
				homepageCnx8ui = new HomepageUICnx8(driver);
				profileUI = ProfilesUI.getGui(cfg.getProductName(), driver);
				serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
				apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
				commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
				cnxAssert = new Assert(log);
				blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
				wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
				forumsUI = ForumsUI.getGui(cfg.getProductName(), driver);
				bookmarksUI = DogearUI.getGui(cfg.getProductName(), driver);
				activitiesUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
				filesUI = FilesUI.getGui(cfg.getProductName(), driver);
				
					
	}

	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Home page</B></li>
	 *<li><B>Step: Load home page and login </B></li>
	 *<li><B>Step: Select Language from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: "Validate the default language is selected as 'English'</B></li> 
	 *</ul>
	 */
	//JIRA Link : jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T118
	@Test (groups = {"regression"} )
	public void homepageLanguageSelector() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Homepage, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Select Français Language from Language Selector Dropdow except English");
		log.info("INFO: Select any Language from Language Selector Dropdow except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Select English from Language Selector Dropdow");
		log.info("INFO: Select English from Language Selector Dropdow");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate the default language is selected as 'English'");
		log.info("INFO: Validate the default language is selected as 'English'");
		String expectedLang = "English";
		String actualLang = homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.lsDropdown));
		cnxAssert.assertEquals(actualLang, expectedLang, "English laguage is selected as default option");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Profiles</B></li>
	 *<li><B>Step: Load Profiles page and login </B></li>
	 *<li><B>Verify: Validate that title 'My Profile' is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that title 'My Profile' is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that title 'My Profile' is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void profilesLanguageSelector() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Profile, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Profile, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		profileUI.loadComponent(Data.getData().ComponentProfiles);
		profileUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that title 'My Profile' is displayed in English");
		log.info("INFO: Validate that title 'My Profile' is displayed in English");
		cnxAssert.assertEquals(driver.getSingleElement(ProfilesUIOnPrem.MyProfile).getText(), "MY PROFILE", "My Profile title is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select any Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that title 'My Profile' is not displayed in English");
		log.info("INFO: Validate that title 'My Profile' is not displayed in English");
		cnxAssert.assertNotEquals(driver.getSingleElement(ProfilesUIOnPrem.MyProfile).getText(), "MY PROFILE", "My Profile title has changed to selected language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that title 'My Profile' is displayed in English");
		log.info("INFO: Validate that title 'My Profile' is displayed in English");
		cnxAssert.assertEquals(driver.getSingleElement(ProfilesUIOnPrem.MyProfile).getText(), "MY PROFILE", "My Profile title has changed to default English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Communities</B></li>
	 *<li><B>Step: Create Community using API </B></li>
	 *<li><B>Step: Load Communities and Log In  </B></li>
	 *<li><B>Step: Navigate to the Community </B></li>
	 *<li><B>Verify: Validate that title 'Communities' is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that title 'Communities' is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that title 'Communities' is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void communitiesLanguageSelector() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).build();

		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as " + cfg.getUseNewUI());
		log.info("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as " + cfg.getUseNewUI());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		logger.strongStep("Validate that title 'Communities' is displayed in English");
		log.info("INFO: Validate that title 'Communities' is displayed in English");
		cnxAssert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.megaMenuOptionCommunities).getText(), "Communities", "Communities title is displayed in  English language");
		
		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select any Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that title 'Communities' is not displayed in English");
		log.info("INFO: Validate that title 'Communities' is not displayed in English");
		cnxAssert.assertNotEquals(driver.getSingleElement(CommunitiesUIConstants.megaMenuOptionCommunities).getText(), "Communities", "Communities title has changed to selected language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that title 'Communities' is displayed in English");
		log.info("INFO: Validate that title 'Communities' is displayed in English");
		cnxAssert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.megaMenuOptionCommunities).getText(), "Communities", "Communities title has changed to default English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Blogs</B></li>
	 *<li><B>Step: Load Blogs page and login </B></li>
	 *<li><B>Verify: Validate that title 'My Blogs' is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that title 'My Blogs' is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that title 'My Blogs' is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void blogsLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();


		logger.strongStep("Load component Blog, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Blog, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		blogUI.loadComponent(Data.getData().ComponentBlogs);
		blogUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that title 'My Blogs' is displayed in English");
		log.info("INFO: Validate that title 'My Blogs' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BlogsUIConstants.BlogInnerTabs).getText(), "MY BLOGS", "My Blogs title is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select any Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that title 'My Blogs' is not displayed in English");
		log.info("INFO: Validate that title 'My Blogs' is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(BlogsUIConstants.BlogInnerTabs).getText(), "MY BLOGS", "My Blogs title has changed to selected language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that title 'My Blogs' is displayed in English");
		log.info("INFO: Validate that title 'My Blogs' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BlogsUIConstants.BlogInnerTabs).getText(), "MY BLOGS", "My Blogs title has changed to default English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Wikis</B></li>
	 *<li><B>Step: Load Wikis page and login </B></li>
	 *<li><B>Step: Reload the page and wait of 'Start a Wiki' button </B></li>
	 *<li><B>Verify: Validate that button 'Start a Wiki' is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that button 'Start a Wiki' is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that button 'Start a Wiki' is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void wikisLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Wikis, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Wikis, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		wikiUI.loadComponent(Data.getData().ComponentWikis);
		wikiUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Reload the page and wait of 'Start a Wiki' button");
		log.info("INFO: Reload the page and wait of 'Start a Wiki' button");
		wikiUI.waitForPageLoaded(driver);
		ui.isElementVisibleWd(By.xpath(BaseUIConstants.publicbuttonForStandaloneComp),5);
		
		logger.strongStep("Validate that button 'Start a Wiki' is displayed in English");
		log.info("INFO: Validate that button 'Start a Wiki' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start a Wiki", "'Start a Wiki' button is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select Français Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that button 'Start a Wiki' is not displayed in English");
		log.info("INFO: Validate that button 'Start a Wiki' is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start a Wiki", "'Start a Wiki' button is not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that button 'Start a Wiki' is displayed in English");
		log.info("INFO: Validate that button 'Start a Wiki' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start a Wiki", "'Start a Wiki' button is displayed in  English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Forums</B></li>
	 *<li><B>Step: Load Forums page and login </B></li>
	 *<li><B>Verify: Validate that description title 'Topics I'm following ' is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that description title 'Topics I'm following ' is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that description title 'Topics I'm following ' is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void forumsLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Forums, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Forums, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		forumsUI.loadComponent(Data.getData().ComponentForums);
		forumsUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that description title 'Topics I'm following ' is displayed in English");
		log.info("INFO: Validate that descrption title 'Topics I'm following ' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(ForumsUIConstants.topicsImfollowingHeadline).getText(), "Topics I'm following", "Descrption title 'Topics I'm following 'is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select Français Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that description title 'Topics I'm following ' is not displayed in English");
		log.info("INFO: Validate that descrption title 'Topics I'm following ' is  not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(ForumsUIConstants.topicsImfollowingHeadline).getText(), "Topics I'm following", "Descrption title 'Topics I'm following 'is  not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that description title 'Topics I'm following ' is displayed in English");
		log.info("INFO: Validate that descrption title 'Topics I'm following ' is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(ForumsUIConstants.topicsImfollowingHeadline).getText(), "Topics I'm following", "Descrption title 'Topics I'm following 'is displayed in  English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Bookmarks</B></li>
	 *<li><B>Step: Load Bookmarks page and login </B></li>
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void bookmarksLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Bookmarks, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Bookmarks, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		bookmarksUI.loadComponent(Data.getData().ComponentDogear);
		bookmarksUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that 'Add a Bookmark' button is displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select Français Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that 'Add a Bookmark' button is not displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that 'Add a Bookmark' button is displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is displayed in  English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Activities</B></li>
	 *<li><B>Step: Load Activities page and login </B></li>
	 *<li><B>Verify: Validate that 'Start an Activity' button is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that 'Start an Activity' button is  not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that 'Start an Activity' button is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void activitiesLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Activities, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Activities, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		activitiesUI.loadComponent(Data.getData().ComponentActivities);
		activitiesUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that 'Start an Activity' button is displayed in English");
		log.info("INFO: Validate that 'Start an Activity' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start an Activity", "'Start an Activity' button is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select Français Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that 'Start an Activity' button is not displayed in English");
		log.info("INFO: Validate that 'Start an Activity' button  is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start an Activity", "'Start an Activity' button is not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that 'Start an Activity' button is displayed in English");
		log.info("INFO: Validate that 'Start an Activity' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Start an Activity", "'Start an Activity' button is displayed in  English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the Language selector is displayed on Files</B></li>
	 *<li><B>Step: Load Files page and login </B></li>
	 *<li><B>Step: Reload the page and wait of 'Get Sync' button </B></li>
	 *<li><B>Verify: Validate that 'Get Sync' button is displayed in English</B></li> 
	 *<li><B>Step: Select any Language from Language Selector Drop-down except English</B></li> 
	 *<li><B>Verify: Validate that 'Get Sync' button is not displayed in English</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that 'Get Sync' button is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression"} )
	public void filesLanguageSelector() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Files, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Files, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Reload the page and wait of 'Get Sync' button");
		log.info("INFO: Reload the page and wait of 'Get Sync' button");
		filesUI.waitForPageLoaded(driver);
		ui.isElementVisibleWd(By.xpath(FilesUIConstants.getSyncButton),5);
		
		logger.strongStep("Validate that 'Get Sync' button is displayed in English");
		log.info("INFO: Validate that 'Get Sync' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(FilesUIConstants.getSyncButton).getText(), "Get Sync", "'Get Sync' button is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown except English");
		log.info("INFO: Select Français Language from Language Selector Dropdown except English");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that 'Get Sync' button is not displayed in English");
		log.info("INFO: Validate that 'Get Sync' button  is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(FilesUIConstants.getSyncButton).getText(), "Get Sync", "'Get Sync' button is not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that 'Get Sync' button is displayed in English");
		log.info("INFO: Validate that 'Get Sync' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(FilesUIConstants.getSyncButton).getText(), "Get Sync", "'Get Sync' button is displayed in  English language");
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test the position of 'HCL Connections' Logo on screen for different languages</B></li>
	 *<li><B>Step: Load Bookmarks page and login </B></li>
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is displayed in English</B></li> 
	 *<li><B>Verify: Validate that 'HCL Connections' Logo is displayed at the left side of the screen</B></li> 
	 *<li><B>Step: Select Arabic Language from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is not displayed in English</B></li> 
	 *<li><B>Verify: Validate that 'HCL Connections' Logo is displayed at the left side of the screen</B></li> 
	 *<li><B>Step: Select English from Language Selector Drop-down</B></li> 
	 *<li><B>Verify: Validate that 'Add a Bookmark' button is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"cplevel2"} )
	public void rightToLeftPresenceValidation() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Bookmarks, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Bookmarks, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		bookmarksUI.loadComponent(Data.getData().ComponentDogear);
		bookmarksUI.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that 'Add a Bookmark' button is displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is displayed in  English language");
		
		logger.strongStep("Validate that 'HCL Connections' Logo is displayed at the left side of the screen");
		log.info("INFO: Validate that 'HCL Connections' Logo is displayed at the left side of the screen");
		ui.validateElementPosition(ui.findElement(By.xpath(BaseUIConstants.hclConnectionsLogo)),"English");

		logger.strongStep("Select Arabic Language from Language Selector Dropdown");
		log.info("INFO: Select Arabic Language from Language Selector Dropdown");
		ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.lsDropdown), 5, "Click on Language Selector Dropdown");
		ui.waitForElementVisibleWd(By.xpath(BaseUIConstants.arabicLang), 3);
		ui.clickLinkWaitWd(By.xpath(BaseUIConstants.arabicLang), 3, "Click on Arabic language Option from dropdown");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'Add a Bookmark' button is not displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is not displayed in English");
		cnxAssert.assertNotEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is not displayed in  English language");
		
		logger.strongStep("Validate that 'HCL Connections' Logo is displayed at the right side of the screen");
		log.info("INFO: Validate that 'HCL Connections' Logo is displayed at the right side of the screen");
		ui.validateElementPosition(ui.findElement(By.xpath(BaseUIConstants.hclConnectionsLogo)),"Arabic");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdown");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that 'Add a Bookmark' button is displayed in English");
		log.info("INFO: Validate that 'Add a Bookmark' button  is displayed in English");
		cnxAssert.assertEquals(driver.getFirstElement(BaseUIConstants.publicbuttonForStandaloneComp).getText(), "Add a Bookmark", "'Add a Bookmark' button is displayed in  English language");
				
		ui.endTest();

	}
}


