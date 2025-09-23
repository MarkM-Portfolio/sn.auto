package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui;

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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
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
 *	Date:		10th February 2016
 *
 * PLEASE NOTE: This class has been designed to support up to 4 VM's when this class is being executed on the grid.
 * 				Certain tests have been prioritised so that they will be run in an expected order with indexing requests executed by expected users.
 * 				This has been done to ensure that indexing requests do NOT clash when these tests are run on multiple VM's.
 * 
 * Priority = 1 (4 tests): Users 1, 2, 3, 4, 5, 6, 7 and 8 are all asked to perform indexing
 * Priority = 2 (4 tests): Users 1, 2, 3 and 4 are all asked to perform indexing - by the time these tests are run, indexing requests from previous tests will be finished
 * Priority = 3 (2 tests): Users 5 and 6 are all asked to perform indexing - by the time these tests are run, all indexing requests from previous tests will be finished
 */

public class FVT_ActivityStream_HashtagSearch extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser3, communitiesAPIUser5;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8;
	private GlobalsearchUI globalSearchUI;
	private HashMap<Community, APICommunitiesHandler> communitiesToBeDeleted = new HashMap<Community, APICommunitiesHandler>();
	private HashMap<String, APIProfilesHandler> statusUpdatesToBeDeleted = new HashMap<String, APIProfilesHandler>();
	private HashMap<APIProfilesHandler, APIProfilesHandler> usersToUnfollow = new HashMap<APIProfilesHandler, APIProfilesHandler>();
	private SearchAdminService searchAdminService;
	private User adminUser, testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		globalSearchUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		
		setListOfAdminUsers(1);
		adminUser = listOfAdminUsers.get(0);
		
		setListOfStandardUsers(9);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);
		profilesAPIUser8 = initialiseAPIProfilesHandlerUser(testUser8);
		
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		communitiesAPIUser5 = initialiseAPICommunitiesHandlerUser(testUser5);
		
		// Set up the search admin service
		URLConstants.setServerURL(serverURL);
		searchAdminService = new SearchAdminService();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		globalSearchUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have all users unfollow any users they followed during the tests
		Set<APIProfilesHandler> setOfUsers = usersToUnfollow.keySet();
		for(APIProfilesHandler user : setOfUsers) {
			usersToUnfollow.get(user).unfollowUser(usersToUnfollow.get(user), user);
		}
		
		// Have all users delete all status updates created during the tests
		Set<String> setOfStatusUpdates = statusUpdatesToBeDeleted.keySet();
		for(String statusUpdateId : setOfStatusUpdates) {
			statusUpdatesToBeDeleted.get(statusUpdateId).deleteBoardMessage(statusUpdateId);
		}
		
		// Have all users delete all of the communities created during the tests
		Set<Community> setOfCommunities = communitiesToBeDeleted.keySet();
		for(Community community : setOfCommunities) {
			communitiesToBeDeleted.get(community).deleteCommunity(community);
		}
	}
	
	/**
	* hashtagSearch_StatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 2 log into Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: User 2 go to the story of the update added</B></li>
	*<li><B>Step: User 2 click the hashtag</B></li>
	*<li><B>Verify: Verify at the top of the screen the tag appears beside the text "Matching"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/41460F9CACC5D14285257C610046CC84">TTT - CR4 - AS SEARCH - 00001 - HASHTAG CLICKED PUTS IT AT THE TOP OF THE STREAM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void hashtag_StatusUpdate() {
		
		ui.startTest();
		
		// User 1 will now post a status update with a valid hashtag
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser1;
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
		statusUpdatesToBeDeleted.put(user1StatusUpdateId, profilesAPIUser1);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser1, serverURL, searchAdminService, adminUser);
		
		// Log in as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 2 will now click on the unique hashtag used by User 1
		UIEvents.clickNewsStoryHashtag(ui, testUser2, user1StatusUpdate, hashtagUser1);
		
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the tag used by User 1 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
		
		// Verify that the status update posted by User 1 is displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate}, true);
		
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
	
	/**
	* hashtag_StatusUpdate_RemoveFilter() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 2 log into Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: User 2 go to the story of the update added</B></li>
	*<li><B>Step: User 2 click the hashtag</B></li>
	*<li><B>Step: User 2 click the "X" in the hashtag filter</B></li>
	*<li><B>Verify: Verify the filter is removed</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/734C6760F68C3F6A85257C610048B5C0">TTT - CR4 - AS SEARCH - 00005 - HASHTAG FILTER "X" CLICKED REMOVES THE FILTER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void hashtag_StatusUpdate_RemoveFilter() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 2 (as User 1) and User 3 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now post a status update to their own profile
		String user1StatusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user1StatusUpdate1);
		statusUpdatesToBeDeleted.put(user1StatusUpdate1Id, profilesAPIUser2);
		
		// User 1 will now post a status update with a valid hashtag
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String user1StatusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser1;
		String user1StatusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, user1StatusUpdate2);
		statusUpdatesToBeDeleted.put(user1StatusUpdate2Id, profilesAPIUser2);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser2, serverURL, searchAdminService, adminUser);
				
		// Log in as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
				
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 2 will now click on the unique hashtag used by User 1
		UIEvents.clickNewsStoryHashtag(ui, testUser3, user1StatusUpdate2, hashtagUser1);
				
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the tag used by User 1 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
				
		// Verify that the status update with hashtag is displayed while the status update which did NOT include the hashtag is NOT displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate2}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate1}, false);
		
		// Remove the filter from the global search results screen
		UIEvents.removeFilteredTagInGlobalSearchUI(ui, testUser3, hashtagUser1);
		
		// Verify that the tag used by User 1 is NOT displayed (ie. the filter has been removed)
		HomepageValid.verifyFilteredTagIsNotDisplayedInGlobalSearchUI(ui, hashtagUser1);
		
		// Verify that both status updates are now displayed now that the filter has been removed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate1, user1StatusUpdate2}, true);
		
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_OnlyHashtagEventsInView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 2 log into Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: User 2 go to the story of the update added</B></li>
	*<li><B>Step: User 2 click the hashtag</B></li>
	*<li><B>Verify: Verify the Activity Stream is re-rendered with only events with that hashtag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0F24C735F9A6BEF785257C6100447966">TTT - CR4 - AS SEARCH - 00020 - HASHTAG CLICKED IN STATUS UPDATE IN THE AS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void hashtag_StatusUpdate_OnlyHashtagEventsInView() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 3 (as User 1) and User 4 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now post a status update to their own profile
		String user1StatusUpdate1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdate1Id = ProfileEvents.addStatusUpdate(profilesAPIUser3, user1StatusUpdate1);
		statusUpdatesToBeDeleted.put(user1StatusUpdate1Id, profilesAPIUser3);
		
		// User 1 will now post a status update with a valid hashtag
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String user1StatusUpdate2 = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser1;
		String user1StatusUpdate2Id = ProfileEvents.addStatusUpdate(profilesAPIUser3, user1StatusUpdate2);
		statusUpdatesToBeDeleted.put(user1StatusUpdate2Id, profilesAPIUser3);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser3, serverURL, searchAdminService, adminUser);
		
		// Log in as User 2 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser4, false);
				
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 2 will now click on the unique hashtag used by User 1
		UIEvents.clickNewsStoryHashtag(ui, testUser4, user1StatusUpdate2, hashtagUser1);
				
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the tag used by User 1 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
				
		// Verify that the status update which included the hashtag is displayed while the status update which did NOT include the hashtag is NOT displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate2}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate1}, false);
				
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}

	/**
	* hashtag_BoardMessage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Profiles</B></li>
	*<li><B>Step: User 1 go to the Profile of User 2</B></li>
	*<li><B>Step: User 1 add a board message with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 1 log into Homepage / I'm Following / Status Updates</B></li>
	*<li><B>Step: User 1 go to the story of the update added</B></li>
	*<li><B>Step: User 1 click the hashtag</B></li>
	*<li><B>Verify: Verify the Activity Stream is re-rendered with only events with that hashtag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B9D82ED952EA179C85257C610045A6D7">TTT - CR4 - AS SEARCH - 00021 - HASHTAG CLICKED IN A BOARD MESSAGE IN THE AS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void hashtag_StatusUpdate_BoardMessage() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 4 (as User 1)
		 */
		ui.startTest();
		
		// User 1 will now post a board message with a valid hashtag to User 2's profile
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser1;
		String user1BoardMessageId = ProfileEvents.addBoardMessage(user1BoardMessage, profilesAPIUser4, profilesAPIUser5);
		statusUpdatesToBeDeleted.put(user1BoardMessageId, profilesAPIUser4);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser4, serverURL, searchAdminService, adminUser);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser4, false);
		
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 2 will now click on the unique hashtag used by User 1
		String postedBoardMessageEvent = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser5.getDisplayName(), testUser4.getDisplayName());
		UIEvents.clickNewsStoryDescriptionHashtag(ui, testUser4, postedBoardMessageEvent, user1BoardMessage, hashtagUser1);
		
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the tag used by User 1 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
		
		// Verify that the status update posted by User 1 is displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1BoardMessage}, true);
		
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a Public Community</B></li>
	*<li><B>Step: User 1 add a community status update with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 1 log into Homepage / Status Updates / Communities</B></li>
	*<li><B>Step: User 1 go to the story of the update added</B></li>
	*<li><B>Step: User 1 click the hashtag</B></li>
	*<li><B>Verify: Verify the Activity Stream is re-rendered with only events with that hashtag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/512A604E235B23E585257C610045A7F7">TTT - CR4 - AS SEARCH - 00022 - HASHTAG CLICKED IN A COMMUNITY STATUS UPDATE IN THE AS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 3)
	public void hashtag_StatusUpdate_PublicCommunity() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 5 (as User 1)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser5, communitiesAPIUser5);
		communitiesToBeDeleted.put(publicCommunity, communitiesAPIUser5);
		
		// User 1 will now post a community status update with valid hashtag
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String user1CommunityStatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser1;
		CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser5, profilesAPIUser5, user1CommunityStatusUpdate);
		
		// User 1 will now post a status update without a hashtag to their own profile
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser5, user1StatusUpdate);
		statusUpdatesToBeDeleted.put(user1StatusUpdateId, profilesAPIUser5);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser5, serverURL, searchAdminService, adminUser);
				
		// Log in as User 1 and go to Status Updates
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser5, false);
				
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 1 will now click on the unique hashtag used by User 1
		String postedCommunityStatusUpdateEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, publicCommunity.getTitle(), testUser5.getDisplayName());
		UIEvents.clickNewsStoryDescriptionHashtag(ui, testUser5, postedCommunityStatusUpdateEvent, user1CommunityStatusUpdate, hashtagUser1);
				
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the community status update posted by User 1 is displayed while the profile status update which did NOT include the hashtag is NOT displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1CommunityStatusUpdate}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate}, false);
				
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 3 follow User 2</B></li>
	*<li><B>Step: User 2 log into Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: User 2 add a comment to status update from step 2 with a hashtag in the format of #<text></B></li>
	*<li><B>Step: Wait 15 mins to let indexing happen</B></li>
	*<li><B>Step: User 3 log into Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 3 go to the story of the comment added</B></li>
	*<li><B>Step: User 3 click the hashtag</B></li>
	*<li><B>Verify: Verify the Activity Stream is re-rendered with only events with that hashtag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/222B01C0A5466DFD85257C610045A932">TTT - CR4 - AS SEARCH - 00023 - HASHTAG CLICKED IN A COMMENT IN THE AS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void hashtag_StatusUpdate_Comment() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 6 (as User 1), User 7 (as User 2) and User 8 (as User 3)
		 */
		ui.startTest();
		
		// User 1 will now post a status update to their own profile - this status update will be commented on
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser6, user1StatusUpdate);
		statusUpdatesToBeDeleted.put(user1StatusUpdateId, profilesAPIUser6);
		
		// User 2 will now post a status update to their own profile - this status update will NOT be commented on
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser7, user2StatusUpdate);
		statusUpdatesToBeDeleted.put(user2StatusUpdateId, profilesAPIUser7);
		
		// User 3 will now follow User 2
		ProfileEvents.followUser(profilesAPIUser7, profilesAPIUser8);
		usersToUnfollow.put(profilesAPIUser7, profilesAPIUser8);
		
		// User 2 will now add a comment to the status update which contains a valid hashtag
		String hashtagUser2 = ProfileEvents.generateValidHashtag();
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand() + " #" + hashtagUser2;
		ProfileEvents.addStatusUpdateComment(user1StatusUpdateId, user2Comment, profilesAPIUser7);
		
		// Perform indexing for the hashtag used by User 2
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser2, testUser7, serverURL, searchAdminService, adminUser);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser8, false);
		
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 3 will now click on the unique hashtag used by User 2
		String commentEvent = ProfileNewsStories.getCommentedOnAnotherUsersMessageNewsStory_User(ui, testUser6.getDisplayName(), testUser7.getDisplayName());
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser8, commentEvent, user1StatusUpdate, user2Comment, hashtagUser2);
				
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the tag used by User 2 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser2);
				
		// Verify that the status update posted by User 1 is displayed while the status update posted by User 2 is NOT displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user2StatusUpdate}, false);		
		
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		ui.endTest();
	}

	/**
	* hashtag_MultipleTags_StatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag</B></li>
	*<li><B>Step: User 2 add a status update with 2 hashtags (1 from point 1)</B></li>
	*<li><B>Step: User 3 add a public community update with 3 tags (2 from point 2)</B></li>
	*<li><B>Step: User 4 add a board message using 4 tags (3 from point 3)</B></li>
	*<li><B>Step: User 5 add a comment to a status update adding 5 tags (4 from point 4)</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 6 go to Homepage / Discover / All</B></li>
	*<li><B>Step: User 6 click all the hashtags in the comment from User 5 - verification point 1</B></li>
	*<li><B>Step: User 6 remove the tag that is not in User 4's board message - verification point 2</B></li>
	*<li><B>Step: User 6 remove the tag that is not in User 3's community update - verification point 3</B></li>
	*<li><B>Step: User 6 remove the tag that is not in User 2's status update- verification point 4</B></li>
	*<li><B>Step: User 6 remove the tag that is not in User 1's status update- verification point 5</B></li>
	*<li><B>Verify: Verify the User 5's comment event appears in the results</B></li>
	*<li><B>Verify: Verify User 5's and User 4's updates is now returned</B></li>
	*<li><B>Verify: Verify User 5's, User 4's and User 3's  updates is now returned</B></li>
	*<li><B>Verify: Verify User 5's, User 4's, User 3's and User 2's  updates is now returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B11BEA452626E0E685257C70005A42BA">TTT - CR4 - AS SEARCH - 00008 - SEARCH CAN BE ON MULTIPLE TAGS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void hashtag_MultipleTags_StatusUpdate() {
		
		String testName = ui.startTest();
		
		// Generate five hashtags to be used in the actions performed by each individual user
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		String hashtagUser2 = ProfileEvents.generateValidHashtag();
		String hashtagUser3 = ProfileEvents.generateValidHashtag();
		String hashtagUser4 = ProfileEvents.generateValidHashtag();
		String hashtagUser5 = ProfileEvents.generateValidHashtag();
		
		// Create the hashtag strings to be appended to all of the status updates / comments in the test case
		String hashtagsUser1 = "#" + hashtagUser1;
		String hashtagsUser2 = "#" + hashtagUser2 + " " + hashtagsUser1;
		String hashtagsUser3 = "#" + hashtagUser3 + " " + hashtagsUser2;
		String hashtagsUser4 = "#" + hashtagUser4 + " " + hashtagsUser3;
		String hashtagsUser5 = "#" + hashtagUser5 + " " + hashtagsUser4;
		
		// User 1 will now post a status update with a hashtag
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " " + hashtagsUser1;
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
		statusUpdatesToBeDeleted.put(user1StatusUpdateId, profilesAPIUser1);
		
		// User 2 will now post a status update with 2 hashtags (including the hashtag used by User 1)
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " " + hashtagsUser2;
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		statusUpdatesToBeDeleted.put(user2StatusUpdateId, profilesAPIUser2);
		
		// User 3 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser3, communitiesAPIUser3);
		communitiesToBeDeleted.put(publicCommunity, communitiesAPIUser3);
		
		// User 3 will now post a status update with 3 hashtags (including the 2 hashtags used by User 2) in the community
		String user3CommunityStatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " " + hashtagsUser3;
		String user3CommunityStatusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser3, profilesAPIUser3, user3CommunityStatusUpdate);
		statusUpdatesToBeDeleted.put(user3CommunityStatusUpdateId, profilesAPIUser3);
		
		// User 4 will now post a board message with 4 hashtags (including the 3 hashtags used by User 3) to User 1
		String user4BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " " + hashtagsUser4;
		String user4BoardMessageId = ProfileEvents.addBoardMessage(user4BoardMessage, profilesAPIUser4, profilesAPIUser1);
		statusUpdatesToBeDeleted.put(user4BoardMessageId, profilesAPIUser4);
		
		// User 5 will now post a status update to be commented on
		String user5StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user5StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser5, user5StatusUpdate);
		statusUpdatesToBeDeleted.put(user5StatusUpdateId, profilesAPIUser5);
		
		// User 5 will now post a comment to the status update with 5 hashtags (including the 4 hashtags used by User 4)
		String user5Comment = Data.getData().commonComment + Helper.genStrongRand() + " " + hashtagsUser5;
		ProfileEvents.addStatusUpdateComment(user5StatusUpdateId, user5Comment, profilesAPIUser5);
		
		// Perform indexing for each of the hashtags used by each of the users
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser1, serverURL, searchAdminService, adminUser);
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser2, testUser2, serverURL, searchAdminService, adminUser);
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser3, testUser3, serverURL, searchAdminService, adminUser);
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser4, testUser4, serverURL, searchAdminService, adminUser);
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser5, testUser5, serverURL, searchAdminService, adminUser);
		
		// Log in as User 6 and navigate to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser6, false);
		
		// Get the handle value for the main browser window before clicking on any of the hashtags
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// User 6 will now click on the unique hashtag used by User 5 (ie. this hashtag will NOT have been used by any other users)
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser5.getDisplayName());
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser6, commentOnStatusEvent, user5StatusUpdate, user5Comment, hashtagUser5);
		
		// Switch focus to the newly opened global search window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the filtered hashtag is displayed in the UI
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser5);
		
		// Verify that only User 5's actions are displayed in the UI and NOT any of the actions performed by Users 1 to 4
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user5StatusUpdate}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate, user2StatusUpdate, user3CommunityStatusUpdate, user4BoardMessage}, false);
		
		// Close the global search window and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		// User 6 will now click on the hashtag used by both User 5 and User 4 (ie. this hashtag will NOT have been used by Users 1 to 3)
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser6, commentOnStatusEvent, user5StatusUpdate, user5Comment, hashtagUser4);
		
		// Switch focus to the newly opened global search window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Verify that the filtered hashtag is displayed in the UI
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser4);
		
		// Verify that User 5's and User 4's actions are displayed in the UI and NOT any of the actions performed by Users 1 to 3
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user5StatusUpdate, user4BoardMessage}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate, user2StatusUpdate, user3CommunityStatusUpdate}, false);
		
		// Close the global search window and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		// User 6 will now click on the hashtag used by Users 3 to 5 (ie. this hashtag will NOT have been used by User 1 and User 2)
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser6, commentOnStatusEvent, user5StatusUpdate, user5Comment, hashtagUser3);
				
		// Switch focus to the newly opened global search window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that the filtered hashtag is displayed in the UI
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser3);
				
		// Verify that User 3 to 5's actions are displayed in the UI and NOT any of the actions performed by User 1 or User 2
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user5StatusUpdate, user4BoardMessage, user3CommunityStatusUpdate}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate, user2StatusUpdate}, false);
				
		// Close the global search window and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		// User 6 will now click on the hashtag used by Users 2 to 5 (ie. this hashtag will NOT have been used by User 1)
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser6, commentOnStatusEvent, user5StatusUpdate, user5Comment, hashtagUser2);
						
		// Switch focus to the newly opened global search window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
						
		// Verify that the filtered hashtag is displayed in the UI
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser2);
						
		// Verify that User 2 to 5's actions are displayed in the UI and NOT any of the actions performed by User 1
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user5StatusUpdate, user4BoardMessage, user3CommunityStatusUpdate, user2StatusUpdate}, true);
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate}, false);
						
		// Close the global search window and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		// User 6 will now click on the hashtag used by Users 1 to 5
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser6, commentOnStatusEvent, user5StatusUpdate, user5Comment, hashtagUser1);
								
		// Switch focus to the newly opened global search window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
								
		// Verify that the filtered hashtag is displayed in the UI
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
								
		// Verify that User 1 to 5's actions are displayed in the UI
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user5StatusUpdate, user4BoardMessage, user3CommunityStatusUpdate, user2StatusUpdate, user1StatusUpdate}, true);
							
		// Close the global search window and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_Mentions() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a status update with a hashtag and a mentions to User 2</B></li>
	*<li><B>Step: User 1 add another status update mentioning User 3 with the same hashtag</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 2 go to Homepage / Mentions</B></li>
	*<li><b>Step: User 2 click the hashtag</b></li>
	*<li><b>Verify: User 2 is brought to the Status Updates view Global Search results page, returning both mentions events containing the hashtag</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/23C0CF753BDD7D2885257C7000596EBA">TTT - CR4 - AS SEARCH - 00013 - ONLY EVENTS IN MENTIONS VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void hashtag_StatusUpdate_Mentions() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 6 (as User 1), User 7 (as User 2) and User 8 (as User 3)
		 */
		ui.startTest();
		
		// User 1 will now post a status update with a valid hashtag and mentions to User 2
		String hashtagUser1 = ProfileEvents.generateValidHashtag();
		Mentions mentionsUser2 = MentionsBaseBuilder.buildBaseMentions(testUser7, profilesAPIUser7, serverURL, Helper.genStrongRand(), Helper.genStrongRand() + " #" + hashtagUser1);
		String mentionsUser2Id = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser6, mentionsUser2);
		statusUpdatesToBeDeleted.put(mentionsUser2Id, profilesAPIUser6);
		
		// User 1 will now post a status update with a valid hashtag and mentions to User 3
		Mentions mentionsUser3 = MentionsBaseBuilder.buildBaseMentions(testUser8, profilesAPIUser8, serverURL, Helper.genStrongRand(), Helper.genStrongRand() + " #" + hashtagUser1);
		String mentionsUser3Id = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser6, mentionsUser3);
		statusUpdatesToBeDeleted.put(mentionsUser3Id, profilesAPIUser6);
		
		// Perform indexing for the hashtag used by User 1
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser1, testUser6, serverURL, searchAdminService, adminUser);
		
		// Log in as User 2 and go to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser7, false);
				
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
						
		// User 2 will now click on the unique hashtag used by User 1
		String mentionsEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser6.getDisplayName());
		String statusUpdateWithMentionsUser2 = mentionsUser2.getBeforeMentionText() + " @" + mentionsUser2.getUserToMention().getDisplayName() + " " + mentionsUser2.getAfterMentionText();
		UIEvents.clickNewsStoryMentionsHashtag(ui, testUser6, mentionsEvent, mentionsUser2, hashtagUser1);
						
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
						
		// Verify that the tag used by User 1 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser1);
						
		// Verify that the status updates with mentions to User 2 and User 3 are displayed
		String statusUpdateWithMentionsUser3 = mentionsUser3.getBeforeMentionText() + " @" + mentionsUser3.getUserToMention().getDisplayName() + " " + mentionsUser3.getAfterMentionText();
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{statusUpdateWithMentionsUser2, statusUpdateWithMentionsUser3}, true);
				
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
	
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_MyNotifications() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 comment on the update with a hashtag in the comment</B></li>
	*<li><B>Step: User 2 add a status update with the same hashtag as in the comment</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All</B></li>
	*<li><B>Step: User 1 click the hashtag</B></li>
	*<li><B>Verify: Verify that the Global Search is activated and returns subjects related to the tag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/706AE3C1BEAA0D1985257C70005970FA">TTT - CR4 - AS SEARCH - 00014 - ONLY EVENTS IN MY NOTIFICATIONS VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void hashtag_StatusUpdate_MyNotifications() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 7 (as User 1), User 8 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now post a status update to their own profile
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser7, user1StatusUpdate);
		statusUpdatesToBeDeleted.put(user1StatusUpdateId, profilesAPIUser7);
		
		// User 2 will now comment on the status update with a valid hashtag
		String hashtagUser2 = ProfileEvents.generateValidHashtag();
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand() + " #" + hashtagUser2;
		ProfileEvents.addStatusUpdateComment(user1StatusUpdateId, user2Comment, profilesAPIUser8);
		
		// User 2 will now post a status update with a valid hashtag (using the same hashtag as posted with the comment)
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUser2;
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser8, user2StatusUpdate);
		statusUpdatesToBeDeleted.put(user2StatusUpdateId, profilesAPIUser8);
		
		// Perform indexing for the hashtag used by User 2
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUser2, testUser8, serverURL, searchAdminService, adminUser);
		
		// Log in as User 1 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser7, false);
						
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
								
		// User 1 will now click on the unique hashtag used by User 2
		String commentEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser8.getDisplayName());
		UIEvents.clickNewsStoryCommentHashtag(ui, testUser7, commentEvent, user1StatusUpdate, user2Comment, hashtagUser2);
								
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
								
		// Verify that the tag used by User 2 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUser2);
								
		// Verify that the status updates posted by User 1 and User 2 are displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user1StatusUpdate, user2StatusUpdate}, true);
						
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		ui.endTest();
	}

	/**
	* hashtag_StatusUpdate_Saved() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 follow User 2</B></li>
	*<li><B>Step: User 2 add a status update with a hashtag</B></li>
	*<li><B>Step: User 3 add a status update with the same hashtag</B></li>
	*<li><B>Step: User 1 save User 2's status update</B></li>
	*<li><B>Step: Let indexing happen</B></li>
	*<li><B>Step: User 1 go to Homepage / Saved / All</B></li>
	*<li><B>Step: User 1 click the hashtag</B></li>
	*<li><B>Verify: Verify that the Global Search is activated and returns subjects related to the tag</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D4F67DC2ED4CDD485257C7000597229">TTT - CR4 - AS SEARCH - 00015 - ONLY EVENTS IN SAVED VIEW APPEAR IN RESULTS</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 3)
	public void hashtag_StatusUpdate_Saved() {
		
		/**
		 * In order to avoid the same users being asked to perform indexing when these tests are run on the grid
		 * this test case will use User 5 (as User 1), User 6 (as User 2) and User 7 (as User 3)
		 */
		ui.startTest();
		
		// User 1 will now follow User 2
		ProfileEvents.followUser(profilesAPIUser6, profilesAPIUser5);
		usersToUnfollow.put(profilesAPIUser6, profilesAPIUser5);
		
		// User 2 will now post a status update with a valid hashtag
		String hashtagUsers2And3 = ProfileEvents.generateValidHashtag();
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUsers2And3;
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser6, user2StatusUpdate);
		statusUpdatesToBeDeleted.put(user2StatusUpdateId, profilesAPIUser6);
		
		// User 3 will now post a status update including the same hashtag as User 2
		String user3StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand() + " #" + hashtagUsers2And3;
		String user3StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser7, user3StatusUpdate);
		statusUpdatesToBeDeleted.put(user3StatusUpdateId, profilesAPIUser7);
		
		// Log in as User 1 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser5, false);
		
		// User 1 will now save the status update posted by User 2
		UIEvents.saveNewsStoryUsingUI(ui, user2StatusUpdate);
		
		// Perform indexing for the hashtag used by User 2
		ProfileEvents.performIndexingForSpecifiedStatusUpdateTag(globalSearchUI, hashtagUsers2And3, testUser6, serverURL, searchAdminService, adminUser);
				
		// User 1 will now navigate to the Saved view
		UIEvents.gotoSaved(ui);
								
		// Get the handle for the current browser window before clicking on the hashtag
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
										
		// User 1 will now click on the unique hashtag used by User 2
		UIEvents.clickNewsStoryHashtag(ui, testUser5, user2StatusUpdate, hashtagUsers2And3);
										
		// Switch focus to the global search results screen
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
										
		// Verify that the tag used by Users 2 and 3 is displayed
		HomepageValid.verifyFilteredTagIsDisplayedInGlobalSearchUI(ui, hashtagUsers2And3);
										
		// Verify that the status updates posted by User 2 and User 3 are displayed
		HomepageValid.verifyItemsInGlobalSearch(ui, driver, new String[]{user2StatusUpdate, user3StatusUpdate}, true);
								
		// Close the global search screen and return focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
		
		ui.endTest();
	}
}