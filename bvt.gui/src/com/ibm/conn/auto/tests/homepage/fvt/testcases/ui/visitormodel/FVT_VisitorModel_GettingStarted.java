package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
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

public class FVT_VisitorModel_GettingStarted extends SetUpMethods2{
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitorModel_gettingStarted_textAndIcons() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in as a Standard User</B></li>
	*<li><B>Step: Invite testUser2 as a visitor</B></li>
	*<li><B>Step: Log in as testUser2 (visitor)</B></li>
	*<li><B>Step: The Getting started web page is displayed by default</B></li>
	*<li><B>Step: The text "Welcome to IBM Connections" will be displayed at the top of the page- Verification point 1</B></li>
	*<li><B>Step: Check that the The getting started page contains the following text and icons:Verification point 2</B></li>
	*<li><B>Step:  IBM Connections gives you the tools you need to collaborate with others.
        
        Homepage ICON**     The Home page will help you quickly kind out what's new, what requires your attention, and
        when people are mentioning you. To get the most out of this page, be sure to follow communities 
        and files, so you can easily stay up-to-date on the latest changes. To open the home page, click the 
        Home tab.

        Communities ICON**     Use Communities to participate in a community of interest, where you can share files, track
        projects, ask and respond to questions , co-edit information, and brainstorm new ideas. To 
        find the communities you belong to, click the Communities tab.

        Files ICON**        Files lets you share files with others and see files that others have shared with you. Pin files that
        you want to get back to often, to find them quickly. Leave comments on files, download the latest
        version, or use the co-editing capabilities to make changes to files online. 
                
        To get going right away, click My Notifications or Action Required, to see if there are any messages
        waiting for you. 

        Want to learn more? Click the help button to learn about all the great features available to you! </B></li>
	*<li><B>Verify: Verification point 1- the correct text is displayed at the top of the page</B></li>
	*<li><B>Verify: Verification point 2 - the correct descriptive text and icons are displayed on the page</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/86623BBFFCD5390185257C8400429E76">TTT - ACTIVITY STREAM - NETWORK - 00020 - STATUS UPDATE DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_gettingStarted_textAndIcons() {
		
		ui.startTest();
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);

		// Verify that all text components are displayed in the UI
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.getData().GettingStartedGreeting, Data.getData().CollaborationToolsMsg, 
											Data.getData().HomepageInfoMsg, Data.getData().CommunitiesInfoMsg, Data.getData().FilesInfoMsg,
											Data.getData().MyNotActionReqInfoMsg, Data.getData().HelpInfoMsg}, null, true);
		
		// Verify that all icons are displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.Visitor_HomepageIcon, HomepageUIConstants.Visitor_CommunitiesIcon, HomepageUIConstants.Visitor_FilesIcon},
												null, true);
		ui.endTest();
	}

	/**
	* visitorModel_gettingStarted_navigation() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in as a Standard User</B></li>
	*<li><B>Step: Invite testUser2 as a visitor</B></li>
	*<li><B>Step: Log in as testUser2 (visitor)</B></li>
	*<li><B>Step: The Getting started web page is displayed by default</B></li>
	*<li><B>Step: Select the a) Homepage icon and b) the Homepage Link - Verification point 1</B></li>
	*<li><B>Step: Select the a) Communities icon and b) the Communities Link - Verification point 2</B></li>
	*<li><B>Step: Select the a) File icon and b) the Files Link - Verification point 3</B></li>
	*<li><B>Verify: Verification point 1 Verify the homepage icon/ homepage link brings User 2 to the Homepage/ I'm following view</B></li>
	*<li><B>Verify: Verification point 2  Verify the communities icon/ communities link brings User 2 to communities</B></li>
	*<li><B>Verify: Verification point 3 Verify the files icon/  files link brings User 2 to the files application</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C6EEE5E88D5E1C3885257C84004414AC">TTT - VISITORS - 00015 - GETTING STARTED - HOMEPAGE/ COMMUNITIES/ FILES/ LINKS WORK</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_gettingStarted_navigation() {
		
		ui.startTest();
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Select the Homepage icon and verify that the user is redirected to I'm Following - assert that all steps complete successfully
		boolean redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorHomepageIconAndVerifyImFollowingViewIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
		
		// Select the Homepage link and verify that the user is redirected to I'm Following - assert that all steps complete successfully
		redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorHomepageLinkAndVerifyImFollowingViewIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
		
		// Select the Communities UI icon and verify that the user is redirected to Communities UI - assert that all steps complete successfully
		redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorCommunitiesIconAndVerifyCommunitiesUIIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
				
		// Select the Communities UI link and verify that the user is redirected to Communities UI - assert that all steps complete successfully
		redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorCommunitiesLinkAndVerifyCommunitiesUIIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
				
		// Select the Files UI icon and verify that the user is redirected to Files UI - assert that all steps complete successfully
		redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorFilesIconAndVerifyFilesUIIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
				
		// Select the Files UI link and verify that the user is redirected to Files UI - assert that all steps complete successfully
		redirectSuccessful = UIEvents.navigateToGettingStartedAndClickVisitorFilesLinkAndVerifyFilesUIIsDisplayed(ui);
		HomepageValid.verifyBooleanValuesAreEqual(redirectSuccessful, true);
		
		ui.endTest();
	}

	/**
	* visitorModel_gettingStarted_helpLink() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in as a Standard User</B></li>
	*<li><B>Step: Invite testUser2 as a visitor</B></li>
	*<li><B>Step: Log in as testUser2 (visitor)</B></li>
	*<li><B>Step: On the getting Started page select the Help link</B></li>
	*<li><B>Verify: Verify the User 2 is brought to the Getting Started Help Page. </B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/99858B432773AD7885257C8400493C97">TTT - VISITORS - 00016 - GETTING STARTED - HELP LINK WORKS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_gettingStarted_helpLink() {
		
		ui.startTest();
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Retrieve the handle for the current browser window before clicking on the Help link
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// Click on the Help link in the footer of the Getting Started screen
		UIEvents.navigateToGettingStartedAndClickFooterHelpLink(ui);
		
		// Switch to the now open Help browser window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the user has been brought to the Help page
		HomepageValid.verifyStringValuesAreEqual(driver.getTitle(), Data.getData().helpIBMConnections);
		
		// Close the Help screen and switch focus back to the main browser window again
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}

	/**
	* visitorModel_gettingStarted_myNotificationsActionRequired() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in as a Standard User</B></li>
	*<li><B>Step: Invite testUser2 as a visitor</B></li>
	*<li><B>Step: Log in as testUser2 (visitor)</B></li>
	*<li><B>Step: On the Getting started page select the My Notifications link -Verification point 1</B></li>
	*<li><B>Step: Then Select the Getting Started Link in the side navigation- Verification point 2</B></li>
	*<li><B>Step: On getting started Page select the Action Required link - Verification point 3</B></li>
	*<li><B>Step: On the Action Required page select the Getting started link in the side navigation - Verification point 4 </B></li>
	*<li><B>Verify: Verification point 1:  Verify selecting the my notifications link on the Getting started page takes the visitor to the My Notifications view.</B></li>
	*<li><B>Verify: Verification point 2 & 4:  Verify The Getting Started page will still be accessed by clicking the Getting Started link in the Side Navigation. </B></li>
	*<li><B>Verify: Verification point 3:  Verify selecting the action required link on the Getting started page takes the visitor to the Action Required view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/68718BC624C7F34785257C840047FA2C">TTT - VISITORS - 00017 - GETTING STARTED - MY NOTIFICATIONS/ ACTION REQUIRED LINKS WORK</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_gettingStarted_myNotificationsActionRequired() {
		
		ui.startTest();
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// User 2 will now click on the My Notifications link
		UIEvents.navigateToGettingStartedAndClickVisitorMyNotificationsLink(ui);
		
		// Verify that User 2 has been redirected to the My Notifications view
		HomepageValid.verifyMyNotificationsIsDisplayed(ui);
		
		// User 2 will now click on the Action Required link
		UIEvents.navigateToGettingStartedAndClickVisitorActionRequiredLink(ui);
		
		// Verify that User 2 has been redirected to the Action Required view
		HomepageValid.verifyActionRequiredIsDisplayed(ui);
		
		ui.endTest();
	}
}