package com.ibm.conn.auto.tests.wikis;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFile;
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
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.WikiPage_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_Page_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Wikis extends  SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Wikis.class);
	private WikisUI ui;
	private DogearUI dUI;
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
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		dUI = DogearUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test to validate mega menu</li>
	 *<li><B>Verify: </B>Verify mega menu Wikis options</li>
	 *<li><B>Verify: </B>Verify mega menu item for Wikis</li>
	 *<li><B>Verify: </B>Verify mega menu item for I'm an Owner</li>
	 *<li><B>Verify: </B>Verify mega menu item for Public Wikis</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt"})
	public void validateMegaMenu() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		// Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);
		
		//Click Mega Menu item
		logger.strongStep("Select the Wikis 'Mega Menu' option");
		log.info("INFO: Select the Wikis 'Mega Menu' option");
		ui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate Wikis option is contained with in drop down menu
		logger.weakStep("Validate that the 'Wikis' option is contained within the drop down menu");
		log.info("INFO: Validate that the 'Wikis' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.wikisOption),
						  "Unable to locate Mega Menu 'Wikis' option in drop down menu");

		//Validate I'm an Owner option is contained with in drop down menu
		logger.weakStep("Validate that the 'I'm an Owner' option is contained within drop down menu");
		log.info("INFO: Validate that the 'I'm an Owner' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.wikisImAnOwner),
						  "Unable to locate Mega Menu 'I'm an Owner' option in drop down menu");

		//Validate Public Wikis option is contained with in drop down menu
		logger.weakStep("Validate that the 'Public Wikis' option is contained within drop down menu");
		log.info("INFO: Validate that the 'Public Wikis' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.wikisPublicWikis),
						  "Unable to locate Mega Menu 'Public Wikis' option in drop down menu");

		ui.endTest();
	
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test to create a public wiki</li>
	 * <li><B>Step: </B>Create a new public wiki</li>
	 * <li><B>Verify: </B>verify that the new wikis homepage is loaded</li>
	 * <li><B>Verify: </B>Verify that all page buttons are visible</li>
	 * <li><B>Verify: </B>verify inline tabs are visible</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"cplevel2", "level2", "bvt", "cnx8ui-level2" })
	public void createPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//create a new wiki
		logger.strongStep("Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		wiki.create(ui);

		log.info("INFO: Validate all objects on the page after a Wiki has been created");

		//Verify that all page buttons are visible
		logger.weakStep("Validate that the 'Wiki Actions' button is present");
		log.info("INFO: Validate that the 'Wiki Actions' button is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Wiki_Actions_Button),
						  "ERROR: The 'Wiki Actions' button is missing");
		
		logger.weakStep("Validate that the 'Edit' button is present");
		log.info("INFO: Validate that the 'Edit' button is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Edit_Button),
						  "ERROR: The 'Edit' button is missing");
		
		logger.weakStep("Validate that the 'Page Actions' button is present");
		log.info("INFO: Validate that the 'Page Actions' button is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button),
						  "ERROR: The 'Page Actions' button is missing");
		
		logger.weakStep("Validate that the 'Follow' button is present");
		log.info("INFO: Validate that the 'Follow button is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Follow_Button),
						  "ERROR: The 'Follow' button is missing");

		//Verify all inline tabs are visible
		logger.weakStep("Validate that the 'Comments' tab is present");
		log.info("INFO: Validate that the 'Comments' tab is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Comments_Tab),
						  "ERROR: The 'Comments' tab is missing");
		
		logger.weakStep("Validate that the 'Versions' tab is present");
		log.info("INFO: Validate that the 'Versions' tab is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Versions_Tab),
						  "ERROR: The 'Versions' tab is missing");
		
		logger.weakStep("Validate that the 'Attachments' tab is present");
		log.info("INFO: Validate that the 'Attachments' tab is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Attachments_Tab),
						  "ERROR: The 'Attachments' tab is missing");
		
		logger.weakStep("Validate that the 'About' tab is present");
		log.info("INFO: Valdiate that the 'About' tab is present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.About_Tab),
						  "ERROR: The 'About' tab is missing");

		//Logout of Wiki
		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test to add Pages to public wiki</li>
	 * <li><B>Step: </B>Create a public wiki</li>
	 * <li><B>Step: </B>Add a child/peer page</li>
	 * <li><B>Verify: </B>verify that the child page has been created </li>
	 * <li><B>Verify: </B>verify that the peer page has been created </li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"cplevel2", "level2", "bvt", "cnx8ui-level2" })
	public void addPagesToPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for creating a Peer wiki page")
												.build();

		BaseWikiPage childPage = new BaseWikiPage.Builder("Child_Wiki_" + Helper.genDateBasedRand(), PageType.Child)
					 							 .tags("tag1, tag2")
					 							 .description("this is a test description for creating a Child wiki page")
					 							 .build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//create a peerpage
		logger.strongStep("Create a new Peer Page in the Wiki");
		log.info("INFO: Create a new Peer Page in the Wiki");
		peerPage.create(ui);

		//create a childpage
		logger.strongStep("Create a new Child page in the Wiki");
		log.info("INFO: Create a new Child Page in the Wiki");
		childPage.create(ui);
		
		//Verify pages have been created
		logger.weakStep("Validate that the Peer Page exists");
		log.info("INFO: Validate that the Peer Page exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(peerPage.getName()),
						  "ERROR:" + peerPage.getName() + "Page does not exist");
		
		logger.weakStep("Validate that the Child Page exists");
		log.info("INFO: Validate that the Child Page exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(childPage.getName()),
		  				  "ERROR:" + childPage.getName() + "Page does not exist");

		//Logout of wiki
		ui.endTest();
		
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test to upload file to public wiki</li>
	 * <li>Step: </B>Create a public wiki</li>
	 * <li>Step: </B>Upload an attachment</li>
	 * <li>Verify: </B>verify that the attachment is uploaded</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "cnx8ui-level2" })
	public void uploadFileToPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: Wiew the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for Wiki page header");
		log.info("INFO: Waiting for Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Upload private file
		logger.strongStep("Upload a file to the Wiki");
		log.info("INFO: Upload a file to the Wiki");
		ui.uploadAttachment(file.getRename(), file.getExtension(), file.getName());

		//Logout of Wiki
		ui.endTest();
	
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Edit the page in Public wiki </li>
	 * <li><B>Step: </B>Create a public wiki </li>
	 * <li><B>Step: </B>Add a page </li>
	 * <li><B>Step: </B>Edit the page using CKEditor</li>
	 * <li><B>Step: </B>Change the name of the BaseWikiPage</li>
	 * <li><B>Step: </B>Change the description of the WikiPage</li>
	 * <li><B>Step: </B>Save the page</li>
	 * <li><B>Verify: </B>Verify that the page has been saved</li>
	 * <li><B>Verify: </B>Verify that you can edit the page in the editor</li>
	 * <li><B>Verify: </B>Verify that the page description has been changed</li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "cnx8ui-level2" })
	public void editPageInPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());		

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
								    .tags("tag" + Helper.genDateBasedRand())
								    .description("Description for test " + testName)
								    .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for Wiki page header");
		log.info("INFO: Waiting for Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//create a new wikipage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);

		//Change name of BaseWikiPage 
		logger.strongStep("Change the name of the Wiki Page inside the BaseWikiPage");
		log.info("INFO: Change the name of the Wiki Page inside the BaseWikiPage");
		wikiPage.setName("New_" + wikiPage.getName());

		logger.strongStep("Change the description of the Wiki Page inside the BaseWikiPage");
		log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
		wikiPage.setDescription("Edit " + wikiPage.getDescription());
		
		//Edit the current page and verify that the page has being edited
		logger.weakStep("Edit the current Wiki Page and validate that the page has been edited");
		log.info("INFO: Edit the current Wiki Page and validate that the page has been edited");
		ui.editWikiPage(wikiPage);

		//Now verify that the page has being saved successfully
		logger.weakStep("Validate that the Page name has been changed");
		log.info("INFO: Validate that the Page name has been changed");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR: Name does not match expected");
		
		logger.weakStep("Validate that the Page description has been changed");
		log.info("INFO: Validate that the Page description has been changed");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getDescription()),
						  "ERROR: Description does not match expected");

			
		//Logout of Wiki
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Adding a comment to Public Wiki</li>
	 * <li><B>Step: </B>Create a public wiki</li>
	 * <li><B>Step: </B>Add a comment to the wiki</li>
	 * <li><B>Verify: </B>Comment is added to the wiki</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "cnx8ui-level2" })
	public void addCommentToPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
	    							.tags("tag" + Helper.genDateBasedRand())
	    							.description("Description for test " + testName)
	    							.build();

		log.info("INFO: Create a new Wiki using API");
		logger.strongStep("Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));;
		
		logger.strongStep("Wait for Wiki page header");
		log.info("INFO: Waiting for Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add a comment and verify that the comment is added
		logger.weakStep("Add a comment to the Wiki and validate that the comment was added");
		log.info("INFO: Add a comment to the Wiki and validate that the comment was added");
		ui.addComment(Data.getData().Comment_For_Public_Wiki);

		//Logout of Wiki
		ui.endTest();
	
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Add a tag to public wiki</li>
	 * <li><B>Step: </B>Create a public wiki using an API</li>
	 * <li><B>Step: </B>Add a page level tag</li>
	 * <li>Verify: </B>Verify that a page level tag is added to the page</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"regression", "bvt"})
	public void addTagToPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for Wiki page header");
		log.info("INFO: Waiting for Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Add new tag to page and verify that the tag is added
		logger.weakStep("Add a new tag to the Wiki and validate that the tag was added");
		log.info("INFO: Add a new tag to the Wiki and validate that the tag was added");
		ui.addWikiTag(Data.getData().Tag_For_Public_Wiki);

		//Logout of Wiki
		ui.endTest();
				
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test liking and unliking public wikis</li>
	 * <li><B>Step: </B>Create a public wiki</li>
	 * <li><B>Step: </B>Like the existing wiki</li>
	 * <li><B>Step: </B>Unlike the current page</li>
	 * <li><B>Verify: </B>If current page is unliked</li>
	 * <li><B>Step: </B>Like the page a second time</li>
	 * <li><B>Verify: </B>You can like and unlike a page in the wiki</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "cnx8ui-level2" })
	public void likeUnlikePublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
		ui.waitForPageLoaded(driver);

		//Like the existing wiki
		logger.strongStep("Like the existing Wiki");
		log.info("INFO: Like the existing Wiki");
		ui.likeUnlikePage("Like");

		//Now unlike the current page and check that it is now unliked
		logger.strongStep("Unlike the existing Wiki");
		log.info("INFO: Unlike the existing Wiki");
		ui.likeUnlikePage("Unlike");

		//Like the page again
		logger.strongStep("Like the existing Wiki a second time");
		log.info("INFO: Like the existing Wiki a second time");
		ui.likeUnlikePage("Like");

		//Logout of Wiki
		ui.endTest();
		
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Deleting a Page test</li>
	 * <li><B>Step: </B>Create a public wiki</li>
	 * <li><B>Step: </B>Add a page</li>
	 * <li><B>Step: </B>Delete the page</li>
	 * <li><B>Verify: </B>The page is deleted and no longer present in the UI </li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = {"regression", "bvt"})
	public void deletePageTest() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + " Child page", PageType.Child)
												.tags("tag1, tag2")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		log.info("INFO: Create a new Wiki using API");
		logger.strongStep("Create a new Wiki using API");
		wiki.createAPI(apiOwner);	

		//Load component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);
				
		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create WikiPage
		logger.strongStep("Create a new Wiki Page inside the Wiki");
		log.info("INFO: Create a new Wiki Page inside the Wiki");
		wikiPage.create(ui);
		
		//Select the WikiPage just created
		logger.strongStep("Select the newly created Wiki Page");
		log.info("INFO: Select the newly created Wiki Page");
		ui.clickLink(WikisUI.getWikiPage(wikiPage));
	
		//Delete specified page
		logger.strongStep("Delete the Page");
		log.info("INFO: Delete the Page");
		wikiPage.delete(ui);

		//Switch to trash view
		logger.strongStep("Switch to the Wiki 'Trash' view");
		log.info("INFO: Switch to the Wiki 'Trash' view");
		WikiPage_LeftNav_Menu.TRASH.select(ui);
		
		//Validate page made it into the trash
		logger.weakStep("Validate that the Page is in the 'Trash' view");
		log.info("INFO: Validate that the Page is in the 'Trash' view");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR: Unable to locate the Page in 'Trash'");	
		
		//Logout of Wiki
		ui.endTest();
				
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Edit Public wiki</li>
	 * <li><B>Step: </B>Create a public wiki </li>
	 * <li><B>Step: </B>Edit the wiki details </li>
	 * <li><B>Step: </B>Navigate to the main wiki page </li>
	 * <li><B>Verify: </B>Wiki name has changed</li>
	 * <li><B>Verify: </B>Wiki name is in list</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = { "regression", "bvt"})
	public void editPublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		BaseWiki newWiki = new BaseWiki.Builder("New Name" + Helper.genDateBasedRand())
									   .tags("newTag" + Helper.genDateBasedRand())
									   .description("New Description for test ")
									   .build();
		
		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Edit wiki replacing information from newWiki
		logger.strongStep("Edit the Wiki name");
		log.info("INFO: Edit the Wiki replacing information from 'newWiki'");
		wiki.edit(ui, newWiki);
		
		//Navigate to wiki pages
		logger.strongStep("Navigate to main Wiki page");
		log.info("INFO: Navigate to main Wiki page");
		ui.clickLinkWait(WikisUIConstants.WikisLink);
		
		//Validate wiki with old name no longer exists
		logger.weakStep("Check to see if the Wiki with the old name is still in the list");
		log.info("INFO: Check to see if the Wiki with the old name is still in the list");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						  "ERROR: The Wiki's old name is still in the list");
		
		//Validate wiki reports new name from edit (newWiki)
		logger.weakStep("Check to see if the Wiki with the new name is in the list");
		log.info("INFO: Check to see if the Wiki with the new name is in the list");
		Assert.assertTrue(ui.fluentWaitTextPresent(newWiki.getName()),
						  "ERROR: The Wiki's new name is not in the list");

		//Logout of Wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();
			
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Deleting a public wiki </li>
	 * <li><B>Step: </B>Create a public wiki using API </li>
	 * <li><B>Step: </B>Then choose to delete the wiki</li>
	 * <li><B>Verify: </B>Wiki is deleted correctly </li>
	 * <li><B>Verify: </B>Wiki is no longer present in the UI </li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * note: this isn't supported on the cloud
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt", "cnx8ui-level2" })
	public void deletePublicWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWithJavascript(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
		
		//ensure deleted
		logger.weakStep("Validate that the Wiki was deleted by waiting for the message: 'The wiki was deleted.'");
		log.info("INFO: Validate that the Wiki was deleted by waiting for the message: 'The wiki was deleted.'");
		Assert.assertTrue(ui.fluentWaitTextPresent("The wiki was deleted."),
							"ERROR: 'The wiki was deleted.' message was not found");
		
		//ensure wiki no longer exists
		logger.weakStep("Check to see if Wiki is still in the list");
		log.info("INFO: Check to see if Wiki is still in the list");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						  "ERROR: The Wiki still exists");

		//Logout of Wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Tests anonymous access</li>
	 * <li><B>Step: </B>Create a wiki</li>
	 * <li><B>Verify: </B>Verify page title contains public wikis</li>
	 * <li><B>Verify: </B>Verify public wikis text is visible</li>
	 * </ul>
	 * Note: This isn't supported on the cloud
	 */
	@Test(groups = { "level2", "bvt", "cnx8ui-level2" })
	public void anonymousAccess() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Get Wikis' public URL
		String wikisURL = Data.getData().ComponentWikis.split("login")[0];
		
		ui.startTest();
		
		logger.strongStep("Load Wikis");
		log.info("INFO: Load the Wikis component");
		ui.loadComponent(wikisURL);
		CommonUICnx8 commonUI = new CommonUICnx8(driver);
		commonUI.toggleNewUI(cfg.getUseNewUI());
		
		logger.weakStep("Validate that the page title contains Public Wikis");
		log.info("Validate that the page title contains 'Public Wikis'");
		ui.fluentWaitElementVisible(WikisUIConstants.Public_Wikis_Filter);
		String pageTitle = driver.getTitle();
		Assert.assertTrue(pageTitle.contains("Public Wikis"), 
						  "ERROR: The page title: '" + pageTitle + "', Page title did not contain Public Wikis");
		
		logger.weakStep("Validate that the page title contains the Public Wikis text");
		log.info("Validate that the page title contains the Public Wikis text");
		Assert.assertTrue(ui.isTextPresent("Public Wikis"), 
				          "ERROR: The Public Wikis text missing.");
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Unliking a Community Wiki </li>
	 * <li><B>Step: </B>Create a community using an API </li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Click on the wiki link in the nav </li>
	 * <li><B>Step: </B>Like the current wiki page </li>
	 * <li><B>Step: </B>Unlike the current wiki</li>
	 * <li><B>Verify: </B>That the current page has been unliked </li>
	 * <li><B>Step: </B>Relike the wiki page a second time </li>
	 * <li><B>Verify: </B>Verify that the wiki page is liked </li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud", "bvt"})
	public void UnlikeCommunityWiki() throws Exception {
		
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
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communites' view");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//Like the existing wiki
		logger.strongStep("Like the existing Wiki");
		log.info("INFO: Like the existing Wiki");
		ui.likeUnlikePage("Like");

		//Now unlike the current page and check that it is now unliked
		logger.strongStep("Unlike the existing Wiki");
		log.info("INFO: Unlike the existing Wiki");
		ui.likeUnlikePage("Unlike");

		//Like the page again
		logger.strongStep("Like the existing Wiki a second time");
		log.info("INFO: Like the existing Wiki a second time");
		ui.likeUnlikePage("Like");

		logger.strongStep("Delete the Community that was created using API");
		apiComOwner.deleteCommunity(comAPI);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test the version tab of a wiki page</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add wiki widget</li>
	 * <li><B>Step: </B>Add a page to the wiki</li>
	 * <li><B>Step: </B>Edit the page in the editor</li>
	 * <li><B>Verify: </B>Page has been editited</li>
	 * <li><B>Step: </B>Check the version tab</li> 
	 * <li><B>Step: </B>Show the comparison</li>
	 * <li><B>Step: </B>Open the old version</li>
	 * <li><B>Step: </B>Show the comparison</li>
	 * <li><B>Step: </B>Delete the old version</li> 
	 * <li><B>Step: </B>Create the third version again</li>
	 * <li><B>Verify: </B>Changes have been made</li>
	 * <li><B>Step: </B>Restore the second version to the current one</li>
	 * <li><B>Verify: </B>The page has been saved successfully</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "level2", "regressioncloud", "bvt", "cnx8ui-level2" })
	public void versionTab() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		User adminUser = cfg.getUserAllocator().getAdminUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("this is a test description for creating a Peer wiki page")
									.build();
		
		BaseWikiPage newwikiPage = new BaseWikiPage.Builder("New_" + wikiPage.getName(), PageType.Peer)
												.tags("updated_tag1, updated_tag2")
												.description("updated with new content")
												.build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//Check Gatekeeper value for Communities Tabbed Nav setting
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//create a new wikipage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);

		//Edit the current page and verify that the page has being edited
		logger.weakStep("Edit the current Page and validate that the Page has been edited");
		log.info("INFO: Edit the current Page and validate that the Page has been edited");
		ui.editWikiPage(newwikiPage);
		
		//Now verify that the page has being saved successfully
		logger.weakStep("Validate that the Page name has been changed");
		log.info("INFO: Validate that the Page name has been changed");
		driver.isTextPresent(newwikiPage.getName());
		
		logger.weakStep("Validate that the Page description has been changed");
		log.info("INFO: Validate that the Page description has been changed");
		driver.isTextPresent(newwikiPage.getDescription());
		
		logger.weakStep("Check the version tab");
		log.info("INFO: Check the version tab");
		ui.clickLinkWait(WikisUIConstants.Versions_Tab);
		Assert.assertTrue(driver.isTextPresent("1-2 of 2"));
		
		logger.weakStep("Compare the versions");
		log.info("INFO: Compare the versions");
		ui.clickLinkWait(WikisUIConstants.ShowComparisonLink);
		Assert.assertTrue(driver.isTextPresent(Data.getData().VersionComparison));
		driver.getSingleElement("css=span:contains(" + wikiPage.getDescription() + ")").getAttribute("class").equals("diff-html-removed");
		driver.getSingleElement("css=span:contains(" + newwikiPage.getDescription() + ")").getAttribute("class").equals("diff-html-added");
		
		logger.weakStep("Check the old version");
		log.info("INFO: Check the old version");
		ui.clickLinkWait(WikisUIConstants.ViewLink);
		Assert.assertTrue(driver.isTextPresent(wikiPage.getName() + ": Version 1"));
		Assert.assertTrue(driver.isTextPresent(wikiPage.getDescription()));
		Assert.assertTrue(driver.isTextNotPresent(newwikiPage.getDescription()));
		
		//TODO: Defect LC-116994 Here is a defect that if on previous version page click show comparison, page goes to error page.
		
		logger.weakStep("Delete the old version");
		log.info("INFO: Delete the old version");
		ui.clickLinkWait(WikisUIConstants.DeleteLink);
		Assert.assertTrue(driver.isTextPresent(Data.getData().DeletePriorVersionConfirmMsg));
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		
		gk_flag = "WIKIS_ENABLE_USED_PAGE_TITLE";
		//Defect 171508, when gk is enabled no longer recieve page doesn't exist but instead redirects to other version
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		value = gkc.getSetting(gk_flag);
		log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value);
		
		if(!value){
			log.info("INFO: Verify page doesn't exist displays");
			Assert.assertTrue(driver.isTextPresent(wikiPage.getName()));
			Assert.assertTrue(driver.isElementPresent(WikisUIConstants.CreateThisPageBtn));
			driver.getSingleElement(WikisUIConstants.NoThanksBtn).click();
		}
						
		logger.weakStep("Create a new version after deleting the old version");
		log.info("INFO: Create a new version after deleting the old version");
		ui.editWikiPage(wikiPage);
		
		//Now verify that the page has being saved successfully
		logger.weakStep("Validate that the Page name has been changed");
		log.info("INFO: Validate that thew Page name has been changed");
		driver.isTextPresent(wikiPage.getName());
		
		logger.weakStep("Validate that the Page description has been changed");
		log.info("INFO: Validate that the Page description has been changed");
		driver.isTextPresent(wikiPage.getDescription());
		
		logger.weakStep("Check the version tab");
		log.info("INFO: Check the version tab");
		ui.clickLinkWait(WikisUIConstants.Versions_Tab);
		Assert.assertTrue(driver.isTextPresent("1-2 of 2"));
		Assert.assertTrue(driver.isTextPresent("3 Today"));
		
		logger.weakStep("Restore the Page");
		log.info("INFO: Restore the Page");
		ui.clickLinkWait(WikisUIConstants.RestoreLink);
		Assert.assertTrue(driver.isTextPresent(Data.getData().RestoreVersionMsg + "2"));
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		Assert.assertTrue(driver.isTextPresent("1-3 of 3"));
		Assert.assertTrue(driver.isTextPresent(Data.getData().RestoreFromMsg + " 2"));

		logger.strongStep("Delete the Community that was created using API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of Wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();		
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test to check I Edited page</li>
	 * <li><B>Step: </B>Create a community using an API</li>
	 * <li><B>Step: </B>Add an wiki widget using an API</li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Create a new peer wiki page as the owner</li>
	 * <li><B>Step: </B>Create a new child wiki pages as a member</li>
	 * <li><B>Step: </B>Select the "Index" link</li>
	 * <li><B>Verify: </B>Three pages are listed on the index page</li>
	 * <li><B>Step: </B>Select the "I Edited" link</li>
	 * <li><B>Verify: </B>One page listed as edited by member</li>
	 * <li><B>Step: </B>The member edits the owner page</li>
	 * <li><B>Step: </B>Select the "I Edited" link</li>
	 * <li><B>Verify: </B>Pages modified by the member is updated to two</li>
	 * </ul>
	 */
	@Test(groups = { "level2", "regressioncloud", "bvt", "cnx8ui-level2" })
	public void iEditedPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		User comMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 .access(defaultAccess)
									 .build();

		BaseWikiPage ownerwikiPage = new BaseWikiPage.Builder(testName + "_Owner", PageType.Peer)
												.description("this is a test description for creating a wiki page")
												.build();
		
		BaseWikiPage memberwikiPage = new BaseWikiPage.Builder(testName + "_Member", PageType.Child)
												.description("this is a test description for creating a wiki page A")
												.build();
		
		BaseWikiPage newownerwikiPage = new BaseWikiPage.Builder("Updated" + testName + "_Owner", PageType.Peer)
													.description("this is a test description for creating a wiki page")
													.build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
				
		//add the UUID to community
		log.info("INFO: Get the UUID of Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Wiki widget is not activated");
			log.info("INFO: Add Wiki widget using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
				
		//this block is to aid with debugging wiki widget
		log.info("INFO: Get the UUID of the Community");
		String commUUID = community.getCommunityUUID_API(apiComOwner, comAPI);
		log.info("INFO: Get the Wiki widget ID of the Community using API");
		String widgetID = apiComOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Wiki");
		log.info("Wikis Widget ID is: " + widgetID);

	
		//GUI
		//Load Communities and login as the owner
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);
		
		String communityLink = isCardView ? cUI.getCommunityLinkCardView(community) : cUI.getCommunityLink(community);

		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);
		
		//navigate to the community and select Wiki in left nav
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//owner creates a new peer page and logs out
		logger.strongStep("Create a peer page as: " + testUser.getDisplayName());
		log.info("INFO: Create a peer page as: " + testUser.getDisplayName());
		ownerwikiPage.create(ui);	
		ui.logout();
		//ui.close(cfg);
		
		//load Communities and login as the member
		logger.strongStep("Load Communities and Log In as: " + comMember.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(comMember);
		
		//navigate to the community
		logger.strongStep("Navigate to the 'I'm a Member' view");
		log.info("INFO: Navigate to the 'I'm a Member' view");
		//Community_View_Menu.IM_A_MEMBER.select(cUI);
		cUI.goToDefaultIamMemberView(isCardView);
		logger.strongStep("Open the Community: " + community.getName());
		log.info("INFO: Open the Community: " + community.getName());
		ui.clickLinkWait(communityLink);

		//Click on the Wikis link in the nav
		if (value)
		{
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui);
		}else {
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//member creates a new child pages
		logger.strongStep("Create child page as: " + comMember.getDisplayName());
		log.info("INFO: Create child page as: " + comMember.getDisplayName());
		memberwikiPage.create(ui);	
		log.info("INFO: Select Index link in the left navigation");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		
		//verify index page displays with a list of all pages
		logger.weakStep("Validate three Wiki Pages listed on the index page");
		log.info("INFO: Verify three Wiki Pages are listed on the index page");
		Assert.assertTrue(driver.isTextPresent("1-3 of 3"), "ERROR: Text [1-3 of 3] is not present");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.list + "a:contains('Welcome to " + community.getName() + "')"),
				"ERROR: The link to the Wiki Page Welcome to " + community.getName() + " is not present");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(ownerwikiPage)),
				"ERROR: The link to the Wiki Page " + ownerwikiPage.getName() + " is not present");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(memberwikiPage)),
				"ERROR: The link to the Wiki Page " + memberwikiPage.getName() + " is not present");
		
		logger.strongStep("Select 'Pages > I Edited' link in the left navigation");
		log.info("INFO: Select Pages > I Edited link in the left navigation");
		Boolean bool = driver.isElementPresent(WikisUIConstants.IEditedLink);
		if(!bool) driver.getSingleElement(WikisUIConstants.PagesSection).click();
		
		//verify I Edit displays with a list of pages modified by the member
		ui.clickLinkWait(WikisUIConstants.IEditedLink);
		ui.fluentWaitTextPresent("I Edited");
		logger.weakStep("Validate one Page display as edited by member: " + comMember.getDisplayName());
		log.info("INFO: Validate one Page display as edited by member: " + comMember.getDisplayName());
		Assert.assertTrue(driver.isTextPresent("1-1 of 1"), "ERROR: Text [1-1 of 1] is not present");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(memberwikiPage)),
				"ERROR: The link to the wiki page " + memberwikiPage.getName() + " is not present");
		
		//member edits the owner page
		log.info("INFO: Select Index link in the left navigation");
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		ui.clickLinkWait(ui.getPageSelectorinListView(ownerwikiPage));
		ui.editWikiPage(newownerwikiPage);
		ui.fluentWaitTextPresent(newownerwikiPage.getName());
		
		//verify I Edit displays updated list of pages modified by the member
		ui.clickLinkWait(WikisUIConstants.IndexLink);
		ui.clickLinkWait(WikisUIConstants.IEditedLink);
		ui.fluentWaitTextPresent("I Edited");
		logger.weakStep("Validate another Page display as edited by member: " + comMember.getDisplayName());
		log.info("INFO: Verify another Page display as edited by member: " + comMember.getDisplayName());
		Assert.assertTrue(driver.isTextPresent("1-2 of 2"), "ERROR: Text [1-2 of 2] is not present");
		Assert.assertTrue(driver.isElementPresent(ui.getPageSelectorinListView(newownerwikiPage)),
				"ERROR: The link to the wiki page " + newownerwikiPage.getName() + " is not present");
		
		logger.strongStep("Delete the Community created via API");
		apiComOwner.deleteCommunity(comAPI);				
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test of checking Tag Cloud</li>
	 * <li><B>Step: </B>Create a community using an API</li>
	 * <li><B>Step: </B>Add an wiki widget using an API</li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Create two wiki pages inside of wiki</li>
	 * <li><B>Verify: The tags display in the cloud</li>
	 * <li><B>Step: </B>Change tag display to List</li>
	 * <li><B>Verify: </B>View changes correctly to List from Cloud</li>
	 */
	@Test(groups = { "regression", "regressioncloud", "bvt" })
	public void TagCloud() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		String uniqueTagPrefix = (Helper.genStrongRand(6) + "-").toLowerCase();
		String commonTag = uniqueTagPrefix + "tag";
		String peerTag = uniqueTagPrefix + "tagpeer";
		String childTag = uniqueTagPrefix + "tagchild";
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage peerwikiPage = new BaseWikiPage.Builder("Peer_" + testName + Helper.genDateBasedRand(), PageType.Peer)
												.description("this is a test description for creating a Peer wiki page")
												.tags(commonTag + "," + peerTag)
												.build();
		
		BaseWikiPage childwikiPage = new BaseWikiPage.Builder("Child_" + testName + Helper.genDateBasedRand(), PageType.Child)
												.description("this is a test description for creating a Child wiki page")
												.tags(commonTag + "," + childTag)
												.build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		
		//add the UUID to community
		log.info("INFO: Get the UUID of Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);

		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//create a new wikipage
		logger.strongStep("Create two Wiki pages and add tags");
		log.info("INFO: Create two Wiki pages and add tags");
		peerwikiPage.create(ui);	
		childwikiPage.create(ui);
		ui.fluentWaitPresent(WikisUIConstants.tagCloudWidget);
		
		//verify tags display in the cloud
		log.info("Validate that the tags display in the tag cloud");
		logger.weakStep("Validate that the tags display in the tag cloud");
		String[] sPeerTags = peerwikiPage.getTags().split(",");
		String[] sChildTags = childwikiPage.getTags().split(",");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudCloudView),
				"ERROR: The element " + WikisUIConstants.tagCloudCloudView + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[0])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[0]) + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[1]) + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sChildTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sChildTags[1]) + " was not found");
		
		logger.strongStep("Click the 'List' link");
		log.info("Click the 'List' link");
		ui.clickLinkWait(BaseUIConstants.ListLink);
		logger.weakStep("Validate that the view changes display correctly");
		log.info("Validate the tag clould changes to display as a list");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudListView),
				"ERROR: The element " + WikisUIConstants.tagCloudCloudView + " was not found");
		int nTags = driver.getElements(WikisUIConstants.TagListinListView).size();
		Assert.assertTrue(nTags > 0, "ERROR: Wrong number of tags in tag cloud: found " + nTags +
				", expected at least one.");

		logger.strongStep("Delete the Community that was creating via API");
		apiComOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}
	/**
	 * <ul>
	 * <li><B>Info: </B>Test to Update Tag</li>
	 * <li><B>Step: </B>Create a community using an API</li>
	 * <li><B>Step: </B>Add an wiki widget using an API</li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Select Wikis from left navigation menu</li>
	 * <li><B>Step: </B>Create new wiki pages with tags</li>
	 * <li><B>Verify: </B>The tags display in the cloud
	 * <li><B>Step: </B>Update the wiki page to add new tag</li>
	 * <li><B>Step: </B>Remove the tag in Details view</li>
	 * <li><B>Verify: </B>Tag no longer displays</li>
	 * <li><B>Step: </B>From Details view, add two tags and a special tag to page</li>
	 * <li><B>Verify: </B>Special tag displays</li>
	 * </ul>
	 */

	@Test(groups = {"level2", "regressioncloud", "bvt", "cnx8ui-level2" })
	public void UpdateTag() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		User comMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		String specialTag;
		
		//Security Proxy Related check
		if(cfg.getSecurityType().equalsIgnoreCase("SITEMINDER")|| cfg.getSecurityType().equalsIgnoreCase("SITEMINDER_SPNEGO"))
			specialTag = "?~@#$%^*){}|[]\\:./";
		else
			specialTag = "?~@#$%^*){}|[]\\:<>./";
		
		
		
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 .access(defaultAccess)
									 .build();

		BaseWikiPage peerwikiPage = new BaseWikiPage.Builder(testName + "_Peer", PageType.Peer)
												.description("this is a test description for creating a peer wiki page")
												.tags("tag,tagnormal")
												.build();
		BaseWikiPage newpeerwikiPage = new BaseWikiPage.Builder(testName + "_Peer", PageType.Peer)
													.description("this is a test description for creating a peer wiki page")
													.tags("tag,tagnormal,tagpeer")
													.build();
		
		BaseWikiPage childwikiPage = new BaseWikiPage.Builder(testName + "_Child", PageType.Child)
												.description("this is a test description for creating a child wiki page")
												.tags("tagchild")
												.build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);

		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui,2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//create a new wikipage
		logger.strongStep("Create two Wiki Pages inside the Wiki");
		log.info("INFO: Create two Wiki Pages inside the Wiki");
		peerwikiPage.create(ui);	
		childwikiPage.create(ui);	
		ui.fluentWaitPresent(WikisUIConstants.tagCloudWidget);
		
		//Verify the tags display in the cloud
		log.info("Validate that the tags display in the tag cloud");
		logger.weakStep("Validate that the tags display in the tag cloud");
		String[] sPeerTags = newpeerwikiPage.getTags().split(",");
		String[] sChildTags = childwikiPage.getTags().split(",");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudCloudView),
				"ERROR: The element " + WikisUIConstants.tagCloudCloudView + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[0])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[0]) + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[1]) + " was not found");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sChildTags[0])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sChildTags[0]) + " was not found");		
		
		logger.strongStep("Add a new tag to the existing Wiki Page");
		log.info("INFO: Add a new tag to the existing Wiki Page");
		ui.fluentWaitPresent(ui.getPageSelectorFromTree(peerwikiPage));
		driver.getFirstElement(ui.getPageSelector(peerwikiPage)).click();
		ui.editWikiPage(newpeerwikiPage);
		logger.weakStep("Validate that the new tag was added");
		log.info("INFO: Validate that the new tag was added");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[2])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[2]) + " was not found");
		
		logger.strongStep("Remove the tag in detail view");
		log.info("INFO: Remove the tag in detail view");
		ui.clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		driver.getVisibleElements("css=li.lotusTag:contains(" + sPeerTags[1] + ") a").get(0).click();
		ui.fluentWaitTextPresent(Data.getData().RemoveTagmsg + sPeerTags[1] + "?");
		driver.getSingleElement(WikisUIConstants.Dialog_OK_Button).click();
		logger.weakStep("Validate that the tag was removed");
		log.info("INFO: Validate that the tag was removed");
		Assert.assertFalse(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[1]) + " was not removed");
		
		logger.strongStep("Add two tags and a special tag to detail view");
		log.info("INFO: Add two tags and add a special tag to detail view");
		driver.getFirstElement(ui.getPageSelector(childwikiPage)).click();
		ui.clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		driver.getSingleElement(WikisUIConstants.TagEditorTextFieldInput).typeWithDelay(sPeerTags[0] + " " + specialTag);
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		int i=0;
		for(i=0; i<driver.getElements(BaseUIConstants.taglinks).size(); i++){
			System.out.println(driver.getElements(BaseUIConstants.taglinks).get(i).getAttribute("title"));
			if(driver.getElements(BaseUIConstants.taglinks).get(i).getAttribute("title").contains(specialTag))
				break;
		}
		logger.strongStep("Validate that the special tag displays");
		log.info("INFO: Validate that the special tag displays");
		Assert.assertTrue(i<driver.getElements(BaseUIConstants.taglinks).size(),
				"ERROR: Special tag not found ");
		
		logger.strongStep("Delete the Community that was creating via API");
		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	/**
	 * <ul>
	 * <li><B>Info: </B>Test to move the page</li>
	 * <li><B>Step: </B>Create community using an API</li>
	 * <li><B>Step: </B>Add a wikiwidget using an API</li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Navigate to community using UUID</li>
	 * <li><B>Verify: </B>Create two pages as MemberA</li>
	 * <li><B>Step: </B>Move owner's page and reorder it</li>
	 * <li><B>Verify: </B>Verify owner's page is correct</li>
	 * </ul>
	 */	
	@Test(groups = {"regressioncloud"})
	public void MovePage() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		User comMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community for " + testName)
									 .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPageA = new BaseWikiPage.Builder(testName + "_PageA", PageType.Peer)
												.description("this is a test description for creating a peer wiki page")
												.build();
		BaseWikiPage wikiPageB = new BaseWikiPage.Builder(testName + "_PageB", PageType.Child)
												.description("this is a test description for creating a peer wiki page")
												.build();		
		BaseWikiPage wikiPageC = new BaseWikiPage.Builder(testName + "_PageC", PageType.Peer)
												.description("this is a test description for creating a child wiki page")
												.build();
		BaseWikiPage wikiPageD = new BaseWikiPage.Builder(testName + "_PageD", PageType.Peer)
												.description("this is a test description for creating a child wiki page")
												.build();
		BaseWikiPage wikiPageE = new BaseWikiPage.Builder(testName + "_PageE", PageType.Child)
												.description("this is a test description for creating a child wiki page")
												.build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);

		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//create a new wikipage	
		logger.strongStep("Create new Wiki Pages A, B, and C");
		wikiPageA.create(ui);	
		wikiPageB.create(ui);	
		wikiPageC.create(ui);
		ui.close(cfg);
		
		logger.strongStep("Create two pages as: " + comMember.getDisplayName());
		log.info("INFO: Create two pages as: " + comMember.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(comMember);
		Community_View_Menu.IM_A_MEMBER.select(cUI);
		ui.clickLinkWait("link=" + community.getName());

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Wikis link in the nav
		if (value)
		{
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed nav menu");
			Community_TabbedNav_Menu.WIKI.select(ui);
		}else {
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		logger.strongStep("Create Wiki Pages D and E");
		wikiPageD.create(ui);
		wikiPageE.create(ui);
		Wiki_Page_Menu.MOVEPAGE.select(ui);
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MovePageDig));
		
		logger.strongStep("Mark as top level to remove all the Wiki Page choices");
		log.info("INFO: Mark as top level to remove all the Wiki Page choices");
		Assert.assertTrue(driver.getElements(WikisUIConstants.PageLinksinSelBox).size()==5);
		driver.getSingleElement(WikisUIConstants.MarkAsTopCheckbox).click();
		Assert.assertTrue(driver.getElements(WikisUIConstants.PageLinksinSelBox).size()==0);
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		ui.fluentWaitElementVisible(WikisUIConstants.BreadCrumb);
		String location = "You are in:  " + community.getName() + " Wiki > " + wikiPageE.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);
		
		logger.strongStep("Move the owner's Page and reorder it");
		log.info("INFO: Move the owner's Page and reorder it");
		ui.expandPage(wikiPageA);
		driver.getFirstElement(ui.getPageSelector(wikiPageC)).click();
		Wiki_Page_Menu.MOVEPAGE.select(ui);
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MovePageDig));
		Assert.assertTrue(driver.getElements(WikisUIConstants.SelectedPageinSelBox).size() == 2);
		if(!driver.getElements(WikisUIConstants.SelectedPageinSelBox).get(0).getText().equals(wikiPageA.getName())){
			Assert.assertEquals(driver.getElements(WikisUIConstants.SelectedPageinSelBox).get(0).getText(), wikiPageC.getName() + " (current page)");
			Assert.assertEquals(driver.getElements(WikisUIConstants.SelectedPageinSelBox).get(1).getText(), wikiPageA.getName());
		}
		else{
			Assert.assertEquals(driver.getElements(WikisUIConstants.SelectedPageinSelBox).get(1).getText(), wikiPageC.getName() + " (current page)");
			Assert.assertEquals(driver.getElements(WikisUIConstants.SelectedPageinSelBox).get(0).getText(), wikiPageA.getName());
		}
		
		driver.getSingleElement(WikisUIConstants.PageNameTypeBox).type(wikiPageB.getName());
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.PageNameDropdown));
		driver.getSingleElement(WikisUIConstants.PageNameDropdownLink).click();
		
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		ui.fluentWaitElementVisible(WikisUIConstants.BreadCrumb);
		location = "You are in:  " + community.getName() + " Wiki > " + wikiPageA.getName() + " > " + wikiPageB.getName() + " > " + wikiPageC.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);
		
		driver.getFirstElement(ui.getPageSelector(wikiPageA)).click();
		Wiki_Page_Menu.MOVEPAGE.select(ui);
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.MovePageDig));
		for(int i = 0; i<driver.getElements(WikisUIConstants.PageLinksinSelBox).size(); i++){
			if(driver.getElements(WikisUIConstants.PageLinksinSelBox).get(i).getText().equals(wikiPageB.getName()))
				driver.getElements(WikisUIConstants.PageLinksinSelBox).get(i).click();
		}
		logger.weakStep("Validate that the owner's Page is correct");
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.PageNameTypeBox).getAttribute("value"), wikiPageB.getName());
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.SelectedPageNameinSortBox).getAttribute("id").endsWith("1"));
		Assert.assertFalse(driver.getSingleElement(WikisUIConstants.MoveUpImg).getAttribute("class").contains("Disabled"));
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.MoveDownImg).getAttribute("class").contains("Disabled"));
		driver.getSingleElement(WikisUIConstants.MoveUpLink).click();
		Assert.assertTrue(driver.getSingleElement(WikisUIConstants.MoveUpImg).getAttribute("class").contains("Disabled"));
		Assert.assertFalse(driver.getSingleElement(WikisUIConstants.MoveDownImg).getAttribute("class").contains("Disabled"));
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		ui.fluentWaitElementVisible(WikisUIConstants.BreadCrumb);
		location = "You are in:  " + community.getName() + " Wiki > " + wikiPageB.getName() + " > " + wikiPageA.getName();
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.BreadCrumb).getText().trim(),location);

		logger.strongStep("Delete the Community that was created using API");
		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Add a page to the wiki widget inside community</B></li>
	 *<li><B>Steps: </B></li>
	 *<li><B>Create a Community Community Type: (Access: Public, Tags, Description, and a Member(Role:Member)</B></li>
	 *<li><B>Add Wikis widget to the community</B></li>
	 *<li><B>Add a page to the Community Wiki</B></li>
	 *<li><B>Verify: Verify that the Community is created </B></li>
	 *<li><B>Verify: Verify that you can add a wiki page to the wiki widget inside a community </B></li>
	 *</ul>
	 *
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void communityWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();

		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .access(Access.PUBLIC)
									 .tags(Data.getData().commonTag)
									 .description("Test Community for " + testName).build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("Wiki_" + Helper.genDateBasedRand(), PageType.Community)
											    .tags("tag1, tag2")
											    .description("this is a test description for creating a wiki page")
											    .build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Creating a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communites and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser); 
			
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		
		//add wiki page
		logger.strongStep("Create a new Wiki page");
		wikiPage.create(ui);

		//check to see that the page is visible
		logger.weakStep("Validate that the Wiki Page is visible");
		Assert.assertTrue(driver.getSingleElement("css=h1[id='wikiPageHeader']")
								.getText().contains(wikiPage.getName()));
		
		
		//delete community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(apiComOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li>Step: Create a peer wiki page in a community wiki</li>
	 * <li>Validate: Verify creation of a Peer Page </li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void createPeerPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity localCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test Widgets inside community")
														.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for " + testName)
												.build();


		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = localCommunity.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		localCommunity.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			localCommunity.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}

		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		localCommunity.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//create a peerpage
		logger.strongStep("Create a new Peer age");
		log.info("INFO: Create a new Peer Page");
		peerPage.create(ui);

		//Verify pages have been created
		logger.weakStep("Validate that the Peer Page has been created");
		log.info("INFO: Validate that the Peer Page has been created");
		Assert.assertTrue(ui.fluentWaitTextPresent(peerPage.getName()),
						  "ERROR:" + peerPage.getName() + "Page does not exist");
		
		logger.strongStep("Delete the Community that was created using API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();	
	}
	
	/**
	 * <ul>
	 * <li>Step: Add a child wiki page to a community Wiki</li>
	 * <li>Validate: Creation of a Child Page</li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void createChildPage() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();	

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							 .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();

		BaseWikiPage childPage = new BaseWikiPage.Builder("Child_Wiki_" + Helper.genDateBasedRand(), PageType.Child)
			 									 .tags("tag1, tag2")
			 									 .description("this is a test description for " + testName)
			 									 .build();
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}

		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
		
		//Wait for 20 seconds for https://swgjazz.ibm.com:8004/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/222425
		//Not ideal
		try {
			log.info("INFO: Wait for 20s to allow wigets to be fully loaded");
			Thread.sleep(20000);
		} catch (Exception e) {
			log.warn("INFO: Wait for 20s failed, BVT continue");
		}

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//create a childpage
		logger.strongStep("Create a new Child Page");
		log.info("INFO: Create a new Child Page");
		childPage.create(ui);

		//Verify pages have been created
		logger.weakStep("Validate that the Child Page exists");
		log.info("INFO: Validate that the Child Page exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(childPage.getName()),
						  "ERROR:" + childPage.getName() + "Page does not exist");

		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li>Step: Delete a community Wiki page</li>
	 * <li>Validate: Verify deletion of a Peer Page</li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void deletePeerPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							 .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + " Child page", PageType.Child)
												.tags("tag1, tag2")
												.description("this is a test description for " + testName)
												.build();
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}

		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}
		
		//Create WikiPage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);
		
		//Select the WikiPage just created
		logger.strongStep("Select the newly created Wiki Page");
		log.info("INFO: Select the newly created Wiki Page");
		ui.clickLink(WikisUI.getWikiPage(wikiPage));
	
		//Delete specified page
		logger.strongStep("Delete the Page");
		log.info("INFO: Delete the Page.");
		wikiPage.delete(ui);

		//Switch to trash view
		logger.strongStep("Switch to the Wiki 'Trash' view");
		log.info("INFO: Switch to the Wiki 'Trash' View");
		ui.clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Validate page made it into the trash
	    logger.weakStep("Validate that Trash header is present");
		log.info("INFO: Validate that Trash header is present");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.Trash),
						"ERROR: Trash header not present");
		
		logger.weakStep("Validate that Wiki Page is in the trash");
		log.info("INFO: Validate the Wiki Page is in the trash");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						"ERROR: Wiki Page is not in trash");	
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wikis");
		ui.endTest();				
	}
	
	/**
	 * <ul>
	 * <li>Step: Add a comment to community Wiki page</li>
	 * <li>Validate: Verify creation of comment to community Wiki page</li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void addCommentWikiPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 						     .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for " + testName)
												.build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
			
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis fromthe tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//create a peerpage
		logger.strongStep("Create a new Peer Page");
		log.info("INFO: Create a new Peer Page");
		peerPage.create(ui);

		//Add a comment and verify that the comment is added
		logger.strongStep("Add a comment");
		log.info("INFO: Add a comment and validate that the comment is added");
		ui.addComment(Data.getData().Comment_For_Public_Wiki);

		//Validate comment 
		logger.weakStep("Validate that the comment was added");
		log.info("INFO: Validate that the comment was added");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().Comment_For_Public_Wiki),
						 "ERROR: Unable to locate the comment");
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);
		
		//Logout of wiki
		ui.endTest();				
	}
	
	/**
	 * <ul>
	 * <li>Step: create a public wiki and add a page, edit the page using CKEditor</li>
	 * <li>Verify: verify that you can edit the page in the editor</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C">TTT Link</a></li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = { "regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"})
	public void editPageInCommunityWiki() throws Exception {

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

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("this is a test description for creating a Peer wiki page")
									.build();

		BaseWikiPage newwikiPage = new BaseWikiPage.Builder("New_" + wikiPage.getName(), PageType.Peer)
												.tags("updated_tag1, updated_tag2")
												.description("Updating the wiki page with new content")
												.build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//create a new wikipage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);

		//Edit the current page and verify that the page has being edited
		logger.strongStep("Edit the current Wiki Page");
		log.info("INFO: Edit the current Wiki Page");
		ui.editWikiPage(newwikiPage);

		logger.weakStep("Validate that the Page name has been changed");
		log.info("INFO: Validate that the Page name has been changed");
		Assert.assertTrue(ui.fluentWaitTextPresent(newwikiPage.getName()),
							"ERROR: Page name was not changed");

		logger.weakStep("Validate that the Page description has been changed");
		log.info("INFO: Validate that the Page description has been changed");
		Assert.assertTrue(ui.fluentWaitTextPresent(newwikiPage.getDescription()),
							"ERROR: Page description was not changed");
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of Wiki
		ui.endTest();		
	}
	
	/**
	 * <ul>
	 * <li>Step: Like a Community Wiki page</li>
	 * <li> Validate: Verify that community wiki page has been liked</li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void likeComWikiPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							 .access(defaultAccess)
		 							 .description("Test Widgets inside community")
									 .build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for " + testName)
												.build();
		
		//create community
		logger.strongStep("Create the Community using API");
		log.info("INFO: Create the Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);	
		cUI.waitForCommunityLoaded();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//create a peerpage
		logger.strongStep("Create a new Peer Page");
		log.info("INFO: Create a new Peer Page");
		peerPage.create(ui);

		//Like the page 
		logger.strongStep("Like the Wiki page");
		log.info("INFO: Like the Wiki page");
		ui.likeUnlikePage("Like");
		
		//Validate You like this message 
		logger.weakStep("Validate that the 'You like this' message appears");
		log.info("INFO: Validate that the 'You like this' message appears");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.LikeMessage),
						 "ERROR: 'You like this' message did not appear");
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);
		
		//Logout of wiki
		ui.endTest();				
	}
	
	/**
	 * <ul>
	 * <li>Step: Add a tag to a community Wiki page</li>
	 * <li>Validate: Verify that tag has been added to a community wiki page</li>
	 * </ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void addTagToComWikiPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test Widgets inside community")									 
									 .access(defaultAccess)
									 .build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for " + testName)
												.build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);	
		cUI.waitForCommunityLoaded();

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis fromthe tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI, 2);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		//create a peerpage
		logger.strongStep("Create a new Peer Page");
		log.info("INFO: Create a new Peer Page");
		peerPage.create(ui);

		//Add new tag to page and verify that the tag is added
		logger.strongStep("Add a new tag to the Wiki");
		log.info("INFO: Add a new tag to the Wiki");
		ui.addWikiTag(Data.getData().Tag_For_Public_Wiki);

		//check for tag 
		logger.weakStep("Validate that the new tag was added");
		log.info("INFO: Validate that the new tag was added");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUI.getTagElement(Data.getData().Tag_For_Public_Wiki)),
						 "ERROR: The new tag was not added");
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of wiki
		ui.endTest();				
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/	
	/**
	 *
	 *<ul>
	 *<li><B>Info: Test case to test that you can create a wiki page and a wiki bookmark</B></li>
	 *<li><B>Step: Create a new community.</B></li> 
	 *<li><B>Step: Add wiki widget to the community.</B></li> 
	 *<li><B>Step: Go to Wiki Tab.</B></li>
	 *<li><B>Step: Create a wiki page.</B></li>
	 *<li><B>Step: Go to Bookmarks Tab.</B></li> 
	 *<li><B>Step: Create a wiki bookmark</B></li>
	 *<li><B>Verify: Validate that the wiki page was created</B></li>
	 *<li><B>Verify: Validate that the wiki bookmark was created</B></li>
	 *<li><B>Clean up: Delete the Community.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void wikiPageCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//String testName = ui.startTest();
		String testName = ui.startTest() + Helper.genDateBasedRandVal();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		String url = Data.getData().commonURL;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(defaultAccess)
													.description("Test description for testcase " + testName)
													.build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("wiki test page for " + testName, PageType.Community)
												.description("this is a wiki page create for the "+testName+" for Smart Cloud smoke test")
												.build();
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
											.community(community)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription + testName)
											.build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();
				
		//Go to The Wikis Tab
		logger.strongStep("Click on the 'Wikis' tab");
		log.info("INFO: Go to the 'Wikis' Tab");
		ui.clickLink(CommunitiesUIConstants.leftNavWikis);
		
		//create a Wikipage
		logger.strongStep("Create a Wiki Page");
		log.info("INFO: Create a Wiki Page");
		wikiPage.create(ui);
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Go to the Bookmarks Tab
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Go to the tabbed navigation bookmarks tab");
			log.info("INFO: Go to the tabbed navigation bookmarks tab");
			Community_TabbedNav_Menu.BOOKMARK.select(cUI);

		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Go to the left navigation bookmarks tab");
			log.info("INFO: Go to the left navigation bookmarks tab");
			Community_LeftNav_Menu.BOOKMARK.select(cUI);
		}
		
		//Create A Wiki Bookmark
		logger.strongStep("Create a Wiki bookmark");
		log.info("INFO: Create a Wiki bookmark");
		dUI.create(bookmark);
		
						
		//delete community
		logger.strongStep("Delete the Community that was created via API");
		log.info("INFO: Delete the Community");
		apiComOwner.deleteCommunity(comAPI);
						
		ui.endTest();				
	
	}
	

	

	

	
	/**
	 * <ul>
	 * <li>Step: Add an attachment to a community Wiki widget</li>
	 * <li>Validate: Verify: Attachment added to community wiki widget</li>
	 * </ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void uploadAttachment() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseFile file = new BaseFile.Builder("Desert.jpg")
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							 .access(defaultAccess)
									 .description("Test Widgets inside community for " + testName)
									 .build();

		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);

		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		//Select Wikis from left navigation menu
		logger.strongStep("Select Wikis from the left navigation menu");
		log.info("INFO: Select Wikis from the left navigation menu");
		Community_LeftNav_Menu.WIKI.select(cUI);

		//Upload private file
		logger.strongStep("Upload a file to the Wiki");
		log.info("INFO: Upload a file to the Wiki");
		ui.uploadAttachment(file.getRename(), file.getExtension(), file.getName());
		
		logger.strongStep("Delete the Community that was created via API");
		apiComOwner.deleteCommunity(comAPI);

		//Logout of Wiki
		ui.endTest();

	}
	

	/**
	* <ul>
	* <li><B>Info: </B>Lock Public wiki</li>
 	* <li><B>Step: </B>Create a public wiki</li>
	* <li><B>Step: </B>Edit the wiki page</li>
	* <li><B>Verify: </B>Wiki name has changed</li>
	* </ul>
	* 
	* @throws Exception
	*/
	@Test(groups = { "level2", "bvt", "cnx8ui-level2" })
	public void lockWikiPage() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		// Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		// GUI
		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		// Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = "communities-tabbed-nav";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);

		String gk_lock = "wikis-enable-lock-on-page-editing";

		if (!ui.checkGKSetting(gk_lock))
		{
			// Skip this test case
			log.info("INFO: wikis lock is not enabled");
			return;
		}

		// create API handlers
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
			testUser.getPassword());

		// Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
			.description("Test Widgets inside community for " + testName).access(defaultAccess).build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer).tags("tag1, tag2")
			.description("this is a test description for creating a Peer wiki page").build();

		BaseWikiPage newwikiPage = new BaseWikiPage.Builder("New_" + wikiPage.getName(), PageType.Peer).tags("updated_tag1, updated_tag2")
			.description("Updating the wiki page with new content").build();

		// create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiComOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);

		// add widget if necessary
		logger.strongStep("Add the Wiki widget to the Community using API");
		if (!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI))
		{
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		cUI.waitForCommunityLoaded();

		// If GK is enabled use TabbedNav, else use LeftNav
		// Select Wikis from the nav menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Wikis from the tabbed navigation menu");
			log.info("INFO: Select Wikis from the tabbed navigation menu");
			Community_TabbedNav_Menu.WIKI.select(cUI,2);
		}
		else
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Wikis from the left navigation menu");
			log.info("INFO: Select Wikis from the left navigation menu");
			Community_LeftNav_Menu.WIKI.select(cUI);
		}

		// create a new wikipage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(ui);

		// Edit the current page and verify that the page has being edited
		logger.strongStep("Edit the current Wiki Page");
		log.info("INFO: Edit the current Wiki Page");
		log.info("INFO: Select the Edit Button");
		ui.clickLinkWait(WikisUIConstants.Edit_Button);

		// check for lock info
		logger.weakStep("Validate that the locking message was shown");
		log.info("INFO: Validate that the locking message was shown");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUI.getWikiPageLockingMessage(Data.getData().WikiPage_Locking_Message)),
			"ERROR: The locking was not shown");

		ui.clickLinkWait(WikisUIConstants.Save_and_Close_Link);
		// Logout of Wiki
		ui.endTest();
	}
}
