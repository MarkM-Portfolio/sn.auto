package com.ibm.conn.auto.tests.dogear.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Dogear_MoreActions_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;

public class RegressionDogear extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(RegressionDogear.class);
	private DogearUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private User testUser1;
	private User testUser2;
	private APIDogearHandler apiOwner;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();

		//Load users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIDogearHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration and GUI
		cfg = TestConfigCustom.getInstance();
		ui = DogearUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the Bookmarking Tools link in the footer.
	 * <li><B>Step: </B>Log into Dogear.
	 * <li><B>Step: </B>Select footer link: Bookmarking Tool.
	 * <li><B>Verify: </B>The instructions for 'Discuss This' correctly displays. 
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void bookmarkingToolDiscussThis() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser1.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);

		//Click on the Bookmark or Discuss this link
		logger.strongStep("Click on the Bookmarking Tools link");
		log.info("INFO: Open the Bookmarking Tools page by clicking on its link");
		ui.clickLink(DogearUIConstants.Nav_HowToBookmark);

		//Verify Discuss This instructions are present
		if (testConfig.browserIs(BrowserType.FIREFOX)){
			logger.strongStep("Verify Discuss This instruction text specific for Firefox browser is present");
			log.info("INFO: Verify Discuss This instruction text specific for Firefox browser is present");
			Assert.assertTrue(driver.isTextPresent(Data.getData().DiscussThis_Firefox),
					"ERROR: The text \"" + Data.getData().DiscussThis_Firefox +
					"\" was not found on the page");
			} else if (testConfig.browserIs(BrowserType.IE)){
			logger.strongStep("Verify first line of Discuss This instruction text specific for IE browser is present");
			log.info("INFO: Verify first line of Discuss This instruction text specific for IE browser is present");
			Assert.assertTrue(driver.isTextPresent(Data.getData().DiscussThis_IE1),
					"ERROR: The text \"" + Data.getData().DiscussThis_IE1 +
					"\" was not found on the page");
			logger.strongStep("Verify second line of Discuss This instruction text specific for IE browser is present");
			log.info("INFO: Verify second line of Discuss This instruction text specific for IE browser is present");
			Assert.assertTrue(driver.isTextPresent(Data.getData().DiscussThis_IE2),
					"ERROR: The text \"" + Data.getData().DiscussThis_IE2 +
					"\" was not found on the page");
			logger.strongStep("Verify third line of Discuss This instruction text specific for IE browser is present");
			log.info("INFO: Verify third line of Discuss This instruction text specific for IE browser is present");
			Assert.assertTrue(driver.isTextPresent(Data.getData().DiscussThis_IE3),
					"ERROR: The text \"" + Data.getData().DiscussThis_IE3 +
					"\" was not found on the page");
			}

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the copying of bookmarks as public and private bookmarks.
	 * <li><B>Step: </B>Create 3 public bookmarks via API.
	 * <li><B>Step: </B>Log in as user 2.
	 * <li><B>Step: </B>Copy the first bookmark created to my bookmarks.
	 * <li><B>Verify: </B>The original title, description and URL are copied to the form, but not the tags.
	 * <li><B>Step: </B>Refresh browser page.
	 * <li><B>Verify: </B>The saved bookmark appears at the top of the bookmark list.
	 * <li><B>Step: </B>Copy the second bookmark created to my bookmarks and mark as private.
	 * <li><B>Verify: </B>The saved bookmark is not in Public Bookmarks view.
	 * <li><B>Verify: </B>The saved bookmark is in My Bookmarks view. 
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void copyBookmark() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Avoiding duplicate bookmark names
		String sRandomNumber = Helper.genDateBasedRandVal();
		String sBookmarkTitle1 = Data.getData().MultiplePublicBookmarksTitle1 + " " + sRandomNumber;
		String sBookmarkTitle2 = Data.getData().MultiplePublicBookmarksTitle2 + " " + sRandomNumber;
		String sBookmarkTitle3 = Data.getData().MultiplePublicBookmarksTitle3 + " " + sRandomNumber;

		//Create several Dogear base state object
		BaseDogear bookmark1 = new BaseDogear.Builder(sBookmarkTitle1 ,Data.getData().MultiplePublicBookmarksUrl1)
								.tags(Data.getData().MultiplePublicBookmarksTag1)
								.description(Data.getData().commonDescription + testName)
								.build();
		BaseDogear bookmark2 = new BaseDogear.Builder(sBookmarkTitle2 ,Data.getData().MultiplePublicBookmarksUrl2)
								.tags(Data.getData().MultiplePublicBookmarksTag2)
								.description(Data.getData().commonDescription + testName)
								.build();
		BaseDogear bookmark3 = new BaseDogear.Builder(sBookmarkTitle3 ,Data.getData().MultiplePublicBookmarksUrl3)
								.tags(Data.getData().MultiplePublicBookmarksTag3)
								.description(Data.getData().commonDescription + testName)
								.build();
		
		//Create bookmarks with Atom API
		logger.strongStep("Use API to create bookmarks: " + bookmark1.getTitle() + ", " + bookmark2.getTitle()+ " and "+bookmark3.getTitle());
		log.info("INFO: Create bookmarks: " + bookmark1.getTitle() + ", " + bookmark2.getTitle() + " and "+bookmark3.getTitle() +" using API");
		bookmark1.createAPI(apiOwner);
		bookmark2.createAPI(apiOwner);
	    bookmark3.createAPI(apiOwner);

		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser2.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser2.getDisplayName());
		log.info("INFO: Created by: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser2);
				
		//Go to Public Bookmarks 
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);
		
		//Get original window handle as Add to My Bookmarks open in separate window
		logger.strongStep("Fetch original window handle since Add to My Bookmarks action opens a new window");
		log.info("INFO: Get original window handle since Add to My Bookmarks action opens a new window");
		String originalWindow = driver.getWindowHandle();
		
		//Select Add to My Bookmark from first bookmark created above
		logger.strongStep("Click on Add to My Bookmarks link for bookmark: " + bookmark1.getTitle());
		log.info("INFO: Select Add to My Bookmarks link for bookmark: " + bookmark1.getTitle());
		ui.clickLinkWithJavascript(DogearUI.getBookmarkLink(bookmark1));
				
		// Switch to the Add Bookmark window
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		//Verify original title, description and url are copied to the form but not the tags
		logger.strongStep("Verify that the title for the bookmark: " + sBookmarkTitle1 + " is auto-populated in the Title text box");
		log.info("INFO: Verify correct bookmark title displays in the Title text box of the Add Bookmark form");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_Title + "[value='" + sBookmarkTitle1 + "']"),
				"ERROR: The bookmark title is not correct in the 'Add Bookmark' form");
		
		logger.strongStep("Verify that the bookmark's URL: " + bookmark1.getURL() + " is auto-populated in the URL text box");
		log.info("INFO: Verify correct bookmark URL displays in the URL text box of the Add Bookmark form");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_Url + "[value='" + bookmark1.getURL() + "']"),
				"ERROR: The bookmark URL is not correct in the 'Add Bookmark' form");
		
		logger.strongStep("Verify that the bookmark's description: " + bookmark1.getDescription() + " is auto-populated in the Description text box");
		log.info("INFO: Verify correct bookmark description displays in the Description text box of the Add Bookmark form");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_Description + ":contains('"
				+ bookmark1.getDescription() +"')"), "ERROR: The bookmark description is not correct in the 'Add Bookmark' form");
		
		logger.strongStep("Verify that the Tags text box is empty");
		log.info("INFO: Verify bookmark tags are empty in the Add Bookmark form");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_Tags + "[value='']"),
				"ERROR: The bookmark tags are not properly empty in the 'Add Bookmark' form");
		
		//Click Save
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select the Save button");
		ui.clickSaveButton();
		
		logger.strongStep("Switch back to the original window and refresh it");
		log.info("INFO: Switch back to original window and refresh it");
		driver.switchToWindowByHandle(originalWindow);
		driver.navigate().refresh(); 
		
		//Verify first bookmark is at top of public bookmarks
		logger.strongStep("Verify the saved bookmark appears at the top of the list of Public Bookmarks");
		log.info("INFO: Validate that the saved bookmark displays as the first bookmark in the PUBLIC BOOKMARKS tab");
		Assert.assertTrue(driver.isElementPresent("css=tr[class='lotusFirst '] td h4 a:contains('" + sBookmarkTitle1 + "')"),
				"ERROR: The recently edited bookmark " + sBookmarkTitle1 +
				" did not appear at the top of the public bookmark list");

		//Select Add to My Bookmark from second bookmark created above and make it private
		logger.strongStep("Click on Add to My Bookmarks link for bookmark: " + bookmark2.getTitle());
		log.info("INFO: Select Add to My Bookmarks link for bookmark: " + bookmark2.getTitle());
		ui.clickLink(DogearUI.getBookmarkLink(bookmark2));
		
		// Switch to the Add Bookmark window
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Click on the Private radio button and click on the Save button");
		log.info("INFO: Select the Private radio button for the bookmark and save the changes");
		driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Radio_Private).click();
		ui.clickSaveButton();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		//Verify saved second bookmark is not in public bookmarks
		logger.strongStep("Verify the saved private bookmark does not appear in the list of Public Bookmarks");
		log.info("INFO: Validate that the saved private bookmark is not in the PUBLIC BOOKMARKS tab");
		Assert.assertFalse(driver.isElementPresent("css=tr[class='lotusFirst '] td h4 a:contains('" + sBookmarkTitle2 + "')"),
				"ERROR: The private bookmark " + sBookmarkTitle2 + " appeared in the public bookmarks view" );
		
		//Verify saved second bookmark is only in my bookmarks
		logger.strongStep("Click on MY BOOKMARKS tab");
		log.info("INFO: Select MY BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.strongStep("Verify the saved private bookmark appears in MY BOOKMARKS tab");
		log.info("INFO: Validate that the saved private bookmark is in MY BOOKMARKS tab");
		Assert.assertTrue(driver.isElementPresent("css=tr td h4 a:contains('" + sBookmarkTitle2 + "')"),
				"ERROR: The bookmark " + sBookmarkTitle2 + " did not appear in the My Bookmarks view");
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the addition of a public bookmark to an Activity.
	 * <li><B>Step: </B>Create an activity via API.
	 * <li><B>Step: </B>Create a public bookmark via the GUI.
	 * <li><B>Step: </B>Change view to Details view.
	 * <li><B>Step: </B>Select More Actions > Add to Activity option for the bookmark.
	 * <li><B>Verify: </B>The dialog displays with section to Add to Activity.
	 * <li><B>Step: </B>Select the Activity name.
	 * <li><B>Step: </B>Load Activities component and open the activity.
	 * <li><B>Verify: </B>The bookmark appears in the activity. 
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void addBookmarktoActivity() throws Exception{	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create an activity base state object
		BaseActivity activity = new BaseActivity.Builder("Add Dogear Bookmark 2 Act " + Helper.genDateBasedRand())
								.tags(testName)
								.goal("Goal for "+ testName)
								.build();
		
		//Create activity with Atom API
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIActivitiesHandler activitiesApiOwner =  new APIActivitiesHandler(cfg.getProductName(),
											serverURL, testUser1.getUid(), testUser1.getPassword());
		
		logger.strongStep("Create activity using API");
		log.info("INFO: Create activity using API");		
		activity.createAPI(activitiesApiOwner);
			
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser1.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//Create a Public bookmark
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		String bookmarkURL = Data.getData().commonURL + Helper.genDateBasedRand();
		BaseDogear bookmark = new BaseDogear.Builder(testName +  Helper.genDateBasedRand(), bookmarkURL)
							.tags(Data.getData().commonTag + Helper.genDateBasedRand())
							.description(Data.getData().commonDescription)
							.build();
		
		logger.strongStep("Click on Add a Bookmark button, enter the details and save the changes");
		log.info("INFO: Using Add a Bookmark button create a bookmark");
		ui.create(bookmark);

		//Display for the view will show Details (expanded to show links for each bookmark)
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);
		
		//Get original window handle as Add to Activity open in separate window
		logger.strongStep("Fetch original window handle since Add to Activity action opens a new window");
		log.info("INFO: Get original window handle since Add to Activity action opens a new window");
		String originalWindow = driver.getWindowHandle();
		
		//Select More Actions > Add to Activity from bookmark created above
		logger.strongStep("Select Add to Activity option under More Actions menu for the bookmark: " + bookmark.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + bookmark.getTitle() + " and then select Add to Activity option");
		String UUID = ui.getUUID(bookmark);
		Dogear_MoreActions_Menu.ADD_TO_ACTIVITY.select(ui, UUID);
		
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Activity field appears with the list of activities to add the bookmark to");
		log.info("INFO: Validate that the Activity field appears with the list of activities to add the bookmark to");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_AddToActivity),
				"ERROR: The option to add the bookmark to an activity could not be found");
		
		//In dialog select the Activity create earlier via API
		logger.strongStep("Select the activity: " + activity.getName() + " from the list of activities and click on the Save button");
		log.info("INFO: Click on the activity: " + activity.getName() + " in the list of activities and click on the Save button");
		ui.clickLinkWait("css=select option:contains('" + activity.getName() + "')"); 
		ui.clickSaveButton();
		
		//Verify bookmark is added to activity
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Wait for the Apps menu to be visible and then load the Activities component");
		log.info("INFO: Verify the Apps menu appears within " + cfg.getFluentwaittime() + " seconds and then load the Activities component");
		ui.fluentWaitPresent(BaseUIConstants.MegaMenuApps);
		driver.navigate().to(cfg.getServerURL() + "activities");
		
		logger.strongStep("Click on the link for the activity: " + activity.getName() + " in My Activities page");
		log.info("INFO: Open the activity: " + activity.getName() + " from My Activities page");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		logger.strongStep("Verify the bookmark: " + bookmark.getTitle() + " appears in the Activity's page");
		log.info("INFO: Validate that the bookmark: " + bookmark.getTitle() + " appears in the Activity's page");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()),
				"ERROR: The bookmark was not found in the activity");
				
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the addition of a public bookmark to a Blog.
	 * <li><B>Step: </B>Create a blog via API.
	 * <li><B>Step: </B>Create a public bookmark via the GUI.
	 * <li><B>Step: </B>Change view to Details view.
	 * <li><B>Step: </B>Select More Actions > Add to Blog option for the bookmark.
	 * <li><B>Verify: </B>The dialog displays with section to Add to Blog.
	 * <li><B>Step: </B>Select the Bog name.
	 * <li><B>Step: </B>Load Blogs component and open the blog.
	 * <li><B>Verify: </B>The bookmark appears in the blog. 
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void addBookmarktoBlog() throws Exception{	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a blog base state object
		BaseBlog blog = new BaseBlog.Builder("Add Dogear Bookmark 2 Blog " + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
  						.tags(Data.getData().commonTag + Helper.genDateBasedRand())
  						.description(Data.getData().commonDescription)
  						.theme(Theme.Blog_with_Bookmarks)
  						.build();
		
		//Create blog with Atom API
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIBlogsHandler blogApiOwner = new APIBlogsHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(blogApiOwner);
			
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser1.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//Create a Public bookmark
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		String bookmarkURL = Data.getData().commonURL + Helper.genDateBasedRand();
		BaseDogear bookmark = new BaseDogear.Builder(testName +  Helper.genDateBasedRand(), bookmarkURL)
							.tags(Data.getData().commonTag + Helper.genDateBasedRand())
							.description(Data.getData().commonDescription)
							.build();
		
		logger.strongStep("Click on Add a Bookmark button, enter the details and save the changes");
		log.info("INFO: Using Add a Bookmark button create a bookmark");
		ui.create(bookmark);

		//Display for the view will show Details (expanded to show links for each bookmark)
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);
		
		//Get original window handle as Add to Blog open in separate window
		logger.strongStep("Fetch original window handle since Add to Blog action opens a new window");
		log.info("INFO: Get original window handle since Add to Blog action opens a new window");
		String originalWindow = driver.getWindowHandle();
		
		//Select More Actions > Add to Blog from bookmark created above
		logger.strongStep("Select Add to Blog option under More Actions menu for the bookmark: " + bookmark.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + bookmark.getTitle() + " and then select Add to Blog option");
		String UUID = ui.getUUID(bookmark);
		Dogear_MoreActions_Menu.ADD_TO_BLOG.select(ui, UUID);
		
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Blog field appears with the list of blogs to add the bookmark to");
		log.info("INFO: Validate that the Blog field appears with the list of blogs to add the bookmark to");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_AddToBlog),
				"ERROR: The option to add the bookmark to a blog could not be found");
		
		//In dialog, select the Blog create earlier via API
		logger.strongStep("Select the blog: " + blog.getName() + " from the list of blogs and click on the Save button");
		log.info("INFO: Click on the blog: " + blog.getName() + " in the list of blogs and click on the Save button");
		ui.clickLinkWait("css=select option:contains('" + blog.getName() + "')"); 
		ui.clickSaveButton();
		
		//Verify bookmark is added to blog
		logger.strongStep("Switch back to the original window and wait for the Apps menu to be visible");
		log.info("INFO: Switch back to original window and verify the Apps menu appears within " + cfg.getFluentwaittime() + " seconds");
		driver.switchToWindowByHandle(originalWindow);
		ui.fluentWaitPresent(BaseUIConstants.MegaMenuApps);
		
		logger.strongStep("Open the Blogs component");
		log.info("INFO: Load the Blogs component");
		ui.loadComponent(Data.getData().ComponentBlogs, true);
		
		logger.strongStep("Click on the Blog's link in My Blogs page");
		log.info("INFO: Open the Blog by clicking on its link in My Blogs page");
		ui.clickLinkWait(BlogsUI.getBlog(blog));
		
		logger.strongStep("Verify the Bookmark: " + bookmark.getTitle() + " is visible on the Blog's page");
		log.info("INFO: Validate that the Bookmark: " + bookmark.getTitle() + " appears on the Blog's page");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()), 
				"ERROR: The bookmark was not found in the blog");
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the addition of a public bookmark to a Community.
	 * <li><B>Step: </B>Create a community via API.
	 * <li><B>Step: </B>Create a public bookmark via the GUI.
	 * <li><B>Step: </B>Change view to Details view.
	 * <li><B>Step: </B>Select More Actions > Add to Community option for the bookmark.
	 * <li><B>Verify: </B>The dialog displays with section to Add to Community.
	 * <li><B>Step: </B>Select the Community name.
	 * <li><B>Step: </B>Load Communities component and open the community.
	 * <li><B>Verify: </B>The bookmark appears in the community. 
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void addBookmarktoCommunity() throws Exception{	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder("Add Dogear Bookmark 2 Com " + Helper.genDateBasedRand())
								.tags(Data.getData().commonTag + Helper.genDateBasedRand())
								.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
								.access(Access.PUBLIC)
								.description("Test description for testcase " + testName)
								.build();
		
		//Create community with Atom API
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APICommunitiesHandler commApiOwner = new APICommunitiesHandler(serverURL,
												testUser1.getUid(), testUser1.getPassword());
	
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI=community.createAPI(commApiOwner);
			
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser1.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//Create a Public bookmark
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		String bookmarkURL = Data.getData().commonURL + Helper.genDateBasedRand();
		BaseDogear bookmark = new BaseDogear.Builder(testName +  Helper.genDateBasedRand(), bookmarkURL)
							.tags(Data.getData().commonTag + Helper.genDateBasedRand())
							.description(Data.getData().commonDescription)
							.build();
		
		logger.strongStep("Click on Add a Bookmark button, enter the details and save the changes");
		log.info("INFO: Using Add a Bookmark button create a bookmark");
		ui.create(bookmark);

		//Display for the view will show Details (expanded to show links for each bookmark)
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);
		
		//Get original window handle as Add to Community open in separate window
		logger.strongStep("Fetch original window handle since Add to Community action opens a new window");
		log.info("INFO: Get original window handle since Add to Community action opens a new window");
		String originalWindow = driver.getWindowHandle();
		
		//Select More Actions > Add to Activity of bookmark created above
		logger.strongStep("Select Add to Community option under More Actions menu for the bookmark: " + bookmark.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + bookmark.getTitle() + " and then select Add to Community option");
		String UUID = ui.getUUID(bookmark);
		Dogear_MoreActions_Menu.ADD_TO_COMMUNITY.select(ui, UUID);
		
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Community field appears with the list of communities to add the bookmark to");
		log.info("INFO: Validate that the Community field appears with the list of communities to add the bookmark to");
		Assert.assertTrue(driver.isElementPresent(DogearUIConstants.Form_AddBookmark_AddToCommunity),
				"ERROR: The option to add the bookmark to a community could not be found");
		
		//In dialog, select the Community create earlier via API
		logger.strongStep("Select the community: " + community.getName() + " from the list of communities and click on the Save button");
		log.info("INFO: Click on the community: " + community.getName() + " in the list of communities and click on the Save button");
		ui.clickLinkWait("css=select option:contains('" + community.getName() + "')"); 
		ui.clickSaveButton();
		
		//Verify bookmark is added to community
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		ui.fluentWaitPresent(BaseUIConstants.MegaMenuApps);
		
		logger.strongStep("Open the Communities component");
		log.info("INFO: Load the Communities component");
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			commApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Click on the Community Card for the community: " + community.getName());
		log.info("INFO: Open the community: " + community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community.getName()));
		
		logger.strongStep("Verify the Bookmark: " + bookmark.getTitle() + " is visible on the community's Overview page");
		log.info("INFO: Validate that the Bookmark: " + bookmark.getTitle() + " appears on the community's Overview page");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()), 
				"ERROR: The bookmark was not found in the community");
				
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Tests the addition of a person to Watchlist and inspecting the left navigation region.
	 * <li><B>Step: </B>Create a bookmark via API.
	 * <li><B>Step: </B>Log in as User2.
	 * <li><B>Step: </B>From Public Bookmarks and find User1.
	 * <li><B>Step: </B>In the filtered view of User1's bookmarks, select link Add to Watchlist .
	 * <li><B>Step: </B>Change view to My Updates > My Watchlist.
	 * <li><B>Verify: </B>User1's bookmarks display in My Watchlist view.
	 * <li><B>Verify: </B>User1 is listed in My Watchlist region in left navigation menu.
	 * <li><B>Verify: </B>User1 is listed in My Associated People region in left navigation menu.
	 * <li><B>Step: </B>Log out as User2.
	 * <li><B>Step: </B>Log in as User1.
	 * <li><B>Step: </B>Change view to My Updates > My Watchlist.
	 * <li><B>Verify: </B>User2 is listed under Users that Watchlisted Me heading in left navigation menu.
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regression" })
	public void watchlistSubscribeToAPerson() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
				
		//Create Dogear base state object
		String bookmarkURL = Data.getData().commonURL;
		BaseDogear bookmark = new BaseDogear.Builder(testName +  Helper.genDateBasedRand(), bookmarkURL)
					.tags(Data.getData().commonTag + Helper.genDateBasedRand())
					.description(Data.getData().commonDescription + testName)
					.build();
		
		//Create bookmark with Atom API
		logger.strongStep("Create bookmark using API");
		log.info("INFO: Create bookmark using API");
		bookmark.createAPI(apiOwner);
		
		// Load the component and login as user 2
		logger.strongStep("Open the Dogear component and login as: " + testUser2.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser2);
		
		//Go to Public Bookmarks and find a person to add to the watchlist
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		logger.strongStep("Click on Find a Person link in the People section of the left navigation menu and wait for the 'Type to find person' text box to appear");
		log.info("INFO: Select Find a Person link from the People section of the left navigation menu and wait for the 'Type to find person' text box to appear");
		ui.clickLinkWait(DogearUIConstants.MyBookmarks_People_SearchLink);
		ui.fluentWaitElementVisible(DogearUIConstants.MyBookmarks_People_SearchBox);
		
		//Enter the user1 name
		logger.strongStep("Input the username: " + testUser1.getDisplayName() + " in the 'Type to find person' text box");
		log.info("INFO: Enter the username " + testUser1.getDisplayName() + " in the 'Type to find person' text box");
		String typeaheadText = testUser1.getAttribute(cfg.getTypeaheadPreference().toLowerCase());
		ui.typeText(DogearUIConstants.MyBookmarks_People_SearchBox, typeaheadText);
		
		logger.strongStep("Select the user: " + testUser1.getDisplayName() + " from the typeahead");
		log.info("INFO: Click on the user: " + testUser1.getDisplayName() + " in the typeahead");
		ui.getFirstVisibleElement("css=div[id^='nameinput_popup'][role='option']:contains('" + testUser1.getDisplayName()
						+ " <" + testUser1.getEmail() + ">')").click();
		
		logger.strongStep("Verify the appearance of the filter view to show bookmarks by " + testUser1.getDisplayName());
		log.info("INFO: Validate the appearance of the filter view to show bookmarks by " + testUser1.getDisplayName());
		ui.fluentWaitPresent("css=a.lotusFilter:contains('" + testUser1.getDisplayName() + "')");
		
		logger.strongStep("Click on Add to Watchlist link to add " + testUser1.getDisplayName() + " to watchlist");
		log.info("INFO: Add " + testUser1.getDisplayName() + " to watchlist by clicking on Add to Watchlist link");
		ui.clickLink(DogearUIConstants.MyBookmarks_AddToWatchlist);
		
		//Go to My Watchlist
		logger.strongStep("Click on MY UPDATES tab");
		log.info("INFO: Select MY UPDATES tab");
		ui.clickLink(DogearUIConstants.Nav_MyUpdates);
		
		//Verify the bookmark appears correctly
		logger.strongStep("Verify the bookmark: " + bookmark.getTitle() + " is visible");
		log.info("INFO: Validate that the bookmark: " + bookmark.getTitle() + " is visible");
		Assert.assertTrue(driver.isElementPresent("link=" + bookmark.getTitle()),
				"ERROR: The watchlisted person's bookmark can not be found");
		
		//Verify user is listed in watchlist and People Tab
		logger.strongStep("Verify the user: " + testUser1.getDisplayName() + " appears under My Watchlist in the left navigation menu");
		log.info("INFO: Validate that the user: " + testUser1.getDisplayName() + " is visible in My Watchlist section of the left navigation menu");
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_MyWatchListRegion).isTextPresent(testUser1.getDisplayName()),
				"ERROR: The watchlisted person not found in My Watchlist region of left nav");

		logger.strongStep("Verify the user: " + testUser1.getDisplayName() + " appears under the heading 'My Associated People' in the left navigation menu");
		log.info("INFO: Validate that the user: " + testUser1.getDisplayName() + " is listed under the heading 'My Associated People' in the left navigation menu");		
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.MyUpdates_MyAssociatedPeopleRegion).isTextPresent(testUser1.getDisplayName()),
				"ERROR: The watchlisted person not found in People region of left nav");
	
		logger.strongStep("Logout of the session and log back in as: " + testUser1.getDisplayName());
		log.info("INFO: Logout of the session and log back in as: " + testUser1.getDisplayName());
		ui.logout();
		ui.loadComponent(Data.getData().ComponentDogear, true);
		ui.login(testUser1);
		
		//Go to Watchlist and login as user 1
		logger.strongStep("Click on MY BOOKMARKS tab");
		log.info("INFO: Select MY BOOKMARKS tab");
		ui.clickLinkWait(DogearUIConstants.Nav_MyBookmarks);
		
		//Expand watchlist section in left navigation
		logger.strongStep("Expand My Watchlist section in the left navigation menu");
		log.info("INFO: Click on My Watchlist in the left navigation menu to expand the section");
		ui.clickLinkWait(DogearUIConstants.MyBookmarks_MyWatchList);
		
		//Verify user 2 displays as having watchlisted user1
		logger.strongStep("Verify the user: " + testUser2.getDisplayName() + " appears under the heading 'Users that Watchlisted Me'");
		log.info("INFO: Validate that the user: " + testUser2.getDisplayName() + " appears under the heading 'Users that Watchlisted Me'");
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_UsersWatchlistedMeRegion).isTextPresent(testUser2.getDisplayName()),
				"ERROR: The watcher person not found in Users that watchlisted me region of left nav");

		ui.endTest();
	}

}
