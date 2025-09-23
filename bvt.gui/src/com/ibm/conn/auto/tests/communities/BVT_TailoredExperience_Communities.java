package com.ibm.conn.auto.tests.communities;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Utils;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.FileViewer_Panel_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_TailoredExperience_Communities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_TailoredExperience_Communities.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser, testUserB, testUserC, testUserD;
	private User adminUser;
	private Member memberB, memberC, memberD;
	private String serverURL;
	private APICommunitiesHandler apiOwner, apiFollower1;
	private APIActivitiesHandler activityApiOwner;
	private APIForumsHandler forumApiOwner;
	private APIFileHandler filesApiOwner;
	private WikisUI wikisUI;
	private ActivitiesUI actUI;
	private FilesUI filesUI;
	private FileViewerUI fileviewerUI;
	private ForumsUI fUI;
	private IcecUI iUI;
	private List<String> templatesToDelete = new ArrayList<String>();

	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		testUserC = cfg.getUserAllocator().getUser();
		testUserD = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();
		memberB = new Member(CommunityRole.MEMBERS, testUserB);
		memberC = new Member(CommunityRole.MEMBERS, testUserC);
		memberD = new Member(CommunityRole.MEMBERS, testUserD);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		activityApiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		forumApiOwner = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		filesApiOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpclass() {
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		wikisUI = WikisUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		fileviewerUI = FileViewerUI.getGui(cfg.getProductName(), driver);
		fUI =  ForumsUI.getGui(cfg.getProductName(), driver);
		iUI = IcecUI.getGui(cfg.getProductName(), driver);
	}

	/**
	*<ul>
	*<li><B>Info:</B>Create a community using create community widget (Tailored Experience)</li>
	*<li><B>Step:</B>Navigate to Create community page</li>
	*<li><B>Step:</B>Without entering community name click next</li>
	*<li><B>Verify:</B>Verify a warning icon is displayed on the vertical grid next to Community Details label</li>
	*<li><B>Step:</B>Click back to jump back to the Community Details tab</li>
	*<li><B>Verify:</B>Verify an error message 'Community name can't be empty' is displayed below community name input field</li>
	*<li><B>Step:</B>Enter community name and description and community type public and click next</li>
	*<li><B>Step:</B>Select default template and click next</li>
	*<li><B>Step:</B>Add testUserB, testUserC and testUserD as members to the community</li>
	*<li><B>Verify:</B>Verify that all 3 testUsers are added and displayed as 'members' on create community widget</li>
	*<li><B>Step:</B>Click next and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on create button to create community.</li>
	*<li><B>Verify:</B>Verify an alert is displayed with 2 options - Continue editing and Create</li>
	*<li><B>Step:</B>Click on continue editing from create community alert</li>
	*<li><B>Verify:</B>Verify user is redirected back to the Optional Settings tab</li>
	*<li><B>Step:</B>Click on create button to create community. Click create from create community alert</li>
	*<li><B>Verify:</B>Verify that community is created successfully and details are displayed correctly on community page/li>
	*</ul>
	 */
	@Test(groups = {"TailoredExperience", "cpunit", "cplevel2"})
	public void createCommunityWithTE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		List<Member> communityMemberList = new ArrayList<Member>();
		communityMemberList.add(memberB);
		communityMemberList.add(memberC);
		communityMemberList.add(memberD);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
				.build();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName)
				.addMembers(communityMemberList)
				.webAddress(Data.getData().commonAddress + Helper.genDateBasedRandVal())
				.communityImage(FilesUI.getFileUploadPath(file.getName(), cfg))
				.startPage(StartPage.OVERVIEW)
				.template("")
				.build();
		
		String expectedUserName=testUser.getDisplayName();
		
		logger.strongStep("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());

		if (cfg.getUseNewUI())  {
			ui.loadComponent(Data.getData().ComponentHomepage);
			ui.login(testUser);
			CommonUICnx8 commonUI = new CommonUICnx8(driver);
			commonUI.toggleNewUI(true);
			AppNavCnx8.COMMUNITIES.select(commonUI);
		} else {
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
		}
		
		filesUI.setLocalFileDetector();
		community.createCommunityFromTailoredExperienceWidget(ui, logger);
		
		logger.strongStep("Verify that community is created successfully");
		log.info("Verify that community is created successfully");
		
		verifyCommunity(logger, community.getName(), community.getDescription(), communityMemberList, community.getTags(), community.getWebAddress(), "community", expectedUserName);
		
		log.info("INFO: Removing community for Test case " + testName );
		deleteCommunity(logger, community);
		
		ui.endTest();
	}


	/**
	*<ul>
	*<li><B>Info:</B>Create a community using API</li>
	*<li><B>Step:</B>Navigate to the community page. Create a subcommunity through community actions-> create sub communities option.</li> 
	*<li><B>Verify:</B>Create community widget (Tailored Experience) opens</li>
	*<li><B>Step:</B>Without entering community name click next</li>
	*<li><B>Verify:</B>Verify a warning icon is displayed on the vertical grid next to Community Details label</li>
	*<li><B>Step:</B>Click back to jump back to the Community Details tab</li>
	*<li><B>Verify:</B>Verify an error message 'Community name can't be empty' is displayed below community name input field</li>
	*<li><B>Step:</B>Enter community name and description and community type public and click next</li>
	*<li><B>Step:</B>Select default template and click next</li>
	*<li><B>Verify:</B>User can add only parent community members</li>
	*<li><B>Step:</B>Select all parent community members to add them to the subcommunity</li>
	*<li><B>Step:</B>Click next and add a tag to the subcommunity and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the subcommunity</li>
	*<li><B>Step:</B>Click on create button to create subcommunity.</li>
	*<li><B>Verify:</B>Verify an alert is displayed with 2 options - Continue editing and Create</li>
	*<li><B>Step:</B>Click on continue editing from create community alert</li>
	*<li><B>Verify:</B>Verify user is redirected back to the Optional Settings tab</li>
	*<li><B>Step:</B>Click on create button to create subcommunity. Click create from create community alert</li>
	*<li><B>Verify:</B>Verify that the subcommunity is created successfully and details are displayed correctly on sub community page</li>
	*</ul>
	 */
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createSubCommunityWithTE(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		String testName = ui.startTest();
		List<Member> communityMemberList = new ArrayList<Member>();
		communityMemberList.add(memberB);
		communityMemberList.add(memberC);
		communityMemberList.add(memberD);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Community: this is a Test description for testcase " + testName)
				.addMembers(communityMemberList)
				.webAddress(Data.getData().commonAddress + Helper.genDateBasedRandVal())
				.build();
		
		BaseSubCommunity subcommunity = new BaseSubCommunity.Builder(Data.getData().commonName + "_subcomm_" + Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(BaseSubCommunity.Access.PUBLIC)
				.description("Subcommunity: this is a Test description for testcase " + testName)
				.UseParentmembers(true)
				.addMembers(communityMemberList)
				.webAddress(Data.getData().commonAddress + Helper.genDateBasedRandVal())
				.template("")
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		String expectedUserName=testUser.getDisplayName();
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.fluentWaitTextPresent(community.getName());
		subcommunity.createSubCommunityFromTailoredExperienceWidget(ui, community, logger);
		
		logger.strongStep("Verify that sub community is created successfully");
		log.info("Verify that sub community is created successfully");
		verifyCommunity(logger, subcommunity.getName(), subcommunity.getDescription(), communityMemberList, subcommunity.getTags(), community.getWebAddress(), "sub community", expectedUserName);
		
		log.info("INFO: Removing community for Test case " + testName );
		deleteCommunity(logger, community);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create new community using activity template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Create an activity with entry and todo inside same community</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Fetch the URL once the Highlights page for the community opens up</li>
	*<li><B>Step:</B>Logout and close the browser</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create an activity template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using Activity template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Verify that the community contains activity, entry and todo item from activity template</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete activity template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "activities-exclude"})
	public void createCommunityWithActivityTemplate() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String templateName= "ActivityTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();
		BaseCommunity community2 = new BaseCommunity.Builder(testName + "2" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.template(templateName)
				.build();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand()).dueDateRandom()
				.useCalPick(true).goal(Data.getData().commonDescription + testName).community(community).build();
		
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder
				.buildBaseActivityToDo(testName + Helper.genStrongRand());
				
		logger.strongStep("Create community and activity,entry, to do inside the same community");
		log.info("Create community and activity,entry, to do inside the same community");
		Community publicCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(community,
				BaseWidget.ACTIVITIES, testUserB, true, testUser, apiOwner);

		Activity communityActivity = CommunityActivityEvents.createCommunityActivity(activity, community, testUser,
				activityApiOwner, apiOwner, publicCommunity);

		BaseActivityEntry basePublicEntry = ActivityBaseBuilder.buildBaseActivityEntry(community.getName(),
				communityActivity, false);
		CommunityActivityEvents.createActivityEntry(testUser, activityApiOwner, basePublicEntry, communityActivity);
		
		CommunityActivityEvents.createActivityTodo(testUser, activityApiOwner, baseActivityTodo, communityActivity);

		logger.strongStep("Creating template with user" + adminUser.getDisplayName());
		log.info("Creating template with user" + adminUser.getDisplayName());
		ui.createTemplate(publicCommunity.getAlternateLink(), templateName, adminUser);

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		logger.strongStep("Create a new Community from Activity Template");
		log.info("INFO: Create a new Community from Activity Template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
		
		logger.strongStep("INFO: Click on the Activities tab");
		log.info("INFO: Click on the Highlights tab");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui);
		
		logger.strongStep("Verify activity inside new community");
		log.info("Verify activity inside new community");
		actUI.verifyActivityInfo(logger, activity);
		
		String entryUUID = actUI.getEntryUUID(basePublicEntry);
		actUI.expandEntry(entryUUID);
		
		logger.strongStep("Verify activity entry inside new community");
		log.info("Verify activity entry inside new community");
		actUI.validatePageInfo(logger, basePublicEntry);
		
		String todoRootUUID = actUI.getEntryUUID(baseActivityTodo);
		actUI.expandEntry(todoRootUUID);
		
		logger.strongStep("Verify activity todo inside new community");
		log.info("Verify activity todo inside new community");
		actUI.validateToDoInfo(logger, baseActivityTodo);
		
		logger.strongStep("Loggin out with "+testUser.getDisplayName()+" and closing the session");
		log.info("Loggin out with "+testUser.getDisplayName()+" and closing the session");
		ui.logout();

		logger.strongStep("INFO: Deleting community for Test case " + testName);
		log.info("INFO: Deleting community for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>The pending invitations in the base community are not available in the inheriting community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Add multiple members to the community</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Select the Members tab from the Navigation Menu</li>
	*<li><B>Step:</B>Send invitations to a couple of users who are not the members of the current community</li>
	*<li><B>Step:</B>Navigate to the Invitations tab from the Members tab</li>
	*<li><B>Verify:</B>The users to whom the invitations were sent are visible in the Invitations tab</li>
	*<li><B>Step:</B>Fetch the current URL to be used during the creation of template for the current community</li>
	*<li><B>Step:</B>Logout, navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and logout</li>
	*<li><B>Step:</B>Load the Communities component and Log In with a user different from the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on the Create button to create the community.</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Step:</B>Select the Members tab from the Navigation Menu</li>
	*<li><B>Step:</B>Send invitations to a couple of users who are not the members of the current community</li>
	*<li><B>Step:</B>Navigate to the Invitations tab from the Members tab</li>
	*<li><B>Verify:</B>The users to whom the invitations were sent in the inheriting community are visible in the Invitations tab</li>
	*<li><B>Verify:</B>The users to whom the invitations were sent in the base community are not visible in the Invitations tab</li>
	*<li><B>Step:</B>Remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithPendingInvitations() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName = "PendingInvitationsTemplate" + Helper.genDateBasedRand();

		User loginUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, loginUser.getAttribute(cfg.getLoginPreference()),loginUser.getPassword());

		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		User testUser3 = cfg.getUserAllocator().getUser();
		User testUser4 = cfg.getUserAllocator().getUser();

		Member member1 = new Member(CommunityRole.MEMBERS, testUser1);
		Member member2 = new Member(CommunityRole.MEMBERS, testUser2);
		Member member3 = new Member(CommunityRole.MEMBERS, testUser3);
		Member member4 = new Member(CommunityRole.MEMBERS, testUser4);
		
		BaseCommunity community = new BaseCommunity.Builder("BaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder("InheritingCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.template(templateName)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("INFO: Invite users: " + testUser1.getDisplayName() + ", " + testUser2.getDisplayName());
		log.info("INFO: Invite users: " + testUser1.getDisplayName() + ", " + testUser2.getDisplayName() + " using API");
		Invitation newInvite1 = new Invitation(testUser1.getEmail(), member1.getUUID(),
				"Invitation to join " + community.getName(),
				"Please consider joining this excellent community.");
		Invitation newInvite2 = new Invitation(testUser2.getEmail(), member2.getUUID(),
				"Invitation to join " + community.getName(),
				"Please consider joining this excellent community.");

		apiOwner.getService().createInvitation(comAPI, newInvite1);
		apiOwner.getService().createInvitation(comAPI, newInvite2);

		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		logger.strongStep("Use a user different from " + loginUser.getDisplayName() + " to create the Inheriting Community");
		log.info("INFO: Use a user different from " + loginUser.getDisplayName() + " to create the Inheriting Community");
		testUser = cfg.getUserAllocator().getUser();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Create a new community using the 'Start A Community' Drop Down");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(community2.getName());

		//Select Members from the Navigation Menu
		logger.strongStep("Select the Members tab from the Navigation Menu");
		log.info("INFO: Select the Members tab from the Navigation Menu");
		Community_TabbedNav_Menu.MEMBERS.select(ui, 2);

		//Click on the Invite Members button & invite a user
		logger.strongStep("Click on the 'Invite Members' button and invite the user: " + testUser3.getDisplayName());
		log.info("INFO: Click on the 'Invite Members' button and invite the user: " + testUser3.getDisplayName());
		try {
			ui.inviteMemberCommunity(member3);
		} catch (Exception e) {
			Assert.assertTrue(false, "Error inviting memeber: " + e.getMessage());
		}
		
		//Click on the Send Invitations button
		logger.strongStep("Click on the 'Send Invitations' button");
		log.info("INFO: Click on the 'Send Invitations' button");
		ui.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);
		
		//Click on the Invite Members button & invite a user
		logger.strongStep("Click on the 'Invite Members' button and invite the user: " + testUser4.getDisplayName());
		log.info("INFO: Click on the 'Invite Members' button and invite the user: " + testUser4.getDisplayName());
		try {
			ui.inviteMemberCommunity(member4);
		} catch (Exception e) {
			Assert.assertTrue(false, "Error inviting memeber: " + e.getMessage());
		}
		
		//Click on the Send Invitations button
		logger.strongStep("Click on the 'Send Invitations' button");
		log.info("INFO: Click on the 'Send Invitations' button");
		ui.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);
		
		logger.strongStep("Navigate to the Invitations tab");
		log.info("INFO: Navigate to the Invitations tab");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.InvitationsTab);
		
		logger.strongStep("Verify that " + testUser3.getDisplayName() + " and " + testUser4.getDisplayName() + " are visible in the Invitations tab");
		log.info("INFO: Verify that " + testUser3.getDisplayName() + " and " + testUser4.getDisplayName() + " are visible in the Invitations tab");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getUserName(testUser3)));
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getUserName(testUser4)));
	    
		logger.strongStep("Verify that the pending invitations from the Base Community i.e. " + testUser1.getDisplayName() + " and " + testUser2.getDisplayName() + " are not visible in the Invitations tab");
		log.info("INFO: Verify that the pending invitations from the Base Community i.e. " + testUser1.getDisplayName() + " and " + testUser2.getDisplayName() + " are not visible in the Invitations tab");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(ui.getUserName(testUser1)));
		Assert.assertFalse(driver.isElementPresent(ui.getUserName(testUser2)));
		driver.turnOnImplicitWaits();

		logger.strongStep("Logout from connections");
		log.info("INFO: Logout from connections");
		ui.logout();

		logger.strongStep("Removing the communities for the Test Case ");
		log.info("INFO: Removing the communities for the Test Case ");
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>The followers of the base community are not the followers of the inheriting community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Follow the community as another user</li>
        <li><B>Verify:</B>API: The community is in the I'm Following list..</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and logout</li>
	*<li><B>Step:</B>Load the Communities component and Log In</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Step:</B>Click on the Create button to create the community.</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button.  Logout.</li>
        *<li><B>Step:</B>Fetch the communities of the the followers of the base community via API</li>
	*<li><B>Verify:</B>API: The user who followed the base community does not follow the inheriting community.</li>
	*<li><B>Step:</B>Remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithFollowers() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String templateName = "FollowersTemplate" + Helper.genDateBasedRand();

		User testUser1 = cfg.getUserAllocator().getUser();
		User owner = cfg.getUserAllocator().getUser();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, owner.getUid(), owner.getPassword());
		apiFollower1 = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		List<Member> communityMemberList = new ArrayList<Member>();
		User member = cfg.getUserAllocator().getUser();
		communityMemberList.add(new Member(CommunityRole.MEMBERS, member));

		List<User> communityFollowersList = new ArrayList<User>();
		communityFollowersList.add(testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder("FollowersBaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder("FollowersInheritingCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.addMembers(communityMemberList)
				.template(templateName)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Follow the community as user '" + testUser1.getDisplayName());
		log.info("INFO: Follow the community as users '" + testUser1.getDisplayName()  + " using API");
		community.followAPI(comAPI, apiFollower1, apiOwner);

		logger.strongStep("Verify that the community '" + community.getName() + "' is followed by user '" + testUser1.getDisplayName() + "'");
		log.info("INFO: Verify that the community '" + community.getName() + "' is followed by user '" + testUser1.getDisplayName() + " using API");
		Feed followedCommunitiesFeed = (Feed) apiFollower1.getService().getFollowedCommunities();
		boolean isFollowing = false;
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(community.getName())) {
				isFollowing = true;
				break;
			}
		}
		Assert.assertTrue(isFollowing, "ERROR: The community '" + community.getName() + "' is not followed by " + testUser1.getDisplayName());

		logger.strongStep("Create a template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		
		testUser=cfg.getUserAllocator().getUser();

		logger.strongStep("Load Communities and Log In as: " + owner.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(owner);

		logger.strongStep("Create a new community using Tailored Experience Widget");
		log.info("INFO: Create a new community using Tailored Experience Widget");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.tabNavCommunityName + " div:contains(" + community2.getName() + ")");

		ui.logout();

		logger.strongStep("Verify that the community '" + community2.getName() + "' is not followed by user '" + testUser1.getDisplayName() + "'");
		log.info("INFO: Verify that the community '" + community2.getName() + "' is not followed by user '" + testUser1.getDisplayName() + "' using API");
		followedCommunitiesFeed = (Feed) apiFollower1.getService().getFollowedCommunities();
		isFollowing = false;
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(community2.getName())) {
				isFollowing = true;
				break;
			}
		}
		Assert.assertFalse(isFollowing, "ERROR: The community '" + community2.getName() + "' is not followed by " + testUser1.getDisplayName());

		log.info("INFO: Removing the communities for the Test Case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>Test new community copies blog content from original community created using blog template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Go to blog app and create a blog entry</li>
	*<li><B>Step:</B>Like the blog entry</li>
	*<li><B>Verify:</B>The blog entry has 1 like</li>
	*<li><B>Step:</B>Fetch the URL once the Highlights page for the community opens up</li>
	*<li><B>Step:</B>Logout and close the browser</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using Blog template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Verify that the blog entry from base community exists in the new community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete theWiki template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithBlogTemplate() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName = "BlogTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();
		List<Member> communityMemberList = new ArrayList<Member>();
		communityMemberList.add(memberB);

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.addMembers(communityMemberList)
				.template(templateName)
				.build();
				
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// create blog entry
		apiOwner.createBlogEntry(blogEntry, comAPI);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a Blog template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a Blog template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		
		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Creating new community using blog template");
		log.info("INFO: Creating new community using blog template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(community2.getName());
		
		logger.strongStep("Navigate to the Blog by clicking on View All Link");
		log.info("INFO: Navigate to the Blog");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.BlogViewAllLink);
		
		logger.strongStep("Verify that entry from base community exists in new community");
		log.info("INFO: Verify that entry from base community exists in new community");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry.getTitle()), "ERROR: Entry not found");
		
		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>The owners and members in the base community are not the owners and members in the inheriting community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Add multiple members and owners to the community</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL</li>
	*<li><B>Step:</B>Close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members and owners to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Click on the Create button to create the community.</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Step:</B>Click on the 'View All' link in the Members widget</li>
	*<li><B>Verify:</B>The current user who is also the community owner is listed on the Members page of the community</li>
	*<li><B>Verify:</B>The members and owners added to the inheriting community are listed on the Members page</li>
	*<li><B>Verify:</B>The members and owners added to the previous community are NOT listed on the Members page</li>
	*<li><B>Step:</B>Remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void validateMembersAndOwnersFromTheBaseCommunity() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String templateName = "MembersAndOwnersTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();
		User testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		Member memberE = new Member(CommunityRole.MEMBERS, cfg.getUserAllocator().getUser());
		Member ownerG = new Member(CommunityRole.OWNERS, cfg.getUserAllocator().getUser());

		Member memberB = new Member(CommunityRole.MEMBERS, cfg.getUserAllocator().getUser());
		Member ownerD = new Member(CommunityRole.OWNERS, cfg.getUserAllocator().getUser());
		
		List<Member> baseCommunityMemberList = new ArrayList<Member>();
		baseCommunityMemberList.add(memberB);
		baseCommunityMemberList.add(ownerD);
		
		List<Member> inheritingCommunityMemberList = new ArrayList<Member>();
		inheritingCommunityMemberList.add(memberE);
		inheritingCommunityMemberList.add(ownerG);

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.addMembers(baseCommunityMemberList)
				.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder("NewCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.template(templateName)
				.addMembers(inheritingCommunityMemberList)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		String expectedUser=testUser.getDisplayName();

		logger.strongStep("Create a new community using the 'Start A Community' Drop Down");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);

		Community_TabbedNav_Menu.MEMBERS.select(ui, 2);

		logger.strongStep("Validate the current user who is also the community owner: " + testUser.getDisplayName() + " is listed on the Members page");
		log.info("INFO: Validate the current user who is also the community owner: " + testUser.getDisplayName() + " is listed on the Members page");
		Assert.assertTrue(driver.isElementPresent("link=" + convertDisplayNameToCamelCase(expectedUser)), 
						  "ERROR : The current user who is also the community owner: " + testUser.getDisplayName() + " is not present on the Members page");

		logger.strongStep("Validate that the members and owners added to the community: " + community2.getName() + " are listed on the Members page");
		log.info("INFO: Validate that the members and owners added to the community: " + community2.getName() + " are listed on the Members page");
		boolean membersFlag=true;
		driver.changeImplicitWaits(2);
		
		for(Member mem : inheritingCommunityMemberList){
			log.info("INFO: Check for: " + mem.getUser().getDisplayName());
			if(!ui.isElementPresent("link=" + convertDisplayNameToCamelCase(mem.getUser().getDisplayName())) && 
					!ui.isElementPresent("link=" + mem.getUser().getDisplayName())) {
				log.error("ERROR: Expected member not found: " + mem.getUser().getDisplayName());
				membersFlag=false;
			}
		}
		
		driver.turnOnImplicitWaits();
		Assert.assertTrue(membersFlag, "ERROR : Members and owners of the new community are NOT present on the Members page");
		
		logger.strongStep("Validate that the members and owners added to the community: " + community.getName() + " are not listed on the Members page");
		log.info("INFO: Validate that the members and owners added to the community: " + community.getName() + " are not listed on the Members page");
		driver.changeImplicitWaits(2);
		
		for(Member mem : baseCommunityMemberList){
			log.info("INFO: Check for: " + mem.getUser().getDisplayName());
			if(ui.isElementPresent("link=" + convertDisplayNameToCamelCase(mem.getUser().getDisplayName())) && 
					ui.isElementPresent("link=" + mem.getUser().getDisplayName())) {
				log.error("ERROR: Unexpected member found: " + mem.getUser().getDisplayName());
				membersFlag=false;
			}
		}
		
		driver.turnOnImplicitWaits();
		Assert.assertTrue(membersFlag, "ERROR : Members and owner of the base community are present on the Members page");
		
		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		log.info("INFO: Removing the communities for the Test Case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Test new community copies forum topic reply content from original community created using forum topic reply template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user, whcih has forum topic and reply added in it</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Navigate to the Forums page in the community</li>
	*<li><B>Step:</B>Click on the link of the forum topic and like the forum topic as well the as reply</li>
	*<li><B>Verify:</B>Verify that both the forum topic and the topic reply now have 1 likes each</li>
	*<li><B>Step:</B>Fetch the URL once the Highlights page for the community opens up</li>
	*<li><B>Step:</B>Logout and close the browser</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using forum topic reply template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Verify that the forum, forum topic and forum replies all have the author as the admin user of the community</li>
	*<li><B>Verify:</B>Verify that both the forum topic and the topic reply have no likes after being copied to the Inheriting Community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete theWiki template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithForumTE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String templateName = "ForumTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder("communityOne" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder("communityTwo"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("This is a Test description for testcase CommunityTwo" + testName)
				.template(templateName)
				.build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("Topic" + Helper.genDateBasedRandVal())
		   		 .tags(Data.getData().commonTag)
		   		 .description("This is a Test description for testcase ForumTopic")
		   		 .partOfCommunity(community)
		   		 .build();
		
		Community publicCommunity = CommunityEvents.createNewCommunity(community, testUser, apiOwner);
		
		CommunityForumEvents.createForumTopicAndAddReply(testUser, apiOwner, publicCommunity, topic, forumApiOwner, topicReply);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, publicCommunity);
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(community.getName());
		String communityUrl = driver.getCurrentUrl();

		logger.strongStep("INFO: Click on the Forum tab");
		log.info("INFO: Click on the Highlights tab");
		Community_TabbedNav_Menu.FORUMS.select(ui);		

		logger.strongStep("Click on the link of the forum topic: " + topic.getTitle());
		log.info("INFO: Click on the link of the forum topic: " + topic.getTitle());
		driver.getFirstElement("link=" + topic.getTitle()).click();

		logger.strongStep("Like the topic '" + topic.getTitle() + "' and the topic reply");
		log.info("INFO: Like the topic '" + topic.getTitle() + "' and the topic reply");
		ui.clickLinkWait(ui.getForumTopic(topic.getTitle()));
		ui.clickLinkWait(CommunitiesUIConstants.topicReply);

		logger.strongStep("Verify that the forum topic '" + topic.getTitle() + "' now has 1 like");
		log.info("INFO: Verify that the forum topic '" + topic.getTitle() + "' now has 1 like");
		Assert.assertEquals(driver.getSingleElement(ui.getTopic(topic.getTitle())).getText(), "1",
					  "ERROR: The forum topic '" + topic.getTitle() + "' does not have 1 like");
   	  
		logger.strongStep("Verify that the topic reply now has 1 like");
		log.info("INFO: Verify that the topic reply now has 1 like");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.topicReplyLike).getText(), "1",
						  "ERROR: The topic reply does not have 1 like");

		ui.logout();
		
		logger.strongStep("Create a Blog template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a Blog template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(communityUrl, templateName, adminUser);
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Creating new community using Forum template");
		log.info("INFO: Creating new community using Forum template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
		
		logger.strongStep("INFO: Click on the Forum tab");
		log.info("INFO: Click on the Highlights tab");
		Community_TabbedNav_Menu.FORUMS.select(ui,2);		
		fUI.clickLinkWait(ForumsUIConstants.communityForumsTab);
		
		logger.strongStep("Verify that the link for the default forum has the author as adminUser");
		log.info("INFO: Verify that the link for the default forum has the author as adminUser");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getLinkOwner(community.getName(), adminUser, 6)),
						  "ERROR: The default forum does not have the author as adminUser");
		
		driver.getFirstElement("link=" + community.getName()).click();
		
		logger.strongStep("Verify that the link for the forum topic  has 'Started By' and 'By' as adminUser");
		log.info("INFO: Verify that the link for the forum topic  has 'Started By' and 'By' as adminUser");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getLinkOwner(topic.getTitle(), adminUser, 2)),
						  "ERROR: The forum topic's link does not have 'Started By' as adminUser");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getLinkOwner(topic.getTitle(), adminUser, 3)),
						  "ERROR: The forum topic's link does not have 'By' as adminUser");

		driver.getFirstElement("link=" + topic.getTitle()).click();
		
		logger.strongStep("Verify that Topic title, Description, text 'Topic Reply' and topic reply are present");
		log.info("INFO: Verifying Topic title, Description, text 'Topic Reply' and topic reply are present");
		Assert.assertTrue(driver.isTextPresent(topic.getTitle()));		
		Assert.assertTrue(driver.isTextPresent(topic.getDescription()));
		Assert.assertTrue(driver.isTextPresent("Topic Reply"));
		Assert.assertTrue(driver.isTextPresent(topicReply));

		logger.strongStep("Verify that the forum topic has the author as adminUser");
		log.info("INFO: Verify that the forum topic has the author as adminUser");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getAdminUserForumTopic(topic.getTitle(), adminUser.getDisplayName())),
				"ERROR: The forum topic does not have the author as adminUser");
		
		logger.strongStep("Verify that the topic reply has the author as adminUser");
		log.info("INFO: Verify that the topic reply has the author as adminUser");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.adminUserReply(adminUser.getDisplayName())),
						  "ERROR: The topic reply does not have the author as adminUser");

		logger.strongStep("Verify that the forum topic has no likes after being copied to the Inheriting Community");
		log.info("INFO: Verify that the forum topic has no likes after being copied to the Inheriting Community");
		Assert.assertEquals(driver.getSingleElement(ui.getTopic(topic.getTitle())).getText(), " ",
						  "ERROR: The forum topic does not have 0 likes");
   	  
		logger.strongStep("Verify that the topic reply has no likes after being copied to the Inheriting Community");
		log.info("INFO: Verify that the topic reply has no likes after being copied to the Inheriting Community");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.topicReplyLike).getText(), " ",
						  "ERROR: The topic reply does not have 0 likes");

		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		log.info("INFO: Removing community for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>The Important Bookmarks in the base community are available in the inheriting community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Create multiple Bookmarks for the community using API</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL</li>
	*<li><B>Step:</B>Close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In with a user different from the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on the Create button to create the community.</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Step:</B>Click on the 'View All' link in the 'Important Bookmarks' widget</li>
	*<li><B>Verify:</B>All 'Important Bookmarks' created previously are visible on the Bookmarks page</li>
	*<li><B>Verify:</B>The author of all 'Important Bookmarks' is the admin user of the community</li>
	*<li><B>Step:</B>Navigate to the Highlights page</li>
	*<li><B>Verify:</B>Half of the 'Important Bookmarks' created in the base community are visible in the 'Important Bookmarks' widget on the Highlights page and half of them are not visible</li>
	*<li><B>Step:</B>Navigate to the 'My Communities' page and add the Overview page to the Inheriting Community if it is not the default landing page</li>
	*<li><B>Step:</B>Click on the 'Community Card' for the Inheriting Community to land on its Overview page</li>
	*<li><B>Verify:</B>Half of the 'Important Bookmarks' created in the base community are visible in the 'Important Bookmarks' widget on the Overview page and half of them are not visible</li>
	*<li><B>Step:</B>Remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithImportantBookmarks() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User loginUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, loginUser.getAttribute(cfg.getLoginPreference()),loginUser.getPassword());
		
		String testName = ui.startTest();
		String templateName = "ImportantBookmarksTemplate" + Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("BaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder("InheritingCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.template(templateName)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create multiple 'Important Bookmarks' in the community");
		log.info("INFO: Create multiple 'Important Bookmarks' in the community");
		BaseDogear[] bookmarks = iUI.createBookmarks(loginUser, serverURL, community, 6);

		logger.strongStep("Create a Important Bookmark template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a Important Bookmark template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		logger.strongStep("Use a user different from " + loginUser.getDisplayName() + " to create the Inheriting Community");
		log.info("INFO: Use a user different from " + loginUser.getDisplayName() + " to create the Inheriting Community");
		testUser = cfg.getUserAllocator().getUser();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Creating new community using Important Bookmark template");
		log.info("INFO: Creating new community using Important Bookmark template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);

		logger.weakStep("Validate that the " + community.getName() + " name is present");
		log.info("INFO: Validate that the " + community.getName() + " name is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getName()),"ERROR: Unable to find the " + community.getName() + " name");
			
		logger.strongStep("Click on the 'View All' link in the 'Bookmarks' widget");
		log.info("INFO: Click on the 'View All' link in the 'Bookmarks' widget");
		ui.clickLinkWait(CommunitiesUIConstants.IMViewAllLink);

		logger.strongStep("Verify that all 'Important Bookmarks' created previously are visible on the Bookmarks page with the author as '" + adminUser.getDisplayName());
		log.info("INFO: Verify that all 'Important Bookmarks' created previously are visible on the Bookmarks page with the author as '" + adminUser.getDisplayName());
		for(int i = 0; i < bookmarks.length; i++) {
			ui.fluentWaitPresentWithRefresh(ui.getBookmarkTitle(bookmarks[i].getTitle()));
			Assert.assertTrue(ui.fluentWaitPresent(ui.getBookmarkTitle(bookmarks[i].getTitle())),
					"Bookmark with title not visible on the Bookmarks page");
			Assert.assertEquals(driver.getSingleElement(ui.getBookmarkAuthor(bookmarks[i].getTitle(), adminUser.getDisplayName())).getText(), adminUser.getDisplayName(),
					"Bookmark with title does not have the author");
		}

		logger.strongStep("Navigate to the Highlights page");
		log.info("INFO: Navigate to the Highlights page");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);

		for(int i = 0; i < bookmarks.length; i++) {
			if(i % 2 == 0) {
				logger.strongStep("Verify that the bookmark: " + bookmarks[i].getTitle() + " is visible in the 'Important Bookmarks' widget on the Highlights page");
				log.info("INFO: Verify that the bookmark: " + bookmarks[i].getTitle() + " is visible in the 'Important Bookmarks' widget on the Highlights page");
				Assert.assertTrue(ui.fluentWaitPresent(ui.getBookmarkLink(bookmarks[i].getURL(), bookmarks[i].getTitle())),
						"Bookmark with title " + bookmarks[i].getTitle() + " not found");
			}  else {
				logger.strongStep("Verify that the bookmark: " + bookmarks[i].getTitle() + " is not visible in the 'Important Bookmarks' widget on the Highlights page");
				log.info("INFO: Verify that the bookmark: " + bookmarks[i].getTitle() + " is not visible in the 'Important Bookmarks' widget on the Highlights page");
				Assert.assertFalse(ui.isElementVisible(ui.getBookmarkLink(bookmarks[i].getURL(), bookmarks[i].getTitle())),
						"Bookmark with title " + bookmarks[i].getTitle() + " displayed when it shouldn't be");
			}
		}

		ui.logout();

		log.info("INFO: Removing the communities for the Test Case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>The subcommunity created in the base community is not a subcommunity in the inheriting community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Add multiple members to the community</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Fetch the URL once the Highlights page for the community opens up</li>
	*<li><B>Step:</B>Create a subcommunity in the community using the "Create Subcommunity" link under the "Community Actions" menu</li>
	*<li><B>Verify:</B>The subcommunity is created successfully</li>
	*<li><B>Step:</B>Logout and close the browser</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL</li>
	*<li><B>Step:</B>Close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members and owners to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on the Create button to create the community.</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Step:</B>Create a subcommunity in the community using the "Create Subcommunity" link under the "Community Actions" menu</li>
	*<li><B>Verify:</B>The subcommunity is created successfully</li>
	*<li><B>Step:</B>Go back to the parent community by clicking on its link</li>
	*<li><B>Step:</B>Click on the Subcommunities navigation menu</li>
	*<li><B>Verify:</B>The subcommunity just created is visible in the Subcommunities navigation menu</li>
	*<li><B>Verify:</B>The subcommunity which was created in the Base Community is not visible in the Subcommunities navigation menu</li>
	*<li><B>Step:</B>Remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void validateSubCommunityFromTheBaseCommunity(){
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String templateName = "SubcommunitiesTemplate" + Helper.genDateBasedRand();
		
		User testUser = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		List<Member> communityMemberList = new ArrayList<Member>();
		communityMemberList.add(memberB);
		communityMemberList.add(memberC);
		communityMemberList.add(memberD);
		
		BaseCommunity community = new BaseCommunity.Builder("BaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.addMembers(communityMemberList)
				.template("")
				.build();
		
		BaseSubCommunity subcommunity = new BaseSubCommunity.Builder("SubCommunityForTheBaseCommunity" + Helper.genDateBasedRandVal())
				.access(BaseSubCommunity.Access.PUBLIC)
				.UseParentmembers(true)
				.template("")
				.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder("InheritingCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.addMembers(communityMemberList)
				.template("")
				.build();
		
		BaseSubCommunity subcommunity2 = new BaseSubCommunity.Builder("SubCommunityForTheInheritingCommunity" + Helper.genDateBasedRandVal())
				.access(BaseSubCommunity.Access.PUBLIC)
				.template(templateName)
				.UseParentmembers(true)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.fluentWaitTextPresent(community.getName());

		logger.strongStep("Fetch the current URL");
		log.info("INFO: Fetch the current URL");
		String communityUrl = driver.getCurrentUrl();

		logger.strongStep("Create a subcommunity: " + subcommunity.getName() + " in the community: " + community.getName());
		log.info("INFO: Create a subcommunity: " + subcommunity.getName() + " in the community: " + community.getName());
		subcommunity.createSubCommunityFromTailoredExperienceWidget(ui, community, logger);

		logger.weakStep("Validate that the " + subcommunity.getName() + " name is present");
		log.info("INFO: Validate that the " + subcommunity.getName() + " name is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(subcommunity.getName()),
						 "ERROR: Unable to find the " + subcommunity.getName() + " name");
		
		ui.logout();
		driver.close();
		
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);

		logger.strongStep("Create a Forum template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a Forum template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(communityUrl, templateName, adminUser);
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		logger.strongStep("Creating new community using Tailored Experience Widget");
		log.info("INFO: Creating new community using Tailored Experience Widget");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);

		logger.weakStep("Validate that the " + community2.getName() + " name is present");
		log.info("INFO: Validate that the " + community2.getName() + " name is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(community2.getName()),
						 "ERROR: Unable to find the " + community2.getName() + " name");

		logger.strongStep("Create a subcommunity: " + subcommunity2.getName() + " in the community: " + community2.getName());
		log.info("INFO: Create a subcommunity: " + subcommunity2.getName() + " in the community: " + community2.getName());
		subcommunity2.createSubCommunityFromTailoredExperienceWidget(ui, community2, logger);
		
		logger.strongStep("Go back to the parent community by clicking on its link");
		log.info("INFO: Go back to the parent community by clicking on its link");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.communityName);
		
		logger.strongStep("Click on the Subcommunities navigation menu");
		log.info("INFO: Click on the Subcommunities navigation menu");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.tabbedNavSubcommLink);
				
		logger.strongStep("Verify the subcommunity: " + subcommunity2.getName() + " is visible in the Subcommunities navigation menu");
		log.info("INFO: Verify the subcommunity: " + subcommunity2.getName() + " is visible in the Subcommunities navigation menu");
		Assert.assertTrue(driver.getVisibleElements(ui.getSubCommunityName(subcommunity2)).size() == 1, "ERROR : The subcommunity: " + subcommunity2.getName() + " is not visible in the Subcommunities navigation menu");

		logger.strongStep("Verify the subcommunity: " + subcommunity.getName() + " which was created in the Base Community is not visible in the Subcommunities navigation menu");
		log.info("INFO: Verify the subcommunity: " + subcommunity.getName() + " which was created in the Base Community is not visible in the Subcommunities navigation menu");
		Assert.assertFalse(driver.isElementPresent(ui.getSubCommunityName(subcommunity)), "ERROR : The subcommunity: " + subcommunity.getName() + " which was created in the community: " + community.getName() + " is visible in the Subcommunities navigation menu");
		
		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();

		log.info("INFO: Removing the communities for the Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);
		
		ui.endTest();
		
	}
	
	/**	
	*<li><B>Info:</B>Test new community copies wiki content from original community created using wiki template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Go to wiki app and create a wiki page and a child page</li>
	*<li><B>Step:</B>Like the default wiki, the wiki page and the child page</li>
	*<li><B>Verify:</B>The default wiki, the wiki page and the child page each have 1 like</li>
	*<li><B>Step:</B>Fetch the URL once the Highlights page for the community opens up</li>
	*<li><B>Step:</B>Logout and close the browser</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using Wiki template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>The Wiki page from Base community exist in community just created</li>
	*<li><B>Verify:</B>The Child page from Base community exist in community just created</li>
	*<li><B>Verify:</B>The default wiki, wiki page and child page all have the author as the admin user of the community</li>
	*<li><B>Verify:</B>The default wiki, the wiki page and the child page have no likes after being copied to the Inheriting Community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete theWiki template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithWikiTemplate() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName = "WikiTemplate" + Helper.genDateBasedRand();
		
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		BaseWikiPage wikiPage = new BaseWikiPage.Builder("Wiki_Page_" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
				.tags("tag1, tag2")
				.description("this is a test description for creating a Peer wiki page")
				.build();
		
		BaseWikiPage childPage = new BaseWikiPage.Builder("Child_Page_" + Helper.genMonthDateBasedRandVal(), PageType.Child)
					.tags("tag1, tag2")
					.description("this is a test description for creating a Child wiki page")
					.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// Navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(community.getName());

		//Navigate to the Wiki and create a Wiki Page and a child page
		logger.strongStep("Navigate to the Wiki and Add wiki page and a child page");
		log.info("INFO: Navigate to the Wiki and Add wiki page and a child page");
		Community_TabbedNav_Menu.WIKI.select(ui);
		
		logger.strongStep("Like the default wiki: " + community.getName());
		log.info("INFO: Like the default wiki: " + community.getName());
		ui.clickLinkWait(ui.getWikiLikeAuthorlink(community.getName(), "//a[text()='Like']"));
		
		logger.strongStep("Verify that the default wiki now has 1 like");
		log.info("INFO: Verify that the default wiki now has 1 like");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(community.getName(), "//div[@class='lotusLikeText']")).getText(), "1",
        				  "ERROR: The default wiki does not have 1 like");

		wikiPage.create(wikisUI);
		
		logger.strongStep("Like the wiki page: " + wikiPage.getName());
		log.info("INFO: Like the wiki page: " + wikiPage.getName());
		ui.clickLinkWait(ui.getWikiLikeAuthorlink(wikiPage.getName(), "//a[text()='Like']"));
		
		logger.strongStep("Verify that the wiki page '" + wikiPage.getName() + "' now has 1 like");
		log.info("INFO: Verify that the wiki page '" + wikiPage.getName() + "' now has 1 like");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(wikiPage.getName(), "//div[@class='lotusLikeText']")).getText(), "1",
        				  "ERROR: The wiki page does not have 1 like");

		childPage.create(wikisUI);
		
		logger.strongStep("Like the child page: " + childPage.getName());
		log.info("INFO: Like the child page: " + childPage.getName());
		ui.clickLinkWait(ui.getWikiLikeAuthorlink(childPage.getName(), "//a[text()='Like']"));
		
		logger.strongStep("Verify that the child page '" + childPage.getName() + "' now has 1 like");
		log.info("INFO: Verify that the child page '" + childPage.getName() + "' now has 1 like");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(childPage.getName(), "//div[@class='lotusLikeText']")).getText(), "1",
        				  "ERROR: The child page does not have 1 like");

		//Get community URL after clicking on highlight tab
		logger.strongStep("Click on the Highlight tab and fetch the community URL");
		log.info("INFO: Click on the Highlight tab and fetch the community URL");
		ui.clickLinkWait(CommunitiesUIConstants.communityHighlightTab);
		String communityUrl = driver.getCurrentUrl();
		
		//Logout and close the session
		logger.strongStep("Logging out from community and closing session with user" + testUser.getDisplayName());
		log.info("INFO: Logging out from community and closing session with user" + testUser.getDisplayName());
		ui.logout();
		driver.close();
		
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);

		logger.strongStep("Create a Wiki template with user" + adminUser.getDisplayName() + "and Logout");
		log.info("INFO: Creating a Wiki template with user" + adminUser.getDisplayName() + "and Logout");
		ui.createTemplate(communityUrl, templateName, adminUser);
		
		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUserB.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUserB.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUserB);

		logger.strongStep("Creating new community using Wiki template");
		log.info("INFO: Creating new community using Wiki template");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);

		//Verify pages have been copies from base community	in new community created using wiki template	
		logger.strongStep("Validate that the Wiki Page from base community exists in new community");
		log.info("INFO: Verify the Wiki Page from base community exists in new community");
		ui.fluentWaitElementVisible(BaseUIConstants.Community_Actions_Button);
		Community_TabbedNav_Menu.WIKI.select(ui);
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR:" + wikiPage.getName() + "Page does not exist");
		
		logger.strongStep("Verify that the default wiki has the author as adminUser");
		log.info("INFO: Verify that the default wiki has the author as adminUser");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(community.getName(), "/a[@aria-label='" + adminUser.getDisplayName() + "']")).getText(), adminUser.getDisplayName(),
        				  "ERROR: The default wiki does not have the author as adminUser");

		logger.strongStep("Verify that the default wiki has no likes after being copied to the Inheriting Community");
		log.info("INFO: Verify that the default wiki has no likes after being copied to the Inheriting Community");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(community.getName(), "//div[@class='lotusLikeText']")).getText(), " ",
        				  "ERROR: The default wiki does not have 0 likes");

		logger.strongStep("Validate that the child Page from base community exists in new community");
		log.info("INFO: Verify the child Page from base community exists in new community");
		ui.clickLinkWait(WikisUI.getWikiPage(wikiPage));
		Assert.assertTrue(ui.fluentWaitTextPresent(childPage.getName()),
		  				  "ERROR:" + childPage.getName() + "Page does not exist");
		
		logger.strongStep("Verify that the wiki page has the author as adminUser");
		log.info("INFO: Verify that the wiki page has the author as adminUser");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(wikiPage.getName(), "/a[@aria-label='" + adminUser.getDisplayName() + "']")).getText(), adminUser.getDisplayName(),
						  "ERROR: The wiki page does not have the author as adminUser");
   	  
		logger.strongStep("Verify that the wiki page has no likes after being copied to the Inheriting Community");
		log.info("INFO: Verify that the wiki page has no likes after being copied to the Inheriting Community");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(wikiPage.getName(), "//div[@class='lotusLikeText']")).getText(), " ",
        				  "ERROR: The wiki page does not have 0 likes");

		logger.strongStep("Navigate to the child page by clicking on its link in the left panel");
		log.info("INFO: Navigate to the child page by clicking on its link in the left panel");
		ui.clickLinkWait(WikisUI.getWikiPage(childPage));

		logger.strongStep("Verify that the child page has the user as adminUser");
		log.info("INFO: Verify that the child page has the user as adminUser");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(childPage.getName(), "/a[@aria-label='" + adminUser.getDisplayName() + "']")).getText(), adminUser.getDisplayName(),
						  "ERROR: The child page does not have the author as adminUser");

		logger.strongStep("Verify that the child page has no likes after being copied to the Inheriting Community");
		log.info("INFO: Verify that the child page has no likes after being copied to the Inheriting Community");
		Assert.assertEquals(driver.getSingleElement(ui.getWikiLikeAuthorlink(childPage.getName(), "//div[@class='lotusLikeText']")).getText(), " ",
        				  "ERROR: The child page does not have 0 likes");

		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>This test case will create, edit and delete category</li>
	*<li><B>Step:</B>Login to tailored Exeperience admin page with admin user</li>
	*<li><B>Step:</B>Create a category</li>
	*<li><B>Verify:</B>Verify new category created successfully</li>
	*<li><B>Step:</B>Edit newly created category</li>
	*<li><B>Verify:</B>Verify category gets edited successfully</li>
	*<li><B>Step:</B>Delete the template category</li>
	*<li><B>Verify:</B>Verify category gets deleted successfully</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createEditAndDeleteTemplateCategory() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		String categoryName = "category" + Helper.genDateBasedRand();

		logger.strongStep("Creating, Editing and Deleting template category");
		log.info("Creating, Editing and Deleting template category");
		Supplier<Void> adminActions = () -> {
			ui.createCategory(categoryName);
			String categoryNameEdited = categoryName + "edited";
			ui.editCategory(categoryNameEdited);
			ui.clickLinkWait(CommunitiesUIConstants.templateAdmin);
			ui.deleteCategory(categoryNameEdited);

			return null;
		};
		CommunitiesUI.performTEAdminActions(adminActions, adminUser, ui);

		ui.endTest();
	}

	
	/**
	*<ul>
	*<li><B>Info:</B>This test case will set category to template and search template with this category</li>
	*<li><B>Step:</B>Create Community with api</li>
	*<li><B>Step:</B>Create template with community created in above step</li>
	*<li><B>Step:</B>Login to tailored Exeperience admin page with admin user</li>
	*<li><B>Step:</B>Create a category</li>
	*<li><B>Verify:</B>Verify new category created successfully</li>
	*<li><B>Step:</B>Open template admin page again</li>
	*<li><B>Step:</B>Set category created above to the template</li>
	*<li><B>Step:</B>Save edited template</li>
	*<li><B>Step:</B>Open template admin page again</li>
	*<li><B>Step:</B>Select category to search template with</li>
	*<li><B>Verify:</B>Verify template gets searched with same category</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void verifySettingCategoryToTemplateAndSearch() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String templateName = "TemplateToEdit" + Helper.genDateBasedRand();
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		// create community
		logger.strongStep("Create Public Community using API");
		log.info("INFO: Create Public Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Create a public community template with user" + adminUser.getDisplayName() + "and Close the session");
		log.info("INFO: Creating a public community template with user" + adminUser.getDisplayName() + "and Close the session");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		
		Supplier<Void> categoryActions = () -> {
			String categoryName = "category" + Helper.genDateBasedRand();

			logger.strongStep("Creating template category");
			log.info("Creating template category");
			ui.createCategory(categoryName);
			
			logger.strongStep("Setting template category");
			log.info("Setting template category");
			ui.setCategoryToTemplate(categoryName,templateName);
			
			// refresh the page before searching it takes a bit to be searchable
			UIEvents.refreshPage(driver);
			
			logger.strongStep("Search template with category "+categoryName);
			log.info("Search template with category "+categoryName);
			ui.searchTemplateWithCategory(categoryName,templateName);
			
			logger.strongStep("Delete category");
			log.info("INFO: Delete category");
			ui.deleteCategory(categoryName);

			return null;
		};
		CommunitiesUI.performTEAdminActions(categoryActions, adminUser, ui);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);

		ui.endTest();
	}
	
	/**	
	*<li><B>Info:</B>Test Edit an existing Template.</li>
	*<li><B>Step:</B>Create a community using API</li>
	*<li><B>Step:</B>Navigate to the Template admin page and create a new template</li>
	*<li><B>Step:</B>Create a new category under template Administrator page</li>
	*<li><B>Step:</B>Edit the created template and update template name ,description,select category and upload images</li>
	*<li><B>Step:</B>Click on template preview button</li>
	*<li><B>Verify:</B>Verify template name has been updated</li>
	*<li><B>Verify:</B>Verify template Description has been updated</li>
	*<li><B>Verify:</B>Verify that the category name is selected</li>
	*<li><B>Step:</B>Delete created category</li>
	*<li><B>Step:</B>Delete created template</li>
	*<li><B>Step:</B>Remove created communities</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void verifyEditAnExistingTemplate() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String templateName= "EditTemplate" + Helper.genDateBasedRand();
		String categoryName = "category" + Helper.genDateBasedRand();
		String templateNameEdited=templateName+ " Edited";
		String templateDesEdited=templateName+ " Description Edited";
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg")
				 .build();
		
		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
				 .comFile(true)
				 .extension(".jpg")
				 .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a new template");
		log.info("INFO: Create a new template");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		
		Supplier<List<String>> categoryActions = () -> {
			logger.strongStep("Create a new category");
			log.info("INFO: Create a new category");
			ui.loadComponent(Data.getData().TailoredExperience_Admin, true);
			ui.createCategory(categoryName);
			
			logger.strongStep("Click on Edit template, update Template name, description, select category and upload images");
			log.info("INFO: Click on Edit template, update Template name, description, select category and upload images");
			filesUI.setLocalFileDetector();
			ui.editTemplate(templateName, categoryName, templateNameEdited, templateDesEdited, fileA, fileB);
			
			logger.strongStep("Click on Preview Template button");
			log.info("INFO: Click on Preview Template button");
			ui.clickLinkWait(CommunitiesUIConstants.previewTemplateButton);
			
			logger.strongStep("Verify that the template name has been changed");
			log.info("INFO: Verify that the template name has been changed");
			Assert.assertTrue(ui.fluentWaitTextPresent(templateNameEdited), "ERROR: Template name is not changed");
			
			logger.strongStep("Verify that the template description has been changed");
			log.info("INFO: Verify that the template description has been changed");
			Assert.assertTrue(ui.fluentWaitTextPresent(templateDesEdited), "ERROR: Template description is not changed");
			
			logger.strongStep("Verify that the category name is selected");
			log.info("INFO: Verify that the category name is selected");
			Assert.assertTrue(ui.fluentWaitTextPresent(categoryName), "ERROR: Category name is not present");
			
			logger.strongStep("Close template page");
			log.info("INFO: Close template page");
			ui.clickLinkWait(CommunitiesUIConstants.closeTemplateBtn);
			
			logger.strongStep("Delete created category");
			log.info("INFO: Delete created category");
			ui.deleteCategory(categoryName);
			
			return null;
		};
		CommunitiesUI.performTEAdminActions(categoryActions, adminUser, ui);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateNameEdited);
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test and verify export and import of a template</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Create a new template</li>
	*<li><B>Step:</B>Export created template and get the downloaded file name</li>
	**<li><B>Verify:</B>Verify template has been exported</li>
	*<li><B>Step:</B>Delete the template</li>
	*<li><B>Verify:</B>Verify template has been deleted</li>
	*<li><B>Step:</B>Click on import template and import exported template</li>
	*<li><B>Verify:</B>Verify that the template is imported</li>
	*<li><B>Step:</B>Remove imported template</li>
	*<li><B>Step:</B>Remove Community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void verifyImportAndExportTemplate() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String templateName = "ImportExportTemplate" + Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("BaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a new template logged in as:" + adminUser.getDisplayName());
		log.info("INFO: Create a new template logged in as:" + adminUser.getDisplayName());
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		
		Supplier<Void> categoryActions = () -> {
			log.info("Load Tailored Experience Admin Page and select created template");
			ui.loadComponent(Data.getData().TailoredExperience_Admin, true);
			ui.selectTemplate(templateName);
			
			logger.strongStep("Click on Export button from template");
			log.info("INFO: Click on Export button from template");
			ui.getFirstVisibleElement(CommunitiesUIConstants.exportTemplateButton).click();
			
			logger.strongStep("Get downloaded file name");
			log.info("INFO: Get downloaded file name");
			String downloadedFileName = filesUI.getDownloadedFileName();
			
			logger.strongStep("Verify Template has been exported");
			log.info("INFO: Verify Template has been exported");
			try {
				filesUI.verifyFileDownloaded(downloadedFileName);
			} catch (Exception e) {
				Assert.assertTrue(false, "Template cannot be exported: " + e.getMessage());
			}
			
			logger.strongStep("Delete the current template");
			log.info("INFO: Delete the current template");
			ui.selectTemplate(templateName);
			ui.clickLinkWait(CommunitiesUIConstants.deleteTemplateButton);
			ui.clickLinkWait(CommunitiesUIConstants.yesButtonDeleteConfrimation);
			
			logger.strongStep("Verify template has been removed");
			log.info("INFO: Verify template has been removed");
			ui.verifyTemplateDeleted(templateName);
			
			logger.strongStep("Click on Import template and select exported file");
			log.info("INFO: Click on Import template and select exported file");
			filesUI.setLocalFileDetector();
			String absolutePathForFile=(cfg.getTestConfig().getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(Data.getData().downloadsFolderSelenoid, Utils.getThreadLocalUniqueTestName().replace(".", "_"), downloadedFileName));
			driver.getSingleElement(CommunitiesUIConstants.importTemplateButton).typeFilePath(absolutePathForFile);
			
			logger.strongStep("Verify template is imported");
			log.info("INFO: Verify template is imported");
			Assert.assertTrue(ui.fluentWaitTextPresent("The template has been saved successfully."), "ERROR:'" + templateName + "' is not imported.");
			
			logger.strongStep("Select imported template and Delete");
			log.info("INFO: Select imported template and Delete");
			ui.loadComponent(Data.getData().TailoredExperience_Admin, true);
			ui.selectTemplate(templateName);
			ui.clickLinkWait(CommunitiesUIConstants.deleteTemplateButton);
			ui.clickLinkWait(CommunitiesUIConstants.yesButtonDeleteConfrimation);
			
			return null;
		};
		CommunitiesUI.performTEAdminActions(categoryActions, adminUser, ui);

		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);

		ui.endTest();
	}
	
	/**	
	*<li><B>Info:</B>Test new community copies all files from original community created using files template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Upload a file using API</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using filesTemplate</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>All the files from Base community exist in new community</li>
	*<li><B>Verify:</B>The files have the author as the admin user of the community</li>
	*<li><B>Verify:</B>The files have no likes after being copied to the Inheriting Community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the files template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithFilesTE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName= "FilesTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		String fileName = testName + "File" + Helper.genDateBasedRand();
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg")
				 .rename(fileName)
				 .build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a Community File using API");
		log.info("INFO: Create a Community File using API");
		File file = new File(FilesUI.getFileUploadPath(fileA.getName(), cfg));
		fileA.createAPI(filesApiOwner, file, comAPI);
		fileA.setName(fileName+fileA.getExtension());

		logger.strongStep("Create a Files template and Logout");
		log.info("INFO: Create a Files template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		logger.strongStep("Creating new community using Files template");
		log.info("INFO: Creating new community using Files template");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);	
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Navigate to Files");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Validate that file A is visible");
		log.info("INFO: Validate that file A is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(fileA)),
				"ERROR: Unable to find the file " + fileA.getName());

		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**	
	*<li><B>Info:</B>Test new community contains bookmark content from base community created using bookmark template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Create a bookmark for the community via API.</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using bookmarkTemplate</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Added Bookmarks from Base community exist in new community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the Wiki template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithBookmarkTE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName="BookmarkTemplate" + Helper.genDateBasedRand();

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		BaseDogear bookmark = new BaseDogear.Builder(Data.getData().BookmarkName , Data.getData().BookmarkURL)
				.community(community)
				.tags(Data.getData().BookmarkTag)
				.description(Data.getData().BookmarkDesc)
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a bookmark for ther community.");
		log.info("INFO: Create a bookmark for ther community.");
		bookmark.createAPI(apiOwner);

		logger.strongStep("Create a bookmark template and Logout");
		log.info("INFO: Create a bookmark template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		logger.strongStep("Creating new community using bookmark template");
		log.info("INFO: Creating new community using bookmark template");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);		
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Navigate to Bookmark app");
		Community_TabbedNav_Menu.BOOKMARK.select(ui);
		
		logger.strongStep("Verify that the bookmark appears in the bookmark list view");
		log.info("INFO: Verify that the bookmark appears in the bookmark list view");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + bookmark.getTitle()),
				"ERROR: Bookmark: " + bookmark.getTitle() + " is not in the bookmark view");
		
		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>The number of downloads of a file are not copied from the Base Community to the Inheriting Community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Select the Files tab from the Navigation Menu</li>
	*<li><B>Step:</B>Select the Details view then download the file uploaded earlier multiple times</li>
	*<li><B>Step:</B>Click on the link for the file and navigate to its About tab</li>
	*<li><B>Verify:</B>The number of downloads for the file in the About tab is correct</li>
	*<li><B>Step:</B>Fetch the current URL to be used during the creation of template for the current community</li>
	*<li><B>Step:</B>Logout, navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and logout</li>
	*<li><B>Step:</B>Load the Communities component and Log In with a user different from the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on the Create button to create the community</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Verify:</B>The details in the Inheriting Community are the same as those entered during the creation of the community</li>
	*<li><B>Verify:</B>The file added to the Base Community is visible in the Files widget on Highlights page</li>
	*<li><B>Step:</B>Click on the link for the file and navigate to its About tab</li>
	*<li><B>Verify:</B>The number of downloads for the file in the About tab is equal to zero</li>
	*<li><B>Step:</B>Logout and remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithMultipleDownloads() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String templateName= "MultipleDownloadsTemplate" + Helper.genDateBasedRand();

		User loginUser = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, loginUser.getAttribute(cfg.getLoginPreference()),loginUser.getPassword());
		APIFileHandler filesApiOwner = new APIFileHandler(serverURL, loginUser.getAttribute(cfg.getLoginPreference()),loginUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder("BaseCommunityForMultipleDownloads" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder("InheritingCommunityForMultipleDownloads" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		String fileName = testName + "File" + Helper.genDateBasedRand();
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg")
				 .rename(fileName)
				 .build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Upload the file '" + file.getName() + "'");
		log.info("INFO: Upload the file '" + file.getName() + "' via API");
		File fileToUpload = new File(FilesUI.getFileUploadPath(file.getName(), cfg));
		file.createAPI(filesApiOwner, fileToUpload, comAPI);
		file.setName(fileName+file.getExtension());

		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + loginUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + loginUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(loginUser);
		
		// Navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		
		//Navigate to the Files
		logger.strongStep("Navigate to the Files page");
		log.info("INFO: Navigate to the Files page");
		Community_TabbedNav_Menu.FILES.select(ui);

		// Switch the display from default Tile to Details
		logger.strongStep("Select the 'Details' display button then download the file '" + file.getName() + "' multiple times");
		log.info("INFO: Select the 'Details' display button then download the file '" + file.getName() + "' multiple times");
		Files_Display_Menu.DETAILS.select(ui);

		int i;
		for(i = 0; i < 3; i++) {
			logger.strongStep("Click on the 'More' button then click on the 'Download' link to download the file: " + file.getName());
			log.info("INFO: Click on the 'More' button then click on the 'Download' link to download the file: " + file.getName());
			file.download(filesUI);
			
			logger.strongStep("Click on the 'Views' button to refresh the number of views");
			log.info("INFO: Click on the 'Views' button to refresh the number of views");
			ui.clickLinkWait(FilesUIConstants.sortByViewsButton);
		}

		//Get community URL
		logger.strongStep("Fetch the community URL");
		log.info("INFO: Fetch the community URL");
		String communityUrl = driver.getCurrentUrl();

		logger.strongStep("Logging out from community with user " + loginUser.getDisplayName());
		log.info("INFO: Logging out from community with user " + loginUser.getDisplayName());
		ui.logout();

		logger.strongStep("Create a template for the community '" + community.getName() + "' with user " + adminUser.getDisplayName() + " and Logout from the admin page");
		log.info("INFO: Create a template for the community '" + community.getName() + "' with user " + adminUser.getDisplayName() + " and Logout from the admin page");
		ui.createTemplate(communityUrl, templateName, adminUser);

		//Load component and login as a different user
		loginUser = cfg.getUserAllocator().getUser();
		logger.strongStep("Load Communities and Log In as a different user: " + loginUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as a different user: " + loginUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(loginUser);

		logger.strongStep("Creating new community using the template '" + templateName + "' created earlier");
		log.info("INFO: Creating new community using the template '" + templateName + "' created earlier");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that the file '" + file.getName() + "' is visible in Files widget on Highlights Page");
		log.info("INFO: Validate that the file '" + file.getName() + "' is visible in Files widget on Highlights Page");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
				"ERROR: Unable to locate the file: " + file.getName());

		logger.strongStep("Click on the link for the file: " + file.getName());
		log.info("INFO: Click on the link for the file: " + file.getName());
		ui.clickLinkWait(FilesUI.selectFile(file));
		
		logger.strongStep("Navigate to the About tab for '" + file.getName() + "' to verify the number of downloads of the file");
		log.info("INFO: Navigate to the About tab for '" + file.getName() + "' to verify the number of downloads of the file");
		FileViewer_Panel_Menu.ABOUT.select(fileviewerUI);
		
		logger.strongStep("Verify that the number of views for the file '" + file.getName() + "' in the About tab is 0");
		log.info("INFO: Verify that the number of views for the file '" + file.getName() + "' in the About tab is 0");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.downloadCount),
				"ERROR: The number of downloads for the file '" + file.getName() + "' is not equal to 0");

		logger.strongStep("Logging out with " + loginUser.getDisplayName());
		log.info("INFO: Logging out with " + loginUser.getDisplayName());
		ui.logout();

		logger.strongStep("Deleting the communities for the test case: " + testName);
		log.info("INFO: Removing the communities for the test case: " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>The post and like created in the Status Updates tab of the Base Community are not copied to the Inheriting Community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Select the Status Updates tab from the Navigation Menu</li>
	*<li><B>Step:</B>Type a post in the Status Update Text Area and post it</li>
	*<li><B>Verify:</B>The post is visible in the News Item Container</li>
	*<li><B>Step:</B>Like the post in the News Item Container</li>
	*<li><B>Verify:</B>After liking the post, the like count changes to 1</li>
	*<li><B>Step:</B>Fetch the current URL to be used during the creation of template for the current community</li>
	*<li><B>Step:</B>Logout, navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and logout</li>
	*<li><B>Step:</B>Load the Communities component and Log In with a user different from the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Add a community web address</li>
	*<li><B>Step:</B>Click on the Create button to create the community</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Verify:</B>The details in the Inheriting Community are the same as those entered during the creation of the community</li>
	*<li><B>Step:</B>Select the Status Updates tab from the Navigation Menu</li>
	*<li><B>Verify:</B>The message 'There are no updates to display.' is visible on the Status Updates page</li>
	*<li><B>Step:</B>Logout and remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithPostAndLike() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String templateName= "PostAndLikeTemplate" + Helper.genDateBasedRand();
		String statusUpdate = Data.getData().statusUpdatesText;

		User loginUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, loginUser.getAttribute(cfg.getLoginPreference()),loginUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder("BaseCommunityForPostAndLike" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder("InheritingCommunityForPostAndLike" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + loginUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + loginUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(loginUser);
		
		// Navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the 'Status Updates' tab in the community");
		log.info("INFO: Navigate to the 'Status Updates' tab in the community");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui, 2);

		logger.strongStep("Verify the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Status Update text area appears within " + cfg.getFluentwaittime() + " seconds");
		Assert.assertTrue(ui.fluentWaitElementVisible(BaseUIConstants.StatusUpdate_iFrame));
		
		logger.strongStep("Switch to the frame: " + BaseUIConstants.StatusUpdate_iFrame + " to start using the Status Update text area");
		log.info("INFO: Switch to the frame containing the Status Update text area");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();

		logger.strongStep("Enter the status update message: " + statusUpdate);
		log.info("INFO: Type the status update message: " + statusUpdate);
		driver.typeNative(statusUpdate);
		driver.switchToFrame().returnToTopFrame();

		//Post status
		logger.strongStep("Click on the Post link");
		log.info("INFO: Click on the Post link");
		ui.clickLink(HomepageUIConstants.PostStatusUpdate);

		logger.strongStep("Verify the post '" + statusUpdate + "' is visible in the News Item Container");
		log.info("INFO: Verify the post '" + statusUpdate + "' is visible in the News Item Container");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.statusUpdate).getText().contains(statusUpdate),
				"ERROR: The posted status is not visible in the News Item Container");

		logger.strongStep("Like the post '" + statusUpdate + "' in the News Item Container");
		log.info("INFO: Like the post '" + statusUpdate + "' in the News Item Container");
		ui.clickLinkWait(WikisUIConstants.likeLink);

		logger.strongStep("Verify that after liking the post '" + statusUpdate + "' the like count changes to 1");
		log.info("INFO: Verify that after liking the post '" + statusUpdate + "' the like count changes to 1");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EELikeCount).getText(), "1",
				"ERROR: The number of likes is not equal to 1");

		//Get community URL after clicking on highlight tab
		logger.strongStep("Fetch the community URL");
		log.info("INFO: Fetch the community URL");
		String communityUrl = driver.getCurrentUrl();

		logger.strongStep("Logging out from community with user " + loginUser.getDisplayName());
		log.info("INFO: Logging out from community with user " + loginUser.getDisplayName());
		ui.logout();

		logger.strongStep("Create a template for the community and logout");
		log.info("INFO: Create a template for the community and logout");
		ui.createTemplate(communityUrl, templateName, adminUser);

		//Load component and login as a different user
		loginUser = cfg.getUserAllocator().getUser();
		logger.strongStep("Load Communities and Log In as a different user: " + loginUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as a different user: " + loginUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(loginUser);

		logger.strongStep("Creating new community using the template '" + templateName + "' created earlier");
		log.info("INFO: Creating new community using the template '" + templateName + "' created earlier");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForCommunityLoaded();

		logger.strongStep("Navigate to the 'Status Updates' tab in the community");
		log.info("INFO: Navigate to the 'Status Updates' tab in the community");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui, 2);

		logger.strongStep("Verify that the post and the like have not been copied from the Base Community to the Inheriting Community");
		log.info("INFO: Verify that the post and the like have not been copied from the Base Community to the Inheriting Community");
		Assert.assertTrue(ui.fluentWaitTextPresent("There are no updates to display."));
		
		logger.strongStep("Logging out with " + loginUser.getDisplayName());
		log.info("INFO: Logging out with " + loginUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting the communities for the test case: " + testName);
		log.info("INFO: Removing the communities for the test case: " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>The updated versions of a file are not copied from the Base Community to the Inheriting Community</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
        *<li><B>Step:</B>Upload a file to the community via API</li>
        *<li><B>Step:</B>Create multiple versions of the file via API</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and logout</li>
	*<li><B>Step:</B>Load the Communities component and Log In with a user different from the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using the 'Start A Community' Drop Down</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Steps:</B>Add the members to the community using the 'Add Members' tab of 'Create Community' widget</li>
	*<li><B>Step:</B>Click on the Next button and add a tag to the community and press enter</li>
	*<li><B>Verify:</B>Tag is added successfully to the community</li>
	*<li><B>Step:</B>Click on the Create button to create the community</li>
	*<li><B>Verify:</B>An alert is displayed with 2 options - 'CONTINUE EDITING' and 'CREATE'</li>
	*<li><B>Step:</B>Click on the 'CREATE' button</li>
	*<li><B>Verify:</B>The details in the Inheriting Community are the same as those entered during the creation of the community</li>
	*<li><B>Step:</B>Select the Files tab from the Navigation Menu and then select the Details view</li>
	*<li><B>Verify:</B>The file added to the Base Community is visible in the Files tab</li>
	*<li><B>Verify:</B>The file has the author as the admin user of the community</li>
	*<li><B>Step:</B>Click on the link for the file and navigate to its Versions tab</li>
	*<li><B>Verify:</B>Only version 1 of the file is visible in the Versions tab, not the other versions</li>
	*<li><B>Verify:</B>The author of version 1 of the file is the admin user</li>
	*<li><B>Step:</B>Logout and remove the Base Community as well as the Inheriting Community</li>
	*<li><B>Step:</B>Remove the template created for the base community</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithMultipleFileVersions() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		testUser = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
		APIFileHandler filesApiOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		String testName = ui.startTest();
		String templateName= "FileVersionsTemplate" + Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("BaseCommunityForFileVersions" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder("InheritingCommunityForFileVersions" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();

		String fileName = testName + "File" + Helper.genDateBasedRand();
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg")
				 .rename(fileName)
				 .build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Upload the file '" + file.getName() + "'");
		log.info("INFO: Upload the file '" + file.getName() + "' via API");
		File fileToUpload = new File(FilesUI.getFileUploadPath(file.getName(), cfg));
		FileEntry communityFile = file.createAPI(filesApiOwner, fileToUpload, comAPI);
		file.setName(fileName+file.getExtension());

		logger.strongStep("Create multiple versions of the file: " + file.getName());
		log.info("INFO: Create multiple versions of the file:  multiple versions of the file: " + file.getName() + " via API");
		for (int i = 1; i < 4; i++) {
			file.updateCommunityFileAPI(filesApiOwner, communityFile, comAPI, fileName+file.getExtension());
		}

		logger.strongStep("Create a File Versions template and Logout");
		log.info("INFO: Create a File Versions template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		//Load component and login as a different user
		testUser = cfg.getUserAllocator().getUser();
		logger.strongStep("Load Communities and Log In as a different user: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as a different user: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		logger.strongStep("Creating new community using the File Versions template created earlier");
		log.info("INFO: Creating new community using the File Versions template created earlier");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Navigate to the Files page");
		log.info("INFO: Navigate to the Files page");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select the 'Details' display button");
		log.info("INFO: Select the 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Validate that the file '" + file.getName() + "' is visible");
		log.info("INFO: Validate that the file '" + file.getName() + "' is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
				"ERROR: Unable to locate the file: " + file.getName());

		logger.strongStep("Click on the link for the file: " + file.getName());
		log.info("INFO: Click on the link for the file: " + file.getName());
		ui.clickLinkWait(FilesUI.selectFile(file));
		
		logger.strongStep("Navigate to the Versions tab for '" + file.getName() + "' to verify the versions of the file");
		log.info("INFO: Navigate to the Versions tab for '" + file.getName() + "' to verify the versions of the file");
		ui.clickLinkWithJavascript(FileViewerUI.versionTab);

		logger.strongStep("Verify that only version 1 of file '" + file.getName() + "' is visible in Versions tab in the Inheriting Community");
		log.info("INFO: Verify that only version 1 of file '" + file.getName() + "' is visible in Versions tab in the Inheriting Community");
		Assert.assertTrue(driver.getVisibleElements(CommunitiesUIConstants.versionNumber).size() == 1,
				"ERROR: Multiple versions visible for the file: " + file.getName());
		Assert.assertTrue(driver.getVisibleElements(CommunitiesUIConstants.versionNumber).get(0).getText().equals("1"),
				"ERROR: Version 1 not visible for the file: " + file.getName());
		
		logger.strongStep("Verify that the author of version 1 of file '" + file.getName() + "' is " + adminUser.getDisplayName());
		log.info("INFO: Verify that the author of version 1 of file '" + file.getName() + "' is " + adminUser.getDisplayName());
		Assert.assertTrue(driver.isElementPresent(ui.getFileAuthorLink(adminUser)),
				"ERROR: The author of version 1 of the file '" + file.getName() + "' is not " + adminUser.getDisplayName());

		logger.strongStep("Logging out with " + testUser.getDisplayName());
		log.info("INFO: Logging out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting the communities for the test case: " + testName);
		log.info("INFO: Removing the communities for the test case: " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}

	/**	
	*<li><B>Info:</B>Test new community created using template contains all content from base community added by different member of the community.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Upload a file to the community via API as the owner of the community</li>
	*<li><B>Step:</B>Create a blog entry to the community via API as another member of the community</li>
	*<li><B>Step:</B>Create a wiki to the community via API as another member of the community</li>
	*<li><B>Step:</B>Upload a file to the community via API as another member of the community</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Click on add content from Rich content widget and add description</li>
	*<li><B>Verify:</B>Verify that description is added successfully</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using richContentTemplate</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Added content in RichContent widget from Base community exist in new community</li>
	*<li><B>Verify:</B>Added blog entry from Base community exist in new community</li>
	*<li><B>Verify:</B>Added wiki page from Base community exist in new community</li>
	*<li><B>Verify:</B>Added images from Base community exist in new community</li>
	*<li><B>Verify:</B>The blog entry, wiki page and files all have the author as the admin user of the community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the created template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void validateCommunityContributedByMoreMembers() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName="CommunityAllContentTemplate" + Helper.genDateBasedRand();

		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		User testUserC = cfg.getUserAllocator().getUser();
		User testUserD = cfg.getUserAllocator().getUser();
		Member memberB = new Member(CommunityRole.MEMBERS, testUserB);
		Member memberC = new Member(CommunityRole.MEMBERS, testUserC);
		Member memberD = new Member(CommunityRole.MEMBERS, testUserD);

		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()),testUserA.getPassword());
		APIFileHandler apiFilesOwner = new APIFileHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()),testUserA.getPassword());
		APICommunitiesHandler testUserBMemeber = new APICommunitiesHandler(serverURL, testUserB.getAttribute(cfg.getLoginPreference()),testUserB.getPassword());
		APIWikisHandler testUserCMemeber = new APIWikisHandler(serverURL, testUserC.getAttribute(cfg.getLoginPreference()),testUserC.getPassword());
		APIFileHandler testUserDMemeber = new APIFileHandler(serverURL, testUserD.getAttribute(cfg.getLoginPreference()),testUserD.getPassword());

		String testName = ui.startTest();
		String rteContent = "Description: This is description for Rich Content Widget";
		
		List<Member> communityMemberList = new ArrayList<Member>();
		communityMemberList.add(memberB);
		communityMemberList.add(memberC);
		communityMemberList.add(memberD);
	
		BaseCommunity community = new BaseCommunity.Builder("BaseCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.addMembers(communityMemberList)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder("NewCommunity" + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.addMembers(new ArrayList<Member>())
				.template(templateName)
				.build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();
		
		BaseWikiPage wikiPage = new BaseWikiPage.Builder("Wiki_Page_" + Helper.genMonthDateBasedRandVal(), PageType.NavPage)
				.tags("tag1")
				.description("this is a test description for creating a nav wiki page")
				.build();

		String fileNameA = testName + "FileA" + Helper.genDateBasedRand();
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg")
				 .rename(fileNameA)
				 .build();

		String fileNameB = testName + "FileB" + Helper.genDateBasedRand();
		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
				 .comFile(true)
				 .extension(".jpg")
				 .rename(fileNameB)
				 .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Add a new file A as " + testUserA.getDisplayName());
		log.info("INFO: API - Add a new file A as " + testUserA.getDisplayName());
		File file = new File(FilesUI.getFileUploadPath(fileA.getName(), cfg));
		fileA.createAPI(apiFilesOwner, file, comAPI);
		fileA.setName(fileNameA+fileA.getExtension());

		logger.strongStep("Add a Blog entry as " + testUserB.getDisplayName());
		log.info("INFO: API - Add a Blog entry as " + testUserB.getDisplayName());
		testUserBMemeber.createBlogEntry(blogEntry, comAPI);

		logger.strongStep("Add wiki page as " + testUserC.getDisplayName());
		log.info("INFO: API - Add wiki page as " + testUserC.getDisplayName());
		Wiki communityWiki = testUserCMemeber.getCommunityWiki(comAPI);
		testUserCMemeber.createWikiPage(wikiPage, communityWiki);

		logger.strongStep("Add a new file B as " + testUserD.getDisplayName());
		log.info("INFO: API - Add a new file B as : " + testUserD.getDisplayName());
		File fileBPath = new File(FilesUI.getFileUploadPath(fileB.getName(), cfg));
		fileB.createAPI(testUserDMemeber, fileBPath, comAPI);
		fileB.setName(fileNameB+fileB.getExtension());

		//Load component and login
		logger.strongStep("Load Communities and Log In as owner: " + testUserA.getDisplayName());
		log.info("INFO: Load Communities and Log In as owner: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);
		
		// Navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on Rich Content widget 'Add Content' button");
		log.info("INFO: Click on Rich Content widget 'Add Content' button");
		ui.fluentWaitElementVisibleOnce(CommunitiesUIConstants.rteAddContent);
		ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);
		
		logger.strongStep("Add rich content description");
		log.info("INFO: Add rich content description");
		driver.executeScript("scroll(0, 80);");
		ui.typeInCkEditor(rteContent);
		
		logger.strongStep("Click on Save button");
		log.info("INFO: Click on Save button");
		ui.scrollIntoViewElement(CommunitiesUIConstants.rteSave);
		ui.clickLinkWait(CommunitiesUIConstants.rteSave);
	
		logger.strongStep("Verify rich content is saved");
		log.info("INFO: Verify rich content is saved");
		Assert.assertTrue(ui.fluentWaitTextPresent(rteContent), "ERROR: Rich content is not saved");

		logger.strongStep("Logout as owner");
		log.info("INFO: Logout as owner");
		ui.logout();

		logger.strongStep("Create a template from community and Logout");
		log.info("INFO: Create a template from community and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
	
		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUserA.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUserA.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserA);
	
		logger.strongStep("Creating new community using CommunityAllContentTemplate template");
		log.info("INFO: Creating new community using CommunityAllContentTemplate template");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify the Rich Content widget description displays");
		log.info("INFO: Verify the Rich Content widget description displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(rteContent),
				"ERROR: Rich Content widget text does not display");
		
		log.info("INFO: Navigate to the Blog");
		Community_TabbedNav_Menu.BLOG.select(ui);
		
		logger.strongStep("Verify that blog entry from base community exists in new community");
		log.info("INFO: Verify that blog entry from base community exists in new community");
		Assert.assertTrue(ui.fluentWaitTextPresentRefresh(blogEntry.getTitle()), "ERROR: Entry not found");
		
		logger.strongStep("Validate that the Wiki Page from base community exists in new community");
		log.info("INFO: Verify the Wiki Page from base community exists in new community");
		ui.clickLinkWait(WikisUIConstants.wikiLinkOnnewUI);
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
						  "ERROR:" + wikiPage.getName() + "Page does not exist");

		logger.strongStep("Navigate to Files and Select 'Details' display button");
		log.info("INFO: Navigate to Files and Select Details display button");
		Community_TabbedNav_Menu.FILES.select(ui);
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Validate that file A is visible");
		log.info("INFO: Validate that file A is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(fileA)),
				"ERROR: Unable to find the file " + fileA.getName());
		
		logger.strongStep("Validate that file B is visible");
		log.info("INFO: Validate that file B is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(fileB)),
				"ERROR: Unable to find the file " + fileB.getName());
		
		logger.strongStep("Loggin out with " + testUserA.getDisplayName());
		log.info("INFO: Loggin out with " + testUserA.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);
	
		ui.endTest();
	}

	/**	
	*<li><B>Info:</B>Test new community contains RTE content from base community created using richContent template.</li>
	*<li><B>Step:</B>Create a community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the community using UUID</li>
	*<li><B>Step:</B>Click on add content from Rich content widget and add description and attach image</li>
	*<li><B>Verify:</B>Verify that image is added successfully then logout</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template for the community created earlier using its URL</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new community using richContentTemplate</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the community is created successfully</li>
	*<li><B>Verify:</B>Added content in RichContent widget from Base community exist in new community</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the RichContent template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createCommunityWithRichContentTE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String templateName="RichContentTemplate" + Helper.genDateBasedRand();;
		String rteContent = "This is image description";
		
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		BaseCommunity newCommunity = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.template(templateName)
				.build();
		
		BaseFile imageFile = new BaseFile.Builder(Data.getData().file1)
				.extension(".jpg")
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// Navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		
		Boolean flag=driver.isElementPresent(CommunitiesUIConstants.richContentWidgetTitleTE);
		if(!flag){
			log.info("INFO: Add new Rich Content Widget");
			ui.addWidget(BaseWidget.RICHCONTENT);
			
			log.info("INFO: Verify the Rich Content widget was added");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.richContentWidgetTitle),
					"ERROR: App name Rich Content does not appear");
		}
		
		logger.strongStep("Click on Rich Content widget 'Add Content' button");
		log.info("INFO: Click on Rich Content widget 'Add Content' button");
		ui.scrollIntoViewElement(CommunitiesUIConstants.rteAddContent);
		ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);
		
		logger.strongStep("Add rich content description");
		log.info("INFO: Add rich content description");
		ui.typeInCkEditor(rteContent);
		
		// Click on 'Insert/Edit Image'
		logger.strongStep("Click on 'Insert/Edit Image' icon");
		log.info("INFO: Click on 'Insert/Edit Image' icon");
		ui.clickLinkWait(CommunitiesUIConstants.insertImageLink);

		// Upload image
		logger.strongStep("Upload image");
		log.info("INFO: Upload image");
		try {
			filesUI.fileToUpload(imageFile.getName(), BaseUIConstants.FileInputField2);
		} catch (Exception e) {
			Assert.assertTrue(false, "Error uploading file.");
		}
		ui.clickButton("Upload Image");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);

		// Verify the image should be uploaded successfully
		logger.strongStep("Verify the image should be uploaded successfully");
		log.info("INFO: Verify the image should be uploaded successfully");
		ui.fluentWaitPresent(CommunitiesUI.imgInRichContent(imageFile));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.imgInRichContent(imageFile)));
		
		logger.strongStep("Click on Save button");
		log.info("INFO: Click on Save button");
		ui.switchToTopFrame();
		ui.scrollIntoViewElement(CommunitiesUIConstants.rteSave);
		ui.clickLinkWait(CommunitiesUIConstants.rteSave);

		logger.strongStep("Verify rich content is saved");
		log.info("INFO: Verify rich content is saved");
		Assert.assertTrue(ui.fluentWaitTextPresent(rteContent), "ERROR: Rich content is not saved");

		logger.strongStep("Logging out from community with user" + testUser.getDisplayName());
		log.info("INFO: Logging out from community with user" + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Create a richContent template and Logout");
		log.info("INFO: Creating a richContent template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);

		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);

		logger.strongStep("Creating new community using richContent template");
		log.info("INFO: Creating new community using richContent template");
		newCommunity.createCommunityFromTailoredExperienceWidget(ui, logger);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify the Rich Content widget description displays");
		log.info("INFO: Verify the Rich Content widget description displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(rteContent),
				"ERROR: Rich Content widget text does not display");
		
		logger.strongStep("Verify that uploaded image should be displayed");
		log.info("INFO: Verify that uploaded image should be displayed");
		ui.fluentWaitPresent(CommunitiesUI.imgInRichContent(imageFile));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.imgInRichContent(imageFile)));
		
		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();
		
		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, newCommunity);
		
		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info:</B>Test new Moderated community created using public community template.</li>
	*<li><B>Step:</B>Create a Public community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the community's owner</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template from the Public community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new Moderated community using previously created template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the Moderated community is created successfully</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the Public community template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createModeratedCommunityWithPublicCommunityTemplate() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String templateName = "publicCommunityTemplate" + Helper.genDateBasedRand();
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.MODERATED)
				.description("Community: this is a Test description for community2" + testName)
				.template(templateName)
				.build();

		// create community
		logger.strongStep("Create Public Community using API");
		log.info("INFO: Create Public Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a public community template and Logout");
		log.info("INFO: Create a public community template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		
		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Creating new Moderated community using public community template");
		log.info("INFO: Creating new Moderated community using public community template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);
	
		logger.weakStep("Validate that the " + community.getName() + " name is moderated community");
		log.info("INFO: Validate that the " + community.getName() + " name is is moderated coommunity");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moderatedCommunityImage),"ERROR: Unable to find moderated community");

		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		log.info("INFO: Add template to be deleted at the end of tests. " + templateName);
		templatesToDelete.add(templateName);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test new Restricted community created using public community template.</li>
	*<li><B>Step:</B>Create a Public community using API with an owner different from the admin user</li>
	*<li><B>Step:</B>Navigate to the 'cnxadmin' page and login as the admin user</li>
	*<li><B>Step:</B>Create a template from the Public community created earlier using its URL and close the browser</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the previous community's owner</li>
	*<li><B>Step:</B>Create a new Restricted community using previously created template</li>
	*<li><B>Step:</B>Enter the details in the 'Community Details' tab and click on the Next button</li>
	*<li><B>Step:</B>Select the template created previously in the 'Select a Template' tab and click on the Next button</li>
	*<li><B>Step:</B>Navigate to the Highlights page if it is not the default landing page</li>
	*<li><B>Verify:</B>Verify that the Restricted community is created successfully</li>
	*<li><B>Step:</B>Logout and delete All created communities</li>
	*<li><B>Step:</B>Delete the Public community template</li>
	*</ul>
	*/
	@Test(groups = {"TailoredExperience", "cplevel2"})
	public void createRestrictedCommunityWithPublicCommunityTemplate() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String templateName = "publicCommunityTemplate" + Helper.genDateBasedRand();
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName +"2"+ Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
				.template(templateName)
				.build();

		// create community
		logger.strongStep("Create Public Community using API");
		log.info("INFO: Create Public Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create a public community template and Logout");
		log.info("INFO: Create a public community template and Logout");
		ui.createTemplate(comAPI.getAlternateLink(), templateName, adminUser);
		
		//Load component and login as a different user
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		logger.strongStep("Creating new Restricted community using public community template");
		log.info("INFO: Creating new Restricted community using public community template");
		community2.createCommunityFromTailoredExperienceWidget(ui, logger);

		logger.weakStep("Validate that the " + community.getName() + " name is restricted community");
		log.info("INFO: Validate that the " + community.getName() + " name is is restricted coommunity");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.restrictedCommunityImage),"ERROR: Unable to find restricted community");

		logger.strongStep("Loggin out with " + testUser.getDisplayName());
		log.info("INFO: Loggin out with " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Deleting communities for Test case " + testName);
		log.info("INFO: Removing communities for Test case " + testName);
		deleteCommunity(logger, community);
		deleteCommunity(logger, community2);

		logger.strongStep("Deleting template with name " + templateName);
		log.info("INFO: Deleting template with name " + templateName);
		List<String> templateToDelete = new ArrayList<String>();
		templateToDelete.add(templateName);
		ui.deleteTemplates(templateToDelete, adminUser);

		ui.endTest();
	}
	
	/**
	 * @param logger
	 * @param communityName
	 * @param communityDescription
	 * @param communityMemberList
	 * @param tags
	 * @param webAddress
	 * @param communityType
	 */
	public void verifyCommunity(DefectLogger logger, String communityName, String communityDescription, List<Member> communityMemberList,
			String tags, String webAddress,String communityType, String testUser){
		
	    //name and description verification	
		logger.strongStep("Navigate to " + communityType + " Overview Page and Get text from Rich content");
		ui.fluentWaitPresent(CommunitiesUIConstants.communityHighlightTab);
		driver.executeScript("scroll(0,-250);");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.communityHighlightTab);

		logger.weakStep("Validate that the " + communityType + " name and description is present");
		log.info("INFO: Validate that the " + communityType + " name and description is present");
		driver.changeImplicitWaits(2);
		Assert.assertTrue(ui.fluentWaitTextPresent(communityName) &&
						  ui.fluentWaitTextPresent(communityDescription),
						 "ERROR: Unable to find the " + communityType + " name and description");
		driver.turnOnImplicitWaits();
		
		//members verification
		log.info("INFO: Click on the Members widget View All link");
		driver.getFirstElement(CommunitiesUIConstants.membersWidgetViewAllLink).click();
		driver.changeImplicitWaits(2);
		
		log.info("INFO: Validate the owner is listed on the " + communityType + " Members page");
		ui.fluentWaitPresentWithRefresh("xpath=//a[contains(text(),\'"+testUser+"\')]");
		Assert.assertTrue(driver.isElementPresent("xpath=//a[contains(text(),\'"+testUser+"\')]"),
				"ERROR : Owner is not present on the " + communityType + " Members page");

		driver.turnOnImplicitWaits();
		log.info("INFO: Validate the community members are listed on the " + communityType + " Members page");
		boolean membersFlag=true;
		driver.changeImplicitWaits(2);
		for(Member item : communityMemberList){
			if(!ui.isElementPresent("link=" + convertDisplayNameToCamelCase(item.getUser().getDisplayName())))
				membersFlag=false;
		}
		driver.turnOnImplicitWaits();
		Assert.assertTrue(membersFlag, "ERROR : Members of community are NOT present on the " + communityType + " Members page");
				
		//select Community Actions menu option Edit Community
		log.info("INFO: Select Edit Community on the community action menu");
		if(communityType.equalsIgnoreCase("community"))
			Com_Action_Menu.EDIT.select(ui);
		else
			Com_Action_Menu.EDITSUB.select(ui);
		
		ui.waitForPageLoaded(driver);
		logger.weakStep("Validate that the " + communityType + " name is present");
		log.info("INFO: Validate that the " + communityType + " name is present");
		ui.fluentWaitPresent(CommunitiesUIConstants.EditCommunityName);
		log.info(driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).getAttribute("value"));
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).getAttribute("value"),
				communityName, "ERROR: Community name is not matching");
	
		//web address verification
		if(communityType.equalsIgnoreCase("community")){
			logger.weakStep("Validate " + communityType + " web address");
			log.info("INFO: Validate " + communityType + " web address");

			//click on the link Access Advanced Features
			log.info("INFO: Click on the link Access Advanced Features");
			ui.getFirstVisibleElement(CommunitiesUIConstants.comAdvancedLink).click();
			ui.isElementPresent(CommunitiesUIConstants.CommunityHandle);
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.CommunityHandle).getAttribute("value"),
					webAddress, "ERROR: Web address for " + communityType + " is not matching");

			if(communityType.equals(BaseCommunity.Access.PUBLIC.commType))
				Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.PublicAccess).isSelected());
			else if(communityType.equals(BaseCommunity.Access.MODERATED.commType))
				Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.ModeratedAccess).isSelected());
			else if(communityType.equals(BaseCommunity.Access.RESTRICTED.commType))
				Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RestrictedAccess).isSelected());
			ui.getFirstVisibleElement(CommunitiesUIConstants.EditCommunityCancelButton).click();
		}
	}
	
	/**
	 * <li><B>Info:</B>This will delete community.</li>
	 * @param logger
	 * @param community
	 */
	public void deleteCommunity(DefectLogger logger, BaseCommunity community){
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
	}
	
	
	/**
	 * <li><B>Info:</B>This function is added to convert user display name to camel case.</li>
	 * @param userDisplayName
	 * @return String in camel case
	 */
	public String convertDisplayNameToCamelCase(String userDisplayName){
		userDisplayName=userDisplayName.replace(userDisplayName.charAt(0), Character.toUpperCase(userDisplayName.charAt(0)));
		return userDisplayName=userDisplayName.replace(userDisplayName.charAt(userDisplayName.indexOf(" ")+1), Character.toUpperCase(userDisplayName.charAt(userDisplayName.indexOf(" ")+1))).trim();
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUp() {
		if (!templatesToDelete.isEmpty())  {
			List<String> notDeleted = ui.deleteTemplates(templatesToDelete, adminUser);
			driver.close();	
			Assert.assertTrue(notDeleted.isEmpty(), "Error cleaning up templates: " + Arrays.toString(notDeleted.toArray()));
		}

	}
}
