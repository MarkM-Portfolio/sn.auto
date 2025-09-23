package com.ibm.conn.auto.tests.metrics_elasticSearch;

import static org.testng.Assert.assertTrue;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BookmarksDataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BookmarksDataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;	
	private DogearUI ui;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private User testUser1;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui = DogearUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		URLConstants.setServerURL(serverURL);

}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Standalone Bookmark - Create a Bookmark</li>
	*<li><B>Step:</B> Create a public bookmark</li>
	*</ul>
	*<B>Note:</B> This test is not supported on the cloud, no standalone Bookmarks (Dogear)on cloud.
	*/
	@Test(groups = {"regression"})
	
	public void createStandaloneBookmark(){
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , Data.getData().commonURL)
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription + testName)
													.build();

		log.info("INFO: Log into Dogear as: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		log.info("INFO: Create public bookmark");
		ui.create(bookmark);
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Standalone Bookmark: Edit Bookmark</li>
	*<li><B>Step:</B> Create a public bookmark</li>
	*<li><B>Step:</B> Edit the bookmark's name</li>
	*</ul>
	*<B>Note:</B> This test is not supported on the cloud, no standalone Bookmarks (Dogear)on cloud.
	*/
	@Test(groups = { "regression"})
	public void editStandaloneBookmark(){
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();

		BaseDogear bookmark = new BaseDogear.Builder(testName , Data.getData().commonURL)
											.description("Description for " + testName)
											.build();

		log.info("INFO: Create a bookmark via API");
		APIDogearHandler apiOwner = new APIDogearHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		bookmark.createAPI(apiOwner);

		log.info("INFO: Log into Dogear as: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);

		log.info("INFO: Edit the title of the existing bookmark");
		ui.editBookmarkTitle(testName, Data.getData().EditBookmarkName);
		
		log.info("INFO: Validate renamed bookmark: " + Data.getData().EditBookmarkName + " displays in My Bookmarks view");
		assertTrue(driver.isTextPresent(Data.getData().EditBookmarkName), 
				   "ERROR: Bookmark: " + Data.getData().EditBookmarkName + " was not found");
		
		ui.endTest();
	}
	
	/**
	*
	*<ul>
	*<li><B>Info:</B> Data Population - Community Bookmark: Create a Bookmark </li>
	*<li><B>Step:</B> Create a Public community </li>
	*<li><B>Step:</B> Add a bookmark </li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void createCommunityBookmark(){
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
		                                           .access(Access.PUBLIC)
		                                           .description("Test community for " + testName)
		                                           .build();

		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

        log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Click on the Add Your First Bookmark link");
		ui.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);

		log.info("INFO: Input Bookmarks URL ");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);

		log.info("INFO: Input Bookmark Name");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);

		log.info("INFO: Save the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();

		log.info("INFO: Verify the bookmark was added");
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
				"ERROR: Bookmark: " + Data.getData().BookmarkName + " was not found");
		
				
		ui.endTest();

}
	
	/**
	*
	*<ul>
	*<li><B>Info:</B> Data Population - Community Bookmark: Edit a Bookmark </li>
	*<li><B>Step:</B> Create a Public community </li>
	*<li><B>Step:</B> Add a bookmark </li>
	*<li><B>Step:</B> Edit the bookmark </li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void editCommunityBookmark(){

		BaseCommunity community = new BaseCommunity.Builder("editCommunityBookmark" + Helper.genDateBasedRand())
		                                           .access(Access.PUBLIC)
		                                           .description("edit Community bookmark test " + Helper.genDateBasedRand())
		                                           .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Click on the Add Your First Bookmark link");
		ui.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);

		log.info("INFO: Input Bookmarks URL ");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);

		log.info("INFO: Input Bookmark Name");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);

		log.info("INFO: Save the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
		
		log.info("INFO: Verify the bookmark was added");
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
				"ERROR: Bookmark: " + Data.getData().BookmarkName + " was not found");
		
		log.info("INFO: Click on More link");
		ui.clickLinkWait(CommunitiesUIConstants.firstBookmarksMoreLink);
		
		log.info("INFO: Click on More Edit Bookmark");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);

		log.info("INFO: Start editing the bookmark URL & Name");			
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).type(Data.getData().EditBookmarkURL);
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).type(Data.getData().EditBookmarkName);
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).type(Data.getData().commonDescription);

		log.info("INFO: Save the changes in Bookmark");
		ui.clickLink(CommunitiesUIConstants.SaveButtonEntry);

		log.info("INFO: Verify the edited bookmark name appears");
		ui.fluentWaitTextPresent(Data.getData().EditBookmarkName);
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().EditBookmarkName),
				"ERROR: Bookmark: " + Data.getData().EditBookmarkName + " was not found");


		ui.endTest();

	}
}