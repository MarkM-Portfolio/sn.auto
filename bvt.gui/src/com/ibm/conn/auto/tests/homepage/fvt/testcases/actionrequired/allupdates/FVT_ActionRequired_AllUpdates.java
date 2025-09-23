package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.allupdates;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author 	Anthony Cox
 *	Date:		3rd November 2015
 */

public class FVT_ActionRequired_AllUpdates extends SetUpMethodsFVT {

	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private boolean user1JoinedNetwork;
	private Community moderatedCommunity;
	private Invitation user2NetworkInvite;
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
		
		// User 1 will now create a moderate community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 2 will now create a public standalone activity and will add User 1 to the activity as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser2, activitiesAPIUser2, testUser1, isOnPremise);
		
		// User 2 will now invite User 1 to join their network
		user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser2, profilesAPIUser1);
		user1JoinedNetwork = false;
	}	
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		// Delete the activity created during the test
		activitiesAPIUser2.deleteActivity(publicActivity);
		
		// Remove User 1 from User 2's network if the network invitation was accepted during the test
		if(user1JoinedNetwork) {
			profilesAPIUser2.deleteUserFromNetworkConnections(profilesAPIUser1);
		}
	}
	
	/**
	* test_ActionRequired_ActioningItemsRemovesFromActionRequired()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 start a moderated community</B></li>
	*<li><B>2. User 2 request to join the community</B></li>
	*<li><b>3. User 2 start an Activity</b></li>
	*<li><b>4. User 2 assign a todo to User 1</b></li>
	*<li><b>5. User 2 invite User 1 to the network</b></li>
	*<li><b>6. User 1 go to Homepage / My Notifications / For Me / All</b></li>
	*<li><b>7. User 1 click the link in AS to accept community join request, accept Network invite from the EE, and click through to complete To Do item</b></li>
	*<li><b>8. User 1 go to Homepage/Action Required / All</b></li>
	*<li><b>Verify: Verify that the stories have been removed from action Required view</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CD9DBA4EBB98777885257D4E003A7A70">TTT: AS - Action Required - 00013 - Actioning Items From My Notifications (All Filter) Removes From Action Required (All Filter)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_ActioningItemsRemovesFromActionRequired() {
		
		String testName = ui.startTest();
		
		// User 2 will now send a request to join User 1's community
		String requestToJoinMessage = CommunityEvents.requestToJoinACommunity(moderatedCommunity, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a to-do item in their activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		Todo todo = ActivityEvents.createTodoAndAssignTodoItemToUser(publicActivity, baseActivityTodo, testUser2, activitiesAPIUser2, profilesAPIUser1);
		
		// Login as User 1 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser1, false);
		
		// Create the news stories to be verified
		String requestToJoinCommunityEvent = CommunityNewsStories.getRequestedToJoinYourCommunityNewsStory(ui, baseCommunity.getName(), testUser2.getDisplayName());
		String invitationToJoinNetworkEvent = ProfileNewsStories.getInvitedYouToBecomeANetworkContactNewsStory_You(ui, testUser2.getDisplayName());
		String assignedToDoEvent = ActivityNewsStories.getAssignedYouAToDoItemNewsStory(ui, baseActivityTodo.getTitle(), baseActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the request to join community event and request message are displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{requestToJoinCommunityEvent, requestToJoinMessage}, null, true);
		
		// Verify that the invitation to join User 2's network event is displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{invitationToJoinNetworkEvent}, null, true);
		
		// Verify that the assigned to-do item event is displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{assignedToDoEvent}, null, true);
		
		// User 1 will now accept the request to join their community (this process is carried out by adding User 2 to the community as a member)
		CommunityEvents.addMemberSingleUser(moderatedCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// User 1 will now accept the invitation to join User 2's network
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUser1, profilesAPIUser2);
		user1JoinedNetwork = true;
		
		// User 2 will now mark the to-do item assigned to User 1 as completed
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser2, activitiesAPIUser2, todo, true);
		
		// Refresh the Action Required view by navigating to I'm Following and then returning to Action Required
		UIEvents.gotoImFollowing(ui);
		UIEvents.gotoActionRequired(ui);
		
		// Verify that the request to join community event and request message are NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{requestToJoinCommunityEvent, requestToJoinMessage}, null, false);
		
		// Verify that the invitation to join User 2's network event is NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{invitationToJoinNetworkEvent}, null, false);
		
		// Verify that the assigned to-do item event is NOT displayed in the Action Required view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{assignedToDoEvent}, null, false);
		
		ui.endTest();
	}
}