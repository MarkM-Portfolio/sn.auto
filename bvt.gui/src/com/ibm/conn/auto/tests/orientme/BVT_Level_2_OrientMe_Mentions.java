package com.ibm.conn.auto.tests.orientme;

import java.util.HashMap;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

import org.testng.Assert;

public class BVT_Level_2_OrientMe_Mentions extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Mentions.class);
	private TestConfigCustom cfg;
	private OrientMeUI omUI;
	
	private User testUserA;
	private User testUserB;
	private User testUserC;
	private SearchAdminService adminService;
	private User searchAdmin;
	private String serverUrl;
	private Community community;
	private APIProfilesHandler apiProfilesTestUserA, apiProfilesTestUserB, apiProfilesTestUserC;
	private APICommunitiesHandler apiCommTestUserA, apiCommTestUserB;
	Map<String, String> resourceStrings = new HashMap<String, String>();
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		omUI = OrientMeUI.getGui(cfg.getProductName(), driver);
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		testUserC = cfg.getUserAllocator().getUser();
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiProfilesTestUserA = new APIProfilesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiProfilesTestUserB = new APIProfilesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiProfilesTestUserC = new APIProfilesHandler(serverUrl, 
				testUserC.getAttribute(cfg.getLoginPreference()), testUserC.getPassword());
		apiCommTestUserA = new APICommunitiesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiCommTestUserB = new APICommunitiesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Top Updates/Latest update tab for UserA displays community created by UserB, UserA was added as a member, community created by UserA and any other updates made by UserA and Any other updates like status update made by UserA.</li>
	*<li><B>Pre-req:</B>As UserA 'Share something' entry with an @mention (UserB)</li>
	*<li><B>Pre-req:</B>As UserA Restricted community with UserB as an additional member, create a Recent Updates entry that @mentions UserB and UserC (non-community member)</li>
	*<li><B>Pre-req:</B>As UserA Public community (no additional members), create a Status Updates entry that @mentions UserB.</li>
	*<li><B>Step:</B>Log into Orient Me as UserB.</li>
	*<li><B>Verify:</B>The 'Mentions' button displays a count and the count looks correct.</li>
	*<li><B>Step:</B>Click on the @Mention button on the 'Important to me' bar.</li>
	*<li><B>Verify:</B>1. Mention from 'Share something' entry. 2. Recent updates entry from the restricted community 3. Status updates entry from the public community</li>
	*<li><B>Step:</B>Click on the 'X' to the right of Mentions in the 'Add filter...' field.</li>
	*<li><B>Verify:</B>count no longer appears on the @mentions button.</li>
	*<li><B>Step:</B>Log into Orient Me as UserC.</li>
	*<li><B>Verify:</B>User does NOT see the Recent Updates entry created in the restricted community because they are NOT a member of the community.</li>
	*@throws Exception 
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void mentionsVerificationTest() throws Exception{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		omUI.startTest();
		int expectedCount = 3;
		// As UserB, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserB.getDisplayName());
		omUI.goToOrientMe(testUserB, true);
		
		// Check if there is already new responses. If so, clear it.		
		omUI.resetMentionsCount();		

		// populate test data using API
		resourceStrings = filterTestDataPop();
		
		// Check Response count
		logger.strongStep(testUserA.getDisplayName() + "goes to OrientMe to check Mentions count to be " + expectedCount);
		driver.navigate().refresh();
		String count = omUI.getMentionsCount();
		Assert.assertTrue(count != null && !count.equals("0") && !count.isEmpty(), "Mentions count is found");
		Assert.assertEquals(Integer.parseInt(count), expectedCount, "Expected Mentions count found");
		
		log.info("INFO: Click on mentions icon");
		// Clear count.		
		omUI.clickLink(OrientMeUIConstants.mentionsNotificationIcon);
		omUI.waitForPageLoaded(driver);
		
		Assert.assertTrue(omUI.isTextPresent(resourceStrings.get("community-mention-rand")), "Recent updates entry from the restricted community is displayed on the screen");
		Assert.assertTrue(omUI.isTextPresent(resourceStrings.get("community-public-rand")), "Recent updates entry from the public community is displayed on the screen");
		Assert.assertTrue(omUI.isTextPresent(resourceStrings.get("status-update-rand")), "Status entry with mentions is displayed on the screen");
		
		omUI.getFirstVisibleElement(OrientMeUIConstants.removeFilter).click();
		omUI.waitForPageLoaded(driver);
		count = omUI.getMentionsCount();
		
		boolean blankCnt= count == null || count.equals("0") || count.isEmpty();
		Assert.assertTrue(blankCnt, "Count no longer found on mentions icon");
		omUI.logout();
		
		// As UserC, load component and login
		logger.strongStep("Load OrientMe and Log In as: " + testUserC.getDisplayName());
		omUI.goToOrientMe(testUserC, true);
		log.info("INFO: Click on mentions icon");
		// Clear count.		
		omUI.clickLink(OrientMeUIConstants.mentionsNotificationIcon);
		omUI.waitForPageLoaded(driver);
		Assert.assertFalse(omUI.isTextPresent(resourceStrings.get("community-mention-rand")), "Recent updates entry from the restricted community is not displayed on the screen");
		
		omUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Data population for comments test</B></li>
	 *<li>As UserA, create a community and add UserB as a member</li>
	 *</ul>
	 *@return map of resource created
	 * @throws Exception 
	 */
	private Map<String, String> filterTestDataPop() throws Exception {
		Map<String, String> resources = new HashMap<String, String>();
		String randomString1 = Helper.genStrongRand();
		String randomString2 = Helper.genStrongRand();
		String randomString3 = Helper.genStrongRand();
		Mentions mentions1 = MentionsBaseBuilder.buildBaseMentions(testUserB, apiProfilesTestUserB, serverUrl, testUserA.getDisplayName(), randomString1);
		Mentions mentions2 = MentionsBaseBuilder.buildBaseMentions(testUserC, apiProfilesTestUserC, serverUrl, testUserA.getDisplayName(), randomString2);
		Mentions mentions3 = MentionsBaseBuilder.buildBaseMentions(testUserB, apiProfilesTestUserB, serverUrl, testUserA.getDisplayName(), randomString3);

		log.info("(API) Add a status update with mention (" + testUserB.getDisplayName() + ")");		
		apiProfilesTestUserA.addMentionsStatusUpdate(mentions3);
		resources.put("status-update-rand", randomString3);
		
		log.info("(API) Create a public community as " + testUserB.getDisplayName());
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				getClass().getSimpleName() + randomString1, Access.PUBLIC);
		community = baseCommunity.createAPI(apiCommTestUserA);
		log.info("(API) Post a status update to the community as " + testUserA.getDisplayName() + " and mention " + testUserB.getDisplayName());
		CommunityEvents.addCommStatusUpdateWithMentions(community, apiCommTestUserA, apiProfilesTestUserA, mentions1);
		resources.put("community-public", testUserA.getDisplayName() + " @" + testUserB.getDisplayName() + " " + randomString1);
		resources.put("community-public-rand", randomString1);
		
		log.info("(API) Create a restricted community as " + testUserA.getDisplayName() + " with member " + testUserB.getDisplayName());
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				getClass().getSimpleName() + randomString2, Access.RESTRICTED);
		community = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUserA, apiCommTestUserA, testUserB);
		
		log.info("(API) Post a status update to the community as " + testUserA.getDisplayName() + " and mention " + testUserB.getDisplayName() + " and " + testUserC.getDisplayName());
		CommunityEvents.addCommStatusUpdateWithTwoMentions(community, apiCommTestUserB, apiProfilesTestUserB, mentions1, mentions2);
		resources.put("community-mention", testUserA.getDisplayName() + " @" + testUserB.getDisplayName() + " @" + testUserC.getDisplayName() + " " + randomString2);
		resources.put("community-mention-rand", randomString2);
		adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		return resources;
	}
}