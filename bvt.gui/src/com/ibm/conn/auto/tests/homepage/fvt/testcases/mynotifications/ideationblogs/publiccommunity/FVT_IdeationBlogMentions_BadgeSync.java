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
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                              		 */
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

public class FVT_IdeationBlogMentions_BadgeSync extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community and with the Ideation Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_IdeationBlog_MentionsEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a public community ideation blog</B></li>
	*<li><B>Step: User 1 add an idea mentioning User 2</B></li>
	*<li><B>Step: User 2 log in to Homepage</B></li>
	*<li><B>Step: User 2 opens their Notifications Center Flyout</B></li>
	*<li><B>Verify: Verify that The badge is removed from My Notifications in the left nav after a short transition, but is NOT removed from Mentions link in left nav</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CDAD648648A1447E85257E6F0051D58A">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00101 - OPENING NOTIFICATIONS CENTER FLYOUT DOES NOT CLEAR MENTIONS BADGE</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_IdeaMentions_BadgeSync() {

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
				
		// User 1 will now post an idea with mentions to User 2 in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityBlogEvents.createIdeationBlogIdeaWithMentions(testUser1, communityBlogsAPIUser1, mentions, baseIdea, publicCommunity);
		
		// Refresh the I'm Following view - this will update the My Notifications and Mentions badge values to 1
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the My Notifications and Notifications Center badge values have been updated to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		// Verify that the Mentions badge value has been updated to 1
		HomepageValid.verifyMentionsBadgeValue(driver, 1);
				
		// User 2 will now open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
				
		/**
		 * When the MN and NC badges are reset in the UI - the values can take a few seconds to update dynamically behind the scenes.
		 * Due to this, we need to ask Selenium to wait around until the values have been updated dynamically to their expected values.
		 * 
		 * Therefore Selenium will just click at position (0, 0) in the UI up to a maximum of 3 times.
		 * This will give the UI the time it needs to update the badge values dynamically (ie. we cannot change views).
		 * Usually the values are updated successfully after 2 iterations of the below loop.
		 */
		int numberOfTries = 0;
		while(numberOfTries < 3 && UIEvents.getMyNotificationsBadgeValue(driver) > 0) {
			// Reset Selenium back to the top of the AS
			UIEvents.resetASToTop(ui);
			numberOfTries ++;
		}
		// Verify that the My Notifications and Notifications Center badge values have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// Verify that the Mentions badge value still has a value of 1
		HomepageValid.verifyMentionsBadgeValue(driver, 1);
				
		ui.endTest();
	}
}