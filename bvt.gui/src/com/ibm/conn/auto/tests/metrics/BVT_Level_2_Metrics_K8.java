package com.ibm.conn.auto.tests.metrics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Metrics_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Metrics_K8 extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Metrics_K8.class);
	private MetricsUI ui;
	private CommunitiesUI commUI;
	private TestConfigCustom cfg;
	private User testUser;
	private APICommunitiesHandler apiCommHandler;
	private APIFileHandler apiFilesHandler;
	private String serverURL;
	private Community comAPI1;
	private BaseCommunity community1;

	@BeforeClass(alwaysRun = true)
	public void setUpClass(ITestContext context) {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		testUser = cfg.getUserAllocator().getUser(this);
		ui = MetricsUI.getGui(cfg.getProductName(), driver);

		if (context.getCurrentXmlTest().getParameter("report") != null)
			ui.setReport(Boolean.valueOf(context.getCurrentXmlTest().getParameter("report")));
		else
			ui.setReport(true);

		apiCommHandler = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		apiFilesHandler = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		community1 = new BaseCommunity.Builder("Community Metrics - General View " + Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
				.description(
						"Test description for testcase " + Thread.currentThread().getStackTrace()[1].getMethodName())
				.build();

		log.info("INFO: Create communities using API");
		comAPI1 = community1.createAPI(apiCommHandler);
	}

	@AfterClass(alwaysRun = true)
	public void cleanUpNetwork() {

		log.info("INFO: Cleanup - delete communities");
		apiCommHandler.deleteCommunity(comAPI1);
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Community Metrics: Default Content View Page</li>
	 * <li><B>Info:</B> Verify the correct view displays when the Content link is
	 * selected</li>
	 * <li><B>Step:</B> Create a Public community using API</li>
	 * <li><B>Step:</B> Create a file in the community</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Click on the Metrics Tab</li>
	 * <li><B>Step:</B> Select 'Number of Files and All Years'</li>
	 * <li><B>Verify:</B> The correct view displays and Total number of number of
	 * new files is 1</li>
	 * <li><B>Step:</B> Select 'Number of Visits'</li>
	 * <li><B>Verify:</B> The correct view displays</li>
	 * <li><B>Step:</B> Click People on left navigation</li>
	 * <li><B>Step:</B> Select 'Number of members who left the community'</li>
	 * <li><B>Verify:</B> The correct view displays</li>
	 * <li><B>Step:</B> Click on the Content link</li>
	 * <li><B>Verify:</B> The correct view displays - Most active content</li>
	 * <li><B>Step:</B> Click on 'View all Metrics' Link"</li>
	 * <li><B>Verify:</B> The correct view displays</li>
	 * </ul>
	 */
	@Test(groups = { "regression" } , enabled=false )
	public void testCommunityMetricsViews() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiCommHandler, comAPI1);

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1).comFile(true).extension(".txt").build();

		log.info("INFO: Add File to community using API");
		community1.addFileAPI(comAPI1, fileA, apiCommHandler, apiFilesHandler);

		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		log.info("INFO: Open the community");
		community1.navViaUUID(commUI);

		log.info("INFO: Click on the Metrics Tab");
		logger.strongStep("INFO: Click on the Metrics Tab");
		Community_LeftNav_Menu.METRICS.select(commUI);

		logger.weakStep("Verify that the Participation page loaded");
		log.info("INFO: Verify that the Participation page is loaded");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.participationDropDown_Updates_K8),
				"ERROR: Participation page DropDown list does not load");
		ui.checkLineChartImage();

		// Close Guided tours
		log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());

		log.info("INFO: Select 'Number of new files' in dropdownlist");
		String numOfFiles = "Number of new files";
		ui.selectDropDownList(MetricsUI.dropdownlist_K8, numOfFiles);
		ui.checkLineChartImage();

		log.info("INFO: Select All years in time range dropdown list");
		String timeRangeofAllyears = "All years";
		ui.selectDropDownList(MetricsUI.dropdownlist_TimeRange_K8, timeRangeofAllyears);
		ui.checkLineChartImage();
		log.info("INFO: Expect of total of Number of new files is : 1");
		log.info("INFO: Actual total of Number of new files is :"
				+ driver.getFirstElement(MetricsUI.staticheadtable_K8).getText());
		Assert.assertEquals("1", driver.getFirstElement(MetricsUI.staticheadtable_K8).getText());

		log.info("INFO: Select 'Number of visits' in dropdownlist");
		String numOfVisits = "Number of visits";
		ui.selectDropDownList(MetricsUI.dropdownlist_K8, numOfVisits);
		ui.checkLineChartImage();

		log.info("INFO: Click on People on the left navigation");
		Metrics_LeftNav_Menu.PEOPLE_SC.select(ui);
		Assert.assertTrue(driver.isElementPresent(MetricsUI.peopleDropDown_Visitors_K8),
				"ERROR: People page dropdown does not load");
		ui.checkLineChartImage();

		log.info("INFO: Select 'Number of members who left the community' in dropdownlist");
		String numOfLeft = "Number of members who left the community";
		ui.selectDropDownList(MetricsUI.dropdownlist_K8, numOfLeft);
		ui.checkLineChartImage();

		log.info("INFO: Click on Content on the left navigation");
		Metrics_LeftNav_Menu.CONTENT_SC.select(ui);

		// Verify the dropdown list on the page loads
		Assert.assertTrue(driver.isElementPresent(MetricsUI.contentDropDown_Content_K8),
				"ERROR: People page dropdown does not load");
		ui.checkLineChartImage();

		log.info("INFO: Click on 'View all Metrics' Link");
		String ViewAllMetricsLink = "View all Metrics";
		Assert.assertTrue(driver.isTextPresent(ViewAllMetricsLink), "ERROR: View All metrics Link does not exist");
		driver.getFirstElement(MetricsUI.view_All_Metrics_Link_K8).click();
		Assert.assertTrue(driver.isTextPresent("This is a complete list of all the community metrics"),
				"ERROR: view all metrics page does not load");

		log.info("INFO: click the first 'most active content'(Bookmarks) to verify the link working");
		driver.getFirstElement(MetricsUI.most_Active_Content_K8).click();
		log.info("INFO: Verify the page is loaded successfully");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.contentDropDown_Content_K8),
				"ERROR: People page dropdown does not load");
		List<Element> selectedOption = driver.getSingleElement(MetricsUI.dropdownlist_K8).useAsDropdown()
				.getAllSelectedOptions();
		Assert.assertEquals("Most active content", selectedOption.get(0).getText());
		ui.checkLineChartImage();

		ui.logout();
		ui.endTest();
	}
}
