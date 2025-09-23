package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.blogs.comments;

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
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_PrivateCommunityBlog_Comments extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2;
	private BaseBlogComment baseBlogCommentUser1, baseBlogCommentUser2;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogComment blogCommentUser1, blogCommentUser2;
	private BlogPost blogPost;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);

		// User 1 create a restricted community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.BLOG, testUser2, isOnPremise, testUser1, communitiesAPIUser1);	
		
		// Set all other relevant global test components to null
		blogPost = null;
		blogCommentUser1 = null;
		blogCommentUser2 = null;
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityBlogComment()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> testUser1 log in to Communities</li> 
	 * <li><B>Step:</B> testUser1 open a private community that has an existing blog entry</li> 
	 * <li><B>Step:</B> testUser1 create a comment on a blog entry</li> 
	 * <li><B>Step:</B> testUser2 log in to Home</li> 
	 * <li><B>Step:</B> testUser2 go to Home \ Activity Stream \ Discover \ All, Blogs & Communities</li> 
	 * <li><B>Verify:</B> Verify that the news story for blog.comment.created is NOT seen</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3807B9F04E52623852578760079E752">TTT - DISC - BLOGS - 00040 - BLOG.COMMENT.CREATED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * </ul>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void PrivateCommunityBlogComment() {

		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createUser1BlogCommentIfRequired();

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);

		// Create the news story to be verified
		String commentsOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentsOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent()}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PrivateCommunityBlogCommentRecommend()</li>
	 * <li><B>Purpose:</B>Verify the story shows in the correct Discover views</li>
	 * <li><B>Step:</B> Log in to Communities</li> 
	 * <li><B>Step:</B> Open a private community that has a blog with an existing blog entry and comment</li> 
	 * <li><B>Step:</B> Recommend this comment</li> 
	 * <li><B>Step:</B> Log in to Home as a different user</li> 
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><b>Step:</b> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.comment.recommended is not seen - negative test</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DCA312F4911B7FD1852578760079E75E">TTT - DISC - BLOGS - 00070 - BLOG.COMMENT.RECOMMENDED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void PrivateCommunityBlogCommentRecommend() {

		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createUser1BlogCommentIfRequired();
		
		// User 1 will now like / recommend the comment on the blog entry
		CommunityBlogEvents.likeComment(blogCommentUser1, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), null, testUser1.getDisplayName());
		
		// Verify that the like comment event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_editCommentToEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with private access</B></li>
	*<li><B>Step: testUser 1 add testUser 2 as a member of the community</B></li>
	*<li><B>Step: testUser 1 add the Blogs widget and create a new blog and blog entry</B></li>
	*<li><B>Step: testUser 1 add a comment to the blog</B></li>	
	*<li><B>Step: testUser 2 login to Homepage</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \Discover Filter by Blogs</B></li>
	*<li><B>Step: testUser 2 click on entry link to open the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 add a comment to the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: testUser 2 filter by Blogs - validation point #1</B></li>	
	*<li><B>Step: testUser 2 now edits their comment on the blog entry - validation point #2</B></li>
	*<li><B>Step: testUser 1 log in to Blogs again</B></li>
	*<li><B>Step: testUser 1 edits the blog comment that they created</B></li>
	*<li><B>Step: testUser 2 logs in to Home and goes to  \ Activity Stream \ Discover - validation point #3</B></li>
	*<li><B>Verify: #1 - testUser 2 does NOT see two comments on the blog entry, one created by themselves, the other created by User 1</B></li>
	*<li><B>Verify: #2 - testUser 2 does NOT see 2 comments on the blog entry, one edited by themselves and one unedited by user 1</B></li>
	*<li><B>Verify: #3 - testUser 2 does NOT see two edited comments on the blog entry</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/D768D97D01AC74E685257B50003AF727">TTT - DISC - BLOGS - 00113 - BLOG.COMMENT.EDITED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	*</ul>
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_editCommentToEntry_PrivateCommunity() {
		
		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createUser1BlogCommentIfRequired();
		
		// User 2 will now post a comment to the blog entry
		createUser2BlogCommentIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String twoCommentsOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheEntryNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the two comments on entry event and both User 1 and User 2's comments are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent()}, TEST_FILTERS, false);

		// User 2 edits their comment
		BaseBlogComment editedCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.editComment(blogCommentUser2, editedCommentUser2, testUser2, communityBlogsAPIUser2);
		
		// Verify that the two comments on entry event, User 1's comment and User 2's original and updated comments are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent(), editedCommentUser2.getContent()}, TEST_FILTERS, false);
		
		// User 1 edits their comment
		BaseBlogComment editedCommentUser1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.editComment(blogCommentUser1, editedCommentUser1, testUser1, communityBlogsAPIUser1);
		
		// Verify that the two comments on entry event, User 1's original and updated comments and User 2's updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnEntryEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), editedCommentUser1.getContent(), editedCommentUser2.getContent()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_deleteCommentToEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with private access</B></li>
	*<li><B>Step: testUser 1 add testUser 2 as a member of the community</B></li>
	*<li><B>Step: testUser 1 add the Blogs widget and create a new blog and blog entry</B></li>
	*<li><B>Step: testUser 1 add a comment to the blog</B></li>	
	*<li><B>Step: testUser 2 login to Homepage</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \Discover Filter by Blogs</B></li>
	*<li><B>Step: testUser 2 click on entry link to open the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 add a comment to the blog entry created by testUser 1</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: testUser 2 filter by Blogs - validation point #1</B></li>	
	*<li><B>Step: testUser 2 now deletes their comment on the blog entry - validation point #2</B></li>
	*<li><B>Step: testUser 1 log in to Blogs again</B></li>
	*<li><B>Step: testUser 1 deletes the blog comment that they created</B></li>
	*<li><B>Step: testUser 2 logs in to Home and goes to  \ Activity Stream \ Discover - validation point #3</B></li>
	*<li><B>Verify: #1 - testUser 2 does NOT see two comments on the blog entry, one created by themselves, the other created by User 1</B></li>
	*<li><B>Verify: #2 - testUser 2 does NOT see one comment on the blog entry, the comment author is testUser 1</B></li>
	*<li><B>Verify: #3 - testUser 2 does NOT see no comments on the blog entry</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/DAB9F9D3CF66961C85257B50003818E7">TTT - DISC - BLOGS - 00103 - BLOG.COMMENT.DELETED - PRIVATE COMMUNITY BLOG (NEG)</a></li>
	*</ul>
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void test_deleteCommentToEntry_PrivateCommunity() {
		
		ui.startTest();

		// User 1 will now create an entry in the blog
		createBlogPostIfRequired();
		
		// User 1 will now post a comment to the blog entry
		createUser1BlogCommentIfRequired();
		
		// User 2 will now post a comment to the blog entry
		createUser2BlogCommentIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String twoCommentsOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheEntryNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on activity entry event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnEntryEvent, baseBlogPost.getContent(), blogCommentUser2.getContent(), blogCommentUser1.getContent()}, TEST_FILTERS, false);

		// User 2 deletes their comment
		CommunityBlogEvents.deleteComment(blogCommentUser2, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		String oneCommentOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the one comment event, two comments event, User 1's comment and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{oneCommentOnEntryEvent, twoCommentsOnEntryEvent, baseBlogPost.getContent(), blogCommentUser2.getContent(), blogCommentUser1.getContent()}, TEST_FILTERS, false);
		
		// User 1 deletes their comment
		CommunityBlogEvents.deleteComment(blogCommentUser1, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create entry event, one comment event, two comments event, User 1's comment and User 2's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, oneCommentOnEntryEvent, twoCommentsOnEntryEvent, blogCommentUser2.getContent(), blogCommentUser1.getContent()}, TEST_FILTERS, false);		
		
		ui.endTest();
	}

	private void createBlogPostIfRequired() {
		if(blogPost == null) {
			// User 1 will now create an entry in the blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		}
	}
	
	private void createUser1BlogCommentIfRequired() {
		if(blogCommentUser1 == null) {
			// User 1 will now post a comment to the blog entry
			baseBlogCommentUser1 = BlogBaseBuilder.buildBaseBlogComment();
			blogCommentUser1 = CommunityBlogEvents.createComment(blogPost, baseBlogCommentUser1, testUser1, communityBlogsAPIUser1);
		}
	}
	
	private void createUser2BlogCommentIfRequired() {
		if(blogCommentUser2 == null) {
			// User 2 will now post a comment to the blog entry
			baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
			blogCommentUser2 = CommunityBlogEvents.createComment(blogPost, baseBlogCommentUser2, testUser2, communityBlogsAPIUser2);
		}
	}
}