package com.ibm.conn.auto.tests.visitor;

import static org.testng.Assert.assertTrue;

import java.util.List;

import com.ibm.conn.auto.webui.constants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_VisitorModel extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_VisitorModel.class);

	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User createExCom, guestUser, adminUser;
	private Member guest;
	private FilesUI filesUI;
	private ForumsUI forumsUI;
	private DogearUI dogearUI;
	private BlogsUI blogsUI;
	private ActivitiesUI actUI;
	private WikisUI wikisUI;
	private FeedsUI feedsUI;
	private String serverURL;
	private APIActivitiesHandler apiActOwner;
	private APICommunitiesHandler apiOwner;
	private Community comAPI;
	private BaseFile file;
	private GatekeeperConfig gkc;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		// Load Users
		createExCom = cfg.getUserAllocator().getGuestModUser();
		guestUser = cfg.getUserAllocator().getGuestUser();
		guest = new Member(CommunityRole.MEMBERS, guestUser);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiActOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, createExCom.getAttribute(cfg.getLoginPreference()), createExCom.getPassword());
		apiOwner = new APICommunitiesHandler(serverURL,
				createExCom.getAttribute(cfg.getLoginPreference()),
				createExCom.getPassword());
		adminUser = cfg.getUserAllocator().getAdminUser();
		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);

		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		forumsUI = ForumsUI.getGui(cfg.getProductName(), driver);
		dogearUI = DogearUI.getGui(cfg.getProductName(), driver);
		blogsUI = BlogsUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		wikisUI = WikisUI.getGui(cfg.getProductName(), driver);
		feedsUI = FeedsUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community</li>
	 * <li><B>Step:</B>Logout and Log in as Visitor</li>
	 * <li><B>Verify:</B>Visitor can see the the correct apps: Communities &
	 * Files but not Apps Menu</li>
	 * <li><B>Verify:</B>Visitor cannot see I'm an Owner and My Communities in
	 * Left Nav Bar</li>
	 * <li><B>Verify:</B>Visitor can see the the community they are a member of</li>
	 * <li><B>Verify:</B>Edited Bookmark is listed in the Overview view</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createCommwithVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();

		//Check Gatekeeper value for Communities Copy Community setting
		User adminUser;
		String gk_flag = "communities-copy-community";
		adminUser = cfg.getUserAllocator().getAdminUser();
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
				gk_flag = "COMMUNITIES_COPY_COMMUNITY";
		}
				
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		boolean value = gkc.getSetting(gk_flag);
		
		// Load component and login
		logger.strongStep("Load communities and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(createExCom);

		//create community
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown");
			logger.strongStep("Create a Community from the Dropdown Menu");
			log.info("INFO: Create a Community");
			community.createFromDropDown(ui);
									
		}else { 	
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' button");
			logger.strongStep("Create a Community");
			log.info("INFO: Create a Community");
			community.create(ui);
		}
				
		//Check for the Tabbed Nav GK flag
		gk_flag = "communities-tabbed-nav";
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
		}
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		value = gkc.getSetting(gk_flag);
		
		//Select Files from menu
		if(value){
			logger.strongStep("Adding guest user from the tabbed navigation menu");
			log.info("INFO: Adding guest user from the tabbed navigation menu");
			Community_TabbedNav_Menu.MEMBERS.select(ui);
		}else{
			logger.strongStep("Adding guest user from the left navigation menu");
			log.info("INFO: Adding guest user from the left navigation menu");
			Community_LeftNav_Menu.MEMBERS.select(ui);
		}
		
		ui.addMemberCommunity(guest);
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);

		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.close();

		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// verify the visitor has correct apps available
		logger.strongStep("Verify that all Visitor can see in the toolbar is "
				+ "Communities and Files in the top toolbar");
		log.info("INFO: Check Communities Link Available");
		Assert.assertTrue(
				ui.fluentWaitElementVisible(CommunitiesUIConstants.CommunitiesLink),
				"ERROR: Communities link not displaying");
		log.info("INFO: Check Files Link Available");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.visitorFilesLink),
				"ERROR: Files link not displaying");
		log.info("INFO: Check Apps Link Not Available");
		Assert.assertTrue(!ui.isElementPresent(BaseUIConstants.MegaMenuApps),
				"ERROR: Apps link is displaying");

		// verify the visitor cannot see I'm an Owner and My Communities
		logger.strongStep("Verify that all Visitor can see in the sidebar is "
				+ "I'm a Member, I'm Following and I'm Invited");
		log.info("INFO: Check I'm an Owner and My Organization Comms links are not available");
		Assert.assertTrue(!ui.isElementPresent(Community_View_Menu.IM_AN_OWNER.getMenuItemLink()),
				"ERROR: I'm an Owner is displaying");
		Assert.assertTrue(!ui.isElementPresent(Community_View_Menu.PUBLIC_COMMUNITIES.getMenuItemLink()),
				"ERROR: My Organization Communities is displaying");

		// verify that visitor can their community and it's restricted
		log.info("INFO: Check the Visitor can see the Community they are a member of");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(CommunitiesUI.getCommunityLink(community)),
				"ERROR: Restricted Visitor Community is not present in I'm a Member view");

		// verify that visitor can enter the community
		logger.strongStep("Open the communities and verify that the visitor can see all the apps");
		log.info("INFO: Open community via link");
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getName(),
				"ERROR: Visitor cannot access the Community"));

		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and Post a Status Update with an uploaded File</li>
	 * <li><B>Verify:</B>Verify that the Success Message appears and that the File attachment is visible</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createVisitorStatusUpdate() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();

		file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.rename(Helper.genDateBasedRand()).build();

		String statusUpdate = "Status update from " + testName;

		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());

		// Post Status Update and Check it appears in Recent Updates
		logger.strongStep("Post Status Update and Check it appears in Recent Updates");
		log.info("INFO: Post Status Update and Check it appears in Recent Updates");
		postVisitorCommunityStatusUpdateAndUploadFile(statusUpdate);

		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();

		apiOwner.deleteCommunity(apiOwner.getCommunity(community
				.getCommunityUUID()));
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community</li>
	 * <li><B>Step:</B>Create a Forum topic and add a file</li>
	 * <li><B>Verify:</B>Validate the forum is created successfully and the file attached correctly</li>
	 * <li><B>Step:</B>Delete the forum topic</li>
	 * <li><B>Verify:</B>Validate the delete message appears</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void startTopicAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();

		BaseForumTopic topic = new BaseForumTopic.Builder("Topic for "
				+ testName).tags(Data.getData().commonTag)
				.description("testing add attachment")
				.addAttachment(Data.getData().file6).partOfCommunity(community)
				.build();

		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());
		
		// Forums - Start a topic, attach a file and then delete the topic
		logger.strongStep("Start a topic, attach a file to it and then delete the topic");
		log.info("INFO: Start a topic, attach a file to it and then delete the topic");
		startForumTopicAndDelete(topic);

		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();

		apiOwner.deleteCommunity(apiOwner.getCommunity(community
				.getCommunityUUID()));
		

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community</li>
	 * <li><B>Step:</B>Add an Important Bookmark</li>
	 * <li><B>Verify:</B>The Add Bookmark button is available and the Bookmark is created successfully
	 * <li><B>Step:</B>Edit the Bookmark Title</li>
	 * <li><B>Verify:</B>Check the title is changed successfully and the bookmark is under Important</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void addBookmarkAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String bookmarkTitle = Data.getData().BookmarkName;

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();

		BaseDogear bookmark = new BaseDogear.Builder(bookmarkTitle,
				Data.getData().BookmarkURL).community(community)
				.tags(Data.getData().BookmarkTag)
				.description(Data.getData().BookmarkDesc).build();

		String editTitle = "EDITED: " + bookmarkTitle;

		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());

		// Bookmarks
		addImportantBookmark(bookmark, editTitle);

		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.close();

		apiOwner.deleteCommunity(apiOwner.getCommunity(community
				.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community</li>
	 * <li><B>Step:</B>Select Files sidebar and upload File</li>
	 * <li><B>Verify:</B>Verify the File has uploaded correctly
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void fileUploadAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				.comFile(true).extension(".jpg").build();
		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());

		// Select Files from left menu
		logger.strongStep("Select Files from the left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(ui);

		// Upload community-owned file
		logger.strongStep("Upload a community-owned file");
		log.info("INFO: Upload a community-owned file");
		if (!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileA.upload(filesUI, gkc);
		else
			fileA.upload(filesUI);

		// Assert the File uploaded correctly
		logger.strongStep("Verify the file has uploaded correctly");
		log.info("INFO: Verify the file has uploaded correctly");
		Assert.assertTrue(ui.fluentWaitTextPresent(fileA.getName()), 
				"ERROR: The file name is not appearing");
		logger.strongStep("Verify the success message displays");
		log.info("INFO: Verify the success message displays");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + fileA.getName()),
				"ERROR: No success message has displayed");
		
		// Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.close();

		apiOwner.deleteCommunity(apiOwner.getCommunity(community
				.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Add a Blogs Widget via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and click new entry link</li>
	 * <li><B>Step:</B>Add a new entry</li>
	 * <li><B>Verify:</B>Verify blog entry is saved</li>
	 * <li><B>Step:</B>Create a blog entry and tick Add as new entry box</li>
	 * <li><B>Verify:</B>Verify blog comment is also a new entry</li>
	 * <li><B>Step:</B>Delete the blog entry</li>
	 * <li><B>Verify:</B>Verify the blog entry has been deleted</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createBlogAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
						.tags(testName + Helper.genDateBasedRand())
						.content("Test description for testcase " + testName)
						.build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		
		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);
		apiOwner.addWidget(comAPI, BaseWidget.BLOG);

		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());

		
		ui.fluentWaitTextPresent("Create Your First Entry");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryLink);
				
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogsUI);
		
		//Verify the blog entry is saved
		logger.strongStep("Verify that the blog title is displayed");
		log.info("INFO: Verify that the blog is displayed in the list");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()),
					          "ERROR: Blog title is not visible in the list");
		
		//Add a comment, and tick add this as a new entry
		blogsUI.createBlogCommentAndAddAsNewEntry(comment);
						
		//Navigate back to Blogs to check new entry exists
		logger.strongStep("Select Files from the left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.BLOG.select(ui);
		
		//Verify that the comment is also an entry
		logger.strongStep("Verify that the comment added is also an entry");
		log.info("INFO: Verify that the comment added is also an entry");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment.getContent()),
				"ERROR: Blog comment is not an entry");
		ui.clickLinkWait("css=a:contains(Re: " + blogEntry.getTitle() + ")");
		
		//delete blog
		logger.strongStep("Delete blog entry");
		log.info("INFO: Delete the blog entry");
		ui.clickLinkWait(BlogsUIConstants.BlogsMoreActions);
		ui.clickLinkWait(BlogsUIConstants.deleteEntry);
		ui.switchToTopFrame();
		ui.clickLinkWait(BlogsUIConstants.deleteEntryOKButton);
						
		//Verify that the entry is no longer displayed
		logger.strongStep("Verify that the blog title is no longer displayed");
		log.info("INFO: Verify that the blog is no longer displayed in the list");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("Re: "+ blogEntry.getTitle() + ""),
					          "ERROR: Blog title is still visible in the list");

		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Add a Blogs Ideation Widget via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and Ideation Blogs</li>
	 * <li><B>Step:</B>Click Contribute an Idea</li>
	 * <li><B>Step:</B>Create a ideation blog entry</li>
	 * <li><B>Verify:</B>Verify ideation blog created successfully with Vote Button and Title appearing</li>
	 * <li><B>Step:</B>Vote in the ideation blog</li>
	 * <li><B>Verify:</B>Confirm the vote registers</li>
	 * <li><B>Step:</B>Add a comment to the ideation blog</li>
	 * <li><B>Verify:</B>Confirm comment has added correctly</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createIdeationBlogAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();

		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
			+ Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.commHandle(
					Data.getData().commonHandle
							+ Helper.genDateBasedRandVal())
			.access(Access.RESTRICTED).allowExternalUserAccess(true)
			.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
		.tags(testName + Helper.genDateBasedRand())
		.content("Test description for testcase " + testName)
		.build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();

	
		// create a community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);
		apiOwner.addWidget(comAPI, BaseWidget.IDEATION_BLOG);
		
		// Load component and login
		logger.strongStep("Load component and login");
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		// click link to enter the community
		logger.strongStep("Open the community");
		log.info("INFO: Open community via link");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.fluentWaitTextPresent(community.getName());
		
		//Navigate to Ideation Blogs to create a new Ideation Blog
		logger.strongStep("Select Ideation Blogs from the left navigation menu");
		log.info("INFO: Select Ideation Blogs from left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		
		//Click Contribute an Idea Button
		ui.clickLinkWait(BlogsUIConstants.contributeAnIdeaButton);
		
		//Add an Entry
		log.info("INFO: Add a new Ideation Idea");
		blogsUI.createBlogEntry(blogEntry, false);
		
		//Verify ideation blog created successfully
		logger.strongStep("Verify that the ideation entry was created successfully");
		log.info("INFO: Verify that the ideation entry was created successfully");
		Assert.assertTrue(ui.fluentWaitElementVisible(BlogsUIConstants.VoteBtn), "ERROR: The vote button has not appeared");
		Assert.assertEquals(ui.getElementText(BlogsUIConstants.ideationBlogTitle), blogEntry.getTitle());
		
		//Click Vote Button and confirm it registers
		logger.strongStep("Verify that the visitor can vote in the Ideation Blog");
		log.info("INFO: Verify that the visitor can vote in the Ideation Blog");
		ui.clickLinkWait(BlogsUIConstants.VoteBtn);
		// Confirm wording becomes Voted and Vote number is 1
		Assert.assertEquals(ui.getElementText(BlogsUIConstants.VoteBtn), "Voted");
		Assert.assertEquals(ui.getElementText(BlogsUIConstants.VoteNumber), "1");
		
		//Ensure Visitor can leave a comment
		logger.strongStep("Verify that the visitor can post a comment in the Ideation Blog");
		log.info("INFO: Verify that the visitor can post a comment in the Ideation Blog");
		blogsUI.createBlogComment(comment);
		Assert.assertTrue(ui.fluentWaitTextPresent(comment.getContent()),
				"ERROR: Blog comment did not post");
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Add an Activities Widget via API</li>
	 * <li><B>Step:</B>Create an Activity via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and Activities</li>
	 * <li><B>Step:</B>Create an entry</li>
	 * <li><B>Verify:</B>Verify entry created successfully</li>
	 * <li><B>Step:</B>Add a section</li>
	 * <li><B>Verify:</B>Verify the section was created</li>
	 * <li><B>Step:</B>Add entry to the section</li>
	 * <li><B>Verify:</B>Verify the entry was created successfully</li>
	 * <li><B>Step:</B>Add a ToDo Item</li>
	 * <li><B>Verify:</B>Verify Item is successfully created</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createActivityAsVisitor() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();
		String sectionTitle = Data.getData().Section_InputText_Title_Data + testName + Helper.genDateBasedRandVal();
		
		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
					+ Helper.genDateBasedRandVal())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.commHandle(
							Data.getData().commonHandle
									+ Helper.genDateBasedRandVal())
					.access(Access.RESTRICTED).allowExternalUserAccess(true)
					.build();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.goal("Goal for "+ testName)
												.community(community)
												.build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry in " + activity.getName() + Helper.genDateBasedRandVal())
				.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				.build();

		BaseActivityEntry sectionEntry = BaseActivityEntry.builder(testName + " entry in " + sectionTitle + Helper.genDateBasedRandVal())
				.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				.build();
		
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				.build();

		
		//Create community
		log.info("INFO: Create community using API");
		logger.strongStep("Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//Add the events widget
		log.info("INFO: Add events widget to the Community using API");
		logger.strongStep("Add the events widget to the community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Create activity
		log.info("INFO: Create activity using API");	
		logger.strongStep("Create activity using API");
		activity.createAPI(apiActOwner, community);
		
		// Load the component and login
		logger.strongStep("Load Activities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);
		
		//Navigate to member communities view
		log.info("INFO: Navigate to the member community views");
		logger.strongStep("Navigate to member communities");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));	
				
		//Click on the Activities link in the navbar and open Activity link
		log.info("INFO: Select Activites in the left navigation");
		logger.strongStep("Navigate to the API community");
		Community_LeftNav_Menu.ACTIVITIES.select(ui);		
		log.info("INFO: Open activity");
		logger.strongStep("Open activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		//Add entry
		log.info("INFO: Create entry");
		logger.strongStep("Add entry");
		entry.create(actUI);
		
		//Verify entry created successfully		
		log.info("INFO: Verify that entry link displays");
		logger.strongStep("Verify file link displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(entry.getTitle()), 
				"ERROR: " + entry.getTitle() + " is not displaying");
		
		//Add section
		log.info("INFO: Add section to the activity");
		logger.strongStep("Add a new section to the activity");
		actUI.addSection(sectionTitle);
		
		//Verify section title
		log.info("INFO: Verify that section title is present");
		logger.strongStep("Verify that section title is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(sectionTitle), 
						"Title for Section is missing");
		
		//Add Entry to the Section
		log.info("INFO: look for Section Action Menu");
		logger.strongStep("Look for 'Section Action Menu'");
		ui.fluentWaitPresent(ActivitiesUIConstants.Section_Action_Menu);
		ui.clickLinkWait(ActivitiesUIConstants.Section_Action_Menu);
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);
		log.info("INFO: Create entry");
		logger.strongStep("Add entry");
		sectionEntry.create(actUI);
		
		//Verify entry created successfully		
		log.info("INFO: Verify that section entry displays");
		logger.strongStep("Verify section entry displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(sectionEntry.getTitle()), 
						"ERROR: " + sectionEntry.getTitle() + " is not displaying");
		
		//Edit Section Name
		ui.clickLinkWait(ActivitiesUIConstants.Section_Action_Menu);
		ui.clickLinkWait(ActivitiesUIConstants.editSectionOption);
		ui.clearText(ActivitiesUIConstants.editSectionTitle);
		String newSectionTitle = "EDITED: " + sectionTitle;
		ui.typeText(ActivitiesUIConstants.editSectionTitle, newSectionTitle);
		ui.clickButton("Save");
		
		//Verify entry created successfully		
		log.info("INFO: Verify that the New Section title is present");
		logger.strongStep("Verify that New Section title is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(newSectionTitle), 
						"New Title for Section is missing");
		
		//Add todo
		log.info("INFO: Add 'todo:Add File' for Activity");
		logger.strongStep("Add 'todo:Add File' for Activity");
		toDo.create(actUI);
		
		//Verify entry info
		log.info("INFO: Validate that 'todo' is present");
		logger.strongStep("Validate that 'todo' is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(toDo.getTitle()), 
				"ERROR: ToDo title not visible");
		
		ui.endTest();
			
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Add a Wiki Widget via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and Wikis</li>
	 * <li><B>Step:</B>Create a peer page</li>
	 * <li><B>Step:</B>Create a child page</li>
	 * <li><B>Step:</B>Upload a file to the child wiki</li>
	 * <li><B>Verify:</B>Verify the file uploaded successfully</li>
	 * <li><B>Verify:</B>Verify the child wiki was created successfully</li>
	 * <li><B>Verify:</B>Verify the peer wiki was created successfully</li>
	 * <li><B>Step:</B>Like the wiki page</li>
	 * <li><B>Verify:</B>Verify wiki page is liked</li>
	 * <li><B>Step:</B>Add a comment to the child page</li>
	 * <li><B>Verify:</B>Verify the comment was created successfully</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createWikiPageAsVisitor() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();
			
		BaseFile file = new BaseFile.Builder("Desert.jpg")
					.extension(".jpg")
					.rename(Helper.genDateBasedRand())
					.build();
		
		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
					+ Helper.genDateBasedRandVal())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.commHandle(
							Data.getData().commonHandle
									+ Helper.genDateBasedRandVal())
					.access(Access.RESTRICTED).allowExternalUserAccess(true)
					.build();
		
		BaseWikiPage wikiPeer = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
					.description("this is a test description for creating a Peer wiki page")			
					.tags("tag1" + "," + "tag2")	
					.build();
		
		BaseWikiPage wikiChild = new BaseWikiPage.Builder(testName + " Child page", PageType.Child)
					.description("this is a test description for creating a Child wiki page")			
					.tags("tag1" + "," + "tag2")	
					.build();

		//Create community
		log.info("INFO: Create community using API");
		logger.strongStep("Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//Add the wikis widget
		log.info("INFO: Add wikis widget to the Community using API");
		logger.strongStep("Add the wikis widget to the community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		//Navigate to member communities view
		log.info("INFO: Navigate to the member community views");
		logger.strongStep("Navigate to member communities");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));	
				
		logger.strongStep("Select Wikis from left navigation menu");
		log.info("INFO: Select Wikis from left navigation menu");
		Community_LeftNav_Menu.WIKI.select(ui);
				
		//Create a peer page
		logger.strongStep("Add a Peer Page to the wiki");
		log.info("INFO: Add Peer page to wiki");
		wikiPeer.create(wikisUI);
		
		//Create a child page
		logger.strongStep("Create a Wiki Child Page inside the Peer Page");
		log.info("INFO: Create a Wiki Child Page inside the Peer Page");
		wikiChild.create(wikisUI);
				
		//Upload file and confirm file has been added
		logger.strongStep("Upload a file to wiki");
		log.info("INFO: Upload file to wiki");
		wikisUI.uploadAttachment(file.getRename(), file.getExtension(), file.getName());
		Assert.assertTrue(ui.fluentWaitTextPresent(file.getName()));
		
		//Verify Visitor can see the Peer and Child Page
		logger.weakStep("Validate that the peer page exists");
		log.info("INFO: Validating peer page exist");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPeer.getName()),
								  "ERROR:" + wikiPeer.getName() + "Page does not exist");
				
		logger.weakStep("Validate that the child page exists");
		log.info("INFO: Validating child page exist");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiChild.getName()),
				  				  "ERROR:" + wikiChild.getName() + "Page does not exist");
		//Like the page 
		logger.strongStep("Like the wiki page");
		log.info("INFO: Like the wiki page");
		wikisUI.likeUnlikePage("Like");
				
		//Validate You like this message 
		logger.strongStep("Validate that you like the message");
		log.info("INFO: Validate you like this message shows up");
		Assert.assertTrue(ui.fluentWaitPresent(WikisUIConstants.LikeMessage),
							"ERROR: Did Not Like The Message");
		
		//Add a comment and verify that the comment is added
		logger.strongStep("Add a comment to the Child Page");
		log.info("INFO: Add a comment and verify that the comment is added");
		wikisUI.addComment(Data.getData().Comment_For_Public_Wiki);
		//Validate comment exists
		logger.weakStep("Verify that comment was added");
		log.info("INFO: Checking for comment");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().Comment_For_Public_Wiki),
							"ERROR: Did Not Find Comment For Child Wiki");
	
		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create a Community with External Access for Visitor</li>
	 * <li><B>Step:</B>Create a restricted community via API with name, tag,
	 * description, handle and external access enabled</li>
	 * <li><B>Step:</B>Add a Visitor as a member of the community via API</li>
	 * <li><B>Step:</B>Add a Feed Widget via API</li>
	 * <li><B>Step:</B>Log in as Visitor</li>
	 * <li><B>Step:</B>Navigate to Community and click "Add a Feed"</li>
	 * <li><B>Step:</B>Add a Feed</li>
	 * <li><B>Verify:</B>Verify the Feeds success message displays</li>
	 * </ul>
	 */
	@Test(groups = { "visitor" })
	public void createFeedAsVisitor() throws Exception{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(
						Data.getData().commonHandle
								+ Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED).allowExternalUserAccess(true)
				.build();
		
		BaseFeed feed = new BaseFeed.Builder(Data.getData().FeedsTitle + Helper.genDateBasedRandVal(), cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL)
		.description(Data.getData().commonDescription)
		.tags(Data.getData().MultiFeedsTag)
		.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);
				
		//add widget
		logger.strongStep("Add Feeds widget to community");
		log.info("INFO: Add Feeds widget to community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		//GUI
		//Load component and login
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);
				
		//Navigate to member communities view
		log.info("INFO: Navigate to the member community views");
		logger.strongStep("Navigate to member communities");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));	
		
		//Click Add Feed link
		logger.strongStep("Click on the 'Add Feed' Link");
		log.info("INFO: Select add feed link");
		ui.clickLinkWait(CommunitiesUIConstants.AddAFeed);
				
		//Add the feed
		logger.strongStep("Add the feed to the community");
		log.info("INFO: Add the feed to the community");
		feedsUI.addFeed(feed);

		//Test feeds success message
		logger.weakStep("Verify that 'feeds success' message is posted");
		log.info("Test that feeds success message is posted");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FeedSuccessMsg),
							"Error : Feeds success message is not shown properly");
		
		ui.endTest();
	}	
	
	@Test(groups = { "visitor" })
	public void checkAccessAsVisitor() throws Exception{
		//Check visitor can't access User Profile link
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();
		
		String ownerProfile = "css=img[class='otherPeople32'][alt*='" + createExCom.getDisplayName() + "']";
		
		// Create a restricted Community with external access
		BaseCommunity community = new BaseCommunity.Builder(testName
					+ Helper.genDateBasedRandVal())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.commHandle(
							Data.getData().commonHandle
									+ Helper.genDateBasedRandVal())
					.access(Access.RESTRICTED).allowExternalUserAccess(true)
					.build();

		//Create community
		log.info("INFO: Create community using API");
		logger.strongStep("Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		apiOwner.addMemberToCommunity(guestUser, comAPI, Role.MEMBER);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser);

		//Navigate to member communities view
		log.info("INFO: Navigate to the member community views");
		logger.strongStep("Navigate to member communities");
		ui.fluentWaitTextPresentRefresh(community.getName());
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));	
				
		logger.strongStep("Select Community Owners Profile and confirm the correct Visitor access");
		log.info("INFO: Select Community Owner and confirm business card");
		ui.clickLinkWait(ownerProfile);
	
		//Assert Visitor cannot access the profile link
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.sendEmailLink));
		
		ui.endTest();
	}
	/* Private UI interaction methods */
	
	private void postVisitorCommunityStatusUpdateAndUploadFile(
			String statusUpdate) throws Exception {

		log.info("INFO: Add status update to community using left nav bar");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		postCommunityUpdate(statusUpdate);
		checkRecentUpdates(statusUpdate);
	}

	private void postCommunityUpdate(String statusUpdate) throws Exception {

		log.info("INFO: Posting the community status update: " + statusUpdate);
		if (driver.isElementPresent(HomepageUIConstants.EnterMentionsStatusUpdate)) {
			driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).type(
					statusUpdate);
		} else {
			List<Element> frames = driver
					.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
			int frameCount = 0;
			for (Element frame : frames) {
				frameCount++;
				log.info("INFO: Frame toString: " + frame.toString());
				log.info("INFO: Frame location: " + frame.getLocation());
				// The first CK Editor iframe will be for the embedded sharebox
				if (frameCount == 1) {
					log.info("INFO: Switching to Frame: " + frameCount);
					driver.switchToFrame().selectFrameByElement(frame);
				}
			}

			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(
					statusUpdate);

			log.info("INFO: Returning to top Frame to click 'Post' button");
			driver.switchToFrame().returnToTopFrame();
		}
		ui.clickLinkWait(HomepageUIConstants.AttachAFile);
		// Add a file to the status update
		filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		ui.clickButton("OK");
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);
		log.info("INFO: Verify that the update and file uploaded correctly");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage.toString()));
		Assert.assertTrue(ui.fluentWaitTextPresent(file.getName()));

	}

	private void checkRecentUpdates(String status) {

		// Check that the visitor can see the status update and file posted in
		// Recent Updates
		log.info("INFO: Check visitor can see their status update");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		log.info("INFO: Verify that the update and file uploaded correctly");
		Assert.assertTrue(ui.fluentWaitTextPresent(status));
		Assert.assertTrue(ui.fluentWaitTextPresent(file.getName()));

		log.info("INFO: Verify Visitor can see updates from other members");
	}

	private void addImportantBookmark(BaseDogear bookmark, String editTitle) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		// create bookmark
		Community_LeftNav_Menu.BOOKMARK.select(ui);
		// Wait until the "add bookmark" button is visible
		logger.strongStep("Navigate to Bookmark and confirm Add Bookmark button is visible");
		log.info("INFO: Clicking on " + Community_LeftNav_Menu.BOOKMARK);
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
				"ERROR: The button to add a bookmark was not found");
		ui.clickLinkWait(DogearUIConstants.AddBookmark);

		// Now add a bookmark and tick the Add to Important Bookmarks checkbox
		logger.strongStep("Fill out bookmark form and save");
		log.info("INFO: Fill out bookmark form and save");
		driver.getSingleElement("css=#addBookmarkImportant").click();
		bookmark.create(dogearUI);

		// Verify that the bookmark was created successful and appears in
		// important Bookmarks
		logger.strongStep("Validate that the bookmark was created");
		log.info("INFO: Checking that the bookmark was generated successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()),
				"ERROR: The bookmark was not generated successfully");

		editBookmarkTitle(editTitle);

		// Verify that the bookmark is present
		logger.strongStep("Validate that the renamed bookmark title displays in 'My Bookmarks' view");
		log.info("INFO: Validate renamed bookmark: " + editTitle
				+ " displays in My Bookmarks view");
		assertTrue(driver.isTextPresent(editTitle), "ERROR: Bookmark: "
				+ editTitle + " was found");

		Community_LeftNav_Menu.OVERVIEW.select(ui);
		String importantBookmarkElement = "css=div[id='importantBookmarks']";
		logger.strongStep("Checking that the important bookmark widget is visible");
		log.info("INFO: Checking that the important bookmark widget is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(importantBookmarkElement),
				"ERROR: The important Bookmark Element was not found");
		
		String linkInsideImportantBookmark = "css=a[class='action bidiAware'][href='"
				+ bookmark.getURL() + "']";
		logger.strongStep("Checking that the created bookmark is listed under Important Bookmarks");
		log.info("INFO: Checking that the created bookmark is listed under Important Bookmarks");
		Assert.assertTrue(ui.fluentWaitElementVisible(linkInsideImportantBookmark),
				"ERROR: The bookmark is not displaying inside Important Bookmark");
	}

	private void editBookmarkTitle(String editTitle) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		// Edit the title of existing bookmark
		logger.strongStep("Edit the title of the existing bookmark");
		log.info("INFO: Edit the title of the existing bookmark");
		ui.getFirstVisibleElement(DogearUIConstants.MoreLink).click();
		ui.clickLinkWait(DogearUIConstants.EditLink);
		ui.clearText(DogearUIConstants.Form_EditBookmark_Title);
		ui.typeText(DogearUIConstants.Form_EditBookmark_Title, editTitle);
		ui.clickButton("Save");
		ui.fluentWaitTextPresent(editTitle);
	}

	private void startForumTopicAndDelete(BaseForumTopic topic) {

		// Create a new topic inside the Forum
		log.info("INFO: Create a new topic");
		topic.create(forumsUI);

		log.info("INFO: Verify Attachments area displays");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachHeader),
				"ERROR: Attachment area not found");

		log.info("INFO: Verify attachment thumbnail displays");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachThumbnail),
				"ERROR: Attachment thumbnail not found");

		// delete topic
		log.info("INFO: Delete the topic");
		deleteTopic(topic);

		log.info("INFO: Validate that the " + topic.getTitle() + " was successfully deleted");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.topicDeleteMsg));
	}

	private void deleteTopic(BaseForumTopic topic) {

		log.info("INFO: Select Delete from menu");
		String deleteLink = "link=Delete";
		ui.clickLinkWait(deleteLink);

		log.info("INFO: Select delete button");
		ui.clickLinkWait(ForumsUIConstants.deleteButton);
	}

}
