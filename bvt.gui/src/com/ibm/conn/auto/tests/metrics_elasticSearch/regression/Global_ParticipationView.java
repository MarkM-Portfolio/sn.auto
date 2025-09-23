package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.Vector;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;

public class Global_ParticipationView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Global_ParticipationView.class);		
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
	 *<li><B>Test Scenario:</B> Global Metrics: Participation view is default (on-premises only)</li>
	 *<li><B>Info:</B> Verify that when Metrics is selected from the mega-menu the default view is Participation</li>
	 *<li><B>Step:</B> Log into Communities as an admin user</li>
	 *<li><B>Step:</B> Click on the Metrics link located on the mega-menu</li>
	 *<li><B>Verify:</B> Participation is the default view</li>
	 *</ul>
	 *NOTE: on-premises only, no 'Metrics' link on the mega-menu in SC
	 */
	
	@Test(groups = {"cplevel2", "regression"})
	public void defaultParticipationView(){
				
		log.info("INFO: Log into Communities as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);
		
		log.info("INFO: Click on Metrics link located on the mega-menu");
		ui.clickLinkWait(BaseUIConstants.MegaMenuMetricsLink);

		log.info("INFO: Verify the default view is the Participation view");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: The Participation view is not the default view");
        
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Participation view is default (cloud only)</li>
	 *<li><B>Info:</B> Verify the Participation view is the default when logging into Metrics</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Verify:</B> The Participation view is the default view</li>
	 *</ul>
	 *NOTE: cloud only
	 */
	
	@Test(groups = {"regressioncloud"})
	public void defaultParticipationView_SC(){
				
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
		
		log.info("INFO: Verify the Participation link is selected - should be underlined");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.participationLinkSelected_SC),
				"ERROR: The Participation link is not selected (underlined)");
        
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Global Metrics - Default Participation View UI </li>
	 *<li><B>Info:</B>Verify the default view UI</li>
	 *<li><B>Step:</B>Log into Metrics as an admin user</li>
	 *<li><B>Verify:</B>Participation view is selected by default</li>
	 *<li><B>Verify:</B>Default drop-down menu option is 'Number of new updates'</li>
	 *<li><B>Verify:</B>Time line displays</li>
	 *<li><B>Verify:</B>Date range text displays</li>
	 *<li><B>Verify:</B>'show table' link displays</li>
	 *<li><B>Verify:</B> headers: Total, Average, Change appear</li>
	 *<li><B>Verify:</B> Organization Overall Total chart displays with correct headers</li>
	 *</ul>
	 */
		
	@Test(groups = {"regression", "regressioncloud"})
	public void defaultViewUI(){

		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);

		if(isOnPremise){
			log.info("INFO: Verify the Participation Metrics page title displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
					"ERROR: The " + metricsData.ParticipationViewPageTitle + " header does not appear");
		}else{
			log.info("INFO: Verify the Participation link is selected - should be underlined");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.participationLinkSelected_SC),
					"ERROR: The Participation link is not selected (underlined)");
		}	

		log.info("INFO: Verify the label 'View' and drop-down menu appear at the top of the page");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.viewLabelAndDropdownMenu),
				"ERROR: 'View' label & drop-down menu do not appear at the top of the page");
		
		log.info("INFO: Verify the default menu option is '" + metricsData.numOfNewUpdates + "'");
		Assert.assertTrue(driver.isTextPresent(metricsData.numOfNewUpdates),
				"ERROR: The default menu option is not: Number of New Updates");

		log.info("INFO: Verify the time line filter appears on the page");
		metricsUI.checkTimeLineOptions();

		log.info("INFO: Verify the date range text: '" + metricsData.dateRangeText + "' displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.dateRangeText),
				"ERROR: The text: " + metricsData.dateRangeText + " does not appear");
		
		log.info("INFO: Verify the 'show table' link displays");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.showTableLink),
				"ERROR: The 'show table' link does not appear on the page");
		
		log.info("INFO: Verify the line chart headers: Total, Average, Change");			
		metricsUI.checkLineChartHeaders(MetricsUI.showTableStatTableHeaders);

		//Determine of SC or OP			
		if(!isOnPremise){
			log.info("INFO: Verify the chart headers displays: Organization Overall Total, Files, Forums, Wikis, etc... ");
			metricsUI.checkAppFilterHeaders(metricsData.ParticipationDefaultViewChartHeaders_GlobSC);
		}else{
			log.info("INFO: Verify the chart headers displays: Community Overall Total, Bookmarks, Files, Forums, etc...");
			metricsUI.checkAppFilterHeaders(metricsData.ParticipationDefaultViewChartHeaders_GlobOP);
		}		
	}

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Participation 'chart' page UI</li>
	 *<li><B>Info:</B> Verify the UI on the page that appears after clicking the 'show table' link</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the 'show table' link</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The date range text appears</li>
	 *<li><B>Verify:</B> The 'show chart' link appears</li>
	 *<li><B>Verify:</B> The chart headers appear: Total, Average & Change</li>
	 *<li><B>Verify:</B> The chart table appears</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void tablePageUI(){
		
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
		
		if(isOnPremise){
			log.info("INFO: Verify the Participation Metrics page title displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
					"ERROR: The " + metricsData.ParticipationViewPageTitle + " header does not appear");
		}else{
			log.info("INFO: Verify the Participation link is selected - should be underlined");
			Assert.assertTrue(driver.isElementPresent(MetricsUI.participationLinkSelected_SC),
					"ERROR: The Participation link is not selected (underlined)");
		};	
		
        log.info("INFO: Click on the 'show table' link");
        ui.clickLinkWait(MetricsUI.showTableLink);
        
        log.info("INFO: Verify the UI elements on the page");
        metricsUI.verifyUIOnPageWithShowChartLink();
                
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: Participation view drop-down menu options </li>
	 *<li><B>Info:</B> Verify the correct Participation views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> The correct views are listed on the drop-down menu</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void dropdownMenuOptions(){
		
		Vector<String> participationMenuViews = new Vector<String>(23);
		Vector<String> expectedOnPremViews = new Vector<String>(23);
		Vector<String> expectedOnCloudViews = new Vector<String>(19);

		//On-premise: Participation drop-down menu options
		expectedOnPremViews.add(metricsData.numOfNewUpdates.toUpperCase()); 
		expectedOnPremViews.add(metricsData.verifyParticipationText.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewEntryComments.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniquePeopleWhoSharedFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewActivities.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewGraduatedIdeas.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewWikiPages.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewEntries.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewBookmarks.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniqueContributors.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniquePeopleDownloadFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniqueDownloadedFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewTopicReplies.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewIdeas.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewRejectedItems.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewForums.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewStatusUpdates.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniqueFollowedPeople.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewBlogs.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniquePeopleWhoUpdatedProfile.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewWikis.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewForumTopics.toUpperCase());

		//Cloud: Participation drop-down menu options
		expectedOnCloudViews.add(metricsData.numOfNewUpdates.toUpperCase()); 
		expectedOnCloudViews.add(metricsData.verifyParticipationText.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewEntryComments.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniquePeopleWhoSharedFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewActivities.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewGraduatedIdeas.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewWikiPages.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewEntries.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniqueContributors.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniquePeopleDownloadFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniqueDownloadedFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewTopicReplies.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewIdeas.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewForums.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewStatusUpdates.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewBlogs.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewWikis.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewForumTopics.toUpperCase());		
				
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
						
		log.info("INFO: Click on the drop-down menu icon");
		driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
		
		log.info("INFO: Verify the correct options appear on the drop-down menu");
		participationMenuViews.addAll(metricsUI.getViewItems());

		Vector<String> expectList=new Vector<String>(23);
		if (isOnPremise){
			expectList.addAll(expectedOnPremViews);
		}
		else {
			expectList.addAll(expectedOnCloudViews);
		}   	
		
		log.info("INFO: Verify the expected list of views matches the actual list");
		metricsUI.sortComparisonLists(expectList, participationMenuViews);

	}

}
