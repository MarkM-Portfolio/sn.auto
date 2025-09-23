package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import java.util.ArrayList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015, 2016, 2017                        */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_SU_PartialMentions extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private User testUser1, testUser2, testUser3;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
	}
	
	/**
	* addStatusUpdate_PartialMentions() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Click into the embedded sharebox</B></li>
	*<li><B>Step: Add the text "@" - verification point 1</B></li>
	*<li><B>Step: Add 2 characters beside the "@" - eg "@Am" - verification point 2</B></li>
	*<li><B>Step: Add more characters - verification point 3</B></li>
	*<li><B>Verify: Verify that when the users types in "@" the text should turn blue to indicate the @mentions feature</B></li>
	*<li><B>Verify: Verify that when the users types "@Am" the typeahead is activated with a list of users that match</B></li>
	*<li><B>Verify: Verify that the typeahead dialog appears directly below the @mentioned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81D1440F3BB8F8D085257BF6006C717E">TTT - @Mentions - 00001 - Typeahead should appear when at least 2 characters have been entered</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_PartialMentions() {

		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Switch focus to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// User 1 will now enter the text '@' into the status update input field
		String mentionsTextAt = "@";
		UIEvents.typeStringWithDelay(ui, mentionsTextAt);
		
		// Verify that the mentions link is displayed and is also in a blue colour
		HomepageValid.verifyMentionsLinkIsDisplayedInBlueInCurrentActiveElement(ui, driver, mentionsTextAt);
		
		// User 1 will now add an additional set of characters "Am" (or a valid username equivalent) to the status update input field
		String mentionsTextFirstTwoChars = testUser2.getDisplayName().substring(0, 2);
		UIEvents.typeStringWithDelay(ui, mentionsTextFirstTwoChars);
		
		// Verify that the typeahead menu has loaded
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		
		// Retrieve the list of selectable names from the typeahead menu
		ArrayList<String> typeaheadMenuItemsList = UIEvents.getTypeaheadMenuItemsAsText(ui, driver);
		
		// Verify that all of the typeahead menu items contain the mentions text that has been entered thus far
		String mentionsTextAll = mentionsTextAt + mentionsTextFirstTwoChars;
		for(String typeaheadMenuItem : typeaheadMenuItemsList) {
			HomepageValid.verifyStringContainsSubstring(typeaheadMenuItem, mentionsTextAll.substring(1));
		}
		// Switch focus back to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
				
		// Verify that the mentions link is displayed and is also in a blue colour
		HomepageValid.verifyMentionsLinkIsDisplayedInBlueInCurrentActiveElement(ui, driver, mentionsTextAll);
		
		/**
		 * Clear the status update entry - Selenium loses focus with the status update input field after the previous validations
		 * and there are many inconsistencies with attempting to complete the mentions to User 2 in the input field afterwards.
		 * 
		 * The only consistent way to bypass this is to clear the status update input field and re-enter the mentions link from scratch.
		 * This is a Selenium issue ONLY. It does not happen manually.
		 */
		UIEvents.clearStatusUpdate(ui);
		
		// Switch focus back to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
				
		// User 1 will now enter all remaining characters in the valid username to the status update input field
		mentionsTextAll = "@" + testUser2.getDisplayName();
		UIEvents.typeStringWithDelay(ui, mentionsTextAll);
		
		// Verify that the typeahead menu has loaded
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		
		// Retrieve the list of selectable names from the typeahead menu
		typeaheadMenuItemsList = UIEvents.getTypeaheadMenuItemsAsText(ui, driver);
		
		// Verify that all of the typeahead menu items contain the mentions text that has been entered thus far
		for(String typeaheadMenuItem : typeaheadMenuItemsList) {
			HomepageValid.verifyStringContainsSubstring(typeaheadMenuItem, mentionsTextAll.substring(1));
		}
		// Switch focus back to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
				
		// Verify that the mentions link is displayed and is also in a blue colour
		HomepageValid.verifyMentionsLinkIsDisplayedInBlueInCurrentActiveElement(ui, driver, mentionsTextAll);
		
		ui.endTest();
	}

	/**
	* partialMentions_SelectUser_Click() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to the Embedded Sharebox</B></li>
	*<li><B>Step: Click in to add a status update and start typing "@xx" (make sure the letters are in a name in the system)</B></li>
	*<li><B>Step: When the typeahead appears click on a name - verification point 1</B></li>
	*<li><B>Step: Start typing another name with "@xx"</B></li>
	*<li><B>Step: When typeahead appears use the up/down arrows to select a user and click "Enter" when you get the user you want - verification point 2</B></li>
	*<li><B>Step: Attempt to edit either of the names that have been selected - verification point 3</B></li>
	*<li><B>Verify: Verify that you can scroll though the name with the mouse and when click the users name is entered in the embedded sharebox</B></li>
	*<li><B>Verify: Verify that you can scroll through the names with the up/down arrow and when you press enter the users name is entered in the embedded sharebox</B></li>
	*<li><B>Verify: Verify that the user cannot edit either of the names in the sharebox that were selected</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/163CA9D32827DADD85257BF6006C7181">TTT - @Mentions - 00002 - User can selected from typeahead</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void partialMentions_SelectUser_Click() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Switch focus to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Enter the mentions to User 2 using only the first two characters of User 2's username
		Mentions user2Mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		UIEvents.typeBeforeMentionsTextAndTypePartialMentions(ui, user2Mentions, 2);
		
		// Wait for the typeahead menu to be displayed
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		
		// Select the first user in the typeahead menu using the mouse
		String selectedUserWithMouse = UIEvents.getFirstTypeaheadMenuItemAndSelectUser(ui, driver);
		
		// Verify that the selected user with mouse has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithMouse, testUser2.getDisplayName().substring(0, 2));
		
		// Switch focus back to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Enter the mentions to User 3 using only the first two characters of User 3's username
		Mentions user3Mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, "", "");
		UIEvents.typeBeforeMentionsTextAndTypePartialMentions(ui, user3Mentions, 2);
		
		// Wait for the typeahead menu to be displayed
		UIEvents.waitForTypeaheadMenuToLoad(ui);
		
		// Select a random user in the typeahead menu using the arrow keys
		String selectedUserWithArrowKeys = UIEvents.selectTypeaheadUserInTopFrameUsingArrowKeys(ui);
		
		// Verify that the selected user with arrow keys has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithArrowKeys, testUser3.getDisplayName().substring(0, 2));
		
		// Switch focus back to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
				
		// Delete both of the mentions links using the backspace key - verifies that the delete key removes the entire mentions link (ie. the mentions link cannot be edited)
		boolean allMentionsDeleted = UIEvents.deleteTwoMentionsLinksWithBackspaceKey(driver, ui, selectedUserWithArrowKeys, selectedUserWithMouse);
		
		// Verify that all mentioned users were deleted successfully
		HomepageValid.verifyBooleanValuesAreEqual(allMentionsDeleted, true);
				
		ui.endTest();
	}
}