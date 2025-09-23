package com.ibm.conn.auto.tests.touchpoint;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.TouchpointUI;

public class BVT_Level_2_Touchpoint_WelcomeScreen extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Touchpoint_WelcomeScreen.class);
	
	private TestConfigCustom cfg;	
	private TouchpointUI ui;
	private User testUser;
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} else {
			testUser = cfg.getUserAllocator().getUser();
		}
		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Welcome to Connections' screen for existing user</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen.</li>
	 * <li><B>Verify: </B>Verify the welcome message with logged user's first name as "Welcome to Connections, Amy!"</li>
	 * <li><B>Verify: </B>Verify static text "Step 1: Create your Profile" appears on screen</li> 
	 * <li><B>Verify: </B>Verify static text "Step 2: Follow People and Communities" appears on screen</li>
	 * <li><B>Verify: </B>Verify "Let's Go" button appears on bottom right of page</li>
	 * <li><B>Verify: </B>Verify that clicking on "Let's Go" button brings you to the next page:  Update your Profile</li>
	 * </ul>
	 */
	@Test(groups = { "level2" })
	public void welcomeScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		
		// Verification for different messages and static text
		log.info("INFO: Verify welcome message: " + "Welcome to Connections, " + testUser.getFirstName() + "! ");
		logger.strongStep("Verify welcome message: " + "Welcome to Connections, " + testUser.getFirstName() + "! ");
		Assert.assertEquals(ui.getWelcomeMessage(), "Welcome to Connections, " + testUser.getFirstName() + "! ", "ERROR: Welcome to Connections was not visible on the screen");

		log.info("INFO: Verify text " + TouchpointUIConstants.informationMsg);
		logger.strongStep("Verify text: " + TouchpointUIConstants.informationMsg);
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.welcomeScreenText).getAttribute("innerText"), TouchpointUIConstants.informationMsg, "ERROR: Information text was not visible on the screen");

		log.info("INFO: Verify static text: " + TouchpointUIConstants.step1Text+" appears on screen.");
		logger.strongStep("Verify static text: " + TouchpointUIConstants.step1Text+" appears on screen.");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.createYourProfile).getAttribute("innerText"), TouchpointUIConstants.step1Text, "ERROR: 'Step 1: Create your Profile' is not visible on the screen");

		log.info("INFO: Verify static text: " + TouchpointUIConstants.step2Text+" appears on screen.");
		logger.strongStep("Verify static text: " + TouchpointUIConstants.step2Text+" appears on screen.");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.followPeopleAndCommunities).getAttribute("innerText"), TouchpointUIConstants.step2Text,"ERROR: 'Step 2: Follow People and Communities' is not visible on the screen");

		log.info("INFO: Verify 'Let's Go' button appears on bottom right of page");
		logger.strongStep("Verify 'Let's Go' button appears on bottom right of page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.buttonLetsGo),"ERROR: 'Let's Go' button is not visible on the screen");
		
		if (cfg.getTestConfig().serverIsMT()) {
			log.info("INFO: Verify 'I Accpet Policy' link appears or not");
			logger.strongStep("Verify 'I Accpet Policy' link appears or not");
			ui.validateAndClickAcceptPolicy();
		}

		log.info("INFO: Verify clicking on 'Let's Go' button brings you to the next page:  Update your Profile");
		logger.strongStep("Verify clicking on 'Let's Go' button brings you to the next page:  Update your Profile");
		ui.clickLink(TouchpointUIConstants.buttonLetsGo);
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.updateProfilePageHeader),"ERROR: User was not navigated to 'Update your Profile' screen");
		
		//Return to welcome screen
		ui.returnToWelcomeScreenfromUpdateProfile();
		ui.endTest();
	}
	
}

