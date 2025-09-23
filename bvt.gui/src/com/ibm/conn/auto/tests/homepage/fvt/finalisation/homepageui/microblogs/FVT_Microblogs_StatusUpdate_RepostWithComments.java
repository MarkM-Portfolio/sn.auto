package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.microblogs;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;

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
 * Date:	29th September 2016
 */

public class FVT_Microblogs_StatusUpdate_RepostWithComments extends SetUpMethods2 {
	
	private String[] TEST_FILTERS_IF_PEOPLE_VIEW;
	private String[] TEST_FILTERS_SU_PEOPLE_VIEW;
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private HomepageUI ui;
	private String serverURL, user2StatusUpdate, user2StatusUpdateId, user3Comment;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 3 unique users are chosen from the CSV file
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
		} while(listOfUsers.size() < 3);
				
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		// Set the filters to be tested during the tests
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			TEST_FILTERS_IF_PEOPLE_VIEW = new String[3];
			TEST_FILTERS_IF_PEOPLE_VIEW[2] = HomepageUIConstants.FilterPeople;
			
			TEST_FILTERS_SU_PEOPLE_VIEW = new String[2];
			TEST_FILTERS_SU_PEOPLE_VIEW[1] = HomepageUIConstants.PeopleIFollow;
		} else {
			TEST_FILTERS_IF_PEOPLE_VIEW = new String[2];
			
			TEST_FILTERS_SU_PEOPLE_VIEW = new String[1];
		}
		TEST_FILTERS_IF_PEOPLE_VIEW[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_IF_PEOPLE_VIEW[1] = HomepageUIConstants.FilterSU;
		
		TEST_FILTERS_SU_PEOPLE_VIEW[0] = HomepageUIConstants.FilterAll;
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 2 will now post a status update
		user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		
		// User 3 will now comment on User 2's status update
		user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdateId, user3Comment, profilesAPIUser3);
		
		// Log in as User 3 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be re-posted
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnAnotherUsersMessageNewsStory_You(ui, testUser2.getDisplayName());
		
		// User 3 will now re-post User 2's status update with all comments attached
		UIEvents.openEEAndRepostNewsStory(ui, commentOnMessageEvent);
		
		// Close the browser window again
		UIEvents.closeCurrentBrowserWindow(ui);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the status updates created during the test
		profilesAPIUser2.deleteBoardMessage(user2StatusUpdateId);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	* test_RepostStatusUpdate_WithComments() 
	*<ul>
	*<li><B>1: User 1 log into Homepage</B></li>
	*<li><B>2: User 2 log into connections and follow User 1</B></li>
	*<li><B>3: User 1 go to Homepage / Discover / Status Updates</B></li>
	*<li><B>4: User 1 go to the story of another user reposted a status update with comments  of another user</B></li>
	*<li><B>5: User 1 open the EE for the story</B></li>
	*<li><B>6: User 1 click "Repost" again</B></li>
	*<li><B>7: User 1 go to Homepage / I'm Following  / All</B></li>
	*<li><B>8: User 1 go to Homepage / I'm Following  / Status Updates</B></li>
	*<li><B>9: User 1 go to Homepage / Status Updates / All</B></li>
	*<li><B>10: User 2 go to Homepage / I'm Following  / All</B></li>
	*<li><B>11: User 2 go to Homepage / I'm Following  / Status Updates</B></li>
	*<li><B>12: User 2 go to Homepage / I'm Following  / People</B></li>
	*<li><B>13: User 2 go to Homepage / Status Updates / People I follow</B></li>
	*<li><B>Verify: For User 1 and User 2 verify the following from point 7 - 13:</B></li>
	*<li><B>Verify: Verify that the update is reposted at the top of I'm Following  view</B></li>
	*<li><B>Verify: Verify that it is of the format is "Reposter name - and the next line is the status update story that has been reposted with comments displayed</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6D3156272EAC90858525795800357F10">Repost - 00027 - Repost another users reposted status update with comments</a></li>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"}, priority = 1)
	public void test_RepostStatusUpdate_WithComments() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Disover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be reposted
		String repostedNewsStoryEvent = ProfileNewsStories.getUserRepostedNewsStory(ui, testUser3.getDisplayName());
		
		// User 1 will now use the EE to repost the news story again
		UIEvents.openEEAndRepostNewsStory(ui, repostedNewsStoryEvent);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Create the news stories and elements to be verified
		repostedNewsStoryEvent = ProfileNewsStories.getUserRepostedNewsStory(ui, testUser1.getDisplayName());
		String messageOriginallyPostedTo = ProfileNewsStories.getMessageOriginallyPostedToUserNewsStory(ui, testUser2.getDisplayName(), testUser2.getDisplayName());
		String repostIcon = HomepageUIConstants.RepostIcon.replaceAll("PLACEHOLDER", user2StatusUpdate);
		
		for(String filter : TEST_FILTERS_IF_PEOPLE_VIEW) {
			if(!filter.equals(HomepageUIConstants.FilterPeople)) {
				// Verify that the 'User reposted' and 'Users message originally posted to...' messages, User 2's status update and User 3's comment are displayed in all views
				HomepageValid.verifyItemsInAS(ui, driver, new String[]{repostedNewsStoryEvent, messageOriginallyPostedTo, user2StatusUpdate, user3Comment}, filter, true);
				
				// Verify that the repost icon is displayed with the relevant news story in all views
				HomepageValid.verifyElementsInAS(ui, driver, new String[]{repostIcon}, null, true);
			}
		}
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		for(String filter : TEST_FILTERS_SU_PEOPLE_VIEW) {
			if(!filter.equals(HomepageUIConstants.PeopleIFollow)) {
				// Verify that the 'User reposted' and 'Users message originally posted to...' messages, User 2's status update and User 3's comment are displayed in all views
				HomepageValid.verifyItemsInAS(ui, driver, new String[]{repostedNewsStoryEvent, messageOriginallyPostedTo, user2StatusUpdate, user3Comment}, filter, true);
				
				// Verify that the repost icon is displayed with the relevant news story in all views
				HomepageValid.verifyElementsInAS(ui, driver, new String[]{repostIcon}, null, true);
			}
		}
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Close the browser window instance - critical step to ensure that the reliability of the next login step is maintained when run on G2, G3 etc.
		UIEvents.closeCurrentBrowserWindow(ui);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		for(String filter : TEST_FILTERS_IF_PEOPLE_VIEW) {
			// Verify that the 'User reposted' and 'Users message originally posted to...' messages, User 2's status update and User 3's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{repostedNewsStoryEvent, messageOriginallyPostedTo, user2StatusUpdate, user3Comment}, filter, true);
			
			// Verify that the repost icon is displayed with the relevant news story in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{repostIcon}, null, true);
		}
		// Navigate to the Status Updates view
		UIEvents.gotoStatusUpdates(ui);
		
		for(String filter : TEST_FILTERS_SU_PEOPLE_VIEW) {
			// Verify that the 'User 1 reposted' and 'Users 2 message originally posted to...' messages, User 2's status update and User 3's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{repostedNewsStoryEvent, messageOriginallyPostedTo, user2StatusUpdate, user3Comment}, filter, true);
			
			// Verify that the repost icon is displayed with the relevant news story in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{repostIcon}, null, true);
		}
		ui.endTest();
	}
}