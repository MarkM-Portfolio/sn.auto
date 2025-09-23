package com.ibm.conn.auto.tests.wikis.regression;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class Like extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Like.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User  testUser2, testUser3, testUser4;
	private String serverURL;
	private String pageLikeCount=" ";
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();		
		
		//initialize API
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIWikisHandler apiUser = new APIWikisHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		//create member list
		members = new ArrayList<Member>();	
		members.add(new Member(WikiRole.OWNER, testUser2, apiUser.getUserUUID(serverURL, testUser2)));
		members.add(new Member(WikiRole.EDITOR, testUser3, apiUser.getUserUUID(serverURL, testUser3)));
		members.add(new Member(WikiRole.READER, testUser4, apiUser.getUserUUID(serverURL, testUser4)));
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		
	}
	
	public Wiki dataCreation(APIWikisHandler apiOwner, BaseWiki wiki, List<BaseWikiPage> pages, User testUser1){
		BaseWikiPage page1, page2, page3;
		BaseWikiPage childPage1, childPage2, childPage3;
				
		page1 = new BaseWikiPage.Builder("Page_1" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
								.tags("tagforliketest")
								.description("this is a test description for creating a Peer wiki page")
								.build();

		page2 = new BaseWikiPage.Builder("Page_2" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
		   						.tags("tagforliketest")
		   						.description("this is a test description for creating a Peer wiki page")
		   						.build();
		
		page3 = new BaseWikiPage.Builder("Page_3" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
		   						.tags("tagforliketest")
		   						.description("this is a test description for creating a Peer wiki page")
		   						.build();
		
		childPage1 = new BaseWikiPage.Builder("Child_Page_1" + Helper.genMonthDateBasedRandVal(), PageType.Child)
		 							.tags("Child1_" + Helper.genDateBasedRand())
		 							.description("this is a test description for creating a Child wiki page")
		 							.build();

		childPage2 = new BaseWikiPage.Builder("Child_Page_2" + Helper.genMonthDateBasedRandVal(), PageType.Child)
									.tags("Child2_" + Helper.genDateBasedRand())
									.description("this is a test description for creating a Child wiki page")
									.build();
		
		childPage3 = new BaseWikiPage.Builder("Child_Page_3" + Helper.genMonthDateBasedRandVal(), PageType.Child)
									 .tags("Child3_" + Helper.genDateBasedRand())
									 .description("this is a test description for creating a Child wiki page")
									 .build();
			
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);	
		pages.add(childPage1);		
		pages.add(childPage2);
		pages.add(childPage3);
		
		log.info("INFO: wikiName" + wiki.getName());
		Wiki apiWiki = wiki.createAPI(apiOwner);
		page1.createAPI(apiOwner, apiWiki);
		page2.createAPI(apiOwner, apiWiki);
		page3.createAPI(apiOwner, apiWiki);

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
	
		//Logout of wiki as a creator
		ui.logout();
		
		return apiWiki;

	}
	
	public void cleanUp(APIWikisHandler apiOwner, Wiki apiWiki, List<BaseWikiPage> pages){
		log.info("INFO: Deleting wiki");
		apiOwner.deleteWiki(apiWiki);
		
		log.info("INFO: Removing all pages from the List ");
		pages.clear();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to test likes the wiki page when login as a creator.</li>
	*<li><B>Step: </B>Load component and login to wikis as creator.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Page.</li>
	*<li><B>Step: </B>Like the page.</li>
	*<li><B>Verify: </B>The message 'You like this' appears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Deleting wiki and created pages.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void LikesPagesAsCreator() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		List<BaseWikiPage> pages = new ArrayList<BaseWikiPage>();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + "LikeWiki" + Helper.genDateBasedRand())
				   .editAccess(EditAccess.EditorsAndOwners)
				   .readAccess(ReadAccess.WikiOnly)
				   .addMembers(members)
				   .tags("tag" + Helper.genDateBasedRand())
				   .description("Description for test " + "LikeWiki")
				   .build();

		User testUser1 = cfg.getUserAllocator().getUser();	
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		Wiki wikiApi=dataCreation(apiOwner, wiki, pages, testUser1);	

		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as creator");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser1);
		
		//view the wiki
		logger.strongStep("Select the ' I'm an Owner ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		for (Iterator<BaseWikiPage> iter = pages.iterator(); iter.hasNext(); ) {
			
			BaseWikiPage page = iter.next();
	
			logger.strongStep("Open page " + page.getName());
			log.info("INFO: Open page " + page.getName());
			ui.openWikiPageNav(page);

			logger.strongStep("Like page");
			log.info("INFO: Like page");
			page.like(ui);
			
			logger.strongStep("Verify the message 'You like this' appears");
			log.info("INFO: Verify the message 'You like this' appears");
			Assert.assertTrue(ui.fluentWaitTextPresent("You like this"),
							"ERROR: Unable to find 'You like this'");
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(driver.getSingleElement(WikisUIConstants.LikeCount).getText()),
					page.getLikeCount(), "ERROR: Like count does not match");
			
		}
		
		logger.strongStep("Remove wiki and pages");
		log.info("INFO: Remove wiki and pages");
		cleanUp(apiOwner, wikiApi, pages);
		
		//Logout of Wiki
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Test case to test Like and Unlike the wiki page when login as a reader.</li>
	*<li><B>Step: </B>Load component and login to wikis as reader.</li>
	*<li><B>Step: </B>Select the ' I'm an Reader ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Page.</li>
	*<li><B>Step: </B>Like the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' appears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Unlike the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' disappears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Deleting wiki and created pages.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void LikesUnLikesPagesAsReader() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		List<BaseWikiPage> pages = new ArrayList<BaseWikiPage>();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + "LikeWiki" + Helper.genDateBasedRand())
				   .editAccess(EditAccess.EditorsAndOwners)
				   .readAccess(ReadAccess.WikiOnly)
				   .addMembers(members)
				   .tags("tag" + Helper.genDateBasedRand())
				   .description("Description for test " + "LikeWiki")
				   .build();

		User testUser1 = cfg.getUserAllocator().getUser();	
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		Wiki wikiApi=dataCreation(apiOwner, wiki, pages, testUser1);
		
		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as reader");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);

		//view the wiki
		logger.strongStep("Select the ' I'm an Reader ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		for (Iterator<BaseWikiPage> iter = pages.iterator(); iter.hasNext(); ) {

			BaseWikiPage page = iter.next();
	
			logger.strongStep("Open page " + page.getName());
			log.info("INFO: Open page " + page.getName());
			ui.openWikiPageNav(page);

			logger.strongStep("Like page");
			log.info("INFO: Like page");
			page.like(ui);
			
			logger.strongStep("Verify the message 'You like this' appears");
			log.info("INFO: Verify the message 'You like this' appears");
			Assert.assertTrue(ui.fluentWaitTextPresent("You like this"),
							"ERROR: Unable to find 'You like this'");
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(driver.getSingleElement(WikisUIConstants.LikeCount).getText()),
					page.getLikeCount(), "ERROR: Like count does not match");
			
			logger.strongStep("Unlike page");
			log.info("INFO: Unlike page");
			page.unlike(ui);
			
			logger.strongStep("Verify the messege 'You like this' disappears");
			log.info("INFO: Verify the messege 'You like this' disappears");
			Assert.assertTrue(ui.fluentWaitTextNotPresent("You like this"),
							"ERROR: Able to find 'You like this'");
			
			pageLikeCount=driver.getSingleElement(WikisUIConstants.LikeCount).getText();
			if(pageLikeCount.equals(" ")){
				pageLikeCount="0";
			}
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(pageLikeCount), page.getLikeCount(), "ERROR: Like count does not match");
			
		}

		logger.strongStep("Remove wiki and pages");
		log.info("INFO: Remove wiki and pages");
		cleanUp(apiOwner, wikiApi, pages);
		
		//Logout of Wiki
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to test Like and Unlike the wiki page when login as a editor.</li>
	*<li><B>Step: </B>Load component and login to wikis as editor.</li>
	*<li><B>Step: </B>Select the ' I'm an Editor ' view from the left navigation menu.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Page.</li>
	*<li><B>Step: </B>Like the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' appears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Unlike the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' disappears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Deleting wiki and created pages.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void LikesUnLikesPagesAsEditor() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		List<BaseWikiPage> pages = new ArrayList<BaseWikiPage>();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + "LikeWiki" + Helper.genDateBasedRand())
				   .editAccess(EditAccess.EditorsAndOwners)
				   .readAccess(ReadAccess.WikiOnly)
				   .addMembers(members)
				   .tags("tag" + Helper.genDateBasedRand())
				   .description("Description for test " + "LikeWiki")
				   .build();

		User testUser1 = cfg.getUserAllocator().getUser();	
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		Wiki wikiApi=dataCreation(apiOwner, wiki, pages, testUser1);	

		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as editor");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);

		//view the wiki
		logger.strongStep("Select the ' I'm an Editor ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		for (Iterator<BaseWikiPage> iter = pages.iterator(); iter.hasNext(); ) {

			BaseWikiPage page = iter.next();
	
			logger.strongStep("Open page " + page.getName());
			log.info("INFO: Open page " + page.getName());
			ui.openWikiPageNav(page);

			logger.strongStep("Like page");
			log.info("INFO: Like page");
			page.like(ui);
			
			logger.strongStep("Verify the message 'You like this' appears");
			log.info("INFO: Verify the message 'You like this' appears");
			Assert.assertTrue(ui.fluentWaitTextPresent("You like this"),
							"ERROR: Unable to find 'You like this'");
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(driver.getSingleElement(WikisUIConstants.LikeCount).getText()),
					page.getLikeCount(), "ERROR: Like count does not match");
			
			logger.strongStep("Unlike page");
			log.info("INFO: Unlike page");
			page.unlike(ui);
			
			logger.strongStep("Verify the messege 'You like this' disappears");
			log.info("INFO: Verify the messege 'You like this' disappears");
			Assert.assertTrue(ui.fluentWaitTextNotPresent("You like this"),
							"ERROR: Able to find 'You like this'");
			
			pageLikeCount=driver.getSingleElement(WikisUIConstants.LikeCount).getText();
			if(pageLikeCount.equals(" ")){
				pageLikeCount="0";
			}
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(pageLikeCount), page.getLikeCount(), "ERROR: Like count does not match");
			
		}

		logger.strongStep("Remove wiki and pages");
		log.info("INFO: Remove wiki and pages");
		cleanUp(apiOwner, wikiApi, pages);
		
		//Logout of Wiki
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test case to test Like and Unlike the wiki page when login as a owner.</li>
	*<li><B>Step: </B>Load component and login to wikis as owner.</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Navigate to the Page.</li>
	*<li><B>Step: </B>Like the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' appears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Unlike the page.</li>
	*<li><B>Verify: </B>The messege 'You like this' disappears.</li>
	*<li><B>Verify: </B>The like count is correct.</li>
	*<li><B>Step: </B>Deleting wiki and created pages.</li>
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void LikesUnLikesPagesAsOwner() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		List<BaseWikiPage> pages = new ArrayList<BaseWikiPage>();
		
		BaseWiki wiki = new BaseWiki.Builder(testName + "LikeWiki" + Helper.genDateBasedRand())
				   .editAccess(EditAccess.EditorsAndOwners)
				   .readAccess(ReadAccess.WikiOnly)
				   .addMembers(members)
				   .tags("tag" + Helper.genDateBasedRand())
				   .description("Description for test " + "LikeWiki")
				   .build();

		User testUser1 = cfg.getUserAllocator().getUser();	
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		Wiki wikiApi=dataCreation(apiOwner, wiki, pages, testUser1);

		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		
		//view the wiki
		logger.strongStep("Select the ' I'm an Owner ' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);

		//Open Wiki created above
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		for (Iterator<BaseWikiPage> iter = pages.iterator(); iter.hasNext(); ) {

			BaseWikiPage page = iter.next();
	
			logger.strongStep("Open page " + page.getName());
			log.info("INFO: Open page " + page.getName());
			ui.openWikiPageNav(page);

			logger.strongStep("Like page");
			log.info("INFO: Like page");
			page.like(ui);
			
			logger.strongStep("Verify the message 'You like this' appears");
			log.info("INFO: Verify the message 'You like this' appears");
			Assert.assertTrue(ui.fluentWaitTextPresent("You like this"),
							"ERROR: Unable to find 'You like this'");
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(driver.getSingleElement(WikisUIConstants.LikeCount).getText()),
					page.getLikeCount(), "ERROR: Like count does not match");
			
			logger.strongStep("unLike page");
			log.info("INFO: unLike page");
			page.unlike(ui);
			
			logger.strongStep("Verify The messege 'You like this' disappears");
			log.info("INFO: Verify The messege 'You like this' disappears");
			Assert.assertTrue(ui.fluentWaitTextNotPresent("You like this"),
							"ERROR: Able to find 'You like this'");
			
			pageLikeCount=driver.getSingleElement(WikisUIConstants.LikeCount).getText();
			if(pageLikeCount.equals(" ")){
				pageLikeCount="0";
			}
			
			logger.strongStep("Verify like count is correct");
			log.info("INFO: Verify like count is correct");
			Assert.assertEquals(Integer.parseInt(pageLikeCount), page.getLikeCount(), "ERROR: Like count does not match");
			
		}

		logger.strongStep("Remove wiki and pages");
		log.info("INFO: Remove wiki and pages");
		cleanUp(apiOwner, wikiApi, pages);
		
		//Logout of Wiki
		ui.endTest();
		
	}

	private void addChildPage(BaseWikiPage parent, BaseWikiPage child){
			
		log.info("INFO: Select " + parent.getName());
		ui.clickLinkWait("link=" + parent.getName());
		
		log.info("INFO: Create child page");
		child.create(ui);

	}
	
}


