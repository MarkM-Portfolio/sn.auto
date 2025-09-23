package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;

public class Global_ContentView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Global_ContentView.class);		
	private CommunitiesUI ui;
	private MetricsUI metricsUI;
	private TestConfigCustom cfg;
	private User adminUser;
	private boolean isOnPremise;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		metricsUI = MetricsUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		adminUser = cfg.getUserAllocator().getAdminUser();
		
		//check environment to see if on-premises or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Global Metrics - Default Content View UI </li>
	 *<li><B>Info:</B>Verify the default view UI</li>
	 *<li><B>Step:</B>Log into Metrics as an admin user</li>
	 *<li><B>Verify:</B>Participation view is selected by default</li>
	 *<li><B>Step:</B>Click on the Content link</li>
	 *<li><B>Verify:</B>Default view is 'Most active communities'</li>
	 *<li><B>Verify:</B>Time line displays</li>
	 *<li><B>Verify:</B>Date range text displays</li>
	 *<li><B>Verify:</B>If there is no content, the message indicating there is no data displays</li>
	 *<li><B>Verify:</B>If there is content, verify the horizontal bar with the title Communities appears</li>
	 *<li><B>Verify:</B>If there is content, 'Content Value is a rank...' text displays</li>
	 *</ul>
	 */
		
	@Test(groups = {"regression", "regressioncloud"})
	public void chartPageUIDefaultView(){

		log.info("INFO: Log into Metrics as: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		if(isOnPremise){
			log.info("INFO: Verify the Participation Metrics page displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
					"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		}else{
			log.info("INFO: Verify the Participation is selected - should be underlined");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.participationLinkSelected_SC),
					"ERROR: The Participation link is not selected (underlined)");
		};	

		if(isOnPremise){
			log.info("INFO: Click on the Content view link");
			ui.clickLinkWait(MetricsUI.contentViewLink);
		}else{
			log.info("INFO: Click on the Content view link");
			ui.clickLinkWait(MetricsUI.contentViewLink_SC);
		};	


		if(isOnPremise){
			log.info("INFO: Verify the Content Metrics page header displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ContentViewPageTitle),
					"ERROR: " + metricsData.ContentViewPageTitle + " header does not appear");
		}
		
		log.info("INFO: Verify the label 'View' and drop-down menu appear at the top of the page");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.viewLabelAndDropdownMenu),
				"ERROR: 'View' label & drop-down menu do not appear at the top of the page");
		
		log.info("INFO: Verify the default view is '" + metricsData.mostActiveCommunities + "'");
		Assert.assertTrue(driver.isTextPresent(metricsData.mostActiveCommunities),
				"ERROR: The default view is not: Most active communities");

		log.info("INFO: Verify the time line filter appears on the page");
		metricsUI.checkTimeLineOptions();

		log.info("INFO: Verify the date range text: '" + metricsData.dateRangeText + "' displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");

		if(!driver.isElementPresent(MetricsUI.contentViewShowTableLink)){
			log.info("INFO: If there is no 'show table' link, verify this message displays: '" + metricsData.noMetricsDataMsg);
			Assert.assertTrue(driver.isTextPresent(metricsData.noMetricsDataMsg),
					"ERROR: The message " + metricsData.noMetricsDataMsg + " does not appear.");

		}else {
			log.info("INFO: Verify the horizontal bar with the title 'Communities' displays");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.horizontalBarWithCommunitiesHeader),
					"ERROR: The horizontal bar with the title 'Communities' does not appear");

			log.info("INFO: Verify the Content Value text 'Content Value is a rank of usage based on creates, reads, visits, likes, updates, follows. Showing top 20 results.' displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.contentValueText),	
					"ERROR: Content Value message does not display");
		}
	}


	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Content view drop-down menu </li>
	 *<li><B>Info:</B> Verify the correct views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> The views listed on the drop-down menu are correct</li>
	 *</ul>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void contentViewDropdownOptions(){

		Vector<String> actualOptions = new Vector<String>(9);
		Vector<String> expectedOptions = new Vector<String>(9);

		//List of views that should appear on the Content drop-down menu
		expectedOptions.add(metricsData.mostActiveCommunities.toUpperCase()); 
		expectedOptions.add(metricsData.mostActiveApplications.toUpperCase());
		expectedOptions.add(metricsData.mostActiveFiles.toUpperCase());
		expectedOptions.add(metricsData.mostActiveFileLibraries.toUpperCase());
		expectedOptions.add(metricsData.mostActiveWikis.toUpperCase());
		expectedOptions.add(metricsData.mostFollowedContent.toUpperCase());
		expectedOptions.add(metricsData.mostActiveForums.toUpperCase());
		expectedOptions.add(metricsData.mostActiveBlogs.toUpperCase());
		expectedOptions.add(metricsData.mostActiveActivities.toUpperCase());

		this.loginToMetricsClickContentView();

		log.info("INFO: Get the drop-down menu options");
		actualOptions.addAll(metricsUI.getViewItems());

		Vector<String> expectList=new Vector<String>(9);
		expectList.addAll(expectedOptions);

		log.info("INFO: Sort the list of expected and actual options & compare the 2 lists to make sure they match");
		metricsUI.sortComparisonLists(expectList, actualOptions);

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Content Default View - Click 'show table' link</li>
	 *<li><B>Info:</B> NOTE: This test will only run if there is content for the default view 'Most active communities'</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Verify:</B> There is a 'show table' link</li>
	 *<li><B>Step:</B> If the 'show table' link does not exist, verify the no metrics data message displays</li>
	 *<li><B>Step:</B> If the 'show table' link does exist, verify the various UI elements on the page</li>
	 *<li><B>Verify:</B> 'View'label & drop-down menu display at the top of the page </li>
	 *<li><B>Verify:</B> Time line options display</li>
	 *<li><B>Verify:</B> Date range text displays</li>
	 *<li><B>Verify:</B> 'show chart' link displays</li>
	 *<li><B>Verify:</B> 'View' label at the top of left corner of the table</li>
	 *<li><B>Verify:</B> drop-down menu next to the 'View' label</li>
	 *<li><B>Verify:</B> the table displays</li>
	 *</ul>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void tablePageUIDefaultView(){

		this.loginToMetricsClickContentView();

		if(!driver.isElementPresent(MetricsUI.contentViewShowTableLink)){

			log.info("INFO: If there is no 'show table' link, verify this message displays: '" + metricsData.noMetricsDataMsg);
			Assert.assertTrue(driver.isTextPresent(metricsData.noMetricsDataMsg),
					"ERROR: The Metrics message " + metricsData.noMetricsDataMsg + " does not appear.");
		}else {
			this.selectShowTableLinkVerifyUI();

			log.info("INFO: Verify the table headers 'Title' , 'Content', & 'Author' display");
			this.verifyTableHeaders(MetricsUI.contentViewTableHeaders, metricsData.ContentView3ColTableHeaders);
		}
	}


	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Content View - 'Most Active Applications' view UI</li>
	 *<li><B>Info:</B> NOTE: This test will check the UI elements on the Most Active Applications 'chart' page</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Step:</B> Select 'Most Active Applications' from the drop-down menu</li>
	 *<li><B>Verify:</B> 'View'label & drop-down menu display at the top of the page </li>
	 *<li><B>Verify:</B> Time line options display</li>
	 *<li><B>Verify:</B> Date range text displays</li>
	 *<li><B>Verify:</B> 'show table' link displays</li>
	 *<li><B>Verify:</B> chart table displays</li>
	 *</ul>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void chartPageUIMostActiveAppsView(){

		this.loginToMetricsClickContentView();

		log.info("INFO: Click on 'Most active applications' on the drop-down menu");
		ui.clickLinkWait(MetricsUI.mostActiveApplications);

		if(!driver.isElementPresent(MetricsUI.contentViewShowTableLink)){

			log.info("INFO: If there is no 'show table' link, verify this message displays: '" + metricsData.noMetricsDataMsg);
			Assert.assertTrue(driver.isTextPresent(metricsData.noMetricsDataMsg),
					"ERROR: The Metrics message " + metricsData.noMetricsDataMsg + " does not appear.");
		}else {
			log.info("INFO: Verify the label 'View' and drop-down menu appear at the top of the page");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.viewLabelAndDropdownMenu),
					"ERROR: 'View' label & drop-down menu do not appear at the top of the page");

			log.info("INFO: Verify the time line filter appears on the page");
			metricsUI.checkTimeLineOptions();

			log.info("INFO: Verify the date range text '" + metricsData.dateRangeText + "' displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
					"ERROR: The text: " + metricsData.dateRangeText + " does not appear");	

			log.info("INFO: Verify the chart appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.barChartBody),
					"ERROR: The chart does not appear");

			log.info("INFO: Verify the chart header Overall appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.horizontalBarWithCommunitiesHeader),
					"ERROR: The header 'Overall' does not appear");

		}
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Content View - 'Most Active Applications' view UI</li>
	 *<li><B>Info:</B> NOTE: This test will check the UI elements on the Most Active Applications 'table' page</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Step:</B> Select 'Most Active Applications' from the drop-down menu</li>
	 *<li><B>Step:</B> Click on the 'show table' link.  If there is no content, test will verify the no metrics data msg displays</li>
	 *<li><B>Info:</B> If the 'show table' link displays, test do the verifications listed</li>
	 *<li><B>Verify:</B> 'View'label & drop-down menu display at the top of the page </li>
	 *<li><B>Verify:</B> Time line options display</li>
	 *<li><B>Verify:</B> Date range text displays</li>
	 *<li><B>Verify:</B> 'show chart' link displays</li>
	 *<li><B>Verify:</B> the table displays</li>
	 *</ul>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void tablePageUIMostActiveAppsView(){

		this.loginToMetricsClickContentView();

		log.info("INFO: Click on 'Most active applications' on the drop-down menu");
		ui.clickLinkWait(MetricsUI.mostActiveApplications);

		if(!driver.isElementPresent(MetricsUI.contentViewShowTableLink)){
			log.info("INFO: If there is no 'show table' link, verify this message displays: '" + metricsData.noMetricsDataMsg);
			Assert.assertTrue(driver.isTextPresent(metricsData.noMetricsDataMsg),
					"ERROR: The Metrics message " + metricsData.noMetricsDataMsg + " does not appear.");
		}else {
			this.selectShowTableLinkVerifyUI();

			log.info("INFO: Verify the table headers 'Title' & 'Content' display");
			this.verifyTableHeaders(MetricsUI.contentViewTableHeaders, metricsData.ContentView2ColTableHeaders);
		}
	}
	
	/*
	 * method will log into Metrics as an admin user and then click on the Content view link
	 */
	private void loginToMetricsClickContentView() {
		log.info("INFO: Log into Metrics as: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		if(isOnPremise){
			log.info("INFO: Click on the Content view link");
			ui.clickLinkWait(MetricsUI.contentViewLink);
		}else{
			log.info("INFO: Click on the Content view link");
			ui.clickLinkWait(MetricsUI.contentViewLink_SC);
		};	
	}
	
	/*
	 * this method will gather the actual table headers & compare them to the expected headers
	 * @param headerSelector - selector to be used to collect the header information
	 * @param expectedHeaders - list of expected table headers
	 */

	private void verifyTableHeaders(String headerSelector, String[]expectedHeaders){
		log.info("INFO: Collect the list of table headers");
		List<Element> tableHeaders = driver.getElements(headerSelector);

		log.info("INFO: Number of actual headers = " + tableHeaders.size());
		log.info("INFO: Number of expected headers = " + expectedHeaders.length);

		Assert.assertEquals(tableHeaders.size(), expectedHeaders.length, 
				"ERROR: The number of actual headers does not match the expected headers");

		for (Element listedHeaders : tableHeaders) {
			String header = listedHeaders.getText();
			log.info("INFO: The header: '" + header + "' is listed");
		}
		for(int i = 0; i < expectedHeaders.length; i++) {				
			Assert.assertEquals(expectedHeaders[i], tableHeaders.get(i).getText(),
					"ERROR: The headers do not match the expected results");
		}
	}
	
	/*
	 * this method will click on the 'show table' link and then verify the UI elements on the page
	 */
	
	private void selectShowTableLinkVerifyUI(){
		log.info("INFO: Click on the 'show table' link");
		ui.clickLinkWait(MetricsUI.contentViewShowTableLink);

		log.info("INFO: Verify the label 'View' and drop-down menu appear at the top of the page");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.viewLabelAndDropdownMenu),
				"ERROR: 'View' label & drop-down menu do not appear at the top of the page");

		log.info("INFO: Verify the time line filter appears on the page");
		metricsUI.checkTimeLineOptions();

		log.info("INFO: Verify the date range text '" + metricsData.dateRangeText + "' displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");	

		log.info("INFO: Verify the 'show chart' link displays");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.contentViewShowChartLink),
				"ERROR: The 'show chart' link does not display");

		log.info("INFO: Verify the chart appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.barChartBody),
				"ERROR: The chart does not appear");

		log.info("INFO: Verify the label 'View:' appear at the top of the table");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.tableViewLabel),
				"ERROR: The 'View:' label does not appear at the top of the table");

		log.info("INFO: Verify a drop-down menu also appears at the top of the table");  
		Assert.assertTrue(driver.isElementPresent(MetricsUI.tableDropDownMenu),
				"ERROR: The drop-down menu does not appear at the top of the table");
	}
}





		

