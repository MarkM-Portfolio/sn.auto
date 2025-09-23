package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_VisitorModel_RecommendationWidget extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_RecommendationWidget.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testExternalUser;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the 'Recommendations' widget in right panel with text 'There are no current recommendations for you.' on each view in home page</B></li>
	*<li><B>Step: External User log in as a External User to home page- Verification point1 </B></li>
	*<li><B>Step: Then Select 'Mentions' from left navigation- Verification point1</B></li>
	*<li><B>Step: Then select 'My Notifications' from left navigation - Verification point1</B></li>
	*<li><B>Step: Then select 'Action Required' from left navigation - Verification point1</B></li>
	*<li><B>Step: Then select 'saved' from left navigation - Verification point1</B></li>
	*<li><B>Verify: Verification point1 :Verify user is able to see the 'Recommendations' widget in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be displayed under it</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_HomepageNavigationLinks() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Log in to Connections as a external user
		log.info("INFO: Log in to Connections as a external user");
		logger.strongStep("Log in to Connections as a external user");
		LoginEvents.loginToHomepage(ui, testExternalUser, false);

		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui, driver);

		// User will now click on the Mentions link
		log.info("INFO: Select the Mentions link.");
		logger.strongStep("Select the Mentions link");
		ui.clickLinkWithJavascript(HomepageUIConstants.AtMentions);

		// Verify the 'Recommendation' widget is displayed in right panel
		log.info("INFO: Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		logger.strongStep("Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui, driver);

		// User will now click on the My Notifications link
		log.info("INFO: Select the My Notifications link");
		logger.strongStep("Select the My Notifications link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_MyNotifications);

		// Verify the 'Recommendation' widget is displayed in right panel
		log.info("INFO: Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		logger.strongStep("Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui, driver);

		// User will now click on the Action Required link
		log.info("INFO: Select the Action Required link");
		logger.strongStep("Select the Action Required link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_ActionRequired);

		// Verify the 'Recommendation' widget is displayed in right panel
		log.info("INFO: Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		logger.strongStep("Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui, driver);

		// User will now click on the Saved link
		log.info("INFO: Select the Saved link");
		logger.strongStep("Select the Saved link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_Saved);

		// Verify the 'Recommendation' widget is displayed in right panel
		log.info("INFO: Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");
		logger.strongStep("Verify that Recommendations' widget is displayed in right panel with text 'There are no current recommendations for you.' and no recommendations entries should be available.");		
		UIEvents.verifyVisitorRecommendationWidgetIsDisplayed(ui, driver);

		ui.endTest();
	}
}

