package com.ibm.conn.auto.tests.blogs;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.webdriver.WebDriverExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_Blogs extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Blogs.class);
	private User testUser1, testAdmin;
	private BlogsUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private String serverURL;
	private BaseCommunity.Access defaultAccess;

	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Validating the Mega Menu</li>
	 *<li><B>Step: </B>Select Blogs Mega Menu option</li>
	 *<li><B>Verify: </B>Mega menu item for Blogs</li>
	 *<li><B>Step: </B>Click Public Blogs</li>
	 *<li><B>Verify: </B>Mega menu item for Latest Entries</li>
	 *<li><B>Verify: </B>Mega menu item for Public Blogs Listing</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt"})
	public void validateMegaMenu() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		
		ui.startTest();
		
		// Load the component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Click Mega Menu item
		logger.strongStep("Click Mega Menu item");
		log.info("INFO: Select Blogs Mega Menu option");
		ui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate Blogs option is contained with in drop down menu
		logger.weakStep("Validate Blogs option is contained with in drop down menu");
		log.info("INFO: Validate 'Blogs' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.blogsOption),
						  "ERROR: Unable to validate Mega Menu 'Blogs' option in drop down menu");

		//Validate Latest Entries option is contained with in drop down menu
		logger.weakStep("Validate Latest Entries option is contained with in drop down menu");
		log.info("INFO: Validate 'Latest Entries' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.blogsLatestEntries),
						  "ERROR: Unable to validate Mega Menu 'Latest Entries' option in drop down menu");

		//Validate Public Blogs Listing option is contained with in drop down menu
		logger.weakStep("Validate Public Blogs Listing option is contained with in drop down menu");
		log.info("INFO: Validate 'Public Blogs Listing' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.blogsPublicBlogsListing),
						  "ERROR: Unable to validate Mega Menu 'Public Blogs Listing' option in drop down menu");

		ui.endTest();
	
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Creating a Blog</li>
	*<li><B>Step: </B>Navigate to Blogs</li>
	*<li><B>Step: </B>Click Start a Blog to create a blog</li> 
	*<li><B>Step: </B>Save the blog</li>
	*<li><B>Step: </B>Open the blog</li>
	*<li><B>Verify: </B>Blog opens and New Entry button displays</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/ 
	@Test(groups = {"regression", "bvt", "cnx8ui-cplevel2"} )
	public void createBlog() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
	
		String testName = ui.startTest();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		//Load component and login
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);
				
		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Verify that the blog opens and New Entry button displays
		logger.weakStep("Verify that the blog opens and New Entry button displays");
		log.info("INFO: Verify that the blog opens and New Entry button displays");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.blogsNewEntryMenuItem),
				          "ERROR: New Entry button not found in the opened blog");
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Deleting a Blog</li>
	*<li><B>Step: </B>Navigate to Blogs</li>
	*<li><B>Step: </B>Create Blog by clicking Start a Blog button</li>
	*<li><B>Step: </B>Open the blog just create and delete it</li>
	*<li><B>Verify: </B>Blog has been deleted, the title no longer appears</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/ 
	@Test(groups = {"level2", "bvt", "cnx8ui-level2"} )
	public void deleteBlog() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		//Load component and login
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);
			
		
		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		
		//delete blog
		logger.strongStep("Delete the blog");
		log.info("INFO: Delete the blog");
		blog.delete(ui);
		
		//Verify that the blog title is no longer displayed
		logger.weakStep("Verify that the blog title is no longer displayed");
		log.info("INFO: Verify that the blog is no longer displayed in the list");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(blog.getName()),
				          "ERROR: Blog title is still visible in the list");
		
		ui.endTest();	
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Edit Blog Site Configuration Settings</li>
	*<li><B>Step: </B>Click Start a Blog to create a blog</li>
	*<li><B>Step: </B>Click Administration tab</li>
	*<li><B>Step: </B>Change field Handle of blog to serve as Blogs Homepage</li>
	*<li><B>Verify: </B>The settings are changed and saved correctly</li>
	*</ul>
	*/
	//@Test(groups = {"level2", "bvt", "icStageSkip"} )
	public void editBlogsSiteSettings() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		testAdmin = cfg.getUserAllocator().getAdminUser();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		//Load component and login
		logger.strongStep("Open Blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);
		
		
		//Edit the admin settings
		logger.strongStep("Edit the admin settings");
		log.info("INFO: Change handle in Admin Settings");
		ui.changeAdminSettings();
		
		//Verify that the settings are saved
		        logger.weakStep("Verify that the settings are saved");
				log.info("INFO: Verify Change saved status message displays");
				Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.SiteAdminSaveChangeMsg),
						          "ERROR: Change saved message not found");

		ui.endTest();
		
	}

	
	/**
	*<ul>
	*<li><B>Info: </B>Blog as an admin user</li>
	*<li><B>Step: </B>As an admin user, click the Start a Blog button to create a Blog </li>
	*<li><B>Step: </B>Open the Blog</li>
	*<li><B>Step: </B>Click New Entry to create an entry</li>
	*<li><B>Step: </B>Add a comment to the entry</li>
	*<li><B>Verify: </B>The comment was created</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/
	@Test(groups = { "level2", "bvt", "icStageSkip", "cnx8ui-level2"})
	public void blogAdminUser() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		testAdmin = cfg.getUserAllocator().getAdminUser();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
		 										 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
		 										 .content("Test description for testcase " + testName)
		 										 .build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		//Load component and login
		log.info("INFO: Open Blogs and login: " + testUser1.getDisplayName());
		logger.strongStep("Load component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testAdmin, cfg.getUseNewUI());

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);
		
		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add a new Entry");
		log.info("INFO: Add the new entry");
		blogEntry.create(ui);

		//Add a comment
		logger.strongStep("Add a comment");
		log.info("INFO: Add a new comment to the entry");
		comment.create(ui);
		
		//Verify that the comment is present
		logger.weakStep("Verify that the comment is present");
		log.info("INFO: Verify that the comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment.getContent()),
						  "ERROR: Comment not found");

		ui.endTest();	
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Blog as a standard user</li>
	*<li><B>Step: </B>As a standard user, click Start a Blog to create a Blog</li>
	*<li><B>Step: </B>Open the Blog</li>
	*<li><B>Step: </B>Click New Entry to create an entry</li>
	*<li><B>Step: </B>Add a comment to the entry</li>
	*<li><B>Verify: </B>The comment was created</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/
	@Test(groups = { "level2", "bvt", "cnx8ui-level2", "cnx8ui-cplevel2"})
	public void blogStandardUser() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)						
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
												 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												 .content("Test description for testcase " + testName)
												 .build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);
		
		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());

		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add a new Entry");
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(ui);

		//Add a comment
		logger.strongStep("Add a comment");
		log.info("INFO: Add a comment to the entry");
		comment.create(ui);
		
		logger.weakStep("Verify that the comment exists");
		log.info("INFO: Verify that the comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment.getContent()),
						  "ERROR: Comment not found");

		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Add an Entry with an Image Embedded</li>
	*<li><B>Step: </B>As an admin user, click Start a Blog button to create a blog</li>
	*<li><B>Step: </B>Click New Entry button to create an entry</li>
	*<li><B>Step: </B>Add a comment to the entry</li>
	*<li><B>Step: </B>Click Manage Blog button and upload a file</li>
	*<li><B>Step: </B>From left nav, Click File Uploads and upload a File</li>
	*<li><B>Step: </B>Click My Blogs tab and open the blog</li>
	*<li><B>Step: </B>Click the New Entry button</li>
	*<li><B>Step: </B>Add the uploaded image to the entry</li>
	*<li><B>Verify: </B>The entry with embedded image was created</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/
	//Temporarily comment this case for task 196056 (new insert image dialog UI will impact this), it will rewrite later
	//@Test(groups = { "level2", "bvt", "icStageSkip"})
	public void addEntryWithImage() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		List<String> FilesToUpload = new ArrayList<String>();
		
		FilesToUpload.add(Data.getData().file5);
		
		String testName = ui.startTest();
		
		testAdmin = cfg.getUserAllocator().getAdminUser();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("BlogEntry1" + Helper.genDateBasedRand()).blogParent(blog)
												 .tags(testName + Helper.genDateBasedRand())
												 .content("Test description for testcase " + testName)
												 .build();
		
		BaseBlogPost blogEntry2 = new BaseBlogPost.Builder("BlogEntry2" + Helper.genDateBasedRand()).blogParent(blog)
												 .tags(testName + Helper.genDateBasedRand())
												 .content("Test description for testcase " + testName)
												 .useUploadedImage(Data.getData().file5)
												 .build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testAdmin);

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);

		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add an Entry A");
		log.info("INFO: Add a new entry to the blog");
		ui.clickLink(BlogsUIConstants.BlogsNewEntry);
		blogEntry1.create(ui);

		//Add a comment
		logger.strongStep("Add a comment");
		log.info("INFO: Add a new comment to the entry A");
		comment.create(ui);
		
		logger.strongStep("Select to manage blog");
		log.info("INFO: Select to manage blog");
		ui.clickLink(BlogsUIConstants.blogManage);
		
		//Upload a file
		logger.strongStep("Upload a file");
		log.info("INFO: Upload " + FilesToUpload.size()+" new file(s)");
		ui.blogsAddFileToUpload(FilesToUpload);

		//select My Blogs from tab
		logger.strongStep("select My Blogs from tab");
		ui.clickLink(BlogsUIConstants.MyBlogs);
		
		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
		//Add a new entry with image that you just uploaded
		logger.strongStep("Add a new entry B with image that you just uploaded");
		log.info("INFO: Add a new entry with an image");
		ui.clickLink(BlogsUIConstants.BlogsNewEntry);
		blogEntry2.create(ui);
		
		//Verify that new entry exists
		logger.weakStep("Verify that new entry B exists");
		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry2.getTitle()), "ERROR: Entry with image not found");
		
		//Verify that the entry contains the image
		logger.weakStep("Verify that the entry B contains the image");
		log.info("INFO: Verify that the uploaded image is present");
		String imageSelector = "css=div.entryContentContainer img[src$='" +	Data.getData().file5 + "']";
		//No need to check the element is valid here as ui.getFirstVisibleElement asserts it is
		Element imageElement = ui.getFirstVisibleElement(imageSelector);
		String imageURL = imageElement.getAttribute("src");
		Assert.assertNotNull(imageURL, "ERROR: Image in entry not found!");
		
		//Check the image link is valid. This cannot be done in webdriver.
		logger.weakStep("Check if the image link is valid. This cannot be done in webdriver.");
		log.info("INFO: Checking if image link at URL " + imageURL + " is OK");
		int httpResponseCode;
		
		boolean flag1 = TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM");
		boolean flag2 = TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM_SPNEGO");
		if(flag1||flag2)
			httpResponseCode = Helper.getResponseCodeHTMLUnit(imageURL, testAdmin.getUid(), testAdmin.getPassword());
		else
			httpResponseCode = Helper.getResponseCode(imageURL);
		
		log.info("INFO: HTTP GET on " + imageURL + " returned code " + httpResponseCode);
		Assert.assertTrue(httpResponseCode == 200, "ERROR: The image cannot be loaded!");
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Viewing Blogs anonymously</li>
	*<li><B>Step: </B>Go to Blogs application anonymously</li>
	*<li><B>Verify: </B>URL redirects to Public Blogs</li>
	*<li><B>Verify: </B>Start a Blog button displays</li>
	*</ul>
	*Note: The cloud doesn't support this feature and so this test is not tested against the cloud
	*/ 
	@Test(groups = { "level2", "bvt", "cnx8ui-level2"})
	public void anonymousAccess() {
		//Get Blogs public URL
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String blogsURL = Data.getData().ComponentBlogs.split("login")[0];
		
		ui.startTest();
		
		logger.strongStep("Load Blog");
		log.info("INFO: Load blogs component");
		ui.loadComponent(blogsURL);
		CommonUICnx8 commonUI = new CommonUICnx8(driver);
		commonUI.toggleNewUI(cfg.getUseNewUI());
		
		logger.weakStep("Check page title contains Public Blogs");
		log.info("Check page title contains Public Blogs");
		String pageTitle = driver.getTitle();
		Assert.assertTrue(pageTitle.contains("Public Blogs"), "ERROR: Page title: '" + pageTitle + "' did not contain Public Blogs");
		
		log.info("Check that Start a Blog button displays");
		logger.weakStep("Check that Start a Blog button displays");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.StartABlog), "ERROR: Start a Blog button not found");
		
		ui.endTest();
		
	}

	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a Blog widget</li>
	*<li><B>Step: </B>Create a Community via API</li>
	*<li><B>Step: </B>Click Community Actions > Add App </li>
	*<li><B>Step: </B>Add Blog widget</li> 
	*<li><B>Step: </B>Click Blog in the left side nav pane</li>
	*<li><B>Verify: </B>New Entry button displays in the Community Blog</li>
	*</ul>
	*/ 
	@Test(groups = {"cplevel2", "level2", "bvt", "regressioncloud", "cnx8ui-level2"})
	public void addBlogWidget() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
            logger.strongStep("Add Blog widget");
            log.info("INFO: Add blog widget with api");
            community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
        }
		
		//GUI
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		boolean isCardView = ui.checkGKSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the blogs link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from the tabbed Navigation menu");
			Community_TabbedNav_Menu.BLOG.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from left Navigation menu");
			Community_LeftNav_Menu.BLOG.select(cUI);
		}

		//Verify that the community blog opens and New Entry button displays
		logger.weakStep("Verify that the community blog opens and New Entry button displays");
		log.info("INFO: Verify that the community blog opens and New Entry button displays");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.blogsNewEntryMenuItem),
				          "ERROR: New Entry button not found in the opened blog");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}


	/**
	*<ul>
	*<li><B>Info: </B>Adding an Entry to Community Blog</li>
	*<li><B>Step: </B>[API]Create a Community with a description</li>
	*<li><B>Step: </B>[API]Click on Community Actions > Add Apps and add Blog widget</li>
	*<li><B>Step: </B>Click on Blog link in the left nav pane</li> 
	*<li><B>Step: </B>Select New Entry button</li>
	*<li><B>Step: </B>Add Entry</li>
	*<li><B>Verify: </B>Entry was created</li>
	*</ul>
	*/ 
	@Test(groups={"level2", "smokeonprem", "bvt", "bvtcloud", "regressioncloud", "smokecloud", "cnx8ui-level2"})
	public void addComBlogEntry() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
		 							.content("Test description for testcase " + testName)
		 							.build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		boolean isCardView = ui.checkGKSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the blogs link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from the tabbed Navigation menu");
			Community_TabbedNav_Menu.BLOG.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from left Navigation menu");
			Community_LeftNav_Menu.BLOG.select(cUI);
		}
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add a new Entry");
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(ui);

		//Verify that new entry exists
		logger.weakStep("Verify that new entry exists");
		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), "ERROR: Entry not found");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	

	/**
	*<ul>
	*<li><B>Info: </B>Check whether GK unify_insert_image_dialog is enabled, if no skip this case</li>
	*<li><B>Step: </B>Navigate to Blogs</li>
	*<li><B>Step: </B>Click Start a Blog to create a blog</li> 
	*<li><B>Step: </B>New a blog entry</li>
	*<li><B>Step: </B>Open the unified insert image dialog and click 'Cancel' button to close it</li>
	*<li><B>Step: </B>Open the unified insert image dialog again and open the addtional options</li>
	*<li><B>Step: </B>Select layout 'Centered', check radio '400 pixels wide' and check-box 'Set this layout as the default'</li>
	*<li><B>Step: </B>Click 'Cancel' button to close the dialog and reopen it and addtional options again</li>
	*<li><B>Verify: </B>The selected status of the layout, size, check-box are same as last time</li>
	*<li><B>Step: </B>Switch to 'Web URL' tab, input an invalid url</li>
	*<li><B>Verify: </B>The warning message of the invalid url should appear</li>
	*<li><B>Step: </B>Post the blog</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud", "cnx8ui-level2"})
	public void testUniInstImgDlgWrongUrl() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		testAdmin = cfg.getUserAllocator().getAdminUser();
		ui.loginAndToggleUI(testAdmin, cfg.getUseNewUI());
	
		//Check Gatekeeper value for unified insert image dialog
		String gk_flag = "unify_insert_image_dialog";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		if (!value) { // Only run this case when GK unify_insert_image_dialog is enabled
			log.info("INFO: GK unify_insert_image_dialog is disabled, return this case");
			return;
		}
				
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for "+ testName  + randval)
				.description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London)
				.theme(Theme.Blog_with_Bookmarks)
				.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry1" + Helper.genDateBasedRand()).blogParent(blog)
				.tags(testName + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(ui);

		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add an Entry A");
		log.info("INFO: Add a new entry to the blog");
		blogEntry.newBlogEntry(ui, true);

		//Click insert image button to open the insert image dialog
		logger.strongStep("Verify that Insert Image Dialog is opened");
		log.info("INFO: Verify that Insert Image Dialog is opened");
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertImageButton);
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertImageButton);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgLocalFilesTab);

		//Click Additional Options to open more settings
		logger.strongStep("Change addtional option settings");
		log.info("INFO: Change addtional option settings");
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutCenter);
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgLayoutCenter);
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgLayoutMedSize);
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgLayoutChkDef);

		//Cancel the dialog and reopen
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKECancelButton);
		logger.strongStep("Verify that Insert Image Dialog is reopened");
		log.info("INFO: Verify that Insert Image Dialog is reopened");
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertImageButton);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgLocalFilesTab);

		//Confirm addtional option settings are changed
		logger.strongStep("Confirm addtional option settings are changed");
		log.info("INFO: Confirm addtional option settings are changed");
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel);
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutCenterSelected), "ERROR: Centered layout is not selected");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsCKEImageDlgLayoutMedSize).isSelected(), "ERROR: Medium size is not selected");
		Assert.assertTrue(driver.getSingleElement(BlogsUIConstants.BlogsCKEImageDlgLayoutChkDef).isSelected(), "ERROR: Set as default is not checked");

		//Swtich to Web Url tab
		logger.strongStep("Swtich to Web Url tab");
		log.info("INFO: Swtich to Web Url tab");
		ui.clickLink(BlogsUIConstants.BlogsCKEImageDlgWebUrlTab);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput);

		//Swtich to Web Url tab
		logger.strongStep("Verify invalid url");
		log.info("INFO: Verify invalid url");
		driver.getFirstElement(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput).typeWithDelay("http://abc:def/ghi");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgMessageInvalidURL), "ERROR: Error message doesn't show");

		//Post the blog entry
		blogEntry.postBlogEntry(ui);

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Add an Entry with an Image Embedded to Community Blog</li>
	*<li><B>Step: </B>Create a Community via the API</li>
	*<li><B>Step: </B>Click on Community Actions > Add Apps and add the Blog widget</li>
	*<li><B>Step: </B>Click on Blogs link in left nav</li>
	*<li><B>Step: </B>Click Blogs Actions > Manage Blog</li>
	*<li><B>Step: </B>Click Manage Blog button and upload a file</li>
	*<li><B>Step: </B>From left nav, Click File Uploads and upload a File</li>
	*<li><B>Step: </B>Click on Blogs link in left nav</li>
	*<li><B>Step: </B>Click the New Entry button</li>
	*<li><B>Step: </B>Add the uploaded image to the entry</li>
	*<li><B>Verify: </B>The entry with embedded image was created</li>
	*</ul>
	*/ 
	//Disabled this test case due to existing defect https://jira.cwp.pnp-hcl.com/browse/COMMUNITY-38
	@Test(groups = {"level2", "bvt", "regressioncloud"}, enabled = false)
	public void addComBlogEntryWithImage() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		testAdmin = cfg.getUserAllocator().getAdminUser();
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		List<String> FilesToUpload = new ArrayList<String>();
		
		FilesToUpload.add(Data.getData().file1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
		 										  .tags(testName + Helper.genDateBasedRand())
		 										  .content("Test description for testcase " + testName)
		 										  .useUploadedImage(Data.getData().file1)
		 										  .build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget blog
		logger.strongStep("Add Blog widget");
		log.info("INFO: Add blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		ui.sleep(5000); // sleep 5 seconds to allow Blogs widget addition ready in the background to avoid later failure
		
		//GUI
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		cUI.goToDefaultIamOwnerView(isCardView);

		//navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the blogs link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from the tabbed Navigation menu");
			Community_TabbedNav_Menu.BLOG.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from left Navigation menu");
			Community_LeftNav_Menu.BLOG.select(cUI);
		}
		// sleep 5s
		log.info("INFO: Wait 5 seconds for page to load");
		ui.sleep(5000);
		
		//Navigate to Manage Your Blog
		log.info("INFO: Open blogs action menu");
		logger.strongStep("Navigate to Manage Your Blog");
		// Try low level way to click the link
		WebDriverExecutor wde =  (WebDriverExecutor)ui.getDriver();
		wde.wd().findElement(By.linkText("Blog Actions")).click();
		ui.clickLinkWait(BlogsUIConstants.blogsActionMenu); // Intermittently fails at this point, so try "clickLinkWait" method to see if there is an improvement or not
		log.info("INFO: Select to manage blog");
		ui.clickLinkWait(BlogsUIConstants.blogsManage);
		
		//Upload a file
		logger.strongStep("Upload a file");
		log.info("INFO: Upload "+FilesToUpload.size()+" new file(s)");
		ui.blogsAddFileToUpload(FilesToUpload);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the blogs link in the nav
		if (value)
		{
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from the tabbed Navigation menu");
			Community_TabbedNav_Menu.BLOG.select(cUI, 2);
		}else {
			logger.strongStep("Click on the blogs link in the nav menu");
			log.info("INFO: Select blogs from left Navigation menu");
			Community_LeftNav_Menu.BLOG.select(cUI);
			//ui.clickLinkWait(BlogsUI.blogLeftNav);
		}
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		String gk_flag2 = "unify_insert_image_dialog";
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag2 = "UNIFY_INSERT_IMAGE_DIALOG";	
		} 
		boolean value2 = gkc.getSetting(gk_flag2);
			
		//Add a new entry with image that you just uploaded
		logger.strongStep("Add a new entry with image that you just uploaded");
		log.info("INFO: Add a new entry with an image");
		if (value2) {
			blogEntry.create(ui, value2); // use unified insert image dialog
		} else {
			blogEntry.create(ui);
		}
		
		//Verify that new entry exists
		logger.weakStep("Verify that new entry exists");
		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), "ERROR: Entry with image not found");
		
		//Verify that the entry contains the image
		logger.weakStep("Verify that the entry contains the image");
		log.info("INFO: Verify that the uploaded image is present");
		String imageSelector = "css=div.entryContentContainer img[src$='" +	Data.getData().file1 + "']";
		//No need to check the element is valid here as ui.getFirstVisibleElement asserts it is
		Element imageElement = ui.getFirstVisibleElement(imageSelector);
		String imageURL = imageElement.getAttribute("src");
		Assert.assertNotNull(imageURL, "ERROR: Image in entry not found!");
		
		//Check the image link is valid. This cannot be done in webdriver.
		logger.weakStep("Check if the image link is valid. This cannot be done in webdriver.");
		log.info("INFO: Checking if image link at URL " + imageURL + " is OK");
		int httpResponseCode;
		
		boolean flag1 = TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM");
		boolean flag2 = TestConfigCustom.getInstance().getSecurityType().equalsIgnoreCase("TAM_SPNEGO");
		if(flag1||flag2)
			httpResponseCode = Helper.getResponseCodeHTMLUnit(imageURL, testAdmin.getUid(), testAdmin.getPassword());
		else
			httpResponseCode = Helper.getResponseCode(imageURL);
		
		log.info("INFO: HTTP GET on " + imageURL + " returned code " + httpResponseCode);
		Assert.assertTrue(httpResponseCode == 200, "ERROR: The image cannot be loaded!");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	

	/**
	*<ul>
	*<li><B>Info: </B>Adding an Ideation Blog widget</li>
	*<li><B>Step: </B>Create a community via API</li>
	*<li><B>Step: </B>Click Community Actions > Add Apps</li>
	*<li><B>Step: </B>Add the Ideation Blog widget</li>
	*<li><B>Step: </B>Click on the Ideation Blog link in nav pane</li> 
	*<li><B>Verify: </B>Contribute an Idea button displays in the Ideation Blog</li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "bvt", "bvtcloud", "regressioncloud"})
	public void addIdeationBlogWidget() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
									 .access(defaultAccess)
									 .description("Test Widgets inside community for " + testName)
									 .build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget ideation blog
		logger.strongStep("Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		//GUI
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		adminUser = cfg.getUserAllocator().getAdminUser();
	
		String gk_flag_card = "catalog-card-view";
		boolean isCardView = ui.checkGKSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		cUI.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		log.info("INFO: Verify that Ideation Blog widget contents display on Overview page");
		logger.weakStep("Verify that Ideation Blog widget contents display on Overview page");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.FirstIdeaLink),
				          "ERROR: The widget's create First Idea link not found");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Add a new idea to an ideation Blog</li>
	*<li><B>Step: </B>[API]Create a community</li>
	*<li><B>Step: </B>[API]Click Community Actions > Add Apps</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B>Click on Ideation Blog link in the left nav pane</li> 
	*<li><B>Step: </B>Click on the Contribute an Idea button</li>
	*<li><B>Step: </B>Create a New Idea and save</li>
	*<li><B>Verify: </B>The New Idea was created</li>
	*</ul>
	*/ 
	@Test(groups = {"cplevel2", "level2", "smokeonprem", "bvt", "bvtcloud", "regressioncloud", "smokecloud", "cnx8ui-level2"})
	public void addIdeationBlogNewIdea() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
									 .access(defaultAccess)
									 .description("Test Widgets inside community for " + testName)
									 .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("Test Content for " + testName)
														 .build();	

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget ideation blog
		logger.strongStep("Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		//GUI
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		boolean isCardView = ui.checkGKSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Ideation blogs link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from the tabbed nav menu");
			Community_TabbedNav_Menu.IDEATIONBLOG.select(cUI,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from left nav menu");
			Community_LeftNav_Menu.IDEATIONBLOG.select(cUI);
		}
		
		//click on ideation blog
		logger.strongStep("Click on Ideation Blog");
		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait("css=table[dojoattachpoint='tableListAP'] h4[class='lotusBreakWord']>a:contains(" + community.getName() + ")");
		
		//select New Idea button
		logger.strongStep("Select New Idea button");
		log.info("INFO: Select New Entry button");
		ui.clickLink(BlogsUIConstants.NewIdea);
		
		//Create a new idea
		logger.strongStep("Create a new idea");
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(ui);
		
		//Verify that new idea exists
		logger.weakStep("Verify that new idea exists");
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), "ERROR: Entry not found"); 
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
}
