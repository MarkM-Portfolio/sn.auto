/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.dogear;

import static org.testng.Assert.assertTrue;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;
import com.ibm.conn.auto.webui.cloud.DogearUICloud;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Dogear extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Dogear.class);
	private DogearUI ui;
	private TestConfigCustom cfg;
	private User testUser1;
	private User testUser2;
	private CommunitiesUI cUI;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	private APICommunitiesHandler comApiOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
			
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		comApiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration and GUI
		cfg = TestConfigCustom.getInstance();
		ui = DogearUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Tests items on the Mega Menu</li>
	 *<li><B>Step:</B>Click the Mega Menu item</li>
	 *<li><B>Verify:</B>Check Bookmarks is an option contained in the drop down menu</li>
	 *<li><B>Verify:</B>Check Popular is an option contained in the drop down menu</li>
	 *<li><B>Verify:</B>Check  Public Bookmarks is an option contained in the drop down menu</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt"})
	public void validateMegaMenu() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		// Load the component and login
		logger.strongStep("Load Dogear and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//Click Mega Menu item
		logger.strongStep("Select 'Apps' option on the banner");
		log.info("INFO: Select Bookmarks Mega Menu option");
		ui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate Bookmarks option is contained with in drop down menu
		logger.weakStep("Validate that the 'Bookmarks' option is contained with in drop down menu");
		log.info("INFO: Validate 'Bookmarks' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.bookmarksOption ),
						  "Unable to validate Mega Menu 'Bookmarks' option in drop down menu");

		//Validate Popular option is contained with in drop down menu
		logger.weakStep("Validate that the 'Popular' option is contained with in drop down menu");
		log.info("INFO: Validate 'Popular' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.bookmarksPopular),
						  "Unable to validate Mega Menu 'Popular' option in drop down menu");

		//Validate Public Bookmarks option is contained with in drop down menu
		logger.weakStep("Validate that the 'Public Bookmarks' option is contained with in drop down menu");
		log.info("INFO: Validate 'Public Bookmarks' option is contained with in drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.bookmarksPublicBookmarks),
						  "Unable to validate Mega Menu 'Public Bookmarks' option in drop down menu");

		ui.endTest();
	
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validates four different bookmark views represented in the UI as tabs</li>
	*<li><B>Step:</B>Click on the My Bookmarks Tab</li>
	*<li><B>Verify:</B>Check that all bookmark views are loaded correctly</li>
	*<li><B>Step:</B>Click on the Public Bookmarks Tab</li>
	*<li><B>Verify:</B>Check that all public bookmark views are loaded correctly</li>
	*<li><B>Step:</B>Click on the Popular Tab</li>
	*<li><B>Verify:</B>Check that all popular views are loaded correctly</li>
	*<li><B>Step:</B>Click on the My Updates Tab</li>
	*<li><B>Verify:</B>Check that all components of the My Updates tab are loaded correctly</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "level2", "cnx8ui-level2",  "bvt"})
	public void verifyAllBookmarkViews() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		// Load the component and login
		logger.strongStep("Load Dogear and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		// Click on the My Bookmarks Tab and verify the page
		logger.strongStep("Click on the 'My Bookmarks' Tab and verify the page");
		log.info("INFO: Select My Bookmarks Tab");
		ui.clickLink(DogearUIConstants.Nav_MyBookmarks);
		
		logger.weakStep("Validate 'My Bookmarks' tab");
		log.info("INFO: Validate My Bookmarks tab");
		assertTrue(driver.getFirstElement(DogearUIConstants.HeaderTextHolder).getAttribute("innerText").contains("My Bookmarks"),
				   "ERROR: My Bookmarks tab is not present");


		// Click on Public Bookmarks tab and verify the page
		logger.strongStep("Select 'Public Bookmarks' tab");
		log.info("INFO: Select Public Bookmarks tab");
		ui.clickLink(DogearUIConstants.Nav_PublicBookmarks);
		
		log.info("INFO: Validate Public Bookmarks tab");
		logger.weakStep("Validate 'Public Bookmarks' tab");
		assertTrue(driver.getFirstElement(DogearUIConstants.HeaderTextHolder).getAttribute("innerText").contains("Public Bookmarks"),
				   "ERROR: Public Bookmarks text not present");


		// Click on Popular tab and verify the pagestep
		logger.strongStep("Click on the 'Popular Bookmarks' tab");
		log.info("INFO: Select Popular Bookmarks tab");
		ui.clickLink(DogearUIConstants.Nav_Popular);
		
		logger.weakStep("Validate that 'Popular' tab displays ");
		log.info("INFO: Validate the page");
		assertTrue(driver.getFirstElement(DogearUIConstants.Bookmarks_Main_Content).isTextPresent("Popular"),
				   "ERROR: Popular tab is not present");


		// Click on My Update tab and verify the page
		logger.strongStep("Select My Update tab");
		log.info("INFO: Select My Update tab");
		ui.clickLink(DogearUIConstants.Nav_MyUpdates);
		
		logger.weakStep("Validate that 'Update' tag displays 'Watchlist'");
		log.info("INFO: Validate the page");
		assertTrue(driver.getFirstElement(DogearUIConstants.HeaderTextHolder).getAttribute("innerText").contains("Watchlist for " + testUser1.getDisplayName()),
				   "ERROR: My Update tab does not display Watchlist");

		// Logout of Connections
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>Create a public bookmark and view it from Public Bookmarks</li>
	*<li><B>Step:</B>Create a public bookmark to Google from My Bookmarks view</li>
	*<li><B>Step:</B>Switch to Public Bookmarks view</li>
	*<li><B>Verify:</B>That the created bookmark displays in Public Bookmarks view</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "level1", "level2", "bvt", "smoke","cnx8ui-level2"})
	public void createPublicBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String dateRandVal = Helper.genDateBasedRandVal();
		String testName = ui.startTest() + dateRandVal;
		String url = Data.getData().commonURL + dateRandVal;
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription + testName).build();

		// Load the component and login
		logger.strongStep("Load dogear and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Create public bookmark to Google with tags and description from My Bookmarks view
		logger.strongStep("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		ui.create(bookmark);
		
		//Switch to Public Bookmarks view and verify public bookmark is there as well
		logger.strongStep("Switch to 'Public Bookmarks' view and verify that 'public bookmarks' is there as well");
		log.info("INFO: Switch to Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		//reliability check
		logger.strongStep("If the bookmark is not present refresh the browser");
		if(driver.isTextNotPresent(testName))
		{
			log.info("INFO:Bookmark not detected, ...refreshing browser ");
			driver.navigate().refresh();
		}
		
		logger.weakStep("Validate that the bookmark displays in the 'Public Bookmarks' view");
		log.info("INFO: Validate bookmark: " +testName+ " displays in Public Bookmarks view");
		assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()), 
				   "ERROR: Bookmark: " + bookmark.getTitle() + " not found");

		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests that a public bookmark works between two users</li>
	*<li><B>Step:</B>Logged in as user 1, create a public bookmark</li>
	*<li><B>Step:</B>Logout and Login as user 2</li>
	*<li><B>Step:</B>Logged in as user 2, add the created public bookmark to My Bookmarks view</li>
	*<li><B>Verify:</B>Check that the public bookmark is now in User2 My Bookmarks view</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "regression", "bvt"})
	public void addPublicBookmarkToMyBookmarks() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.nfl.com" + Helper.genDateBasedRandVal();
		BaseDogear bookmark = new BaseDogear.Builder(testName, url)
											.description("Description for " + testName)
											.build();
		
		//User1 creates bookmark via API
		logger.strongStep("User " +testUser1.getDisplayName() +" creates a bookmark ");
		log.info("INFO: Create bookmark via API");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		bookmark.createAPI(apiOwner);
	
		//Load the component
		logger.strongStep("Load Dogear and login with User " +testUser2.getDisplayName());
		log.info("INFO: Login with user " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser2);
		
		// User2 adds bookmark to My Bookmarks view
		logger.strongStep("User " +testUser2.getDisplayName() +" adds bookmarks to 'My Bookmarks' View");
		ui.addToMyBookmarks(bookmark);
		
		//Switch to My Bookmarks view and verify the bookmark was added and displays in the view
		logger.strongStep("Switch to 'My Bookmarks' View");
		log.info("INFO: Switch to My Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		logger.weakStep("Validate that bookmark displays in 'My Bookmarks' view");
		log.info("INFO: Validate bookmark: " + bookmark.getTitle() + " displays in My Bookmarks view");
		assertTrue(ui.fluentWaitTextPresentRefresh(bookmark.getTitle()), 
				   "ERROR: Bookmark: " + bookmark.getTitle() + " not found");

		//User1 deletes bookmark as a part of cleanup
		logger.strongStep("User " +testUser1.getDisplayName() +" deletes bookmark");
		ui.logout();
		ui.waitForPageLoaded(driver);
		
		//Security related -- Navigate back to dogear component.
		if(cfg.getSecurityType().equalsIgnoreCase("false"))
		{
			//wait for log out and then select My Bookmarks to receive login page
			try {
				log.info("INFO: Login with user " + testUser1.getDisplayName());
				ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
				ui.waitForPageLoaded(driver);
				
			} catch (TimeoutException te) {
				// sometimes the Log in link doesn't show quick enough and since logout()
				// deletes cookies, UI is bounced back to the Log In page immediately
				log.info("INFO: Log In link not found. Check if user is already in Log In page");
				ui.fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
				log.info("INFO: User is already in Log In page");
			}
			ui.login(testUser1);
		}
		else
		{			
			driver.close();
			
			//wait for log out and then load My Bookmarks to delete the bookmark
			logger.strongStep("Login to My Bookmarks as User A");
			log.info("INFO: Login with user " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentDogear);
			ui.login(testUser1);			
		}

		logger.strongStep("User " +testUser1.getDisplayName() +" logins and deletes the bookmark");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		log.info("INFO: Delete the bookmark");
		ui.delete(bookmark);
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests that a private bookmark wont appear in Pulic Bookmarks view</li>
	*<li><B>Step:</B>Create a private bookmark from My Bookmarks view</li>
	*<li><B>Step:</B>Switch to Public Bookmark view</li>
	*<li><B>Verify:</B>Check the bookmark only displays in My Bookmarks view and not in Public Bookmarks view</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "cplevel2", "regression", "bvt"})
	public void createPrivateBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.mars.com";

		BaseDogear bookmark = new BaseDogear.Builder(testName , url).access(Access.RESTRICTED).build();

		//Load component and login
		logger.strongStep("Load Dogear and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		//User1 creates a private bookmark from My Bookmarks view
		logger.strongStep("Switch to 'Public Bookmarks' view and create a bookmark");
		log.info("INFO: Switch to Public Bookmarks view and create bookmark.");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		ui.create(bookmark);
		
		//Switch to Public Bookmark  view and verify the private bookmark isn't there
		logger.strongStep("Switch to 'Public Bookmarks' view");
		log.info("INFO: Switch to Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		logger.weakStep("Verify that private bookmark does not display in the 'Public Bookmarks' View");
		assertTrue(driver.isTextNotPresent(testName), "Bookmark: " +testName+ " was found");
		log.info("INFO: Verified private bookmark: " +testName+ " doesn't display in Public Bookmarks view");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Adds and removes a user from the Watchlist</li>
	*<li><B>Step:</B>From within My Bookmarks view, click the Add to Watchlist button</li>
	*<li><B>Verify:</B>Check that the current user is added to the watchlist </li>
	*<li><B>Step:</B>Click the Remove from Watchlist button</li>
	*<li><B>Verify:</B>Check that the current user is removed from the watchlist</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "regression", "bvt"})
	public void addCurrentUserToWatchList() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
			
		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Dogear and Login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
				
		//add to watchlist and then remove
		logger.strongStep("Switch to 'Public Bookmarks; view and create bookmark");
		log.info("INFO: Switch to Public Bookmarks view and create bookmark.");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		ui.addAndRemoveFromWatchList(testUser1);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Chagnes the title of a public bookmark</li>
	*<li><B>Step:</B>Create a public bookmark</li>
	*<li><B>Step:</B>Edit the bookmark's name</li>
	*<li><B>Verify:</B>Check the name of the bookmark has changed</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "cplevel2", "level2", "cnx8ui-level2", "bvt"})
	public void editPublicBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.ford.com";
		String editTitle = "EDITED: " + testName;

		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
											.description("Description for " + testName)
											.build();

		logger.strongStep("Create a bookmark via API");
		log.info("INFO: Create bookmark via API");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		bookmark.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load Dogear and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		// Edit the title of existing bookmark
		logger.strongStep("Edit the title of the existing bookmark");
		ui.editBookmarkTitle(testName, editTitle);

		// Verify that the bookmark is present
		logger.weakStep("Validate that the renamed bookmark title displays in 'My Bookmarks' view");
		log.info("INFO: Validate renamed bookmark: " + editTitle + " displays in My Bookmarks view");
		assertTrue(driver.isTextPresent(editTitle), 
				   "ERROR: Bookmark: " +editTitle+ " was found");


		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Deletes a bookmark created from My Bookmarks view</li>
	*<li><B>Step:</B>Create a public bookmark from My Bookmarks view</li>
	*<li><B>Step:</B>Delete the bookmark</li>
	*<li><B>Verify:</B>Check that the bookmark is deleted</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Native Dogear therefore it will not work in that environment
	*/
	@Test(groups = { "level2", "cnx8ui-level2", "bvt"})
	public void deletePublicBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "www.tivo.com";
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
											.description("Description for " + testName)
											.build();

		log.info("INFO: Create bookmark");
		logger.strongStep("Create bookmark");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		bookmark.createAPI(apiOwner);
		
		//Load component and login
		logger.strongStep("Load Dogear and Login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//User1 deletes the bookmark and verify it is no longer present
		logger.strongStep("Switch to 'My Bookmarks' view");
		log.info("INFO: Switch to 'My Bookmarks' view");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		
		log.info("Click on Delete Selected button");
		logger.strongStep("Click on Delete Selected button");
		ui.clickLinkWait(DogearUIConstants.MyBookmarks_Delete);
		ui.fluentWaitTextPresent("Select one or more bookmarks before performing this action.");
		
		log.info("Verify text 'Select one or more bookmarks before performing this action.' is displayed");
		logger.strongStep("Verify text 'Select one or more bookmarks before performing this action.' is displayed");
		assertTrue(ui.isTextPresent("Select one or more bookmarks before performing this action."));
		ui.clickLinkWait(DogearUIConstants.ConfirmBookmarkDelete);
		
		logger.strongStep("Delete the bookmark");
		log.info("INFO: Delete the bookmark");
		ui.delete(bookmark);

		logger.weakStep("Verify that the bookmark was deleted");
		log.info("INFO: Verify bookmark: " + bookmark.getTitle() + " was deleted");
		assertTrue(ui.fluentWaitTextNotPresent(bookmark.getTitle()), 
				   "ERROR: Bookmark: " +testName+ " was found");

		ui.endTest();
	}

	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	/**
	 *
	 *<ul>
	 *<li><B>Info: </B>Test the Bookmark function is working inside of a community</li>
	 *<li><B>Step: </B>Create a new community using API</li> 
	 *<li><B>Step: </B>Add a Bookmark to the community</li> 
	 *<li><B>Verify: </B>Verify that the Bookmark was added to the community, and has the correct information.</li>
	 *<li><B>CleanUp:</B> Delete the Community.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void bookmarkCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();			

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .tags(Data.getData().commonTag)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .description("Test Community for " + testName).build();

		BaseDogear bookmark = new BaseDogear.Builder(testName , Data.getData().IbmURL + "/us/en/")
											.community(community)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription + testName)
											.build();


		//create community
		logger.strongStep("Create a community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);
				
		//GUI
		//Login
		logger.strongStep("Load communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		logger.strongStep("Navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		// Navigate to "Bookmarks" tab on the left
		logger.strongStep("Click on the 'Bookmarks' tab");
		log.info("INFO: Clicking the Bookmarks tab");
		Community_LeftNav_Menu.BOOKMARK.select(cUI);
		
		//Wait until the "add bookmark" button is visible
		logger.weakStep("Wait for the 'Add Bookmark' button to become visible");
		log.info("INFO: Clicking on "+Community_LeftNav_Menu.BOOKMARK);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
					     "ERROR: The button to add a bookmark was not found");
		//Create a bookmark
		logger.strongStep("Add a bookmark to the community");
		log.info("INFO: Add a bookmark to the community");
		ui.create(bookmark);
		
		//Verify that the bookmark was created successful
		logger.weakStep("Validate that the bookmark was created");
		log.info("INFO: Checking that the bookmark was generated successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()), 
				   		 "ERROR: The bookmark was not generated successfully");
		
		//Click the link generated with the URL in the bookmark
		logger.strongStep("Click on the generated bookmark URL");
		log.info("INFO: Clicking on link to go to URL");
		ui.clickLink(DogearUIConstants.bookmarkLink);
		
		//assert whether the page we went to is the page specified in the bookmark
		logger.weakStep("Validate that the bookmark link is the same as the page navigated to");
		log.info("INFO: Comparing the URL in the bookmark to the current webpage's URL.");
		Assert.assertFalse(bookmark.getURL().contains(driver.getCurrentUrl()), 
				  "ERROR: The bookmark link is not the same as the page navigated to");
		
		logger.strongStep("Navigate back to the community using the 'Back' option");
		log.info("INFO: Navigate back to the community use back option");
		driver.navigate().back();

		//delete community
		logger.strongStep("Delete the community");
		log.info("INFO: Removing community");
		comApiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	

	
	/**
	 *
	 *<ul>
	 *<li><B>Info: </B>Tests the ability to cancel the creation of a bookmark</li>
	 *<li><B>Step: </B>Create a new community</li> 
	 *<li><B>Step: </B>Go to the "Add bookmark" page</li> 
	 *<li><B>Verify: </B>Verify that the "Add bookmark" page has all the required text input boxes</li>
	 *<li><B>Step: </B>Click the "Cancel" Button</li>
	 *<li><B>Verify: </B>Verify the title doesn't exist</li>
	 *<li><B>Verify: </B>Verify the bookmark was not created</li>
	 *<li><B>CleanUp: </B>Delete the Community</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void bookmarkCreationCancelValidation() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();			

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .tags(Data.getData().commonTag)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .description("Test Community for " + testName).build();

		// The bookmark001test is there because the community is named with the name of this test as well,
		// and when you check for the name in the community, you would have it fail the test because t
		BaseDogear bookmark = new BaseDogear.Builder("Bookmark001test"+testName , Data.getData().IbmURL)
											.community(community)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription + testName)
											.build();


		//GUI
		//Login
		logger.strongStep("Load communities and login: " +testUser1.getDisplayName());
		log.info("INFO: Login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//Create A Community
		logger.strongStep("Create a community");
		log.info("INFO: Create community");
		community.create(cUI);
		
		// Navigate to "Bookmarks" tab on the left
		logger.strongStep("Click on the 'Bookmarks' tab");
		log.info("INFO: Clicking the Bookmarks tab");
		Community_LeftNav_Menu.BOOKMARK.select(cUI);
		
		//Wait until the "add bookmark" button is visible
		logger.weakStep("Wait for the 'Add Bookmark' button to become visible");
		log.info("INFO: Clicking on "+Community_LeftNav_Menu.BOOKMARK);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
				   		  "ERROR: The button to add a bookmark was not found");
		
		//click on the "add bookmark" button
		logger.strongStep("Click on the 'Add Bookmark' button");
		log.info("INFO: Clicking on the "+ DogearUIConstants.AddBookmark+" button");
		ui.clickLinkWait(DogearUIConstants.AddBookmark);
				
		//FORM VALIDATION
		logger.weakStep("Begin validation of web form");
		log.info("INFO: Beginning validation of web form.");
		// Check that all the fields are visible / exist
		logger.weakStep("Test that the URL input field exists");
		log.info("INFO: Testing that the URL input field exists");
		Assert.assertTrue(ui.fluentWaitElementVisible(DogearUICloud.Form_AddBookmark_Url), 
						"ERROR: Input box for the URL was not found");
		
		logger.weakStep("Test that the URL input field exists");
		log.info("INFO: Testing that the URL input field exists");
		Assert.assertTrue(ui.fluentWaitElementVisible(DogearUICloud.Form_AddBookmark_Title), 
				   		  "ERROR: Input box for the title was not found");
		
		logger.weakStep("Test that the URL input field exists");
		log.info("INFO: Testing that the URL input field exists");
		Assert.assertTrue(ui.fluentWaitElementVisible(DogearUICloud.Form_AddBookmark_Description), 
						  "ERROR: Input box for the description was not found");
		
		logger.weakStep("Test that the URL input field exists");
		log.info("INFO: Testing that the URL input field exists");
		Assert.assertTrue(ui.fluentWaitElementVisible(DogearUICloud.Form_AddBookmark_Tags), 
						 "ERROR: Input box for the tags was not found");
		
		//Check for the title field is available
		logger.strongStep("Entering information into the fields");
		log.info("INFO: Entering information into the fields");
		
		//Enter the text into the fields
		logger.strongStep("Add URL to the bookmark");
		log.info("INFO: Adding URL:" + bookmark.getURL());
		driver.getSingleElement(DogearUICloud.Form_AddBookmark_Url).type(bookmark.getURL());

		//Add a Title to the bookmark
		logger.strongStep("Add title to bookmark");
		log.info("INFO: Adding title:" + bookmark.getTitle());
		driver.getSingleElement(DogearUICloud.Form_AddBookmark_Title).type(bookmark.getTitle());
		if(bookmark.getDescription()!= null){
			logger.strongStep("Enter bookmark description");
			log.info("INFO: Entering bookmark description "+bookmark.getDescription());
			driver.getSingleElement(DogearUICloud.Form_AddBookmark_Description).type(bookmark.getDescription());
		}
		if(bookmark.getTags()!= null){
			logger.strongStep("Enter bookmark tags");
			log.info("INFO: Entering bookmark tags: "+bookmark.getTags());
			driver.getSingleElement(DogearUICloud.Form_AddBookmark_Tags).type(bookmark.getTags());
		}
		
		//Checks for the cancel button
		logger.weakStep("Validate that cancel button exists");
		log.info("INFO: Validate cancel button exists");
		Assert.assertTrue(ui.fluentWaitElementVisibleOnce(DogearUIConstants.commAddBookmark_Cancel),
						  "ERROR: The button to cancel the creation of this bookmark is not visible");
		
		//Cancels the bookmark
		logger.strongStep("Click on the cancel button");
		log.info("INFO: Clicking the cancel button");
		ui.clickLink(DogearUIConstants.commAddBookmark_Cancel);

		//go back to bookmarks page, search to see if our bookmark was created
		logger.strongStep("Select Bookmarks from left navigation menu");
		log.info("INFO: Select Bookmarks from left naviagtion menu");
		Community_LeftNav_Menu.BOOKMARK.select(cUI);
		
		//Checks to see that the bookmark was not created
		logger.weakStep("Validate that the bookmark title does not exist");
		log.info("INFO: Validate that the bookmark title does not exist");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(bookmark.getTitle()), 
				 		 "ERROR: The bookmark was generated when it should have been canceled");

		//delete community
		logger.strongStep("Delete the community");
		log.info("INFO: Removing community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}
}
