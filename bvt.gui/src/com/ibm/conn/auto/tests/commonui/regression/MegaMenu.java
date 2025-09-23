package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.WikisUI;

public class MegaMenu extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(MegaMenu.class);
	private ActivitiesUI aui;
	private BlogsUI bui;
	private CommunitiesUI cui;
	private DogearUI dui;
	private ProfilesUI pui;
	private HomepageUI hui;
	private FilesUI fui;
	private ForumsUI fmui;
	private WikisUI wui;
	private TestConfigCustom cfg;	
	private User testUser;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		aui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		bui = BlogsUI.getGui(cfg.getProductName(), driver);
		cui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		dui = DogearUI.getGui(cfg.getProductName(), driver);
		fui = FilesUI.getGui(cfg.getProductName(), driver);
		fmui = ForumsUI.getGui(cfg.getProductName(), driver);
		hui = HomepageUI.getGui(cfg.getProductName(), driver);
		pui = ProfilesUI.getGui(cfg.getProductName(), driver);
		wui = WikisUI.getGui(cfg.getProductName(), driver);
	
	}	

	
	/**
	* validateActivitiesMenu()
	*<ul>
	*<li><B>Info:</B> Validating Activities menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>
	*<li><B>Verify:</B> Activity option is present</li>
	*<li><B>Verify:</B> To Do List option is present</li>
	*<li><B>Verify:</B> High Priority Activities option is present</li>
    *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"}  , enabled=false )
	public void validateActivitiesMenu() throws Exception {
	
		aui.startTest();
		
		//Load the component and login
		aui.loadComponent(Data.getData().ComponentActivities);
		aui.login(testUser);
		
		//Click on Mega Menu - Apps
		log.info("INFO: Click on Mega Menu Apps");
		aui.clickLinkWait(aui.getMegaMenuApps());	
		
		//Validate option Activities
		log.info("INFO: Validate option 'Activities'");
		Assert.assertTrue(aui.fluentWaitPresent(ActivitiesUIConstants.activitiesOption),
						  "Unable to locate Mega Menu item 'Activiies'");

		//Validate option To Do List
		log.info("INFO: Validate optiont 'To Do List'");
		Assert.assertTrue(aui.fluentWaitPresent(ActivitiesUIConstants.activitiesToDoList),
						  "Unable to locate Mega Menu item 'To Do List'");

		//Validate option High Priority Activities
		log.info("INFO: Validate option 'High Priority Activities'");
		Assert.assertTrue(aui.fluentWaitPresent(ActivitiesUIConstants.activitiesHighPriorityAct),
						  "Unable to locate Mega Menu item 'High Priority Activities'");

		aui.endTest();
	}
	

	/**
	* validateBlogsMenu()
	*<ul>
	*<li><B>Info:</B> Validating Blogs menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>
	*<li><B>Verify:</B> Blogs option is present</li>
	*<li><B>Verify:</B> Latest Entries option is present</li>
	*<li><B>Verify:</B> Public Blogs Listing option is present</li>
    *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} , enabled=false  )
	public void validateBlogsMenu() throws Exception {
		
		bui.startTest();
		
		//Load the component and login
		log.info("INFO: Load Blogs and log in");
		bui.loadComponent(Data.getData().ComponentBlogs);
		bui.login(testUser);
		
		//Click on Mega Menu - Apps
		log.info("INFO: Click on Mega Menu Apps");
		bui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate option Blogs 
		log.info("INFO: Validate option 'Blogs'");
		Assert.assertTrue(bui.fluentWaitPresent(BlogsUIConstants.blogsOption),
						  "ERROR: Unable to validate Mega Menu item 'Blogs'");

		//Validate option Latest Entries 
		log.info("INFO: Validate option 'Latest Entries'");
		Assert.assertTrue(bui.fluentWaitPresent(BlogsUIConstants.blogsLatestEntries),
						  "ERROR: Unable to validate Mega Menu item 'Latest Entries'");

		//Validate option Public Blogs Listing 
		log.info("INFO: Validate option 'Public Blogs Listing'");
		Assert.assertTrue(bui.fluentWaitPresent(BlogsUIConstants.blogsPublicBlogsListing),
						  "ERROR: Unable to validate Mega Menu item 'Public Blogs Listing'");

		bui.endTest();
	}	

	
	/**
	* validateBookmarksMenu()
	*<ul>
	*<li><B>Info:</B> Validating Bookmarks menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>>
	*<li><B>Verify:</B> Check Bookmarks option is present</li>
	*<li><B>Verify:</B> Check Popular option is present</li>
	*<li><B>Verify:</B> Check  Public Bookmarks is present</li>
    *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"}  , enabled=false )
	public void validateBookmarksMenu() throws Exception {
		
		dui.startTest();
		
		//Load the component and login
		dui.loadComponent(Data.getData().ComponentDogear);
		dui.login(testUser);
		
		//Click Mega Menu item
		log.info("INFO: Select Bookmarks Mega Menu option");
		dui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate option Bookmarks 
		log.info("INFO: Validate option 'Bookmarks'");
		Assert.assertTrue(dui.fluentWaitPresent(DogearUIConstants.bookmarksOption ),
						  "Unable to validate Mega Menu item 'Bookmarks'");

		//Validate option Popular option
		log.info("INFO: Validate option 'Popular'");
		Assert.assertTrue(dui.fluentWaitPresent(DogearUIConstants.bookmarksPopular),
						  "Unable to validate Mega Menu item 'Popular'");

		//Validate option Public Bookmarks 
		log.info("INFO: Validate option 'Public Bookmarks'");
		Assert.assertTrue(dui.fluentWaitPresent(DogearUIConstants.bookmarksPublicBookmarks),
						  "Unable to validate Mega Menu item 'Public Bookmarks'");

		dui.endTest();
	
	}	
	

	/**
	* validateCommunitiesMenu()
	*<ul>
	*<li><B>Info:</B> Validating Communities filter menu items on my communities page</li>
	*<li><B>Verify:</B> I'm an owner option is present</li>
	*<li><B>Verify:</B> I'm an Member option is present</li>
	*<li><B>Verify:</B> I'm Following option is present</li>
	*<li><B>Verify:</B> I created option is present</li>
	*<li><B>Verify:</B> My Organization Communities option is present</li>
    *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"})
	public void validateCommunitiesMenu() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		cui.startTest();
		
		//Load the component and login
		logger.strongStep("Load communities url in browser");
		log.info("INFO: Load communities url in browser");
		cui.loadComponent(Data.getData().ComponentCommunities);
		
		logger.strongStep("Login to connections with a test user");
		log.info("INFO: Login to connections with a test user");
		cui.login(testUser);
		
		//Validate option I'm an owner
		logger.strongStep("Validate option I'm an owner");
		log.info("INFO: Validate option I'm an owner");
		Assert.assertTrue(cui.fluentWaitTextPresent("I'm an owner"),
						  "ERROR:The content I'm an owner is not present");

		//Validate option I'm a Member
		logger.strongStep("Validate option I'm a Member");
		log.info("INFO: Validate option I'm a Member");
		Assert.assertTrue(cui.fluentWaitTextPresent("I'm a Member"),
						  "ERROR:The content I'm a Member is not present");

		//Validate option I'm Following
		logger.strongStep("Validate option I'm Following");
		log.info("INFO: Validate option I'm Following");
		Assert.assertTrue(cui.fluentWaitTextPresent("I'm Following"),
						  "ERROR:The content I'm Following is not present");

		//Validate option I Created
		logger.strongStep("Validate option I Created");
		log.info("INFO: Validate option I Created");
		Assert.assertTrue(cui.fluentWaitTextPresent("I Created"),
						  "ERROR:The content I Created is not present");

		//Validate option My Organization Communities
		logger.strongStep("Validate option My Communities");
		log.info("INFO: Validate option My Communities");
		Assert.assertTrue(cui.fluentWaitTextPresent("My Communities"),
						  "ERROR:The content My Communities is not present");

		cui.endTest();
	}
	
	
	/**
	* validateFilesMenu()
	*<ul>
	*<li><B>Info:</B> Validating Files menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>
	*<li><B>Verify:</B> Files option is present</li>
	*<li><B>Verify:</B> Shared With Me option is present</li>
	*<li><B>Verify:</B> Pinned Folders option is present</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} , enabled=false  )
	public void validateFilesMenu() throws Exception {
			
		fui.startTest();
		
		//Load the component and login
		log.info("INFO: Load Files and log in");
		fui.loadComponent(Data.getData().ComponentFiles);
		fui.login(testUser);
		
		//Click on Mega Menu - Apps
		log.info("INFO: Click on Mega Menu Apps");
		fui.clickLinkWait(fui.getMegaMenuApps());
		
		//Validate option Files 
		log.info("INFO: Validate option 'Files'");
		Assert.assertTrue(fui.fluentWaitPresent(fui.getFilesOption()),
						  "ERROR: Unable to validate Mega Menu item 'Files'");

		//Validate option Shared With Me
		log.info("INFO: Validate option 'Shared With Me'");
		Assert.assertTrue(fui.fluentWaitPresent(FilesUIConstants.filesSharedWithMe ),
						  "ERROR: Unable to validate Mega Menu item 'Shared With Me'");
		
		//Validate option Pinned Folders
		log.info("INFO: Validate option 'Pinned Folders'");
		Assert.assertTrue(fui.fluentWaitPresent(FilesUIConstants.filesPinnedFolders),
						  "ERROR: Unable to validate Mega Menu item 'Pinned Folders'");

		fui.endTest();
	}

	
	/**
	* validateForumsMenu()
	*<ul>
	*<li><B>Info:</B> Validating Forums menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>
	*<li><B>Verify:</B> Forums options is present</li>
	*<li><B>Verify:</B> I'm an Owner option is present</li>
	*<li><B>Verify:</B> Public Forums option is present</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} )
	public void validateForumsMenu() throws Exception {
			
		fmui.startTest();
		
		//Load the component and login
		fmui.loadComponent(Data.getData().ComponentForums);
		fmui.login(testUser);
		
		//Click on Mega Menu - Apps
		log.info("INFO: Click on Mega Menu Apps");
		fmui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate option Forums
		log.info("INFO: Validate option 'Forums'");
		Assert.assertTrue(fmui.fluentWaitPresent(ForumsUIConstants.forumsOption),
						  "ERROR: Unable to locate the Mega Menu item 'Forums'");

		//Validate option I'm an Owner
		log.info("INFO: Validate option 'I'm an Owner'");
		Assert.assertTrue(fmui.fluentWaitPresent(ForumsUIConstants.forumsImAnOwner),
						  "ERROR: Unable to locate the Mega Menu item 'I'm an Owner'");

		//Validate option Public Forums 
		log.info("INFO: Validate option 'Public Forums'");
		Assert.assertTrue(fmui.fluentWaitPresent(ForumsUIConstants.forumsPublicForums),
						  "ERROR: Unable to locate the Mega Menu item 'Public Forums'");

		fmui.endTest();	
	}
	

	/**
	* validateHomepageMenu()
	*<ul>
	*<li><B>Info:</B> Validating Mega Menu - Homepage</li>
	*<li><B>Step:</B> Load Homepage and login</li>
	*<li><B>Verify:</B> Homepage option is present</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} )
	public void validateHomepageMenu() throws Exception {
				
		hui.startTest();
		
		//Load component and login
		log.info("INFO: Load Homepage and log in");
		hui.loadComponent(Data.getData().ComponentHomepage);
		hui.login(testUser);

		//Validate option Homepage
		log.info("INFO: Validate mega menu Homepage");
		Assert.assertTrue(hui.fluentWaitPresent(HomepageUIConstants.homepage),
				  		  "ERROR: Unable to validate Mega Menu 'Homepage' option");
		
		hui.endTest();		
	}
	
	
	/**
	* validateProfilesMenu()
	*<ul>
	*<li><B>Test Scenario:</B> Verifying the Profiles's Main Navigation menu</li>
	*<li><B>Step:</B> Load Profiles and log in</li>
	*<li><B>Verify:</B> Profiles menu options: My Profile, My Network, Directory, Edit My Profile and Status Updates exist</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} )
	public void validateProfilesMenu() throws Exception {

		pui.startTest();	
		            
		//Load the component and login
		pui.loadComponent(Data.getData().ComponentProfiles);
		pui.login(testUser);
	
		//Click on Profiles from mega menu
		log.info("INFO:Click on profiles banner");
		pui.clickLinkWait(ProfilesUIConstants.ProfilesBanner);
		
		//Validate option My Profile
		log.info("INFO: Validate option My Profile");
		Assert.assertTrue(pui.fluentWaitTextPresent("My Profile"),
						  "ERROR:The content My Profile is not present");

		//Validate option My Network
		log.info("INFO: Validate option My Network");
		Assert.assertTrue(pui.fluentWaitTextPresent("My Network"),
						  "ERROR:The content My Network is not present");

		//Validate option Directory
		log.info("INFO: Validate option Directory");
		Assert.assertTrue(pui.fluentWaitTextPresent("Directory"),
						  "ERROR:The content Directory is not present");

		//Validate option Edit My Profile
		log.info("INFO: Validate option Edit My Profile");
		Assert.assertTrue(pui.fluentWaitTextPresent("Edit My Profile"),
						  "ERROR:The content Edit My Profile is not present");

		//Validate option Status Updates
		log.info("INFO: Validate option Status Updates");
		Assert.assertTrue(pui.fluentWaitTextPresent("Status Updates"),
						  "ERROR:The content Status Updates is not present");
	
		pui.endTest();
	}

	/**
	* validateWikisMenu()
	*<ul>
	*<li><B>Info:</B> Validating Wikis menu items on Mega Menu</li>
	*<li><B>Step:</B> Expand Apps drop down</li>
	*<li><B>Verify: </B>Wikis option is present</li>
	*<li><B>Verify: </B>I'm an Owner option is present</li>
	*<li><B>Verify: </B>Public Wikis option is present</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Mega Menu</a></li>
	*</ul>
	*Note: On Prem only, Mega Menu in SC is owned by Foundation team
	*/
	@Test (groups = {"regression"} )
	public void validateWikisMenu() throws Exception {
		
		wui.startTest();
				
		//Load the component and login
		wui.loadComponent(Data.getData().ComponentWikis);
		wui.login(testUser);
		
		//Click on Mega Menu - Apps
		log.info("INFO: Click on Mega Menu Apps");
		wui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate option Wikis 
		log.info("INFO: Validate option 'Wikis'");
		Assert.assertTrue(wui.fluentWaitPresent(WikisUIConstants.wikisOption),
						  "Unable to validate Mega Menu item 'Wikis'");

		//Validate option I'm an Owner 
		log.info("INFO: Validate option 'I'm an Owner'");
		Assert.assertTrue(wui.fluentWaitPresent(WikisUIConstants.wikisImAnOwner),
						  "Unable to validate Mega Menu item 'I'm an Owner'");

		//Validate option Public Wikis 
		log.info("INFO: Validate option 'Public Wikis'");
		Assert.assertTrue(wui.fluentWaitPresent(WikisUIConstants.wikisPublicWikis),
						  "Unable to validate Mega Menu item 'Public Wikis'");

		wui.endTest();	
	}
	
}
