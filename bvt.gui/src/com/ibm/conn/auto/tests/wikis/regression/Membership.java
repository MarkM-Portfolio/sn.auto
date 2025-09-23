package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
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


public class Membership extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Membership.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	private APIWikisHandler apiOwner;
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
		testUser5 = cfg.getUserAllocator().getUser();
		testUser6 = cfg.getUserAllocator().getUser();
		testUser7 = cfg.getUserAllocator().getUser();
		
		
		//initialize API
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));


	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test a wiki after changing the wiki access rights to private.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as creator of wiki.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Members section.</li>
	*<li><B>Step: </B>Click on the Manage Access button to change access rights.</li>
	*<li><B>Step: </B>Select 'Wiki Members Only' radio button under 'Read access' section.</li>
	*<li><B>Step: </B>Select 'Wiki Editors and Members Only' radio button under 'Edit access' section.</li>
	*<li><B>Step: </B>Save the change.</li>
	*<li><B>Step: </B>Navigate to the main view.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm an Owner' view.</li>
	*<li><B>Step: </B>Select the ' I'm an Editor ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as different user.</li>
	*<li><B>Verify: </B>The wiki is not listed in Public Wikis view.</li>
	*<li><B>Step: </B>Logout of the wiki and login to wikis as creator.</li>
	*<li><B>Step: </B>Select the ' I'm an Owner ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add three members to the wiki.</li>
	*<li><B>Step: </B>Logout of the wiki and login to wikis as member just created.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm a Reader' view.</li>
	*<li><B>Step: </B>Logout of the wiki and login to wikis as member just created.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm a Editor' view.</li>
	*<li><B>Step: </B>Logout of the wiki and login to wikis as member just created.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm a Owner' view.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void wikiPublicToPrivate()throws Exception{
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
			
		//Load component and login as creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as creator");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Click on the members link
		logger.strongStep("Open the members section");
		log.info("INFO: Open the members section");
		ui.clickLinkWait(WikisUIConstants.Members_Link);
		
		//Click on the Manage Access button to open the form to change access rights
		logger.strongStep("Click on the Manage Access button to open the form to change access rights");
		log.info("INFO: Click on the Manage Access button to open the form to change access rights");
		ui.clickLinkWait(WikisUIConstants.ManageAccess);
		
		//Select 'Wiki Members Only' radio button under 'Read access' section
		logger.strongStep("Select 'Wiki Members Only' radio button under 'Read access' section");
		log.info("INFO: Select 'Wiki Members Only' radio button under 'Read access' section");
		ui.clickLinkWait(WikisUIConstants.WikiMembersOnly_RadioButton);
		
		//Select 'Wiki Editors and Members Only' radio button under 'Edit access' section
		logger.strongStep("Select 'Wiki Editors and Members Only' radio button under 'Edit access' section");
		log.info("INFO: Select 'Wiki Editors and Members Only' radio button under 'Edit access' section");
		ui.clickLinkWait(WikisUIConstants.WikiEditorsAndOwnersOnly_RadioButton);
		
		//Save the change
		logger.strongStep("Save the change");
		log.info("INFO: Save the change");
		ui.clickButton("Save");
		
		//Return to the main view
		logger.strongStep("Navigate to the main view");
		log.info("INFO: Navigate to the main view");
		ui.clickLinkWait(WikisUIConstants.WikisLinkInHeader);
		
		//Verify that the wiki is listed in 'I'm the Owner' view 
		logger.strongStep("Verify the wiki is listed in 'I'm an Owner' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Owner' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.OWNER, true);
		
		//view the wiki
		logger.strongStep("Select the ' I'm an Editor ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and Login again as a different user 
		logger.strongStep("Open browser and login to Wikis as: " + testUser5.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser5);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Click on Public Wikis link in the nav and verify wiki is not listed in the Public Wikis view
		logger.strongStep("Verify wiki is not listed in Public Wikis view");
		log.info("INFO: Verify wiki is not listed in Public Wikis view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.PUBLICWIKIS, false);
						
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and login to wikis as the creator of the wiki
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as creator");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser1);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//view the wiki
		logger.strongStep("Select the ' I'm an Owner ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
	
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add three more members as Owner,Editor and Reader to the wiki
		logger.strongStep("Add three members as Owner,Editor and Reader to the wiki");
		log.info("INFO: Add three members as Owner,Editor and Reader to the wiki");
		ui.addMember(new Member(WikiRole.OWNER, testUser5), wiki);
		ui.addMember(new Member(WikiRole.EDITOR, testUser6), wiki);
		ui.addMember(new Member(WikiRole.READER, testUser7), wiki);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and login to wikis as each member just added
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//click on 'I'm a Reader' view and verify the wiki is listed in 'I'm a Reader' view
		logger.strongStep("Verify wiki is listed in 'I'm a Reader' view");
		log.info("INFO: Verify wiki is listed in 'I'm a Reader' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.READER, true);
				
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and login to wikis as each member just added
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//click on 'I'm an Editor' view and verify that the wiki is listed in this view
		logger.strongStep("Verify the wiki is listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, true);
				
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and login to wikis as each member just added
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//click on 'I'm an Owner' view and verify that the wiki is listed in this view
		logger.strongStep("Verify the wiki is listed in 'I'm an Owner' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Owner' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.OWNER, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test a wiki after changing member access rights.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Click on the members link.</li>
	*<li><B>Verify: </B>Verify the membership rights of each member.</li>
	*<li><B>Step: </B>Change roles of existing wiki members.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as different user.</li>
	*<li><B>Verify: </B>The wiki is not listed in 'I'm an Owner' view.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm an Editor' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as different user.</li>
	*<li><B>Verify: </B>The wiki is not listed in 'I'm an Editor' view.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm a Reader' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as different user.</li>
	*<li><B>Verify: </B>The wiki is not listed in 'I'm a Reader' view.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm an Editor' view.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void changeMemberAccessRights() throws Exception{
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
			
		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		ui.waitForPageLoaded(driver);

		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Click on the members link
		logger.strongStep("Open the members section");
		log.info("INFO: Open the members section");
		ui.clickLinkWait(WikisUIConstants.Members_Link);
		
		//Verify the existing role of 1st member is as expected
		logger.strongStep("Verify the existing Role of 1st member is Owner");
		log.info("INFO: Verify the existing Role of 1st member is Owner");
		Assert.assertTrue(validateMemberRole(new Member(WikiRole.OWNER, testUser2)),
						  "ERROR: Role is not as expected");
		
		//Verify the existing role of 2nd member is as expected
		logger.strongStep("Verify the existing Role of 2nd member is Editor");
		log.info("INFO: Verify the existing Role of 2nd member is Editor");
		Assert.assertTrue(validateMemberRole(new Member(WikiRole.EDITOR, testUser3)),
						  "ERROR: Role is not as expected");
		
		//Verify the existing role of 3rd member is as expected
		logger.strongStep("Verify the existing Role of 3rd member is Reader");
		log.info("INFO: Verify the existing Role of 3rd member is Reader");
		Assert.assertTrue(validateMemberRole(new Member(WikiRole.READER, testUser4)),
						  "ERROR: Role is not as expeceted");
		
		//Change roles of existing wiki members
		logger.strongStep("Change roles of existing wiki members");
		log.info("INFO: Change roles of existing wiki members");
		Assert.assertTrue(ui.changeMemberRole(new Member(WikiRole.OWNER, testUser2), WikiRole.EDITOR),
		  				  "ERROR: Unable to change Role");	
		
		Assert.assertTrue(ui.changeMemberRole(new Member(WikiRole.EDITOR, testUser3), WikiRole.READER),
						  "ERROR: Unable to change Role");
		
		Assert.assertTrue(ui.changeMemberRole(new Member(WikiRole.READER, testUser4), WikiRole.EDITOR),
						  "ERROR: Unable to change Role");
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a different user
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		ui.waitForPageLoaded(driver);
		
		//Verify that the wiki is not listed in 'I'm an Owner' view
		logger.strongStep("Verify the wiki is not listed in 'I'm an Owner' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm an Owner' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.OWNER, false);
		
		//Verify that the wiki is listed in 'I'm an Editor' view
		logger.strongStep("Verify the wiki is listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, true);
				
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a different user
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		ui.waitForPageLoaded(driver);
		ui.waitForJQueryToLoad(driver);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is not listed in 'I'm an Editor' view
		logger.strongStep("Verify the wiki is not listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, false);
		
		//Verify that the wiki is listed in 'I'm a Reader' view
		logger.strongStep("Verify the wiki is listed in 'I'm a Reader' view");
		log.info("INFO: Verify the wiki is listed in 'I'm a Reader' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.READER, true);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login again as a different user
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		ui.waitForPageLoaded(driver);
		ui.waitForJQueryToLoad(driver);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is not listed in 'I'm a Reader' view
		logger.strongStep("Verify the wiki is not listed in 'I'm a Reader' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm a Reader' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.READER, false);
		
		//Verify that the wiki is listed in 'I'm an Editor' view
		logger.strongStep("Verify the wiki is listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests a wiki is not listed in any view after adding and deleting members from the wiki.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add three members to the wiki.</li>
	*<li><B>Step: </B>Delete some members from the wiki.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the deleted members.</li>
	*<li><B>Verify: </B>The wiki is not listed in 'I'm an Owner' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the deleted members.</li>	
	*<li><B>Verify: </B>The wiki is not listed in 'I'm an Editor' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the deleted members.</li>	
	*<li><B>Verify: </B>The wiki is not listed in 'I'm a Reader' view.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void AddAndDeleteMembers()throws Exception{
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
			
		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add three more members
		logger.strongStep("Add three members as Owner,Editor and Reader to the wiki");
		log.info("INFO: Add three members as Owner,Editor and Reader to the wiki");
		ui.addMember(new Member(WikiRole.OWNER, testUser5), wiki);
		ui.addMember(new Member(WikiRole.EDITOR, testUser6), wiki);
		ui.addMember(new Member(WikiRole.READER, testUser7), wiki);
		
		//Delete some members from the wiki
		logger.strongStep("Delete all members just added from the wiki");
		log.info("INFO: Delete all members just added from the wiki");
		ui.removeMember(new Member(WikiRole.OWNER, testUser5), wiki);
		ui.removeMember(new Member(WikiRole.OWNER, testUser6), wiki);
		ui.removeMember(new Member(WikiRole.OWNER, testUser7), wiki);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login as one of the deleted members
		logger.strongStep("Open browser and login to Wikis as: " + testUser5.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser5);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is not listed in 'I'm an Owner' view
		logger.strongStep("Verify the wiki is not listed in 'I'm an Owner' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm an Owner' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.OWNER, false);
						
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login as one of the deleted members
		logger.strongStep("Open browser and login to Wikis as: " + testUser6.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser6);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is not listed in 'I'm an Editor' view
		logger.strongStep("Verify the wiki is not listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, false);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login as one of the deleted members
		logger.strongStep("Open browser and login to Wikis as: " + testUser7.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser7);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is not listed in 'I'm a Reader' view
		logger.strongStep("Verify the wiki is not listed in 'I'm a Reader' view");
		log.info("INFO: Verify the wiki is not listed in 'I'm a Reader' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.READER, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests a wiki in different views when login as diffrent members.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add three members to the wiki.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the added members.</li>
	*<li><B>Verify: </B>The wiki is listed in 'I'm an Owner' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the added members.</li>	
	*<li><B>Verify: </B>The wiki is listed in 'I'm an Editor' view.</li>
	*<li><B>Step: </B>Logout of the wiki and Login to wikis as one of the added members.</li>	
	*<li><B>Verify: </B>The wiki is listed in 'I'm a Reader' view.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void VerifyMembersView()throws Exception{
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
			
		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add three more members
		logger.strongStep("Add three members to the wiki");
		log.info("INFO: Add three members to the wiki");
		ui.addMember(new Member(WikiRole.OWNER, testUser5), wiki);
		ui.addMember(new Member(WikiRole.EDITOR, testUser6), wiki);
		ui.addMember(new Member(WikiRole.READER, testUser7), wiki);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login to wikis as one of the added members
		logger.strongStep("Open browser and login to Wikis as: " + testUser5.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser5);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(WikisUIConstants.StartWikiBtn);
		
		//Verify that the wiki is listed in 'I'm an Owner' view
		logger.strongStep("Verify the wiki is listed in 'I'm an Owner' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Owner' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.OWNER, true);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login to wikis as one of the added members
		logger.strongStep("Open browser and login to Wikis as: " + testUser6.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser6);
		ui.waitForPageLoaded(driver);
		
		//Verify that the wiki is listed in 'I'm an Editor' view
		logger.strongStep("Verify the wiki is listed in 'I'm an Editor' view");
		log.info("INFO: Verify the wiki is listed in 'I'm an Editor' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.EDITOR, true);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Login to wikis as one of the added members
		logger.strongStep("Open browser and login to Wikis as: " + testUser7.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser7);
		ui.waitForPageLoaded(driver);
		
		//Verify that the wiki is listed in 'I'm a Reader' view
		logger.strongStep("Verify the wiki is listed in 'I'm a Reader' view");
		log.info("INFO: Verify the wiki is listed in 'I'm a Reader' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.READER, true);
		
		ui.endTest();
	}
	
	/**
	 * confirmWikiView -
	 * @param Wiki_Title
	 * @param ViewName
	 * @param ViewName1
	 * @param WikiPresent
	 * @throws Exception
	 */
	public void confirmWikiView(BaseWiki wiki, Wiki_LeftNav_Menu view, boolean WikiPresent) throws Exception{
		
		log.info("INFO: Opening the view: " + view.getMenuItemText());
		ui.fluentWaitTextPresent(view.getMenuItemText());
		
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

	/**
	 * validateMemberRole -
	 * @param member
	 * @return boolean
	 */
	public boolean validateMemberRole(Member member){
		
		boolean found = false;
		log.info("INFO: Validate Member role");
		List<Element> memberList = driver.getVisibleElements(WikisUIConstants.memberWebElement);

		log.info("INFO: Looking for " + member.getUser().getDisplayName());
		for (ListIterator<Element> iter = memberList.listIterator(); iter.hasNext(); ) {
			Element element = iter.next();
			if(element.getText().contains(member.getUser().getDisplayName() + "\n")){
				log.info("INFO: Found User checking role");
				if (element.getText().contains(member.getRole().toString())){
					log.info("INFO: Roll is correct");
					found = true;
				}
				//User was found break out
				break;
			}
		}

		return found;
	}
	

	
}

