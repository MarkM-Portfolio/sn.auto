package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_VisitorModel_HomePageView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_HomePageView.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testExternalUser;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);

	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Info: Test that different links like Home, Communities, Files are displayed when external user logged into homepage component</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Verify: Verify that different links like Home, Communities, Files are displayed on the top of page</B></li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void visitorModel_HomepageLinks() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		// Log in to Connections as a external user
		logger.strongStep("Log in to Connections as a external user");
		log.info("INFO: Log in to Connections as a external user");
		LoginEvents.loginToHomepage(ui, testExternalUser, false);

		// Verify that different links like Home, Communities, Files are displayed on the top of page
		log.info("INFO: Verify that different links like Home, Communities, Files are displayed on the top of page");
		logger.strongStep("Verify that different links like Home, Communities, Files are displayed on the top of page");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.homepage,
				HomepageUIConstants.Visitor_CommunitiesLinkMT, HomepageUIConstants.Visitor_FilesLinkMT}, null, true);
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user can see correct Home page view after logged in to application</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Select the Home page link - Verification point 1 and point 2</B></li>
	*<li><B>Step: Then Select 'Mentions' from left navigation- Verification point 3</B></li>
	*<li><B>Step: Then select 'My Notifications' from left navigation - Verification point 4</B></li>
	*<li><B>Step: Then select 'Action Required' from left navigation - Verification point 5</B></li>
	*<li><B>Step: Then select 'saved' from left navigation - Verification point 6</B></li>
	*<li><B>Verify: Verification point 1: Verify selecting the Home page link from top navigation takes the visitor to by default I'm Following view.</B></li>
	*<li><B>Verify: Verification point 2: Verify that by default, the following options appear in the left navigation panel: Updates -> with I'm Following, and Status Updates tabs</B></li>
	*<li><B>Verify: Verification point 3: Verify selecting the 'Mentions' from left navigation takes the visitor to the Mentions view.</B></li>
	*<li><B>Verify: Verification point 4: Verify selecting the 'My Notifications' from left navigation takes the visitor to the My Notifications view.</B></li>
	*<li><B>Verify: Verification point 5: Verify selecting the 'Action Required' from left navigation takes the visitor to the Action Required view.</B></li>
	*<li><B>Verify: Verification point 6: Verify selecting the 'Saved' from left navigation takes the visitor to the Saved view</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_HomepageNavigationLinks() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Log in to Connections home page as a external user
		log.info("INFO: Log in to Connections as a external user");
		logger.strongStep("Log in to Connections as a external user");
		LoginEvents.loginToHomepage(ui, testExternalUser, false);

		// Verify that the user has been redirected to the I'm Following view
		log.info("INFO: Verify that the user has been redirected to the I'm Following view.");
		logger.strongStep("Verify that the user has been redirected to the I'm Following view");
		UIEvents.verifyIMFollowingViewIsDisplayed(ui,driver);
		
		// Verify that by default, the following options appear in the left navigation panel: Updates -> with I'm Following, and Status Updates tabs
		log.info("INFO: Verify that by default, the following options appear in the left navigation panel: Updates -> with I'm Following, and Status Updates tabs.");
		logger.strongStep("Verify that by default, the following options appear in the left navigation panel: Updates -> with I'm Following, and Status Updates tabs.");
		UIEvents.verifyUpdatesIsDisplayed(ui, driver);

		// User will now click on the Mentions link
		log.info("INFO: Select the Mentions link.");
		logger.strongStep("Select the Mentions link");
		ui.clickLinkWithJavascript(HomepageUIConstants.AtMentions);

		// Verify that User has been redirected to the Mentions view
		log.info("INFO: Verify that User has been redirected to the Mentions view.");
		logger.strongStep("Verify that User has been redirected to the Mentions view");
		UIEvents.verifyMentionsIsDisplayed(ui, driver);

		// User will now click on the My Notifications link
		log.info("INFO: Select the My Notifications link");
		logger.strongStep("Select the My Notifications link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_MyNotifications);

		// Verify that User has been redirected to the My Notifications view
		log.info("INFO: Verify that User has been redirected to the My Notifications view.");
		logger.strongStep("Verify that User has been redirected to the My Notifications view");
		UIEvents.verifyMyNotificationsIsDisplayed(ui,driver);

		// User will now click on the Action Required link
		log.info("INFO: Select the Action Required link");
		logger.strongStep("Select the Action Required link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_ActionRequired);

		// Verify that User has been redirected to the Action Required view
		log.info("INFO: Verify that User has been redirected to the Action Required view.");
		logger.strongStep("Verify that User has been redirected to the Action Required view");
		UIEvents.verifyActionRequiredIsDisplayed(ui, driver);

		// User will now click on the Saved link
		log.info("INFO: Select the Saved link");
		logger.strongStep("Select the Saved link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_Saved);

		// Verify that User has been redirected to the Saved view
		log.info("INFO: Verify that User has been redirected to the Saved view.");
		logger.strongStep("Verify that User has been redirected to the Saved view");
		UIEvents.verifySavedIsDisplayed(ui, driver);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see the widgets 'Events' and 'Recommendations' in the right panel on Home page view after logged in to application</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Verify: Verify that different widgets like 'Events','Recommendations' are displayed in the right panel page</B></li>
	*<li><B>Verify: Verify the text 'There are no current recommendations for you.' displayed under Recommendations widget</B></li>
	*<li><B>Verify: Verify that no recommendations available under Recommendations widget</B></li>
	*</ul>
	*/

	@Test(groups = { "mtlevel2" })
	public void visitorModel_HomepageWidgets() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Log in to Connections as a external user
		logger.strongStep("Log in to Connections as a external user");
		log.info("INFO: Log in to Connections as a external user");
		LoginEvents.loginToHomepage(ui, testExternalUser, false);

		// Verify that To DO List and Events widgets are displayed on home page in right side pane
		log.info("INFO: Verify that Events widgets and Recommendations widgets are displayed on home page in right side pane");
		logger.strongStep("Verify that Events widgets and Recommendations widgets are displayed on home page in right side pane");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.Visitor_EventsMT, HomepageUIConstants.Visitor_RecommendationsMT}, null, true);

		// Verify the Recommendations widget with text under it
		log.info("INFO: Verify the Recommendations widget with 'There are no current recommendations for you.' text is present and no recommedations available under Recommendation widget");
		logger.strongStep("Verify the Recommendations widget with 'There are no current recommendations for you.' text is present and no recommedations available under Recommendation widget");
		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui,driver);
		
		ui.endTest();
	}			
}
