package com.ibm.conn.auto.tests.communities.mt.switchurl;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;

public class BVT_Level_2_CommunitiesWidgets_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_CommunitiesWidgets_MT_Boundary.class);

	private CommunitiesUI ui;
	
	private TestConfigCustom cfg;
	private User testUser_orgA,testUser_orgA1;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APICommunitiesHandler apiOwner;



	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgA1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for Moderated sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a external restricted community with name, tag, description and members in it</li>
	 * <li><B>Step: </B> Go to created community.</li>
	 * <li><B>Step: </B> Add widgets Wiki, Related communities to community.</li>
	 * <li><B>Step: </B> Select each widget from community view</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "mtlevel3" })
	public void externalRestrictedCommWidgets() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);
		
		BaseCommunity communityExtrnl = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED)
				.description("Test description for testcase: " + Data.getData().descriptionExternalComm)
				.addMember(member)
				.shareOutside(true) 
				.build();

		//create community
		logger.strongStep("Login to test user and create a Community");
		log.info("INFO: Create community using API");
		Assert.assertNotNull(apiOwner);
		Community comAPI = communityExtrnl.createAPI(apiOwner);
		
		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		logger.strongStep("Get UUID of community");
		communityExtrnl.setCommunityUUID(communityExtrnl.getCommunityUUID_API(apiOwner, comAPI));

		// add widget Wiki
		logger.strongStep("Add the Wiki widget to the Community using API");
		if (!apiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			communityExtrnl.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);
		}
		
		//add widget Related comunities
		log.info("INFO: Add RELATED_COMMUNITIES widget with api");
		logger.strongStep("Add RELATED_COMMUNITIES widget with api");
		communityExtrnl.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES); 
		
		// Load component and login
		log.info("INFO:Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA,Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);
		
		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		communityExtrnl.navViaUUID(ui);
		
		// Validate switch url for Wiki
		log.info("INFO: Validate switch url for Wiki");
		logger.strongStep("Validate access denied error message after switching url for Wiki");
		ui.validateSwitchURLCommWidget("wiki",serverURL_MT_orgB, CommunitiesUIConstants.commWikiPagActionBtn, "Page Actions");
		
		// Validate switch url for Related communities
		log.info("INFO: Validate switch url for Related communities");
		logger.strongStep("Validate access denied error message after switching url for Related communities");
		ui.validateSwitchURLCommWidget("related communities",serverURL_MT_orgB, ui.getCommWidgetPageHeader("#fullRecomm"), "Related Communities");
	
		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for Moderated sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a "Restricted But Listed" community with name, tag, description and members in it</li>
	 * <li><B>Step: </B> Go to created community.</li>
	 * <li><B>Step: </B> Add widgets Events, Highlights to community.</li>
	 * <li><B>Step: </B> Select each above widgets including Metrics from community view</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	
	@Test(groups = { "mtlevel3" })
	public void restrictedButListedCommWidgets() throws Exception {
		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED)
				.description("Test description for testcase: " + Data.getData().descriptionExternalComm)
				.addMember(member)
				.shareOutside(true) 
				.build();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// create community
		logger.strongStep("Login to test user and create a Community");
		log.info("INFO: Create community using API");
		Assert.assertNotNull(apiOwner);
		Community comAPI = community.createAPI(apiOwner);
		
		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		logger.strongStep("Get UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, comAPI));
		
		// add widget Wiki
		logger.strongStep("Enable all widgets");
		log.info("INFO: Add EVENTS widget with api");
		logger.strongStep("Add EVENTS widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		// add widget Gallery
		logger.strongStep("Add the Highlight widget to the Community using API");
		if (!apiOwner.hasWidget(comAPI, BaseWidget.HIGHLIGHTS)) {
			log.info("INFO: Add the Highlight widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.HIGHLIGHTS);
		}

		// Load component and login
		log.info("INFO:Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		logger.strongStep("Open Communities and Log In as: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForCommunityLoaded();
		
		// Validate switch url for highlights
		log.info("INFO: Validate switch url for highlights");
		logger.strongStep("Validate access denied error message after switching url for highlights");
		ui.validateSwitchURLCommWidget("highlights", serverURL_MT_orgB, IcecUI.customizeButton, "CUSTOMIZE");

		// Validate switch url for events
		log.info("INFO: Validate switch url for events");
		logger.strongStep("Validate access denied error message after switching url for events");
		ui.validateSwitchURLCommWidget("events", serverURL_MT_orgB, ui.getCommWidgetPageHeader("div[id='calendarViews']"), "Events");

		// Validate switch url for metrics
		log.info("INFO: Validate switch url for metrics");
		logger.strongStep("Validate access denied error message after switching url for metrics");
		ui.validateSwitchURLCommWidget("metrics", serverURL_MT_orgB, CommunitiesUIConstants.commMetricsHeader, "Participation Metrics");

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
	 * <li><B>Step: </B> Add a Moderated sub community to the 'Public'community.</li>
	 * <li><B>Step: </B> Go to created 'Public' community.</li>
	 * <li><B>Step: </B> Select each Recent Updates,Status updates, Members</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be
	 * displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "mtlevel2" })
	public void publicCommWidgets() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);

		BaseCommunity communityPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
						.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
						.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
						.description("Test description for testcase " + testName)
						.addMember(member)
						.build();

		
		String subcommunityNameModerated = Data.getData().commonName + "_subcomm_" + Helper.genDateBasedRand();
		BaseSubCommunity subcommunityModerated = new BaseSubCommunity.Builder(subcommunityNameModerated + "_Mod")
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(BaseSubCommunity.Access.MODERATED)
				.description("Test switch url subcommunity.")
				.UseParentmembers(true)
				.build();

		log.info("INFO: API user: " + testUser_orgA.getDisplayName());
		Assert.assertNotNull(apiOwner);
		// create community using API
		log.info("INFO: Create community using API");
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

		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Navigate to the community using UUID");
		communityPub.navViaUUID(ui);
		ui.waitForCommunityLoaded();

		// Navigate to the API community
		log.info("INFO: Create modertaed sub community under public community");
		logger.strongStep("Create modertaed sub community under public community");
		ui.createSubCommunity(subcommunityModerated);

		// Validate switch url for recent updates
		log.info("INFO: Validate switch url for recent updates");
		logger.strongStep("Validate access denied error message after switching url for recent updates");
		ui.validateSwitchURLCommWidget("recent updates", serverURL_MT_orgB, CommunitiesUIConstants.commRecentUpdatesHeader, "Recent Updates");

		// Validate switch url for status updates
		log.info("INFO: Validate switch url for status updates");
		logger.strongStep("Validate access denied error message after switching url for status updates");
		ui.validateSwitchURLCommWidget("status updates", serverURL_MT_orgB, CommunitiesUIConstants.commStatusUpdatesHeader, "Status Updates");

		// Validate switch url for recent updates
		log.info("INFO: Validate switch url for members");
		logger.strongStep("Validate access denied error message after switching url for members");
		ui.validateSwitchURLCommWidget("members", serverURL_MT_orgB, ui.getCommWidgetPageHeader("div[id='Members']"), "Members");

		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
}