package com.ibm.conn.auto.tests.surveys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.SurveysUI;

public class Smoke_RO_Surveys extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Surveys.class);
	private SurveysUI ui;
	private CommunitiesUI commui;
	private TestConfigCustom cfg;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = SurveysUI.getGui(cfg.getProductName(), driver);
		commui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Check the CreateSurvey form loads successfully</li>
	 * 
	 * <li><B>Step:</B>Log in and nav to pre-existing Community</li>
	 * <li><B>Step:</B>Using left nav, go to Surveys</li>
	 * <li><B>Step:</B>Click Create a new Survey button</li>
	 * <li><B>Step:</B>Enter name for survey and click continue</li>
	 * <li><B>Verify:</B>The form for adding questions loads</li>
	 * </ul>
	 */
	@Test(groups = { "smoke" })
	public void testUILoad() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue)
				.description("Test for Survey" + testName)
				.anonResponse(false).build();

		// Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Checking to see if community exists");
		boolean communityExists = commui.communityExist(Data.getData().productionCommunityName);
		if(!communityExists) 
			Assert.fail("Prerequisite community '" + Data.getData().productionCommunityName + "' does not exits.");
		
		log.info("INFO: Open the community");
		commui.openCommunity(Data.getData().productionCommunityName);
		
		// Click on the widget link in the navigation
		log.info("INFO: Select surveys from left navigation menu");
		Community_LeftNav_Menu.SURVEYS.select(ui);
		
		// survey creation
		log.info("INFO: Starting Survey creation process to access form");
		ui.createSurvey(survey);
		ui.fluentWaitTextPresent(survey.getName());
		Assert.assertTrue(
				ui.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),
				"ERROR: Survey was not created successfuly");

	}

}
