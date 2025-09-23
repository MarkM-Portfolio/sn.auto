package com.ibm.conn.auto.tests.mt;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;

public class BVT_Level_2_MT_Boundary_switchURL extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_MT_Boundary_switchURL.class);
	private ActivitiesUI ui;
	private HomepageUI hUI;
	private CommunitiesUI cUI;
	private ProfilesUI pUI;
	private FilesUI fUI;

	private TestConfigCustom cfg;
	private User testUser_orgA;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APICommunitiesHandler apiHandler;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		pUI = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}

	private void switchToOrgBURL() {
		String url = driver.getCurrentUrl();
		String org1 = url.substring(url.indexOf("/") + 2, url.indexOf("."));
		String org2 = serverURL_MT_orgB.substring(serverURL_MT_orgB.indexOf("/") + 2, serverURL_MT_orgB.indexOf("."));
		log.info("Org1 is: " + org1);
		log.info("Org2 is: " + org2);
		url = url.replace(org1, org2);
		driver.navigate().to(url);
	}

	private void validateAccessDenied(String accessDeniedErrMsg, String noPermissionErrMsg) {

		log.info("Verify access denied error message should be displayed");
		ui.fluentWaitElementVisible(BaseUIConstants.errorBox);
		Element ele1 = driver.getFirstElement(BaseUIConstants.AccessDenied);
		Element ele2 = driver.getFirstElement(BaseUIConstants.NoPermissionToAccess);
		String msg1 = ele1.getText();
		String msg2 = ele2.getText();
		log.info("1) Message is:" + msg1);
		log.info("2) Message is:" + msg2);

		Assert.assertEquals(msg1, accessDeniedErrMsg);
		Assert.assertEquals(msg2, noPermissionErrMsg);

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Test that orgA user is not able switch activity URL to
	 * orgB</li>
	 * <li><B>Step: </B> Go to Activities Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch communities URL
	 * to orgB</li>
	 * <li><B>Step: </B> Go to Communities Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */
	@Test(groups = { "mtlevel2" })
	public void switchURLCommunities() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Communities and Log In as " + testUser_orgA.getDisplayName());
		cUI.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		cUI.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		cUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch MyCommunities
	 * URL to orgB</li>
	 * <li><B>Step: </B> Go to My Communities Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLMyCommunities() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Communities and Log In as " + testUser_orgA.getDisplayName());
		cUI.loadComponent(serverURL_MT_orgA, CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY);
		cUI.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		cUI.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch newly created community URL to orgB</li>
	 * <li><B>Step: </B> [API] Create a public community with name, tag and description</li>
	 * <li><B>Step: </B> Go to newly created Community Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2" })
	public void switchURLNewCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Build the community to be created later
		log.info("INFO: Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).build();

		// Instantiate APIHandler
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());
		log.info("INFO: API user: " + testUser_orgA.getDisplayName());

		// create community using API
		log.info("INFO: Create community using API");
		Community community = baseCom.createAPI(apiHandler);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCom.setCommunityUUID(baseCom.getCommunityUUID_API(apiHandler, community));

		// Load component and Login as a user
		logger.strongStep("Load Communities and Log In as " + testUser_orgA.getDisplayName());
		cUI.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		cUI.login(testUser_orgA);

		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		baseCom.navViaUUID(cUI);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		driver.navigate().back();

		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiHandler.deleteCommunity(community);

		cUI.endTest();

	}


	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch Public
	 * Communities URL to orgB</li>
	 * <li><B>Step: </B> Go to My Public Communities Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLPublicCommunities() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Communities and Log In as " + testUser_orgA.getDisplayName());
		cUI.loadComponent(serverURL_MT_orgA, Data.getData().publicCommunityURL);
		cUI.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		cUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch Profile URL to
	 * orgB</li>
	 * <li><B>Step: </B> Go to Profile Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLProfiles() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Profile and Log In as " + testUser_orgA.getDisplayName());
		pUI.loadComponent(serverURL_MT_orgA, Data.getData().ComponentProfiles);
		pUI.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch Files URL to
	 * orgB</li>
	 * <li><B>Step: </B> Go to Files Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLFiles() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Files and Log In as " + testUser_orgA.getDisplayName());
		fUI.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		fUI.login(testUser_orgA);

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		fUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch Homepage URL to
	 * orgB</li>
	 * <li><B>Step: </B> Go to Home Page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void switchURLHomepage() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load component and Login as a user
		logger.strongStep("Load Home page and Log In as " + testUser_orgA.getDisplayName());
		hUI.loadComponent(serverURL_MT_orgA, Data.getData().ComponentHomepage);
		hUI.login(testUser_orgA);

		// Check and Dismiss Tour Welcome Popup if present
		logger.strongStep("Dismiss Tour Welcome Popup if present");
		log.info("INFO: Dismiss Tour Welcome popup");
		hUI.dismissTourWelcomePopup();

		// switch URL to orgB
		logger.strongStep("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		log.info("Switching URL" + driver.getCurrentUrl() + "to orgB.");
		switchToOrgBURL();

		// Validate error message
		logger.weakStep("Verify access denied error message should be displayed");
		validateAccessDenied("We are unable to process your request",
				"Click the browser back button to return to the previous page and try again. If this error persists, report the problem to your administrator.");

		hUI.endTest();

	}

}
