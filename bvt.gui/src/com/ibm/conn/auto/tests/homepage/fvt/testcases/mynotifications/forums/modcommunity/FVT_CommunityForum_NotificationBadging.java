package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.forums.modcommunity;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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
 * [Notification/Mentions Badges] FVT UI Automation for Story 146307
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/147242
 * @author Patrick Doherty
 */

public class FVT_CommunityForum_NotificationBadging extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser2, forumsAPIUser3, forumsAPIUser4, forumsAPIUser5, forumsAPIUser6;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(6);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		forumsAPIUser3 = initialiseAPIForumsHandlerUser(testUser3);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		forumsAPIUser5 = initialiseAPIForumsHandlerUser(testUser5);
		forumsAPIUser6 = initialiseAPIForumsHandlerUser(testUser6);
		
		// User 1 will now create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(moderatedCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	* test_Topic_LikeEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a moderate community you own adding User 2,3,4,5,6 as members</B></li>
	*<li><B>Step: User 1 add the Forums Widget</B></li>
	*<li><B>Step: User 1 add 3 topics</B></li>
	*<li><B>Step: User 2 like all topics</B></li>
	*<li><B>Step: User 3 like the last 2 topics</B></li>
	*<li><B>Step: User 4 like the first 2 topics</B></li>
	*<li><B>Step: User 5 and User 6 like the first topic</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 4 unlike the first topic</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '3'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/05D13CDAC322D9FA85257E18003BB48E">TTT - NOTIFICATIONS BADGING - FORUMS - 00014 - DIFFERENT TOPIC LIKE EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Topic_LikeEvents() {

		String testName = ui.startTest();
		
		// User 1 will now add the first topic to the community forum
		BaseForumTopic baseForumTopic1 = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic1 = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic1);
		
		// User 1 will now add the second topic to the community forum
		BaseForumTopic baseForumTopic2 = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic2 = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic2);
		
		// User 1 will now add the third topic to the community forum
		BaseForumTopic baseForumTopic3 = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic3 = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic3);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to I'm Following to reset the My Notifications badge to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that both the My Notifications badge and Notification Center badge have been reset to 0 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now like all three of the forum topics
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, forumTopic1);
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, forumTopic2);
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, forumTopic3);
		
		// User 3 will now like the last two forum topics only
		CommunityForumEvents.likeForumTopic(testUser3, forumsAPIUser3, forumTopic2);
		CommunityForumEvents.likeForumTopic(testUser3, forumsAPIUser3, forumTopic3);
		
		// User 4 will now like the first two forum topics only
		String user4LikeTopicURL = CommunityForumEvents.likeForumTopic(testUser4, forumsAPIUser4, forumTopic1);
		CommunityForumEvents.likeForumTopic(testUser4, forumsAPIUser4, forumTopic2);
		
		// User 5 will now like the first topic only
		CommunityForumEvents.likeForumTopic(testUser5, forumsAPIUser5, forumTopic1);
		
		// User 6 will now like the first topic only
		CommunityForumEvents.likeForumTopic(testUser6, forumsAPIUser6, forumTopic1);
		
		// Refresh the current page to refresh the My Notifications and Notification Center badge counters
		UIEvents.refreshPage(driver);
		
		// Verify that both the My Notifications badge and Notification Center badge now have a value of 3 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 3);
		
		// User 4 will now unlike the first topic only
		CommunityForumEvents.unlikeForumTopic(testUser4, forumsAPIUser4, forumTopic1, user4LikeTopicURL);
		
		// Refresh the badges by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that both the My Notifications badge and Notification Center badge still have a value of 3 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 3);
		
		ui.endTest();
	}
}