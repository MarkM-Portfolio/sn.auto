/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.cisco.testcases;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.webui.cloud.BSSUICloud;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;

/**
 * @author Jovie Fung - jovief@hk1.ibm.com
 * This testcase is aimed to test Cisco WebEx Integration change:
 * Show or hide Connections Meetings widget on homepage according to app registry settings.
 * 
 * REQUIREMENT: an admin user of a test org with Cisco WebEx enabled, saved with valid webEx site name.
 * 
 */

public class BVT_Level_2_Cisco_Homepage extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Cisco_Homepage.class);
	private BSSUICloud adminui;
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void setUp() {	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		adminui = BSSUICloud.getGui(cfg.getProductName(), driver);
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser = cfg.getUserAllocator().getGroupUser("cisco_adminusers", this);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void initialize() {
		//Login admin and set "Disable Connections Meeting" unchecked.
		LoginEvents.loginToHomepage(ui, testUser, false);
		adminui.enableWebExMeetings();
		ui.closeCurrentBrowserWindow();
	}
	
	/**
	* ciscoWebEx_ConnectionsMeetingsWidgetHidden() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Login a admin user who customer has disable Connections Meetings is checked on Cisco WebEx settings</B></li>
	*<li><B>Verify: Verify that Connections meetings widget is hidden on Homepage</B></li>
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ciscotc", "bvtcloud", "regressioncloud"})
	public void ciscoWebEx_ConnectionsMeetingsWidgetHidden() {
		ui.startTest();

		//Load component and login
		LoginEvents.loginToHomepage(ui, testUser, false);
		
		adminui.checkWebExDisableConnectionsMeetings(true);
		ui.gotoHome();
		
		log.info("INFO: Verify Connections meetings widget is hidden on Homepage");
		Assert.assertFalse(ui.isElementPresent(HomepageUIConstants.meetingsWidget), "verify Connections meetings widget is hidden on homepage");
		
		ui.closeCurrentBrowserWindow();
		ui.endTest();
	}
	
	/**
	* ciscoWebEx_ConnectionsMeetingsWidgetShown() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with uiHp.login and ends with uiHp.logout and the browser being closed</B></li>
	*<li><B>Step: Login a admin user who customer has disable Connections Meetings is unchecked on Cisco WebEx settings</B></li>
	*<li><B>Verify: Verify that Connections meetings widget is shown on Homepage</B></li>
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ciscotc", "bvtcloud", "regressioncloud"})
	public void ciscoWebEx_ConnectionsMeetingsWidgetShown() {
		ui.startTest();

		//Load component and login
		LoginEvents.loginToHomepage(ui, testUser, false);
		
		adminui.checkWebExDisableConnectionsMeetings(false);
		ui.gotoHome();
		
		log.info("INFO: Verify Connections meetings widget is shown on Homepage");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.meetingsWidget), "verify Connections meetings widget title is shown on homepage");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.meetingsWidgetIframe), "verify Connections meetings widget iframe is shown on homepage");
		
		ui.closeCurrentBrowserWindow();
		ui.endTest();
	}
	
}