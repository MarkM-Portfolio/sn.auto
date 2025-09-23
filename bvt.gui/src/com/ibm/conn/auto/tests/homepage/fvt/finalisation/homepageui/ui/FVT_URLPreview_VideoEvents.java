package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

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
 * Date:	29th September 2016
 */

public class FVT_URLPreview_VideoEvents extends SetUpMethods2 {
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * test_URLPreview_VideoAdded_NotPosted() 
	 *<ul>
	 *<li><B>1: On one browser go youtube and copy the URL for a video</B></li>
	 *<li><B>2: On another browser log into Connections</B></li>
	 *<li><B>3: Go to Homepage</B></li>
	 *<li><B>4: Click into the sharebox and paste in the URL for the video</B></li>
	 *<li><B>Verify: Verify the video appears in the URL Preview</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6ACBF7D9C112891485257C2F0040E588">URL PREVIEW - SHAREBOX - 00050 - INLINE VIDEO CAN BE ADDED</a></li>
	 *</ul>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_URLPreview_VideoAdded_NotPosted() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// User 1 will now enter a URL for a video into the embedded sharebox
		String user1VideoURL = Data.getData().ibmConnectionsVideo;
		boolean urlPreviewDisplayedCorrectly = UIEvents.typeStatusWithVideoURL(ui, "", user1VideoURL);
		
		// Verify that the URL Preview widget for the video was displayed correctly
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewDisplayedCorrectly, true);
		
		ui.endTest();
	}
}