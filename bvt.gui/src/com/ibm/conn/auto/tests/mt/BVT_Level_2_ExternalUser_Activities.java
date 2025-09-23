package com.ibm.conn.auto.tests.mt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_ExternalUser_Activities extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_ExternalUser_Activities.class);
	private CommunitiesUI ui;
	private ActivitiesUI actUI;
	private TestConfigCustom cfg;
	private User testUser_orgA, extTestUser1;
	private String serverURL_MT_orgA;
	private APICommunitiesHandler apiHandler;
	private APIActivitiesHandler apiActivitiesHandler;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		extTestUser1 = cfg.getUserAllocator().getGroupUser("external_users_orga");
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();	

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);

	}

	/**
	*<ul>
	*<li><B>Info: </B>Verify that for an external user the links for both Activities and Apps are not visible in the mega-menu on Homepage</li>
	*<li><B>Step: </B>Load Homepage and login as an external user</li>
	*<li><B>Verify: </B>The links for Activities and Apps are not visible in the mega-menu</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2"})
	public void activitiesLinkNotVisibleToExternalUser() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		logger.strongStep("Load Homepage and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load Homepage and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(extTestUser1);

		driver.changeImplicitWaits(5);
		
		logger.strongStep("Verify that the links for Activities and Apps are not visible in the mega-menu");
		log.info("INFO: Verify that the links for Activities and Apps are not visible in the mega-menu");
		Assert.assertFalse(driver.isElementPresent(HomepageUI.getAppLinkInMegaMenu("Activities")),
				"The link for Activities is not visible in the mega-menu");
		Assert.assertFalse(driver.isElementPresent(HomepageUI.getAppLinkInMegaMenu("Apps")),
				"The link for Apps is not visible in the mega-menu");

		driver.turnOnImplicitWaits();
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify that an external user is not allowed access to an internal private activity created by an internal user</li>
	*<li><B>Step: </B>Create an internal private activity as an internal user using API</li>
	*<li><B>Step: </B>Load the Activities component and login as the internal user who created the activity</li>
	*<li><B>Step: </B>Click on the link for the activity</li>
	*<li><B>Step: </B>Fetch the URL for the activity</li>
	*<li><B>Step: </B>Logout as the internal user</li>
	*<li><B>Step: </B>Load the Homepage component and login as an external user</li>
	*<li><B>Step: </B>Navigate to the activity previously created by the internal user using its URL</li>
	*<li><B>Verify: </B>The error 'Access Denied' appears for the external user trying to access an internal private activity</li>
	*<li><B>Verify: </B>The error 'You do not have permission to view this page.' appears for the external user trying to access an internal private activity</li>
	*<li><B>Step: </B>Delete the activity</li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void accessDeniedToExternalUserForInternalPrivateActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String permissionDenied = "You do not have permission to view this page.";

		apiActivitiesHandler = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseActivity privateActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													   .tags(testName)
													   .goal(Data.getData().commonDescription + testName)
													   .build();

		logger.strongStep("Create an internal private activity using API");
		log.info("INFO: Create an internal private activity using API");
		Activity activity = privateActivity.createAPI(apiActivitiesHandler);

		//Load the Activities component and login as the internal user who created the activity
		logger.strongStep("Load Activities and login as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Activities and login as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		logger.strongStep("Click on the link for the activity: " + privateActivity.getName());
		log.info("INFO: Click on the link for the activity: " + privateActivity.getName());
		ui.clickLinkWait(ActivitiesUI.getActivityLink(privateActivity));

		logger.strongStep("Fetch the URL for the activity");
		log.info("INFO: Fetch the URL for the activity");
		String activityURL = driver.getCurrentUrl();

		logger.strongStep("Logout as the internal user: " + testUser_orgA);
		log.info("INFO: Logout as the internal user: " + testUser_orgA);
		ui.logout();

		//Load the Homepage component and login as an external user
		logger.strongStep("Load Homepage and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load Homepage and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the activity created by " + testUser_orgA.getDisplayName() + " using its URL");
		log.info("INFO: Navigate to the activity created by " + testUser_orgA.getDisplayName() + " using its URL");
		driver.load(activityURL, true);

		logger.strongStep("Verify that the external user is not allowed access to the internal private activity");
		log.info("INFO: Verify that the external user is not allowed access to the internal private activity");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.accessDeniedMsg),
				"The error 'Access Denied' appears for the external user trying to access an internal private activity");
		Assert.assertTrue(ui.fluentWaitTextPresent(permissionDenied),
				"The error '" + permissionDenied + "' appears for the external user trying to access an internal private activity");

		//Delete the activity
		logger.strongStep("Delete the activity");
		log.info("INFO: Delete the activity");
		apiActivitiesHandler.deleteActivity(activity);

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify that an external user is not allowed access to an internal public activity created by an internal user</li>
	*<li><B>Step: </B>Create an internal public activity as an internal user using API</li>
	*<li><B>Step: </B>Load the Activities component and login as the internal user who created the activity</li>
	*<li><B>Step: </B>Click on the link for the activity</li>
	*<li><B>Step: </B>Navigate to Members section using the left panel</li>
	*<li><B>Verify: </B>The access of the activity is set to 'Private (Default)'</li>
	*<li><B>Step: </B>Click on the Change link to change the access level for the activity</li>
	*<li><B>Step: </B>Click on the radio button for Everyone in the dialog box</li>
	*<li><B>Step: </B>Click on the Save button</li>
	*<li><B>Verify: </B>The access of the activity has changed to Everyone</li>
	*<li><B>Step: </B>Navigate to Activity Outline section using the left panel</li>
	*<li><B>Step: </B>Fetch the URL for the activity</li>
	*<li><B>Step: </B>Logout as the internal user</li>
	*<li><B>Step: </B>Load the Homepage component and login as an external user</li>
	*<li><B>Step: </B>Navigate to the activity previously created by the internal user using its URL</li>
	*<li><B>Verify: </B>The error 'Access Denied' appears for the external user trying to access an internal public activity</li>
	*<li><B>Verify: </B>The error 'You do not have permission to view this page.' appears for the external user trying to access an internal public activity</li>
	*<li><B>Step: </B>Delete the activity</li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void accessDeniedToExternalUserForInternalPublicActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String permissionDenied = "You do not have permission to view this page.";

		apiActivitiesHandler = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseActivity publicActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													  .tags(testName)
													  .goal(Data.getData().commonDescription + testName)
													  .build();

		logger.strongStep("Create an internal public activity using API");
		log.info("INFO: Create an internal public activity using API");
		Activity activity = publicActivity.createAPI(apiActivitiesHandler);

		//Load the Activities component and login as the internal user who created the activity
		logger.strongStep("Load Activities and login as: " + testUser_orgA.getDisplayName());
		log.info("INFO: Load Activities and login as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		logger.strongStep("Click on the link for the activity: " + publicActivity.getName());
		log.info("INFO: Click on the link for the activity: " + publicActivity.getName());
		ui.clickLinkWait(ActivitiesUI.getActivityLink(publicActivity));

		logger.strongStep("Navigate to Members section using the left panel");
		log.info("INFO: Navigate to Members section using the left panel");
		ui.clickLinkWait(ActivitiesUIConstants.SelectMembers);

		logger.strongStep("Verify that the access of the activity is set to 'Private (Default)'");
		log.info("INFO: Verify that the access of the activity is set to 'Private (Default)'");
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.Activity_Private_Access),
				"The access of the activity is set to 'Private (Default)'");

		logger.strongStep("Click on the Change link to change the access level for the activity");
		log.info("INFO: Click on the Change link to change the access level for the activity");
		ui.clickLink(HomepageUIConstants.scChangeAccess);

		logger.strongStep("Click on the radio button for Everyone in the dialog box");
		log.info("INFO: Click on the radio button for Everyone in the dialog box");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.PublicAccess_RadioBtn);

		logger.strongStep("Click on the Save button");
		log.info("INFO: Click on the Save button");
		ui.clickLinkWait(ActivitiesUIConstants.SaveButton_PermissionChange_SC);

		logger.strongStep("Verify that the access of the activity has changed to Everyone");
		log.info("INFO: Verify that the access of the activity has changed to Everyone");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.Activity_Public_Access_SC),
				"The access of the activity has changed to Everyone");

		logger.strongStep("Navigate to Activity Outline section using the left panel");
		log.info("INFO: Navigate to Activity Outline section using the left panel");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Outline);

		logger.strongStep("Fetch the URL for the activity");
		log.info("INFO: Fetch the URL for the activity");
		String activityURL = driver.getCurrentUrl();

		logger.strongStep("Logout as the internal user: " + testUser_orgA);
		log.info("INFO: Logout as the internal user: " + testUser_orgA);
		ui.logout();

		//Load the Homepage component and login as an external user
		logger.strongStep("Load Homepage and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load Homepage and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(extTestUser1);

		logger.strongStep("Navigate to the activity created by " + testUser_orgA.getDisplayName() + " using its URL");
		log.info("INFO: Navigate to the activity created by " + testUser_orgA.getDisplayName() + " using its URL");
		driver.load(activityURL, true);

		logger.strongStep("Verify that the external user is not allowed access to the internal public activity");
		log.info("INFO: Verify that the external user is not allowed access to the internal public activity");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.accessDeniedMsg),
				"The error 'Access Denied' appears for the external user trying to access an internal public activity");
		Assert.assertTrue(ui.fluentWaitTextPresent(permissionDenied),
				"The error '" + permissionDenied + "' appears for the external user trying to access an internal public activity");

		//Delete the activity
		logger.strongStep("Delete the activity");
		log.info("INFO: Delete the activity");
		apiActivitiesHandler.deleteActivity(activity);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Verify that an external user is not allowed to access any internal activities except the community activities</li>
	*<li><B>Step: </B>Create an external restricted community in orgA with an external user as a member</li>
	*<li><B>Step: </B>Get UUID of the community</li>
	*<li><B>Step: </B>Create an activity inside the community</li>
	*<li><B>Step: </B>Create standalone private and public activities in orgA using API</li>
	*<li><B>Step: </B>Add the Activities widget to the community if not already added</li>
	*<li><B>Step: </B>Load the Activities component and login as the external user who is a member of the orgA community</li>
	*<li><B>Step: </B>Go to different sections of the Activities page</li>
	*<li><B>Verify: </B>Neither the standalone activities created earlier nor any other activity from orgA is visible anywhere</li>
	*<li><B>Step: </B>Click on the 'My Activities' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'High Priority' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'Medium Priority' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'Active' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'Communities Activities' link in the left panel of the Activities page</li>
	*<li><B>Step: </B>Click on the link for the community activity from orgA</li>
	*<li><B>Verify: </B>The activity opens inside the community</li>
	*<li><B>Step: </B>Delete the standalone activities and the community</li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void activitiesFromOrgaNotVisibleToExternalUser() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		apiActivitiesHandler = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
													  .access(Access.RESTRICTED)
													  .allowExternalUserAccess(true)
													  .rbl(true)
													  .shareOutside(true)
													  .description("Test description for testcase " + testName)
													  .addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
													  .build();

		BaseActivity baseCommActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													  .dueDateRandom()
												      .useCalPick(true)
													  .goal(Data.getData().commonDescription + testName)
													  .community(orgaExternalRestricted)
													  .build();

		BaseActivity standalonePrivateActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				  									  .tags(testName)
													  .goal(Data.getData().commonDescription + testName)
													  .build();

		BaseActivity standalonePublicActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													  .tags(testName)
													  .goal(Data.getData().commonDescription + testName)
													  .isPublic(true)
													  .build();

		logger.strongStep("Create an external restricted community in orgA with " + extTestUser1.getDisplayName() + " as a member");
		log.info("INFO: Create an external restricted community in orgA with " + extTestUser1.getDisplayName() + " as a member");
		Community community = orgaExternalRestricted.createAPI(apiHandler);

		logger.strongStep("Get UUID of the community");
		log.info("INFO: Get UUID of the community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, community);

		logger.strongStep("Create an activity inside the community");
		log.info("INFO: Create an activity inside the community");
		baseCommActivity.createAPI(apiActivitiesHandler, orgaExternalRestricted);

		logger.strongStep("Create standalone private and public activities in orgA using API");
		log.info("INFO: Create standalone private and public activities in orgA using API");
		Activity privateActivity = standalonePrivateActivity.createAPI(apiActivitiesHandler);
		Activity publicActivity = standalonePublicActivity.createAPI(apiActivitiesHandler);

		logger.strongStep("Add the Activities widget to the community if not already added");
		log.info("INFO: Add the Activities widget to the community if not already added");
		if(apiHandler.getWidgetID(community.getUuid(), "Activities").isEmpty())
			orgaExternalRestricted.addWidgetAPI(community, apiHandler, BaseWidget.ACTIVITIES);

		logger.strongStep("Load the Activities component and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Activities component and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(extTestUser1);

		//Go to different sections of the Activities page and verify that neither the standalone activities created earlier nor any other activity from orgA is visible anywhere
		logger.strongStep("Click on the 'My Activities' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'My Activities' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallMyActivity);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'High Priority' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'High Priority' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallHighPriority);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'Medium Priority' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'Medium Priority' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallMediumPriority);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'Active' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'Active' link in the left panel of the Activities page");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.PublicActivities_Active);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'Community Activities' link and then click on the link for the orgA activity: " + baseCommActivity.getName());
		log.info("INFO: Click on the 'Community Activities' link and then click on the link for the orgA activity: " + baseCommActivity.getName());
		actUI.openCommunityActivity(baseCommActivity);

		logger.strongStep("Verify that the activity opens inside the community");
		log.info("INFO: Verify that the activity opens inside the community");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.communityName).getText(), orgaExternalRestricted.getName(),
				"The activity did not open inside the community");

		//Delete the standalone activities and the community
		logger.strongStep("Delete the standalone activities and the community");
		log.info("INFO: Delete the standalone activities and the community");
		apiActivitiesHandler.deleteActivity(privateActivity);
		apiActivitiesHandler.deleteActivity(publicActivity);
		apiHandler.deleteCommunity(community);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: </B>Verify that an external user is allowed to access an activity created inside a community if the user is a member of the community</li>
	*<li><B>Step: </B>Create an external restricted community in orgA with an external user as a member</li>
	*<li><B>Step: </B>Create an activity inside the community and add an Entry and To Do to the activity</li>
	*<li><B>Step: </B>Create a standalone public activity in orgA using API</li>
	*<li><B>Step: </B>Add the Activities widget to the community if not already added</li>
	*<li><B>Step: </B>Get UUID of the community</li>
	*<li><B>Step: </B>Load the Activities component and login as the external user who is a member of the orgA community</li>
	*<li><B>Step: </B>Go to different sections of the Activities page</li>
	*<li><B>Verify: </B>Neither the standalone activity created earlier nor any other activity from orgA is not visible anywhere</li>
	*<li><B>Step: </B>Mark the standalone activity as complete</li>
	*<li><B>Step: </B>Click on the 'Completed Activities' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'Tuned Out Activities' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Click on the 'Trash' link in the left panel of the Activities page</li>
	*<li><B>Verify: </B>The message 'No activities to display.' is visible</li>
	*<li><B>Step: </B>Load the Communities component</li>
	*<li><B>Step: </B>Navigate to the community created earlier using its UUID</li>
	*<li><B>Step: </B>Navigate to the Activities tab in the community</li>
	*<li><B>Step: </B>Click on the link for the activity created inside the community</li>
	*<li><B>Step: </B>Expand the Entry created inside the community activity</li>
	*<li><B>Verify: </B>The description of the Entry is visible</li>
	*<li><B>Step: </B>Expand the To Do created inside the community activity</li>
	*<li><B>Verify: </B>The description of the To Do is visible</li>
	*<li><B>Step: </B>Delete the standalone activity and the community</li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void accessAllowedToExternalUserForInternalCommunityActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		apiActivitiesHandler = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		BaseCommunity orgaExternalRestricted = new BaseCommunity.Builder("orgaExternalRestricted" + Helper.genDateBasedRand())
													  .access(Access.RESTRICTED)
													  .allowExternalUserAccess(true)
													  .rbl(true)
													  .shareOutside(true)
													  .description("Test description for testcase " + testName)
													  .addMember(new Member(CommunityRole.MEMBERS, extTestUser1))
													  .build();

		BaseActivity baseCommActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													  .dueDateRandom()
												      .useCalPick(true)
													  .goal(Data.getData().commonDescription + testName)
													  .community(orgaExternalRestricted)
													  .build();

		BaseActivity standaloneActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
													  .tags(testName)
													  .goal(Data.getData().commonDescription + testName)
													  .isPublic(true)
													  .build();

		logger.strongStep("Create an external restricted community in orgA with " + extTestUser1.getDisplayName() + " as a member");
		log.info("INFO: Create an external restricted community in orgA with " + extTestUser1.getDisplayName() + " as a member");
		Community community = orgaExternalRestricted.createAPI(apiHandler);

		logger.strongStep("Create an activity inside the community and add an Entry and To Do to the activity");
		log.info("INFO: Create an activity inside the community and add an Entry and To Do to the activity");
		Activity commActivity = CommunityActivityEvents.createCommunityActivity(baseCommActivity, orgaExternalRestricted, testUser_orgA,
				apiActivitiesHandler, apiHandler, community);
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(orgaExternalRestricted.getName(), commActivity, false);
		BaseActivityToDo baseActivityToDo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntry(testUser_orgA, apiActivitiesHandler, baseActivityEntry, commActivity);
		CommunityActivityEvents.createActivityTodo(testUser_orgA, apiActivitiesHandler, baseActivityToDo, commActivity);

		logger.strongStep("Create a standalone public activity in orgA using API");
		log.info("INFO: Create a standalone public activity in orgA using API");
		Activity activity = standaloneActivity.createAPI(apiActivitiesHandler);

		logger.strongStep("Add the Activities widget to the community if not already added");
		log.info("INFO: Add the Activities widget to the community if not already added");
		if(apiHandler.getWidgetID(community.getUuid(), "Activities").isEmpty())
			orgaExternalRestricted.addWidgetAPI(community, apiHandler, BaseWidget.ACTIVITIES);

		logger.strongStep("Get UUID of the community");
		log.info("INFO: Get UUID of the community");
		orgaExternalRestricted.getCommunityUUID_API(apiHandler, community);

		logger.strongStep("Load the Activities component and login as: " + extTestUser1.getDisplayName());
		log.info("INFO: Load the Activities component and login as: " + extTestUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(extTestUser1);

		logger.strongStep("Mark the standalone activity as complete");
		log.info("INFO: Mark the standalone activity as complete");
		activity.setIsComplete(true);

		//Go to different sections of the Activities page and verify that neither the standalone activity created earlier nor any other activity from orgA is visible anywhere
		logger.strongStep("Click on the 'Completed Activities' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'Completed Activities' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallCompletedActivities);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'Tuned Out Activities' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'Tuned Out Activities' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallTurnedOut);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Click on the 'Trash' link in the left panel of the Activities page");
		log.info("INFO: Click on the 'Trash' link in the left panel of the Activities page");
		ui.clickLinkWait(ActivitiesUIConstants.OverallTrash);

		logger.strongStep("Verify that the message 'No activities to display.' is visible");
		log.info("INFO: Verify that the message 'No activities to display.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(ActivitiesUIConstants.NoActivitiesMessage),
				"The message 'No activities to display.' is visible");

		logger.strongStep("Load the Communities component");
		log.info("INFO: Load the Communities component");
		ui.loadComponent(Data.getData().ComponentCommunities, true);

		logger.strongStep("Navigate to the community created earlier using its UUID");
		log.info("INFO: Navigate to the community created earlier using its UUID");
		orgaExternalRestricted.navViaUUID(ui);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Navigate to the Activities tab in the community");
		log.info("INFO: Navigate to the Activities tab in the community");
		Community_TabbedNav_Menu.ACTIVITIES.select(ui);

		logger.strongStep("Click on the link for the activity: " + baseCommActivity.getName());
		log.info("INFO: Click on the link for the activity: " + baseCommActivity.getName());
		ui.clickLinkWait(ActivitiesUI.getActivityLink(baseCommActivity));

		//Verify that the Entry and To Do created inside the community activity are visible to the external user
		logger.strongStep("Expand the Entry: " + baseActivityEntry.getTitle());
		log.info("INFO: Expand the Entry: " + baseActivityEntry.getTitle());
		actUI.expandEntry(actUI.getEntryUUID(baseActivityEntry));

		logger.strongStep("Verify that the description of the Entry is visible");
		log.info("INFO: Verify that the description of the Entry is visible");
		Assert.assertTrue(ui.fluentWaitTextPresent(baseActivityEntry.getDescription()),
				"The description of the Entry is visible");

		logger.strongStep("Expand the To Do: " + baseActivityToDo.getTitle());
		log.info("INFO: Expand the To Do: " + baseActivityToDo.getTitle());
		actUI.expandEntry(actUI.getEntryUUID(baseActivityToDo));

		logger.strongStep("Verify that the description of the To Do is visible");
		log.info("INFO: Verify that the description of the To Do is visible");
		Assert.assertTrue(ui.fluentWaitTextPresent(baseActivityToDo.getDescription()),
				"The description of the To Do is visible");

		//Delete the standalone activity and the community
		logger.strongStep("Delete the standalone activity and the community");
		log.info("INFO: Delete the standalone activity and the community");
		apiActivitiesHandler.deleteActivity(activity);
		apiHandler.deleteCommunity(community);

		ui.endTest();

	}

}
