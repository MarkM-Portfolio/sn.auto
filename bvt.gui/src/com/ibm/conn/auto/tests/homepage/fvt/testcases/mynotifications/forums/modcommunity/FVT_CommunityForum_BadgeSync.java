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
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [FVT Automation for Story 154601] Scynchronize realtime updates with badging
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/156301
 * @author Patrick Doherty
 */
public class FVT_CommunityForum_BadgeSync extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
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
	* test_TopicLike_BadgeSync() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a moderate community you own adding User 2 as a member</B></li>
	*<li><B>Step: User 1 add the Forums Widget</B></li>
	*<li><B>Step: User 1 add a topics</B></li>
	*<li><B>Step: User 2 like the topic</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view and the Notification Center</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B5A18884C50B0CB85257E6F003C510C">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00050 - FORUMS EVENTS MY NOTIFICATIONS BADGE SYNCED WITH HEADER</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_TopicLike_BadgeSync() {

		String testName = ui.startTest();
		
		// User 1 will now add a topic to the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to I'm Following to reset the My Notifications badge to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that both the My Notifications badge and Notification Center badge have been reset to 0 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now like the forum topic
		CommunityForumEvents.likeForumTopic(testUser2, forumsAPIUser2, forumTopic);
		
		// Refresh the badges by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that both the My Notifications badge and Notification Center badge now have a value of 1 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}