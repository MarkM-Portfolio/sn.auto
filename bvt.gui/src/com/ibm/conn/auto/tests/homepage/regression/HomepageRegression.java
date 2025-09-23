package com.ibm.conn.auto.tests.homepage.regression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

public class HomepageRegression extends SetUpMethods2 {
	
	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current Homepage regression suite have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */	

	private User testUser;
	private static Logger log = LoggerFactory.getLogger(HomepageRegression.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		//initialize the configuration
				cfg = TestConfigCustom.getInstance();
				ui = HomepageUI.getGui(cfg.getProductName(), driver);
				
				//Load Users
				log.info("INFO: loading user");
				testUser = cfg.getUserAllocator().getUser();	
	}

	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the navigation menu item Mentions and its tab exist and work.</B></li>
	 *<li><B>Verify: Validate the navigation menu item Mentions exists.</B></li>
	 *<li><B>Step: Select Mentions from the navigation menu.</B></li> 
	 *<li><B>Verify: Validate the Mentions tab exists.</B></li> 
	 *<li><B>Step: Select the Mentions tab.</B></li>
	 *</ul>
	 */
	@Test (groups = {"regression", "regressioncloud","cnx8ui-regression"} )
	public void homepageMentions() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("If sametime is enabled, allow time for it load");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		
		logger.strongStep("Select and Validate Mentions Tab options");
		log.info("INFO: Select and Validate Mentions Tab options");
		ui.gotoMentions();
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the navigation menu item My Notifications and its tabs exist and work.</B></li>
	 *<li><B>Verify: Validate the navigation menu item My Notifications exists.</B></li>
	 *<li><B>Step: Select My Notifications from the navigation menu.</B></li> 
	 *<li><B>Verify: Validate the For Me tab exists.</B></li> 
	 *<li><B>Step: Select the For Me tab.</B></li>
	 *<li><B>Verify: Validate the From Me tab exists.</B></li>
	 *<li><B>Step: Select the From Me tab.</B></li>
	 *</ul>
	 */
	@Test (groups = {"regression", "regressioncloud","cnx8ui-regression"} )
	public void homepageMyNotifications() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("If sametime is enabled, allow time for it to load");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		
		logger.strongStep("Select and Validate My Notification Tab options");
		log.info("INFO: Select and Validate My Notification Tab options");
		ui.gotoMyNotifications();
		
		logger.strongStep("Click and Validate For Me and From Me Tab options");
		log.info("INFO: Click and Validate For Me and From Me Tab options");
		ui.validateforandfromme();
		
		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the navigation menu item Saved and its tab exist and work.</B></li>
	 *<li><B>Verify: Validate the navigation menu item Saved exists.</B></li>
	 *<li><B>Step: Select Saved from the navigation menu.</B></li> 
	 *<li><B>Verify: Validate the Saved tab exists.</B></li> 
	 *<li><B>Step: Select the Saved tab.</B></li>
	 *</ul>
	 */
	@Test (groups = {"regression", "regressioncloud","cnx8ui-regression"} )
	public void homepageSavedTab() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: "  + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("If sametime is enabled, allow time for it load");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Select and Validate Saved Tab options");
		log.info("INFO: Select and Validate Saved Tab options");
		ui.gotoSaved();
		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test if the navigation menu item Updates and its tabs exist.</B></li>
	 *<li><B>Verify: Validate the navigation menu item Updates exists.</B></li>
	 *<li><B>Step: Select Updates from the navigation menu.</B></li> 
	 *<li><B>Verify: Validate the I'm Following tab exists.</B></li> 
	 *<li><B>Step: Select the I'm Following tab.</B></li> 
	 *<li><B>Verify: Validate the Status Updates tab exists.</B></li>
	 *<li><B>Step: Select the Status Updates tab.</B></li> 
	 *<li><B>Verify: Validate the Discover tab exists.</B></li>
	 *<li><B>Step: Select the Discover tab.</B></li> 
	 *</ul>
	 */
	@Test (groups = {"regression", "regressioncloud","cnx8ui-regression"} )
	public void homepageUpdates() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		logger.strongStep("Load homepage and login");
		log.info("Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("If sametime is enabled, allow time for it to login");
		log.info("INFO: Wait for sametime to load if enabled");
		ui.waitForSameTime();
		
		logger.strongStep("Select and Validate Discover Tab options");
		log.info("INFO: Select and Validate Discover Tab options");
		ui.gotoUpdatesTabsValidation();
				
		ui.endTest();

	}
}


