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

public class FVT_ImFollowing_PrivateCommunity_Forums extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser3;
	private BaseCommunity baseCommunity, baseCommunityWithFollower;
	private Community restrictedCommunity, restrictedCommunityWithFollower;
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
	
		// User 1 will now create a restricted community with User 3 (acting as User 2) added as a member and with no followers
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser3);
		
		// User 1 will now create a restricted community with User 2 added as a member and also following the community
		baseCommunityWithFollower = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunityWithFollower = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseCommunityWithFollower, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunityWithFollower);
	}
		
	/**
	* test_CreateForum_FollowingCommunity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log in to Communities</B></li>
	*<li><B>Step: User 1 open a community with private access that you have owner access to</B></li>
	*<li><B>Step: User 1 add the forums widget within this community if not already present</B></li>
	*<li><B>Step: User 2 follow the community</B></li>
	*<li><b>Step: User 1 add a new forum</b></li>
	*<li><b>Step: User 2 log in to Home</b></li>
	*<li><B>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: User 2 go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.created is displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.create is displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/228957F116A2F8B9852578F8002D8C23">TTT - AS - FOLLOW - FORUMS - 00014 - forum.created - PRIVATE COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateForum_FollowingCommunity_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(restrictedCommunityWithFollower, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	* test_CreateTopic_FollowingCommunity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.topic.created is displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.topic.created is displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BD3FBE90FBE5A051852578F80030DD60">TTT - AS - FOLLOW - FORUMS - 00034 - forum.topic.created - PRIVATE COMMUNITY</a></li>  
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateTopic_FollowingCommunity_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, restrictedCommunityWithFollower, baseTopic);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunityWithFollower.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}

	/**
	* test_CreateTopic_FollowingTopic_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user and follow the topic</b></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.topic.created is not displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.topic.created is not displayed in forums</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/CE1017BBA88E70CC85257BD600500DDF">TTT - AS - FOLLOW - FORUM TOPIC - 00014 - FORUM.TOPIC.CREATED - PRIVATE COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateTopic_FollowingTopic_PrivateCommunity(){
	
		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 will follow the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, testUser3, forumsAPIUser3);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseTopic.getDescription()}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	* test_CreateResponse_FollowingCommunity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</B></li>
	*<li><B>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</B></li>
	*<li><B>Verify: Verify the forum.topic.reponse.created is displayed in communities</B></li>
	*<li><b>Verify: Verify the forum.topic.reponse.created is displayed in forums</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9FF69531BCCF51D785257BE80033E5F8">TTT - AS - FOLLOW - FORUMS - 00144 - FORUM.RESPONSE.CREATED - PRIVATE COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingCommunity_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a forum topic and will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, restrictedCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
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
	 *<li><B>Name: test_CreateResponse_FollowingTopic_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities</B></li>
	 *<li><B>Step: Open a community with private access that you have owner access to</B></li>	
	 *<li><B>Step: Respond to a topic within the forum that is in the community</B></li>
	 *<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home / Activity Stream / I'm Following Filtered by Forums - verification point #2</b></li>
	 *<li><b>Verify: Verify that the forum.response.created is displayed in communities</b></li>
	 *<li><B>Verify: Verify that the forum.response.created is displayed in forums</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/41195C560CBF219885257BD60050CF07">TTT - AS - FOLLOW - FORUM TOPIC - 00024 - FORUM.RESPONSE.CREATED - PRIVATE COMMUNITY</a></li> 
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateResponse_FollowingTopic_PrivateCommunity(){

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic and User 2 follows it and User 1 will reply to that topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createTopicWithOneFollowerAndAddReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseTopic.getDescription(), topicReply}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	* test_UpdateTopic_FollowingTopic_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as User 2 who has access to the Community and follow the Forum topic.</b></li>
	*<li><b>Step: User 1 update the topic in the forum</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities</B></li>
	*<li><b>Verify: Verify the forum.topic.updated is displayed in any view</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/75A04FBB854417BB85257BD600515A09">TTT - AS - FOLLOW - FORUM TOPIC - 00034 - FORUM.TOPIC.UPDATED - PRIVATE COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTopic_FollowingTopic_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower and will edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic topic = CommunityForumEvents.createForumTopicWithOneFollower(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, testUser3, forumsAPIUser3);
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
	* test_UpdateForum_FollowingForum_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Update a forum that is in the community</B></li>
	*<li><b>Step: Log in to Home as a different user who is following the forum</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities</B></li>
	*<li><b>Verify: Verify the forum.updated is displayed in any view</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2B300DAEE3DAB69B85257BE80033688E">TTT - AS - FOLLOW - FORUMS - 00124 - FORUM.UPDATED - PRIVATE COMMUNITY</a></li>  
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateForum_FollowingForum_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a new community forum, User 2 will follow that forum and then User 1 will edit the description of the forum
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForumWithOneFollowerAndEditDescription(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3, editedDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String updateForumEvent = CommunityForumNewsStories.getUpdateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());
		
		// Verify that the event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateForumEvent, editedDescription}, TEST_FILTERS, true);

		ui.endTest();
	}
	
	/**
	* test_UpdateResponse_FollowingTopic_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Create a reply to a topic in the forum</b></li>
	*<li><b>Step: Edit the reply and save the updated reply</b></li>
	*<li><b>Step: Log in to Home as a different user who is following the topic</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities - verifications 1, 2 and 3</B></li>
	*<li><b>Verify: Verify that the forum.topic.reply.created event is displayed</b></li>
	*<li><b>Verify: Verify that the edited / updated reply is displayed</b></li>
	*<li><b>Verify: Verify the forum.topic.reply.updated event is NOT displayed</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C07E02917C1F98B985257BD60055510E">TTT - AS - FOLLOW - FORUM TOPIC - 00044 - FORUM.TOPIC.REPLY.UPDATED - PRIVATE COMMUNITY</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingTopic_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForum(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// Create the forum topic with User 2 as a follower, reply to the topic and edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply, editedReply);
		
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
	* test_UpdateResponse_FollowingCommunity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Communities</B></li>
	*<li><B>Step: Open a community with private access that you have owner access to</B></li>
	*<li><B>Step: Create a topic within a forum that is in the community</B></li>
	*<li><b>Step: Create a reply to a topic in the forum</b></li>
	*<li><b>Step: Edit the reply and save the updated reply</b></li>
	*<li><b>Step: Log in to Home as a different user who is following the community</b></li>
	*<li><B>Step: Go to Home /  I'm Following / All, Forums & Communities</B></li>
	*<li><b>Verify: Verify the forum.topic.reply.updated is NOT displayed in all views</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EFAC5BF29C782A46852579BB006C8DA0">TTT - AS - FOLLOW - FORUMS - 00064 - forum.topic.reply.updated - PRIVATE COMMUNITY (NEG SC NOV)</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateResponse_FollowingCommunity_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create a community forum topic, add a reply to the topic and then edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, restrictedCommunityWithFollower, baseForumTopic, forumsAPIUser1, topicReply, editedTopicReply);
		
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
	 *<li><B>Name: test_LikeTopic_FollowingCommunity_PrivateCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 go to a private community you own</B></li>
	 *<li><B>Step: User 1 start a topic in the forum in the community</B></li>	
	 *<li><B>Step: User 2 follow the community</B></li>
	 *<li><b>Step: User 1 like the topic</b></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	 *<li><b>Verify: Verify that the forum.topic.recommended story does NOT appear</b></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/229E84011D92D13385257AC4005321FE">TTT - AS - FOLLOW - FORUMS - 00074 - forum.topic.recommended - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li>  
	 */
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingCommunity_PrivateCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a forum topic and like the topic
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, restrictedCommunityWithFollower, baseTopic, forumsAPIUser1);
		
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
	* test_LikeTopic_FollowingTopic_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private community you own and User 2 is a member of</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the topic</B></li>
	*<li><b>Step: User 1 like the topic</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.recommended story does not appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A6BA95EF2215C1AC85257BD60056B4E0">TTT - AS - FOLLOW - FORUM TOPIC - 00054 - FORUM.TOPIC.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingTopic_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum topic with User 2 as a follower
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndLikeTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3);
		
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
	* test_LikeTopic_FollowingForum_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private community you own and User 2 is a member of</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the forum</B></li>
	*<li><b>Step: User 1 like the topic</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7241711522AD181785257BE80034E132">TTT - AS - FOLLOW - FORUMS - 00174 - FORUM.TOPIC.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeTopic_FollowingForum_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
				
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
	* test_LikeResponse_FollowingCommunity_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a public community you own</B></li>
	*<li><B>Step: User 2 follow the private community</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/1110935D1605A20B85257AC40053281F">TTT - AS - FOLLOW - FORUMS - 00084 - forum.topic.reply.recommended - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingCommunity_PrivateCommunity(){
	
		String testName = ui.startTest();

		// Create the forum topic, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunityWithFollower);
		CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, restrictedCommunityWithFollower, baseTopic, forumsAPIUser1, topicReply);
		
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
	* test_LikeResponse_FollowingTopic_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private community you own and User 2 is a member of</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: User 2 follow the topic</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does not appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6B12379A8515905185257BD60058519E">TTT - AS - FOLLOW - FORUM TOPIC - 00064 - FORUM.TOPIC.REPLY.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li>  
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingTopic_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// Create the forum topic with User 2 as a follower, add a reply and like the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicWithOneFollowerAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseTopic, forumsAPIUser1, testUser3, forumsAPIUser3, topicReply);
		
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
	* test_LikeResponse_FollowingForum_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 go to a private community you own and User 2 is a member of</B></li>
	*<li><B>Step: User 2 follow the forum in the private community</B></li>
	*<li><B>Step: User 1 start a topic in the forum in the community</B></li>
	*<li><b>Step: User 1 reply to the topic</b></li>
	*<li><b>Step: User 1 like the reply</b></li>
	*<li><b>Step: User 2 go to Home / I'm Following / All, Communities & Forums</b></li>
	*<li><b>Verify: Verify that the forum.topic.reply.recommended story does NOT appear</b></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/93A4B04FEBA17CB285257BE8003542A7">TTT - AS - FOLLOW - FORUMS - 00184 - FORUM.TOPIC.REPLY.RECOMMENDED - PRIVATE COMMUNITY FORUM (NEG SC NOV)</a></li> 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LikeResponse_FollowingForum_PrivateCommunity() {

		/**
		 * This test case will use User 3 as User 2
		 */
		String testName = ui.startTest();

		// User 1 will now create a forum in the community with User 2 as a follower
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForumWithOneFollower(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum, testUser3, forumsAPIUser3);
		
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