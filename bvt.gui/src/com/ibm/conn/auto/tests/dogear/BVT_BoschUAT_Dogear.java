/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright HCL Technologies Limited 2019,2020                      */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.dogear;

import static org.testng.Assert.assertTrue;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_BoschUAT_Dogear extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Dogear.class);
	private DogearUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private User testUser;
	private String serverURL;
	private APICommunitiesHandler comApiOwner;
	private APIDogearHandler bookmarkOwner;
	private APIActivitiesHandler activityOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load users
		testUser = cfg.getUserAllocator().getUser();			
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		bookmarkOwner = new APIDogearHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), 
				testUser.getPassword());
		activityOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL,
				testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		comApiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = DogearUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify user is able to add bookmark to an Activity and Community</li>
	 * <li><B>Step:</B>Create community using API</li>
	 * <li><B>Step:</B>Create activity using API</li>
	 * <li><B>Step:</B>Create two bookmarks using API</li>
	 * <li><B>Step:</B>Users login to Connections</li>
	 * <li><B>Step:</B>Navigate to Bookmarks from homepage.On the mega-menu, click on Apps > Bookmarks</li>
	 * <li><B>Verify: </B>My Bookmarks page should be displayed with Add a Bookmark, Notify, Delete Selected and More Actions option, along with list of bookmarks</li>
	 * <li><B>Step: </B>Click on 'More' link located at the far right hand side of the bookmark displayed at position 1st in the list</li>
	 * <li><B>Verify: </B>Verify that bookmark should expand displaying the options Edit, Notify Other People, and More Actions</li>
	 * <li><B>Step: </B>Click on 'More Actions' option for bookmark1</li>
	 * <li><B>Verify: </B>Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options</li>
	 * <li><B>Step: </B>Select 'Add to Activity'</li>
	 * <li><B>Verify: </B>Verify the 'Add Bookmark' pop-up dialog should displayed in new window</li>
	 * <li><B>Verify: </B>Verify the required fields (Title and URL) should be automatically pre-populated.</li>
	 * <li><B>Step: </B>Fill in the other fields like description</li>
	 * <li><B>Step: </B>Select the Activity to add the bookmark to and click the 'Save' button</li>
	 * <li><B>Verify: </B>Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page</li>
	 * <li><B>Step: </B>Navigate to activities component and open the activity the bookmark was added to</li>
	 * <li><B>Verify: </B>Verify the bookmark should be displayed under activity</li>
	 * <li><B>Step: </B>Navigate back to 'Bookmarks' app</li>
	 * <li><B>Step: </B>Click on 'More' link located at the far right hand side of the bookmark displayed at position 2nd in the list</li>
	 * <li><B>Step: </B>Click on 'More Actions' option for bookmark2</li>
	 * <li><B>Verify: </B>Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options</li>
	 * <li><B>Step: </B>Select 'Add to Community'</li>
	 * <li><B>Verify: </B>Verify the 'Add Bookmark' pop-up dialog should displayed in new window</li>
	 * <li><B>Verify: </B>Verify the required fields (Title and URL) should be automatically pre-populated.</li>
	 * <li><B>Step: </B>Fill in the other fields like discussion text area</li>
	 * <li><B>Step: </B>Select the Community to add the bookmark to and click the 'Save' button</li>
	 * <li><B>Verify: </B>Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page</li>
	 * <li><B>Step: </B>Navigate to Communities component and open the Community the bookmark was added to</li>
	 * <li><B>Verify: </B>Verify the bookmark should be displayed under community</li>
	 * </ul>
	 */
	@Test(groups = { "regression" })
	public void addBookmarkToActivityAndCommunity() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String bookmarkTags = Data.getData().commonTag + Helper.genStrongRand();
		String bookmarkDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(com.ibm.conn.auto.appobjects.base.BaseCommunity.Access.PUBLIC)
				.description("Test description for testcase " + testName).build();
		
		BaseActivity baseActivityPub = new BaseActivity.Builder(testName+"_Activity_" + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName).isPublic(true).build();
		
		BaseDogear baseBookmark1 = new BaseDogear.Builder(testName+"_1_"+Helper.genDateBasedRandVal(), Data.getData().commonURL)
												.tags(bookmarkTags)
												.description(bookmarkDescription)
												.build();
		BaseDogear baseBookmark2 = new BaseDogear.Builder(testName+"_2_"+Helper.genDateBasedRandVal2(), Data.getData().bbcURL)
				.tags(bookmarkTags)
				.description(bookmarkDescription)
				.build();
		
		// create community using API
		log.info("INFO: Create community using API");
		logger.strongStep(" Create community using API");
		Community comAPI = baseCommunity.createAPI(comApiOwner);
		
		// add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCommunity.getCommunityUUID_API(comApiOwner, comAPI);
		
		// Create activity using API
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		Activity activity = baseActivityPub.createAPI(activityOwner);
	
		// Create two bookmarks using API
		log.info("INFO: Create two bookmarks using API");
		logger.strongStep("Create two bookmarks using API");
		Bookmark bookmark1 = bookmarkOwner.createBookmark(baseBookmark1);
		Bookmark bookmark2 = bookmarkOwner.createBookmark(baseBookmark2);
		log.info("INFO: Bookmark successfully created");
		
		logger.strongStep("Login to Connections System - Homepage");
		log.info("INFO: Login to Connections System - Homepage");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		logger.strongStep("Navigate to Bookmarks from homepage.On the mega-menu, click on Apps > Bookmarks");
		log.info("INFO: Navigate to Bookmarks from homepage.On the mega-menu, click on Apps > Bookmarks");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(DogearUIConstants.bookmarksOption);
		
		logger.strongStep("My Bookmarks page should be displayed with Add a Bookmark, Notify, Delete Selected and More Actions option, along with list of bookmarks");
		log.info("INFO: My Bookmarks page should be displayed with Add a Bookmark, Notify, Delete Selected and More Actions option, along with list of bookmarks");
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.MyBookMarksTab).getAttribute("class").contains("Selected"));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddABookmark));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.MyBookmarks_Notify));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.MyBookmarks_Delete));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.MyBookmarks_MoreActions));
		
		logger.strongStep("Click on 'More' link located at the far right hand side of of the bookmark displayed at position 1st in the list");
		log.info("INFO:Click on 'More' link located at the far right hand side of of the bookmark displayed at position 1st in the list");
		ui.clickLinkWithJavascript(DogearUIConstants.Nav_BookmarksHome_ExpandDetails_Bookmark1);
		
		logger.strongStep("Verify that bookmark should expand displaying the options Edit, Notify Other People, and More Actions");
		log.info("INFO: Verify that bookmark should expand displaying the options Edit, Notify Other People, and More Actions");
		Assert.assertTrue(ui.isElementVisible(DogearUI.BookmarksHome_ExpandDetailsOptions_Bookmark("Edit",1)));
		Assert.assertTrue(ui.isElementVisible(DogearUI.BookmarksHome_ExpandDetailsOptions_Bookmark("Notify Other People",1)));
		Assert.assertTrue(ui.isElementVisible(DogearUI.BookmarksHome_ExpandDetailsOptions_Bookmark("More Actions",1)));
		
		logger.strongStep("Click on 'More Actions' option for bookmark1");
		log.info("INFO: Click on the More Actions menu for the bookmark: " + baseBookmark2.getTitle());
		ui.clickLinkWithJavascript(DogearUI.BookmarksHome_ExpandDetailsOptions_Bookmark("More Actions",1));
		
		logger.strongStep("Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options");
		log.info("INFO: Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options");
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoCommunityLink));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoActivityLink));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoBlogLink));

		logger.strongStep("Fetch original window handle since Add to Activity action opens a new window");
		log.info("INFO: Get original window handle since Add to Activity action opens a new window");
		String originalWindow = driver.getWindowHandle();

		logger.strongStep("Select 'Add to Activity'");
		log.info("INFO: Select 'Add to Activity'");
		ui.clickLinkWithJavascript(DogearUIConstants.AddtoActivityLink);
		
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		logger.strongStep("Verify the 'Add Bookmark' pop-up dialog should displayed in new window");
		log.info("INFO: Verify the 'Add Bookmark' pop-up dialog should displayed in new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.ActivityWidget), "ERROR: activity widget doesn't show up");
		
		logger.strongStep("Verify the required fields (Title and URL) should be automatically pre-populated");
		log.info("INFO: Verify the required fields (Title and URL) should be automatically pre-populated");
		log.info("title is: "+driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).getAttribute("value"));
		log.info("url is: "+driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Url).getAttribute("value"));
		log.info("url is: "+baseBookmark2.getTitle());
		log.info("url is: "+Data.getData().bbcURL);
		
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).getAttribute("value").equals(baseBookmark2.getTitle()));
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Url).getAttribute("value").equals(Data.getData().bbcURL));
		
		logger.strongStep("Fill in the other fields like Additional description ");
		log.info("INFO: Fill "+ testName + " in the other fields like Additional description ");
		driver.getSingleElement(DogearUIConstants.BookmarkletActivityArea).type(testName);
		
		logger.strongStep("Select the Activity to add the bookmark to and click the 'Save' button");
		log.info("ISelect the Activity "+baseActivityPub.getName()+" to add the bookmark to and click the 'Save' button");
		List<Element> activitylist = driver.getElements(DogearUIConstants.ActivityList);
		int i=0;
		for(; i<activitylist.size();i++){
			if(activitylist.get(i).getText().equals(baseActivityPub.getName())) {
				logger.strongStep("Click on the activity created before with name as " + baseActivityPub.getName());
				log.info("INFO: Click on the activity created before with name as " + baseActivityPub.getName());
				activitylist.get(i).click();
				break;
			}
		}		
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page");
		log.info("INFO: Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page");
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.MyBookmarks_Notify));
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(DogearUIConstants.ActivityWidget), "ERROR: activity widget doesn't show up");
		driver.turnOnImplicitWaits();
	
		logger.strongStep("Navigate to activities component and open the activity the bookmark was added to");
		log.info("INFO: Navigate to activities component and open the activity link "+baseActivityPub.getName()+" the bookmark was added to");
		ui.loadComponent("activities",true);
		ui.clickLinkWait("link=" + baseActivityPub.getName());
		
		logger.strongStep("Verify the bookmark: " + baseBookmark2.getTitle() + " appears in the Activity's page");
		log.info("INFO: Validate that the bookmark: " + baseBookmark2.getTitle() + " appears in the Activity's page");
		assertTrue(driver.isTextPresent(baseBookmark2.getTitle()),"ERROR: the bookmark is not added successfully.");

		logger.strongStep("Navigate back to the 'Bookmarks' app");
		log.info("INFO: Navigate back to the 'Bookmarks' app");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		ui.clickLinkWithJavascript(DogearUIConstants.bookmarksOption);
		ui.fluentWaitPresent(DogearUIConstants.AddABookmark);
		
		logger.strongStep("Click on 'More' link located at the far right hand side of of the bookmark displayed at position 2nd in the list");
		log.info("INFO: Click on 'More' link located at the far right hand side of of the bookmark displayed at position 2nd in the list");
		ui.clickLinkWithJavascript(DogearUIConstants.Second_More_Link);
		
		logger.strongStep("Click on 'More Actions' option for bookmark2");
		log.info("INFO: Click on 'More Actions' option for bookmark2");
		ui.clickLinkWithJavascript(DogearUI.BookmarksHome_ExpandDetailsOptions_Bookmark("More Actions", 2));
		
		logger.strongStep("Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options");
		log.info("INFO: Verify a drop box should be displayed with Add to Activity, Add to Community and Add to Blog options");
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoCommunityLink));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoActivityLink));
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.AddtoBlogLink));
		
		logger.strongStep("Select 'Add to Community'");
		log.info("INFO: Select 'Add to Community'");
		ui.clickLinkWithJavascript(DogearUIConstants.AddtoCommunityLink);

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		logger.strongStep("Verify the 'Add Bookmark' should appears in the new window");
		log.info("INFO: Verify the 'Add Bookmark' should appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.CommunityWidget), "ERROR: community widget doesn't show up");
		
		logger.strongStep("Verify the required fields (Title and URL) should be automatically pre-populated");
		log.info("INFO: Verify the required fields (Title and URL) should be automatically pre-populated");
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Title).getAttribute("value").equals(baseBookmark1.getTitle()));
		Assert.assertTrue(driver.getSingleElement(DogearUIConstants.Form_AddBookmark_Url).getAttribute("value").equals(Data.getData().commonURL));
		
		logger.strongStep("Fill in the other fields like discussion text area");
		log.info("INFO: Fill in the "+testName+" other fields like discussion text area");
		driver.getSingleElement(DogearUIConstants.BookmarkletCommunityArea).type(testName);

		logger.strongStep("Select the Community to add the bookmark to and click the 'Save' button");
		log.info("INFO: Select the Community "+baseCommunity.getName()+" to add the bookmark to and click the 'Save' button");
		List<Element> communitylist = driver.getElements(DogearUIConstants.CommunityList);
		int j = 0;
		for (; j < communitylist.size(); j++) {
			if (communitylist.get(j).getText().equals(baseCommunity.getName())) {
				logger.strongStep("Click on the community created before with name as " + baseCommunity.getName());
				log.info("INFO: Click on the community created before with name as " + baseCommunity.getName());
				communitylist.get(j).click();
				break;
			}
		}
		driver.getSingleElement(BaseUIConstants.SaveButton).click();

		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);

		logger.strongStep("Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page");
		log.info("INFO: Verify the 'Add Bookmark' dialog should no longer display and user remains on the 'My Bookmarks' page");
		Assert.assertTrue(ui.isElementVisible(DogearUIConstants.MyBookmarks_Notify));
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(DogearUIConstants.CommunityWidget), "ERROR: community widget is displayed");
		driver.turnOnImplicitWaits();
	
		logger.strongStep("Navigate to Communities component and open the Community the bookmark was added to");
		log.info("INFO: Navigate to Communities component and open the Community "+baseCommunity.getName()+" the bookmark was added to");
		ui.loadComponent("communities", true);
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			Community communitycom = comApiOwner.getCommunity(baseCommunity.getCommunityUUID());
			comApiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
		}
		ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(baseCommunity.getName()));

		logger.strongStep("Verify the bookmark should be displayed under community");
		log.info("INFO: Verify the bookmark should be displayed under community");
		Assert.assertTrue(driver.isTextPresent(baseBookmark1.getTitle()),"ERROR: the bookmark is not added successfully.");

		bookmarkOwner.deleteBookmark(bookmark1);
		bookmarkOwner.deleteBookmark(bookmark2);
		activityOwner.deleteActivity(activity);
		comApiOwner.deleteCommunity(comAPI);
		ui.endTest();

	}

	}
