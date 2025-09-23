package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.forums;

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
/* Copyright IBM Corp. 2015, 2016 		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_CommunityForumEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2;
	private BaseCommunity moderatedBaseCommunity, publicBaseCommunity, restrictedBaseCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
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
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
		// User 1 will now create a moderated community with User 2 added as a member
		moderatedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneMember(moderatedBaseCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// User 1 will now create a public community with User 2 added as a member
		publicBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneMember(publicBaseCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// User 1 will now create a restricted community with User 2 added as a member
		restrictedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(restrictedBaseCommunity, testUser1, communitiesAPIUser1, testUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_TopicReply_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a forum</B></li>
	*<li><B>Step: User 1 add a topic</B></li>
	*<li><B>Step: User 2 reply to the topic twice</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 like one of User 2's replies</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/06E10A9CEE35B55B85257E2F0036A460">TTT - INLINE COMMENTS - 00060 - FORUM EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_TopicReply_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a forum topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), publicBaseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// User 2 will now post two replies to the topic
		String user2Reply1 = Data.getData().commonComment + Helper.genStrongRand();
		String user2Reply2 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply forumReply = CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply1);
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply2);
		
		// Log in as User 1 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String topicReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), publicBaseCommunity.getName(), testUser2.getDisplayName());

		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		// User 1 will now like one of User 2's replies
		CommunityForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, forumReply);
		
		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_TopicReply_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a forum</B></li>
	*<li><B>Step: User 1 add a topic</B></li>
	*<li><B>Step: User 2 reply to the topic twice</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 like one of User 2's replies</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/06E10A9CEE35B55B85257E2F0036A460">TTT - INLINE COMMENTS - 00060 - FORUM EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_TopicReply_ModCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a forum topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), moderatedBaseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic);
		
		// User 2 will now post two replies to the topic
		String user2Reply1 = Data.getData().commonComment + Helper.genStrongRand();
		String user2Reply2 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply forumReply = CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply1);
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply2);
		
		// Log in as User 1 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String topicReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), moderatedBaseCommunity.getName(), testUser2.getDisplayName());

		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		// User 1 will now like one of User 2's replies
		CommunityForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, forumReply);
		
		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* test_TopicReply_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a forum</B></li>
	*<li><B>Step: User 1 add a topic</B></li>
	*<li><B>Step: User 2 reply to the topic twice</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 1 like one of User 2's replies</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/06E10A9CEE35B55B85257E2F0036A460">TTT - INLINE COMMENTS - 00060 - FORUM EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_TopicReply_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a forum topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic);
		
		// User 2 will now post two replies to the topic
		String user2Reply1 = Data.getData().commonComment + Helper.genStrongRand();
		String user2Reply2 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply forumReply = CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply1);
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2Reply2);
		
		// Log in as User 1 and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String topicReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), restrictedBaseCommunity.getName(), testUser2.getDisplayName());

		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		// User 1 will now like one of User 2's replies
		CommunityForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, forumReply);
		
		// Verify that the forum topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription(), user2Reply1, user2Reply2}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}