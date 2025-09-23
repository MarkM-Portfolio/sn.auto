package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

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
 * @author Patrick Doherty
 */

public class FVT_Mentions_PublicActivity extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS_IM_FOLL_VIEW = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };
	
	private Activity publicActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseActivity baseActivity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public standalone activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		publicActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(publicActivity);		
	}

	/**
	* activities_directedMention_publicActivityEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public activity</B></li>
	*<li><B>Step: testUser1 add an activity entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify the event for the mentions appears in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C4B22A52B497B65D85257C93004EAC42">TTT - @MENTIONS - 040 - MENTIONS DIRECTED TO YOU IN ACTIVITY ENTRY - PUBLIC ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_publicActivityEntry() {

		ui.startTest();
		
		// User 1 will now create an activity entry which includes mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		ActivityEntry activityEntry = ActivityEvents.createActivityEntryWithMentions(publicActivity, mentions, testUser1, activitiesAPIUser1, false);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news stories to be verified
		String mentionedYouEvent = ActivityNewsStories.getMentionedYouInTheEntryNewsStory(ui, activityEntry.getTitle().trim(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the mentions event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsText}, TEST_FILTERS_IM_FOLL_VIEW, true);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_publicActivityToDo() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public activity</B></li>
	*<li><B>Step: testUser1 add an activity todo mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify the event for the mentions appears in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C3E5FB384348416B85257C93004EAC49">TTT - @MENTIONS - 050 - MENTIONS DIRECTED TO YOU IN ACTIVITY TO-DO - PUBLIC ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_publicActivityToDo() {

		ui.startTest();
		
		// User 1 will now add a to-do item to the community activity mentioning to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		Todo todo = ActivityEvents.createActivityTodoWithMentions(publicActivity, mentions, testUser1, activitiesAPIUser1, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = ActivityNewsStories.getMentionedYouInTheToDoItemNewsStory(ui, todo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, true);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS_IM_FOLL_VIEW, true);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_publicActivityEntry_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public activity</B></li>
	*<li><B>Step: testUser1 add an activity entry</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify the event for the mentions appears in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6B73DDDBFA585FBF85257C6F007C8B41">TTT - @MENTIONS - 040 - MENTIONS DIRECTED TO YOU IN ACTIVITY ENTRY COMMENT - PUBLIC ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_publicActivityEntry_Comment() {

		String testName = ui.startTest();

		// User 1 will now add an entry to the community activity with a comment mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityEntry, publicActivity, mentions, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = ActivityNewsStories.getMentionedYouInACommentOnTheEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityEntry.getDescription().trim()}, null, true);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityEntry.getDescription().trim()}, TEST_FILTERS_IM_FOLL_VIEW, true);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_publicActivityToDo_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public activity</B></li>
	*<li><B>Step: testUser1 add an activity todo</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify the event for the mentions appears in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/652DDFB13E92132585257C6F007E0441">TTT - @MENTIONS - 050 - MENTIONS DIRECTED TO YOU IN ACTIVITY TO-DO COMMENT - PUBLIC ACTIVITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_publicActivityToDo_Comment() {

		String testName = ui.startTest();

		// User 1 will now add an to-do item to the community activity with a comment mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityToDo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicActivity, false);
		ActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityToDo, publicActivity, mentions, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = ActivityNewsStories.getMentionedYouInACommentOnTheToDoItemNewsStory(ui, baseActivityToDo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityToDo.getDescription().trim()}, null, true);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityToDo.getDescription().trim()}, TEST_FILTERS_IM_FOLL_VIEW, true);
		
		ui.endTest();
	}
}