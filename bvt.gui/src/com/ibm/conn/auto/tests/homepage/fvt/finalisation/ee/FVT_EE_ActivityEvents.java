package com.ibm.conn.auto.tests.homepage.fvt.finalisation.ee;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016  			                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	12th October 2016
 */

public class FVT_EE_ActivityEvents extends SetUpMethods2 {
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private BaseActivity baseActivity;
	private boolean isOnPremise;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		
		activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a public standalone activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);
	}
	
	/**
	*	test_Activity_EE_IsDisplayed() 
	*<ul>
	*<li><B>1: User 1 create a public standalone activity</b></li>
	*<li><B>2: Login as User 1 and navigate to the Discover view</b></li>
	*<li><B>3: Open the EE for the activity made public news story - verification point</b></li>
	*<li><B>Verify: Verify that the EE opens correctly with all relevant data displayed</b></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_Activity_EE_IsDisplayed() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String makeActivityPublicEvent = ActivityNewsStories.getMakeActivityPublicNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		// Open the EE for the make activity public event
		UIEvents.openEE(ui, makeActivityPublicEvent);
		
		// Create the news story to be verified
		String madeAnActivityPublicEvent = ActivityNewsStories.getMadeAnActivityPublicNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the made an activity public event, the activity title and the activity description are displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{madeAnActivityPublicEvent, baseActivity.getName(), baseActivity.getGoal()}, null, true);
		
		ui.endTest();
	}
	
	/**
	*	test_Activity_EE_ActivityTitleLinksToActivitiesUI() 
	*<ul>
	*<li><B>1: User 1 create a public standalone activity</b></li>
	*<li><B>2: Login as User 1 and navigate to the Discover view</b></li>
	*<li><B>3: Open the EE for the activity made public news story</b></li>
	*<li><B>4: Click on the Activity title in the EE - verification point</b></li>
	*<li><B>Verify: Verify that the user is redirected to the Activities UI screen for the public activity</b></li>
	*<li>No TTT link for this test - this test has been created solely for finalisation automation purposes</li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_Activity_EE_ActivityTitleLinksToActivitiesUI() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Retrieve the window handle for the main browser window
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// Create the news story to be used to open the EE
		String makeActivityPublicEvent = ActivityNewsStories.getMakeActivityPublicNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		
		// Open the EE for the make activity public event
		UIEvents.openEE(ui, makeActivityPublicEvent);
		
		// Click on the activity title in the EE
		UIEvents.clickActivityTitleLinkInEEUsingUI(ui, publicActivity);
		
		// Switch focus to the help window which has now opened
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the user has been redirected correctly to Activities UI
		HomepageValid.verifyActivitiesUIIsDisplayed(ui, driver, publicActivity, true);
		
		// Close the Activities UI screen and switch focus back to the main browser window again
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}