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
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourTopic_UpdateReply_You_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2, forumsAPIUser3, forumsAPIUser4, forumsAPIUser5;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		forumsAPIUser5 = initialiseAPIForumsHandlerUser(testUser5);
		
		// User 1 will now create a public community with all relevant members added
		User[] membersList = { testUser2, testUser3, testUser4, testUser5 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(publicCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	* test_Topic_Update_YourReply_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a topic in the forum</B></li>
	*<li><B>Step: User 1 reply to your topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community forum</B></li>
	*<li><B>Step: User 2 reply to the topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 5 reply to the topic and edit the replies</B></li>
	*<li><B>Step: User 1 reply to your topic and edit the reply</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "<user 2> and you replied to your <topicEntryName> topic thread in the <forumName> forum" </B></li>
	*<li><B>Verify: Verify the event shows "You and 4 others replied to your <topicEntryName> topic thread in the <forumName> forum"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4AC49885BA7B64D85257DEA003E562C">TTT - MY NOTIFICATIONS - FORUMS - 00021 - UPDATED REPLY TO YOUR OWN TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Topic_Update_YourReply_You_Others_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum, will post a reply to that topic and then update the reply
		String user1TopicReply1 = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedTopicReply1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, user1TopicReply1, user1EditedTopicReply1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseCommunity.getName());
		
		// Verify that the reply to topic event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user1EditedTopicReply1}, TEST_FILTERS, false);
		
		// User 2 will now post a reply to the topic and then update the reply
		String user2TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user2EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser2, forumsAPIUser2, forumTopic, user2TopicReply, user2EditedTopicReply);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_UserAndYou(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the reply to topic event and User 1's and User 2's edited replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2EditedTopicReply, user1EditedTopicReply1}, TEST_FILTERS, true);
		
		// User 3 will now post a reply to the topic and then update the reply
		String user3TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user3EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser3, forumsAPIUser3, forumTopic, user3TopicReply, user3EditedTopicReply);
				
		// User 4 will now post a reply to the topic and then update the reply
		String user4TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user4EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser4, forumsAPIUser4, forumTopic, user4TopicReply, user4EditedTopicReply);
				
		// User 5 will now post a reply to the topic and then update the reply
		String user5TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String user5EditedTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser5, forumsAPIUser5, forumTopic, user5TopicReply, user5EditedTopicReply);
		
		// User 1 will now post a second reply to the topic and then update the second reply
		String user1TopicReply2 = Data.getData().commonComment + Helper.genStrongRand();
		String user1EditedTopicReply2 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReplyAndEditReply(testUser1, forumsAPIUser1, forumTopic, user1TopicReply2, user1EditedTopicReply2);
		
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_YouAndMany(ui, "4", baseForumTopic.getTitle(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to topic event, User 1's second updated reply and User 5's reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user5EditedTopicReply, user1EditedTopicReply2}, filter, true);
			
			// Verify that User 1's first updated reply, User 2's updated reply, User 3's updated reply and User 4's updated reply are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1EditedTopicReply1, user2EditedTopicReply, user3EditedTopicReply, user4EditedTopicReply}, null, false);
		}
		ui.endTest();
	}
}