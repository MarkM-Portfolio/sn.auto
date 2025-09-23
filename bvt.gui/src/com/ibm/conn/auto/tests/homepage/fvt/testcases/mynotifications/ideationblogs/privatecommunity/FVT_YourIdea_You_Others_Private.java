package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.ideationblogs.privatecommunity;

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
/* Copyright IBM Corp. 2015, 2016, 2017		                         */
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

public class FVT_YourIdea_You_Others_Private extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);

		// User 1 create a restricted community, add User 2 and User 3 as members and add the Ideation Blog widget
		User[] membersList = { testUser2, testUser3 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1, membersList);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* test_YourIdea_CommentUpdate_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 create a Private Community with User 2 and User 3 as members and adding the Ideation Blogs widget</B></li>
	*<li><B>Step: User 1 create an idea in the Blog</B></li>
	*<li><B>Step: User 1 comment on the idea and edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community and go to the Ideation Blog User 1 owns</B></li>
	*<li><B>Step: User 2 comment on the idea and edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 comment on the idea and edit the comments</B></li>
	*<li><B>Step: User 1 comment on the idea and edit the comment</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Blogs, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><B>Verify: Verify the event shows "You and 2 others commented on your {ideaName} idea in the {ideaBlogName} Ideation Blog."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D22C66FF9BAB693785257DE8004A1C83">TTT - MY NOTIFICATIONS - IDEATION BLOGS - 00061 - UPDATED COMMENT ON YOUR OWN IDEA ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourIdea_CommentUpdate_You_Others_Rollup(){

		String testName = ui.startTest();

		// User 1 will now post an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will add a comment to the idea and edit the comment
		BaseBlogComment baseBlogComment1User1 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogComment1User1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(blogPost, baseBlogComment1User1, updatedBaseBlogComment1User1, testUser1, communityBlogsAPIUser1);

		// User 1 log in and go to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories to be verified
		String commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());
		String updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());

		// Verify the comment event, update comment event, the original comment and the updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, baseBlogPost.getContent(), baseBlogComment1User1.getContent(), updatedBaseBlogComment1User1.getContent()}, TEST_FILTERS, false);

		// User 2 add a comment and edit the comment
		BaseBlogComment baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(blogPost, baseBlogCommentUser2, updatedBaseBlogCommentUser2, testUser2, communityBlogsAPIUser2);
		
		// Create the news stories to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event, User 2's updated comment and User 1's updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), updatedBaseBlogComment1User1.getContent(), updatedBaseBlogCommentUser2.getContent()}, filter, true);
			
			// Verify that the update comment event and both User 2's and User 1's original comments are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogComment1User1.getContent(), baseBlogCommentUser2.getContent()}, null, false);
		}
		// User 3 add a comment and edit the comment
		BaseBlogComment baseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(blogPost, baseBlogCommentUser3, updatedBaseBlogCommentUser3, testUser3, communityBlogsAPIUser3);

		// User 1 add another comment and edit the comment
		BaseBlogComment baseBlogComment2User1 = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogComment updatedBaseBlogComment2User1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndEditComment(blogPost, baseBlogComment2User1, updatedBaseBlogComment2User1, testUser1, communityBlogsAPIUser1);

		// Create the news stories to be verified
		commentEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_YouAndMany(ui, "2", baseBlogPost.getTitle(), baseCommunity.getName());
		updateCommentEvent = CommunityBlogNewsStories.getUpdateCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event, User 3's updated comment and User 1's updated second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseBlogPost.getContent(), updatedBaseBlogCommentUser3.getContent(), updatedBaseBlogComment2User1.getContent()}, filter, true);
			
			// Verify that the update comment event, User 3's original comment, User 1's original second comment, User 2's updated comment and User 1's updated first comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogCommentUser3.getContent(), baseBlogComment2User1.getContent(), updatedBaseBlogComment1User1.getContent(), updatedBaseBlogCommentUser2.getContent()}, null, false);
		}
		ui.endTest();	
	}
}