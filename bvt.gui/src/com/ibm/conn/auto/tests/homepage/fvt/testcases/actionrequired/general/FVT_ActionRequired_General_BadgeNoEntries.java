package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.general;

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
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author 	Anthony Cox
 *	Date:		13th January 2016
 */

public class FVT_ActionRequired_General_BadgeNoEntries extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		/**
		 * 	It is necessary for this test to pick a user that has zero items in their Action Required view.
		 * 	As a result, this loop will continually search for a user until one with no events in their AR view is found.
		 */
		boolean foundUser = false;
		boolean preserveInstance = false;
		while(!foundUser) {
			// Select a user from the user pool
			setListOfStandardUsers(1);
			testUser1 = listOfStandardUsers.get(0);
			
			// Log in as User 1 and verify that this user has no events in their Action Required view
			LoginEvents.loginToHomepage(ui, testUser1, preserveInstance);
			
			if(preserveInstance == false) {
				preserveInstance = true;
			}
			if(UIEvents.getActionRequiredBadgeValue(driver) == 0) {
				// This user has no events in their Action Required view
				foundUser = true;
			}
			// Log out from Homepage
			LoginEvents.logout(ui);
		}
		// Close the browser instance now that the user has been found
		driver.close();
	}
	
	/**
	* test_ActionRequired_BadgeDoesNotAppearWhenNoEntries()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Homepage AS</B></li>
	*<li><B>2. Look at the Action Required view on from all other views on Homepage</B></li>
	*<li><b>Verify: Verify that there is no badge with number beside the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D499B01EBB53367E0525798400465911">TTT: AS - Action Required - 00010 - Badge Does Not Appear When No Entries</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_BadgeDoesNotAppearWhenNoEntries() {
		
		ui.startTest();
		
		// Log in as User 1 and go the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the Action Required view
		UIEvents.gotoActionRequired(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the Action Required badge value has the expected value of 0
		HomepageValid.verifyIntValuesAreEqual(UIEvents.getActionRequiredBadgeValue(driver), 0);
		
		ui.endTest();
	}
}