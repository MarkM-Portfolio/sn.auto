package com.ibm.conn.auto.tests.homepage.fvt.testcases.statusupdates;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2017                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_NetworkFilters_Communities extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS_IM_FOLLOWING;
	private String[] TEST_FILTERS_STATUS_UPDATES;
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private Invitation user1NetworkInvitation;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		if(isOnPremise) {
			TEST_FILTERS_STATUS_UPDATES = new String[4];
			TEST_FILTERS_STATUS_UPDATES[1] = HomepageUIConstants.MyNetworkAndPeopleIFollow;
			TEST_FILTERS_STATUS_UPDATES[2] = HomepageUIConstants.MyNetwork;
			
			TEST_FILTERS_IM_FOLLOWING = new String[4];
			TEST_FILTERS_IM_FOLLOWING[2] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS_STATUS_UPDATES = new String[2];
			TEST_FILTERS_IM_FOLLOWING = new String[3];
		}
		TEST_FILTERS_STATUS_UPDATES[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_STATUS_UPDATES[TEST_FILTERS_STATUS_UPDATES.length - 1] = HomepageUIConstants.FilterCommunities;
		
		TEST_FILTERS_IM_FOLLOWING[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_IM_FOLLOWING[1] = HomepageUIConstants.FilterSU;
		TEST_FILTERS_IM_FOLLOWING[TEST_FILTERS_IM_FOLLOWING.length - 1] = HomepageUIConstants.FilterCommunities;
		
		// User 1 will now invite User 2 to join their network
		user1NetworkInvitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);
		
		// User 2 will accept the network invitation
		ProfileEvents.acceptInvitationToJoinANetwork(user1NetworkInvitation, profilesAPIUser2, profilesAPIUser1);
		
		// User 2 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a restricted community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser2, communitiesAPIUser2);
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		// Remove User 2 from User 1's network now that all tests have completed
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		
		// Delete all of the communities created during the test
		communitiesAPIUser2.deleteCommunity(publicCommunity);
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
	}

	/**
	* myNetwork_statusUpdate_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a public community they own</B></li>
	*<li><B>Step: testUser1 go to Homepage/Updates/Status Updates/ All, My Network, My Network and People I follow, & Communities</B></li>
	*<li><B>Step: testUser1 go to Homepage/I'm Following / All, Status Updates, People and Communities</B></li>
	*<li><B>Verify: Verify that testUser1 can see the status update news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the status update news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/71EB8A9682C28ACD85257C0D002DF239">TTT - ACTIVITY STREAM - NETWORK - 00022 - PUBLIC COMMUNITY STATUS UPDATE DOES NOT APPEAR IN I'm FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdate_publicCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the public community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, basePublicCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the community status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}
	
	/**
	* myNetwork_statusUpdateComment_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a public community they own</B></li>
	*<li><B>Step: testUser2 comments on the status update</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates, People & Communities</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network, My Network & People I Follow & Communities</B></li>
	*<li><B>Verify: Verify that testUser1 can see the comment news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the comment news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB76688BCBFE190285257C0D002F49CE">TTT - ACTIVITY STREAM - NETWORK - 00023 - PUBLIC COMMUNITY STATUS UPDATE COMMENT DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdateComment_publicCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the public community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// User 2 will now post a comment to the status update in the public community
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user2StatusUpdateId, user2Comment);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, basePublicCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the community status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_STATUS_UPDATES, true);
				
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}

	/**
	* myNetwork_statusUpdate_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a moderated community they own</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People & Communities</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network, My Network & People I Follow & Communities</B></li>
	*<li><B>Verify: Verify that testUser1 can see the status update news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the status update news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A5BC88739279BDDA85257C0D00311F82">TTT - ACTIVITY STREAM - NETWORK - 00024 - MODERATED COMMUNITY STATUS UPDATE STORIES DO NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdate_modCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the moderated community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the community status update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_STATUS_UPDATES, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}
	
	/**
	* myNetwork_statusUpdateComment_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a moderated community they own</B></li>
	*<li><B>Step: testUser2 comments on the status update</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates, People & Communities</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network, My Network & People I Follow & Communities</B></li>
	*<li><B>Verify: Verify that testUser1 can see the comment news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the comment news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/58335C6DFDF4B63885257C0D00314AD82">TTT - ACTIVITY STREAM - NETWORK - 00025 - MODERATED COMMUNITY STATUS UPDATE COMMENT DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdateComment_modCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the moderated community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// User 2 will now post a comment to the status update in the moderated community
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user2StatusUpdateId, user2Comment);
				
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, baseModeratedCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the community status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_STATUS_UPDATES, true);
				
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}

	/**
	* myNetwork_statusUpdate_privateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a private community they own</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates & People & Communities</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network, My Network & People I Follow & Communities</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the status update news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the status update news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FAE241D73B4C0B5085257C0D004E08BE">TTT - ACTIVITY STREAM - NETWORK - 00026 - PRIVATE COMMUNITY STATUS UPDATE STORIES DO NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdate_privateCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the restricted community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the community status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_STATUS_UPDATES, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}
	
	/**
	* myNetwork_statusUpdateComment_privateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 invites testUser2 to their network, but does not follow them</B></li>
	*<li><B>Step: testUser2 accepts the invitation</B></li>
	*<li><B>Step: testUser2 posts a status update to a private community</B></li>
	*<li><B>Step: testUser2 comments on the status update</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / Status Updates / All, Status Updates, People & Communities</B></li>
	*<li><B>Step: testUser1 login to Homepage / Updates / I'm Following / All, My Network, My Network & People I Follow & Communities</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the comment news story in all filters in the Status Updates view</B></li>
	*<li><B>Verify: Verify that testUser1 CANNOT see the comment news story in any filter in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/11BE5584A779A54985257C0D005732A6">TTT - ACTIVITY STREAM - NETWORK - 00027 - PRIVATE COMMUNITY STATUS UPDATE COMMENT DOES NOT APPEAR IN I'M FOLLOWING VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void myNetwork_statusUpdateComment_privateCommunity(){

		ui.startTest();
		
		// User 2 will now post a status update to the restricted community
		String user2StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user2StatusUpdateId = CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser2, profilesAPIUser2, user2StatusUpdate);
		
		// User 2 will now post a comment to the status update in the restricted community
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user2StatusUpdateId, user2Comment);
				
		// Log in as User 1 and go to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the news story to be verified
		String communityStatusUpdateEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, baseRestrictedCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the community status update event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_STATUS_UPDATES, false);
				
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the community status update event and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{communityStatusUpdateEvent, user2StatusUpdate, user2Comment}, TEST_FILTERS_IM_FOLLOWING, false);
		
		ui.endTest();
	}
}