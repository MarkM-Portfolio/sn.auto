package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
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

public class FVT_ImFollowing_ModeratedCommunity_Forums extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser3;
	private BaseCommunity baseCommunity, baseCommunityWithFollower;
	private Community moderatedCommunity, moderatedCommunityWithFollower;
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
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
	
		// User 1 will now create a moderated community with no followers
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community with User 2 following the community
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunityWithFollower = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunityWithFollower);
	}
		
	/**
	 *<ul>
	 *<li><B>Name: test_CreateForum_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log in to Communities</B></li>
	 *<li><B>Step: User 1 open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: User 1 add the forums widget within this community if not already present</B></li>
	 *<li><b>Step: User 2 follow the community</b></li>
	 *<li><b>Step: User 1 add a new forum</b></li>
	 *<li><b>Step: User 2 log in to Home</b></li>
	 *<li><b>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify that the forum.created story is displayed in communities</b></li>
	 *<li><B>Verify: Verify that the forum.created story is displayed in forums</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3ADD2388F769033852578F8002D8B1E">TTT - AS - FOLLOW - FORUMS - 00013 - forum.created - MODERATE COMMUNITY</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateForum_FollowingCommunity_ModeratedCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(moderatedCommunityWithFollower, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateTopic_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify the forum.topic.created is displayed in communities</b></li>
	 *<li><B>Verify: Verify the forum.topic.created is displayed in forums</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4328C9AE3B28D1C2852578F80030DBF2">TTT - AS - FOLLOW - FORUMS - 00033 - forum.topic.created - MODERATE COMMUNITY</a></li>
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateTopic_FollowingCommunity_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseTopic);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateTopic_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify the forum.topic.created is NOT displayed in communities</b></li>
	 *<li><B>Verify: Verify the forum.topic.created is NOT displayed in forums</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/28A8238A6186EFC285257BD6004FEC1B">TTT - AS - FOLLOW - FORUM TOPIC - 00013 - FORUM.TOPIC.CREATED - MODERATE COMMUNITY</a></li>
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateTopic_FollowingTopic_ModeratedCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 will follow the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, moderatedCommunity, baseTopic, testUser3, forumsAPIUser3);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateResponse_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify that the forum.response.created is displayed in communities</b></li>
	 *<li><B>Verify: Verify that the forum.response.created is displayed in forums</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D75A6F4BFEBD9B73852578F800322788">TTT - AS - FOLLOW - FORUMS - 00043 - forum.response.created - MODERATE COMMUNITY</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingCommunity_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name: test_CreateResponse_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify that the forum.response.created is displayed in communities</b></li>
	 *<li><B>Verify: Verify that the forum.response.created is displayed in forums</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/FA367716C1EC30B185257BD60050A642">TTT - AS - FOLLOW - FORUM TOPIC - 00023 - FORUM.RESPONSE.CREATED - MODERATE COMMUNITY</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingTopic_ModeratedCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 follows it and User 1 will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createTopicWithOneFollowerAndAddReply(testUser1, communitiesAPIUser1, moderatedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTopic_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Update the topic in the forum</b></li>
	 *<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	 *<li><b>Step: Go to Home / I'm Following / All, Forums & Communities</b></li>
	 *<li><b>Verify: Verify the forum.topic.updated is displayed in any view</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B697C4F9BA39090085257BD600514126">TTT - AS - FOLLOW - FORUM TOPIC - 00033 - FORUM.TOPIC.UPDATED - MODERATE COMMUNITY</a></li>
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTopic_FollowingTopic_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower and will edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic topic = CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, moderatedCommunity, baseTopic, testUser3, forumsAPIUser3);
		CommunityForumEvents.editForumTopicDescription(testUser1, forumsAPIUser1, topic, editedDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTopicEvent, editedDescription}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTopic_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Update the topic in the forum</b></li>
	 *<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	 *<li><b>Step: Go to Home / I'm Following / All, Forums & Communities</b></li>
	 *<li><b>Verify: Verify the forum.topic.updated is NOT displayed in any view</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B3388033B5265E8B852579BB006942EF">TTT - AS - FOLLOW - FORUMS - 00053 - forum.topic.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>  
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTopic_FollowingCommunity_ModeratedCommunity(){

		String testName = ui.startTest();

		// Create the forum topic and edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndEditDescription(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseTopic, forumsAPIUser1, editedDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Forum topic created news story should appear as the update topic news story should NOT surface and cause a rollup
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event and original topic description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, filter, true);
			
			// Verify that the update topic event and edited description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateResponse_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Create a reply to a topic in the forum</b></li>
	 *<li><b>Step: Edit the reply and save the updated reply</b></li>
	 *<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	 *<li><b>Step: Go to Home / I'm Following / All, Forums & Communities</b></li>
	 *<li><b>Verify: Verify the forum.topic.reply.updated is NOT displayed in any view</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1FE925C7F6EAE128852579BB006C001A">TTT - AS - FOLLOW - FORUMS - 00063 - forum.topic.reply.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>  
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingCommunity_ModeratedCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a community forum topic, add a reply to the topic and then edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseForumTopic, forumsAPIUser1, topicReply, editedTopicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
	
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String updateReplyEvent = CommunityForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event, the topic description and the updated reply are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription().trim(), editedTopicReply}, filter, true);
			
			// Verify that the update reply event and original reply (before edit) are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, topicReply}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateResponse_FollowingForum_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Create a reply to a topic in the forum</b></li>
	 *<li><b>Step: Edit the reply and save the updated reply</b></li>
	 *<li><b>Step: Log in to Home as a different user who is following the forum</b></li>
	 *<li><b>Step: Go to Home / I'm Following / All, Forums & Communities</b></li>
	 *<li><b>Verify: Verify the forum.topic.reply.updated is NOT displayed in any view</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EBD69A018348395785257BE8003474E3">TTT - AS - FOLLOW - FORUMS - 00163 - FORUM.TOPIC.REPLY.UPDATED - MODERATE COMMUNITY (NEG SC NOV)</a></li>  
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingForum_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(moderatedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
		// Create the forum topic, reply to the topic and edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndAddReplyAndEditReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply, editedReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String updateReplyEvent = CommunityForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event, the topic description and the updated reply are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription().trim(), editedReply}, filter, true);
			
			// Verify that the update reply event and original reply (before edit) are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, topicReply}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateResponse_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with moderate access that you have owner access to</B></li>	
	 *<li><B>Step: Create a topic within a forum that is in the community</B></li>
	 *<li><b>Step: Create a reply to a topic in the forum</b></li>
	 *<li><b>Step: Edit the reply and save the updated reply</b></li>
	 *<li><b>Step: Log in to Home as a different user who is following the forum</b></li>
	 *<li><b>Step: Go to Home / I'm Following / All, Forums & Communities</b></li>
	 *<li><b>Verify: Verify the forum.topic.reply.updated is NOT displayed in any view</b></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/5A699347FF10F7BB85257BD6005539FF">TTT - AS - FOLLOW - FORUM TOPIC - 00043 - FORUM.TOPIC.REPLY.UPDATED - MODERATE COMMUNITY</a></li>  
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingTopic_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForum(moderatedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// Create the forum topic with User 2 as a follower, reply to the topic and edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply, editedReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateReplyEvent = CommunityForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event, the topic description and the updated reply are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription().trim(), editedReply}, filter, true);
			
			// Verify that the update reply event and original reply (before edit) are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, topicReply}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeTopic_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the community</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does NOT appear</b></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/07727830FEC87DAA85257AC400532097">TTT - AS - FOLLOW - FORUMS - 00073 - forum.topic.recommended - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li>  
	 */
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingCommunity_ModeratedCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a forum topic and like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseTopic, forumsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified (Note: Since the topic was created before User 2 followed the topic, NO news story should appear at all for this topic)
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String likeForumTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, filter, true);

			// Verify that the like forum topic event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeForumTopicEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeTopic_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the topic</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does not appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3C6CC7AC0524D4C685257BD60055BE15">TTT - AS - FOLLOW - FORUM TOPIC - 00053 - FORUM.TOPIC.RECOMMENDED - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li>  
	 */
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingTopic_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndLikeTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified - NONE of these news stories should appear in the AS since User 2 has followed the topic AFTER it has been created
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeForumTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create forum topic event and like forum topic event are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription(), likeForumTopicEvent}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeTopic_FollowingForum_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the forum</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does not appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/844FDF0101085B3D85257BE80034C63E">TTT - AS - FOLLOW - FORUMS - 00173 - FORUM.TOPIC.RECOMMENDED - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingForum_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(moderatedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
				
		// User 1 will now create a topic in the forum and will like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndLikeTopic(testUser1, forumsAPIUser1, baseTopic);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String likeForumTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, filter, true);

			// Verify that the like forum topic event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeForumTopicEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeResponse_FollowingCommunity_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the community</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/6D85A20DABC1DC0C85257AC4005326D1">TTT - AS - FOLLOW - FORUMS - 00083 - forum.topic.reply.recommended - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingCommunity_ModeratedCommunity(){

		String testName = ui.startTest();

		// Create the forum topic, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, moderatedCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		String likeReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, filter, true);
			
			// Verify that the like reply event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeReplyEvent}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeResponse_FollowingTopic_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the topic</B></li>
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.recommended story does not appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/28BBA25FEA37BB9285257BD60057982E">TTT - AS - FOLLOW - FORUM TOPIC - 00063 - FORUM.TOPIC.REPLY.RECOMMENDED - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingTopic_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// Create the forum topic with User 2 as a follower, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, moderatedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, filter, true);
			
			// Verify that the like reply event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeReplyEvent}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_LikeResponse_FollowingForum_ModeratedCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a moderate community you own</B></li>
	 *<li><b>Step: User 2 follow the forum in the moderate community</b></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><b>Step: User 1 reply to the topic</b></li>
	 *<li><b>Step: User 1 like the reply</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.reply.recommended story does not appear</b></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B084097533B4A90785257BE800353332">TTT - AS - FOLLOW - FORUMS - 00183 - FORUM.TOPIC.REPLY.RECOMMENDED - MODERATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingForum_ModeratedCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(moderatedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
		// User 1 will now create a topic in the forum, add a reply to that topic and will then like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, baseForumTopic, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String likeReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create reply event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription(), topicReply}, filter, true);
			
			// Verify that the like reply event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeReplyEvent}, null, false);
		}
		ui.endTest();
	}
}