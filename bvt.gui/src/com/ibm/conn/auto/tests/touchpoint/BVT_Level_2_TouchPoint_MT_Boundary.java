package com.ibm.conn.auto.tests.touchpoint;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.TouchpointUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_TouchPoint_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_TouchPoint_MT_Boundary.class);
	private TouchpointUI ui;
	private ProfilesUI pui;
	private TestConfigCustom cfg;
	private User testUser_orgA,testUser_orgB,testUser;
	private String  serverURL_MT_orgB;
	private APICommunitiesHandler  apiHandler_OrgB;
	private CustomizerUI uiCnx7;
	private String appRegAppName = "Touchpoint";
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();	
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser("app_admin_users");
		} else {
			testUser = cfg.getUserAllocator().getAdminUser();
		}		
		uiCnx7 = new CustomizerUI(driver);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = TouchpointUI.getGui(cfg.getProductName(), driver);	
		pui = ProfilesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that added Tag in OrgB is not displayed on OrgA Add Your Interest Screen (Touchpoint)</li>
	 * <li><B>Step: </B>Load the component and login as OrgB User</li>
	 * <li><B>Step: </B>Navigating to 'My Profile' view</li>
	 * <li><B>Step: </B>Add a tag on OrgB MyProfile page and Log out</li>
	 * <li><B>Step: </B>Open Touchpoint and login as OrgA user</li> 
	 * <li><B>Step: </B>Ensure that existing user is on Welcome screen</li> 
	 * <li><B>Verify: </B>Verify user navigates to 'Add Your Interests' page</li> 
	 * <li><B>Step: </B>Type recently added OrgB Tag in search field that does not appear in the 'Suggested Interests'</li> 
	 * <li><B>Verify: </B>Verify that 'Create "org_bbb_TAG"' is visible in type ahead search results as this tag is not available on OrgA</li> 
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2"})
	public void orgBTagsOnAddYourInterestScreenOfOrgA() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		String addedTag = "org_bbb_TAG"+Helper.genDateBasedRand();

		// Load the component and login
		logger.strongStep("Load the component and login as OrgB User: " + testUser_orgB.getDisplayName());
		log.info("Load the component and login as OrgB User ");
		ui.loadComponent(serverURL_MT_orgB, Data.getData().ComponentProfiles);
		ui.login(testUser_orgB);

		// load the My Profile view
		logger.strongStep("Navigating to 'My Profile' view");
		log.info("Navigating to 'My Profile' view ");
		pui.myProfileView();

		// Add a tag
		logger.strongStep("Add a tag on OrgB MyProfile page and Log out");
		log.info("Add a tag on OrgB MyProfile page and Log out");
		pui.profilesAddATag(testUser_orgB, addedTag);
		ui.logout();
		driver.close();

		// Load component and login
		logger.strongStep("Open Touchpoint and login as OrgA user: " + testUser_orgA.getDisplayName());
		log.info("Open Touchpoint and login as OrgA user");
		ui.goToTouchpoint(testUser_orgA, false);

		// Ensure that existing user is on Welcome screen
		logger.strongStep("Ensure that existing user is on Welcome screen");
		log.info("Ensure that existing user is on Welcome screen");
		ui.checkScreenAndBringUserToWelcomeScreen();
	
		logger.strongStep("Verify user navigates to 'Add Your Interests' page");
		log.info("INFO: Verify user navigates to 'Add Your Interests' page");
		ui.goToAddYourInterests();
		
		logger.strongStep("Type recently added Tag in search field that does not appear in the 'Suggested Interests'");
		log.info("INFO: Type recently added Tag in search field that does not appear in the 'Suggested Interests'");
		ui.typeText(TouchpointUIConstants.searchBox, addedTag);
		
		logger.strongStep("Verify that 'Create "+addedTag +"'' is visible in type ahead search results as this tag is not available on OrgA");
		log.info("INFO: Verify that 'Create "+addedTag +"' is visible in type ahead search results");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.searchTypeaheadResult).getText(),"Create "+addedTag, "ERROR: Entered text in serachbox was not present in type ahead search results");
		
		// Return to welcome screen
		ui.returnToWelcomeScreenfromAddYourInterests();
		ui.endTest();

	}


	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test that an orgB user is not able to search from orgA over Touchpoint's FollowColleagues screen</li>
	 * <li><B>Step: </B>Open Touchpoint and login</li>
	 * <li><B>Step: </B>Go to 'Follow Colleagues' page</li>	
	 * <li><B>Info: </B>Click and Clear the search input box</li>	
	 * <li><B>Verify: </B>Enter OrgB User and validate the search result message</li>	
	 * </ul>
	 */
	@Test(groups = { "mtlevel2" })
	public void orgBUsersOnFollowColleaguesScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser_orgA.getDisplayName());
		ui.goToTouchpoint(testUser_orgA, false);

		// Ensure that existing user is on Welcome screen
		logger.strongStep("Ensure that existing user is on Welcome screen");
		ui.checkScreenAndBringUserToWelcomeScreen();

		logger.strongStep("Go to 'Follow Colleagues' page");
		ui.goToFollowColleagues();
		
		log.info("INFO: Click and Clear the search input box ");
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).click();
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).clear();
		
		logger.strongStep("Enter OrgB User and validate the search result message");
		log.info("INFO: Enter OrgB User and validate the search result message");
		ui.typeTextWithDelay(TouchpointUIConstants.searchForColleagues, testUser_orgB.getDisplayName());
		ui.fluentWaitElementVisible(TouchpointUIConstants.getSearchedResults);
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.getSearchedResults).getText(), "Sorry, we couldn't find any colleague matching your search term");

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowColleagues();
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that OrgB's community is not searchable at OrgA's Follow Communities screen (Touchpoint)</li>
	 * <li><B>Info: </B>API Handler for the communities is the orgB user</li>	
	 * <li><B>Step: </B>Create a community in orgB using API</li>	
	 * <li><B>Step: </B>Open Touchpoint and login as OrgA user</li>	
	 * <li><B>Info: </B>Ensure that existing user is on Welcome screen</li>	
	 * <li><B>Step: </B>Go to 'Follow Communities' page</li>	
	 * <li><B>Verify: </B>Verify that recently created Community in OrgB is not displayed at OrgA's Follow Community Screen</li>	
	 * <li><B>Step: </B>Delete community that was created in orgB</li>		
	 * </ul>
	 */
	
	@Test(groups = { "mtlevel2" })
	public void orgBCommOnFollowCommunitiesScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity orgbCom = new BaseCommunity.Builder("orgbPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).description("Test description for testcase " + testName).build();
		
		// Instantiate API Handler
		log.info("INFO: API Handler for the communities is the orgB user: " + testUser_orgB.getDisplayName());
		apiHandler_OrgB = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
				testUser_orgB.getPassword());
		
		//Create Communities
		logger.strongStep("Create a community in orgB using API");
		log.info("INFO: Create a community in orgB using API");
		Community community = orgbCom.createAPI(apiHandler_OrgB);

		// Log in OrgA and Load Touchpoint
		logger.strongStep("Open Touchpoint and login as OrgA user: " + testUser_orgA.getDisplayName());
		ui.goToTouchpoint(testUser_orgA, false);

		// Ensure that existing user is on Welcome screen
		log.info("Ensure that existing user is on Welcome screen");
		ui.checkScreenAndBringUserToWelcomeScreen();

		logger.strongStep("Go to 'Follow Communities' page");
		ui.goToFollowCommunities();

		// Validate that the community is not found
		logger.strongStep("Verify that recently created Community in OrgB is not displayed at OrgA's Follow Community Screen");
		Assert.assertFalse(ui.isCommFoundInSearchResult(orgbCom), "ERROR: Able to find Org'B community "+ orgbCom.getName() + " in search result");
		
		// Delete community that was created in orgB
		log.info("INFO: Delete community that was created in orgB");
		logger.strongStep("Delete community that was created in orgB");
		apiHandler_OrgB.deleteCommunity(community);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowCommunity();
		ui.endTest();
	}
	
	@Test(groups= {"tpconfig"})
	public void configureTouchPoint()
	{
		// check TouchPoint config in Appreg and make configuration
		TouchpointUI.touchPointConfig( testUser, appRegAppName, uiCnx7, cfg, driver);
	}
	
	@Test(groups= {"tpconfig"})
	public void unconfigureTouchPoint()
	{
		// check TouchPoint config in Appreg and make configuration
		TouchpointUI.touchPointUnConfig( testUser, appRegAppName, uiCnx7, cfg, driver);
	}
	
	
}
