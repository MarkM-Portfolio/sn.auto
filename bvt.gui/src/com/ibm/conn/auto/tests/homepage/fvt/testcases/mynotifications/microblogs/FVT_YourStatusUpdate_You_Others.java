package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015, 2017                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourStatusUpdate_You_Others extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7;
	private String statusUpdate1, statusUpdate1Id, statusUpdate2Id;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(7);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);

		// Set the filters to be tested
		if(isOnPremise) {
			TEST_FILTERS = new String[2];
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS = new String[1];
		}
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		
		// User 1 will now post their first status update
		statusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdate1Id);
		profilesAPIUser1.deleteBoardMessage(statusUpdate2Id);
	}

	/**
	* test_YourStatusUpdate_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 1 like their status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 1</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 2</B></li>
	*<li><B>Step: User 1 add another status update</B></li>
	*<li><B>Step: User 3 to User 7 like the Status Update</B></li>
	*<li><B>Step: User 1 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Profiles - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your message."</B></li>
	*<li><B>Verify: Verify the event shows "You and 5 others liked your message."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/339E140602B9D56C85257DEA0051CF54">TTT - MY NOTIFICATIONS - MICROBLOGGING - 00021 - LIKE ON YOUR OWN STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourStatusUpdate_Like_You_Others_Rollup(){

		ui.startTest();
		
		// User 1 will now like / recommend their own status update
		ProfileEvents.likeStatusUpdate(profilesAPIUser1, statusUpdate1Id);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeStatusUpdateEvent = ProfileNewsStories.getLikedYourMessageNewsStory_You(ui);
		
		// Verify that the like status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdateEvent, statusUpdate1}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend User 1's status update
		ProfileEvents.likeStatusUpdate(profilesAPIUser2, statusUpdate1Id);
		
		// Create the news story to be verified
		likeStatusUpdateEvent = ProfileNewsStories.getLikedYourMessageNewsStory_UserAndYou(ui, testUser2.getDisplayName());
		
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdateEvent, statusUpdate1}, TEST_FILTERS, true);
		
		// User 1 will now post their second status update
		String statusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate2);
				
		// Users 3 through to User 7 will now like / recommend User 1's second status update with User 1 then liking / recommending their second status update
		ProfileEvents.likeStatusUpdate(profilesAPIUser3, statusUpdate2Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser4, statusUpdate2Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser5, statusUpdate2Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser6, statusUpdate2Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser7, statusUpdate2Id);
		ProfileEvents.likeStatusUpdate(profilesAPIUser1, statusUpdate2Id);
		
		// Create the news story to be verified
		likeStatusUpdateEvent = ProfileNewsStories.getLikedYourMessageNewsStory_YouAndMany(ui, "5");
		
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdateEvent, statusUpdate2}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}