package com.ibm.conn.auto.tests.dogear.regression;

import static org.testng.Assert.assertTrue;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
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
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class FVT_BookmarkMoreActions extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(FVT_BookmarkMoreActions.class);
	private DogearUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6;
	private APIDogearHandler apiOwner1, apiOwner2, apiOwner3, apiOwner4, apiOwner5, apiOwner6;
	private String serverURL;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();

		//Load users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		testUser5 = cfg.getUserAllocator().getUser();
		testUser6 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner1 = new APIDogearHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		apiOwner2 = new APIDogearHandler(serverURL, testUser2.getUid(), testUser2.getPassword());
		apiOwner3 = new APIDogearHandler(serverURL, testUser3.getUid(), testUser3.getPassword());
		apiOwner4 = new APIDogearHandler(serverURL, testUser4.getUid(), testUser4.getPassword());
		apiOwner5 = new APIDogearHandler(serverURL, testUser5.getUid(), testUser5.getPassword());
		apiOwner6 = new APIDogearHandler(serverURL, testUser6.getUid(), testUser6.getPassword());
		
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
	*<li><B>Info: </B>Tests the tags in public and private bookmarks.
	*<li><B>Step: </B>Login to create public bookmarks with tags.
	*<li><B>Step: </B>Create 5 public bookmarks with tags.
	*<li><B>Step: </B>Create 3 private bookmarks with tags.
	*<li><B>Verify: </B>The bookmarks and tags have been created.
	*<li><B>Verify: </B>The "No selection" message window prompts correctly.
	*<li><B>Step: </B>Deselect all bookmarks.
	*<li><B>Step: </B>Reload the page.
	*<li><B>Verify: </B>The target element is the first element.
	*<li><B>Step: </B>Add tags for all my bookmarks.
	*<li><B>Step: </B>Replace tags for all my bookmarks.
	*<li><B>Step: </B>Delete tags for all my bookmarks.
	*<li><B>Verify: </B>The all tags have been deleted.
	*@throws Exception
	*</ul>
	*/	
	@Test(groups = {"regression"})
	
	public void VerifyBookmarkTags() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseDogear[] publicBookmarks = new BaseDogear[5];
		BaseDogear[] privateBookmarks = new BaseDogear[3];
		String PUBLIC_BOOKMARK_TITLE = Data.getData().PublicBookmarkTitle;
		String PUBLIC_BOOKMARK_TAG = Data.getData().PublicBookmarkTag;
		String PUBLIC_BOOKMARK_url = Data.getData().PublicBookmarkURL;
		String PRIVATE_BOOKMARK_TITLE = Data.getData().PrivateBookmarkTitle;
		String PRIVATE_BOOKMARK_TAG = Data.getData().PrivateBookmarkTag;
		String PRIVATE_BOOKMARK_URL = Data.getData().PrivateBookmarkURL;
		String ADDTAGS = Data.getData().TagForMyBookmarks;

		ui.startTest();
		
		logger.strongStep("Create 5 public bookmarks with tags");
		log.info("INFO: Start of creating public bookmarks with tags");	

		//Create 5 public bookmarks with tags, the same tag - PUBLIC_BOOKMARK_TAG, and another special tag for each bookmark
		for(int i=0; i<5; i++){
			// Enter text into the forms
			
			publicBookmarks[i] = new BaseDogear.Builder(PUBLIC_BOOKMARK_TITLE + String.valueOf(i), PUBLIC_BOOKMARK_url + String.valueOf(i))
														.tags(PUBLIC_BOOKMARK_TAG + "," + PUBLIC_BOOKMARK_TAG + Helper.genDateBasedRand())
														.description(Data.getData().commonDescription + PUBLIC_BOOKMARK_TITLE+String.valueOf(i))
														.build();
			//create bookmark
			logger.strongStep("Create public bookmark " + publicBookmarks[i].getTitle() + "using API");
			log.info("INFO: Create public bookmark " + publicBookmarks[i].getTitle() + "using API");
			publicBookmarks[i].createAPI(apiOwner1);
		 }
		
		//Create 3 private bookmarks with tags, the same tag - PRIVATE_BOOKMARK_TAG, and another special tag for each bookmark
		for(int j=0; j<3; j++){
			// Enter text into the forms
			privateBookmarks[j] = new BaseDogear.Builder(PRIVATE_BOOKMARK_TITLE + String.valueOf(j), PRIVATE_BOOKMARK_URL+String.valueOf(j))
			                      .tags(PRIVATE_BOOKMARK_TAG + "," + PRIVATE_BOOKMARK_TAG + Helper.genDateBasedRand())
			                      .description(Data.getData().commonDescription + PRIVATE_BOOKMARK_TITLE+String.valueOf(j))
			                      .access(Access.RESTRICTED).build();

			//create bookmark
			logger.strongStep("Create private bookmark " + privateBookmarks[j].getTitle() + "using API");
			log.info("INFO: Create private bookmark " + privateBookmarks[j].getTitle() + "using API");
			privateBookmarks[j].createAPI(apiOwner1);
			
		}
		
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser1.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		for(int i=0; i<5; i++){
			String multiTags[] = publicBookmarks[i].getTags().split(",");			
			for(int b=0; b>=multiTags.length; b++){
				logger.strongStep("Verify the Public Bookmark tag: "+ multiTags[b]);
				log.info("INFO: Validating Public Bookmark tag: "+ multiTags[b]);
				assertTrue(driver.isTextPresent(multiTags[b]), "Bookmark: " + publicBookmarks[i].getTags() + " not found");
			}
		}
		for(int j=0;j<3;j++){
			//Verify those bookmarks are present	
			logger.strongStep("Confirm that the bookmark: " + privateBookmarks[j].getTitle() + " displays in My Bookmarks view");
			log.info("INFO: Verify the bookmark: " + privateBookmarks[j].getTitle() + " displays in My Bookmarks view");
			assertTrue(driver.isTextPresent(privateBookmarks[j].getTitle()), "Bookmark: " + privateBookmarks[j].getTitle() + " not found");
		}
		
		//Select MY BOOKMARKS tab
		logger.strongStep("Click on MY BOOKMARKS tab");
		log.info("INFO: Select MY BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.MyBookmarks);

		//Deselect all bookmarks
		logger.strongStep("Click on Deselect All link and then hit the Delete Selected button");
		log.info("INFO: Deselect all bookmarks and then hit the Delete Selected button");
		ui.clickLink(DogearUIConstants.MyBookmarks_DeselectAll);
		driver.getSingleElement(DogearUIConstants.MyBookmarks_Delete).click();

		logger.strongStep("Verify that the Deleting Bookmarks popup appears and contains the text 'Select one or more bookmarks before performing this action.'");
		log.info("INFO: Validate that the Deleting Bookmarks popup appears and contains the text 'Select one or more bookmarks before performing this action.");
		assertTrue(driver.getFirstElement(DogearUIConstants.MyBookmarks_NoBookmarkSelectedWhileDeleting).isTextPresent("Select one or more bookmarks before performing this action."),
					"Error: Delete Bookmarks - No Bookmark selected confirm message shows incorrectly.");
	  
		logger.strongStep("Close the Deleting Bookmarks popup and click on the Notify button");
		log.info("INFO: Dismiss the Deleting Bookmarks popup and click on the Notify button");
		ui.clickLink(DogearUIConstants.MyBookmarks_CloseNoBookmarkMsg);
		ui.fluentWaitPresent(DogearUIConstants.MyBookmarks_Notify);
		driver.getSingleElement(DogearUIConstants.MyBookmarks_Notify).click();

		logger.strongStep("Verify that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.'");
		log.info("INFO: Validate that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.");
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_NoBookmarkSelectedMsg).isTextPresent("Select one or more bookmarks before performing this action."),
					"Error: Bookmarks Notify- No Bookmark selected confirm message shows incorrectly.");
		
		logger.strongStep("Close the Message popup and select Add Tag(s) option under More Actions menu");
		log.info("INFO: Dismiss the Message popup and select Add Tag(s) option under More Actions menu");
		ui.clickLink(DogearUIConstants.MyBookmarks_CloseNoBookmarkMsg);
		ui.clickLink(DogearUIConstants.MyBookmarks_MoreActions);
		driver.getSingleElement(DogearUIConstants.MyBookmarks_AddTags).click();

		logger.strongStep("Verify that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.'");
		log.info("INFO: Validate that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.");
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_NoBookmarkSelectedMsg).isTextPresent("Select one or more bookmarks before performing this action."),
					"Error: Add Tags - No Bookmark selected confirm message shows incorrectly.");
		
		logger.strongStep("Close the Message popup");
		log.info("INFO: Dismiss the Message popup");
		ui.clickLink(DogearUIConstants.MyBookmarks_CloseNoBookmarkMsg);
		
		//Reload the page to make sure the target element is the first element
		logger.strongStep("Click on MY BOOKMARKS tab to reload the page");
		log.info("INFO: Select MY BOOKMARKS tab to reload the page");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.MyBookmarks);

		logger.strongStep("Select Replace Tag option under More Actions menu");
		log.info("INFO: Click on More Actions menu and then select Replace Tag option");
		ui.clickLink(DogearUIConstants.MyBookmarks_MoreActions);
		driver.getFirstElement(DogearUIConstants.MyBookmarks_ReplaceTag).click();

		logger.strongStep("Verify that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.'");
		log.info("INFO: Validate that the Message popup appears and contains the text 'Select one or more bookmarks before performing this action.");
		assertTrue(driver.getSingleElement(DogearUIConstants.MyBookmarks_NoBookmarkSelectedMsg).isTextPresent("Select one or more bookmarks before performing this action."),
					"Error: Add Tags - No Bookmark selected confirm message shows incorrectly.");
		
		logger.strongStep("Close the Message popup");
		log.info("INFO: Dismiss the Message popup");
		ui.clickLink(DogearUIConstants.MyBookmarks_CloseNoBookmarkMsg);

		//Add tags for all my bookmarks
		logger.strongStep("Add tags for all bookmarks under My Bookmarks");
		log.info("INFO: Add tags for bookmarks under My Bookmarks");
		ui.addTagsForAllMyBookmarks(ADDTAGS);
		
		//Replace tags from all my bookmarks
		logger.strongStep("Change the tag for all bookmarks to " + PRIVATE_BOOKMARK_TAG);
		log.info("INFO: Replace the tag for all bookmarks with " + PRIVATE_BOOKMARK_TAG);
		ui.replaceTagFromMyBookmarks(PUBLIC_BOOKMARK_TAG, PRIVATE_BOOKMARK_TAG);
		
		//Delete Tags from all my bookmarks
		logger.strongStep("Delete all tags from My Bookmarks");
		log.info("INFO: Delete all tags from My BookmarksS");
		ui.deleteTagsFromMyBookmarks(ADDTAGS);
		
		//Delete all my testing bookmarks
		logger.strongStep("Delete all bookmarks from My Bookmarks");
		log.info("INFO: Delete all bookmarks from My Bookmarks");
		ui.deleteAllMyBookmarks();

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a bookmark to a community as the owner.
	*<li><B>Step: </B>Create a community.
	*<li><B>Step: </B>Create a bookmark.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Click the more action link under the bookmark.
	*<li><B>Step: </B>Add it to the community as an important bookmark.
	*<li><B>Verify: </B>The community widget shows up.
	*<li><B>Verify: </B>The bookmark shows in My community.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void verifyAddBookmarkToMyOwnCommunity() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();	
		
		log.info("INFO: Start of creating community and bookmarks with tags");
		BaseCommunity community = new BaseCommunity.Builder(testName)
												  .tags(Data.getData().commonTag)
												  .description("Test Community for " + testName).build();
		
		BaseDogear publicBookmarks = new BaseDogear.Builder("Bookmark" + testName, "www.tivo.com")
											.tags(testName)
											.description(Data.getData().commonDescription + testName)
											.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		APICommunitiesHandler commapiOwner = new APICommunitiesHandler(serverURL, testUser2.getUid(), testUser2.getPassword());
		Community comAPI=community.createAPI(commapiOwner);
		
		//create bookmark
		logger.strongStep("Create public bookmark " + publicBookmarks.getTitle() + "using API");
		log.info("INFO: Create public bookmark " + publicBookmarks.getTitle() + "using API");
		publicBookmarks.createAPI(apiOwner2);
		
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser2.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser2);
		
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.PublicBookmarks);
		
		// Get original window handle since add to community action opens a new window
		logger.strongStep("Fetch original window handle since Add to Community action opens a new window");
		log.info("INFO: Get original window handle since Add to Community action opens a new window");
		String originalWindow = driver.getWindowHandle();		
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Community option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Community option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoCommunityLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		logger.strongStep("Verify the Community Widget appears in the new window");
		log.info("INFO: Validate that the Community Widget appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.CommunityWidget), "ERROR: community widget doesn't show up");
		
		List<Element> communitylist = driver.getElements(DogearUIConstants.CommunityList);
		int i=0;
		for(; i<communitylist.size();i++){
			if(communitylist.get(i).getText().equals(community.getName())) {
				logger.strongStep("Click on the community created before with name as " + community.getName());
				log.info("INFO: Click on the community created before with name as " + community.getName());
				communitylist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the community: " + community.getName() + " appears in the Community List");
		log.info("INFO: Validate that the community: " + community.getName() + " appears in the Community List");
		assertTrue(i<communitylist.size(), "ERROR: cannot find the community");
		
		logger.strongStep("Select the Add to important bookmarks checkbox, type: " + testName + " in the Message for discussion text area and finally click on the Save button");
		log.info("INFO: Click on the Add to important bookmarks checkbox, type: " + testName + " in the Message for discussion text area and finally click on the Save button");
		driver.getSingleElement(DogearUIConstants.ImportantBookmarkCheckbox).click();
		driver.getSingleElement(DogearUIConstants.BookmarkletCommunityArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Open Communities component");
		log.info("INFO: Load the Communities component");
		ui.loadComponent("communities",true);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			commapiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Click on the Community Card for the community: " + community.getName());
		log.info("INFO: Open the community: " + community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community.getName()));
		
		logger.strongStep("Verify that Add a Bookmark link is visible on the Overview page of the community");
		log.info("INFO: Verify that Add a Bookmark link is visible on the Overview page of the community");
		ui.fluentWaitElementVisible(DogearUIConstants.AddABookmark);
	
		logger.strongStep("Verify the number of bookmarks is equal to 2 which denotes the bookmark was succesfully added to the community");
		log.info("INFO: Verify there are 2 bookmarks on the Overview page which denotes the bookmark was succesfully added to the community");
		assertTrue(driver.getElements(ui.getBookmarkSelector(publicBookmarks)).size() == 2,
				"ERROR: the bookmark is not added successfully.");		

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a bookmark to a community not as the owner but as a member.
	*<li><B>Step: </B>Create a community.
	*<li><B>Step: </B>Create a bookmark with tags.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Create community using API.
	*<li><B>Step: </B>Create public bookmark using API.
	*<li><B>Step: </B>Click open the detailed view.
	*<li><B>Step: </B>Click "add to community" link.
	*<li><B>Step: </B>Add bookmark to community.
	*<li><B>Verify: </B>The community widget shows up.
	*<li><B>Verify: </B>The bookmark has been added to the community.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void verifyAddBookmarkToOtherCommunity() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();	
		User comMem = cfg.getUserAllocator().getUser();
		
		log.info("INFO: Start of creating community and bookmarks with tags");
		BaseCommunity memcommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													  .tags(Data.getData().commonTag)
													  .addMember(new Member(CommunityRole.MEMBERS, comMem))
													  .description("Test Community for " + testName).build();
		
		BaseDogear publicBookmarks = new BaseDogear.Builder("Bookmark"+testName, "www.tivo.com")
											.tags(testName)
											.description(Data.getData().commonDescription + testName)
											.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		APICommunitiesHandler commapiOwner = new APICommunitiesHandler(serverURL, testUser3.getUid(), testUser3.getPassword());
		Community comAPI=memcommunity.createAPI(commapiOwner);
		
		//create bookmark
		logger.strongStep("Create public bookmark " + publicBookmarks.getTitle() + "using API");
		log.info("INFO: Create public bookmark " + publicBookmarks.getTitle() + "using API");
		publicBookmarks.createAPI(apiOwner3);
		
		//Load the component and login
		logger.strongStep("Open Communities component and login as: " + comMem.getDisplayName());
		log.info("INFO: Load the Communities component and login as: " + comMem.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(comMem);
		
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.PublicBookmarks);
		
		// Get original window handle since add to community action opens a new window
		logger.strongStep("Fetch original window handle since Add to Community action opens a new window");
		log.info("INFO: Get original window handle since Add to Community action opens a new window");
		String originalWindow = driver.getWindowHandle();		
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Community option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Community option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoCommunityLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		logger.strongStep("Verify the Community Widget appears in the new window");
		log.info("INFO: Validate that the Community Widget appears in the new window");		
		assertTrue(driver.isElementPresent(DogearUIConstants.CommunityWidget), "ERROR: community widget doesn't show up");
		
		List<Element> communitylist = driver.getElements(DogearUIConstants.CommunityList);
		int i=0;
		for(; i<communitylist.size();i++){
			if(communitylist.get(i).getText().equals(memcommunity.getName())) {
				logger.strongStep("Click on the community created before with name as " + memcommunity.getName());
				log.info("INFO: Click on the community created before with name as " + memcommunity.getName());
				communitylist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the community: " + memcommunity.getName() + " appears in the Community List");
		log.info("INFO: Validate that the community: " + memcommunity.getName() + " appears in the Community List");
		assertTrue(i<communitylist.size(), "ERROR: cannot find the community");
		
		logger.strongStep("Select the Add to important bookmarks checkbox, type: " + testName + " in the Message for discussion text area and finally click on the Save button");
		log.info("INFO: Click on the Add to important bookmarks checkbox, type: " + testName + " in the Message for discussion text area and finally click on the Save button");
		driver.getSingleElement(DogearUIConstants.ImportantBookmarkCheckbox).click();
		driver.getSingleElement(DogearUIConstants.BookmarkletCommunityArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Open the link for My Communities");
		log.info("INFO: Load My Communities component");
		ui.loadComponent("communities/service/html/mycommunities",true);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			commapiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Click on the Community Card for the community: " + memcommunity.getName());
		log.info("INFO: Open the community: " + memcommunity.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(memcommunity.getName()));
		
		logger.strongStep("Verify that Add a Bookmark link is visible on the Overview page of the community");
		log.info("INFO: Verify that Add a Bookmark link is visible on the Overview page of the community");
		ui.fluentWaitElementVisible(DogearUIConstants.AddABookmark);
		
		logger.strongStep("Verify the number of bookmarks is equal to 2 which denotes the bookmark was succesfully added to the community");
		log.info("INFO: Verify there are 2 bookmarks on the Overview page which denotes the bookmark was succesfully added to the community");
		assertTrue(driver.getElements(ui.getBookmarkSelector(publicBookmarks)).size() == 2,
				"ERROR: the bookmark is not added successfully.");		

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a user can add a bookmark to an activity in My Activities.
	*<li><B>Step: </B>Create an activity.
	*<li><B>Step: </B>Add bookmark with tags to activity.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Create activity using API.
	*<li><B>Step: </B>Create public bookmark with tags using API.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Click open the detailed view.
	*<li><B>Step: </B>Click "Add to activity" link.
	*<li><B>Verify: </B>The Activity widget shows up.
	*<li><B>Verify: </B>The bookmark has been added to My Activities.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void verifyAddBookmarkToMyActivity() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();	
		
		log.info("INFO: Start of creating activity and bookmarks with tags");
		BaseActivity activity = new BaseActivity.Builder(testName)
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.dueDateRandom()
												.useCalPick(true)
												.goal(Data.getData().commonDescription + testName)
												.build();
		
		BaseDogear publicBookmarks = new BaseDogear.Builder("Bookmark" + testName, "www.tivo.com")
											.tags(testName)
											.description(Data.getData().commonDescription + testName)
											.build();
		
		logger.strongStep("Create activity using API");
		log.info("INFO: Create activity using API");		
		APIActivitiesHandler activityapiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser4.getUid(), testUser4.getPassword());
		activity.createAPI(activityapiOwner);
		
		//create bookmark
		logger.strongStep("Create public bookmark " + publicBookmarks.getTitle() + "using API");
		log.info("INFO: Create public bookmark " + publicBookmarks.getTitle() + "using API");
		publicBookmarks.createAPI(apiOwner4);
		
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser4.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser4);
		
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.PublicBookmarks);
		
		// Get original window handle since add to activity action opens a new window
		logger.strongStep("Fetch original window handle since Add to Activity action opens a new window");
		log.info("INFO: Get original window handle since Add to Activity action opens a new window");
		String originalWindow = driver.getWindowHandle();		
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Activity option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Activity option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoActivityLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");

		logger.strongStep("Verify the Activity Widget appears in the new window");
		log.info("INFO: Validate that the Activity Widget appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.ActivityWidget), "ERROR: activity widget doesn't show up");
		
		List<Element> activitylist = driver.getElements(DogearUIConstants.ActivityList);
		int i=0;
		for(; i<activitylist.size();i++){
			if(activitylist.get(i).getText().equals(activity.getName())) {
				logger.strongStep("Click on the activity created before with name as " + activity.getName());
				log.info("INFO: Click on the activity created before with name as " + activity.getName());
				activitylist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the activity: " + activity.getName() + " appears in the Activity List");
		log.info("INFO: Validate that the activity: " + activity.getName() + " appears in the Activity List");
		assertTrue(i<activitylist.size(), "ERROR: cannot find the activity");
		
		logger.strongStep("Type: " + testName + " in the Additional description text area and click on the Save button");
		log.info("INFO: Enter: " + testName + " in the Additional description text area and click on the Save button");
		driver.getSingleElement(DogearUIConstants.BookmarkletActivityArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Open Activities component");
		log.info("INFO: Load the Activities component");
		ui.loadComponent("activities",true);
		
		logger.strongStep("Click on the link for the activity: " + activity.getName() + " in My Activities page");
		log.info("INFO: Open the activity: " + activity.getName() + " from My Activities page");
		ui.clickLinkWait("link=" + activity.getName());
		
		logger.strongStep("Verify the bookmark: " + publicBookmarks.getTitle() + " appears in the Activity's page");
		log.info("INFO: Validate that the bookmark: " + publicBookmarks.getTitle() + " appears in the Activity's page");
		assertTrue(driver.isTextPresent(publicBookmarks.getTitle()),"ERROR: the bookmark is not added successfully.");

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of a bookmark to a Blog in My Blogs.
	*<li><B>Step: </B>Create a blog.
	*<li><B>Step: </B>Create bookmark with tags.
	*<li><B>Verify: </B>The bookmark with tags has been created.
	*<li><B>Step: </B>Create blog using API.
	*<li><B>Step: </B>Create public bookmark using API.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Open the public bookmarks page.
	*<li><B>Step: </B>Click open the detailed view.
	*<li><B>Step: </B>Click "Add to blog" link.
	*<li><B>Step: </B>Switch to bookmark form.
	*<li><B>Verify: </B>The blogs list shows up.
	*<li><B>Step: </B>Select the correct blog.
	*<li><B>Step: </B>Set a description for this bookmark.
	*<li><B>Verify: </B>The bookmark has been added to blog.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void verifyAddBookmarkToMyBlog() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();	
		
		log.info("INFO: Start of creating blog and bookmarks with tags");
		BaseBlog blog = new BaseBlog.Builder(testName, Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.tags("Tag for "+testName)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseDogear publicBookmarks = new BaseDogear.Builder("Bookmark" + testName, "www.tivo.com")
											.tags(testName)
											.description(Data.getData().commonDescription + testName)
											.build();
		
		log.info("INFO: Create blog using API");		
		APIBlogsHandler blogapiOwner = new APIBlogsHandler(serverURL, testUser5.getUid(), testUser5.getPassword());
		blog.createAPI(blogapiOwner);
		
		//create bookmark
		logger.strongStep("Create public bookmark " + publicBookmarks.getTitle() + "using API");
		log.info("INFO: Create public bookmark " + publicBookmarks.getTitle() + "using API");
		publicBookmarks.createAPI(apiOwner5);
		
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser5.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser5.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser5);
		
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.PublicBookmarks);
		
		// Get original window handle since add to blog action opens a new window
		logger.strongStep("Fetch original window handle since Add to Blog action opens a new window");
		log.info("INFO: Get original window handle since Add to Blog action opens a new window");
		String originalWindow = driver.getWindowHandle();		
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Blog option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Blog option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoBlogLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Blog Widget appears in the new window");
		log.info("INFO: Validate that the Blog Widget appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.BlogWidget), "ERROR: blog list widget doesn't show up");
		
		List<Element> bloglist = driver.getElements(DogearUIConstants.BlogList);
		int i=0;
		for(; i<bloglist.size();i++){
			if(bloglist.get(i).getText().equals(blog.getName())) {
				logger.strongStep("Click on the Blog created before with name as " + blog.getName());
				log.info("INFO: Click on the Blog created before with name as " + blog.getName());
				bloglist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the Blog: " + blog.getName() + " appears in the Blog List");
		log.info("INFO: Validate that the Blog: " + blog.getName() + " appears in the Blog List");
		assertTrue(i<bloglist.size(), "ERROR: cannot find the blog");
		
		logger.strongStep("Type: " + testName + " in the Additional description text area and click on the Save button");
		log.info("INFO: Enter: " + testName + " in the Additional description text area and click on the Save button");
		driver.getSingleElement(DogearUIConstants.BookmarkletBlogArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Open Blogs component");
		log.info("INFO: Load the Blogs component");
		ui.loadComponent("blogs",true);
		
		logger.strongStep("Click on the Blog's link in My Blogs page");
		log.info("INFO: Open the Blog by clicking on its link in My Blogs page");
		ui.clickLinkWait("link=" + blog.getName());
		
		logger.strongStep("Verify the Bookmark: " + publicBookmarks.getTitle() + " is visible on the Blog's page");
		log.info("INFO: Validate that the Bookmark: " + publicBookmarks.getTitle() + " appears on the Blog's page");
		assertTrue(driver.isTextPresent(publicBookmarks.getTitle()),"ERROR: the bookmark is not added successfully.");
		
		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests if a bookmark can be to a Blog and an Ideation Blog in My Blogs.
	*<li><B>Step: </B>Create a community.
	*<li><B>Step: </B>Create a bookmark with tags.
	*<li><B>Verify: </B>The bookmark has been created.
	*<li><B>Step: </B>Create a community using API.
	*<li><B>Step: </B>Create a public bookmark using API.
	*<li><B>Step: </B>Add ideation blog widget using API.
	*<li><B>Step: </B>Add blog using API.
	*<li><B>Step: </B>Open the public bookmarks page.
	*<li><B>Step: </B>Show detailed view.
	*<li><B>Step: </B>Click "add to blog" link.
	*<li><B>Verify: </B>The blog list shows up.
	*<li><B>Step: </B>select correct blog.
	*<li><B>Step: </B>Set a desctiption for bookmark.
	*<li><B>Verify: </B>The bookmark has been added to the blog.
	*<li><B>Verify: </B>The bookmark has been added to the ideation blog.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void verifyAddBookmarkToMyCommunityBlogs() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRandVal();	
		
		log.info("INFO: Start of creating community and bookmarks with tags");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												 .access(CommunitiesUI.getDefaultAccess(cfg.getProductName()))
												 .description(testName)
												 .build();
												
		BaseDogear publicBookmarks = new BaseDogear.Builder("Bookmark" + testName, "www.tivo.com")
											.tags(testName)
											.description(Data.getData().commonDescription + testName)
											.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");		
		APICommunitiesHandler communityapiOwner = new APICommunitiesHandler(serverURL, testUser6.getUid(), testUser6.getPassword());
		Community comAPI = community.createAPI(communityapiOwner);
		
		//create bookmark
		logger.strongStep("Create public bookmark " + publicBookmarks.getTitle() + "using API");
		log.info("INFO: Create public bookmark " + publicBookmarks.getTitle() + "using API");
		publicBookmarks.createAPI(apiOwner6);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(communityapiOwner, comAPI);
		
		//add widget ideation blog and blog
		logger.strongStep("Add Ideation Blog and Blog widgets to the community using API");
		log.info("INFO: Add Ideation Blog and Blog widgets to the community using API");
		community.addWidgetAPI(comAPI, communityapiOwner, BaseWidget.IDEATION_BLOG);
		//community.addWidgetAPI(comAPI, communityapiOwner, BaseWidget.BLOG);
		
		//Load the component and login
		logger.strongStep("Open the Dogear component and login as: " + testUser6.getDisplayName());
		log.info("INFO: Load the Dogear component and login as: " + testUser6.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser6);
		
		logger.strongStep("Click on PUBLIC BOOKMARKS tab");
		log.info("INFO: Select PUBLIC BOOKMARKS tab");
		ui.selectBookmarkView(DogearUI.SelectBookmarkViews.PublicBookmarks);
		
		// Get original window handle since add to blog action opens a new window
		logger.strongStep("Fetch original window handle since Add to Blog action opens a new window");
		log.info("INFO: Get original window handle since Add to Blog action opens a new window");
		String originalWindow = driver.getWindowHandle();		
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Blog option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Blog option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoBlogLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Blog Widget appears in the new window");
		log.info("INFO: Validate that the Blog Widget appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.BlogWidget), "ERROR: blog list widget doesn't show up");
		
		List<Element> ideationbloglist = driver.getElements(DogearUIConstants.BlogList);
		int i=0;
		for(; i<ideationbloglist.size();i++){
			if(ideationbloglist.get(i).getText().equals(community.getName() + " (Ideation Blog)")) {
				logger.strongStep("Click on the Ideation Blog created before with name as " + community.getName() + " (Ideation Blog)");
				log.info("INFO: Click on the Ideation Blog created before with name as " + community.getName() + " (Ideation Blog)");
				ideationbloglist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the Ideation Blog: " + community.getName() + " (Ideation Blog) appears in the Blog List");
		log.info("INFO: Validate that the Ideation Blog: " + community.getName() + " (Ideation Blog) appears in the Blog List");
		assertTrue(i<ideationbloglist.size(), "ERROR: cannot find the ideation blog");
		
		logger.strongStep("Type: " + testName + " in the Additional description text area and click on the Save button");
		log.info("INFO: Enter: " + testName + " in the Additional description text area and click on the Save button");
		driver.getSingleElement(DogearUIConstants.BookmarkletBlogArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Click on the Details View icon");
		log.info("INFO: Select the Details View");
		ui.clickLink(DogearUIConstants.BookmarksList_DetailsView);
		
		logger.strongStep("Select Add to Blog option under More Actions menu for the bookmark: " + publicBookmarks.getTitle());
		log.info("INFO: Click on the More Actions menu for the bookmark: " + publicBookmarks.getTitle() + " and then select Add to Blog option");
		ui.ClickOnMoreActionsLinkForTheGivenBookmark(publicBookmarks);
		driver.getSingleElement(DogearUIConstants.AddtoBlogLink).click();

		// Switch to the Bookmark form
		logger.strongStep("Switch to the new window whose title is Add Bookmark");
		log.info("INFO: Switch to the Add Bookmark window");
		driver.switchToFirstMatchingWindowByPageTitle("Add Bookmark");
		
		logger.strongStep("Verify the Blog Widget appears in the new window");
		log.info("INFO: Validate that the Blog Widget appears in the new window");
		assertTrue(driver.isElementPresent(DogearUIConstants.BlogWidget), "ERROR: blog list widget doesn't show up");
		
		List<Element> bloglist = driver.getElements(DogearUIConstants.BlogList);
		for(i=0; i<bloglist.size();i++){
			if(bloglist.get(i).getText().equals(community.getName())) {
				logger.strongStep("Click on the Blog created before with name as " + community.getName());
				log.info("INFO: Click on the Blog created before with name as " + community.getName());
				bloglist.get(i).click();
				break;
			}
		}
		
		logger.strongStep("Verify the Blog: " + community.getName() + " appears in the Blog List");
		log.info("INFO: Validate that the Blog: " + community.getName() + " appears in the Blog List");
		assertTrue(i<bloglist.size(), "ERROR: cannot find the community blog");
		
		logger.strongStep("Type: " + testName + " in the Additional description text area and click on the Save button");
		log.info("INFO: Enter: " + testName + " in the Additional description text area and click on the Save button");
		driver.getSingleElement(DogearUIConstants.BookmarkletBlogArea).type(testName);
		driver.getSingleElement(BaseUIConstants.SaveButton).click();
		
		logger.strongStep("Switch back to the original window");
		log.info("INFO: Switch back to original window");
		driver.switchToWindowByHandle(originalWindow);
		
		logger.strongStep("Open Blogs component");
		log.info("INFO: Load the Blogs component");
		ui.loadComponent("blogs",true);
		
		logger.strongStep("Click on the Blog's link in My Blogs page");
		log.info("INFO: Open the Blog by clicking on its link in My Blogs page");
		driver.getElements("link=" + community.getName().substring(0, 40) + "...").get(0).click();
		
		logger.strongStep("Verify the Bookmark: " + publicBookmarks.getTitle() + " is visible on the Blog's page");
		log.info("INFO: Validate that the Bookmark: " + publicBookmarks.getTitle() + " appears on the Blog's page");
		assertTrue(driver.isTextPresent(publicBookmarks.getTitle()),"ERROR: the bookmark is not added successfully.");
		
		logger.strongStep("Reopen Blogs component");
		log.info("INFO: Reload the Blogs component");
		ui.loadComponent("blogs",true);
		
		logger.strongStep("Click on the Ideation Blog's link in My Blogs page");
		log.info("INFO: Open the Ideation Blog by clicking on its link in My Blogs page");
		driver.getElements("link=" + community.getName().substring(0, 40) + "...").get(1).click();
		
		logger.strongStep("Verify the Bookmark: " + publicBookmarks.getTitle() + " is visible on the Ideation Blog's page");
		log.info("INFO: Validate that the Bookmark: " + publicBookmarks.getTitle() + " appears on the Ideation Blog's page");
		assertTrue(driver.isTextPresent(publicBookmarks.getTitle()),"ERROR: the bookmark is not added successfully.");
		
		ui.endTest();

	}
	
}
