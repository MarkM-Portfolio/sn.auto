package com.ibm.conn.auto.tests.activities;


import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityTemplate;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.ActivitiesUICnx8;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Cnx8UI_Activities extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Activities.class);
	private Assert cnxAssert;
	private ActivitiesUICnx8 ui;
	private ActivitiesUI actUI;
	private TestConfigCustom cfg;
	private User testUser;
	private String serverURL;
	private APIActivitiesHandler apiOwner;
	private APICommunitiesHandler apiComOwner;
	private BaseCommunity.Access defaultAccess;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = new ActivitiesUICnx8(driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate Top level tabs, Start an Activity Button and Tag section on My activity page
	 * also different buttons and link on Activity entry page</li>
	 *<li><B>Step:</B> Create an Activity via API</li>
	 *<li><B>Step:</B> Login to Activities and Toggle to the new UI</li>
	 *<li><B>Verify: </B>Verify Start an Activity button</li>
	 *<li><B>Verify: </B>Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs</li>
	 *<li><B>Verify: </B>Verify Tag section is displayed on right-side of panel</li>
	 *<li><B>Step:</B> Navigate to Created Activity</li>
	 *<li><B>Verify: </B>Verify 'Add Section', 'Add Entry', and 'Add To Do Item' buttons</li>
	 *<li><B>Verify: </B>Verify 'Follow the Activity', 'Mark Activity Complete', and 'Activity Actions' links</li>
	 *<li><B>Verify: </B>Verify Tag section is displayed on right-side of panel</li>
	 *</B>Verify 'Activity Outline', 'Recent Updates', 'To Do Items' , 'Trash' , 'Members' , 'Sections' links are displayed on the left-side nav bar</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/130556</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T809</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyLinksOnMyActivityAndActivityEntryPage() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
				.dueDateRandom()
				.useCalPick(true)
				.goal(Data.getData().commonDescription + testName)
				.build();
		
		logger.strongStep("Create An Activity via API");
		log.info("INFO: Create An Activity via API");
		Activity act = activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Activities, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForElementInvisibleWd(By.cssSelector(ActivitiesUIConstants.loadingIcon), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Start an Activity button displayed");
		log.info("INFO: Verify Start an Activity button displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.startAnActivityBtn)), "Start an Activity button is visible");
		
		logger.strongStep("Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs displayed on top of header");
		log.info("INFO: Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs displayed on top of header");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activitiesTab)), "Activities Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.toDoListTab)), "To Do List Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityTemplatesTab)), "Activity Templates Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.recentUpdatesTab)), "Recent Activities Tab is visible");
		
		logger.strongStep("Verify Tag section is displayed on right-side of panel");
		log.info("INFO: Verify Tag section is displayed on right-side of panel");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.myActivities_tagSection)), "Tag section displayed");
		
		logger.strongStep("Open the Activity");
		log.info("INFO: Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify 'Add Section', 'Add Entry', and 'Add To Do Item' buttons are displayed");
		log.info("INFO: Verify 'Add Section', 'Add Entry', and 'Add To Do Item' buttons are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.addSectionBtn)), "Add Section btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.addEntryBtn)), "Add Entry btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.addToDoItemBtn)), "Add To Do Item btn is visible");
		
		logger.strongStep("Verify 'Follow the Activity', 'Mark Activity Complete', and 'Activity Actions' links are displayed on the right-side nav bar");
		log.info("INFO: Verify 'Follow the Activity', 'Mark Activity Complete', and 'Activity Actions' links are displayed on the right-side nav bar");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.followThisActLink)), "Follow the Activity is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.markActCompleteLink)), "Mark Activity Complete is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityActionLink)), "Activity Actions is visible");
		
		logger.strongStep("Verify Tag section is displayed on right-side nav bar");
		log.info("INFO: Verify Tag section is displayed on right-side of nav bar");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityOutline_tagSection)), "Tag Section is visible");
		
		logger.strongStep("Verify 'Activity Outline', 'Recent Updates', 'To Do Items' , 'Trash' , 'Members' , 'Sections' links are displayed on the left-side nav bar");
		log.info("INFO: Verify 'Activity Outline', 'Recent Updates', 'To Do Items' , 'Trash' , 'Members' , 'Sections' links are displayed on the left-side nav bar");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityOutline)), "Activity Outline is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.recentUpdates)), "Recent Updates is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.todoItems)), "To Do Items is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.trash)), "Trash is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.members)), "Members is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.sectionsDropdown)), "Sections is visible");
		
		// Delete Activity via API
		apiOwner.deleteActivity(act);
		
		ui.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate Community Header, Following Actions, Mark Activity Complete,Activity Actions, Community Actions links
	 * and Tag section on Community Activities page are displayed</li>
	 *<li><B>Step:</B> Create a community and add Activities app to community via API</li>
	 *<li><B>Step:</B> Create an Activity in community via API</li>
	 *<li><B>Step:</B> Login to Activities and Toggle to the new UI</li>
	 *<li><B>Step:</B> Navigate to Community Activities page from left nav menu</li>
	 *<li><B>Step:</B> Open created community activities</li>
	 *<li><B>Verify:</B>Verify Community Header is displayed</li>
	 *<li><B>Verify:</B>Verify 'Following Actions', 'Mark Activity Complete', 'Activity Actions', and 'Community Actions' links are displayed</li>
	 *<li><B>Verify:</B>Verify Tag section is displayed on right-side of panel</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T805</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyCommunityActivityPage() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.goal("Goal for "+ testName)
				.community(community)
				.build();
		
		//Create community
		log.info("INFO: Create a Community using API");
		logger.strongStep("Create a Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//Add the Activities widget
		log.info("INFO: Add the 'Activities' widget to the Community using API");
		logger.strongStep("Add the 'Activities' widget to the Community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.ACTIVITIES);
		
		//Add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Create activity
		log.info("INFO: Create an Activity in the community using API");
		logger.strongStep("Create an Activity in the community using API");
		activity.createAPI(apiOwner, community);
		
		// Load the component and login
		logger.strongStep("Load Activities, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Activities, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Select Community Activities from left nav bar");
		log.info("INFO: Select Community Activities from left nav bar");
		ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.communityActivitiesLink), 5, "Community Activities is selected");
		
		logger.strongStep("Open Community Activity");
		log.info("INFO: Open Community Activity");
		ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.communityActivitiesName.replace("PLACEHOLDER", activity.getName())), 5, "Clicked on Community Activity");
		ui.waitForElementInvisibleWd(By.cssSelector(ActivitiesUIConstants.loadingIcon), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Community header is displayed");
		log.info("INFO: Verify Start an Activity button displayed");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.activityActionButton), 5);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.communityHeaderName.replace("PLACEHOLDER", community.getName()))), "Community Header is visible");
		
		logger.strongStep("Verify Actions buttons 'Following Actions', 'Mark Activity Complete', 'Activity Actions', and 'Community Actions' are displayed");
		log.info("INFO: Verify Actions buttons 'Following Actions', 'Mark Activity Complete', 'Activity Actions', and 'Community Actions' are displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.actionsButton.replace("PLACEHOLDER", "Following Actions"))), "Following Actions btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.actionsButton.replace("PLACEHOLDER", "Mark Activity Complete"))), "Mark Activity Complete btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityActionButton)), "Activity Actions btn is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.actionsButton.replace("PLACEHOLDER", "Community Actions"))), "Community Actions btn is visible");
		
		logger.strongStep("Verify Tag section is displayed on right-side of panel");
		log.info("INFO: Verify Tag section is displayed on right-side of panel");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.comActivities_tagSection)), "Tag section displayed");
		
		logger.strongStep("Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate Top level tabs, Start an Activity Button and Tag section on My activity page
	 * also different buttons and link on Activity entry page</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to the new UI</li>
	 *<li><B>Step:</B> Navigate to Activites page from left nav menu</li>
	 *<li><B>Verify: </B>Verify Start an Activity button</li>
	 *<li><B>Verify: </B>Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs</li>
	 *<li><B>Step:</B> Click on 'To Do List' tab from Top Navigation bar</li>
	 *<li><B>Verify:</B> Verify 'My To Do Items' page title is displayed</li>
	 *<li><B>Step:</B> Click on 'Activity Templates' tab from Top Navigation bar</li>
	 *<li><B>Verify:</B> Verify 'Activity Templates' page title is displayed</li>
	 *<li><B>Step:</B> Click on 'Recent Updates' tab from Top Navigation bar</li>
	 *<li><B>Verify:</B> Verify 'Recent Updates' page title is displayed</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T810</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyNavigationToAllTopNavheaders() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();

		// Load the component and login
		logger.strongStep("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Select Activities in left nav menu");
		log.info("INFO: Select Activities in left nav menu");
		AppNavCnx8.ACTIVITIES.select(ui);
		ui.waitForElementInvisibleWd(By.cssSelector(ActivitiesUIConstants.loadingIcon), 5);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Verify Start an Activity button displayed");
		log.info("INFO: Verify Start an Activity button displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.startAnActivityBtn)), "Start an Activity button is visible");

		logger.strongStep("Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs displayed on top of header");
		log.info("INFO: Verify 'Activities', 'To Do List', 'Activity Templates', and 'Recent Activities' Tabs displayed on top of header");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activitiesTab)), "Activities Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.toDoListTab)), "To Do List Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activityTemplatesTab)), "Activity Templates Tab is visible");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.recentUpdatesTab)), "Recent Activities Tab is visible");

		logger.strongStep("Click on 'To Do List' tab from Top Navigation bar");
		log.info("INFO: Click on 'To Do List' tab from Top Navigation bar");
		ui.clickLinkWd(ui.findElement(By.xpath(ActivitiesUIConstants.toDoListTab)), "Clicked on 'To Do List' tab from Top Navigation bar");

		logger.strongStep("Verify 'My To Do Items' page title is displayed");
		log.info("INFO: Verify 'My To Do Items' page title is displayed");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(ActivitiesUIConstants.todoPageTitle))), "My To Do Items", "My To Do Items is visible");

		logger.strongStep("Click on 'Activity Templates' tab from Top Navigation bar");
		log.info("INFO: Click on 'Activity Templates' tab from Top Navigation bar");
		ui.clickLinkWd(By.xpath(ActivitiesUIConstants.activityTemplatesTab), "Clicked on 'Activity Templates' tab from Top Navigation bar");

		logger.strongStep("Verify 'Activity Templates' page title is displayed");
		log.info("INFO: Verify 'Activity Templates' page title is displayed");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(ActivitiesUIConstants.activityTemplatesPageTitle))), "Activity Templates", "Activity Templates is visible");

		logger.strongStep("Click on 'Recent Updates' tab from Top Navigation bar");
		log.info("INFO: Click on 'Recent Updates' tab from Top Navigation bar");
		ui.clickLinkWd(By.xpath(ActivitiesUIConstants.recentUpdatesTab), "Clicked on 'Recent Updates' tab from Top Navigation bar");

		logger.strongStep("Verify 'Recent Updates' page title is displayed");
		log.info("INFO: Verify 'Recent Updates' page title is displayed");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(ActivitiesUIConstants.recentUpdatesPageTitle))), "Recent Updates", "Recent Updates is visible");

		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Verify pagination along with result per page drop-down on My Activities, Community Activities and Completed Activities page</li>
	 *<li><B>Step:</B>[API] create activity,community activity,and completed activity</li>
	 *<li><B>Step:</B> Login to Activity and Toggle to the new UI</li>
	 *<li><B>Verify: </B>Verify pagination along with result page drop-down is displayed on My Activity page</li>
	 *<li><B>Verify: </B> Select 'Community Activities' from left nav and verify pagination along with result page drop-down is displayed</li>
	 *<li><B>Verify: </B> Select 'Completed Activities' from left nav and verify pagination along with result page drop-down is displayed</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T807</li>
	 *</ul>
	 */

	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPaginationForActivityPages() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();

		BaseActivity activity,completeActivity,comActivity;
		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(BaseCommunity.Access.PUBLIC).build();
 
		activity = new BaseActivity.Builder("activity"+testName + Helper.genDateBasedRand())
				.dueDateRandom()
				.isPublic(true)
				.goal(Data.getData().commonDescription + testName)
				.build();
		
		completeActivity = new BaseActivity.Builder("completeActivity"+testName + Helper.genDateBasedRand()).dueDateRandom()
				.complete(true)
				.isPublic(true)
				.goal(Data.getData().commonDescription + testName).build();

		comActivity = new BaseActivity.Builder("comActivity" + testName + Helper.genDateBasedRand()).dueDateRandom()
				.community(community).goal(Data.getData().commonDescription + testName).build();
		
		// Create community
		log.info("INFO: Create a Community using API");
		logger.strongStep("Create a Community using API");
		Community comAPI = community.createAPI(apiComOwner);

		logger.strongStep("Create different Activities via API");
		log.info("INFO: Create different Activities via API");
		Activity act = activity.createAPI(apiOwner);
		Activity comAct = comActivity.createAPI(apiOwner);
		Activity completeAct = completeActivity.createAPI(apiOwner);
			
		logger.strongStep("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Verify pagination along with result per page drop-down is displayed on My Activity page");
		log.info("INFO: Verify pagination along with result page drop-down is displayed on My Activity page");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.activitiesPagination), 6);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activitiesPagination)), "Pagination is visible");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.resultPerPageDropdown), 6);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.resultPerPageDropdown)), "Result per page dropdown is visible");
		
		logger.strongStep("Select 'Community Activity' from left nav and verify pagination along with result per page drop-down is displayed");
		log.info("INFO: Select 'Community Activity' from left nav and verify pagination along with result per page drop-down is displayed");
		selectLeftNavOptionAndVerifyPagination(By.xpath(ActivitiesUIConstants.communityActivities),"Community Activity");
		
		logger.strongStep("Select 'Completed Activity' from left nav and verify pagination along with result per page drop-down is displayed");
		log.info("INFO: Select 'Completed Activity' from left nav and verify pagination along with result per page drop-down is displayed");
		selectLeftNavOptionAndVerifyPagination(By.xpath(ActivitiesUIConstants.completedActivities),"Completed Activity");
		
		apiOwner.deleteActivity(act);
		apiOwner.deleteActivity(comAct);
		apiOwner.deleteActivity(completeAct);
		
		// Delete community via API
		apiComOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Verify pagination along with result per page drop-down on High Priority , Medium Priority pages</li>
	 *<li><B>Step:</B>[API] create activities</li>
	 *<li><B>Step:</B> Login to Activity and Toggle to the new UI</li>
	 *<li><B>Step:</B>Select 'My Activities' from left nav</li>
	 *<li><B>Step:</B>Select 'More' link and Mark activity as turned out from priority drop-down </li>
	 *<li><B>Verify:</B> Verify pagination along with result page drop-down is displayed on Turned out Activity page</li>
	 *<li><B>Step:</B>Select 'My Activities' from left nav</li>
	 *<li><B>Step:</B>Select 'More' link and Mark activity as 'High Priority' from priority drop-down </li>
	 *<li><B>Verify:</B> Verify pagination along with result page drop-down is displayed on High Priority Activity page</li>
	 *<li><B>Step:</B>Select 'My Activities' from left nav</li>
	 *<li><B>Step:</B>Select 'More' link and Mark activity as 'Medium Priority' from priority drop-down </li>
	 *<li><B>Verify:</B> Verify pagination along with result page drop-down is displayed on Medium Priority Activity page</li>
	 *<li><B>Step:</B>Select 'My Activities' from left nav</li>
	 *<li><B>Step:</B>Select 'More' link and delete the activity</li>
	 *<li><B>Verify:</B> Verify pagination along with result page drop-down is displayed on Trash page</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T807</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPaginationForPrioritizeActivityPages() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();

		BaseActivity activity,turnedOutActivity;
		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		//List<Activity> activities = new ArrayList<>();
	
		activity = new BaseActivity.Builder("activity"+testName + Helper.genDateBasedRand())
				.dueDateRandom()
				.isPublic(true)
				.goal(Data.getData().commonDescription + testName)
				.build();
				
		turnedOutActivity = new BaseActivity.Builder("turnedOutActivity"+testName + Helper.genDateBasedRand())
				.dueDateRandom()
				.isPublic(true).build();
		
		logger.strongStep("Create activity via API");
		log.info("INFO: Create activity via API");
		Activity act = activity.createAPI(apiOwner);
		Activity turnedOutAct = turnedOutActivity.createAPI(apiOwner);
		
		logger.strongStep("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Mark activity as Turned out and verify pagination");
		log.info("INFO: Mark activity as Turned out and verify pagination");
		markActivityAndVerifyPagination(turnedOutActivity,ActivitiesUIConstants.markAsTurnedOut,ActivitiesUIConstants.turnedOutLeftNav,"Turned Out");
		
		logger.strongStep("Mark activity as High Priority and verify pagination");
		log.info("INFO: Mark activity as High Priority and verify pagination");
		markActivityAndVerifyPagination(activity,ActivitiesUIConstants.highPriority,ActivitiesUIConstants.highPriorityLeftNav,"High Priority");
		
		logger.strongStep("Mark activity as Medium Priority and verify pagination");
		log.info("INFO: Mark activity as Medium Priority and verify pagination");
		markActivityAndVerifyPagination(activity,ActivitiesUIConstants.mediumPriority,ActivitiesUIConstants.mediumPriorityLeftNav,"Medium Priority");
			
		logger.strongStep("Select 'My Activities' from left nav");
		log.info("INFO: Select 'My Activities' from left nav");
		ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.myActivity), 6, "Select My activities");
		
		logger.strongStep("Delete activity");
		log.info("INFO: Delete activity");
		actUI.delete(activity);
		
		logger.strongStep("Select 'Trash' from left nav and verify pagination along with result per page drop-down is displayed");
		log.info("INFO:Select 'Trash' from left nav and verify pagination along with result per page drop-down is displayed");
		selectLeftNavOptionAndVerifyPagination(By.xpath(ActivitiesUIConstants.turnedOutLeftNav),"Turned out Activities");
		
		apiOwner.deleteActivity(act);
		apiOwner.deleteActivity(turnedOutAct);

		ui.endTest();
	}
	/**
	 *<ul>
	 *<li><B>Info: </B>Verify pagination along with result per page drop-down on Activity Template page</li>
	 *<li><B>Step:</B> Login to Activity and Toggle to the new UI</li>
	 *<li><B>Step:</B> Select 'Activity Template' from top nav and create a Activity Template</li>
	 *<li><B>Step:</B> Click on 'Activity Templates' tab from Top Navigation bar</li>
	 *<li><B>Verify:</B> Verify pagination along with result page drop-down is displayed on Activity Template</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T807</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPaginationForActivityTemplate() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();

		BaseActivityTemplate template = new BaseActivityTemplate.Builder(testName + Helper.genDateBasedRand())
				.description("Description for " + testName + Helper.genDateBasedRand())
				.build();
		
		logger.strongStep("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("INFO: Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO: Select 'Activity Templates' tab from Top nav and create a new template");
		template.create(actUI);
		
		logger.strongStep("Click on 'Activity Templates' tab from Top Navigation bar");
		log.info("INFO: Click on 'Activity Templates' tab from Top Navigation bar");
		ui.clickLinkWd(By.xpath(ActivitiesUIConstants.activityTemplatesTab), "Clicked on 'Activity Templates' tab from Top Navigation bar");
		
		logger.strongStep("Verify pagination along with result per page drop-down is displayed");
		log.info("INFO: Verify pagination along with result page drop-down is displayed");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.activitiesPagination), 6);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.activitiesPagination)), "Pagination is visible");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.resultPerPageDropdown), 6);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(ActivitiesUIConstants.resultPerPageDropdown)), "Result per page dropdown is visible");
	
		ui.endTest();
	}



	/**
	 * This method selects specified left nav option and verifies pagination on that page
	 * 
	 * @param leftNavOption
	 */
	private void selectLeftNavOptionAndVerifyPagination(By leftNavOption , String navMenu) {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("INFO: Select left nav menu option  "+navMenu);
		logger.strongStep("Select left nav menu option  "+navMenu);
		ui.clickLinkWaitWd(leftNavOption, 4, "Select left nav option");
		
		logger.strongStep("Verify pagination along with rsult per page dropdown is displayed");
		log.info("INFO: Verify pagination along with rsult per page dropdown is displayed");
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.activitiesPagination), 6);
		cnxAssert.assertTrue(isVisible(ActivitiesUIConstants.activitiesPagination),"Pagination is visible");		
		ui.waitForElementVisibleWd(By.xpath(ActivitiesUIConstants.resultPerPageDropdown), 6);
		cnxAssert.assertTrue(isVisible(ActivitiesUIConstants.resultPerPageDropdown),"Result per page dropdown is visible");
	}
	
	/**
	 * This method handles staleelement exception for spefied locator
	 * @param locator
	 * @return
	 */
	private boolean isVisible(String locator) {
		boolean isEleVisible;
		try {
			isEleVisible = ui.isElementDisplayedWd(By.xpath(locator));
		} catch (StaleElementReferenceException se) {
			log.info("Relocate the element");
			isEleVisible = ui.isElementDisplayedWd(By.xpath(locator));
		}
		return isEleVisible;
	}
	/**
	 * This method marks the activity to specified option from priority menu
	 * @param act
	 * @param priorityMenuLocator
	 * @param leftNavLocator
	 * @param markAsText
	 */
	private void markActivityAndVerifyPagination(BaseActivity act, String priorityMenuLocator,String leftNavLocator, String markAsText)
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		logger.strongStep("Select 'My Activities' from left nav");
		log.info("INFO: Select 'My Activities' from left nav");
		ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.myActivity), 6, "Select My activities");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Select 'More' link and Mark activity as "+markAsText+" from priority drop-down");
		log.info("INFO: Select 'More' link and Mark activity as "+markAsText+" from priority drop-down");
		ui.setPriority(act,priorityMenuLocator);
		
		logger.strongStep("Select "+markAsText+" from left nav and verify pagination along with result per page drop-down is displayed");
		log.info("INFO:Select "+markAsText+" from left nav and verify pagination along with result per page drop-down is displayed");
		selectLeftNavOptionAndVerifyPagination(By.xpath(leftNavLocator),markAsText);
	}
}

