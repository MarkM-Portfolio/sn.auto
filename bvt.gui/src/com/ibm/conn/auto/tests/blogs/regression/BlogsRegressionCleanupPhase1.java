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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;

public class BlogsRegressionCleanupPhase1 extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BlogsRegressionCleanupPhase1.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private CommunitiesUI cUI;
	private User testUser1, testUser3;
	private APIBlogsHandler apiOwner, apiOwner3;
	private String serverURL;
	private boolean isOnPremise;
	
	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current Blogs regression suite have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work...Phase 2 of cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Check environment to see if on-prem or on the cloud
				if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
					isOnPremise = true;
				} else {
					isOnPremise = false;
				}
				testUser1 = cfg.getUserAllocator().getUser();
				cfg.getUserAllocator().getUser();
				testUser3 = cfg.getUserAllocator().getUser();
				cfg.getUserAllocator().getAdminUser();

				serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
				new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
				apiOwner = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
				apiOwner3 = new APIBlogsHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
				
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if the number of paging controls is an expected value.
	 *<li><B>Verify: </B>The real number of paging controls is equal to the expected number of paging controls.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void pagerControl() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
									.tags(Data.getData().commonTag + rand)
									.description(Data.getData().commonDescription)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		logger.strongStep("Verify that the number of paging controls is equal to 2");
		log.info("INFO: Confirm that the number of paging controls is equal to 2");
		List<Element> pagingControls = driver.getElements(BlogsUIConstants.BlogsPagingNumbers);
		int realNumberOfPagingControls = pagingControls.size();
		int expectedNumberOfPagingControls = 2;
		Assert.assertEquals(realNumberOfPagingControls, expectedNumberOfPagingControls);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if the search box exists and scope is within blog when a blog is selected.
	 *<li><B>Step: </B>Create a blog.
	 *<li><B>Step: </B>Login and Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Verify: </B>The search icon should be visible.
	 *<li><B>Step: </B>Click on the search icon.
	 *<li><B>Verify: </B>The search bar should be visible.
	 *<li><B>Step: </B>Type a word in the search text area.
	 *<li><B>Verify: </B>The search scope 'My Blog Entries' should be visible.
	 *<li><B>Step: </B>Click on the new blog's link.
	 *<li><B>Verify: </B>The search icon should be visible.
	 *<li><B>Step: </B>Click on the search icon.
	 *<li><B>Verify: </B>The search bar should be visible.
	 *<li><B>Step: </B>Type a word in the search text area.
	 *<li><B>Verify: </B>The search scope 'This Blog' should be visible.
	 *</ul>
	 */
	@Test(groups = {"regression"} )
	public void searchBarAndScope() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiOwner);
		
		//GUI
		//Load the component and ui.login
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Click on the Search icon
		logger.strongStep("Confirm that the search icon is visible and click on it");
		log.info("INFO: Verify the search icon displays then click on it");
		Assert.assertTrue(driver.isElementPresent(GlobalsearchUI.OpenSearchPanel),
				"ERROR: The global search icon does not exist");
		ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		
		//Wait for the search text area to display
		logger.strongStep("Confirm that the search bar is visible");
		log.info("INFO: Verify the search text area displays");
		Assert.assertTrue(driver.isElementPresent(GlobalsearchUI.TextAreaInPanel),
				"ERROR: The global search text area does not exist");
		
		//Enter a word to search on
		logger.strongStep("Type a word in the search text area");
		log.info("INFO: Enter a word to search on");
		ui.typeText(GlobalsearchUI.TextAreaInPanel, Data.getData().BlogsNewEntryTag);
		
		//Verify the search scope is 'My Blog Entries'
		logger.strongStep("Confirm that the search scope 'My Blog Entries' is visible");
		log.info("INFO: Verify the search scope is 'My Blog Entries'");
		Assert.assertTrue(driver.isElementPresent("css=a[class='icLocalScope']:contains(My Blog Entries)"),
				"ERROR: The search scope is not 'My Blog Entries'");
				
		//Select the new blog
		logger.strongStep("Click on the newly created blog's link");
		log.info("INFO: Select the new blog");
		ui.clickLinkWait("link=" + blog.getName());
		
		//Click on the Search icon
		logger.strongStep("Click on the search icon and verify it is still visible");
		log.info("INFO: Click on the search icon and verify it is still apparent");
		ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		Assert.assertTrue(driver.isElementPresent(GlobalsearchUI.OpenSearchPanel),
				"ERROR: The global search icon does not exist");

		//Wait for the search text area to display 
		logger.strongStep("Confirm that the search bar is visible");
		log.info("INFO: Wait for the search text area to display");
		Assert.assertTrue(driver.isElementPresent(GlobalsearchUI.TextAreaInPanel),
				"ERROR: The global search text area does not exist");
		
		//Enter a word to search on
		logger.strongStep("Type a word in the search text area");
		log.info("INFO: Enter a word to search on");
		ui.typeText(GlobalsearchUI.TextAreaInPanel, Data.getData().BlogsNewEntryTag);
		
		//Verify the search scope is 'This Blog'
		logger.strongStep("Confirm that the search scope 'This Blog' is visible");
		log.info("Validate the Search scope is 'This Blog'");
		Assert.assertTrue(driver.isElementPresent("css=a[class='icLocalScope']:contains(This Blog)"),
				"ERROR: The search scope is not 'This Blog'");
						
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Test will verify the UI of the Insert Image dialog box.
	 *<li><B>Step: </B>Check test environment: If on-premise is selected execute test, if cloud is selected skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the Rich Text Editor, click on the Insert Image icon.
	 *<li><B>Verify: </B>The Insert Image dialog displays with Local Files tab selected by default.
	 *<li><B>Verify: </B>The dialog header is 'Insert Image'.
	 *<li><B>Verify: </B>The Web URL & Existing Images tabs exist.
	 *<li><B>Verify: </B>Browse button appears on the dialog.
	 *<li><B>Verify: </B>The Drag and Drop message displays.
	 *<li><B>Verify: </B>The image size message displays.
	 *<li><B>Verify: </B>The Additional Options link displays.
	 *<li><B>Verify: </B>The Upload Image button is disabled.
	 *<li><B>Verify: </B>The Cancel button displays.
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageDlgDefaultUI() throws Exception{	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Dialog default UI")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();

		if (isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);
				
				logger.strongStep("Confirm that the heading of the dialog box is 'Insert Image'");
				log.info("INFO: Verify the dialog header is 'Insert Image'");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgHeader),
						"ERROR: Dialog header is not 'Insert Image'");

				logger.strongStep("Validate that Local Files is selected by default on the Insert Image dialog box");
				log.info("INFO: Verify that Local Files is selected by default on the Insert Image dialog");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLocalFilesSelected),
						"ERROR: Insert Image dialog with Local Files selected by default does not display");				

				logger.strongStep("Verify the 'X' icon to close the dialog box is visible");
				log.info("INFO: Verify there is an 'X' icon to close the dialog");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgCloseIcon),
						"ERROR: 'X' icon to close the dialog does not appear");

				logger.strongStep("Verify the 'Web URL' tab is visible");
				log.info("INFO: Verify the tab 'Web URL' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlTab),
						"ERROR: 'Web URL' tab does not appear");			

				logger.strongStep("Verify the 'Existing Images' tab is visible");
				log.info("INFO: Verify the tab 'Existing Images' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgExistingImagesTab),
						"ERROR: 'Existing Images' tab does not appear");

				logger.strongStep("Verify the 'Browse' button is available in the dialog box");
				log.info("INFO: Verify the 'Browse' button appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgBrowseButton),
						"ERROR: Browse button does not appear on the dialog");			

				logger.strongStep("Verify the 'Drag and drop images...' text appears");
				log.info("INFO: Verify the 'Drag and drop images...' text appears");
				Assert.assertTrue(driver.isTextPresent(Data.getData().DragAndDropImagesToUploadMsg),
						"ERROR: Drag and drop message does not appear");

				logger.strongStep("Verify the 'Image size must not exceed 1 MB' text message appears");
				log.info("INFO: Verify the 'Image size must not exceed 1 MB' message appears");
				Assert.assertTrue(driver.isTextPresent(Data.getData().ImageSizeMustNotExceedMsg),
						"ERROR: 'Image size must not exceed 1 MB' message does not appear");

				logger.strongStep("Verify the 'Additional Options link' is available");
				log.info("INFO: Verify the 'Additional Options link' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel),
						"ERROR: The Additional Options link does not appear");

				logger.strongStep("Confirm that the 'Upload Image' button is inactive");
				log.info("INFO: Verify the 'Upload Image' button is disabled");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEUploadImageButtonDisabled),
						"ERROR: The Upload Image button is not disabled");			

				logger.strongStep("Validate that the 'Cancel' button is apparent");
				log.info("INFO: Verify the 'Cancel' button appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKECancelButton),
						"ERROR: Cancel button does not appear");

				logger.strongStep("Click on the 'Cancel' button and close the 'Insert Image' dialog");
				log.info("INFO: Click the 'Cancel' button to close the 'Insert Image' dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the 'Cancel' button in the 'New Entry' form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}

		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs' is not supported in the Cloud");
			
		}
	}
			
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Test will verify expanding/collapsing Additional Options twistie in the 'Insert Image' dialog box.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the Rich Text Editor, click on the Insert Image icon.
	 *<li><B>Verify: </B>The Insert Image dialog displays with Local Files tab selected by default.
	 *<li><B>Verify: </B>The Additional Options twistie displays and is collapsed/closed by default.
	 *<li><B>Verify: </B>The Additional Options link exist.
	 *<li><B>Step: </B>Click on the twistie to expand the section.
	 *<li><B>Verify: </B>The twistie is now expanded.
	 *<li><B>Step: </B>Click on the twistie to collapse the section.
	 *<li><B>Verify: </B>The twistie is now collapsed.
	 *<li><B>Step: </B>Click the Additional Options link.
	 *<li><B>Verify: </B>The section is now expanded.
	 *<li><B>Step: </B>Click on the Additional Options link.
	 *<li><B>Verify: </B>The section is now collapsed again.
	 *<li><B>Verify: </B>The Cancel button displays.
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageAddlOptTwistie() throws Exception{		
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Expand/Collapse Additional Options")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();

		if (isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Validate that the 'Additional Options' twistie icon is collapsed by default");
				log.info("INFO: Verify the Additional Options twistie is collapsed");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieCollapsed),
						"ERROR: The Additional Options twistie is not collapsed");

				logger.strongStep("Confirm that the 'Additional Options' link is visible");
				log.info("INFO: Verify the 'Additional Options' link appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel),
						"ERROR: The Additional Options link does not appear");

				logger.strongStep("Click on the twistie icon to expand 'Additional Options' section");
				log.info("INFO: Click on the twistie to expand it");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieCollapsed);

				logger.strongStep("Confirm that the 'Additional Options' twistie icon is expanded");
				log.info("INFO: Verify the Additional Options twistie is expanded");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieExpanded),
						"ERROR: The Additional Options twistie is not expanded");

				logger.strongStep("Click on the twistie to collapse the section");
				log.info("INFO: Click on the twistie to collapse the section");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieExpanded);

				logger.strongStep("Verify that the 'Additional Options' twistie icon is now collapsed");
				log.info("INFO: Verify the twistie is now collapsed");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieCollapsed),
						"ERROR: The Additional Options twistie is not collapsed");

				logger.strongStep("Click on the Additonal Options link to expand the section");
				log.info("INFO: Click on the Additonal Options link to expand the section");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel);

				logger.strongStep("Verify the Additional Options section is expanded again");
				log.info("INFO: Verify the Additional Options section is expanded again");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptExpanded),
						"ERROR: The Additional Options section is not expanded");

				logger.strongStep("Click on the Additional Options link to collapse the section");
				log.info("INFO: Click on the Additional Options link to collapse the section");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel);

				logger.strongStep("Verify the Additional Options section is collapsed again");
				log.info("INFO: Verify the Additional Options section is collapsed again");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptCollapsed),
						"ERROR: The Additional Options section is not collapsed");			

				logger.strongStep("Verify the 'Cancel' button is visible");
				log.info("INFO: Verify the 'Cancel' button appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKECancelButton),
						"ERROR: Cancel button does not appear");

				logger.strongStep("Click on the Cancel button to close the Insert Image dialog");
				log.info("INFO: Click the Cancel button to close the Insert Image dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}

		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Test will verify the options in the Additional Options section.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the Rich Text Editor, click on the Insert Image icon.
	 *<li><B>Verify: </B>The Insert Image dialog displays with Local Files tab selected by default.	 
	 *<li><B>Step: </B>Click on the twistie to expand the Additional Options section.
	 *<li><B>Verify: </B>Image Layouts header displays.
	 *<li><B>Verify: </B>Each of the (4) layouts appear: Left 1, Left 2, Centered & Right.
	 *<li><B>Verify: </B>Image Size header displays.
	 *<li><B>Verify: </B>Image Size help icon displays.
	 *<li><B>Step: </B> Hover over the help icon.
	 *<li><B>Verify: </B>The help text is correct.
	 *<li><B>Verify: </B>Each of the (4) image sizes appear: Original, 200 pixel wide, 400 pixel wide, Fit page width.
	 *<li><B>Verify: </B>The option to set the layout as the default option displays.
	 *<li><B>Verify: </B>The 'Cancel' button is visible.
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageAddlOptions() throws Exception{		
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Additional Options - Image Layout & Image Size")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();

		if(isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Click on the twistie icon to expand 'Additional Options' section");
				log.info("INFO: Click on the twistie to expand Additional Options");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgAddiOptTwistieCollapsed);

				logger.strongStep("Confirm that the 'Image Layout:' header is visible'");
				log.info("INFO: Verify the 'Image Layout:' header appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutLabel),
						"ERROR: 'Image Layout:' header does not appear");

				logger.strongStep("Verify the 4 layouts: Left 1, Left 2, Centered & Right appear under 'Image Layout:' header");
				log.info("INFO: This section will verify the 4 layouts: Left 1, Left 2, Centered & Right appear");
				
				logger.strongStep("Verify the layout 'Left 1' appears and is selected by default");
				log.info("INFO: Verify layout 'Left 1' appears and is selected by default");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutLeft1Selected),
						"ERROR: 'Left 1' is not selected by default");

				logger.strongStep("Verify the layout 'Left 2' appears");
				log.info("INFO: Verify the layout 'Left 2' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutLeft2),
						"ERROR: 'Left 2' does not appear as a layout option.");

				logger.strongStep("Verify the layout 'Centered' appears");
				log.info("INFO: Verify the layout 'Centered' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutCenter),
						"ERROR: 'Centered' does not appear as a layout option.");

				logger.strongStep("Verify the layout 'Right' appears");
				log.info("INFO: Verify the layout 'Right' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutRight),
						"ERROR: 'Right' does not appear as a layout option.");

				logger.strongStep("Verify the 'Image Size:' header appears");
				log.info("INFO: Verify the 'Image Size:' header appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgImageSizeLabel),
						"ERROR: 'Image Size:' header does not appear");

				logger.strongStep("Verify a help icon appears for the 'Image Size:' header");
				log.info("INFO: Verify a help icon appears for the 'Image Size:' header");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgImageSizeHelpIcon),
						"ERROR: Image size help icon does not appear");

				logger.strongStep("Hover over the Image Size help icon");
				log.info("INFO: Hover over the Image Size help icon");
				driver.getFirstElement(BlogsUIConstants.BlogsCKEImageDlgImageSizeHelpIcon).hover();

				logger.strongStep("Verify the Image Size help text is correct");
				log.info("INFO: Verify the image size help text is correct");
				Assert.assertTrue(driver.isTextPresent(Data.getData().imageSizeHelpIconTxt),
						"ERROR: Image size help text does not appear or is not correct");

				logger.strongStep("Verify all 4 Image Size options appear under the header 'Image Size'");
				log.info("INFO: This section will verify each of the 4 Image Size options appear");
				
				logger.strongStep("Verify the size 'Original' appears");
				log.info("INFO: Verify the size 'Original' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgImageSizeOriginal),
						"ERROR: The image size 'Original' does not appear");

				logger.strongStep("Verify the size '200 pixels wide' appears");
				log.info("INFO: Verify the size '200 pixels wide' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgImageSize200Pixels),
						"ERROR: The image size '200 pixels wide' does not appear");

				logger.strongStep("Verify the size '400 pixels wide' appears");
				log.info("INFO: Verify the size '400 pixels wide' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutMedSize),
						"ERROR: The image size '400 pixels wide' does not appear");

				logger.strongStep("Verify the size 'Fit page width' appears");
				log.info("INFO: Verify the size 'Fit page width' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgImageSizeFitPage),
						"ERROR: The image size 'Fit page width' does not appear");	

				logger.strongStep("Verify the check box to set the layout as default appears");
				log.info("INFO: Verify the option to set the layout as default appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgLayoutChkDef),
						"ERROR: The option to set the layout as default does not appear");

				logger.strongStep("Verify the 'Cancel' button is visible");
				log.info("INFO: Verify the 'Cancel' button appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKECancelButton),
						"ERROR: Cancel button does not appear");

				logger.strongStep("Click on the Cancel button to close the Insert Image dialog");
				log.info("INFO: Click the Cancel button to close the Insert Image dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}

		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}
	}	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Tests the default UI of Web URL tab in the Insert Image dialog box.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the Rich Text Editor, click on the Insert Image icon.	 
	 *<li><B>Step: </B>Click on the Web URL tab.
	 *<li><B>Verify: </B>The Web URL tab is selected.
	 *<li><B>Verify: </B>The 'Web Image URL' field label displays.
	 *<li><B>Verify: </B>The web URL input field displays.
	 *<li><B>Verify: </B>The Preview area displays.
	 *<li><B>Verify: </B>The Additional Options link displays.
	 *<li><B>Verify: </B>The 'Insert Image' button is disabled.
	 *<li><B>Verify: </B>The 'Cancel' button is visible.
	 *<li><B>Verify: </B>The 'X' icon to close the dialog displays.
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageWebURLTabUI() throws Exception{		
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image Web URL tab UI test")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();
				
		if (isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Switch to Web URL tab");
				log.info("INFO: Click on the Web URL tab");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgWebUrlTab);

				logger.strongStep("Confirm that the Web URL tab is now selected");
				log.info("INFO: Verify the Web URL tab is selected");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlTabSelected),
						"ERROR: The Web URL tab is not selected");

				logger.strongStep("Verify the label 'Web Image URL:' appears");
				log.info("INFO: Verify the field label 'Web Image URL:' appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlFieldLabel),
						"ERROR: The Field label 'Web Image URL:' does not appear");

				logger.strongStep("Verify the Web URL input field appears");
				log.info("INFO: Verify the web URL input field appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput),
						"ERROR: The web URL input field does not appear");

				logger.strongStep("Verify the Preview area appears");
				log.info("INFO: Verify the Preview area appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgWebUrlPreviewArea),
						"ERROR: The preview area does not appear");

				logger.strongStep("Confirm that the 'Additional Options' link is visible");
				log.info("INFO: Verify the 'Additional Options' link appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgAddiOptLabel),
						"ERROR: The Additional Options link does not appear");

				logger.strongStep("Verify the 'Insert Image' button is inactive");
				log.info("INFO: Verify the 'Insert Image' button is disabled");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEInsertImageButtonDisabled),
						"ERROR: The Upload Image button is not disabled");			

				logger.strongStep("Verify the 'Cancel' button is visible");
				log.info("INFO: Verify the 'Cancel' button appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKECancelButton),
						"ERROR: Cancel button does not appear");

				logger.strongStep("Verify the 'X' icon to close the dialog box appears");
				log.info("INFO: Verify the 'X' icon to close the dialog appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgCloseIcon),
						"ERROR: The X icon does not appear");

				logger.strongStep("Click on the Cancel button to close the Insert Image dialog");
				log.info("INFO: Click the Cancel button to close the Insert Image dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}
		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Tests the insertion of an image using a valid image URL in the Web URL Tab of the Insert Image dialog box.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the editor, click on the Insert Image icon.
	 *<li><B>Step: </B>Click on the Web URL tab.	 
	 *<li><B>Step: </B>Enter a valid image URL into the Web URL input field.
	 *<li><B>Verify: </B>The 'Insert Image' button is now enabled.
	 *<li><B>Step: </B>Click on the Insert Image button.
	 *<li><B>Verify: </B>The user is returned to the New Entry form.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageValidWebURL() throws Exception{		
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Enter a valid image Web URL")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();
				
		if (isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor,click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Switch to Web URL tab");
				log.info("INFO: Click on the Web URL tab");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgWebUrlTab);

				logger.strongStep("Input a valid image URL into the Web Image URL field");
				log.info("INFO: Enter a valid image URL into the Web URL field");
				ui.getFirstVisibleElement(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput).typeWithDelay(Data.getData().InsertImageWebURL);

				logger.strongStep("Confirm that the Insert Image button is now enabled");
				log.info("INFO: Verify the Insert Image button is now enabled");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgInsertImageButton),
						"ERROR: The Insert Image button is not enabled");

				logger.strongStep("Click on the Insert Image button");
				log.info("INFO: Click on the Insert Image button");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgInsertImageButton);

				logger.strongStep("Verify the New Entry form reappears");
				log.info("INFO: Verify user is returned to the New Entry form");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntryFormHeader),
						"ERROR: User is not on the New Entry form");

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}
		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Tests the reception of error message when invalid image URLs are used in the Web URL Tab of the Insert Image dialog box.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link.
	 *<li><B>Step: </B>In the editor, click on the Insert Image icon.
	 *<li><B>Step: </B>Click on the Web URL tab.
	 *<li><B>Step: </B>Enter an invalid URL into the Web Image URL field.
	 *<li><B>Verify: </B>An error message displays indicating the URL is invalid .
	 *<li><B>Step: </B>Clear the invalid URL from the input field.
	 *<li><B>Step: </B>Enter a valid non-image URL.
	 *<li><B>Verify: </B>A message displays asking if the URL is a valid image URL .
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageInvalidWebURL() throws Exception{	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Enter an invalid web URL")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();
				
		if (isOnPremise){

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser1);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Switch to Web URL tab");
				log.info("INFO: Click on the Web URL tab");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgWebUrlTab);

				logger.strongStep("Input an invalid image URL into the Web Image URL field");
				log.info("INFO: Enter an invalid URL into the Web URL field");
				ui.getFirstVisibleElement(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput).typeWithDelay(Data.getData().InsertImageInvalidWebURL);

				logger.strongStep("Confirm that the invalid image URL error message displays");
				log.info("INFO: Verify an invalid URL error message displays");
				Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgMessageInvalidURL),
						"ERROR: Incorrect error message returned");

				logger.strongStep("Clear the invalid image URL from the Web Image URL field");
				log.info("INFO: Clear the invalid URL from the Web URL field");
				ui.getFirstVisibleElement(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput).clear();

				logger.strongStep("Input a URL that is valid, but not an image into the Web Image URL field");
				log.info("INFO: Enter a URL that is valid, but not an image into the Web URL field");
				ui.getFirstVisibleElement(BlogsUIConstants.BlogsCKEImageDlgWebUrlInput).typeWithDelay(Data.getData().MultiplePublicBookmarksUrl1);

				logger.strongStep("Confirm that the invalid image URL error message displays");
				log.info("INFO: Verify an error message displays");
				Assert.assertTrue(ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEImageDlgMessageNonImageURL),
						"ERROR: Incorrect error message returned");			

				logger.strongStep("Click on the Cancel button to close the Insert Image dialog");
				log.info("INFO: Click the Cancel button to close the Insert Image dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				
			}
		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Standalone Blog: Empty Existing Images Tab UI.
	 *<li><B>Step: </B>Check test environment: if on-premise execute test, if cloud skip test.
	 *<li><B>Step: </B>Create a Blog using the API.
	 *<li><B>Step: </B>Login as UserA.
	 *<li><B>Step: </B>Check to see if the Insert Image GK flag is enabled - if not skip test.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Locate the blog and click on the New Entry link. 
	 *<li><B>Step: </B>In the editor, click on the Insert Image icon.
	 *<li><B>Step: </B>Click on the Existing Images tab.
	 *<li><B>Verify: </B>The Existing Images tab is selected.
	 *<li><B>Verify: </B>The no images icon displays.
	 *<li><B>Verify: </B>The message indicating no images have been added yet displays.
	 *<li><B>Verify: </B>The 'Insert Image' button is disabled.
	 *<li><B>Verify: </B>The 'Cancel' button is visible.
	 *<li><B>Verify: </B>The 'X' icon to close the dialog displays.
	 *<li><B>Step: </B>Cancel out of the Insert Image dialog.
	 *<li><B>Step: </B>Cancel out of the New Entry form.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Step: </B>Click on the blog's link.
	 *<li><B>Step: </B>Click on the 'Manage Blog' link.
	 *<li><B>Step: </B>Cleanup: Delete the blog.
	 *</ul>
	 *NOTE: On-Premise ONLY - Standalone Blogs not supported in the cloud.
	 *
	 */
	@Test(groups = {"regression"})
	public void insertImageEmptyExistImgTab() throws Exception{		
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();		
		String rand = Helper.genDateBasedRand();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
		                            .tags(Data.getData().commonTag + rand)
		                            .description("Insert Image: Empty Existing Images tab UI")
		                            .theme(Theme.Blog_with_Bookmarks)
		                            .build();
				
		if (isOnPremise){			

			logger.strongStep("Create blog using API");
			log.info("INFO: Create blog using API");
			blog.createAPI(apiOwner3);

			logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
			log.info("INFO: Log into Blogs as " + testUser3.getDisplayName());
			ui.loadComponent(Data.getData().ComponentBlogs);
			ui.login(testUser3);

			if(cUI.checkGKSetting(Data.getData().gk_unifyInsertImageDialog)){

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Navigate to My Blogs");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on New Entry link for the current blog");
				log.info("INFO: Navigate to the blog & click New Entry");
				ui.clickLinkWait(BlogsUI.getNewEntryBtnForBlog(blog));

				logger.strongStep("Click on the Insert Image icon in the Rich Text Editor");
				log.info("INFO: In the editor, click on the Insert Image icon");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEInsertImageButton);

				logger.strongStep("Switch to Existing Images tab");
				log.info("INFO: Click on the Existing Images tab");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKEImageDlgExistingImagesTab);

				logger.strongStep("Verify that the Existing Images tab is selected");
				log.info("INFO: Verify that the Existing Images tab is selected");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgExistingImagesTabSelected),
						"ERROR: The Existing Images tab is not selected");

				logger.strongStep("Confirm that the no images icon displays");
				log.info("INFO: Verify the no images icon displays");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgExistingImagesNoImagesIcon),
						"ERROR: The icon to illustrate no images have been added does not appear");

				logger.strongStep("Validate that the message indicating no images have been added yet displays");
				log.info("INFO: Verify the message indicating no images have been added yet displays");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgExistingImagesNoImageMsg),
						"ERROR: The message to indicate no images have been added yet does not appear");

				logger.strongStep("Verify the Insert Image button is inactive");
				log.info("INFO: Verify the Insert Image button is disabled");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEInsertImageButtonDisabled),
						"ERROR: The Insert Image button is not disabled");

				logger.strongStep("Verify the Cancel button is visible");
				log.info("INFO: Verify the Cancel button displays");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKECancelButton),
						"ERROR: The Cancel button does not appear");

				logger.strongStep("Verify the 'X' icon to close the dialog box appears");
				log.info("INFO: Verify the 'X' icon to close the dialog appears");
				Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsCKEImageDlgCloseIcon),
						"ERROR: The X icon to close the dialog does not appear");

				logger.strongStep("Click on the Cancel button to close the Insert Image dialog");
				log.info("INFO: Click the Cancel button to close the Insert Image dialog");
				ui.clickLinkWait(BlogsUIConstants.BlogsCKECancelButton);

				logger.strongStep("Click on the Cancel button in the New Entry form");
				log.info("INFO: Click Cancel to exit out the New Entry form");
				ui.getFirstVisibleElement(BaseUIConstants.CancelButton).click();

				logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
				log.info("INFO: Click My Blogs tab");
				ui.clickLinkWait(BlogsUIConstants.MyBlogs);

				logger.strongStep("Click on the blog's link");
				log.info("INFO: Open the blog");
				ui.clickLinkWait("link=" + blog.getName());

				logger.strongStep("Click on the 'Manage Blog' link then click on 'Delete Blog' button");
				logger.strongStep("Confirm the check box is present in the delete blog dialog box");
				logger.strongStep("Select the check box");
				logger.strongStep("Click on the 'Delete' button to delete the blog");
				log.info("INFO: Delete the blog");
				blog.delete(ui);

				ui.endTest();	

			}else{
				
				logger.strongStep("'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");
				log.info("INFO: 'Unify_Insert_Image_Dialog' gatekeeper flag is not enabled");

			}
		} else {
			
			logger.strongStep("'Standalone Blogs' is not supported in the Cloud");
			log.info("INFO: 'Standalone Blogs is not supported in the Cloud");
			
		}

	}

}
