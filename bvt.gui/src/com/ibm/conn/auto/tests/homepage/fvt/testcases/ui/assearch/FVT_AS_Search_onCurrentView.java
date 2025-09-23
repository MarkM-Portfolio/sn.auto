package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.assearch;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_AS_Search_onCurrentView extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1;
	private String statusUpdate1, statusUpdate1Id, statusUpdate2, statusUpdate2Id, statusUpdateRandom1, statusUpdateRandom2;
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// Initialise the random status update strings to be used in the AS Search panels in all tests
		statusUpdateRandom1 = Helper.genStrongRand();
		statusUpdateRandom2 = Helper.genStrongRand();
		
		// User 1 will now post a status update which includes the first random string
		statusUpdate1 = Data.getData().commonStatusUpdate + " " + statusUpdateRandom1;
		statusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate1);
		
		// User 1 will now post a status update which includes the second random string
		statusUpdate2 = Data.getData().commonStatusUpdate + " " + statusUpdateRandom2;
		statusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate2);
	}
									   
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdate1Id);
		profilesAPIUser1.deleteBoardMessage(statusUpdate2Id);
	}
	
	/**
	* asSearch_onCurrent_imFollowingView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update</B></li>
	*<li><B>Step: Allow sufficient time to pass for indexing to occur</B></li>
	*<li><B>Step: Click the search icon at the end of the banner with the view name</B></li>
	*<li><B>Step: Add in some text that appears in the status update in that view</B></li>
	*<li><B>Verify: Verify the event is returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/67E56E79359B31FE85257C3500507AB4">TTT - AS SEARCH - 00030 - SEARCH IS ON CURRENT I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_onCurrent_imFollowingView() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Search for the first random String using AS Search
		UIEvents.searchUsingASSearch(ui, testUser1, statusUpdateRandom1);
		
		// Verify that User 1's status update which includes the first random string is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1}, null, true);
		
		// Verify that User 1's status update which includes the second random string is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2}, null, false);
		
		ui.endTest();
	}

	/**
	* asSearch_onCurrent_discoverView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: Post a status update</B></li>
	*<li><B>Step: Allow sufficient time to pass for indexing to occur</B></li>
	*<li><B>Step: Click the search icon at the end of the banner with the view name</B></li>
	*<li><B>Step: Add in some text that appears in the status update in that view</B></li>
	*<li><B>Verify: Verify the event is returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ECBFF9DA5CF2446285257C350052F8A3">TTT - AS SEARCH - 00032 - SEARCH IS ON CURRENT DISCOVER VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_onCurrent_discoverView() {

		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Search for the first random String using AS Search
		UIEvents.searchUsingASSearch(ui, testUser1, statusUpdateRandom1);
		
		// Verify that User 1's status update which includes the first random string is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1}, null, true);
		
		// Verify that User 1's status update which includes the second random string is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2}, null, false);
		
		ui.endTest();
	}

	/**
	* asSearch_onCurrent_statusUpdatesView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / Status Updates / All</B></li>
	*<li><B>Step: Post a status update</B></li>
	*<li><B>Step: Allow sufficient time to pass for indexing to occur</B></li>
	*<li><B>Step: Click the search icon at the end of the banner with the view name</B></li>
	*<li><B>Step: Add in some text that appears in the status update in that view</B></li>
	*<li><B>Verify: Verify the event is returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FF1FFD8BBC1647B385257C350052F717">TTT - AS SEARCH - 00031 - SEARCH IS ON CURRENT STATUS UPDATES VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_onCurrent_statusUpdatesView() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Search for the first random String using AS Search
		UIEvents.searchUsingASSearch(ui, testUser1, statusUpdateRandom1);
		
		// Verify that User 1's status update which includes the first random string is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1}, null, true);
		
		// Verify that User 1's status update which includes the second random string is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2}, null, false);
		
		ui.endTest();
	}
}