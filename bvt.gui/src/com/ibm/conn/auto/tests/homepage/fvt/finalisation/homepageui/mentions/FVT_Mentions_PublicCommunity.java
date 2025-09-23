package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.mentions;

import java.util.ArrayList;

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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

public class FVT_Mentions_PublicCommunity extends SetUpMethods2 {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community publicCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 7 unique users are chosen from the CSV file
		ArrayList<User> listOfUsers = new ArrayList<User>();
		do {
			User currentUser = cfg.getUserAllocator().getUser(this);
			int index = 0;
			boolean userAlreadyChosen = false;
			while(index < listOfUsers.size() && userAlreadyChosen == false) {
				if(listOfUsers.get(index).getDisplayName().equals(currentUser.getDisplayName())) {
					userAlreadyChosen = true;
				}
				index ++;
			}
			if(userAlreadyChosen == false) {
				listOfUsers.add(currentUser);
			}
		} while(listOfUsers.size() < 7);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		testUser4 = listOfUsers.get(3);
		testUser5 = listOfUsers.get(4);
		testUser6 = listOfUsers.get(5);
		testUser7 = listOfUsers.get(6);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		profilesAPIUser4 = new APIProfilesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		profilesAPIUser5 = new APIProfilesHandler(serverURL, testUser5.getAttribute(cfg.getLoginPreference()), testUser5.getPassword());
		profilesAPIUser6 = new APIProfilesHandler(serverURL, testUser6.getAttribute(cfg.getLoginPreference()), testUser6.getPassword());
		profilesAPIUser7 = new APIProfilesHandler(serverURL, testUser7.getAttribute(cfg.getLoginPreference()), testUser7.getPassword());
		
		// Log in to Communities as User 1 - this will ensure that the community is created successfully when this class is run against G2, G3 etc.
		LoginEvents.loginToCommunities(ui, testUser1, false);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// Return to the Home screen and logout
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Close the current browser window instance
		UIEvents.closeCurrentBrowserWindow(ui);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	 * test_MultipleMentionsLinks_CommunityStatusUpdate_Posted() 
	 *<ul>
	 *<li><B>1: Log into a public community you own</B></li>
	 *<li><B>2: Go to the status update</B></li>
	 *<li><B>3: Add a status update with more than 5 user @mentioned</B></li>
	 *<li><B>Verify: Verify that the all the @mentioned appear correctly and all get notified about it in there Homepage Activity Stream</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B39DAA920282AF3385257BF6006C717F">@Mentions - 00003 - User can add multiple @mentions</a></li>
	 *</ul>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_MultipleMentionsLinks_CommunityStatusUpdate_Posted() {
		
		ui.startTest();
		
		// Create all of the Mentions instances for all of the users to be mentioned
		Mentions user2Mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		Mentions user3Mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, "", "");
		Mentions user4Mentions = MentionsBaseBuilder.buildBaseMentions(testUser4, profilesAPIUser4, serverURL, "", "");
		Mentions user5Mentions = MentionsBaseBuilder.buildBaseMentions(testUser5, profilesAPIUser5, serverURL, "", "");
		Mentions user6Mentions = MentionsBaseBuilder.buildBaseMentions(testUser6, profilesAPIUser6, serverURL, "", "");
		Mentions user7Mentions = MentionsBaseBuilder.buildBaseMentions(testUser7, profilesAPIUser7, serverURL, "", "");
		Mentions[] allUserMentions = { user2Mentions, user3Mentions, user4Mentions, user5Mentions, user6Mentions, user7Mentions };
		
		// Log in as User 1 and post a community status update with mentions to Users 2 to 7
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMultipleMentions(publicCommunity, baseCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, allUserMentions, false);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Ensure User 1 has been logged out by closing the browser window instance
		UIEvents.closeCurrentBrowserWindow(ui);
		
		// Create the news stories to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = "@" + testUser2.getDisplayName() + " @" + testUser3.getDisplayName() + " @" + testUser4.getDisplayName() + " @" + testUser5.getDisplayName()
								+ " @" + testUser6.getDisplayName() + " @" + testUser7.getDisplayName();
		
		User[] usersToLogin = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7 };
		boolean preserveInstance = false;
		for(User userToLogin : usersToLogin) {
			// Log in as the specified user and go to the Mentions view
			LoginEvents.loginAndGotoMentions(ui, userToLogin, preserveInstance);
			
			// Verify that the mentions event and mentions text are displayed in the Mentions view
			HomepageValid.verifyItemsInAS(ui, driver, new String[] {mentionedYouEvent, mentionsText}, null, true);
			
			// Log out from Connections
			LoginEvents.logout(ui);
			
			if(preserveInstance == false) {
				preserveInstance = true;
			}
		}
		ui.endTest();
	}
}