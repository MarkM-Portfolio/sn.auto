package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.allupdates;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author 	Anthony Cox
 *	Date:		27th January 2016
 */

public class FVT_ActionRequired_AllUpdates_AllFilter extends SetUpMethodsFVT {

	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseModeratedCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, restrictedCommunity;
	private Invitation invitation = null;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser1, communitiesAPIUser1);
		
		// User 2 will now create a public activity and add User 1 to the activity as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser2, activitiesAPIUser2, testUser1, isOnPremise);
		
		// User 2 will now create a restricted community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser2, communitiesAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
		
		// Delete the activity created during the test
		activitiesAPIUser2.deleteActivity(publicActivity);
		
		if(invitation != null) {
			// Delete the invitation to join User 2's network - by accepting the invitation and then having User 2 remove User 1 from their network again
			profilesAPIUser1.acceptNetworkInvitation(invitation, profilesAPIUser2);
			profilesAPIUser2.deleteUserFromNetworkConnections(profilesAPIUser1);
		}
	}
	
	/**
	* test_ActionRequired_AllFilterIsPopulated()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 start a moderated community</B></li>
	*<li><B>2. User 2 request to join the community</B></li>
	*<li><b>3. User 2 start an Activity</b></li>
	*<li><b>4. User 2 assign a todo to User 1</b></li>
	*<li><b>5. User 2 invite User 1 to the network</b></li>
	*<li><b>6. User 2 invite User 1 to a Private Community they own</b></li>
	*<li><b>7. User 1 go to Homepage / Action Required / All and My Notifications / For Me / All</b></li>
	*<li><b>Verify: Verify that the request to join the community, the todo ,the network invite and the community invite events show up in the All Filter of Action Required and my Notifications</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D0A1702B119EE2318525792F004D9EAC">TTT: AS - Action Required - 00011 - All Filter Of Action Required Is Populated</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_AllFilterIsPopulated() {
		
		String testName = ui.startTest();
		
		// User 2 will now request to join the community
		String communityRequestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a to-do item in the activity and will assign it to User 1
		BaseActivityToDo baseActivityToDo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityToDo, testUser2, activitiesAPIUser2, profilesAPIUser1);
		
		// User 2 will now invite User 1 to join their network
		invitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser2, profilesAPIUser1);
				
		// User 2 will now invite User 1 to join the restricted community
		CommunityEvents.inviteUserToJoinCommunity(restrictedCommunity, testUser2, communitiesAPIUser2, profilesAPIUser1);
		
		// Log in as User 1 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories to be verified in My Notifications
		String communityInviteEvent = CommunityNewsStories.getInvitedYouToJoinTheCommunityNewsStory(ui, baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		String networkInviteEvent = ProfileNewsStories.getInvitedYouToBecomeANetworkContactNewsStory_You(ui, testUser2.getDisplayName());
		String todoUpdatedEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseActivityToDo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		String communityRequestEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that all items are displayed in My Notifications view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityInviteEvent, baseRestrictedCommunity.getDescription(), networkInviteEvent, todoUpdatedEvent, baseActivityToDo.getDescription(), communityRequestEvent, communityRequestToJoinMessage}, null, true);
		
		// Navigate to the Action Required view
		ui.gotoActionRequired();
		
		// Create the news stories required for the Action Required view
		String todoAssignedEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityToDo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that all items are displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityInviteEvent, baseRestrictedCommunity.getDescription(), networkInviteEvent, todoAssignedEvent, baseActivityToDo.getDescription(), communityRequestEvent, communityRequestToJoinMessage}, null, true);
		
		ui.endTest();
	}
}