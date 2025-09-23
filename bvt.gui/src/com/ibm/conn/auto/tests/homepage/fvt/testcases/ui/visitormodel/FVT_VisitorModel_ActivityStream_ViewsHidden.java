package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
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

public class FVT_VisitorModel_ActivityStream_ViewsHidden extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_ActivityStream_ViewsHidden.class);

	private APIProfilesHandler testUser1Profile, testUser2Profile;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		testUser2Profile = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitorModel_visitor_noDiscoverView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invite testUser2 as a visitor in Connections</B></li>
	*<li><B>Step: testUser2 accept the invite</B></li>
	*<li><B>Step: testUser2 log into the Homepage Activity Stream\ Updates</B></li>
	*<li><B>Verify: Verify there is no Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/50331B8099B0825E85257C8B0051165D">TTT - UI CHANGE - 00010 - VISITOR WILL NOT GET DISCOVER VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_visitor_noDiscoverView() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser2.getDisplayName() + " log into the Homepage Activity Stream / Updates");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		ui.gotoImFollowing();
		
		// Verify that there is no Discover view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.discoverTab}, null, false);
		
		ui.endTest();
	}

	/**
	* visitorModel_visitor_noImFollowingTagsFilter() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invite testUser2 as a visitor in Connections</B></li>
	*<li><B>Step: testUser2 accept the invite</B></li>
	*<li><B>Step: testUser2 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser2 go to I'm Following</B></li>
	*<li><B>Step: testUser2 click to show the dropdown filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify there is no I'm Following / Tags view for the user</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D8CAD135F5C88A7085257C8B00522F1E">TTT - UI CHANGE - 00012 - VISITOR USER WILL NOT GET I'M FOLLOWING TAGGED VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_visitor_noImFollowingTagsFilter() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser2.getDisplayName() + " log into the Homepage Activity Stream");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		
		log.info("INFO: " + testUser2.getDisplayName() + " go to I'm Following");
		ui.gotoImFollowing();
		
		log.info("INFO: Retrieve all possible filter options from the I'm Following filter");
		List<String> listOfFilterOptions = ui.getAllDropDownMenuOptions(HomepageUIConstants.FilterBy);
		boolean foundTags = false;
		for(String filterOption : listOfFilterOptions) {
			if(filterOption.equals(HomepageUIConstants.FilterTags)) {
				foundTags = true;
				break;
			}
		}
		
		// Verify that the Tags filter option was NOT found
		HomepageValid.verifyBooleanValuesAreEqual(foundTags, false);
		
		ui.endTest();
	}

	/**
	* visitorModel_visitor_noActivityStreamProfiles() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invite testUser2 as a visitor in Connections</B></li>
	*<li><B>Step: testUser2 log on to Connections</B></li>
	*<li><B>Step: testUser2 go to My Profile</B></li>
	*<li><B>Verify: User 2 (Visitor) cannot view Activity Stream in profiles</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ED41AD97C54EE0EF85257C8B005BA1D4">TTT - UI CHANGE - 00014 - VISITOR WILL NOT GET AS IN PROFILES</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_visitor_noActivityStreamProfiles() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser2.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		
		log.info("INFO: Navigate to " + testUser2.getDisplayName() + "'s profile page");
		driver.navigate().to(serverURL + "/" + Data.User_profilePage + testUser2Profile.getUUID());
		ui.waitForPageLoaded(driver);
		
		// Verify that the Recent Updates tab and Status Update input frame are NOT displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{ProfilesUIConstants.RecentUpdatesTab, BaseUIConstants.StatusUpdate_iFrame}, null, false);
		
		// Verify that the 'Feeds for these entries' text is NOT displayed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.getData().feedsForTheseEntries}, null, false);
		
		ui.endTest();
	}

	/**
	* visitorModel_standardUser_hasDiscoverView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream \ Updates</B></li>
	*<li><B>Verify: Verify there is a Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4637FDD663CDBE9185257C8B005120F9">TTT - UI CHANGE - 00011 - STANDARD USER WILL GET DISCOVER VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_hasDiscoverView() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into the Homepage Activity Stream / Updates");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		ui.gotoImFollowing();
		
		// Verify that there is an option for the Discover view
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.discoverTab}, null, true);
		
		log.info("INFO: Navigate to the Discover view");
		ui.gotoDiscover();
		
		ui.endTest();
	}

	/**
	* visitorModel_standardUser_hasImFollowingTagsFilter() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into in Connections</B></li>
	*<li><B>Step: testUser1 log into the Homepage Activity Stream</B></li>
	*<li><B>Step: testUser1 go to I'm Following</B></li>
	*<li><B>Step: testUser1 click to show the dropdown filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify there IS A I'm Following / Tags view for the user</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAE2F860EB6DC05B85257C8B00523098">TTT - UI CHANGE - 00013 - STANDARD USER WILL GET I'M FOLLOWING TAGGED VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_hasImFollowingTagsFilter() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into the Homepage Activity Stream");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: " + testUser1.getDisplayName() + " go to I'm Following");
		ui.gotoImFollowing();
		
		log.info("INFO: Retrieve all possible filter options from the I'm Following filter");
		List<String> listOfFilterOptions = ui.getAllDropDownMenuOptions(HomepageUIConstants.FilterBy);
		boolean foundTags = false;
		for(String filterOption : listOfFilterOptions) {
			if(filterOption.equals(HomepageUIConstants.FilterTags)) {
				foundTags = true;
				break;
			}
		}
		
		// Verify that the Tags filter option was found
		HomepageValid.verifyBooleanValuesAreEqual(foundTags, true);
		
		ui.endTest();
	}

	/**
	* visitorModel_standardUser_hasActivityStreamProfiles() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Connections</B></li>
	*<li><B>Step: testUser1 try click on their  username in homepage/ updates</B></li>
	*<li><B>Verify: Standard user CAN view Activity Stream  in profiles</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/405B75FF6E1EA3C185257C8B005BA3BC">TTT - UI CHANGE - 00015 - STANDARD USER WILL GET AS IN PROFILES</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_hasActivityStreamProfiles() {
		
		ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to their own profile page");
		driver.navigate().to(serverURL + "/" + Data.User_profilePage + testUser1Profile.getUUID());
		ui.waitForPageLoaded(driver);
		
		// Verify that the Recent Updates tab and Status Update input frame are NOT displayed
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{ProfilesUIConstants.RecentUpdatesTab, BaseUIConstants.StatusUpdate_iFrame}, null, true);
		
		// Verify that the 'Feeds for these entries' text is NOT displayed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{Data.getData().feedsForTheseEntries}, null, true);
		
		ui.endTest();
	}
}