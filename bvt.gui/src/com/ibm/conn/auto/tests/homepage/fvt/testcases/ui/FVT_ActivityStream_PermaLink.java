package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui;

import java.util.List;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
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
/* Copyright IBM Corp. 2016				                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 *	Author:		Anthony Cox
 *	Date:		9th February 2016
 */

public class FVT_ActivityStream_PermaLink extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1;
	private String statusUpdate1User1, statusUpdate1User1Id, statusUpdate2User1, statusUpdate2User1Id;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now post the first of two status updates to their profile
		statusUpdate1User1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdate1User1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate1User1);
		
		// User 1 will now post the second of two status updates to their profile
		statusUpdate2User1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdate2User1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate2User1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdate1User1Id);
		profilesAPIUser1.deleteBoardMessage(statusUpdate2User1Id);
	}
	
	/**
	* test_StatusUpdate_TimestampLinksToMyProfile() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Post a status update</B></li>
	*<li><B>Step: Click the timestamp permalink for the status update</B></li>
	*<li><B>Verify: Verify that the user is brought to their profile page</B></li>
	*<li><b>Verify: Verify that it only shows the story of the status update in the profile AS</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4DBC964295FBC95E852579A0003C299C">TTT - AS - Perma-link - 00010 - Your profile Status Update will be a perma link</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdate_TimestampLinksToMyProfile() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Get the handle for the current browser window before clicking on the timestamp
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 1 will now click on the news story timestamp for their first status update to open the Profiles screen
		UIEvents.clickNewsStoryTimestamp(ui, testUser1, statusUpdate1User1);
		
		// Switch focus to the Profiles screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the user is brought to their own profile page
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersOwnProfile(ui, testUser1);
		
		// Verify that the status update selected by User 1 is displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1User1}, null, true);
		
		// Verify that all other status updates posted by User 1 are NOT displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2User1}, null, false);
		
		// Close the Profiles UI screen
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		// User 1 will now click on the news story timestamp for their second status update to open the Profiles screen
		UIEvents.clickNewsStoryTimestamp(ui, testUser1, statusUpdate2User1);
				
		// Switch focus to the Profiles screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the user is brought to their own profile page
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersOwnProfile(ui, testUser1);
				
		// Verify that the status update selected by User 1 is displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2User1}, null, true);
				
		// Verify that all other status updates posted by User 1 are NOT displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1User1}, null, false);
		
		// Close the Profiles UI screen
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		ui.endTest();
	}
	
	/**
	* test_StatusUpdate_TimestampLinksToMyProfile_FromOtherUser() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 go to Homepage / Discover / Status Updates</B></li>
	*<li><b>Step: User 2 go to the story of the status update added in point 2</b></li>
	*<li><b>Step: User 2 click the time link that the status update was added</b></li>
	*<li><B>Verify: Verify that the user is brought to their profile page</B></li>
	*<li><b>Verify: Verify that it only shows the story of the status update in the profile AS</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD5DB07324F57966852579A0003CE7EE">TTT - AS - Perma-link - 00011 - Another users profile Status Update will be a perma link</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdate_TimestampLinksToMyProfile_FromOtherUser() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Get the handle for the current browser window before clicking on the timestamp
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 2 will now click on the news story timestamp for User 1's first status update to open the Profiles screen
		UIEvents.clickNewsStoryTimestamp(ui, testUser2, statusUpdate1User1);
				
		// Switch focus to the Profiles screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the user is brought to User 1's profile page
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersProfileAsAnotherUser(ui, testUser1);
				
		// Verify that the status update selected by User 2 is displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1User1}, null, true);
				
		// Verify that all other status updates posted by User 1 are NOT displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2User1}, null, false);
				
		// Close the Profiles UI screen
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		// User 2 will now click on the news story timestamp for User 1's second status update to open the Profiles screen
		UIEvents.clickNewsStoryTimestamp(ui, testUser2, statusUpdate2User1);
						
		// Switch focus to the Profiles screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
						
		// Verify that the user is brought to User 1's profile page
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersProfileAsAnotherUser(ui, testUser1);
						
		// Verify that the status update selected by User 2 is displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2User1}, null, true);
						
		// Verify that all other status updates posted by User 1 are NOT displayed in Profiles UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1User1}, null, false);
				
		// Close the Profiles UI screen
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
	
	/**
	* test_StatusUpdate_TimestampLinksToMyProfile_FilterViews() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Go to Homepage</B></li>
	*<li><B>Step: Go to Discover / Status Updates</B></li>
	*<li><B>Step: Select the hyper link (TIME LINK) for the status update was added (this should take you to the Profiles AS)</B></li>
	*<li><B>Verify: Verify that user is taken to the Profiles page with the status update shown in the Activity Stream</B></li>
	*<li><b>Verify: Verify that the filter option say "Select a Filter" and that the user can select another filter option</b></li>
	*<li><b>Verify: Verify that the "Select a Filter" option does not appear anymore</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F8A75919D8D1DA49852579A70048B07A">TTT - AS - Perma-link - 00014 - User can filter to another view in the Profile AS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_StatusUpdate_TimestampLinksToMyProfile_FilterViews() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Get the handle for the current browser window before clicking on the timestamp
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 1 will now click on the news story timestamp for their first status update to open the Profiles screen
		UIEvents.clickNewsStoryTimestamp(ui, testUser1, statusUpdate1User1);
		
		// Switch focus to the Profiles screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the user is brought to their own profile page
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersOwnProfile(ui, testUser1);
		
		// Retrieve the currently selected filter option in Profiles UI
		String currentFilter = UIEvents.getSelectedASViewFilterOption(ui);
		
		// Verify that the current filter selection is for the 'Select a filter' option
		HomepageValid.verifyStringValuesAreEqual(currentFilter.trim(), HomepageUIConstants.FilterSelectAFilter);
		
		// Filter the Profiles UI AS by 'All'
		UIEvents.filterBy(ui, HomepageUIConstants.FilterAll);
		
		// Retrieve all of the selectable filters from Profiles UI
		List<String> allSelectableFilters = UIEvents.getAllASViewFilterOptions(ui);
		
		int index = 0;
		boolean foundSelectAFilter = false;
		while(index < allSelectableFilters.size() && foundSelectAFilter == false) {
			if(allSelectableFilters.get(index).trim().equals(HomepageUIConstants.FilterSelectAFilter)) {
				foundSelectAFilter = true;
			}
			index ++;
		}
		// Verify that the 'Select a filter' option was no longer present in the list of selectable filters in Profiles UI
		HomepageValid.verifyBooleanValuesAreEqual(foundSelectAFilter, false);
		
		// Close the Profiles UI screen
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}