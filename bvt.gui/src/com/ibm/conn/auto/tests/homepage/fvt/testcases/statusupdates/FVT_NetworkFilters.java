package com.ibm.conn.auto.tests.homepage.fvt.testcases.statusupdates;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_NetworkFilters extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS_IM_FOLLOWING;
	private String[] TEST_FILTERS_STATUS_UPDATES;
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private ArrayList<String> listOfStatusUpdateIds = new ArrayList<String>();
	private Invitation user1NetworkInvitation;
	private User testUser1, testUser2, testUser3;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		if(isOnPremise) {
			TEST_FILTERS_STATUS_UPDATES = new String[3];
			TEST_FILTERS_STATUS_UPDATES[1] = HomepageUIConstants.MyNetworkAndPeopleIFollow;
			TEST_FILTERS_STATUS_UPDATES[2] = HomepageUIConstants.MyNetwork;
			
			TEST_FILTERS_IM_FOLLOWING = new String[3];
			TEST_FILTERS_IM_FOLLOWING[2] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS_STATUS_UPDATES = new String[1];
			TEST_FILTERS_IM_FOLLOWING = new String[2];
		}
		TEST_FILTERS_STATUS_UPDATES[0] = HomepageUIConstants.FilterAll;
		
		TEST_FILTERS_IM_FOLLOWING[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_IM_FOLLOWING[1] = HomepageUIConstants.FilterSU;
		
		// User 1 will now invite User 2 to join their network
		user1NetworkInvitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);
		
		// User 2 will accept the network invitation
		ProfileEvents.acceptInvitationToJoinANetwork(user1NetworkInvitation, profilesAPIUser2, profilesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		// Remove User 2 from User 1's network now that all tests have completed
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		
		// Delete all of the board messages and status updates posted during the test
		for(String statusUpdateId : listOfStatusUpdateIds) {
			profilesAPIUser2.deleteBoardMessage(statusUpdateId);
		}
	}

	/**
	* myNetwork_addStatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network and My Network & People I Follow</B></li>
	*<li><B>Verify: Verify that testUser1 can see the status update news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the status update news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/03E54D0A586BB5178525799E00337941">TTT - ACTIVITY STREAM - NETWORK - 00020 - STATUS UPDATE DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_addStatusUpdate(){

		ui.startTest();
		
		// User 2 will now post a status update
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		listOfStatusUpdateIds.add(user2StatusUpdateId);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Verify that User 2's status update is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user2StatusUpdate}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that User 2's status update is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user2StatusUpdate}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}
	
	/**
	* myNetwork_addStatusUpdateComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update</B></li>
	*<li><B>Step: testUser2 comments on the status update</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network and My Network & People I Follow</B></li>
	*<li><B>Verify: Verify that testUser1 can see the comment news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the comment news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE9DC085ACDD83A285257C0C006E1342">TTT - ACTIVITY STREAM - NETWORK - 00021 - STATUS UPDATE COMMENT DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_addStatusUpdateComment(){

		ui.startTest();
		
		// User 2 will now post a status update
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		listOfStatusUpdateIds.add(user2StatusUpdateId);
		
		// User 2 will now post a comment to their status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdateId, user2Comment, profilesAPIUser2);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser2.getDisplayName());
		
		// Verify that the comment on status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the comment on status update event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_IM_FOLLOWING, false);
				
		ui.endTest();
	}
	
	/**
	* myNetwork_boardMessage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update on their own Profile page</B></li>
	*<li><B>Step: testUser2 posts a status update on testUser 3's Profile page</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network and My Network & People I Follow</B></li>
	*<li><B>Verify: Verify that testUser1 can see the board message news stories in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the board message news stories in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B161C559BAF461A585257C0E002EFD16">TTT - ACTIVITY STREAM - NETWORK - 00028 - BOARD MESSAGE STORY DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_boardMessage(){

		ui.startTest();
		
		// User 2 will now post a status update
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		listOfStatusUpdateIds.add(user2StatusUpdateId);
		
		// User 2 will now post a board message on User 3's profile
		String user2BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2BoardMessageId = ProfileEvents.addBoardMessage(user2BoardMessage, profilesAPIUser2, profilesAPIUser3);
		listOfStatusUpdateIds.add(user2BoardMessageId);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String boardMessageEvent = ProfileNewsStories.getPostedAMessageToUserNewsStory(ui, testUser3.getDisplayName(), testUser2.getDisplayName());
		
		// Verify that User 2's status update, the board message event and User 2's board message are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user2StatusUpdate, boardMessageEvent, user2BoardMessage}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that User 2's status update, the board message event and User 2's board message are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{user2StatusUpdate, boardMessageEvent, user2BoardMessage}, TEST_FILTERS_IM_FOLLOWING, false);
				
		ui.endTest();
	}

	/**
	* myNetwork_boardMessageComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update on their own Profile page</B></li>
	*<li><B>Step: testUser2 comments on the status update on their own Profile page</B></li>
	*<li><B>Step: testUser2 posts a status update on testUser 3's Profile page</B></li>
	*<li><B>Step: testUser2 comments on their the board message on testUser3's Profile page</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network and My Network & People I Follow</B></li>
	*<li><B>Verify: Verify that testUser1 can see the board message comment news stories in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the board message comment news stories in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C41C1BD3E5179BC85257C0E00308321">TTT - ACTIVITY STREAM - NETWORK - 00029 - BOARD MESSAGE COMMENT STORY DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_boardMessageComment(){

		ui.startTest();
		
		// User 2 will now post a status update
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser2, user2StatusUpdate);
		listOfStatusUpdateIds.add(user2StatusUpdateId);
		
		// User 2 will now post a comment to their status update
		String user2StatusUpdateComment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2StatusUpdateId, user2StatusUpdateComment, profilesAPIUser2);
		
		// User 2 will now post a board message on User 3's profile
		String user2BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2BoardMessageId = ProfileEvents.addBoardMessage(user2BoardMessage, profilesAPIUser2, profilesAPIUser3);
		listOfStatusUpdateIds.add(user2BoardMessageId);
		
		// User 2 will now post a comment to their board message
		String user2BoardMessageComment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(user2BoardMessageId, user2BoardMessageComment, profilesAPIUser2);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news stories to be verified
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnTheirOwnMessageNewsStory(ui, testUser2.getDisplayName());
		String commentOnBoardMessageEvent = ProfileNewsStories.getCommentedOnTheirOwnMessagePostedToUserNewsStory(ui, testUser3.getDisplayName(), testUser2.getDisplayName());
		
		// Verify that all comment events, all board messages and all comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user2StatusUpdate, user2StatusUpdateComment, commentOnBoardMessageEvent, user2BoardMessage, user2BoardMessageComment}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that all comment events, all board messages and all comments are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user2StatusUpdate, user2StatusUpdateComment, commentOnBoardMessageEvent, user2BoardMessage, user2BoardMessageComment}, TEST_FILTERS_IM_FOLLOWING, false);
				
		ui.endTest();
	}
}