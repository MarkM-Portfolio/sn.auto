package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout UX updates] FVT UI Automation for Story 142225
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143014
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_OpenFlyout_Hover extends SetUpMethodsFVT {

	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}

	/**
	* test_OpenFlyout_Hover() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Hover over the Notification Center Header in the top navigation</B></li>
	*<li><B>Verify: Verify the flyout opens</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8642A3000E32FB3785257E110035C0A2">TTT - NOTIFICATION CENTER FLYOUT - 00013 - HOVERING ON HEADER OPENS FLYOUT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_OpenFlyout_Hover(){
		
		ui.startTest();
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Hover over the Notification Center header to open the flyout
		boolean flyoutOpened = UIEvents.hoverOverNotificationCenter(ui);
		
		// Verify that the Notification Center flyout opened as expected
		HomepageValid.verifyBooleanValuesAreEqual(flyoutOpened, true);
		
		ui.endTest();
	}
}