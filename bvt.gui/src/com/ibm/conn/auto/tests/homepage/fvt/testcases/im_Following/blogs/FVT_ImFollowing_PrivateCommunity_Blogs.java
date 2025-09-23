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

public class FVT_ImFollowing_PrivateCommunity_Blogs extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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

		// User 1 create a restricted restrictedCommunity, add User 2 as a member, User 2 follows it and User 1 adds the blogs widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1, BaseWidget.BLOG, isOnPremise);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(restrictedCommunity, testUser1, communitiesAPIUser1);
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_AddBlogWidget_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/17865D3C3CD2482E852578760079E8CD">TTT - NF - FOLLOW - BLOGS - 00012 - BLOG.CREATED - PRIVATE COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_AddBlogWidget_PrivateCommunity() {

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
	*<li><B>Name:</B> test_AddBlogEntry_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.entry.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B50435FA1DD5489E852578760079E8D1">TTT - NF - FOLLOW - BLOGS - 00022 - BLOG.ENTRY.CREATED - PRIVATE COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddBlogEntry_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
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
	*<li><B>Name:</B> test_AddComment_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D79038F2B8357B08852578760079E8DA">TTT - NF - FOLLOW - BLOGS - 00042 - BLOG.COMMENT.CREATED - PRIVATE COMMUNITY BLOG</a></li>
	* @author Hugh Caren 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddComment_PrivateCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry to the community blog and will post a comment to the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createBlogPostAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
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
	*<li><B>Name:</B> test_RecommendBlogEntry_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and recommend (like) it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.entry.recommended story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AC48F58C0F2DBC79852578760079E8E2">TTT -  NF - FOLLOW - BLOGS - 00062 - BLOG.ENTRY.RECOMMENDED - PRIVATE COMMUNITY BLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_RecommendBlogEntry_PrivateCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog and will like / recommend the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndLikeBlogPost(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
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
	*<li><B>Name:</B> test_RecommendBlogComment_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry, add a comment to it and recommend (like) the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.comment.recommended story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE63F7F5C626913C852578760079E8E6">TTT - NF - FOLLOW - BLOGS - 00072 - BLOG.COMMENT.RECOMMENDED - PRIVATE COMMUNITY BLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_RecommendBlogComment_PrivateCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
		// User 1 will now post a comment to the entry and will like / recommend the comment
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndLikeComment(blogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
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
	*<li><B>Name:</B> test_DeleteBlogComment_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 1</B></li>
	*<li><B>Step: testUser 1 delete the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 2</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is displayed in all filters</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/94B57069DAAF7C7185257B4E0055176C">TTT - NF - FOLLOW - BLOGS - 00092 - BLOG.COMMENT.DELETED - PRIVATE COMMUNITY BLOG</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_DeleteBlogComment_PrivateCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog and will post a comment to the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment = CommunityBlogEvents.createBlogPostAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
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
	*<li><B>Name:</B> test_AddBlogTrackback_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with private access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with private access and add the blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new blog entry and add a trackback to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the blog.trackback.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6671283952FBAE74852578760079E8DE">TTT - NF - FOLLOW - BLOGS - 00052 - BLOG.TRACKBACK.CREATED - PRIVATE COMMUNITY BLOG</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddBlogTrackback_PrivateCommunity() {
		
		String testName = ui.startTest();

		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
		// User 1 will now log in and post a trackback comment on the community blog entry
		String trackBackComment = Data.getData().StatusComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddCommunityTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, restrictedCommunity, uiCo, baseBlogPost, trackBackComment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);

		// Outline the event to be verified
		String trackBackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the trackback event and trackback comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trackBackEvent, baseBlogPost.getContent(), trackBackComment}, TEST_FILTERS, true);
		
		ui.endTest();	
	}
}