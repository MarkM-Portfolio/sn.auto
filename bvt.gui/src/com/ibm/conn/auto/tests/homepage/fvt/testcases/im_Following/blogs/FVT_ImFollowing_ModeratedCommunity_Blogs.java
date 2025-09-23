package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
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
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_ModeratedCommunity_Blogs extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private CommunitiesUI uiCo;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);

		// User 1 create a moderated community and User2 follows it
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, BaseWidget.BLOG, testUser1, communitiesAPIUser1, isOnPremise);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(moderatedCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	*<ul>
	*<li><B>Name: test_AddBlogWidget_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E26AD57FCAB43E84852578760079E8CC">TTT - NF - FOLLOW - BLOGS - 00011 - BLOG.CREATED - MODERATED COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_AddBlogWidget_ModeratedCommunity() {

		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_AddBlogEntry_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.entry.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/157B087461344B5F852578760079E8D0">TTT - NF - FOLLOW - BLOGS - 00021 - BLOG.ENTRY.CREATED - MODERATED COMMUNITY BLOG</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddBlogEntry_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Outline event to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_AddComment_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D03CC05797A8B254852578760079E8D9">TTT - NF - FOLLOW - BLOGS - 00041 - BLOG.COMMENT.CREATED - MODERATED COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddComment_ModeratedCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry to the community blog and will post a comment to the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createBlogPostAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String entryCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{entryCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_RecommendBlogEntry_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and recommend (like) it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.entry.recommended story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AC1FF23FCFF0DA66852578760079E8E1">TTT - NF - FOLLOW - BLOGS - 00061 - BLOG.ENTRY.RECOMMENDED - MODERATED COMMUNITY BLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_RecommendBlogEntry_ModeratedCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog and will like / recommend the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndLikeBlogPost(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline events to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeEntryEvent = CommunityBlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter: TEST_FILTERS) {
			// Verify that the create entry event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the like entry event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeEntryEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_RecommendBlogComment_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry, add a comment to it and recommend (like) the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.comment.recommended story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2CB3B2C84113E9BC852578760079E8E5">TTT - NF - FOLLOW - BLOGS - 00071 - BLOG.COMMENT.RECOMMENDED - MODERATED COMMUNITY BLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_RecommendBlogComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 1 will now post a comment to the entry and will like / recommend the comment
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndLikeComment(blogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline events to be verified
		String entryCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter: TEST_FILTERS) {
			// Verify that the comment on entry event and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{entryCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, filter, true);
			
			// Verify that the like comment event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeCommentEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_DeleteBlogComment_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 1</B></li>
	*<li><B>Step: testUser 1 delete the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 2</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is displayed in all filters</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/28303DEF31E770F085257B4E005595B4">TTT - NF - FOLLOW - BLOGS - 00091 - BLOG.COMMENT.DELETED - MODERATED COMMUNITY BLOG</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_DeleteBlogComment_ModeratedCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog and will post a comment to the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment = CommunityBlogEvents.createBlogPostAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline events to be verified
		String entryCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the comment on entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{entryCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		// User 1 delete the comment
		CommunityBlogEvents.deleteComment(blogComment, testUser1, communityBlogsAPIUser1);

		for(String filter: TEST_FILTERS) {
			// Verify that the create entry event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the comment on entry event and User 1's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{entryCommentEvent, baseBlogComment.getContent()}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_AddBlogTrackback_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a trackback to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.trackback.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/16DB0DBD0A4393F6852578760079E8DD">TTT - NF - FOLLOW - BLOGS - 00051 - BLOG.TRACKBACK.CREATED - MODERATED COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddBlogTrackback_ModeratedCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 1 will now log in and post a trackback comment on the community blog entry
		String trackBackComment = Data.getData().StatusComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddCommunityTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, moderatedCommunity, uiCo, baseBlogPost, trackBackComment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);

		// Outline the event to be verified
		String trackBackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the trackback event and trackback comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trackBackEvent, baseBlogPost.getContent(), trackBackComment}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
}