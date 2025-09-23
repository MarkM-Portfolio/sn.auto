package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.profiles;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 *	Author: 	Anthony Cox
 *	Date:		24th September 2015
 */

public class FVT_Saved_Profiles extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private String user1MessageToUser2, user1MessageToUser2Id, user1MessageToUser3, user1MessageToUser3Id, user4MessageToUser5, user4MessageToUser5Id;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configurations
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		TEST_FILTERS = new String[2];
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		if(isOnPremise) {
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS[1] = HomepageUIConstants.FilterSU;
		}
		
		// User 1 will now post a board message to User 2
		user1MessageToUser2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1MessageToUser2Id = ProfileEvents.addBoardMessage(user1MessageToUser2, profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now post a board message to User 3
		user1MessageToUser3 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1MessageToUser3Id = ProfileEvents.addBoardMessage(user1MessageToUser3, profilesAPIUser1, profilesAPIUser3);
		
		// User 4 (acting as User 1) will now post a board message to User 5 (acting as User 2)
		user4MessageToUser5 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user4MessageToUser5Id = ProfileEvents.addBoardMessage(user4MessageToUser5, profilesAPIUser4, profilesAPIUser5);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the board messages posted during the test
		profilesAPIUser1.deleteBoardMessage(user1MessageToUser2Id);
		profilesAPIUser1.deleteBoardMessage(user1MessageToUser3Id);
		profilesAPIUser4.deleteBoardMessage(user4MessageToUser5Id);
	}
	
	/**
	* test_Profiles_SavingProfileStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 1 post a message to another users board (not User 2)</B></li>
	*<li><B>3. User 1 go to Homepage / Discover / Profiles</B></li>
	*<li><B>4. User 1 go to the story of the message on users board and mark it as Saved</B></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>5: User 1 go to Homepage / Saved / Profiles </b></li>
	*<li><b>Verify: User 1 should see the message posted in Homepage / Saved / Profiles</b></li>
	*<li><b>6: User 1 post a message on User 2 board</b></li>
	*<li><b>7: User 2 log into connections go to Homepage / My Notifications / For Me / Profiles</b></li>
	*<li><b>8: User 2 go to the story of User 1 leaving a message on User 2 board and mark it as Saved</b></li>
	*<li><b>Verify: User 2 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 2 go to Homepage / Saved / Profiles</b></li>
	*<li><B>Verify: User 2 should see the message posted in Homepage / Saved / Profiles</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/A8381478178C3ED28525793B00341865">TTT: AS - Saved - 00011 - Marking Profile Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Profiles_SavingProfileStories() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved and verified
		String postedMessageToUser3Event = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser3.getDisplayName(), testUser1.getDisplayName());
		
		// Save the board message to User 3 event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, postedMessageToUser3Event);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the board message to User 3 event and User 1's board message are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{postedMessageToUser3Event, user1MessageToUser3}, TEST_FILTERS, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, true);
		
		// Create the news story to be saved and verified
		String postedMessageToUser2Event = ProfileNewsStories.getPostedAMessageToYouNewsStory(ui, testUser1.getDisplayName());
		
		// Save the board message to User 2 event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, postedMessageToUser2Event);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the board message to User 2 event and User 1's board message are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{postedMessageToUser2Event, user1MessageToUser2}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_Profiles_RemovingSavedStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. Go to Homepage / Saved / Profiles</B></li>
	*<li><B>3. Click the 'X' in the story related to the board message that was posted by User 1</B></li>
	*<li><b>Verify: User 1 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><B>4. Go to Homepage / Discover / Profiles</B></li>
	*<li><b>Verify: User 1 - The story in Homepage / Discover / Profiles should have a clickable "Save this" link again</b></li>
	*<li><b>5: User 2 log into Connections</b></li>
	*<li><b>6: Go to Homepage / Saved / Profiles</b></li>
	*<li><b>7: Click the "X" in the story related to the board message that was posted by User 1</b></li>
	*<li><b>Verify: User 2 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>8: Go to Homepage / My Notifications / For Me / Profiles</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / I'm Following / For Me / Profiles should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/88342E2A572440B08525793B0035EB79">TTT: AS - Saved - 00012 - Removing Profile Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Profiles_RemovingSavedStories() {
		
		/**
		 * To avoid clashing with news stories from the previous test case - this test case will use User 4 (as User 1) and User 5 (as User 2)
		 */
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser4, false);
		
		// Create the news story to be saved and verified
		String postedMessageToUser2Event = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser5.getDisplayName(), testUser4.getDisplayName());
		
		// Save the board message to User 2 event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, postedMessageToUser2Event);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the board message to User 2 event and User 1's board message are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{postedMessageToUser2Event, user4MessageToUser5}, TEST_FILTERS, true);
		
		// Remove the board message to User 2 event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, postedMessageToUser2Event);
		
		// Verify that the board message to User 2 event and User 1's board message are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{postedMessageToUser2Event, user4MessageToUser5}, TEST_FILTERS, false);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the board message to User 2 event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, postedMessageToUser2Event);
		
		// Log out from Connections
		LoginEvents.logout(ui);
				
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser5, true);
		
		// Create the news story to be saved and verified
		String messageReceivedFromUser1Event = ProfileNewsStories.getPostedAMessageToYouNewsStory(ui, testUser4.getDisplayName());
		
		// Save the board message received from User 1 event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, messageReceivedFromUser1Event);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the board message received from User 1 event and User 1's board message are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messageReceivedFromUser1Event, user4MessageToUser5}, TEST_FILTERS, true);
		
		// Remove the board message received from User 1 event news story using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, messageReceivedFromUser1Event);
		
		// Verify that the board message received from User 1 event and User 1's board message are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messageReceivedFromUser1Event, user4MessageToUser5}, TEST_FILTERS, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the board message received from User 1 event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, messageReceivedFromUser1Event);
				
		ui.endTest();
	}
}