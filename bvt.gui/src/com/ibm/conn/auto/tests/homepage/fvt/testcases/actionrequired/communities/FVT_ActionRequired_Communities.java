package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author:	Anthony Cox
 *	Date:		22nd October 2015
 */

public class FVT_ActionRequired_Communities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser6, communitiesAPIUser7;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private BaseCommunity baseCommunity, baseCommunity1ForDeletion, baseCommunity2ForDeletion;
	private boolean deletedCommunity1, deletedCommunity2;
	private Community moderatedCommunity, moderatedCommunity1ForDeletion, moderatedCommunity2ForDeletion;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(7);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser6 = initialiseAPICommunitiesHandlerUser(testUser6);
		communitiesAPIUser7 = initialiseAPICommunitiesHandlerUser(testUser7);
		
		// User 1 will now create a moderated community to be used to send invitations to multiple users
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a first moderated community to be deleted to verify that the community invite news story is also removed
		baseCommunity1ForDeletion = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity1ForDeletion = CommunityEvents.createNewCommunity(baseCommunity1ForDeletion, testUser1, communitiesAPIUser1);
		deletedCommunity1 = false;
		
		// User 1 will now create a second moderated community to be deleted to verify that the request to join the community news story is also removed
		baseCommunity2ForDeletion = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity2ForDeletion = CommunityEvents.createNewCommunity(baseCommunity2ForDeletion, testUser1, communitiesAPIUser1);
		deletedCommunity2 = false;
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities deleted during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		if(deletedCommunity1 == false) {
			communitiesAPIUser1.deleteCommunity(moderatedCommunity1ForDeletion);
		}
		
		if(deletedCommunity2 == false) {
			communitiesAPIUser1.deleteCommunity(moderatedCommunity2ForDeletion);
		}
	}
	
	/**
	* test_ActionRequired_CommunityInviteNotification()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. Invite User 2 to the community</B></li>
	*<li><B>4. User 2 checks their Action Required view</B></li>
	*<li><b>Verify: Verify that a story appears in the view regarding the invite</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DD70457ED36252338525792F0046CDAB">TTT: AS - Action Required - 00011 - Community Invite Notification To Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_CommunityInviteNotification() {
		
		ui.startTest();
		
		// User 1 will now invite User 2 to join the community
		CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Create the news story to be verified
		String invitedToCommunityEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the community invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_RevokeCommunityInviteRemovesStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 1 invites User 2 to the community</B></li>
	*<li><B>4. User 2 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 1 then revokes the invite (membership \ invitations \ revoke)</b></li>
	*<li><b>6. User 2 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the Invitation story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CE134E369AD08C378525792F005253D0">TTT: AS - Action Required - 00012 - Revoke Community Invite Removes From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_RevokeCommunityInviteRemovesStory() {
		
		/**
		 * To avoid clashes with duplicate invitations to the same community, this test will use User 3 as User 2
		 */
		ui.startTest();
		
		// User 1 will now invite User 2 to join the community
		Invitation communityInvitation = CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser3);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser3, false);
		
		// Create the news story to be verified
		String invitedToCommunityEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the community invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now revoke the community invitation
		CommunityEvents.revokeCommunityInvitation(communityInvitation, testUser1, communitiesAPIUser1);
		
		// Verify that the community invite event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_DeleteCommunityAutoRemovesStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 1 invites User 2 to the community</B></li>
	*<li><B>4. User 2 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 1 then deletes the community</b></li>
	*<li><b>6. User 2 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the Invitation story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AB89C8652DCE9C30852579D60047BEE5">TTT: AS - Action Required - 00013 - Delete Community Auto Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_DeleteCommunityAutoRemovesStory() {
		
		ui.startTest();
		
		// User 1 will now invite User 2 to join the community
		CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity1ForDeletion, testUser1, communitiesAPIUser1, profilesAPIUser2);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Create the news story to be verified
		String invitedToCommunityEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseCommunity1ForDeletion.getName(), testUser1.getDisplayName());
		
		// Verify that the community invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity1ForDeletion.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now delete the community
		CommunityEvents.deleteCommunity(moderatedCommunity1ForDeletion, testUser1, communitiesAPIUser1);
		deletedCommunity1 = true;
		
		// Verify that the community invite event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity1ForDeletion.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_AdminUserAddsUserToCommunityAutoRemovesStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 1 invites User 2 to the community</B></li>
	*<li><B>4. User 2 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 1 then explicitly adds User 2 to the community membership</b></li>
	*<li><b>6. User 2 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the Invitation story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5A68925937DD0BC4852579D60047C05F">TTT: AS - Action Required - 00014 - Admin User Adds User To Community Auto Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_AdminUserAddsUserToCommunityAutoRemovesStory() {
		
		/**
		 * To avoid clashes with duplicate invitations to the same community, this test will use User 4 as User 2
		 */
		ui.startTest();
		
		// User 1 will now invite User 2 to join the community
		CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser4);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser4, false);
		
		// Create the news story to be verified
		String invitedToCommunityEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the community invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now explicitly add User 2 to the community as a member
		CommunityEvents.addMemberSingleUser(moderatedCommunity, testUser1, communitiesAPIUser1, testUser4);
		
		// Verify that the community invite event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_UserJoinsCommunityAutoRemovesStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a public community</B></li>
	*<li><B>3. User 1 invites User 2 to the community</B></li>
	*<li><B>4. User 2 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 2 joins the community</b></li>
	*<li><b>6. User 2 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the Invitation story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3CCB5CA6381A1B95852579D60047C21B">TTT: AS - Action Required - 00015 - User Joins Community Auto Removes Story From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_UserJoinsCommunityAutoRemovesStory() {
		
		/**
		 * To avoid clashes with duplicate invitations to the same community, this test will use User 5 as User 2
		 */
		ui.startTest();
		
		// User 1 will now invite User 2 to join the community
		CommunityEvents.inviteUserToJoinCommunity(moderatedCommunity, testUser1, communitiesAPIUser1, profilesAPIUser5);
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser5, false);
		
		// Create the news story to be verified
		String invitedToCommunityEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the community invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, true);
		
		// Join the community by accepting the invite
		CommunityEvents.acceptCommunityInviteUsingUI(ui, moderatedCommunity, invitedToCommunityEvent, testUser5);
		
		// Verify that the community invite event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{invitedToCommunityEvent, moderatedCommunity.getContent()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_CommunityRequestToJoinNotification()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 2 requests to join the community</B></li>
	*<li><B>4. User 1 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>Verify: Verify that the RequestToJoin story is present in the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7806F5C3EF7D9844852579D600499732">TTT: AS - Action Required - 00021 - Community Request To Join Notification To Action Required Story</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_CommunityRequestToJoinNotification() {
	
		/**
		 * To avoid clashes with duplicate requests to the same community, this test will use User 6 as User 2
		 */
		ui.startTest();
		
		// User 2 will now send a request to User 1 to join the community
		String requestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity, testUser6, communitiesAPIUser6);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Create the news story to be verified
		String requestToJoinEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseCommunity.getName(), testUser6.getDisplayName());
		
		// Verify that the request to join a community event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{requestToJoinEvent, requestToJoinMessage}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_UserAddedToCommunityRemovesRequestToJoin()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 2 requests to join the community</B></li>
	*<li><B>4. User 1 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 1 click 'Add Member' and add the user to the community</b></li>
	*<li><b>6. User 1 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the RequestToJoin story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EFB6AF1564444211852579D60049997C">TTT: AS - Action Required - 00022 - User Added To Community Removes RequestToJoin From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_UserAddedToCommunityRemovesRequestToJoin() {
		
		/**
		 * To avoid clashes with duplicate requests to the same community, this test will use User 7 as User 2
		 */
		ui.startTest();
		
		// User 2 will now send a request to User 1 to join the community
		String requestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity, testUser7, communitiesAPIUser7);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Create the news story to be verified
		String requestToJoinEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseCommunity.getName(), testUser7.getDisplayName());
		
		// Verify that the request to join a community event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{requestToJoinEvent, requestToJoinMessage}, TEST_FILTERS, true);
		
		// User 1 will now accept User 2's request to join the community
		CommunityEvents.acceptRequestToJoinCommunityUsingUI(ui, moderatedCommunity, requestToJoinEvent, testUser1, testUser7);
		
		// Verify that the request to join a community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{requestToJoinEvent, requestToJoinMessage}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ActionRequired_DeleteCommunityRemovesRequestToJoin()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities as User 1</B></li>
	*<li><B>2. Create a moderated community</B></li>
	*<li><B>3. User 2 requests to join the community</B></li>
	*<li><B>4. User 1 checks their Action Required view to confirm that the story is in Action Required</B></li>
	*<li><b>5. User 1 then deletes the community</b></li>
	*<li><b>6. User 1 checks their Action Required view</b></li>
	*<li><b>Verify: Verify that the RequestToJoin story is removed from the Action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/37E10DAF7FC4CB6A852579D600499C03">TTT: AS - Action Required - 00023 - Delete Community Removes Request To Join From Action Required</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_DeleteCommunityRemovesRequestToJoin() {

		ui.startTest();
		
		// User 2 will now send a request to User 1 to join the community
		String requestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity2ForDeletion, testUser2, communitiesAPIUser2);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Create the news story to be verified
		String requestToJoinEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseCommunity2ForDeletion.getName(), testUser2.getDisplayName());
		
		// Verify that the request to join a community event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{requestToJoinEvent, requestToJoinMessage}, TEST_FILTERS, true);
		
		// User 1 will now delete the community
		CommunityEvents.deleteCommunity(moderatedCommunity2ForDeletion, testUser1, communitiesAPIUser1);
		deletedCommunity2 = true;
		
		// Verify that the request to join a community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{requestToJoinEvent, requestToJoinMessage}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}