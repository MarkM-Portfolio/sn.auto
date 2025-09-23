package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.mynotifications;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
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
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	3rd October 2016
 */

public class FVT_MicroblogEvents_Rollup extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 4 unique users are chosen from the CSV file
		ArrayList<User> listOfUsers = new ArrayList<User>();
		listOfUsers.add(cfg.getUserAllocator().getUser(this));
		
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
		} while(listOfUsers.size() < 4);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		testUser4 = listOfUsers.get(3);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		communitiesAPIUser4 = new APICommunitiesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		profilesAPIUser4 = new APIProfilesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		// User 1 will now create a moderated community with Users 2, 3 and 4 added as members and with User 2 added as a follower
		User membersList[] = { testUser2, testUser3, testUser4 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollower(baseCommunity, testUser1, communitiesAPIUser1, membersList, testUser2, communitiesAPIUser2);
	
		int index = 0;
		User[] loginList = { testUser1, testUser2, testUser3, testUser4 };
		APICommunitiesHandler[] apiLoginList = { communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4 };
		while(index < loginList.length) {
			// Log in as the specified user - critical step for all status update and comment-related API calls to work correctly on G2, G3 etc.
			CommunityEvents.loginAndNavigateToCommunity(moderatedCommunity, baseCommunity, ui, uiCo, loginList[index], apiLoginList[index], false);
			
			// Log out of Connections
			LoginEvents.gotoHomeAndLogout(ui);
			
			// Close the current browser window instance - critical step for the remaining login attempts to work when run on G2, G3 etc.
			UIEvents.closeCurrentBrowserWindow(ui);
			
			index ++;
		}
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_ModeratedCommunity_StatusUpdate_Comments_Rollup() 
	*<ul>
	*<li><B>1: User 1 log into a Moderated Community you own</B></li>
	*<li><b>2: User 2 follow the Community</B></li>
	*<li><B>3: User 1 add a status update in the community</B></li>
	*<li><B>4: User 1 comment on the status update</B></li>
	*<li><B>5: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>6: User 2 log in to Homepage</B></li>
	*<li><B>7: User 2 comment on the status update</B></li>
	*<li><B>8: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>9: User 3 and User 4 comment on the status update</B></li>
	*<li><B>10: User 1 comment again</B></li>
	*<li><B>11: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your message in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others commented on your message in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81CAC1D0640E2FF185257DEA00563CA8">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00011 - COMMENT ON YOUR STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvt_final_onprem", "fvt_final_cloud"})
	public void test_ModeratedCommunity_StatusUpdate_Comments_Rollup() {

		ui.startTest();
		
		// User 1 will now post a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will now post their first comment to their status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser1, user1StatusUpdateId, user1Comment1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusUpdateEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_You(ui, baseCommunity.getName());
		
		// Verify that the comment on status update event and User 1's first comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusUpdateEvent, user1StatusUpdate, user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment);
		
		// Create the news story to be verified
		commentOnStatusUpdateEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_UserAndYou(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on status update event, User 2's comment and User 1's first comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusUpdateEvent, user1StatusUpdate, user2Comment, user1Comment1}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the status update
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser3, user1StatusUpdateId, user3Comment);
		
		// User 4 will now post a comment to the status update
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser4, user1StatusUpdateId, user4Comment);
		
		// User 1 will now post their second comment to their status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser1, user1StatusUpdateId, user1Comment2);
		
		// Create the news story to be verified
		commentOnStatusUpdateEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_YouAndMany(ui, "3", baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status update event, User 4's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusUpdateEvent, user1StatusUpdate, user4Comment, user1Comment2}, filter, true);
			
			// Verify that User 1's first comment, User 2's comment and User 3's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment, user3Comment}, null, false);
		}
		ui.endTest();
	}	
}