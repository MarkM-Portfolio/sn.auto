package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
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
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Page_Level_Tags extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Page_Level_Tags.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4;
	private APIWikisHandler apiOwner;
	private String serverURL;
	private BaseWikiPage wikiPage;
	private CommunitiesUI cUI;
	private BaseCommunity.Access defaultAccess;

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
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiOwner.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiOwner.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiOwner.getUserUUID(serverURL, testUser4)));

		//Generic pages
		wikiPage = new BaseWikiPage.Builder("Via_Nav" + Helper.genDateBasedRand(), PageType.NavPage)
									  .tags("tag1, tag2")
									  .description("this is a test description for creating a Peer wiki page")
									  .build();
	}
	
	
	
	/**
	 * This test case is to verify that page level tag functions are working as expected
	 * Created By: Conor Pelly
	 * Date: 20/10/2010
	 * Updated: 12/06/2013
	 */	
	
	/**
	*<ul>
	*<li><B>Info: </B>Test to verify that page level tag functions are working as expected when login as creator.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as a creator.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a tag to wiki page.</li>
	*<li><B>Verify: </B>The tag is added to the wiki page.</li>
	*<li><B>Step: </B>Create a wiki page.</li>	
	*<li><B>Step: </B>Add a tag with max characters allowed.</li>	
	*<li><B>Verify: </B>The tag is added with max characters allowed.</li>
	*<li><B>Step: </B>Add a tag which exceeds the max length.</li>
	*<li><B>Step: </B>Check the text 'is too long' appears.</li>	
	*<li><B>Step: </B>Click on 'Shorten tag?' link to shorten tag to max length .</li>	
	*<li><B>Step: </B>Add a tag with special characters.</li>	
	*<li><B>Step: </B>Add a tag to be deleted.</li>	
	*<li><B>Step: </B>Delete the tag.</li>	
	*<li><B>Step: </B>Search for a page level tag.</li>		
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void tagsAsCreator() throws Exception {
		
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
				
		//Load component and login to wikis as a creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as a creator");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
	
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
					
		//add a tag to wiki
		logger.strongStep("Add a new wiki tag");
		log.info("INFO: Add a new wiki tag");
		ui.addWikiTag(Data.getData().commonTag);
		
		//Validate that the new tag is added to the wiki
		logger.strongStep("Verify the wiki tag is added");
		log.info("INFO: Verify the wiki tag is added");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().commonTag),
						"ERROR: Unable to find the new wiki tag");
				
		//Create a new page
		logger.strongStep("Create a wiki page");
		log.info("INFO: Create a wiki page");
		wikiPage.create(ui);
			
		//add a tag with max characters allowed
		logger.strongStep("Add tag with max characters allowed");
		log.info("INFO: Add tag with max characters allowed");
		ui.addPageTag(Data.getData().MaxLengthTag);
		
		//Validate that the tag with max characters allowed is added
		logger.strongStep("Verify the tag is added with max characters allowed");
		log.info("INFO: Verify the tag is added with max characters allowed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MaxLengthTag),
					 	"ERROR: Unable to find tag with max characters allowed");		
	
		//Exceed the max amount of characters - should be asked to shorten
		logger.strongStep("Add a tag which exceeds the max length");
		log.info("INFO: Add a tag which exceeds the max length");
		ui.addPageTag(Data.getData().LongTag);
		
		//Check that the text 'is too long' appears
		logger.strongStep("Look for text 'is too long'");
		log.info("INFO: Look for text 'is too long'");
		ui.fluentWaitTextPresent(Data.getData().LongTagMessage);
		
		//Click on 'Shorten tag?' link to shorten tag to max length and click on ok button
		logger.strongStep("Shorten tag to max length by clicking on 'Shorten tag?' link");
		log.info("INFO: Shorten tag to max length by clicking on 'Shorten tag?' link");
		ui.clickLinkWait(WikisUIConstants.shortenTag);
		ui.clickLinkWait(WikisUIConstants.OK_Button);
			
		//add a tag with special characters
		logger.strongStep("Add a tag with special characters");
		log.info("INFO: Add a tag with special characters");
		ui.addPageTag(Data.getData().All_SpecialChars.replace("&", ""));
		
		//add a tag to be deleted
		logger.strongStep("Add a tag to delete");
		log.info("INFO: Add a tag to delete");
		ui.addPageTag(Data.getData().Deletetag);
			
		//delete the tag
		logger.strongStep("Delete the tag");
		log.info("INFO: Delete the tag");
		ui.deleteTag(Data.getData().Deletetag);
		
		//Search for a page level tag
		logger.strongStep("Search for a page level tag");
		log.info("INFO: Search for a page level tag");
		ui.addPageTag(Data.getData().Searchtag);
		searchForTag(Data.getData().Searchtag, wiki);
							
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test to verify that page level tag functions are working as expected when login as an owner.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as an owner.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a tag to wiki page.</li>
	*<li><B>Verify: </B>The tag is added to the wiki page.</li>
	*<li><B>Step: </B>Create a wiki page.</li>	
	*<li><B>Step: </B>Add a tag with max characters allowed.</li>	
	*<li><B>Verify: </B>The tag is added with max characters allowed.</li>
	*<li><B>Step: </B>Add a tag which exceeds the max length.</li>
	*<li><B>Step: </B>Check the text 'is too long' appears.</li>	
	*<li><B>Step: </B>Click on 'Shorten tag?' link to shorten tag to max length .</li>	
	*<li><B>Step: </B>Add a tag with special characters.</li>	
	*<li><B>Step: </B>Add a tag to be deleted.</li>	
	*<li><B>Step: </B>Delete the tag.</li>	
	*<li><B>Step: </B>Search for a page level tag.</li>		
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void tagsAsOwner() throws Exception {
		
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
				
		//Load component and login as owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser2);
	
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
					
		//add a tag to wiki
		logger.strongStep("Add a new wiki tag");
		log.info("INFO: Add a new wiki tag");
		ui.addWikiTag(Data.getData().commonTag);
		
		//Validate that the new tag is added to the wiki
		logger.strongStep("Verify the wiki tag is added");
		log.info("INFO: Verify the wiki tag is added");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().commonTag),
						"ERROR: Unable to find the new wiki tag");
				
		//Create a new page
		logger.strongStep("Create a wiki page");
		log.info("INFO: Create a wiki page");
		wikiPage.create(ui);
			
		//add a tag with max characters allowed
		logger.strongStep("Add tag with max characters allowed");
		log.info("INFO: Add tag with max characters allowed");
		ui.addPageTag(Data.getData().MaxLengthTag);
		
		//Validate that the tag with max characters allowed is added
		logger.strongStep("Verify the tag is added with max characters allowed");
		log.info("INFO: Verify the tag is added with max characters allowed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MaxLengthTag),
					 	"ERROR: Unable to find tag with max characters allowed");		
	
		//Exceed the max amount of characters - should be asked to shorten
		logger.strongStep("Add a tag which exceeds the max length");
		log.info("INFO: Add a tag which exceeds the max length");
		ui.addPageTag(Data.getData().LongTag);
		
		//Check that the text 'is too long' appears
		logger.strongStep("Look for text 'is too long'");
		log.info("INFO: Look for text 'is too long'");
		ui.fluentWaitTextPresent(Data.getData().LongTagMessage);
		
		//Click on 'Shorten tag?' link to shorten tag to max length and click on ok button
		logger.strongStep("Shorten tag to max length by clicking on 'Shorten tag?' link");
		log.info("INFO: Shorten tag to max length by clicking on 'Shorten tag?' link");
		ui.clickLinkWait(WikisUIConstants.shortenTag);
		ui.clickLinkWait(WikisUIConstants.OK_Button);
			
		//add a tag with special characters
		logger.strongStep("Add a tag with special characters");
		log.info("INFO: Add a tag with special characters");
		ui.addPageTag(Data.getData().All_SpecialChars.replace("&", ""));
		
		//add a tag to be deleted
		logger.strongStep("Add a tag to delete");
		log.info("INFO: Add a tag to delete");
		ui.addPageTag(Data.getData().Deletetag);
			
		//delete the tag
		logger.strongStep("Delete the tag");
		log.info("INFO: Delete the tag");
		ui.deleteTag(Data.getData().Deletetag);
			
		//Search for a page level tag
		logger.strongStep("Search for a page level tag");
		log.info("INFO: Search for a page level tag");
		ui.addPageTag(Data.getData().Searchtag);
		searchForTag(Data.getData().Searchtag, wiki);
							
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test to verify that page level tag functions are working as expected when login as an Editor.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis as an editor.</li>
	*<li><B>Step: </B>Navigate to 'I'm an Editor' view.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Add a tag to wiki page.</li>
	*<li><B>Verify: </B>The tag is added to the wiki page.</li>
	*<li><B>Step: </B>Create a wiki page.</li>	
	*<li><B>Step: </B>Add a tag with max characters allowed.</li>	
	*<li><B>Verify: </B>The tag is added with max characters allowed.</li>
	*<li><B>Step: </B>Add a tag which exceeds the max length.</li>
	*<li><B>Step: </B>Check the text 'is too long' appears.</li>	
	*<li><B>Step: </B>Click on 'Shorten tag?' link to shorten tag to max length .</li>	
	*<li><B>Step: </B>Add a tag with special characters.</li>	
	*<li><B>Step: </B>Add a tag to be deleted.</li>	
	*<li><B>Step: </B>Delete the tag.</li>	
	*<li><B>Step: </B>Search for a page level tag.</li>		
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void tagsAsEditor() throws Exception {
		
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
				
		//Load component and login as an editor
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as an editor");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser3);
	
		//Switch to 'I'm an Editor' view
		logger.strongStep("Select the ' I'm an Editor ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
			
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
					
		//add a tag to wiki
		logger.strongStep("Add a new wiki tag");
		log.info("INFO: Add a new wiki tag");
		ui.addWikiTag(Data.getData().commonTag);
		
		//Validate that the new tag is added to the wiki
		logger.strongStep("Verify the wiki tag is added");
		log.info("INFO: Verify the wiki tag is added");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().commonTag),
						"ERROR: Unable to find the new wiki tag");
				
		//Create a new page
		logger.strongStep("Create a wiki page");
		log.info("INFO: Create a wiki page");
		wikiPage.create(ui);
			
		//add a tag with max characters allowed
		logger.strongStep("Add tag with max characters allowed");
		log.info("INFO: Add tag with max characters allowed");
		ui.addPageTag(Data.getData().MaxLengthTag);
		
		//Validate that the tag with max characters allowed is added
		logger.strongStep("Verify the tag is added with max characters allowed");
		log.info("INFO: Verify the tag is added with max characters allowed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MaxLengthTag),
					 	"ERROR: Unable to find tag with max characters allowed");		
	
		//Exceed the max amount of characters - should be asked to shorten
		logger.strongStep("Add a tag which exceeds the max length");
		log.info("INFO: Add a tag which exceeds the max length");
		ui.addPageTag(Data.getData().LongTag);
		
		//Check that the text 'is too long' appears
		logger.strongStep("Look for text 'is too long'");
		log.info("INFO: Look for text 'is too long'");
		ui.fluentWaitTextPresent(Data.getData().LongTagMessage);
		
		//Click on 'Shorten tag?' link to shorten tag to max length and click on ok button
		logger.strongStep("Shorten tag to max length by clicking on 'Shorten tag?' link");
		log.info("INFO: Shorten tag to max length by clicking on 'Shorten tag?' link");
		ui.clickLinkWait(WikisUIConstants.shortenTag);
		ui.clickLinkWait(WikisUIConstants.OK_Button);
			
		//add a tag with special characters
		logger.strongStep("Add a tag with special characters");
		log.info("INFO: Add a tag with special characters");
		ui.addPageTag(Data.getData().All_SpecialChars.replace("&", ""));
		
		//add a tag to be deleted
		logger.strongStep("Add a tag to delete");
		log.info("INFO: Add a tag to delete");
		ui.addPageTag(Data.getData().Deletetag);
			
		//delete the tag
		logger.strongStep("Delete the tag");
		log.info("INFO: Delete the tag");
		ui.deleteTag(Data.getData().Deletetag);
			
		//Search for a page level tag
		logger.strongStep("Search for a page level tag");
		log.info("INFO: Search for a page level tag");
		ui.addPageTag(Data.getData().Searchtag);
		searchForTag(Data.getData().Searchtag, wiki);
							
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test to verify that page level tag functions are working as expected when login as an Editor.</li>
	*<li><B>Step: </B>Use API to create a wiki.</li>
	*<li><B>Step: </B>Load component and login to wikis.</li>		
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Create a wiki page.</li>	
	*<li><B>Step: </B>Logout of wiki and login to wiki as a reader.</li>	
	*<li><B>Step: </B>Navigate to 'I'm a Reader' view.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Verify: </B>Add tags link is not present.</li>	
	*<li><B>Step: </B>Select the wiki page.</li>
	*<li><B>Verify: </B>Add tags link is not present.</li>		
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void tagsAsReader() throws Exception {
		
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
				
		//Load component and login to wikis as a creator
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as a creator");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
				
		//Create a new page
		logger.strongStep("Create a wiki page");
		log.info("INFO: Create a wiki page");
		wikiPage.create(ui);
			
		//wait until page is created before logging out
		ui.fluentWaitTextPresent("The page was created.");
			
		//Logout of Wiki as creator
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
					
		//Load component and login to wikis as a reader
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as a reader");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
			
		//Switch to 'I'm a Reader' view
		logger.strongStep("Select the 'I'm a Reader' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
			
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
			
		//Validate Add tags link is not present
		logger.strongStep("Verify Add tags link is not present");
		log.info("INFO: Verify Add tags link is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("Add tags"),
						"ERROR: Add tags is present");
			
		//Select the Page
		logger.strongStep("Select the wiki page " + wikiPage.getName());
		log.info("INFO: Select the wiki page " + wikiPage.getName());
		ui.openWikiPageNav(wikiPage);
				
		//Validate Add tags link is not present
		logger.strongStep("Verify Add tags link is not present");
		log.info("INFO: Verify Add tags link is not present");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("Add tags"),
						"ERROR: Add tags is present");
			
		ui.endTest();	
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test of checking Tag Cloud</li>
	 * <li><B>Step: </B>Create a community using an API</li>
	 * <li><B>Step: </B>Add an wiki widget using an API</li>
	 * <li><B>Step: </B>Open the community</li>
	 * <li><B>Step: </B>Create two wiki pages inside of wiki</li>
	 * <li><B>Verify: </B>Tags for the events are located within the cloud</li>
	 * <li><B>Step: </B>Click list link</li>
	 * <li><B>Verify: </B>View changes are correct</li>
	 * <li><B>Step: </B>Click Tag Link in Tag cloud</li>
	 * <li><B>Verify: </B>Check if all pages show up</li>
	 * <li><B>Step: </B>Remove tag link in main tag cloud</li>
	 * <li><B>Verify: </B>Pages show up correctly</li>
	 * <li><B>Step: </B>Remove tag link in tagcloud</li>
	 * <li><B>Verfiy: </B>Pages show up correctly</li>
	 * <li><B>Step: </B>Enter notexisted tag in tagcloud</li>
	 * <li><B>Verify: </B>Pages show up correctly</li>
	 * </ul>
	 */
	@Deprecated
	@Test(groups = { "regression", "regressioncloud" })
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
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add wiki widget to community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add wiki widget to community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		logger.strongStep("Navigate to the owned communtiy views");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		
		//navigate to the API community
		logger.strongStep("Navigate to the communtiy");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Wikis from left navigation menu");
		log.info("INFO: Select Wikis from left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//create a new wikipage
		logger.strongStep("Creating two WikiPages inside wiki");
		log.info("INFO: Creating two WikiPages inside wiki");
		peerwikiPage.create(ui);	
		childwikiPage.create(ui);
		
		logger.strongStep("Wait for tag cloud widget to be present");
		log.info("INFO: Wait for tag cloud Widet to be present");
		ui.fluentWaitPresent(WikisUIConstants.tagCloudWidget);
		
		//Validate the tags for the events are located within the cloud
		logger.weakStep("Validate tags for the events that are located within the cloud are present");
		String[] sPeerTags = peerwikiPage.getTags().split(",");
		String[] sChildTags = childwikiPage.getTags().split(",");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudCloudView),
				"ERROR: The element " + WikisUIConstants.tagCloudCloudView + " was not found on the page.");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[0])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[0]) +
				" was not found on the page.");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sPeerTags[1]) +
				" was not found on the page.");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sChildTags[1])),
				"ERROR: The element " + ui.sTagLinkinTagCloud(sChildTags[1]) +
				" was not found on the page.");
		
		logger.strongStep("Click 'list link'");
		log.info("click list link, check if view changes correctly");
		ui.clickLinkWait(BaseUIConstants.ListLink);
		logger.weakStep("Validate that the view changes correctly");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudListView),
				"ERROR: The element " + WikisUIConstants.tagCloudCloudView + " was not found on the page.");
		int nTags = driver.getElements(WikisUIConstants.TagListinListView).size();
		Assert.assertTrue(nTags > 0, "ERROR: Wrong number of tags in tag cloud: found " + nTags +
				", expected at least one.");

		logger.strongStep("Click tag link in tag cloud");
		log.info("click tag link in tagcloud, check if all pages show up");
		ui.clickLinkWait(BaseUIConstants.CloudLink);
		ui.fluentWaitPresent(ui.sTagLinkinTagCloud(sPeerTags[0]));		
		driver.getSingleElement(ui.sTagLinkinTagCloud(sPeerTags[0])).hover();		
		ui.clickLinkWait(ui.sTagLinkinTagCloud(sPeerTags[0]));
		
		logger.weakStep("Verify that all pages show up properly");
		ui.fluentWaitTextPresent("Index");
		ui.fluentWaitTextPresent("1-2 of 2");
		ui.fluentWaitPresent(ui.getPageSelectorinListView(peerwikiPage));  
		ui.fluentWaitPresent(ui.getPageSelectorinListView(childwikiPage));	
		ui.fluentWaitPresent(ui.sRemoveTagLinkinTagCloud(sPeerTags[0]));
		ui.fluentWaitPresent(ui.sRemoveTagLinkinMain(sPeerTags[0]));
		
		logger.strongStep("Remove tag link in main");
		log.info("remove tag link in main, check if pages show up correctly");		
		ui.clickLinkWait(ui.sRemoveTagLinkinMain(sPeerTags[0]));
		ui.fluentWaitTextPresent("1-3 of 3");	
		logger.weakStep("Validate that pages show up properly");
		Assert.assertFalse(driver.isElementPresent(ui.sRemoveTagLinkinTagCloud(sPeerTags[0])),
				"ERROR: tag " + sPeerTags[0] + " remains after removing it");
		Assert.assertFalse(driver.isElementPresent(ui.sRemoveTagLinkinMain(sPeerTags[0])),
				"ERROR: tag " + sPeerTags[0] + " remains after removing it");
		
		ui.getFirstVisibleElement(BaseUIConstants.FindTag).click();
		ui.fluentWaitPresent(WikisUIConstants.tagTextBox);
		ui.typeText(WikisUIConstants.tagTextBox, sPeerTags[0]);
		ui.fluentWaitPresent(WikisUIConstants.tagTypeahead);
		nTags = driver.getElements(WikisUIConstants.tagLinksinTagTypeahead).size();
		Assert.assertTrue(nTags == 6, "ERROR: Wrong number of tags in typeahaed: " +
				"Expected 6, actual: " + nTags);
		ui.clearText(WikisUIConstants.tagTextBox);
		ui.typeText(WikisUIConstants.tagTextBox, sPeerTags[1]);
		ui.clickLinkWait(WikisUIConstants.tagLinkinTagTypeahead);
		ui.fluentWaitPresent(ui.sRemoveTagLinkinMain(sPeerTags[1]));
		ui.fluentWaitPresent(ui.sRemoveTagLinkinTagCloud(sPeerTags[1]));
		
		logger.weakStep("Validate that child wiki page is present");
		ui.fluentWaitTextPresent("1-1 of 1");
		Assert.assertFalse(driver.isElementPresent(ui.getPageSelectorinListView(childwikiPage)),
				"ERROR: Child wiki page is present when the peer should be displayed"); 
		ui.fluentWaitPresent(ui.getPageSelectorinListView(peerwikiPage));	
		ui.fluentWaitPresent(ui.sRemoveTagLinkinTagCloud(sPeerTags[1]));
		ui.fluentWaitPresent(ui.sRemoveTagLinkinMain(sPeerTags[1]));
		
		logger.strongStep("Remove tag link in tagcloud");
		log.info("remove tag link in tagcloud, check if pages show up correctly");		
		ui.clickLink(ui.sRemoveTagLinkinTagCloud(sPeerTags[1]));
		ui.fluentWaitTextPresent("1-3 of 3");
		logger.weakStep("Validate that peer wiki page is present");
		ui.fluentWaitPresent(ui.getPageSelectorinListView(childwikiPage)); 	
		Assert.assertFalse(driver.isElementPresent(ui.sRemoveTagLinkinTagCloud(sPeerTags[1])),
				"ERROR: Peer wiki page's tags are present when the child should be displayed");
		Assert.assertFalse(driver.isElementPresent(ui.sRemoveTagLinkinMain(sPeerTags[1])),
				"ERROR: Peer wiki page's tags are present when the child should be displayed");					
			
		logger.strongStep("Enter a tag that doesn't exist in the tagcloud");
		log.info("enter notexisted tag in tagcloud, check if pages show up correctly");	
		String newTag = uniqueTagPrefix + "newtag";
		
		ui.getFirstVisibleElement(BaseUIConstants.FindTag).click();
		ui.fluentWaitPresent(WikisUIConstants.tagTextBox);
		ui.typeText(WikisUIConstants.tagTextBox, newTag);
		
		logger.weakStep("Validate that the page shows up properly after entering a non existent tag");
		Assert.assertFalse(driver.isElementPresent(WikisUIConstants.tagTypeahead),
				"ERROR: Typeahead input for tags remains after after entering a tag");
		ui.clickLinkWait(WikisUIConstants.TagSearchBtn);
		
		ui.fluentWaitPresent(ui.sRemoveTagLinkinMain(newTag));
		ui.fluentWaitTextPresent(Data.getData().NoPageMsg);

		logger.strongStep("Delete community that was creating via API");
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
	 * <li><B>Step: </B>Create a new wiki page using two Wiki Pages inside wiki</li>
	 * <li><B>Step: </B>Update the wiki page to add new tag</li>
	 * <li><B>Step: </B>Remove the tag in detail view</li>
	 * <li><B>Step: </B>Detail view adds two tags</li>
	 * <li><B>Step: </B>Detail view adds special tag</li>
	 * <li><B>Step: </B>Click list link</li>
	 * <li><B>Verify: </B>Tag count is 2</li>
	 * </ul>
	 */
	@Deprecated
	@Test(groups = {"regression", "regressioncloud"})
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
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiComOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//add widget
		logger.strongStep("Add wiki widget to community using API");
		if(!apiComOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add wiki widget to community using API");
			community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.WIKI);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the owned communtiy views");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		//navigate to the API community
		logger.strongStep("Navigate to the communtiy");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Wikis from left navigation menu");
		log.info("INFO: Select Wikis from left navigation menu");
		Community_TabbedNav_Menu.WIKI.select(cUI,2);
		
		//create a new wikipage
		logger.strongStep("Creating two WikiPages inside wiki");
		log.info("INFO: Creating two WikiPages inside wiki");
		peerwikiPage.create(ui);	
		childwikiPage.create(ui);	
		
		logger.strongStep("Wait for tag cloud widget to be present");
		log.info("INFO: Wait for tag cloud Widet to be present");
		ui.fluentWaitPresent(WikisUIConstants.tagCloudWidget);
		
		//Validate the tags for the events are located within the cloud
		logger.weakStep("Validate that the tags for the events are located within the cloud");
		String[] sPeerTags = newpeerwikiPage.getTags().split(",");
		String[] sChildTags = childwikiPage.getTags().split(",");
		
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.tagCloudCloudView));
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[0])));
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])));
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sChildTags[0])));		
		
		logger.strongStep("Update wiki page to add a new tag");
		log.info("INFO:update wiki page to add new tag");
		driver.getFirstElement(ui.getPageSelector(peerwikiPage)).click();
		ui.editWikiPage(newpeerwikiPage);
		logger.weakStep("Validate that new tag was added");
		Assert.assertTrue(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[2])));
		
		logger.strongStep("Remove tag in detail view");
		log.info("INFO: remove tag in detail view");
		ui.clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		driver.getVisibleElements("css=li.lotusTag:contains(" + sPeerTags[1] + ") a").get(0).click();
		ui.fluentWaitTextPresent(Data.getData().RemoveTagmsg + sPeerTags[1] + "?");
		driver.getSingleElement(WikisUIConstants.Dialog_OK_Button).click();
		logger.weakStep("Verify that tag was removed");
		Assert.assertFalse(driver.isElementPresent(ui.sTagLinkinTagCloud(sPeerTags[1])));
		
		logger.strongStep("Add two tags and special tag to detail view");
		log.info("INFO: detail view can add two tags, can add special tag");
		driver.getFirstElement(ui.getPageSelector(childwikiPage)).click();
		ui.clickLinkWait(WikisUIConstants.Add_or_RemoveTags_Link);
		driver.getSingleElement(WikisUIConstants.TagEditorTextFieldInput).type(sPeerTags[0] + " " + specialTag);
		driver.getSingleElement(WikisUIConstants.OK_Button).click();
		int i=0;
		for(i=0; i<driver.getElements(BaseUIConstants.taglinks).size(); i++){
			System.out.println(driver.getElements(BaseUIConstants.taglinks).get(i).getAttribute("title"));
			if(driver.getElements(BaseUIConstants.taglinks).get(i).getAttribute("title").contains(specialTag))
				break;
		}
		Assert.assertTrue(i<driver.getElements(BaseUIConstants.taglinks).size());
		
		logger.weakStep("Click on 'List Link' and check that the tag count is 2");
		log.info("click list link, check if tag count is 2");
		Assert.assertTrue(driver.getSingleElement(ui.sTagLinkinTagCloud(sPeerTags[0])).getAttribute("title").contains("count 2"));

		apiComOwner.deleteCommunity(comAPI);
		
		ui.endTest();
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
