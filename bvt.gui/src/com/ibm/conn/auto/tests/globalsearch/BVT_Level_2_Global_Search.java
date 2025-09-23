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

package com.ibm.conn.auto.tests.globalsearch;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APISearchHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.lcapi.common.Profile;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
 
public class BVT_Level_2_Global_Search<adminUser> extends SetUpMethods2 {


	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Global_Search.class);
	private TestConfigCustom cfg;
	private GlobalsearchUI ui;
	private CommunitiesUI commUI;
	private ActivitiesUI acUI;
	private SearchAdminService adminService;
	private BaseCommunity.Access defaultAccess;
	private long searchTime = 120 * 60 * 1000; // in milliseconds, which equates to 2 hrs
	private User itemOwner, testUser, testUser2, adminUser;
	private String serverURL;
	
	private static boolean indexerFailed = false;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context)  {

		super.beforeClass(context);
		
		cfg = TestConfigCustom.getInstance();
		ui = GlobalsearchUI.getGui(cfg.getProductName(), driver);

		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		itemOwner = cfg.getUserAllocator().getUser();
		
		// For MT servers, admin user might not be part of the org being tested or might not have serach-index role
		// Use another group called search_admin_users in this case.
		if (testConfig.serverIsMT() || testConfig.serverIsMTAsStandalone()) {
			adminUser = getSearchAdminUser();
		} else {
			adminUser = cfg.getUserAllocator().getAdminUser();
		}
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());	
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Search for a unique tag on an Activity</li>
	*<li><B>Step: </B>Create an Activity using the API</li> 
	*<li><B>Step: </B>Clear all check boxes</li>
	*<li><B>Step: </B>Check the Activities check box</li>
	*<li><B>Step: </B>Search for the unique tag for the Activity previously created</li>
	*<li><B>Verify: </B>The Activity displays in the search results</li>
	*</ul>
	*/ 
	@Test(groups = { "cplevel2", "regression", "bvt", "regressioncloud" })
	public void searchForActivityTag() throws Exception {
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		

		BaseActivity baseAct = new BaseActivity.Builder("Search Activity" + Helper.genDateBasedRand())
		  									   .goal(Data.getData().commonDescription + Helper.genDateBasedRand())
		  									   .tags("acttag" + Helper.genDateBasedRand()+" acttag")
		  									   .build();	
		
		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		
		ui.startTest();
		
		// Populate activity
		logger.strongStep("Add an Activity using API");
		log.info("INFO: Add Activity using API");
		baseAct.createAPI(apiOwner);
		try {
			ui.indexNow(serverURL, adminService, baseAct.getName(), "activities", itemOwner, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}
				
		
		// Create a list of text to look for: Owner's Display Name, Activity Name , Activity Goal and tags
		// This helps with identifying correct result displayed
		ArrayList<String> assertList = new ArrayList<String>();
		List<String> tags = Arrays.asList(baseAct.getTags().split("\\s"));
		assertList.add(itemOwner.getDisplayName());
		assertList.add(baseAct.getName());
		assertList.add(baseAct.getGoal());
		for (String tag : tags) {
			assertList.add(tag);
		}  
		

		logger.strongStep("Load Global Search and login in as: " +itemOwner.getDisplayName());
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(itemOwner);

		// Clear all check boxes
		logger.strongStep("Uncheck all of the check boxes");
		ui.advancedSearchClearAll();

		// Check the Activities check box and then enter the unique tag for the Activity previously created
		logger.strongStep("Check the Activities check box and then enter the unique tag for the Activity previously created");
		ui.searchAComponentForATag(GlobalsearchUI.ActivitiesCheckbox, tags.get(0));

		// Verify the results
		logger.weakStep("Verify the Activity displays in the search results");
		log.info("INFO: Validate the Activity displays in the search results");
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Search for unique tag on a Blog</li>
	*<li><B>Step: </B>Create a Blog using the API</li> 
	*<li><B>Step: </B>Clear all check boxes</li>
	*<li><B>Step: </B>Check the Blogs check box</li>
	*<li><B>Step: </B>Search for the unique tag for the Blog previously created</li>
	*<li><B>Verify: </B>The Blog displays in the search results</li>
	*</ul>
	*<B>Note: This is not supported on the cloud environment</B>
	*/ 
	@Test(groups = { "level2", "mt-exclude", "bvt" })
	public void searchForBlogTag() throws Exception {
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseBlog baseBlog = new BaseBlog.Builder("Search Blog" + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
		   								.tags("blogtag" + Helper.genDateBasedRand() + " blogtag")
		   								.description("Test description for testcase ")
		   								.build();		
				
		ui.startTest();

		// Populate blog
		logger.strongStep("Create a Blog using API");
		log.info("Create a Blog using API");
		baseBlog.createAPI(new APIBlogsHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword()));
		
		try{
			adminService.indexNow("blogs", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}

		// Create a list of text looking for: Owner's Display Name, Blog Name, Blog Description and tags
		// This helps with identifying correct result displayed
		ArrayList<String> assertList = new ArrayList<String>();
		List<String> tags = Arrays.asList(baseBlog.getTags().split("\\s"));
		assertList.add(itemOwner.getDisplayName());
		assertList.add(baseBlog.getName());
		assertList.add(baseBlog.getDescription());
		for (String tag : tags) {
			assertList.add(tag);
		}  

		logger.strongStep("Load Global Search and login in as: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(testUser);

		// Clear all check boxes
		logger.strongStep("Uncheck all the checkboxes");
		ui.advancedSearchClearAll();

		// Check the Blogs check box and then enter the unique tag for the Blog previously created
		logger.strongStep("Check the Blogs check box and then enter the unique tag for the Blog previously created");
		ui.searchAComponentForATag(GlobalsearchUI.BlogsCheckbox, tags.get(0));

		// Verify the results
		logger.weakStep("Verify the Blog displays in the search results");
		log.info("INFO: Validate the Blog displays in the search results");
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Search for unique tag on a Bookmark</li>
	*<li><B>Step: </B>Create a Bookmark using the API</li> 
	*<li><B>Step: </B>Clear all check boxes</li>
	*<li><B>Step: </B>Check the Bookmark check box</li>
	*<li><B>Step: </B>Search for the unique tag for the Bookmark previously created</li>
	*<li><B>Verify: </B>The Bookmark displays in the search results</li>
	*</ul>
	*<B>Note: This is not supported on the cloud environment</B>
	*/ 
	@Test(groups = { "regression", "mt-exclude", "bvt" })
	public void searchForBookmarkTag() throws Exception {
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseDogear baseBmark = new BaseDogear.Builder("searchDog" + Helper.genDateBasedRand(), "http://www.ibm.com/" + Helper.genDateBasedRand())
		  									 .tags("btag" + Helper.genDateBasedRand() + " btag")
		  									 .description("IBM")
		  									 .build();

		ui.startTest();

		// Populate bookmark
		logger.strongStep("Create a Bookmark using API");
		log.info("INFO: Create a Bookmark using API");
		Bookmark bookmark = baseBmark.createAPI(new APIDogearHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword()));
		try{
			adminService.indexNow("dogear", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}
		
		// Create a list of text looking for: Bookmark Title, Bookmark Description and tags
		// This helps with identifying correct result displayed
		ArrayList<String> assertList = new ArrayList<String>();
		assertList.add(bookmark.getTitle());
		String description = bookmark.getContent().length() < 80 ? bookmark.getContent() : bookmark.getContent().substring(0, 80);
		assertList.add(description);
		List<Category> tags = bookmark.getTags();
		for (Category tag : tags) {
			assertList.add(tag.getTerm());
		}

		logger.strongStep("Load Global Search and login in as: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(testUser);

		// Clear all checkboxes
		logger.strongStep("Uncheck all the checkboxes");
		ui.advancedSearchClearAll();

		// Check the Bookmarks check box and then enter the unique tag for the Bookmark previously created
		logger.strongStep("Check the Bookmarks check box and then enter the unique tag for the Bookmark previously created");
		ui.searchAComponentForATag(GlobalsearchUI.BookmarkCheckbox, tags.get(0).getTerm());

		// Verify the results
		logger.weakStep("Verify the Bookmark displays in the search results");
		log.info("Verify the Community displays in the search results");
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Search for unique tag on a Community</li>
	*<li><B>Step: </B>Create a Community using the API</li>
	*<li><B>Step: </B>Clear all check boxes</li>
	*<li><B>Step: </B>Check the Community check box</li> 
	*<li><B>Step: </B>Search for the unique tag for the Community previously created</li>
	*<li><B>Verify: </B>The Community displays in the search results</li>
	*</ul>
	*<B>Clean-up: </B>Delete the community
	*/ 
	@Test(groups = { "cplevel2", "level2", "bvt", "regressioncloud" })
	public void searchForCommunityTag() throws Exception {
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		BaseCommunity baseCom = new BaseCommunity.Builder("Search Community" + Helper.genDateBasedRand())
		  						   				 .tags("comtag" + Helper.genDateBasedRand() + " comtag")
		  						   				 .access(Access.PUBLIC)
		  						   				 .description("Test description for testcase ")
		  						   				 .build();
	
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		
		ui.startTest();
		

		// Populate community
		logger.strongStep("Create a community using API");
		log.info("INFO: Create a community using API");
		Community community = baseCom.createAPI(apiOwner);
		try{
			ui.indexNow(serverURL, adminService, baseCom.getName(), "communities", itemOwner, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}

		// Create a list of text looking for: Owner's Display Name, Community Name, Community Description and tags
		// This helps with identifying correct result displayed
		ArrayList<String> assertList = new ArrayList<String>();
		List<String> tags = Arrays.asList(baseCom.getTags().split("\\s"));
		assertList.add(itemOwner.getDisplayName());
		assertList.add(baseCom.getName());
		assertList.add(baseCom.getDescription());
		for (String tag : tags) {
			assertList.add(tag);
		} 
		
		logger.strongStep("Load Global Search and login in as: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(testUser);

		// Clear all checkboxes
		logger.strongStep("Uncheck all the checkboxes");
		ui.advancedSearchClearAll();

		// Check the Communities check box and then enter the unique tag for the Community previously created
		logger.strongStep("Check the Communities check box and then enter the unique tag for the Community previously created");
		ui.searchAComponentForATag(GlobalsearchUI.CommunitiesCheckbox, tags.get(0));

		// Verify the results
		logger.weakStep("Verify the Community displays in the search results");
		log.info("INFO: Validate the Community displays in the search results");
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		// Clean-up: Delete the community
		logger.strongStep("Delete the community");
		apiOwner.deleteCommunity(community);
		
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Search for unique tag on a Profile</li>
	*<li><B>Step: </B>Add two tags on a profile</li>
	*<li><B>Step: </B>Check the Profiles check box</li> 
	*<li><B>Step: </B>Search for the unique tag on the Profile</li>
	*<li><B>Verify: </B>The Profile displays in the search results</li>
	*</ul>
	*/ 
	@Test(groups = { "level2", "bvt", "regressioncloud" })
	public void searchForProfileTag() throws Exception {
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String profileTag = "ptag" + Helper.genDateBasedRand() + " ptag";	
		
		
		ui.startTest();

		// Add tags to profile
		logger.strongStep("Add tags to the profile using API");
		log.info("INFO: Tagging " + itemOwner.getDisplayName()+" with "+profileTag);
		Profile profile = new APIProfilesHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword()).setProfileTags(profileTag);
		try{
			ui.indexNow(serverURL, adminService, profileTag, "profiles", itemOwner, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}
		
		//Validate two tags were added
		ArrayList<String> tags = profile.getTags();
		assertTrue(tags.size() == 2, 
				   "Expected to have 2 tags, update required if the number of tags has changed");


		logger.strongStep("Load Global Search and login in as: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(testUser);

		// Clear all checkboxes
		logger.strongStep("Uncheck all the checkboxes");
		ui.advancedSearchClearAll();

		// Check the Profiles checkbox and then enter the unique tag for the Profile
		logger.strongStep("Check the Profiles checkbox and then enter the unique tag for the Profile");
		ui.searchAComponentForATag(GlobalsearchUI.ProfilesCheckbox, tags.get(0));

		// Verify the results
		logger.weakStep("Verify the Profile displays in the search results");
		log.info("INFO: Validate the Profile displays in the search results");
		Assert.assertTrue(ui.isElementPresent("css=a:contains(" + itemOwner.getDisplayName()+ ")"),
							"ERROR: Unable to find " + itemOwner.getDisplayName());
		

		ui.endTest();
	}
	

	/**
	 *<ul>
	 *<li><B>Info: </B>Search for the user's related information</li>
	 *<li><B>Steps: </B>Login and navigate to Advanced Search page </li>
	 *<li><B>Steps: </B>Enter UserB into person search field</li>
	 *<li><B>Steps: </B>Select Search button</li>
	 *<li><B>Verify: </B>The search filter button displays under the header containing UserB's name </li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void advSearchUserName() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String product = cfg.getProductName();
		ui.startTest();
		
		//Load component and login
		logger.strongStep("Login as user: " +testUser.getDisplayName() +" and navigate to the Advanced Search page");
		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(testUser);
	
		//Wait for sametime to load if enabled
		logger.weakStep("If Sametime is enabled, allow time for it load");
		ui.waitForSameTime();
		
		//Type user name into person
		logger.strongStep("Enter User: " + testUser2.getDisplayName() +" into Person search field");
		log.info("INFO: Enter User: " + testUser2.getDisplayName() +" into Person search field");
		ui.fluentWaitPresent(GlobalsearchUI.AdvancedSearchPerson);		
		ui.typeTextWithDelay(GlobalsearchUI.AdvancedSearchPerson, testUser2.getDisplayName());	
	
		//Select user from popup using email
		logger.strongStep("Select User: " + testUser2.getDisplayName() +"  from popup list");
		//determine of SC or OP
		if(product.equalsIgnoreCase("cloud")){
			log.info("INFO: Select User by email: " + testUser2.getEmail());
			ui.clickLinkWait(GlobalsearchUI.AdvancedSearchUserTextDetails+":contains(" + testUser2.getEmail() + " )");
		}else{
			log.info("INFO: Select User by display name: " + testUser2.getDisplayName());
			ui.clickLinkWait(GlobalsearchUI.AdvancedSearchUserDirectory);
			ui.clickLinkWait(GlobalsearchUI.AdvancedSearchUserTextDetails + ":contains(" + testUser2.getDisplayName() + " )");
		}
		
		//Click search button
		logger.strongStep("Select 'Search' Button");
		log.info("INFO: Select search button");
		ui.clickLinkWait(GlobalsearchUI.AdvancedSearchButton);		
		ui.fluentWaitPresent(GlobalsearchUI.AdvancedSearchResultsHeading);
		
		// wait for whole search result page be loaded
		logger.weakStep("Waiting for text 'Search index was last updated:'");
		ui.fluentWaitTextPresent(GlobalsearchUI.AdvancedSearchIndex);
		
		//Validate filter is present
		logger.strongStep("Verify the " + testUser2.getDisplayName() +" search filter button displays under the header");
		log.info("INFO: Validate " + testUser2.getDisplayName() +" search filter button is present");
		Assert.assertEquals(driver.isElementPresent(GlobalsearchUI.advSearchSelectPersonFilter(testUser2)), true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Create a new Activity and searches for it</li>
	 *<li><B>Step: </B>Create Activity via API</li>
	 *<li><B>Step: </B>Force the indexer to run immediately</li>
	 *<li><B>Step: </B>Search for Activity via UI</li>
	 *<li><B>Verify: </B>The search is performed and the Activity is returned successfully</li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokeonprem"})
	public void searchNewActivity() throws Exception{
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		boolean found = false;
		
		BaseActivity activity = new BaseActivity.Builder(testName + " Activity " + Helper.genDateBasedRand())
									.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
									.dueDateRandom()
									.goal(Data.getData().commonDescription)
									.addMember(new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON))
									.build();
		
		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		logger.strongStep("Create new activity using API");
		log.info("INFO: Attempting to create Activity via API");
		activity.createAPI(apiOwner);		
				
		log.info("INFO: Kick off the indexer");
		try{
			ui.indexNow(serverURL, adminService, activity.getName(), "activities", testUser, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}
		
		logger.strongStep("Load Activity and login in as: " +testUser.getDisplayName());
		log.info("INFO: Searching for Activity via UI");
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);
		
		logger.weakStep("Verify the Activity displays in the search results");
		found = ui.searchForActivity(activity);
		Assert.assertTrue(found, "ERROR: Could not find Activity via UI.");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Creates a new Community and searches for it</li>
	 *<li><B>Step: </B>Create Community via API</li>
	 *<li><B>Step: </B>Force the indexer to run immediately</li>
	 *<li><B>Step: </B>Search for Community via UI</li>
	 *<li><B>Verify: </B>The search is performed and the Community is returned successfully</li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokeonprem"})
	public void searchNewCommunity() throws Exception{
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		boolean found = false;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + " Community " + Helper.genDateBasedRand())
					.tags(Data.getData().commonTag + Helper.genDateBasedRand())
					.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
					.description("Test description for testcase " + Helper.genDateBasedRand())
					.build();

		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: Creating Community via API");
		logger.strongStep("Create a community using API");
		community.createAPI(apiOwner);
		
		log.info("INFO: Kick off the indexer");
		try{
			ui.indexNow(serverURL, adminService, community.getName(), "communities", testUser, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}
		
		logger.strongStep("Load Communities and login in as: " +testUser.getDisplayName());
		log.info("INFO: Searching for Community via UI");
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		logger.weakStep("Verify the Community displays in the search results");
		found = ui.searchForCommunity(community);
		Assert.assertTrue(found, "ERROR: Could not find Community via UI.");
		
		// Clean-up: Delete the community
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	
	private void searchActivity(User myUser, String acName) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		acUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		acUI.startTest();
		
		//Load component and login
		logger.strongStep("Load Activities and login as: " +myUser.getDisplayName());
		acUI.loadComponent(Data.getData().ComponentActivities);
		acUI.login(myUser);
	
		//Wait for sametime to load if enabled
		log.info("INFO: If sametime is enabled, allow time for it to load");
		acUI.waitForSameTime();

		logger.strongStep("Seach for activity: " +acName);
		acUI.searchActivities(acName);	
		logger.weakStep("Validate that the search results were found");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("No search results found"),
					" Found activity for " + acName);				
	
		acUI.endTest(); 
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Search for existing Activity created at least two hours ago</li>
	 *<li><B>Step: </B>[API] Get the Activity name that was created two hours ago</li>
	 *<li><B>Step: </B>Search for the Activity via GUI</li>
	 *<li><B>Verify: </B>The search is performed and the Activity is returned successfully</li>
	 *<li><B>***In the case when there is no existing Activity***</B>
	 *<li><B>Step: </B>[API] Create a new Activity</li>
	 *<li><B>Step: </B>[API] Wait for 30 minutes and then search for the Activity via GUI</li>
	 *<li><B>Verify: </B>The search is performed and the Activity is returned successfully</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void searchExistingActivity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User searchUser = cfg.getUserAllocator().getGroupUser("search_users");
		
		boolean ret = false;
		int activitySize = 0;
		String activityName = null;
	
		long currentTime;
		Date updatedDate = null;
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIActivitiesHandler activityOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, searchUser.getAttribute(cfg.getLoginPreference()), searchUser.getPassword());
		
		ArrayList<Activity> ac = activityOwner.getService().getMy75Activities();
	
		if ( ac != null )
		{
			activitySize =  ac.size();
			currentTime = new Date().getTime();
		
			for ( int index = 0; index < (activitySize - 1); index++) {
				
				updatedDate = ac.get(index).getUpdated();
			
				// get an old activity that is at least 2 hours old
				logger.strongStep("Get an activity that was created at least 2 hrs ago");
				if ( (currentTime - updatedDate.getTime()) > searchTime) {
					// get the activity name which be searched for
					logger.strongStep("Search for the activity");
					activityName = ac.get(index).getTitle();
				
					log.info("INFO: Got Activity Name: " + activityName);
					break;
				}	
			}
	
			if ( activityName != null)
				searchActivity(searchUser, activityName);
			else {
				String testName = "SearchActivity" + Helper.genDateBasedRandVal3();
				APISearchHandler apiSearch = new APISearchHandler(serverURL, searchUser.getAttribute(cfg.getLoginPreference()), searchUser.getPassword()); 
				
				// need to create a new activity
				log.info("INFO: Need to create a new activity");		
				serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			
				BaseActivity activity = new BaseActivity.Builder(testName )
						.tags(testName)
						.goal("Two ACT in regression test for " + testName)
						.build();

				// Create activity with Activity API
				logger.strongStep("Create Activities using API");
				log.info("INFO: Create activities using API");
				Activity acAPI = activity.createAPI(activityOwner);		
				Assert.assertNotNull(acAPI);	
				
				// wait for 30 minutes and check for the activity via API
				logger.strongStep("Wait for 30 minutes for the index to update");
				ret= apiSearch.waitForIndexer("activities", testName, 30) ;				
				Assert.assertEquals(ret, true);
				
				// Do the UI search for the newly created activity
				searchActivity(searchUser, testName);
			}
		}
		else {
				//  Skip this test case
				 throw new SkipException("Test Search Existing Activity was Skipped");		
		}
	}
	
	
	
	private void searchCommunity(User myUser, String commName) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		commUI.startTest();
	
		//Load component and login
		logger.strongStep("Load Communities and login in as: " +myUser.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(myUser);
		
		//Wait for same time to load if enabled
		log.info("INFO: If sametime is enabled, allow time for it to load");
		commUI.waitForSameTime();

		// get community to search
		logger.strongStep("Search for community: " +commName);
		commUI.searchCommunities(commName);	
		logger.weakStep("Verify the community is present");
		log.info("INFO: Validate the community is present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("No search results found"),
				" Found activity for " + commName);					

		ui.endTest(); 	
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Search for existing Community created at least two hours ago</li>
	 *<li><B>Step: </B>[API] Get the Community name that was created two hours ago</li>
	 *<li><B>Step: </B>Search for the Community via GUI</li>
	 *<li><B>Verify: </B>The search is performed and the Community is returned successfully</li>
	 *<li><B>***In the case when there is no existing Community***</B>
	 *<li><B>Step: </B>[API] Create a new Community</li>
	 *<li><B>Step: </B>[API] Wait for 30 minutes and then search for the Community via GUI</li>
	 *<li><B>Verify: </B>The search is performed and the Community is returned successfully</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void searchExistingCommunity() throws Exception {
		
		User searchUser = cfg.getUserAllocator().getGroupUser("search_users");
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		boolean ret = false;
		int communitySize;
		String communityName = null;
			
		Date updatedDate;
		long currentTime;
		
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, searchUser.getAttribute(cfg.getLoginPreference()), searchUser.getPassword());
		
		// get all the possible communities for this user
		logger.strongStep("Get all possible communities for the user using API");
		ArrayList<Community> cs = apiOwner.getCommunities();
	
		currentTime = new Date().getTime();
		if ( cs != null )
		{
			communitySize =  cs.size();

			for ( int index = 0; index < (communitySize - 1); index++) {
			
				updatedDate = cs.get(index).getUpdated();
		
				// get an old activity that is at least 2 hours old
				logger.strongStep("Get a community that was created at least 2 hrs ago");
				if ( (currentTime - updatedDate.getTime()) > searchTime) {
					// get the community name which be searched for
					logger.strongStep("Search for the community");
					communityName = cs.get(index).getTitle();
				
					log.info("INFO: Search for Community Name " + communityName );
					break;
				}	
			}	
		
			if ( communityName != null) {
				searchCommunity(searchUser, communityName);
			}
			else {
				String testName = "SearchCommunity";
				
				defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
				APISearchHandler apiSearch = new APISearchHandler(serverURL, searchUser.getAttribute(cfg.getLoginPreference()), searchUser.getPassword()); 
				
				// need to create a new community
				logger.strongStep("Create a new community");
				log.info("INFO: Need to create a community");
				BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				   .access(defaultAccess)
				   .tags(Data.getData().commonTag)
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description("Test Community for " + testName).build();
	
				//create community
				log.info("INFO: Create community using API");
				Community comAPI = community.createAPI(apiOwner);
				Assert.assertNotNull(comAPI);	
				
				// wait for 30 minutes and check for community via API
				logger.strongStep("Wait for 30 minutes for the index to update");
				ret= apiSearch.waitForIndexer("communities", community.getName(), 30) ;
				Assert.assertEquals(ret, true);
				
				// Do the UI search for the newly created community
				searchCommunity(searchUser, community.getName());
			
			}
		} else {
			//  Skip this test case
			 throw new SkipException("Test Search Existing Community was Skipped");		
		}
	}
	

	
	/**
	 *<ul>
	 *<li><B>Info: </B>Create a new File and searches for it</li>
	 *<li><B>Step: </B>Create File via API</li>
	 *<li><B>Step: </B>Force the indexer to run immediately</li>
	 *<li><B>Step: </B>Search for File via UI</li>
	 *<li><B>Verify: </B>The search is performed and the File is returned successfully</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"})
	public void searchNewFile() throws Exception{
		if(indexerFailed){
			throw new SkipException("Indexer failed. Skipping test.");
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		boolean found = false;
		
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
											 .comFile(true)
											 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
											 .rename(testName + "File" + Helper.genDateBasedRand())
											 .extension(".jpg")
											 .build();

		APIFileHandler apiOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: Attempting to create File via API");
		logger.strongStep("Create a file using API");
		file.createAPI(apiOwner, new File("resources"+File.separator+"Desert.jpg"));
		file.setName(file.getRename());
		
		log.info("INFO: Kick off the indexer");
		try{
			ui.indexNow(serverURL, adminService, file.getName(), "files", testUser, adminUser);
		} catch (AssertionError e) {
			indexerFailed = true;			
			throw e;
		}

		logger.strongStep("Load Files and login in as: " +testUser.getDisplayName());
		log.info("INFO: Searching for File via UI");
		ui.loadComponent(Data.getData().ComponentFiles, true);	
		ui.login(testUser);
		
		logger.weakStep("Verify the File displays in the search results");
		found = ui.searchForFile(file);
		Assert.assertTrue(found, "ERROR: Could not find File via UI.");
		
		ui.endTest();
	}
	
	
	/**
	 * User configuration for some test environments assumes admin_users group (eg. ajones1) also has search indexing privilege
	 * so there is no specific group for search.  It presents problems for HCL MT because the internal searchAdmin API 
	 * needs a user in org so check to see if there is a user group for search first and use admin_users group as fallback.
	 * @return serach admin user
	 */
	private User getSearchAdminUser() {
		String searchGroupName = "search_admin_users";
		if (cfg.getUserAllocator().isUserGroupEmpty(searchGroupName))  {
			return cfg.getUserAllocator().getAdminUser();
		} else {
			return cfg.getUserAllocator().getGroupUser(searchGroupName);
		}
	}
	
}
