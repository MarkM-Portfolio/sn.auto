package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
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
 * Date:	29th September 2016
 */

public class FVT_Microblogs_StatusUpdate_WithHashtag extends SetUpMethods2 {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.MyUpdates };
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	* test_PostStatusUpdate_WithHashtag() 
	*<ul>
	*<li><B>1: Log into Connections</B></li>
	*<li><B>2: Go to Homepage / Status Updates</B></li>
	*<li><B>3: Post a status with a hashtag in the sharebox</B></li>
	*<li><B>Verify: Verify that the status added successfully message appears</B></li>
	*<li><B>Verify: Verify that the status appears in the following dynamically - 1. Status Updates / All and 2. Status Updates / My Updates</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BBD0626254378EDE852579420040EC8D">Activity Stream Sharebox - 00013 - User should be able to post a status with a hashtag</a></li>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PostStatusUpdate_WithHashtag() {
		
		ui.startTest();
		
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now post a status update with a hashtag using the AS Sharebox
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1Hashtag = "#" + ProfileEvents.generateValidHashtag();
		ProfileEvents.addStatusUpdateUsingUI(ui, testUser1, user1StatusUpdate + " " + user1Hashtag, false);
		
		for(String filter : TEST_FILTERS) {
			String testFilter = filter;
			if(filter.equals(HomepageUIConstants.FilterAll)) {
				testFilter = null;
			}
			// Verify that User 1's status update is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate}, testFilter, true);
			
			// Verify that the hashtag is displayed in all views
			HomepageValid.verifyHashtagLinkIsDisplayed(ui, user1Hashtag);
		}
		ui.endTest();
	}
}