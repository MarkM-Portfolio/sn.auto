package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	30th September 2016
 */

public class FVT_AS_Widgets extends SetUpMethods2 {
	
	private boolean isOnPremise;
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * test_WidgetsInAS_EventsWidget_Menu_HelpLink() 
	 *<ul>
	 *<li><B>1: Log into Homepage Activity Stream</B></li>
	 *<li><B>2: Click the dropdown for the list of Actions</B></li>
	 *<li><B>3: Click "Help"</B></li>
	 *<li><B>Verify: Verify the help window for the events widget opens</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5E0BF31DFE1F176A852579BA0057C470">ACTIVITY STREAM - EVENTS WIDGET - 00011 - WIDGET MENU - HELP</a></li>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_WidgetsInAS_EventsWidget_Menu_HelpLink() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Retrieve the window handle for the main browser window
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// Click on the actions menu for the 'Events' widget and select the 'Help' option from the menu
		boolean successfullyClickedHelp = UIEvents.openEventsWidgetActionsMenuAndClickHelp(ui, driver);
		
		// Verify that the 'Help' option was successfully clicked in the UI
		HomepageValid.verifyBooleanValuesAreEqual(successfullyClickedHelp, true);
		
		// Switch focus to the help window which has now opened
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		if(isOnPremise) {
			// Verify that the user has been brought to the Help page
			HomepageValid.verifyStringValuesAreEqual(driver.getTitle(), Data.getData().helpIBMConnections);
		} else {
			// Verify that the user has been brought to the IBM Knowledge Center
			HomepageValid.verifyStringContainsSubstring(driver.getTitle(), Data.getData().IBMKnowledgeCenter);
		}
		// Close the Help screen and switch focus back to the main browser window again
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}