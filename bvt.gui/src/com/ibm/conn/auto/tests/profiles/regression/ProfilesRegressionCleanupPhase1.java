package com.ibm.conn.auto.tests.profiles.regression;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfilesRegressionCleanupPhase1 extends SetUpMethods2 {
	
	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current Profiles regression suite have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */		
		
	private static Logger log = LoggerFactory.getLogger(ProfilesRegressionCleanupPhase1.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	private FilesUI fUI;
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		
	}

	
	/**
	*<ul>
	*<li><B>Profile_Background_AboutMe</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 12: My Profile Page - Background - About Me </li>
	*<li><B>Info:</B> The My Profile page's Background - About Me data</li>
	*<li><B>Step:</B> Open the My Profile page</li>
	*<li><B>Step:</B> Click Edit my Profile button </li>
	*<li><B>Step:</B> Select the "About Me" tab</li>
	*<li><B>Step:</B> Add 2048 characters to the Background and About Me fields, click Save button</li>
	*<li><B>Step:</B> Click the Background tab</li>
	*<li><B>Verify:</B> The data in the About Me and Background matches</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8D9378C7C72DC38785257E120066B8AA">SC - IC Profiles Regression 12: My Profile Page - Background - About Me</a></li>
	*</ul>
	*/
	@Test(groups={ "regression", "ptcsc","ptc" })
	public void backgroundAboutMe() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Edit profile, Add 2048 characters to the Background and About Me fields, click Save button
		log.info("INFO: Edit profile, Add 2048 characters to the Background and About Me fields, click Save button");
		ui.updateProfileBackground(Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47));
		
		//Click the Background tab
		log.info("INFO: Click the Background tab");
		ui.clickLinkWait(ProfilesUIConstants.BackgroundTab);
		
		//Verify the data in the About Me and Background matches
		log.info("INFO: Verify the data in the About Me matches");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.BackgroundInfoContent).getText().contains("About me:\n" + Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47)),
				"ERROR: Data in the About Me does not matche");
		
		log.info("INFO: Verify the data in the Background matches");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.BackgroundInfoContent).getText().contains("Background:\n" + Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47)),
				"ERROR: Data in the Background does not matche");
		
		//End test
		ui.endTest();
	}	

}
