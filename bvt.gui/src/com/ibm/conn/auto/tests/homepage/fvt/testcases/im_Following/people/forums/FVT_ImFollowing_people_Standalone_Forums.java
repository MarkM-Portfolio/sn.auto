package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / People) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 07/2015
 */

public class FVT_ImFollowing_people_Standalone_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };
	
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseForum baseForum1, baseForum2;
	private Forum standaloneForum1, standaloneForum2;
	private User testUser1 , testUser2;	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
				
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);

		// User 1 will now create the first standalone forum
		baseForum1 = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum1 = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum1);
		
		// User 1 will now create the second standalone forum
		baseForum2 = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum2 = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum2);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the forum now that the test has completed
		forumsAPIUser1.deleteForum(standaloneForum1);
		forumsAPIUser1.deleteForum(standaloneForum2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateForum_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Create a forum</B></li>
	*<li><B>Step: testUser 2 Follow User 1 log into Home</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.created story is displayed within the People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E06AE103946C7D76852578FC002E86B0">TTT - AS - FOLLOW - PERSON - FORUMS - 00271 - forum.created - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_person_CreateForum_Standalone() {

		ui.startTest();
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForum1.getName(), testUser1.getDisplayName());
		
		//Verify the news story does NOT appear in any filter
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum1.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateForum_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Update a forum</B></li>
	*<li><B>Step: testUser 2 Follow User 1 log into Home</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/30714C0A36B6110B852578FC002E8FB7">TTT - AS - FOLLOW - PERSON - FORUMS - 00281 - forum.updated - STANDALONE FORUM (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_person_UpdateForum_Standalone() {

		ui.startTest();

		// User 1 will now edit the description of the forum
		String editedContent = Data.getData().commonDescription + Helper.genStrongRand();
		ForumEvents.editForumDescription(testUser1, forumsAPIUser1, standaloneForum2, editedContent);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForum2.getName(), testUser1.getDisplayName());
		String updateForumEvent = ForumNewsStories.getUpdateForumNewsStory(ui, baseForum2.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create forum event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createForumEvent, baseForum2.getDescription()}, filter, true);
			
			// Verify that the update forum event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateForumEvent, editedContent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateTopic_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 1 Create a topic within a forum that is already there</B></li>
	*<li><B>Step: testUser 2 Follow User 1  log into Home</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.created story is displayed within the People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B6C1F862600AF2AA852578FC002E9C5B">TTT - AS - FOLLOW - PERSON - FORUMS - 00291 - forum.topic.created - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_person_CreateForumTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateForumTopic_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 1 Update an existing topic within a forum that is already there</B></li>
	*<li><B>Step: testUser 2 Follow User 1  log into Home</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4E925D0F6E4F4CC852579BF0048A6F7">TTT - AS - FOLLOW - PERSON - FORUMS - 00295 - forum.topic.updated - STANDALONE FORUM (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_person_UpdateForumTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will then update the description of the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ForumEvents.createForumTopicAndEditTopicDescription(testUser1, forumsAPIUser1, baseForumTopic, editedDescription);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());
		String updateTopicEvent = ForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event is displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent}, filter, true);
			
			// Verify that the update forum topic event is NOT displayed in any of the filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateforumTopicReply_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 1 Create a response to the topic in the forum</B></li>
	*<li><B>Step: testUser 2 Follow User 1  log into Home</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>	
	*<li><B>Verify: Verify that the forum.response.created story is displayed within the People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7FD54640E64A712852578FC002EA6CF">TTT - AS - FOLLOW - PERSON - FORUMS - 00301 - forum.response.created - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_person_CreateForumTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will then post a reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumTopic forumTopic = ForumEvents.createForumTopicAndAddReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateforumTopicReply_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 1 Update an existing response to the topic in the forum</B></li>
	*<li><B>Step: testUser 2 Follow User 1  log into Home</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>	
	*<li><B>Verify: Verify that the forum.topic.reply.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F62A96125CF313B3852579BF00495719">TTT - AS - FOLLOW - PERSON - FORUMS - 00305 - forum.topic.reply.updated - STANDALONE FORUM (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_person_UpdateForumTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic
		String forumReply = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, forumsAPIUser1, baseForumTopic, forumReply, replyEdit);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());
		String updateReplyEvent = ForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event and updated reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update reply event and original reply are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, forumReply}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_person_LikeTopic_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 go to a forum you own</B></li>
	*<li><B>Step: testUser 1 start a topic in the forum</B></li>
	*<li><B>Step: testUser 1 like the topic</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>	
	*<li><B>Verify: Verify that the forum.topic.recommended story appears for points 5-7</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A6C80A4B8BC7412585257AC40054DCE2">TTT - AS - FOLLOW - PERSON - FORUMS - 00309 - forum.topic.recommended - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_person_LikeTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic and will like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumEvents.createForumTopicAndLikeTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicEvent = ForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		// Verify that the like topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_person_LikeTopicReply_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Forums</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 go to a forum you own</B></li>
	*<li><B>Step: testUser 1 start a topic in the forum</B></li>
	*<li><B>Step: testUser 1 reply to the topic</B></li>
	*<li><B>Step: testUser 1 like the reply</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>	
	*<li><B>Verify: Verify that the forum.topic.reply.recommended story appears for points 6-8 (All, Forums & people)</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA005ED945BBC2F285257AC4005526E0">TTT - AS - FOLLOW - PERSON - FORUMS - 00313 - forum.topic.reply.recommended - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_person_LikeTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic, post a reply to the topic and then like / recommend the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum1);
		ForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply);
		
		// User 2 logs in and navigates to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicReply = ForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum1.getName(), testUser1.getDisplayName());

		// Verify that the like topic reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReply, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}