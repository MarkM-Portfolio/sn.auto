package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

public class FVT_VisitorModel_Communities_SharedExternally_MemberRemoved extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_Communities_SharedExternally_MemberRemoved.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler testUser2Profile;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		do {
			testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} while(testUser1.getDisplayName().equalsIgnoreCase(testUser2.getDisplayName()));
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		testUser2Profile = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}
	
	/**
	* visitorModel_standardUser_sharedExternallyHeader_privateCommunity_memberRemoved() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community that can have visitors as members and testUser2 (employee) is a member</B></li>
	*<li><B>Step: testUser1 removes testUser2 from the community</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me / All</B></li>
	*<li><B>Step: testUser2 goes to the story of the community membership</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title of the community membership</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/638AE148A0ED2BB985257C8C00386AD1">TTT - VISITORS - ACTIVITY STREAM - 00028- SHARED EXTERNALLY HEADER - STANDARD USER - REMOVED FROM COMMUNITY- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_sharedExternallyHeader_privateCommunity_memberRemoved() {
		
		String testName = ui.startTest();
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communitiesAPIUser1.addMemberToCommunity(testUser2, restrictedCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now remove " + testUser2.getDisplayName() + " from the community");
		communitiesAPIUser1.removeMemberFromCommunity(restrictedCommunity, testUser2Profile);
		
		log.info("INFO: " + testUser2.getDisplayName() + " og into Homepage / My Notifications / For Me / All");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser2);
		ui.gotoMyNotifications();
		
		// Assign the news stories to be verified as having the "Shared Externally" header displayed alongside them
		String addedNewsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, baseCommunity.getName(), null, testUser1.getDisplayName());
		String removedNewsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_REMOVE_MEMBER_FOR_ME, baseCommunity.getName(), null, testUser1.getDisplayName());
		
		// Verify that all components are displayed in the All filter
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{addedNewsStory, baseCommunity.getDescription()}, HomepageUIConstants.FilterAll, true);
		verifySharedExternallyIsDisplayed(addedNewsStory, null);
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{removedNewsStory}, null, true);
		verifySharedExternallyIsDisplayed(removedNewsStory, null);
		
		// Verify that all components are displayed in the Communities filter
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{addedNewsStory, baseCommunity.getDescription()}, HomepageUIConstants.FilterCommunities, true);
		verifySharedExternallyIsDisplayed(addedNewsStory, null);
		
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{removedNewsStory}, null, true);
		verifySharedExternallyIsDisplayed(removedNewsStory, null);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	 * Verifies that the Shared Externally components are displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should be displayed
	 */
	private void verifySharedExternallyIsDisplayed(String newsStoryContent, String filter) {
		
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
		
		// Verify that both selectors are NOT displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, filter, true);
	}
}