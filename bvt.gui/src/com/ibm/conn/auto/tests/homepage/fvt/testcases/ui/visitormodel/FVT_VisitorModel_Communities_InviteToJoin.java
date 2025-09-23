package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;
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

public class FVT_VisitorModel_Communities_InviteToJoin extends SetUpMethods2{
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private HomepageUI ui;	
	private String serverURL;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);	

		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		do {
			testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} while(testUser1.getDisplayName().equals(testUser2.getDisplayName()));
		testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());		
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());		
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove all of the communities created during the tests
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		
		for(Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}

	/**
	* visitorModel_visitor_inviteToJoin_privateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community</B></li>
	*<li><B>Step: testUser1 invites  a visitor (testUser2) to join the community</B></li>
	*<li><B>Step: testUser2 (visitor) logs on</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me</B></li>
	*<li><B>Verify: Action links on the Invite to join notification notification having the following order - 1) Join this community 2) Decline this invitation 3) Save this</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/490705EF078CFF6B85257C8B0044DD40">TTT - AS - MY NOTIFICATIONS - FOR ME - 00062 - VISITORS - PRIVATE COMMUNITY - INVITE TO JOIN</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_visitor_inviteToJoin_privateCommunity() {
		
		/**
		 * In this test case, User 3 acts as User 2 as User 3 is the only configured visitor user
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a restricted visitor model community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser1);
		
		// User 1 will now invite User 2 (a visitor user) to join the community
		CommunityEvents.inviteUserToJoinCommunity(restrictedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser3);
		
		// Log in as User 2 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser3, false);
		
		// Verify that the community invitation and all relevant links are displayed in the correct order as expected
		boolean inviteDisplayedCorrectly = CommunityEvents.verifyCommunityInviteIsDisplayedInMyNotifications(ui, driver, testUser1, restrictedCommunity);
		HomepageValid.verifyBooleanValuesAreEqual(inviteDisplayedCorrectly, true);
		
		ui.endTest();
	}	

	/**
	* visitorModel_standardUser_inviteToJoin_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a public community</B></li>
	*<li><B>Step: testUser1 invites a standard user (testUser2) to join the community</B></li>
	*<li><B>Step: testUser2 (standard user) logs on</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me</B></li>
	*<li><B>Verify: Action links on the Invite to join notification notification having the following order - 1) Join this community 2) Decline this invitation 3) Save this</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C82EB49CF1016C9185257C8B0044DE67">TTT - AS - MY NOTIFICATIONS - FOR ME - 00063 - STANDARD USER- PUBLIC COMMUNITY - INVITE TO JOIN</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_inviteToJoin_publicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a standard public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(publicCommunity, communitiesAPIUser1);
		
		// User 1 will now invite User 2 (a standard user) to join the community
		CommunityEvents.inviteUserToJoinCommunity(publicCommunity, testUser1, communitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Verify that the community invitation and all relevant links are displayed in the correct order as expected
		boolean inviteDisplayedCorrectly = CommunityEvents.verifyCommunityInviteIsDisplayedInMyNotifications(ui, driver, testUser1, publicCommunity);
		HomepageValid.verifyBooleanValuesAreEqual(inviteDisplayedCorrectly, true);
		
		ui.endTest();
	}		

	/**
	* visitorModel_standardUser_inviteToJoin_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a moderated community</B></li>
	*<li><B>Step: testUser1 invites  a standard user (testUser2) to join the community</B></li>
	*<li><B>Step: testUser2 (standard user) logs on</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me</B></li>
	*<li><B>Verify: Action links on the Invite to join notification notification having the following order - 1) Join this community 2) Decline this invitation 3) Save this</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/256382DE68EEFFDB85257C8B0044DFA8">TTT - AS - MY NOTIFICATIONS - FOR ME - 00064 - STANDARD USER- MODERATED COMMUNITY - INVITE TO JOIN</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_inviteToJoin_modCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a standard moderated community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);
		Community moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(moderatedCommunity, communitiesAPIUser1);
		
		// User 1 will now invite User 2 (a standard user) to join the community
		CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Verify that the community invitation and all relevant links are displayed in the correct order as expected
		boolean inviteDisplayedCorrectly = CommunityEvents.verifyCommunityInviteIsDisplayedInMyNotifications(ui, driver, testUser1, moderatedCommunity);
		HomepageValid.verifyBooleanValuesAreEqual(inviteDisplayedCorrectly, true);
		
		ui.endTest();
	}

	/**
	* visitorModel_standardUser_inviteToJoin_privateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community</B></li>
	*<li><B>Step: testUser1 invites a standard (testUser2) to join the community</B></li>
	*<li><B>Step: testUser2 (visitor) logs on</B></li>
	*<li><B>Step: testUser2 goes to Home/ My Notifications/ For Me</B></li>
	*<li><B>Verify: Action links on the Invite to join notification notification having the following order - 1) Join this community 2) Decline this invitation 3) Save this</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A555748F46AF24BD85257C8B0045321D">TTT - AS - MY NOTIFICATIONS - FOR ME - 00065 - STANDARD USER- PRIVATE COMMUNITY - INVITE TO JOIN</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitorModel_standardUser_inviteToJoin_privateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a standard restricted community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser1);
		
		// User 1 will now invite User 2 (a standard user) to join the community
		CommunityEvents.inviteUserToJoinCommunity(restrictedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Verify that the community invitation and all relevant links are displayed in the correct order as expected
		boolean inviteDisplayedCorrectly = CommunityEvents.verifyCommunityInviteIsDisplayedInMyNotifications(ui, driver, testUser1, restrictedCommunity);
		HomepageValid.verifyBooleanValuesAreEqual(inviteDisplayedCorrectly, true);
		
		ui.endTest();		
	}
}