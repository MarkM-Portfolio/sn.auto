/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.communities;

import java.util.Set;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;

import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class FVT_HelpMenu extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(FVT_HelpMenu.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();		
	}

	/**
	 *<ul>
	 *<li><B>Info: Create a Community and enable survey option</B></li>
	 *<li><B>Steps: 
	 *<li><B>Create a Community Community Type: (Access: Public, Tags, Description</B></li>
	 *<li><B>Enable the surveys option widget</B></li>
	 *<li><B>Verify: The community is created and that you can add the Survey Widget to the community</B> </li>
	 *</ul>
	 *@author Cheryl Wang
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void testCommunitiesHelpMenu() throws Exception {

		ui.startTest();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		log.info("INFO: User logged in : " + testUser1.getDisplayName());
		
		log.info("INFO: check Community help menu");

		String url = null;
		
		ui.clickLinkWait(CommunitiesUIConstants.helpMenu);
	
		driver.getFirstElement(CommunitiesUIConstants.helpItem).click();
		
		Set<String> test = driver.getWindowHandles();
		String wHelpWindow = null;
		
		for (String a:test){
			log.info("Got window title: " + a.toString());
			driver.switchToWindowByHandle(a.toString());
			
			log.info("Switch to new window: " + driver.getTitle());
			
			if (driver.getTitle().equalsIgnoreCase(CommunitiesUIConstants.helpWindowTitle)){
		
				wHelpWindow = a.toString();
				log.info("Got help window " + wHelpWindow);
				
				url = driver.getCurrentUrl();
				log.info("current URL = " + url);
			
				break;
			}	
		}			
		    
		Assert.assertTrue(url.contains("cloud.communities.doc"), "Got communities.doc");				
		driver.saveScreenshot("Community Help");
		log.info("After saving help screen shot");
	
		ui.endTest();
		
	}
}
