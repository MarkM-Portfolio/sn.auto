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
public class FVT_YourTopic_You_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2, forumsAPIUser3, forumsAPIUser4, forumsAPIUser5, forumsAPIUser6, forumsAPIUser7;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(7);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		forumsAPIUser5 = initialiseAPIForumsHandlerUser(testUser5);
		forumsAPIUser6 = initialiseAPIForumsHandlerUser(testUser6);
		forumsAPIUser7 = initialiseAPIForumsHandlerUser(testUser7);
		
		// User 1 will now create a public community with multiple members added - this is required so as the users can post replies to forum topics
		User membersList[] = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourTopic_Reply_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 1 reply to the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 reply to the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 reply to the topic</B></li>
	*<li><B>Step: User 3 to User 7 reply to the topic</B></li>
	*<li><B>Step: User 1 reply to the topic again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "You and 6 others replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B9E157E994BF5EA285257DEA003E562A">TTT - MY NOTIFICATIONS - FORUMS - 00011 - REPLY TO YOUR OWN TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_YourTopic_Reply_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply to that topic
		String user1ForumTopicReply1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic communityForumTopic = CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, user1ForumTopicReply1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String replyToForumTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the forum topic reply event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToForumTopicEvent, baseForumTopic.getDescription(), user1ForumTopicReply1}, TEST_FILTERS, false);
		
		// User 2 will now post a reply to the community forum topic
		String user2ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, communityForumTopic, user2ForumTopicReply);
		
		// Create the news story to be verified
		replyToForumTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_UserAndYou(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the forum topic reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToForumTopicEvent, baseForumTopic.getDescription(), user1ForumTopicReply1, user2ForumTopicReply}, TEST_FILTERS, true);
		
		// User 3 through to User 7 will now post a reply to the community forum topic with User 1 posting another reply after that
		String user3ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser3, forumsAPIUser3, communityForumTopic, user3ForumTopicReply);
		
		String user4ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser4, forumsAPIUser4, communityForumTopic, user4ForumTopicReply);
		
		String user5ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser5, forumsAPIUser5, communityForumTopic, user5ForumTopicReply);
		
		String user6ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser6, forumsAPIUser6, communityForumTopic, user6ForumTopicReply);
		
		String user7ForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser7, forumsAPIUser7, communityForumTopic, user7ForumTopicReply);
		
		String user1ForumTopicReply2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, communityForumTopic, user1ForumTopicReply2);
		
		// Create the news story to be verified
		replyToForumTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_YouAndMany(ui, "6", baseForumTopic.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the forum topic reply event, User 7's reply and User 1's second reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToForumTopicEvent, baseForumTopic.getDescription(), user7ForumTopicReply, user1ForumTopicReply2}, filter, true);
			
			// Verify that User 1's first comment and the comments posted by Users 2 through to User 6 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1ForumTopicReply1, user2ForumTopicReply, user3ForumTopicReply, user4ForumTopicReply, user5ForumTopicReply, user6ForumTopicReply}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_Topic_Like_YourReply_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 1 reply to the topic and like the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 like User 1's reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 reply to the topic again</B></li>
	*<li><B>Step: User 3 to User 5 like the reply</B></li>
	*<li><B>Step: User 1 like the reply again</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your reply to the {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "You and 3 others liked your reply to the {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/75527142BD9E26AB85257DEA003E7939">TTT - MY NOTIFICATIONS - FORUMS - 00041 - LIKE A REPLY ON YOUR OWN TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Topic_Like_YourReply_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic communityForumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 1 will now post a reply to the forum topic
		String user1Reply1 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply user1ForumTopicReply1 = CommunityForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, communityForumTopic, user1Reply1);
		
		// User 1 will now like / recommend the forum topic reply
		CommunityForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, user1ForumTopicReply1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
				
		// Create the news story to be verified
		String likeReplyEvent = CommunityForumNewsStories.getLikeReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseCommunity.getName());
				
		// Verify that the like reply event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeReplyEvent, baseForumTopic.getDescription(), user1Reply1}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend the forum topic reply
		CommunityForumEvents.likeForumTopicReply(testUser2, forumsAPIUser2, user1ForumTopicReply1);
		
		// Create the news story to be verified
		likeReplyEvent = CommunityForumNewsStories.getLikeYourReplyToTheTopicNewsStory_UserAndYou(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the like reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeReplyEvent, baseForumTopic.getDescription(), user1Reply1}, TEST_FILTERS, true);
		
		// User 1 will now post a second reply to the forum topic
		String user1Reply2 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply user1ForumTopicReply2 = CommunityForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, communityForumTopic, user1Reply2);
		
		// User 3 through to User 5 will now like / recommend the second forum topic reply with User 1 then liking / recommending the reply
		CommunityForumEvents.likeForumTopicReply(testUser3, forumsAPIUser3, user1ForumTopicReply2);
		CommunityForumEvents.likeForumTopicReply(testUser4, forumsAPIUser4, user1ForumTopicReply2);
		CommunityForumEvents.likeForumTopicReply(testUser5, forumsAPIUser5, user1ForumTopicReply2);
		CommunityForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, user1ForumTopicReply2);
		
		// Create the news story to be verified
		likeReplyEvent = CommunityForumNewsStories.getLikeYourReplyToTheTopicNewsStory_YouAndMany(ui, "3", baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the like reply event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeReplyEvent, baseForumTopic.getDescription(), user1Reply1, user1Reply2}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_YourTopic_Like_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 1 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 1 start another topic in the forum</B></li>
	*<li><B>Step: User 3 and User 4 like the topic</B></li>
	*<li><B>Step: User 1 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify  no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} liked your topic named {topicName} in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "You and 2 others liked your topic named {topicName} in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C17B83CE5162DED485257DEA003E562E">TTT - MY NOTIFICATIONS - FORUMS - 00031 - LIKE YOUR OWN TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_YourTopic_Like_You_Others_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will like / recommend the topic
		BaseForumTopic baseForumTopic1 = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic communityForumTopic1 = CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic1, forumsAPIUser1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
						
		// Create the news story to be verified
		String likeForumTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_You(ui, baseForumTopic1.getTitle(), baseCommunity.getName());
						
		// Verify that the like forum topic event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeForumTopicEvent, baseForumTopic1.getDescription()}, TEST_FILTERS, false);
		
		// User 2 will now like / recommend the forum topic
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, communityForumTopic1);
		
		// Create the news story to be verified
		likeForumTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_UserAndYou(ui, baseForumTopic1.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the like forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeForumTopicEvent, baseForumTopic1.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now create another forum topic in the community forum
		BaseForumTopic baseForumTopic2 = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic communityForumTopic2 = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic2);
		
		// User 3 and User 4 will now like / recommend the second forum topic with User 1 then liking / recommending the forum topic
		CommunityForumEvents.likeForumTopic(testUser3, forumsAPIUser3, communityForumTopic2);
		CommunityForumEvents.likeForumTopic(testUser4, forumsAPIUser4, communityForumTopic2);
		CommunityForumEvents.likeForumTopic(testUser1, forumsAPIUser1, communityForumTopic2);
		
		// Create the news story to be verified
		likeForumTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_YouAndMany(ui, "2", baseForumTopic2.getTitle(), baseCommunity.getName());
		
		// Verify that the like forum topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeForumTopicEvent, baseForumTopic2.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}