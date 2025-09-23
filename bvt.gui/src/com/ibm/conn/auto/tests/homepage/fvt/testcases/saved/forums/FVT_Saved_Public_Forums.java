package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 *	Author: 	Anthony Cox
 *	Date:		23rd September 2015
 */

public class FVT_Saved_Public_Forums extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };
	
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseForum baseForum1, baseForum2;
	private Forum standaloneForum1, standaloneForum2;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create the first public standalone forum with User 2 added as a follower
		baseForum1 = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum1 = ForumEvents.createForumWithOneFollower(testUser1, forumsAPIUser1, testUser2, forumsAPIUser2, baseForum1);
		
		// User 1 will now create a second public standalone forum with User 2 added as a follower
		baseForum2 = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum2 = ForumEvents.createForumWithOneFollower(testUser1, forumsAPIUser1, testUser2, forumsAPIUser2, baseForum2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the forums created during the test
		forumsAPIUser1.deleteForum(standaloneForum1);
		forumsAPIUser1.deleteForum(standaloneForum2);
	}
	
	/**
	* test_PublicForum_MarkingForumStoriesAsSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Forums</B></li>
	*<li><B>2. User 1 start a public forum - User 2 follow this forum</B></li>
	*<li><B>3. User 1 add a topic to the forum</B></li>
	*<li><B>4. User 1 go to Homepage / Discover / Forums</B></li>
	*<li><b>5: User 1 go to the story of the forum creation and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>6: User 1 go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: User 1 should see the forum creation in Homepage / Saved / Forums</b></li>
	*<li><b>7: User 2 log into connections go to Homepage / I'm Following / Forums</b></li>
	*<li><b>8: User 2 go to the story of User 1 creating of the topic in the forum and mark it as Saved</b></li>
	*<li><b>Verify: User 2 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>9: User 2 go to Homepage / Saved / Forums</b></li>
	*<li><B>Verify: User 2 should see the topic added in Homepage / Saved / Forums</B></li>
	*<li><b>10: User 2 add a comment to the forum topic</b></li>
	*<li><b>11: User 1 go to Homepage / My Notifications / For Me / Forums</b></li>
	*<li><b>12: User 1 mark the story of the comment on the forum topic as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>13: User 1 go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: User 1 should see the comment on the topic added in Homepage / Saved / Forums</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/E41D1C48FBFE713585257936004E4659">TTT: AS - Saved - 00011 - Marking Forum Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicForum_MarkingForumStoriesAsSaved() {
		
		String testName = ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForum1.getName(), testUser1.getDisplayName());
		
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createForumEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create forum event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum1.getDescription()}, TEST_FILTERS, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumTopic forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Create the news story to be saved
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, forumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());
		
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createTopicEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the forum topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, topicReply);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news story to be saved
		String commentOnYourTopicEvent = ForumNewsStories.getReplyToYourTopicNewsStory_User(ui, forumTopic.getTitle(), baseForum1.getName(), testUser2.getDisplayName());
		
		// Save the news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnYourTopicEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String commentOnTheTopicEvent = ForumNewsStories.getReplyToTheTopicNewsStory_User(ui, forumTopic.getTitle(), baseForum1.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnTheTopicEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicForum_RemovingForumStoriesFromSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. Go to Homepage / Saved / Forums</B></li>
	*<li><B>3. Click the 'X' in the story related to the Forum that had been created by User 1</B></li>
	*<li><b>Verify: User 1 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><B>4. Go to Homepage / Discover / Forums</B></li>
	*<li><b>Verify: User 1 - The story in Homepage / Discover / Forums should have a clickable "Save this" link again</b></li>
	*<li><b>5: User 2 log into Connections</b></li>
	*<li><b>6: Go to Homepage / Saved / Forums</b></li>
	*<li><b>7: Click the "X" in the story related to the forum topic that was started by User 1</b></li>
	*<li><b>Verify: User 2 - Once the 'X' is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>8: Go to Homepage / I'm Following / Forums</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / I'm Following / Forums should have a clickable "Save this" link again</b></li>
	*<li><b>9: User 1 go to Homepage / Saved / Forums</b></li>
	*<li><b>10: Click the "X" in the story related to the comment on the forum topic</b></li>
	*<li><B>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</B></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>11: Go to Homepage / My Notifications / For Me / Forums</b></li>
	*<li><b>Verify: User 2 - The story in Homepage / My Notifications / For Me / Forums should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/3DFB8EB83C9C8D3F8525793B0035E9D8">TTT: AS - Saved - 00012 - Removing Forum Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicForum_RemovingForumStoriesFromSaved() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum2);
		ForumTopic forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
				
		// Create the news stories to be saved
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForum2.getName(), testUser1.getDisplayName());
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum2.getName(), testUser1.getDisplayName());
		
		// User 1 will now save the news story of the create forum event using the API
		ProfileEvents.saveNewsStory(profilesAPIUser1, createForumEvent, true);
		
		// User 2 will now save the news story of the create forum topic event using the API
		ProfileEvents.saveNewsStory(profilesAPIUser2, createTopicEvent, false);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		
		// Remove the create forum event news story from User 1's Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createForumEvent);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the create forum event news story now has a 'Save This' link displayed with it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createForumEvent);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser2, true);
		
		// Remove the create forum topic event news story from User 2's Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createTopicEvent);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the create forum topic event news story now has a 'Save This' link displayed with it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createTopicEvent);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// User 2 will now post a comment to the forum topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, topicReply);
		
		// Create the news story to be saved
		String commentOnTheTopicEvent = ForumNewsStories.getReplyToTheTopicNewsStory_User(ui, forumTopic.getTitle(), baseForum2.getName(), testUser2.getDisplayName());
		
		// User 1 will now save the news story of the comment on forum topic event using the API
		ProfileEvents.saveNewsStory(profilesAPIUser1, commentOnTheTopicEvent, true);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, true);
		
		// Remove the comment on forum topic event news story from User 1's Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, commentOnTheTopicEvent);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Create the news story to be verified
		String commentOnYourTopicEvent = ForumNewsStories.getReplyToYourTopicNewsStory_User(ui, forumTopic.getTitle(), baseForum2.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on forum topic event news story now has a 'Save This' link displayed with it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnYourTopicEvent);
		
		ui.endTest();
	}
	
	/**
	* test_PublicForum_SaveAStoryOfAForumTopic()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Forums</B></li>
	*<li><B>2. Start a forum</B></li>
	*<li><B>3. Start a topic in the forum</B></li>
	*<li><B>4. Go to Homepage / Discover / Forums</B></li>
	*<li><b>5: Go to the story of the topic in the forum and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story saved is of the topic in the forum</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/C6E6E3BFF6E925B3852579B300348D86">TTT: AS - Saved - 00070 - Save A Story Of A Forum Topic</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicForum_SaveAStoryOfAForumTopic() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
				
		// Create the news stories to be saved
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());
		
		// User 1 will now save the news story of the create forum topic event using the API
		ProfileEvents.saveNewsStory(profilesAPIUser1, createTopicEvent, true);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		
		// Verify that the create forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicForum_ForumTopicRepliedToAfterTopicStartedIsSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Forums</B></li>
	*<li><B>2. Start a forum</B></li>
	*<li><B>3. Start a topic in the forum</B></li>
	*<li><B>4. Go to Homepage / Discover / Forums</B></li>
	*<li><b>5: Go to the story of the topic in the forum and 'Save This'</b></li>
	*<li><b>6: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story saved is of the topic in the forum</b></li>
	*<li><b>7: Go back to the forum topic and reply to the topic</b></li>
	*<li><b>8: Go to Homepage / Discover / Forums</b></li>
	*<li><b>Verify: Verify that the story in discover is now of the reply to the topic</b></li>
	*<li><b>9: Go to Homepage / Saved / Forums</b></li>
	*<li><b>Verify: Verify that the story still saved is of the topic in the forum</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/EEBB0C79ECCFC4BB852579B300348EDC">TTT: AS - Saved - 00071 - Forum Topic Replied To After A Topic Started Story Is Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicForum_ForumTopicRepliedToAfterTopicStartedIsSaved() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumTopic forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
				
		// Create the news stories to be saved
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());
		
		// User 1 will now save the news story of the create forum topic event using the API
		ProfileEvents.saveNewsStory(profilesAPIUser1, createTopicEvent, true);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		
		// Verify that the create forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now post a comment to the forum topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, forumTopic, topicReply);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String commentOnTopicEvent = ForumNewsStories.getReplyToYourTopicNewsStory_You(ui, forumTopic.getTitle(), baseForum1.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTopicEvent, baseForumTopic.getDescription(), topicReply}, filter, true);
			
			// Verify that the create forum topic event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent},  null, false);
		}
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
						
			// Verify that the comment on forum topic event and User 1's comment are NOT displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnTopicEvent, topicReply}, null, false);
		}
		ui.endTest();
	}
}