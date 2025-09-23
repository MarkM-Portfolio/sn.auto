package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
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
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_CommunityBlogEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
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
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		
		// User 1 will now create a moderated community with the blogs widget added to it
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community with the blogs widget added to it
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community with User 2 added as a member and with the blogs widget added to it
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.BLOG, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* test_Entry_FinalTwoComments_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a Blog</B></li>
	*<li><B>Step: User 1 add an entry</B></li>
	*<li><B>Step: User 2 add 3 comments on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify the second last comment only is shown inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7866BC26D04FC5885257E2F0036A45C">TTT - INLINE COMMENTS - 00020 - BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Entry_FinalTwoComments_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 post a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		// User 2 will now post their first comment to the entry
		BaseBlogComment user2Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment1, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now post their second comment to the entry
		BaseBlogComment user2Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment2, testUser2, communityBlogsAPIUser2);
				
		// User 2 will now post their third comment to the entry
		BaseBlogComment user2Comment3 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment user2BlogComment3 = CommunityBlogEvents.createComment(communityBlogPost, user2Comment3, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second and third comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent(), user2Comment3.getContent()}, filter, true);
			
			// Verify that the first comment posted by User 2 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent()}, null, false);
		}
		
		// User 2 will now delete their third comment from the community entry
		CommunityBlogEvents.deleteComment(user2BlogComment3, testUser2, communityBlogsAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent()}, filter, true);
			
			// Verify that the first and third comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent(), user2Comment3.getContent()}, null, false);
		}		
		ui.endTest();
	}

	/**
	* test_Entry_FinalTwoComments_ModeratedCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a Blog</B></li>
	*<li><B>Step: User 1 add an entry</B></li>
	*<li><B>Step: User 2 add 3 comments on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify the second last comment only is shown inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7866BC26D04FC5885257E2F0036A45C">TTT - INLINE COMMENTS - 00020 - BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Entry_FinalTwoComments_ModeratedCommunity(){

		String testName = ui.startTest();
		
		// User 1 post a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 2 will now post their first comment to the entry
		BaseBlogComment user2Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment1, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now post their second comment to the entry
		BaseBlogComment user2Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment2, testUser2, communityBlogsAPIUser2);
				
		// User 2 will now post their third comment to the entry
		BaseBlogComment user2Comment3 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment user2BlogComment3 = CommunityBlogEvents.createComment(communityBlogPost, user2Comment3, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second and third comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent(), user2Comment3.getContent()}, filter, true);
			
			// Verify that the first comment posted by User 2 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent()}, null, false);
		}
		
		// User 2 will now delete their third comment from the community entry
		CommunityBlogEvents.deleteComment(user2BlogComment3, testUser2, communityBlogsAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent()}, filter, true);
			
			// Verify that the first and third comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent(), user2Comment3.getContent()}, null, false);
		}		
		ui.endTest();
	}

	/**
	* test_Entry_FinalTwoComments_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a Blog</B></li>
	*<li><B>Step: User 1 add an entry</B></li>
	*<li><B>Step: User 2 add 3 comments on the entry</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify the second last comment only is shown inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7866BC26D04FC5885257E2F0036A45C">TTT - INLINE COMMENTS - 00020 - BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_Entry_FinalTwoComments_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 post a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
		// User 2 will now post their first comment to the entry
		BaseBlogComment user2Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment1, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now post their second comment to the entry
		BaseBlogComment user2Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityBlogPost, user2Comment2, testUser2, communityBlogsAPIUser2);
				
		// User 2 will now post their third comment to the entry
		BaseBlogComment user2Comment3 = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment user2BlogComment3 = CommunityBlogEvents.createComment(communityBlogPost, user2Comment3, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseBlogPost.getTitle(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second and third comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent(), user2Comment3.getContent()}, filter, true);
			
			// Verify that the first comment posted by User 2 is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent()}, null, false);
		}
		
		// User 2 will now delete their third comment from the community entry
		CommunityBlogEvents.deleteComment(user2BlogComment3, testUser2, communityBlogsAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry event and User 2's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), user2Comment2.getContent()}, filter, true);
			
			// Verify that the first and third comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent(), user2Comment3.getContent()}, null, false);
		}		
		ui.endTest();
	}
}