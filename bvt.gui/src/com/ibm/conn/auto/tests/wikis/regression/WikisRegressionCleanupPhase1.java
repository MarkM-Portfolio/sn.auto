package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class WikisRegressionCleanupPhase1 extends  SetUpMethods2{

	/*
	 * Phase 1 of regression test cleanup work
	 * Passing tests from the current Wikis regression suite have been copied into this file.
	 * As failing regression tests get fixed, they will be moved into this file.
	 * This file will become the new regression suite - to be implemented.
	 * 
	 * NOTE: These test methods may also need some additional cleanup work
	 * ie: remove code comments and replace with info.log, add cleanup/delete entry steps, cleanup css & create
	 * new selectors in common repository etc...
	 */	
	
	private static Logger log = LoggerFactory.getLogger(WikisRegressionCleanupPhase1.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3, testUser4;
	private List<Member> members;
	private APIWikisHandler apiOwner;
	private String serverURL;
	private BaseWiki wiki;
	private BaseWikiPage page1, page2, page3, page4, page5;
	private BaseWikiPage childPage1, childPage2, childPage3, childPage4, childPage5;
	private List<BaseWikiPage> pages = new ArrayList<BaseWikiPage>();
	private boolean isOnPremise;
	
	@BeforeClass(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser(); 
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		CommunitiesUI.getDefaultAccess(cfg.getProductName());

		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}		

	    if (isOnPremise) {
	    	apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());	
	    	new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
	    	//create member list
	    	members = new ArrayList<Member>();	
	    	members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
	    	members.add(new Member(WikiRole.OWNER, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
	    	members.add(new Member(WikiRole.OWNER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));
		
	    	//Generic pages
	    	wiki = new BaseWiki.Builder("LikeWiki" + Helper.genDateBasedRand())
						   	   .editAccess(EditAccess.EditorsAndOwners)
						   	   .readAccess(ReadAccess.WikiOnly)
						   	   .addMembers(members)
						   	   .tags("tag" + Helper.genDateBasedRand())
						   	   .description("Description for test " + "LikeWiki")
						   	   .build();
		
		
	    	page1 = new BaseWikiPage.Builder("Page_1" + Helper.genDateBasedRand(), PageType.NavPage)
									.tags("tagforliketest")
									.description("this is a test description for creating a Peer wiki page")
									.build();

	    	page2 = new BaseWikiPage.Builder("Page_2" + Helper.genDateBasedRand(), PageType.NavPage)
		   							.tags("tagforliketest")
		   							.description("this is a test description for creating a Peer wiki page")
		   							.build();
		
	    	page3 = new BaseWikiPage.Builder("Page_3" + Helper.genDateBasedRand(), PageType.NavPage)
		   							.tags("tagforliketest")
		   							.description("this is a test description for creating a Peer wiki page")
		   							.build();
		
	    	page4 = new BaseWikiPage.Builder("Page_4" + Helper.genDateBasedRand(), PageType.NavPage)
		   							.tags("tagforliketest")
		   							.description("this is a test description for creating a Peer wiki page")
		   							.build();
		
	    	page5 = new BaseWikiPage.Builder("Page_5" + Helper.genDateBasedRand(), PageType.NavPage)
		   							.tags("tagforliketest")
		   							.description("this is a test description for creating a Peer wiki page")
		   							.build();

	    	childPage1 = new BaseWikiPage.Builder("Child_Page_1" + Helper.genDateBasedRand(), PageType.Child)
		 								 .tags("Child1_" + Helper.genDateBasedRand())
		 								 .description("this is a test description for creating a Child wiki page")
		 								 .build();

	    	childPage2 = new BaseWikiPage.Builder("Child_Page_2" + Helper.genDateBasedRand(), PageType.Child)
										 .tags("Child2_" + Helper.genDateBasedRand())
										 .description("this is a test description for creating a Child wiki page")
										 .build();
		
	    	childPage3 = new BaseWikiPage.Builder("Child_Page_3" + Helper.genDateBasedRand(), PageType.Child)
									 	 .tags("Child3_" + Helper.genDateBasedRand())
									 	 .description("this is a test description for creating a Child wiki page")
									 	 .build();
		
	    	childPage4 = new BaseWikiPage.Builder("Child_Page_4" + Helper.genDateBasedRand(), PageType.Child)
									 	 .tags("Child_4" + Helper.genDateBasedRand())
									 	 .description("this is a test description for creating a Child wiki page")
									 	 .build();
		
	    	childPage5 = new BaseWikiPage.Builder("Child_Page_5" + Helper.genDateBasedRand(), PageType.Child)
									 	 .tags("Child_5" + Helper.genDateBasedRand())
									 	 .description("this is a test description for creating a Child wiki page")
									 	 .build();

	    	new BaseWikiPage.Builder("Via_Nav" + Helper.genDateBasedRand(), PageType.NavPage)
		  						   	   .tags("tag1, tag2")
		  						   	   .description("this is a test description for creating a Peer wiki page")
		  						   	   .build();

	    	new BaseWikiPage.Builder("Via_Nav" + Helper.genDateBasedRand(), PageType.NavPage)
		  							  	  .tags("tag1, tag2")
		  							  	  .description("this is a test description for creating a Peer wiki page")
		  							  	  .build();

	    	new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
								   	   .tags("tag1, tag2")
								   	   .description("this is a test description for creating a Peer wiki page")
								   	   .build();

	    	new BaseWikiPage.Builder("Child_Wiki_" + Helper.genDateBasedRand(), PageType.Child)
								    	.tags("tag1, tag2")
								    	.description("this is a test description for creating a Child wiki page")
								    	.build();
		
	    	pages.add(page1);
	    	pages.add(page2);
	    	pages.add(page3);
	    	pages.add(page4);
	    	pages.add(page5);	
	    	pages.add(childPage1);		
	    	pages.add(childPage2);
	    	pages.add(childPage3);
	    	pages.add(childPage4);
	    	pages.add(childPage5);
		
	    	log.info("INFO: wikiName" + wiki.getName());
	    	Wiki apiWiki = wiki.createAPI(apiOwner);
	    	page1.createAPI(apiOwner, apiWiki);
	    	page2.createAPI(apiOwner, apiWiki);
	    	page3.createAPI(apiOwner, apiWiki);
	    	page4.createAPI(apiOwner, apiWiki);
	    	page5.createAPI(apiOwner, apiWiki);

	    	//Load component and login
	    	ui.loadComponent(Data.getData().ComponentWikis);
	    	ui.login(testUser1);
		
	    	//Open Wiki created above
	    	log.info("INFO: Open Wiki created via API");
	    	ui.clickLinkWait(WikisUI.getWiki(wiki));
		
	    	log.info("INFO: Add child pages");
	    	addChildPage(page1, childPage1);
	    	addChildPage(page2, childPage2);
	    	addChildPage(page3, childPage3);
	    	addChildPage(page4, childPage4);
	    	addChildPage(page5, childPage5);
	
	    	//close browser
	    	ui.logout();
	    	ui.close(cfg);
	    }	//End of if
	}

	//String for checking if the user is following the page
	Boolean UserFollowingPageYes = true;
	Boolean UserFollowingPageNo = false;
	String StartFollowingWiki = "Start";
	String StopFollowingWiki = "Stop";	

	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify a wiki is not listed in 'I'm following' view after stop following a wiki.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis .</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Verify: </B>Wiki is created.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>	
	*<li><B>Step: </B>Select the 'I'm Following' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Select 'Stop Following this Wiki' link.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void StopFollowingAWiki()throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();


		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);

		//Load Wikis component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		logger.strongStep("Verify the wiki has been created");
		log.info("INFO: Verify the wiki has been created");
		verifyNewHomePageUI(wiki);
		
		//Navigate to the Wikis view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//view the wiki
		logger.strongStep("Select the 'I'm Following' view from the left navigation menu");
		log.info("INFO: Select the 'I'm Following' view from the left navigation menu");
		Wiki_LeftNav_Menu.FOLLOWING.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Stop following this wiki
		logger.strongStep("Select 'Stop Following this Wiki' link");
		log.info("INFO: Select 'Stop Following this Wiki' link");
		ui.followWikiAction(StopFollowingWiki);
		
		//Return to the Wikis view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");		
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that a wiki is not visible to other users in the 'I'm Following' view.</li>
	*<li><B>Step: </B>Load component and login to wikis as a 1st user.</li>
	*<li><B>Step: </B>Create a Wiki.</li>
	*<li><B>Verify: </B>Wiki is created.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>	
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is listed in the I'm following view.</li>
	*<li><B>Step: </B>Logout of wiki and login to wikis as 2nd user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*<li><B>Step: </B>Logout of wiki and login to wikis as 3rd user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*<li><B>Step: </B>Logout of wiki and login to wikis as 4th user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void PublicWikiAllUsersAreNotFollowing()throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.readAccess(ReadAccess.All)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		//Load component and login to wikis as first user
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as first user");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a Wiki
		logger.strongStep("Create wiki: " + wiki.getName());
		log.info("INFO: Create wiki: " + wiki.getName());
		wiki.create(ui);
		
		//Verify the wiki is created
		logger.strongStep("Verify the wiki has been created");
		log.info("INFO: Verify the wiki has been created");
		verifyNewHomePageUI(wiki);
		
		//Navigate to the wikis homepage view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of wiki
		logger.strongStep("Logout of wiki as first user");
		log.info("INFO: Logout of wiki as first user");
		ui.logout();
		
		//Load component and login to wikis as second user
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as second user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//Logout of wiki
		logger.strongStep("Logout of wiki as second user");
		log.info("INFO: Logout of wiki as second user");
		ui.logout();
		
		//Load component and login to wikis as third user
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as third user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//logout of wiki
		logger.strongStep("Logout of wiki as third user");
		log.info("INFO: Logout of wiki as third user");
		ui.logout();
		
		//Load component and login to wikis as fourth user
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as fourth user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that all users are following a wiki and listed in the I'm following view.</li>
	*<li><B>Step: </B>Load component and login to wikis as a 1st user.</li>
	*<li><B>Step: </B>Create a Wiki.</li>
	*<li><B>Verify: </B>Wiki is created.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>	
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is listed in the I'm following view.</li>
	*<li><B>Step: </B>Logout of wiki and login to wikis as 2nd user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*<li><B>Step: </B>Select the 'Public Wikis' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the first Wiki page from the list.</li>
	*<li><B>Step: </B>Start following this wiki.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is listed in the I'm following view.</li>	
	*<li><B>Step: </B>Logout of wiki and login to wikis as 3rd user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>	
	*<li><B>Step: </B>Select the 'Public Wikis' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the first Wiki page from the list.</li>
	*<li><B>Step: </B>Start following this wiki.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is listed in the I'm following view.</li>	
	*<li><B>Step: </B>Logout of wiki and login to wikis as 4th user.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view.</li>
	*<li><B>Step: </B>Select the 'Public Wikis' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the first Wiki page from the list.</li>
	*<li><B>Step: </B>Start following this wiki.</li>
	*<li><B>Step: </B>Navigate to the Wikis homepage view.</li>
	*<li><B>Verify: </B>Click on the I'm following' view and Verify that the wiki is listed in the I'm following view.</li>		
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void PublicWikiAllUsersAreFollowing()throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.readAccess(ReadAccess.All)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		//Load component and login to wikis as first user
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as first user");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Create a Wiki
		logger.strongStep("Create wiki: " + wiki.getName());
		log.info("INFO: Create wiki: " + wiki.getName());
		wiki.create(ui);
		
		//Verify the wiki is created
		logger.strongStep("Verify the wiki has been created");
		log.info("INFO: Verify the wiki has been created");
		verifyNewHomePageUI(wiki);
		
		//Navigate to the Wikis homepage view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of wiki
		logger.strongStep("Logout of wiki as first user");
		log.info("INFO: Logout of wiki as first user");
		ui.logout();
		
		//Load component and login to wikis as second user
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as second user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);

		//view the wiki
		logger.strongStep("Select the 'Public Wikis' view from the left navigation menu");
		log.info("INFO: Select the 'Public Wikis' view from the left navigation menu");
		Wiki_LeftNav_Menu.PUBLICWIKIS.select(ui);
		
		//Open the wiki
		logger.strongStep("Open the first Wiki page from the list");
		log.info("INFO: Open the first Wiki page from the list");
		ui.clickLinkWait(WikisUI.getWiki(wiki) + ":nth(0)");
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
		
		//Navigate to the Wikis homepage view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of wiki
		logger.strongStep("Logout of wiki as second user");
		log.info("INFO: Logout of wiki as second user");
		ui.logout();
		
		//Load component and login to wikis as third user
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as third user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//view the wiki
		logger.strongStep("Select the 'Public Wikis' view from the left navigation menu");
		log.info("INFO: Select the 'Public Wikis' view from the left navigation menu");
		Wiki_LeftNav_Menu.PUBLICWIKIS.select(ui);
		
		//Open the wiki
		logger.strongStep("Open the first Wiki page from the list");
		log.info("INFO: Open the first Wiki page from the list");
		ui.clickLinkWait(WikisUI.getWiki(wiki) + ":nth(0)");
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
		
		//Navigate to the Wikis homepage view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//logout of wiki
		logger.strongStep("Logout of wiki as third user");
		log.info("INFO: Logout of wiki as third user");
		ui.logout();
		
		//Load component and login to wikis as fourth user
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as fourth user");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		
		//Click on the I'm following' view and Verify that the wiki is not listed in the I'm following view 
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//View the Wiki
		logger.strongStep("Select the 'Public Wikis' view from the left navigation menu");
		log.info("INFO: Select the 'Public Wikis' view from the left navigation menu");
		Wiki_LeftNav_Menu.PUBLICWIKIS.select(ui);
		
		//Open the wiki
		logger.strongStep("Open the first Wiki page from the list");
		log.info("INFO: Open the first Wiki page from the list");
		ui.clickLinkWait(WikisUI.getWiki(wiki) + ":nth(0)");
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
		
		//Return to the Wikis homepage view
		logger.strongStep("Navigate to the Wikis homepage view");
		log.info("INFO: Navigate to the Wikis homepage view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		ui.endTest();
	}	

	/**
	 * confirmWikiView -
	 * @param wiki
	 * @param view
	 */	 
	public void confirmWikiView(BaseWiki wiki, Wiki_LeftNav_Menu view, boolean WikiPresent){
		log.info("INFO: Opening the view: " + view.toString());

		//click on the appropriate view to test
		view.select(ui);
		ui.waitForPageLoaded(driver);

		if (WikiPresent){
			ui.fluentWaitTextPresent(wiki.getName());
			log.info("INFO: View was opened and wiki confirmed in the view");
		}else {
			driver.isTextNotPresent(wiki.getName());
			log.info("INFO: View was opened and wiki is not in the view as expected");
		}
	}

	/** Verify new homepage UI */
	public void verifyNewHomePageUI(BaseWiki wiki) throws Exception {

		//Verify wiki name & breadcrumbs
		Assert.assertTrue(driver.isTextPresent(wiki.getName()),
						  "ERROR: Wiki name isn't visible");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.All_Breadcrumb_Text),
						  "ERROR: Breadcrumbs aren't visible");

		//Verify tagging UI
		Assert.assertTrue(driver.isTextPresent("None"), 
						  "ERROR: Tag Name isn't visible");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Add_tags_Link),
						  "ERROR: Add tags link isn't visible");

		//Verify recommendations
		Assert.assertTrue(driver.isTextPresent("Like"),
						  "ERROR: Recommendations isn't correct");

		//Verify that all page buttons are visible
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Edit_Button),
						  "ERROR: Edit button is missing" );
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button),
						  "ERROR: Page Actions button is missing");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Follow_Button),
						  "ERROR: Follow button is missing");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Wiki_Actions_Button),
						  "ERROR: Wiki Actions button is missing" );

		//Verify all inline tabs are visible
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Comments_Tab),
						  "ERROR: Comments tab is missing" );
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Versions_Tab),
						  "ERROR: Versions tab is missing" );
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Attachments_Tab),
						  "ERROR: Attachments tab is missing" );
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.About_Tab),
						  "ERROR: About tab is missing" );

	}

	
	private void addChildPage(BaseWikiPage parent, BaseWikiPage child){
		
		log.info("INFO: Select " + parent.getName());
		ui.clickLinkWait("link=" + parent.getName());
		
		log.info("INFO: Create child page");
		child.create(ui);

	}	


	/** Search for a tag */
	private void searchForTag(String TagToSearchFor, BaseWiki wiki)throws Exception{

		ui.clickLinkWait("link=Find a Tag");
		ui.fluentWaitPresent("css=input#lconnTagWidgetcommonTagsTypeAhead");
		driver.getSingleElement("css=input#lconnTagWidgetcommonTagsTypeAhead").type(TagToSearchFor);

		driver.typeNative(Keys.ENTER);
		
		ui.fluentWaitPresent("css=a.lotusFilter");
		
		String ExpectedTagText = "Tagged with '"+TagToSearchFor+"'";
		
		driver.getSingleElement("css=a.lotusFilter").getText().contains(ExpectedTagText);
		
		//Verify that the wiki which has the tag is appearing in the filter
		driver.getSingleElement("css=h4 a.entry-title").getText().contains(wiki.getName());
		
	}	
	
}
