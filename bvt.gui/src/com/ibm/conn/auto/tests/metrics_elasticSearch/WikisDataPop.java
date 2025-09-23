package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;



public class WikisDataPop extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(WikisDataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;
	private WikisUI ui;
	private APICommunitiesHandler apiCommOwner;
	private APIWikisHandler apiOwner;
	private String serverURL;
	private User testUser1, testUser2, testUser3, testUser4;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui = WikisUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		URLConstants.setServerURL(serverURL);
	}	
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Standalone Wiki: Edit Wiki </li>
	 * <li><B>Step:</B> Create a public wiki </li>
	 * <li><B>Step:</B> Edit the wiki name </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, no standalone Wiki on the cloud.
	 */
	@Test(groups = { "regression"})
	public void editStandaloneWiki(){
		
		ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder("Standalone Wiki to be edited " + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description of wiki to be edited")
									.build();

		BaseWiki newWiki = new BaseWiki.Builder("Edited Standalone Wiki Name " + Helper.genDateBasedRand())
									   .tags("newTag " + Helper.genDateBasedRand())
									   .description("New Description for edited wiki ")
									   .build();
		
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		log.info("INFO: Click on the Wiki view: I'm an Owner");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		log.info("INFO: Wait for the Wiki page header to appear");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		log.info("INFO: Edit the Wiki replacing information from 'newWiki'");
		wiki.edit(ui, newWiki);
		
		log.info("INFO: Navigate to the main Wiki page");
		ui.clickLinkWait(WikisUIConstants.WikisLink);
		
		log.info("INFO: Check to see if the updated wiki name appears");
		Assert.assertTrue(ui.fluentWaitTextPresent(newWiki.getName()),
						  "ERROR: The updated wiki name does not appear");

		ui.endTest();
			
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Standalone Wikis: Edit Wiki Page </li>
	 * <li><B>Step:</B> Create a public wiki </li>
	 * <li><B>Step:</B> Add a page </li>
	 * <li><B>Step:</B> Edit the wiki page name</li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"})
	public void editStandaloneWikiPage() {
			
		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
								    .tags("tag" + Helper.genDateBasedRand())
								    .description("Description for test " + testName)
								    .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("Test Wiki Page to be edited " + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1")
												.description("Test description for edit wiki page test")
												.build();
		
		log.info("INFO: Create a new Wiki using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		
		log.info("INFO: Create a wiki page using API");
		wikiPage.createAPI(apiOwner, apiWiki);
		
		log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		log.info("INFO: Click on the Wiki view: I'm an Owner");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		log.info("INFO: Wait for Wiki page header to appear");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		log.info("INFO: Change the name of the Wiki page inside the BaseWikiPage");
		wikiPage.setName(Data.getData().editedData + wikiPage.getName());

		log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
		wikiPage.setDescription(Data.getData().editedData + wikiPage.getDescription());
		
		log.info("INFO: Edit the current Wiki page");
		ui.editWikiPage(wikiPage);	

		log.info("INFO: Validate that the Wiki page name has been edited");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR: The edited page name does not appear");
		
		ui.endTest();
		
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Edit Wiki Page </li>
	 * <li><B>Step:</B> Create a community using the API </li>
	 * <li><B>Step:</B> Add wiki app - if on-premises </li>
	 * <li><B>Step:</B> Add a wiki page </li>
	 * <li><B>Step:</B> Edit the wiki page name </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void editPageInCommunityWiki() {

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("editWikiPageInComm " + Helper.genDateBasedRand())
                                     .description("Edit a wiki page from within a community ")
                                     .access(Access.PUBLIC)
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("this is a test description for creating a Peer wiki page")
									.build();

		BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Peer)
												.tags("updated_tag1, updated_tag2")
												.description("Updating the wiki page with new content")
												.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);

		log.info("INFO: Edit the current Wiki Page");
		ui.editWikiPage(newWikiPage);

		log.info("INFO: Validate that the wiki page name has been edited");
		Assert.assertTrue(ui.fluentWaitTextPresent(newWikiPage.getName()),
							"ERROR: The edited page name does not appear");
		
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Members Follow Wiki </li>
	 * <li><B>Step:</B> Create a community with (2) additional users using the API </li>
	 * <li><B>Step:</B> Add wiki app - if on-premises </li>
	 * <li><B>Step:</B> Log in as community creator, UserA, & follow the community wiki </li>
	 * <li><B>Step:</B> Log out as UserA & log in as UserB (comm Member) </li>
	 * <li><B>Step:</B> As UserB, follow the community wiki </li>
	 * <li><B>Step:</B> Log out as UserB & log in as UserC (add'l comm Owner) </li>
	 * <li><B>Step:</B> As UserC, follow the community wiki </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void followCommunityWiki() {
		
		String testName = ui.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
                                     .description("Follow community wiki test ")
                                     .access(Access.PUBLIC)
                                     .addMember(member)
									 .addMember(member1)
                                     .build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		this.startFollowingCommunityWiki();
		
		log.info("INFO: Log out as: " + testUser1.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		this.startFollowingCommunityWiki();		
		
		log.info("INFO: Log out as: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser3);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
     
		this.startFollowingCommunityWiki();
		
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Standalone Wiki: Users Follow Wiki </li>
	 * <li><B>Step:</B> Create a wiki using the API </li>
	 * <li><B>Step:</B> Log in as UserB & 'follow' the wiki </li>
	 * <li><B>Step:</B> Log out as UserB & log in as UserC </li>
	 * <li><B>Step:</B> As UserC follow the standalone wiki </li>
	 * <li><B>Step:</B> Log out as UserC & log in as UserD </li>
	 * <li><B>Step:</B> As UserD follow the standalone wiki </li>
	 * </ul>
	 * NOTE: NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud	 
	 */
	@Test(groups = {"regression"})
	public void followStandaloneWiki() {
		
		ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder("Standalone Wiki to be followed " + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description of wiki to be followed")
									.build();		

		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		log.info("INFO: Log into Wikis as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		this.startFollowingStandaloneWiki(wiki);
		
		log.info("INFO: Log out as: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Wikis as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);

		this.startFollowingStandaloneWiki(wiki);
		
		log.info("INFO: Log out as: " + testUser3.getDisplayName());
		ui.logout();
						
		log.info("INFO: Log into Wikis as: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);

		this.startFollowingStandaloneWiki(wiki);
		
		ui.endTest();
			
	}
	
	
	/**
	* The startFollowingCommunityWiki method will: 
	* - click Wikis tab on the nav. menu
	* - click Follow Actions menu link
	* - click on the Follow this Wiki link
	* - verify the follow confirmation message displays
	*/		
	private void startFollowingCommunityWiki() {
		
		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Click on the Following Actions menu");
		ui.clickLinkWait(WikisUIConstants.Follow_Button);
		
		log.info("INFO: Click on the Follow this Wiki link");
		ui.clickLinkWait(WikisUIConstants.Start_Following_this_wiki);
		
		log.info("INFO: Verify the follow wiki confirmation message displays");
		Assert.assertTrue(driver.isTextPresent(WikisUIConstants.Follow_Wiki_Message),
				"ERROR: The follow wiki confirmation message does not display");
		
	}
	
	/**
	* The startFollowingStandaloneWiki method will: 
	* - click Wikis tab on the nav. menu
	* - click Follow Actions menu link
	* - click on the Follow this Wiki link
	* - verify the follow confirmation message displays
	*/		
	private void startFollowingStandaloneWiki(BaseWiki wiki) {
		
		log.info("INFO: Click on the Wiki view: Public Wikis");
		Wiki_LeftNav_Menu.PUBLICWIKIS.select(ui);
		
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		log.info("INFO: Clicking on the Following Actions menu");
		ui.fluentWaitPresent(WikisUIConstants.Follow_Button);
		ui.clickLinkWait(WikisUIConstants.Follow_Button);
		
		log.info("INFO: Click on the Follow this Wiki link");
		ui.clickLinkWait(WikisUIConstants.Start_Following_this_wiki);
		
		log.info("INFO: Verify the follow wiki confirmation message displays");
		Assert.assertTrue(driver.isTextPresent(WikisUIConstants.Follow_Wiki_Message),
				"ERROR: The follow wiki confirmation message does not display");
		
	}
	
}
