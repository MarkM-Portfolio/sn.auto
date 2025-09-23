package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.statusupdates.privatecommunity;

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
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 creates a private community with User 2 and User 3 as members
		User[] usersBeingAdded = { testUser2, testUser3 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, usersBeingAdded);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* test_YourComment_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a private Community you own and User 2 and User 3 are members</B></li>
	*<li><B>Step: User 1 add a status update in the community</B></li>
	*<li><B>Step: User 1 comment on the status update</B></li>
	*<li><B>Step: User 1 like their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Communities</B></li>
	*<li><B>Step: User 2 like the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 add another comment on the status update</B></li>
	*<li><B>Step: User 3 like this comment</B></li>
	*<li><B>Step: User 1 like this comment also</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Status Updates, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you liked your comment in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "You and 2 others liked your comment in the {communityName} community"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63372CD164BD678F85257DEA00563CAA">TTT - MY NOTIFICATIONS - COMMUNITY MICROBLOGGING - 00031 - LIKE YOUR OWN COMMENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourComment_Like_You_Others_Rollup() {

		ui.startTest();
		
		// User 1 will now post a status update to the community
		String statusMessage = Data.getData().UpdateStatus + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, statusMessage);
		
		// User 1 will now post a comment to the community status update and will like / recommend the comment
		String statusComment = Data.getData().StatusComment + Helper.genStrongRand();
		String commentId = CommunityEvents.addStatusUpdateCommentAndLikeComment(profilesAPIUser1, profilesAPIUser1, statusUpdateId, statusComment);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_You(ui, baseCommunity.getName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusMessage, statusComment}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend the comment
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser2, commentId);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_UserAndYou(ui, baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusMessage, statusComment}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the comment
		CommunityEvents.likeStatusUpdateComment(profilesAPIUser3, commentId);
		
		// Create the news story to be verified
		likeCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_YouAndMany(ui, "2", baseCommunity.getName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusMessage, statusComment}, TEST_FILTERS, true);
		
		ui.endTest();	
	}	
}