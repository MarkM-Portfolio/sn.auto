package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.mentions;

import java.util.ArrayList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016  			                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	28th September 2016
 */

public class FVT_Mentions_Microblogs extends SetUpMethods2 {

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * test_MentionsLink_StatusUpdate_NotPosted() 
	 *<ul>
	 *<li><B>1: Go to Homepage Activity Stream</B></li>
	 *<li><B>2: Click to enter in a status update in the embedded sharebox</B></li>
	 *<li><B>3: Add the text "@" - verification point 1</B></li>
	 *<li><B>4: Add 2 characters beside the "@" - eg "@Am" - verification point 2</B></li>
	 *<li><B>5: Add more characters - verification point 3</B></li>
	 *<li><B>Verify: Verify that when the users types in "@" the text should turn blue to indicate the @mentions feature</B></li>
	 *<li><B>Verify: Verify that when the users types "@Am" the typeahead is activated with a list of users that match</B></li>
	 *<li><B>Verify: Verify that the typeahead dialog appears directly below the @mentioned</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81D1440F3BB8F8D085257BF6006C717E">@Mentions - 00001 - Typeahead should appear when at least 2 characters have been entered</a></li>
	 *</ul>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_MentionsLink_StatusUpdate_NotPosted() {
		
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
}