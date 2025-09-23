package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Wiki_Page_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class WidgetTree extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(WidgetTree.class);
	private WikisUI wkUI;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private String serverURL;
	private BaseCommunity.Access defaultAccess;
	
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		wkUI = WikisUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}

	
	/**
	* expandNcollapseWikiPages()
	* <ul>
	* <li><B>Info: </B>Test to expand and collapse Wiki pages</li>
	* <li><B>Step: </B>Create community using API</li>
	* <li><B>Step: </B>Add a Wiki widget using API</li>
	* <li><B>Step: </B>Open the community</li>
	* <li><B>Step: </B>Navigate to Wiki</li>
	* <li><B>Step: </B>Create three pages as ownerUser</li>
	* <li><B>Step: </B>Log out and log back in as memberUser</li>
	* <li><B>Step: </B>Expand top level wiki pageA</li>
	* <li><B>Step: </B>Expand second level wiki pageB</li>
	* <li><B>Verify: </B>Third level wiki pageC is present</li>
	* <li><B>Step: </B>Collapse top level wiki pageA</li>
	* <li><B>Verify: </B>Verify top level wiki pageA is collapsed</li>
	* <li><B>Step: </B>Delete the community created with api</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Widget Tree</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression", "regressioncloud"})
	public void expandNcollapseWikiPages() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = wkUI.startTest();

		//Allocate users
		User ownertUser = cfg.getUserAllocator().getUser();
		User memberUser = cfg.getUserAllocator().getUser();
		
		//Create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, ownertUser.getAttribute(cfg.getLoginPreference()), ownertUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community for " + testName)
									 			   .addMember(new Member(CommunityRole.MEMBERS, memberUser))
									 			   .access(defaultAccess)
									 			   .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder(testName + "_PageA", PageType.Peer)
												 .description("this is a test description for creating a peer wiki page")
												 .build();
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder(testName + "_PageB", PageType.Child)
												 .description("this is a test description for creating a child wiki page")
												 .build();		
		BaseWikiPage wikiPageC = new BaseWikiPage.Builder(testName + "_PageC", PageType.Child)
												 .description("this is a test description for creating a child wiki page")
												 .build();

		
		//Create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//Add the UUID to community
		logger.strongStep("Get the UUID of the Community");
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Add Wiki widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		log.info("INFO: Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) 
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
				
		//Load component and login as ownerUser
		logger.strongStep("Load component and login as ownerUser: " + ownertUser.getDisplayName());
		log.info("INFO: Load component and login as ownerUser: " + ownertUser.getDisplayName());
		wkUI.loadComponent(Data.getData().ComponentCommunities);
		wkUI.login(ownertUser);
			
		//Navigate to the API community
		logger.strongStep("Navigate to the Community using UUID");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		//Select Wiki from the left navigation menu
		logger.strongStep("Select Wiki from the left navigation menu");
		log.info("INFO: Select Wiki from the left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//Create Wiki pages
		logger.strongStep("Create new Wiki Pages A, B, and C");
		log.info("INFO: Create new Wiki Pages A, B, and C");
		wikiPageA.create(wkUI);	
		wikiPageB.create(wkUI);	
		wikiPageC.create(wkUI);

		//Log out as ownerMember
		logger.strongStep("Log out as ownerMember");
		log.info("INFO: Log out as ownerMember");
		wkUI.close(cfg);
		
		//Load component and login as memberUser
		logger.strongStep("INFO: Load component and login as memberUser: " + memberUser.getDisplayName());
		log.info("INFO: Load component and login as memberUser: " + memberUser.getDisplayName());
		wkUI.loadComponent(Data.getData().ComponentCommunities);
		wkUI.login(memberUser);
		
		//Navigate to I'm a Member view and select the community
		logger.strongStep("Navigate to I'm a Member view and select the community");
		log.info("INFO:Navigate to I'm a Member view and select the community");
		Community_View_Menu.IM_A_MEMBER.select(cUI);
		wkUI.clickLinkWait("css=div[aria-label="+community.getName()+"] div.card-header");

		//Select Wiki from the left navigation menu
		logger.strongStep("Select Wiki from the left navigation menu");
		log.info("INFO: Select Wiki from the left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//Expand top level wiki pageA
		logger.strongStep("Expand top level wiki pageA");
		log.info("INFO: Expand top level wiki pageA");
		wkUI.expandPage(wikiPageA);
					
		//Expand second level wiki pageB
		logger.strongStep("Expand top level wiki pageB");
		log.info("INFO: Expand second level wiki pageB");
		wkUI.expandPage(wikiPageB);
		
		//Verify third level wiki pageC is present and can be selected
		logger.strongStep("Verify third level wiki pageC is present and can be selected");
		log.info("INFO: Verify third level wiki pageC is present and can be selected");
		driver.getFirstElement(wkUI.getPageSelector(wikiPageC)).click();
		
		//Verify wiki pageC's location on breadcrumbs trail
		logger.strongStep("Verify wiki pageC's location on breadcrumbs trail");
		log.info("INFO: Verify wiki pageC's location on breadcrumbs trail");
		String location = "You are in:  " + community.getName() + " Wiki > " + wikiPageA.getName() + " > " + wikiPageB.getName() + " > " + wikiPageC.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);
		
		//Click on top level wiki pageA
		logger.strongStep("Click on top level wiki pageA");
		log.info("INFO: Click on top level wiki pageA");
		driver.getFirstElement(wkUI.getPageSelector(wikiPageA)).click();

		//Collapse top level wiki pageA
		logger.strongStep("Collapse top level wiki pageA");
		log.info("INFO: Collapse top level wiki pageA");
		wkUI.clickLink(WikisUIConstants.TwistyTopLevelCollapse);
		
		//Validate top level wiki pageA was collapsed by checking wiki pageB is not present
		logger.strongStep("Validate top level wiki pageA was collapsed by checking wiki pageB is not present");
		log.info("INFO: Validate top level wiki pageA was collapsed by checking wiki pageB is not present");
		Assert.assertFalse(wkUI.isTextPresent(wikiPageB.getName()),
	 			"ERROR: Top level wiki pageA was NOT collasped");
		
		//Clean Up: Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
		
		wkUI.endTest();
	}	
	
	
	/**
	* moveDownWikiPages()
	* <ul>
	* <li><B>Info: </B>Test to move Wiki page one level down</li>
	* <li><B>Step: </B>Create community using API</li>
	* <li><B>Step: </B>Add a Wiki widget using API</li>
	* <li><B>Step: </B>Open the community</li>
	* <li><B>Step: </B>Navigate to Wiki</li>
	* <li><B>Step: </B>Create three pages as ownerUser</li>
	* <li><B>Step: </B>Move wiki pageC from second level to third level</li>
	* <li><B>Verify: </B>Verify wiki pageC is moved correctly</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Widget Tree</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression", "regressioncloud"})
	public void moveDownWikiPages() throws Exception{
		
		String testName = wkUI.startTest();

		//Allocate users
		User ownertUser = cfg.getUserAllocator().getUser();
		
		//Create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, ownertUser.getAttribute(cfg.getLoginPreference()), ownertUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community for " + testName)
									 			   .access(defaultAccess)
									 			   .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder(testName + "_PageA", PageType.Peer)
												 .description("this is a test description for creating a peer wiki page")
												 .build();
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder(testName + "_PageB", PageType.Child)
												 .description("this is a test description for creating a child wiki page")
												 .build();		
		BaseWikiPage wikiPageC = new BaseWikiPage.Builder(testName + "_PageC", PageType.Peer)
												 .description("this is a test description for creating a peer wiki page")
												 .build();

		
		//Create community
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//Add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Add Wiki widget
		log.info("INFO: Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) 
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
				
		//Load component and login as ownerUser
		log.info("INFO: Load component and login as ownerUser: " + ownertUser.getDisplayName());
		wkUI.loadComponent(Data.getData().ComponentCommunities);
		wkUI.login(ownertUser);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		//Select Wiki from the left navigation menu
		log.info("INFO: Select Wiki from the left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//Create Wiki pages	
		log.info("INFO: Create new Wiki Pages A, B, and C");
		wikiPageA.create(wkUI);	
		wikiPageB.create(wkUI);	
		wikiPageC.create(wkUI);

		//Focus is now on wiki pageC, click on Page Actions/Move Page
		log.info("Focus is now on wiki pageC, click on Page Actions/Move Page");
		Wiki_Page_Menu.MOVEPAGE.select(wkUI);
		
		//Verify Move Page dialog displays
		log.info("Verify Move Page dialog displays");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MovePageDig));
	
		//Move wiki pageC into wiki pageB
		driver.getSingleElement(WikisUIConstants.PageNameTypeBox).type(wikiPageB.getName());
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.PageNameDropdown));
		driver.getSingleElement(WikisUIConstants.PageNameDropdownLink).click();
		
		//Click on OK button to close Move Page dialog
		log.info("Click on OK button to close Move Page dialog");
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
	
		//Verify wiki pageC's location on breadcrumbs trail
		log.info("INFO: Verify wiki pageC's location on breadcrumbs trail");
		wkUI.fluentWaitElementVisible(WikisUIConstants.BreadCrumb);
		String location = "You are in:  " + community.getName() + " Wiki > " + wikiPageA.getName() + " > " + wikiPageB.getName() + " > " + wikiPageC.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
		
		wkUI.endTest();
	}	

	
	/**
	* moveUpWikiPages()
	* <ul>
	* <li><B>Info: </B>Test to move Wiki page from bottom level to top level</li>
	* <li><B>Step: </B>Create community using API</li>
	* <li><B>Step: </B>Add a Wiki widget using API</li>
	* <li><B>Step: </B>Open the community</li>
	* <li><B>Step: </B>Navigate to Wiki</li>
	* <li><B>Step: </B>Create three pages as ownerUser</li>
	* <li><B>Step: </B>Move wiki pageC from third level to top level</li>
	* <li><B>Verify: </B>Verify wiki pageC is moved correctly</li>
	* <li><B>Step: </B>Delete the community created with api</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Widget Tree</a></li>
	*</ul>
	*/	
	@Test(groups = {"regression", "regressioncloud"})
	public void moveUpWikiPages() throws Exception{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = wkUI.startTest();

		//Allocate users
		User ownertUser = cfg.getUserAllocator().getUser();
		
		//Create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, ownertUser.getAttribute(cfg.getLoginPreference()), ownertUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community for " + testName)
									 			   .access(defaultAccess)
									 			   .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder(testName + "_PageA", PageType.Peer)
												 .description("this is a test description for creating a peer wiki page")
												 .build();
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder(testName + "_PageB", PageType.Child)
												 .description("this is a test description for creating a child wiki page")
												 .build();
		BaseWikiPage wikiPageC = new BaseWikiPage.Builder(testName + "_PageC", PageType.Child)
												 .description("this is a test description for creating a child wiki page")
												 .build();
		
		//Create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//Add the UUID to community
		logger.strongStep("Get the UUID of the Community");
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Add Wiki widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		log.info("INFO: Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) 
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
				
		//Load component and login as ownerUser
		logger.strongStep("Load component and login as ownerUser: " + ownertUser.getDisplayName());
		log.info("INFO: Load component and login as ownerUser: " + ownertUser.getDisplayName());
		wkUI.loadComponent(Data.getData().ComponentCommunities);
		wkUI.login(ownertUser);
		
		//Navigate to the API community
		logger.strongStep("Navigate to the Community using UUID");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		//Select Wiki from the left navigation menu
		logger.strongStep("Select Wiki from the left navigation menu");
		log.info("INFO: Select Wiki from the left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
				
		//Create Wiki pages
		logger.strongStep("Create new Wiki Pages A, B and C");
		log.info("INFO: Create new Wiki Pages A, B and C");
		wikiPageA.create(wkUI);
		wikiPageB.create(wkUI);
		wikiPageC.create(wkUI);
		
		//Focus is on wiki pageC, click on Page Actions/Move Page to launch Move Page dialog
		logger.strongStep("Focus is on wiki pageC, click on Page Actions/Move Page to launch Move Page dialog");
		log.info("INFO: Focus is on wiki pageC, click on Page Actions/Move Page to launch Move Page dialog");
		Wiki_Page_Menu.MOVEPAGE.select(wkUI);
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MovePageDig));
		
		//Select 'Make this a top level page' option so wiki pageC will move to top level
		logger.strongStep("Select 'Make this a top level page' option so wiki pageC will move to top level");
		log.info("INFO: Select 'Make this a top level page' option so wiki pageC will move to top level");
		driver.getSingleElement(WikisUIConstants.MarkAsTopCheckbox).click();
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		
		//Verify wiki pageC is moved correctly by checking the location on breadcrumbs trail
		logger.strongStep("Verify wiki pageC is moved correctly by checking the location on breadcrumbs trail");
		log.info("INFO: Verify wiki pageC is moved correctly by checking the location on breadcrumbs trail");
		wkUI.fluentWaitElementVisible(WikisUIConstants.BreadCrumb);
		wkUI.fluentWaitPresent("css=span:contains('The page was moved.')");
		String location = "You are in:  " + community.getName() + " Wiki > " +wikiPageA.getName()+" > " +wikiPageB.getName()+" > "+ wikiPageC.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);
		
		//Clean Up: Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
		
		wkUI.endTest();
	}
	
}
