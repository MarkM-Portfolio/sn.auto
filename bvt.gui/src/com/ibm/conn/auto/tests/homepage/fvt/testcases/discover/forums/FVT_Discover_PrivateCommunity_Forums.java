package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
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

public class FVT_Discover_PrivateCommunity_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);

		// Creating the restricted community with all setup steps completed.
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);	
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForum_PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 reply to a forum topic</B></li>
	 *<li><B>Step: testUser 1 like the forum topic reply</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.created is NOT seen - negative test</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2AB3A915F04B13FF852578760079E805">TTT - DISC - FORUMS - 00000 - FORUM.CREATED - PRIVATE COMMUNITY (NEG)</a></li> 
	 *</ul>
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateForum_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a new forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent}, TEST_FILTERS, false);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopic_PrivateCommunity()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 add a new forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.created is NOT seen - negative test</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DFE5E39C57055AC6852578760079E78C">TTT -  DISC - FORUMS - 00010 - FORUM.TOPIC.CREATED - PRIVATE COMMUNITY (NEG)</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_CreateForumTopic_PrivateCommunity() {

		String testName = ui.startTest();

		// Create the forum topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, false);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopicReply_PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 add a response to a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.response.created is seen</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5657BB84C8FE1E27852578760079E791">TTT - DISC - FORUMS - 00020 - FORUM.RESPONSE.CREATED - PRIVATE COMMUNITY (NEG)</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_CreateForumTopicReply_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, false);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopic_PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 update an existing forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.updated is NOT seen - negative test</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5A9A18115E3000E5852579BC005C0F1B">TTT - DISC - FORUMS - 00015 - FORUM.TOPIC.UPDATED - PRIVATE COMMUNITY (NEG)</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_UpdateForumTopic_PrivateCommunity() {

		String testName = ui.startTest();

		// Create the forum topic and edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndEditDescription(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, editedDescription);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String topicEditEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicEditEvent, editedDescription}, TEST_FILTERS, false);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopicReply_PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 update an existing response to a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.reply.updated is NOT seen - negative test</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CEDBB24D5BDC11BA852579BC005D179F">TTT -  DISC - FORUMS - 00025 - FORUM.TOPIC.REPLY.UPDATED - PRIVATE COMMUNITY (NEG)</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_UpdateForumTopicReply_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a community forum topic, add a reply to the topic and then edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic, forumsAPIUser1, topicReply, editedTopicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String topicReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), editedTopicReply}, TEST_FILTERS, false);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopic__PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 like a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the story of forum.topic.recommended story does NOT appear in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CEC2D621612231C85257AC4004F3D66">TTT - DISC - FORUMS - 00030 - FORUM.TOPIC.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG)</a></li>
	 *</ul>
	 */
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, false);			

		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopicReply_PrivateCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with private access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 reply to a forum topic</B></li>
	 *<li><B>Step: testUser 1 like the forum topic reply</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the story of forum.topic.reply.recommended story does NOT appear in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F4301624F26A7DB585257AC40051D41D">TTT - DISC - FORUMS - 00035 - FORUM.TOPIC.REPLY.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG)</a></li>
	 *</ul> 
	 */
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopicReply_PrivateCommunity() {

		String testName = ui.startTest();

		// Create the forum topic, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, false);			

		ui.endTest();
	}
}