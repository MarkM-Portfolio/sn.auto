package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.forums.publiccommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
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
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016	                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */
public class FVT_YourTopic_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2, forumsAPIUser3, forumsAPIUser4, forumsAPIUser5, forumsAPIUser6, forumsAPIUser7, forumsAPIUser8, forumsAPIUser9, forumsAPIUser10;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(10);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		testUser10 = listOfStandardUsers.get(9);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		forumsAPIUser5 = initialiseAPIForumsHandlerUser(testUser5);
		forumsAPIUser6 = initialiseAPIForumsHandlerUser(testUser6);
		forumsAPIUser7 = initialiseAPIForumsHandlerUser(testUser7);
		forumsAPIUser8 = initialiseAPIForumsHandlerUser(testUser8);
		forumsAPIUser9 = initialiseAPIForumsHandlerUser(testUser9);
		forumsAPIUser10 = initialiseAPIForumsHandlerUser(testUser10);
		
		// User 1 will now create a public community with all relevant members added
		User[] membersList = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser10 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(publicCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	* test_YourTopic_Reply_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 reply to the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 reply to the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 5 reply to the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 5} and 3 others replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2F0E99CE14B04F8485257DEA003E5629">TTT - MY NOTIFICATIONS - FORUMS - 00010 - REPLY TO TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_YourTopic_Reply_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 2 will now post a reply to the community forum topic
		String user2TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2TopicReply);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the reply to forum topic event and User 2's topic reply are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2TopicReply}, TEST_FILTERS, true);
		
		// User 3 will now post a reply to the community forum topic
		String user3TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser3, forumsAPIUser3, forumTopic, user3TopicReply);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the reply to forum topic event and User 2's and User 3's topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2TopicReply, user3TopicReply}, TEST_FILTERS, true);
		
		// User 4 will now post a reply to the community forum topic
		String user4TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser4, forumsAPIUser4, forumTopic, user4TopicReply);
		
		// User 5 will now post a reply to the community forum topic
		String user5TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser5, forumsAPIUser5, forumTopic, user5TopicReply);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_UserAndMany(ui, testUser5.getDisplayName(), "3", baseForumTopic.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to forum topic event and User 4's and User 5's topic replies are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user4TopicReply, user5TopicReply}, filter, true);
			
			// Verify that User 2's and User 3's topic replies are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2TopicReply, user3TopicReply}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_Topic_Like_YourReply_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 1 reply to the topic</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 like the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 5 like the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your reply to the {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your reply to the {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 4} and 2 others liked your reply to the {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC2DC9637AAFFD6185257DEA003E7938">TTT - MY NOTIFICATIONS - FORUMS - 00040 - LIKE A TOPIC REPLY ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Topic_Like_YourReply_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 1 will now post a reply to the community forum topic
		String user1TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply user1ForumReply = CommunityForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, forumTopic, user1TopicReply);
		
		// User 2 will now like the community forum topic reply posted by User 1
		CommunityForumEvents.likeForumTopicReply(testUser2, forumsAPIUser2, user1ForumReply);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String likeTopicReplyEvent = CommunityForumNewsStories.getLikeReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like forum topic reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReplyEvent, baseForumTopic.getDescription(), user1TopicReply}, TEST_FILTERS, true);
		
		// User 3 will now like the community forum topic reply posted by User 1
		CommunityForumEvents.likeForumTopicReply(testUser3, forumsAPIUser3, user1ForumReply);
		
		// Create the news story to be verified
		likeTopicReplyEvent = CommunityForumNewsStories.getLikeYourReplyToTheTopicNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the like forum topic reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReplyEvent, baseForumTopic.getDescription(), user1TopicReply}, TEST_FILTERS, true);
		
		// User 3 will now like the community forum topic reply posted by User 1
		CommunityForumEvents.likeForumTopicReply(testUser4, forumsAPIUser4, user1ForumReply);
		
		// Create the news story to be verified
		likeTopicReplyEvent = CommunityForumNewsStories.getLikeYourReplyToTheTopicNewsStory_UserAndMany(ui, testUser4.getDisplayName(), "2", baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the like forum topic reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicReplyEvent, baseForumTopic.getDescription(), user1TopicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_Topic_Update_YourReply_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 reply to the topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 reply to the topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 reply to the topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "<user 2> replied to your <topicEntryName> topic thread in the <forumName> forum"</B></li>
	*<li><B>Verify: Verify the event shows "<user 3> and <user 2> replied to your <topicEntryName> topic thread in the <forumName> forum"</B></li>
	*<li><B>Verify: Verify the event shows "<user 5> and 3 others replied to your <topicEntryName> topic thread in the <forumName> forum"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A70CF798E9CAF09F85257DEA003E562B">TTT - MY NOTIFICATIONS - FORUMS - 00020 - UPDATE REPLY TO TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Topic_Update_YourReply_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 2 will now post a reply to the community forum topic and will edit the reply
		String user2TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser2, forumsAPIUser2, forumTopic, user2TopicReply, user2EditedTopicReply);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the reply to forum topic event and User 2's edited comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2EditedTopicReply}, TEST_FILTERS, true);
		
		// User 3 will now post a reply to the community forum topic and will edit the reply
		String user3TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user3EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser3, forumsAPIUser3, forumTopic, user3TopicReply, user3EditedTopicReply);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseForumTopic.getTitle(), baseCommunity.getName());
				
		// Verify that the reply to forum topic event and User 2's and User 3's edited comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2EditedTopicReply, user3EditedTopicReply}, TEST_FILTERS, true);
		
		// User 4 will now post a reply to the community forum topic and will edit the reply
		String user4TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user4EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser4, forumsAPIUser4, forumTopic, user4TopicReply, user4EditedTopicReply);
		
		// User 5 will now post a reply to the community forum topic and will edit the reply
		String user5TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user5EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser5, forumsAPIUser5, forumTopic, user5TopicReply, user5EditedTopicReply);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_UserAndMany(ui, testUser5.getDisplayName(), "3", baseForumTopic.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to forum event and User 4's and User 5's edited comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user4EditedTopicReply, user5EditedTopicReply}, filter, true);
			
			// Verify that User 2's and User 3's edited comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2EditedTopicReply, user3EditedTopicReply}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_YourTopic_Like_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 3 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 4 to User 10 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your topic named {topicName} in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} liked your topic named {topicName} in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 10} and 8 others liked your topic named {topicName} in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4A53E3488AA0B65B85257DEA003E562D">TTT - MY NOTIFICATIONS - FORUMS - 00030 - LIKE A TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_YourTopic_Like_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 2 will now like / recommend the forum topic
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, forumTopic);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
						
		// Create the news story to be verified
		String likeTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the like forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		// User 3 will now like / recommend the forum topic
		CommunityForumEvents.likeForumTopic(testUser3, forumsAPIUser3, forumTopic);
		
		// Create the news story to be verified
		likeTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseForumTopic.getTitle(), baseCommunity.getName());
						
		// Verify that the like forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		// Users 4 through to User 10 will now like / recommend the forum topic
		CommunityForumEvents.likeForumTopic(testUser4, forumsAPIUser4, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser5, forumsAPIUser5, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser6, forumsAPIUser6, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser7, forumsAPIUser7, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser8, forumsAPIUser8, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser9, forumsAPIUser9, forumTopic);
		CommunityForumEvents.likeForumTopic(testUser10, forumsAPIUser10, forumTopic);
		
		// Create the news story to be verified
		likeTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_UserAndMany(ui, testUser10.getDisplayName(), "8", baseForumTopic.getTitle(), baseCommunity.getName());
								
		// Verify that the like forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}