package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Community_ContentView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Community_ContentView.class);		
	private CommunitiesUI ui;
	private MetricsUI metricsUI;
	private TestConfigCustom cfg;
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private Community comAPI1, comAPI2, comAPI3;
	private BaseCommunity community1, community2, community3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		metricsUI = MetricsUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		community1 = new BaseCommunity.Builder("Community Metrics - default content view " + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)	
		                              .description("verify the default Content view is correct.")
		                              .build();
		
		community2 = new BaseCommunity.Builder("Community Metrics - Content views on drop-down menu " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("verify the correct Content views appear on the drop-down menu.")
                                      .build();
		
		community3 = new BaseCommunity.Builder("Community Metrics - Unique Authenticated Visitors Page UI " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("verify the UI: headers, links, etc.")
                                      .build();
		
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);
		
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
	} 
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: Default Content View Page</li>
	 *<li><B>Info:</B> Verify the correct view displays when the Content link is selected</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Verify:</B> The correct view displays - Most active content</li>
	 *</ul>
	 */
	
	@Test(groups = {"cplevel2", "regression", "regressioncloud", "mtlevel2"})
	public void contentViewDefaultView(){
		
		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        ui.waitForPageLoaded(driver);
        ui.fluentWaitTextPresent(metricsData.ParticipationViewPageTitle);
        WebDriverWait wait2 = new WebDriverWait((WebDriver)driver.getBackingObject(), 30);
		wait2.until(ExpectedConditions.titleContains(metricsData.ParticipationViewPageTitle));

        //Note: this verification is needed; otherwise, the test will fail
		log.info("INFO: Verify the Participation Metrics page displays by default");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
        log.info("INFO: Click on the Content view link");
        ui.clickLinkWait(MetricsUI.contentViewLink);
        
        log.info("INFO: Verify the Content Metrics page header displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ContentViewPageTitle),
				"ERROR: " + metricsData.ContentViewPageTitle + " header does not appear");
		
		log.info("INFO: Verify the default view is 'Most active content'");
		Assert.assertTrue(driver.isTextPresent(metricsData.mostActiveContent),
				"ERROR: The default view is not: " + metricsData.mostActiveContent);
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: Content views </li>
	 *<li><B>Info:</B> Verify the correct views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> The correct views are listed on the drop-down menu</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void contentViewDropdownOptions(){
		
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
        log.info("INFO: Verify the Participation Metrics page displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
        			
        log.info("INFO: Click on the Content view link");
        ui.clickLinkWait(MetricsUI.contentViewLink);
        
        log.info("INFO: Verify the Content Metrics header displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ContentViewPageTitle),
				"ERROR: " + metricsData.ContentViewPageTitle + " header does not appear");
				
		log.info("INFO: Click on the View drop-down menu icon");
		driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
		
		log.info("INFO: Verify the view 'Most active content' displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.mostActiveContent),
				"ERROR: View is not " + metricsData.mostActiveContent);
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: Content View - Default Page Content</li>
	 *<li><B>Info:</B> Verify the UI is correct on the default Content view page</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the Content link</li>
	 *<li><B>Verify:</B> The header 'Content Metrics' appears on the page</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The no metrics data message displays</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void contentViewDefaultViewUI(){
		
		log.info("INFO: Get UUID of community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community3.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
		log.info("INFO: Verify the Participation Metrics page displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
        log.info("INFO: Click on the Content view link");
        ui.clickLinkWait(MetricsUI.contentViewLink);
        
        log.info("INFO: Verify the header 'Content Metrics' appears");
        Assert.assertTrue(driver.isTextPresent(metricsData.ContentViewPageTitle),
        		"ERROR: The header " + metricsData.ContentViewPageTitle + " does not exist.");
        
        log.info("INFO: Verify the time line filter appears on the page");
        metricsUI.checkTimeLineOptions();
        		
		log.info("INFO: Verify the date range text displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");
		
		log.info("INFO: Verify the no metrics data message appears on the page");
		Assert.assertTrue(driver.isTextPresent(metricsData.noMetricsDataMsg),
				"ERROR: The message " + metricsData.noMetricsDataMsg + " does not appear.");
        
	}	
	

}

