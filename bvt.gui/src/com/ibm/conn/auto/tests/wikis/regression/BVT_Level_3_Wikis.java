package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
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
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_Level_3_Wikis extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_3_Wikis.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;
	private User testUser2;
	private User testUser3;
	private User testUser4;
	private APIWikisHandler apiOwner;
	private String serverURL;
	private List<Member> members;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		//creator
		testUser1 = cfg.getUserAllocator().getUser();
		//owner
		testUser2 = cfg.getUserAllocator().getUser();
		//editor
		testUser3 = cfg.getUserAllocator().getUser();
		//reader
		testUser4 = cfg.getUserAllocator().getUser();

		//initialize API
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
				
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the Wiki general GUI.
	*<li><B>Step: </B>Create a wiki via API. 
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Verify: </B>The wiki's name and the Breadcrumbs section are visible.
	*<li><B>Verify: </B>The tagging UI is working expectedly by verifying 'None' is displayed for Tags and the Add Tags link is visible.
	*<li><B>Verify: </B>The recommendations are correct and the Like button is visible.
	*<li><B>Verify: </B>The buttons - Edit, Page Actions, Following Actions and Wiki Actions are visible.
	*<li><B>Verify: </B>The inline tabs - Comments, Versions, Attachments and About are visible.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void validateWikiGUI() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Verify wiki name & breadcrumbs
		logger.strongStep("Verify the wiki's name and the Breadcrumbs section are visible");
		log.info("INFO: Validate that the wiki's name and the Breadcrumbs section are visible");
		Assert.assertTrue(driver.isTextPresent(wiki.getName()), 
						  "ERROR: Wiki name isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.All_Breadcrumb_Text),
						  "ERROR: Breadcrumbs aren't visible");

		//Verify tagging UI
		logger.strongStep("Verify that the text 'None' is displayed next to Tags and the Add Tags link is visible");
		log.info("INFO: Validate the tagging UI by making sure 'None' is displayed for Tags and the Add Tags link is visible");
		Assert.assertTrue(driver.isTextPresent("None"), 
						  "ERROR: Tag Name isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Add_tags_Link),
						  "ERROR: Add tags link isn't visible");

		//Verify recommendations
		logger.strongStep("Verify the Like button is visible");
		log.info("INFO: Validate that recommendations are correct and the Like button is visible");
		Assert.assertTrue(driver.isTextPresent("Like"), 
						  "ERROR: Recommendations isn't correct");

		//Verify that all page buttons are visible
		logger.strongStep("Verify the buttons - Edit, Page Actions, Following Actions and Wiki Actions are visible");
		log.info("INFO: Validate that the buttons - Edit, Page Actions, Following Actions and Wiki Actions are visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Edit_Button),
						  "ERROR: Edit button is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button),
						  "ERROR: Page Actions button is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Follow_Button),
						  "ERROR: Follow button is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Wiki_Actions_Button),
						  "ERROR: Wiki Actions button is missing");

		//Verify all inline tabs are visible
		logger.strongStep("Verify the inline tabs - Comments, Versions, Attachments and About are visible");
		log.info("INFO: Validate that the inline tabs - Comments, Versions, Attachments and About are visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Comments_Tab),
						  "ERROR: Comments tab is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Versions_Tab),
						  "ERROR: Versions tab is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Attachments_Tab),
						  "ERROR: Attachments tab is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.About_Tab),
						  "ERROR: About tab is missing");

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the Edit Form for a wiki when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki via API. 
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Click on the Edit button to modify the wiki.
	*<li><B>Verify: </B>The Edit Form opens up for the wiki's owner.
	*<li><B>Verify: </B>The Edit Form contains the inline tabs - Rich Text, HTML Source and Preview.
	*<li><B>Verify: </B>The 'Tags:' heading is visible and the Add tags link also appears.
	*</ul>
	*/
	@Test (groups = {"regression" })
	public void validateEditFormOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_Page" + Helper.genDateBasedRand(), PageType.NavPage)
												.tags("tag1")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
			
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		logger.strongStep("Click on the Edit button to modify the wiki");
		log.info("INFO: Modify the wiki by clicking on the Edit button");
		ui.clickLink(WikisUIConstants.Edit_Button);

		//Validate Edit Form UI for wiki owner
		logger.strongStep("Verify the Edit Form opens up for the wiki's owner and contains the inline tabs - Rich Text, HTML Source and Preview");
		log.info("INFO: Validate that the Edit Form opens up for the wiki's owner and contains the inline tabs - Rich Text, HTML Source and Preview");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Rich_Text_Tab),
						  "ERROR: Rich Text tab isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.HTML_Source_Tab),
						  "ERROR: HTML Source tab isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Preview_Tab),
						  "ERROR: Preview tab isn't visible");

		logger.strongStep("Verify the 'Tags:' heading is visible and the Add tags link also appears");
		log.info("INFO: Validate that the 'Tags:' heading is visible and the Add tags link also appears");
		Assert.assertTrue(driver.isTextPresent("Tags:"), 
						  "ERROR: Tags heading is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Add_tags_Link),
						  "ERROR: Add remove link not present");

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the Edit Form for a wiki when logged in as the wiki's editor.
	*<li><B>Step: </B>Create a wiki via API. 
	*<li><B>Step: </B>Login as the editor and open the wiki.
	*<li><B>Step: </B>Click on the Edit button to modify the wiki.
	*<li><B>Verify: </B>The Edit Form opens up for the wiki's editor.
	*<li><B>Verify: </B>The Edit Form contains the inline tabs - Rich Text, HTML Source and Preview.
	*<li><B>Verify: </B>The 'Tags:' heading is visible and the Add tags link also appears.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void validateEditFormEditor() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)						
									.description("Description for test " + testName)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_Page" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1")
												.description("this is a test description for creating a Peer wiki page")
												.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
			
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as editor: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Editor' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		logger.strongStep("Click on the Edit button to modify the wiki");
		log.info("INFO: Modify the wiki by clicking on the Edit button");
		ui.clickLink(WikisUIConstants.Edit_Button);

		//Validate Edit Form UI for wiki editor
		logger.strongStep("Verify the Edit Form opens up for the wiki's editor and contains the inline tabs - Rich Text, HTML Source and Preview");
		log.info("INFO: Validate that the Edit Form opens up for the wiki's editor and contains the inline tabs - Rich Text, HTML Source and Preview");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Rich_Text_Tab),
						  "ERROR: Rich Text tab isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.HTML_Source_Tab),
						  "ERROR: HTML Source tab isn't visible");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Preview_Tab),
						   "ERROR: Preview tab isn't visible");
		
		logger.strongStep("Verify the 'Tags:' heading is visible and the Add tags link also appears");
		log.info("INFO: Validate that the 'Tags:' heading is visible and the Add tags link also appears");
		Assert.assertTrue(driver.isTextPresent("Tags:"), 
		  				  "ERROR: Tags heading is missing");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Add_tags_Link),
		  				  "ERROR: Add remove link not present");
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of recommendations when logged in as a wiki's owner.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Recommend the wiki by clicking on the Like button.
	*<li><B>Verify: </B>Recommendation UI is updated and the text 'You like this' appears.
	*<li><B>Step: </B>Click on the Unlike button.
	*<li><B>Verify: </B>Recommendation UI is updated and the Like button reappears.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void addRecommendationOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		// Load the component
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);

		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Recommend the current page
		logger.strongStep("Hit the Like button to recommend the wiki");
		log.info("INFO: Recommend the wiki by clicking on the Like button");
		ui.clickLink(WikisUIConstants.Recommendations_Info);

		//Verify that recommendation UI is correct after a user has recommended the page
		logger.strongStep("Verify the text 'You like this' appears after liking the wiki");
		log.info("INFO: Validate that the text 'You like this' appears after liking the wiki");
		Assert.assertTrue(driver.isTextPresent("You like this"),
		  				  "ERROR: Recommendation text is incorrect");
		
		//Undo Recommendation
		logger.strongStep("Click on the Unlike button");
		log.info("INFO: Unlike the wiki by clicking on the Unlike button");
		ui.clickLink(WikisUIConstants.UndoRecommendation);
		
		logger.strongStep("Verify the Like button reappears once the wiki is unliked");
		log.info("INFO: Validate that the Like button reappears once the Unlike button is clicked");
		Assert.assertTrue(ui.fluentWaitElementVisible(WikisUIConstants.Recommendations_Info),
		  				  "ERROR: Recommendation link is not visible");
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of recommendations when logged in as a wiki's editor.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the editor and open the wiki.
	*<li><B>Step: </B>Recommend the wiki by clicking on the Like button.
	*<li><B>Verify: </B>Recommendation UI is updated and the text 'You like this' appears.
	*<li><B>Step: </B>Click on the Unlike button.
	*<li><B>Verify: </B>Recommendation UI is updated and the Like button reappears.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void addRecommendationEditor() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		// Load the component
		logger.strongStep("Open Wikis component and login as editor: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);

		//view the wiki
		logger.strongStep("Click on the 'I'm an Editor' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Recommend the current page
		logger.strongStep("Hit the Like button to recommend the wiki");
		log.info("INFO: Recommend the wiki by clicking on the Like button");
		ui.clickLink(WikisUIConstants.Recommendations_Info);

		//Verify that recommendation UI is correct after a user has recommended the page
		logger.strongStep("Verify the text 'You like this' appears after liking the wiki");
		log.info("INFO: Validate that the text 'You like this' appears after liking the wiki");
		Assert.assertTrue(driver.isTextPresent("You like this"),
		  				  "ERROR: Recommendation text is incorrect");
		
		//Undo Recommendation
		logger.strongStep("Click on the Unlike button");
		log.info("INFO: Unlike the wiki by clicking on the Unlike button");
		ui.clickLink(WikisUIConstants.UndoRecommendation);
		
		logger.strongStep("Verify the Like button reappears once the wiki is unliked");
		log.info("INFO: Validate that the Like button reappears once the Unlike button is clicked");
		Assert.assertTrue(ui.fluentWaitElementVisible(WikisUIConstants.Recommendations_Info),
		  				  "ERROR: Recommendation link is not visible");
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of recommendations when logged in as a wiki's reader.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the reader and open the wiki.
	*<li><B>Step: </B>Recommend the wiki by clicking on the Like button.
	*<li><B>Verify: </B>Recommendation UI is updated and the text 'You like this' appears.
	*<li><B>Step: </B>Click on the Unlike button.
	*<li><B>Verify: </B>Recommendation UI is updated and the Like button reappears.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void addRecommendationReader() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		// Load the component
		logger.strongStep("Open Wikis component and login as reader: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser4);

		//view the wiki
		logger.strongStep("Click on the 'I'm a Reader' view in the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Recommend the current page
		logger.strongStep("Hit the Like button to recommend the wiki");
		log.info("INFO: Recommend the wiki by clicking on the Like button");
		ui.clickLink(WikisUIConstants.Recommendations_Info);

		//Verify that recommendation UI is correct after a user has recommended the page
		logger.strongStep("Verify the text 'You like this' appears after liking the wiki");
		log.info("INFO: Validate that the text 'You like this' appears after liking the wiki");
		Assert.assertTrue(driver.isTextPresent("You like this"),
		  				  "ERROR: Recommendation text is incorrect");
		
		//Undo Recommendation
		logger.strongStep("Click on the Unlike button");
		log.info("INFO: Unlike the wiki by clicking on the Unlike button");
		ui.clickLink(WikisUIConstants.UndoRecommendation);
		
		logger.strongStep("Verify the Like button reappears once the wiki is unliked");
		log.info("INFO: Validate that the Like button reappears once the Unlike button is clicked");
		Assert.assertTrue(ui.fluentWaitElementVisible(WikisUIConstants.Recommendations_Info),
		  				  "ERROR: Recommendation link is not visible");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of tags when logged in as a wiki's owner.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Click on the Add Tags link and add a tag to the wiki.
	*<li><B>Verify: </B>The new tag appears on the wiki's Welcome page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void addTagOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		// Load the component
		logger.strongStep("Open Wikis component and login as owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);

		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add new tag to the wiki
		logger.strongStep("Click on the Add Tags link and add the tag: " + Data.getData().TagForPrivateWiki + " to the wiki");
		log.info("INFO: Add the tag: " + Data.getData().TagForPrivateWiki + " to the wiki");
		ui.addWikiTag(Data.getData().TagForPrivateWiki);
		
		//Verify tag has been added
		logger.strongStep("Verify the new tag appears on the wiki's Welcome page");
		log.info("INFO: Validate that the tag is added to the wiki");
		Assert.assertTrue(driver.isTextPresent(Data.getData().TagForPrivateWiki));
		
		ui.endTest();
	}	

	/**
	*<ul>
	*<li><B>Info: </B>Tests the addition of tags when logged in as a wiki's editor.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the editor and open the wiki.
	*<li><B>Step: </B>Click on the Add Tags link and add a tag to the wiki.
	*<li><B>Verify: </B>The new tag appears on the wiki's Welcome page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void addTagEditor() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.addMembers(members)
									.readAccess(ReadAccess.WikiOnly)
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
		
		// Load the component
		logger.strongStep("Open Wikis component and login as editor: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);

		//view the wiki
		logger.strongStep("Click on the 'I'm an Editor' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Add new tag to the wiki
		logger.strongStep("Click on the Add Tags link and add the tag: " + Data.getData().TagForPrivateWiki + " to the wiki");
		log.info("INFO: Add the tag: " + Data.getData().TagForPrivateWiki + " to the wiki");
		ui.addWikiTag(Data.getData().TagForPrivateWiki);
		
		//Verify tag has been added
		logger.strongStep("Verify the new tag appears on the wiki's Welcome page");
		log.info("INFO: Validate that the tag is added to the wiki");
		Assert.assertTrue(driver.isTextPresent(Data.getData().TagForPrivateWiki));
		
		ui.endTest();
	}	

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a wiki can be edited and its name changed when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki via API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Click on the Edit button and change the wiki's name.
	*<li><B>Verify: </B>The new name appears on the wiki's Welcome page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void editWikiOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)								
									.description("Description for test " + testName)
									.build();
		
		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
				
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Change wiki name
		logger.strongStep("Click on the Edit button and change the wiki's name");
		log.info("INFO: Edit the wiki and change its name");
		wiki.setName("New" + wiki.getName());
		wiki.edit(ui, wiki);

		//Verify that wiki title has been changed
		logger.strongStep("Verify the new name appears on the wiki's Welcome page");
		log.info("INFO: Validate that the wiki now has the new name");
		Assert.assertTrue(driver.isTextPresent(wiki.getName()), 
						  "ERROR: the text [" + wiki.getName() + "] is not present on the web page");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests if a peer page can be edited and its name and description changed when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki and a peer page inside it using API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Open the peer page by clicking on its link in the left navigation menu.
	*<li><B>Step: </B>Click on the Edit button and change the page's title and description.
	*<li><B>Verify: </B>The changes are saved and the message for saved changes appears.
	*<li><B>Verify: </B>The new name and description appear for the peer page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void editPageOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_Page" + Helper.genDateBasedRand(), PageType.NavPage)
												.tags("tag1")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		logger.strongStep("Create a wiki and a peer page inside it using API");
		log.info("INFO: Create a wiki and a peer page inside it using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
				
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki 
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Open newly created peer page
		logger.strongStep("Click on the peer page's link in the left navigation menu to open it");
		log.info("INFO: Select the newly created peer page from the left navigation menu");
		ui.clickLinkWait("link=" + peerPage.getName());
		
		//Change page title and description
		logger.strongStep("Click on the Edit button and change the page's title and description");
		log.info("INFO: Edit the page and change its title and description");
		peerPage.setName("New" + peerPage.getName());
		peerPage.setDescription(Data.getData().New_Content_For_Editors_Private_Wiki);
		peerPage.edit(ui);
		
		//verify that page reports that it was edited
		logger.strongStep("Verify the text 'The page \"" + peerPage.getName() + "\" was saved.' appears");
		log.info("INFO: Validate that the text 'The page \"" + peerPage.getName() + "\" was saved.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("The page \"" + peerPage.getName() + "\" was saved."),
						  "ERROR: Page did not report that it was edited");
		
		//Verify that new page title is correct
		logger.strongStep("Verify the new name appears for the peer page");
		log.info("INFO: Validate that the peer page has the new name");
		Assert.assertTrue(ui.fluentWaitTextPresent(peerPage.getName()),
						  "ERROR: Unable to find new page title");
		
		//Verify that the description was added correctly
		logger.strongStep("Verify the new description appears for the peer page");
		log.info("INFO: Validate that the peer page has the new description");
		Assert.assertTrue(ui.fluentWaitPresent("css=p[dir='ltr']:contains("+peerPage.getDescription()+")"),
						  "ERROR: Unable to validate content was updated");
		 
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a peer page can be edited and its name and description changed when logged in as the wiki's editor.
	*<li><B>Step: </B>Create a wiki and a peer page inside it using API.
	*<li><B>Step: </B>Login as the editor and open the wiki.
	*<li><B>Step: </B>Open the peer page by clicking on its link in the left navigation menu.
	*<li><B>Step: </B>Click on the Edit button and change the page's title and description.
	*<li><B>Verify: </B>The changes are saved and the message for saved changes appears.
	*<li><B>Verify: </B>The new name and description appear for the peer page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void editPageEditor() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)							
									.description("Description for test " + testName)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_Page" + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		logger.strongStep("Create a wiki and a peer page inside it using API");
		log.info("INFO: Create a wiki and a peer page inside it using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
				
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as editor: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Editor' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Open newly created peer page
		logger.strongStep("Click on the peer page's link in the left navigation menu to open it");
		log.info("INFO: Select the newly created peer page from the left navigation menu");
		ui.clickLinkWait("link=" + peerPage.getName());
		
		//Change page title and description
		logger.strongStep("Click on the Edit button and change the page's title and description");
		log.info("INFO: Edit the page and change its title and description");
		peerPage.setName("New" + peerPage.getName());
		peerPage.setDescription(Data.getData().New_Content_For_Editors_Private_Wiki);
		peerPage.edit(ui);

		//verify that page reports that it was edited
		logger.strongStep("Verify the text 'The page \"" + peerPage.getName() + "\" was saved.' appears");
		log.info("INFO: Validate that the text 'The page \"" + peerPage.getName() + "\" was saved.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("The page \"" + peerPage.getName() + "\" was saved."),
						  "ERROR: Page did not report that it was edited");
				
		//Verify that new page title is correct
		logger.strongStep("Verify the new name appears for the peer page");
		log.info("INFO: Validate that the peer page has the new name");
		Assert.assertTrue(driver.isTextPresent(peerPage.getName()), 
						"the text [" + peerPage.getName() + "] is not present on the web page");
		
		//Verify that content was added correctly
		logger.strongStep("Verify the new description appears for the peer page");
		log.info("INFO: Validate that the peer page has the new description");
		Assert.assertTrue(driver.isTextPresent(Data.getData().New_Content_For_Editors_Private_Wiki), 
						"the text [" + Data.getData().New_Content_For_Editors_Private_Wiki + "] is not present on the web page");
		 
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a child page can be edited and its name and description changed when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki and a peer page inside it using API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Open the peer page by clicking on its link in the left navigation menu.
	*<li><B>Step: </B>Click on the 'Page Actions' menu and then click on 'Create Child' option to create a child page and save the changes.
	*<li><B>Verify: </B>The peer page and child page have been successfully created by making sure their links are visible.
	*<li><B>Step: </B>Click on the Edit button and change the child page's title and description.
	*<li><B>Verify: </B>The new name and description appear for the child page.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void editChildPageOwner() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
				
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRandVal())
									.tags(Data.getData().TagForPrivateWiki + Helper.genDateBasedRand())
									.addMembers(members)
									.readAccess(ReadAccess.WikiOnly)
									.description("Description for test " + testName)
									.build();

		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer for " + testName , PageType.Peer)
												.tags(Data.getData().TagForWikiPages)
												.description(Data.getData().PeerPageDescription)
												.build();

		BaseWikiPage childPage = new BaseWikiPage.Builder("Child for " + testName, PageType.Child)
												 .tags(Data.getData().TagForWikiPages)
		 										 .description(Data.getData().ChildPageDescription)
		 										 .build();

		logger.strongStep("Create a wiki and a peer page inside it using API");
		log.info("INFO: Create a wiki and a peer page inside it using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
		
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Open newly created peer page
		logger.strongStep("Click on the peer page's link in the left navigation menu to open it");
		log.info("INFO: Select the newly created peer page from the left navigation menu");
		ui.clickLink("link=" + peerPage.getName());

		//Add a new child page
		logger.strongStep("Click on the 'Page Actions' menu and then click on 'Create Child' option to create a child page and save the changes");
		log.info("INFO: Add a child page using the 'Create Child' option under 'Page Actions' menu");
		childPage.create(ui);
	
		//Verify pages have been created
		logger.strongStep("Verify the peer page and child page have been successfully created by making sure their links are visible");
		log.info("INFO: Validate that the peer and child pages exist");
		Assert.assertTrue(ui.fluentWaitTextPresent(peerPage.getName()),
						  "ERROR:" + peerPage.getName() + "Page does not exist");
		Assert.assertTrue(ui.fluentWaitTextPresent(childPage.getName()),
		  				  "ERROR:" + childPage.getName() + "Page does not exist");
		
		//Change child page name and description
		logger.strongStep("Click on the Edit button and change the child page's title and description");
		log.info("INFO: Edit the child page and change its title and description");
		childPage.setName("REVISED " + childPage.getName());
		childPage.setDescription("EDIT: " + childPage.getDescription());
		childPage.edit(ui);
		
		//Now verify that the page has being saved successfully
		logger.strongStep("Verify the new name appears for the child page");
		log.info("INFO: Validate that the child page has the new name");
		driver.isTextPresent(childPage.getName());

		logger.strongStep("Verify the new description appears for the child page");
		log.info("INFO: Validate that the child page has the new description");
		driver.isTextPresent(childPage.getDescription());

		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a wiki can be deleted when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki and a peer page inside it using API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Click on the 'Wiki Actions' menu and then click on 'Delete Wiki' option.
	*<li><B>Step: </B>Enter the owner's name as the signature in the Delete Wiki dialog box, select the checkbox and finally click on the Delete button.
	*<li><B>Verify: </B>The text 'The wiki was deleted.' appears.
	*<li><B>Verify: </B>The wiki does not appear on My Wikis page anymore.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void deleteWikiOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)								
									.description("Description for test " + testName)
									.build();

		logger.strongStep("Create wiki using API");
		log.info("INFO: Create wiki using API");
		wiki.createAPI(apiOwner);
				
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
	
		logger.strongStep("Click on the 'Wiki Actions' menu, then click on 'Delete Wiki' option followed by entering: " + testUser2.getDisplayName() + ""
				+ " as the signature in the Delete Wiki dialog box, selecting the checkbox and finally clicking on the Delete button");
		log.info("INFO: Delete the wiki");
		wiki.delete(ui, testUser2);
		
		//Verify the 'The wiki was deleted.' message appears
		logger.strongStep("Verify the text 'The wiki was deleted.' appears");
		log.info("INFO: Validate that the text 'The wiki was deleted.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("The wiki was deleted."),
						  "ERROR: Wiki was not deleted");
		
		//Verify wiki does not exist anymore
		logger.strongStep("Verify the wiki does not appear on My Wikis page anymore");
		log.info("INFO: Validate that the wiki does not appear on My Wikis page anymore");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(wiki.getName()),
						  "ERROR: Wiki was not deleted");
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests if a peer page can be deleted when logged in as the wiki's owner.
	*<li><B>Step: </B>Create a wiki and a peer page inside it using API.
	*<li><B>Step: </B>Login as the owner and open the wiki.
	*<li><B>Step: </B>Click on the peer page's link in the left navigation menu to open it.
	*<li><B>Step: </B>Delete the page by clicking on the 'Move to Trash' option under 'Page Actions' menu and then clicking on the OK button in the 'Move to Trash' dialog box.
	*<li><B>Verify: </B>The text 'The page was moved to the trash.' appears.
	*<li><B>Verify: </B>The page does not appear on the wiki's Welcome page anymore.
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void deletePageOwner() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.readAccess(ReadAccess.WikiOnly)
									.addMembers(members)
									.description("Description for test " + testName)
									.build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer_Wiki_Page" + Helper.genDateBasedRand(), PageType.NavPage)
												.tags("tag1")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		logger.strongStep("Create a wiki and a peer page inside it using API");
		log.info("INFO: Create a wiki and a peer page inside it using API");
		Wiki apiWiki = wiki.createAPI(apiOwner);
		peerPage.createAPI(apiOwner, apiWiki);
			
		//Enter owners username & password
		logger.strongStep("Open Wikis component and login as owner: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
			
		//view the wiki
		logger.strongStep("Click on the 'I'm an Owner' view in the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//open wiki
		logger.strongStep("Click on the link for the wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Open newly created peer page
		logger.strongStep("Click on the peer page's link in the left navigation menu to open it");
		log.info("INFO: Select the newly created peer page from the left navigation menu");
		ui.clickLinkWait("link=" + peerPage.getName());
		
		//Delete specified page
		logger.strongStep("Click on the 'Page Actions' menu, then click on 'Move to Trash' option and finally click on the OK button in the 'Move to Trash' dialog box to delete the page");
		log.info("INFO: Delete the page by clicking on the 'Move to Trash' option under 'Page Actions' menu and then clicking on the OK button in the 'Move to Trash' dialog box");
		peerPage.delete(ui);
				
		//Verify the page moved to trash message appears
		logger.strongStep("Verify the text 'The page was moved to the trash.' appears");
		log.info("INFO: Validate that the text 'The page was moved to the trash.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("The page was moved to the trash."),
						  "ERROR: Page was not moved to the trash");
		
		//Verify wiki page has been deleted successfully
		logger.strongStep("Verify the page does not appear on the wiki's Welcome page anymore");
		log.info("INFO: Validate that the page does not appear on the wiki's Welcome page anymore");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(peerPage.getName()),
						  "ERROR: Page was not deleted");

		ui.endTest();
	}
}
