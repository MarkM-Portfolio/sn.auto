package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
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

public class FVT_Mentions_Activity_PrivateCommunity_NonMember extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };

	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community privateCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
	
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 will now create a restricted community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		privateCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now add an activity to the community
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, privateCommunity);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(privateCommunity);
	}
	
	/**
	* activities_directedMention_privateCommunity_entry_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add an activity to the private community</B></li>
	*<li><B>Step: testUser1 add an activity entry mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: User 2 verify the event for the mentions does NOT appear</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E4D396CF7E49C7A485257C93004EAC48">TTT - @MENTIONS - 046 - MENTIONS DIRECTED TO YOU IN ACTIVITY ENTRY - PRIVATE COMMUNITY ACTIVITY - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_privateCommunity_entry_nonMember(){

		ui.startTest();

		// User 1 will now add an entry to the community activity mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		ActivityEntry activityEntry = CommunityActivityEvents.createActivityEntryWithMentions(communityActivity, mentions, testUser1, activitiesAPIUser1, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityActivityNewsStories.getMentionedYouInTheEntryNewsStory(ui, activityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, false);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_privateCommunity_entryComment_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add an activity to the private community</B></li>
	*<li><B>Step: testUser1 add an activity entry</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FDC08EB9A31404C985257C6F007D72FE">TTT - @MENTIONS - 046 - MENTIONS DIRECTED TO YOU IN ACTIVITY ENTRY COMMENT - PRIVATE COMMUNITY ACTIVITY - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_privateCommunity_entryComment_nonMember(){

		String testName = ui.startTest();

		// User 1 will now add an entry to the community activity with a comment mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityEntryAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, mentions, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityActivityNewsStories.getMentionedYouInACommentOnTheEntryNewsStory(ui, baseActivityEntry.getTitle(), communityActivity.getTitle(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityEntry.getDescription().trim()}, null, false);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_privateCommunity_toDo_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add an activity to the private community</B></li>
	*<li><B>Step: testUser1 add an activity to do item mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A5E74C7EC8FEFA6085257C93004EAC4F">TTT - @MENTIONS - 056 - MENTIONS DIRECTED TO YOU IN ACTIVITY TO-DO - PRIVATE COMMUNITY ACTIVITY - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_privateCommunity_toDo_nonMember(){

		ui.startTest();

		// User 1 will now add a to-do item to the community activity mentioning to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		Todo todo = CommunityActivityEvents.createActivityTodoWithMentions(communityActivity, mentions, testUser1, activitiesAPIUser1, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityActivityNewsStories.getMentionedYouInTheToDoItemNewsStory(ui, todo.getTitle(), communityActivity.getTitle(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, false);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	* activities_directedMention_privateCommunity_toDoComment_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add an activity to the private community</B></li>
	*<li><B>Step: testUser1 add an activity to do item</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in both views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/119C297BD6385A6C85257C6F007E0447">TTT - @MENTIONS - 056 - MENTIONS DIRECTED TO YOU IN ACTIVITY TO-DO COMMENT - PRIVATE COMMUNITY ACTIVITY - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void activities_directedMention_privateCommunity_toDoComment_nonMember(){

		String testName = ui.startTest();

		// User 1 will now add an to-do item to the community activity with a comment mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseActivityToDo baseActivityToDo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, false);
		CommunityActivityEvents.createActivityTodoAndAddCommentWithMentions(testUser1, activitiesAPIUser1, baseActivityToDo, communityActivity, mentions, false);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityActivityNewsStories.getMentionedYouInACommentOnTheToDoItemNewsStory(ui, baseActivityToDo.getTitle(), communityActivity.getTitle(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityToDo.getDescription().trim()}, null, false);
		
		// Navigate to I'm Following
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT seen in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseActivityToDo.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
}