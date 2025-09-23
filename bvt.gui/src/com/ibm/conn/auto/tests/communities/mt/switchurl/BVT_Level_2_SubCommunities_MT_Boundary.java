package com.ibm.conn.auto.tests.communities.mt.switchurl;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;

public class BVT_Level_2_SubCommunities_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_SubCommunities_MT_Boundary.class);

	private CommunitiesUI ui;
	
	private BaseSubCommunity subcommunityModerated, subcommunityRestricted;
	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgA1;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APICommunitiesHandler apiOwner;

	BaseCommunity communityPub;
	private String communityName;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgA1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
				testUser_orgA.getPassword());
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		initialize();

	}

	private void initialize() throws Exception {
		log.info("INFO: initialize");

		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);
		String rndNum = Helper.genDateBasedRand();

		// Create a community base state object
		communityName = Data.getData().commonName + rndNum;
		communityPub = new BaseCommunity.Builder(communityName + 1)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + communityName).addMember(member).build();

		// Create a sub-community base state object
		String subcommunityNameModerated = Data.getData().commonName + "_subcomm_" + Helper.genDateBasedRand();
		subcommunityModerated = new BaseSubCommunity.Builder(subcommunityNameModerated + "_Mod")
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(BaseSubCommunity.Access.MODERATED)
				.description("Test switch url subcommunity.").UseParentmembers(true).build();

		subcommunityRestricted = new BaseSubCommunity.Builder(subcommunityNameModerated + "_restricted")
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(BaseSubCommunity.Access.RESTRICTED).description("Test switch url subcommunity.")
				.build();
	}
	private void createSubComm(BaseCommunity community, BaseSubCommunity subcommnunity) throws Exception {

		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForCommunityLoaded();

		// Create subcommunity to the API community
		log.info("INFO: createCommunity");
		ui.createSubCommunity(subcommnunity);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for public community having Moderated and internal restricted sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	 * <li><B>Step: </B> Add a Moderated and Internal Restricted sub community to the 'Public' community.</li>
	 * <li><B>Step: </B> Go to each sub community page</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "mtlevel2" })
	public void publicCommWithSubcomm() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: API user: " + testUser_orgA.getDisplayName());
		Assert.assertNotNull(apiOwner);

		// create community using API
		log.info("INFO: Create community using API");
		Community comAPI = communityPub.createAPI(apiOwner);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		communityPub.setCommunityUUID(communityPub.getCommunityUUID_API(apiOwner, comAPI));

		// Load component and Login as a user
		log.info("INFO: Using test user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Using test user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Create sub community
		log.info("INFO: Create moderated subcommunity");
		logger.strongStep("INFO: Create moderated subcommunity");
		createSubComm(communityPub, subcommunityModerated);
		
		ui.waitForCommunityLoaded();

		// switch URL to orgB
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);
		
		ui.waitForPageLoaded(driver);

		// Validate error message
		logger.strongStep("Verfiy access denied message should be displayed.");
		log.info("INFO: Verfiy access denied message should be displayed.");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");
		driver.navigate().back();

		// Create sub community
		log.info("INFO: Create resticted subcommunity");
		logger.strongStep("INFO: Create resticted subcommunity");
		createSubComm(communityPub, subcommunityRestricted);
		
		ui.waitForCommunityLoaded();

		// switch URL to orgB
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);
		
		ui.waitForPageLoaded(driver);

		// Validate error message
		logger.strongStep("Verfiy access denied message should be displayed.");
		log.info("INFO: Verfiy access denied message should be displayed.");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for Moderated sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	 * <li><B>Step: </B> Add a Moderated sub community to the 'Public' community.</li>
	 * <li><B>Step: </B> Go to Moderated sub community page</li>
	 * <li><B>Step: </B> Select each Forums, Bookmarks, Files widget</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "mtlevel2" })
	public void moderatedSubCommWidgets() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: API user: " + testUser_orgA.getDisplayName());
		Assert.assertNotNull(apiOwner);
		// create community using API
		log.info("INFO: Create community using API");
		logger.strongStep("Create community using API");
		Community comAPI = communityPub.createAPI(apiOwner);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		logger.strongStep("Get UUID of community");
		communityPub.setCommunityUUID(communityPub.getCommunityUUID_API(apiOwner, comAPI));

		// Load component and Login as a user
		log.info("INFO:Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Create moderated sub community to the API community
		log.info("INFO: create moderated subcommunity under above community");
		logger.strongStep("create moderated subcommunity under above community");
		createSubComm(communityPub, subcommunityModerated);
		
		// Validate switch url for forums
		log.info("INFO: Validate error message after switching url for forums");
		logger.strongStep("Validate access denied error message after switching url for forums");
		ui.validateSwitchURLCommWidget("forums", serverURL_MT_orgB, ui.getCommWidgetPageHeader(".dfCommunityForums"), "Forums");
		
		// Validate switch url for bookmarks
		log.info("INFO: Validate switch url for bookmarks");
		logger.strongStep("Validate access denied error message after switching url for bookmarks");
		ui.validateSwitchURLCommWidget("bookmarks", serverURL_MT_orgB,ui.getCommWidgetPageHeader("#bookmarkContentAreaSub"),"Bookmarks");
		
		// Validate switch url for files
		log.info("INFO: Validate switch url for files");
		logger.strongStep("Validate access denied error message after switching url for files");
		ui.validateSwitchURLCommWidget("files", serverURL_MT_orgB,ui.getCommWidgetPageHeader("#lotusContentHeader"),"All Community Files");
		
		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for Moderated sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a 'Public' community with name, tag, description and members in it</li>
	 * <li><B>Step: </B> Add a 'Internal Restricted' sub-community to the 'Public' community.</li>
	 * <li><B>Step: </B> Go to 'Internal Restricted' sub-community page</li>
	 * <li><B>Step: </B> Add widgets Blog, Ideation Blog, Activities to sub community.</li>
	 * <li><B>Step: </B> Select each widget from sub-community view</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	
	@Test(groups = { "mtlevel3" })
	public void internalRestrictedSubCommWidgets() throws Exception {
		String widgetName;
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// create community
		logger.strongStep("Login to test user and create a Community");
		log.info("INFO: Create community using API");
		Assert.assertNotNull(apiOwner);
		Community comAPI = communityPub.createAPI(apiOwner);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		communityPub.setCommunityUUID(communityPub.getCommunityUUID_API(apiOwner, comAPI));

		// GUI
		// Load component and login
		log.info("INFO:Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Create sub community to the API community
		log.info("INFO: create moderated sub community under above community");
		logger.strongStep("INFO: create moderated sub community under above community");
		createSubComm(communityPub, subcommunityRestricted);

		// Click on Community Actions link
		log.info("INFO: Click on the Community Actions link");
		logger.strongStep("Click on the Community Actions link");
		Com_Action_Menu.ADDAPP.open(ui);

		// Click on the Add Apps link to bring up the widget palette
		log.info("INFO: From the Community Actions drop-down menu click on Add Apps");
		logger.strongStep("From the Community Actions drop-down menu click on Add Apps");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.addAppslink);

		// collect all the disabled widget elements
		List<Element> widgets = ui.collectDisabledCommWidgets();

		// Add Blog, ideation blog and ACtivities widgets to sub community
		log.info("INFO: Add Blog, ideation blog and ACtivities widgets to sub community");
		logger.strongStep("Add Blog, ideation blog and ACtivities widgets to sub community");
		
		// select the widgets from above
		for (Element widget : widgets) {

			ui.fluentWaitElementVisible(CommunitiesUIConstants.WidgetSectionClose);
			widgetName = widget.getText();
			if (widgetName.contains("Blog") || widgetName.contains("ideation blog")
					|| widgetName.contains("Activities")) {

				// Click on widget
				widget.click();
				ui.waitForPageLoaded(driver);
			}

			else {
				log.info("Skip adding" + widgetName);
				logger.strongStep("Skip adding " + widgetName);
			}
		}
		// Close the application palette
		log.info("INFO: Close the application palette");
		logger.strongStep("INFO: Close the application palette");
		ui.clickLinkWait(CommunitiesUIConstants.WidgetSectionClose);
		
		// Validate switch url for blog
		log.info("INFO: Validate switch url for blog");
		logger.strongStep("Validate access denied error message after switching url for blog");
		ui.validateSwitchURLCommWidget("blog", serverURL_MT_orgB, CommunitiesUIConstants.commBlogEntryFeedLink, "Feed for Blog Entries");		

		// Validate switch url for ideation blog
		log.info("INFO: Validate switch url for ideation blog");
		logger.strongStep("Validate access denied error message after switching url for ideation blog");
		ui.validateSwitchURLCommWidget("ideation blog", serverURL_MT_orgB, ui.getCommWidgetPageHeader("div[id='ideationsListContainer'] div[class='lotusHeader']"), "Ideation Blogs");

		// Validate switch url for activities
		log.info("INFO: Validate switch url for activities");
		logger.strongStep("Validate access denied error message after switching url for activities");
		ui.validateSwitchURLCommWidget("activities", serverURL_MT_orgB, ui.getCommWidgetPageHeader("#activityList"), "Activities");
		
		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
}
