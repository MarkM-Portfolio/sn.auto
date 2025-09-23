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
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;

public class BVT_Level_2_CommunitiesTrashView_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_CommunitiesTrashView_MT_Boundary.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgA1;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APICommunitiesHandler apiOwner;
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
	}

	private void restoreComm(BaseCommunity community) {

		// Go to my communities
		log.info("Go to my communities");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);

		// Restore community
		log.info("Restore community: " + community);
		ui.clickLinkWait(CommunitiesUIConstants.topNavMyCommunitiesCardView);
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardView);
		ui.clickLinkWait(CommunitiesUIConstants.viewSelectorDropDownCardViewTrashMT);
		ui.fluentWaitPresentWithRefresh(ui.getCommunityRestoreButtonLink(community));
		ui.clickLinkWait(ui.getCommunityRestoreButtonLink(community));
		ui.fluentWaitElementVisible(CommunitiesUIConstants.SuccessMessageAfterCommunityCardRestore);
	}

	private void navToCommValidateMsg(String url) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Navigate to community url
		driver.navigate().to(url);
	    
		// switch URL to orgB
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);

		// Validate error message
		log.info("Validate that access denied message should be displayed");
		logger.strongStep("Validate that access denied message should be displayed");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");
		driver.navigate().back();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch URLs for public community having Moderated and internal restricted sub-communities to orgB</li>
	 * <li><B>Step:</B> [API] Create a two public community with name, tag and description</li>
	 * <li><B>Step: </B> Save the url for both communities</li>
	 * <li><B>Step: </B> Delete both communities.</li>
	 * <li><B>Step: </B> Restore one of the communities.</li>
	 * <li><B>Step: </B> Navigate to each url saved earlier.</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */

	@Test(groups = { "mtlevel2" })
	public void commTrashView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser_orgA1);
		BaseCommunity community1 = new BaseCommunity.Builder(testName + "_comm1_" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + communityName).addMember(member).build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName + "_comm2_" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + communityName).addMember(member).build();

		log.info("INFO: API user: " + testUser_orgA.getDisplayName());

		// create community using API
		log.info("INFO: Create community using API");
		Assert.assertNotNull(apiOwner);
		logger.strongStep("Create community using API");
		Community comAPI1 = community1.createAPI(apiOwner);
		Community comAPI2 = community2.createAPI(apiOwner);

		// Add the UUID to community 1
		log.info("INFO: Get UUID of community");
		logger.strongStep("Get UUID of communityPub: " + community1);
		community1.setCommunityUUID(community1.getCommunityUUID_API(apiOwner, comAPI1));

		// Add the UUID to community 2
		log.info("INFO: Get UUID of community");
		logger.strongStep("Get UUID of community: " + community2);
		community2.setCommunityUUID(community2.getCommunityUUID_API(apiOwner, comAPI2));

		// Load component and Login as a user
		log.info("INFO: Using test user: " + testUser_orgA.getDisplayName());
		logger.strongStep("Using test user: " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// Navigate to the API community 1 and get url
		log.info("INFO: Navigate to the community1 using UUID");
		logger.strongStep("INFO: Navigate to the community1using UUID");
		community1.navViaUUID(ui);
		ui.fluentWaitPresent(CommunitiesUIConstants.topNavBar);
		String comURL1 = driver.getCurrentUrl();

		// Navigate to the API community 2 and get url
		log.info("INFO: Navigate to the community2 using UUID");
		logger.strongStep("INFO: Navigate to the community2 using UUID");
		community2.navViaUUID(ui);
		ui.fluentWaitPresent(CommunitiesUIConstants.topNavBar);
		String comURL2 = driver.getCurrentUrl();

		// Delete both communities
		log.info("INFO: Delete both communities");
		logger.strongStep("Delete both communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);

		// Restore one of the deleted community
		log.info("INFO: Restore Community");
		logger.strongStep("Restore Community ");
		restoreComm(community1);

		// Navigate to restored community url
		log.info("INFO: Navigate to restored Community url");
		logger.strongStep("Navigate to restored Community url");
		navToCommValidateMsg(comURL1);

		// Navigate to deleted community url
		log.info("INFO: Navigate to deleted Community url");
		logger.strongStep("Navigate to deleted Community url");
		navToCommValidateMsg(comURL2);

		// Delete community
		log.info("INFO: Delete Community with API");
		logger.strongStep("Delete Community with API");
		apiOwner.deleteCommunity(comAPI1);

		ui.endTest();
	}
}
