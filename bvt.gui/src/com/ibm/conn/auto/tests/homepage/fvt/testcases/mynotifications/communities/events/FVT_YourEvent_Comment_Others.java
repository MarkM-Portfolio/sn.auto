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

public class FVT_YourEvent_Comment_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICalendarHandler calendarAPIUser1, calendarAPIUser2, calendarAPIUser3, calendarAPIUser4;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		calendarAPIUser2 = initialiseAPICalendarHandlerUser(testUser2);
		calendarAPIUser3 = initialiseAPICalendarHandlerUser(testUser3);
		calendarAPIUser4 = initialiseAPICalendarHandlerUser(testUser4);
		
		// User 1 will now create a public community with multiple followers and with the Events widget added
		User[] usersToFollowCommunity = { testUser2, testUser3, testUser4 };
		APICommunitiesHandler[] apiUsersToFollowCommunity = { communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithMultipleFollowersAndAddWidget(baseCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1, usersToFollowCommunity, apiUsersToFollowCommunity);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourEvent_Comment_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a single event</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 comment on the event</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 comment on the event</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 comment on the event</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} commented on your event {eventName} in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} commented on your event {eventName} in the{communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "{user 4} and 2 others commented on your event {eventName} in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/08F24328F9DC901A85257DEA004BB97C">TTT - MY NOTIFICATIONS - COMMUNITY CALENDAR - 00010 - COMMENT ON EVENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEvent_Comment_Rollup() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a single calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 2 will now post a comment to the calendar event
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, user2Comment, testUser2, calendarAPIUser2);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on calendar event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment}, TEST_FILTERS, true);
		
		// User 3 will now post a comment to the calendar event
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, user3Comment, testUser3, calendarAPIUser3);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_TwoUsers(ui, testUser2.getDisplayName(), baseEvent.getName(), baseCommunity.getName(), testUser3.getDisplayName());
		
		// Verify that the comment on calendar event, User 2's comment and User 3's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user2Comment, user3Comment}, TEST_FILTERS, true);
		
		// User 4 will now post a comment to the calendar event
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, user4Comment, testUser4, calendarAPIUser4);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_UserAndMany(ui, testUser4.getDisplayName(), "2", baseEvent.getName(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on calendar event, User 3's comment and User 4's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user3Comment, user4Comment}, filter, true);
			
			// Verify that the comment posted by User 2 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, false);
		}
		ui.endTest();
	}	
}