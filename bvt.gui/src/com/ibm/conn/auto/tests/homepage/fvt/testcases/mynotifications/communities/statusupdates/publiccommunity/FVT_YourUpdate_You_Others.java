package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.publiccommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016  		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourUpdate_You_Others extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourUpdate_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a Public Community you own</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 1 like their status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 add another status update in the community</B></li>
	*<li><B>Step: User 3 to User 5 like the Status Update</B></li>
	*<li><B>Step: User 1 like the Status Update also</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your message in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others liked your message in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E4883A8AF0CBDFE085257DEA00563CAC">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00021 - LIKE ON YOUR OWN STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourUpdate_Like_You_Others_Rollup(){

		ui.startTest();
		
		// User 1 will now post the first status update to the community
		String user1Message1 = Data.getData().UpdateStatus + Helper.genStrongRand();
		String user1Message1Id = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1Message1);
		
		// User 1 will now like / recommend the status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser1, user1Message1Id);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeStatusUpdateEvent = CommunityNewsStories.getLikeYourMessageNewsStory_You(ui, baseCommunity.getName());
		
		// Verify that the like status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdateEvent, user1Message1}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend the status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser2, user1Message1Id);
		
		// Create the news story to be verified
		likeStatusUpdateEvent = CommunityNewsStories.getLikeYourMessageNewsStory_UserAndYou(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdateEvent, user1Message1}, TEST_FILTERS, true);
		
		// User 1 will now post the second status update to the community
		String user1Message2 = Data.getData().UpdateStatus + Helper.genStrongRand();
		String user1Message2Id = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1Message2);
		
		// Users 3 through to User 5 will now like / recommend the second status update with User 1 then liking the status update
		APIProfilesHandler[] apiUsersLikingStatusUpdate = { profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser1 };
		CommunityEvents.likeStatusUpdateMultipleUsers(apiUsersLikingStatusUpdate, user1Message2Id);
		
		// Create the news story to be verified
		String likeSecondStatusEvent = CommunityNewsStories.getLikeYourMessageNewsStory_YouAndMany(ui, "3", baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the like first status update event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeStatusUpdateEvent, user1Message1}, filter, true);
			
			// Verify that the like second status update event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeSecondStatusEvent, user1Message2}, filter, true);
		}		
		ui.endTest();
	}	
}