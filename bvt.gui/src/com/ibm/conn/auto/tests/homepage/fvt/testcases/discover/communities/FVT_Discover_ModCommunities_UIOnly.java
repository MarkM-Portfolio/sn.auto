/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2013                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.HomepageUI;


public class FVT_Discover_ModCommunities_UIOnly extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(LogManager.class);

	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private FeedsUI fUI;
	private HomepageUI uiHP;
	private DogearUI uiBM;

	private User testUser1;
	private User testUser2;
	private User testUser3;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiHP = HomepageUI.getGui(cfg.getProductName(), driver);
		uiBM = DogearUI.getGui(cfg.getProductName(), driver);
		fUI = FeedsUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();

	}

	/**
	* testCreateModComm()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: Select a community type - Moderated</B></li>
	*<li><B>Verify: that the community shows up as news story</B></li>
	*</ul>
	*/
	@Test (groups = {"fvtonprem"})
	public void testCreateModComm() throws Exception {

		String testName = ui.startTest();		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(Access.MODERATED)
		   										   .description("Test description for testcase " + testName)
		   										   .addMember(new Member(CommunityRole.MEMBERS, testUser3))
		   										   .build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//Create moderated community
		log.info("INFO: Create moderated community");
		community.create(ui);

		//Logout
		ui.logout();	
		
		//Load component and login
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		
		//Switch to update view
		log.info("INFO: Switch to update view");
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//Validate news story added
		log.info("INFO: Validate News story added properly");
		verifyNewsStory(testUser1.getDisplayName() + " created a community named " + community.getName() + ".",
							HomepageUIConstants.Discover,
							"Communities", 
							true);

		ui.endTest();
	}

	/**
	* testAddBookmark_ModComm()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: Select a community type - Moderated</B></li>
	*<li><B>Step: Add a bookmark</B></li>
	*<li><B>Verify: that the community and bookmark show up as news story</B></li>
	*</ul>
	*/
	@Test (groups = {"fvtonprem"})
	public void testAddBookmark_ModComm() throws Exception {

		String testName = ui.startTest();
		String url = Data.getData().commonURL;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   											.access(Access.MODERATED)
		   											.description("Test description for testcase " + testName)
		   											.addMember(new Member(CommunityRole.MEMBERS, testUser3))
		   											.build();
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
											.community(community)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description(Data.getData().commonDescription + " " + testName)
											.build();

		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//Create moderated community
		log.info("INFO: Create moderated community");
		community.create(ui);
		
		//Add feed using the left nav bar
		log.info("INFO: Add bookmark to community using left nav bar");
		ui.clickLink(CommunitiesUIConstants.leftNavOverview);
		ui.clickLink(CommunitiesUIConstants.leftNavBookmarks);
		ui.clickLink(DogearUIConstants.AddBookmark);
		
		//Fill out feed form
		log.info("INFO: Fill out bookmark form and save");
		bookmark.create(uiBM);
		
		//Logout
		ui.logout();

		//Load component and login
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		
		//Switch to update view
		log.info("INFO: Switch to update view");
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//Validate news story added
		log.info("INFO: Validate News story added properly");
		verifyNewsStory(testUser1.getDisplayName() + " added the " + bookmark.getTitle() + " bookmark to the " + community.getName() + " community.",
							 HomepageUIConstants.Discover,
							 "Communities", 
							 true);

		ui.endTest();
	}
	
	/**
	* testCreateFeed_ModComm()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: Select a community type - Moderated</B></li>
	*<li><B>Step: Add Feeds Widget</B></li>
	*<li><B>Step: Add Feed to community</B></li>
	*<li><B>Verify: that the community and feed show up as news story</B></li>
	*</ul>
	*/
	@Test (groups = {"fvtonprem"})
	public void testCreateFeed_ModComm() throws Exception {

		String testName = ui.startTest();		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   											.access(Access.MODERATED)
		   											.description("Test description for testcase " + testName)
		   											.addMember(new Member(CommunityRole.MEMBERS, testUser3))
		   											.build();
		
		BaseFeed feed = new BaseFeed.Builder(Data.getData().FeedsTitle, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL)
									.tags(Data.getData().FeedsTag)
									.description(Data.getData().commonDescription).build();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Create moderated community");
		community.create(ui);

		//Customize community - Add the Feeds widget
		log.info("INFO: Adding the " + BaseWidget.FEEDS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.FEEDS);
		
		//Add feed using the left nav bar
		log.info("INFO: Add feed to community using left nav bar");
		ui.clickLink(CommunitiesUIConstants.leftNavOverview);
		ui.clickLink(CommunitiesUIConstants.leftNavFeeds);
		ui.clickLink(FeedsUI.AddFeedLink);

		//Fill out feed form
		log.info("INFO: Fill out feed form and save");
		fUI.addFeed(feed);

		//Logout
		ui.logout();
		
		//Load component and login
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		
		//Switch to update view
		log.info("INFO: Switch to update view");
		ui.clickLinkWait(HomepageUIConstants.Updates);

		//Validate news story added
		log.info("INFO: Validate News story added properly");
		verifyNewsStory(testUser1.getDisplayName() + " added the " + feed.getTitle() + " feed to the " + community.getName() + " community.",
							 HomepageUIConstants.Discover,
							 "Communities", 
							 true);

		ui.endTest();
	}
	
	
	
	public void verifyNewsStory(String NewsStory, String NavOption, String component, boolean Visible){
		
		//select the option you want to use
		ui.clickLink(NavOption);
		
		//Filter by component from Drop-down
		log.info("INFO: Filter component using the dropdown");
		uiHP.filterBy(component);
		
		if(Visible==false){
			//Verify that the news story is there
			if(ui.fluentWaitTextPresentRefresh(NewsStory)){
				log.error("Fail: News story " + NewsStory + " should not be visible");
				Assert.fail("Fail: News story " + NewsStory + " should not be visible");
			}
			}

		else{
			
			if(!driver.isTextPresent(NewsStory)){
				log.error("Fail: Newsstory " + NewsStory + " should be visible");
				Assert.fail("Fail: Newsstory " + NewsStory + " should be visible");
			}
		}

	}
	

}

