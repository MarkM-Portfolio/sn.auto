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
/* Copyright IBM Corp. 2015, 2017                                    */
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

public class FVT_NotificationCenter_FooterLink extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}

	/**
	* test_NotificationCenter_FooterLink() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: User 1 open the notification center flyout</B></li>
	*<li><B>Step: User 1 look at the My Notifications link in the Footer - verification point 1</B></li>
	*<li><B>Step: User 1 click on the activity entry name - verification point 2</B></li>
	*<li><B>Verify: Verify the  name is a clickable link</B></li>
	*<li><B>Verify: Verify that it opens the My Notifications/For Me view in Homepage</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/53CB78B79C840A1185257E1B004F1BE3">TTT - NOTIFICATION CENTER FLYOUT - 00034 - FOOTER HAS A CLICKABLE LINK TO TAKE USER TO MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_FooterLink(){
		
		ui.startTest();
		
		// Log in to Homepage as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// User 1 will now open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		// Verify that the Notification Footer link 'See All' is displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.NotificationCenterFooter}, null, true);
		
		// Retrieve the main browser window handle before clicking on the footer link
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// Click on the 'See All' link in the Notification Center flyout
		UIEvents.clickSeeAllInNotificationCenter(ui);
		
		// Switch focus to the newly opened 'My Notifications' browser window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the My Notifications view is displayed in the new browser window
		HomepageValid.verifyMyNotificationsIsDisplayed(ui);
		
		// Close the Activities UI browser window and switch focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}