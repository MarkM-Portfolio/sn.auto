package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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

public class FVT_GlobalShareboxMention_NonMember extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterSU };
	private final String[] TEST_FILTERS_MY_NOTIFICATIONS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterProfiles };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a restricted community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* globalSharebox_nonMemberMention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to Homepage</B></li>
	*<li><B>Step: User 1 open the Global Sharebox and choose the community picker</B></li>
	*<li><B>Step: User 1 add a private community to post an update to that they are the owner of</B></li>
	*<li><B>Step: User 1 start to add a status update with an @mentions for User 2 - who is not a member of the private community</B></li>
	*<li><B>Step: User 1 select User 2's name for the @mentions from the dialog - verification point 1</B></li>
	*<li><B>Step: User 1 save the update in the private community</B></li>
	*<li><B>Step: User 1 go to the story in the Private Community Activity Stream and look at the name of User 2 - verification point 2</B></li>
	*<li><B>Step: User 2 log into Home / I'm Following / All, Communities & Status Updates</B></li>
	*<li><B>Step: User 2 log into Home / My Notification / For Me  / All, Communities & Profiles</B></li>
	*<li><B>Step: User 2 go to their Profile Activity Stream and filter by All, Communities & Status Updates - verification point 3</B></li>
	*<li><B>Verify: Verify that when the user adds an @mentions to User 2 a message appears saying "Note: The following people do not have access and cannot view or receive notification for this message: <User 2>"</B></li>
	*<li><B>Verify: Verify that when the update is saved with User 2 as an @mentions the name appears without the "@" character in front of it</B></li>
	*<li><B>Verify: Verify that the User 2 being mentioned in the comment story does NOT appeared in any of the views and filters for User 2</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B06186B6F6F3B5685257A9C00316329">TTT - @Mentions - 00072 - Non-member mentioned in Private Community SU from Global Sharebox - message will appear</a></li>	
	*/
	@Test(groups = {"fvtonprem"})
	public void globalSharebox_nonMemberMention() {

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Open the Global Sharebox and verify all Global Sharebox components
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// User 1 will now post a status update to the restricted community which includes mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		UIEvents.postCommunityStatusWithMentionsInGlobalSharebox(ui, driver, restrictedCommunity, mentions, true);
		
		// Create the news story to be verified
		String messagePostedEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String invalidMentionsStatusUpdate = mentions.getBeforeMentionText() + " " + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the message posted to community event and User 1's status message with invalid mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messagePostedEvent, invalidMentionsStatusUpdate}, TEST_FILTERS, true);
		
		// Log out as User 1
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Verify that the message posted to community event and User 1's status message with invalid mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messagePostedEvent, invalidMentionsStatusUpdate}, TEST_FILTERS, false);
		
		// Navigate to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the message posted to community event and User 1's status message with invalid mentions are NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{messagePostedEvent, invalidMentionsStatusUpdate}, null, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the message posted to community event and User 1's status message with invalid mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messagePostedEvent, invalidMentionsStatusUpdate}, TEST_FILTERS_MY_NOTIFICATIONS, false);
		
		// Navigate to the My Profile view
		UIEvents.gotoMyProfile(ui);
		
		// Verify that the message posted to community event and User 1's status message with invalid mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{messagePostedEvent, invalidMentionsStatusUpdate}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}