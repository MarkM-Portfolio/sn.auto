package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
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
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_IdeationBlogEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2;
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
		
		// User 1 create a public community and add the Ideation Blogs widget
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a moderated community and add the Ideation Blogs widget
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community with User 2 added as a member and with the Ideation Blogs widget added
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.IDEATION_BLOG, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_Idea_FinalTwoComments_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an Ideation Blog</B></li>
	*<li><B>Step: User 1 add an idea</B></li>
	*<li><B>Step: User 2 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add 3 more comments</B></li>
	*<li><B>Step: User 2 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the comment is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 comments are now shown inline in the view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87BF60024C06CAE185257E2F0036A463">TTT - INLINE COMMENTS - 00080 - IDEATION BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Idea_FinalTwoComments_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(publicCommunity, baseIdea, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now post a comment to the idea
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(idea, user2Comment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseIdea.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on idea event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseIdea.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// User 2 will now post 3 more comments to the idea
		BaseBlogComment[] user2Comments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		for(int index = 0; index < user2Comments.length; index ++) {
			CommunityBlogEvents.createComment(idea, user2Comments[index], testUser2, communityBlogsAPIUser2);
		}
		// User 2 will now vote for the idea
		CommunityBlogEvents.likeOrVote(idea, testUser2, communityBlogsAPIUser2);
		
		// Create the additional news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_User(ui, baseIdea.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event, vote for idea event and User 2's last 2 comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, voteForIdeaEvent, baseIdea.getContent(), user2Comments[1].getContent(), user2Comments[2].getContent()}, filter, true);
			
			// Verify that User 2's first 2 comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment.getContent(), user2Comments[0].getContent()}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_Idea_FinalTwoComments_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an Ideation Blog</B></li>
	*<li><B>Step: User 1 add an idea</B></li>
	*<li><B>Step: User 2 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add 3 more comments</B></li>
	*<li><B>Step: User 2 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the comment is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 comments are now shown inline in the view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87BF60024C06CAE185257E2F0036A463">TTT - INLINE COMMENTS - 00080 - IDEATION BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Idea_FinalTwoComments_ModCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(moderatedCommunity, baseIdea, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now post a comment to the idea
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(idea, user2Comment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseIdea.getTitle(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on idea event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseIdea.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// User 2 will now post 3 more comments to the idea
		BaseBlogComment[] user2Comments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		for(int index = 0; index < user2Comments.length; index ++) {
			CommunityBlogEvents.createComment(idea, user2Comments[index], testUser2, communityBlogsAPIUser2);
		}
		// User 2 will now vote for the idea
		CommunityBlogEvents.likeOrVote(idea, testUser2, communityBlogsAPIUser2);
		
		// Create the additional news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_User(ui, baseIdea.getTitle(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event, vote for idea event and User 2's last 2 comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, voteForIdeaEvent, baseIdea.getContent(), user2Comments[1].getContent(), user2Comments[2].getContent()}, filter, true);
			
			// Verify that User 2's first 2 comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment.getContent(), user2Comments[0].getContent()}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_Idea_FinalTwoComments_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an Ideation Blog</B></li>
	*<li><B>Step: User 1 add an idea</B></li>
	*<li><B>Step: User 2 comment on the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add 3 more comments</B></li>
	*<li><B>Step: User 2 vote for the idea</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the comment is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 comments are now shown inline in the view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87BF60024C06CAE185257E2F0036A463">TTT - INLINE COMMENTS - 00080 - IDEATION BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_Idea_FinalTwoComments_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseIdea = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = CommunityBlogEvents.createIdea(restrictedCommunity, baseIdea, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now post a comment to the idea
		BaseBlogComment user2Comment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(idea, user2Comment, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseIdea.getTitle(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on idea event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseIdea.getContent(), user2Comment.getContent()}, TEST_FILTERS, true);
		
		// User 2 will now post 3 more comments to the idea
		BaseBlogComment[] user2Comments = { BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment(), BlogBaseBuilder.buildBaseBlogComment() };
		for(int index = 0; index < user2Comments.length; index ++) {
			CommunityBlogEvents.createComment(idea, user2Comments[index], testUser2, communityBlogsAPIUser2);
		}
		// User 2 will now vote for the idea
		CommunityBlogEvents.likeOrVote(idea, testUser2, communityBlogsAPIUser2);
		
		// Create the additional news story to be verified
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_User(ui, baseIdea.getTitle(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event, vote for idea event and User 2's last 2 comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, voteForIdeaEvent, baseIdea.getContent(), user2Comments[1].getContent(), user2Comments[2].getContent()}, filter, true);
			
			// Verify that User 2's first 2 comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment.getContent(), user2Comments[0].getContent()}, null, false);
		}
		ui.endTest();
	}
}