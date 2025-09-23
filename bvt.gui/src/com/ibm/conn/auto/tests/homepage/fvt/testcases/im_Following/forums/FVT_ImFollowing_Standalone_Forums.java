package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.forums;

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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_ImFollowing_Standalone_Forums extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };
	
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2, forumsAPIUser3,forumsAPIUser4;
	private BaseForum baseForum, baseForumWithFollower;
	private Forum standaloneForum, standaloneForumWithFollower;
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
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		
		// User 1 will now create a standalone forum with no followers
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
		
		// User 1 will now create a standalone forum with User 2 then following the forum
		baseForumWithFollower = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForumWithFollower = ForumEvents.createForumWithOneFollower(testUser1, forumsAPIUser1, testUser2, forumsAPIUser2, baseForumWithFollower);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		
		// Delete the forums created during the test
		forumsAPIUser1.deleteForum(standaloneForum);
		forumsAPIUser1.deleteForum(standaloneForumWithFollower);
	}
	
	/**
	 * test_CreateForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a new Forum</B></li>
	 *<li><B>Step: Log in to Home as a different user</B></li>
	 *<li><B>Step: Follow the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.creates is not displayed in the Forums Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F98C82965EDE9C08852578F8002D86EA">TTT - AS - FOLLOW - FORUMS - 00011 - forum.created - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_CreateForum_Standalone() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		// Verify that the create forum event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * test_CreateForumTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.created story is displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9C55B199FBB3A50F852578F80030D944">TTT - AS - FOLLOW - FORUMS - 00031 - forum.topic.created - STANDALONE FORUM</a></li> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateForumTopic_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		// Verify that the create topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * test_CreateForumTopic_FollowTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Log in to Home as a different user and start following the topic</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.created story is not displayed in the Forum Filter</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9C3B1278A18A33C685257BD6004F1EF5">TTT - AS - FOLLOW - FORUM TOPIC - 00011 - FORUM.TOPIC.CREATED - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateForumTopic_FollowTopic_Standalone() {
		
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollower(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
				
		// Create the news story to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		// Verify that the create topic event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 * test_CreateForumTopic_PreFollow_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Log in to Home as a different user and follow the forum now that the topic has been created (ie. follow the forum AFTER the topic has been created)</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.created story is not displayed in the Forum Filter</B></li>
	 *<li><B>There are NO TTT LINKS for this test case</B></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateForumTopic_PreFollow_Standalone() {
		
		/**
		 * This test case will use User 4 as User 2
		 * This is necessary since User 2 is already following a forum and User 3 cannot follow any of the forums during these tests
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 will now follow the standalone forum
		ForumEvents.followForumSingleUser(standaloneForum, testUser4, forumsAPIUser4);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser4, false);
						
		// Create the news story to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
				
		// Verify that the create topic event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	 * test_CreateForumTopicReply_FollowingForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Respond to a topic within the Forum that is there</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.response.created story is displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4FF6689AF71BA783852578F800322465">TTT - AS - FOLLOW - FORUMS - 00041 - forum.response.created - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateForumTopicReply_FollowingForum_Standalone(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum and will post a reply to the topic
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopicAndAddReply(testUser1, forumsAPIUser1, baseForumTopic, user1Comment);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		// Verify that the reply to topic event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * test_CreateForumTopicReply_FollowingTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Respond to a topic within the Forum that is there</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum topic</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.response.created story is displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/96EFEA63DABB50D285257BD600502F43">TTT - AS - FOLLOW - FORUM TOPIC - 00021 - FORUM.RESPONSE.CREATED - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateForumTopicReply_FollowingTopic_Standalone() {
		
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic and User 1 will post a reply to the topic
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollowerAndAddReply(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic, user1Comment);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		// Verify that the reply to topic event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * test_UpdateForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Update a Forum that is there</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.updated story is displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/81CB88BFDEDCA53F852578F8003012FC">TTT - AS - FOLLOW - FORUMS - 00021 - forum.updated - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 3)
	public void test_UpdateForum_Standalone() {
		
		ui.startTest();
		
		// User 1 will now update the standalone forum description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		ForumEvents.editForumDescription(testUser1, forumsAPIUser1, standaloneForumWithFollower, editedDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateForumEvent = ForumNewsStories.getUpdateForumNewsStory(ui, baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		// Verify that the update forum event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateForumEvent, editedDescription}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	 * test_UpdateForumTopic_FollowingForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Update the topic in the forum</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.updated story is NOT displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7779EEEE924910BB852579BB006542F3">TTT - AS - FOLLOW - FORUMS - 00051 - forum.topic.updated - STANDALONE FORUM (NEG SC NOV)</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopic_FollowingForum_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum and will edit the description of the topic
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopicAndEditTopicDescription(testUser1, forumsAPIUser1, baseForumTopic, editedDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		String updateTopicEvent = ForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
			
			// Verify that the update topic event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_UpdateForumTopicReply_FollowingForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Create a reply to a topic in the forum</B></li>
	 *<li><B>Step: Edit the reply and save the updated reply</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.reply.updated story is NOT displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/77F530082E73CCC5852579BB0069C6D8">TTT - AS - FOLLOW - FORUMS - 00061 - forum.topic.reply.updated - STANDALONE FORUM (NEG - SC - NOV)</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopicReply_FollowingForum_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum and will post a reply to the topic and then edit the reply
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, forumsAPIUser1, baseForumTopic, user1Comment, user1EditedComment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		String updateReplyEvent = ForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event and User 1's updated reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1EditedComment}, filter, true);
			
			// Verify that the update reply event and User 1's original reply are NOT displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_UpdateForumTopic_FollowingTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Update the topic in the forum</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum topic</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.updated story is displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/25FFCAEAAE5BA84585257BD600510FD5">TTT - AS - FOLLOW - FORUM TOPIC - 00031 - FORUM.TOPIC.UPDATED - STANDALONE FORUM</a></li>
	 * @author Hugh Caren 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopic_FollowingTopic_Standalone() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic and User 1 will edit the description of the topic
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollowerAndEditTopicDescription(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic, editedDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String updateTopicEvent = ForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the update topic event and updated description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, filter, true);
			
			// Verify that the original description is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseForumTopic.getDescription()}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_UpdateForumTopicReply_FollowingTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Forums</B></li>
	 *<li><B>Step: Create a topic within the forum</B></li>
	 *<li><B>Step: Create a reply to a topic in the forum</B></li>
	 *<li><B>Step: Edit the reply and save the updated reply</B></li>
	 *<li><B>Step: Log in to Home as a different user who is following the forum topic</B></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following</b></li>
	 *<li><b>Step: Filter by Forum</b></li>
	 *<li><B>Verify: Verify the forum.topic.reply.updated story is NOT displayed in the Forum Filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C38131E497C851185257BD600550AF1">TTT - AS - FOLLOW - FORUM TOPIC - 00041 - FORUM.TOPIC.REPLY.UPDATED - STANDALONE FORUM</a></li> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_UpdateForumTopicReply_FollowingTopic_Standalone() {
		
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic and User 1 will post a reply to the topic and then edit the reply
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollowerAndAddReplyAndEditReply(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic, user1Comment, user1EditedComment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String updateReplyEvent = ForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event and User 1's updated reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1EditedComment}, filter, true);
			
			// Verify that the update reply event and User 1's original reply are NOT displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, user1Comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_LikeTopic_FollowingForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 2 follow the forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><B>Step: User 1 like the topic</B></li>
	 *<li><B>Step: User 2 go to Home / I'm Following / All</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Forums</b></li>
	 *<li><B>Verify: Verify that the forum.topic.recommended story DOES NOT appear for point 5-6</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E5A160AA62F5D34785257AC400531DDC">TTT - AS - FOLLOW - FORUMS - 00071 - forum.topic.recommended - STANDALONE FORUM (NEG SC NOV)</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopic_FollowingForum_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum and will then like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopicAndLikeTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		String likeTopicEvent = ForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
			
			// Verify that the like topic event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeTopicEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_LikeTopic_FollowingTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><B>Step: User 2 follow the topic</B></li>
	 *<li><B>Step: User 1 like the topic</B></li>
	 *<li><B>Step: User 2 go to Home / I'm Following / All</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Forums</b></li>
	 *<li><B>Verify: Verify that the forum.topic.recommended story DOES NOT appear for point 5-6</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/54D8C8915CB9800785257BD6005568BB">TTT - AS - FOLLOW - FORUM TOPIC - 00051 - FORUM.TOPIC.RECOMMENDED - STANDALONE FORUM</a></li> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopic_FollowingTopic_Standalone() {
		
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic and User 1 will then like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollowerAndLikeTopic(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
				
		// Create the news story to be verified
		String likeTopicEvent = ForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		// Verify that the like topic event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, false);
			
		ui.endTest();
	}
	
	/**
	 * test_LikeTopicReply_FollowingForum_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 2 follow the forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All & Forums</b></li>
	 *<li><b>Verify: Verify that NO forum.topic.reply.recommended story appears for point 6-7</b></li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8CCE3FD7344C38B185257AC4005323A9">TTT - AS - FOLLOW - FORUMS - 00081 - forum.topic.reply.recommended - STANDALONE FORUM (NEG SC NOV)</a></li> 
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopicReply_FollowingForum_Standalone() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum, will post a reply to the topic and will then like / recommend the reply
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForumWithFollower);
		ForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, baseForumTopic, user1Comment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName()); 
		String likeReplyEvent = ForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForumWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1Comment}, filter, true);
			
			// Verify that the like reply event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeReplyEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * test_LikeTopicReply_FollowingTopic_Standalone() 
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 start a Forum</B></li>
	 *<li><B>Step: User 1 start a topic in the forum</B></li>
	 *<li><B>Step: User 2 follow the topic</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All & Forums</b></li>
	 *<li><b>Verify: Verify that NO forum.topic.reply.recommended story appears for point 6-7</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/65B78207289D791C85257BD600575811">TTT - AS - FOLLOW - FORUM TOPIC - 00061 - FORUM.TOPIC.REPLY.RECOMMENDED - STANDALONE FORUM</a></li>
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeTopicReply_FollowingTopic_Standalone(){
		
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the standalone forum with User 2 then following the topic, User 1 will then post a reply and will like / recommend the reply
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopicWithOneFollowerAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, testUser3, forumsAPIUser3, baseForumTopic, user1Comment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName()); 
		String likeReplyEvent = ForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1Comment}, filter, true);
			
			// Verify that the like reply event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeReplyEvent}, null, false);
		}
		ui.endTest();
	}
}