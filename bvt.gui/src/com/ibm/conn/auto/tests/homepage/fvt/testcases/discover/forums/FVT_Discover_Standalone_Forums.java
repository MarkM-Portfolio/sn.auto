package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.forums;

import java.util.HashMap;
import java.util.Set;

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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

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

public class FVT_Discover_Standalone_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };
	
	private APIForumsHandler forumsAPIUser1;
	private BaseForum baseForum;
	private Forum forum;
	private HashMap<Forum, APIForumsHandler> forumsForDeletion = new HashMap<Forum, APIForumsHandler>();
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		// User 1 create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		forum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the standalone forum created during the test
		forumsAPIUser1.deleteForum(forum);
		
		// Delete any additional forums created during the test
		Set<Forum> setOfForums = forumsForDeletion.keySet();
		
		for(Forum forum : setOfForums) {
			forumsForDeletion.get(forum).deleteForum(forum);
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForum_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 2 go to Home / Updates / Discover / All & Forums</B></li>
	 *<li><B>Verify: Verify that the forum.created story does appear</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5E06C8327B48BE81852578760079E802">TTT - DISC - FORUMS - 00000 - FORUM.CREATED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_CreateForum_Standalone() {

		ui.startTest();
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any filter
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopic_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.created story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/13C5A21B531CA36A852578760079E78A">TTT - DISC - FORUMS - 00010 - FORUM.TOPIC.CREATED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_CreateForumTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, baseForumTopic.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopicReply_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.created story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2A92BB59BB1A167A852578760079E78E">TTT - DISC - FORUMS - 00020 - FORUM.RESPONSE.CREATED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_CreateForumTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic and add a reply to the topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopicAndAddReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);

		// Create the news story to be verified
		String event = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForum_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 update the forum</B></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.updated story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/93F5260C6AF1F53E852578760079E809">TTT - DISC - FORUMS - 00005 - FORUM.UPDATED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_UpdateForum_Standalone() {

		String testName = ui.startTest();

		// User 1 create a standalone forum
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum forum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
		
		// Add the forum to the Hashmap for deletion in tear down
		forumsForDeletion.put(forum, forumsAPIUser1);
		
		// User 1 will edit the forum description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ForumEvents.editForumDescription(testUser1, forumsAPIUser1, forum, editedDescription);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getUpdateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, editedDescription}, TEST_FILTERS, true);

		// User 1 delete the forum
		forumsAPIUser1.deleteForum(forum);
		forumsForDeletion.remove(forum);
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopic_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 update the forum topic</b></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.updated story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F0E7CB6550093ED3852579BC005C6B15">TTT - DISC - FORUMS - 00015 - FORUM.TOPIC.UPDATED - STANDALONE FORUM</a></li>
	 *</ul>  
	 */
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic and then update the description of the topic
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopicAndEditTopicDescription(testUser1, forumsAPIUser1, baseForumTopic, editedDescription);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		// Verify that the update topic news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, editedDescription}, TEST_FILTERS, true);
			
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopicReply_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 update the reply</b></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the news story for forum.topic.reply.created is seen with the updated response</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8A85B6CD5E5F40B0852579BC005D882F">TTT - DISC - FORUMS - 00025 - FORUM.TOPIC.REPLY.UPDATED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic, add a reply and then edit the reply
		String forumReply = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, forumsAPIUser1, baseForumTopic, forumReply, replyEdit);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createReplyEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String updateReplyEvent = ForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event and updated reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update reply event and original reply are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, forumReply}, null, false);
		}
		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopic__Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/162D9D6269D6A8AE85257AC4004F38E4">TTT - DISC - FORUMS - 00030 - FORUM.TOPIC.RECOMMENDED - STANDALONE FORUM</a></li> 
	 *</ul> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopic_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic and like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopicAndLikeTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		// Verify that the like topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, baseForumTopic.getDescription()}, TEST_FILTERS, true);
			
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopicReply_Standalone()</li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / Updates / Discover / All & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.recommended story does appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/039B3A94222DF34D85257AC40051D0E5">TTT - DISC - FORUMS - 00035 - FORUM.TOPIC.REPLY.RECOMMENDED - STANDALONE FORUM</a></li>
	 *</ul> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopicReply_Standalone() {

		String testName = ui.startTest();

		// User 1 create a forum topic, add a reply and then like / recommend the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), forum);
		ForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply);
		
		// User 2 logs in and navigates to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String event = ForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		// Verify that the topic reply recommended news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
}