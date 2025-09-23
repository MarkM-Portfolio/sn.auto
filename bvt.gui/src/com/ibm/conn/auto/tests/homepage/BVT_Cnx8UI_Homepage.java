package com.ibm.conn.auto.tests.homepage;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.FilesUICnx8;
import com.ibm.conn.auto.webui.cnx8.HomepageSecNav;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8_LatestUpdate;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ItmNavUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_Cnx8UI_Homepage extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser,testUser1,searchAdmin;
	private HomepageSecNav secNavUI;
	private CommonUICnx8 commonUI;
	private HomepageUI ui;
	private HomepageUICnx8 homepageCnx8ui;	
	private ProfilesUICnx8 profilesUiCnx8;
	private HomepageUICnx8_LatestUpdate homePageUICnx8LatestUpdate;
	private SearchAdminService adminService;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser1=cfg.getUserAllocator().getUser();		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		secNavUI = new HomepageSecNav(driver);
		commonUI = new CommonUICnx8(driver);
		profilesUiCnx8 = new ProfilesUICnx8(driver);
		homepageCnx8ui = new HomepageUICnx8(driver);
		cnxAssert = new Assert(log);
		homePageUICnx8LatestUpdate = new HomepageUICnx8_LatestUpdate(driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> POC: Test new UI toggle switch</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify new UI after toggling to new UI</li>
	 *<li><B>Step:</B> Toggle to the old UI</li>
	 *<li><B>Verify:</B> Verify old UI after toggling to old UI</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyNewUIToggle() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		commonUI.startTest();
		
		logger.strongStep("Login to Connections");
		commonUI.loadComponent(Data.getData().HomepageImFollowing);
		commonUI.login(testUser);
		
		logger.strongStep("Toggle to the new UI as" + true);
		cnxAssert.assertTrue(commonUI.toggleNewUI(true),"Verify toggle to new UI");

		logger.strongStep("Verify Element of Con8 are displayed");
		cnxAssert.assertTrue(commonUI.isElementVisibleWd(By.xpath(HomepageUIConstants.secondaryNavigation), 4),"Verify Secondary Navigation is displayed");
		
		cnxAssert.assertTrue(commonUI.isElementVisibleWd(By.xpath(ItmNavUIConstants.ItmBar), 4),"Verify Secondary Navigation is displayed");

		cnxAssert.assertTrue(commonUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 4),"Verify Global Search Icon is displayed");	
		
		logger.strongStep("Toggle to the new UI as" + false);
		cnxAssert.assertFalse(commonUI.toggleNewUI(false),"Verify toggle to old UI");
		
		logger.strongStep("Verify Element of Con8 are not displayed");
		cnxAssert.assertFalse(commonUI.isElementVisibleWd(By.xpath(HomepageUIConstants.secondaryNavigation), 4),"Verify Secondary Navigation is not displayed");
		
		cnxAssert.assertFalse(commonUI.isElementVisibleWd(By.xpath(ItmNavUIConstants.ItmBar), 4),"Verify Secondary Navigation is displayed");

		cnxAssert.assertFalse(commonUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 4),"Verify Global Search Icon is not displayed");	
			
		commonUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test secondary nav for Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on My Page link in secondary nav</li>
	 *<li><B>Verify:</B> Verify My Page Customize widget is visible</li>
	 *<li><B>Step:</B> Click on Latest Updates link in secondary nav</li>
	 *<li><B>Verify:</B> Verify Homepage I am following Filter is visible</li>
	 *<li><B>Step:</B> Click on Discover link in secondary nav</li>
	 *<li><B>Verify:</B> Verify Homepage I am following Filter is not visible</li>
	 *<li><B>Step:</B> Click on Top Update in secondary nav</li>
	 *<li><B>Verify:</B> Verify Top update page is visible</li>
	 *</ul>
	 */
	@Test (groups = {"cnx8ui-cplevel2"} )
	public void verifyHomePageSecondLevelNav() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		secNavUI.startTest();

		logger.strongStep("Load component Homepage and Login to Connections");
		secNavUI.loadComponent(Data.getData().HomepageImFollowing);
		secNavUI.login(testUser);

		logger.strongStep("Toggle to the new UI as "+ cfg.getUseNewUI());
		commonUI.toggleNewUI(cfg.getUseNewUI());
		commonUI.waitForPageLoaded(driver);
				
		logger.strongStep("Click on Homepage is selected in nav");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), 8);
		commonUI.clickLinkWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), "Click on Homepage in selected in nav");

		logger.strongStep("Select My Page in secondary nav menu");
		log.info("INFO: Select My Page in secondary nav menu");	
		secNavUI.clickSecNavItem(secNavUI.myPage);

		logger.strongStep("Verify the My Page Customize widget is visible");
		cnxAssert.assertTrue(secNavUI.isElementVisibleWd(secNavUI.createByFromSizzle(HomepageUIConstants.WidgetsCustomize), 8),
				"Customized widget is visible");

		logger.strongStep("Click on Homepage is selected in nav");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), 5);
		commonUI.clickLinkWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), "Click on Homepage in selected in nav");

		logger.strongStep("Select Latest Updates in secondary nav menu");
		log.info("INFO: Select Latest Updates in secondary nav menu");	
		secNavUI.clickSecNavItem(secNavUI.updates);

		logger.strongStep("Verify the 'Latest Updates' link is still visible.");
		cnxAssert.softAssertTrue(secNavUI.isElementVisibleWd(By.cssSelector(
				secNavUI.getSecNavLocator(secNavUI.updates)), 3),
				"'Latest Updates' link is still visible");

		logger.strongStep("Verify Homepage I am Following tab is visible");
		cnxAssert.assertTrue(secNavUI.isElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesImFollowingFilter), 6),
				"Homepage I am Following tab is visible");
		
		logger.strongStep("Click on Homepage is selected in nav");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), 5);
		commonUI.clickLinkWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), "Click on Homepage in selected in nav");

		logger.strongStep("Select Discover in secondary nav menu");
		log.info("INFO: Select Discover in secondary nav menu");	
		secNavUI.clickSecNavItem(secNavUI.discover);

		logger.strongStep("Verify the 'Discover' link is still visible.");
		cnxAssert.softAssertTrue(secNavUI.isElementVisibleWd(By.cssSelector(
				secNavUI.getSecNavLocator(secNavUI.discover)), 5),
				"'Discover' link is still visible");

		logger.strongStep("Verify Homepage I am Following tab is not visible");
		cnxAssert.assertFalse(secNavUI.isElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesImFollowingFilter), 2),
				"Homepage I am Following tab is visible");

		logger.strongStep("Click on Homepage is selected in nav");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), 5);
		commonUI.clickLinkWd(By.xpath(AppNavCnx8.HOMEPAGE.getAppMenuLocator()), "Click on Homepage in selected in nav");

		logger.strongStep("Select Top Update in secondary nav menu");
		log.info("INFO: Select Top Update in secondary nav menu");	
		secNavUI.clickSecNavItem(secNavUI.topUpdate);

		logger.strongStep("Verify the 'Top Update' link is still visible.");
		cnxAssert.softAssertTrue(secNavUI.isElementVisibleWd(By.cssSelector(
				secNavUI.getSecNavLocator(secNavUI.topUpdate)), 5),
				"'Top Update' link is still visible");

		logger.strongStep("Verify Top Updates page is displayed");
		log.info("INFO: Verify Top Updates page is displayed");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesPage), 3), "Verify Top Updates page is displayed");

		cnxAssert.softAssertAll();

		secNavUI.endTest();
	}	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify My Notification and Action Required Badges in Latest Updates</li>
	 *<li><B>Step:</B> Login to Profiles with UserA</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Directory tab and Search for userB</li>
	 *<li><B>Step:</B> Send Invite to UserB</li>
	 *<li><B>Step:</B> Login with UserB</li>
	 *<li><B>Step:</B> Toggle to new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Tab</li>
	 *<li><B>Step:</B>Click on Personal Filter</li>
	 *<li><B>Verify:</B>Verify  Badge Count on My Notification filter menu is visible</li>
	 *<li><B>Step</B>Click on My Notification filter menu</li>
	 *<li><B>Step</B>Verify Notification Posts are displayed</li>
	 *<li><B>Step:</B>Click on Personal Filter</li>
	 *<li><B>Verify:</B>Verify  Badge Count on My Notification filter menu is not visible Anymore</li>
	  *<li><B>Step:</B>Click on Personal Filter</li>
	 *<li><B>Verify:</B>Get Badge Count on Action Required filter menu</li>
	 *<li><B>Step</B>Click on Action Required filter menu</li>
	 *<li><B>Step</B>Verify Action Required Posts are displayed</li>
	 *<li><B>step</B>Click on Post and Accept Invitation send by UserA</li>
	 *<li><B>Step:</B>Get Badge Count on My Notification filter menu after invitation is accepted</li>
	 *<li><B>Verify:</B>Verify Badge Count before accepting invitation is more than Badge count after accepting invitation</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T591</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyMyNotificationAndActionRequiredAfterSendingInvite() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		
		ui.startTest();
		String[] newsStory = null;

		// Load the component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("INFO : Click on People from Primary Nav bar");
		log.info("INFO : Click on People from Primary Nav bar");
		homepageCnx8ui.clickLinkWd(By.xpath(AppNavCnx8.PEOPLE.getAppMenuLocator()), "Click on People");
		
		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab), 5);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		logger.strongStep("INFO : Delete "+testUser1.getEmail()+ "from " + testUser.getEmail() + " network");
		log.info("INFO : Delete "+testUser1.getEmail()+ "from " + testUser.getEmail()+ " network");
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), testUser1.getEmail(), testUser1.getPassword());
		new APIProfilesHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), testUser.getEmail(), testUser.getPassword()).deleteUserFromNetworkConnections(testUser1Profile);
		
		logger.strongStep("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		log.info("Search for " + testUser1.getDisplayName() +"and Verify " +  testUser1.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUser1),"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUser1.getLastName() + "and Verify Business card");
		log.info("Hover on " + testUser1.getLastName() + "and Verify Business card");
		//TODO- cnx8ui - new Hover function of HCBase is not working
		Element userName =homepageCnx8ui.getFirstVisibleElement(HomepageUIConstants.userNamelink+"[@title='PLACEHOLDER1']/b[text()='PLACEHOLDER2']".replace("PLACEHOLDER1",testUser1.getDisplayName() ).replace("PLACEHOLDER2",testUser1.getFirstName())) ; //driver.getFirstElement(HomepageUIConstants.userSurNamelink);
		userName.hover();
		
		logger.strongStep("INFO :Click on More Action and Click on Invite To My Network");
		log.info("INFO : Click on More Action and Click on Invite To My Network");
		profilesUiCnx8.sendInvite();
		
		log.info("Logout as " + testUser.getDisplayName() + "Load My Profiles and Log In as " + testUser1.getDisplayName());
		logger.strongStep("Logout as " + testUser.getDisplayName() + "Load My Profiles and Log In as " + testUser1.getDisplayName());
		ui.logout();
		
		// Closing the browser because as part of struts to spring changes in homepage, 
		// when we logout from application and re-login we are rendering the json page due to cookies
		// Jira ticket - https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12480
		logger.strongStep("Close the browser");
		log.info("INFO: Close the browser");
		driver.close();
			
		logger.strongStep("Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Navigate to Latest Updates Tab"+cfg.getUseNewUI());
		log.info("INFO: Navigate to Latest Updates Tab"+cfg.getUseNewUI());
		ui.gotoUpdates();
		
		log.info("INFO: Click on Personal Filter");
		logger.strongStep("Click on Personal Filter");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter");

		log.info("INFO: Verify  Badge Count on My Notification filter menu is visible");
		logger.strongStep("Verify  Badge Count on My Notification filter menu is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementDisplayedWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilterCount)),"Verify  Badge Count on My Notification filter menu is visible");
		
		log.info("INFO: Click on My Notification Menu");
		logger.strongStep("Click on I'm Following Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilter),3,"Click on My Notification Menu");
		
		newsStory = new String[1];
		
		newsStory[0]=(ui.replaceNewsStory(Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME, null, null, testUser.getDisplayName()));
		
		log.info("INFO: Verify Notification posts are displayed");
		logger.strongStep("Verify Notification posts are displayed");
        HomepageValid.verifyItemsInAS(ui, driver, newsStory, null, true);

        log.info("INFO: Click on Personal Filter");
		logger.strongStep("Click on Personal Filter");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter");
		
		log.info("INFO: Verify  Badge Count on My Notification filter menu is not visible Anymore");
		logger.strongStep("Verify  Badge Count on My Notification filter menu is not visible Anymore");
		cnxAssert.assertFalse(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilterCount),2),"Verify  Badge Count on My Notification filter menu is not visible Anymore");
		
		log.info("INFO: Get Badge Count on Action Required filter menu");
		logger.strongStep("Get Badge Count on Action Required filter menu");
		int countBeforeAction = Integer.parseInt(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilterCount)));
		
		log.info("INFO: Click on Action Required Filter Menu");
		logger.strongStep("Click on Action Required Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilter),3,"Click on Action Required Filter Menu");
	    
		log.info("INFO:Verify Action Required posts are displayed");
		logger.strongStep("Verify Action Required posts are displayed");
		newsStory = new String[1];
		newsStory[0]=(ui.replaceNewsStory(Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME, null, null, testUser.getDisplayName()));
		String inviteMsg = Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME.replace("USER", "");
		HomepageValid.verifyItemsInAS(ui, driver, newsStory, null, true);
        
		log.info("INFO: Click on Action Required Post");
		logger.strongStep("Click on Action Required Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.postContents.replace("PLACEHOLDER1", testUser.getDisplayName()).replace("PLACEHOLDER2",inviteMsg)), 0, "Click on Action Required Post");
        
		log.info("INFO: Switch to EE frame for Action Required and Click on Invite Accept button");
		logger.strongStep("Switch to EE frame for Action Required and Click on Invite Accept button");
		homepageCnx8ui.switchToFrame(homepageCnx8ui.findElement(By.xpath(HomepageUIConstants.inviteEEIFrame)));
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.acceptButtonIFrame), 5);
		homepageCnx8ui.clickLinkWd(By.xpath(HomepageUIConstants.acceptButtonIFrame));
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.successMessageOnIframe), 5);

		log.info("INFO: Refresh Page");
		logger.strongStep("Refresh Page");
		homepageCnx8ui.refreshPage();
		
		log.info("INFO: Click on Personal Filter");
		logger.strongStep("Click on Personal Filter");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter");
        
		log.info("INFO: Get Badge Count on My Notification filter menu after invitation is accepted");
		logger.strongStep("Get Badge Count on My Notification filter menu after invitation is accepted");
		int countAfterAction = homePageUICnx8LatestUpdate.verifyActionRequiredCount(By.xpath(HomepageUIConstants.latestUpdatesActionRequiredFilterCount));
        
		log.info("INFO: Verify Badge Count before accepting invitation is more than Badge count after accepting invitation");
		logger.strongStep("Verify Badge Count before accepting invitation is more than Badge count after accepting invitation");
		cnxAssert.assertEquals(countBeforeAction-1, countAfterAction, "Verify Badge Count before accepting invitation is more than Badge count after accepting invitation");

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	
	}


	/**
	 *<ul>
	 *<li><B>Info: Test case to validate the non English text is displayed after switch to Italiano language</B></li>
	 *<li><B>Step: Load home page and login </B></li>
	 *<li><B>Verify: Validate that 'My Page' link is displayed in English </B></li> 
	 *<li><B>Step: Select Italiano Language from Language Selector Dropdown except English</B></li> 
	 *<li><B>Verify: Validate that 'My Page' link is not displayed in English </B></li> 
	 *<li><B>Step: Select English from Language Selector Dropdown</B></li> 
	 *<li><B>Verify: Validate that 'My Page' link is displayed in English</B></li> 
	 *</ul>
	 */
	@Test (groups = {"cnx8ui-cplevel2","cp-only"} )
	public void verifyHomepageLanguageSelectorItaliano() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Homepage, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate that 'My Page' link is displayed in English");
		log.info("INFO: Validate that 'My Page' link  is displayed in English");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.cssSelector(HomepageUIConstants.MyPageCnx8)), "My Page", "'My Page' link is displayed in  English language");

		logger.strongStep("Select Italiano Language from Language Selector Dropdown except English");
		log.info("INFO: Select any Language from Language Selector Dropdow except English");
		ui.selecteLanguage("Italiano");
		
		logger.strongStep("Validate that 'My Page' link is not displayed in English");
		log.info("INFO: Validate that 'My Page' link  is not displayed in English");
		cnxAssert.assertNotEquals(homepageCnx8ui.getElementTextWd(By.cssSelector(HomepageUIConstants.MyPageCnx8)), "My Page", "'My Page' link is not displayed in  English language");
		
		logger.strongStep("Select English from Language Selector Dropdown");
		log.info("INFO: Select English from Language Selector Dropdow");
		ui.selecteLanguage("English");
		
		logger.strongStep("Validate that 'My Page' link is displayed in English");
		log.info("INFO: Validate that 'My Page' link  is displayed in English");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.cssSelector(HomepageUIConstants.MyPageCnx8)), "My Page", "'My Page' link is displayed in  English language");
				
		ui.endTest();

	}
	

	/**
	 *<ul>
	 *<li><B>Verify More option Index in Homepage primary navigation</B></li>
	 *<li><B>Step: Load home page and login to application and toggle to new new UI</B></li>
	 *<li><B>Verify: Validate that More options is at 4th index </B></li> 
	 *<li><B>Step: Select Wiki from more options</B></li> 
	 *<li><B>Verify: Validate that Wikis options is at 4th index and More options is at 5th index</B></li> 
	 *<li><B>Step: Select Bookmarks from more options</B></li> 
	 *<li><B>Verify: Validate that Bookmarks options is at 4th index and More options is at 5th index</B></li> 
	 *<li><B>Step: Select Blogs from more options</B></li> 
	 *<li><B>Verify: Validate that Blogs options is at 4th index and More options is at 5th index</B></li> 
	 *</ul>
	 *JIRA ID : https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2405
	 */
	@Test (groups = {"cnx8ui-cplevel2"} )
	public void verifyMoreOptionIndexInPrimaryNavigation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		List<WebElement> elements = new LinkedList<>();
		String navbarWidgets = AppNavCnx8.NAVBAR.getAppMenuLocator()+"//span";
		secNavUI.startTest();

		logger.strongStep("Login to Connections");
		secNavUI.loadComponent(Data.getData().HomepageImFollowing);
		secNavUI.login(testUser);

		logger.strongStep("Toggle to the new UI as "+ cfg.getUseNewUI());
		commonUI.toggleNewUI(cfg.getUseNewUI());
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.MORE.getAppMenuLocator()), 5);
		
		logger.strongStep("Get list of all elements");
		log.info("INFO: Get list of all elements");
		elements = homepageCnx8ui.findElements(By.xpath(navbarWidgets));	
		
		log.info("INFO: Verify More widget is at 4th index");
		logger.strongStep("Verify More widget is at 4th index");
		cnxAssert.assertEquals(elements.get(4).getText(), "More", "Verify More is at 4th index"); ;
		
		logger.strongStep("Select Wikis from Homepage primary navigation");
		log.info("INFO: Select Wikis from Homepage primary navigation");
		AppNavCnx8.WIKIS.select(commonUI);
		
		logger.strongStep("Get list of all elements after selecting Wikis");
		log.info("INFO: Get list of all elements after selecting Wikis");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.MORE.getAppMenuLocator()), 5);
		elements = homepageCnx8ui.findElements(By.xpath(navbarWidgets));

		log.info("INFO: Verify More widget is at 5th index and Wikis widget is at 4th index");
		logger.strongStep("Verify More widget is at 5th index and Wikis widget is at th index");
		cnxAssert.assertEquals(elements.get(5).getText(), "More", "Verify More is at 5th index"); ;
		cnxAssert.assertEquals(elements.get(4).getText(), "Wikis", "Verify Wikis is at 4th index"); ;
		
		logger.strongStep("Select Bookmark from Homepage primary navigation");
		log.info("INFO: Select Bookmark from Homepage primary navigation");
		AppNavCnx8.BOOKMARKS.select(commonUI);
		
		logger.strongStep("Get list of all elements after selecting Bookmarks");
		log.info("INFO: Get list of all elements after selecting Bookmarks");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.MORE.getAppMenuLocator()), 5);
		elements = homepageCnx8ui.findElements(By.xpath(navbarWidgets));
		
		log.info("INFO: Verify More widget is at 5th index and Bookmarks widget is at 4th index");
		logger.strongStep("Verify More widget is at 5th index and Bookmarks widget is at th index");
		cnxAssert.assertEquals(elements.get(5).getText(), "More", "Verify More is at 5th index"); ;
		cnxAssert.assertEquals(elements.get(4).getText(), "Bookmarks", "Verify Bookmarks is at 4th index"); ;
		
		logger.strongStep("Select Blogs from Homepage primary navigation");
		log.info("INFO: Select Blogs from Homepage primary navigation");
		AppNavCnx8.BLOGS.select(commonUI);
		
		logger.strongStep("Get list of all elements after selecting Blogs");
		log.info("INFO: Get list of all elements after selecting Blogs");
		commonUI.waitForElementVisibleWd(By.xpath(AppNavCnx8.MORE.getAppMenuLocator()), 5);
		elements = homepageCnx8ui.findElements(By.xpath(navbarWidgets));
		
		log.info("INFO: Verify More widget is at 5th index and Blogs widget is at 4th index");
		logger.strongStep("Verify More widget is at 5th index and Blogs widget is at th index");
		cnxAssert.assertEquals(elements.get(5).getText(), "More", "Verify More is at 5th index"); ;
		cnxAssert.assertEquals(elements.get(4).getText(), "Blogs", "Verify Blogs is at 4th index"); ;
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to verify that homepage is able to display navbar entries in a different language</B></li>
	 *<li><B>Step: Load home page and login </B></li>
	 *<li><B>Verify: Validate that Homepage, Community and People entries text are displayed in English </B></li> 
	 *<li><B>Step: Select Francais Language from Language Selector Dropdown</B></li> 
	 *<li><B>Verify: Validate that Homepage, Community and People entries text are displayed in Francais </B></li> 
	 *<li><B>Step: Select polski Language from Language Selector Dropdown</B></li> 
	 *<li><B>Verify: Validate that Homepage, Community and People entries text are displayed in polski</B></li>
	 *<li><B>Step: Select Chinese (zh) Language from Language Selector Dropdown</B></li> 
	 *<li><B>Verify: Validate that Homepage, Community and People entries text are displayed in Chinese (zh)</B></li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T696</li> 
	 *</ul>
	 */

	@Test (groups = {"cnx8ui-cplevel2","cp-only"}, enabled=false ) 
	//Blocked by https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14119
	public void verifyNavigationBarWithMultiLanguage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load component Homepage, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
			
		logger.strongStep("Validate that Homepage, Community and People entries text are displayed in English");
		log.info("INFO: Validate that Homepage, Community and People entries text are displayed in English");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryHomepageText.replace("PLACEHOLDER",  "Home")), 10), "Homepage text is displayed in  English language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryCommunityText.replace("PLACEHOLDER",  "Communities")), 10), "Communities text is displayed in  English language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryPeopleText.replace("PLACEHOLDER",  "People")), 10), "People text is displayed in  English language");

		logger.strongStep("Select Français Language from Language Selector Dropdown");
		log.info("INFO: Select Français Language from Language Selector Dropdown");
		ui.selecteLanguage("Français");
		
		logger.strongStep("Validate that Homepage, Community and People entries text are displayed in Français");
		log.info("INFO: Validate that Homepage, Community and People entries text are displayed in Français");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryHomepageText.replace("PLACEHOLDER",  "Accueil")), 10), "Homepage text is displayed in  Français language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryCommunityText.replace("PLACEHOLDER",  "Communautés")), 10), "Communities text is displayed in  Français language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryPeopleText.replace("PLACEHOLDER",  "Personnes")), 10), "People text is displayed in  Français language");
		
		logger.strongStep("Select polski Language from Language Selector Dropdown");
		log.info("INFO: Select polski Language from Language Selector Dropdown");
		ui.selecteLanguage("polski");
		
		logger.strongStep("Validate that Homepage, Community and People entries text are displayed in polski");
		log.info("INFO: Validate that Homepage, Community and People entries text are displayed in polski");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryHomepageText.replace("PLACEHOLDER",  "Strona główna")), 10), "Homepage text is displayed in  polski language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryCommunityText.replace("PLACEHOLDER",  "Społeczności")), 10), "Communities text is displayed in  polski language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryPeopleText.replace("PLACEHOLDER",  "Osoby")), 10), "People text is displayed in  polski language");		
		
		logger.strongStep("Select Chinese (zh) Language from Language Selector Dropdown");
		log.info("INFO: Select Chinese (zh) Language from Language Selector Dropdown");
		ui.selecteLanguage("中文 (‏简体)");
		
		logger.strongStep("Validate that Homepage, Community and People entries text are displayed in Chinese (zh)");
		log.info("INFO: Validate that Homepage, Community and People entries text are displayed in Chinese (zh)");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryHomepageText.replace("PLACEHOLDER",  "主页")), 10), "Homepage text is displayed in  Chinese (zh) language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryCommunityText.replace("PLACEHOLDER",  "社区")), 10), "Communities text is displayed in  Chinese (zh) language");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.navBarEntryPeopleText.replace("PLACEHOLDER",  "人员")), 10), "People text is displayed in  Chinese (zh) language");
					
		ui.endTest();

	}
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Test Primary navigation bar</li>
	 *<li><B>Step:</B> Load component Homepage,Validate Login page,Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on each option of left navigation bar</li>
	 *<li><B>Verify:</B> Verify that the clicked option is selected</li>
	 *</ul>
	 */
	//Calendar and Mail options are not working on CPBVT as of now hence not added on test case
	@Test (groups = {"cnx8ui-cplevel2","cnx8ui-level2"} )
	public void verifyPrimarySideNavigation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		secNavUI.startTest();

		logger.strongStep("Load component Homepage,Validate Login page, Login to application and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: Load component Homepage, Login to application and and toggle to New UI as "+ cfg.getUseNewUI());		
		secNavUI.loadComponent(Data.getData().HomepageImFollowing);
		loginPageValidation();
		secNavUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		String cnxPages[] = { "COMMUNITIES", "PEOPLE", "FILES", "PROFILE" ,
				"ACTIVITIES","BOARDS","BLOGS","BOOKMARKS","NOTIFICATIONS", "FORUMS", "WIKIS",  };
		

		for (String app : cnxPages) {
			if (app.equalsIgnoreCase("BOARDS") && !(cfg.getIsKudosboardEnabled())) {
				log.warn("Kudosboard is disabled, skipping Boards test.");
				continue;
			}
			
			logger.strongStep("Select " + app + " in nav menu");
			log.info("INFO: Select " + app + " in nav menu");
			AppNavCnx8 appNav = AppNavCnx8.valueOf(app);
			appNav.select(commonUI);
			commonUI.waitForElementVisibleWd(By.xpath(appNav.getAppMenuLocator()
					.replace(AppNavCnx8.SUBNAV.getAppMenuLocator(), AppNavCnx8.NAVBAR.getAppMenuLocator())), 9);

			logger.strongStep("Verify " + app + " is selected in nav");
			log.info("INFO:  Verify " + app + " is selected in nav");
			cnxAssert.assertTrue(appNav.isAppSelected(commonUI), app + " is selected in navigation");

		}
	}
	
	 /**
		 * This function will validate Log in Title and Headings     
		 */	
	    public void loginPageValidation() {
			log.info("Validate 'Log-in' title is displayed");
			commonUI.waitForElementVisibleWd(By.xpath(BaseUIConstants.loginTitle), 3);
			cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(BaseUIConstants.loginTitle),3), "Log In");
			
			log.info("Validate 'Email/username' heading is displayed");
			commonUI.waitForElementVisibleWd(By.xpath(BaseUIConstants.emailHeading), 3);
			cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(BaseUIConstants.emailHeading),3), "Email/user name");
			
			log.info("Validate 'Password' heading is displayed");
			commonUI.waitForElementVisibleWd(By.xpath(BaseUIConstants.passwordHeading), 3);
			cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(BaseUIConstants.passwordHeading),3), "Password");
			
			log.info("Validate and Click 'TogglePassword' icon ");
			commonUI.waitForElementVisibleWd(By.xpath(BaseUIConstants.togglePasswordIcon), 3);
			commonUI.clickLinkWaitWd(By.xpath(BaseUIConstants.togglePasswordIcon), 3, "Click 'TogglePassword' icon");
			
			if(homepageCnx8ui.isComponentPackInstalled()) {
			log.info("Validate 'Reset Guest Password' link ");
			commonUI.waitForElementVisibleWd(By.xpath(BaseUIConstants.resetGuestPasswordLink), 3);
			cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(BaseUIConstants.resetGuestPasswordLink),3), "Reset Guest Password");
			}

		}
	    /**
		 * <ul>
		 * <li><B>Info:</B> Verify that uploaded file displays properly in new tab when previewing it form EE </li>
		 * <li><B>Step:</B> Load Homepage page and login</li>
		 * <li><B>Step:</B> Go to latest Updates</li>
		 * <li><B>Step:</B> Share something with attached file in Top Updates</li>
		 * <li><B>Step:</B>  Open shared file in EE </li>
		 * <li><B>Step:</B>  Click on preview Link of the file</li>
		 * <li><B>Step:</B>  File should be displayed in new tab </li>
		 * <li><B>JIRA Link</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14050</li>
		 * </ul>
		 */
	    @Test(groups = { "cnx8ui-cplevel2" })
		public void verifyFilePreviewFromEE() throws UnsupportedEncodingException {
			DefectLogger logger = dlog.get(Thread.currentThread().getId());
			ui.startTest();
			
			log.info("INFO: Uploading file via API");
			APIFileHandler apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());	
			BaseFile file = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
			FileEvents.addFile(file, testUser, apiFileOwner);
			adminService.indexNow("files", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
			log.info("INFO: Load Homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
			logger.strongStep("Load homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
			ui.loadComponent(Data.getData().ComponentHomepage);
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
			ui.waitForPageLoaded(driver);
			
			log.info("INFO: Go to latest Updates");
			logger.strongStep("Go to latest Updates");
			ui.waitForElementVisibleWd(By.id("myStreamView"), 5);
			boolean isLatestUpdatesTabSelected = ui.findElement(By.id("myStreamView")).getAttribute("class").contains("Selected");
			if(!isLatestUpdatesTabSelected) {
				log.info("Selct 'Latest Updates' tab ");
				ui.clickLinkWd(By.id("myStreamView"), "Select Latest Updates tab");
				
			}
			ui.waitForElementVisibleWd(ui.createByFromSizzle(BaseUIConstants.StatusUpdate_iFrame), 5);
		
			log.info("INFO: User  will now post a status update with file attachment from 'My Files'");
			logger.strongStep("User  will now post a status update with file attachment from 'My Files'");
			String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
			ProfileEvents.addStatusUpdateWithFileAttachmentUsingUI_FromMyFiles(ui, testUser, file, user1StatusUpdate);

			log.info("INFO: Open shared file in EE");
			logger.strongStep("Open shared file in EE");
			ui.waitForElementVisibleWd(By.xpath(HomepageUICnx8.getFileImageIconOfNewsStory(user1StatusUpdate, file)), 5);
			ui.scrollToElementWithJavaScriptWd(By.xpath(HomepageUICnx8.getFileImageIconOfNewsStory(user1StatusUpdate, file)));
			ui.openNewsStoryEE(user1StatusUpdate);
			
			log.info("INFO: Click on preview Link of the file");
			logger.strongStep("Click on preview Link of the file");
			ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.fileInNewsStoryPreviewlink), 10);
			ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.fileInNewsStoryPreviewlink), 6, "Click on preview link");

			log.info("INFO: File should be displayed properly in new tab");
			logger.strongStep("File should be displayed properly in new tab");
			WebDriverWait wait = new WebDriverWait((WebDriver) driver.getBackingObject(), 5);
			wait.until(ExpectedConditions.numberOfWindowsToBe(2));
			ui.switchToNextWindowWd("filePreview");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUICnx8.fileImageOnPreview(file))),"File image should be displayed on preview");
			ui.endTest();

		}
}
