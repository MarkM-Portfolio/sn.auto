package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_MultipleMentions extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;	
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(7);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);	
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* addCommunitySU_MultipleMentions() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go into a public community you own</B></li>
	*<li><B>Step: Add a status update with more than 5 user @mentioned</B></li>
	*<li><B>Verify: Verify that the all the @mentioned appear correctly and all get notified about it in there Homepage Activity Stream</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B39DAA920282AF3385257BF6006C717F">TTT - @Mentions - 00003 - User can add multiple @mentions</a></li>	
	*/	
	@Test(groups = {"fvtonprem"})
	public void addCommunitySU_MultipleMentions() {
		
		ui.startTest();
		
		// Create all of the Mentions instances for all of the users to be mentioned
		Mentions user2Mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		Mentions user3Mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, "", "");
		Mentions user4Mentions = MentionsBaseBuilder.buildBaseMentions(testUser4, profilesAPIUser4, serverURL, "", "");
		Mentions user5Mentions = MentionsBaseBuilder.buildBaseMentions(testUser5, profilesAPIUser5, serverURL, "", "");
		Mentions user6Mentions = MentionsBaseBuilder.buildBaseMentions(testUser6, profilesAPIUser6, serverURL, "", "");
		Mentions user7Mentions = MentionsBaseBuilder.buildBaseMentions(testUser7, profilesAPIUser7, serverURL, "", "");
		Mentions[] allUserMentions = { user2Mentions, user3Mentions, user4Mentions, user5Mentions, user6Mentions, user7Mentions };
		
		// Log in as User 1, navigate to the public community and post a status update with mentions to multiple users
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMultipleMentions(publicCommunity, baseCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, allUserMentions, false);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Create the news stories to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = "@" + testUser2.getDisplayName() + " @" + testUser3.getDisplayName() + " @" + testUser4.getDisplayName() + " @" + testUser5.getDisplayName()
								+ " @" + testUser6.getDisplayName() + " @" + testUser7.getDisplayName();
		
		User[] usersToLogin = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7 };
		for(User userToLogin : usersToLogin) {
			// Log in as the specified user and go to the Mentions view
			LoginEvents.loginAndGotoMentions(ui, userToLogin, true);
			
			// Verify that the mentions event and mentions text are displayed in the Mentions view
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
			
			// Log out from Connections
			LoginEvents.logout(ui);
		}
		ui.endTest();
	}
}