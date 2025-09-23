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

public class FVT_NotificationCenter_MyPage extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}

	/**
	* test_NC_Icon_Visible_MyPage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to "My Page"</B></li>
	*<li><B>Verify: Verify the Notification Center is displayed in the Mega Menu</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D2B98A5BFC073BDC85257DCB00348401">TTT - NOTIFICATION CENTER HEADER - 00011 - HEADER IS SHOWN ON MY PAGE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_NC_Icon_Visible_MyPage() {
		
		ui.startTest();
		
		// Log in to Connections as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Navigate to the My Page view
		UIEvents.gotoMyPage(ui);
		
		// Verify that the Notification Center icon is displayed in the My Page view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.NotificationCenterBtn}, null, true);
		
		ui.endTest();
	}
}