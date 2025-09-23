package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.assearch;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
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
/* Copyright IBM Corp. 2014,  2016                                   */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_AS_Search_Actions extends SetUpMethodsFVT {
	
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
									   
	/**
	* asSearch_bannerSearchIcon_hoverText() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following, Homepage / Updates / Status Updates and Homepage / Updates / Discover</B></li>
	*<li><B>Step: On each of the views hover over the search icon at the end of the banner with the view name</B></li>
	*<li><B>Verify: Verify the hover text is "Open the search bar to Search the current view"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/507DC5CB386E778C85257C310045B2C9">TTT - AS SEARCH - 00011 - SEARCH ICON HAS HOVER TEXT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_bannerSearchIcon_hoverText() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Verify that the AS Search hover text displays the expected String content
		verifyASSearchHoverText();
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the AS Search hover text displays the expected String content
		verifyASSearchHoverText();
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the AS Search hover text displays the expected String content
		verifyASSearchHoverText();
		
		ui.endTest();
	}
	
	/**
	* asSearch_inputBox_shadowText() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following, Homepage / Updates / Status Updates and Homepage / Updates / Discover</B></li>
	*<li><B>Step: Click on the search icon at the end of the banner with the view name</B></li>
	*<li><B>Verify: Verify the search input box has the shadow text "Search this stream"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1C9EC711CC9871C985257C31004628BF">TTT - AS SEARCH - 00014 - SEARCH INPUT BOX SHADOW TEXT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_inputBox_shadowText() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Verify that the AS Search input field shadow text displays the expected String content
		verifyASSearchShadowText();
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the AS Search input field shadow text displays the expected String content
		verifyASSearchShadowText();
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the AS Search input field shadow text displays the expected String content
		verifyASSearchShadowText();
		
		ui.endTest();
	}
	
	/**
	* asSearch_inputBox_xIcon() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following, Homepage / Updates / Status Updates and Homepage / Updates / Discover</B></li>
	*<li><B>Step: Click on the search icon at the end of the banner with the view name</B></li>
	*<li><B>Step: Click the X icon in the input box</B></li>
	*<li><B>Verify: Verify the search input box is closed when the X icon has been clicked</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/70768F07E959A3A685257C310046B658">TTT - AS SEARCH - 00019 - CLICKING X ICON CLOSES THE INPUT BOX</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_inputBox_xIcon(){
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Open AS Search and verify its components are displayed - then close AS Search using the 'X' icon and verify its components are NOT displayed
		verifyASSearchComponents();
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Open AS Search and verify its components are displayed - then close AS Search using the 'X' icon and verify its components are NOT displayed
		verifyASSearchComponents();
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Open AS Search and verify its components are displayed - then close AS Search using the 'X' icon and verify its components are NOT displayed
		verifyASSearchComponents();
				
		ui.endTest();
	}

	/**
	* asSearch_applicationFilter_removed() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to each of the following Activity Stream views
	*				Updates / I'm Following
	*				Updates / Status Updates
	*				Updates / Discover</B></li>
	*<li><B>Step: Click on the search icon at the end of the banner with the view name</B></li>
	*<li><B>Verify: Verify there is no Filter menu visible when the input box is opened</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/73F59125336CC24085257F4C0044D8C0">TTT - AS SEARCH - 00025 - OPENING SEARCH INPUT BOX REMOVES FILTER MENU</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_applicationFilter_removed(){
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Verify that the AS filter is displayed when AS Search is closed - and that the AS filter is NOT displayed when AS Search is open
		verifyASFilterComponents();
		
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		// Verify that the AS filter is displayed when AS Search is closed - and that the AS filter is NOT displayed when AS Search is open
		verifyASFilterComponents();
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the AS filter is displayed when AS Search is closed - and that the AS filter is NOT displayed when AS Search is open
		verifyASFilterComponents();
		
		ui.endTest();
	}
	
	/**
	 * Verifies that the AS Search icons hover text contains the expected String content
	 */
	private void verifyASSearchHoverText() {
		
		/**
		 * At this point - Selenium will see the element but will NOT be able to retrieve the "title" attribute of the element - it will return an empty String
		 * 
		 * In order to resolve this, we have to ask Selenium to click on the element twice - once to open AS Search and again to close AS Search.
		 * After doing this, Selenium is able to retrieve the "title" with the expected value.
		 * 
		 * This does NOT happen manually - the hover text appears as expected (and Firebug confirms that the HTML element contains the expected title text).
		 * This is solely a Selenium-related issue.
		 */
		// Click to open AS Search
		UIEvents.openASSearch(ui);
		
		// Click to close AS Search
		UIEvents.cancelASSearchUsingMagnifyingGlassIcon(ui);
		
		// Retrieve the 'title' attribute for the element - this acts as the hover text for the element
		String elementHoverText = UIEvents.getElementTitleAttribute(driver, HomepageUIConstants.AS_SearchOpenElement);
		
		// Verify that the hover text contains the expected text
		HomepageValid.verifyStringValuesAreEqual(elementHoverText, Data.getData().AS_SearchToolTipText);
	}
	
	/**
	 * Verifies that the AS Search input field shadow text contains the expected String content
	 */
	private void verifyASSearchShadowText() {
		
		// Click to open AS Search
		UIEvents.openASSearch(ui);
		
		// Retrieve the 'Shadow Text' for the input box in AS Search
		String shadowTextValue = UIEvents.getElementText(ui, HomepageUIConstants.AS_SearchTextArea);
		
		// Verify that the 'Shadow Text' value is the expected 'Search this stream' String
		HomepageValid.verifyStringValuesAreEqual(shadowTextValue, Data.getData().AS_SearchShadowText);
		
		// Click to close AS Search
		UIEvents.cancelASSearchUsingMagnifyingGlassIcon(ui);
	}
	
	/**
	 * Verifies that the AS Search components are displayed after opening the AS Search panel
	 * Also verifies that these components are NOT displayed again after closing the AS Search panel
	 */
	private void verifyASSearchComponents() {
		
		// Click to open AS Search
		UIEvents.openASSearch(ui);
		
		// Verify that all AS Search components are displayed
		HomepageValid.verifyASSearchIsDisplayed(ui);
		
		// Close the AS Search panel by clicking on the 'X' icon
		UIEvents.cancelASSearchUsingXIcon(ui);
		
		// Verify that the AS Search components are NOT displayed
		HomepageValid.verifyASSearchIsNotDisplayed(ui);
	}
	
	/**
	 * Verifies that the AS Filter components are displayed before the AS Search panel is open
	 * Then verifies that the AS Filter components are NOT displayed when the AS Search panel is open
	 * Finally verifies that the AS Filter components are displayed again after the AS Search panel has been closed
	 */
	private void verifyASFilterComponents() {
		
		// Verify that the AS filter is displayed
		HomepageValid.verifyASFilterIsDisplayedWhenASSearchIsClosed(driver);
		
		// Click to open AS Search
		UIEvents.openASSearch(ui);
		
		// Verify that the AS filter is NOT displayed
		HomepageValid.verifyASFilterIsNotDisplayedWhenASSearchIsOpen(driver);
		
		// Click to close AS Search
		UIEvents.cancelASSearchUsingXIcon(ui);
		
		// Verify that the AS filter is displayed again
		HomepageValid.verifyASFilterIsDisplayedWhenASSearchIsClosed(driver);
	}
}