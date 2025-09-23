package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BlogsDataPop extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BlogsDataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;
	private BlogsUI ui;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private User testUser1, testUser2, testUser3, testUser4;
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		URLConstants.setServerURL(serverURL);
	}	
	
			
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Community Blog: Add a Blog Entry</li>
		*<li><B>Step:</B> Create a Public community using the API</li>
		*<li><B>Step:</B> Add the Blog widget</li>
		*<li><B>Step:</B> Create a Blog entry and Save</li>
		*</ul>
		*/ 
		@Test(groups = {"regression", "regressioncloud"})
		public void createCommunityBlogEntry(){
			
			String testName = ui.startTest();
						
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                          .access(Access.PUBLIC)
                                          .description("Create a Public community.  Add the Blogs widget & create an entry. ")
                                          .build();
						
			BaseBlogPost blogEntry = new BaseBlogPost.Builder("Blog entry " + Helper.genDateBasedRandVal())
															 .tags("blogTag" + Helper.genDateBasedRand())
															 .content("Test blog entry ")
															 .build();	

			log.info("INFO: Create community using API");
			Community comAPI1 = community.createAPI(apiOwner);
					
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI1);
			
			log.info("INFO: Add the Blogs widget using API");
			community.addWidgetAPI(comAPI1, apiOwner, BaseWidget.BLOG);
			
			log.info("INFO: Login to Communities as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(commUI);	

			log.info("INFO: Select Blogs from the tabbed nav menu");
			Community_TabbedNav_Menu.BLOG.select(commUI);
			
			log.info("INFO: Select New Entry button");
			ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Add a new entry to the blog");
			blogEntry.create(ui);

			log.info("INFO: Verify that the new blog entry exists");
			Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), 
					"ERROR: Entry not found");
			
			ui.endTest();
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Community Blog: add a blog entry</li>
		*<li><B>Step:</B> Create a Public community using the API</li>
		*<li><B>Step:</B> Add the Blogs widget</li>
		*<li><B>Step:</B> Create a blog entry and Save</li>
		*<li><B>Step:</B> Add a comment to the blog entry</li>
		*</ul>
		*/ 
		@Test(groups = {"regression", "regressioncloud"})
		public void addCommentToCommBlogEntry(){
			
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                       .access(Access.PUBLIC)
                                                       .description("Create a Public community.  Add Blogs widget, an entry & comment to entry. ")
                                                       .build();
						
			BaseBlogPost blogEntry = new BaseBlogPost.Builder("Blog entry " + Helper.genDateBasedRandVal())
															 .tags("blogTag" + Helper.genDateBasedRand())
															 .content("Test blog entry ")
															 .build();	
			
			BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();

			log.info("INFO: Create community using API");
			Community comAPI1 = community.createAPI(apiOwner);

			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI1);

			log.info("INFO: Add the Blogs widget using API");
			community.addWidgetAPI(comAPI1, apiOwner, BaseWidget.BLOG);

			log.info("INFO: Login to Communities as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1);

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(commUI);	

			log.info("INFO: Select Blogs from the tabbed nav menu");
			Community_TabbedNav_Menu.BLOG.select(commUI);

			log.info("INFO: Select New Entry button");
			ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

			log.info("INFO: Add a new entry to the blog");
			blogEntry.create(ui);

			log.info("INFO: Verify that the new blog entry exists");
			Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), 
					"ERROR: Entry not found");

			log.info("INFO: Add a new comment to the entry A");
			comment.create(ui);

			ui.endTest();
		}

		
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Standalone Blog: Create a Blog</li>
		*<li><B>Step:</B> Navigate to Blogs</li>
		*<li><B>Step:</B> Click on Start a Blog</li> 
		*<li><B>Step:</B> Create a Blog</li>
		*</ul>
		*Notes: This test is not supported on the cloud. No standalone Blog on cloud. 
		*/ 
		@Test(groups = {"regression"} )
		public void createStandaloneBlog(){
			
			String testName = ui.startTest();
						
			BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
										.tags("Tag for "+testName  + Helper.genDateBasedRandVal())
										.description("Test description for testcase " + testName)
										.timeZone(Time_Zone.Europe_London)
										.theme(Theme.Blog_with_Bookmarks)
										.build();
			
            log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			log.info("INFO: Create a new blog");
			blog.create(ui);
			
			log.info("INFO: Open the blog");
			ui.clickLinkWait("link=" + blog.getName());
						
			log.info("INFO: Verify that the blog opens and New Entry button displays");
			Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.blogsNewEntryMenuItem),
					          "ERROR: New Entry button not found in the opened blog");
			
			ui.endTest();
			
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Standalone Blogs: Create a Blog Entry</li>
		*<li><B>Step:</B> Create a Blog </li>
		*<li><B>Step:</B> Create a Blog entry</li>
		*</ul>
		*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
		*/
		@Test(groups = { "regression"})
		public void createStandaloneBlogEntry(){
			
			String testName = ui.startTest();
			
			BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
										.tags("Tag for "+testName  + Helper.genDateBasedRandVal())
										.description("Test description for testcase " + testName)
										.timeZone(Time_Zone.Europe_London)
										.theme(Theme.Blog_with_Bookmarks)
										.build();

			BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("BlogEntry1" + Helper.genDateBasedRand()).blogParent(blog)
													 .tags(testName + Helper.genDateBasedRand())
													 .content("Test description for testcase " + testName)
													 .build();
			
			log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			log.info("INFO: Create a new blog");
			blog.create(ui);

			log.info("INFO: Open blog");
			ui.clickLink("link=" + blog.getName());
			
			log.info("INFO: Select New Entry button");
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Add a new entry to the blog");
			ui.clickLink(BlogsUIConstants.BlogsNewEntry);
			blogEntry1.create(ui);
			
			ui.endTest();
			
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Standalone Blog: Add Comment to Blog Entry</li>
		*<li><B>Step:</B> Create a blog</li>
		*<li><B>Step:</B> Click New Entry button to create an entry</li>
		*<li><B>Step:</B> Add a comment to the entry & Save</li>
		*</ul>
		*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
		*/
		@Test(groups = { "regression"} , enabled=false )
		public void addCommentStandaloneBlogEntry(){
			
			String testName = ui.startTest();
			
			BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
										.tags("Tag for "+testName  + Helper.genDateBasedRandVal())
										.description("Test description for testcase " + testName)
										.timeZone(Time_Zone.Europe_London)
										.theme(Theme.Blog_with_Bookmarks)
										.build();

			BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("BlogEntry1" + Helper.genDateBasedRand()).blogParent(blog)
													 .tags(testName + Helper.genDateBasedRand())
													 .content("Test description for testcase " + testName)
													 .build();
			
			BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
			
			log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			log.info("INFO: Create a new blog");
			blog.create(ui);

			log.info("INFO: Open blog");
			ui.clickLink("link=" + blog.getName());
			
			log.info("INFO: Select New Entry button");
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Add a new entry to the blog");
			ui.clickLink(BlogsUIConstants.BlogsNewEntry);
			blogEntry1.create(ui);
			
			log.info("INFO: Add a new comment to the entry A");
			comment.create(ui);
			
			ui.endTest();
			
		}
	
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Community Blogs: Edit Blog Entry</li>
		*<li><B>Step:</B> Create a community</li>
		*<li><B>Step:</B> Add the Blog widget</li>
		*<li><B>Step:</B> Post a Blog entry</li>
		*<li><B>Step:</B> Edit the Blog name</li>
		*</ul>
		*/
		@Test(groups = {"regression", "regressioncloud"})
		public void editCommBlogsEntry(){
			
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                       .access(Access.PUBLIC)
                                                       .description("Create a Public community.  Add Blogs widget. Create & edit an entry. ")
                                                       .build();
						
			BaseBlogPost NewEntry1 = new BaseBlogPost.Builder("First Blog Entry " + Helper.genDateBasedRand())
					   								 .tags("Tag" + Helper.genDateBasedRand())
					   								 .content("Test blog content")
					   								 .build();
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, comAPI));
			
			log.info("INFO: Add the Blogs widget using the API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
			
			log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(commUI);	
			
			log.info("INFO: Navigate to the Blogs full widget page");
			Community_TabbedNav_Menu.BLOG.select(commUI);
		
			log.info("INFO: Select New Entry from Menu Item");
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Create a new entry1"); 
			NewEntry1.create(ui);
			
			log.info("INFO: Click on the Edit link");
			ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);
			
			log.info("INFO: Clear the title field");
			ui.clearText(BlogsUIConstants.BlogsNewEntryTitle);
			
			log.info("INFO: Enter the new title");
			ui.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().SearchScopeEntries);
			
			log.info("INFO: Save the entry");
			ui.clickLinkWait(BlogsUIConstants.blogPostEntryID);
			
			ui.endTest();		
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Standalone Blogs: Edit a Blog Entry</li>
		*<li><B>Step:</B> Create a Blog</li>
		*<li><B>Step:</B> Create a Blog entry</li>
		*<li><B>Step:</B> Edit the Blog entry</li>
		*</ul>
		*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
		*/
		@Test(groups = { "regression"})
		public void editStandaloneBlogEntry(){
			
			String testName = ui.startTest();
			
			BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
										.tags("Tag for " + testName  + Helper.genDateBasedRandVal())
										.description("Test description for testcase " + testName)
										.timeZone(Time_Zone.Europe_London)
										.theme(Theme.Blog_with_Bookmarks)
										.build();

			BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("BlogEntry1 " + Helper.genDateBasedRand()).blogParent(blog)
													 .tags(testName + Helper.genDateBasedRand())
													 .content("Test description for testcase " + testName)
													 .build();
			
			log.info("INFO: Log into Blogs as: " + testUser2.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser2);

			log.info("INFO: Create a new Blog");
			blog.create(ui);

			log.info("INFO: Open Blog");
			ui.clickLink("link=" + blog.getName());
			
			log.info("INFO: Select New Entry button");
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Add a new entry to the Blog");
			ui.clickLink(BlogsUIConstants.BlogsNewEntry);
			blogEntry1.create(ui);
			
			log.info("INFO: Click on the Edit link");
			ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);
						
			log.info("INFO: Clear the title field");
			ui.clearText(BlogsUIConstants.BlogsNewEntryTitle);
			
			log.info("INFO: Enter the new title");
			ui.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().SearchScopeEntries);
			
			log.info("INFO: Save the entry");
			ui.clickLinkWait(BlogsUIConstants.blogPostEntryID);
						
			ui.endTest();
			
		}	
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population - Standalone Blogs: Multiple Users Follow Blog</li>
		*<li><B>Step:</B> UserA creates a Blog</li>
		*<li><B>Step:</B> UserA follows the Blog</li>
		*<li><B>Step:</B> Logout as UserA, login as UserB</li>
		*<li><B>Step:</B> UserB follows the Blog</li>
		*<li><B>Step:</B> Logout as UserB, login as UserC</li>
		*<li><B>Step:</B> UserC follows the Blog</li>
		*<li><B>Step:</B> Logout as UserC, login as UserD</li>
		*<li><B>Step:</B> UserD follows the Blog</li>
		*</ul>
		*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
		*/
		@Test(groups = { "regression"})
		public void followStandaloneBlog(){
			
			String testName = ui.startTest();
			
			BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
										.description("Description for follow Blog test " + testName)
										.timeZone(Time_Zone.Europe_London)
										.build();
			
			log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			log.info("INFO: Create a new Blog");
			blog.create(ui);

			log.info("INFO: Open Blog");
			ui.clickLinkWait("link=" + blog.getName());
			
			log.info("INFO: Click on the Follow this Blog link");
			ui.clickLinkWait(BlogsUIConstants.followThisBlogBtn);
			
			log.info("INFO: Verify the follow confirmation message displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().followThisBlogMsg),
					"ERROR: The follow this blog confirmation message does not appear");
			
			log.info("INFO: Log out user: " + testUser1.getDisplayName());
			ui.logout();
			
			log.info("INFO: Log into Blogs as: " + testUser2.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs, true);
			ui.login(testUser2);
			
			this.followBlog(blog);

			log.info("INFO: Log out user: " + testUser2.getDisplayName());
			ui.logout();

			log.info("INFO: Log into Blogs as: " + testUser3.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs, true);
			ui.login(testUser3);
			
			this.followBlog(blog);

			log.info("INFO: Log out user: " + testUser3.getDisplayName());
			ui.logout();

			log.info("INFO: Log into Blogs as: " + testUser4.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs, true);
			ui.login(testUser4);
			
			this.followBlog(blog);
			
			ui.endTest();
			
		}	
		
		/**
		* The followBlog method will follow the desired blog: 
		* @param blog - the blog to be followed
		*/		
		
		private void followBlog(BaseBlog blog) {
			log.info("INFO: Click on the Public Blogs link");
			ui.clickLinkWait(BlogsUIConstants.PublicBlogs);

			log.info("INFO: Click on the Blogs Listing view");
			ui.clickLinkWait(BlogsUIConstants.BlogsPublicListing);

			log.info("INFO: Click on the blog to be followed");
			ui.clickLinkWait("link=" + blog.getName());

			log.info("INFO: Click on the Follow this Blog link");
			ui.clickLinkWait(BlogsUIConstants.followThisBlogBtn);

			log.info("INFO: Verify the follow confirmation message displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().followThisBlogMsg),
					"ERROR: The follow this blog confirmation message does not appear");
		}

}

