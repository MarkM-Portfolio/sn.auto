package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.communities.events;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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

public class FVT_YourEvent_Comment_You_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICalendarHandler calendarAPIUser1, calendarAPIUser2, calendarAPIUser3, calendarAPIUser4, calendarAPIUser5, calendarAPIUser6;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4, communitiesAPIUser5, communitiesAPIUser6;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(6);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		communitiesAPIUser5 = initialiseAPICommunitiesHandlerUser(testUser5);
		communitiesAPIUser6 = initialiseAPICommunitiesHandlerUser(testUser6);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		calendarAPIUser2 = initialiseAPICalendarHandlerUser(testUser2);
		calendarAPIUser3 = initialiseAPICalendarHandlerUser(testUser3);
		calendarAPIUser4 = initialiseAPICalendarHandlerUser(testUser4);
		calendarAPIUser5 = initialiseAPICalendarHandlerUser(testUser5);
		calendarAPIUser6 = initialiseAPICalendarHandlerUser(testUser6);
		
		// User 1 will now create a moderated community with multiple followers and with the Events widget added
		User[] usersToFollowCommunity = { testUser2, testUser3, testUser4, testUser5, testUser6 };
		APICommunitiesHandler[] apiUsersToFollowCommunity = { communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4, communitiesAPIUser5, communitiesAPIUser6 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithMultipleFollowersAndAddWidget(baseCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1, usersToFollowCommunity, apiUsersToFollowCommunity);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}

	/**
	* test_YourEvent_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a Moderate Community you own adding the events widget</B></li>
	*<li><B>Step: User 1 start a repeating event</B></li>
	*<li><B>Step: User 1 comment on the event</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 comment on the event</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 6 comment on the event</B></li>
	*<li><B>Step: User 1 comment on the event again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your event {eventName} in the {communityName} community."</B></li>
	*<li><B>Verify: Verify the event shows "You and 5 others commented on your event {eventName} in the {communityName} community."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/36AEE6B7FCFFA43585257DEA004BB97D">TTT - MY NOTIFICATIONS - COMMUNITY CALENDAR - 00011 - COMMENT ON YOUR OWN EVENT ROLLS UP</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_YourEvent_Comment_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 1 will now post their first comment to the calendar event
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, user1Comment1, testUser1, calendarAPIUser1);
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_You(ui, baseEvent.getName(), baseCommunity.getName());
		
		// Verify that the comment on calendar event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now post their first comment to the calendar event
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, user2Comment, testUser2, calendarAPIUser2);
		
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_UserAndYou(ui, baseEvent.getName(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on calendar event, User 1's comment and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), user1Comment1, user2Comment}, TEST_FILTERS, true);
		
		// Users 3 through to User 6 will now comment on the calendar event with User 1 commenting again after all other comments have been posted
		User[] usersPostingComments = { testUser3, testUser4, testUser5, testUser6, testUser1 };
		APICalendarHandler[] apiUsersPostingComments = { calendarAPIUser3, calendarAPIUser4, calendarAPIUser5, calendarAPIUser6, calendarAPIUser1 };
		String commentsPosted[] = new String[usersPostingComments.length];
		int index = 0;
		while(index < usersPostingComments.length) {
			// Set the comment to be posted
			commentsPosted[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			// The user will now post a comment to the calendar event
			CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, commentsPosted[index], usersPostingComments[index], apiUsersPostingComments[index]);
			
			index ++;
		}
		// Create the news story to be verified
		commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_YouAndMany(ui, "5", baseEvent.getName(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on calendar event, User 6's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), commentsPosted[3], commentsPosted[4]}, filter, true);
			
			// Verify that User 1's first comment and all comments posted by User 2 through to User 5 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment, commentsPosted[0], commentsPosted[1], commentsPosted[2]}, null, false);
		}
		ui.endTest();
	}
}