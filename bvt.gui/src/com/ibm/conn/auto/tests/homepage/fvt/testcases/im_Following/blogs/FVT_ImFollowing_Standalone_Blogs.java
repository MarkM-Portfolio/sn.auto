package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_ImFollowing_Standalone_Blogs extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };
	
	private APIBlogsHandler blogsAPIUser1, blogsAPIUser2;
	private BaseBlog baseBlog;
	private Blog standaloneBlog;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		blogsAPIUser2 = initialiseAPIBlogsHandlerUser(testUser2);

		// User 1 create a blog and User 2 will follow it
		baseBlog = BlogBaseBuilder.buildBaseBlog(Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlogWithFollower(testUser1, blogsAPIUser1, baseBlog, testUser2, blogsAPIUser2);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> StandAloneBlogTest()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.created story is displayed in all filters</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5CEEA5E78BC67619852578760079E8CF">TTT - NF - FOLLOW - BLOGS - 00014 - BLOG.CREATED - STANDALONE BLOG</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_CreateBlog_Standalone() {
		
		ui.startTest();
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String nonEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlog.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{nonEvent, baseBlog.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> StandAloneBlogEntry()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.entry.created story is displayed in all filters</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/910EB0ABEE1F16C8852578760079E8D3">TTT - NF - FOLLOW - BLOGS - 00024 - BLOG.ENTRY.CREATED - STANDALONE BLOG</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateEntry_Standalone() {

		String testName = ui.startTest();

		// User 1 will add a blog entry to the blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String newsStory = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> test_LikeBlogEntry_Standalone()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 1 recommend (like) the new blog entry</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.entry.recommended story is NOT displayed in any filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAEFA7D949866261852578760079E8E4">TTT - NF - FOLLOW - BLOGS - 00064 - BLOG.ENTRY.RECOMMENDED - STANDALONE BLOG (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeEntry_Standalone() {

		String testName = ui.startTest();

		// User 1 create a blog entry and like it
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndLike(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String nonEvent = BlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		String newsStory = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the blog entry creation news story is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the like blog event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> test_EditBlogEntry_Standalone()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 1 update the new blog entry</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.entry.updated story is NOT displayed in any filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/183A3F75F30E2F33852578760079E8D8">TTT - NF - FOLLOW - BLOGS - 00034 - BLOG.ENTRY.UPDATED - STANDALONE BLOG (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_EditBlogEntry_Standalone() {

		String testName = ui.startTest();

		// User 1 creates a blog entry and edits it
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		String edit = Helper.genStrongRand();
		BlogEvents.createBlogPostAndEditDescription(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, edit);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String newsStory = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		String nonEvent  = BlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the blog entry creation news story is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the update blog entry event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> test_CreateBlogComment_Standalone()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 1 add a comment to the new blog entry</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.comment.created story is displayed in all filters</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DD14FA5AF2F1CD8E852578760079E8DC">TTT - NF - FOLLOW - BLOGS - 00044 - BLOG.COMMENT.CREATED - STANDALONE BLOG</a></li>	
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_CreateBlogComment_Standalone() {

		String testName = ui.startTest();

		// User 1 create a blog entry and add a comment to it
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = new BaseBlogComment.Builder(Data.getData().StatusComment + Helper.genStrongRand()).build();
		BlogEvents.createBlogPostAndAddComment(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, baseBlogComment);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String newsStory = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name:</B> test_LikeComment_Standalone()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 1 add a comment to the new blog entry</B></li>
	 *<li><B>Step: testUser 1 recommend (like) the comment</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.comment.recommended story is NOT displayed in any filter</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CE9CCB4A477FB317852578760079E8E8">TTT - NF - FOLLOW - BLOGS - 00074 - BLOG.COMMENT.RECOMMENDED - STANDALONE BLOG (NEG SC NOV)</a></li>	
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_LikeComment_Standalone() {

		String testName = ui.startTest();

		// User 1 create a blog entry, add a comment and like the comment
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = new BaseBlogComment.Builder(Data.getData().StatusComment + Helper.genStrongRand()).build();
		BlogEvents.createBlogPostAndAddCommentAndLikeComment(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, baseBlogComment);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String newsStory = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		String nonEvent = BlogNewsStories.getLikeTheirOwnCommentOnEntryNewsStory(ui, baseBlogPost.getTitle(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the blog entry comment creation news story is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), baseBlogComment.getContent()}, filter, true);
			
			// Verify that the like comment event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent}, null, false);
		}
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Name:</B> test_CreateBlogTrackback_Standalone()</li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: testUser 1 log into Connections</B></li>
	 *<li><B>Step: testUser 1 create a new blog</B></li>
	 *<li><B>Step: testUser 2 follow this blog</B></li>
	 *<li><B>Step: testUser 1 create a new blog entry</B></li>
	 *<li><B>Step: testUser 1 create a trackback on the new blog entry</B></li>
	 *<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Blogs</B></li>
	 *<li><B>Verify: Verify that the blog.trackback.created story is displayed in all filters</B></li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2E22412CC2155ED2852578760079E8E0">TTT - NF - FOLLOW - BLOGS - 00054 - BLOG.TRACKBACK.CREATED - STANDALONE BLOG</a></li>	
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_Trackback_StandAlone() {

		String testName = ui.startTest();

		// User 1 create a blog entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
		
		// User 1 adds a trackback comment
		String trkbackComment = Data.getData().StatusComment + Helper.genStrongRand();
		BlogEvents.loginAndAddTrackbackComment(ui, driver, testUser1, baseBlog, baseBlogPost, trkbackComment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);

		// Outline event to be verified
		String newsStory = BlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBlogPost.getContent(), trkbackComment}, TEST_FILTERS, true);

		ui.endTest();	
	}	
}