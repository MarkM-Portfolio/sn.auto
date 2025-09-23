package com.ibm.conn.auto.tests.blogs.regression;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.BlogsUI;

public class Announcements extends SetUpMethods2{
	
	
	private static Logger log = LoggerFactory.getLogger(Announcements.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private User testUser1, testAdmin;
	private APIBlogsHandler apiAdmin;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testAdmin = cfg.getUserAllocator().getAdminUser();
		testUser1 = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiAdmin = new APIBlogsHandler(serverURL, testAdmin.getAttribute(cfg.getLoginPreference()), testAdmin.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests to see if an announcement is visible after posting.
	*<li><B>Step: </B>Create a blog.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Create a new blog entry. 
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box is visible.
	*<li><B>Verify: </B>The link for the Blog Entry is available inside the Announcement box.
	*</ul>
	*/ 
	@Test(groups = {"regression"} )
	public void announceBasic() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();

		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ rand)
												.advanced(true)
												.tags(Data.getData().commonAddress + rand)
												.blogParent(blog)
												.content(Data.getData().commonDescription).announcement(true).build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiAdmin);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " +testAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);
			
		//Go to My Blogs page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Select new blog entry");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);	
		
		//Click on the Public Blogs tab
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Keep refreshing until the Announcement box becomes visible");
		log.info("INFO: Wait for Announcement box to be visible with refresh");
		ui.fluentWaitPresentWithRefresh(BlogsUIConstants.BlogsAnnouncement);

		logger.strongStep("Verify that the Announcement box is present on the page");
		log.info("INFO: Validate the Announcement box is visible");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						 "ERROR: announcements box is not visible after creating single announcement");

		logger.strongStep("Confirm that the link for the Blog Entry is available inside the Announcement box");
		log.info("INFO: Validate the link name for the Blog Entry is present");
		Assert.assertTrue(driver.getFirstElement("link=" + blogPost.getTitle()).isVisible(), 
							"ERROR: link named [" + blogPost.getTitle() + "] could not be found on the page");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests to see if announcements are visible after posting two blog entries.
	*<li><B>Step: </B>Create a blog.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Go to the blog page.
	*<li><B>Step: </B>Create 2 new posts.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box is visible.
	*<li><B>Verify: </B>The links for both Blog Entries are available inside the Announcement box.
	*</ul>
	*/ 
	@Test(groups = {"regression"})
	public void announceMultiple() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
									.tags(Data.getData().commonTag + rand)
									.description(Data.getData().commonDescription)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogPost1 = new BaseBlogPost.Builder("BlogEntry1_" + rand)
												 .advanced(true)
		  										 .tags(Data.getData().commonAddress + rand)
												 .blogParent(blog)
												 .content(Data.getData().commonDescription)
												 .announcement(true)
												 .build();
		
		//create a blog entry base state object
		BaseBlogPost blogPost2 = new BaseBlogPost.Builder("BlogEntry2_" + rand)
												 .advanced(true)
												 .tags(Data.getData().commonAddress + rand)
												 .blogParent(blog)
												 .content(Data.getData().commonDescription)
												 .announcement(true)
												 .build();
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiAdmin);	
		
		//Load the component
		logger.strongStep("Open Blogs and login: " +testAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);
			
		//Go to My Blogs page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Select new blog entry");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));
		
		//Add two new Entries with announcements
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost1.create(ui);

		logger.strongStep("Click on the New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		logger.strongStep("Create the second entry and submit");
		log.info("INFO: Adding the second entry");
		blogPost2.create(ui);
		
		//Click on the Public Blogs tab
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Keep refreshing until the Announcement box becomes visible");
		log.info("INFO: Wait for Announcement box to be visible with refresh");
		ui.fluentWaitPresentWithRefresh(BlogsUIConstants.BlogsAnnouncement);

		//Verify that both entries appear in the announcement box
		logger.strongStep("Confirm that the link for the first Blog Entry is available inside the Announcement box");
		log.info("INFO: Validate First post link is present under Announcements");
		Assert.assertTrue(driver.getFirstElement("link=" + blogPost1.getTitle()).isVisible(), 
						  "ERROR: blog post with name [" + blogPost1.getTitle() + "] was not visible in the announcement box after being posted");

		logger.strongStep("Confirm that the link for the second Blog Entry is available inside the Announcement box");
		log.info("INFO: Validate Second post link is present under Announcements");
		Assert.assertTrue(driver.getFirstElement("link=" + blogPost2.getTitle()).isVisible(), 
						  "ERROR: blog post with name [" + blogPost2.getTitle() + "] was not visible in the announcement box after being posted");
		
		//Logout
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests to see if a blog post with a delay is not visible in the announcement box.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Step: </B>Create a new post with delay.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box is visible.
	*<li><B>Verify: </B>The Blog Entry is not available inside the Announcement box.
	*</ul>
	*/ 
	@Test(groups = {"regression"})
	public void announceDelay() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRand())
												.advanced(true)
												.preExistingBlog(true)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												.delay(true)
												.content(Data.getData().commonDescription)
												.announcement(true)
												.build();
		
		
		//Load the component
		logger.strongStep("Open Blogs and login: " +testAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);
		
		//Click on the Public Blogs tab
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Press button to access New Entry form
		logger.strongStep("Click on the New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntry);

		//Add two new Entries with announcements
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Create a new blog entry");
		blogPost.create(ui);

		//Click on the Public Blogs tab
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blog tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Keep refreshing until the Announcement box becomes visible");
		log.info("INFO: Wait for Announcement box to be visible with refresh");
		ui.fluentWaitPresentWithRefresh(BlogsUIConstants.BlogsAnnouncement);
		
		//Verify that the new entry does not appear in the announcement box
		logger.strongStep("Confirm that the Blog Entry is not available inside the Announcement box");
		log.info("INFO: Validate the Announcement box does not contains the blog post");
		Assert.assertFalse(driver.isElementPresent(BlogsUI.newEntryAnnouncements(blogPost)), 
						  "ERROR: blog entry with name [" + blogPost.getTitle() + "] was visible in the announcement box after being posted with a delay");

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests to see if a blog post is announced to other users after posting.
	*<li><B>Step: </B>Create a blog.
	*<li><B>Step: </B>Login as an Admin user who is the Blog's Owner.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Click on New Entry link for the current blog. 
	*<li><B>Step: </B>Add a Blog Entry.
	*<li><B>Verify: </B>The New Entry button is visible.
	*<li><B>Step: </B>Log out of the session.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box appears in Public Blogs tab.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Login as a different user say user2.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box appears in Public Blogs tab.
	*<li><B>Step: </B>Log out of the session.
	*<li><B>Step: </B>Close the Announcement box.
	*<li><B>Verify: </B>The Announcement box is not visible anymore.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Login again as user2.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box does not appear in Public Blogs tab.
	*<li><B>Step: </B>Log out of the session.
	*<li><B>Step: </B>Refresh the page after deleting all cookies.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box appears in Public Blogs tab.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Login again as user2.
	*<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	*<li><B>Verify: </B>The Announcement box appears in Public Blogs tab.
	*<li><B>Step: </B>Close the Announcement box.
	*<li><B>Verify: </B>The Announcement box is not visible anymore.
	*</ul>
	*/
	//This Scenario is Failing in validation for Announcement is close and not visible.
	@Test(groups = {"regression"},enabled=false)
	public void announceMultipleUsers() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, Data.getData().BlogsAddress1 + rand)
									.tags(Data.getData().commonTag + rand)
									.description(Data.getData().commonDescription)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder(testName + rand)
												.advanced(true)
												.tags(Data.getData().commonAddress + rand)
												.blogParent(blog)
												.content(Data.getData().commonDescription)
												.announcement(true)
												.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiAdmin);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " +testAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);
			
		//Go to My Blogs page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Select new blog entry");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and verify the New Entry button is visible");
		log.info("INFO: Create a new blog entry");
		blogPost.create(ui);
		ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);

		//Logout
		logger.strongStep("Logging out of the session");
		ui.logout();		

		//Go to public Blogs page
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that the announcements appears on the blogs homepage
		logger.strongStep("Verify the Announcement box is visible");
		log.info("INFO: Validate the user can see the Announcement box");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						  "ERROR: announcement box is not visible when logged out in Public Blogs page");
		
		//Go back to the Blogs sign-in page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//login
		logger.strongStep("Logging in as " + testUser1.getDisplayName());
		ui.login(testUser1);
		
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that the announcements appear on the blogs homepage
		logger.strongStep("Verify the Announcement box is visible");
		log.info("INFO: Validate that the user can see the Annoncement box");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						 "ERROR: announcement box is not visible when logged in as user in Public Blogs page");
		
		//Logout
		logger.strongStep("Logging out of the session");
		ui.logout();
	
		//Close the announcement box
		logger.strongStep("Click Close (X) button in the Announcement box");
		log.info("INFO: Close the Announcement box");
		ui.clickLinkWait(BlogsUIConstants.CloseAnnouncementBtn);
		
		//Verify that the announcements do not appear on the blogs homepage
		logger.strongStep("Verify the Announcement box is not visible anymore");
		log.info("INFO: Validate that the Announcement box no longer appears on blogs homepage");
		Assert.assertFalse(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						   "ERROR: announcement box was visible when logged out in Public Blogs page");
		
		//Go back to the Blogs sign-in page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//ui.login as user
		logger.strongStep("Logging in as " + testUser1.getDisplayName());
		ui.login(testUser1);
		
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that the announcements do not show in the blogs homepage
		logger.strongStep("Verify the Announcement box is not visible anymore");
		log.info("INFO: Validate that the Announcement box no longer appears on blogs homepage");
		Assert.assertFalse(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						  "ERROR: announcement box was visible when logged in as user in Public Blogs page");
		
		//Logout
		logger.strongStep("Logging out of the session");
		ui.logout();
		
		//Clear cookies and refresh the browser
		logger.strongStep("Refresh the page after deleting all cookies");
		log.info("INFO: Deleting all cookies and refreshing browser");
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		wd.navigate().refresh();
		
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that the announcement box does show in the blogs homepage
		logger.strongStep("Verify the Announcement box is visible");
		log.info("INFO: Validate the Announcement box appears on the blogs homepage");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						  "ERROR: announcement box was not visible after cookies were deleted and the page refreshed");
		
		//Go back to the Blogs sign-in page
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//ui.login as user
		logger.strongStep("INFO: Logging in as " + testUser1.getDisplayName());
		ui.login(testUser1);
		
		//Go to Public Blogs page
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that the announcement box does show in the blogs homepage
		logger.strongStep("Verify the Announcement box is visible");
		log.info("INFO: Validate the Annoucement box is visible");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						  "ERROR: announcement box was not visible after logging in after deleting cookies");
		
		//Close the announcement box
		logger.strongStep("Click Close (X) button in the Announcement box");
		log.info("INFO: Close the Annoucement box");
		ui.clickLinkWait(BlogsUIConstants.closeAnnouncement);
		
		//Verify that the announcement box does not show in the blogs homepage
		logger.strongStep("Verify the Announcement box is not visible anymore");
		log.info("INFO: Validate the Annoucement box is no longer visible");
		Assert.assertFalse(driver.getSingleElement(BlogsUIConstants.BlogsAnnouncement).isVisible(),
						   "ERROR: announcement box was visible after being closed");
		
		ui.endTest();
		
	}

}
