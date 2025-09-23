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
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_PrivateCommunityMicroblogEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a restricted community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_SU_FinalTwoComments_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a community they own</B></li>
	*<li><B>Step: User 1 add a community status update</B></li>
	*<li><B>Step: User 2 like the status update</B></li>
	*<li><B>Step: User 2 comment on the status update</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 3 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the comment displays inline</B></li>
	*<li><B>Verify: Verify the last 2 comments display inline and the show previous link is there</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F213D716457D8E285257E2F0036A45E">TTT - INLINE COMMENTS - 00040 - COMMUNITY MICROBLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_SU_FinalTwoComments_PrivateCommunity(){

		ui.startTest();
		
		// User 1 will now add a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateId = CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 2 will now like / recommend the community status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser2, user1StatusUpdateId);
		
		// User 2 will now post a comment to the status update
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment1);
		
		// Log in as User 1 and go to the My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories to be verified
		String commentOnStatusEvent = CommunityNewsStories.getCommentOnYourMessageNewsStory_User(ui, baseCommunity.getName(), testUser2.getDisplayName());
		String likeStatusEvent = CommunityNewsStories.getLikeYourMessageNewsStory_User(ui, baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on status event, like status event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnStatusEvent, likeStatusEvent, user1StatusUpdate, user2Comment1}, TEST_FILTERS, true);
		
		// User 2 will now post another 3 comments to the status update
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment2);
		
		String user2Comment3 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment3);
		
		String user2Comment4 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser2, user1StatusUpdateId, user2Comment4);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the like status update event, comment on status update event, User 2's third comment and User 2's fourth comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusEvent, likeStatusEvent, user1StatusUpdate, user2Comment3, user2Comment4}, filter, true);
			
			// Verify that the first two comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1, user2Comment2}, null, false);
		}
		ui.endTest();
	}	
}