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
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class FollowingWikis extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FollowingWikis.class);
	private WikisUI ui;
	private TestConfigCustom cfg;	
	private List<Member> members;
	private User testUser1, testUser2, testUser3, testUser4;
	private APIWikisHandler apiOwner;
	private String serverURL;

	/*
	 * This test case is to verify that the following feature is working as expected
	 * Created By: Conor Pelly
	 * Date: 20/10/2010
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
	
	//String for checking if the user is following the page
	Boolean UserFollowingPageYes = true;
	Boolean UserFollowingPageNo = false;
	String StartFollowingWiki = "Start";
	String StopFollowingWiki = "Stop";

	/**
	*<ul>
	*<li><B>Info: </B>Test case to test to start following a wiki when login as a different user.</li>
	*<li><B>Step: </B>Create a wiki using API.</li>
	*<li><B>Step: </B>Load component and Login to wikis </li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Verify: </B>The wiki has been created.</li>
	*<li><B>Step: </B>Navigate to the main view.</li>
	*<li><B>Verify: </B>The wiki is listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Logout of wiki and Login to wikis as owner</li>
	*<li><B>Verify: </B>The wiki is not listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Select the 'I'm an Owner' view from the left navigation menu</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Select 'Follow this Wiki' link.</li>
	*<li><B>Step: </B>Navigate to the main view.</li>
	*<li><B>Verify: </B>The wiki is listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Logout of wiki and Login to wikis as editor</li>
	*<li><B>Verify: </B>The wiki is not listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Select the 'I'm an Editor' view from the left navigation menu</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Select 'Follow this Wiki' link.</li>
	*<li><B>Step: </B>Navigate to the main view.</li>
	*<li><B>Verify: </B>The wiki is listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Logout of wiki and Login to wikis as reader</li>
	*<li><B>Verify: </B>The wiki is not listed in the 'I'm following' view.</li>
	*<li><B>Step: </B>Select the 'I'm a Reader' view from the left navigation menu</li>
	*<li><B>Step: </B>Open the wiki.</li>
	*<li><B>Step: </B>Select 'Follow this Wiki' link.</li>
	*<li><B>Step: </B>Navigate to the main view.</li>
	*<li><B>Verify: </B>The wiki is listed in the 'I'm following' view.</li>
	*</ul>
	*/
	@Test (groups = {"regression"})
	public void StartFollowingAsDifferentUsers()throws Exception{		
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
		Wiki wikiPage=wiki.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Open browser and login to Wikis as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Wikis");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser1);
		ui.waitForPageLoaded(driver);

		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		//Verify the wiki has been created
		logger.strongStep("Verify the wiki has been created");
		log.info("INFO: Verify the wiki has been created");
		verifyNewHomePageUI(wiki);
		
		//Return to the main view
		logger.strongStep("Navigate to the main view");
		log.info("INFO: Navigate to the main view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and Verify that the wiki is listed in the I'm following view 
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
				
		//Load component and Login as owner
		logger.strongStep("Open browser and login to Wikis as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as owner");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser2);
		ui.waitForPageLoaded(driver);
		
		//Click on the I'm following' view and verify that the wiki is not listed in this view
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//View the wiki
		logger.strongStep("Select the 'I'm an Owner' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Owner' view from the left navigation menu");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
				
		//return to the main view
		logger.strongStep("Navigate to the main view");
		log.info("INFO: Navigate to the main view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and verify that the wiki is listed in this view
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of Wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and login as editor
		logger.strongStep("Open browser and login to Wikis as: " + testUser3.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as editor");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser3);
		ui.waitForPageLoaded(driver);
		
		//Click on the I'm following' view and verify that the wiki is not listed in this view
		logger.strongStep("Verify the wiki is not listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is not listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//View the wiki
		logger.strongStep("Select the 'I'm an Editor' view from the left navigation menu");
		log.info("INFO: Select the 'I'm an Editor' view from the left navigation menu");
		Wiki_LeftNav_Menu.EDITOR.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
		
		//return to the main view
		logger.strongStep("Navigate to the main view");
		log.info("INFO: Navigate to the main view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and verify that the wiki is listed in this view
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		//Logout of the wiki
		logger.strongStep("Logout of Wiki");
		log.info("INFO: Logout of Wiki");
		ui.logout();
		
		//Load component and Login as reader
		logger.strongStep("Open browser and login to Wikis as: " + testUser4.getDisplayName());
		log.info("INFO: Open browser and login to Wikis as reader");
		ui.loadComponent(Data.getData().ComponentWikis, true);
		ui.login(testUser4);
		ui.waitForPageLoaded(driver);
		
		//Click on the I'm following' view and verify that the wiki not is listed in this view
		logger.strongStep("Verify the wiki not is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki not is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageNo);
		
		//View the wiki
		logger.strongStep("Select the 'I'm a Reader' view from the left navigation menu");
		log.info("INFO: Select the 'I'm a Reader' view from the left navigation menu");
		Wiki_LeftNav_Menu.READER.select(ui);
		
		//Open Wiki created earlier
		logger.strongStep("Open Wiki created via API");
		log.info("INFO: Open Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		//Start following this wiki
		logger.strongStep("Select 'Follow this Wiki' link");
		log.info("INFO: Select 'Follow this Wiki' link");
		ui.followWikiAction(StartFollowingWiki);
			
		//return to the main view
		logger.strongStep("Navigate to the main view");
		log.info("INFO: Navigate to the main view");
		ui.clickLink(WikisUIConstants.WikisLinkInHeader);
		
		//Click on the I'm following' view and verify that the wiki is listed in this view
		logger.strongStep("Verify the wiki is listed in the 'I'm following' view");
		log.info("INFO: Verify the wiki is listed in the 'I'm following' view");
		confirmWikiView(wiki, Wiki_LeftNav_Menu.FOLLOWING, UserFollowingPageYes);
		
		apiOwner.deleteWiki(wikiPage);
		
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
		    ui.fluentWaitElementVisible("css=div[dojoattachpoint='topPageNode']");
			ui.fluentWaitTextPresent(wiki.getName());
			log.info("INFO: View was opened and wiki confirmed in the view");
		}else {
			driver.changeImplicitWaits(5);
			driver.isTextNotPresent(wiki.getName());
			log.info("INFO: View was opened and wiki is not in the view as expected");
			driver.turnOnImplicitWaits();
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
}
