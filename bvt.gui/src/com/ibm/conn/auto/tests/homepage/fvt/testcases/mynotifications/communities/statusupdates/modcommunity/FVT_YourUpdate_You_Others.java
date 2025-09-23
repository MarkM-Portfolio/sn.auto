package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.modcommunity;

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

public class FVT_YourUpdate_You_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun=true)
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
		
		// User 1 will now create a moderated community with Users 2 through to User 4 added as members
		User[] membersList = { testUser2, testUser3, testUser4 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);		
	}

	/**
	* test_YourSU_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a Moderated Community you own</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 1 comment on the status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log in to Homepage</B></li>
	*<li><B>Step: User 2 comment on the status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 and User 4 comment on the status update</B></li>
	*<li><B>Step: User 1 comment again on the status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your message in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others commented on your message in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81CAC1D0640E2FF185257DEA00563CA8">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00011 - COMMENT ON YOUR STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourSU_Comment_You_Others_Rollup(){

		ui.startTest();
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will now post their first comment to the status update
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser1, user1StatusUpdateId, user1Comment1);
		
		// Log in as User 1 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_You(ui, baseCommunity.getName());
		
		// Verify that the comment on status update event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, commentOnStatusEvent}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment);
		
		// Create the news story to be verified
		commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_UserAndYou(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on status update event, User 1's comment and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, user1Comment1, user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the status update
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser3, user1StatusUpdateId, user3Comment);
				
		// User 4 will now post a comment to the status update
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser4, user1StatusUpdateId, user4Comment);
				
		// User 1 will now post their second comment to the status update
		String user1Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser1, user1StatusUpdateId, user1Comment2);
		
		// Create the news story to be verified
		commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_YouAndMany(ui, "3", baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status update event, User 4's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, user4Comment, user1Comment2}, filter, true);
			
			// Verify that User 1's first comment, User 2's comment and User 3's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment, user3Comment}, null, false);
		}
		ui.endTest();
	}	
}