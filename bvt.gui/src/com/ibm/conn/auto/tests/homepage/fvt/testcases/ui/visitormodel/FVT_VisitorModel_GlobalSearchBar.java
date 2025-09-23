package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
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
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_GlobalSearchBar extends SetUpMethods2{
	
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
	* visitorModel_globalSearchBar_noProfileFilter() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Homepage</B></li>
	*<li><B>Step: testUser1 invite a guest</B></li>
	*<li><B>Step: testUser2 who is the guest accept the invite</B></li>
	*<li><B>Step: testUser2 log into Homepage</B></li>
	*<li><B>Step: testUser2 check the Global Search Bar</B></li>
	*<li><B>Verify: Verify the guest user does not get the "Profile" filter in the global search bar</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A3A3A9C568351A2985257C74003EF2B5">TTT - GUEST/VISITOR USER DOES NOT SEE COMPANY DIRECTORY AS AN OPTION FOR SEARCHING</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_globalSearchBar_noProfileFilter() {
		
		ui.startTest();
		
		// Log in to Homepage as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Click open the global search bar component and verify that the "Profiles" option is NOT displayed within
		boolean profilesIsDisplayed = ProfileEvents.openGlobalSearchBarAndSearchForOption(ui, "Profiles");
		HomepageValid.verifyBooleanValuesAreEqual(profilesIsDisplayed, false);
		
		ui.endTest();
	}
}