package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.privatecommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                    */
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

public class FVT_YourUpdate_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		
		// User 1 will now create a restricted community with Users 2 through to User 4 added as members
		User[] membersList = { testUser2, testUser3, testUser4 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);		
	}

	/**
	* test_YourUpdate_Like_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a private Community you own adding User 2, 3, 4 as members</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 like the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your message in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your message in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 4} and 2 others liked your message in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B721035873C782F485257DEA00563CAB">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00020 - LIKE ON A STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourUpdate_Like_Rollup() {

		ui.startTest();
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 2 will now like / recommend the status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser2, user1StatusUpdateId);
		
		// Log in as User 1 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeStatusUpdatetEvent = CommunityNewsStories.getLikeYourMessageNewsStory_User(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdatetEvent, user1StatusUpdate}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser3, user1StatusUpdateId);
		
		// Create the news story to be verified
		likeStatusUpdatetEvent = CommunityNewsStories.getLikeYourMessageNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdatetEvent, user1StatusUpdate}, TEST_FILTERS, true);
				
		// User 4 will now like / recommend the status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser4, user1StatusUpdateId);
		
		// Create the news story to be verified
		likeStatusUpdatetEvent = CommunityNewsStories.getLikeYourMessageNewsStory_UserAndMany(ui, testUser4.getDisplayName(), "2", baseCommunity.getName());
						
		// Verify that the like status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeStatusUpdatetEvent, user1StatusUpdate}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
}