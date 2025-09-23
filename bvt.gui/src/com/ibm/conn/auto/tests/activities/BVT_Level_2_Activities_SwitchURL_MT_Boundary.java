package com.ibm.conn.auto.tests.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;

public class BVT_Level_2_Activities_SwitchURL_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Activities_SwitchURL_MT_Boundary.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA,testUser_orgA1;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APIActivitiesHandler apiHandleract;
	ActivityMember reader;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgA1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
		apiHandleract = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA,
				testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
		reader = new ActivityMember(ActivityRole.READER, testUser_orgA1, ActivityMember.MemberType.PERSON);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}

	private void switchURLActivity(BaseActivity baseActivity) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Create activity using API
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		Activity act = baseActivity.createAPI(apiHandleract);
		
		// Login to component and open the activity
		loginAndOpenActivity(baseActivity);
		
		// Switch the activity URL to orgB and validate error message
		validateSwitchURL(baseActivity, act);
	}

	private void loginAndOpenActivity(BaseActivity baseActivity) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		log.info("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(baseActivity));
	}

	private void validateSwitchURL(BaseActivity baseActivity, Activity activity) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// switch URL to orgB
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);

		// Validate error message
		logger.strongStep("Verify access denied error message should be displayed");
		log.info("Verify access denied error message should be displayed");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		// Delete activity
		logger.strongStep("Delete activity");
		log.info("Delete activity");
		apiHandleract.deleteActivity(activity);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch newly created public Activity URL to orgB</li>
	 * <li><B>Step: </B> [API] Create a public Activity with name, goal and member</li>
	 * <li><B>Step: </B> Go to newly created public Activity Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLPublicActivity() {

		String testName = ui.startTest();
		BaseActivity baseActivityPub = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName).addMember(reader).isPublic(true).build();

		// Switch the activity URL to orgB and validate error message
		switchURLActivity(baseActivityPub);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch newly created private Activity URL to orgB</li>
	 * <li><B>Step: </B> [API] Create a private Activity with name, goal member </li>
	 * <li><B>Step: </B> Go to newly created public Activity Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLPrivateActivity() {
		String testName = ui.startTest();
		BaseActivity baseActivityPrivate = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName)
				.addMember(reader)
				.isPublic(false)
				.build();

		// Switch the activity URL to orgB and validate error message
		switchURLActivity(baseActivityPrivate);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch newly created private Activity with entry added to it URL to orgB</li>
	 * <li><B>Step: </B> [API] Create a private Activity with name, goal and member</li>
	 * <li><B>Step: </B> Go to newly created public Activity Page</li>
	 * <li><B>Step: </B> Added entry with custom field to newly created private Activity</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel3" })
	public void switchURLPrivateActivityWithEntry() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseActivity baseActivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName)
				.addMember(reader)
				.build();

		// Create activity using API
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		Activity activity = baseActivity.createAPI(apiHandleract);

		// Create New entry for activity created above
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.customText(Data.getData().CustomFieldName, "Custom text for " + testName).dateRandom()
				.description(Data.getData().commonDescription + testName)
				.build();
		
		// Create New entry for activity
		logger.strongStep("Create New entry for activity");
		entry.setParent(activity);
		log.info("INFO: " + testUser_orgA.getDisplayName() + " will now create an entry in the private activity using the API");

		// Login to component and open the activity
		loginAndOpenActivity(baseActivity);
		
		// Switch the activity URL to orgB and validate error message
		validateSwitchURL(baseActivity, activity);

		ui.endTest();
	}
}