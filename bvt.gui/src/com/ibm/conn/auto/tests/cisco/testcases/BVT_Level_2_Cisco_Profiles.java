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

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.testng.annotations.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.webui.cloud.BSSUICloud;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;

/**
 * @author Jovie Fung - jovief@hk1.ibm.com
 * This testcase is aimed to test Cisco Jabber and Spark Integration change:
 * Video Call button and soft phone link of phone number on Profiles page.
 * 
 * REQUIREMENT: an admin user and a user of a test org with Cisco Jabber (Video call and phone call) or Cisco Spark (Video Call).
 * 
 */

public class BVT_Level_2_Cisco_Profiles extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Cisco_Homepage.class);
	private BSSUICloud adminui;
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	private User adminUser;
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void setUp() {	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		adminui = BSSUICloud.getGui(cfg.getProductName(), driver);
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);		
		
		adminUser = cfg.getUserAllocator().getGroupUser("cisco_adminusers", this);
		testUser = cfg.getUserAllocator().getGroupUser("cisco_users", this);
	}
		
	/**
	* ciscoJabber_ProfilesPhoneCallandVideoCall() 
	*<ul>
	*<li><B>Info: Test if Video Call button and Phone Call link shows on Profiles page when Cisco Jabber Chat is enabled.</B></li>
	*<li><B>Step: Login a admin user and enable Jabber Chat</B></li>
	*<li><B>Step: Open Profiles page of another user</B></li>
	*<li><B>Verify: Verify Video Call button and Phone Call link is displayed, Phone Call link is correct.</B></li>
	*</ul>
	*/
	@Test(groups = {"ptcsc", "ciscotc", "bvtcloud", "regressioncloud"})
	public void ciscoJabber_ProfilesPhoneCallandVideoCall() throws Exception {
		ui.startTest();
		
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(adminUser);
		
		adminui.enableJabberChat();
		navigateToMyProfilePage();
		ui.openAnotherUserProfileByEmail(testUser);
		
		
		log.info("INFO: Verify Video Call button on other user's Profiles page when Jabber is enabled.");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.VideoCallButton), "verify Video Call button exists.");
		
		log.info("INFO: Verify phone call link on other user's Profiles page basic information when Jabber is shown.");
		Assert.assertTrue(ui.checkJabberPhoneCallLink("basicInformation"), "verify phone call link is correct on user basic information.");
		ui.gotoContactInformation();
		Assert.assertTrue(ui.checkJabberPhoneCallLink("contactInformation"), "verify phone call link is correct on user contact information.");
		
		log.info("INFO: Verify Sametime status bar is hidden.");
		Assert.assertFalse(ui.isElementVisible(ProfilesUIConstants.ProfileSametimeStatus), "verify Sametime status bar is hidden.");
		
		ui.endTest();
	}
	
	/**
	* ciscoSpark_ProfilesVideoCall() *Spark video call is revoked
	*<ul>
	*<li><B>Info: Test if Video Call button shows on Profiles page when Cisco Spark Chat is enabled.</B></li>
	*<li><B>Step: Login a admin user and enable Spark Chat</B></li>
	*<li><B>Step: Open Profiles page of another user</B></li>
	*<li><B>Verify: Verify Video Call button is displayed</B></li>
	*</ul>
	*/
/*	@Test(groups = {"ptcsc", "ciscotc", "bvtcloud", "regressioncloud"})
	public void ciscoSpark_ProfilesVideoCall() {
		ui.startTest();

		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(adminUser);
		
		adminui.enableSparkChat();
		navigateToMyProfilePage();
		ui.openAnotherUserProfileByEmail(testUser);
				
		log.info("INFO: Verify Video Call button on other user's Profiles page when Jabber is enabled.");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUI.VideoCallButton), "verify Video Call button exists.");
		
		log.info("INFO: Verify Sametime status bar is hidden.");
		Assert.assertFalse(ui.isElementVisible(ProfilesUI.ProfileSametimeStatus), "verify Sametime status bar is hidden.");
		
		ui.endTest();
	}*/
	
	private void navigateToMyProfilePage() {
		//click the user icon on the top-right corner
		String profilesSelector = "css=div.user";
		ui.clickLink(profilesSelector);
		//click My Profile
		profilesSelector = "css=div.user>ul>li.userprofile";
		ui.clickLink(profilesSelector);
	}
	
}