package com.ibm.conn.auto.tests.homepage;

import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseHpWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Homepage_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cloud.HomepageUICloud;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8_Discover;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Homepage extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser, otherUser;
	private APIActivitiesHandler apiActivitiesOwner;
	private String serverURL;
	private HomepageUICnx8_Discover discoverUi;

	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		testUser = cfg.getUserAllocator().getUser();
		otherUser = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiActivitiesOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		discoverUi = new HomepageUICnx8_Discover(driver);

		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding Activity widgets</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Remove all widgets</li>
	*<li><B>Step: </B>Add the Activities widget</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the Activities widget is added</li>
	*<li><B>Verify: </B>Verify the help for activities is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T594</li>
	*</ul>
	*/
	@Test(groups = {"level1", "level2", "mt-exclude", "smoke", "bvt", "cnx8ui-cplevel2"})
	public void addActivitiesWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();
		
		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
			
		//Remove all widgets
		logger.strongStep("Remove all the widgets");
		List<Element> visibleWidgets = ui.getHomepageWidgets();
		String id = "";
		
		Iterator<Element> visWidget = visibleWidgets.iterator();
		while(visWidget.hasNext())
			{
		    Element widget = visWidget.next();
		    id = widget.getAttribute("id");
			log.info("INFO: Removing widget " + driver.getSingleElement(HomepageUI.getWidgetMenu(id)).getText());
			
			ui.clickLinkWithJavascript(HomepageUI.getWidgetActionMenu(id));
	        if(!ui.isElementVisible(HomepageUI.ClickForActionsOption(id, "Remove"))) {
	        	ui.clickLinkWithJavascript(HomepageUI.getWidgetActionMenu(id));
	        }
			ui.clickLinkWithJavascript(HomepageUI.ClickForActionsOption(id, "Remove"));

			}
		
		//get associated element and add activities widget
		logger.strongStep("If the activities widget is not present, add it");
		log.info("INFO: Add Activities Widget");
		ui.addWidgetIfNotPresent("Activities", "Activities");
			
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		if(!cfg.getIsKudosboardEnabled()) {
			logger.weakStep("Open and close acitvities widget and switch to vertify that the tab is correct");
			log.info("INFO: Verify Activities Widget opens in separate window");
			HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.ACTIVITIES);
		}
			
		//Open/close the Help for the 'listed component'
		//logger.weakStep("Open and close 'help' for acitvities widget");
		//log.info("INFO: Verify Help for Activities Widget opens in separate window");
		//HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.ACTIVITIES);
		//^^^^^ To be renabled later

		logger.strongStep("Verify new entries are present");
		assertTrue(driver.isElementPresent(HomepageUIConstants.ViewActivityEntries),"'View new entries..' link not present!");

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a Blogs widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the Blogs widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the Blogs widget is added</li>
	*<li><B>Verify: </B>Verify the help for Blogs is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T595</li>
	*</ul>
	*/
	@Test(groups = { "cnx8ui-cplevel2","cnx8ui-level2", "mt-exclude" })
	public void addBlogsWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();
		
		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the blogs widget if needed");
		ui.addWidgetIfNotPresent("Blogs", "Blogs", "Blogs");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.weakStep("Open and close the blogs widget and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.BLOGS);

		//Open/close the Help for the 'listed component'
		logger.weakStep("Open and close the 'Help' link for blogs widget");
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.BLOGS);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a Wikis widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the Wikis widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the Wikis widget is added</li>
	*<li><B>Verify: </B>Verify the help for Wikis is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T596</li>
	*</ul>
	*/
	@Test(groups = { "cnx8ui-cplevel2","cnx8ui-level2", "mt-exclude" })
	public void addWikisWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();
		
		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the wikis widget if needed");
		ui.addWidgetIfNotPresent("Wikis", "Wikis", "Wikis");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.weakStep("Open and close the wikis widget and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.LATESTWIKIS);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a Files widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the Files widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the Files widget is added</li>
	*<li><B>Verify: </B>Verify the help for Files is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T599</li>
	*</ul>
	*/
	@Test(groups = { "cnx8ui-cplevel2","cp-only" })
	public void addFilesWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();
		
		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the files widget if needed");
		ui.addWidgetIfNotPresent("Files", "Files", "Files");
			
		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.weakStep("Open and close the files widget and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.FILES);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a bookmark widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the bookmarks widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the bookmarks widget is added</li>
	*<li><B>Verify: </B>Verify the help for bookmarks is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T597</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2", "level2", "mt-exclude", "bvt", "cnx8ui-cplevel2" })
	public void addBookmarksWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();

		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the widget if needed, if not ignore");
		ui.addWidgetIfNotPresent("Bookmarks", "Bookmarks", "Bookmarks");

		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.strongStep("Open and close bookmarks and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.BOOKMARKS);
			
		//Open/close the Help for the 'listed component'
		//logger.strongStep("Open and close the 'Help' link for bookmarks");
		//HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.BOOKMARKS);
		//^^^^^ To be renabled later
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a communities widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the communities widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the communities widget is added</li>
	*<li><B>Verify: </B>Verify the help for communities is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T598</li>
	*</ul>
	*/
	@Test(groups = {  "cnx8ui-cplevel2","cnx8ui-level2" })
	public void addCommunitiesWidget() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		User testUser = cfg.getUserAllocator().getUser();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();

		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the communitites widget if needed");
		ui.addWidgetIfNotPresent("Communities", "Communities", "Communities");

		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.strongStep("Open and close communities widgets and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.COMMUNITIES);
			
		//Open/close the Help for the 'listed component'
		logger.strongStep("Open and close the 'Help' link for communities widget");
		HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.COMMUNITIES);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a profiles widget</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the profiles widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the profiles widget is added</li>
	*<li><B>Verify: </B>Verify the help for profiles is launched</li>
	*<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T600</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2", "level2", "mt-exclude", "bvt", "cnx8ui-cplevel2" })
	public void addProfilesWidget() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser1 = cfg.getUserAllocator().getUser();
	
		ui.startTest();
		
		logger.strongStep("Load Homepage component");
		log.info("INFO: Load Homepage component");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		logger.strongStep("Navigate to 'My Page' link");
		log.info("Info: Navigate to 'My Page' link");
		ui.gotoMyPage();

		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
					
		//Add the widget if not present or ignore if already present
		logger.strongStep("Add the profiles widget if needed");
		ui.addWidgetIfNotPresent("Profiles", "Profiles", "Profiles");
		
		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);

		//Open/close the 'listed' component and switch and verify that tab is correct
		logger.strongStep("Open and close the profiles widget and switch to verify that the tab is correct");
		HomepageValid.verifyWidgetToComponentPopupLink(ui, driver, BaseHpWidget.PROFILES);
			
		//Open/close the Help for the 'listed component'
		//logger.weakStep("Open and close the 'Help' link for the profiles widget");
		//HomepageValid.verifyWidgetHelpPopupLink(ui, driver, BaseHpWidget.PROFILES);
		//^^^^^ to be reenabled later

		logger.strongStep("Validate that profile link is present");
		assertTrue(driver.isElementPresent(HomepageUI.getWidgetTitleLinkSelector("Profiles")),
											"Profile link not present!");


		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding an open social</li>
	*<li><B>Step: </B>Go to Homepage/My Page</li>
	*<li><B>Step: </B>Add the Open Social widget (if it is not already present)</li>
	*<li><B>Step: </B>Open the help from the menu</li>
	*<li><B>Verify: </B>Verify that all widgets are removed</li>
	*<li><B>Verify: </B>Verify that the Open Social widget is added</li>
	*<li><B>Verify: </B>Verify the help for Open Social is launched</li>
	*</ul>
	*/
	@Test(groups = { "level1", "level2", "mt-exclude", "smoke", "bvt", "icStageSkip", "cnx8ui-cplevel2"})
	public void addOpenSocial() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User adminUser = cfg.getUserAllocator().getAdminUser();	
		
		ui.startTest();

		//Load component and login
	    logger.strongStep("Load homepage and login");
	    log.info("Logging in with user: " + adminUser.getEmail());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(adminUser, cfg.getUseNewUI());
		
		ui.gotoAdministration();
			
		//special character open social widget is not added with this code
		if(!ui.dropdownContains(HomepageUIConstants.EnabledWidgetsList, Data.getData().NewWidgetTitle)) {
				if(!ui.dropdownContains(HomepageUIConstants.DisabledWidgetsList, Data.getData().NewWidgetTitle)) {
					ui.addNewOpenSocialWidget();
				}
				ui.enableWidget(Data.getData().NewWidgetTitle);
			}
			
		//Click My Page
		logger.strongStep("Click on 'My Page'");
		ui.gotoMyPage();

		logger.strongStep("Wait for page to load");
		ui.waitForPageLoaded(driver);
		
		//add open social widget
		logger.strongStep("Add an open social widget");
		ui.addWidgetIfNotPresentOpenSocial(true, Data.getData().NewWidgetTitle, "All", Data.getData().NewWidgetTitle);
			
		logger.weakStep("Validate that open social widget is present");
		Assert.assertTrue(driver.isElementPresent(HomepageUI.getWidgetTitleLinkSelector(Data.getData().NewWidgetTitle)), "Open Social Widget was not added.");
			
		ui.checkOpenSocialContainer();

		ui.endTest();
		
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/	
	/**
	 *<ul>
	 *<li><B>Info:</B> Validate the proper links are present on Homepage</li>
	 *<li><B>Step:</B> Navigate to user's Homepage</li>
	 *<li><B>Verify:</B> Verify that all of the normal Homepage links are present:</li>
	 *<li><B>Verify:</B> I'm Following tab</li>
	 *<li><B>Verify:</B> Status Updates tab</li>
	 *<li><B>Verify:</B> Discover tab</li>
	 *<li><B>Verify:</B> Updates left menu item</li>
	 *<li><B>Verify:</B> Mentions left menu item</li>
	 *<li><B>Verify:</B> My Notifications left menu item</li>
	 *<li><B>Verify:</B> Action Required left menu item</li>
	 *<li><B>Verify:</B> Saved left left menu item</li>
	 *<li><B>Verify:</B> Getting Started left menu item</li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem","regression"} )
	public void homepageLinks() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load homepage and login as User: " + testUser.getEmail());
		log.info("INFO: Loading homepage and logging in as User: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.weakStep("Select Updates from left navigation menu");
		log.info("INFO: Select Updates from left navigation menu");
		Homepage_LeftNav_Menu.UPDATES.select(ui);

		//Following tab
		logger.weakStep("Validate 'Following' tab view is present");
		log.info("INFO: Validate Following tab view");
		ui.fluentWaitPresent(HomepageUIConstants.ImFollowingTab);
	
		//Status Updates tab
		logger.weakStep("Validate 'Status Updates' tab view is present");
		log.info("INFO: Validate Status Updates tab view");
		ui.fluentWaitPresent(HomepageUIConstants.StatusUpdatesTab);
		
		//Discover Tab
		logger.weakStep("Validate 'Discover' tab view is present");
		log.info("INFO: Validate Discover tab view");
		ui.fluentWaitPresent(HomepageUIConstants.DiscoverTab);

		//Updates left menu item
		logger.weakStep(" Validate 'Updates' left menu option is present");
		log.info("INFO: Validate Updates left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_Updates);
				
		//@Mentions left menu item
		logger.weakStep(" Validate '@Mentions' left menu option is present");
		log.info("INFO: Validate @Mentions left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.AtMentions);

		//My Notifications left menu item
		logger.weakStep("Validate 'My Notifications' left menu option is present");
		log.info("INFO: Validate My Notifications left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_MyNotifications);
	
		//Action Required left menu item
		logger.weakStep("Validate 'Required' left menu option is present");
		log.info("INFO: Validate Required left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_ActionRequired);
		
		// Saved left menu item
		logger.weakStep(" Validate 'Saved' left menu option is present");
		log.info("INFO: Validate Saved left menu option");
		ui.fluentWaitPresent(HomepageUIConstants.Ckpt_Saved);

		// NOTE: check Getting Started link - We won't be checking for this link as this is removed from UI
		//logger.weakStep("Validate that 'Getting Started' Link is NOT present");
		//log.info("Verify the 'Getting Started' link is NOT present");
		//Assert.assertFalse(ui.isElementVisible(HomepageUI.Ckpt_GettingStarted),
		//		  "ERROR: The Getting Started link was present.");

		ui.endTest();
		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify @mention is functional.</li>
	 *<li><B>Step:</B> Navigate to User A's Homepage, then create an @mention about User B.</li>
	 *<li><B>Step:</B> Have User A log out.</li>
	 *<li><B>Step:</B> Login with User B.</li>
	 *<li><B>Step:</B> look for @mention from User A under the @mentions tab.</li>
	 *<li><B>Verify:</B> Validate that the @mention is present when selecting @mention menu item </li>
	 *</ul>
	 */
	//TODO Cnx8UI : Removed groupname "cnx8ui-cplevel2" due to defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-11892
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem","regression"} )
	public void homepageAtMentions() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUser1 = cfg.getUserAllocator().getUser();
		
		String message = testName + " message " + Helper.genDateBasedRand();

		//Load component and login
		logger.strongStep("Load homepage and login as User A,load new UI "+cfg.getUseNewUI());
		log.info("INFO: Loading homepage and logging in as User A: " + otherUser.getEmail());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(otherUser,cfg.getUseNewUI());
		
		log.info("Navigate to discover is new UI "+cfg.getUseNewUI());
		logger.strongStep("Navigate to discover is new UI "+cfg.getUseNewUI());
		ui.gotoDiscover();
		
		logger.strongStep("Post status update with @mention about "+ testUser1.getEmail());
		log.info("INFO: Creating and posting the @mention about: " + testUser1.getEmail());
		ui.postAtMentionUserUpdate(testUser1, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);
		log.info("INFO: Steps completed to post the mention");
		
		//validate the message was successfully posted
		logger.strongStep("Validate that the message was succesfully posted");
		Assert.assertTrue(ui.fluentWaitTextPresent("The message was successfully posted."), "ERROR: The message was not successfully posted");
		
		Assert.assertTrue(discoverUi.verifyDiscoverPost(testUser1, message));	
		logger.strongStep("Logout of User A");
		log.info("INFO: Logging out of User A");
		ui.logout();
		
		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);

		// TODO : Cnx8UI - Need to revisit when @mentions code is developed in CNX8UI
		if(!cfg.getUseNewUI()) {
			//Load component and login
			logger.strongStep("Re-load homepage and login as User B");
			log.info("INFO: Re-loading component and logging in as User B: " + testUser1);
			ui.loadComponent(Data.getData().ComponentHomepage);
			ui.login(testUser1);

			logger.strongStep("Click on link for @mentions");
			log.info("INFO: Clicking on link to @mentions");
			ui.clickLinkWait(HomepageUIConstants.AtMentions);

			//Validate the @Mentions message
			logger.weakStep("Validate that the @mentions message is present");
			log.info("INFO: Validate the @mentions message");
			Assert.assertTrue(ui.fluentWaitPresent(HomepageUI.getAtMentionMessage(message)),
					"ERROR: @mentions message not present ");
		}
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Check Notifications works by creating a community with another user (User B).</li>
	 *<li><B>Steps:</B> Create a Community with (Access: Public), and add User B as (Role: Owner).</li>
	 *<li><B>Verify:</B> Log in with User B and validate that User B was notified under My Notification menu item </li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem","regression","cnx8ui-regression"} )
	public void homepageNotifications() throws Exception {
		
		// testUser = user A, otherUser = user B
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//Create a community through API with User A and add User B to it
		logger.strongStep("Create a community with User A and add User B to it");
		log.info("Using API to create a Community with User A: " + testUser.getEmail() + " and add user B: " + otherUser.getEmail() + " to it");
		logger.weakStep("Build community object");
		log.info("INFO: Building community object");
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .addMember(new Member(CommunityRole.OWNERS, otherUser)).build();
		
		Community newCommunity = community.createAPI(apiComOwner);

		//Load component and login with User B
		logger.strongStep("Load the component and login as User B");
		log.info("INFO: Loading the component and logging in as User B: " + otherUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(otherUser,cfg.getUseNewUI());
		
		//click on my notifications
		logger.weakStep("Navigate to 'My Notifications'");
		ui.gotoMyNotifications();
		
		//Validate MyNotifications 
		logger.weakStep("Validate 'My notifications' message: You were added to the community ");
		log.info("INFO: Validate My Notifications message that you were added to community");
		Assert.assertTrue(driver.getVisibleElements("css=div[class='lotusPostAction asLinkContainer']:contains("+ 
				"added you to the " + community.getName() + " community." + ")").size() > 0, 
				"ERROR: My Notification message not present ");

		
		//delete community
		logger.strongStep("Delete the community through the API");
		log.info("INFO: Deleting the community through the API");
		apiComOwner.deleteCommunity(newCommunity);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Check Action Required works by creating an Activity and assigning a To-Do to a second user (User B)</li>
	 *<li><B>Step: </B>Create a new Activity. (Add a new member (User B) as (Type: Person) and (Role: Owner))</li>
	 *<li><B>Step: </B>Create a new ToDo. (Assign to User B)</li>
	 *<li><B>Step: </B>Login User B.</li>
	 *<li><B>Step: </B>Navigate to Homepage, then to Action Required.</li>
	 *<li><B>Verify:</B> Validate that the user (User B) was notified under Action Required menu item. </li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem", "regression", "cnx8ui-regression"} )
	public void homepageActionRequired() throws Exception {
		
		// testUser = user A, otherUser = user B
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();	
		
		// Create an activity and a ToDo for it through the API with user A
		logger.strongStep("Create an activity and a ToDo item through API with User A");
		log.info("Creating an activity through API with User A: " + testUser.getEmail());
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												 .addMember(new ActivityMember(ActivityRole.OWNER, testUser, ActivityMember.MemberType.PERSON))
												 .build();
		
		Activity newActivity = activity.createAPI(apiActivitiesOwner);
		
		log.info("INFO: Creating a todo item through API with User A: " + testUser.getEmail() + " and assigning it to User B: " + otherUser.getEmail());
		BaseActivityToDo toDo = BaseActivityToDo.builder("todo" + Helper.genDateBasedRand())
				.assignTo(otherUser).build();	
		toDo.setParent(newActivity);
		Todo newTodo = toDo.createTodoAPI(apiActivitiesOwner);
		
		// Use API to add user B to the activity and assign ToDo to user B
		logger.strongStep("Add user B to the activity and assign ToDo to user B");
		log.info("Adding user B: " + otherUser.getEmail() + " to the activity");
		apiActivitiesOwner.addMemberToActivity(newActivity, otherUser);
		
		log.info("INFO: Assigning user B to todo");
		APIProfilesHandler apiProfilesOwner = new APIProfilesHandler(serverURL, otherUser.getEmail(), otherUser.getPassword());
		apiActivitiesOwner.assignToDoItemToUser(newTodo, apiProfilesOwner);
		
		//Load component and login with user B
		logger.strongStep("Load homepage and login as User B");
		log.info("INFO: Loading homepage and logging in as User B: " + otherUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(otherUser, cfg.getUseNewUI());
		
		//click on Action Required
		logger.strongStep("Click on 'Action Required' from left menu/personal filter");
		log.info("INFO: Click on 'Action Required' from left menu/personal filter");
		ui.gotoActionRequired();
		
		//Validate Action Required 
		logger.weakStep("Validate 'Action Required' message: You have an action assigned to you");
		log.info("INFO: Validate Action Required message that you have an action assigned to you");
		Assert.assertTrue(ui.fluentWaitTextPresent("assigned you a to-do item named " + toDo.getTitle() + 
											   " in the " + activity.getName() + " activity."), 
											   "ERROR: Action Required message not present");
		
		
		//click on activity link
		logger.strongStep("Select the Activity's link");
		log.info("INFO: Clicking the Activity's link");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
	
		//Switch to Activity window
		logger.strongStep("Switch to the 'New Activity' window");
		log.info("INFO: Switch to the new Activity window");
		driver.switchToFirstMatchingWindowByPageTitle(activity.getName());
		
		//Go to Activities list
		logger.strongStep("Switch to the list of activities");
		log.info("INFO: Switch to the list of activies");
		ui.clickLink(ActivitiesUIConstants.Activities_Tab);
		
		//Check the newly created activity
		logger.strongStep("Validating the newly created activity is in the catalog of the activities list");
		log.info("INFO: Validating the newly created activity is in the catalog of the activities list");
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()), 
				   "ERROR: "+activity.getName()+" not present in Activities List");

		//delete activity through API
		log.info("INFO: Deleting the activity");
		apiActivitiesOwner.deleteActivity(newActivity);
		
		ui.endTest();
	}
	
	@Deprecated
	@Test (groups = {""} )
	public void homepageActionRequiredLinkCheck() throws Exception {
		
		// testUser = user A, otherUser = user B
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();	
		
		// Create an activity and a ToDo for it through the API with user A
		logger.strongStep("Create an activity and a ToDo item through API with User A");
		log.info("Creating an activity through API with User A: " + testUser.getEmail());
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												 .addMember(new ActivityMember(ActivityRole.OWNER, testUser, ActivityMember.MemberType.PERSON))
												 .build();
		
		Activity newActivity = activity.createAPI(apiActivitiesOwner);
		
		log.info("INFO: Creating a todo item through API with User A: " + testUser.getEmail() + " and assigning it to User B: " + otherUser.getEmail());
		BaseActivityToDo toDo = BaseActivityToDo.builder("todo" + Helper.genDateBasedRand())
				.assignTo(otherUser).build();	
		toDo.setParent(newActivity);
		Todo newTodo = toDo.createTodoAPI(apiActivitiesOwner);
		
		// Use API to add user B to the activity and assign ToDo to user B
		logger.strongStep("Add user B to the activity and assign ToDo to user B");
		log.info("Adding user B: " + otherUser.getEmail() + " to the activity");
		apiActivitiesOwner.addMemberToActivity(newActivity, otherUser);
		
		log.info("INFO: Assigning user B to todo");
		APIProfilesHandler apiProfilesOwner = new APIProfilesHandler(serverURL, otherUser.getEmail(), otherUser.getPassword());
		apiActivitiesOwner.assignToDoItemToUser(newTodo, apiProfilesOwner);
		
		//Load component and login with user B
		logger.strongStep("Load homepage and login as User B");
		log.info("INFO: Loading homepage and logging in as User B: " + otherUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(otherUser);
		
		//click on Action Required
		logger.strongStep("Click on 'Action Required' from left menu");
		log.info("INFO: Select Action Required from left menu");
		ui.clickLinkWait(HomepageUIConstants.Ckpt_ActionRequired);
		
		//Validate Action Required 
		logger.weakStep("Validate 'Action Required' message: You have an action assigned to you");
		log.info("INFO: Validate Action Required message that you have an action assigned to you");
		Assert.assertTrue(ui.fluentWaitTextPresent("assigned you a to-do item named " + toDo.getTitle() + 
											   " in the " + activity.getName() + " activity."), 
											   "ERROR: Action Required message not present");
		
		//click on activity link
		logger.strongStep("Checking the Activity's link");
		log.info("INFO: Checking the Activity's link exists");
		ui.fluentWaitElementVisible(ActivitiesUI.getActivityLink(activity));
		log.info("INFO: The Activity Link exists in homepage ActionRequired tab.");
		
		
		//Go to Activities list
		logger.strongStep("Switch to the list of activities");
		log.info("INFO: Switch to the list of activies");
		ui.loadComponent(Data.getData().ComponentActivities, true);
				
		//Check the newly created activity
		logger.strongStep("Validating the newly created activity is in the catalog of the activities list");
		log.info("INFO: Validating the newly created activity is in the catalog of the activities list");
		Assert.assertTrue(ui.fluentWaitTextPresent(activity.getName()), 
						   "ERROR: "+activity.getName()+" not present in Activities List");

		//delete activity through API
		log.info("INFO: Deleting the activity");
		apiActivitiesOwner.deleteActivity(newActivity);
				
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Check Action Required works by creating an Activity and then assigning yourself a ToDo.</li>
	 *<li><B>Step:</B> Create a new Activity.</li>
	 *<li><B>Step:</B> Create a new ToDo, assign to yourself.</li>
 	 *<li><B>Step:</B>Go to Homepage and inspect Action Required in left nav</li>
	 *<li><B>Verify:</B>The action required badge displays</li>
	 *<li><B>Step:</B>Click Action Required in left nav and filter view by Activities</li>
	 *<li><B>Verify:</B>The news story appears in action required</li>
	 *<li><B>Step:</B> Select to save the action you are required to do.</li>
	 *<li><B>Step:</B> Navigate to homepage's 'Saved'.</li>
	 *<li><B>Verify:</B> Validate that the item was saved under Saved menu item. </li>
	 *</ul>
	 */
	@Test (groups = {"level2", "smokeonprem", "regressioncloud", "bvtcloud", "smokecloud","regression", "cnx8ui-cplevel2"} )
	public void homepageActionRequiredAndSaved() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();	
		
		//Create an activity	
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).build();
		logger.strongStep("Create an Activity using API");
		log.info("INFO: Creating an Activity using API");
		Activity newActivity = activity.createAPI(apiActivitiesOwner);
		
		//create a todo item for newActivity with API
		BaseActivityToDo toDo = BaseActivityToDo.builder("todo" + Helper.genDateBasedRand())
				.assignTo(testUser).build();	
		toDo.setParent(newActivity);
		
		String ARNewsStoryOLD = "assigned you a to-do item named " + toDo.getTitle() + " in the " + activity.getName() + " activity.";
		String ARNewsStoryNEW = "assigned themselves a to-do item named " + toDo.getTitle() + " in the " + activity.getName() + " activity.";

		logger.strongStep("Create a 'TODO' item with API");
		log.info("INFO: Creating a todo item with API");
		Todo newTodo = toDo.createTodoAPI(apiActivitiesOwner);
		
		// Assign the ToDo to testUser
		log.info("INFO: Assigning the user to todo");
		APIProfilesHandler apiProfilesOwner = new APIProfilesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiActivitiesOwner.assignToDoItemToUser(newTodo, apiProfilesOwner);
		
		//Login to Homepage
		logger.strongStep("Load homepage I am following page");
		log.info("INFO: Loading the component homePage I am following page");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		
		logger.strongStep("Login and toggle to New UI as "+ cfg.getUseNewUI());
		log.info("Info: login and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		//click on Action Required
		logger.strongStep("Select 'Action Required' menu item");
		log.info("INFO: Select Action Required menu item");
		ui.selectHomepageMenu("Action Required");
			
		//Check that action required badge appears
		logger.weakStep("Verify that 'Action Required' badge appears");
		log.info("INFO: Verify that action required badge appears");
		Assert.assertTrue(ui.isElementVisibleWd((cfg.getUseNewUI()?By.xpath(HomepageUIConstants.ActionRequiredBadgeNew):ui.createByFromSizzle(HomepageUIConstants.ActionRequiredBadge)), 2),"Action required badge not visible");

		//Filter by Activities
		logger.strongStep("Filter results by Activities");
		log.info("INFO: Filter results by Activities");
		ui.filterBy("Activities");
		
		//Check that news story appears in action required
		logger.weakStep("Verify that news story appears in 'Action Required'");
		log.info("INFO: Verify that news story appears in action required");		
		Assert.assertTrue(ui.fluentWaitTextPresent(ARNewsStoryOLD, ARNewsStoryNEW),
						 "ERROR: News story failed to appear");

		//click on Save this for our News Item
		logger.strongStep("Click on 'Save this for our news item'");
		log.info("INFO: Click on 'Save this for our news item'");
		ui.fluentWaitTextPresent(toDo.getTitle());
		ui.clickLinkWait("css=div[aria-describedby='" + ui.getNewsStoryElementID(toDo.getTitle()) +
							 "_openeedescription'] div[class='lotusPostContent'] div[class='lotusMeta lotusChunk'] ul[dojoattachpoint='actionListContainer'] li[class*='lotusFirst'] a[role='button']");	
		
		//validate that the News Item now reports being saved
		logger.weakStep("Validate that news item reports as saved");
		ui.fluentWaitPresent("css=div[aria-describedby='" + ui.getNewsStoryElementID(toDo.getTitle()) + 
							 "_openeedescription']:contains(Saved)");
		
		ui.selectHomepageMenu("Saved");
		
		//Validate Saved menu item 
		logger.weakStep("Validate saved message: 'You have an action assigned to you'");
		log.info("INFO: Validate Saved message that you have an action assigned to you");
		Assert.assertTrue(ui.fluentWaitTextPresent("to-do item named " + toDo.getTitle() + 
											   " in the " + activity.getName() + " activity."), 
											   "ERROR: Saved message not present ");
		
		//delete activity through API
		log.info("INFO: Deleting the activity");
		apiActivitiesOwner.deleteActivity(newActivity);
		
		ui.endTest();
	}
				
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the left navigation menu item Action Required and its tab exist and work.</B></li>
	 *<li><B>Verify: Validate the left navigation menu item Action Required exists.</B></li>
	 *<li><B>Step: Select Action Required from the navigation menu.</B></li> 
	 *<li><B>Verify: Validate the Action Required tab exists.</B></li> 
	 *<li><B>Step: Select the Action Required tab.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void homepageActionsRequired() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);

		logger.strongStep("If sametime is enabled, allow time for it load");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		
		//Validate the left navigation menu item Action Required exists
		logger.weakStep("Validate that the left navigation menu item 'Action Required' exists");
		log.info("INFO: Validate the left navigation menu item Action Required exists");
		Assert.assertTrue(ui.fluentWaitPresent("linkpartial=" + Homepage_LeftNav_Menu.ACTIONREQUIRED.getMenuItemText()),
						  "ERROR: Unable to find Action Required Left menu item");

		logger.strongStep("Select 'Actions Required' from the left navigation menu");
		log.info("INFO: Select Action Required from Left Navigation menu");
		Homepage_LeftNav_Menu.ACTIONREQUIRED.select(ui);

		//Validate Action Required tab
		logger.weakStep("INFO: Verify the 'Action Required' tab is present");
		log.info("INFO: Verify the Action Required tab");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUIConstants.ActionRequiredTab),
						"ERROR: Action Required tab is not present.");
		
		logger.strongStep("Select the 'Action Required' tab");
		log.info("INFO: Select tab");
		ui.clickLinkWait(HomepageUIConstants.ActionRequiredTab);
		
		ui.endTest();

	}
		
	/**
	 *<ul>
	 *<li><B>Smart Cloud Test ONLY</B></li>
	 *<li><B>Info: Test case to test if the left navigation menu item Getting Started does NOT exist</B></li>
	 *<li><B>Verify: Validate that the left menu item Getting Started does NOT exist on Smart Cloud</B></li>
	 *<li><B>Step: Log in as any valid user and verify the absence of the "Getting Started" link from the navigation menu</B></li>
	 *</ul>
	 */
	@Deprecated
	//@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void homepageGettingStarted() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);

		logger.strongStep("If sametime is enabled, allow time for it load");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		
		// Validate the left navigation menu item Getting Started does NOT exist
		logger.weakStep("Validate that the left navigation menu item 'Getting Started ' does NOT exist");
		log.info("INFO: Validate the left navigation menu item Getting Started does NOT exist");
		Assert.assertFalse(ui.isElementVisible("linkpartial=" + Homepage_LeftNav_Menu.GETTINGSTARTED.getMenuItemText()),
						  "ERROR: The 'Getting Started' Left menu item was incorrectly displayed in the UI");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Validate the widgets in the right side panel of Updates page</li>
	 *<li><B>Step:</B> Select Updates from left navigation menu</li>
	 *<li><B>Verify:</B> Meetings widget displays in the right side panel</li>
	 *<li><B>Verify:</B> Events widget displays in the right side panel</li>
	 *<li><B>Verify:</B> Recommendations widget displays in the right side panel</li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem","regression","cnx8ui-regression"} )
	public void homepageRightSection() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Homepage and login");
		log.info("Loading Homepage and logging in as User: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Select 'Updates' from the left navigation menu");
		log.info("INFO: Select Updates from left navigation menu");
		Homepage_LeftNav_Menu.UPDATES.select(ui);

		// Check for Meeting Section
		logger.weakStep("Verify that 'Meeting' section is present");
		ui.verifyMeetingsWidget();
		
		// Check for event Section
		// Since CNXSERV-6964 won't be fixed, just check if there is at least one Events widget
		logger.weakStep("Validate that 'Events' widget is present in the right side panel");
		log.info("INFO: Validate Events widget in right side panel");
		ui.getFirstVisibleElement(HomepageUIConstants.eventsWidget);
		ui.getFirstVisibleElement(HomepageUIConstants.eventsWidgetIframe);
		ui.switchToFrame(HomepageUIConstants.eventsWidgetIframe, HomepageUIConstants.eventsGadgetDiv);
		ui.switchToTopFrame();
				
		// Check for Recommendations Section
		logger.weakStep("Validate that the 'Reccomendations' widget in the right side panel is present");
		log.info("INFO: Validate Recommendations widget in right side panel");
		ui.getFirstVisibleElement(HomepageUIConstants.recommendationsWidet);
		try {
			ui.getFirstVisibleElement(HomepageUIConstants.recommendationsWidgetContent);
		} catch (AssertionError ae) {
			// recommendation widget might have content, look for the widget container
			ui.getFirstVisibleElement(HomepageUIConstants.recommendationsWidgetContainer);	
		}
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Validate the proper links are present on Homepage for a guest user</li>
	 *<li><B>Step:</B> Log in as guest user and navigate to guest user's Homepage</li>
	 *<li><B>Verify:</B> Verify left panel's myStream link presents </li>
	 *<li><B>Verify:</B> Verify left panel's MyNotifications link presents </li>
	 *<li><B>Verify:</B> Verify left panel's ActionRequired link presents </li>
	 *<li><B>Verify:</B> Verify left panel's Saved link presents </li>
	 *<li><B>Verify:</B> Verify left panel's getting Started link presents </li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void GuesthomepageLinks() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		User guestUser = cfg.getUserAllocator().getGuestUser();
		
		//Load component and login as guest user
		logger.strongStep("Load homepage and login as guest user");
		log.info("INFO: Load homepage and log in as guest user: " + guestUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(guestUser);
	
		//Checks 'MyStream' is present
		logger.weakStep("Validate the 'myStream' link is present");
		log.info("INFO: Validate The 'myStream' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUICloud.myStream),
				  "ERROR: The myStream link was not present.");
		
		//Checks 'My Notifications' is present 
		logger.weakStep("Validate the 'My Notifications' link is present");
		log.info("INFO: Validate the 'My Notifications' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.Ckpt_MyNotifications),
				  "ERROR: The MyNotifications link was not present.");
				
		//Checks 'Action Required' is present 
		logger.weakStep("Validate the 'Action Required' left menu option is present");
		log.info("INFO: Validate the 'Action Required' left menu option is present");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.Ckpt_ActionRequired),
				  "ERROR: The ActionRequired link was not present.");
		
		//Checks 'Saved' option is present
		logger.weakStep("Validate the 'Saved' left menu option is present");
		log.info("INFO: Validate the 'Saved' left menu option is present");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.Ckpt_Saved),
				  "ERROR: The Saved link was not present.");
		
		// NOTE: check Getting Started link - We won't be checking for this link as this is removed from UI
				//logger.weakStep("Validate that 'Getting Started' Link is NOT present");
				//log.info("Verify the 'Getting Started' link is NOT present");
				//Assert.assertFalse(ui.isElementVisible(HomepageUI.Ckpt_GettingStarted),
				//		  "ERROR: The Getting Started link was present.");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify help page for Homepage</li>
	*<li><B>Step: </B>Go to Homepage</li>
	*<li><B>Step: </B>Click on Help logo on top right corner of homepage</li>
	*<li><B>Verify: </B>Verify Homepage text from Homepage help page</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2","cnx8ui-cplevel2","cnx8ui-level2" })
	public void verifyHelpOnHomepage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Select 'Help' menu on homepage");
		log.info("INFO: Select 'Help' menu on homepage");
		ui.gotoHelp();

		logger.strongStep("Switch to homepage help window");
		log.info("INFO: Switch to homepage help window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().homepageHelpContent);
		
		ui.waitForPageLoaded(driver);
	
		String HomePageHelpContent = ui.findElement(By.cssSelector(HomepageUIConstants.HomepageWelcomeText)).getText();
		
		logger.strongStep("Verify help content for Homepage is opened");
		log.info("INFO: Verify help content for Homepage is opened");
		Assert.assertTrue(HomePageHelpContent.equals(Data.getData().homepageHelpContent), "Home page help is available");
		
		driver.close();

		ui.endTest();
	}
}
