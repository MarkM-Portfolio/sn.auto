package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.publiccommunity;

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

public class FVT_YourUpdate_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(15);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		testUser10 = listOfStandardUsers.get(9);
		testUser11 = listOfStandardUsers.get(10);
		testUser12 = listOfStandardUsers.get(11);
		testUser13 = listOfStandardUsers.get(12);
		testUser14 = listOfStandardUsers.get(13);
		testUser15 = listOfStandardUsers.get(14);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);
		profilesAPIUser8 = initialiseAPIProfilesHandlerUser(testUser8);
		profilesAPIUser9 = initialiseAPIProfilesHandlerUser(testUser9);
		profilesAPIUser10 = initialiseAPIProfilesHandlerUser(testUser10);
		profilesAPIUser11 = initialiseAPIProfilesHandlerUser(testUser11);
		profilesAPIUser12 = initialiseAPIProfilesHandlerUser(testUser12);
		profilesAPIUser13 = initialiseAPIProfilesHandlerUser(testUser13);
		profilesAPIUser14 = initialiseAPIProfilesHandlerUser(testUser14);
		profilesAPIUser15 = initialiseAPIProfilesHandlerUser(testUser15);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);		
	}

	/**
	* test_YourUpdate_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 go to a Public Community you own</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 15 comment on the Status Update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your message in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your message in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 15} and 13 others commented on your message in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD51E94D373ACD2C85257DEA00563CA7">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00010 - COMMENT ON STATUS UPDATE ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourUpdate_Comment_Rollup(){

		ui.startTest();
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment);
		
		// Log in as User 1 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_User(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on status update event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the status update
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser3, user1StatusUpdateId, user3Comment);
		
		// Create the news story to be verified
		commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the comment on status update event, User 2's comment and User 3's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, user2Comment, user3Comment}, TEST_FILTERS, true);
		
		// Users 4 through to 15 will now comment on the status update
		APIProfilesHandler[] usersPostingComments = { profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15 };
		String[] userComments = new String[usersPostingComments.length];
		int index = 0;
		for(APIProfilesHandler userPostingComment : usersPostingComments) {
			// Post the comment to the status update as the specified user
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			CommunityEvents.addStatusUpdateComment(userPostingComment, user1StatusUpdateId, userComments[index]);
			index ++;
		}
		
		// Create the news story to be verified
		commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_UserAndMany(ui, testUser15.getDisplayName(), "13", baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status update event, User 14's comment and User 15's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusEvent, user1StatusUpdate, userComments[10], userComments[11]}, filter, true);
			
			// Verify that none of the comments posted by Users 2 through to User 13 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, userComments[0], userComments[1], userComments[2], userComments[3]}, null, false);
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{userComments[4], userComments[5], userComments[6], userComments[7], userComments[8], userComments[9]}, null, false);
		}
		ui.endTest();
	}	
}