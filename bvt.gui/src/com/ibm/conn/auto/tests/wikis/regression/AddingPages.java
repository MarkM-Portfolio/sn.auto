package com.ibm.conn.auto.tests.wikis.regression;


import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class AddingPages extends  SetUpMethods2{

	
	private static Logger log = LoggerFactory.getLogger(AddingPages.class);
	private WikisUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3, testUser4;
	private List<Member> members;
	private APIWikisHandler apiOwner;
	private APICommunitiesHandler apiComOwner;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){		
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());	
		apiComOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.OWNER, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.OWNER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));

		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a new page for a wiki using the New Page link in the left navigation menu.
	*<li><B>Step: </B>Create a wiki via API. 
	*<li><B>Step: </B>Login as the owner of the wiki. 
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the New Page link in the left navigation menu to add a page and save the changes.
	*<li><B>Verify: </B>The page is created.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void pagesFromNav() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage navPage = new BaseWikiPage.Builder("NavPage" + Helper.genDateBasedRand(), PageType.NavPage)
												.tags("tag1")
												.description("this is a test description for creating from Left Nav wiki page")
												.build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a page using the New Page link in the nav
		logger.strongStep("Click on the New Page link in the left navigation menu to add a page and save the changes");
		log.info("INFO: Add a page using the New Page link in the left navigation menu");
		navPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  "ERROR: Page was not created");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a peer page for a wiki using the 'Create Peer' option under the 'Page Actions' menu.
	*<li><B>Step: </B>Create a wiki via API. 
	*<li><B>Step: </B>Login as the owner of the wiki. 
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void pagesActionsPeerPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
						  "ERROR: New page was not created");
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a child page for a wiki using the 'Create Child' option under the 'Page Actions' menu.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Child' option to create a child page and save the changes.
	*<li><B>Verify: </B>The child page is created.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void pagesActionsChildPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		BaseWikiPage childPage = new BaseWikiPage.Builder("ChildPage" + Helper.genDateBasedRand(), PageType.Child)
		 										  .tags("tag1")
		 										  .description("this is a test description for creating a Child wiki page")
		 										  .build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);
		
		//Add a child page using the 'Create Child' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Child' option to create a child page and save the changes");
		log.info("INFO: Add a child page using the 'Create Child' option under 'Page Actions' menu");
		childPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the child page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  "ERROR: Child page was not created");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a peer page by right clicking on the 'Welcome to ' link in the left navigation menu.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Verify: </B>The link 'Welcome to ' appears in the left navigation menu.
	*<li><B>Step: </B>Right click on the link 'Welcome to ' in the left navigation menu.
	*<li><B>Step: </B>Click on the 'Create Peer' option in the right click menu to create a peer page and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void contextPeerPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Context_Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		logger.strongStep("Verify the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in the left navigation menu in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' to appear in the left navigation menu");
		ui.fluentWaitElementVisible(WikisUIConstants.welcomeTo);
		
		logger.strongStep("Right click on the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' in the left navigation menu");
		log.info("INFO: Right click on the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' in the left navigation menu");
		driver.getSingleElement(WikisUIConstants.welcomeTo).rightMouseClick();
		
		//Add a peer page using the 'Create Peer' option in the right click menu
		logger.strongStep("Click on 'Create Peer' option in the right click menu to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option in the right click menu");
		peerPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  				  "ERROR: Peer page was not created");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a child page by right clicking on a peer page link in the left navigation menu.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*<li><B>Verify: </B>The peer page link appears in the left navigation menu.
	*<li><B>Step: </B>Right click on the peer page link in the left navigation menu to create a child page.
	*<li><B>Step: </B>Create a child page and save the changes.
	*<li><B>Verify: </B>The child page is created.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void contextChildPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		BaseWikiPage childPage = new BaseWikiPage.Builder("ChildPage2" + Helper.genDateBasedRand(), PageType.Context_Child)
		  										 .tags("tag1")
		  										 .description("this is a test description for creating a Child wiki page")
		  										 .build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
							"ERROR: Peer page was not created");
		
		logger.strongStep("Verify the peer page link appears in the left navigation menu in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Wait for page to be visible in the left navigation menu");
		ui.fluentWaitElementVisible("css=a[title='" + peerPage.getName() + "']");
		
		logger.strongStep("Right click on the peer page link in the left navigation menu to create a child page");
		log.info("INFO: Create a child page by right clicking on the peer page link in the left navigation menu");
		driver.getSingleElement("css=a[title='" + peerPage.getName() + "']").rightMouseClick();
		
		logger.strongStep("Create a child page and save the changes");
		log.info("INFO: Create a child page");
		childPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the child page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  					"ERROR: Child page was not created");

		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests creating a child page for a wiki using the 'Create new child page' under the About tab.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*<li><B>Step: </B>Create a child page using the 'Create new child page' link under the About tab and save the changes.
	*<li><B>Verify: </B>The child page is created.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void aboutChildPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		BaseWikiPage childPage = new BaseWikiPage.Builder("ChildPage2" + Helper.genDateBasedRand(), PageType.About_Child)
		  										 .tags("tag1")
		  										 .description("this is a test description for creating a Child wiki page")
		  										 .build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);

		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);

		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
						  "ERROR: Peer page was not created");

		logger.strongStep("Use the 'Create new child page' under the About tab to create a child page and save the changes");
		log.info("INFO: Create a child page using the 'Create new child page' link under the About tab");
		childPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the child page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
						  "ERROR: Child page was not created");

		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests that a peer page can have a title with the length equal to the maximum permissible number of characters.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The link 'Welcome to ' appears in the left navigation menu.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page with a title whose length is equal to the maximum permissible number of characters and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void maxTitlePeerPage() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder(Data.getData().MaxPageName, PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);

		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		logger.strongStep("Verify the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in the left navigation menu in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' to appear in the left navigation menu");
		ui.fluentWaitElementVisible(WikisUIConstants.welcomeTo);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page with a title whose length is equal to the maximum permissible number of characters and save the changes");
		log.info("INFO: Add a peer page with a title that has the number of characters equal to the maximum permissible length using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);

		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  "ERROR: Peer page was not created");
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests that a peer page can't have a title with the greater than the maximum permissible number of characters.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The link 'Welcome to ' appears in the left navigation menu.
	*<li><B>Step: </B>Right click on the link 'Welcome to ' in the left navigation menu.
	*<li><B>Step: </B>Click on 'Create Peer' option in the right click menu to create a peer page with a title whose length is greater than the maximum permissible number of characters and try to save the changes.
	*<li><B>Verify: </B>The error message: 'The page name is too long.' is displayed.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void overMaxTitlePeerPage() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder(Data.getData().MaxPageName + Data.getData().MaxPageName, PageType.NavPage)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in the left navigation menu in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' to appear in the left navigation menu");
		ui.fluentWaitElementVisible(WikisUIConstants.welcomeTo);
		
		logger.strongStep("Right click on the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' in the left navigation menu");
		log.info("INFO: Right click on the link 'Welcome to " + testName + Helper.genDateBasedRand() + "' in the left navigation menu");
		driver.getSingleElement(WikisUIConstants.welcomeTo).click();

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on 'Create Peer' option in the right click menu to create a peer page with a title whose length is greater than the maximum permissible number of characters and try to save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option in the right click menu with a title whose length is greater than the maximum permissible number of characters");
		peerPage.create(ui);
		
		logger.strongStep("Verify the error message: 'The page name is too long.' is displayed");
		log.info("INFO: Validate that the error message: 'The page name is too long.' was displayed");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageTooLongLink),
		  				  "ERROR: The error message: 'The page name is too long.' was not displayed");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Edit button to rename the title of a peer page.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*<li><B>Step: </B>Click on the Edit button to edit the peer page and change its name.
	*<li><B>Verify: </B>The peer page has the new name.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void renamePeerPage() throws Exception {
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("PeerPage" + Helper.genDateBasedRand(), PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page and save the changes");
		log.info("INFO: Add a peer page using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);

		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  				"ERROR: Peer page was not created");

		//change name of wiki page
		logger.strongStep("Choose a different name for the peer page");
		log.info("INFO: Change the name of the peer page");
		peerPage.setName("Rename" + Helper.genDateBasedRand());

		//edit wiki page
		logger.strongStep("Click on the Edit button to edit the peer page and change its name to " + peerPage.getName());
		log.info("INFO: Edit the peer page and change its name to " + peerPage.getName());
		peerPage.edit(ui);

		logger.strongStep("Validate that the peer page has the new name");
		log.info("INFO: Verify that the peer page has the new name");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUI.getWikiPageTitle(peerPage)),
						  "ERROR: Peer page does not have the new name");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of special characters in the name of a peer page.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page whose name contains special characters and save the changes.
	*<li><B>Verify: </B>The peer page is created.
	*<li><B>Verify: </B>The peer page's name that contains special characters is visible.
	*</ul>
	*/	
	@Test(groups = {"regression"})
	public void specCharPeerPage() throws Exception {
				
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder(Data.getData().specialCharacterForWiki, PageType.Peer)
		 										 .tags("tag1")
		 										 .description("this is a test description for creating a Peer wiki page")
		 										 .build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a peer page using the 'Create Peer' option under 'Page Actions' menu
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Peer' option to create a peer page whose name contains special characters and save the changes");
		log.info("INFO: Add a peer page with the name containing special characters using the 'Create Peer' option under 'Page Actions' menu");
		peerPage.create(ui);
		
		logger.strongStep("Validate 'The page was created.' text is displayed");
		log.info("INFO: Validate the peer page was created");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
		  "ERROR: Peer page was not created");
		
		logger.strongStep("Validate that the peer page's name that contains special characters is visible");
		log.info("INFO: Verify that the peer page's name that contains special characters is visible");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUI.getWikiPageTitle(peerPage)),
						"ERROR: The name of the peer page is not visible");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of invalid characters in the name of a peer page.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner of the wiki.
	*<li><B>Step: </B>Open the wiki.
	*<li><B>Verify: </B>The wiki header appears.
	*<li><B>Step: </B>Create a peer page with an invalid name.
	*<li><B>Step: </B>Click on the 'Replace invalid characters with '_'?' link to replace the invalid characters with underscores.
	*<li><B>Step: </B>Click on the 'Save and Close' button.
	*<li><B>Verify: </B>The peer page is created.
	*<li><B>Step: </B>Repeat the process of creating peer pages with names containing different types of invalid characters and then replacing the invalid characters with underscores.
	*<li><B>Verify: </B>Each peer page is successfully created.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void invalidPageNames() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseWikiPage[] invalidPage = new BaseWikiPage[8];

		List<String> specChar = new ArrayList<String>();
		specChar.add(Data.getData().specChar1);
		specChar.add(Data.getData().specChar2);
		specChar.add(Data.getData().specChar3);
		specChar.add(Data.getData().specChar4);
		specChar.add(Data.getData().specChar5);
		specChar.add(Data.getData().specChar6);
		specChar.add(Data.getData().specChar7);
		specChar.add(Data.getData().specChar8);
		
		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.description("Description for test " + testName)
									.readAccess(ReadAccess.WikiOnly)
									.editAccess(EditAccess.EditorsAndOwners)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open Wikis component and login as owner: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Verify the header 'Welcome to " + testName + Helper.genDateBasedRand() + "' appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Waiting for wiki page header 'Welcome to " + testName + Helper.genDateBasedRand() + "'");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		for (int i=0; i < invalidPage.length; i++) { 
			invalidPage[i] = new BaseWikiPage.Builder(specChar.get(i), PageType.Peer)
			 								 .tags("tag1")
			 								 .description("this is a test description")
											 .build();
			
			logger.strongStep("Create a peer page with an invalid name");
			log.info("INFO: Creating invalid named peer page");
			invalidPage[i].create(ui);
			
			logger.strongStep("Click on the 'Replace invalid characters with '_'?' link to replace the invalid characters with underscores");
			log.info("INFO: Replace the invalid characters with underscores by clicking on the 'Replace invalid characters with '_'?' link");
			ui.clickLinkWait(WikisUIConstants.replaceInvalidCharacters);
			
			//save and close
			logger.strongStep("Click on the 'Save and Close' button");
			log.info("INFO: Save and close the wiki page.");
			driver.getSingleElement(WikisUIConstants.saveAndClose).click();
			
			logger.strongStep("Validate 'The page was created.' text is displayed");
			log.info("INFO: Validate the peer page was created");
			Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.pageCreated),
					"ERROR: Peer page was not created");
			
		}

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Index page to sort the entries in a wiki by name, creation date and last update date.
	*<li><B>Step: </B>Create a communtiy via API.
	*<li><B>Step: </B>Add a wiki widget to the community using API.
	*<li><B>Step: </B>Login as the owner of the community and open it.
	*<li><B>Step: </B>Open the wiki created using API.
	*<li><B>Step: </B>Creating two peer pages in the wiki.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Verify: </B>The Updated button which sorts the the entries on the page from least recently updated to most recently updated is selected by default.
	*<li><B>Step: </B>Click on the Created button to sort the entries from most recently created to least recently created.
	*<li><B>Verify: </B>The second peer page appears as the first entry.
	*<li><B>Step: </B>Click on the Name button to sort the entries by name in alphabetical order.
	*<li><B>Verify: </B>The first peer page appears as the first entry.
	*<li><B>Step: </B>Click on the link for the first peer page.
	*<li><B>Step: </B>Click on the Edit button and change the name, tags and description for the page.
	*<li><B>Verify: </B>The name and the description of the page have been updated.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Verify: </B>The first peer page appears as the first entry on the Index page and has the new name.
	*<li><B>Verify: </B>The Updated column reflects that the first peer page was updated today.
	*</ul>
	*/
	@Test(groups = { "regression" })
	public void SortByNameAndUpdate() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder("A_" + testName + Helper.genDateBasedRand(), PageType.Peer)
									.description("this is a test description for creating a Peer wiki page A")
									.build();
		
		BaseWikiPage newwikiPageA = new BaseWikiPage.Builder("Update_" + wikiPageA.getName(), PageType.Peer)
												.description("updated with new content")
												.build();
		
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder("B_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description("this is a test description for creating a Peer wiki page B")
												.build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		/*//add widget
		logger.strongStep("Use API to add a wiki widget to the community");
		log.info("INFO: Add a wiki widget to the community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);*/
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiComOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);
		
		//create a new wikipage
		logger.strongStep("Create two peer pages in the wiki");
		log.info("INFO: Creating two peer pages inside wiki");
		wikiPageA.create(ui);		
		wikiPageB.create(ui);
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait("link=Index");
		
		logger.strongStep("Verify the Updated button which sorts the the entries on the page from least recently updated to most recently updated is selected by default");
		log.info("INFO: Validate that the Updated button is selected by default");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.updateLink).getAttribute("class").equals("lotusDescending lotusActiveSort"));
		
		logger.strongStep("Click on the Created button to sort the entries from most recently created to least recently created");
		log.info("INFO: Click on the Created button");
		ui.clickLinkWait("link=Created");
		
		logger.strongStep("Verify the peer page: " + wikiPageB.getName() + " appears as the first entry");
		log.info("INFO: Validate the peer page: " + wikiPageB.getName() + " appears as the first entry on the Index page");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals(wikiPageB.getName()));
			
		logger.strongStep("Click on the Name button to sort the entries by name in alphabetical order");
		log.info("INFO: Sort the entries by name in alphabetical order by clicking on the Name button");
		ui.clickLinkWait("link=Name");
		
		logger.strongStep("Verify the peer page: " + wikiPageA.getName() + " appears as the first entry");
		log.info("INFO: Validate the peer page: " + wikiPageA.getName() + " appears as the first entry on the Index page");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals(wikiPageA.getName()));
		
		logger.strongStep("Click on the link for the peer page: " + wikiPageA.getName());
		log.info("INFO: Select the peer page: " + wikiPageA.getName());
		driver.getFirstElement("link=" + wikiPageA.getName()).click();
		
		logger.strongStep("Click on the Edit button and change the name, tags and description for the page");
		log.info("INFO: Change the name, tags and description for the page after clicking on the Edit button");
		ui.editWikiPage(newwikiPageA);
		
		//Now verify that the page has being saved successfully
		logger.strongStep("Verify that the name of the page has been updated");
		log.info("INFO: Validate that the page name has being changed");
		driver.isTextPresent(newwikiPageA.getName());
		
		logger.strongStep("Verify that the description of the page has been updated");
		log.info("INFO: Validate that the page description has being changed");
		driver.isTextPresent(newwikiPageA.getDescription());
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait("link=Index");
		
		logger.strongStep("Verify that the peer page: " + wikiPageA.getName() + " appears as the first entry on the Index page and has the new name");
		log.info("INFO: Verify that the peer page: " + wikiPageA.getName() + " appears as the first entry on the Index page and has the new name");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals(newwikiPageA.getName()));
		
		logger.strongStep("Verify that the Updated column reflects that the peer page: " + wikiPageA.getName() + " was updated today");
		log.info("INFO: Verify that the Updated column reflects that the peer page: " + wikiPageA.getName() + " was updated today");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntryUpdateCell).getText().contains("Today at"));
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the 'Edited By...' link in the left navigation menu by using it for some users and a text.
	*<li><B>Step: </B>Create a communtiy via API.
	*<li><B>Step: </B>Add a wiki widget to the community using API.
	*<li><B>Step: </B>Login as the owner of the community and open it.
	*<li><B>Step: </B>Open the wiki created using API.
	*<li><B>Step: </B>Create a peer page in the wiki and close the session.
	*<li><B>Step: </B>Login as the member of the community and open it.
	*<li><B>Step: </B>Open the wiki once again.
	*<li><B>Step: </B>Create a child page in the wiki.
	*<li><B>Step: </B>Click on the Index link to go to the Index page.
	*<li><B>Verify: </B>The number of entries on the Index page is three.
	*<li><B>Step: </B>Search for the 'I Edited' link in the left navigation menu, if not found expand the Pages section.
	*<li><B>Step: </B>Click on the 'Edited by...' link in the left navigation menu.
	*<li><B>Step: </B>Type the name of the community member in the text box and click on the Search button.
	*<li><B>Verify: </B>The page reads that the page was edited by one user.
	*<li><B>Verify: </B>The 'I Edited' link and the child page's link are visible.
	*<li><B>Step: </B>Select the 'Edited by...' link from the left navigation menu.
	*<li><B>Step: </B>Type the name of the community owner in the text box.
	*<li><B>Verify: </B>The typeahead widget appears.
	*<li><B>Step: </B>Select the owner from the typeahead widget.
	*<li><B>Verify: </B>The texts 'Edited by ' (followed by the owner's name) and '1-2 of 2' are displayed.
	*<li><B>Verify: </B>The link for the peer page is visible.
	*<li><B>Step: </B>Click on the 'Edited by...' link in the left navigation menu and type a text in the text box.
	*<li><B>Step: </B>Click on the Search button.
 	*<li><B>Verify: </B>The Alert popup appears which reads 'No user was found. Try typing the name and selecting a user from the list provided.'.
	*</ul>
	*/
	@Test(groups = { "regression", "onprem_only" })
	public void EditedBy() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User comMember = cfg.getUserAllocator().getUser();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 .access(defaultAccess)
									 .build();

		BaseWikiPage ownerwikiPage = new BaseWikiPage.Builder(testName + "_Owner", PageType.Peer)
												.description("this is a test description for creating a wiki page")
												.build();
		
		BaseWikiPage memberwikiPage = new BaseWikiPage.Builder(testName + "_MemberA", PageType.Child)
												.description("this is a test description for creating a wiki page A")
												.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		/*//add widget
		logger.strongStep("Use API to add a wiki widget to the community");
		log.info("INFO: Add a wiki widget to the community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);*/
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiComOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);
		
		//create a new wikipage
		logger.strongStep("Create a peer page in the wiki and close the session");
		log.info("INFO: Creating a peer page inside wiki and closing the session");
		ownerwikiPage.create(ui);	
		ui.close(cfg);
		
		logger.strongStep("Open Communities component and login as member: " + comMember.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(comMember);
		
		logger.strongStep("Select the 'I'm a Member' view and then click on the Community Card for the community: " + community.getName());
		log.info("INFO: Choose the 'I'm a Member' view and then click on the Community Card for the community: " + community.getName());
		Community_View_Menu.IM_A_MEMBER.select(cUI);
		ui.clickLinkWait(CommunitiesUIConstants.communityNameLink.replace("PLACEHOLDER", community.getName()));

		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);
		
		logger.strongStep("Create a child page in the wiki");
		log.info("INFO: Creating a child page inside wiki");
		memberwikiPage.create(ui);	
		
		logger.strongStep("Click on the Index link to go to the Index page");
		log.info("INFO: Navigate to the Index page by clicking on the Index link");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		logger.strongStep("Verify that the Index page contains the text: '1-3 of 3'");
		log.info("INFO: Verify that the text: '1-3 of 3' is displayed on the Index page");
		Assert.assertTrue(driver.isTextPresent("1-3 of 3"), "ERROR: Text [1-3 of 3] is not present");

		logger.strongStep("Look for the 'I Edited' link in the left navigation menu, if not found expand the Pages section");
		log.info("INFO: Search for the 'I Edited' link in the left navigation menu, if not found expand the Pages section");
		Boolean bool = driver.isElementPresent(WikisUIConstants.IEditedLink);
		if(!bool) driver.getSingleElement(WikisUIConstants.PagesSection).click();
		
		logger.strongStep("Click on the 'Edited by...' link in the left navigation menu");
		log.info("INFO: Select the 'Edited by...' link from the left navigation menu");
		ui.fluentWaitElementVisible(WikisUIConstants.EditedByLink);
		ui.clickLinkWithJavascript(WikisUIConstants.EditedByLink);
		
		logger.strongStep("Type the name of the community member: " + comMember.getDisplayName() + " in the text box and click on the Search button");
		log.info("INFO: Enter the name of the community member: " + comMember.getDisplayName() + " in the text box and click on the Search button");
		driver.getSingleElement(WikisUIConstants.EditedByPersonInput).typeWithDelay(comMember.getDisplayName());
		driver.getVisibleElements(WikisUIConstants.PageSectionSearchBtn).get(0).click();
		
		logger.strongStep("Verify the texts 'I Edited' and '1-1 of 1' are there on the page");
		log.info("INFO: Verify the texts 'I Edited' and '1-1 of 1' are displayed on the page");
		ui.fluentWaitTextPresent("I Edited");
		Assert.assertTrue(driver.isTextPresent("1-1 of 1"), "ERROR: Text [1-1 of 1] is not present");
		
		logger.strongStep("Verify that the link to the child page " + memberwikiPage.getName() + " is present");
		log.info("INFO: Validate that the link to the child page " + memberwikiPage.getName() + " is present");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(memberwikiPage)),
				"ERROR: The link to the wiki page " + memberwikiPage.getName() + " is not present.");
		
		logger.strongStep("Click on the 'Edited by...' link in the left navigation menu");
		log.info("INFO: Select the 'Edited by...' link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.EditedByLink);
		
		logger.strongStep("Type the name of the community owner: " + testUser1.getDisplayName() + " in the text box and verify the typeahead widget appears");
		log.info("INFO: Enter the name of the community owner: " + testUser1.getDisplayName() + " in the text box and verify the typeahead widget appears");
		driver.getSingleElement(WikisUIConstants.EditedByPersonInput).typeWithDelay(testUser1.getDisplayName());
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.EditedByPersonTypeahead),
				"ERROR: The selector {" + WikisUIConstants.EditedByPersonTypeahead + "} matches no element.");
		
		logger.strongStep("Select " + testUser1.getDisplayName() + " from the typeahead widget");
		log.info("INFO: Click on " + testUser1.getDisplayName() + "'s name in the typeahead widget");
		driver.getVisibleElements(WikisUIConstants.EditedByPersonTypeaheadLink).get(0).click();
		
		logger.strongStep("Verify the texts 'Edited by " + testUser1.getDisplayName() + "' and '1-2 of 2' are displayed");
		log.info("INFO: Validate that the texts 'Edited by " + testUser1.getDisplayName() + "' and '1-2 of 2' are displayed");
		Assert.assertTrue(driver.isTextPresent("Edited by " + testUser1.getDisplayName()),
				"ERROR: The text " + "Edited by " + testUser1.getDisplayName() + " is not present.");
		Assert.assertTrue(driver.isTextPresent("1-2 of 2"),
				"ERROR: The text " + "1-2 of 2" + " is not present.");
		
		logger.strongStep("Verify the link for the page: " + ownerwikiPage.getName() + " is visible");
		log.info("INFO: Verify the link for the page: " + ownerwikiPage.getName() + " is visible");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(ownerwikiPage)),
				"ERROR: The selector {" + ui.getPageSelectorinListView(ownerwikiPage) + "} matches no element.");
				
		logger.strongStep("Click on the 'Edited by...' link in the left navigation menu and type the text 'test' in the text box");
		log.info("INFO: Select the 'Edited by...' link from the left navigation menu and enter the text 'test' in the text box");
		ui.clickLinkWait(WikisUIConstants.EditedByLink);
		driver.getSingleElement(WikisUIConstants.EditedByPersonInput).type("test");
		
		logger.strongStep("Click on the Search button and verify the Alert popup appears which reads: " + Data.getData().EditedByUserNotExist);
		log.info("INFO: Hit the Search button and verify the Alert popup appears which reads: " + Data.getData().EditedByUserNotExist);
		driver.getVisibleElements(WikisUIConstants.PageSectionSearchBtn).get(0).click();
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditedByUserNotExist),
				"ERROR: The text " + Data.getData().EditedByUserNotExist + " is not present.");
		
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Index view to update a wiki page.
	*<li><B>Step: </B>Create a communtiy via API.
	*<li><B>Step: </B>Add a wiki widget to the community using API.
	*<li><B>Step: </B>Login as the owner of the community and open it.
	*<li><B>Step: </B>Open the wiki created using API.
	*<li><B>Step: </B>Create a peer page in the wiki.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Step: </B>Click on the More link for the peer page.
	*<li><B>Step: </B>Like the peer page by clicking on the Like button.
	*<li><B>Step: </B>Click on the Add tags link and add a tag to the page.
	*<li><B>Step: </B>Click on the peer page's link to navigate to it.
	*<li><B>Verify: </B>The page contains the text 'You like this'.
	*<li><B>Verify: </B>The tag added before appears on the page.
	*</ul>
	*/
	@Test(groups = { "regression" })
	public void UpdatePageinIndexView() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder("A_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description("this is a test description for creating a Peer wiki page A")
												.build();

		String tags = "wikipage_tag";
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		/*//add widget
		logger.strongStep("Use API to add a wiki widget to the community");
		log.info("INFO: Add a wiki widget to the community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);*/
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiComOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);
		
		//create a new wikipage
		logger.strongStep("Create a peer page in the wiki");
		log.info("INFO: Creating a peer page inside wiki");
		wikiPageA.create(ui);	
		
		logger.strongStep("Click on the Index link in the left navigation menu followed by clicking on the More link for the peer page");
		log.info("INFO: Select the Index link from the left navigation menu, then click on the More link for the peer page");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		driver.getFirstElement(WikisUIConstants.moreLink).click();
		
		//Now check sorting by Page Views
		logger.strongStep("Hit the Like button and like the peer page");
		log.info("INFO: Like the peer page by clicking on the Like button");
		ui.clickLinkWait("link=Like");
		
		logger.strongStep("Click on the Add tags link and add the tag: " + tags + " to the page");
		log.info("INFO: Click on the Add tags link, enter the tag: " + tags + " and click on the OK button");
		ui.clickLinkWait(WikisUIConstants.wikiPageAddTagLink);
		driver.getSingleElement(WikisUIConstants.wikiPageAddTagText).type(tags);
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		
		logger.strongStep("Click on the peer page's link to navigate to it");
		log.info("INFO: Open the peer page by clicking on its link");
		driver.getFirstElement("link=" + wikiPageA.getName()).click();
		
		logger.strongStep("Verify the page contains the text: " + Data.getData().Expected_Like_Text);
		log.info("INFO: Validate that the page contains the text: " + Data.getData().Expected_Like_Text);
		Assert.assertTrue(driver.isTextPresent(Data.getData().Expected_Like_Text),
				"ERROR: The text " + Data.getData().Expected_Like_Text + " is not present.");
		
		logger.strongStep("Verify the tag added before appears on the page");
		log.info("INFO: Verify the tag added before appears on the page");
		String tagSelector = "css=ul[class='qkrInlineList'] a:contains('" + tags + "')";
		Assert.assertTrue(driver.isElementPresent(tagSelector),
				"ERROR: The selector {" + tagSelector + "} matches no element.");
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the usage of the Index page to sort the entries in a wiki by page views and size.
	*<li><B>Step: </B>Create a communtiy via API.
	*<li><B>Step: </B>Add a wiki widget to the community using API.
	*<li><B>Step: </B>Login as the owner of the community and open it.
	*<li><B>Step: </B>Open the wiki created using API.
	*<li><B>Step: </B>Create two peer pages in the wiki.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Verify: </B>The second peer page appears as the first entry on the Index page.
	*<li><B>Step: </B>Click on the link for the first peer page to open it.
	*<li><B>Step: </B>Edit the peer page and change its name and delete the description.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Step: </B>Click on the Page View button to sort the entries from most visited to least visited.
	*<li><B>Verify: </B>The first peer page appears as the first entry with the updated name.
	*<li><B>Verify: </B>The first peer page has 3 views.
	*<li><B>Step: </B>Click on the Size button to sort the entries by size from largest to smallest.
	*<li><B>Verify: </B>The wiki's link appears as the first entry.
	*</ul>
	*/
	@Test(groups = { "regression" })
	public void SortByPageViewsandSize() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder("A_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description("this is a test description for creating a Peer wiki page A")
												.build();
		
		BaseWikiPage newwikiPageA = new BaseWikiPage.Builder("A_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description(" ")
												.build();
				
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder("B_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description("this is a test description for creating a Peer wiki page B")
												.build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		/*//add widget
		logger.strongStep("Use API to add a wiki widget to the community");
		log.info("INFO: Add a wiki widget to the community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);*/
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiComOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		
		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);
		
		//create two new wikipages
		logger.strongStep("Create two peer pages in the wiki");
		log.info("INFO: Creating two peer pages inside wiki");
		wikiPageA.create(ui);		
		wikiPageB.create(ui);
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		logger.strongStep("Verify '" + wikiPageB.getName() + "' appears as the first entry on the Index page");
		log.info("INFO: Validate that the peer page '" + wikiPageB.getName() + "' appears as the first entry on the Index page");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals(wikiPageB.getName()));
				
		logger.strongStep("Click on the link for the peer page: " + wikiPageA.getName());
		log.info("INFO: Open the peer page: " + wikiPageA.getName());
		driver.getFirstElement("link=" + wikiPageA.getName()).click();
		
		logger.strongStep("Edit the peer page and change its name and delete the description");
		log.info("INFO: Click on the Edit button to edit the page and change its name and delete the description");
		ui.editWikiPage(newwikiPageA);
		//Now check sorting by Page Views	
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		logger.strongStep("Click on the Page View button to sort the entries from most visited to least visited");
		log.info("INFO: Hit the Page View button to sort the entries from most visited to least visited");
		ui.clickLinkWait(WikisUIConstants.PageViewLink);
		
		logger.strongStep("Verify the page: " + newwikiPageA.getName() + " appears as the first entry");
		log.info("INFO: Verify the page: " + newwikiPageA.getName() + " appears as the first entry");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals(newwikiPageA.getName()));
		
		logger.strongStep("Verify the number of views for the page: " + newwikiPageA.getName() + " is 3");
		log.info("INFO: Verify the page: " + newwikiPageA.getName() + " has 3 views");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntryPageCell).getText().equals("3"));
		
		logger.strongStep("Click on the Size button to sort the entries by size from largest to smallest");
		log.info("INFO: Hit the Size button to sort the entries by size from largest to smallest");
		ui.clickLinkWait(WikisUIConstants.SizeLink);
		
		logger.strongStep("Verify the link for the wiki: Welcome to " + community.getName() + " appears as the first entry");
		log.info("INFO: Validate that the wiki: Welcome to " + community.getName() + " appears as the first entry");
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.firstWikiEntry).getText().equals("Welcome to " + community.getName()));
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the 'Go to Page...' link in the left navigation menu by using it to search for both existing and inexistent pages.
	*<li><B>Step: </B>Create a communtiy via API.
	*<li><B>Step: </B>Add a wiki widget to the community using API.
	*<li><B>Step: </B>Open Communities component and login.
	*<li><B>Step: </B>Select the 'I'm an Owner' view.
	*<li><B>Step: </B>Open the community created via API and then open the wiki.
	*<li><B>Step: </B>Create a peer page in the wiki.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Step: </B>Search for the 'Go to Page...' link in the left navigation menu, if not found expand the Pages section.
	*<li><B>Step: </B>Click on the 'Go to Page...' link in the left navigation menu.
	*<li><B>Step: </B>Enter partial name of the peer page in the text box.
	*<li><B>Verify: </B>The typeahead appears and contains the name of the peer page.
	*<li><B>Step: </B>Select the peer page from the typeahead.
	*<li><B>Verify: </B>The peer page opens by validating its name and the description.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Step: </B>Click on the 'Go to Page...' link in the left navigation menu.
	*<li><B>Step: </B>Enter the name of an inexistent page in the text box and click on the Search button.
	*<li><B>Verify: </B>The error message reading the page has not been created yet is displayed.
	*<li><B>Verify: </B>The 'Get Me Out of Here' button exists.
	*<li><B>Step: </B>Click on the 'Create This Page' button.
	*<li><B>Step: </B>Enter only the description for the page and click on the Save and Close button.
	*<li><B>Verify: </B>The 'The page was created.' message appears.
	*<li><B>Step: </B>Click on the Index link in the left navigation menu.
	*<li><B>Step: </B>Click on the 'Go to Page...' link in the left navigation menu.
	*<li><B>Step: </B>Enter the name of the page just created in the text box and click on the Search button.
	*<li><B>Verify: </B>The page opens by validating its name and the description.
	*<li><B>Step: </B>Delete the community that was created using API.
	*</ul>
	*/
	@Test(groups = { "regression", "regressioncloud"})
	public void GotoPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage peerwikiPage = new BaseWikiPage.Builder(testName + "_Peer", PageType.Peer)
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		BaseWikiPage childwikiPage = new BaseWikiPage.Builder(testName + "_Child", PageType.Child)
												.description("this is a test description for creating a Child wiki page")
												.build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			logger.strongStep("Use API to add a wiki widget to the community");
			log.info("INFO: Add a wiki widget to the community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		logger.strongStep("Select the 'I'm an Owner' view under Filter By");
		log.info("INFO: Choose the 'I'm an Owner' view from Filter By");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);
		
		logger.strongStep("Click on Wiki link in the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//create a new wikipage
		logger.strongStep("Create a peer page in the wiki");
		log.info("INFO: Creating a peer page inside wiki");
		peerwikiPage.create(ui);
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		logger.strongStep("Look for the 'Go to Page...' link in the left navigation menu, if not found expand the Pages section");
		log.info("INFO: Search for the 'Go to Page...' link in the left navigation menu, if not found expand the Pages section");
		Boolean bool = driver.isElementPresent(WikisUIConstants.GotoPageLink);
		if(!bool) driver.getSingleElement(WikisUIConstants.PagesSection).click();
		
		logger.strongStep("Click on the 'Go to Page...' link in the left navigation menu");
		log.info("INFO: Select the 'Go to Page...' link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.GotoPageLink);
		
		logger.strongStep("Enter '" + testName + "' in the text box and verify the typeahead appears");
		log.info("INFO: Type '" + testName + "' in the text box and verify the typeahead appears");
		driver.getSingleElement(WikisUIConstants.PageSectionInput).typeWithDelay(testName);
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.PageSectionTypeahead));
		int i=0;
		for(; i<driver.getElements(WikisUIConstants.PageSectionTypeaheadLink).size(); i++){
			if(driver.getElements(WikisUIConstants.PageSectionTypeaheadLink).get(i).getText().equals(peerwikiPage.getName())) break;
		}
		
		logger.strongStep("Verify '" + peerwikiPage.getName() + "' appears in the typeahead");
		log.info("INFO: Verify '" + peerwikiPage.getName() + "' appears in the typeahead");
		Assert.assertTrue(i<driver.getElements(WikisUIConstants.PageSectionTypeaheadLink).size());
		
		logger.strongStep("Select '" + peerwikiPage.getName() + "' from the typeahead");
		log.info("INFO: Click on '" + peerwikiPage.getName() + "' in the typeahead");
		driver.getElements(WikisUIConstants.PageSectionTypeaheadLink).get(i).click();
		
		logger.strongStep("Verify the page '" + peerwikiPage.getName() + "' opens by looking at its name and the description");
		log.info("INFO: Validate that the selected page '" + peerwikiPage.getName() + "' displays");
		Assert.assertTrue(driver.isTextPresent(peerwikiPage.getName()));
		Assert.assertTrue(driver.isTextPresent(peerwikiPage.getDescription()));
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.IndexLink);

		logger.strongStep("Click on the 'Go to Page...' link in the left navigation menu");
		log.info("INFO: Select the 'Go to Page...' link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.GotoPageLink);
		
		logger.strongStep("Enter '" + childwikiPage.getName() + "' in the text box and click on the Search button");
		log.info("INFO: Type '" + childwikiPage.getName() + "' in the text box and click on the Search button");
		driver.getSingleElement(WikisUIConstants.PageSectionInput).typeWithDelay(childwikiPage.getName());
		driver.getVisibleElements(WikisUIConstants.PageSectionSearchBtn).get(0).click();
		
		logger.strongStep("Verify the error message reading the page has not been created yet is displayed");
		log.info("INFO: Validate that the error message saying the page has not been created yet is displayed");
		Assert.assertTrue(driver.isTextPresent(childwikiPage.getName()));
		Assert.assertTrue(driver.isTextPresent(Data.getData().NotCreatedMsg));
		
		logger.strongStep("Verify the 'Get Me Out of Here' button exists, then click on the 'Create This Page' button");
		log.info("INFO: Validate that the 'Get Me Out of Here' button exists, then click on the 'Create This Page' button");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.NoThanksBtn));
		driver.getSingleElement(WikisUIConstants.CreateThisPageBtn).click();
		
		logger.strongStep("Enter the description: " + childwikiPage.getDescription() + " for the page");
		log.info("INFO: Input: " + childwikiPage.getDescription() + " in the CKEditor");
		ui.typeInCkEditor(childwikiPage.getDescription());
		//save and close
		logger.strongStep("Click on the Save and Close button");
		log.info("INFO: Save the wiki page by clicking on the Save and Close button");
		driver.getSingleElement(WikisUIConstants.Save_and_Close_Link).click();
		
		logger.strongStep("Validate that the 'The page was created.' message appears in " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Wait for 'The page was created.' message");
		ui.fluentWaitTextPresent("The page was created.");	
		
		logger.strongStep("Click on the Index link in the left navigation menu");
		log.info("INFO: Select the Index link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		logger.strongStep("Click on the 'Go to Page...' link in the left navigation menu");
		log.info("INFO: Select the 'Go to Page...' link from the left navigation menu");
		ui.clickLinkWait(WikisUIConstants.GotoPageLink);
		
		//Searches for the wiki in the searchbox under index
		logger.strongStep("Enter '" + childwikiPage.getName() + "' in the text box and click on the Search button");
		log.info("INFO: Type '" + childwikiPage.getName() + "' in the text box and click on the Search button");
		driver.getSingleElement(WikisUIConstants.PageSectionInput).click();
		driver.getSingleElement(WikisUIConstants.PageSectionInput).typeWithDelay(childwikiPage.getName());
		driver.getVisibleElements(WikisUIConstants.PageSectionSearchBtn).get(0).click();
		
		logger.strongStep("Verify the page '" + childwikiPage.getName() + "' opens by looking at its name and the description");
		log.info("INFO: Validate that the searched page '" + childwikiPage.getName() + "' displays");
		Assert.assertTrue(driver.isTextPresent(childwikiPage.getName()), childwikiPage.getName() + " could not be found:");
		Assert.assertTrue(driver.isTextPresent(childwikiPage.getDescription()), childwikiPage.getDescription() + " could not be found:");

		logger.strongStep("Delete the community that was created using API");
		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
}
