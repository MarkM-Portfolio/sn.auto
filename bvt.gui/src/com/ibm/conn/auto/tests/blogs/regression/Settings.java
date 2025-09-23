package com.ibm.conn.auto.tests.blogs.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlog.comTime;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.BlogRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.BlogSettings_LeftNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.BlogsUI.EditVia;

public class Settings extends SetUpMethods2{
	
	
	private static Logger log = LoggerFactory.getLogger(Settings.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private User testUser1, testUser2, testAdmin;
	private APIBlogsHandler apiOwner;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		testAdmin = cfg.getUserAllocator().getAdminUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you get an error message for attempting to set a blog name to "" (Empty string).
	 *<li><B>Step: </B>Select the recently created blog.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Verify: </B>An error message is thrown for attempting to erase blog name
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void editBlogEmptyName() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());

		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
			
		logger.strongStep("Try to change the name of the blog to empty");
		log.info("INFO: Attempt to change the name of the blog to empty");
		blog.setName("");
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//verify error message
		logger.strongStep("Verify that an error message is thrown for attempting to erase blog name");
		log.info("INFO: Validate that you receive an error message for attempting to delete blog name");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsErrorMissingNameMsg),
						   "ERROR: Validate error message missing title is not present");
	
		logger.strongStep("Click on the Cancel button");
		log.info("INFO: Select the cancel button");
		ui.clickLinkWait(BaseUIConstants.CancelButton);
	
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can successfully change a blog's properties (name, description, etc.).
	 *<li><B>Step: </B>Select the recently created blog.
	 *<li><B>Step: </B>Set the blog name, description etc
	 *<li><B>Verify: </B>The message 'Saved changes to blog settings' appears.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Verify: </B>The new name and description appear for the blog.
	 *<li><B>Verify: </B>The link for the blog appears on the page.
	 *</ul>
	 */
	@Test(groups = "regression")
	public void editBlog() throws Exception{
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		logger.strongStep("Change the blog name, tag, description and click on Update Blog Settings button");
		log.info("INFO: Edit the blog name, tag and description");
		blog.setName("modified blog" + Helper.genDateBasedRand());
		blog.setTags("modified Tag");
		blog.setDescription("new" + blog.getDescription());
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//Verify success message
		logger.strongStep("Verify the message 'Saved changes to blog settings' appears");
		log.info("INFO: Validate success message after editing the blog");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsSaveChangeMsg),
		   				  "ERROR: No success message after editing the blog");

		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Verify that there is a blog element on the page with the appropriate (changed) name
		logger.strongStep("Verify the new name appears for the blog");
		log.info("INFO: Validate the blog name has changed");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + blog.getName()), 
						  "ERROR: The new blog title [" + blog.getName() + "] could not be located");

		//Verify that there is a blog element on the page
		logger.strongStep("Verify the link for the blog appears on the page");
		log.info("INFO: Validate the blog link on the page");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUI.getBlog(blog)),
						  "ERROR:Validate that there is a blog element on the page");
		
		logger.strongStep("Verify the new description appears for the blog");
		log.info("INFO: Validate the new description");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUI.getBlog(blog)),
						  "ERROR: Validate the new description");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if toggling the blog to inactive/active will reflect on the ability to add comments.
	 *<li><B>Step: </B>Select the recently created blog.
	 *<li><B>Step: </B>Access the blog settings page .
	 *<li><B>Step: </B>Change the blog to inactive via Manage Blog.
	 *<li><B>Verify: </B>The message 'Saved changes to blog settings' appears.
	 *<li><B>Verify: </B>The message 'Turned comments off for inactive blog' appears.
	 *<li><B>Step: </B>Go back to the blog.
	 *<li><B>Step: </B>Navigate to the page to create.
	 *<li><B>Step: </B>Add an Entry.
	 *<li><B>Verify: </B>The 'Add a Comment' link is grey and disabled.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Change the blog to active again via Manage Blog.
	 *<li><B>Step: </B>Go back to the blog.
	 *<li><B>Step: </B>Navigate to the page to create a new blog entry for the blog.
	 *<li><B>Step: </B>Add an Entry.
	 *<li><B>Verify: </B>The 'Add a Comment' link is enabled.
	 *<li><B>Step: </B>Click on the 'Add a Comment' link but don't add a comment.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void markBlogInactive() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		BaseBlogPost blogPost1 = new BaseBlogPost.Builder("BVT Moderation blogEntry"+ Helper.genDateBasedRand())
												 .blogParent(blog)
												 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												 .content(Data.getData().commonDescription)
												 .build();	

		BaseBlogPost blogPost2 = new BaseBlogPost.Builder("BVT Moderation blogEntry"+ Helper.genDateBasedRand())
												 .blogParent(blog)
												 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												 .content(Data.getData().commonDescription)
												 .build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page 
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		logger.strongStep("Deselect the 'Blog is active:' checkbox");
		log.info("INFO: Change the blog to inactive using the 'Blog is active:' checkbox");
		blog.setIsActive(false);
		blog.edit(ui, EditVia.MANAGEBLOG);

		//Verify success message
		logger.strongStep("Verify the message 'Saved changes to blog settings' appears");
		log.info("INFO: Validate success message after editing the blog");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsSaveChangeMsg),
		   				  "ERROR: No success message after editing the blog");
		
		//Verify Turned comments off for inactive blog message 
		logger.strongStep("Verify the message 'Turned comments off for inactive blog' appears");
		log.info("INFO: Validate the display of 'Turned comments off for inactive blog' message");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsTurnOffCommentMsg),
			  			  "ERROR: Unable to find Turned comments off for inactive blog message");
	
		//go back to the blog 
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost1.create(ui);
		
		//verify that the add a comment link is grey and disabled
		logger.strongStep("Verify that the 'Add a Comment' link is grey and disabled");
		log.info("INFO: Validate that the 'Add a Comment' link is grey and disabled");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCommentDisabled),
						  "ERROR: The add a comment link is not grey and disabled");

		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		logger.strongStep("Select the 'Blog is active:' checkbox to make the blog an active one again");
		log.info("INFO: Change the blog to active again via Manage Blog");
		blog.setIsActive(true);
		blog.edit(ui, EditVia.MANAGEBLOG);

		//go back to the blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost2.create(ui);
		
		//verify that the add a comment link is enabled
		logger.strongStep("Verify that the 'Add a Comment' link is enabled");
		log.info("INFO: Validate that the 'Add a Comment' link is enabled");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsAddACommentLink),
						  "ERROR: the [Add a Comment] link is either disabled or does not exist on the page");
		
		//Create comment
		logger.strongStep("Click on the 'Add a Comment' link but don't add a comment");
		log.info("INFO: Click on the 'Add a Comment' link but don't add a comment");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if toggling the blog to no emoticons/emoticons will reflect on the ability to add emoticons.
	 *<li><B>Step: </B>Select the recently created blog.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Edit blog and enable use emoticons via manage blog.
	 *<li><B>Step: </B>Go back to the blog.
	 *<li><B>Step: </B>Add an entry.
	 *<li><B>Verify: </B>The emoticon is enabled.
	 *<li><B>Step: </B>Go to blog settings.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Deselect the 'Use emoticons:' checkbox.
	 *<li><B>Step: </B>Click on 'Update Blog Settings' button.
	 *<li><B>Verify: </B>The newly created entries' emoticons checkbox is unchecked.
	 *</ul>
	 */
	@Test(groups = {"regression"} )
	public void emoticons() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		
		BaseBlogPost blogPost1 = new BaseBlogPost.Builder("BVT Moderation blogEntry"+ Helper.genDateBasedRand())
												 .blogParent(blog)
												 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												 .advanced(true)
												 .content(Data.getData().commonDescription)
												 .complete(false)
												 .build();	

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
			
		logger.strongStep("Select the 'Use emoticons:' checkbox and click on 'Update Blog Settings' button");
		log.info("INFO: Edit blog, enable emoticons and submit");
		blog.setUseEmoticons(true);
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//go back to the blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost1.create(ui);
		
		logger.strongStep("Verify that the emoticon is enabled");
		log.info("INFO: Validate that emoticon is enabled");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsEmoticonSelect).isSelected(),
						  "ERROR: Use Emoticons is not enabled");

		//go to blog settings
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the link for the blog");
		ui.clickLinkWait("link=" + blog.getName());

		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		//uncheck use emoticon
		logger.strongStep("Deselect the 'Use emoticons:' checkbox and click on 'Update Blog Settings' button");
		log.info("INFO: Edit blog, disable emoticons and submit");
		blog.setUseEmoticons(true);
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//Verify that newly created entries' emoticons checkbox is unchecked
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the New Entry link for the blog " + blog);
		log.info("INFO: Click on the New Entry link for the blog " + blog);
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

		logger.strongStep("Verify that newly created entries' emoticons checkbox is unchecked");
		log.info("INFO: Verify that newly created entries' emoticons checkbox is unchecked");
		Assert.assertFalse(driver.getSingleElement(BlogsUIConstants.BlogsNewEntryAdvanEmoticons).isSelected(),
							"ERROR: Emoticons checkbox is checked");

		ui.endTest();
	}
	
	/**
	 <ul>
	 *<li><B>Info: </B>Tests to see if changing the blog timezone will reflect on the timezone of comments.
	 *<li><B>Step: </B>Select the recently created blog.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Change time zone to Time_Zone.Europe_Amsterdam.
	 *<li><B>Verify: </B>The entries' timezone is that which was set in the settings.
	 *<li><B>Step: </B>Navigate to the page to create a new blog entry for the blog.
	 *<li><B>Verify: </B>The time zone equals the expected time zone.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void timezone() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.timeZone(Time_Zone.Pacific_Tongatapu)
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.complete(false)
												.advanced(true)
												.build();		

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
				
		logger.strongStep("Change time zone to 'Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna'");
		log.info("INFO: Change time zone to " + Time_Zone.Europe_Amsterdam.name);
		blog.setTimeZone(Time_Zone.Europe_Amsterdam);
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//verify that entries' timezone is that which was set in the settings
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);

		logger.strongStep("Validate that the time zone equals the expected time zone");
		log.info("INFO: Validate that the time zone equals the expected time zone");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogTimeZone).getText().contains(blog.getTimeZone().name),
							"ERROR: Timezone does not expected timezone " + blog.getTimeZone().name);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if a user can disallow comments.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Edit blog and disable comments.
	 *<li><B>Step: </B>Navigate to the page to create a new blog entry for the blog.
	 *<li><B>Verify: </B>The 'Add a Comment' link is grey and disabled.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void disallowComments() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();	
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Select the recently created blog
		logger.strongStep("Click on the link for the blog " + blog.getName());
		log.info("INFO: Select the recently created blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		logger.strongStep("Deselect the 'Use emoticons:' checkbox and click on 'Update Blog Settings' button");
		log.info("INFO: Edit blog, disable emoticons and submit");
		blog.setAllowComments(false);
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//go back to the blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);

		//verify that the add a comment link is grey and disabled
		logger.strongStep("Verify the 'Add a Comment' link is disabled");
		log.info("INFO: Validate that the 'Add a Comment' link is disabled");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCommentsDisabled),
						  "ERROR: Add a Comment link is either enabled or could not be found");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if a user can allow comments.
	 *<li><B>Step: </B>Access the blog settings page.
	 *<li><B>Step: </B>Edit the blog and change the comment default to 2 days.
	 *<li><B>Step: </B>Navigate to the page to create a new blog entry for the blog.
	 *<li><B>Verify: </B>The 'Add a Comment' link is visible and enabled.
	 *<li><B>Step: </B>Click on the Edit link.
	 *<li><B>Step: </B>Click on Advanced Settings to expand the section.
	 *<li><B>Verify: </B>The 'Allow comments for' check box is visible and selected.
	 *<li><B>Verify: </B>The default time to post a comment is 2 days.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void allowComments() throws Exception { 
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal()).blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();	


		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		logger.strongStep("Click on the blog link");
		log.info("INFO: Select blog link to open blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		logger.strongStep("Edit blog and change the comment default to 2 days");
		log.info("INFO: Edit blog and change the comment default to 2 days");
		blog.setCommentsTime(comTime._2DAYS);
		blog.edit(ui, EditVia.MANAGEBLOG);
			
		//go back to the blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//select the new blog entry button
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);	

		//go back to any entry page for this blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the blog link");
		log.info("INFO: Select blog link to open blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		logger.strongStep("Click on the Blog Entry");
		log.info("INFO: Select the blog post by title");
		ui.clickLinkWait("link=" + blogPost.getTitle());
		
		//verify that the add a comment link is enabled
		logger.strongStep("Verify the 'Add a Comment' is visible");
		log.info("INFO: Validate that the add a comment link is visible and enabled");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsAddACommentLink),
						  "ERROR: The add a comment link is not enabled");
		
		//click the edit link for this entry
		logger.strongStep("Click on the Edit link");
		log.info("INFO: Select the Edit link");
		ui.clickLinkWait("link=Edit");

		//expand advanced settings,
		logger.strongStep("Click on Advanced Settings to expand the section");
		log.info("INFO: Expand the Advanced Settings section");
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryDelay);

		logger.strongStep("Verify the 'Allow comments for' check box is visible");
		log.info("INFO: Validate the allow comments check box is present");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryAllowComments),
						   "ERROR: The allow comments checkbox was not located");
		
		logger.strongStep("Verify the 'Allow comments for' check box is selected by default");
		log.info("INFO: Validate that the 'Allow comments for' check box is selected by default");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsNewEntryAllowComments).isSelected(),
						  "ERROR: The allow comments checkbox was not selected");
		
		//default time is 2 days
		logger.strongStep("Verify the default time to post a comment is 2 days");
		log.info("INFO: Validate that the default time to post a comment is 2 days");
		List<Element> selectedOptions = driver.getSingleElement(BlogsUIConstants.BlogsNewEntryAllowCommentsFor).useAsDropdown().getAllSelectedOptions();
		String actualTimeToAllowComments = selectedOptions.get(0).getText();
		Assert.assertEquals(actualTimeToAllowComments, blog.getCommentsTime().commentDays,
				            "ERROR: Default time to post a comment is not 2 days");
			
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the defaulting function for disallow comments in the blog.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Click on the blog's link and then click on Manage Blog link.
	 *<li><B>Step: </B>Uncheck 'Allow comments for your blog:' check box.
	 *<li><B>Step: </B>Select the 'Apply comment defaults to existing entries (this time only):' check box.
	 *<li><B>Step: </B>Navigate to the page to create a new blog entry for the blog.
	 *<li><B>Verify: </B>The 'Add a Comment' link is grey and disabled.
	 *<li><B>Step: </B>Click the Edit link for this entry.
	 *<li><B>Step: </B>Expand the Advanced Settings section.
	 *<li><B>Verify: </B>The 'Allow comments for' checkbox is checked.
	 *<li><B>Verify: </B>The default time is unlimited days.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void disallowCommentsDefault() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//go back to the blog
		logger.strongStep("Click on the blog link");
		log.info("INFO: Navigate to the blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		//uncheck "By default allow comments for new entries"
		log.info("INFO: Edit the blog, disallow comments and apply comment defaults to existing entries");
		logger.strongStep("Deselect the 'Allow comments for your blog:' check box");
		blog.setAllowComments(false);
		logger.strongStep("Select the 'Apply comment defaults to existing entries (this time only):' check box");
		blog.setApplyExistEntry(true);
		logger.strongStep("Click on 'Update Blog Settings' button");
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		//go back to blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);	
		
		//verify that the add a comment link is grey and disabled
		logger.strongStep("Verify the 'Add a Comment' link is grey and disabled");
		log.info("INFO: Validate that the 'Add a Comment' link is grey and disabled");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCommentsDisabled),
						 "ERROR: the Add a Comment link was either enabled or could not be found");

		//click the edit link for this entry
		logger.strongStep("Click on the Edit link");
		log.info("INFO: Select the Edit link for this entry");
		ui.clickLinkWait("link=Edit");

		
		//expand advanced settings
		logger.strongStep("Click on Advanced Settings to expand the section");
		log.info("INFO: Expand the Advanced Settings section");
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryDelay);

		//verify that...
		//allow comments checkbox is unchecked 
		logger.strongStep("Verify the 'Allow comments for' check box is selected");
		log.info("INFO: Validate that the 'Allow comments for' check box is selected");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsNewEntryAllowComments).isSelected(),
						  "ERROR: Allow comments is not selected");

		//default time is unlimited days
		logger.strongStep("Verify 'unlimited days' is selected in the drop down next to the 'Allow comments for' check box");
		log.info("INFO: Validate that 'Allow comments for' is set to 'unlimited days'");
		List<Element> selectedElements = driver.getSingleElement(BlogsUIConstants.BlogsNewEntryAllowCommentsFor).useAsDropdown().getAllSelectedOptions();
		String actualTimeToAllowComments = selectedElements.get(0).getText();
		Assert.assertEquals(actualTimeToAllowComments, blog.getCommentsTime().commentDays,
							"ERROR: Allow comments for is not set to unlimited days");
			
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests if a blog can be deleted.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Click on the blog's link and then click on Manage Blog link.
	 *<li><B>Step: </B>Click on the Delete Blog button.
	 *<li><B>Step: </B>Click on Cancel button in the 'Confirm Deletion of Blog' dialog box.
	 *<li><B>Verify: </B>The blog is not removed from My Blogs list.
	 *<li><B>Step: </B>Click on the blog's link and then click on Manage Blog link.
	 *<li><B>Step: </B>Click on the Delete Blog button.
	 *<li><B>Step: </B>Select 'I understand that this action cannot be undone' check box in 'Confirm Deletion of Blog' dialog box.
	 *<li><B>Step: </B>Click on the Delete button.
	 *<li><B>Step: </B>Switch back to My Blogs tab.
	 *<li><B>Verify: </B>The blog is removed from My Blogs list.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void removeBlog() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Go to the blog
		logger.strongStep("Click on the blog link");
		log.info("INFO: Navigate to the blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
				
		//Click remove blog		
		logger.strongStep("Click on Delete Blog button");
		log.info("INFO: Select Delete blog button");
		ui.clickLinkWait(BlogsUIConstants.BlogsSettingsRemoveBlog);
		
		//Click Cancel in the confirmation dialog
		logger.strongStep("Click on Cancel button in the 'Confirm Deletion of Blog' dialog box");
		log.info("INFO: Select Cancel in the confirmation dialog");
		ui.clickLinkWait(BlogsUIConstants.BlogsSettingsRemoveBlogConfirmationNo);

		//Verify blog is not removed
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Verify the blog shows in My Blogs tab");
		log.info("INFO: Verify the blog name is found in My Blogs list");
		Assert.assertTrue(ui.fluentWaitTextPresent(blog.getName()),
						  "ERROR: Blog not found");
		
		//go to the settings
		logger.strongStep("Click on the blog's link");
		log.info("INFO: Select blog by clicking on its link");
		ui.clickLinkWait("link=" + blog.getName());
			
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
			
		//click remove blog
		logger.strongStep("Click on Delete Blog button");
		log.info("INFO: Select Delete blog button");
		ui.clickLinkWait(BlogsUIConstants.BlogsSettingsRemoveBlog);
		
		
		//Enable checkbox and click Delete in confirmation dialog
		logger.strongStep("Select 'I understand that this action cannot be undone' check box in 'Confirm Deletion of Blog' dialog box");
		log.info("INFO: Enable 'I understand that this action cannot be undone' check box in 'Confirm Deletion of Blog' dialog box");
		ui.clickLink(BlogsUIConstants.BlogsSettingsRemoveBlogCheckBox);
		logger.strongStep("Click on the Delete button");
		log.info("INFO: Select Delete button in the confirmation dialog");
		ui.clickLinkWait(BlogsUIConstants.BlogsSettingsRemoveBlogConfirmationYes);
		
		//verify blog is removed
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Verify the blog does not show in My Blogs tab");
		log.info("INFO: Verify the blog name is no longer found in My Blogs list");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(blog.getName()),
						  "ERROR: Blog found in list");
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests if two elevated users can edit the same blog.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Open Manage Blog page.
	 *<li><B>Step: </B>Navigate to Authors page using the left navigation menu.
	 *<li><B>Step: </B>Add an author and a drafter.
	 *<li><B>Verify: </B>The author and drafter were added.
	 *<li><B>Step: </B>Go back to the blog and log in with the author.
	 *<li><B>Step: </B>Create an entry.
	 *<li><B>Step: </B>Login as drafter.
	 *<li><B>Step: </B>Open the blog and then the blog entry.
	 *<li><B>Verify: </B>The New Entry link is visible and enabled for the blog's drafter.
	 *<li><B>Step: </B>Click on the Edit link for the entry.
	 *<li><B>Step: </B>Change contents in tile.
	 *<li><B>Step: </B>Click on 'Update Blog Settings' button.
	 *<li><B>Verify: </B>The modified blog entry is still visible.
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void crossEdit() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Allocate three unique users
		User author = cfg.getUserAllocator().getUser();
		User drafter = cfg.getUserAllocator().getUser();
		
		Member memberDraft = new Member(BlogRole.DRAFT, drafter);
		Member memberAuthor = new Member(BlogRole.AUTHOR, author);

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();		
	
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Go to the blog
		logger.strongStep("Click on the blog link");
		log.info("INFO: Navigate to the blog");
		ui.clickLinkWait("link=" + blog.getName());

		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		//Navigate to Authors page
		logger.strongStep("Click on 'Authors' link in the left navigation menu");
		log.info("INFO: Navigate to 'Authors' page using the left navigation choice");
		BlogSettings_LeftNav_Menu.AUTHORS.select(ui);

		logger.strongStep("Add a draft member to the blog");
		log.info("INFO: Add a draft member to the blog");
		ui.addMember(memberDraft);
		
		logger.strongStep("Add an author member to the blog");
		log.info("INFO: Add an author member to the blog");
		ui.addMember(memberAuthor);
		
		//Verify author and drafter were added
		logger.strongStep("Verify the author was added to the blog members");
		log.info("INFO Validate that the author was added to the blog members");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUI.getBlogMember(author)),
						  "ERROR: Author was not added");
		
		logger.strongStep("Verify the drafter was added to the blog members");
		log.info("INFO: Validate that the drafter was added to the blog members");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUI.getBlogMember(drafter)),
						  "ERROR: Drafter was not added");

		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		// go back to the blog and log in with the author, make an entry
		logger.strongStep("Open Blogs and login: " + author.getDisplayName());
		log.info("INFO: Log into Blogs as " + author.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(author);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);
		
		// logout
		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		logger.strongStep("Open Blogs and login: " + drafter.getDisplayName());
		log.info("INFO: Log into Blogs as " + drafter.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(drafter);

		// go to the blog
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Got Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Click on 'Blogs Listing' link in the left navigation menu");
		log.info("INFO: Go to Public Blogs listing page using the left navigation menu");
		ui.clickLinkWait(BlogsUIConstants.BlogsPublicListing);

		logger.strongStep("Click on the blog's link");
		log.info("INFO: Select the blog by name");
		ui.clickLinkWait("css=div[aria-label='Blog list'] a:contains(" + blog.getName() + ")");
		
		logger.strongStep("Click on the blog entry's link");
		log.info("INFO: Select the blog post by name");
		ui.clickLinkWait("link=" + blogPost.getTitle());
		
		logger.strongStep("Verify the New Entry link is visible and enabled for the blog's drafter");
		log.info("INFO: Validate that the drafter can create a New Entry");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=New Entry"), 
						  "ERROR: the New Entry link could not be located");	

		logger.strongStep("Click on the Edit link");
		log.info("INFO: Select Edit link");
		ui.clickLinkWait("link=Edit");
		
		//change contents in tile
		logger.strongStep("Change the blog entry's title to " + blogPost.getTitle());
		log.info("INFO: Clear the blog entry's title and then change it to " + blogPost.getTitle());
		blogPost.setTitle("modified blog" + Helper.genDateBasedRand());
		driver.getSingleElement(BlogsUIConstants.BlogsNewEntryTitle).clear();
		driver.getSingleElement(BlogsUIConstants.BlogsNewEntryTitle).type(blogPost.getTitle());
		
		//click Update Blog Settings button 
		logger.strongStep("Click on 'Update Blog Settings' button");
		log.info("INFO: Select 'Update Blog Settings' button");
		ui.clickLinkWait(BlogsUIConstants.blogPostEntryID);

		logger.strongStep("Verify the modified blog entry is visible");
		log.info("INFO: Validate the modified blog entry is present");
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()),
						  "ERROR: Modified blog entry is not present");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to verify that a comment is moderated before it is shown.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Login as user1.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Create a blog entry for the blog.
	 *<li><B>Step: </B>Access the Manage Blog page.
	 *<li><B>Step: </B>Check the 'Moderate comments:' box.
	 *<li><B>Step: </B>Click on 'Update Blog Settings' link.
	 *<li><B>Step: </B>Login as user2.
	 *<li><B>Step: </B>Navigate to the blog and then the blog entry.
	 *<li><B>Step: </B>Add a comment to the entry.
	 *<li><B>Verify: </B>The message 'Your comment has been submitted to the moderator for approval' appears.
	 *<li><B>Verify: </B>The comment does not appear.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void moderateComment() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();	

		BaseBlogComment comment = new BaseBlogComment.Builder("This is the test for the comment to be added to the entry in blogs")
													 .build();
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//go back to blog
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create a new blog entry for the blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);	
		
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Go to the blog
		logger.strongStep("Click on the blog link");
		log.info("INFO: Navigate to the blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Access the blog settings page
		logger.strongStep("Click on Manage Blog link");
		log.info("INFO: Select Manage Blog");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);

		//check "moderate comment" 
		logger.strongStep("Check the 'Moderate comments:' box and click on 'Update Blog Settings' link");
		blog.setModerateComments(true);
		blog.edit(ui, EditVia.MANAGEBLOG);
		
		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		logger.strongStep("Open Blogs and login: " + testUser2.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser2);

		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		logger.strongStep("Click on 'Blogs Listing' link in the left navigation menu");
		log.info("INFO: Go to Public Blogs listing page using the left navigation menu");
		ui.clickLinkWait(BlogsUIConstants.BlogsPublicListing);
		
		logger.strongStep("Click on the blog's link");
		log.info("INFO: Select the blog by name");
		ui.clickLinkWait("link=" + blog.getName());
		
		logger.strongStep("Click on the blog entry's link");
		log.info("INFO: Select the blog post by name");
		ui.clickLinkWait("link=" + blogPost.getTitle());
		
		//Add a comment
		logger.strongStep("Click on 'Add a Comment', add a comment and click on Submit button");
		log.info("INFO: Add a new comment to the entry");
		comment.create(ui);

		//verify you receive moderate message
		logger.strongStep("Verify the message 'Your comment has been submitted to the moderator for approval' appears");
		log.info("INFO: Validate that Your comment has been submitted to the moderator for approval is received");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsModerateMsg),
						   "ERROR: Validate moderate message was not present");
		
		//verify comment does not appear
		logger.strongStep("Verify the comment is not visible");
		log.info("INFO: Validate that the comment is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(comment.getContent()),
						  "ERROR: Comment is present");
		
		ui.endTest();
		
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests custom admin extensions.
	 *<li><B>Step: </B>Login as an Admin user.
	 *<li><B>Step: </B>Select Administration link.
	 *<li><B>Step: </B>Clear the 'Allowed extensions:' field and and new extensions.
	 *<li><B>Step: </B>Click on Save button.
	 *<li><B>Verify: </B>The message 'Change Saved' is displayed.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void adminCustomExtensions() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		//Load the component
		logger.strongStep("Open Blogs and login: " + testAdmin.getDisplayName());
		log.info("INFO: Log into Blogs as: " + testAdmin.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);

		logger.strongStep("Click on the Administration link");
		log.info("INFO: Select Administration link");
		ui.clickLink(BlogsUIConstants.Administration);
		
		logger.strongStep("Clear the 'Allowed extensions:' field and and new extensions");
		log.info("INFO: Clear the 'Allowed extensions:' field and and new extensions");
		driver.getSingleElement(BlogsUIConstants.allowedExtensionsInput).clear();
		driver.getSingleElement(BlogsUIConstants.allowedExtensionsInput).type(BlogsUIConstants.allowedExtensions);
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Save the change to new extensions");
		ui.clickLink(BlogsUIConstants.BlogsSiteSettingsSave);
	
		//Verify confirmation status is "Change saved."
		logger.strongStep("Verify the message 'Change Saved' is displayed");
		log.info("INFO: Validate the message 'Change Saved' appears");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.SiteAdminSaveChangeMsg),
						 "ERROR: Validate the blogs message reports change is saved");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the delay functionality for posts.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Login and Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Create an entry.
	 *<li><B>Verify: </B>The 'Delay publishing the entry until the following time:' check box is visible.
	 *<li><B>Verify: </B>The drop down to select the hour is present.
	 *<li><B>Verify: </B>The drop down to select the minute is present.
	 *<li><B>Verify: </B>The delay publishing date picker is present.
	 *<li><B>Verify: </B>The timezone label is present.
	 *<li><B>Step: </B>Save the post.
	 *<li><B>Verify: </B>The smiley emoticon is not present since the Emoticons check box was not selected for the new entry.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void delayPostingUI() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT Moderation blogEntry"+ Helper.genDateBasedRand())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												.advanced(true)
												.content(Data.getData().commonDescription)
												.complete(false)
												.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);
		
		//Verify existence of delay publishing checkbox
		logger.strongStep("Verify the 'Delay publishing the entry until the following time:' check box is visible");
		log.info("INFO: Validate the existence of 'Delay publishing the entry until the following time:' check box");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryDelay),
						  "ERROR: Delay publishing checkbox not present in new entry form advanced options");
		
		//Verify existence of delay publishing hours combo box
		logger.strongStep("Verify the drop down to select the hour is present");
		log.info("INFO: Validate the existence of delay publishing hours combo box");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryHour),
				          "ERROR: Delay publishing hours combobox not present in new entry form advanced options");
		
		//Verify existence of delay publishing minutes combo box
		logger.strongStep("Verify the drop down to select the minute is present");
		log.info("INFO: Validate the existence of delay publishing minutes combo box");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryMinutes),
						  "ERROR: Delay publishing minutes combobox not present in new entry form advanced options");
		
		//Verify existence of delay publishing date picker
		logger.strongStep("Verify the delay publishing date picker is present");
		log.info("INFO: Validate the existence of delay publishing date picker");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryDate),
						  "ERROR: Delay publishing date picker not present in new entry form advanced options");
		
		//Verify existence of timezone label
		logger.strongStep("Verify the timezone label is present");
		log.info("INFO: Validate the existence of timezone label");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsTimezone),
						  "ERROR: Timezone not present in new entry form advanced options");
		
		//After validating fields above save post
		logger.strongStep("Click on the Post button");
		log.info("INFO: Save post");
		ui.clickButton(Data.getData().buttonPost);
		
		//Verify that the smiley emoticon is not present
		logger.strongStep("Verify the smiley emoticon is not present since the Emoticons check box was not selected for the new entry");
		log.info("INFO: Validate the smiley emoticon is not present");
		Assert.assertFalse(driver.isElementPresent(BlogsUIConstants.Smiley),
						  "ERROR: Smiley emoticon was present after new entry was posted with emoticons disabled");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if the emoticon menu is present while editing.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on New Entry link.
	 *<li><B>Step: </B>Add an Entry.
	 *<li><B>Verify: </B>The smiley emoticon is present.
	 *</ul>
	 */
	//This scenario is failing while validating the Emotions.
	@Test(groups = {"regression"},enabled=false)
	public void smileyEmoticons() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		   				   			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		   				   			.description(Data.getData().commonDescription)
		   				   			.theme(Theme.Blog_with_Bookmarks)
		   				   			.build();
		
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT Moderation blogEntry"+ Helper.genDateBasedRand())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												.advanced(true)
												.content(Data.getData().commonDescription+" :-)")
												.enableEmoticons(true)
												.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);

		//Load the component
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to the page to create
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the page and click new entry");
		ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an entry");
		blogPost.create(ui);

		//Verify that the smiley emoticon is present
		logger.strongStep("Verify the smiley emoticon is present");
		log.info("INFO: Valdiate that the smiley emoticon is present");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.Smiley),
						  "Error: smiley emoticon is not present in entry text after posting \":)\" entry with emoticons enabled");

		ui.endTest();
	}
	
	
}
