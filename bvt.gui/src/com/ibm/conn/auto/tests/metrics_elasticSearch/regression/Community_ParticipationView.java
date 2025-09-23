package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.Collections;
import java.util.Vector;

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

public class Community_ParticipationView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Community_ParticipationView.class);		
	private CommunitiesUI ui;
	private MetricsUI metricsUI;
	private TestConfigCustom cfg;
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private Community comAPI1, comAPI2, comAPI3, comAPI4, comAPI5, comAPI6;
	private BaseCommunity community1, community2, community3, community4, community5, community6;
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
		
		
		community1 = new BaseCommunity.Builder("Community Metrics - Participation Default View " + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)	
		                              .description("Verify when Metrics link is selected, Participation is the default view.")
		                              .build();
		
		community2 = new BaseCommunity.Builder("Community Metrics - Participation views on drop-down menu " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("Verify the correct views are listed on the drop-down menu.")
                                      .build();
		
		community3 = new BaseCommunity.Builder("Community Metrics - Participation views on drop-down menu, all apps added " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("Verify the correct views are listed on the drop-down menu when all apps are added.")
                                      .build();		
		
		community4 = new BaseCommunity.Builder("Community Metrics - default Participation view UI " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("Verify the default view UI is correct - no additional apps added.")
                                      .build();
		
		community5 = new BaseCommunity.Builder("Community Metrics - Participation view after clicking show table link " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("Verify the Participation UI is correct after clicking the show table link.")
                                      .build();
		
		community6 = new BaseCommunity.Builder("Community Metrics - default Participation view with all apps added " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)	
                                      .description("Verify the default view UI is correct when all apps are added.")
                                      .build();
		
		
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);
		comAPI4 = community4.createAPI(apiOwner);
		comAPI5 = community5.createAPI(apiOwner);
		comAPI6 = community6.createAPI(apiOwner);
		
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
		apiOwner.deleteCommunity(comAPI4);
		apiOwner.deleteCommunity(comAPI5);
		apiOwner.deleteCommunity(comAPI6);
	} 
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: Click Metrics - Default Participation View</li>
	 *<li><B>Info:</B> Verify the correct view displays when the Metrics tab is selected</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Verify:</B> The Participation view is selected by default</li>
	 *<li><B>Verify:</B> The default Participation view page is Number of new updates</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void defaultParticipationView(){
		
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
		
		log.info("INFO: Verify the default Participation view is Number of new updates");
		Assert.assertTrue(driver.isTextPresent(metricsData.numOfNewUpdates),
				"ERROR: The default Participation view is not " + metricsData.numOfNewUpdates);
        
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: List of Default Participation Views </li>
	 *<li><B>Info:</B> Verify the correct number of Participation views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Create a Public community using API - no additional apps added</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> The correct views are listed on the drop-down menu</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void dropdownOptionsByDefault(){
		
		Vector<String> participationMenuViews = new Vector<String>(11);
		Vector<String> expectedOnPremViews = new Vector<String>(11);
		Vector<String> expectedOnCloudViews = new Vector<String>(12);

		//On-premise: list of default views that should appear on the Participation drop-down menu
		expectedOnPremViews.add(metricsData.numOfUniquePeopleSharedFiles.toUpperCase()); 
		expectedOnPremViews.add(metricsData.verifyParticipationText.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfPeopleFollowingComm.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewForumTopics.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewUpdates.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewBookmarks.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniqueContributors.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniquePeopleDownloadFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfUniqueDownloadedFiles.toUpperCase());
		expectedOnPremViews.add(metricsData.numOfNewTopicReplies.toUpperCase());

		//Cloud: list of default views that should appear on the Participation drop-down menu 
		expectedOnCloudViews.add(metricsData.numOfUniquePeopleSharedFiles.toUpperCase()); 
		expectedOnCloudViews.add(metricsData.verifyParticipationText.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfPeopleFollowingComm.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewForumTopics.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewUpdates.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewBookmarks.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniqueContributors.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniquePeopleDownloadFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfUniqueDownloadedFiles.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewTopicReplies.toUpperCase());
		expectedOnCloudViews.add(metricsData.numOfNewWikiPages.toUpperCase());
		
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
        log.info("INFO: Verify the Participation Metrics page header displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
				
		log.info("INFO: Click on the drop-down menu icon");
		driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
		
		log.info("INFO: Verify the correct views appear");
		participationMenuViews.addAll(metricsUI.getViewItems());

		Vector<String> expectList=new Vector<String>(12);
		if (isOnPremise){
			expectList.addAll(expectedOnPremViews);
		}
		else {
			expectList.addAll(expectedOnCloudViews);

		}   				

		// Sort the two lists to make the comparison easier to do
		Collections.sort(expectList);
		Collections.sort(participationMenuViews);

		log.info("INFO: Number of Expected View Dropdown Menu Items = " + expectList.size());

		for (String expected:expectList)
		{
			log.info("INFO: Expected View Dropdown Menu Items: "+ expected);
		}

		log.info("INFO: Number of Actual Dropdown Menu Items = " + participationMenuViews.size());

		for (String actual:participationMenuViews)
		{
			log.info("INFO: Actual Dropdown Menu Items: "+ actual);
		}

		log.info("INFO: Verify the number of entries in the expected list is the same as the actual list");
		Assert.assertEquals(expectList.size(), participationMenuViews.size(),
				"ERROR: The number of entries in the expected & actual tabs list do not match");

		log.info("INFO: Compare the list of expected & actual menu itmes to make sure they contain the same entries");
		for(int index=0;index<expectList.size();index++)
		{
			log.info("INFO: Comparing the actual menu item: " + participationMenuViews.get(index)+
					" with the expected " + expectList.get(index));
			Assert.assertEquals(expectList.get(index).toLowerCase(),participationMenuViews.get(index).toLowerCase(),
					"ERROR: Mis-match in list of menu items ");
		}

		log.info("INFO: The menu lists match!");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: List of Participation Views - All Apps Added </li>
	 *<li><B>Info:</B> Verify the correct number of Participation views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Add all the apps via the API</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> The correct views are listed on the drop-down menu</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void dropdownOptionsAllAppsAdded(){
		
		Vector<String> actualOptions = new Vector<String>(17);
		Vector<String> expectedOptions = new Vector<String>(17);

		//List of views that should appear on the Participation drop-down menu when all apps are added
		expectedOptions.add(metricsData.numOfUniquePeopleSharedFiles.toUpperCase()); 
		expectedOptions.add(metricsData.verifyParticipationText.toUpperCase());
		expectedOptions.add(metricsData.numOfNewFiles.toUpperCase());
		expectedOptions.add(metricsData.numOfPeopleFollowingComm.toUpperCase());
		expectedOptions.add(metricsData.numOfNewForumTopics.toUpperCase());
		expectedOptions.add(metricsData.numOfNewUpdates.toUpperCase());
		expectedOptions.add(metricsData.numOfNewBookmarks.toUpperCase());
		expectedOptions.add(metricsData.numOfUniqueContributors.toUpperCase());
		expectedOptions.add(metricsData.numOfUniquePeopleDownloadFiles.toUpperCase());
		expectedOptions.add(metricsData.numOfUniqueDownloadedFiles.toUpperCase());
		expectedOptions.add(metricsData.numOfNewTopicReplies.toUpperCase());
		expectedOptions.add(metricsData.numOfNewWikiPages.toUpperCase());
		expectedOptions.add(metricsData.numOfNewEntryComments.toUpperCase());
		expectedOptions.add(metricsData.numOfNewActivities.toUpperCase());
		expectedOptions.add(metricsData.numOfNewGraduatedIdeas.toUpperCase());
		expectedOptions.add(metricsData.numOfNewEntries.toUpperCase());
		expectedOptions.add(metricsData.numOfNewIdeas.toUpperCase());
		
				
		log.info("INFO: Get UUID of community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community3.navViaUUID(ui);
		
		log.info("INFO: Add all apps using the API");
		metricsUI.addAppsViaAPI(community3, comAPI3, apiOwner);		
		
		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
        log.info("INFO: Verify the Participation Metrics page header displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
				
		log.info("INFO: Click on the drop-down menu icon");
		driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
		
		log.info("INFO: Verify the correct views appear");
		actualOptions.addAll(metricsUI.getViewItems());
		
		Vector<String> expectList=new Vector<String>(17);
		expectList.addAll(expectedOptions);

		// Sort the two lists to make the comparison easier to do
		Collections.sort(expectList);
		Collections.sort(actualOptions);

		log.info("INFO: Number of Expected View Dropdown Menu Items = " + expectList.size());

		for (String expected:expectList)
		{
			log.info("INFO: Expected View Dropdown Menu Items: "+ expected);
		}

		log.info("INFO: Number of Actual Dropdown Menu Items = " + actualOptions.size());

		for (String actual:actualOptions)
		{
			log.info("INFO: Actual Dropdown Menu Items: "+ actual);
		}

		log.info("INFO: Verify the number of entries in the expected list is the same as the actual list");
		Assert.assertEquals(expectList.size(), actualOptions.size(),
				"ERROR: The number of entries in the expected & actual tabs list do not match");

		log.info("INFO: Compare the list of expected & actual menu itmes to make sure they contain the same entries");
		for(int index=0;index<expectList.size();index++)
		{
			log.info("INFO: Comparing the actual menu item: " + actualOptions.get(index)+
					" with the expected " + expectList.get(index));
			Assert.assertEquals(expectList.get(index).toLowerCase(),actualOptions.get(index).toLowerCase(),
					"ERROR: Mis-match in list of menu items ");
		}

		log.info("INFO: The menu lists match!");
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: Participation View - Default Page Content</li>
	 *<li><B>Info:</B> Verify the correct UI appears on the People view default page - Numbers of unique authenticated visitors</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Verify:</B> The header 'Participation Metrics' appears on the page</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The 'show table' link appears</li>
	 *<li><B>Verify:</B> The chart headers appear: Total, Average & Change</li>
	 *<li><B>Verify:</B> The chart header Community Overall Total appears</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void defaultViewUI(){
		
		log.info("INFO: Get UUID of community");
		community4.getCommunityUUID_API(apiOwner, comAPI4);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community4.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
		log.info("INFO: Verify the Participation Metrics page displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
		log.info("INFO: Verify UI elements on the page");
		metricsUI.verifyUIOnPageWithShowTableLink();
		
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
	 *<li><B>Test Scenario:</B> Community Metrics: People View - Click 'show table' link</li>
	 *<li><B>Info:</B> Verify the correct UI appears when the 'show table' link is clicked</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Step:</B> Click on the People link</li>
	 *<li><B>Step:</B> Click on the 'show table' link</li>
	 *<li><B>Verify:</B> The header 'People Metrics' appears on the page</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The 'show chart' link appears</li>
	 *<li><B>Verify:</B> The chart headers appear: Total, Average & Change</li>
	 *<li><B>Verify:</B> The chart table appears</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void tablePageUI(){
		
		log.info("INFO: Get UUID of community");
		community5.getCommunityUUID_API(apiOwner, comAPI5);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community5.navViaUUID(ui);

		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
        log.info("INFO: Verify the Participation Metrics page displays");
        Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
        		"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
        
        log.info("INFO: Click on the 'show table' link");
        ui.clickLinkWait(MetricsUI.showTableLink);
        
        log.info("INFO: Verify common UI elements on the page");
        metricsUI.verifyUIOnPageWithShowChartLink();		
        
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Metrics: List of Participation Views - All Apps Added </li>
	 *<li><B>Info:</B> Verify the correct number of Participation views are listed on the drop-down menu</li>
	 *<li><B>Step:</B> Create a Public community using API</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Add all the apps via the API</li>
	 *<li><B>Step:</B> Click on the Metrics link</li>
	 *<li><B>Verify:</B> The header 'Participation Metrics' appears on the page</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The 'show table' link appears</li>
	 *<li><B>Verify:</B> The chart headers appear: Total, Average & Change</li>
	 *<li><B>Verify:</B> The chart header Community Overall Total appears</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void defaultViewUIAllAppsAdded(){
						
		log.info("INFO: Get UUID of community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);
		
		log.info("INFO: Log into Communities as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community6.navViaUUID(ui);
		
		log.info("INFO: Add all apps using the API");
		metricsUI.addAppsViaAPI(community6, comAPI6, apiOwner);
		
		log.info("INFO: Click on the Metrics link");
        Community_TabbedNav_Menu.METRICS.select(ui);
        
        //Note: this verification is needed; otherwise, the test will fail
        log.info("INFO: Verify the Participation Metrics page header displays");
		Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationViewPageTitle),
				"ERROR: " + metricsData.ParticipationViewPageTitle + " header does not appear");
		
		log.info("INFO: Verify UI elements on the page");
		metricsUI.verifyUIOnPageWithShowTableLink();
		
		log.info("INFO: Verify the chart headers displays: Community Overall Total, Activities, Bookmarks, Files, Forums, etc...");
		metricsUI.checkAppFilterHeaders(metricsData.LineChartAppFilterTextAllAppsAdded);
		
		
	}
			
}
