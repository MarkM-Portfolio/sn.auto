package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
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

public class Community_ViewAllMetricsView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Community_ViewAllMetricsView.class);		
	private CommunitiesUI ui;
	private MetricsUI metricsUI;
	private TestConfigCustom cfg;
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private Community comAPI1, comAPI2;
	private BaseCommunity community1, community2;
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
		 
		community1 = new BaseCommunity.Builder("Community Metrics - View All Metrics: Default Apps " + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)	
		                              .description("No additional apps added.  Verify the correct sections & links appear.")
		                              .build();
		
		community2 = new BaseCommunity.Builder("Community Metrics - View All Metrics: All Apps Added " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("All apps added.  Verify the correct sections & links appear.")
                                      .build();
		
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);

	}

	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {

		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
	} 

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: View All Metrics - default apps</li>
	 *<li><B>Info:</B> Verify the section headers & links within each section are correct</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the View All Metrics link</li>
	 *<li><B>Verify:</B> The correct section headers appear on the page: Bookmarks, Files, Forums, Wikis (SC only), Others</li>
	 *<li><B>Verify:</B> The correct links appear for each of the sections listed</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void viewAllMetricsPageDefaultApps(){
		
		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: test fails without this verification
		log.info("INFO: Verify the Participation Metrics page displays by default");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
        log.info("INFO: Click on the View All Metrics link");
        ui.clickLinkWait(MetricsUI.viewAllMetricsViewLink);
        
        log.info("INFO: Verify the View all Metrics page displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.viewAllMetricsViewText),
				"ERROR: The View all Metrics page text: " + metricsData.viewAllMetricsViewText + " does not appear.");
		
		log.info("INFO: Verify the default section headers are listed on the page.");
		this.checkDefaultListedSections(metricsData.ViewAllMetricsDefaultSections, metricsData.ViewAllMetricsDefaultSections_SC);
		
		log.info("INFO: Verify the correct list of links display for Bookmarks");
		this.checkLinks(MetricsUI.viewAllMetricsBookmarksLinks, metricsData.BookmarksLinks,Data.getData().appBookmarks);
		
		log.info("INFO: Verify the correct list of links display for Files");
		this.checkLinks(MetricsUI.viewAllMetricsFilesLinks, metricsData.FilesLinks, Data.getData().appFiles);
		
		log.info("INFO: Verify the correct list of links display for Forums");
		this.checkLinks(MetricsUI.viewAllMetricsForumsLinks, metricsData.ForumsLinks, Data.getData().appForums);

		//Determine of SC or OP			
		if(!isOnPremise){
			log.info("INFO: Verify the correct list of links display for Wikis");
			this.checkLinks(MetricsUI.viewAllMetricsWikisLinks, metricsData.WikisLinks, Data.getData().appWiki);
		}else{
			log.info("INFO: Verify the correct list of links display for Others");
			this.checkLinks(MetricsUI.viewAllMetricsOthersLinks, metricsData.OthersLinks, metricsData.OthersHeader);
		}
	}	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: View All Metrics Page - all apps added</li>
	 *<li><B>Info:</B> Verify the sections & links within each app section are correct</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Add all apps to the community using API</li>
	 *<li><B>Step:</B> Refresh the browser page so all the added apps appear</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the View All Metrics link</li>
	 *<li><B>Verify:</B> The correct sections appear on the page: Activities, Blogs, Bookmarks, Files, Forum, Ideation Blog, Wikis, & Others</li>
	 *<li><B>Verify:</B> The correct links appear for each of the sections listed</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void viewAllMetricsPageAllAppsAdded(){
		
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);
		
		log.info("INFO: Add all apps using the API");
		metricsUI.addAppsViaAPI(community2, comAPI2, apiOwner);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: test fails without this verification
		log.info("INFO: Verify the Participation Metrics page displays by default");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
        log.info("INFO: Click on the View All Metrics view link");
        ui.clickLinkWait(MetricsUI.viewAllMetricsViewLink);
        
        log.info("INFO: Verify the View all Metrics page displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.viewAllMetricsViewText),
				"ERROR: The View all Metrics page text: " + metricsData.viewAllMetricsViewText + " does not appear.");
		
		log.info("INFO: Verify the all the sections are listed on the page.");
		this.checkListedSectionsAllAppsAdded();
		
		log.info("INFO: Verify the correct list of links display for Bookmarks");
		this.checkLinks(MetricsUI.viewAllMetricsBookmarksLinks, metricsData.BookmarksLinks, Data.getData().appBookmarks);
		
		log.info("INFO: Verify the correct list of links display for Files");
		this.checkLinks(MetricsUI.viewAllMetricsFilesLinks, metricsData.FilesLinks, Data.getData().appFiles);
		
		log.info("INFO: Verify the correct list of links display for Forums");
		this.checkLinks(MetricsUI.viewAllMetricsForumsLinks, metricsData.ForumsLinks, Data.getData().appForums);
		
		log.info("INFO: Verify the correct list of links display for Activities");
		this.checkLinks(MetricsUI.viewAllMetricsActivitiesLinks, metricsData.ActivitiesLinks, Data.getData().appActivities);
		
		log.info("INFO: Verify the correct list of links display for Blogs");
		this.checkLinks(MetricsUI.viewAllMetricsBlogsLinks, metricsData.BlogsLinks, Data.getData().appBlog);
		
		log.info("INFO: Verify the correct list of links display for Ideation Blog");
		this.checkLinks(MetricsUI.viewAllMetricsIdeationBlogLinks, metricsData.IdeationBlogLinks, Data.getData().appIdeationBlog);

		log.info("INFO: Verify the correct list of links display for Wikis");
		this.checkLinks(MetricsUI.viewAllMetricsWikisLinks, metricsData.WikisLinks, Data.getData().appWiki);

		log.info("INFO: Verify the correct list of links display for Others");
		this.checkLinks(MetricsUI.viewAllMetricsOthersLinks, metricsData.OthersLinks, metricsData.OthersHeader);
	
		
	}	

	/**
	 * Use this method when no additional widgets/apps have been added to the community
	 * This method checks the default sections (Bookmarks, Files, Forums, Wikis (SC only), Others) appear on the page
	 * 	 * 
	 * @param expectedSectionOnPrem - list of expected sections for on-premises
	 * @param expectedSectionsSC - list of expected sections for the cloud
	 */

	private void checkDefaultListedSections(String[]expectedSectionsOnPrem, String[]expectedSectionsSC) {

		log.info("INFO: Collect the list of sections that appear on the page");
		List<Element> actualSections = driver.getElements(MetricsUI.viewAllMetricsPageHeaders);

		log.info("INFO: Number of sections on the page = " + actualSections.size());

		if(isOnPremise){
			log.info("INFO: Number of expected sections = " + metricsData.ViewAllMetricsDefaultSections.length);

			Assert.assertEquals(actualSections.size(), metricsData.ViewAllMetricsDefaultSections.length,
					"ERROR: Number of actual sections does not equal the number of expected sections");
		}
		else{
			log.info("INFO: Number of expected sections = " + metricsData.ViewAllMetricsDefaultSections_SC.length);

			Assert.assertEquals(actualSections.size(), metricsData.ViewAllMetricsDefaultSections_SC.length,
					"ERROR: Number of actual sections does not equal the number of expected sections");			
		}

		for (Element listedHeaders : actualSections) {
			String header = listedHeaders.getText();
			log.info("INFO: The header: '" + header + "' is listed");
		}

		//Determine of SC or OP			
		if(isOnPremise){
			for(int i = 0; i < expectedSectionsOnPrem.length; i++) {				
				Assert.assertEquals(expectedSectionsOnPrem[i], actualSections.get(i).getText(),
						"ERROR: The sections do not match the expected result");
			}
		}else{
			for(int i = 0; i < expectedSectionsSC.length; i++) {				
				Assert.assertEquals(expectedSectionsSC[i], actualSections.get(i).getText(),
						"ERROR: The sections do not match the expected result");
			}		

		}
	}
	
	/**
	 * Use this method when all widgets/apps have been added to the community
	 * This method checks that all the expected sections appear on the page	 * 
	 * Should see sections:  Activities, Blogs, Bookmarks, Files, Forums, Ideation Blog, Wikis & Others on the page
	 * 
	 */
	
	private void checkListedSectionsAllAppsAdded() {
		
		log.info("INFO: Collect the list of sections that appear on the page");
		List<Element> actualSections = driver.getElements(MetricsUI.viewAllMetricsPageHeaders);

		log.info("INFO: Number of sections on the page = " + actualSections.size());
		log.info("INFO: Number of expected sections = " + metricsData.ViewAllMetricsSectionsAllAppsAdded.length);

		Assert.assertEquals(actualSections.size(), metricsData.ViewAllMetricsSectionsAllAppsAdded.length,
				"ERROR: Number of actual sections does not equal the number of expected sections");

		for (Element listedHeaders : actualSections) {
			String header = listedHeaders.getText();
			log.info("INFO: The header: '" + header + "' is listed");
		}
		for(int i = 0; i < metricsData.ViewAllMetricsSectionsAllAppsAdded.length; i++) {				
			Assert.assertEquals(metricsData.ViewAllMetricsSectionsAllAppsAdded[i], actualSections.get(i).getText(),
					"ERROR: The sections do not match the expected result");
		}

	}
	
	/**
	 * This method checks the correct links that appear for the specified section
	 * 
	 * @param sectionLinkSelector - selector to get the list of links
	 * @param expectedLinks - list of expected links to appear in the specified section
	 * @param sectionTitle - name of the section to get the list of links from ie: Bookmarks, Activities, Files, etc...
	 */
	
	private void checkLinks(String sectionLinkSelector,String[]expectedLinks,String sectionTitle ) {

		log.info("INFO: Collect the list of links that appear for " + sectionTitle);
		List<Element> actualLinks = driver.getElements(sectionLinkSelector);

		log.info("INFO: Number of actual links = " + actualLinks.size());
		log.info("INFO: Number of expected links = " + expectedLinks.length);

		Assert.assertEquals(actualLinks.size(), expectedLinks.length,
				"ERROR: Number of links does not equal the number of sections");

		for (Element listedLinks : actualLinks) {
			String link = listedLinks.getText();
			log.info("INFO: The link: '" + link + "' is listed");
		}

		for(int i = 0; i < expectedLinks.length; i++) {				
			Assert.assertEquals(expectedLinks[i], actualLinks.get(i).getText(),
					"ERROR: The " + sectionTitle + " links do not match the expected results");
		}
	}

}
