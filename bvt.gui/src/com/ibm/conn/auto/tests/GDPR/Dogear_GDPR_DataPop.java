package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.appobjects.role.BlogRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.BlogSettings_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_MegaMenu_Menu;
import com.ibm.conn.auto.util.menu.Dogear_MoreActions_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Dogear_GDPR_DataPop extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Dogear_GDPR_DataPop.class);
	private TestConfigCustom cfg;
	private User testUser1, testUser2;
	private DogearUI ui;
	private CommunitiesUI commUI;
	private ActivitiesUI actUI;
	private BlogsUI blogsUI;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private APIActivitiesHandler apiActivityOwner1;
	private APIBlogsHandler apiBlogOwner1;
	private String serverURL;
	private boolean isOnPremise;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();	
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		ui = DogearUI.getGui(cfg.getProductName(),driver);
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);	
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		blogsUI = BlogsUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		
		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Bookmarks are not supported - skipping tests");
		}		
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

		apiActivityOwner1 =  new APIActivitiesHandler(cfg.getProductName(),serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		apiBlogOwner1 = new APIBlogsHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Data Population: UserA Creates Bookmark, UserB Adds Bookmark To 'My Bookmarks'</li>
	*<li><B>Step:</B>UserA creates a bookmark</li>
	*<li><B>Step:</B>UserB adds the bookmark to 'My Bookmarks'</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear); therefore, it will not work in that environment
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesBookmarkUserBAddsToMyBookmarks(){
			
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "http://www.usatoday.com";
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: Dogear - " + testName , url)
											.description("GDPR data pop: " + testName)
											.build();
		
		log.info("INFO: Login to Bookmarks as UserA: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		log.info("INFO: Create a bookmark");
		ui.create(bookmark);
		
		log.info("INFO: Logout as UserA");
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: Login Bookmarks as UserB");
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser2);
			
		log.info("INFO: Add the bookmark to 'My Bookmarks'");
		ui.addToMyBookmarks(bookmark);						
		
	    log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
	    ui.logout();
	    ui.close(cfg);
		
		ui.endTest();  
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Data Population: UserA Adds a Bookmark to Their Watchlist</li>
	*<li><B>Step:</B>UserA creates a bookmark</li>
	*<li><B>Step:</B>UserA switches to the My Bookmarks view</li>
	*<li><B>Step:</B>If the Add to Watchlist link exists, click it.  If 'Remove from Watchlist' link appears, nothing is done</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear);therefore, it will not work in that environment
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void userASelectsAddBookmarkToWatchlist(){
 		
		String testName = ui.startTest() + Helper.genDateBasedRand();
		String url = "http://www.abcnews.com";
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: Add to Watchlist - " + testName , url)
		                                    .description("GDPR data pop: " + testName)
		                                    .build();

		log.info("INFO:Login to Bookmarks as UserA: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);

		log.info("INFO: Create a bookmark to Google");
		ui.create(bookmark);

		log.info("INFO: Switch to 'My Bookmarks' view.");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);

		log.info("INFO: If the 'Add to Watchlist' link appears, click it; otherwise, do nothing.");
		if(!ui.isElementPresent(DogearUIConstants.MyBookmarks_RemoveFromWatchlist)){
			ui.clickLinkWithJavascript(DogearUIConstants.MyBookmarks_AddToWatchlist);

		}else {
			log.info("INFO: " + testUser1.getDisplayName() + " is already on the Watchlist");
		}
		
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Data Population: UserA Creates & Edits a Private Bookmark</li>
	*<li><B>Step:</B>UserA creates a private bookmark</li>
	*<li><B>Step:</B>UserA edits the private bookmark</li>
	*</ul>
	*<B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear); therefore, it will not work in that environment
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesAndEditsPrivateBookmark() {
		
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "http://www.mars.com";
		String editTitle = "EDITED: " + testName;

		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
		                                    .access(com.ibm.conn.auto.appobjects.base.BaseDogear.Access.RESTRICTED)
		                                    .build();		

		log.info("INFO: Login to Bookmarks as UserA: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
				
		log.info("INFO: Switch to 'Public Bookmarks' view and create a bookmark.");
		ui.selectBookmarkView(SelectBookmarkViews.MyBookmarks);
		ui.create(bookmark);
		
		log.info("INFO: Edit the bookmark title");
		ui.editBookmarkTitle(testName, editTitle);
						
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		ui.endTest();
	}
	
		
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population: UserA Adds a Bookmark to a Community</li>
	 * <li><B>Step:</B>UserA creates a community via API</li>
	 * <li><B>Step:</B>UserA navigates to Bookmarks & creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA changes the view to 'Details'</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Community option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the Community name </li>
	 * <li><B>Step:</B>UserA navigates to Communities opens the community</li>
	 * <li><B>Step:</B>Verify bookmark appears</li>
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAAddsBookmarkToCommunity(){	
				
		String testName = ui.startTest() + Helper.genDateBasedRandVal();
		String url = "http://www.nbcnews.com";
				
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: Dogear - " + testName , url)
											.description("GDPR data pop: " + testName)
											.build();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName)
                                                   .access(Access.PUBLIC)	
                                                   .description("GDPR data pop - UserA creates a stand-alone bookmark & adds it to a community ")
                                                   .build();
				
	
		log.info("INFO: Create a community & bookmark using the API");
		createCommunityAndBookmark(community, bookmark, apiCommOwner1, testUser1);
		
		log.info("INFO: Get the bookmark UUID");
		String UUID = ui.getUUID(bookmark);
		
		log.info("INFO: Add the bookmark to the community");
		addBookmarkToCommunity(community, bookmark, UUID);

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population: UserB Creates Bookmark & UserA Adds the Bookmark to a Community</li>
	 * <li><B>Step:</B>UserB creates a community with UserA as a Member via API</li>
	 * <li><B>Step:</B>UserB navigates to Bookmarks & creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA logs into Bookmarks and changes the view to Details</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Community option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the Community name </li>
	 * <li><B>Step:</B>UserA navigates to Communities opens the community</li>
	 * <li><B>Verify:</B>Bookmark appears in the community</li> 
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userBCreatesBookmarkUserAAddsItToCommunity(){	
				
			String testName = ui.startTest() + Helper.genDateBasedRandVal();
			String url = "http://www.cbsnews.com";

			BaseDogear bookmark = new BaseDogear.Builder("GDPR: Dogear - " + testName , url)
			                                    .description("GDPR data pop: " + testName)
			                                    .build();

			BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName)
			                                           .access(Access.PUBLIC)	
			                                           .description("GDPR data pop - UserB creates a stand-alone bookmark & UserA adds it to a community ")
			                                           .addMember(new Member(CommunityRole.MEMBERS, testUser1))
			                                           .build();

			log.info("INFO: Create a community & bookmark using the API");
			createCommunityAndBookmark(community, bookmark, apiCommOwner2, testUser2);
			
			log.info("INFO: Get the bookmark UUID");
			String UUID = ui.getUUID(bookmark);
			
			log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
			ui.logout();
			ui.close(cfg);
			
			log.info("INFO: Login to Bookmarks as UserA " + testUser1.getDisplayName());
			ui.loadComponent(Data.getData().ComponentDogear);
			ui.login(testUser1);
			
			log.info("INFO: Navigate to the Public Bookmarks view");
			ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

			log.info("INFO: Add the bookmark to the community");
			addBookmarkToCommunity(community,bookmark,UUID);

			log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
			ui.logout();
			ui.close(cfg);

			ui.endTest();
	}

	
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population: UserA Creates a Bookmark & Adds it to an Activity</li>
	 * <li><B>Step:</B>UserA creates an activity via API</li>
	 * <li><B>Step:</B>UserA navigates to Bookmarks and creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA changes the view to Details</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Activity option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the Activity name </li>
	 * <li><B>Step:</B>UserA navigates to Activities opens the activity</li>
	 * <li><B>Verify:</B>Bookmark appears in the activity </li> 
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = { "regression" }, enabled=false)
	public void userAAddsBookmarkToActivity(){
		
		String testName = ui.startTest() + Helper.genDateBasedRand();		
		String url = "http://www.msn.com";
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: " + testName, url)
		                                    .description("GDPR data pop - UserA will add a bookmark to an Activity")
		                                    .build();
		
		BaseActivity activity = new BaseActivity.Builder("GDPR: " + testName)
								                .tags(testName)
								                .goal("GDPR data pop - UserA will add a standalone bookmark to this activity " + testName)
								                .build();
		
		log.info("INFO: Create activity using API");
		activity.createAPI(apiActivityOwner1);

		log.info("INFO: Login to Activities as UserA: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);

		log.info("INFO: Navigate to the 'Public Bookmarks' view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		log.info("INFO: Create a bookmark");
		ui.create(bookmark);
		
		log.info("INFO: Get the bookmark UUID");
		String UUID = ui.getUUID(bookmark);

		log.info("INFO: Add the bookmark to the activity");
		addBookmarkToActivity(activity, bookmark, UUID);
		
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population: UserB Creates a Bookmark & UserA Adds Bookmark to an Activity</li>
	 * <li><B>Step:</B>UserB creates a activity with UserA as an Owner via API</li>
	 * <li><B>Step:</B>UserB navigates to Bookmarks & creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA logs into Bookmarks and changes the view to Details</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Community option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the activity name </li>
	 * <li><B>Step:</B>UserA navigates to Activities opens the activity</li>
	 * <li><B>Verify:</B>Bookmark appears in the activity</li> 
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = { "regression" }, enabled=false)
	public void userBCreatesBookmarkUserAAddsItToActivity(){
		
		String testName = ui.startTest() + Helper.genDateBasedRand();		
		String url = "http://www.foxnews.com";
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: " + testName, url)
		                                    .description("GDPR data pop - UserA will add this bookmark to an Activity")
		                                    .build();
		
		BaseActivity activity = new BaseActivity.Builder("GDPR: " + testName)
								                .goal("GDPR data pop - UserA adds a standalone bookmark to this activity "+ testName)
								                .addMember(new ActivityMember(ActivityRole.OWNER, testUser1, ActivityMember.MemberType.PERSON))
								                .build();
		
		log.info("INFO: Login to Activities as UserB: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser2);
		
		//Note: creating Activity via UI, for some reason member does not get added when using .createAPI method
		log.info("INFO: Create an Activity");
		activity.create(actUI);
		
		log.info("INFO: Select Bookmarks from the mega-menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		ui.clickLinkWait(DogearUIConstants.bookmarksOption);

		log.info("INFO: Navigate to the 'Public Bookmarks' view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		log.info("INFO: Create a bookmark");
		ui.create(bookmark);
		
		log.info("INFO: Get the bookmark UUID");
		String UUID = ui.getUUID(bookmark);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: Login to Bookmarks as UserA " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'Public Bookmarks' view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		log.info("INFO: Add the bookmark to the activity");
		addBookmarkToActivity(activity, bookmark, UUID);

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population - UserA Creates a Bookmark & Adds to a Blog</li>
	 * <li><B>Step:</B>UserA creates an Blog via API</li>
	 * <li><B>Step:</B>UserA navigates to Bookmarks and creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA changes the view to Details</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Blog option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the blog name </li>
	 * <li><B>Step:</B>UserA navigates to Blogs (Public Bookmarks view)</li>
	 * <li><B>Verify:</B>Bookmark appears</li> 
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAAddsBookmarkToBlog(){
		
		String testName = ui.startTest() + Helper.genDateBasedRand();
		
		String url = "http://www.noaa.gov";
		String blogAddress = Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal();
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: Bookmark - " + testName, url)
							                .description("GDPR data pop - this bookmark will be added to a blog")
							                .build();
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: Standalone Blog - " + testName, blogAddress)
  						            .description("GDPR data pop - UserA will add a standalone bookmark to this blog")
  						            .theme(Theme.Blog_with_Bookmarks)
  						            .build();
		
		log.info("INFO: Create blog using API");
		blog.createAPI(apiBlogOwner1);

		log.info("INFO: Log into Bookmarks as UserA: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser1);

		log.info("INFO: Navigate to the Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		log.info("INFO: Create bookmark");
		ui.create(bookmark);

		log.info("INFO: Get the bookmark UUID");
		String UUID = ui.getUUID(bookmark);

		log.info("INFO: Add the bookmark to the blog");
		addBookmarkToBlog(blog, bookmark, UUID);

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
}

	
	/**
	 * <ul>
	 * <li><B>Info:</B>Data Population - UserB Creates a Bookmark & UserA Adds Bookmark to a Blog</li>
	 * <li><B>Step:</B>UserB creates an Blog via API</li>
	 * <li><B>Step:</B>UserB adds UserA as an Author to the blog</li>
	 * <li><B>Step:</B>UserB navigates to Bookmarks and creates a standalone bookmark</li>
	 * <li><B>Step:</B>UserA logs into Bookmarks & changes the view to Details</li>
	 * <li><B>Step:</B>UserA selects More Actions > Add to Blog option for the bookmark</li>
	 * <li><B>Step:</B>UserA selects the blog name </li>
	 * <li><B>Step:</B>UserA navigates to Blogs (Public Bookmarks view)</li>
	 * <li><B>Verify:</B>Bookmark appears</li> 
	 * </ul>
	 * <B>Note:</B>Cloud does not support Standalone Bookmarks (Dogear) therefore it will not work in that environment
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userBCreatesBookmarkUserAAddsBookmarkToBlog(){
		
		String testName = ui.startTest() + Helper.genDateBasedRand();		
		String url = "http://www.msnbc.com";		
		Member memberOwner = new Member(BlogRole.OWNER, testUser1);		
		String blogAddress = Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal();
		
		BaseDogear bookmark = new BaseDogear.Builder("GDPR: Bookmark - " + testName, url)
							                .description("GDPR data pop - this bookmark will be added to a blog")
							                .build();
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: Blog - " + testName , blogAddress)
  						            .description("GDPR data pop - UserB created blog, UserA will add a standalone bookmark to this blog")
  						            .theme(Theme.Blog_with_Bookmarks)  						            
  						            .build();
		
		log.info("INFO: Log into Blogs as UserB: " + testUser2.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser2);

		log.info("INFO: Create a new Blog");
		blog.create(blogsUI);
		
		log.info("INFO: Click on the Settings link for the first Blog listed");
		ui.getFirstVisibleElement(BlogsUIConstants.SettingsLink).click();

		log.info("INFO: Click on the link Author");
		BlogSettings_LeftNav_Menu.AUTHORS.select(blogsUI);		
		
		log.info("INFO: Add the member as an 'Owner' to the Blog");
		blogsUI.addMember(memberOwner);		

		log.info("INFO: Navigate to Bookmarks");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		ui.clickLinkWait(DogearUIConstants.bookmarksOption);

		log.info("INFO: Navigate to the Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		log.info("INFO: Create bookmark");
		ui.create(bookmark);

		log.info("INFO: Get the bookmark UUID");
		String UUID = ui.getUUID(bookmark);

		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: Log into Bookmarks as UserA: " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentDogear);
		blogsUI.login(testUser1);
		
		log.info("INFO: Navigate to the Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		log.info("INFO: Add the bookmark to the blog");
		addBookmarkToBlog(blog, bookmark, UUID);
		
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);

		ui.endTest();
}
	
	/**
	 * createCommunityAndBookmark will: 
	 * - create a community using API 
	 * - get the community UUID
	 * - log into Bookmarks as the test user
	 * - navigate to Public Bookmarks view & create a bookmark
	 * 
	 * @param community - community to be created
	 * @param bookmark - bookmark to be created
	 * @param apiCommOwner - Community API owner to create the community
	 * @param testUser - user logging into standalone bookmarks (Dogear)
	 */
	private void createCommunityAndBookmark(BaseCommunity community, BaseDogear bookmark, APICommunitiesHandler apiCommOwner, User testUser){
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiCommOwner);

		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner, commAPI);

		log.info("INFO: Login to Bookmarks as User: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser);

		log.info("INFO: Navigate to the Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);

		log.info("INFO: Create a bookmark");
		ui.create(bookmark);


}
	
	/**
	 * addBookmarkToCommunity will: 
	 * - navigate to Public Bookmarks view 
	 * - change view to 'Details'
	 * - locate the bookmark, click More Actions > Add to Community & select the community to add the bookmark to
	 * - navigate to communities & make sure the bookmark appears
	 * 
	 * @param community - community to add bookmark to
	 * @param bookmark - bookmark to be added to community
	 * @param bookmarkUUID - UUID of the bookmark
	 */
	
	private void addBookmarkToCommunity(BaseCommunity community, BaseDogear bookmark, String bookmarkUUID){
		log.info("INFO: Navigate to the Public Bookmarks view");
		ui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		log.info("INFO: Refresh browser window to make sure bookmark appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Change view from Display to Details");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);

		//Get original window handle as Add Bookmark open in separate window
		String originalWindow = driver.getWindowHandle();

		log.info("INFO: For the newly created bookmark, click More Actions > Add to Community");
		Dogear_MoreActions_Menu.ADD_TO_COMMUNITY.select(ui, bookmarkUUID);

		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().AddBookmarkDialogTitle);

		log.info("INFO: Select the community created earlier from the list");
		ui.clickLinkWait(DogearUI.selectCommunityToAddBookmarkTo(community));

		log.info("INFO: Click on the Save button");
		ui.clickSaveButton();

		driver.switchToWindowByHandle(originalWindow);

		log.info("INFO: Cick on Communities I'm an Owner from the mega-menu");
		ui.fluentWaitPresent(BaseUIConstants.MegaMenuApps);
		ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenu);
		Community_MegaMenu_Menu.IM_AN_OWNER.select(commUI);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Verify the bookmark appears in the community");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()), 
				"ERROR: The bookmark was not found in the community");
	}
	
	/**
	 * addBookmarkToActivity will: 
	 * - change view to 'Details'
	 * - locate the bookmark, click More Actions > Add to Activity & select the activity to add the bookmark to
	 * - navigate to activities & make sure the bookmark appears
	 * 
	 * @param activity - activity to add bookmark to
	 * @param bookmark - bookmark to be added to activity
	 * @param bookmarkUUID - UUID of the bookmark
	 */
	
	private void addBookmarkToActivity(BaseActivity activity, BaseDogear bookmark, String bookmarkUUID){
		log.info("INFO: Change the view from the Display to Details");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);

		//Get original window handle as Add Bookmark open in separate window
		String originalWindow = driver.getWindowHandle();

		log.info("INFO: For the newly created bookmark, click More Actions > Add to Activity");		
		Dogear_MoreActions_Menu.ADD_TO_ACTIVITY.select(ui, bookmarkUUID);

		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().AddBookmarkDialogTitle);		

		log.info("INFO: Select the activity created earlier from the list");
		ui.clickLinkWait(DogearUI.selectActivityToAddBookmarkTo(activity));
		
		log.info("INFO: Click on the Save button");
		ui.clickSaveButton();

		driver.switchToWindowByHandle(originalWindow);
		
		log.info("INFO: Select Activities from the mega-menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		ui.clickLinkWait(ActivitiesUIConstants.activitiesOption);

		log.info("INFO: Open the activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		log.info("INFO: Verify the bookmark appears in the activity");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()),
				"ERROR: The bookmark was not found in the activity");
	}
	
	/**
	 * addBookmarkToBlog will: 
	 * - change view to 'Details'
	 * - locate the bookmark, click More Actions > Add to Blog & select community to add bookmark to
	 * - navigate to blogs & make sure the bookmark appears
	 * 
	 * @param blog - blog to add bookmark to
	 * @param bookmark - bookmark to be added to blog
	 * @param bookmarkUUID - UUID of the bookmark
	 */
	
	private void addBookmarkToBlog(BaseBlog blog, BaseDogear bookmark, String bookmarkUUID){
		log.info("INFO: Change from the Display to Details view");
		ui.clickLinkWait(DogearUIConstants.BookmarksList_DetailsView);

		//Get original window handle as Add Bookmark open in separate window
		String originalWindow = driver.getWindowHandle();

		log.info("INFO: For the newly created bookmark select More Actions > Add to Blog");		
		Dogear_MoreActions_Menu.ADD_TO_BLOG.select(ui, bookmarkUUID);

		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().AddBookmarkDialogTitle);

		log.info("INFO: Select the blog to add the bookmark to");
		ui.clickLinkWait(DogearUI.selectBlogToAddBookmarkTo(blog)); 

		log.info("INFO: Click on the Save button");
		ui.clickSaveButton();

		driver.switchToWindowByHandle(originalWindow);

		log.info("INFO: Select Blogs from the mega-menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		ui.clickLinkWait(BlogsUIConstants.blogsOption);
		
		log.info("INFO: Click on the 'Public Blogs' view");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);

		log.info("INFO: Verify the bookmark appears in the blogs view");
		Assert.assertTrue(driver.isTextPresent(bookmark.getTitle()), 
				"ERROR: The bookmark was not found in the blog");
	}
}
