package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs.publiccommunity;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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
/* Copyright IBM Corp. 2015, 2016, 2017                              */
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

public class FVT_IdeationBlogMentions_NotificationBadging extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass(){

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 create a public community and add the Ideation Blogs widget to that community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown(){
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_IdeationBlog_MentionsEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public community ideation blog</B></li>
	*<li><B>Step: User 1 add an idea mentioning User 2</B></li>
	*<li><B>Step: User 1 add 3 comments on each mentioning User 2</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 2 look at the badge number of the Mentions view - verification point 1</B></li>
	*<li><B>Step: User 2 look at the badge number of the My Notifications view - verification point 2</B></li>
	*<li><B>Verify: Verify the badge on the Mentions view shows the number '4'</B></li>
	*<li><B>Verify: Verify the badge on the My Notifications view shows the number '1'</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9D7FCBBCF2CE459D85257E180040B5DD">TTT - NOTIFICATIONS BADGING - IDEATION BLOGS - 00016 - IDEA MENTIONS EVENTS</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_IdeationBlog_MentionsEvents() {

		String testName = ui.startTest();
		
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Navigate to the Mentions view - this will reset the Mentions and My Notifications badge values to 0
		UIEvents.gotoMentions(ui);
		
		// Navigate to the I'm Following view - switching focus away from the Mentions and My Notifications views
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// Verify that the Mentions badge value has been reset to 0
		HomepageValid.verifyMentionsBadgeValue(driver, 0);
		
		// User 1 will now create an Ideation blog idea with a mention to User 2 
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdeationBlogIdeaWithMentions(testUser1, communityBlogsAPIUser1, mentions, baseIdea, publicCommunity);
		
		// User 1 will now post 3 comments with mentions to User 2
		for(int index = 0; index < 3; index ++){
			CommunityBlogEvents.createCommentWithMentions(idea, mentions, testUser1, communityBlogsAPIUser1);
		}
		// Refresh the I'm Following view - this will update the My Notifications and Mentions badge values to 1 and 4 respectively
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notifications Center badge values have now been updated to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// Verify that the Mentions badge value has now been updated to 4
		HomepageValid.verifyMentionsBadgeValue(driver, 4);
		
		ui.endTest();
	}
}