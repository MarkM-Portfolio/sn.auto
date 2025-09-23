package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.calendarevent;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityCalendarNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_CalendarEvent_PublicCommunity extends SetUpMethodsFVT {

	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		
		// User 1 will now create a public community with the Events widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* calendarEvents_directedMention_publicCommunity_event() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 add the events widget to the public community</B></li>
	*<li><B>Step: testUser1 create an event mentioning testUser2 in the description</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that the mentions event appears in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F410A90923C3350185257C98005ACDF8">TTT - @MENTIONS - 120 - MENTIONS DIRECTED TO YOU IN CALENDAR EVENT - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEvents_directedMention_publicCommunity_event() {
		
		String testName = ui.startTest();

		// User 1 will now add a calendar event to the community - the event will mention User 2 in the event description
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEventWithMentions(publicCommunity, baseEvent, mentions, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news stories to be verified
		String mentionsEvent = CommunityCalendarNewsStories.getMentionedYouInTheEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String descriptionWithMentions = baseEvent.getDescription().trim() + " " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, descriptionWithMentions}, null, true);
		
		ui.endTest();
	}

	/**
	* calendarEvents_directedMention_publicCommunity_eventComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 add the events widget to the public community</B></li>
	*<li><B>Step: testUser1 create an event</B></li>
	*<li><B>Step: testUser1 mentioning testUser2 in a comment on the event</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that the mentions event appears in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1C9DCE9D3EED679A85257C980058F81F">TTT - @MENTIONS - 110 - MENTIONS DIRECTED TO YOU IN CALENDAR EVENT COMMENT - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEvents_directedMention_publicCommunity_eventComment() {

		/**
		 * When run locally alongside the other test case, User 2's mention in the comment returns a 201: CREATED in the console log yet doesn't appear in the UI.
		 * When this test is isolated and run by itself - User 2's mention in the comment appears in the UI as expected.
		 * 
		 * Therefore to work around this issue, User 3 is used as User 2 in this test case.
		 */
		String testName = ui.startTest();

		// User 1 will now add a calendar event to the community and will then post a comment with mentions to User 2 to the calendar event
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEventAndAddCommentWithMentions(publicCommunity, baseEvent, mentions, testUser1, calendarAPIUser1);
				
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser3, false);
				
		// Create the news stories to be verified
		String mentionsEvent = CommunityCalendarNewsStories.getMentionedYouInACommentOnTheEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String commentWithMentions = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, baseEvent.getDescription(), commentWithMentions}, null, true); 	
		
		ui.endTest();
	}
}
