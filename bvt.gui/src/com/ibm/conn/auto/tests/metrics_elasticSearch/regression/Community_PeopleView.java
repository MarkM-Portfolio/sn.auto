package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;


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
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class Community_PeopleView extends SetUpMethods2 {
		private static Logger log = LoggerFactory.getLogger(Community_PeopleView.class);		
		private CommunitiesUI ui;
		private MetricsUI metricsUI;
		private TestConfigCustom cfg;
		private User testUser;
		private APICommunitiesHandler apiOwner;
		private Community comAPI1, comAPI2, comAPI3, comAPI4;
		private BaseCommunity community1, community2, community3, community4;
		private boolean isOnPremise;
		
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
			
			//check environment to see if on-prem or on the cloud
			if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
				isOnPremise = true;
			} else {
				isOnPremise = false;
			}
						
			community1 = new BaseCommunity.Builder("Community Metrics - People views drop-down menu " + Helper.genDateBasedRandVal())
                                          .access(Access.PUBLIC)	
                                          .description("verify the correct views are listed on the drop-down menu.")
                                          .build();
			
			community2 = new BaseCommunity.Builder("Community Metrics - Click 'show table' link " + Helper.genDateBasedRandVal())
                                          .access(Access.PUBLIC)	
                                          .description("verify the UI on the page after clicking the 'show table' link")
                                          .build();
			
			community3 = new BaseCommunity.Builder("Community Metrics - Default People page content " + Helper.genDateBasedRandVal())
                                          .access(Access.PUBLIC)	
                                          .description("verify UI on the default People page")
                                          .build();
			
			community4 = new BaseCommunity.Builder("Community Metrics - People page UI with all apps added " + Helper.genDateBasedRand())
			                              .access(Access.PUBLIC)
			                              .description("verify the UI with all apps added")
			                              .build();
			
			
			
			log.info("INFO: create communities via the API");
			comAPI1 = community1.createAPI(apiOwner);
			comAPI2 = community2.createAPI(apiOwner);
			comAPI3 = community3.createAPI(apiOwner);
			comAPI4 = community4.createAPI(apiOwner);
			
		}
		
		@AfterClass(alwaysRun=true)
		public void cleanUpNetwork() {
			
			log.info("INFO: Cleanup - delete communities");
			apiOwner.deleteCommunity(comAPI1);
			apiOwner.deleteCommunity(comAPI2);
			apiOwner.deleteCommunity(comAPI3);
			apiOwner.deleteCommunity(comAPI4);
		} 
				
		/**
		 *<ul>
		 *<li><B>Test Scenario:</B> Community Metrics: List of People views </li>
		 *<li><B>Info:</B> Verify the (3) People views are listed on the drop-down menu</li>
		 *<li><B>Step:</B> Create a Public community using API</li>
		 *<li><B>Step:</B> Open the community</li>
		 *<li><B>Step:</B> Click on the Metrics link</li>
		 *<li><B>Step:</B> Click on the People link</li>
		 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
		 *<li><B>Verify:</B> The default view is: Number of unique authenticated visitors</li>
		 *<li><B>Verify:</B> The correct views appear on the drop-down menu</li>
		 *</ul>
		 */
		
		@Test(groups = {"regression", "regressioncloud"})
		public void dropdownMenuOptions(){
			
			log.info("INFO: Get UUID of community");
			community1.getCommunityUUID_API(apiOwner, comAPI1);
			
			log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			log.info("INFO: Navigate to the community using UUID");
			community1.navViaUUID(ui);

			log.info("INFO: Click on the Metrics link");
            Community_TabbedNav_Menu.METRICS.select(ui);
            
            //Note: this verification is needed; otherwise, the test will fail
            log.info("INFO: Verify the Participation Metrics page displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
					"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
            
            log.info("INFO: Verify the People view link appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.peopleViewLink),
					"ERROR: " + metricsData.PeopleView + " link does not appear");
            			
            log.info("INFO: Click on the People view link");
            ui.clickLinkWait(MetricsUI.peopleViewLink);
            			
			log.info("INFO: Verify the default view is Number of unique authenticated visitors");
			Assert.assertTrue(driver.isTextPresent(metricsData.verifyPeopleText),
					"ERROR: The default view is not " + metricsData.verifyPeopleText);
			
			log.info("INFO: Click on the View drop-down menu icon");
			driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
			
			log.info("INFO: Verify the view 'Number of members who left the community' appears on the drop-down menu");
			Assert.assertTrue(driver.isTextPresent(metricsData.numberOfMembersWhoLeftCommText),
					"ERROR: The view " + metricsData.numberOfMembersWhoLeftCommText + " does not appear");
			
			log.info("INFO: Verify the view 'Number of new members' appears on the drop-down menu");
			Assert.assertTrue(driver.isTextPresent(metricsData.numOfNewMembers),
					"ERROR: The view " + metricsData.numOfNewMembers + " does not appear");
			
		}
		
		
		/**
		 *<ul>
		 *<li><B>Test Scenario:</B> Community Metrics: People View - Click 'show table' link</li>
		 *<li><B>Info:</B> Verify the correct tables/charts/links appear when the 'show table' link is clicked</li>
		 *<li><B>Step:</B> Create a Public community using API</li>
		 *<li><B>Step:</B> Open the community</li>
		 *<li><B>Step:</B> Click on the Metrics link</li>
		 *<li><B>Step:</B> Click on the People link</li>
		 *<li><B>Step:</B> Click on the 'show table' link</li>
		 *<li><B>Verify:</B> The header 'People Metrics' appears on the page</li>
		 *<li><B>Verify:</B> The time line filter displays</li>
		 *<li><B>Verify:</B> The time range text appears</li>
		 *<li><B>Verify:</B> The 'show chart' link appears</li>
		 *<li><B>Verify:</B> The chart headers appear: Total, Average, Change & Top Contributors</li>
		 *<li><B>Verify:</B> The chart table appears</li>
		 *</ul>
		 */
		
		@Test(groups = {"regression", "regressioncloud"})
		public void tablePageUI(){
			
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
			
            log.info("INFO: Click on the People view link");
            ui.clickLinkWait(MetricsUI.peopleViewLink);
                                   
            log.info("INFO: Click on the 'show table' link");
            ui.clickLinkWait(MetricsUI.showTableLink);   
            
            log.info("INFO: Verify UI elements on the page");
            metricsUI.verifyUIOnPageWithShowChartLink();  
            									
			log.info("INFO: Verify the Top Contributor header appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.topContributorHeader),
					"ERROR: " + metricsData.OthersLinks + " header does not appear");
			
			
			           
		}
		
		/**
		 *<ul>
		 *<li><B>Test Scenario:</B> Community Metrics: People View - Default Page UI</li>
		 *<li><B>Info:</B> Verify the correct tables/charts/links appear on the People view default page - Numbers of unique authenticated visitors</li>
		 *<li><B>Step:</B> Create a Public community using API - no additional apps added</li>
		 *<li><B>Step:</B> Open the community</li>
		 *<li><B>Step:</B> Click on the Metrics link</li>
		 *<li><B>Step:</B> Click on the People link</li>
		 *<li><B>Verify:</B> The header 'People Metrics' appears on the page</li>
		 *<li><B>Verify:</B> The time line filter displays</li>
		 *<li><B>Verify:</B> The time range text appears</li>
		 *<li><B>Verify:</B> The 'show table'link appears</li>
		 *<li><B>Verify:</B> The chart headers appear: Total, Average, Change & Top Contributors</li>
		 *<li><B>Verify:</B> The chart header Community Overall Total appears</li>
		 *</ul>
		 */
		
		@Test(groups = {"regression", "regressioncloud"})
		public void defaultViewUI(){
			
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

			log.info("INFO: Click on the People view link");
			ui.clickLinkWait(MetricsUI.peopleViewLink);

			log.info("INFO: Verify UI elements on the page");
			metricsUI.verifyUIOnPageWithShowTableLink();
			
			log.info("INFO: Verify the Top Contributor header appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.topContributorHeader),
					"ERROR: Top Contributor header does not appear");
						
			//Determine of SC or OP			
			if(!isOnPremise){
				log.info("INFO: Verify the chart headers displays: Community Overall Total, Bookmarks, Files, Forums, Wikis");
				metricsUI.checkAppFilterHeaders(metricsData.LineChartDefaultAppFilterText_SC);
			}else{
				log.info("INFO: Verify the chart headers displays: Community Overall Total, Bookmarks, Files, Forums");
				metricsUI.checkAppFilterHeaders(metricsData.LineChartDefaultAppFilterText);
			}
				            
		}
		
		/**
		 *<ul>
		 *<li><B>Test Scenario:</B> Community Metrics: People View - All Apps Added UI</li>
		 *<li><B>Info:</B> Verify the correct tables/charts/links appear on the People view default page when all apps have been added</li>
		 *<li><B>Step:</B> Create a Public community using API</li>
		 *<li><B>Step:</B> Add all the apps using the API</li>
		 *<li><B>Step:</B> Open the community</li>
		 *<li><B>Step:</B> Click on the Metrics link</li>
		 *<li><B>Step:</B> Click on the People link</li>
		 *<li><B>Verify:</B> The header 'People Metrics' appears on the page</li>
		 *<li><B>Verify:</B> The time line filter displays</li>
		 *<li><B>Verify:</B> The time range text appears</li>
		 *<li><B>Verify:</B> The 'show table'link appears</li>
		 *<li><B>Verify:</B> The chart headers appear: Total, Average, Change & Top Contributors</li>
		 *<li><B>Verify:</B> The chart header Community Overall Total appears</li>
		 *</ul>
		 */
		
		
		@Test(groups = {"regression", "regressioncloud"})
		public void defaultViewAllAppsAddedUI(){
			
			log.info("INFO: Get UUID of community");
			community4.getCommunityUUID_API(apiOwner, comAPI4);
			
			log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);

			log.info("INFO: Navigate to the community using UUID");
			community4.navViaUUID(ui);
			
			log.info("INFO: Add all apps using the API");
			metricsUI.addAppsViaAPI(community4, comAPI4, apiOwner);
			
			log.info("INFO: Click on the Metrics link");
			Community_TabbedNav_Menu.METRICS.select(ui);

			//Note: this verification is needed; otherwise, the test will fail
			log.info("INFO: Verify the Participation Metrics page displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
					"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");

			log.info("INFO: Click on the People view link");
			ui.clickLinkWait(MetricsUI.peopleViewLink);
			
			log.info("INFO: Verify UI elements on the page");			
			metricsUI.verifyUIOnPageWithShowTableLink();	
			
			log.info("INFO: Verify the Top Contributor header appears");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.topContributorHeader),
					"ERROR: Top Contributor header does not appear");

			log.info("INFO: Verify the chart headers displays: Community Overall Total, Activities, Bookmarks, Files, Forums, etc...");
			metricsUI.checkAppFilterHeaders(metricsData.LineChartAppFilterTextAllAppsAdded);

		}
		
	
}