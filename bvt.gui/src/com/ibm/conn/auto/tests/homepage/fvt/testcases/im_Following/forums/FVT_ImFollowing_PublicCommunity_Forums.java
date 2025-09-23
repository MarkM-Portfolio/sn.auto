package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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

public class FVT_ImFollowing_PublicCommunity_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser3;
	private BaseCommunity baseCommunity, baseCommunityWithFollower;
	private Community publicCommunity, publicCommunityWithFollower;
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
	
		// User 1 will now create a public community with no followers
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community with User 2 following the community
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityWithFollower = CommunityEvents.createNewCommunityWithOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunityWithFollower);
	}
	
	/**
	* test_CreateForum_FollowingCommunity_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log in to Communities</B></li>
	*<li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	*<li><B>Step: User 1 add the forums widget within this community if not already present</B></li>
	*<li><B>Step: User 2 follow the community</B></li>
	*<li><b>Step: User 1 add a new forum</b></li>
	*<li><b>Step: User 2 log in to Home</b></li>
	*<li><B>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.created is displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.create is displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12C144E7BD166DEB852578F8002D8A04">TTT - AS - FOLLOW - FORUMS - 00012 - forum.created - PUBLIC COMMUNITY</a></li>   
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateForum_FollowingCommunity_PublicCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(publicCommunityWithFollower, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	* test_CreateTopic_FollowingTopic_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user and follow the topic</b></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.topic.created is not displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.topic.created is not displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BF51657A355DCF0885257BD6004FCCC8">TTT - AS - FOLLOW - FORUM TOPIC - 00012 - FORUM.TOPIC.CREATED - PUBLIC COMMUNITY</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateTopic_FollowingTopic_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 will follow the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, testUser3, forumsAPIUser3);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	* test_CreateResponse_FollowingCommunity_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.topic.reponse.created is displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.topic.reponse.created is displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BBFD8E00AB0D35FE852578F800322651">TTT - AS - FOLLOW - FORUMS - 00042 - forum.response.created - PUBLIC COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingCommunity_PublicCommunity() {
	
		String testName = ui.startTest();

		// User 1 will now create a forum topic and will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, publicCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
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
	 *<li><B>Name: test_CreateResponse_FollowingTopic_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with public access that you have owner access to</B></li>	
	 *<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify that the forum.response.created is displayed in communities</b></li>
	 *<li><B>Verify: Verify that the forum.response.created is displayed in forums</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/95858E4C80EEA83D85257BD600504DAE">TTT - AS - FOLLOW - FORUM TOPIC - 00022 - FORUM.RESPONSE.CREATED - PUBLIC COMMUNITY</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingTopic_PublicCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 follows it and User 1 will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createTopicWithOneFollowerAndAddReply(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	* test_UpdateTopic_FollowingTopic_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as User 2 who has access to the Community and follow the Forum topic.</b></li>
	*<li><b>Step: User 1 update the topic in the forum</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities</B></li>
	*<li><b>Verify: Verify the forum.topic.updated is displayed in any view</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8C5333217989240785257BD60051268F">TTT - AS - FOLLOW - FORUM TOPIC - 00032 - FORUM.TOPIC.UPDATED - PUBLIC COMMUNITY</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTopic_FollowingTopic_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower and will edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic topic = CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, testUser3, forumsAPIUser3);
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
	* test_UpdateTopic_FollowingForum_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Create a topic within a Public Community forum that you own</B></li>
	*<li><B>Step: Update the topic in the forum</B></li>
	*<li><b>Step: Log in to Home as a different user who is following the forum</b></li>
	*<li><B>Step: Go to Home \ Activity Stream \ I'm Following</B></li>
	*<li><b>Step: Filter by Forum, Communities</b></li>
	*<li><b>Verify: Verify the forum.topic.updated story is NOT displayed in the Filters</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/53ABC4969E649DAD85257BE80033F32B">TTT - AS - FOLLOW - FORUMS - 00152 - FORUM.TOPIC.UPDATED - PUBLIC COMMUNITY (NEG SC NOV)</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTopic_FollowingForum_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community and then User 2 will follow that forum
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
		// User 1 will now create a topic in the forum and will update the topic
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndEditDescription(testUser1, forumsAPIUser1, baseForumTopic, editedDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create forum topic event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
			
			// Verify that the update forum topic event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* test_UpdateResponse_FollowingTopic_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Create a reply to a topic in the forum</b></li>
	*<li><b>Step: Edit the reply and save the updated reply</b></li>
	*<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities - verifications 1, 2 and 3</B></li>
	*<li><b>Verify: Verify that the forum.topic.reply.created event is displayed</b></li>
	*<li><b>Verify: Verify that the edited / updated reply is displayed</b></li>
	*<li><b>Verify: Verify the forum.topic.reply.updated event is NOT displayed</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1342A387D05AF57D85257BD60055237A">TTT - AS - FOLLOW - FORUM TOPIC - 00042 - FORUM.TOPIC.REPLY.UPDATED - PUBLIC COMMUNITY</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingTopic_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// Create the forum topic with User 2 as a follower, reply to the topic and edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply, editedReply);
		
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
	* test_UpdateResponse_FollowingForum_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Create a reply to a topic in the forum</b></li>
	*<li><b>Step: Edit the reply and save the updated reply</b></li>
	*<li><b>Step: Log in to Home as a different user who is following the forum</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities</B></li>
	*<li><b>Verify: Verify the forum.topic.reply.updated is NOT displayed in any view</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/581C093B71FEF9BC85257BE8003467CA">TTT - AS - FOLLOW - FORUMS - 00162 - FORUM.TOPIC.REPLY.UPDATED - PUBLIC COMMUNITY (NEG SC NOV)</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingForum_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
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
	 *<li><B>Name: test_LikeTopic_FollowingCommunity_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a public community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the community</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does NOT appear</b></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/130FC137313BAF4585257AC400531F7E">TTT - AS - FOLLOW - FORUMS - 00072 - forum.topic.recommended - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li>  
	 */
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingCommunity_PublicCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, publicCommunityWithFollower, baseTopic, forumsAPIUser1);
		
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
	* test_LikeTopic_FollowingTopic_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the topic</B></li>
	*<li><b>Step: User 1 like the topic</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.recommended story does not appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/62947640979EF17885257BD600559AE3">TTT - AS - FOLLOW - FORUM TOPIC - 00052 - FORUM.TOPIC.RECOMMENDED - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test ( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingTopic_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndLikeTopic(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3);
		
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
	* test_LikeTopic_FollowingForum_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the forum</B></li>
	*<li><b>Step: User 1 like the topic</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7E01CA5DD02394B485257BE80034945C">TTT - AS - FOLLOW - FORUMS - 00172 - FORUM.TOPIC.RECOMMENDED - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingForum_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
				
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
	* test_LikeResponse_FollowingCommunity_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 2 follow the public community</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8983EF7EE8425BF985257AC40053257A">TTT - AS - FOLLOW - FORUMS - 00082 - forum.topic.reply.recommended - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingCommunity_PublicCommunity() {
	
		String testName = ui.startTest();

		// Create the forum topic, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, publicCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
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
	* test_LikeResponse_FollowingForum_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 2 follow the forum in the public community</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1E65F79423FFB6CD85257BE80034F8BD">TTT - AS - FOLLOW - FORUMS - 00182 - FORUM.TOPIC.REPLY.RECOMMENDED - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingForum_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
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

	/**
	* test_LikeResponse_FollowingTopic_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the topic</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/F0B919BB7F04CAF185257BD600577A70">TTT - AS - FOLLOW - FORUM TOPIC - 00062 - FORUM.TOPIC.REPLY.RECOMMENDED - PUBLIC COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingTopic_PublicCommunity() {
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// Create the forum topic with User 2 as a follower, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, publicCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
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
}