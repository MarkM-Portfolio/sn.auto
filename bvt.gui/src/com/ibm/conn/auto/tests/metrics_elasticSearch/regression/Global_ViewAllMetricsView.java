package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.List;

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

public class Global_ViewAllMetricsView extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Global_ViewAllMetricsView.class);		
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User adminUser;
	private boolean isOnPremise;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
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
	 *<li><B>Test Scenario:</B> Global Metrics: View All Metrics</li>
	 *<li><B>Info:</B> Verify the section headers & links within each section are correct</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the View All Metrics link</li>
	 *<li><B>Verify:</B> The correct section headers appear on the page: Bookmarks, Files, Forums, Wikis (SC only), etc</li>
	 *<li><B>Verify:</B> The correct links appear for each of the sections listed</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void viewAllMetricsPageUI(){		
		
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
		
		if(!isOnPremise){
			log.info("INFO: Click on the View All Metrics view link");
			ui.clickLinkWait(MetricsUI.viewAllMetricsViewLink_SC);
		}else{
            log.info("INFO: Click on the View All Metrics view link");
            ui.clickLinkWait(MetricsUI.viewAllMetricsViewLink); 
            
            log.info("INFO: Verify the View all Metrics page text displays");
    		Assert.assertTrue(driver.isTextPresent(metricsData.viewAllMetricsViewText_Glob),
    				"ERROR: The View all Metrics page text: " + metricsData.viewAllMetricsViewText_Glob + " does not appear.");
		}
        		
		log.info("INFO: Verify the correct list of links display for Bookmarks");
		this.checkLinks(MetricsUI.viewAllMetricsBookmarksLinks, metricsData.BookmarksLinks_Glob, Data.getData().appBookmarks);
		
		log.info("INFO: Verify the correct list of links display for Files");
		this.checkLinks(MetricsUI.viewAllMetricsFilesLinks, metricsData.FilesLinks_Glob, Data.getData().appFiles);
		
		log.info("INFO: Verify the correct list of links display for Forums");
		this.checkLinks(MetricsUI.viewAllMetricsForumsLinks, metricsData.ForumsLinks_Glob, Data.getData().appForums);
		
		log.info("INFO: Verify the correct list of links display for Activities");
		this.checkLinks(MetricsUI.viewAllMetricsActivitiesLinks, metricsData.ActivitiesLinks_Glob, Data.getData().appActivities);
		
		log.info("INFO: Verify the correct list of links display for Blogs");
		this.checkLinks(MetricsUI.viewAllMetricsBlogsLinks, metricsData.BlogsLinks_Glob, Data.getData().appBlog);		
		
		log.info("INFO: Verify the correct list of links display for Wikis");
		this.checkLinks(MetricsUI.viewAllMetricsWikisLinks, metricsData.WikisLinks_Glob, Data.getData().appWiki);
		
		log.info("INFO: Verify the correct list of links display for Homepage");
		this.checkLinks(MetricsUI.viewAllMetricsHomepageLinks, metricsData.HomepageLinks_Glob, Data.getData().appHomepage);
		
		log.info("INFO: Verify the correct list of links display for Communities");
		this.checkLinks(MetricsUI.viewAllMetricsCommunitiesLinks, metricsData.CommunitiesLinks_Glob, Data.getData().appCommunities);
		
		log.info("INFO: Verify the correct list of links display for Profiles");
		this.checkLinks(MetricsUI.viewAllMetricsProfilesLinks, metricsData.ProfilesLinks_Glob, Data.getData().appProfiles);		

		log.info("INFO: Verify the correct list of links display for Others");
		this.checkLinks(MetricsUI.viewAllMetricsOthersLinks, metricsData.OthersLinks_Glob, metricsData.OthersHeader);
			
		//Check for Ideation Blog on the cloud only - Ideation Blog currently does not appear on-premises
		if(!isOnPremise){
			log.info("INFO: Verify the correct list of links display for Ideation Blog");
			this.checkLinks(MetricsUI.viewAllMetricsIdeationBlogLinks, metricsData.IdeationBlogLinks_Glob, Data.getData().appIdeationBlog);
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
				"ERROR: Number of actual links (" + actualLinks.size() + ") does not equal the expected number of links (" + expectedLinks.length + ") in this section: " + sectionTitle);


		for(int i = 0; i < expectedLinks.length; i++) {				
			Assert.assertEquals(expectedLinks[i], actualLinks.get(i).getText(),
					"ERROR: The " + sectionTitle + " links do not match the expected results");
		}
	}
}
