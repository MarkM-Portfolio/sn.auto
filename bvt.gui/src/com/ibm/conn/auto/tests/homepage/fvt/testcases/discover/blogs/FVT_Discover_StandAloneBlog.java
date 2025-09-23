package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discover_StandAloneBlog extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };

	private APIBlogsHandler blogsAPIUser1;
	private BaseBlog baseBlog;
	private BaseBlogPost baseBlogPost;
	private Blog standaloneBlog;
	private BlogPost blogEntry;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);
		
		// User 1 will now create a standalone blog
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlog);
		
		// Set all other relevant global test components to null
		blogEntry = null;
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
	
		// Delete the standalone blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_CreateBlog_Standalone()</li>
	 * <li><B>Step:</B> Log in to Blogs</li>
	 * <li><B>Step:</B> Create a new blog</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/208D927D389402B6852578760079E742">TTT - DISC - BLOGS - 00010 - BLOG.CREATED - STANDALONE BLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem"}, priority = 1)
	public void test_CreateBlog_Standalone() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = BlogNewsStories.getCreateBlogNewsStory(ui, baseBlog.getName(), testUser1.getDisplayName());
		
		// Verify that the create blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_CreateEntry_Standalone()</li>
	 * <li><B>Step:</B> Log in to Blogs</li>
	 * <li><B>Step:</B> Open your blog</li>
	 * <li><B>Step:</B> Create a new blog entry</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0420679F17084854852578760079E746">TTT - DISC - BLOGS - 00020 - BLOG.ENTRY.CREATED - STANDALONE BLOG</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem"}, priority = 2)
	public void test_CreateEntry_Standalone() {

		ui.startTest();
		
		// User 1 will now add an entry to the standalone blog
		createEntryIfRequired();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Create the news story to be verified
		String createEntryEvent = BlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
				
		// Verify that the create entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_EditBlogEntry_Standalone()</li>
	 * <li><B>Step:</B> Log in to Blogs</li>
	 * <li><B>Step:</B> Open a standalone blog that has an existing blog entry</li>
	 * <li><B>Step:</B> Update an exiting blog entry</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.updated is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EBC067614FEC2E10852578760079E74A">TTT - DISC - BLOGS - 00030 - BLOG.ENTRY.UPDATED - STANDALONE BLOG</a></li>
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem"}, priority = 3)
	public void test_EditBlogEntry_Standalone() {

		ui.startTest();
		
		// User 1 will now add an entry to the standalone blog
		createEntryIfRequired();
				
		// User 1 will now edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		blogEntry = BlogEvents.editBlogPostDescription(testUser1, blogsAPIUser1, blogEntry, editedDescription);
						
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
								
		// Create the news story to be verified
		String updateEntryEvent = BlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
								
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_LikeEntry_Standalone()</li>
	 * <li><B>Step:</B> Log in to Blogs</li>
	 * <li><B>Step:</B> Open a standalone blog that has an existing blog entry</li>
	 * <li><B>Step:</B> Recommend this entry</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.entry.recommended is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/74E63E7067AA0F97852578760079E757">TTT - DISC - BLOGS - 00060 - BLOG.ENTRY.RECOMMENDED - STANDALONE BLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test (groups = {"fvtonprem"}, priority = 4)
	public void test_LikeEntry_Standalone() {
		
		ui.startTest();
		
		// User 1 will now add an entry to the standalone blog
		createEntryIfRequired();
				
		// User 1 will now like / recommend the entry
		BlogEvents.likeBlogPost(testUser1, blogsAPIUser1, blogEntry);
				
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeEntryEvent = BlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
						
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEntryEvent, blogEntry.getContent().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Trackback_StandAlone()</li>
	 * <li><B>Step:</B> Log in to Blogs</li>
	 * <li><B>Step:</B> Create a blog if you do not already have one</li>
	 * <li><B>Step:</B> Open a standalone blog that has an existing blog entry</li>
	 * <li><B>Step:</B> Create a comment on a blog entry, select the option "Add this as a new entry in my blog..." and save</li>
	 * <li><B>Step:</B> Log in to Home as a different user</li>
	 * <li><B>Step:</B> Go to Home \ Activity Stream \ Discover</li>
	 * <li><B>Step:</B> Filter by Blogs</li>
	 * <li><B>Verify:</B> Verify that the news story for blog.trackback.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B0036ADD25D621CD852578760079E753">TTT - DISC - BLOGS - 00050 - BLOG.TRACKBACK.CREATED - STANDALONE BLOG</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test (groups = {"fvtonprem"}, priority = 5)
	public void test_Trackback_StandAlone() {

		ui.startTest();
		
		// User 1 will now add an entry to the standalone blog
		createEntryIfRequired();
		
		// User 1 will now log in and add a trackback comment to the blog entry
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		BlogEvents.loginAndAddTrackbackComment(ui, driver, testUser1, baseBlog, baseBlogPost, trackbackComment, false);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
										
		// Create the news story to be verified
		String trackbackEvent = BlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
										
		// Verify that the add trackback event and trackback comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trackbackEvent, blogEntry.getContent().trim(), trackbackComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	private void createEntryIfRequired() {
		if(blogEntry == null) {
			// User 1 will now add a blog entry to the standalone blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogEntry = BlogEvents.createBlogPost(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog);
		}
	}
}