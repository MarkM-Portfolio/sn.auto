package com.ibm.conn.auto.tests.wikis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;

public class Smoke_Community_Wikis extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Smoke_Community_Wikis.class);
	private CommunitiesUI ui;
	private WikisUI wUI;
	private TestConfigCustom cfg;	
	private User testUser, testLookAheadUser;

	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		wUI = WikisUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();		

		log.info("INFO: Using test user: " + testUser.getDisplayName());
		log.info("INFO: Using testLookAhead user: " + testLookAheadUser.getDisplayName());
		
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Create a community and then add a new wiki page to this community</li>
	*<li><B>Step: </B>Create a community</li>
	*<li><B>Step: </B>Using the Create a Wiki Page link in overview create a wiki page with title and description</li>
	*<li><B>Step: </B>Return to overview scene and verify that the page was created</li>
	*<li><B>Verify: </B>Community is created and a wiki page is created</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"smoke"})
	public void smokeCommunityWiki() throws Exception {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("wiki test page for " + testName, PageType.Community)
												.description("this is a wiki page create for the "+testName+" for Smart Cloud smoke test")
												.build();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Create the community
		community.create(ui);
		
		//Customize community - Add the Wikis widget
		log.info("INFO: Adding the " + BaseWidget.WIKI.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.WIKI);

		//Now create a wiki page and verify that the page was created successfully
		log.info("INFO: Create wiki page");
		wikiPage.create(wUI);
		
		//Now switch back to overview view and verify that the page is listed
		log.info("INFO: Select Overview from community left navigation menu");
		Community_LeftNav_Menu.OVERVIEW.select(ui);
		
		ui.waitForPageLoaded(driver);
		String pageExists = "css=tbody tr td h4 a:contains(" + wikiPage.getName() + ")";
		driver.isElementPresent(pageExists);
		log.info("INFO: wiki page - " + wikiPage.getName() + " exists in the overview view");
		
		//Add a bookmark - why this is in a test called community wiki???
		
		//End of test
		ui.endTest();
	}	

}
