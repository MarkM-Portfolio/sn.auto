package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.blogs;

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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;

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
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / people) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 07/2015
 */

public class FVT_ImFollowing_people_StandaloneBlogs extends SetUpMethodsFVT {

	private String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterPeople };
	
	private APIBlogsHandler blogsAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseBlog baseBlogAfterFollow, baseBlog;
	private Blog standaloneBlog, standaloneBlogAfterFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());	
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 will now create a standalone blog now before User 2 is following User 1
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlog);
				
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
				
		// User 1 will now create a standalone blog now that User 2 is following User 1
		baseBlogAfterFollow = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlogAfterFollow = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlogAfterFollow);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
		blogsAPIUser1.deleteBlog(standaloneBlogAfterFollow);

		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateBlog_Stanadalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Follow User 2</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 Create a Blog</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & People </B></li>
	*<li><B>Verify: Verify that the story blog.created is displayed in People and Blogs views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/274B15754C73FA1A852578FB003B51D9">TTT - AS - FOLLOW - PERSON - BLOGS - 00071 - blog.created - STANDALONE BLOG</a></li>
	*</ul>
	*/		
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void test_People_CreateBlog_Standalone(){
			
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlogAfterFollow.getName(), testUser1.getDisplayName());
		
		// Verify that the create blog event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	 }	
	
	/**
	*<ul>
	*<li><B>Name: test_People_BlogSettingUpdate_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow User 2</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>	
	*<li><B>Step: testUser 2 Update the settings of an existing blog (i.e. Name)</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the story blog.settings.updated is displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C182ED592285FF2A852579BF0044F72A">TTT - AS - FOLLOW - PERSON - BLOGS - 00075 - blog.settings.updated - STANDALONE BLOG (NEG SC NOV)</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem"}, priority = 3)
	public void test_People_BlogSettingUpdate_Standalone(){
		
		String testName = ui.startTest();
		
		// User 1 will now log in and update the blog name
		String editedBlogName = testName + Helper.genStrongRand();
		BlogEvents.loginAndEditBlogName(baseBlog, ui, driver, testUser1, editedBlogName, false);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
				
		// Create the news story to be verified
		String updateBlogEvent = BlogNewsStories.getUpdateBlogNewsStory(ui, editedBlogName, testUser1.getDisplayName());
		
		// Verify that the update blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_BlogEntry_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 Log in to Blogs</B></li>
	*<li><B>Step: testUser 2 Create a new blog </B></li>
	*<li><B>Step: testUser 1 FOLLOW User 2</B></li>
	*<li><B>Step: testUser 2 Create a new blog entry</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the story blog.entry.created is displayed in People and Blogs views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/699BF00A1716ED8F852578FB003C63A6">TTT - AS - FOLLOW - PERSON - BLOGS - 00081 - blog.entry.created - STANDALONE BLOG</a></li>
	*</ul>
	*/		
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_People_CreateEntry_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		
		// Verify that the create entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateBlogEntry_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 Log in to Blogs</B></li>
	*<li><B>Step: testUser 2 Create a new blog </B></li>
	*<li><B>Step: testUser 1 FOLLOW User 2</B></li>
	*<li><B>Step: testUser 2 Update an existing blog entry</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the blog.entry.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/26C99B3C44D9CEE3852578FB003C6847">TTT -AS - FOLLOW - PERSON - BLOGS - 00091 - blog.entry.updated - STANDALONE BLOG (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_People_UpdateBlogEntry_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog and will edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndEditDescription(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, editedDescription);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		String updateEntryEvent = BlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_People_CreateBlogComment_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 Log in to Blogs</B></li>
	*<li><B>Step: testUser 2 Create a new blog </B></li>
	*<li><B>Step: testUser 1 FOLLOW User 2</B></li>
	*<li><B>Step: testUser 2 Create a comment on an existing blog entry</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the blog.comment.created story is displayed within the People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12AA5CE59DF6A1F9852578FB003C6DEF">TTT - AS - FOLLOW - PERSON - BLOGS - 00101 - blog.comment.created - STANDALONE BLOG</a></li>
	*@author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_People_CreateBlogComment_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog and will post a comment to the entry
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndAddComment(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, baseBlogComment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_RecommendBlogEntry_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 Log in to Blogs</B></li>
	*<li><B>Step: testUser 2 Create a new blog </B></li>
	*<li><B>Step: testUser 1 FOLLOW User 2</B></li>
	*<li><B>Step: testUser 2 Recommend an existing blog entry</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the blog.entry.recommended is displayed in the People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/37773DD7C8141E1D852578FB004D4B15">TTT - AS - FOLLOW - PERSON - BLOGS - 00121 - blog.entry.recommended - STANDALONE BLOG</a></li>
	*@author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_People_RecommendBlogEntry_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog and will like / recommend the entry
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndLike(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeEntryEvent = BlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());	
		
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_RecommendBlogComment_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 Log in to Blogs</B></li>
	*<li><B>Step: testUser 2 Create a new blog </B></li>
	*<li><B>Step: testUser 1 FOLLOW User 2</B></li>
	*<li><B>Step: testUser 2 Recommend an existing comment in the blog entry</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & people</B></li>
	*<li><B>Verify: Verify that the blog.comment.recommended story is displayed within the People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/98BA9B53BB03C364852578FB004D508D">TTT - AS - FOLLOW - PERSON - BLOGS - 00131 - blog.comment.recommended - STANDALONE BLOG</a></li>
	*@author Srinivas Vechha
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void test_People_RecommendBlogComment_Standalone(){

		String testName = ui.startTest();
		
		// User 1 will now create an entry in the standalone blog, will post a comment to the entry and then like / recommend the comment
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndAddCommentAndLikeComment(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, baseBlogComment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeCommentEvent = BlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}