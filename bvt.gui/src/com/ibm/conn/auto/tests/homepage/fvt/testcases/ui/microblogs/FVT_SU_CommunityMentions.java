package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017	                                 */
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

public class FVT_SU_CommunityMentions extends SetUpMethodsFVT {
	
	private String TEST_FILTERS_IM_FOLLOWING[];
	private String TEST_FILTERS_MY_NOTIFICATIONS[];
	private String TEST_FILTERS_PROFILES[];

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;	
	private Community restrictedCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// Assign the filters to be used for verifications depending on whether the test is being run On Premise / Smart Cloud
		if(isOnPremise) {
			TEST_FILTERS_IM_FOLLOWING = new String[4];
			TEST_FILTERS_IM_FOLLOWING[3] = HomepageUIConstants.FilterPeople;
			
			TEST_FILTERS_MY_NOTIFICATIONS = new String[3];
			TEST_FILTERS_MY_NOTIFICATIONS[2] = HomepageUIConstants.FilterProfiles;
			
			TEST_FILTERS_PROFILES = new String[4];
			TEST_FILTERS_PROFILES[3] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS_IM_FOLLOWING = new String[3];
			TEST_FILTERS_MY_NOTIFICATIONS = new String[2];
			TEST_FILTERS_PROFILES = new String[3];
		}
		TEST_FILTERS_IM_FOLLOWING[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_IM_FOLLOWING[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS_IM_FOLLOWING[2] = HomepageUIConstants.FilterSU;
		
		TEST_FILTERS_MY_NOTIFICATIONS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_MY_NOTIFICATIONS[1] = HomepageUIConstants.FilterCommunities;
		
		TEST_FILTERS_PROFILES[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS_PROFILES[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS_PROFILES[2] = HomepageUIConstants.FilterSU;
		
		// User 1 will now create a restricted community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* privateCommunitySU_MemberMention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a Private Community you own that User 2 is a member of</B></li>
	*<li><B>Step: User 1 add a status update with an @mentions to User 2 in the community</B></li>
	*<li><B>Step: User 2 log into Home / I'm Following / All & Status Updates - #verification point 1</B></li>
	*<li><B>Step: User 2 log into Home / My Notification / For Me  / All & Profiles - #verification point 2</B></li>
	*<li><B>Step: User 2 go to their Profile Activity Stream and filter by All & Status Updates - #verification point 1</B></li>
	*<li><B>Verify: Verify that the community status update story has appeared in all views and filters EXCEPT PROFILES for User 2</B></li>
	*<li><B>Verify: Verify that the community status update story has NOT appeared in the My Notifications view and filters for User 2</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9E369F2F972BF48585257A77003474FB">TTT - @Mentions - 00064 - User mentioned in a Private Community SU can see the story - member</a></li>	
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunitySU_MemberMention() {

		ui.startTest();
		
		// User 1 will now log in to the restricted community and will post a status update with mentions to User 2 (who is a member)
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(restrictedCommunity, baseCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, false);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Create the news story to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS_IM_FOLLOWING) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			boolean verifyAsDisplayed;
			if(filter.equals(HomepageUIConstants.FilterPeople) || filter.equals(HomepageUIConstants.FilterProfiles)) {
				verifyAsDisplayed = false;
			} else {
				verifyAsDisplayed = true;
			}
			// Verify that the mentions event and mentions text are displayed / NOT displayed in the current view
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, filter, verifyAsDisplayed);
		}
		// Navigate to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentions event and mentions test are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		for(String filter : TEST_FILTERS_MY_NOTIFICATIONS) {
			// Filter the UI by the specified filter
			UIEvents.filterBy(ui, filter);
			
			boolean verifyAsDisplayed;
			if(filter.equals(HomepageUIConstants.FilterProfiles)) {
				verifyAsDisplayed = false;
			} else {
				verifyAsDisplayed = true;
			}
			// Verify that the mentions event and mentions text are displayed / NOT displayed in the current view
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, filter, verifyAsDisplayed);
		}
		// Navigate to the My Profiles view
		UIEvents.gotoMyProfile(ui);
		
		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsText}, TEST_FILTERS_PROFILES, false);
				
		ui.endTest();
	}

	/**
	* privateCommunitySU_nonMemberMention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a Private Community that you own and User 2 is not a member of</B></li>
	*<li><B>Step: User 1 go to a community status update there and add a comment</B></li>
	*<li><B>Step: User 1 in the comment have a @mentions to User 2 who is not a member of the Private Community - verification point 1</B></li>
	*<li><B>Step: User 1 save the update in the private community</B></li>
	*<li><B>Step: User 1 go to the story in the Private Community Activity Stream and look at the name of User 2 - verification point 2</B></li>
	*<li><B>Step: User 2 log into Home / I'm Following / All & Status Updates</B></li>
	*<li><B>Step: User 2 log into Home / My Notification / For Me  / All & Profiles</B></li>
	*<li><B>Step: User 2 go to their Profile Activity Stream and filter by All & Status Updates - verification point 3</B></li>
	*<li><B>Verify: Verify that when the user adds an @mentions to User 2 a message appears saying "Note: The following people do not have access and cannot view or receive notification for this message: <User 2>"</B></li>
	*<li><B>Verify: Verify that when the update is saved with User 2 as an @mentions the name appears without the "@" character in front of it</B></li>
	*<li><B>Verify: Verify that the User 2 being mentioned in the comment story does NOT appeared in any of the views and filters for User 2</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F2E9AF5F7D6604C785257A77003CDDDC">TTT - @Mentions - 00069 - Non-member mentioned in a comment on a Private Community SU - message will appear</a></li>	
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void privateCommunitySU_nonMemberMention() {
		
		ui.startTest();
		
		// User 1 will now log in to the restricted community and will post a status update with mentions to User 3 (who is a NOT member)
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(restrictedCommunity, baseCommunity, ui, driver, uiCo, testUser1, communitiesAPIUser1, mentions, false, true);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, true);
		
		// Create the news story to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " " + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsText}, TEST_FILTERS_IM_FOLLOWING, false);
		
		// Navigate to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentions event and mentions test are NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsText}, TEST_FILTERS_MY_NOTIFICATIONS, false);
				
		// Navigate to the My Profiles view
		UIEvents.gotoMyProfile(ui);
		
		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsText}, TEST_FILTERS_PROFILES, false);
				
		ui.endTest();
	}
}