package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.WikisUI;


public class Smoke_Communities extends SetUpMethods2{

	protected static Logger log = LoggerFactory.getLogger(LogManager.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;	
	private WikisUI wikiUI;
	private DogearUI uiBM;
	private FilesUI filesUI;
	private User testUser1, testUser2;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		uiBM = DogearUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		}

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		//Load Users
		cfg = TestConfigCustom.getInstance();

		testUser1 = cfg.getUserAllocator().getUser();	
		testUser2 = cfg.getUserAllocator().getUser();	
		}
	
	/**
	 * communityBookmark()
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create an open community </B> </li>
	 *<li><B>Verify: Verify that the Community is created</B> </li>
	 *<li><B>Verify: Verify that you can add a bookmark</B> </li>
	 *<li><B>Verify: Verify that you can edit a bookmark</B> </li>
	 *</ul>
	 */
	@Test (groups = { "level2", "bvt" } )
	public void communityBookmark() throws Exception {

			String testName = ui.startTest();

			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												.access(Access.PUBLIC)
												.tags(Data.getData().commonTag)
												.addMember(new Member(CommunityRole.MEMBERS, testUser2))
												.description("Test Community for " + testName).build();

			BaseDogear bookmark = new BaseDogear.Builder(Data.getData().BookmarkName , Data.getData().BookmarkURL)
												.community(community)
												.tags(Data.getData().BookmarkTag)
												.description(Data.getData().BookmarkDesc)
												.build();
			
			//Load component and login
			log.info("INFO: Load component and login");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1); 
			
			//create community
			log.info("INFO: Create community");
			community.create(ui);				
				
			//Navigate to bookmark widget
			log.info("INFO: Add bookmark to community using left nav bar");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
			ui.clickLink(CommunitiesUIConstants.leftNavBookmarks);
			
			log.info("INFO: Click add bookmark button");
			ui.clickLink(DogearUIConstants.AddBookmark);
			
			//Now add a bookmark
			log.info("INFO: Fill out bookmark form and save");
			bookmark.create(uiBM);
			
			log.info("INFO: Verify new Bookmark");
			verifyBookmarkInCommunity(Data.getData().BookmarkName);
			
			//Change bookmark title
			bookmark.setTitle(Data.getData().EditBookmarkName);
			bookmark.setDescription("edited description for " + testName);
			bookmark.setURL(Data.getData().EditBookmarkURL);
			bookmark.setTags(Data.getData().EditBookmarkTag);
			
			//Now Edit the bookmark
			log.info("INFO: Edit the bookmark");
			bookmark.edit(uiBM);
			
			log.info("INFO: Verify Bookmark was edited");
			verifyBookmarkInCommunity(Data.getData().EditBookmarkName);			
			
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser1);
			
			ui.endTest();
		} 	
	/**
	 * communityWiki()
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a public community and add a page to wiki widget</B> </li>
	 *<li><B>Verify: Verify that the Community is created </B> </li>
	 *<li><B>Verify: Verify that you can add a wiki page to the wiki widget inside a community </B> </li>
	 *</ul>
	 */
	@Test (groups = { "level2", "bvt" } )
	public void communityWiki() throws Exception {

			String testName = ui.startTest();

			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										 .access(Access.PUBLIC)
										 .tags(Data.getData().commonTag)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
										 .description("Test Community for " + testName).build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("Wiki_" + Helper.genDateBasedRand(), PageType.Community)
													.tags("tag1, tag2")
													.description("this is a test description for creating a wiki page")
													.build();
			
			
			//Load component and login
			log.info("INFO: Load component and login");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1); 
			
			//create community
			log.info("INFO: Creating community");
			community.create(ui);				

			ui.addWidget(BaseWidget.WIKI);
			
			//add wiki page
			wikiPage.create(wikiUI);

			//check to see that the page is visible
			Assert.assertTrue(driver.getSingleElement("css=h1[id='wikiPageHeader']")
									.getText().contains(wikiPage.getName()));
			
			
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser1);
			
			ui.endTest();
		} 

	/**
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: If community is onprem then add a handle</B></li>
	*<li><B>Step: Select a community type - Restricted</B></li>
	*<li><B>Step: Add a member to the community</B></li>
	*<li><B>Step: Upload a file to the community</B></li>
	*<li><B>Verify: that the community has being created - code checks for the description enter above and some widgets in the community</B></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "bvt"})
	public void communityAddFile() throws Exception {

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.RESTRICTED)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2)).build();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).build();
				
		//Load component and login
		log.info("INFO: Load component and login");	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//create community
		log.info("INFO: Create community");
		community.create(ui);

		//Upload community-owned file
		log.info("INFO: Upload a file to the community");
		filesUI.uploadFileToCommunity(file);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser1);
		
		ui.endTest();
	}
	
	
	public void verifyBookmarkInCommunity(String Bookmark) throws Exception {
		//Verify that the bookmark is appearing in the bookmark list view
		Assert.assertTrue(driver.isElementPresent("link="+Bookmark));
		ui.fluentWaitPresent("link="+Bookmark);
		log.info("INFO: Verified the bookmark in the bookmark view");
		//Now return to the overview page
		Community_LeftNav_Menu.OVERVIEW.select(ui);
		//Verify that the bookmark is appearing in the Overview
		ui.fluentWaitPresent("link="+Bookmark);
		log.info("INFO: Verified the bookmark in the Overview view");
	}
	
	
}
