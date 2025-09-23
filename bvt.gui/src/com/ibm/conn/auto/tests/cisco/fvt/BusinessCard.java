package com.ibm.conn.auto.tests.cisco.fvt;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                              		     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * 	@author:	Yueqin - yqfeng@hk1.ibm.com
 *	Date:		24 January, 2017
 */

public class BusinessCard extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BusinessCard.class);
	private ProfilesUI Pui;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance(); 
		Pui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser("cisco_users", this);
	}
		
	/**
	*<ul>
	*<li><B>Profile_BusinessCard_CiscoIntegration_Basics</B></li>
	*<li><B>Info:</B> To Verify: the My Profile page's Business Card Cisco Integration Links</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click - select the Business Card button, to the right of the user's name.</li>
	*<li><B>Verify:</B> The card contains Profiles, Chat, More Actions links</li>
	*<li><B>Step:</B> Close the user's Business Card, by using the <Esc> key.</li>
	*<li><B>Verify :</B> The business card closes</li>
	*<li><B>Step:</B> Close the user's Business Card, by moving focus out of the Business Card.</li> 
	*<li><B>Verify:</B> The business card closes after a few seconds from when the focus leaves the card.</li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ciscotc", "regressioncloud"})
	public void businessCardCiscoIntegrationDetails() {
		//Start Test
		Pui.startTest();
		
		//Load the component and login as below user
		Pui.loadComponent(Data.getData().ComponentProfiles);
		Pui.login(testUser1);
		
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		Pui.clickLinkWait(ProfilesUICloud.People);
		Pui.clickLinkWait(ProfilesUICloud.MyProfile);
		
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Pui.waitForSameTime();
		Pui.fluentWaitTextPresent(Data.getData().feedFooter);
		Pui.openProfileBusinessVcard();

		//The User business card is opened
		log.info("INFO: Verify the User business card  is opened");
		Assert.assertTrue(Pui.fluentWaitElementVisible(ProfilesUICloud.BusinessCardWindow),
						 "ERROR: The User business card  is not opened");
		
		//Verify chat links in Business Card
		log.info("INFO: Verify card contains Chat link");
		Assert.assertTrue(Pui.isElementPresent(ProfilesUICloud.VcardChatLink),
						 "ERROR: Business card do not contain Chat link");
		try {
			WebClient webClient = new WebClient();		
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			DefaultCredentialsProvider credential = new DefaultCredentialsProvider();
			credential.addCredentials(testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());          
			webClient.setCredentialsProvider(credential);  
			
			log.info("INFO: Verify card Chat link: Jabber Chat or Sametime Chat");
			String jabberExtURL = cfg.getTestConfig().getBrowserURL() 
					+ "appregistry/api/v2/applications/app_id/" + Data.getData().CiscoJabberAppId + "/extensions/ext_id/" + Data.getData().CiscoJabberBizcardExtensionId;
			Page jabberPage = webClient.getPage(jabberExtURL);
			//verify response
			log.info("INFO: HTTP GET on " + jabberExtURL + " returned code " + jabberPage.getWebResponse().getStatusCode());
			log.info("INFO: HTTP GET on " + jabberExtURL + " returned string " + jabberPage.getWebResponse().getStatusMessage());
			String prop = driver.getSingleElement(ProfilesUICloud.VcardChatLink).getAttribute("href");
			//Verify 'Chat' link
			log.info("INFO: Business card Chat link is: " + prop);
			if(200 == jabberPage.getWebResponse().getStatusCode()) {
				Assert.assertTrue(prop.startsWith("xmpp://"),
						 "ERROR: Business card do not show Jabber Chat link");
			} else {
				Assert.assertTrue(prop.equalsIgnoreCase("javascript:void(0);"),
						 "ERROR: Business card do not show Sametime Chat link");
			}
			
			
			log.info("INFO: Verify card contains More Actions link");
			Assert.assertTrue(Pui.isElementPresent(ProfilesUICloud.MoreActionsLink),
							 "ERROR: Business card do not contain More Actions link");
			String webexExtURL = cfg.getTestConfig().getBrowserURL() 
					+ "appregistry/api/v2/applications/app_id/com.ibm.meetings.disable/extensions/ext_id/com.ibm.navbar.hidemeetings";
			Page webexPage = webClient.getPage(webexExtURL);
			 //verify response
			log.info("INFO: HTTP GET on " + webexExtURL + " returned code " + webexPage.getWebResponse().getStatusCode());
			log.info("INFO: HTTP GET on " + webexExtURL + " returned string " + webexPage.getWebResponse().getStatusMessage());
			//Verify 'Invite to Meeting' link
			log.info("INFO: Verify card contains More Actions -> Invite to a Meeting link");
			Pui.clickLinkWait(ProfilesUICloud.MoreActionsLink);
			if(200 == webexPage.getWebResponse().getStatusCode()) {
				Assert.assertFalse(Pui.isElementPresent(ProfilesUICloud.InviteToAMeetingLink),
						 "ERROR: Business card contain More Actions -> Invite to a Meeting link");
			} else {
				Assert.assertTrue(Pui.isElementPresent(ProfilesUICloud.InviteToAMeetingLink),
						 "ERROR: Business card do not contain More Actions -> Invite to a Meeting link");
			}
		} catch (MalformedURLException e) {
			log.info("Fail to excecute webClient.getPage command, get Exception: " + e.getMessage());
			fail("Fail to excecute webClient.getPage, fail the test.");
		} catch (IOException e) {
			log.info("Fail to excecute webClient.getPage command, get Exception: " + e.getMessage());
			fail("Fail to excecute webClient.getPage, fail the test.");
		}
		
		//Close the user's Business Card, by moving focus out of the Business Card
		log.info("INFO: Close the user's Business Card, by moving focus out of the Business Card");
		driver.getSingleElement(ProfilesUIConstants.MyICProfileCloud).hover();
		int numberOfWaits = 0;
		while(numberOfWaits < 5 && Pui.isElementVisible(ProfilesUICloud.BusinessCardWindow) == true) {
			log.info("INFO: Attempt " + (numberOfWaits + 1) + " of 5: Waiting for the Business Card component to be hidden in the UI");
			numberOfWaits ++;
		}
		//The business card closes
		log.info("INFO: Verify business card closes");
		Assert.assertFalse(Pui.isElementVisible(ProfilesUICloud.BusinessCardWindow),
				"ERROR: Business card is not closed");

		//End test
		Pui.endTest();
	}
	
}