package com.ibm.conn.auto.tests.orientme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_OrientMe_TopUpdates_Entries extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_Status_Entries.class);
	private TestConfigCustom cfg;
	private OrientMeUI omUI;
	private HomepageUI homepageUI;
	private APICommunitiesHandler apiCommTestUserA;
	private APIProfilesHandler apiProfilesTestUserA, apiProfilesTestUserB;
	
	private User testUserA;
	private User testUserB;
	private String serverUrl;
	private ItmNavCnx8 itmNavCnx8;
	private Assert cnxAssert;
	
	List<Community> communities = new ArrayList<Community>();
	Map<String, String> resourceStrings = new HashMap<String, String>();
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		omUI = OrientMeUI.getGui(cfg.getProductName(), driver);
		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverUrl);
		
		apiCommTestUserA = new APICommunitiesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());	
		apiProfilesTestUserA = new APIProfilesHandler(serverUrl, 
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiProfilesTestUserB = new APIProfilesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		
		resourceStrings = topUpdatesTestDataPop();
		itmNavCnx8 = new ItmNavCnx8(driver);
		cnxAssert = new Assert(log);
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test message is shown in the Top updates tab in OrientMe when user was mentioned.</li>
	*<li><B>Step:</B>UserA creates a status update and mentions UserB</li>
	*<li><B>Step:</B>Log in as UserB and go to OrientMe</li>
	*<li><B>Verify:</B>Message about UserA mentioned UserB in a message is displayed in the Top Updates tab.</li>
	*/
	@Test(groups = {"level2", "cplevel2"})
	public void mentionInTopUpdatesTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		omUI.startTest();
		
		logger.strongStep("Log In to OrientMe and go to Top Updates as " + testUserB.getDisplayName());
		log.info("INFO: " + "Log In to OrientMe and go to Top Updates as " + testUserB.getDisplayName());
		LoginEvents.loginAndGoToOMTopUpdatesTab(homepageUI, omUI, testUserB, driver, false);
		
		// verify target messages are displayed in the tab
		Supplier<Void> tester = () -> {
			String title = testUserA.getDisplayName() + " added you to the " + resourceStrings.get("community_title") + " community";
			logger.strongStep("Verify message: '" + title + "' is found.");
			omUI.fluentWaitTextPresent(title);

			title = testUserA.getDisplayName() + " mentioned you in a message.";
			logger.strongStep("Verify message title: '" + title + "' is found.");
			omUI.fluentWaitTextPresent(title);
			String message = testUserA.getDisplayName() + " @" +  testUserB.getDisplayName() + " " + resourceStrings.get("status-update-rand");
			logger.strongStep("Verify message content: '" + message + "' is found.");
			omUI.fluentWaitTextPresent(message);
			return null;
		};
		omUI.verifyWithScrolling(tester);
		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test message is shown in the Top updates tab in OrientMe when user created a community.</li>
	*<li><B>Step:</B>As UserA, create a community and add UserB as a member.</li>
	*<li><B>Step:</B>Log in as UserA and go to OrientMe</li>
	*<li><B>Verify:</B>Message about UserA created a forum is displayed in the Top Updates tab.</li>
	*/
	@Test(groups = {"regression"})
	public void commForumMsgForOwnerInUpdatesTest() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		omUI.startTest();
		
		logger.strongStep("Log In to OrientMe and go to Top Updates as " + testUserA.getDisplayName());
		log.info("INFO: " + "Log In to OrientMe and go to Top Updates as " + testUserA.getDisplayName());
		LoginEvents.loginAndGoToOMTopUpdatesTab(homepageUI, omUI, testUserA, driver, false);
		
		// verify target message is displayed in the tab
		Supplier<Void> tester = () -> {
			String title = testUserA.getDisplayName() + " created a forum";
			logger.strongStep("Verify message title: '" + title + "' is found.");
			omUI.fluentWaitTextPresent(title);
			String message = resourceStrings.get("community_title");
			logger.strongStep("Verify message content: '" + message + "' is found.");
			omUI.fluentWaitTextPresent(message);
			return null;
		};
		omUI.verifyWithScrolling(tester);
		
		omUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validate Application is navigating to new UI Homepage while using '\social' on url</li>
	*<li><B>Step:</B>Load OrientMe Component</li>
	*<li><B>Step:</B>Log In to OrientMe</li>
	*<li><B>Verify:</B>Validate Top Updates Link is displayed on new UI</li>
	*/
	@Test(groups = {"cnx8ui-cplevel2"})
	public void validateComponentOMNavigationOnNewUIHomepage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		omUI.startTest();
		
		logger.strongStep("Load OrientMe Component");
		log.info("INFO: Load OrientMe Component");
		omUI.loadComponent(Data.getData().ComponentOrientMe);
		
		logger.strongStep("Log In to OrientMe" + testUserA.getDisplayName());
		log.info("INFO: " + "Log In to OrienMe " + testUserA.getDisplayName());
		omUI.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("Validate Top Updates Link is displayed on new UI");
		log.info("INFO: Validate Top Updates Link is displayed on new UI");
		itmNavCnx8.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 4) ;
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(HomepageUIConstants.topUpdatesLink)), "Top Updates Link is displayed on new UI");
		
		omUI.endTest();
	}
	

	
	/**
	 *<ul>
	 *<li><B>Data population for Top Updates test</B></li>
	 *<li>UserA creates a community and add UserB as a member</li>
	 *<li>UserA creates a status update and mentions UserB</li>
	 *Note: search index will NOT be kicked off because it should use ElasticSearch.
	 *</ul>
	 *@return map of resource created
	 */
	private Map<String, String> topUpdatesTestDataPop() {
		String randomString = Helper.genRandString(5);
		
		log.info("(API) Create a community as " + testUserA.getDisplayName() + " with member " + testUserB.getDisplayName());	
		BaseCommunity baseCommunity = new BaseCommunity.Builder("TopUpdates_" + randomString)
				.access(Access.RESTRICTED)
				.addMember(new Member(CommunityRole.MEMBERS,testUserB)).shareOutside(false)
				.approvalRequired(false)
				.build();		
		Community community = baseCommunity.createAPI(apiCommTestUserA);
		
		resourceStrings.put("community_title", community.getTitle());
		communities.add(community);
		
		log.info("(API) Add a status update as " + testUserA.getDisplayName() + " with mention " + testUserB.getDisplayName());	
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testUserB, apiProfilesTestUserB, serverUrl, testUserA.getDisplayName(), randomString);
		apiProfilesTestUserA.addMentionsStatusUpdate(mention);
		resourceStrings.put("status-update-rand", randomString);
		
		return resourceStrings;
	}
	
	
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		if (communities.size() > 0) {
			for (Community comm : communities)  {
				apiCommTestUserA.deleteCommunity(comm);
			}
		}
	}
}
