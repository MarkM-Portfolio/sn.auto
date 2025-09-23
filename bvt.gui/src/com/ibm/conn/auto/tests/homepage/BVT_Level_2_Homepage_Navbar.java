package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cloud.HomepageUICloud;

public class BVT_Level_2_Homepage_Navbar extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_Navbar.class);
	private User testUser;
	private TestConfigCustom cfg;
	private String serverURL;
	private APIActivitiesHandler apiActivitiesOwner;
	private HomepageUI ui;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
			
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiActivitiesOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
	}
	
	@Test (groups = {"smokecloud"} )
	public void navbarCloud() throws Exception {
	DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
	ui.startTest();
	
	User testUser = cfg.getUserAllocator().getUser();
	
	//Load component and login as guest user
	logger.strongStep("Load homepage and login as standard user");
	log.info("INFO: Load homepage and log in as standard user: " + testUser.getEmail() + " / " + testUser.getPassword());
	ui.loadComponent(Data.getData().ComponentHomepage);
	ui.login(testUser);
	
	//Validating navbar with gk vs non gk [GK's - NAVIGATION_GENERIC_CHERRY, NAVIGATION_GENERIC_FIG ]
	String gk_flag_RD = "NAVIGATION_GENERIC_CHERRY";
	String gk_flag_menu = "NAVIGATION_GENERIC_FIG";	
		
	//Gatekeeper check for RD gk flag
	log.info("INFO: Check to see if the Gatekeeper " + gk_flag_RD + " setting is enabled");
	String RD_gk_value = GatekeeperConfig.getFoundationValue(driver, gk_flag_RD);
	log.info("INFO:Gatekeeper flag " + gk_flag_RD +  " is " + RD_gk_value);
	    		    	
	//Gatekeeper check for more menu gk flag
	log.info("INFO: Check to see if the Gatekeeper " + gk_flag_menu + " setting is enabled");
	String menu_gk_value = GatekeeperConfig.getFoundationValue(driver, gk_flag_menu);
	log.info("INFO:Gatekeeper flag " + gk_flag_menu +  " is " + menu_gk_value);
	
	// get the CR4 Catalog Card View gate keeper flag	
	String gk_flag_card = Data.getData().gk_catalog_card_view;
	log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
	boolean isCardView =  ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	log.info("INFO:Gatekeeper flag " + gk_flag_card +  " is " + isCardView);

	
	if ((RD_gk_value == "true") && (menu_gk_value == "true")){
		

		//Navigating to community from navbar	
		log.info("INFO: Click on community from navbar");
		ui.clickLink(HomepageUICloud.communitynavbar);
		if (isCardView) {
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.topNavMyCommunitiesCardView),
				"Error: My Communities TAB is missing");
		}
		else {
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.ImanOwnerleftnav), 
				"Error: I'm an owner text is not visible on the page");
		}
		
		//Navigating to activity from navbar
		log.info("INFO: Click on activity from navbar");
		ui.clickLink(HomepageUICloud.activityinavbar);
		log.info("INFO: Navigating to activities component");
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.activity), 
					"Error: Create a activity button is not visible on the page");
		
			//Navigating to meetings from navbar (May 15th 2017 - Meetings test disabled after consensus agreed not to test reason being test doesn't have any functional purpose
			//log.info("INFO: Click on meetings from navbar");
			//ui.clickLink(HomepageUICloud.meetingsinavbar);
			//log.info("INFO: Navigating to meetings component");
			//	Assert.assertTrue(driver.isElementPresent(HomepageUICloud.meetings), 
			//				   "Error: Link 'Get the Mobile Meetings App' is not visible on the page");	
		
			//Navigating to files from navbar
			log.info("INFO: Click on files from navbar");
			ui.clickLink(HomepageUICloud.filesinavbar);
			log.info("INFO: Navigating to files component");
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.files), 
						"Error: My Files text is not visible on the page");			
	     	} 	    	    
	else{
			
		//Navigating to community from navbar
		log.info("INFO: Click on community from navbar");
		ui.clickLink(HomepageUICloud.communitynavbar);
		log.info("INFO: Navigating to community component");
		if (isCardView) {
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.topNavMyCommunitiesCardView),
				"Error: My Communities TAB is missing");
		}
		else {
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.ImanOwnerleftnav), 
				"Error: I'm an owner text is not visible on the page");
		}
	
		//Navigating to activity from more menu 
		log.info("INFO: Click on more menu in navbar");
		ui.clickLink(HomepageUICloud.morelink);
		log.info("INFO: Click on activity under more menu");
			ui.clickLink(HomepageUICloud.activityundermore);
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.activity), 
					"Error: Create a activity button is not visible on the page");
		
			//Navigating to meetings from navbar (May 15th 2017 - Meetings test disabled after consensus agreed not to test, reason being test doesn't have any functional purpose 
			//log.info("INFO: Click on more menu in navbar");
			//ui.clickLink(HomepageUICloud.morelink);
			//log.info("INFO: Click on meetings under more menu");
			//ui.clickLink(HomepageUICloud.meetingsundermoremenu);
			//	Assert.assertTrue(driver.isElementPresent(HomepageUICloud.meetings), 
			//					"Error: Link 'Get the Mobile Meetings App' is not visible on the page");
		
			//Navigating to files from more menu
			log.info("INFO: Click on more menu in navbar");
			ui.clickLink(HomepageUICloud.morelink);
			log.info("INFO: Click on files under more menu");
			ui.clickLink(HomepageUICloud.filesundermoremenu);
			Assert.assertTrue(driver.isElementPresent(HomepageUICloud.files), 
						"Error: My files text is not visible on the page");		
	}	
	   
	ui.endTest();
	}
}