package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.modcommunity;

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
/* Copyright IBM Corp. 2015, 2016 		                             */
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
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15, profilesAPIUser16, profilesAPIUser17, profilesAPIUser18;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(18);
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
		testUser16 = listOfStandardUsers.get(15);
		testUser17 = listOfStandardUsers.get(16);
		testUser18 = listOfStandardUsers.get(17);
		
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
		profilesAPIUser16 = initialiseAPIProfilesHandlerUser(testUser16);
		profilesAPIUser17 = initialiseAPIProfilesHandlerUser(testUser17);
		profilesAPIUser18 = initialiseAPIProfilesHandlerUser(testUser18);
		
		// User 1 will now create a moderated community with Users 2 through to User 18 added as members
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10, testUser11, testUser12, testUser13, testUser14, testUser15, testUser16, testUser17, testUser18 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);		
	}

	/**
	* test_YourComment_Like_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a Moderated Community you own</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 1 comment on the status update</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 18 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your comment in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your comment in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 18} and 16 others liked your comment in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CE00F59CD3E0BA6685257DEA00563CA9">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00030 - LIKE A COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourComment_Like_Rollup() {

		ui.startTest();
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(moderatedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will now post a comment to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentId = CommunityEvents.addStatusUpdateComment(profilesAPIUser1, user1StatusUpdateId, user1Comment);
		
		// User 2 will now like / recommend the comment posted by User 1
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser2, user1CommentId);
		
		// Log in as User 1 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_User(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the comment posted by User 1
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser3, user1CommentId);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, true);
		
		// Users 4 through to 18 will now like / recommend the comment posted by User 1
		APIProfilesHandler[] usersLikingComment = { profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7, profilesAPIUser8, profilesAPIUser9, profilesAPIUser10, profilesAPIUser11, profilesAPIUser12, profilesAPIUser13, profilesAPIUser14, profilesAPIUser15, profilesAPIUser16, profilesAPIUser17, profilesAPIUser18 };
		CommunityEvents.likeStatusUpdateCommentMultipleUsers(usersLikingComment, user1CommentId);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_UserAndMany(ui, testUser18.getDisplayName(), "16", baseCommunity.getName());
			
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, true);		
				
		ui.endTest();
	}	
}