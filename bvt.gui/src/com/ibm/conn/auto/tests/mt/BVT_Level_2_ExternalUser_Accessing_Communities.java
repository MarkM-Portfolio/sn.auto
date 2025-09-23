package com.ibm.conn.auto.tests.mt;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;


public class BVT_Level_2_ExternalUser_Accessing_Communities extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_ExternalUser_Accessing_Communities.class);
	private CommunitiesUI ui;
	private ForumsUI forumUI;
	private ActivitiesUI actUI;
	private BlogsUI blogUI;
	private WikisUI wikiUI;
	private CalendarUI calUI;
	private DogearUI dogearUI;
	private FilesUI filesUI;
	private APIFileHandler fileHandler;
	private TestConfigCustom cfg;
	private User testUser_orgA,exttestUser1;
	private String  serverURL_MT_orgA;
	private APIActivitiesHandler activityApiOwner;
	private APICommunitiesHandler apiHandler;
	
	String serverURL ;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		exttestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		cfg.getUserAllocator().getGroupUser("external_users_orga");
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();	
		testConfig.useBrowserUrl_Mt_OrgB();	
		cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());
		fileHandler = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		activityApiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		forumUI = ForumsUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		dogearUI = DogearUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		
	}
		
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating an external listed community and add an activity widget to it</li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Add activity to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to members tab</li>
	 * <li><B>Verify: </B>Verify owner of community and external user as a member</li>
	 * <li><B>Step: </B>Navigate to activity tab</li> 
	 * <li><B>Verify: </B>Verify community activity created info</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * 
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingExternalRestrictedCommunityWithOrgA() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.dueDateRandom()
												.useCalPick(true)
												.goal(Data.getData().commonDescription + testName)
												.community(orgaExternalRestricted)
												.build();
		
		Member member = new Member(CommunityRole.MEMBERS, exttestUser1);

		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Add activity to community using API");
		log.info("INFO: Add activity to community using API");
		CommunityActivityEvents.createCommunityActivity(activity, orgaExternalRestricted, testUser_orgA,
				activityApiOwner, apiHandler, comAPI);
		
		logger.strongStep("Add activities widget if not present");
		log.info("INFO: Add activities widget if not present");
		if(apiHandler.getWidgetID(comAPI.getUuid(), "Activities").isEmpty()) {
			orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler, BaseWidget.ACTIVITIES);
		}
				
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate the Owner appears on the Members full page");
		log.info("INFO: Validate the Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()), "Owner is present in community");
		
		logger.strongStep("Validate the Members appears on the Members full page");
		log.info("INFO: Validate the Member appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + exttestUser1.getDisplayName()), "Member is present in community");
		
		logger.strongStep("Click on the Activities tab");
		log.info("INFO: Click on the Activities tab");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui);
		
		logger.strongStep("Verify activity inside new community");
		log.info("INFO:Verify activity inside new community");
		actUI.verifyActivityInfo(logger, activity);
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create an external listed Community for UserA and add
	 * ExternalUser as Member then create and verify Sub-community</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Internal user</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Step: </B>Create a Sub Community </li>
	 * <li><B>Verify: </B>Verify Sub Community is created </li>
	 * <li><B>Verify: </B>Validate are Owners & Members from the parent community have been added to child sub community </li>
	 * <li><B>Verify: </B>Validate the Owner appears on the Members full page </li>
	 * <li><B>Verify: </B>Validate the Members appears on the Members full page </li>
	 * <li><B>Step: </B>Create a new Forum Topic on default Forum </li>
	 * <li><B>Verify: </B>Verify newly created ForumTopic Title </li>
	 * <li><B>Step: </B>Clicking on the Communities link </li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createAndverifyExternalRestrictedSubcommunityOrgA(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String subcommName = "SubCommunity" + Helper.genDateBasedRand();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(subcommName).access(BaseSubCommunity.Access.RESTRICTED)
															.UseParentmembers(true)
															.tags(Data.getData().commonTag + Helper.genDateBasedRand())
															.description("Test creating a restricted subcommunity " + testName)
															.build();
		
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRandVal())
				  									  .tags(Data.getData().ForumTopicTag)
				  									  .description(Data.getData().commonDescription)
				  									  .build();
		
		
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		//Create and Verify an External Restricted Sub-Community
		logger.strongStep("Create a Sub Community");
		log.info("INFO: Creating Sub Community ");
		subCommunity.create(ui);
		
		logger.strongStep("Verify Sub Community is created");
		log.info("INFO: Verify Sub Community is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),"Subcommunity is created");
		
		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		logger.strongStep("Validate the Owner appears on the Members full page");
		log.info("INFO: Validate the Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()), "Owner is present in Subcommunity");
		
		logger.strongStep("Validate the Members appears on the Members full page");
		log.info("INFO: Validate the Member appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + exttestUser1.getDisplayName()), "Member is present in Subcommunity");
		
		//Create a forum
		logger.strongStep("Create a new Forum Topic on default Forum");
		log.info("INFO: Create a new Forum  on default Forum");
		Community_TabbedNav_Menu.FORUMS.select(ui);
		forumTopic.create(forumUI);
		
		logger.strongStep("Verify newly created ForumTopic Title");
		log.info("INFO: Verify newly created ForumTopic Title");
		Assert.assertTrue(ui.fluentWaitTextPresent(forumTopic.getTitle()), "Forum topic is present in the community");
		
		logger.strongStep("Clicking on the Communities link");
		log.info("INFO: Clicking on the Communities link");
		ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);

		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a external listed Community Forum Topic for Internal User 
	 * and reply to that Forum topic from External User</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Verify: </B>Validate the Owner appears on the Members full page </li>
	 * <li><B>Verify: </B>Validate the Members appears on the Members full page </li>
	 * <li><B>Step: </B>Create a new Forum Topic on default Forum </li>
	 * <li><B>Verify: </B>Verify newly created ForumTopic Title </li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Forums page</li>
	 * <li><B>Step: </B>Select the Forum created earlier from Internal User's log in</li>
	 * <li><B>Step: </B>Create a reply to the Forum topic</li>
	 * <li><B>Step: </B>Select the forum created by Internal User</li>
	 * <li><B>Step: </B>Navigate back to the Forum created by Internal User</li>
	 * <li><B>Verify: </B>Validate that only one reply to the Forum topic was recorded</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void replyToForumTopicFromExternalUserOrgA(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRandVal())
				  									  .tags(Data.getData().ForumTopicTag)
				  									  .description(Data.getData().commonDescription)
				  									  .build();
		
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as Org's Internal User: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab");
		Community_TabbedNav_Menu.MEMBERS.select(ui,2);

		logger.strongStep("Validate the Owner appears on the Members full page");
		log.info("INFO: Validate the Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()),"Owner is displayed in Community");

		logger.strongStep("Validate the Members appears on the Members full page");
		log.info("INFO: Validate the Member appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + exttestUser1.getDisplayName()),"Member is displayed in Community");

		//Create a forum topic
		logger.strongStep("Create a new Forum Topic on default Forum");
		log.info("INFO: Create a new Forum Topic on default Forum");
		Community_TabbedNav_Menu.FORUMS.select(ui,1);
		forumTopic.create(forumUI);

		logger.strongStep("Verify newly created ForumTopic Title");
		log.info("INFO: Verify newly created ForumTopic Title");
		Assert.assertTrue(ui.fluentWaitTextPresent(forumTopic.getTitle()), "Forum topic has displayed");

		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();

		logger.strongStep("Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to Forums page");
		log.info("INFO: Navigate to Forums page");
		Community_TabbedNav_Menu.FORUMS.select(ui,2);
		
		logger.strongStep("Select the Forum created earlier");
		log.info("INFO: Select the Forum created earlier");
		ui.clickLinkWait("link=" + forumTopic.getTitle());

		//Reply to topic
		logger.strongStep("Create a reply to the Forum topic");
		log.info("INFO: Create a reply to the Forum topic");
		forumUI.replyToTopic(forumTopic);

		//Select the forum created by Internal User
		logger.strongStep("Navigate back to the Forum created by Internal User" + testUser_orgA.getDisplayName());
		log.info("INFO: Navigate back to the Forum created by Internal User " + testUser_orgA.getDisplayName());
		ui.clickLinkWithJavascript(forumUI.getBackToCommunityForum(orgaExternalRestricted));

		//Verify that the count of replies to the topic created above is 1
		logger.weakStep("Validate that only one reply to the Forum topic was recorded");
		log.info("INFO: Validate that only one reply to the Forum topic was recorded");
		ui.fluentWaitPresent(ForumsUIConstants.First_Topic_Number_of_Replies);
		String repliesNum = driver.getSingleElement(ForumsUIConstants.First_Topic_Number_of_Replies).getText();
		Assert.assertTrue(repliesNum.compareToIgnoreCase("1") == 0,
				"Number of replies is '" + repliesNum + "', expected '1'");

		// Delete the community logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a external listed Community Forum Topic for Internal User 
	 * and reply to that Forum topic from External User</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Forums page</li>
	 * <li><B>Step: </B>Create a new Forum Topic on default Forum</li>
	 * <li><B>Step: </B>Verify newly created ForumTopic Title</li>
	 * <li><B>Step: </B>Edit newly created ForumTopic</li>
	 * <li><B>Step: </B>Navigate back to the Forum created by Internal user</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createAndeditForumTopicforExternalUserOrgA(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRandVal())
				  									  .tags(Data.getData().ForumTopicTag)
				  									  .description(Data.getData().commonDescription)
				  									  .build();
		
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to Forums page");
		log.info("INFO: Navigate to Forums page");
		Community_TabbedNav_Menu.FORUMS.select(ui,1);

		//Create a forum topic
		logger.strongStep("Create a new Forum Topic on default Forum");
		log.info("INFO: Create a new Forum Topic on default Forum");
		Community_TabbedNav_Menu.FORUMS.select(ui,1);
		forumTopic.create(forumUI);
		
		ui.waitForPageLoaded(driver);

		logger.strongStep("Verify newly created ForumTopic Title");
		log.info("INFO: Verify newly created ForumTopic Title");
		Assert.assertTrue(ui.fluentWaitTextPresent(forumTopic.getTitle()), "Forum topic has displayed");	
		
		logger.strongStep("Edit newly created ForumTopic");
		log.info("INFO: Edit newly created ForumTopic");
		forumUI.editDescriptionInForumTopic(forumTopic, "Edit the ForumTopic" +testName);

		//Navigate back to the main Forum created by External user
		logger.strongStep("Navigate back to the Forum created by External user " + exttestUser1.getDisplayName());
		log.info("INFO: Navigate back to the Forum created by External user " + exttestUser1.getDisplayName());
		ui.clickLinkWait(forumUI.getBackToCommunityForum(orgaExternalRestricted));

		//Delete the community logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating post in recent update of external listed community 
	 * and verify it with external user</li>
	 * <li><B>Step: </B>Create community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to recent updates tab</li>
	 * <li><B>Step: </B>Type a message and post it</li>
	 * <li><B>Verify: </B>Verify the message gets posted</li>
	 * <li><B>Step: </B>Logout as an org a internal user</li>
	 * <li><B>Step: </B>login in as an org a external user</li> 
	 * <li><B>Step: </B>Navigate to recent updates tab</li> 
	 * <li><B>Verify: </B>Verify message posted by internal user in recent updates tab</li> 
	 * <li><B>Step: </B>Like the message posted by internal user</ul>
	 * <li><B>Verify: </B>Verify like count is increased by 1</li> 
	 * <li><B>Step: </B>Type a message as an external user in recent udpates and post it</li>
	 * <li><B>Verify: </B>Verify the message gets posted</li>
	 * <li><B>Step: </B>Delete community</li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingEntryInRecentUpdateWithExternalCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
					
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);
			
		logger.strongStep("Click on the Recent Update tab");
		log.info("INFO: Click on the Recent Update tab");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui,1);
		
		logger.strongStep("Post Message in recent updates");
		log.info("INFO: Post Message in recent updates");
		commentOrPostMessage(CommunitiesUIConstants.iframePostMessage,Data.getData().UpdateStatus.trim(),CommunitiesUIConstants.StatusPost);
		
		// Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
				"Status message is displayed");
				
		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();
		
		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Navigate to the recent update tab");
		log.info("INFO: Navigate to the recent update tab");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui,1);
		
		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message posted by internal user");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
						"Status message is displayed");
		
		logger.strongStep("Verify there are no likes");
		log.info("INFO: Verify there are no likes");
		verifyLikeCount(" ");
		
		log.info("INFO: like Status message posted by internal user");
		driver.getFirstElement(CommunitiesUIConstants.LikeStatusMessage).click();
		
		logger.strongStep("Verify like count is increased by 1");
		log.info("INFO: Verify like count is increased by 1");
		verifyLikeCount("1");
		
		logger.strongStep("Click on link to comment on status message");
		log.info("INFO: Click on link to comment on status message");
		driver.getFirstElement(CommunitiesUIConstants.commentStatusMessage).click();
		
		logger.strongStep("Add comment as an external user");
		log.info("INFO: Add comment as an external user");
		commentOrPostMessage(CommunitiesUIConstants.commentiframe,"Comment by external User",CommunitiesUIConstants.commentPostButton);
		
		logger.strongStep("Post status as an external user");
		log.info("INFO: Post status as an external user");
		commentOrPostMessage(CommunitiesUIConstants.iframePostMessage,"Status message from external User",CommunitiesUIConstants.StatusPost);
	
		// Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(
				driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains("Status message from external User"),
				"Status message is getting displayed");
				
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a external listed Community Ideation Blog for Internal User 
	 * and comment and vote to that Ideation Blog from External User</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Add Ideation Blog widget</li>
	 * <li><B>Step: </B>Open Ideation Blogs and login as Internal User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Ideation Blog page</li>
	 * <li><B>Step: </B>Click on newly created Ideation Blog</li>
	 * <li><B>Step: </B>Click on New Idea button</li>
	 * <li><B>Step: </B>Create a new idea</li>
	 * <li><B>Verify: </B>Verify that new idea exists</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component Blogs and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Ideation Blog page</li>
	 * <li><B>Step: </B>Click on default Ideation Blog created by Internal user</li>
	 * <li><B>Step: </B>Click on  New Idea button</li>
	 * <li><B>Step: </B>Add a new Entry as an External user</li>
	 * <li><B>Step: </B>Select the Add a comment link for entry</li>
	 * <li><B>Step: </B>Type in the comment form</li>
	 * <li><B>Step: </B>Submit the comment</li>
	 * <li><B>Verify: </B>Verify that the comment is present</li>
	 * <li><B>Verify: </B>Verify Vote Text and Vote for newly created Idea</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createAndcommentAndvoteIdeationBlogforExternalUserOrgA(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
				 										 .tags("IdeaTag" + Helper.genDateBasedRand())
				 										 .content("Test Content for " + testName)
				 										 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		// add widget ideation blog
		logger.strongStep("Add Ideation Blog widget");
		log.info("INFO: Add ideation blog widget with api");
		orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler, BaseWidget.IDEATION_BLOG);

		// Load component and login
		logger.strongStep("Open ideation blogs and login as Internal User: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		logger.strongStep("Navigate to Ideation Blog page");
		log.info("INFO: Navigate to Ideation Blog page");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui,2);
		
		logger.strongStep("Click on newly created Ideation Blog");
		log.info("INFO: Click on newly created Ideation Blog");
		blogUI.clickLinkWait("css=table[dojoattachpoint='tableListAP'] h4[class='lotusBreakWord']>a:contains(" + orgaExternalRestricted.getName() + ")");
		
		//select New Idea button
		logger.strongStep("Click on New Idea button");
		log.info("INFO: Click on New Entry button");
		blogUI.clickLink(BlogsUIConstants.NewIdea);
		
		//Create a new idea
		logger.strongStep("Create a new idea");
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogUI);
		
		//Verify that new idea exists
		logger.weakStep("Verify that new idea exists");
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(blogUI.fluentWaitTextPresent(ideationBlogEntry.getTitle()), "Entry found"); 
		
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();

		logger.strongStep("Load component Blogs and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Navigate to Ideation Blog page");
		log.info("INFO: Navigate to Ideation Blog page");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui, 2);

		logger.strongStep("Click on default Ideation Blog created by Internal user");
		log.info("INFO: Click on default Ideation Blog created by Internal user");
		ui.clickLinkWait("css=table[dojoattachpoint='tableListAP'] h4[class='lotusBreakWord']>a:contains("+ orgaExternalRestricted.getName() + ")");

		// select New Entry button
		logger.strongStep("Click on  New Idea button");
		log.info("INFO: Click on  New Entry button");
		ui.clickLink(BlogsUIConstants.NewIdea);

		// Add an Entry
		logger.strongStep("Add a new Entry as an External user");
		log.info("INFO: Add the new entry as an External user");
		ideationBlogEntry.create(blogUI);

		// Add a comment
		logger.strongStep("Select the Add a comment link for entry");
		log.info("INFO: Select the Add a comment link for entry");
		blogUI.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		logger.strongStep("Type in the comment form");
		log.info("INFO: Fill in the comment form");
		blogUI.typeNativeInCkEditor(comment.getContent());
		
		logger.strongStep("INFO: Submit the comment");
		log.info("INFO: Submit the comment");
		Assert.assertTrue(blogUI.isElementPresent(BlogsUIConstants.BlogsCommentSubmit));
		blogUI.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);

		// Verify that the comment is present
		logger.strongStep("Verify that the comment is present");
		log.info("INFO: Verify that the comment is present");
		Assert.assertTrue(blogUI.fluentWaitTextPresent(comment.getContent()), "Comment found");
		
		logger.strongStep("Verify Vote Text and Vote for newly created Idea");
		log.info("INFO: Verify Vote Text and Vote for newly created Idea");
		Assert.assertTrue(blogUI.isElementPresent(BlogsUIConstants.VoteText));
		driver.getFirstElement(BlogsUIConstants.VoteText).click();
	
		logger.strongStep("Verify vote count as 1");
		log.info("INFO: Verify vote count as 1");
		verifyVoteCount("1");

		//Delete the community logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create an external listed Community for UserA and add
	 * ExternalUser as Member then create and verify Related Community</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Internal user</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Step: </B>Add Related Communities widget</li>
	 * <li><B>Step: </B>Navigate to Related Communities page</li>
	 * <li><B>Step: </B>Click Add a Community Link</li>
	 * <li><B>Step: </B>Input the current community's URL, community name and description as the related community's info</li>
	 * <li><B>Step: </B>Click Save button in the Add a Community dialog</li>
	 * <li><B>Verify: </B>Verify the related community is added</li>
	 * <li><B>Verify: </B>Verify that 'Remove the Community' link is visible to Internal user</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Related Communities page</li>
	 * <li><B>Verify: </B>Verify the related community is displayed</li>
	 * <li><B>Verify: </B>Verify that 'Remove the Community' link is not visible to External user</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void relatedCommunitiesAccessRestrictionforExternaluser(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String relatedcommName = "RelatedCommunity" + Helper.genDateBasedRand();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		String commUUIDA = orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		String commURL = serverURL +"/communities/service/html/communityoverview?" + commUUIDA;

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		logger.strongStep("Add Related Communities widget");
		log.info("INFO: add Related Communities widget ");
		ui.addWidget(BaseWidget.RELATED_COMMUNITIES);
		
		logger.strongStep("Navigate to Related Communities page");
		log.info("INFO: Navigate to Related Communities page");
		Community_TabbedNav_Menu.RELATEDCOMMUNITIES.select(ui);

		logger.strongStep("Click Add a Community Link ");
		log.info("INFO: click Add a Community Link ");
		ui.clickLinkWait(CommunitiesUIConstants.addRelatedCommBtn);

		logger.strongStep("Input the current community's URL, community name and description as the related community's info");
		log.info("INFO: input the current community's URL, community name and description as the related community's info");
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityURL).type(commURL);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityName).type(relatedcommName);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityDesc).type("testing Related Communities");

		logger.strongStep("Click Save button in the Add a Community dialog");
		log.info("INFO: click Save button in the Add a Community dialog");
		ui.clickSaveButton();

		logger.strongStep("Verify the related community is added");
		log.info("INFO: verify the related community is added");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getRelatedCommunityNameLink(relatedcommName)),"Added a community to Related Communities");
		
		logger.strongStep("Verify that 'Remove the Community' link is visible to Internal user");
		log.info("INFO: Verify that 'Remove the Community' link is visible to Internal user");
		driver.getFirstElement(CommunitiesUIConstants.relatedCommunityMoreLink).click();
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.relatedCommunityRemoveLink).isDisplayed(), "Remove the Comunity'link is visible");
		
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();

		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentBlogs,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		logger.strongStep("Navigate to Related Communities page");
		log.info("INFO: Navigate to Related Communities page");
		Community_TabbedNav_Menu.RELATEDCOMMUNITIES.select(ui);
		
		logger.strongStep("Verify the related community is displayed");
		log.info("INFO: verify the related community is displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getRelatedCommunityNameLink(relatedcommName)),"Added a community to Related Communities");

		logger.strongStep("Verify that 'Remove the Community' link is not visible to External user");
		log.info("INFO:Verify that 'Remove the Community' link is not visible to External user");
		driver.getFirstElement(CommunitiesUIConstants.relatedCommunityMoreLink).click();
		Assert.assertFalse(driver.getFirstElement(CommunitiesUIConstants.relatedCommunityRemoveLink).isDisplayed(), "Remove community link is visible");
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a external listed Community Wiki for Internal User 
	 * and comment and edit to that Wiki from External User</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community</li> 
	 * <li><B>Step: </B>Add the Wiki widget to the Community using API</li>
	 * <li><B>Step: </B>Load component and login as Internal User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Wiki page</li>
	 * <li><B>Step: </B>Create a Wiki Page inside the Wiki</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Navigate to Wiki page</li>
	 * <li><B>Step: </B>Open the newly created Wiki Page by Internal user</li>
	 * <li><B>Step: </B>Edit the current Page and validate that the Page has been edited</li>
	 * <li><B>Verify: </B>Validate that the Page name has been changed</li>
	 * <li><B>Step: </B>Add a comment by External user</li>
	 * <li><B>Verify: </B>Validate the added comment</li>
	 * <li><B>Step: </B>Delete the Community</li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createAndEditWikiforExternalUserOrgA(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
												.tags("tag1, tag2")
												.description("this is a test description for creating a Peer wiki page")
												.build();
		
		BaseWikiPage editedwikiPage = new BaseWikiPage.Builder("Edited_" + wikiPage.getName(), PageType.Peer)
												   .tags("updated_tag1, updated_tag2")
												   .description("updated with new content")
												   .build();
		String externalUserComment = "Comment added by MT External user "+exttestUser1.getDisplayName();
		
		exttestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		
		Member member = new Member(CommunityRole.MEMBERS, exttestUser1);
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		// add widget
		logger.strongStep("Add the Wiki widget to the Community using API");
		if (!apiHandler.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler, BaseWidget.WIKI);
		}

		// GUI
		// Load component and login
		logger.strongStep("Load component and login as Internal User: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		logger.strongStep("Navigate to Wiki page");
		log.info("INFO: Navigate to Wiki page");
		Community_TabbedNav_Menu.WIKI.select(ui,2);

		// create a new wikipage
		logger.strongStep("Create a Wiki Page inside the Wiki");
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wikiUI);
		
		logger.strongStep("Validate that the wiki page has been created");
		log.info("INFO: Validate that the wiki page has been created");
		Assert.assertTrue(driver.isTextPresent(wikiPage.getName()),"Created wiki page found");
				
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();

		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to Wiki page");
		log.info("INFO: Navigate to Wiki page");
		Community_TabbedNav_Menu.WIKI.select(ui,2);
		
		//Open newly created Wiki page
		logger.strongStep("Open the newly created Wiki Page by Internal user");
		log.info("INFO: Open the newly created Wiki Page by Internal user");
		driver.getFirstElement(wikiUI.getPageSelector(wikiPage)).click();

		// Edit the current page and verify that the page has being edited
		logger.strongStep("Edit the current Page and validate that the Page has been edited");
		log.info("INFO: Edit the current Page and validate that the Page has been edited");
		wikiUI.editWikiPage(editedwikiPage);

		// Now verify that the page has being saved successfully
		logger.strongStep("Validate that the Page name has been changed");
		log.info("INFO: Validate that the Page name has been changed");
		Assert.assertTrue(driver.isTextPresent(editedwikiPage.getName()),"Found edited wiki page name");

		// Add a comment and verify that the comment is added
		logger.strongStep("Add a comment by External user");
		log.info("INFO: Add a comment  by External user");
		wikiUI.addComment(externalUserComment);

		// Validate comment
		logger.strongStep("Validate the added comment");
		log.info("INFO: Validate the added comment");
		Assert.assertTrue(ui.fluentWaitTextPresent(externalUserComment),"Comment Found");

		// Delete the community logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating an external listed community activity with entry and todo</li>
	 * <li><B>Info: </B> Verify activity, entry , todo with external user and comment on them</li>
	 * <li><B>Info: </B> Create entry , todo with external user </li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Add activity to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Create entry and todo inside activity to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to activity tab</li>
	 * <li><B>Verify: </B>Verify activity info entry and todo for the created activity</li>
	 * <li><B>Step: </B>Logout as internal user</li> 
	 * <li><B>Step: </B>Login in as an external user</li> 
	 * <li><B>Step: </B>Navigating to activity tab</li>
	 * <li><B>Verify: </B>Verify activity info entry and todo for the created activity </li>
	 * <li><B>Step: </B>Add comment to entry and todo item as an external user</li> 
	 * <li><B>Step: </B>Create entry and todo to the same internal user activity with external user</li> 
	 * <li><B>Verify: </B>Verify activity info entry and todo for the created activity </li>
	 * <li><B>Step: </B>Delete Community</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingActivity_InExtRestrictedCommWithOrgA() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		User testUser1_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		
		APICommunitiesHandler apiHandler1 = new APICommunitiesHandler(serverURL_MT_orgA, testUser1_orgA.getAttribute(cfg.getLoginPreference()),
				testUser1_orgA.getPassword());
		User exttestUser2 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		
		Member member = new Member(CommunityRole.MEMBERS, exttestUser2);
		
		APIActivitiesHandler activityApiOwner1 = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser1_orgA.getAttribute(cfg.getLoginPreference()),
				testUser1_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED)
																.allowExternalUserAccess(true)
																.rbl(true)
																.shareOutside(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.dueDateRandom()
												.useCalPick(true)
												.goal(Data.getData().commonDescription + testName)
												.community(orgaExternalRestricted)
												.build();
		
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		
		BaseActivityToDo externalUser_Todo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand()+"_"+exttestUser2.getDisplayName());

		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler1);
		
		logger.strongStep("Add activity to community using API");
		log.info("INFO: Add activity to community using API");
		Activity communityActivity = CommunityActivityEvents.createCommunityActivity(activity, orgaExternalRestricted, testUser1_orgA,
				activityApiOwner1, apiHandler1, comAPI);
		
		BaseActivityEntry basePublicEntry = ActivityBaseBuilder
											.buildBaseActivityEntry(orgaExternalRestricted.getName(),communityActivity, false);
		
		BaseActivityEntry externalUser_Entry = ActivityBaseBuilder
				.buildBaseActivityEntry(orgaExternalRestricted.getName()+"_"+exttestUser2.getDisplayName(),communityActivity, false);
		
		CommunityActivityEvents.createActivityEntry(testUser1_orgA, activityApiOwner1, basePublicEntry, communityActivity);
		
		CommunityActivityEvents.createActivityTodo(testUser1_orgA, activityApiOwner1, baseActivityTodo, communityActivity);
		
		logger.strongStep("Add activities widget if not present");
		log.info("INFO: Add activities widget if not present");
		if(apiHandler1.getWidgetID(comAPI.getUuid(), "Activities").isEmpty()) {
			orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler1, BaseWidget.ACTIVITIES);
		}
				
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler1, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser1_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			Assert.assertFalse(false, "Add member to community failed due to "+e.getMessage());
		}
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);
		
		ui.waitForPageLoaded(driver);
						
		logger.strongStep("Click on the Activities tab");
		log.info("INFO: Click on the Activities tab");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui);
		
		logger.strongStep("Verify activity inside new community");
		log.info("INFO:Verify activity inside new community");
		actUI.verifyActivityInfo(logger, activity);
		
		String entryUUID = actUI.getEntryUUID(basePublicEntry);
		actUI.expandEntry(entryUUID);
		
		logger.strongStep("Verify activity entry inside new community");
		log.info("INFO:Verify activity entry inside new community");
		actUI.validatePageInfo(logger, basePublicEntry);
		
		String todoRootUUID = actUI.getEntryUUID(baseActivityTodo);
		actUI.expandEntry(todoRootUUID);
		
		logger.strongStep("Verify activity todo inside new community");
		log.info("INFO:Verify activity todo inside new community");
		actUI.validateToDoInfo(logger, baseActivityTodo);
		
		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();
		
		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser2);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Navigate to the activities tab");
		log.info("INFO: Navigate to the activities tab");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui,2);
		
		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.fluentWaitPresentWithRefresh(ActivitiesUI.getActivityLink(activity));
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		logger.strongStep("Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		actUI.expandEntry(entryUUID);
		ui.getFirstVisibleElement(ui.getComment(entryUUID)).click();
		ui.typeInCkEditor("External Users Comment in Entry");
		
		logger.strongStep("INFO: Click on Save button");
		log.info("INFO: Click on Save button");
        ui.clickLinkWithJavascript(BaseUIConstants.SaveButton);
        
        log.info("INFO: Click on todo link");
		logger.strongStep("Click on todo link");
		String todoItemLink = ActivitiesUIConstants.Activity_Todo_Item_Link.replaceAll("PLACEHOLDER", baseActivityTodo.getTitle().trim());
		driver.getFirstElement(todoItemLink).click();

		logger.strongStep("Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		ui.getFirstVisibleElement(ui.getComment(todoRootUUID)).click();
		ui.typeInCkEditor("External Users Comment in todo");
		
		logger.strongStep("Click Save button");
		log.info("INFO: Click Save button");
        ui.clickLinkWithJavascript(BaseUIConstants.SaveButton);
        
		//Add entry
		log.info("INFO: Create a new Entry with an external user");
		logger.strongStep("Create a new Entry with an external user");
		externalUser_Entry.create(actUI);

		//Verify entry info
		log.info("INFO: Validate that the Entry is present");
		logger.strongStep("Validate that the Entry is present");
		actUI.validatePageInfo(logger,externalUser_Entry);
		
		//Add todo
		log.info("INFO: Add todo for the Activity with an external user");
		logger.strongStep("Add todo for the Activity with an external user");
		externalUser_Todo.create(actUI);

		//Select more link for todo
		log.info("INFO: Select the 'More' link for the todo");
		logger.strongStep("Select the 'More' link for the todo"); 
		List<Element> entries = driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		actUI.validatePageInfo(logger,externalUser_Todo);
		ui.clickCancelButton();
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler1.deleteCommunity(apiHandler1.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
		
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a Public Parent Community for UserA and do not add any
	 * ExternalUser as Member then create a moderated Sub-community and verify 'Access Denied' message for any External user</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Internal user</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Step: </B>Create a Sub Community </li>
	 * <li><B>Verify: </B>Verify Sub Community is created </li>
	 * <li><B>Verify: </B>Validate are Owners & No Members from the parent community have been added to child sub community</li>
	 * <li><B>Verify: </B>Validate that Owner appears on the Members full page</li>
	 * <li><B>Verify: </B>Validate that no Members appears on the Members full page</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Verify: </B>Validate that 'Access Denied' message is displayed</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createPublicCommunityWithNoExternalUserAccess(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String subcommName = "SubCommunity" + Helper.genDateBasedRand();

		BaseCommunity publicCommunity = new BaseCommunity.Builder("publicCommunity" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.description("Test description for testcase " + testName)
																.build();
		
		BaseSubCommunity moderatedSubCommunity = new BaseSubCommunity.Builder(subcommName).access(BaseSubCommunity.Access.MODERATED)
															.UseParentmembers(true)
															.tags(Data.getData().commonTag + Helper.genDateBasedRand())
															.description("Test creating a restricted subcommunity " + testName)
															.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = publicCommunity.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		publicCommunity.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		publicCommunity.navViaUUID(ui);
		
		//Create and Verify an External Restricted Sub-Community
		logger.strongStep("Create a Sub Community");
		log.info("INFO: Creating Sub Community ");
		moderatedSubCommunity.create(ui);
		
		logger.strongStep("Verify Sub Community is created");
		log.info("INFO: Verify Sub Community is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(moderatedSubCommunity.getName()),"Subcommunity is created");
		
		logger.strongStep("Go to Members tab");
		log.info("INFO: Go to Members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		logger.strongStep("Validate that Owner appears on the Members full page");
		log.info("INFO: Validate that Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()), "Owner is present in Subcommunity");
		
		logger.strongStep("Validate the no Members appears on the Members full page");
		log.info("INFO: Validate the no Member appears on the Members full page");
		Assert.assertFalse(driver.isElementPresent("link=" + exttestUser1.getDisplayName()), "Member is present in Subcommunity");
	
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();
		
		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		publicCommunity.navViaUUID(ui);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'Access Denied' message is displayed");
		log.info("INFO: Validate that 'Access Denied' message is displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.accessDeniedMsg), "'Access Denied' message is displayed");
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(publicCommunity.getCommunityUUID()));

		ui.endTest();

	}
	
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a Internal Restricted Parent Community for UserA and do not add any
	 * ExternalUser as Member then create a restricted Sub-community and verify 'Access Denied' message for any External user</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Internal user</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Step: </B>Create a Sub Community </li>
	 * <li><B>Verify: </B>Verify Sub Community is created </li>
	 * <li><B>Verify: </B>Validate are Owners & No Members from the parent community have been added to child sub community</li>
	 * <li><B>Verify: </B>Validate that Owner appears on the Members full page</li>
	 * <li><B>Verify: </B>Validate that no Members appears on the Members full page</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Verify: </B>Validate that 'Access Denied' message is displayed</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createInternalRestrictedCommunityWithNoExternalUserAccess(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String subcommName = "SubCommunity" + Helper.genDateBasedRand();

		BaseCommunity commInternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).rbl(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseSubCommunity internalRestrictedSubCommunity = new BaseSubCommunity.Builder(subcommName).access(BaseSubCommunity.Access.RESTRICTED)
															.UseParentmembers(true)
															.tags(Data.getData().commonTag + Helper.genDateBasedRand())
															.description("Test creating a restricted subcommunity " + testName)
															.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = commInternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		commInternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		commInternalRestricted.navViaUUID(ui);
		
		//Create and Verify an External Restricted Sub-Community
		logger.strongStep("Create a Sub Community");
		log.info("INFO: Creating Sub Community ");
		internalRestrictedSubCommunity.create(ui);
		
		logger.strongStep("Verify Sub Community is created");
		log.info("INFO: Verify Sub Community is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(internalRestrictedSubCommunity.getName()),"Subcommunity is created");
		
		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		logger.strongStep("Validate that Owner appears on the Members full page");
		log.info("INFO: Validate that Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()), "Owner is not present in Subcommunity");
		
		logger.strongStep("Validate the no Members appears on the Members full page");
		log.info("INFO: Validate the no Member appears on the Members full page");
		Assert.assertFalse(driver.isElementPresent("link=" + exttestUser1.getDisplayName()), "Member is present in Subcommunity");
	
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();
		
		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		commInternalRestricted.navViaUUID(ui);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'Access Denied' message is displayed");
		log.info("INFO: Validate that 'Access Denied' message is displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.accessDeniedMsg), "'Access Denied' message is displayed");
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(commInternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	

	/**
	 * <ul>
	 * <li><B>Info: </B>Create a External Restricted Parent Community for UserA and do not add any
	 * ExternalUser as Member then create a restricted Sub-community and verify 'Access Denied' message for any External user</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Get UUID of community </li> 
	 * <li><B>Step: </B>Load Communities and Log In as Internal user</li>
	 * <li><B>Step: </B>Navigate to the community using UUID </li>
	 * <li><B>Step: </B>Create a Sub Community </li>
	 * <li><B>Verify: </B>Verify Sub Community is created </li>
	 * <li><B>Verify: </B>Validate are Owners & No Members from the parent community have been added to child sub community</li>
	 * <li><B>Verify: </B>Validate that Owner appears on the Members full page</li>
	 * <li><B>Verify: </B>Validate that no Members appears on the Members full page</li>
	 * <li><B>Step: </B>Log Out from application as Internal user</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Verify: </B>Validate that 'Access Denied' message is displayed</li>
	 * <li><B>Step: </B>Delete the Community </li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createExternalRestrictedCommunityWithNoExternalUserAccess(){

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String subcommName = "SubCommunity" + Helper.genDateBasedRand();

		BaseCommunity commExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).rbl(true)
																.description("Test description for testcase " + testName)
																.build();
		
		BaseSubCommunity restrictedSubCommunity = new BaseSubCommunity.Builder(subcommName).access(BaseSubCommunity.Access.RESTRICTED)
															.UseParentmembers(true)
															.tags(Data.getData().commonTag + Helper.genDateBasedRand())
															.description("Test creating a restricted subcommunity " + testName)
															.build();
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = commExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		commExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		commExternalRestricted.navViaUUID(ui);
		
		//Create and Verify an External Restricted Sub-Community
		logger.strongStep("Create a Sub Community");
		log.info("INFO: Creating Sub Community ");
		restrictedSubCommunity.create(ui);
		
		logger.strongStep("Verify Sub Community is created");
		log.info("INFO: Verify Sub Community is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(restrictedSubCommunity.getName()),"Subcommunity is created");
		
		logger.strongStep("Navigate to members tab");
		log.info("INFO: Navigate to members tab" );
		Community_TabbedNav_Menu.MEMBERS.select(ui);
		
		logger.strongStep("Validate that Owner appears on the Members full page");
		log.info("INFO: Validate that Owner appears on the Members full page");
		Assert.assertTrue(driver.isElementPresent("link=" + testUser_orgA.getDisplayName()), "Owner is present in Subcommunity");
		
		logger.strongStep("Validate the no Members appears on the Members full page");
		log.info("INFO: Validate the no Member appears on the Members full page");
		Assert.assertFalse(driver.isElementPresent("link=" + exttestUser1.getDisplayName()), "Member is present in Subcommunity");
	
		logger.strongStep("Log Out from application as Internal user");
		log.info("INFO: Log Out from application as Internal user");
		ui.logout();
		
		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		commExternalRestricted.navViaUUID(ui);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'Access Denied' message is displayed");
		log.info("INFO: Validate that 'Access Denied' message is displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.accessDeniedMsg), "'Access Denied' message is displayed");
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(commExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	

	/**
	 * <ul>
	 * <li><B>Info: </B>Create a external listed Community for Internal User 
	 * then create and edit an Event from External User</li> 
	 * <li><B>Step: </B>Create community using API </li> 
	 * <li><B>Info: </B>Add the events widget</li> 
	 * <li><B>Step: </B>Get UUID of community</li>
	 * <li><B>Step: </B>Load component and Log In as Org's External User</li>
	 * <li><B>Step: </B>Navigate to the community using UUID</li>
	 * <li><B>Step: </B>Click on the Events link in the tabbed navigation menu</li>
	 * <li><B>Step: </B>Create an Event</li>
	 * <li><B>Step: </B>Open the Event</li>
	 * <li><B>Step: </B>Verify user is listed to attend the event and displays in people attending section</li>
	 * <li><B>Step: </B>Select the Edit button</li>
	 * <li><B>Step: </B>Update Location field</li>
	 * <li><B>Step: </B>Submit the Edited event</li>
	 *  <li><B>Step: </B>Verify that the Location is updated</li>
	 * <li><B>Step: </B>Delete the Community</li>
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul> 
	 * @throws Exception 
	 */
	
	@Test(groups = { "mtlevel2"})
	public void createAndEditEventforExternalUserOrgA() throws Exception{

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
																.tags("testTags" + Helper.genDateBasedRand())
																.access(Access.RESTRICTED).allowExternalUserAccess(true).rbl(true).shareOutside(true)
																.description("Test description for testcase " + testName)
																.addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
																.build();
		
		BaseEvent event = new BaseEvent.Builder(testName + " event" + Helper.genDateBasedRand())
				   					   .tags(Data.getData().commonTag)
				   					   .description(Data.getData().commonDescription)
				   					   .build();
		String expectedText = "Location:" + Data.getData().EventLocation;
		
		
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		orgaExternalRestricted.addWidgetAPI(comAPI, apiHandler, BaseWidget.EVENTS);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		logger.strongStep("Load component and Log In as Org's External User: " + exttestUser1.getDisplayName());
		log.info("INFO: Log In as Org's External User");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		
		logger.strongStep("Click on the Events link in the tabbed navigation menu");
		log.info("INFO: Select Events in the tabbed navigation");
		Community_TabbedNav_Menu.EVENTS.select(calUI);

		// Create an Event
		logger.strongStep("Create an Event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		// Open the Event
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		logger.weakStep("Verify user is listed to attend the event and displays in people attending section");
		log.info("INFO: Verify " + exttestUser1.getDisplayName() + " displays in people attending section" );
		Assert.assertTrue(driver.isElementPresent(calUI.AttendeeList(exttestUser1.getDisplayName())),
				exttestUser1.getDisplayName() + " displayed in attendee list");

		// Edit all event field and update the single event to repeating event
		logger.strongStep("Select the Edit button");
		log.info("INFO: Select Edit button");
		ui.clickLinkWait(CalendarUI.Edit);
		
		logger.strongStep("Update Location field");
		log.info("INFO: Update Location field");
		ui.clickLinkWait(CalendarUI.EventLocation);
		ui.clearText(CalendarUI.EventLocation);
		driver.getSingleElement(CalendarUI.EventLocation).type(Data.getData().EventLocation);
		
		logger.strongStep("Submit the Edited event");
		log.info("INFO: Submit the Edited event");
		ui.clickLinkWait(CalendarUI.EventSubmit);

		// Verify updated location for the event
		logger.weakStep("Verify that the Location is updated");
		log.info("INFO: Verify Location is updated");
		ui.fluentWaitTextPresent(driver.getSingleElement(CalendarUI.locationText).getText());
		Assert.assertEquals(driver.getSingleElement(CalendarUI.locationText).getText(), expectedText,
				"Event location was updated");

		// Delete the community logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();
	}
		
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating an external listed community and
	 * create a blog entry , login with external user comment and edit the existing entry and
	 * create blog entry with external user</li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Create blog entry to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to blog tab</li>
	 * <li><B>Verify: </B>Verify blog entry created by owner</li>
	 * <li><B>Step: </B>Logout as internal user</li> 
	 * <li><B>Step: </B>Login as external user and navigate to blogs tab</li> 
	 * <li><B>Step: </B>Comment on the entry created by internal user</li> 
	 * <li><B>Step: </B>Edit comment as an external user</li> 
	 * <li><B>Step: </B>Create blog entry in same community as an external user</li>
	 * <li><B>Verify: </B>Verify blog entry created by external user</li> 
	 * <li><B>Step: </B>Delete community</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingBlog_WithExternalRestrictedCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder(
				"orgaExternalRestricted" + Helper.genDateBasedRand()).access(Access.RESTRICTED)
						.allowExternalUserAccess(true).rbl(true).shareOutside(true)
						.description("Test description for testcase " + testName)
						.addMember(new Member(CommunityRole.MEMBERS, exttestUser1)).build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName).build();

		BaseBlogComment comment = new BaseBlogComment.Builder("comment for " + testName).build();

		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);

		// create blog entry
		apiHandler.createBlogEntry(blogEntry, comAPI);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);

		logger.strongStep("Click on the Blog tab");
		log.info("INFO: Click on the Blog tab");
		Community_TabbedNav_Menu.BLOG.select(ui, 2);

		logger.strongStep("Verify that entry from base community exists in new community");
		log.info("INFO: Verify that entry from base community exists in new community");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry.getTitle()), "Blog Entry found");

		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();

		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the blogs tab");
		log.info("INFO: Navigate to the blogs tab");
		Community_TabbedNav_Menu.BLOG.select(ui, 2);

		// open blog
		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blogEntry.getTitle());

		// Add a comment
		logger.strongStep("Add a comment");
		log.info("INFO: Add a new comment to the entry");
		comment.create(blogUI);
		
		logger.strongStep("Validate blog comment");
		log.info("INFO: Validate blog comment");
		Assert.assertTrue(driver.isTextPresent(comment.getContent()), "Blog comment by external user found");

		// Add a comment
		logger.strongStep("Edit a comment");
		log.info("INFO: Edit an existing comment");
		comment.setContent("Edited Comment");
		comment.edit(blogUI,1);
		
		logger.strongStep("Validate blog comment");
		log.info("INFO: Validate blog comment");
		Assert.assertTrue(driver.isTextPresent("Edited Comment"), "Edited Blog comment by external user found");

		// select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);

		// Add an Entry
		logger.strongStep("Add a new entry with an external user");
		log.info("INFO: Add a new entry with an external user");
		blogEntry.setTitle(exttestUser1.getDisplayName()+" Entry");
		blogEntry.create(blogUI);

		// Verify that new entry exists
		logger.weakStep("Verify that new blog entry exists");
		log.info("INFO: Verify that new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), "Blog entry found");

		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating an external listed community and
	 * create a bookmark , login with external user and verify boookmark
	 * create bookmark with external user</li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Create bookmark to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to bookmarks tab</li>
	 * <li><B>Verify: </B>Verify bookmark created by owner</li>
	 * <li><B>Step: </B>Logout as internal user</li> 
	 * <li><B>Step: </B>Login as external user and navigate to bookmarks tab</li> 
	 * <li><B>Step: </B>Verify bookmark created by internal user</li> 
	 * <li><B>Step: </B>Create bookmark in same community as an external user</li>
	 * <li><B>Verify: </B>Verify bookmark created by external user</li> 
	 * <li><B>Step: </B>Delete community</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingBookmark_WithExternalRestrictedCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder(
				"orgaExternalRestricted" + Helper.genDateBasedRand()).access(Access.RESTRICTED)
						.allowExternalUserAccess(true).rbl(true).shareOutside(true)
						.description("Test description for testcase " + testName)
						.addMember(new Member(CommunityRole.MEMBERS, exttestUser1)).build();


		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
		
		String url = "http://www.hcl" + Helper.genDateBasedRand() + ".com";
		String name = "BOOKMARK" + Helper.genDateBasedRand();
		String tag = "bmTag" + Helper.genDateBasedRand();
		String description = "Sample bookmark description " + Helper.genDateBasedRand();
		BaseDogear bookmark = new BaseDogear.Builder(name, url)
				.community(orgaExternalRestricted)
				.tags(tag)
				.description(description)
				.build();
	
		//Create bookmark
		log.info("INFO: Create bookmarks using API");
		bookmark.createAPI(new APICommunitiesHandler(serverURL, testUser_orgA.getUid(), testUser_orgA.getPassword()));
		
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);

		logger.strongStep("Click on the BOOKMARK tab");
		log.info("INFO: Click on the BOOKMARK tab");
		Community_TabbedNav_Menu.BOOKMARK.select(ui, 2);

		logger.strongStep("Verify that bookmark exists in bookmark tab");
		log.info("INFO: Verify that bookmark exists in bookmark tab");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(bookmark.getTitle()), "BOOKMARK Entry found");

		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();

		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);

		logger.strongStep("Navigate to the bookmark tab");
		log.info("INFO: Navigate to the bookmark tab");
		Community_TabbedNav_Menu.BOOKMARK.select(ui, 2);

		logger.strongStep("Verify that bookmark is visible to external user");
		log.info("INFO: Verify that bookmark is visible to external user");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(bookmark.getTitle()), "BOOKMARK Entry found");
				
		bookmark.setURL("http://www.hcl" + Helper.genDateBasedRand() + ".com");
		
		BaseDogear bkmrk_ExtUser = new BaseDogear.Builder("BOOKMARK_External" + Helper.genDateBasedRand(),
				"http://www.hcl" + Helper.genDateBasedRand() + ".com")
				.community(orgaExternalRestricted)
				.tags(tag)
				.description("externalUser description")
				.build();
		
		
		//Wait until Add a Bookmark button is visible
		log.info("INFO: Wait until Add a Bookmark button is visible");
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
					     "Add a Bookmark button is present");
		
		//Click on Add a Bookmark button
		log.info("INFO: Click add bookmark button");
		ui.clickLink(DogearUIConstants.AddBookmark);
		
		//Now add a bookmark
		log.info("INFO: Fill out bookmark form and save");
		bkmrk_ExtUser.create(dogearUI);
		
		logger.strongStep("Verify that bookmark created by external user is visible");
		log.info("INFO: Verify that bookmark created by external user is visible");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(bkmrk_ExtUser.getTitle()), "BOOKMARK Entry found");
		
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify files and and folders created by internal user in 
	 * 	external restricted community with an external user</li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Create file and folder to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to files tab</li>
	 * <li><B>Verify: </B>Verify file and folder created by owner</li>
	 * <li><B>Step: </B>Logout as internal user</li> 
	 * <li><B>Step: </B>Create file and folder to same external listed community with api for external user</li>
	 * <li><B>Step: </B>Login as external user and navigate to Files tab</li> 
	 * <li><B>Verify: </B>Verify File and folder created by internal user</li> 
	 * <li><B>Verify: </B>Verify File and folder created by external user</li>
	 * <li><B>Step: </B>Delete community</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyCreatingFilesAndFolders_WithExternalRestrictedCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
	    exttestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
	    apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
					testUser_orgA.getPassword());
	    fileHandler = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());


		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder(
				"orgaExternalRestricted" + Helper.genDateBasedRand()).access(Access.RESTRICTED)
						.allowExternalUserAccess(true).rbl(true).shareOutside(true)
						.description("Test description for testcase " + testName)
						.addMember(new Member(CommunityRole.MEMBERS, exttestUser1)).build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.rename(Helper.genDateBasedRandVal())
				.extension(".jpg")
				.comFile(true)
				.build();

		BaseFile baseFolder = new BaseFile.Builder("Folder_" + testName + Helper.genStrongRand())
					.tags(Helper.genStrongRand())
					.shareLevel(ShareLevel.EVERYONE)
					.comFile(true)
					.build();	

		file.setName(file.getRename() + file.getExtension());
		
		String internalUserFileName = file.getName();
		String internalUserFolderName = baseFolder.getName();

		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		log.info("INFO: Creating the folder");
		fileHandler.createCommunityFolder(comAPI, baseFolder);
		log.info("INFO: Folder creatd in community successfully");
		logger.strongStep("Folder creatd in community successfully");

		logger.strongStep("Add a file to community via API");
		log.info("INFO: Add a file to community via API");
		orgaExternalRestricted.addFileAPI(comAPI, file, apiHandler, fileHandler);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
			
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForCommunityLoaded();

		logger.strongStep("Click on the Files tab");
		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(ui, 3);

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Verify uploaded file by internal user is present");
		logger.strongStep("Verify uploaded file by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(internalUserFileName), "File found");
		
		log.info("INFO: Navigate to community folders");
		logger.strongStep("Navigate to community folders");
		ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
		
		log.info("INFO: Verify folder created by internal user is present");
		logger.strongStep("Verify folder created by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(internalUserFolderName), "Folder found");
		
		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();
		
		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);
		
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, exttestUser1.getAttribute(cfg.getLoginPreference()),
				exttestUser1.getPassword());
		fileHandler = new APIFileHandler(serverURL_MT_orgA, exttestUser1.getAttribute(cfg.getLoginPreference()), exttestUser1.getPassword());
	
		baseFolder.setName("Folder_" + testName + Helper.genStrongRand()+"_external");

		log.info("INFO: Creating the folder with external user in community created by internal user");
		fileHandler.createCommunityFolder(comAPI, baseFolder);
		log.info("INFO: Folder with external user created in community created by internal user");
		logger.strongStep("Folder with external user created in community created by internal user");
		
		file.setName(file.getRename()+"_external" + file.getExtension());

		logger.strongStep("Add a file via API with external user ");
		log.info("INFO: Add a file via API with external user ");
		orgaExternalRestricted.addFileAPI(comAPI, file, apiHandler, fileHandler);
		
		String externalUserFileName = file.getName();
		String externalUserFolderName = baseFolder.getName();

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the files tab");
		log.info("INFO: Navigate to the files tab");
		Community_TabbedNav_Menu.FILES.select(ui, 3);
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Verify uploaded file by external user is present");
		logger.strongStep("Verify uploaded file by external user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(externalUserFileName), "External User File found");
		
		log.info("INFO: Verify uploaded file by internal user is present");
		logger.strongStep("Verify uploaded file by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(internalUserFileName), "Internal User File found");
				
		ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
		
		log.info("INFO: Verify folder created by external user is present");
		logger.strongStep("Verify folder created by external user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(externalUserFolderName), "External User Folder found");
		
		log.info("INFO: Verify folder created by internal user is present");
		logger.strongStep("Verify folder created by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(internalUserFolderName), "Internal User Folder found");
	
		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();

	}
	

	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify creating an external listed community and as an external user
	 * <li><B>Locate the File created by User1 and<B></li>
	 * <li><B>Add a tag<B></li>
	 * <li><B>Edit properties<B></li>
	 * <li><B>Lock/unlock file<B></li>
	 * <li><B>Upload a new version<B></li>
	 * <li><B>Step: </B>Create external restricted community for orga having member as external user with api for orga</li>
	 * <li><B>Step: </B>Upload a file to external listed community with api for orga user</li>
	 * <li><B>Step: </B>Login to connections as OrgA User</li>
	 * <li><B>Step: </B>Navigating to files tab</li>
	 * <li><B>Verify: </B>Verify file uploaded by owner</li>
	 * <li><B>Step: </B>Logout as internal user</li> 
	 * <li><B>Step: </B>Login as external user and navigate to files tab</li> 
	 * <li><B>Step: </B>Add tag to file uploaded by internal user</li> 
	 * <li><B>Verify: </B>Verify tag created by external user</li>
	 * <li><B>Step: </B>With edit properties opion update file name of file uploaded by internal user</li> 
	 * <li><B>Verify: </B>Verify updated file name by external user</li>
	 * <li><B>Step: </B>As an external user Lock and unlock file uploaded by internal user</li> 
	 * <li><B>Verify: </B>Verify messages when external user lock and unlock file</li>
	 * <li><B>Step: </B>As an external user upload new version of file uploaded by internal user</li> 
	 * <li><B>Verify: </B>Verify messages when external user updates new version of uploaded file</li>
	 * <li><B>Step: </B>Delete community</li> 
	 * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void verifyUpdatingFiles_WithExternalRestrictedCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
	    exttestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
	    apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
					testUser_orgA.getPassword());
	    fileHandler = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder(
				"orgaExternalRestricted" + Helper.genDateBasedRand()).access(Access.RESTRICTED)
						.allowExternalUserAccess(true).rbl(true).shareOutside(true)
						.description("Test description for testcase " + testName)
						.addMember(new Member(CommunityRole.MEMBERS, exttestUser1)).build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.rename(Helper.genDateBasedRandVal())
				.extension(".jpg")
				.comFile(true)
				.build();

		file.setName(file.getRename() + file.getExtension());

		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
		
		logger.strongStep("Add a file via API");
		log.info("INFO: Add a file via API");
		orgaExternalRestricted.addFileAPI(comAPI, file, apiHandler, fileHandler);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);
			
		logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);

		logger.strongStep("Click on the Files tab");
		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(ui, 3);

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Verify uploaded file by internal user is present");
		logger.strongStep("Verify uploaded file by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(file.getName()), "File found");
		
		logger.strongStep("Logout as an internal user");
		log.info("INFO: Logout as an internal user");
		ui.logout();
				
		logger.strongStep("Load communities component and login as an external user");
		log.info("INFO: Load communities component and login as an external user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(exttestUser1);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		orgaExternalRestricted.navViaUUID(ui);

		ui.waitForPageLoaded(driver);

		logger.strongStep("Navigate to the Files tab");
		log.info("INFO: Navigate to the Files tab");
		Community_TabbedNav_Menu.FILES.select(ui, 3);
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Verify uploaded file by internal user is present");
		logger.strongStep("Verify uploaded file by internal user is present");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(file.getName()), "File found");
		
		log.info("INFO: Click on more link for files");
		logger.strongStep("Click on more link for files");
		ui.clickLinkWait(filesUI.fileSpecificMore(file));
			
		logger.strongStep("Click on Add Tag link, type tag and save");
		log.info("INFO: Click on Add Tag link, type tag and save");
		driver.getFirstElement(FilesUIConstants.AddATagToFile).click();
		ui.typeTextWithDelay(FilesUIConstants.inputTag, "Filetag");
		driver.getFirstElement(FilesUIConstants.SaveTag).click();
		
		Assert.assertTrue(ui.isTextPresent("Filetag"), "File tag found");
				
		filesUI.editFileProperties(file);
		
		file.setName(Data.getData().editedFileName + file.getExtension());
						
		log.info("INFO: Click on more link for files");
		logger.strongStep("Click on more link for files");
		ui.clickLinkWait(filesUI.fileSpecificMore(file));
		
		log.info("INFO: Click on the More Actions link");
		ui.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);
		
		log.info("INFO: Lock file as an external user");
		ui.clickLinkWithJavascript(FilesUIConstants.LockFileOption_ext);
		
		logger.strongStep("Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		log.info("INFO: Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		Assert.assertTrue(ui.fluentWaitTextPresent(FilesUIConstants.fileLockedMessage),
				"The message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");

		log.info("INFO: Click on more link for files");
		logger.strongStep("Click on more link for files");
		ui.clickLinkWait(filesUI.fileSpecificMore(file));
		
		log.info("INFO: Lock file as an external user");
		filesUI.unlockFile(file);
		
		log.info("INFO: Click on more link for files");
		logger.strongStep("Click on more link for files");
		ui.clickLinkWait(filesUI.fileSpecificMore(file));
		
		log.info("INFO: Upload new version of file as an external user");
		ui.clickLinkWait(FilesUIConstants.uploadNewVersionLink);
						
		filesUI.setLocalFileDetector();
		Element fileInput = ui.getFirstVisibleElement(FileViewerUI.FileInput);
		BaseFile file2 = new BaseFile.Builder(Data.getData().file1)
				.extension(".jpg").rename(Helper.genDateBasedRand()).build();
		fileInput.typeFilePath(FilesUI.getFileUploadPath(file2.getName(), cfg));
		ui.clickLinkWait(FilesUIConstants.Upload_Button);
		log.info("The new version was saved. File name: " + file.getName());

		// Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
     * <ul>
     * <li><B>Info: </B>Verify creating post in Status Updates of external listed community 
     * and verify it with external user</li>
     * <li><B>Step: </B>Create community with api for orga user</li>
     * <li><B>Step: </B>Login to connections as OrgA User</li>
     * <li><B>Step: </B>Navigating to recent updates tab</li>
     * <li><B>Step: </B>Type a message and post it</li>
     * <li><B>Verify: </B>Verify the message gets posted</li>
     * <li><B>Step: </B>Logout as an org a internal user</li>
     * <li><B>Step: </B>login in as an org a external user</li> 
     * <li><B>Step: </B>Navigate to Status Updates tab</li> 
     * <li><B>Verify: </B>Verify message posted by internal user in Status Updates tab</li> 
     * <li><B>Step: </B>Comment on the post in status updates</ul>
     * <li><B>Step: </B>Type a message as an external user in Status Updates and post it</li>
     * <li><B>Verify: </B>Verify the message gets posted</li>
     * <li><B>Step: </B>Delete community</li>
     * <li><B>JIRA Link: </B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T198</li>
     * </ul>
     */
    @Test(groups = { "mtlevel2"})
    public void verifyStatusUpdatesEntryWithExternalCommunity() {
        DefectLogger logger = dlog.get(Thread.currentThread().getId());
        String testName = ui.startTest();
        
        testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
        exttestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
        apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());

        BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
                                                                .access(Access.RESTRICTED)
                                                                .allowExternalUserAccess(true)
                                                                .rbl(true)
                                                                .shareOutside(true)
                                                                .description("Test description for testcase " + testName)
                                                                .addMember(new Member(CommunityRole.MEMBERS, exttestUser1))
                                                                .build();
        
        logger.strongStep("Create community using API");
        log.info("INFO: Create community using API");
        Community comAPI = orgaExternalRestricted.createAPI(apiHandler);
                    
        logger.strongStep("Get UUID of community");
        log.info("INFO: Get UUID of community");
        orgaExternalRestricted.getCommunityUUID_API(apiHandler, comAPI);

        logger.strongStep("Load Communities and Log In as: " + testUser_orgA.getDisplayName());
        log.info("INFO: Load Communities and Log In as: " + testUser_orgA.getDisplayName());
        ui.loadComponent(Data.getData().ComponentCommunities);
        ui.login(testUser_orgA);


        logger.strongStep("Navigate to the community using UUID");
        log.info("INFO: Navigate to the community using UUID");
        orgaExternalRestricted.navViaUUID(ui);
        ui.waitForPageLoaded(driver);
            
        logger.strongStep("Click on the Status Updates tab");
        log.info("INFO: Click on the Status Updates tab");
        Community_TabbedNav_Menu.STATUSUPDATES.select(ui);
        
        logger.strongStep("Post a Status message");
        log.info("INFO: Post a Status message");
        commentOrPostMessage(CommunitiesUIConstants.iframePostMessage, Data.getData().UpdateStatus.trim(), CommunitiesUIConstants.StatusPost);
        
        logger.strongStep("Verify Status message is saved");
        log.info("INFO: Verify Status message is saved");
        Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
                "Status message is displayed");
                
        logger.strongStep("Logout as an internal user");
        log.info("INFO: Logout as an internal user");
        ui.logout();
        
        logger.strongStep("Load communities component and login as an external user");
        log.info("INFO: Load communities component and login as an external user");
        ui.loadComponent(Data.getData().ComponentCommunities, true);
        ui.login(exttestUser1);
        
        logger.strongStep("Navigate to the community using UUID");
        log.info("INFO: Navigate to the community using UUID");
        orgaExternalRestricted.navViaUUID(ui);    
        ui.waitForPageLoaded(driver);
        
        logger.strongStep("Navigate to the Status Updates tab");
        log.info("INFO: Navigate to the Status Updates tab");
        Community_TabbedNav_Menu.STATUSUPDATES.select(ui);
        
        logger.strongStep("Verify Status message posted by internal user");
        log.info("INFO: Verify Status message posted by internal user");
        Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
                        "Status message is displayed");
        
        logger.strongStep("Click on comment and add a comment as external user");
        log.info("INFO: Click on comment and add a comment as external user");
        driver.getFirstElement(CommunitiesUIConstants.commentStatusMessage).click();    
        commentOrPostMessage(CommunitiesUIConstants.commentiframe, "Comment by external User", CommunitiesUIConstants.commentPostButton);
        
        logger.strongStep("Post a status message as a External User");
        log.info("INFO: Post a status message as a External User");
        commentOrPostMessage(CommunitiesUIConstants.iframePostMessage, Data.getData().MultiplePublicBookmarksUrl2, CommunitiesUIConstants.StatusPost);
    
        logger.strongStep("Verify Status message is posted");
        log.info("INFO: Verify Status message is posted");
        Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().MultiplePublicBookmarksUrl2),
                "Status message is getting displayed");
                
        // Delete the community
        logger.strongStep("Delete the Community");
        log.info("INFO: Delete the Community");
        apiHandler.deleteCommunity(apiHandler.getCommunity(orgaExternalRestricted.getCommunityUUID()));

        ui.endTest();

    }
	
	/**
	 * This method will comment or post status message
	 * @param iFrame locator for post message textbox
	 * @param msg to post
	 * @param postButton locator
	 */
	private void commentOrPostMessage(String iFrame,String msg,String postButton) {
		
		log.info("INFO: Comment on status message posted by internal user");
		driver.switchToFrame().selectFrameByElement(driver.getFirstElement(iFrame));
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(msg);
		log.info("INFO: Returning to parent frame to click 'Post' button");
		ui.switchToTopFrame();
		driver.getFirstElement(postButton).click();
	}

	/**
	 * This method verifies like count
	 * @param count to verify
	 */
	private void verifyLikeCount(String count) {
		log.info("INFO: Verifying like count");
		String likeCountBefore = driver.getFirstElement(CommunitiesUIConstants.LikeCountStatusMessage).getText();
		Assert.assertTrue(likeCountBefore.compareToIgnoreCase(count) == 0,"Number of likes is '" + likeCountBefore);
	}
	
	/**
	 * This method verifies Vote count
	 * @param count to verify
	 */
	private void verifyVoteCount(String count) {
		log.info("INFO: Verifying Vote count");
		String voteCountAfter = driver.getFirstElement(BlogsUIConstants.VoteNumber).getText();
		Assert.assertTrue(voteCountAfter.compareToIgnoreCase(count) == 0,"Number of likes is '" + voteCountAfter);
	}
	
	
}
