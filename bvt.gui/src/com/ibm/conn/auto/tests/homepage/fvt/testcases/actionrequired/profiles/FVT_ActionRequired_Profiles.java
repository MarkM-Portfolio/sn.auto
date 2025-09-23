package com.ibm.conn.auto.tests.homepage.fvt.testcases.actionrequired.profiles;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
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
 *	@author 	Anthony Cox
 *	Date:		30th October 2015
 */
public class FVT_ActionRequired_Profiles extends SetUpMethodsFVT {
	
	private String TEST_FILTERS[];
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private Invitation user1NetworkInvitation;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configurations before any tests are run
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[2];
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS = new String[1];
		}
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		
		// User 1 will now invite User 2 to join their network
		user1NetworkInvitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the invitation to join User 1's network by accepting the invite as User 2 and then having User 1 remove User 2 from their network again
		profilesAPIUser2.acceptNetworkInvitation(user1NetworkInvitation, profilesAPIUser1);
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
	}
	
	/**
	* test_ActionRequired_NetworkInvitesShouldAppear()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Profiles</B></li>
	*<li><B>2. User 1 searches for User 2</B></li>
	*<li><b>3. User 1 invites User 2 to their network</b></li>
	*<li><b>4. User 2 logs into Homepage and goes to Action Required</b></li>
	*<li><b>Verify: User 2 should see a story about the invite network</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A3614D10CE5F5960852579E500416097">TTT: AS - Action Required - 00020 - Network Invites Should Appear When User Is Invited To Join Network</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_ActionRequired_NetworkInvitesShouldAppear() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Action Required view
		LoginEvents.loginAndGotoActionRequired(ui, testUser2, false);
		
		// Create the news story to be verified
		String networkInviteEvent = ProfileNewsStories.getInvitedYouToBecomeANetworkContactNewsStory_You(ui, testUser1.getDisplayName());
		
		// Verify that the network invite event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{networkInviteEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}