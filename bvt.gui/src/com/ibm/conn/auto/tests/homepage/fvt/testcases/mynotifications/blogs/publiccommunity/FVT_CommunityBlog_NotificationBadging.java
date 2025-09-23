package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs.publiccommunity;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016             		                 */
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

public class FVT_CommunityBlog_NotificationBadging extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3, communityBlogsAPIUser4;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);
		communityBlogsAPIUser4 = initialiseAPICommunityBlogsHandlerUser(testUser4);
		
		// User 1 will now create a public community with the Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community now that the test has completed
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_Entry_LikeEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Public Community adding the Blogs widget</B></li>
	*<li><B>Step: User 1 create an entry in the Blog</B></li>
	*<li><B>Step: User 2 and User 3 like the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>Step: User 4 like the entry</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>Step: User 2 unlike the entry</B></li>
	*<li><B>Step: User 1 refresh the I'm Following view again</B></li>
	*<li><B>Step: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/65ED151219115C3585257E12006DC9B1">TTT - NOTIFICATIONS BADGING - BLOGS - 00011 - ENTRY LIKE EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Entry_LikeEvents(){

		String testName = ui.startTest();
		
		// User 1 will now add an entry to the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view - this will reset the My Notifications badge value to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badge values have been reset to 0 as expected
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 will now like / recommend the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogEntry, testUser2, communityBlogsAPIUser2);
		
		// User 3 will now like / recommend the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogEntry, testUser3, communityBlogsAPIUser3);
		
		// Navigate to the I'm Following view - this will update the My Notifications badge values
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badge values now have the expected value of '1'
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 4 will now like / recommend the community blog entry
		CommunityBlogEvents.likeOrVote(communityBlogEntry, testUser4, communityBlogsAPIUser4);
		
		// Navigate to the I'm Following view - this will update the My Notifications badge values
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badge values now have the expected value of '1'
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// User 2 will now unlike the community blog entry again
		CommunityBlogEvents.unlikeOrRemoveVote(communityBlogEntry, testUser2, communityBlogsAPIUser2);
		
		// Navigate to the I'm Following view - this will update the My Notifications badge values
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badge values now have the expected value of '1'
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();	
	}
}