package com.ibm.conn.auto.tests.metrics_elasticSearch.regression;

import java.util.Vector;

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

public class Global_PeopleView extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Global_PeopleView.class);		
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
	 *<li><B>Test Scenario:</B> Global Metrics: Default People View UI</li>
	 *<li><B>Info:</B> Verify the default view UI</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the People link</li>
	 *<li><B>Verify:</B> The header 'People Metrics' appears on the page (on-premises only)</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The 'show table'link appears</li>
	 *<li><B>Verify:</B> These headers appear: Total, Average, Change & Top Followed People</li>
	 *<li><B>Verify:</B> The chart headers Organization Overall Total, Activities, Blogs, etc appear</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void defaultViewUI(){
		
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
		
		if(!isOnPremise){
			log.info("INFO: Click on the People view link");
			ui.clickLinkWait(MetricsUI.peopleViewLink_SC);
		}else{
            log.info("INFO: Click on the People view link");
            ui.clickLinkWait(MetricsUI.peopleViewLink);
            
            log.info("INFO: Verify the People Metrics header displays");
			Assert.assertTrue(driver.isTextPresent(metricsData.PeopleViewPageTitle),
					"ERROR: The " + metricsData.PeopleViewPageTitle + " header does not display");
            
		}
		
		log.info("INFO: Verify UI elements on the page");
		metricsUI.verifyUIOnPageWithShowTableLink();
		
		log.info("INFO: Verify the 'Top Followed People' header appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.topFollowedPeople),
				"ERROR: The 'Top Followed People' header does not appear");
					
		//Determine of SC or OP			
		if(!isOnPremise){
			log.info("INFO: Verify the chart headers displays: Organization Overall Total, Blogs, Files, Forums, Wikis");
			metricsUI.checkAppFilterHeaders(metricsData.PeopleDefaultViewChartHeaders_GlobSC);
		}else{
			log.info("INFO: Verify the chart headers displays: Organization Overall Total, Blogs, Bookmarks, Files, Forums");
			metricsUI.checkAppFilterHeaders(metricsData.PeopleDefaultViewChartHeaders_GlobOP);
		}
			            
	}

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: People 'chart' page UI</li>
	 *<li><B>Info:</B> Verify the UI after clicking the 'show table' link</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the People link</li>
	 *<li><B>Step:</B> Click on the 'show table' link</li>
	 *<li><B>Verify:</B> The header 'People Metrics' appears on the page (on-premises only)</li>
	 *<li><B>Verify:</B> The time line filter displays</li>
	 *<li><B>Verify:</B> The time range text appears</li>
	 *<li><B>Verify:</B> The 'show chart' link appears</li>
	 *<li><B>Verify:</B> These headers appear: Total, Average, Change & Top Followed People</li>
	 *<li><B>Verify:</B> The chart table appears</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void tablePageUI(){
				
		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
				
		if(!isOnPremise){
			log.info("INFO: Click on the People view link");
			ui.clickLinkWait(MetricsUI.peopleViewLink_SC);
		}else{
            log.info("INFO: Click on the People view link");
            ui.clickLinkWait(MetricsUI.peopleViewLink);            
		}
		
		log.info("INFO: Click on the 'show table' link");
        ui.clickLinkWait(MetricsUI.showTableLink);  
		
		if(isOnPremise){
			log.info("INFO: Verify the People Metrics header displays on the 'table' page");
			Assert.assertTrue(driver.isTextPresent(metricsData.PeopleViewPageTitle),
					"ERROR: " + metricsData.PeopleViewPageTitle + " header does not display on the 'table' page");
		}		         
                 
        log.info("INFO: Verify UI elements on the page");
        metricsUI.verifyUIOnPageWithShowChartLink();  
        									
		log.info("INFO: Verify the 'Top Followed People' header appears");
		Assert.assertTrue(driver.isElementPresent(MetricsUI.topFollowedPeople),
				"ERROR: The 'Top Followed People' header does not appear");
				           
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Metrics: People view drop-down menu options </li>
	 *<li><B>Info:</B> Verify the view 'Number of unique authenticated visitors' appears on the drop-down menu</li>
	 *<li><B>Step:</B> Log into Metrics as an admin user</li>
	 *<li><B>Step:</B> Click on the People link</li>
	 *<li><B>Step:</B> Click on the 'View' drop-down menu icon</li>
	 *<li><B>Verify:</B> This view appears: Number of unique authenticated visitors</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression", "regressioncloud"})
	public void dropdownMenuOptions(){
		
		Vector<String> peopleMenuViews = new Vector<String>(1);
		Vector<String> expectedGlobalViews = new Vector<String>(1);

		expectedGlobalViews.add(metricsData.verifyPeopleText.toUpperCase());

		log.info("INFO: Log into Metrics as admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentMetrics);
		ui.login(adminUser);
		
		if(isOnPremise){
			log.info("INFO: Click on the People view link");
	        ui.clickLinkWait(MetricsUI.peopleViewLink);
	        			
		}else{
			log.info("INFO: Click on the People view link");
			ui.clickLinkWait(MetricsUI.peopleViewLink_SC);
		}
        
		log.info("INFO: Click on the drop-down menu icon");
		driver.getSingleElement(MetricsUI.viewDropdownMenu).useAsDropdown();
		
		log.info("INFO: Verify the correct views appear on the drop-down menu");
		peopleMenuViews.addAll(metricsUI.getViewItems());

		Vector<String> expectList=new Vector<String>(1);		
		expectList.addAll(expectedGlobalViews);
		
		log.info("INFO: Verify the expected list of views matches the actual list");
		metricsUI.sortComparisonLists(expectList, peopleMenuViews);		
				
	}
	
}
