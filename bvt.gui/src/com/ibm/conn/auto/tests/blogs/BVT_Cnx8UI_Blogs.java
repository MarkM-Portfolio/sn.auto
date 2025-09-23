
package com.ibm.conn.auto.tests.blogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.BlogsUICnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_Cnx8UI_Blogs extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Blogs.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private BlogsUICnx8 ui;
	CommonUICnx8 commonUI;
	private SearchAdminService adminService;
	private User testUser,testUserAddedToITM,searchAdmin;
	private String serverURL;
	private ItmNavCnx8 itmNavCnx8;
	private APIBlogsHandler apiBlogOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		itmNavCnx8 = new ItmNavCnx8(driver);
		apiBlogOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new BlogsUICnx8(driver);
		commonUI = new CommonUICnx8(driver);
		cnxAssert = new Assert(log);
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Start a Blog button and Form on clicking button</li>
	 *<li><B>Step:</B> Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Verify:</B> Start a Blog button is visible</li>
	 *<li><B>Step:</B> Click on Start a Blog button</li>
	 *<li><B>Verify:</B> Start a Blog entry form is open</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T757</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyStartBlogBtnAndBlogEntryForm() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());		

		logger.strongStep("Verify: Start a Blog button is visible");
		log.info("INFO: Verify Start a Blog button is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(
				ui.createByFromSizzle(BlogsUIConstants.StartABlog), 2), "Start a Blog button is visible.");
		
		logger.strongStep("Click on Start a Blog link");
		log.info("INFO: Clicking on Start a Blog link");
		ui.clickLinkWaitWd(ui.createByFromSizzle(BlogsUIConstants.StartABlog), 3, "Start a Blog Link");
		
		logger.strongStep("Verify: Blog entry Form is visible");
		log.info("INFO: Verify blog entry Form is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.blogEntryForm), 5), "Blog entry Form is visible");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify clicking on the filter icon of a person on the ITM bar from blogs should show blogs belonging to that user </li>
	 *<li><B>Prereq:</B>[API] testUserAddedToITM create blog </li>
	 *<li><B>Step:</B> Login to Blogs with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user navigates to page with URL Server_URL/blogs/roller-ui/allblogs?userid=${USER_ID}</li>
	 *<li><B>Verify:</B> Verify that blogs belonging to the testUserAddedToITM whose filter icon is clicked should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyClickingPersonFilterFromBlogs() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		APIBlogsHandler apiBlogOwner = new APIBlogsHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		APIProfilesHandler profilesAPIUser = new APIProfilesHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		
		String testName = itmNavCnx8.startTest();
		String uid = profilesAPIUser.getUUID();
		
		BaseBlog blog = new BaseBlog.Builder("TestBlog" + Helper.genDateBasedRandVal(), testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.description("Blogs Test description for testcase " + testName).build();

		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser.getDisplayName());
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		itmNavCnx8.loadComponent(Data.getData().ComponentBlogs);
		itmNavCnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());

		// Adding user to ITM if it is already not added
		logger.strongStep("Add user in ITM and Click on filter icon associated with user in ITM");
		itmNavCnx8.addUserToITMAndClickFilterIcon(testUserAddedToITM);
		
		log.info("INFO: Verify that "+testUserAddedToITM.getDisplayName() +" blog page is opened");
		logger.strongStep("Verify that "+testUserAddedToITM.getDisplayName() +" blog page is opened");	
		String expectedUrl = Data.getData().myBlogsUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid);
		cnxAssert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase()),"User navigates to "+expectedUrl);
		
		log.info("Verify that blog belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		logger.strongStep("Verify that blog belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		cnxAssert.assertTrue(itmNavCnx8.isElementVisibleWd(By.xpath(BlogsUICnx8.getBlogLink(blog)),4),"Blog is displayed");
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		itmNavCnx8.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Links availability on Public Blogs page</li>
	 *<li><B>Step:</B> Login to Homepage and Toggle to the new UI</li>
	 *<li><B>Step:</B> Navigate to Blogs from navigation menu</li>
	 *<li><B>Step:</B> Click on Public Blog tab</li>
	 *<li><B>Verify:</B> Latest Blog Entries link is visible on public blog page</li>
	 *<li><B>Verify:</B> Blogs Listing link is visible on public blog page</li>
	 *<li><B>Verify:</B> My Likes/Votes link is visible on public blog page</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T758</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyPublicBlogsPage() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Homepage, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Select Blogs in nav menu");
		log.info("INFO: Select Blogs in nav menu");
		AppNavCnx8.BLOGS.select(ui);
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 6);
		
		logger.strongStep("Verify Public Blogs page with links 'Latest Blog Entries', 'Blogs listing' and 'My Likes/Votes' in ternary navigation are displayed");
		log.info("INFO: Verify Public Blogs page with links 'Latest Blog Entries', 'Blogs listing' and 'My Likes/Votes' in ternary navigation are displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.blogsListingNav), 5), "Blogs Listing link is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.latestBlogEntryNav), 5), "Link Latest Blog Entries is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.myLikesNav), 5), "My Likes/Votes link is visible");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Links availability on My Updates page</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify 'My Blogs' is selected in ternary navigation</li>
	 *<li><B>Step:</B> Click on My updates tab</li>
	 *<li><B>Verify:</B> Recent Entries and Comments link is visible on My updates page</li>
	 *<li><B>Verify:</B> Notification Received link is visible on My updates page</li>
	 *<li><B>Verify:</B> Notification Sent link is visible on My updates page</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T760</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T759</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyMyUpdatesPage() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Verify 'My Blogs' is selected in ternary navigation");
		log.info("INFO: Verify 'My Blogs' is selected in ternary navigation");
		cnxAssert.assertEquals(ui.getElementTextWd(By.cssSelector(BlogsUIConstants.SelectedNavTab)), "My Blogs", "My Blogs is selected in navigation tab");
		
		logger.strongStep("Click on My updates tab");
		log.info("INFO: Click on My updates tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.myUpdatesTab), 6);
		
		logger.strongStep("Verify My updates page with links 'Recent Entries and Comments', 'Notification Received' and 'Notification Sent' in ternary navigation are displayed");
		log.info("INFO: Verify My updates page with links 'Recent Entries and Comments', 'Notification Received' and 'Notification Sent' in ternary navigation are displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.recentEntryNav), 5), "Recent Entries link is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.notificationReceiveNav), 5), "Link Notification Received is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.notificationSentNav), 5), "Notification Sent link is visible");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify Links availability on Administration page</li>
	 *<li><B>Step:</B> Load & Login to Blogs with admin user and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Administration tab</li>
	 *<li><B>Verify:</B> Configuration link is visible on Administration page</li>
	 *<li><B>Verify:</B> Administer Users link is visible on Administration page</li>
	 *<li><B>Verify:</B> Blogs Homepage Theme link is not displayed on Administration page</li>
	 *<li><B>Step:</B> Click on Configuration link in ternary navigation</li>
	 *<li><B>Verify:</B> Verify 'Theme Settings' is not displayed in configuration page</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T761</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyAdministrationPage() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Blogs, Log in with Admin user and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in with Admin user and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(searchAdmin, cfg.getUseNewUI());
		
		logger.strongStep("Click on Administration tab");
		log.info("INFO: Click on Administration tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.administrationTab), 6);
		
		logger.strongStep("Verify Administration page with links 'Configuration', 'Administer Users' are Displayed in ternary navigation");
		log.info("INFO: Verify Administration page with links 'Configuration', 'Administer Users' are Displayed in ternary navigation");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.configurationNav), 5), "Configuration link is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.administerUsersNav), 5), "Link Administer Users is visible");
		
		logger.strongStep("Verify 'Blogs Homepage Theme' not displayed in ternary navigation");
		log.info("INFO: Verify 'Blogs Homepage Theme' not displayed in ternary navigation");
		cnxAssert.assertFalse(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.blogHomepageThemeNav), 5), "Blogs Homepage Theme link is not displayed");
		
		logger.strongStep("Click on Configuration link in ternary navigation");
		log.info("INFO: Click on Configuration link in ternary navigation");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.configurationNav), 6);
		
		logger.strongStep("Verify 'Theme Settings' is not displayed in configuration page");
		log.info("INFO: Verify 'Theme Settings' is not displayed in configuration page");
		cnxAssert.assertFalse(ui.isElementVisibleWd(By.xpath(BlogsUIConstants.administration_settings.replace("PLACEHOLDER", "Theme Settings")), 5), "Theme Settings is not displayed");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test to verify blogs tags</li>
	 *<li><B>Step:</B> Create a new blog via API</li>
	 *<li><B>Step:</B> Create a blog entry via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Step:</B> Click on List link under Tags section</li>
	 *<li><B>Verify:</B> Verify tags are visible along with the number inside tag chip</li>
	 *<li><B>Step:</B> Click on Cloud link under Tags section</li>
	 *<li><B>Step:</B> Click on added tag in created blog entry</li>
	 *<li><B>Verify:</B> Verify tag related blogs entry are displayed</li>
	 *<li><B>Step:</B> Remove selected tags clicking on remove icon</li>
	 *<li><B>Verify:</B> Verify no tags are selected now</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T770</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void clickBlogsTag() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String blogEntryName = testName + Helper.genDateBasedRand();
		User testUser = cfg.getUserAllocator().getUser();
		APIBlogsHandler apiBlogOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(blogEntryName)
				 								.tags("tag" + Helper.genDateBasedRandVal3())
				 								.content("Test description for testcase " + testName)
				 								.build();
		
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Create a blog entry");
		log.info("INFO: Create a blog entry");
		blogEntry.createAPI(apiBlogOwner, blogAPI);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		
		logger.strongStep("Select list view from tag section");
		log.info("INFO: Select list view from tag section");
		ui.scrollToElementWithJavaScriptWd(By.xpath(BlogsUIConstants.tagsListLink));
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.tagsListLink), 6);
		
		logger.strongStep("Verify tags are visible with tag count");
		log.info("INFO: Verify tags are visible with tag count");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.cssSelector(BlogsUIConstants.listTagCount)), "tag number is visible");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.cssSelector(BlogsUIConstants.listTagName)), "tag name is visible");
		
		logger.strongStep("Select Cloud tag from tag section");
		log.info("INFO: Select Cloud tag from tag section");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.tagsCloudLink), 6);
		ui.scrollToElementWithJavaScriptWd(By.xpath(BlogsUIConstants.tagsCloudLink));
		
		logger.strongStep("Click on a tag "+ blogEntry.getTags() +" added in blog entry");
		log.info("INFO: Click on a tag "+ blogEntry.getTags() +" added in blog entry");
		WebElement cloudTag = ui.findElement(By.cssSelector(BlogsUIConstants.cloudTagName.replace("PLACEHOLDER", blogEntry.getTags())));
		ui.clickLinkWithJavaScriptWd(cloudTag);
		
		logger.strongStep("Verify Created Entry is displayed in public blog tab");
		log.info("INFO: Verify Created Entry is displayed in public blog tab");
		cnxAssert.assertTrue(ui.getFirstVisibleElement("link=" + blogEntryName).isVisible(), "Entry is visible");
		
		logger.strongStep("Remove selected tag by clicking on tag remove icon");
		log.info("INFO: Remove selected tag by clicking on tag remove icon");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.tagRemove.replace("PLACEHOLDER", blogEntry.getTags())));
		WebElement tagRemoveEle = ui.findElement(By.cssSelector(BlogsUIConstants.tagRemove.replace("PLACEHOLDER", blogEntry.getTags())));
		ui.clickLinkWithJavaScriptWd(tagRemoveEle);
		
		logger.strongStep("Verify tag is not selected");
		log.info("INFO: Verify tag is not selected");
		ui.waitForElementInvisibleWd(By.cssSelector(BlogsUIConstants.tagSelection.replace("PLACEHOLDER", blogEntry.getTags())), 4);
		cnxAssert.assertFalse(ui.isElementPresentWd(By.cssSelector(BlogsUIConstants.tagSelection.replace("PLACEHOLDER", blogEntry.getTags()))), "Tag is removed");
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to verify the pagination bar in public blogs</li>
	 *<li><B>Step:</B> Create a Blog via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Step:</B> Add multiple blog entries if entries are less than per page value 10</li>
	 *<li><B>Step:</B> Change result per page value to 10</li>
	 *<li><B>Verify:</B> Verify Result entry counts are correct</li>
	 *<li><B>Step:</B> Click on next and previous page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to next and previous page</li>
	 *<li><B>Step:</B> Click on Last and first page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to last and first page</li>
	 *<li><B>Step:</B> Delete Blog</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T775</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void publicBlogsPagination() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(testName + Helper.genDateBasedRandVal())
												.tags("tag" + Helper.genDateBasedRandVal3())
												.content("Test description for testcase " + testName)
												.build();
		
		logger.strongStep("Create a blog via API");
		log.info("INFO: Creating a blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		
		// Fetching Blog entries count
		int entryCount = ui.findElements(By.cssSelector(BlogsUIConstants.entryListCount)).size();
		
		// Creating Blog entries if entries is less than or equals to per page value 10 to enable page navigation in pagination
		if(entryCount <= 10) {
			log.info("INFO: Creating blog entries via API");
			for(int i = entryCount; i <= 11; i++) {
				blogEntry.createAPI(apiBlogOwner, blogAPI);
			}
			
			log.info("INFO: Running Search indexer for blogs");
			adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
			
			driver.navigate().refresh();
		}
		
		logger.strongStep("Changes result per page value to 10 in pagination bar");
		log.info("INFO: Changes result per page value to 10 in pagination bar");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage), 5);
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage_10), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify displayed result entries are equals to the result per page value 10");
		log.info("INFO: Verify displayed result entries are equals to the result per page value 10");
		cnxAssert.assertTrue((ui.findElements(By.cssSelector(BlogsUIConstants.entryListCount)).size() == 10), "Entries count are correct");
		
		// Verify Next and previous page
		checkNextAndPreviousPage();
		
		// Verify Last and first page
		checkLastAndFirstPage();
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to validate searching a tag</li>
	 *<li><B>Step:</B> Create a new blog via API</li>
	 *<li><B>Step:</B> Create a blog entry via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Step:</B> Enter a tag name in the tag search input</li>
	 *<li><B>Step:</B> Select the tag from the pop-up list</li>
	 *<li><B>Step:</B> Hit search icon</li>
	 *<li><B>Verify:</B> Verify tag related blogs entry are displayed</li>
	 *<li><B>Step:</B> Delete the blog via API</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T773</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void searchBlogsTag() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String blogEntryName = "BlogEntry" + Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(blogEntryName)
				.tags("tag" + Helper.genDateBasedRandVal3())
				.content("Test description for testcase " + testName)
				.build();
		
		logger.strongStep("Create a new blog via API");
		log.info("INFO: Create a new blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Create a blog entry via API");
		log.info("INFO: Create a blog entry via API");
		blogEntry.createAPI(apiBlogOwner, blogAPI);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		
		// Perform a tag search from Tag section
		searchForTag(blogEntry);
		
		logger.strongStep("Verify Created Entry is displayed in public blog tab");
		log.info("INFO: Verify Created Entry is displayed in public blog tab");
		cnxAssert.assertTrue(ui.fluentWaitElementVisible("link=" + blogEntryName), "Blog entry is visible");
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to validate featured blogs entries</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Verify:</B> Verify featured blogs entries section is displayed on the page</li>
	 *<li><B>Step:</B> Click on the first featured blog entry</li>
	 *<li><B>Verify:</B> Verify blogs entries list are displayed </li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T774</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void featuredBlogsEntry() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify Featured Blogs Entries Section is displayed");
		log.info("INFO: Verify Featured Blogs Entries Section is displayed");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(BlogsUIConstants.featuredBlogEntrySection)), "Featured Blog entry is visible");
		
		List<WebElement> featureEntry = ui.findElements(By.cssSelector(BlogsUIConstants.featuredBlogEntry));
		if(featureEntry != null) {
			String name = featureEntry.get(0).getAttribute("title");
			
			logger.strongStep("Click on first featured blog entry");
			log.info("INFO: Click on first featured blog entry");
			featureEntry.get(0).click();
			ui.waitForPageLoaded(driver);
			
			logger.strongStep("Verify Blogs Entries page and All entries button are displayed");
			log.info("INFO: Verify Blogs Entries page and All entries button are displayed");
			cnxAssert.assertTrue(ui.isTextPresent(name), "Blog entry name is visible");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(BlogsUIConstants.viewAllEntriesBtn)), "All entries btn is displayed");
		}
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to validate sort Public Blog List</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Verify:</B> Verify Date filter is selected by default</li>
	 *<li><B>Step:</B> Click on Title filter</li>
	 *<li><B>Verify:</B>Verify it's selected and its ascending and descending order</li>
	 *<li><B>Step:</B> Click on Likes/Votes filter</li>
	 *<li><B>Verify:</B>Verify it's selected and its ascending and descending order</li>
	 *<li><B>Step:</B> Click on Comments filter</li>
	 *<li><B>Verify:</B>Verify it's selected and its ascending and descending order</li>
	 *<li><B>Step:</B> Click on Views filter</li>
	 *<li><B>Verify:</B>Verify it's selected and its ascending and descending order</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T771</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void sortPublicBlogs() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		
		logger.strongStep("Verify Sort By Date is selected");
		log.info("INFO: Verify Sort By Date is selected");
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedSortByFilter)).getText(), "Date", "Date filter is selected");
		
		// Select Title filter
		selectAndVerifySortByOption("title", "Title", "Title is selected");
		// Select Likes/Votes filter
		selectAndVerifySortByOption("number of likes or votes", "Likes/Votes", "Likes/Votes is selected");
		// Select Comment filter
		selectAndVerifySortByOption("number of comments", "Comments", "Comments is selected");
		// Select Views filter
		selectAndVerifySortByOption("number of views", "Views", "Views is selected");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to validate Like/Heart Icon and Tag section on Blog Listing page</li>
	 *<li><B>Step:</B> Create a blog via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Public blog tab</li>
	 *<li><B>Step:</B> Click on Blog Listing tab</li>
	 *<li><B>Verify:</B> Verify Like icon is displayed Besides View Link</li>
	 *<li><B>Verify:</B> Verify Tag section displayed</li>
	 *<li><B>Step:</B> Delete Blog</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T783</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void LikeIconOnBlogListingPage() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		logger.strongStep("Create a new blog via API");
		log.info("INFO: Create a new blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.publicBlogsTab), 5);
		
		logger.strongStep("Click on Blog Listing Tab");
		log.info("INFO: Click on Blog Listing Tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.blogsListingNav), 5);
		
		logger.strongStep("Verify Like icon is displayed correctly");
		log.info("INFO: Verify Like icon is displayed correctly");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(BlogsUIConstants.heartIcon.replace("PLACEHOLDER", blog.getName()))), "Like Icon is visible");
		
		logger.strongStep("Verify Tag section displayed");
		log.info("INFO: Verify Tag section displayed");
		ui.scrollToElementWithJavaScriptWd(By.xpath(BlogsUIConstants.tagsListLink));
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(BlogsUIConstants.tagsListLink)), "Tag section is displayed");
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to verify the pagination bar on Notification Sent page under My Updates Tab</li>
	 *<li><B>Step:</B> Create a Blog and Blog Entry via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on My Blog tab</li>
	 *<li><B>Step:</B> Open Blog</li>
	 *<li><B>Step:</B> Open Blog Entry</li>
	 *<li><B>Step:</B> Click on More Actions Link and select Notify Other People option</li>
	 *<li><B>Step:</B> Type & Select multiple users in the name input field one by one and Click on Send Button </li>
	 *<li><B>Step:</B> Go to My Updates tab and select Notification Sent link from left nav panel</li>
	 *<li><B>Step:</B> Change result per page value to 10</li>
	 *<li><B>Verify:</B> Verify Result entry counts are correct</li>
	 *<li><B>Step:</B> Click on next and previous page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to next and previous page</li>
	 *<li><B>Step:</B> Click on Last and first page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to last and first page</li>
	 *<li><B>Step:</B> Delete Blog</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T793</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyPaginationOnNotificationSentTabOfBlogs() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String entryName = "blogEntry" + Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder("BlogTest" + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryName)
												.tags("tag" + Helper.genDateBasedRandVal3())
												.content("Test description for testcase " + testName)
												.build();
		
		logger.strongStep("Create a blog via API");
		log.info("INFO: Creating a blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Create a blog entry via API");
		log.info("INFO: Create a blog entry via API");
		blogEntry.createAPI(apiBlogOwner, blogAPI);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on My Blog tab");
		log.info("INFO: Click on My Blog tab");
		ui.clickLinkWaitWd(ui.createByFromSizzle(BlogsUIConstants.myBlogsTab), 5);
		
		logger.strongStep("Open the Blog");
		log.info("INFO: Open blog");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.blogName.replace("PLACEHOLDER", blog.getName())), 6);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Open the Blog Entry");
		log.info("INFO: Open blog");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.blogName.replace("PLACEHOLDER", entryName)), 9);
		
		logger.strongStep("Add multiple users to notify people");
		log.info("INFO: Add multiple users to notify people");
		addPeopleToNotify();
		
		logger.strongStep("Click on My Updates tab");
		log.info("INFO: Click on My Updates tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.myUpdatesTab), 6);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Notifications Sent' link from left panel");
		log.info("INFO: Click on 'Notifications Sent' link from left panel");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.notificationSentNav), 6);
		
		logger.strongStep("Changes result per page value to 10 in pagination bar");
		log.info("INFO: Changes result per page value to 10 in pagination bar");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage), 5);
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage_10), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify displayed Notification Sent entries are equals to the result per page value 10");
		log.info("INFO: Verify displayed Notification Sent entries are equals to the result per page value 10");
		cnxAssert.assertTrue((ui.findElements(By.cssSelector(BlogsUIConstants.notificationSentCount)).size() == 10), "Notification Count are equals");
		
		// Verify Next and previous page
		checkNextAndPreviousPage();
		
		// Verify Last and first page
		checkLastAndFirstPage();
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();	
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to verify the pagination bar on Notification Received page under My Updates Tab</li>
	 *<li><B>Step:</B> Create a Blog and Blog Entry via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on My Blog tab</li>
	 *<li><B>Step:</B> Open Blog</li>
	 *<li><B>Step:</B> Open Blog Entry</li>
	 *<li><B>Step:</B> Click on More Actions Link and select Notify Other People option</li>
	 *<li><B>Step:</B> Type & Select one user in the name input field one by one and Click on Send Button </li>
	 *<li><B>Step:</B> Logout from current user and login again with user, to which notification is sent</li>
	 *<li><B>Step:</B> Load Blogs component and click on My updates</li>
	 *<li><B>Step:</B> Click on Notification Received </li>
	 *<li><B>Verify:</B> Verify Result per page section is displayed</li>
	 *<li><B>Verify:</B> Verify pagination is displayed</li>
	 *<li><B>Step:</B> Delete Blog</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T792</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyPaginationOnNotificationReceivedTabOfBlogs() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String entryName = "blogEntry" + Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder("BlogTest" + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(entryName)
												.tags("tag" + Helper.genDateBasedRandVal3())
												.content("Test description for testcase " + testName)
												.build();
		
		logger.strongStep("Create a blog via API");
		log.info("INFO: Creating a blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Create a blog entry via API");
		log.info("INFO: Create a blog entry via API");
		blogEntry.createAPI(apiBlogOwner, blogAPI);
		
		logger.strongStep("Run Search indexer for blogs");
		log.info("INFO: Run Search indexer for blogs");
		adminService.indexNow("blogs", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on My Blog tab");
		log.info("INFO: Click on My Blog tab");
		ui.clickLinkWaitWd(ui.createByFromSizzle(BlogsUIConstants.myBlogsTab), 5);
		
		logger.strongStep("Open the Blog");
		log.info("INFO: Open blog");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.blogName.replace("PLACEHOLDER", blog.getName())), 9);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Open the Blog Entry");
		log.info("INFO: Open the Blog Entry");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.blogName.replace("PLACEHOLDER", entryName)), 9);
		
		logger.strongStep("Click on More Action Link");
		log.info("INFO: Click on More Action Link");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.entry_MoreAction), 6);
		
		logger.strongStep("Select Notify Other People Option");
		log.info("INFO: Select Notify Other People Option");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.entry_NotifyOtherPeople), 6);
		
		User testUser1 = cfg.getUserAllocator().getUser();
		logger.strongStep("Type User name "+ testUser1.getDisplayName() +" in name field");
		log.info("INFO: Type User name "+ testUser1.getDisplayName() +" in name field");
		ui.typeWithDelayWd(testUser1.getDisplayName(), By.cssSelector(BlogsUIConstants.sendEmailInput));
		
		logger.strongStep("Select User "+ testUser1.getDisplayName() +" from typeAhead list");
		log.info("INFO: Select User "+ testUser1.getDisplayName() +" from typeAhead list");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.typeAheadFullSearch), 6);
		ui.typeaheadSelection(testUser1.getDisplayName(), BlogsUIConstants.typeAheadItem);
		
		logger.strongStep("Click Send button");
		log.info("INFO: Click Send button");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn), 6);
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn)));
		
		logger.strongStep("Verify message 'The notification has been sent' is displayed");
		log.info("INFO: Verify message 'The notification has been sent' is displayed");
		cnxAssert.assertTrue(ui.isTextPresentWd("The notification has been sent."), "Text is present");
		
		logger.strongStep("Logout from application");
		log.info("INFO: Logout from application");
		ui.waitForPageLoaded(driver);
		ui.logout();
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs,true);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		logger.strongStep("Click on My Updates tab");
		log.info("INFO: Click on My Updates tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.myUpdatesTab), 6);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Notifications Received' link from left panel");
		log.info("INFO: Click on 'Notifications Received' link from left panel");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.notificationReceivedNav), 6);
		
		logger.strongStep("Verify Result per page is displayed");
		log.info("INFO: Verify Result per page is displayed");
		ui.isElementDisplayedWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		
		logger.strongStep("Verify Page 1 is selected in pagination");
		log.info("INFO: Verify Page 1 is selected in pagination");
		int size = ui.findElements(By.cssSelector(BlogsUIConstants.pagesCount)).size();
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedPageNum)).getText(), Integer.toString(size), "Last page is displayed");
		
		logger.strongStep("Delete the Blog");
		log.info("INFO: Delete the Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();	
	}
	
	/**
	 * Method to add multiple Users to notify the entry others user
	 */
	private void addPeopleToNotify() {
		log.info("INFO: Click on More Action Link");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.entry_MoreAction), 6);
		
		log.info("INFO: Select Notify Other People Option");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.entry_NotifyOtherPeople), 6);
		
		//Add more than 10 users to the HashMap
		HashMap<Integer, User> notifyUsers = new HashMap<Integer, User>();
		for(int i=0; i<=10; i++) {
			User testUser = cfg.getUserAllocator().getUser();
			notifyUsers.put(i, testUser);
		}
		
		for (Entry<Integer, User> entry : notifyUsers.entrySet()) {
			String searchUser = entry.getValue().getDisplayName();
			
			log.info("INFO: Type User name "+ searchUser +" in name field");
			ui.typeWithDelayWd(searchUser, By.cssSelector(BlogsUIConstants.sendEmailInput));
			
			log.info("INFO: Click on full search option if no results found and then select User "+ searchUser +" from typeAhead list");
			String typeAheadFirstElement = ui.findElement(By.xpath(BlogsUIConstants.typeAheadFirstEle)).getText();
			if(typeAheadFirstElement.equals("No results found.")) {
				ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.typeAheadFullSearch), 6);
				ui.waitForElementsVisibleWd(By.xpath(BlogsUIConstants.typeAhead), 5);
			}
			ui.typeaheadSelection(searchUser, BlogsUIConstants.typeAheadItem);
		}
		
		log.info("INFO: Click Send button");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn), 6);
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.cssSelector(BlogsUIConstants.notificationFormSubmitBtn)));
		
		log.info("INFO: Verify message 'The notification has been sent' is displayed");
		cnxAssert.assertTrue(ui.isTextPresentWd("The notification has been sent."), "Text is present");
	}


	/**
	 * Method to select and verification of a option for sorting public blogs
	 * @param String sortByOption
	 * @param String expectedSortOption
	 * @param String msg
	 */
	private void selectAndVerifySortByOption(String sortByOption, String expectedSortOption, String msg) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Click on "+ expectedSortOption +" link for sorting");
		log.info("INFO: Click on "+ expectedSortOption +" link for sorting");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.sortBy.replace("PLACEHOLDER", sortByOption)), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify "+ expectedSortOption +" is selected in sort by section");
		log.info("INFO: Verify "+ expectedSortOption +" is selected in sort by section");
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedSortByFilter)).getText(), expectedSortOption, msg);
		
		logger.strongStep("Verify Ascending and Descending order of filter " + expectedSortOption);
		log.info("INFO: Verify Ascending and Descending order of filter " + expectedSortOption);
		if(ui.isElementPresentWd(By.cssSelector(BlogsUIConstants.sortOrder.replace("PLACEHOLDER", "ascending")))) {
			
			log.info("INFO: Click on "+ expectedSortOption +" link again for sorting in descending order");
			ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.sortBy.replace("PLACEHOLDER", sortByOption)), 5);
			
			log.info("INFO: Verify Descending order link is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(BlogsUIConstants.sortOrder.replace("PLACEHOLDER", "descending"))), "Descending Order link displayed");
		}else if(ui.isElementPresentWd(By.cssSelector(BlogsUIConstants.sortOrder.replace("PLACEHOLDER", "descending")))) {
			
			log.info("INFO: Click on "+ expectedSortOption +" link again for sorting in Ascending order");
			ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.sortBy.replace("PLACEHOLDER", sortByOption)), 5);
			
			log.info("INFO: Verify Ascending order link is displayed");
			cnxAssert.assertTrue(ui.isElementDisplayedWd(By.cssSelector(BlogsUIConstants.sortOrder.replace("PLACEHOLDER", "ascending"))), "Ascending Order link displayed");
		}
	}

	/**
	 * Method to Search and select a tag
	 * @param BaseBlogPost blogEntry
	 */
	private void searchForTag(BaseBlogPost blogEntry) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Enter tag name in tag search input field");
		log.info("INFO: Enter tag name in tag search input field");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.tagInput));
		ui.typeWithDelayWd(blogEntry.getTags(), By.cssSelector(BlogsUIConstants.tagInput));
		
		logger.strongStep("Select Tag name from the typeahead list");
		log.info("INFO: Select Tag name from the typeahead list");
		ui.clickLinkWd(By.xpath(BlogsUIConstants.tagPopUp.replace("PLACEHOLDER", blogEntry.getTags())), "Selected tag");
		
		logger.strongStep("Click on search icon");
		log.info("INFO: Click on search icon");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.tagSubmit), 5, "Search icon clicked");
		ui.waitForPageLoaded(driver);	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test case to verify the pagination bar in public blogs</li>
	 *<li><B>Step:</B> Create a Blog via API</li>
	 *<li><B>Step:</B> Load & Login to Blogs and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on My update blog tab</li>
	 *<li><B>Step:</B> Add multiple blog entries if entries are less than per page value 10</li>
	 *<li><B>Step:</B> Add comments in each of the created  blog entries</li>
	 *<li><B>Step:</B> Change result per page value to 10</li>
	 *<li><B>Verify:</B> Verify Result entry counts are correct</li>
	 *<li><B>Step:</B> Click on next and previous page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to next and previous page</li>
	 *<li><B>Step:</B> Click on Last and first page in pagination</li>
	 *<li><B>Verify:</B> Verify pagination navigates to last and first page</li>
	 *<li><B>Step:</B> Delete Blog</li>
	 *<li><B>JIRA link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T791</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void verifyPaginationOnMyUpdatesTabOfBlogs() throws Exception {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+ testName  + Helper.genDateBasedRandVal())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(testName + Helper.genDateBasedRandVal())
												.tags("tag" + Helper.genDateBasedRandVal3())
												.content("Test description for testcase " + testName)
												.build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
				
		
		logger.strongStep("Create a blog via API");
		log.info("INFO: Creating a blog via API");
		Blog blogAPI = blog.createAPI(apiBlogOwner);
		
		logger.strongStep("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		log.info("Load Blogs, Log in and Toggle to new UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Public Blog tab");
		log.info("INFO: Click on Public Blog tab");
		ui.clickLinkWaitWd(By.xpath(BlogsUIConstants.myUpdatesTab), 5);
		
		// Fetching Blog entries count
		int entryCount = ui.findElements(By.cssSelector(BlogsUIConstants.entryListCount)).size();
		// Creating Blog entry comments if entry comments is less than or equals to per page value 10 to enable page navigation in pagination
		if(entryCount <= 10) {
			log.info("INFO: Creating blog entries via API");
			for(int i = entryCount; i <= 11; i++)
			{
				BlogPost blogPostApi = blogEntry.createAPI(apiBlogOwner, blogAPI);
				comment.createAPI(apiBlogOwner,blogPostApi);
			}
			
			driver.navigate().refresh();
		}

		logger.strongStep("Changes result per page value to 10 in pagination bar");
		log.info("INFO: Changes result per page value to 10 in pagination bar");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage), 5);
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.resultPerPage_10), 5);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify displayed result entries are equals to the result per page value 10");
		log.info("INFO: Verify displayed result entries are equals to the result per page value 10");
		cnxAssert.assertTrue((ui.findElements(By.cssSelector(BlogsUIConstants.entryListCount)).size() == 10), "Entries count are correct");
		
		// Verify Next and previous page
		checkNextAndPreviousPage();
		
		// Verify Last and first page
		checkLastAndFirstPage();
		
		log.info("INFO: Delete Blog");
		apiBlogOwner.deleteBlog(blogAPI);
		
		ui.endTest();
		
	}
	
	/**
	 * Method to check Last page and First page in navigation
	 */
	private void checkLastAndFirstPage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Click on Last page icon in pagination");
		log.info("INFO: Click on Last page icon in pagination");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.pagination_lastPage), 5);
		
		logger.strongStep("Verify Last page is selected in pagination");
		log.info("INFO: Verify Last page is selected in pagination");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		int size = ui.findElements(By.cssSelector(BlogsUIConstants.pagesCount)).size();
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedPageNum)).getText(), Integer.toString(size), "Last page is displayed");
		
		logger.strongStep("Click on First page icon in pagination");
		log.info("INFO: Click on First page icon in pagination");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.pagination_firstPage), 5);
		
		logger.strongStep("Verify First page is selected in pagination");
		log.info("INFO: Verify First page is selected in pagination");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedPageNum)).getText(), "1", "First page is displayed");	
	}
	
	/**
	 * Method to check Next page and Previous page in navigation
	 */
	private void checkNextAndPreviousPage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Click on Next page icon in pagination");
		log.info("INFO: Click on Next page icon in pagination");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.pagination_nextPage));
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.pagination_nextPage), 5);
		
		logger.strongStep("Verify Second page is selected in pagination");
		log.info("INFO: Verify Second page is selected in pagination");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedPageNum)).getText(), "2", "Second page is displayed");
		
		logger.strongStep("Click on Previous page icon in pagination");
		log.info("INFO: Click on Previous page icon in pagination");
		ui.clickLinkWaitWd(By.cssSelector(BlogsUIConstants.pagination_prevPage), 5);
		
		logger.strongStep("Verify First page is selected in pagination");
		log.info("INFO: Verify First page is selected in pagination");
		ui.scrollToElementWithJavaScriptWd(By.cssSelector(BlogsUIConstants.resultPerPage));
		cnxAssert.assertEquals(ui.findElement(By.cssSelector(BlogsUIConstants.selectedPageNum)).getText(), "1", "First page is displayed");	
	}
}
