package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 04/2015
 */

public class FVT_ImFollowing_Tags_Standalone_Blogs extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };
	
	private APIBlogsHandler blogsAPIUser2;
	private BaseBlog baseBlog, baseBlogForEditWithTag, baseBlogWithTag;
	private Blog blog, blogForEditWithTag, blogWithTag;
	private String tagToFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
			
		blogsAPIUser2 = initialiseAPIBlogsHandlerUser(testUser2);

		// User 1 will now log in and follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);

		// User 2 will now create a standalone blog which includes the tag User 2 is following
		baseBlogWithTag = BlogBaseBuilder.buildBaseBlogWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), tagToFollow);
		blogWithTag = BlogEvents.createBlog(testUser2, blogsAPIUser2, baseBlogWithTag);
		
		// User 2 will now create a second standalone blog which includes the tag User 2 is following
		baseBlogForEditWithTag = BlogBaseBuilder.buildBaseBlogWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), tagToFollow);
		blogForEditWithTag = BlogEvents.createBlog(testUser2, blogsAPIUser2, baseBlogForEditWithTag);
		
		// User 2 will now create a standalone blog
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		blog = BlogEvents.createBlog(testUser2, blogsAPIUser2, baseBlog);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the blogs created during the test
		blogsAPIUser2.deleteBlog(blogWithTag);
		blogsAPIUser2.deleteBlog(blogForEditWithTag);
		blogsAPIUser2.deleteBlog(blog);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_Create_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 create a new blog and add the tag User 1 is following to the blog</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All,  Blog & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the blog.created story is displayed in Homepage / All Updates filtered by Tags and Blog</B></li>
	*<li><a HREF=" Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/279017D63BB0E766852578FC004DD59B">TTT -AS - FOLLOW - TAG - BLOGS - 00051 - blog.created - STANDALONE BLOG</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem"})
	public void test_Tags_CreateBlog_Standalone(){
		
		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createBlogEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlogWithTag.getName(), testUser2.getDisplayName());
		
		// Verify that the create blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_BlogEntry_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a blog that you are the owner of</B></li>
	*<li><B>Step: testUser 2 add an entry to the blog with the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the blog.entry.created story is displayed in Homepage / All Updates filtered by Tags and Blogs</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/69DD59FEC30312C1852578FC004E1EFF">TTT - AS - FOLLOW - TAG - BLOGS - 00061 - blog.entry.created - STANDALONE BLOG</a></li>
	*</ul>
	*/		
	@Test(groups = {"fvtonprem"})
	public void test_Tags_CreateEntry_Standalone(){

		String testName = ui.startTest();
		
		// User 2 will now add an entry to the standalone blog which includes the tag followed by User 1
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		BlogEvents.createBlogPost(testUser2, blogsAPIUser2, baseBlogPost, blog);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser2.getDisplayName());
		
		// Verify that the create entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateBlogEntry_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a blog that you are the owner of</B></li>
	*<li><B>Step: testUser 2 update an entry to the blog with the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Blog & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the blog.entry.updated story is displayed in Homepage / All Updates filtered by Tags and Blogs</B></li>
	*<li><a HREF=" Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F767529898558E2C852578FC004E25EE">TTT -AS - FOLLOW - TAG - BLOGS - 00071 - blog.entry.updated - STANDALONE BLOG</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_Tags_EditBlogEntry_Standalone(){

		String testName = ui.startTest();
		
		// User 2 will now add an entry to the standalone blog which includes the tag followed by User 1 and will then update the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPostWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		BlogEvents.createBlogPostAndEditDescription(testUser2, blogsAPIUser2, baseBlogPost, blog, editedDescription);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateEntryEvent = BlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the update entry event and edited description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, filter, true);
			
			// Verify that the original description is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogPost.getContent()}, null, false);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_BlogSettingUpdate_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a blog that you are the owner of</B></li>
	*<li><B>Step: testUser 2 change the blog settings (Name) with the tag User 1 is following <tag>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All Blogs & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the blog.settings.updated story is displayed in Homepage / All Updates filtered by Tags and Blogs</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FA9D0D8B6071AB9F852579BB0071A75D">TTT - AS - FOLLOW - TAG - BLOGS - 00066 - blog.settings.updated - STANDALONE BLOG</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem"})
	public void test_Tags_BlogSettingUpdate_Standalone(){
		
		ui.startTest();
		
		// Log in as User 2 and update the standalone blog name to match the content of the tag that User 1 is following
		BlogEvents.loginAndEditBlogName(baseBlogForEditWithTag, ui, driver, testUser2, tagToFollow, false);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		
		// Create the news story to be verified
		String editBlogEvent = BlogNewsStories.getUpdateBlogNewsStory(ui, tagToFollow, testUser2.getDisplayName());
		
		// Verify that the edit blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
}