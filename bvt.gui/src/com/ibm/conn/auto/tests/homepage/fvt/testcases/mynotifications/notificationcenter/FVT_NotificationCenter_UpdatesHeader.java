package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout] FVT UI Automation for Story 140633
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143012
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_UpdatesHeader extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}

	/**
	* test_HeaderVisible_Updates() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following, Status Updates and Discover</B></li>
	*<li><B>Verify: Verify the Notification Center is present all of the views in Updates</B></li>
	*<li><a HREF="Notes://Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DDC55D6D1606A1DE85257DCB00342CCC">TTT - NOTIFICATION CENTER HEADER - 00010 - HEADER IS SHOWN ON UPDATES VIEWS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_HeaderVisible_Updates(){
		
		ui.startTest();
		
		// User 1 will now log in to Homepage
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the Notification Center is displayed in the view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.NotificationCenterBtn}, null, true);
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the Notification Center is displayed in the view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.NotificationCenterBtn}, null, true);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the Notification Center is displayed in the view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.NotificationCenterBtn}, null, true);
		
		ui.endTest();
	}
}