package com.ibm.conn.auto.tests.blogs;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_BoschUAT_Blogs extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Blogs.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private BlogsUI blogsUi;
	private CommunitiesUI comUi;
	private  APICommunitiesHandler comApiOwner; 
	private APIBlogsHandler blogApiOwner;
	private Blog standaloneBlog;
	private BaseBlog baseBlog;
	private BaseBlogPost baseBlogPost;
	private User testUserA,testUserB;
	private String serverURL;
	private BaseCommunity.Access defaultAccess;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUserA = cfg.getUserAllocator().getUser(this);
		testUserB = cfg.getUserAllocator().getUser(this);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		comApiOwner = new APICommunitiesHandler(serverURL, testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		blogApiOwner = new APIBlogsHandler(serverURL, testUserB.getAttribute(cfg.getLoginPreference()),testUserB.getPassword());
		baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUserB, blogApiOwner, baseBlog);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpMethod() {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();

		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		blogsUi = BlogsUI.getGui(cfg.getProductName(), driver);
		comUi = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Create a Blog, Blog Entry And Update Blog entry. Verify the Blog entry events in My Profile Recent Updates</li>
	 *<li><B>Step:</B> Login to application and Create a Blog</li> 
	 *<li><B>Step:</B> Create Blog Entry</li>
	 *<li><B>Verify:</B> Verify the Create Blog entry event in My Profile Recent Updates.</li>
	 *<li><B>Step:</B> Update Blog Entry</li>
	 *<li><B>Verify:</B> Verify the Update Blog entry event in My Profile Recent Updates.</li>
	 *<li><B>Step:</B> Delete Blog Entry</li>
	 *<li><B>Verify:</B> Verify the Blog entry events are not displayed in My Profile Recent Updates.</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateEditEntriesAppearInTheMyProfileActivityStream() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	

		String testName = ui.startTest();

		String randval = Helper.genDateBasedRandVal();

		BaseBlog blog = new BaseBlog.Builder("test" + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for "+testName  + randval)
				.description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London)
				.theme(Theme.Blog_with_Bookmarks)
				.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();

		//Storing content value now and update it later for tying updated text in ckEditor
		String content = blogEntry.getContent();

		// Load the component and login
		log.info("Load HomePage and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load HomePage and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUserA);

		log.info("INFO: Select the Blogs 'Mega Menu' option");
		logger.strongStep("Select the Blogs 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsOption);

		log.info("Create new blog" + blog.getName() +"and verify the same");
		logger.strongStep("Create new blog" + blog.getName() +"and verify the same");
		blog.create(blogsUi);

		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		blogsUi.clickLink(BlogsUIConstants.createdBlogEntryLink.replace("PLACEHOLDER",blog.getName()));

		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		blogsUi.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		logger.strongStep("Add a new Entry");
		log.info("INFO: Add the new entry");
//		blogsUi.clickLink(BlogsUI.BlogsNewEntry);
		blogEntry.create(blogsUi);

		logger.strongStep("INFO: Load the My Profile view");
		log.info("INFO: Load the My Profile view");
		ui.clickLinkWait(HomepageUIConstants.homepage);
		ui.clickLink(ProfilesUIConstants.UserMenu);
		ui.clickLink(ProfilesUIConstants.MyProfile);

		log.info("Verify that the created Blog entry event is displayed in all views" );
		logger.strongStep("Verify that the created Blog entry event is displayed in all views");
		String newsStory = ui.replaceNewsStory(Data.CREATE_BLOG_ENTRY, blogEntry.getTitle() , blog.getName(),testUserA.getDisplayName());
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);

		log.info("INFO: Select the Blogs Latest entries 'Mega Menu' option");
		logger.strongStep("Select the Blogs Latest Entries 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsLatestEntries);

		log.info("INFO: Click on Created Blog Entry");
		logger.strongStep("INFO: Click on Created Blog Entry");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogEntryLink.replace("PLACEHOLDER", blogEntry.getTitle()));
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsEditEntry);

		log.info("INFO: Type updated value in ckEditor Text box and click on Post");
		logger.strongStep("INFO: Type updated value in ckEditor Text box and click on Post");
		blogsUi.clearCkEditor();
		content = content.replace("testcase", "updatedTestcase");
		blogsUi.typeNativeInCkEditor(content);
		blogsUi.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);

		logger.strongStep("INFO: Load the My Profile view");
		log.info("INFO: Load the My Profile view");
		ui.clickLinkWait(HomepageUIConstants.homepage);
		ui.clickLink(ProfilesUIConstants.UserMenu);
		ui.clickLink(ProfilesUIConstants.MyProfile);

		log.info("Verify that the updated Blog entry event is displayed in all views" );
		logger.strongStep("Verify that the updated Blog entry event is displayed in all views");
		newsStory = ui.replaceNewsStory(Data.UPDATE_BLOG_ENTRY, blogEntry.getTitle() , blog.getName(),testUserA.getDisplayName());
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);

		log.info("Verify that the updated Blog entry event has updated comment underneath it." );
		logger.strongStep("Verify that the updated Blog entry event has updated comment underneath it.");
		Assert.assertTrue(ui.getFirstVisibleElement(HomepageUIConstants.commentsInsideEvent).getText().equals(content));

		log.info("INFO: Select the Blogs Latest entries 'Mega Menu' option");
		logger.strongStep("Select the Blogs Latest Entries 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsLatestEntries);

		log.info("INFO: Click on Created Blog Entry");
		logger.strongStep("INFO: Click on Created Blog Entry");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogEntryLink.replace("PLACEHOLDER", blogEntry.getTitle()));

		logger.strongStep("Delete the blog entry using 'Manage Blog' link");
		log.info("INFO: Delete the blog entry using 'Manage Blog' link");
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsSettings);
		ui.clickLinkWait(BlogsUIConstants.entriesLinkLeftNav);
		ui.clickLinkWithJavascript(BlogsUI.getBlogPost(blogEntry));
		ui.clickLinkWait(BlogsUIConstants.deleteEntryButton);
		ui.clickLinkWait(BlogsUIConstants.deleteEntryOKButton);

		logger.strongStep("Verify that Blog entry is not displayed now");
		log.info("Verify that Blog entry is not displayed now");
		driver.changeImplicitWaits(5);
		Assert.assertFalse(blogsUi.isElementVisible(BlogsUIConstants.BlogEntryLink.replace("PLACEHOLDER", blogEntry.getTitle())));
		driver.turnOnImplicitWaits();
		
		logger.strongStep("INFO: Load the My Profile view");
		log.info("INFO: Load the My Profile view");
		ui.clickLinkWait(HomepageUIConstants.homepage);
		ui.clickLink(ProfilesUIConstants.UserMenu);
		ui.clickLink(ProfilesUIConstants.MyProfile);

		log.info("Verify that the created Blog entry event is not displayed in all views" );
		logger.strongStep("Verify that the created Blog entry event is displayed in all views");
		newsStory = ui.replaceNewsStory(Data.CREATE_BLOG_ENTRY, blogEntry.getTitle() , blog.getName(),testUserA.getDisplayName());
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, false);

		log.info("Verify that the updated Blog entry event is not displayed in all views" );
		logger.strongStep("Verify that the updated Blog entry event is not displayed in all views");
		newsStory = ui.replaceNewsStory(Data.UPDATE_BLOG_ENTRY, blogEntry.getTitle() , blog.getName(),testUserA.getDisplayName());
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, false);

	}

	/**
	*<ul>
	*<li><B>Info: </B> Blog Basic UI Test Case</li>
	*<li><B>Step: </B>Load blogs component and login</li>
	*<li><B>Step: </B>Create a new blog</li>
	*<li><B>Verify: </B>Verify new blog creation message section is displayed</li>
	*<li><B>Step: </B>Click on  New Entry button</li>
	*<li><B>Step: </B>Add a new entry to the blog</li>
	*<li><B>Step: </B>Click on 'My Blog' link</li>
	*<li><B>Step: </B>Open the blog</li>
	*<li><B>Verify: </B>Verify the presence of 'New Entry' button</li>
	*<li><B>Verify: </B>Verify and Click on 'Entry Name' Link</li>
	*<li><B>Verify: </B>Verify Entry Page Components</li>
	*<li><B>Verify: </B>Add a comment and Verify comment options</li>
	*<li><B>Verify: </B>Verify that the comment is present</li>
	*</ul>
	*Notes: This is not supported on the cloud.
	*/
	@Test(groups = { "regression"})
	public void blogBasicUI() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for " + testName + randval).description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London).theme(Theme.Blog_with_Bookmarks).build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand()).blogParent(blog)
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName).build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUserA.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUserA);

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(blogsUi);

		logger.strongStep("Verify new blog creation message section is displayed");
		log.info("INFO: Verfiy new blog creation message section is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.msgBody),
				"ERROR: New Blog creation message section is not displayed");

		// select New Entry Link
		logger.strongStep("Click on  New Entry button");
		log.info("INFO: CLick on  New Entry button");
		driver.getFirstElement(BlogsUIConstants.BlogsNewEntryButton.replace("PLACEHOLDER", blog.getName())).click();

		logger.strongStep("Add a new entry to the blog");
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogsUi);
		
		logger.strongStep("Click on 'My Blog' link");
		log.info("INFO: Click on 'My Blog' link");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);

		//open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());

		logger.strongStep("Verify the presence of 'New Entry' button");
		log.info("INFO: Verify the presence of 'New Entry' button");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.blogsNewEntryMenuItem), "ERROR: New Entry button is not displayed");

		logger.strongStep("Verify and Click on 'Entry Name' Link");
		log.info("INFO: Verify and Click on 'Entry Name' Link");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.entryNameLink), "ERROR: Entry Name link is not displayed");
		ui.clickLink(BlogsUIConstants.entryNameLink);

		logger.strongStep("Verify Entry Page Components");
		log.info("INFO: Verify EntryPage Components");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.blogsNewEntryMenuItem), "ERROR: 'New Entry' button is not displayed");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.viewAllLinksBtn),"ERROR: 'View All Links' button is not displayed");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.BlogsAddACommentLink), "ERROR: 'Add a comment' link is not displayed");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.editLink), "ERROR: 'Edit' link is not displayed");
		Assert.assertTrue(ui.isElementPresent(BlogsUIConstants.BlogsMoreActions), "ERROR: 'MoreActions' link is not displayed");

		// Add a comment
		logger.strongStep("Add a comment and Verify Text box options");
		log.info("INFO: Add a comment to the entry and Verify Text box options");
		comment.create(blogsUi);

		// Verify that the comment is present
		logger.strongStep("Verify that the comment is present");
		log.info("INFO: Verify that the comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment.getContent()), "ERROR: Comment not found");

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify HTML editor in community's Ideation Blog entry</li>
	 *<li><B>Step:</B> [API]Create community using API</li> 
	 *<li><B>Step:</B> Login to communities component</li>
	 *<li><B>Step:</B> [API]Add 'Ideation Blog' to community</li>
	 *<li><B>Step:</B> Navigate to community created above</li>
	 *<li><B>Step:</B> Select 'Ideation Blog' from top nav bar menu</li>
	 *<li><B>Step:</B> Select the default ideation blog link</li>
	 *<li><B>Step:</B> Select 'New Idea' button</li>
	 *<li><B>Step:</B> Select HTML source tab</li>
	 *<li><B>Step:</B> Enter a HTML tag with text</li>
	 *<li><B>Step:</B> Switch to Rich Text tab</li>
	 *<li><B>Verify:</B> Verify that entered text should be displayed correctly in Rich Text editor</li>
	 *<li><B>Step:</B> Switch back to HTML source tab</li>
	 *<li><B>Verify:</B>Verify that entered text should be displayed correctly in HTML editor</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyHTMLEditorInCommIdeationBlogEntry() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String HTMLText = "<p dir =\"ltr\">This is test</p>";
		String RichText = "This is test";

		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(com.ibm.conn.auto.appobjects.base.BaseCommunity.Access.PUBLIC)
				.description("Test description for testcase " + testName).build();

		// create community using API
		log.info("INFO: Create community using API");
		logger.strongStep(" Create community using API");
		Community comAPI = baseCommunity.createAPI(comApiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCommunity.getCommunityUUID_API(comApiOwner, comAPI);
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserB);

		// add widget IDEATION BlOG
		logger.strongStep("Add 'Ideation Blog' to community");
		baseCommunity.addWidgetAPI(comAPI, comApiOwner, BaseWidget.IDEATION_BLOG);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		baseCommunity.navViaUUID(comUi);

		// Select 'Select 'Ideation Blog' from top nav bar menu
		logger.strongStep("Select 'Ideation Blog' from top nav bar menu");
		log.info("INFO: Select 'Ideation Blog' from top nav bar menu");
		//Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui,2);

		// Open Ideation blog
		logger.strongStep("Select the default ideation blog link");
		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWithJavascript(blogsUi.getCommIdeationBlogLink(baseCommunity));
		ui.fluentWaitPresent(BlogsUIConstants.NewIdea);

		// Select New Idea
		logger.strongStep("Select 'New Idea' button");
		log.info("INFO: Select 'New Idea' button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);

		// Select HTML Source tab
		logger.strongStep("Select HTML source tab");
		log.info("INFO: Select HTML source tab");
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);

		// Enter HTML tag with text
		logger.strongStep("Enter a HTML tag with text");
		log.info("INFO: Enter a HTML tag with text: " + HTMLText);
		ui.typeText(BlogsUIConstants.HTMTTextArea, HTMLText);

		// Switch to Rich Text tab
		logger.strongStep("Switch to Rich Text tab");
		log.info("INFO: Switch to Rich Text tab");
		ui.clickLinkWait(BlogsUIConstants.Rich_Text_Tab);

		// Verify entered text in Rich Text Tab
		logger.strongStep("Verify that entered text should be displayed correctly in Rich Text editor");
		log.info("INFO: Verify that entered text should be displayed correctly in Rich Text editor: " + RichText);
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		log.info(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText());
		Assert.assertEquals(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText(), RichText);
		ui.switchToTopFrame();

		// Switch back to HTML source tab
		logger.strongStep("Switch back to HTML source tab");
		log.info("INFO: Switch back to HTML source tab");
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);

		// Verify entered text in HTML Source Tab
		logger.strongStep("Verify that entered text should be displayed correctly in HTML editor");
		log.info("INFO: Verify that entered text should be displayed correctly in HTML editor: " + HTMLText);
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.HTMTTextArea).getAttribute("value").trim(),"<p dir=\"ltr\">This is test</p>");

		// Delete community
		comApiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify HTML editor in standalone Blog entry</li>
	 *<li><B>Step:</B> Login to Blogs component</li>
	 *<li><B>Step:</B> [API] Create blog post using API</li>
	 *<li><B>Step:</B> Select 'Blog' link</li>
	 *<li><B>Step:</B> Select and edit 'Blog Entry' link</li>
	 *<li><B>Step:</B> Select HTML source tab</li>
	 *<li><B>Step:</B> Enter a HTML tag with text</li>
	 *<li><B>Step:</B> Switch to Rich Text tab</li>
	 *<li><B>Verify:</B> Verify that entered text should be displayed correctly in Rich Text editor</li>
	 *<li><B>Step:</B> Switch back to HTML source tab</li>
	 *<li><B>Verify:</B>Verify that entered text should be displayed correctly in HTML editor</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyHTMLEditorInStandaloneBlogEntry() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String HTMLText = "<p dir =\"ltr\">This is test</p>";
		String RichText = "This is test";
		ui.startTest();

		// Login to Blogs component
		logger.strongStep("Login to Blogs component");
		log.info("INFO: Login to Blogs component");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUserB);

		// Create blog post using API
		logger.strongStep("Create blog post using API");
		log.info("INFO: Create blog post using API");
		BlogEvents.createBlogPost(testUserB, blogApiOwner, baseBlogPost, standaloneBlog);

		// Select 'Blog' link
		logger.strongStep("Select 'Blog' link");
		log.info("INFO: Select 'Blog' link");
		ui.clickLinkWait(BlogsUI.getBlog(baseBlog));

		// Select and edit 'Blog Entry' link
		logger.strongStep("Select and edit 'Blog Entry' link");
		log.info("INFO: Select and edit 'Blog Entry' link");
		ui.clickLinkWait(BlogsUI.getBlogPost(baseBlogPost));
		ui.clickLinkWait(BlogsUIConstants.editLink);

		// Select HTML Source tab
		logger.strongStep("Select HTML source tab");
		log.info("INFO: Select HTML source tab");
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);

		// Enter HTML tag with text
		logger.strongStep("Enter a HTML tag with text");
		log.info("INFO: Enter a HTML tag with text: " + HTMLText);
		driver.getSingleElement(BlogsUIConstants.HTMTTextArea).clear();
		ui.typeText(BlogsUIConstants.HTMTTextArea, HTMLText);

		// Switch to Rich Text tab
		logger.strongStep("Switch to Rich Text tab");
		log.info("INFO: Switch to Rich Text tab");
		ui.clickLinkWait(BlogsUIConstants.Rich_Text_Tab);

		// Verify entered text in Rich Text Tab
		logger.strongStep("Verify that entered text should be displayed correctly in Rich Text editor");
		log.info("INFO: Verify that entered text should be displayed correctly in Rich Text editor: " + RichText);
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		log.info(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText());
		Assert.assertEquals(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText(), RichText);

		// Switch back to HTML source tab
		logger.strongStep("Switch back to HTML source tab");
		log.info("INFO: Switch back to HTML source tab");
		ui.switchToTopFrame();
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);

		// Verify entered text in HTML Source Tab
		logger.strongStep("Verify that entered text should be displayed correctly in HTML editor");
		log.info("INFO: Verify that entered text should be displayed correctly in HTML editor: " + HTMLText);
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.HTMTTextArea).getAttribute("value").trim(),"<p dir=\"ltr\">This is test</p>");

		blogApiOwner.deleteBlog(standaloneBlog);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Blog Tags View on Cloud and List</li>
	*<li><B>Step: </B>Load component and login</li>
	*<li><B>Step: </B>Create a new blog</li>
	*<li><B>Step: </B>Open the blog</li>
	*<li><B>Step: </B>Select New Entry button</li>
	*<li><B>Verify: </B>Add a new Entry</li>
	*<li><B>Verify: </B>View the added Tag in Cloud section</li>
	*<li><B>Verify: </B>View the added Tag in List section</li>
	*</ul>
	*/
	@Test(groups = { "regression"})
	public void blogsTagsonCloudandListView() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for " + testName + randval).description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London).theme(Theme.Blog_with_Bookmarks).build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand()).blogParent(blog)
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName).build();

		//Load component and login
		logger.strongStep("Open blogs and login: " + testUserA.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUserA);

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(blogsUi);
		
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
	//	ui.clickLink(BlogsUI.BlogsNewEntry);
		blogEntry.create(blogsUi);

		logger.strongStep("View the added Tag in Cloud section");
		log.info("INFO: View the added Tag in Cloud section");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.cloudTagView));
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(blogEntry.getTags()),"ERROR: Added Tag is not dispalyed");
		
		logger.strongStep("View the added Tag in List section");
		log.info("INFO: View the added Tag in List section");
		ui.clickLink(BaseUIConstants.listTagView);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(blogEntry.getTags()),"ERROR: Added Tag is not dispalyed");

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in Blogs Entry Comment</li>
	 *<li><B>Step:</B> Login to application and Create a Blog</li> 
	 *<li><B>Step:</B> Create Blog Entry</li>
	 *<li><B>Step:</B> Add a comment with Mentions to the created Blog Entry and Save the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateCommentsWithMentionsInBlogEntry() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());	

		String testName = ui.startTest();

		String randval = Helper.genDateBasedRandVal();

		BaseBlog blog = new BaseBlog.Builder("test" + randval, Data.getData().BlogsAddress1 + randval)
				.tags("Tag for "+testName  + randval)
				.description("Test description for testcase " + testName)
				.timeZone(Time_Zone.Europe_London)
				.theme(Theme.Blog_with_Bookmarks)
				.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();
		

		// Load the component and login
		log.info("INFO : Load HomePage and Log In as " + testUserA.getDisplayName());
		logger.strongStep("INFO : Load HomePage and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUserA);

		log.info("INFO: Select the Blogs 'Mega Menu' option");
		logger.strongStep("INFO : Select the Blogs 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(BlogsUIConstants.blogsOption);

		log.info("INFO : Create new blog" + blog.getName());
		logger.strongStep("INFO : Create new blog" + blog.getName());
		blog.create(blogsUi);

		logger.strongStep("INFO : Open the blog");
		log.info("INFO: Open blog");
		blogsUi.clickLink(BlogsUIConstants.createdBlogEntryLink.replace("PLACEHOLDER",blog.getName()));

		logger.strongStep("INFO : Select New Entry button");
		log.info("INFO: Select New Entry button");
		blogsUi.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		logger.strongStep("INFO : Add a new Entry");
		log.info("INFO: Add the new entry");
	//	blogsUi.clickLink(BlogsUI.BlogsNewEntry);
		blogEntry.create(blogsUi);
		
		logger.strongStep("INFO: Select the Add a comment link for entry");
		log.info("INFO: Select the Add a comment link for entry");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		blogsUi.typeMentionInCkEditor("Hello @"+testUserB.getDisplayName());
	
		logger.strongStep("INFO: Click On Submit Button");
		log.info("INFO: Click On Submit Button");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		//Verify that the comment is present
		logger.strongStep("Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(blogsUi.fluentWaitTextPresent("Hello @"+testUserB.getDisplayName()),
						  "ERROR: Comment not found");

		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", "@"+testUserB.getDisplayName())),
				  "ERROR: Mention link not present");
		ui.endTest();	
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify the Mention comments in Ideation Blog</li>
	*<li><B>Step: </B>Create a community and add Ideation Blog widget via API</li>
	*<li><B>Step: </B>Click Community Actions > Add Apps</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B>Click on Ideation Blog link in the left nav pane</li> 
	*<li><B>Step: </B>Create a New Idea and save</li>
	*<li><B>Verify: </B>The New Idea was created</li>
	*<li><B>Step: </B>Add a comment with Mention in New Idea and save</li>
	*<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	*</ul>
	*/ 
	@Test(groups = {"regression"})
	public void verifyCreateCommentsWithMentionsIdeationBlog() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
									 .access(defaultAccess)
									 .description("Test Widgets inside community for " + testName)
									 .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("Test Content for " + testName)
														 .build();	

		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("INFO: Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		logger.strongStep("INFO: Open blogs and login: " + testUserA.getDisplayName());
		log.info("INFO: Open blogs and login: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		logger.strongStep("INFO: Navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUi);	

		logger.strongStep("Click on the Ideation Blogs link in the nav");
		log.info("INFO: Select Ideation blogs from left nav menu");
		///Community_LeftNav_Menu.IDEATIONBLOG.select(comUi);
		Community_TabbedNav_Menu.IDEATIONBLOG.select(comUi,2);
		

		logger.strongStep("INFO: Click on Ideation Blog");
		log.info("INFO: Select the default ideation blog link");
		blogsUi.clickLinkWait(blogsUi.getCommIdeationBlogLink(community));
		
		logger.strongStep("INFO: Select New Idea button");
		log.info("INFO: Select New Entry button");
		ui.clickLink(BlogsUIConstants.NewIdea);
		
		logger.strongStep("INFO: Create a new idea");
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogsUi);
		
		logger.strongStep("INFO: Verify that new idea exists");
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), "ERROR: Entry not found"); 
		
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		blogsUi.typeMentionInCkEditor("@"+testUserB.getDisplayName());
		blogsUi.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(blogsUi.fluentWaitTextPresent("@"+testUserB.getDisplayName()),
						  "ERROR: Comment not found");
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", "@"+testUserB.getDisplayName())),
				  "ERROR: Mention link not present");

		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}

}
