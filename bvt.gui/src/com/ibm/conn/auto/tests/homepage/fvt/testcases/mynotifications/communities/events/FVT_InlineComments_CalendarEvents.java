package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.events;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityCalendarNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016          		                     */
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

public class FVT_InlineComments_CalendarEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICalendarHandler calendarAPIUser1, calendarAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		calendarAPIUser2 = initialiseAPICalendarHandlerUser(testUser2);
		
		// User 1 will now create a moderated community with User 2 added as a follower and with the Events widget added
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseModeratedCommunity, testUser2, communitiesAPIUser2, BaseWidget.EVENTS, testUser1, communitiesAPIUser1, isOnPremise);
		
		// User 1 will now create a public community with User 2 added as a follower and with the Events widget added
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(basePublicCommunity, testUser2, communitiesAPIUser2, BaseWidget.EVENTS, testUser1, communitiesAPIUser1, isOnPremise);
		
		// User 1 will now create a restricted community with User 2 added as a member and follower and with the Events widget added
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseRestrictedCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1, BaseWidget.EVENTS, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_RepeatingEvent_FinalTwoComments_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a community calendar repeating event</B></li>
	*<li><B>Step: User 1 add a comment</B></li>
	*<li><B>Step: User 2 add 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 delete their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D2F0E7CC217ED9E85257E2F0036A45D">TTT - INLINE COMMENTS - 00030 - COMMUNITY CALENDAR EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_RepeatingEvent_FinalTwoComments_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar repeatingCalendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 1 will now post a comment to the calendar event
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommentToEvent user1CommentOnEvent = CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user1Comment1, testUser1, calendarAPIUser1);
		
		// User 2 will now post two comments to the calendar event
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment1, testUser2, calendarAPIUser2);
		
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment2, testUser2, calendarAPIUser2);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_UserAndYou(ui, baseEvent.getName(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		
		// User 1 will now delete their comment from the calendar event
		CommunityCalendarEvents.deleteCommentFromCalendarEvent(user1CommentOnEvent, testUser1, calendarAPIUser1);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		ui.endTest();
	}	

	/**
	* test_RepeatingEvent_FinalTwoComments_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a community calendar repeating event</B></li>
	*<li><B>Step: User 1 add a comment</B></li>
	*<li><B>Step: User 2 add 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 delete their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D2F0E7CC217ED9E85257E2F0036A45D">TTT - INLINE COMMENTS - 00030 - COMMUNITY CALENDAR EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_RepeatingEvent_FinalTwoComments_ModCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar repeatingCalendarEvent = CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 1 will now post a comment to the calendar event
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommentToEvent user1CommentOnEvent = CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user1Comment1, testUser1, calendarAPIUser1);
		
		// User 2 will now post two comments to the calendar event
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment1, testUser2, calendarAPIUser2);
		
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment2, testUser2, calendarAPIUser2);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_UserAndYou(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		
		// User 1 will now delete their comment from the calendar event
		CommunityCalendarEvents.deleteCommentFromCalendarEvent(user1CommentOnEvent, testUser1, calendarAPIUser1);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		ui.endTest();
	}	

	/**
	* test_RepeatingEvent_FinalTwoComments_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 add a community calendar repeating event</B></li>
	*<li><B>Step: User 1 add a comment</B></li>
	*<li><B>Step: User 2 add 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 delete their comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8D2F0E7CC217ED9E85257E2F0036A45D">TTT - INLINE COMMENTS - 00030 - COMMUNITY CALENDAR EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_RepeatingEvent_FinalTwoComments_PrivateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar repeatingCalendarEvent = CommunityCalendarEvents.addCalendarEvent(restrictedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 1 will now post a comment to the calendar event
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommentToEvent user1CommentOnEvent = CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user1Comment1, testUser1, calendarAPIUser1);
		
		// User 2 will now post two comments to the calendar event
		String user2Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment1, testUser2, calendarAPIUser2);
		
		String user2Comment2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(repeatingCalendarEvent, user2Comment2, testUser2, calendarAPIUser2);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_UserAndYou(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		
		// User 1 will now delete their comment from the calendar event
		CommunityCalendarEvents.deleteCommentFromCalendarEvent(user1CommentOnEvent, testUser1, calendarAPIUser1);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and both of User 2's comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment1, user2Comment2}, filter, true);
			
			// Verify that User 1's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1}, null, false);
		}
		ui.endTest();
	}
}