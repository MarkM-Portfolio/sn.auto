package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
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
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_StandaloneForumEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };

	private APIForumsHandler forumsAPIUser1, forumsAPIUser2;
	private BaseForum baseForum;
	private Forum standaloneForum;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
		// User 1 will now create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the forum created during the test
		forumsAPIUser1.deleteForum(standaloneForum);
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
	@Test(groups ={"fvtonprem"})
	public void test_TopicReply_StandaloneForum() {

		String testName = ui.startTest();

		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumTopic forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 will now post a reply to the forum topic
		String forumTopicReply1 = Data.getData().commonComment + Helper.genStrongRand();
		ForumReply user2Reply1 = ForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, forumTopicReply1);
		
		// User 2 will now post a second reply to the forum topic
		String forumTopicReply2 = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, forumTopicReply2);

		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = ForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser2.getDisplayName());
		
		// Verify that the reply to forum topic event and both of User 2's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), forumTopicReply1, forumTopicReply2}, TEST_FILTERS, true);
		
		// User 1 will now like / recommend one of the replies posted by User 2
		ForumEvents.likeForumTopicReply(testUser1, forumsAPIUser1, user2Reply1);
		
		// Verify that the reply to forum topic event and both of User 2's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), forumTopicReply1, forumTopicReply2}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}