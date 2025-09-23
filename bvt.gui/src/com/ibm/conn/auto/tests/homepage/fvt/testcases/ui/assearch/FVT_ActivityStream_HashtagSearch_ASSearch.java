package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.assearch;

import java.util.HashMap;
import java.util.Set;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016				                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 *	Author:		Anthony Cox
 *	Date:		15th February 2016
 */

public class FVT_ActivityStream_HashtagSearch_ASSearch extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private APICommunitiesHandler communitiesAPIUser2, communitiesAPIUser3;
	private boolean user3FollowingUser4;
	private GlobalsearchUI globalSearchUI;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private HashMap<String, APIProfilesHandler> statusUpdatesForDeletion = new HashMap<String, APIProfilesHandler>();
	private SearchAdminService searchAdminService;
	private User adminUser, testUser1, testUser2, testUser3, testUser4, testUser5;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		globalSearchUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		
		setListOfAdminUsers(1);
		adminUser = listOfAdminUsers.get(0);
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		
		URLConstants.setServerURL(serverURL);
		searchAdminService = new SearchAdminService();
		
		user3FollowingUser4 = false;
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		globalSearchUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		Set<String> statusUpdateIds = statusUpdatesForDeletion.keySet();
		for(String statusUpdateId : statusUpdateIds) {
			statusUpdatesForDeletion.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
		
		// Delete all of the communities created during the test
		Set<Community> communities = communitiesForDeletion.keySet();
		for(Community community : communities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
		
		if(user3FollowingUser4) {
			// Have User 3 unfollow User 4 again
			ProfileEvents.unfollowUser(profilesAPIUser4, profilesAPIUser3);
		}
	}

	/**
	* hashtag_StatusUpdate_DiscoverView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag</B></li>
	*<li><B>Step: User 2 write a board message on User 4's board with the same hashtag</B></li>
	*<li><B>Step: User 3 go to a private community and add a status update with the same hashtag</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 3 go to Homepage / Discover / All</B></li>
	*<li><b>Step: User 3 open the Activity Stream Search , enter the hashtag used by users 1 ,2 and 3 and click the search icon</b></li>
	*<li><B>Verify: Verify the Discover view is re-rendered</B></li>
	*<li><B>Verify: User 1's and User 2's updates is returned in the results</B></li>
	*<li><b>Verify: User 3's update is not returned in the results</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/511B3865E4612C6385257C70004F73AD">TTT - CR4 - AS SEARCH - 00012 - ONLY EVENTS IN DISCOVER VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void hashtag_StatusUpdate_DiscoverView() {
		
		String testName = ui.startTest();
		
		// Create the hashtag to be used in this test
		String hashtag = ProfileEvents.generateValidHashtag();
		
		// User 1 will now add a status update with a hashtag
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
		statusUpdatesForDeletion.put(user1StatusUpdateId, profilesAPIUser1);
		
		// User 2 will now post a board message to User 4 which includes the hashtag
		String user2BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user2BoardMessageId = ProfileEvents.addBoardMessage(user2BoardMessage, profilesAPIUser2, profilesAPIUser4);
		statusUpdatesForDeletion.put(user2BoardMessageId, profilesAPIUser2);
		
		// User 3 will now create a restricted community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser3, communitiesAPIUser3);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser3);
		
		// User 3 will now post a status update which includes the hashtag to the community
		String user3StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser3, profilesAPIUser3, user3StatusUpdate);
		
		// User 1 will now request for indexing to be performed for the hashtag
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtag, testUser1, serverURL, searchAdminService, adminUser);
		
		// Log in as User 3 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Search for the hashtag using AS Search
		UIEvents.searchUsingASSearch(ui, testUser3, hashtag);
		
		// Verify that User 1's status update and User 2's board message events are displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate, user2BoardMessage}, null, true);
		
		// Verify that User 3's restricted community status update event is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user3StatusUpdate}, null, false);
		
		ui.endTest();
	}
	
	/**
	* hashtag_StatusUpdate_StatusUpdatesView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 follow a public community owned by User 2</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag</B></li>
	*<li><B>Step: User 2 add a community status update to the community User 1 is following</B></li>
	*<li><B>Step: User 3 add a status update with the same hashtag</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 1 go to Homepage / Status Updates / All</B></li>
	*<li><B>Step: User 1 open the Activity Stream Search , enter the hashtag used by user 1 ,2 and 3,  and click search button</B></li>
	*<li><b>Verify: Verify the Status Updates view is re-rendered</b></li>
	*<li><B>Verify: User 2's update is returned in the results</B></li>
	*<li><B>Verify: User 3's update is not returned in the results</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B629F94EADBC1DB385257C70004F7299">TTT - CR4 - AS SEARCH - 00011 - ONLY EVENTS IN STATUS UPDATES VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void hashtag_StatusUpdate_StatusUpdatesView() {
		
		/**
		 * In order for indexing to be requested by a unique user - this test case will use User 2 (as User 1), User 3 (as User 2) and User 4 (as User 3)
		 */
		String testName = ui.startTest();
		
		// Create the hashtag to be used in this test
		String hashtag = ProfileEvents.generateValidHashtag();
		
		// User 1 will now add a status update with a hashtag
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user1StatusUpdate);
		statusUpdatesForDeletion.put(user1StatusUpdateId, profilesAPIUser2);
		
		// User 2 will now create a public community with User 1 added as a follower
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunityWithOneFollower(baseCommunity, testUser2, communitiesAPIUser2, testUser3, communitiesAPIUser3);
		communitiesForDeletion.put(publicCommunity, communitiesAPIUser3);
		
		// User 2 will now post a status update which includes the hashtag to the community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser3, profilesAPIUser3, user2StatusUpdate);
		
		// User 3 will now add a status update with a hashtag
		String user3StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user3StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser4, user3StatusUpdate);
		statusUpdatesForDeletion.put(user3StatusUpdateId, profilesAPIUser4);
			
		// User 1 will now request for indexing to be performed for the hashtag
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtag, testUser2, serverURL, searchAdminService, adminUser);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser2, false);
		
		// Search for the hashtag using AS Search
		UIEvents.searchUsingASSearch(ui, testUser2, hashtag);
		
		// Verify that User 1's status update and User 2's community status update events are displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate, user2StatusUpdate}, null, true);
		
		// Verify that User 3's status update is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user3StatusUpdate}, null, false);
		
		ui.endTest();
	}
	
	/**
	* hashtag_StatusUpdate_ImFollowingView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 follow User 2</B></li>
	*<li><B>Step: User 2 add a status update with a hashtag</B></li>
	*<li><B>Step: User 3 add a status update with the same hashtag</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 1 go to Homepage / I'm Following / All</B></li>
	*<li><B>Step: User 1 open the Activity Stream Search , enter the hashtag used by user 2 and user 3 and click search button</B></li>
	*<li><B>Verify: Verify the I'm Following view is re-rendered</B></li>
	*<li><B>Verify: User 2's updates is returned in the results</B></li>
	*<li><b>Verify: User 3's update is not returned in the results</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BCA8DCFE84B594AC85257C70004F7158">TTT - CR4 - AS SEARCH - 00010 - ONLY EVENTS IN I'M FOLLOWING VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void hashtag_StatusUpdate_ImFollowingView() {
		
		/**
		 * In order for indexing to be requested by a unique user - this test case will use User 3 (as User 1), User 4 (as User 2) and User 5 (as User 3)
		 */
		ui.startTest();
		
		// Create the hashtag to be used in this test
		String hashtag = ProfileEvents.generateValidHashtag();
		
		// User 1 will now follow User 2
		ProfileEvents.followUser(profilesAPIUser4, profilesAPIUser3);
		user3FollowingUser4 = true;
		
		// User 2 will now add a status update with a hashtag
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser4, user2StatusUpdate);
		statusUpdatesForDeletion.put(user2StatusUpdateId, profilesAPIUser4);
		
		// User 3 will now add a status update with a hashtag
		String user3StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtag;
		String user3StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser5, user3StatusUpdate);
		statusUpdatesForDeletion.put(user3StatusUpdateId, profilesAPIUser5);
		
		// User 2 will now request for indexing to be performed for the hashtag
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtag, testUser4, serverURL, searchAdminService, adminUser);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Search for the hashtag using AS Search
		UIEvents.searchUsingASSearch(ui, testUser4, hashtag);
		
		// Verify that User 2's status update event is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2StatusUpdate}, null, true);
		
		// Verify that User 3's status update event is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user3StatusUpdate}, null, false);
		
		ui.endTest();
	}
}