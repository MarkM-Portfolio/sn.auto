package com.ibm.conn.auto.tests.metrics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Metrics_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.MetricsUI;

public class BVT_Level_2_Metrics extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Metrics.class);
	private MetricsUI ui;
	private CommunitiesUI commUI;
	private HomepageUI hui;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser, adminUser;

	@BeforeMethod(alwaysRun = true)
	public void setUp(ITestContext context) throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = MetricsUI.getGui(cfg.getProductName(), driver);

		if (context.getCurrentXmlTest().getParameter("report") != null)
			ui.setReport(Boolean.valueOf(context.getCurrentXmlTest()
					.getParameter("report")));
		else
			ui.setReport(true);

		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		hui = HomepageUI.getGui(cfg.getProductName(), driver);
		testUser = cfg.getUserAllocator().getUser(this);
		testLookAheadUser = cfg.getUserAllocator().getUser(this);
		adminUser = cfg.getUserAllocator().getAdminUser();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Testing Metrics Navigation</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Step: </B>Click on each navigation link on the left side bar</li>
	 * <li><B>Verify: </B>Verify text on the pages that open when navigation
	 * links are clicked</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "smoke", "bvt", "smokeonprem" })
	public void testMetricsNavigation() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();
		logger.strongStep("Load Metrics and login: " +adminUser.getDisplayName());
		// Load the component
		// Check on this Monday

		logger.strongStep("Get Monday Data");
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// Verify the view is loaded
		logger.weakStep("Verify that the view is loaded");
		log.info("INFO: Verify that the view is loaded ");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);

		// expend navigation tree if collapsed
		logger.strongStep("Expand the navigation tree if it is collapsed");
		log.info("INFO: Expand navigation tree if collapsed");
		ui.expendMetricsLeftNav();

		// click on the People link in left nav
		logger.strongStep("Click on the 'People' link in the left navigation menu");
		log.info("INFO: lick on the 'People' link in left nav");
		Metrics_LeftNav_Menu.PEOPLE.select(ui);

		logger.weakStep("Verify that People view loads");
		ui.verifyViewContent(metricsData.PeopleView,
				MetricsUI.peopleGraphFrame, metricsData.verifyPeopleText);

		// click on the Participation link in left nav
		logger.strongStep("Click on the 'Participation' link in the left navigation menu");
		log.info("INFO: Click on the Participation link in left nav");
		Metrics_LeftNav_Menu.PARTICIPATION.select(ui);

		logger.weakStep("Verify that Participation view loads");
		ui.verifyViewContent(metricsData.ParticipationView,
				MetricsUI.participationGraphFrame,
				metricsData.verifyParticipationText);

		// click on the Content link in left nav
		logger.strongStep("Click on the 'Content' link in the left navigation menu");
		log.info("INFO: Click on the Content link in left nav");
		Metrics_LeftNav_Menu.CONTENT.select(ui);

		logger.weakStep("Verify that Content view loads");
		ui.verifyViewContent(metricsData.ContentView,
				MetricsUI.contentGraphFrame, metricsData.verifyContentText);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test Communities Metric view</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Navigate to Metrics</li>
	 * <li><B>Verify: </B>Verify Metrics is trying to load</li>
	 * <li><B>Verify: </B>Verify metrics not yet available message is shown</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level1", "smoke", "bvt", "smokeonprem" })
	public void testCommunityMetricsView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.commHandle(
						Data.getData().commonHandle + Helper.genDateBasedRand())
				.description("Test description for testcase " + Thread.currentThread().getStackTrace()[1].getMethodName())
				.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser))
				.build();

		// Load the component
		logger.strongStep("Load communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// create community
		logger.strongStep("Create community");
		log.info("INFO: Create community");
		community.create(commUI);

		// go to metrics page
		logger.strongStep("Go to the metrics page");
		log.info("INFO: Go to metrics page");
		ui.clickLink(MetricsUI.CommunityMetricsLink);

		logger.weakStep("Validate metrics page");
		log.info("INFO: Validate metrics page");
		Assert.assertTrue(driver.isTextPresent(metricsData.Loading),
				"No text present: \"" + metricsData.Loading + "\"");
		Assert.assertTrue(driver.isTextPresent(metricsData.NoMetricsMessage),
				"No text present: \"" + metricsData.NoMetricsMessage + "\"");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Testing metrics overview</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Verify: </B>Verify View Ranges</li>
	 * <li><B>Verify: </B>Verify graphs exist for People and Participation</li>
	 * <li><B>Step: </B>Select custom view</li>
	 * <li><B>Verify: </B>Verify graphs exist</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "bvt" })
	public void testOverView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load Metrics and login: " +adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// verify that the view is loaded
		logger.weakStep("Verify that the view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);
		ui.checkViewRanges();
		ui.checkGraphsExist(false);
		ui.checkCustomDateRange();
		ui.checkGraphsExist(true);

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test if people metrics</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Step: </B>Click People on left navigation</li>
	 * <li><B>Verify: </B>Verify text on page</li>
	 * <li><B>Verify: </B>Verify graphs displayed</li>
	 * <li><B>Verify: </B>Verify view ranges</li>
	 * <li><B>Verify: </B>Verify App filter</li>
	 * <li><B>Step: </B>Click on first App filter</li>
	 * <li><B>Verify: </B>Verify App filter is applied</li>
	 * <li><B>Verify: </B>Verify App graph is displayed</li>
	 * <li><B>Verify: </B>Verify other filters present</li>
	 * <li><B>Step: </B>Click on custom view filter</li>
	 * <li><B>Verify: </B>Verify Graph is displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt" })
	public void testPeopleView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load Metrics and login: " +adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// verify that the view is loaded
		logger.weakStep("Verify that the view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);

		// expend navigation tree if collapsed
		logger.strongStep("Expand the navigation tree if it is collapsed");
		log.info("INFO: Expend navigation tree if it is collapsed");
		ui.expendMetricsLeftNav();

		// click on the People link in left nav
		logger.strongStep("Click on the 'People' link in the left navigation menu");
		log.info("INFO: Click on the People link in left nav");
		Metrics_LeftNav_Menu.PEOPLE.select(ui);

		// verify that the view is loaded
		logger.weakStep("Verify that the People view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.PeopleView,
				MetricsUI.peopleGraphFrame, metricsData.verifyPeopleText);
		log.info("INFO: Verify that the view is loaded");
		ui.checkGraphDisplays("Default", "People");
		ui.checkViewRanges();
		ui.checkAppFilters("People");
		ui.checkOtherFilters("People");
		ui.checkCustomDateRange();
		ui.checkGraphDisplays("Custom", "People");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test participation view</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Step: </B>Click Participation on left navigation</li>
	 * <li><B>Verify: </B>Verify text on page</li>
	 * <li><B>Verify: </B>Verify graphs displayed</li>
	 * <li><B>Verify: </B>Verify view ranges</li>
	 * <li><B>Verify: </B>Verify App filter</li>
	 * <li><B>Step: </B>Click on first App filter</li>
	 * <li><B>Verify: </B>Verify App filter is applied</li>
	 * <li><B>Verify: </B>Verify graph is displayed</li>
	 * <li><B>Verify: </B>Verify other filters present</li>
	 * <li><B>Step: </B>Click on custom view filter</li>
	 * <li><B>Verify: </B>Verify Graph is displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "bvt" })
	public void testParticipationView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load Metrics and login: " +adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// verify that the view is loaded
		logger.weakStep("Verify that the view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);

		// expand navigation tree if collapsed
		logger.strongStep("Expand the navigation tree if it is collapsed");
		log.info("INFO: Expand navigation tree if collapsed");
		ui.expendMetricsLeftNav();

		// click on the Participation link in left nav
		logger.strongStep("Click on the 'Participation' link the left navigation menu");
		log.info("INFO: Click on the Participation link in left nav");
		Metrics_LeftNav_Menu.PARTICIPATION.select(ui);

		// verify that the view is loaded
		logger.weakStep("Verify that the Participation view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.ParticipationView,
				MetricsUI.participationGraphFrame,
				metricsData.verifyParticipationText);
		ui.checkGraphDisplays("Default", "Participation");
		ui.checkViewRanges();
		ui.checkAppFilters("Participation");
		ui.checkOtherFilters("Participation");
		ui.checkCustomDateRange();
		ui.checkGraphDisplays("Custom", "Participation");
		ui.loadComponent(Data.getData().ComponentMetrics);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test content view</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Step: </B>Click Content on left navigation</li>
	 * <li><B>Verify: </B>Verify text on page</li>
	 * <li><B>Verify: </B>Verify view ranges</li>
	 * <li><B>Step: </B>Click on first App filter</li>
	 * <li><B>Verify: </B>Verify App filter is applied</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt" })
	public void testContentView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load metrics and login: " +adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// verify that the view is loaded
		logger.weakStep("Verify that the view loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);

		// expand navigation tree if collapsed
		logger.strongStep("Expand the navigation tree if it is collapsed");
		log.info("INFO: Expand navigation tree if collapsed");
		ui.expendMetricsLeftNav();

		// click on the Content link in left nav
		logger.strongStep("Click on the 'Content' link in the left navigation menu");
		log.info("INFO: Click on the Content link in left nav");
		Metrics_LeftNav_Menu.CONTENT.select(ui);

		// verifyViewContent(metricsData.ContentView);
		logger.weakStep("Verify the Content view is present");
		log.info("INFO: Verify View Content");
		ui.verifyViewContent(metricsData.ContentView,
				MetricsUI.contentGraphFrame, metricsData.verifyContentText);
		ui.checkViewRanges();
		ui.checkAppFilters("Content");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test view of all metrics</li>
	 * <li><B>Step: </B>Open Metrics</li>
	 * <li><B>Step: </B>Click View all Metrics on left navigation</li>
	 * <li><B>Verify: </B>Verify text on the page</li>
	 * <li><B>Verify: </B>Verify all applications present</li>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt" })
	public void testViewAllMetrics() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load Metrics and login: " +adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		// verify that the view is loaded
		logger.strongStep("Verify that metrics loads");
		log.info("INFO: Verify that the view is loaded");
		ui.verifyViewContent(metricsData.MainView, MetricsUI.contentGraphFrame,
				metricsData.verifyContentText);

		// click on the view all metrics link in the left nav
		logger.strongStep("Click on the 'All Metrics' link the left navigation menu");
		log.info("INFO: Click on the view all metrics link in the left nav");
		ui.clickLink(MetricsUI.viewAllMetrics);

		// validate all components
		logger.weakStep("Validate the all components if the 'All Metrics' view");
		log.info("INFO: Check all companents");
		ui.fluentWaitTextPresent(metricsData.AllMetricsView);
		Assert.assertEquals(metricsData.AllMetricsView, driver
				.getSingleElement(MetricsUI.MetricsViewHeader).getText());
		ui.checkAllComponents();

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>This test is incomplete and notice it is commented out</B></li>
	 * 
	 */
	// @Test(groups = {"level2", "bvt" })
	public void testCommunityMetricsComponents() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.commHandle(
						Data.getData().commonHandle + Helper.genDateBasedRand())
				.description("Test description for testcase " + Thread.currentThread().getStackTrace()[1].getMethodName())
				.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser))
				.build();

		// Load the component
		logger.strongStep("Load Metrics and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(testUser);

		// create community
		logger.strongStep("Create community");
		log.info("INFO: Create community");
		community.create(commUI);

		// go to metrics page
		logger.strongStep("Go to the metrics page");
		log.info("INFO: Go to metrics page");
		ui.clickLink(MetricsUI.CommunityMetricsLink);

		// Validate
		logger.weakStep("Validate the 'Get Started' message");
		log.info("INFO: Validate get started message");
		Assert.assertTrue(driver.isTextPresent(metricsData.GetStartedMessage),
				"No text present: \"" + metricsData.GetStartedMessage + "\"");

		logger.strongStep("Click on 'Update Metrics'");
		log.info("INFO: Select update Metrics validate response");
		ui.clickLink(MetricsUI.UpdateMetrics);

		logger.strongStep("Select 'Update'");
		log.info("INFO: Select update");
		ui.clickLink(MetricsUI.Update);

		logger.weakStep("Validate metrics data");
		log.info("INFO: Validate metrics data");
		Assert.assertTrue(driver.isTextPresent(metricsData.UpdatingMessage),
				"ERROR: No text present: \"" + metricsData.UpdatingMessage
						+ "\"");

		// sleep for 5 minutes
		logger.strongStep("Sleep for 5 minutes");
		ui.sleep(300000);
		driver.navigate().refresh();

		logger.weakStep("After 5 minutes check that Metrics graph is present. If not graph is not present sleep another 5 minutes");
		if (driver.isTextPresent(metricsData.UpdatingMessage)) {
			ui.sleep(300000);
			driver.navigate().refresh();
		}

		if (driver.isTextPresent(metricsData.UpdatingMessage)) {
			Assert.fail("Metric graphs did not show up after 10 minutes of waitting");
		}

		// check if graph exists
		logger.weakStep("Validate that graph exists");
		log.info("INFO: Check if the graph exists");
		ui.checkGraphsExist(false);

		// click on the People link in left nav
		logger.strongStep("Click on the 'People' link in the left navigation menu");
		log.info("INFO: Click on the People link in left nav");
		Metrics_LeftNav_Menu.PEOPLE.select(ui);

		logger.weakStep("Validate People view");
		ui.verifyViewContent(metricsData.PeopleView,
				MetricsUI.peopleGraphFrame, metricsData.verifyPeopleText);

		// verifyViewContent(metricsData.PeopleView);
		log.info("INFO: Verify view content");
		ui.checkGraphDisplays("No", "People");

		// click on the Participation link in left nav
		logger.strongStep("Click on the 'Participation' link in the left navigation menu");
		log.info("INFO: Click on the Participation link in left nav");
		Metrics_LeftNav_Menu.PARTICIPATION.select(ui);

		logger.weakStep("Validate Participation view");
		ui.verifyViewContent(metricsData.ParticipationView,
				MetricsUI.participationGraphFrame,
				metricsData.verifyParticipationText);

		// check graph displays
		logger.weakStep("Check graph displays");
		log.info("INFO: Check graph displays");
		ui.checkGraphDisplays("No", "Participation");

		// click on the Content link in left nav
		logger.strongStep("Click on the 'Content' link in the left navigation menu");
		log.info("INFO: Click on the Content link in left nav");
		Metrics_LeftNav_Menu.CONTENT.select(ui);

		logger.weakStep("Validate Participation view");
		ui.verifyViewContent(metricsData.ContentView,
				MetricsUI.contentGraphFrame, metricsData.verifyContentText);

		// check graph displays
		logger.weakStep("Check graph displays");
		log.info("INFO: Check graph displays");
		ui.checkGraphDisplays("No", "Content");

		ui.endTest();
	}

	@Test(groups = { "bvtcloud" })
	public void testCommunityMetricsView_SC() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String noDataMsg = "Metrics data may not be available for the time period";

		String testName = ui.startTest();
	
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
				.description("Test description for testcase " + Thread.currentThread().getStackTrace()[1].getMethodName())
				.build();

		// Load the component
		logger.strongStep("Load Metrics and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Close Guided tours 
		log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());
		
		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_flag);
	    log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value );

		// get the CR4 Catalog Card View gate keeper flag	
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView =  ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		log.info("INFO:Gatekeeper flag " + gk_flag_card +  " is " + isCardView);

		//create community from new drop down vs single button click
		if (value) {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown");
			logger.strongStep("Create a restricted Community from the Dropdown Menu");
			log.info("INFO: Create a Community using the UI");
			if(isCardView) {
			 community.createFromDropDownCardView(commUI);
			} else {
			 community.createFromDropDown(commUI);
			}
		} else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' button");
			logger.strongStep("Create a Community");
			log.info("INFO: Create a Community using the UI");
			community.create(commUI);
		}
	    
		log.info("INFO: Click on the Metrics link in left nav");
		logger.strongStep("Click on the Metrics link in left nav");
		Community_LeftNav_Menu.METRICS.select(commUI);

		logger.weakStep("Verify that the Participation page loads");
		log.info("INFO: Verify that the Participation page is loaded");

		// Verify the dropdown list on the page loads
		Assert.assertTrue(
				driver.isElementPresent(MetricsUI.participationDropDown_Updates_SC),
				"ERROR: Participation page DropDown list does not load");

		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
					||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");

		
		//click other item in dropdown list and page can be loaded
		
		String numOfFiles = "Number of new files";
		driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().selectOptionByVisibleText(numOfFiles);
		
		//verify "number of new files" is selected in dropdown list
		List<Element> selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().getAllSelectedOptions();
		log.info("selected is " + selectedOption.get(0).getText());
		Assert.assertEquals(numOfFiles, selectedOption.get(0).getText());
		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
					||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");


		String numOfVisits = "Number of visits";
		driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().selectOptionByVisibleText(numOfVisits);

		//verify "Number of visits" is selected in dropdown list
		selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().getAllSelectedOptions();
		Assert.assertEquals(numOfVisits, selectedOption.get(0).getText());
	
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
		
	
		// verify that the view is loaded
		logger.weakStep("Verify that the People page loads");
		log.info("INFO: Verify that the People page is loaded");

		// select "People" view on left navigation
		Metrics_LeftNav_Menu.PEOPLE_SC.select(ui);

		// Verify the dropdown list on the page loads
		Assert.assertTrue(driver.isElementPresent(MetricsUI.peopleDropDown_Visitors_SC),
				"ERROR: People page dropdown does not load");
		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
		
		
		String numOfLeft = "Number of members who left the community";
		driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().selectOptionByVisibleText(numOfLeft);

		//verify "Number of members who left the community" is selected in dropdown list
		selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().getAllSelectedOptions();
		Assert.assertEquals(numOfLeft, selectedOption.get(0).getText());
		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
			"ERROR: Metrics chart does not load");


		// verify that the view is loaded
		logger.weakStep("Verify that the Content page loads");
		log.info("INFO: Verify that the Content page is loaded");

		// select "Content" view on left navigation
		Metrics_LeftNav_Menu.CONTENT_SC.select(ui);

		// Verify the dropdown list on the page loads
		Assert.assertTrue(driver.isElementPresent(MetricsUI.contentDropDown_Content_SC),
				"ERROR: People page dropdown does not load");
		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
		
		//Verify "view all metrics" link displays and click it the page loads successfully
		String ViewAllMetricsLink = "View all Metrics";
		Assert.assertTrue(driver.isTextPresent(ViewAllMetricsLink), "ERROR: View All metrics Link does not exist");
		ui.clickLink("link=" + ViewAllMetricsLink);
		
		Assert.assertTrue(driver.isTextPresent("This is a complete list of all the community metrics"), 
				"ERROR: view all metrics page does not load");
		
		//click the first "most active content"(Activities) to verify the link working
		driver.getFirstElement("link=Most active content").click();
		// Verify the page is loaded successfully
		Assert.assertTrue(driver.isElementPresent(MetricsUI.contentDropDown_Content_SC),
				"ERROR: People page dropdown does not load");

		//verify "Most active content" is selected in dropdown list
		selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().getAllSelectedOptions();
		Assert.assertEquals("Most active content", selectedOption.get(0).getText());

		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
		
		
		ui.clickLink("link=" + ViewAllMetricsLink);
		driver.getFirstElement("link=Number of new forum topics").click();
		
		// Verify the page is loaded successfully
		Assert.assertTrue(driver.isElementPresent(MetricsUI.participationDropDown_Updates_SC),
				"ERROR: 'Number of new forum topics' page does not load");

		//verify "Number of new forum topics" is selected in dropdown list
		selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_SC).useAsDropdown().getAllSelectedOptions();
		Assert.assertEquals("Number of new forum topics", selectedOption.get(0).getText());

		
		Assert.assertTrue(
				driver.isTextPresent(noDataMsg)
				||driver.isElementPresent(MetricsUI.chartDiv),
				"ERROR: Metrics chart does not load");
		
		ui.logout();
		ui.endTest();
	}
  }


