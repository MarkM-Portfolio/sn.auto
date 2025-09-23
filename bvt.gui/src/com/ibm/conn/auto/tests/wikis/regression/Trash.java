package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.WikisUI;

public class Trash extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Trash.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4;
	private APIWikisHandler apiOwner;
	private String serverURL;
	private BaseWikiPage peerPageNav;
	private BaseWikiPage peerPage;
	private BaseWikiPage childPage;
	
	/*
	 * This test case is to verify that pages can be added and then deleted
	 * Trash can is populated with the deleted pages
	 * Owners can delete/restore the pages entirely
	 * Editors can only restore
	 * Readers can view
	 * 
	 * Created By: Conor Pelly
	 * Date: 31/05/2012
	 */
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();		
		
		//initialize API
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());

		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));

		//Generic pages
		peerPageNav = new BaseWikiPage.Builder("Via_Nav" + Helper.genDateBasedRand(), PageType.NavPage)
									  .tags("tag1, tag2")
									  .description("this is a test description for creating a Peer wiki page")
									  .build();

		peerPage = new BaseWikiPage.Builder("Peer_Wiki_" + Helper.genDateBasedRand(), PageType.Peer)
								   .tags("tag1, tag2")
								   .description("this is a test description for creating a Peer wiki page")
								   .build();


		childPage = new BaseWikiPage.Builder("Child_Wiki_" + Helper.genDateBasedRand(), PageType.Child)
		 							.tags("tag1, tag2")
		 							.description("this is a test description for creating a Child wiki page")
		 							.build();


	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);

	}

	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that pages can be added,deleted and restored when login as a creator.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as a creator.</li>		
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a page using 'New Page' link in the nav.</li>	
	*<li><B>Step: </B>Add a peer page.</li>	
	*<li><B>Step: </B>Add a child page.</li>
	*<li><B>Step: </B>Select the Peer Nav Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>	
	*<li><B>Step: </B>Select the Peer Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>
	*<li><B>Step: </B>Switch to the Wiki Trash View.</li>	
	*<li><B>Verify: </B>The peer wiki page is in the trash view.</li>
	*<li><B>Verify: </B>The peer nav wiki page is in the trash view.</li>
	*<li><B>Step: </B>Restore peer wiki page .</li>
	*<li><B>Step: </B>Return to the wiki view.</li>	
	*<li><B>Verify: </B>The page is now restored successfully.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void pagesAsCreator() throws Exception {
		
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
			
		//Load component and login to wikis as creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as a creator");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add a page using 'New Page' link in the nav
		logger.strongStep("Add a page using 'New Page' link in the nav");
		log.info("INFO: Add a page using 'New Page' link in the nav");
		peerPageNav.create(ui);

		//Add a peer page
		logger.strongStep("Add a peer page");
		log.info("INFO: Add a peer page");
		peerPage.create(ui);
		
		//Add a child page
		logger.strongStep("Add a child page");
		log.info("INFO: Add a child page");
		childPage.create(ui);

		//Select the Peer Nav Wiki Page
		logger.strongStep("Select the wikipage " + peerPageNav.getName());
		log.info("INFO: Select the wikipage " + peerPageNav.getName());
		ui.openWikiPageNav(peerPageNav);
	
		//Move the peer nav wiki page to trash 
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPageNav.delete(ui);

		//Select the Peer Wiki Page
		logger.strongStep("Select the wikipage " + peerPage.getName());
		log.info("INFO: Select the wikipage " + peerPage.getName());
		ui.openWikiPageNav(peerPage);

		//Move the Peer wiki page to trash
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPage.delete(ui);

		//Switch to trash view
		logger.strongStep("Switch to the Wiki Trash View");
		log.info("INFO: Switch to the Wiki Trash View");
		ui.fluentWaitPresent(WikisUIConstants.Trash_Link);
		ui.clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Validate that the peer wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPage.getName());	
		
		//Validate that the peer nav wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPageNav.getName());
		
		//Restore peer wiki page 
		logger.strongStep("Restore a page");
		log.info("INFO: Restore a page");
		peerPage.restore(ui);
		
		//return to the wiki view
		logger.strongStep("Return to the wiki view");
		log.info("INFO: Return to the wiki view");
		ui.clickLinkWait("css=div.lotusBreadcrumbs a");

		//Verify that the page is now Restored
		logger.strongStep("Verify the page is now restored successfully");
		log.info("INFO: Verify the page is now restored successfully");		
		driver.isTextPresent(peerPage.getName());

		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that pages can be added,deleted and restored when login as an owner.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as an owner.</li>		
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a page using 'New Page' link in the nav.</li>	
	*<li><B>Step: </B>Add a peer page.</li>	
	*<li><B>Step: </B>Add a child page.</li>
	*<li><B>Step: </B>Select the Peer Nav Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>	
	*<li><B>Step: </B>Select the Peer Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>
	*<li><B>Step: </B>Switch to the Wiki Trash View.</li>	
	*<li><B>Verify: </B>The peer wiki page is in the trash view.</li>
	*<li><B>Verify: </B>The peer nav wiki page is in the trash view.</li>
	*<li><B>Step: </B>Restore peer wiki page .</li>
	*<li><B>Step: </B>Return to the wiki view.</li>	
	*<li><B>Verify: </B>The page is now restored successfully.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void pagesAsOwner() throws Exception {
		
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
			
		//Load component and login to wikis as an owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as an owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add a page using 'New Page' link in the nav
		logger.strongStep("Add a page using 'New Page' link in the nav");
		log.info("INFO: Add a page using 'New Page' link in the nav");
		peerPageNav.create(ui);

		//Add a peer page
		logger.strongStep("Add a peer page");
		log.info("INFO: Add a peer page");
		peerPage.create(ui);
		
		//Add a child page
		logger.strongStep("Add a child page");
		log.info("INFO: Add a child page");
		childPage.create(ui);

		//Select the Peer Nav Wiki Page
		logger.strongStep("Select the wikipage " + peerPageNav.getName());
		log.info("INFO: Select the wikipage " + peerPageNav.getName());
		ui.openWikiPageNav(peerPageNav);
	
		//Move the peer nav wiki page to trash 
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPageNav.delete(ui);

		//Select the Peer Wiki Page
		logger.strongStep("Select the wikipage " + peerPage.getName());
		log.info("INFO: Select the wikipage " + peerPage.getName());
		ui.openWikiPageNav(peerPage);

		//Move the Peer wiki page to trash
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPage.delete(ui);

		//Switch to trash view
		logger.strongStep("Switch to the Wiki Trash View");
		log.info("INFO: Switch to the Wiki Trash View");
		ui.fluentWaitPresent(WikisUIConstants.Trash_Link);
		ui.clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Validate that the peer wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPage.getName());	
		
		//Validate that the peer nav wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPageNav.getName());
			
		//Restore peer wiki page 
		logger.strongStep("Restore a page");
		log.info("INFO: Restore a page");
		peerPage.restore(ui);
		
		//return to the wiki view
		logger.strongStep("Return to the wiki view");
		log.info("INFO: Return to the wiki view");
		ui.clickLinkWait("css=div.lotusBreadcrumbs a");

		//Verify that the page is now Restored
		logger.strongStep("Verify that the page is now restored successfully");
		log.info("INFO: Verify that the page is now restored successfully");		
		driver.isTextPresent(peerPage.getName());

		ui.endTest();
				
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that pages can be added,deleted and restored when login as an owner.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as an editor.</li>
	*<li><B>Step: </B>Navigate to the 'I'm an Editor' view.</li>	
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a page using 'New Page' link in the nav.</li>	
	*<li><B>Step: </B>Add a peer page.</li>	
	*<li><B>Step: </B>Add a child page.</li>
	*<li><B>Step: </B>Select the Peer Nav Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>	
	*<li><B>Step: </B>Select the Peer Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>
	*<li><B>Step: </B>Switch to the Wiki Trash View.</li>	
	*<li><B>Verify: </B>The peer wiki page is in the trash view.</li>
	*<li><B>Verify: </B>The peer nav wiki page is in the trash view.</li>
	*<li><B>Step: </B>Restore peer wiki page .</li>
	*<li><B>Step: </B>Return to the wiki view.</li>	
	*<li><B>Verify: </B>The page is now restored successfully.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void pagesAsEditor() throws Exception {
		
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
			
		//Load component and login to wikis as an editor
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as an editor");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);

		//Navigate to the 'I'm an Editor' view
		logger.strongStep("Select the ' I'm an Editor ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add a page using 'New Page' link in the nav
		logger.strongStep("Add a page using 'New Page' link in the nav");
		log.info("INFO: Add a page using 'New Page' link in the nav");
		peerPageNav.create(ui);

		//Add a peer page
		logger.strongStep("Add a peer page");
		log.info("INFO: Add a peer page");
		peerPage.create(ui);
		
		//Add a child page
		logger.strongStep("Add a child page");
		log.info("INFO: Add a child page");
		childPage.create(ui);

		//Select the Peer Nav Wiki Page
		logger.strongStep("Select the wikipage " + peerPageNav.getName());
		log.info("INFO: Select the wikipage " + peerPageNav.getName());
		ui.openWikiPageNav(peerPageNav);
	
		//Move the peer nav wiki page to trash 
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPageNav.delete(ui);

		//Select the Peer Wiki Page
		logger.strongStep("Select the wikipage " + peerPage.getName());
		log.info("INFO: Select the wikipage " + peerPage.getName());
		ui.openWikiPageNav(peerPage);

		//Move the Peer wiki page to trash
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPage.delete(ui);

		//Switch to trash view
		logger.strongStep("Switch to the Wiki Trash View");
		log.info("INFO: Switch to the Wiki Trash View");
		ui.fluentWaitPresent(WikisUIConstants.Trash_Link);
		ui.clickLinkWait(WikisUIConstants.Trash_Link);
		
		//Validate that the peer wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPage.getName());	
		
		//Validate that the peer nav wiki page is in the trash view
		logger.strongStep("Verify the page is in the trash view");
		log.info("INFO: Verify the page is in the trash view");
		ui.fluentWaitPresent(WikisUIConstants.Trash);
		ui.fluentWaitTextPresent(peerPageNav.getName());
		
		//Restore peer wiki page 
		logger.strongStep("Restore a page");
		log.info("INFO: Restore a page");
		peerPage.restore(ui);
		
		//return to the wiki view
		logger.strongStep("Return to the wiki view");
		log.info("INFO: Return to the wiki view");
		ui.clickLinkWait("css=div.lotusBreadcrumbs a");

		//Verify that the page is now Restored
		logger.strongStep("Verify that the page is now restored successfully");
		log.info("INFO: Verify that the page is now restored successfully");		
		driver.isTextPresent(peerPage.getName());

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that a Reader can not perform restore, delete and Empty trash actions.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as a reader.</li>
	*<li><B>Step: </B>Select the ' I'm an Reader ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Trash View.</li>	
	*<li><B>Verify: </B>Reader can not perform any actions on pages in trash.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void pagesAsReader() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.editAccess(EditAccess.EditorsAndOwners)
									.addMembers(members)
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki: " + wiki.getName() + " using API");
		log.info("INFO: Create wiki: " + wiki.getName() + " using API");
		wiki.createAPI(apiOwner);
			
		//Load component and login to wiki as a reader
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as a reader");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser4);

		//Switch to reader view
		logger.strongStep("Select the ' I'm a Reader ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Click on the trash link
		logger.strongStep("Navigate to the Trash View");
		log.info("INFO: Navigate to the Trash View");
		ui.clickLink("css=a:contains(Trash)");
		
		//Verify that reader can not perform any actions on pages in trash
		logger.strongStep("Verify that the 'Restore', 'Delete' and 'Empty Trash' links are not available ");
		log.info("INFO: Verify that the 'Restore', 'Delete' and 'Empty Trash' links are not available");
		driver.isTextNotPresent("Restore");
		driver.isTextNotPresent("Delete");
		driver.isTextNotPresent("Empty Trash");		
		
		ui.endTest();
				
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to verify that owner can add pages,move the pages to trash and can empty the trash.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as an owner.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a page using 'New Page' link in the nav.</li>	
	*<li><B>Step: </B>Add a peer page.</li>	
	*<li><B>Step: </B>Add a child page.</li>
	*<li><B>Step: </B>Select the Peer Nav Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>	
	*<li><B>Step: </B>Select the Peer Wiki Page.</li>
	*<li><B>Step: </B>Move the page to trash.</li>
	*<li><B>Verify: </B>Empty the trash and verify the trash is empty.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void emptyTrash() throws Exception {
		
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
			
		//Load component and login to wikis as an owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as an owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add a page using 'New Page' link in the nav
		logger.strongStep("Add a page using 'New Page' link in the nav");
		log.info("INFO: Add a page using 'New Page' link in the nav");
		peerPageNav.create(ui);

		//Add a peer page
		logger.strongStep("Add a peer page");
		log.info("INFO: Add a peer page");
		peerPage.create(ui);
		
		//Add a child page
		logger.strongStep("Add a child page");
		log.info("INFO: Add a child page");
		childPage.create(ui);

		//Select the Peer Nav Wiki Page
		logger.strongStep("Select the wikipage " + peerPageNav.getName());
		log.info("INFO: Select the wikipage " + peerPageNav.getName());
		ui.openWikiPageNav(peerPageNav);
	
		//Move the peer nav wiki page to trash 
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPageNav.delete(ui);

		//Select the Peer Wiki Page
		logger.strongStep("Select the wikipage " + peerPage.getName());
		log.info("INFO: Select the wikipage " + peerPage.getName());
		ui.openWikiPageNav(peerPage);

		//Move the Peer wiki page to trash
		logger.strongStep("Move the page to trash.");
		log.info("INFO: Move the page to trash.");
		peerPage.delete(ui);

		//Empty the trash and verify the trash is empty
		logger.strongStep("Empty trash and Verify the trash is empty");
		log.info("INFO: Empty trash and Verify the trash is empty");
		ui.emptyTheTrashCan(wiki.getName());
		
		ui.endTest();
				
	}

	
}

