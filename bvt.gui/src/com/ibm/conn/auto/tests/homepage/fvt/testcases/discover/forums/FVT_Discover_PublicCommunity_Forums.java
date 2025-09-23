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

public class FVT_Discover_PublicCommunity_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
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

		// Creating the public community with all setup steps completed.
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);	
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>atest_CreateForum_PublicCommunity()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with Public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 add a new forum</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.created is seen</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7A1844E9AA85682F852578760079E803">TTT - DISC - FORUMS - 00000 - FORUM.CREATED - PUBLIC COMMUNITY</a></li> 
	 *</ul>
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void atest_CreateForum_PublicCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a new forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent}, TEST_FILTERS, true);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopic_PublicCommunity()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 add a new forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.created is seen</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4FDF71F08A80882B852578760079E78D">TTT - DISC - FORUMS - 00010 - FORUM.TOPIC.CREATED - PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_CreateForumTopic_PublicCommunity() {

		String testName = ui.startTest();

		// Create the forum topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseTopic);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, true);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_CreateForumTopicReply_PublicCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 add a response to a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.response.created is seen</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/04FABDF551CE4B9D852578760079E78F">TTT - DISC - FORUMS - 00020 - FORUM.RESPONSE.CREATED - PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_CreateForumTopicReply_PublicCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopic_PublicCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 update an existing forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.updated is seen</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F980634B1CEE62B9852579BC005C3B86">TTT - DISC - FORUMS - 00015 - FORUM.TOPIC.UPDATED - PUBLIC COMMUNITY</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_UpdateForumTopic_PublicCommunity() {

		String testName = ui.startTest();

		// Create the forum topic and edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndEditDescription(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, editedDescription);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String topicEditEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicEditEvent, editedDescription}, TEST_FILTERS, true);			

		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_UpdateForumTopicReply_PublicCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 update an existing response to a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the news story for forum.topic.reply.created is seen with the updated response</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1BAF5961A4480A53852579BC005D54DA">TTT - DISC - FORUMS - 00025 - FORUM.TOPIC.REPLY.UPDATED - PUBLIC COMMUNITY</a></li> 
	 *</ul>
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_UpdateForumTopicReply_PublicCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a community forum topic, add a reply to the topic and then edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, topicReply, editedTopicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String topicReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), editedTopicReply}, TEST_FILTERS, true);			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopic__PublicCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 like a forum topic</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the story of forum.topic.recommended story appears in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D3D9EC53002E34E685257AC4004F3A63">TTT - DISC - FORUMS - 00030 - FORUM.TOPIC.RECOMMENDED - PUBLIC COMMUNITY FORUM</a></li>
	 *</ul> 
	 */
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_PublicCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, true);			

		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Title:</B>test_LikeTopicReply_PublicCommunity()</li>
	 *<li><B>Step: testUser 1 log in to Communities</B></li>
	 *<li><B>Step: testUser 1 open a community with public access that you have owner access to</B></li>
	 *<li><B>Step: testUser 1 add the forums widget within this community if not already present</B></li>
	 *<li><B>Step: testUser 1 reply to a forum topic</B></li>
	 *<li><B>Step: testUser 1 like the forum topic reply</B></li>
	 *<li><B>Step: testUser 2 go to Homepage / Updates / Discover / All, Communities & Forums</B></li>
	 *<li><B>Verify: Verify that the story of forum.topic.reply.recommended story appears in the views</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3BC582EE8E240CFA85257AC40051D1F1">TTT - DISC - FORUMS - 00035 - FORUM.TOPIC.REPLY.RECOMMENDED - PUBLIC COMMUNITY FORUM</a></li>
	 *</ul> 
	 */
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopicReply_PublicCommunity() {

		String testName = ui.startTest();

		// Create the forum topic, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 login and got to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeTopicReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, forumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
	
		// Verify that the news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);			

		ui.endTest();	
	}
}