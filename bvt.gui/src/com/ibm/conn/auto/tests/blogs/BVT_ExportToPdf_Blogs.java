package com.ibm.conn.auto.tests.blogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
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
import com.ibm.conn.auto.webui.PdfExportUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_ExportToPdf_Blogs extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_ExportToPdf_Blogs.class);
	private BlogsUI ui;
	private PdfExportUI pUi;
	private TestConfigCustom cfg;
	private APIBlogsHandler apiOwner;
	private User testUser1;
	private String serverURL;
	private List<Blog> testBlogs = new ArrayList<Blog>();
	private BaseCommunity.Access defaultAccess;
	private CommunitiesUI cUI;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		pUi = PdfExportUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify export a blog as pdf.</li>
	*<li><B>Step:</B>(API) Create a blog as UserA and create an entry.</li>
	*<li><B>Step:</B>Go to the blog and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for blog's name, TOC and entry name</li>
	*<li><B>Verify:</B>PDF content contains blog's name, TOC and entry name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport", "cnx8ui-level2" })
	public void smokeTestExportBlogToPdf() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + rand).blogParent(blog)
				.tags(Data.getData().commonTag + rand)
				.content("Test description for testcase " + testName).build();
		
		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Blog testBlog = blog.createAPI(apiOwner);
		testBlogs.add(testBlog);
		
		logger.strongStep("Create an entry (API)");
		log.info("INFO: Create an entry (API)");
		BlogPost testBlogEntry = blogEntry.createAPI(apiOwner, testBlog);
		
		logger.strongStep("Log in to Blogs");
		log.info("Load Blogs and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());
		
		// call common smoketest method
		pUi.smokeTest(pUi, logger, testBlog.getTitle(), testBlogEntry.getTitle(), true);
		
		pUi.endTest();	
	}

	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a blog as pdf.</li>
	*<li><B>Step:</B>(API) Create a blog as UserA and create multiple entries.</li>
	*<li><B>Step:</B>Go to the blog and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for blog's name, TOC and entry name</li>
	*<li><B>Verify:</B>PDF content contains blog's name, TOC and entry name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void exportPdfContentValidation() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).build();

		BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("BlogEntry1" + rand).blogParent(blog)
				.tags(Data.getData().commonTag + rand).content("Test description for testcase " + testName).build();

		BaseBlogPost blogEntry2 = new BaseBlogPost.Builder("BlogEntry2" + rand).blogParent(blog)
				.tags(Data.getData().commonTag + rand).content("Test description for testcase " + testName).build();

		BaseBlogPost blogEntry3 = new BaseBlogPost.Builder("BlogEntry3" + rand).blogParent(blog)
				.tags(Data.getData().commonTag + rand).content("Test description for testcase " + testName).build();

		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Blog testBlog = blog.createAPI(apiOwner);
		testBlogs.add(testBlog);

		logger.strongStep("Create an entry (API)");
		log.info("INFO: Create an entry (API)");
		BlogPost testBlogEntry = blogEntry1.createAPI(apiOwner, testBlog);

		logger.strongStep("Log in to Blogs");
		log.info("Load Blogs and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		logger.strongStep("Open the blog");
		log.info("INFO: Open blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());

		// select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		// Add an Entry
		logger.strongStep("Add a new Entry 2");
		log.info("INFO: Add a new entry to the blog");
		blogEntry2.create(ui);

		// select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		// Add an Entry
		logger.strongStep("Add a new Entry 3 ");
		log.info("INFO: Add a new entry to the blog");
		blogEntry3.create(ui);
		
		String Title1 = blogEntry1.getTitle();
		log.info("Entry 1:" +Title1);
		String Title2 = blogEntry2.getTitle();
		log.info("Entry 2:" +Title2);
		String Title3 = blogEntry3.getTitle();
		log.info("Entry 3:" +Title3);

		// call common method for content selection
		pUi.contentSelection(pUi, logger, testBlog.getTitle(), testBlogEntry.getTitle(), true , Title1, Title2, Title3);
		

		pUi.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a blog as pdf.</li>
	*<li><B>Step:</B>(API) Create a blog as UserA and create an entry.</li>
	*<li><B>Step:</B>Go to the blog entry , add some comments and click the Export as PDF button.</li>
	*<li><B>Verify:</B>Export PDF Window contains All Options in 'Information included Section'.</li>
	*<li><B>Step:</B>Select all Options from 'Information included Section' List of Export PDF Window.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void infIncludedExportPdf() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		HashMap<String,String> Blogmap = new HashMap<String,String>();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.description("Blogs Test description for testcase " + testName).build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + rand).blogParent(blog)
				.tags(Data.getData().commonTag + rand)
				.content("Blogs Entry Test description for testcase " + testName).build();
		
		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Blog testBlog = blog.createAPI(apiOwner);
		testBlogs.add(testBlog);
		
		logger.strongStep("Create an entry (API)");
		log.info("INFO: Create an entry (API)");
		BlogPost testBlogEntry = blogEntry.createAPI(apiOwner, testBlog);
		
		logger.strongStep("Log in to Blogs");
		log.info("Load Blogs and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());
		
		String userid = pUi.getFirstVisibleElement("xpath=//td[@class='lotusAlignLeft']/span/a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		logger.strongStep("Open the blog entry");
		log.info("INFO: Open blog " + blogEntry.getTitle());
		ui.clickLinkWait("link=" + blogEntry.getTitle());
				
		//Create comment
		String BlogsComment = "This the Comment for the Blog Entry.";
		
		Blogmap.put("author", userid);
		Blogmap.put("childcomment", BlogsComment);
		Blogmap.put("parentdesc", blog.getDescription());
		Blogmap.put("childdesc", blogEntry.getContent());
		Blogmap.put("parenttags", blog.getTags().toLowerCase());
		Blogmap.put("childtags", blogEntry.getTags().toLowerCase());
		
		logger.strongStep("Click on Add a Comment link");
		log.info("INFO: Select Add a Comment link ");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		logger.strongStep("Verify the comment text area appears");
		log.info("INFO: Wait for comment text area to be present");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		logger.strongStep("Type " + BlogsComment + " in the comment text area");
		log.info("INFO: Type the comment into the text area");
		ui.typeInCkEditor(BlogsComment);

		//submit comment
		logger.strongStep("Click on Submit button");
		log.info("INFO: Submit comment");
		ui.clickLink(BlogsUIConstants.BlogsSubmitButton);
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:comments:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, Blogmap, 
				testBlog.getTitle(), testBlogEntry.getTitle(), ValidationList);
		
		// call common smoketest method
		pUi.smokeTest(pUi, logger, testBlog.getTitle(), testBlogEntry.getTitle(), true);
		
		
		pUi.endTest();	
	}


	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a blog as pdf.</li>
	*<li><B>Step:</B>(API) Create a blog as UserA and login to Connection Application.</li>
	*<li><B>Step:</B>Open the Created Blog</li>
	*<li><B>Verify:</B>Export PDF Button should Not be present for the above created Blog.</li>
	*<li><B>Step:</B>Create a New Entry Inside this Blog</li>
	*<li><B>Verify:</B>Export PDF Button should be present for the above created Blog now.</li>
	*<li><B>Step:</B>Go to the blog entry , add some comments and click the Export as PDF button.</li>
	*<li><B>Step:</B>Select all Options from 'Information included Section' List of Export PDF Window.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void ExportPdfBlogs() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		HashMap<String,String> Blogmap = new HashMap<String,String>();
		String userid = "";
		String BlogsComment = "This the Comment for the Blog Entry.";
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.description("Blogs Test description for testcase " + testName).build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand()).blogParent(blog)
				 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				 .content("Test description for testcase " + testName)
				 .build();

		Blogmap.put("author", userid);
		Blogmap.put("childcomment", BlogsComment);
		Blogmap.put("parentdesc", blog.getDescription());
		Blogmap.put("childdesc", blogEntry.getContent());
		Blogmap.put("parenttags", blog.getTags().toLowerCase());
		Blogmap.put("childtags", blogEntry.getTags().toLowerCase());
		
		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Blog testBlog = blog.createAPI(apiOwner);
		testBlogs.add(testBlog);
		
		logger.strongStep("Log in to Blogs");
		log.info("Load Blogs and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());
		
		driver.turnOffImplicitWaits();
		Assert.assertFalse(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is not Expected but still present.");
		driver.turnOnImplicitWaits();
				
		//Add an Entry
		logger.strongStep("Add a new Entry");
		log.info("INFO: Add the new entry");
		ui.clickLink(BlogsUIConstants.BlogsNewEntry);
		blogEntry.create(ui);
		
		driver.turnOffImplicitWaits();
		Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is Expected but still Not present.");
		driver.turnOnImplicitWaits();
				
		//Validate Template Drop Down
		pUi.getFirstVisibleElement("xpath=//td[@class='lotusAlignLeft']/span/a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		logger.strongStep("Click on Add a Comment link");
		log.info("INFO: Select Add a Comment link ");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		logger.strongStep("Verify the comment text area appears");
		log.info("INFO: Wait for comment text area to be present");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		logger.strongStep("Type " + BlogsComment + " in the comment text area");
		log.info("INFO: Type the comment into the text area");
		ui.typeInCkEditor(BlogsComment);

		//submit comment
		logger.strongStep("Click on Submit button");
		log.info("INFO: Submit comment");
		ui.clickLink(BlogsUIConstants.BlogsSubmitButton);
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:comments:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, Blogmap, 
				testBlog.getTitle(), blogEntry.getTitle(), ValidationList);
		
		pUi.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Smoke test to verify export an Ideation blog as pdf.</li>
	*<li><B>Step:</B>Create community using API</li>
	*<li><B>Step:</B>Add Ideation Blog widget</li>
	*<li><B>Step:</B>Open communities and login</li>
	*<li><B>Step:</B>Navigate to the API community</li>
	*<li><B>Step:</B>Click on Ideation Blog</li>
	*<li><B>Step:</B>Select New Idea button</li>
	*<li><B>Step:</B>Create a new idea</li>
	*<li><B>Verify:</B>Validate Export PDF feature and Side bar contents</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void exportIdeationBlogToPdf() {
		
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();

		APICommunitiesHandler apiCommOwner = new APICommunitiesHandler(serverURL,
				testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
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
		Community comAPI = community.createAPI(apiCommOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner, comAPI);
		
		//add widget ideation blog
		logger.strongStep("Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiCommOwner, BaseWidget.IDEATION_BLOG);
		
		//GUI
		//Load component and login
		logger.strongStep("Open communities and login: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
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
		
		logger.strongStep("Validate Export PDF feature and Side bar contents");
		log.info("Validate Export PDF feature and Side bar contents");
		pUi.smokeTest(pUi, logger, comAPI.getTitle(), ideationBlogEntry.getTitle(), true);
		
		pUi.endTest();	
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		for (Blog blog : testBlogs)  {
			apiOwner.deleteBlog(blog);
		}
	}
}
