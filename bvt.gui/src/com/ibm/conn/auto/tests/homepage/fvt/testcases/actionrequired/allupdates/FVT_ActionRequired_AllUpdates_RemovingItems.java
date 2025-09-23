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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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

public class FVT_ActionRequired_AllUpdates_RemovingItems extends SetUpMethodsFVT {

	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 2 will now create a public activity and add User 1 to the activity as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser2, activitiesAPIUser2, testUser1, isOnPremise);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		// Delete the activity created during the test
		activitiesAPIUser2.deleteActivity(publicActivity);
		
		if(invitation != null) {
			// Delete the invitation to join User 2's network - by accepting the invitation and then having User 2 remove User 1 from their network again
			profilesAPIUser1.acceptNetworkInvitation(invitation, profilesAPIUser2);
			profilesAPIUser2.deleteUserFromNetworkConnections(profilesAPIUser1);
		}
	}
	
	/**
	* test_ActionRequired_RemovingItemsFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 start a moderated community</B></li>
	*<li><B>2. User 2 request to join the community</B></li>
	*<li><b>3. User 2 start an Activity</b></li>
	*<li><b>4. User 2 assign a todo to User 1</b></li>
	*<li><b>5. User 2 invite User 1 to the network</b></li>
	*<li><b>6. User 1 go to Homepage / Action Required / All</b></li>
	*<li><b>7. User 1 click the "X" that appears on each story when you hover over it</b></li>
	*<li><b>Verify: Verify that when the user clicks the "X" a prompt appears and when they say "Ok" the story is removed and the badge number decrements</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6E50839B66C89EC08525792F005309D0">TTT: AS - Action Required - 00012 - Removing Items From Action Required (All Filter)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_RemovingItemsFromActionRequired() {
		
		String testName = ui.startTest();
		
		// User 2 will now request to join the community
		String communityRequestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a to-do item in the activity and will assign it to User 1
		BaseActivityToDo baseActivityToDo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityToDo, testUser2, activitiesAPIUser2, profilesAPIUser1);
		
		// User 2 will now invite User 1 to join their network
		invitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser2, profilesAPIUser1);
		
		// Log in as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Retrieve the action required badge value before any news stories have been removed from the view
		int actionRequiredCounterBefore = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Create the news stories to be verified
		String requestToJoinCommunityEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseCommunity.getName(), testUser2.getDisplayName());
		String networkInviteEvent = ProfileNewsStories.getInvitedYouToBecomeANetworkContactNewsStory_You(ui, testUser2.getDisplayName());
		String todoAssignedEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityToDo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the request to join community event is displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{requestToJoinCommunityEvent, communityRequestToJoinMessage}, null, true);
		
		// Remove the request to join the community event from the Action Required view using the UI
		UIEvents.removeNewsStoryFromActionRequiredViewUsingUI(ui, requestToJoinCommunityEvent);
		
		// Verify that the request to join community event is NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{requestToJoinCommunityEvent, communityRequestToJoinMessage}, null, false);
		
		// Retrieve the action required badge value after the community invitation event has been removed from the view
		int actionRequiredCounterAfter = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required counter value has decremented by 1
		HomepageValid.verifyIntValuesAreEqual(actionRequiredCounterAfter, (actionRequiredCounterBefore - 1));
		
		/**
		 * PLEASE NOTE: The removal of the 'news story removed successfully' message is necessary as the presence of this message 
		 * 				needs to be re-verified again when the next news story is removed.
		 */
		UIEvents.hideEntrySuccessfullyRemovedMessageInActionRequiredUsingUI(ui);
		
		// Verify that the network invitation event is displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{networkInviteEvent}, null, true);
		
		// Remove the network invitation event from the Action Required view using the UI
		UIEvents.removeNewsStoryFromActionRequiredViewUsingUI(ui, networkInviteEvent);
		
		// Verify that the network invite event is NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{networkInviteEvent}, null, false);
		
		// Retrieve the action required badge value after the network invitation event has been removed from the view
		actionRequiredCounterAfter = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required counter value has decremented by another 1 (ie. the total decrement for the counter is now 2)
		HomepageValid.verifyIntValuesAreEqual(actionRequiredCounterAfter, (actionRequiredCounterBefore - 2));
		
		/**
		 * PLEASE NOTE: The removal of the 'news story removed successfully' message is necessary as the presence of this message 
		 * 				needs to be re-verified again when the next news story is removed.
		 */
		UIEvents.hideEntrySuccessfullyRemovedMessageInActionRequiredUsingUI(ui);
		
		// Verify that the to-do item assigned event is displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{todoAssignedEvent, baseActivityToDo.getDescription()}, null, true);
		
		// Remove the to-do item assigned event from the Action Required view using the UI
		UIEvents.removeNewsStoryFromActionRequiredViewUsingUI(ui, todoAssignedEvent);
		
		// Verify that the to-do item assigned event is NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{todoAssignedEvent, baseActivityToDo.getDescription()}, null, false);
		
		// Retrieve the action required badge value after the to-do item assigned event has been removed from the view
		actionRequiredCounterAfter = UIEvents.getActionRequiredBadgeValue(driver);
		
		// Verify that the Action Required counter value has decremented by another 1 (ie. the total decrement for the counter is now 3)
		HomepageValid.verifyIntValuesAreEqual(actionRequiredCounterAfter, (actionRequiredCounterBefore - 3));
				
		ui.endTest();
	}	
}