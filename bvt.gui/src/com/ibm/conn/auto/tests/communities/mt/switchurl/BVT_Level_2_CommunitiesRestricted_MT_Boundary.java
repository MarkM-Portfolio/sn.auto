package com.ibm.conn.auto.tests.communities.mt.switchurl;

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
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;

public class BVT_Level_2_CommunitiesRestricted_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_CommunitiesRestricted_MT_Boundary.class);
	
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

	private void switchURLCommunity(BaseCommunity community) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// create community using API
		Assert.assertNotNull(apiOwner);
		log.info("INFO: Create community using API");
		logger.strongStep("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		logger.strongStep("INFO: Get UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, comAPI));

		// Load component and Login as a user
		log.info("INFO: Using test user: " + testUser_orgA.getDisplayName());
		logger.strongStep("INFO: Using test user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		// switch URL to orgB
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);

		// Validate error message
		log.info("Verify access denied error message should be displayed");
		logger.strongStep("Verify access denied error message should be displayed");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");

		// Delete community
		log.info("INFO: Delete community");
		logger.strongStep("Delete community");
		apiOwner.deleteCommunity(comAPI);

	}

		/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for external restricted community to orgB</li>
	 * <li><B>Step:</B> [API] Create a external restricted community with name, tag, description and members in it</li>
	 * <li><B>Step: </B> Open newly created community in above step</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 */

	@Test(groups = { "mtlevel2" })
	public void externalRestrictedComm() throws Exception {

		String testName = ui.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED)
				.description("Test description for testcase: " + Data.getData().descriptionExternalComm)
				.addMember(member).shareOutside(true)
				.build();

		// Switch the activity URL to orgB and validate error message
		switchURLCommunity(community);
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for community with access level set to restricted but listed to orgB</li>
	 * <li><B>Step:</B> [API] Create a restricted but listed community with name, tag, description and members in it</li>
	 * <li><B>Step: </B> Open newly created community in above step</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 */

	@Test(groups = { "mtlevel2" })
	public void restrictedButListedComm() throws Exception {

		String testName = ui.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.RESTRICTED)
				.description("Test description for testcase " + testName)
				.addMember(member).rbl(true)
				.shareOutside(false)
				.build();

		// Switch the activity URL to orgB and validate error message
		switchURLCommunity(community);

		ui.endTest();
	}
}
