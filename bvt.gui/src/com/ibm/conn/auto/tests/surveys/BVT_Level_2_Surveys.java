package com.ibm.conn.auto.tests.surveys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.SurveysUI;
import com.ibm.conn.auto.webui.SurveysUI.MoreLink;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.appobjects.base.BaseSurveyQuestion;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Surveys extends SetUpMethods2 {

	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Surveys.class);
	private CommunitiesUI commui;
	private TestConfigCustom cfg;
	private SurveysUI ui;
	private HomepageUI hui;
	private User testUser, testUser1;
	private APICommunitiesHandler apiOwner;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private String serverURL;

	@BeforeClass(alwaysRun = true)
	public void setUpclass() {
		// Initialize the configuration	
		cfg = TestConfigCustom.getInstance();
		
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		
		ui = SurveysUI.getGui(cfg.getProductName(), driver);
		hui= HomepageUI.getGui(cfg.getProductName(), driver);
		commui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Survey Lifecycle</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Take survevy</li>
	 *<li><B>Verify:</B> View survey results</li>
	 *<li><B>Step:</B> Copy survey</li>
	 *<li><B>Step:</B> Export survey</li>
	 *<li><B>Step:</B> Delete survey</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	
	@Test(groups = { "smokecloud" })
	public void checkSurveyLifecycle() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String randvalue = Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(testName
				+ randvalue).tags(Data.getData().commonTag + randvalue)
				.access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder(
				"Question A", BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
				.addOption(
						new BaseSurveyQuestion.Option("Answer A 1",
								"Answer A 1"))
				.addOption(
						new BaseSurveyQuestion.Option("Answer A 2",
								"Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue)
				.description("Test for Survey" + testName).questions(questions)
				.anonResponse(false).build();

		BaseSurvey surveyCopy = new BaseSurvey.Builder(testName + randvalue
				+ "Copy").description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// add widget SURVEYS
		log.info("INFO: Add SURVEYS and FEATUREDSURVEYS widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS);

		// Load component and login
		logger.strongStep("Load communities and Login:"+testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);

		// Click on the widget link in the navigation
		logger.strongStep("Select surveys from left navigation menu");
		log.info("INFO: Select surveys from left navigation menu");
		Community_LeftNav_Menu.SURVEYS.select(ui);

		// survey creation
		logger.strongStep("Create survey");
		log.info("INFO: Creating survey");
		ui.createSurvey(survey);
		ui.fluentWaitTextPresent(survey.getName());
		Assert.assertTrue(
				ui.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),
				"ERROR: Survey was not created successfuly");

		// Add question to survey
		logger.strongStep("Add Question by question type");
		log.info("INFO: Adding question by question type");
		logger.weakStep("Verify Questions are added to survey");
		Assert.assertTrue(ui.addQuestionsByQuestionType(question),
				"ERROR: Issue adding questions to the survey");

		// save survey
		logger.strongStep("Save Survey");
		log.info("INFO: Save survey");
		ui.saveSurvey();
		Assert.assertTrue(
				ui.fluentWaitElementVisible(SurveysUI.surveySuccessImg),
				"ERROR: The survey was not saved successfully");

		// Start survey
		logger.strongStep("Start a Survey");
		log.info("INFO:Start survey");
		ui.startSurvey(survey);
		Assert.assertTrue(ui.fluentWaitElementVisible(ui
				.getSurveyLinkInList(survey.getName())),
				"ERROR: Survey not started successfully");
		
		driver.navigate().refresh();

		// Take survey
		logger.strongStep("Take survey");
		log.info("INFO:Take simple survey");
		ui.clickSurveyMoreOption(survey, MoreLink.TAKE_SURVEY);
		ui.takeSimpleSurvey(survey);
		Assert.assertTrue(ui.fluentWaitTextPresent("Completed Today at"),
				"ERROR: Survey not completed successfully");
		 
		driver.navigate().refresh();
		
		// View survey results
		logger.strongStep("view survey results");
		log.info("INFO: Viewing survey results");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);
		Assert.assertTrue(ui.viewSurveyResults(survey),
				"ERROR: Issues viewing the survey Results");

		log.info("INFO: Copying survey");
		ui.copySurvey(survey, surveyCopy);
		driver.executeScript("scroll(0,0);");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui
				.getSurveyLinkInList(surveyCopy.getName())),
				"Error: The Survey was not copied successfully");
		logger.strongStep("Copy Survey");
		log.info("INFO: Starting and Stopping the copied survey");
		ui.clickSurveyMoreOption(surveyCopy, MoreLink.EDIT);
		ui.startSurvey(surveyCopy);
		ui.clickSurveyMoreOption(surveyCopy, MoreLink.EDIT);
		ui.stopSurvey(surveyCopy);
		logger.strongStep("Validate if survey stopped to update in feed");
		log.info("INFO: Navigating to Homepage to ensure surveyStopped update is in feed");
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		Assert.assertTrue(
				ui.fluentWaitPresent(ui.getSurveyStoppedUpdate(surveyCopy)),
				"ERROR: Survey not stopped successfully (Survey stopped update not in feed)");

		log.info("INFO Returning to Community");
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		community.navViaUUID(commui);
		logger.weakStep("Add survey to Featured survey widget");
		log.info("INFO: Add survey to Featured Survey widget");
		Assert.assertTrue(ui.selectSurveyForFeaturedSurvey(survey),
				"ERROR: Unable to set survey as Featured Survey");

		logger.weakStep("Confirm export survey diaglog appears");
		log.info("INFO: Confirm the export Survey dialog appears");
		Community_LeftNav_Menu.SURVEYS.select(ui);
		ui.exportSurvey(surveyCopy);
		
		logger.strongStep("Delete copied survey");
		log.info("INFO: Deleting the copied Survey");
		ui.deleteSurvey(surveyCopy);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(surveyCopy.getName()),"ERROR: The Survey was not deleted successfully");

		//Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}

	private void goToAnotherPageIfWidgetAddedNotPresent(String widgetAdded) {
		driver.turnOffImplicitWaits();
		if (!driver.isElementPresent(widgetAdded)) {
			log.info("INFO: More than one page of apps found, navigating to another page");
			ui.clickLink(CommunitiesUIConstants.widgetPaletteNavButton);
		}
		driver.turnOnImplicitWaits();
	}

	@AfterClass(alwaysRun = true)
	private void performCleanUp() {

		// Remove all of the communities created during the tests
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		for (Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}

	private void navigateToSurveysWidget() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String randvalue = Helper.genDateBasedRandVal();

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test surveys gets added" + Data.getData().commonName).build();

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// add widget SURVEYS
		logger.strongStep("Add 'Surveys' and 'Featured Surveys' to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS);

		// Load component and login
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);

		// Select 'Select 'Surveys' from top nav bar menu
		logger.strongStep("Select 'Surveys' from top nav bar menu");
		Community_TabbedNav_Menu.SURVEYS.select(ui,2);

		// Put community in Map for deletion
		communitiesForDeletion.put(comAPI, apiOwner);
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify that user is able to add 'Surveys' widget in community</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step: </B> Log in to Communities component</li>
	 *<li><B>Step: </B> Navigate to community created above</li>
	 *<li><B>Step:</B> From the Overview page, click Community Actions > Add Apps > Featured Survey</li>
	 *<li><B>Verify:</B> Verify the 'Add Surveys Widget' dialog should display </li>
	 *<li><B>Verify:</B> Verify the 'Add Surveys Widget' dialog display with header 'Add Surveys Widget'</li>
	 *<li><B>Verify:</B> Verify the 'Add Surveys Widget' dialog displays with text 'The "Featured Survey" widget requires the associated "Surveys" widget. Both widgets will be added to your community.</li>
	 *<li><B>Verify:</B> Verify the 'Add Surveys Widget' dialog displays with 'OK' button</li>
	 *<li><B>Step:</B> On the 'Add Surveys Widget' dialog, click OK</li>
	 *<li><B>Verify:</B> Verify the Add Apps palette will display '2 applications added'.</li>
	 *<li><B>Step:</B> Verify 'Surveys Added' and 'Featured Survey Added' should display on the add apps palette</li>
	 *<li><B>Step:</B> Close the Add Apps palette by clicking on the 'X'</li>
	 *<li><B>Verify:</B> Verify Featured Survey summary widget should appear in the right column on the Overview page</li>
	 *<li><B>Verify:</B> Verify Surveys summary widget should appear in the middle column on the Overview page</li>
	 *<li><B>Verify:</B> Verify Surveys should appear on the top nav bar if not visible on the top nav bar, click on 'More'. Surveys should be listed on the drop-down menu</li>
	 *</ul>
	 */

	@Test(groups = { "level2" })
	public void addSurveysWidget() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String randvalue = Helper.genDateBasedRandVal();

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test surveys gets added" + testName).build();

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		commui.changingCommunityLandingPage(apiOwner, comAPI);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);

		// Go to add widget palette
		logger.strongStep("From the Overview page, click Community Actions > Add Apps");
		Com_Action_Menu.ADDAPP.select(commui);

		// Select 'Featured Survey' Widget
		logger.strongStep("Select 'Featured Survey' Widget");
		commui.selectWidget(BaseWidget.FEATUREDSURVEYS);

		logger.strongStep("Verify the 'Add Surveys Widget' dialog should display");
		Assert.assertTrue(driver.isElementPresent(SurveysUI.addSurveyPopUp));

		logger.strongStep("Verify the 'Add Surveys Widget' dialog display with header 'Add Surveys Widget'");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.addSurveyPopUpHeader).getText(), "Add Surveys Widget");

		logger.strongStep("Verify the 'Add Surveys Widget' dialog display with text 'The 'Featured Survey' widget requires the associated 'Surveys' widget. Both widgets will be added to your community.");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.addSurveyPopUpText).getText(),"The \"Featured Survey\" widget requires the associated \"Surveys\" widget. Both widgets will be added to your community.");

		logger.strongStep("Verify the 'Add Surveys Widget' dialog displays with 'OK' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.okButton));

		// Select 'OK' button
		logger.strongStep("On the 'Add Surveys Widget' dialog, click OK");
		ui.clickLink(CommunitiesUIConstants.okButton);

		// Verify 'Featured Survey Added' should display on the add apps palette
		logger.strongStep("Verify 'Featured Survey Added' should display on the add apps palette");
		goToAnotherPageIfWidgetAddedNotPresent(SurveysUI.FeatureSurveysAdded);
		Assert.assertTrue(driver.isElementPresent(SurveysUI.FeatureSurveysAdded),BaseWidget.FEATUREDSURVEYS.getTitle() + "  link: is not present in community");

		// Verify 'Surveys Added' should display on the add apps palette
		logger.strongStep("Verify 'Surveys Added' should display on the add apps palette");
		goToAnotherPageIfWidgetAddedNotPresent(SurveysUI.SurveysAdded);
		Assert.assertTrue(driver.isElementPresent(SurveysUI.SurveysAdded),BaseWidget.SURVEYS.getTitle() + "  link: present in community");

		// close the widget palette
		logger.strongStep("Close the Add Apps palette by clicking on the 'X'");
		driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();

		// Surveys summary widget should appear in the middle column on the Overview page
		logger.strongStep("Surveys summary widget should appear in the middle column on the Overview page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.surveysSummaryWidget));

		// Featured Survey summary widget should appear in the right column on the Overview page
		logger.strongStep("Featured Survey summary widget should appear in the right column on the Overview page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.featuredSummarySurveyWidget));

		// Surveys should appear on the top nav bar if not visible on the top nav bar, click on 'More'. Surveys should be listed on the drop-down menu
		logger.strongStep("Surveys should appear on the top nav bar if not visible on the top nav bar, click on 'More'. Surveys should be listed on the drop-down menu");
		List<String> widgets = commui.getTopNavItems(false);
		boolean found = false;
		for (String widgetTitle : widgets) {
			log.info("Widget title is:" + widgetTitle);
			if (widgetTitle.equals("surveys")) {
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);

		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify community 'Surveys' widget page</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> [API] Create a community via API</li>
	 *<li><B>Step: </B> [API] Add 'Surveys' and 'Featured Surveys' to community</li>
	 *<li><B>Step: </B> Log in to Communities component</li>
	 *<li><B>Step: </B> Navigate to community created above</li>
	 *<li><B>Step:</B> Select 'Surveys' from top nav bar or go to 'More' dropdown if not listed in top nav bar</li>
	 *<li><B>Verify:</B> Verify the Surveys full widget page should be displayed</li>
	 *<li><B>Verify:</B> Verify the 'Create Survey' button should display on Surveys widget page</li>
	 *<li><B>Verify:</B> Verify the 'Import Survey' button should display on Surveys widget page</li>
	 *<li><B>Verify:</B> Verify text 'Use surveys to share community opinions and feedback.' should display on Surveys widget page</li>
	 *<li><B>Verify:</B> Verify text 'There are no surveys currently available for this community.' should display on Surveys widget page</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void verifySurveyWidgetPage() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// navigate to community 'Surveys' widget page
		navigateToSurveysWidget();

		// Verify Surveys full widget page with different buttons and texts
		logger.strongStep("Verify the full surveys widget page should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.surveysPage));
		logger.strongStep("Verify the 'Create Survey' button should display on Surveys widget page");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.createSurvey));
		logger.strongStep("Verify the 'Import Survey' button should display on Surveys widget page");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.importSurvey));
		logger.strongStep("Verify text 'Use surveys to share community opinions and feedback.' should display on Surveys widget page");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.surveyText1).getText(),SurveysUI.text1);
		logger.strongStep("Verify text 'There are no surveys currently available for this community.' should display on Surveys widget page");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.surveyText2).getText(),SurveysUI.text2);
		
		ui.endTest();
	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify 'Create Surveys' page</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> [API] Create a community via API</li>
	 *<li><B>Step: </B> [API] Add 'Surveys' and 'Featured Surveys' to community</li>
	 *<li><B>Step: </B> Log in to Communities component</li>
	 *<li><B>Step: </B> Navigate to community created above</li>
	 *<li><B>Step:</B> Select 'Surveys' from top nav bar or go to 'More' dropdown if not listed in top nav bar</li>
	 *<li><B>Step:</B> Select 'Create Survey' button</li>
	 *<li><B>Verify:</B> Verify header 'Create Survey' should be displayed</li>
	 *<li><B>Verify:</B> Verify label '*Name:' should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Name' input field should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Description' input field should be displayed</li>
	 *<li><B>Verify:</B> Verify label 'Description:' should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Continue' button should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Cancel' button should be displayed</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void verifyCreateSurveyPage() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		// navigate to community 'Surveys' widget page
		navigateToSurveysWidget();
		
		// Select 'Create Survey'
		logger.strongStep("Select 'Create Survey' button");
		ui.clickLink(SurveysUI.createSurveyButton);

		// Verify 'Create Survey' page with different fields, labels and buttons 
		logger.strongStep("Verify header 'Create Survey' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.createSurveysHeader).getText(),SurveysUI.createSurveysHeaderText);
		logger.strongStep("Verify label '*Name:' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.createSurveyNameLabel).getText(), "*Name:");
		logger.strongStep("Verify 'Name' input field should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.createSurveyNameField));
		logger.strongStep("Verify 'Description' input field should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.createSurveyDescField));
		logger.strongStep("Verify label 'Description:' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.createSurveyDescLabel).getText(), "Description:");
		logger.strongStep("Verify 'Continue' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.createSurveyContinueButton));
		logger.strongStep("Verify 'Cancel' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.cancelButtonSurveyPage));

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Survey page</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> [API] Create a community via API</li>
	 *<li><B>Step: </B> [API] Add 'Surveys' and 'Featured Surveys' to community</li>
	 *<li><B>Step: </B> Log in to Communities component</li>
	 *<li><B>Step: </B> Navigate to community created above</li>
	 *<li><B>Step:</B> Select 'Surveys' from top nav bar or go to 'More' dropdown if not listed in top nav bar</li>
	 *<li><B>Step:</B> Create Survey by putting required details</li>
	 *<li><B>Verify:</B> Verify 'Add Question' button should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Save' button should be disabled</li>
	 *<li><B>Verify:</B> Verify 'Start' button should be disabled</li>
	 *<li><B>Verify:</B> Verify 'More Actions' button should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Cancel' button should be displayed</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void verifySurveyPage() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		String randvalue = Helper.genDateBasedRandVal();

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue)
				.description("Test for Survey" + testName)
				.anonResponse(false).build();
		
		// navigate to community 'Surveys' widget page
		navigateToSurveysWidget();

		// survey creation
		logger.strongStep("Create survey");
		log.info("INFO: Creating survey");
		ui.createSurvey(survey);
		ui.fluentWaitTextPresent(survey.getName());
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),"ERROR: Survey was not created successfuly");

		// Verify Survey page after creating particular survey
		logger.strongStep("Verify 'Add Question' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addSurveyQuestionButton));
		logger.strongStep("Verify 'Save' button should be disabled");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.saveSurveyButton).getAttribute("class").contains("Disabled"));
		logger.strongStep("Verify 'Start' button should be disabled");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.startSurveyButton).getAttribute("class").contains("Disabled"));
		logger.strongStep("Verify 'More Actions' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.moreAction));
		logger.strongStep("Verify 'Cancel' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.cancelSurveyButton));

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Take Survey</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Save survey</li>
	 *<li><B>Step:</B> Start survey</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Verify:</B> View survey results</li>
	 *<li><B>Verify:</B> Verify Summary Text is displayed</li>
	 *<li><B>Step:</B> Logging out from testUser </li>
	 *<li><B>Step:</B> Load component and login with testUser1 </li>
	 *<li><B>Step:</B> Navigating to Community</li>
	 *<li><B>Verify:</B> Surveys summary widget should appear in the middle column on the Overview page</li>
	 *<li><B>Step:</B> Select surveys from Survey summary widget</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Verify:</B> Verify the message 'Thank you for participating.' is displayed</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void takeSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// add widget SURVEYS
		log.info("INFO: Add SURVEYS and FEATUREDSURVEYS widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS);

		// Load component and login
		logger.strongStep("Load communities and Login:"+testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);

		log.info("Changing the Landing Page of Community as Overview, if default is Highlights");
		commui.changingCommunityLandingPage(apiOwner, comAPI);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);
		
		logger.strongStep("Click on Create survey Link from Survey summary widget ");
		log.info("INFO: Click on Create survey Link from Survey summary widget ");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		Assert.assertTrue(driver.isElementPresent(SurveysUI.createSurveyFromWidget), "Create Survey Link is not visible");
		ui.clickLinkWithJavascript(SurveysUI.createSurveyFromWidget);

		// survey creation
		logger.strongStep("Create survey");
		log.info("INFO: Creating survey");
		ui.createSurveyFromWidget(survey);
		ui.fluentWaitTextPresent(survey.getName());
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),
				"ERROR: Survey was not created successfuly");

		// Add question to survey
		logger.strongStep("Add Question by question type");
		log.info("INFO: Adding question by question type");
		logger.weakStep("Verify Questions are added to survey");
		Assert.assertTrue(ui.addQuestionsByQuestionType(question),
				"ERROR: Issue adding questions to the survey");

		// save survey
		logger.strongStep("Save Survey");
		log.info("INFO: Save survey");
		ui.saveSurvey();
		Assert.assertTrue(ui.fluentWaitElementVisible(SurveysUI.surveySuccessImg),
				"ERROR: The survey was not saved successfully");

		// Start survey
		logger.strongStep("Start a Survey");
		log.info("INFO:Start survey");
		ui.startSurvey(survey);
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getSurveyLinkInList(survey.getName())),
				"ERROR: Survey not started successfully");
		
		driver.navigate().refresh();

		// Take survey
		logger.strongStep("Take survey");
		log.info("INFO:Take simple survey");
		ui.clickSurveyMoreOption(survey, MoreLink.TAKE_SURVEY);
		ui.takeSimpleSurvey(survey);
		Assert.assertTrue(ui.fluentWaitTextPresent("Completed Today at"),"ERROR: Survey not completed successfully");
		 
		driver.navigate().refresh();
		
		// View survey results
		logger.strongStep("view survey results");
		log.info("INFO: Viewing survey results");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);
		Assert.assertTrue(ui.viewSurveyResults(survey), "ERROR: Issues viewing the survey Results");
		logger.strongStep("Verify Summary Text is displayed");
		log.info("INFO: Verify Summary Text is displayed");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);
		ui.verifySummaryTab(survey);
		
		log.info("INFO: Logging out from testUser");
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser1 ");
		logger.strongStep("Load communities and Login:" + testUser1.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser1);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		commui.changingCommunityLandingPage(apiOwner, comAPI);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);

		// Surveys summary widget should appear in the middle column on the Overview page
		logger.strongStep("Surveys summary widget should appear in the middle column on the Overview page");
		log.info("INFO: Surveys summary widget should appear in the middle column on the Overview page");
		Assert.assertTrue(driver.isElementPresent(SurveysUI.surveysSummaryWidget));

		logger.strongStep("Select surveys from Survey summary widget ");
		log.info("INFO: Select surveys from Survey summary widget ");
		driver.executeScript("arguments[0].scrollIntoView(true);", driver.getElements(SurveysUI.surveyTitleOnCommOverview).get(0).getWebElement());
		ui.clickLinkWait(SurveysUI.surveyTitleOnCommOverview);

		// Take survey
		logger.strongStep("Take survey");
		log.info("INFO:Take simple survey");
		ui.takeSimpleSurvey(survey);
		Assert.assertTrue(ui.fluentWaitTextPresent("Completed Today at"), "ERROR: Survey not completed successfully");
		logger.strongStep(" Verify the message 'Thank you for participating.' is displayed");
		log.info("INFO:Verify the message 'Thank you for participating.' is displayed");
		driver.getSingleElement(SurveysUI.surveyTitle).click();
		Assert.assertEquals(driver.getSingleElement(SurveysUI.thankyouMsgforMemberofComm).getText(),"Thank you for participating.");

		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Copy Survey</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Save survey</li>
	 *<li><B>Verify:</B> Verify Survey is Saved Successfully</li>
	 *<li><B>Step:</B> Start survey</li>
	 *<li><B>Verify:</B> Verify Survey is Started Successfully</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Verify:</B> Verify Survey is Completed Successfully</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> View all' Link from Survey widget and application shall navigate to Surveys</li>
	 *<li><B>Step:</B> Launch Copy Survey dialog box through More link and Click on 'Cancel' button</li>
	 *<li><B>Info:</B> Copying survey</li>
	 *<li><B>Step:</B> Starting and Stopping the copied survey</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void copySurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		BaseSurvey surveyCopy = new BaseSurvey.Builder(testName + randvalue + "Copy")
				.description("Test for Survey" + testName).questions(questions).anonResponse(false).build();
		
		// Create Survey to Take survey common navigation
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);

		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		logger.strongStep("Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		log.info("INFO: Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();
		
		logger.strongStep("Launch Copy Survey dialog box through More link and Click on 'Cancel' button");
		log.info("INFO: Launch Copy Survey dialog box through More link and Click on 'Cancel' button");
		ui.clickCancelButtonOncopySurvey(survey, surveyCopy);
		driver.navigate().refresh();

		logger.strongStep("Copying survey");
		log.info("INFO: Copying survey");
		ui.copySurvey(survey, surveyCopy);
		driver.executeScript("scroll(0,0);");
		logger.strongStep("Verify Survey is Copied Successfully");
		log.info("INFO: Verify Survey is Copied Successfully");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getSurveyLinkInList(surveyCopy.getName())),
				"Error: The Survey was not copied successfully");
		logger.strongStep("Starting and Stopping the copied survey");
		log.info("INFO: Starting and Stopping the copied survey");
		ui.clickSurveyMoreOption(surveyCopy, MoreLink.EDIT);
		ui.startSurvey(surveyCopy);
		ui.clickSurveyMoreOption(surveyCopy, MoreLink.EDIT);
		ui.stopSurvey(surveyCopy);
		logger.strongStep("Validate if survey stopped to update in feed");
		log.info("INFO: Navigating to Homepage to ensure surveyStopped update is in feed");
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		Assert.assertTrue(ui.fluentWaitPresent(ui.getSurveyStoppedUpdate(surveyCopy)),
				"ERROR: Survey not stopped successfully (Survey stopped update not in feed)");
		
		// Cleanup
		logger.strongStep("Delete Community");
		log.info("INFO: Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Export Survey</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Save survey</li>
	 *<li><B>Verify:</B> Verify Survey is Saved Successfully</li>
	 *<li><B>Step:</B> Start survey</li>
	 *<li><B>Verify:</B> Verify Survey is Started Successfully</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Verify:</B> Verify Survey is Completed Successfully</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Click on 'View all' Link from Survey widget and application shall navigate to Surveys</li>
	 *<li><B>Step:</B> Confirm export survey dialog appears with 'Cancel' and 'Export' button</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void exportSurveyDialog() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		//Create Survey to Take survey common navigation
		fromCreateSurveyToTakeSurveyNavigation(survey,question,community);
		
		logger.strongStep("Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		logger.strongStep("Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		log.info("INFO: Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();

		logger.strongStep("Confirm export survey diaglog appears with 'Cancel' and 'Export' button");
		log.info("INFO: Confirm the export Survey dialog appears");
		ui.cancelAndExportSurvey(survey);

		// Cleanup
		logger.strongStep("Delete Community");
		log.info("INFO: Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	private void fromCreateSurveyToTakeSurveyNavigation(BaseSurvey survey, BaseSurveyQuestion question, BaseCommunity community) throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// add widget SURVEYS
		log.info("INFO: Add SURVEYS and FEATUREDSURVEYS widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS);

		// Load component and login
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		commui.changingCommunityLandingPage(apiOwner, comAPI);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);

		ui.fluentWaitPresent(SurveysUI.createSurveyFromWidget);
		logger.strongStep("Click on Create survey Link from Survey summary widget ");
		log.info("INFO: Click on Create survey Link from Survey summary widget ");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		ui.fluentWaitElementVisible(SurveysUI.createSurveyFromWidget);
		driver.getSingleElement(SurveysUI.createSurveyFromWidget).click();

		// survey creation
		logger.strongStep("Create survey");
		log.info("INFO: Creating survey");
		ui.createSurveyFromWidget(survey);
		ui.fluentWaitTextPresent(survey.getName());
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),
				"ERROR: Survey was not created successfuly");

		// Add question to survey
		logger.strongStep("Add Question by question type");
		log.info("INFO: Adding question by question type");
		logger.weakStep("Verify Questions are added to survey");
		Assert.assertTrue(ui.addQuestionsByQuestionType(question), "ERROR: Issue adding questions to the survey");

		// save survey
		logger.strongStep("Save Survey");
		log.info("INFO: Save survey");
		ui.saveSurvey();
		Assert.assertTrue(ui.fluentWaitElementVisible(SurveysUI.surveySuccessImg),
				"ERROR: The survey was not saved successfully");

		// Start survey
		logger.strongStep("Start a Survey");
		log.info("INFO:Start survey");
		ui.startSurvey(survey);
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getSurveyLinkInList(survey.getName())),
				"ERROR: Survey not started successfully");

		driver.navigate().refresh();

		// Take survey
		logger.strongStep("Take survey");
		log.info("INFO:Take simple survey");
		ui.clickSurveyMoreOption(survey, MoreLink.TAKE_SURVEY);
		ui.takeSimpleSurvey(survey);
		Assert.assertTrue(ui.fluentWaitTextPresent("Completed Today at"), "ERROR: Survey not completed successfully");

		driver.navigate().refresh();

	}

	private String getMessage(String locator) {
		String finalMessage = "";
		String actualWelcomeMsg = driver.getSingleElement(locator).getText();
		String a[] = actualWelcomeMsg.split("\\s+");
		for (int i = 0; i < a.length; i++) {
			log.info("Word is " + a[i]);
			finalMessage = finalMessage + a[i] + " ";
		}

		return finalMessage;
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify that user is able to create surveys successfully</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step: </B> Log in to Communities component</li>
	 *<li><B>Step: </B> Navigate to community created above</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Select 'Add Question' button</li>
	 *<li><B>Verify:</B> Verify Question dialogue box should appear </li>
	 *<li><B>Verify:</B> Verify question name field should appear on the box</li>
	 *<li><B>Verify:</B> Verify question name label 'Question:' should appear on the box</li>
	 *<li><B>Verify:</B> Verify question type dropdown should appear on the box</li>
	 *<li><B>Verify:</B> Verify question type dropdown label should appear on the box</li>
	 *<li><B>Verify:</B> Verify OK button should appear on the box</li>
	 *<li><B>Verify:</B> Verify Cancel button should appear on the box</li>
	 *<li><B>Verify:</B> Verify tab 'Display Item' should appear on the box</li>
	 *<li><B>Step:</B> Typing Question in 'Question Name' field</li>
	 *<li><B>Step:</B> Selecting question type from dropdown</li>
	 *<li><B>Verify:</B> Verify 'Option' should appear after selecting question type</li>	 
	 *<li><B>Step:</B> Typing Option value in first option</li>
	 *<li><B>Verify:</B> Verify that typed values for first option should appear at both places in 'Displayed Value' and 'Saved Value'</li>
	 *<li><B>Step:</B> Add one more option by selecting '+'</li>
	 *<li><B>Verify:</B> Verify new row of option gets added</li>
	 *<li><B>Step:</B> Typing Option value in second option box</li>
	 *<li><B>Verify:</B> Verify that typed values for second option should appear at both places in 'Displayed Value' and 'Saved Value'</li>
	 *<li><B>Step:</B> Selecting 'OK' button</li>
	 *<li><B>Verify:</B> Verify warning message 'Changes to this survey have not been saved.' should be displayed</li>
	 *<li><B>Step:</B> Verify text 'Add one or more questions to your survey, then click Start to start collecting responses.' should be displayed</li>
	 *<li><B>Verify:</B> Verify that added question view should be displayed properly</li>
	 *<li><B>Verify:</B> Verify that added question should be displayed properly</li>
	 *<li><B>Verify:</B> Verify that added question text should be displayed properly</li>
	 *<li><B>Verify:</B> Verify that added options count under question should be displayed properly</li>
	 *<li><B>Step:</B> Save Survey</li>
	 *<li><B>Verify:</B> Verify that Survey should be saved successfully</li>
	 *<li><B>Verify:</B> Verify 'Save' button should be disabled after saving surveys</li>
	 *<li><B>Step:</B> Select 'Start' button</li>
	 *<li><B>Verify:</B> Verify 'Start' survey pop up</li>
	 *<li><B>Verify:</B> Verify the pop up header 'Start' should be displayed</li>
	 *<li><B>Verify:</B> Verify the pop up message should be displayed</li>
	 *<li><B>Verify:</B> Verify 'End Date' field should be displayed</li>
	 *<li><B>Verify:</B> Verify 'End Date:' label should be displayed</li>
	 *<li><B>Verify:</B> Verify 'Save Survey anonymously' checkbox should be displayed</li>
	 *<li><B>Verify:</B> Verify button 'Start' should be displayed at bottom on the pop up</li>
	 *<li><B>Step:</B> Select survey end date as any future date</li>
	 *<li><B>Step:</B> Select 'Start' button on pop up</li>
	 *<li><B>Verify:</B> Verify that survey should be started successfully</li>
	 *<li><B>Verify:</B> Verify that end date selected from start pop up should be displayed correctly under survey name</li>
	 *<li><B>Step:</B> Select 'More' link from the far right of survey names</li>
	 *<li><B>Verify:</B> Verify action link 'Take Survey' should be displayed</li>
	 *<li><B>Verify:</B> Verify action link 'View Results' should be displayed</li>
	 *<li><B>Verify:</B> Verify action link 'Edit' should be displayed</li>
	 *<li><B>Verify:</B> Verify action link 'Copy' should be displayed</li>
	 *<li><B>Verify:</B> Verify action link 'Export Survey' should be displayed</li>
	 *<li><B>Verify:</B> Verify action link 'Delete' should be displayed</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void createSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String randvalue = Helper.genDateBasedRandVal();
		String optionValue1 = "Autumn";
		String optionValue2 = "Winter";

		// navigate to community 'Surveys' widget page
		navigateToSurveysWidget();

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.anonResponse(false).build();

		// survey creation
		logger.strongStep("Create survey");
		log.info("INFO: Creating survey");
		ui.createSurvey(survey);
		ui.fluentWaitTextPresent(survey.getName());

		logger.strongStep("Selecting 'Add question' button");
		ui.clickLink(SurveysUI.addSurveyQuestionButton);
		ui.switchToFrame(SurveysUI.addQuestionIFrame, SurveysUI.addQuestionNameField);

		// add question dialogue box
		log.info("INFO: Verifying add survey dialogue box");
		logger.strongStep("Verify Question dialogue box should appear");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionDialogueBox));
		logger.strongStep("Verify question name field should appear on the box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionNameField));
		logger.strongStep("Verify question name label 'Question:' should appear on the box");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.addQuestionNameLabel).getText(), "Question:");
		logger.strongStep("Verify question type dropdown should appear on the box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionTypeDropdown));
		logger.strongStep("Verify question type dropdown label '* Type:' should appear on the box");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.addQuestionTypeDropdownLabel).getText(), "* Type:");
		logger.strongStep("Verify 'OK' button should appear on the box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionOKButton));
		logger.strongStep("Verify 'Cancel' button should appear on the box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionCancelButton));
		logger.strongStep("Verify tab 'Display Item' button should appear on the box");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.addQuestionDisplayItemTab).getText(), "DISPLAY ITEM");

		log.info("INFO: Typing Question in 'Question Name' field");
		logger.strongStep("Typing Question in 'Question Name' field.");
		String surveyQue = "What is your favorite season";
		ui.typeText(SurveysUI.addQuestionNameField, surveyQue);

		log.info("INFO: Selecting question type from dropdown");
		logger.strongStep("Selecting question type from dropdown");
		driver.getSingleElement(SurveysUI.addQuestionTypeDropdown).useAsDropdown().selectOptionByVisibleText("Multiple choice: One answer only");

		log.info("INFO: Verify 'Option' should appear after selecting question type");
		logger.strongStep("Verify 'Option' should appear after selecting question type");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addQuestionOptionSection));

		log.info("INFO: Typing Option value in first option");
		logger.strongStep("Typing Option value in first option");
		driver.getSingleElement(SurveysUI.option1DisplayedBox).clear();
		ui.typeText(SurveysUI.option1DisplayedBox, optionValue1);

		log.info("INFO: Verify that typed values for first option should appear at both places in 'Displayed Value' and 'Saved Value'");
		logger.strongStep("Verify that typed values for first option should appear at both places in 'Displayed Value' and 'Saved Value'");
		log.info("Option1 displayed value is : "+ driver.getSingleElement(SurveysUI.option1SavedBox).getAttribute("value") + "and saved value is:"+ driver.getSingleElement(SurveysUI.option1SavedBox).getAttribute("value"));
		Assert.assertEquals(driver.getSingleElement(SurveysUI.option1DisplayedBox).getAttribute("value"), optionValue1);
		Assert.assertEquals(driver.getSingleElement(SurveysUI.option1SavedBox).getAttribute("value"), optionValue1);

		log.info("INFO: Add one more option by selecting '+'");
		logger.strongStep("Add one more option by selecting '+'");
		ui.clickLink(SurveysUI.addQuestionAddOption);

		log.info("INFO: Add one more option by selecting '+'");
		logger.strongStep("Verify new row of option should get added");
		Assert.assertTrue(ui.isElementVisible(SurveysUI.addQuestionSecondOptionRow));

		log.info("INFO: Verify that typed values for second option should appear at both places in 'Displayed Value' and 'Saved Value'");
		logger.strongStep("Typing Option value in second option");
		driver.getSingleElement(SurveysUI.option2DisplayedBox).clear();
		ui.typeText(SurveysUI.option2DisplayedBox, optionValue2);
		logger.strongStep("Verify that typed values for second option should appear at both places in 'Displayed Value' and 'Saved Value'");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.option2DisplayedBox).getAttribute("value"), optionValue2);
		Assert.assertEquals(driver.getSingleElement(SurveysUI.option2SavedBox).getAttribute("value"), optionValue2);

		logger.strongStep("Selecting 'OK' button");
		log.info("INFO: Selecting 'OK' button");
		ui.clickLink(SurveysUI.addQuestionOKButton);
		ui.switchToTopFrame();

		log.info("Warning message is " + driver.getSingleElement(SurveysUI.surveyWarningMessage).getText());
		logger.strongStep("Verify warning message 'Changes to this survey have not been saved.' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.surveyWarningMessage).getText(),"Changes to this survey have not been saved.");
		log.info("INFO: Verify text 'Add one or more questions to your survey, then click Start to start collecting responses.' should be displayed");
		logger.strongStep("Verify text 'Add one or more questions to your survey, then click Start to start collecting responses.' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.quetionText(survey.getName())).getText(),"Add one or more questions to your survey, then click Start to start collecting responses.");

		ui.switchToFrame(SurveysUI.addQuestionIFrame, SurveysUI.addedSurveyQueView);
		log.info("value is: " + ui.isElementPresent(SurveysUI.addedSurveyQueView));
		log.info("INFO: Verify that added question view should be displayed properly");
		logger.strongStep("Verify that added question view should be displayed properly");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addedSurveyQueView));

		log.info("INFO: Verify that added question text should be displayed properly");
		logger.strongStep("Verify that added question text should be displayed properly");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.surveyQuestion).getText(), surveyQue);
		List<Element> options = driver.getVisibleElements(SurveysUI.surveyQueOptions);
		log.info("INFO: Verify that added options count under question should be displayed properly");
		logger.strongStep("Verify that added options count under question should be displayed properly");
		Assert.assertEquals(options.size(), 2);

		// save survey
		ui.switchToTopFrame();
		logger.strongStep("Save Survey");
		log.info("INFO: Save survey");
		ui.saveSurvey();
		log.info("INFO: Verify that Survey should be saved successfully");
		logger.strongStep("Verify that Survey should be saved successfully");
		Assert.assertTrue(ui.fluentWaitElementVisible(SurveysUI.surveySuccessImg),"ERROR: The survey was not saved successfully");

		log.info("INFO: Verify 'Save' button should be disabled after saving surveys");
		logger.strongStep("Verify 'Save' button should be disabled after saving surveys");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.saveSurveyButton).getAttribute("class").contains("Disabled"));

		// start survey
		log.info("INFO: Verifying 'Start' survey dialogue box");
		logger.strongStep("Select 'Start' button");
		ui.clickLink(SurveysUI.startSurveyButton);
		logger.strongStep("Verify 'Start' survey pop up");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.startSurveyPopUp));
		logger.strongStep("Verify the pop up header 'Start' should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.startSurveyPopUpHeader).getText(), "Start");
		logger.strongStep("Verify the pop up message should be displayed");
		Assert.assertEquals(getMessage(SurveysUI.msgOnstartSurveyPopUp),"Start the survey to collect responses. When you start the survey, it becomes active in the community until you stop the survey. You can stop the survey at any time. ");
		logger.strongStep("Verify 'End Date' field should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.startSurveyEndDateField));
		logger.strongStep("Verify 'End Date:' label should be displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.startSurveyPopUpEndDateLabel).getText(), "End Date:");
		logger.strongStep("Verify 'Save Survey anonymously' checkbox should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.startSurveyPopUpSaveChekbox));
		logger.strongStep("Verify button 'Start' should be displayed at bottom on the pop up");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.startSurveyConfirmButton));

		logger.strongStep("Select survey end date as any future date");
		Calendar target = driver.getBrowserDatetime();
		target.add(Calendar.MONTH, 6);
		log.info("INFO: Random Future Date: " + new SimpleDateFormat("d MMM yyyy").format(target.getTime()));
		ui.pickDojoDate(SurveysUI.DatePicker_Surveys_InputField, target, true);
		String actualDate = driver.getSingleElement(SurveysUI.startSurveyEndDate).getAttribute("value");

		log.info("INFO: Select 'Start' button on pop up");
		logger.strongStep("Select 'Start' button on pop up");
		ui.clickLink(SurveysUI.startSurveyConfirmButton);
		log.info("Selected end date is: "+actualDate);

		SimpleDateFormat month_date = new SimpleDateFormat("MMM d ", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(actualDate);
		String month_name = month_date.format(date);
		log.info("Month name is: " + month_name);

		logger.strongStep("Verify that survey should be started successfully");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getSurveyLinkInList(survey.getName())),"ERROR: Survey not started successfully");

		ui.fluentWaitPresent(SurveysUI.getSurveyEndDate(survey.getName()));
		String endDateString = driver.getSingleElement(SurveysUI.getSurveyEndDate(survey.getName())).getText();
		logger.strongStep("Verify that end date selected from start pop up should be displayed correctly under survey name");
		Assert.assertTrue(endDateString.contains(month_name.trim()));

		logger.strongStep("Select 'More' link from the far right of survey name");
		ui.clickMoreLink(survey);

		log.info("INFO: verifying actions links under 'More' link");
		logger.strongStep("Verify action link 'Take Survey' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.TAKE_SURVEY)));
		logger.strongStep("Verify action link 'View Results' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.VIEW_RESULTS)));
		logger.strongStep("Verify action link 'Edit' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.EDIT)));
		logger.strongStep("Verify action link 'Copy' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.COPY)));
		logger.strongStep("Verify action link 'Export Survey' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.EXPORT_SURVEY)));
		logger.strongStep("Verify action link 'Delete' should be displayed");
		Assert.assertTrue(ui.isElementPresent(ui.getSurveyMoreOption(survey, MoreLink.DELETE)));

		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Delete Survey</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Save survey</li>
	 *<li><B>Step:</B> Start survey</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Click on 'View all' Link</li>
	 *<li><B>Step:</B> Click on 'View all' Link from Survey widget and application shall navigate to Surveys</li>
	 *<li><B>Step:</B> Delete survey</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Locate 'Select a Survey to Display'link on Featured Survey Widget</li>
	 *<li><B>Step:</B> Verify no surveys are available on Featured Survey Widget</li>
	 *<li><B>Step:</B> Locate Survey Widget</li>
	 *<li><B>Step:</B> Verify no survey available message is displayed on Survey Widget</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void deleteSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		// Create Survey to Take survey common navigation
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		logger.strongStep("Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		log.info("INFO:Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();
		
		logger.strongStep("Delete survey");
		log.info("INFO:Delete survey");
		ui.deleteSurveyFromSurveys(survey, MoreLink.DELETE);

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();
		
		logger.strongStep("Locate 'Select a Survey to Display'link on Featured Survey Widget");
		log.info("INFO: Locate 'Select a Survey to Display'link on Featured Survey Widget");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.noSurveyMsgFeaturedSurvey).get(0).getWebElement());
		
		logger.strongStep("Verify no surveys are available on Featured Survey Widget");
		log.info("INFO: Verify no surveys are available on Featured Survey Widget");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.noSurveyMsgFeaturedSurvey).getText(),SurveysUI.noSurveyTxtFeaturedSurvey);
		
		logger.strongStep("Locate Survey Widget");
		log.info("INFO: Locate Survey Widget");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		
		logger.strongStep("Verify no survey available message is displayed on Survey Widget");
		log.info("INFO: Verify no survey available message is displayed on Survey Widget");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.noSurveyMsg).getText(),SurveysUI.noSurveyAvlText);

		// Cleanup
		logger.strongStep("Delete Community");
		log.info("INFO:Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify surveys 'Responses' view</li>
	 * <li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 * <li><B>Step:</B>testUser take the survey in community added with 'Surveys' widget created by testUser via API</li>
	 * <li><B>Step: </B>Logging out from testUser</li>
	 * <li><B>Step: </B>Load component and login with testUser1</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>testUser1 take the survey and completes it successfully</li>
	 * <li><B>Step: </B>Logging out from testUser1</li>
	 * <li><B>Step: </B>Load component and login with testUser</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>Locate the surveys widget on overview page and select link 'View All'</li>
	 * <li><B>Step: </B>Select 'More' link of created survey</li>
	 * <li><B>Step: </B>Select 'View Results'</li>
	 * <li><B>Verify: </B>Verify 'Summary' tab should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Responses' tab should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Search' button should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Refresh' tab should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Summary' tab should be selected by default after navigating to view results</li> *
	 * <li><B>Verify: </B>Verify text 'viewAs' should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Pie Chart' link should be select by default</li>
	 * <li><B>Verify: </B>Verify 'Bar Chart' link should be displayed</li>
	 * <li><B>Verify: </B>Verify 'Data Table' link should be displayed</li>
	 * <li><B>Verify: </B>Verify pie chart view should be visible by default</li>
	 * </ul>
	 */

	@Test(groups = { "level2" })
	public void surveysResponsesTab() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		logger.strongStep(testUser.getDisplayName()+" take the survey in community added with 'Surveys' widget created by testUser via API");
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);
		
		logger.strongStep("Logging out from testUser");
		log.info("INFO: Logging out from testUser");
		commui.sleep(500);
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser1 ");
		logger.strongStep("Load communities and Login:" + testUser1.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser1);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);

		log.info("INFO: testUser1 take the survey and completes it successfully");
		logger.strongStep("testUser1 take the survey and completes it successfully");
		log.info("INFO: "+testUser1.getDisplayName()+" take the survey and completes it successfully");
		ui.fluentWaitPresent(SurveysUI.surveyTitleOnCommOverview);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.surveyTitleOnCommOverview).get(0).getWebElement());
		ui.clickLinkWait(SurveysUI.surveyTitleOnCommOverview);
		ui.takeSimpleSurvey(survey);
		ui.fluentWaitTextPresent("Completed Today at");
		
		logger.strongStep("Logging out from testUser1");
		log.info("INFO: Logging out from testUser1");
		commui.sleep(500);
		LoginEvents.logout(hui);
		
		log.info("INFO: Load component and login with testUser");
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		log.info("Locate the surveys widget on overview page and select link 'View All'");
		logger.strongStep("Locate the surveys widget on overview page and select link 'View All'");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();
		
		log.info("Select 'View Results'");
		logger.strongStep("Select 'View Results'");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);

		ui.switchToFrame(SurveysUI.viewResultsiFrame, SurveysUI.summaryTab);
		log.info("Verify 'Summary' tab should be displayed");
		logger.strongStep("Verify 'Summary' tab should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.summaryTab));
		
		log.info("Verify 'Responses' tab should be displayed");
		logger.strongStep("Verify 'Responses' tab should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.responsesTab));
		
		log.info("Verify 'Search' button should be displayed");
		logger.strongStep("Verify 'Search' button should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.searchButton));
		
		log.info("Verify 'Refresh' tab should be displayed");
		logger.strongStep("Verify 'Refresh' tab should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.refreshButton));

		log.info("Verify 'Summary' tab should be selected by default after navigating to view results");
		logger.strongStep("Verify 'Summary' tab should be selected by default after navigating to view results");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.summaryTab).getAttribute("aria-selected").equalsIgnoreCase("true"));

		log.info("Verify text 'viewAs' should be displayed");
		logger.strongStep("Verify text 'viewAs' should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewAs));
		
		log.info("Verify 'Pie Chart' link should be select by default");
		logger.strongStep("Verify 'Pie Chart' link should be select by default");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.pieChartLink).getAttribute("class").contains("Selected"));
		
		log.info("Verify 'Bar Chart' link should be displayed");
		logger.strongStep("Verify 'Bar Chart' link should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.barChartLink));
		
		log.info("Verify 'Data Table' link should be displayed");
		logger.strongStep("Verify 'Data Table' link should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.dataTableLink));
		
		log.info("Verify pie chart view should be visible by default");
		logger.strongStep("Verify pie chart view should be visible by default");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.pieChartView));
		
		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
		
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify surveys 'Responses' search view</li>
	 * <li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 * <li><B>Step:</B>testUser take the survey in community added with 'Surveys' widget created by testUser via API</li>
	 * <li><B>Step: </B>Logging out from testUser</li>
	 * <li><B>Step: </B>Load component and login with testUser1</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>testUser1 take the survey and completes it successfully</li>
	 * <li><B>Step: </B>Logging out from testUser1</li>
	 * <li><B>Step: </B>Load component and login with testUser</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>Locate the surveys widget on overview page and select link 'View All'</li>
	 * <li><B>Step: </B>Select 'More' link of created survey</li>
	 * <li><B>Step: </B>Select 'View Results'</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Verify: </B>Verify Search dialogue box should appear</li>
	 * <li><B>Verify: </B>Verify 'Select Item' dropdown should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify 'Choose Operator' dropdown should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify search input field should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify add icon should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify remove icon should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify radio button 'And' should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify radio button 'Or' should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify 'Search' button should appear on Search dialogue box</li>
	 * <li><B>Verify: </B>Verify 'Cancel' button should appear on Search dialogue box</li>
	 * <li><B>Step: </B>Select dropdown'Select Item'</li>
	 * <li><B>Verify: </B>Verify 'Select Item' dropdown should contains options 'Created By','Last Updated By','Create Date','Last Update Date' and question</li>
	 * <li><B>Step: </B>Select question from 'Select Item' dropdown</li>
	 * <li><B>Step: </B>Select 'Equals' from 'Choose Operator' dropdown</li>
	 * <li><B>Step: </B>Enter testUser1's survey response from take survey section</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Verify: </B>Verify that testUser1's responses should be returned</li>
	 * <li><B>Verify: </B>Verify that testUser1's responses should be displayed in pie chart view by default</li>
	 * <li><B>Verify: </B>Verify text 'From 2 submission(s) there were 2 response(s)' should appear below pie chart</li>
	 * <li><B>Verify: </B>Verify 'Filter Enabled' field should displayed next to refresh button</li>
	 * <li><B>Verify: </B>Verify 'Clear Filters' field should displayed next to 'Filter Enabled'</li>
	 * <li><B>Step: </B>Select 'Clear Filters'</li>
	 * <li><B>Verify: </B>Verify 'Filter Enabled' field should not be visible on the page</li>
	 * <li><B>Step: </B>Select 'Bar chart' link from 'View As' options</li>
	 * <li><B>Verify: </B>Verify responses should be displayed in 'Bar chart' view</li>
	 * <li><B>Step: </B>Select 'data Table' link from 'View As' options</li>
	 * <li><B>Verify: </B>Verify responses should be displayed in 'Data Table' view</li>
	 * <li><B>Step: </B>Select 'Responses' tab</li>
	 * <li><B>Verify: </B>Verify view should show he users info in table format with columns like 'ID','Author','Last Updated',and question</li>
	 * <li><B>Verify: </B>Verify that user's name should be displayed one for each user</li>
	 * <li><B>Verify: </B>Verify that 2 'ID' value should be c as '0' and '1' one for each user</li>
	 * <li><B>Verify: </B>Verify that 2 last updated value should be displayed one for each user</li>
	 * <li><B>Verify: </B>Verify that question's response should be displayed one for each user</li>
	 * </ul>
	 */

	@Test(groups = { "level2" })
	public void surveysResponsesSearch() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		logger.strongStep(testUser.getDisplayName()+" take the survey in community added with 'Surveys' widget created by testUser via API");
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);
		
		logger.strongStep("Logging out from testUser");
		log.info("INFO: Logging out from testUser");
		commui.sleep(500);
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser1 ");
		logger.strongStep("Load communities and Login:" + testUser1.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser1);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);

		log.info("INFO: testUser1 take the survey and completes it successfully");
		logger.strongStep("testUser1 take the survey and completes it successfully");
		log.info("INFO: "+testUser1.getDisplayName()+" take the survey and completes it successfully");
		ui.fluentWaitPresent(SurveysUI.surveyTitleOnCommOverview);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.surveyTitleOnCommOverview).get(0).getWebElement());
		ui.clickLinkWait(SurveysUI.surveyTitleOnCommOverview);
		ui.takeSimpleSurvey(survey);
		ui.fluentWaitTextPresent("Completed Today at");
		
		logger.strongStep("Logging out from testUser1");
		log.info("INFO: Logging out from testUser1");
		commui.sleep(500);
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser");
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		log.info("Locate the surveys widget on overview page and select link 'View All'");
		logger.strongStep("Locate the surveys widget on overview page and select link 'View All'");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();
		
		log.info("Select 'View Results'");
		logger.strongStep("Select 'View Results'");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);

		ui.switchToFrame(SurveysUI.viewResultsiFrame, SurveysUI.summaryTab);
		log.info("Verify 'Summary' tab should be displayed");
		logger.strongStep("Verify 'Summary' tab should be displayed");
		ui.fluentWaitPresent(SurveysUI.summaryTab);

		log.info("Select 'Search' button");
		logger.strongStep("Select 'Search' button");
		ui.clickLink(SurveysUI.searchButton);
	
		log.info("Verify Search dialogue box should appear");
		logger.strongStep("Verify Search dialogue box should appear");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.searchDialogue));
		
		log.info("Verify 'Select Item' dropdown should appear on Search dialogue box");
		logger.strongStep("Verify 'Select Item' dropdown should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.selectItemDropdowns));
		
		log.info("Verify 'Choose Operator' dropdown should appear on Search dialogue box");
		logger.strongStep("Verify 'Choose Operator' dropdown should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.chooseOperator));
		
		log.info("Verify search input field should appear on Search dialogue box");
		logger.strongStep("Verify search input field should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.searchInputBox));
		
		log.info("Verify add icon should appear on Search dialogue box");
		logger.strongStep("Verify add icon should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.addIcon));
		
		log.info("Verify remove icon should appear on Search dialogue box");
		logger.strongStep("Verify remove icon should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.removeIcon));
		
		log.info("Verify radio button 'And' should appear on Search dialogue box");
		logger.strongStep("Verify radio button 'And' should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.radioButtonAnd));
		
		log.info("Verify radio button 'Or' should appear on Search dialogue box");
		logger.strongStep("Verify radio button 'Or' should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.radioButtonOr));
		
		log.info("Verify 'Search' button should appear on Search dialogue box");
		logger.strongStep("Verify 'Search' button should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.advSearchButton));
		
		log.info("Verify 'Cancel' button should appear on Search dialogue box");
		logger.strongStep("Verify 'Cancel' button should appear on Search dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.advSearchCancelButton));

		log.info("Select dropdown'Select Item'");
		logger.strongStep("Select dropdown 'Select Item'");
		ui.clickLink(SurveysUI.selectItemDropdowns);
		List<Element> options = driver.getVisibleElements(SurveysUI.selectItemDropDownOptions);
		List<String> optionsFromDropdown = new ArrayList<>();
		List<String> expectedOptions = new ArrayList<>();
		expectedOptions.add("Created By");
		expectedOptions.add("Last Updated By");
		expectedOptions.add("Create Date");
		expectedOptions.add("Last Update Date");
		expectedOptions.add(question.getQuestion());

		for (Element option : options) {
			String optionName = option.getText();
			log.info("Option name is: "+optionName);
			optionsFromDropdown.add(optionName);
		}

		logger.strongStep("Verify 'Select Item' dropdown should contains options 'Created By','Last Updated By','Create Date','Last Update Date' and question");
		log.info("actual options are:" + optionsFromDropdown);
		log.info("expected options are:" + expectedOptions);
		Assert.assertTrue(optionsFromDropdown.equals(expectedOptions));

		log.info("INFO: Select question from 'Select Item' dropdown");
		logger.strongStep("Select question from 'Select Item' dropdown");
		ui.clickLink(SurveysUI.selectItemOption(question.getQuestion()));
		
		log.info("INFO: Select 'Equals' from 'Choose Operator' dropdown");
		logger.strongStep("Select 'Equals' from 'Choose Operator' dropdown");
		ui.clickLink(SurveysUI.chooseOperator);
		ui.clickLink(SurveysUI.chooseOperatorOption("Equals"));
	
		log.info("INFO: Enter "+testUser1.getDisplayName()+"'s survey response "+question.getOptions().get(0).display+" from take survey section");
		logger.strongStep("Enter testUser1's survey response from take survey section");
		ui.typeText(SurveysUI.searchInputBox, question.getOptions().get(0).display);
		
		log.info("INFO: Select 'Search' button");
		logger.strongStep("Select 'Search' button");
		ui.clickLink(SurveysUI.advSearchButton);
		
		log.info("INFO: Verify that testUser1's responses should be returned");
		logger.strongStep("Verify that testUser1's responses should be returned");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.questionText).getText().equals(question.getQuestion()));
		
		log.info("INFO: Verify that "+testUser1.getDisplayName()+"'s responses should be displayed in pie chart view by default");
		logger.strongStep("Verify that testUser1's responses should be displayed in pie chart view by default");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.pieChartView));
		
		log.info("INFO: Verify text is"+driver.getSingleElement(SurveysUI.chartInfo).getText());
		logger.strongStep("Verify text 'From 2 submission(s) there were 2 response(s)' should appear below pie chart");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.chartInfo).getText().equals("From 2 submission(s) there were 2 response(s)"));
		
		log.info("INFO:Verify 'Filter Enabled' field should displayed next to refresh button");
		logger.strongStep("Verify 'Filter Enabled' field should displayed next to refresh button");
		Assert.assertTrue(ui.isElementVisible(SurveysUI.filtersEnabled));
		
		log.info("INFO: Verify 'Clear Filters' field should displayed next to 'Filter Enabled'");
		logger.strongStep("Verify 'Clear Filters' field should displayed next to 'Filter Enabled'");
		Assert.assertTrue(ui.isElementVisible(SurveysUI.clearFilters));
		
		log.info("INFO: Select 'Clear Filters'");
		logger.strongStep("Select 'Clear Filters'");
		ui.clickLinkWithJavascript(SurveysUI.clearFilters);
		
		log.info("INFO: Verify 'Filter Enabled' field should not be visible on the page");
		logger.strongStep("Verify 'Filter Enabled' field should not be visible on the page");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementVisible(SurveysUI.filtersEnabled));
		driver.turnOnImplicitWaits();
		
		log.info("INFO: Select 'Bar chart' link from 'View As' options");
		logger.strongStep("Select 'Bar chart' link from 'View As' options");
		ui.clickLinkWithJavascript(SurveysUI.barChartLink);
		
		log.info("INFO: Verify responses should be displayed in 'Bar chart' view");
		logger.strongStep("Verify responses should be displayed in 'Bar chart' view");
		Assert.assertTrue(ui.isElementVisible(SurveysUI.barChartView));
		
		log.info("INFO: Select 'data Table' link from 'View As' options");
		logger.strongStep("Select 'data Table' link from 'View As' options");
		ui.clickLinkWithJavascript(SurveysUI.dataTableLink);
		
		log.info("INFO: Verify responses should be displayed in 'Data Table' view");
		logger.strongStep("Verify responses should be displayed in 'Data Table' view");
		Assert.assertTrue(ui.isElementVisible(SurveysUI.dataTableView));
		
		log.info("INFO: Select 'Responses' tab");
		logger.strongStep("Select 'Responses' tab");
		ui.clickLinkWithJavascript(SurveysUI.responsesTabLink);
		ui.waitForPageLoaded(driver);
		
		ui.fluentWaitElementVisible(SurveysUI.author);
		log.info("INFO: Verify view should show he users info in table format with columns like 'ID','Author','Last Updated',and question");
		logger.strongStep("Verify view should show the users info in table format with columns like 'ID','Author','Last Updated',and question");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.author).getText().equals("Author"));
		Assert.assertTrue(driver.getSingleElement(SurveysUI.lastUpdated).getText().equals("Last Updated"));
		Assert.assertTrue(driver.getSingleElement(SurveysUI.id).getText().equals("ID"));
		Assert.assertTrue(driver.getSingleElement(SurveysUI.question).getText().equals(question.getQuestion()));
		
		log.info("INFO: Verify that user's name should be displayed for one for each user");
		logger.strongStep("Verify that user's name should be displayed for one for each user");
		verifyValuesEachUserRows(SurveysUI.authorName, testUser.getDisplayName(), testUser1.getDisplayName(),2);
		
		log.info("INFO: Verify that 2 'ID' value should be displayed as '0' and '1' one for each user");
		logger.strongStep("Verify that 'ID' value should be displayed as '0' and '1' one for each user");
		verifyValuesEachUserRows(SurveysUI.idValue, "0", "1",2);
		
		log.info("INFO: Verify that 2 last updated value should be displayed one for each user");
		logger.strongStep("Verify that last updated value should be displayed one for each user");
		List<Element> lastUpdatedValue=driver.getVisibleElements(SurveysUI.lastUpdatedValue);
		Assert.assertTrue(lastUpdatedValue.size()==2);
		
		log.info("INFO: Verify that question's response should be displayed one for each user");
		logger.strongStep("Verify that uestion's response should be displayed one for each user");
		verifyValuesEachUserRows(SurveysUI.response, question.getOptions().get(0).display,question.getOptions().get(0).display,2);
		
		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}

	private void verifyValuesEachUserRows(String locator, String valueToBeVerified1, String valueToBeVerified2, int count) {
		List<Element> fieldValueEle=driver.getVisibleElements(locator);
		List<String> fieldValues=new ArrayList<>();
		for (Element ele : fieldValueEle) {
			String questionValue=ele.getText();
			log.info("Value is: "+questionValue);
			fieldValues.add(questionValue);
		}
		log.info("value is: "+fieldValues);
		Assert.assertTrue(fieldValues.contains(valueToBeVerified1));
		log.info("value is: "+fieldValues.contains(valueToBeVerified2));
		Assert.assertTrue(fieldValues.contains(valueToBeVerified2));
		Assert.assertTrue(fieldValueEle.size()==count);		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify search 'Responses' view</li>
	 * <li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 * <li><B>Step:</B>testUser take the survey in community added with 'Surveys' widget created by testUser via API</li>
	 * <li><B>Step: </B>Logging out from testUser</li>
	 * <li><B>Step: </B>Load component and login with testUser1</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>testUser1 take the survey and completes it successfully </li>
	 * <li><B>Step: </B>Logging out from testUser1</li>
	 * <li><B>Step: </B>Load component and login with testUser</li>
	 * <li><B>Step: </B>Navigating to Community</li>
	 * <li><B>Step: </B>Locate the surveys widget on overview page and select link 'View All'</li>
	 * <li><B>Step: </B>Select 'More' link of created survey</li>
	 * <li><B>Step: </B>Select 'View Results'</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Step: </B>Select 'Responses' tab</li>
	 * <li><B>Step: </B>Select 'Customize' button</li>
	 * <li><B>Verify: </B>Verify 'View Properties' dialogue box should be displayed</li>
	 * <li><B>Verify: </B>Verify the different elements present on 'View Properties' dialogue box</li>
	 * <li><B>Step: </B>Select 'Cancel' button</li>
	 * <li><B>Verify: </B>Verify 'View Properties' dialogue box should be disappeared</li>
	 * <li><B>Step: </B>Select 'Customize' button again</li>
	 * <li><B>Step: </B>Uncheck the checkbox 'Show ID' on 'View Properties' dialogue box</li>
	 * <li><B>Step: </B>Clicking 'OK' button</li>
	 * <li><B>Verify: </B>Verify the column 'ID' in responses view should be disappeared</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Step: </B>Click on '+' i.e add icon</li>
	 * <li><B>Verify: </B>Verify filter count get added by 1</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Step: </B>Select dropdown'Select Item'</li>
	 * <li><B>Step: </B>Select option 'Created By' from 'Select Item' dropdown </li>
	 * <li><B>Step: </B>Select 'Starts with' option from 'Choose Operator' dropdown</li>
	 * <li><B>Step: </B>Enter testUser1's first name who took the survey</li>
	 * <li><B>Step: </B>Select 'Search' button</li>
	 * <li><B>Verify: </B>Verify that responses of user matching with filter criteria should be returned</li>
	 * <li><B>Step: </B>Select 'Export Data' button</li>
	 * <li><B>Verify: </B>Verify 'Export Data' dialogue box should be displayed</li>
	 * <li><B>Verify: </B>Verify the different elements present on 'Export Data' dialogue box</li>
	 * <li><B>Step: </B>Select 'Cancel' button on export dialogue box</li>
	 * <li><B>Verify: </B>Verify export dialogue box should be disappeared</li>
	 * <li><B>Step: </B>Select survey 'Close' button</li>
	 * <li><B>Verify: </B>Verify user navigated to 'Survey' page</li>
	 * </ul>
	 */

	@Test(groups = { "level2" })
	public void surveysResponsesCustomizeAndExport() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		logger.strongStep(testUser.getDisplayName()+" take the survey in community added with 'Surveys' widget created by testUser via API");
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);
		
		logger.strongStep("Logging out from testUser");
		log.info("INFO: Logging out from testUser");
		commui.sleep(500);
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser1 ");
		logger.strongStep("Load communities and Login:" + testUser1.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser1);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);

		log.info("INFO: testUser1 take the survey and completes it successfully");
		logger.strongStep("testUser1 take the survey and completes it successfully");
		log.info("INFO: "+testUser1.getDisplayName()+" take the survey and completes it successfully");
		ui.fluentWaitPresent(SurveysUI.surveyTitleOnCommOverview);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.surveyTitleOnCommOverview).get(0).getWebElement());
		ui.clickLinkWait(SurveysUI.surveyTitleOnCommOverview);
		ui.takeSimpleSurvey(survey);
		ui.fluentWaitTextPresent("Completed Today at");
		
		logger.strongStep("Logging out from testUser1");
		log.info("INFO: Logging out from testUser1");
		commui.sleep(500);
		LoginEvents.logout(hui);

		log.info("INFO: Load component and login with testUser");
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities, true);
		commui.login(testUser);

		log.info("INFO Navigating to Community");
		community.navViaUUID(commui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		log.info("Locate the surveys widget on overview page and select link 'View All'");
		logger.strongStep("Locate the surveys widget on overview page and select link 'View All'");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();
		
		log.info("Select 'View Results'");
		logger.strongStep("Select 'View Results'");
		ui.clickSurveyMoreOption(survey, MoreLink.VIEW_RESULTS);
		
		log.info("Select 'Responses' tab");
		logger.strongStep("Select 'Responses' tab");
		ui.switchToFrame(SurveysUI.viewResultsiFrame, SurveysUI.summaryTab);
		ui.fluentWaitPresent(SurveysUI.responsesTab);
		ui.clickLinkWithJavascript(SurveysUI.responsesTabLink);
		
		log.info("Select 'Customize' button");
		logger.strongStep("Select 'Customize' button");
		ui.fluentWaitPresent(SurveysUI.customizeTab);
		ui.clickLinkWithJavascript(SurveysUI.customizeTab);
		
		log.info("Verify 'View Properties' dialogue box should be displayed");
		logger.strongStep("Verify 'View Properties' dialogue box should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewPropertiesDialogueBox));
		
		log.info("Verify the different elements present on 'View Properties' dialogue box");
		logger.strongStep("Verify the different elements present on dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.formSpecificColumnOption));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.applicationSpecificOption));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.showIDCheck));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.showStageCheck));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.showAuthorCheck));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.showLastUpdatedCheck));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.questionOnViewPropertiexBox));
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.viewFormInRightPane).get(0).getWebElement());
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewFormInRightPane));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewPropertiesOKButton));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewPropertiesApplyButton));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.viewPropertiesCancelButton));
		
		log.info("Select 'Cancel' button");
		logger.strongStep("Select 'Cancel' button");
		ui.clickLinkWithJavascript(SurveysUI.viewPropertiesCancelButton);
		
		log.info("Verify 'View Properties' dialogue box should be disappeared");
		logger.strongStep("Verify 'View Properties' dialogue box should be disappeared");
		Element targetItem = driver.getSingleElement(SurveysUI.viewPropertiesDialogueBox);
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
		wait.until(ExpectedConditions.invisibilityOf(targetItem.getWebElement()));
		Assert.assertFalse(ui.isElementVisible(SurveysUI.viewPropertiesDialogueBox));
		driver.turnOnImplicitWaits();

		log.info("Select 'Customize' button again");
		logger.strongStep("Select 'Customize' button again");
		ui.clickLinkWithJavascript(SurveysUI.customizeTab);
		ui.fluentWaitElementVisible(SurveysUI.viewPropertiesDialogueBox);
		
		log.info("Uncheck the checkbox 'Show ID' on 'View Properties' dialogue box");
		logger.strongStep("Uncheck the checkbox 'Show ID' on 'View Properties' dialogue box");
		ui.clickLinkWithJavascript(SurveysUI.showIDCheckBox);
		
		log.info("Clicking 'OK' button");
		logger.strongStep("Clicking 'OK' button");
		ui.clickLinkWithJavascript(SurveysUI.viewPropertiesOKButton);
		
		log.info("Verify the column 'ID' in responses view should be disappeared");
		logger.strongStep("Verify the column 'ID' in responses view should be disappeared");
		ui.fluentWaitElementVisible(SurveysUI.customizeTab);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.customizeTab).get(0).getWebElement());
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementVisible(SurveysUI.id));
		driver.turnOnImplicitWaits();
		
		log.info("Select 'Search' button");
		logger.strongStep("Select 'Search' button");
		ui.clickLinkWithJavascript(SurveysUI.searchTab);
		ui.fluentWaitPresent(SurveysUI.searchDialogue);
		List<Element> filterRows= driver.getVisibleElements(SurveysUI.addedFilterRows);
		int filterRowsBeforeAdding =filterRows.size();
		
		log.info("Click on '+' i.e add icon");
		logger.strongStep("Click on '+' i.e add icon ");
		ui.clickLinkWithJavascript(SurveysUI.addIcon);
		
		log.info("Verify filter count get added by 1");
		logger.strongStep("Verify filter count get added by 1");
		filterRows= driver.getVisibleElements(SurveysUI.addedFilterRows);
		int filterRowsAfterAdding =filterRows.size();
		Assert.assertEquals(filterRowsAfterAdding-filterRowsBeforeAdding, 1);
		ui.clickLinkWithJavascript(SurveysUI.removeIcon);
		
		log.info("Select dropdown'Select Item'");
		logger.strongStep("Select dropdown 'Select Item'");
		ui.clickLink(SurveysUI.selectItemDropdowns);
		List<Element> options = driver.getVisibleElements(SurveysUI.selectItemDropDownOptions);
		List<String> optionsFromDropdown = new ArrayList<>();
		List<String> expectedOptions = new ArrayList<>();
		expectedOptions.add("Created By");
		expectedOptions.add("Last Updated By");
		expectedOptions.add("Create Date");
		expectedOptions.add("Last Update Date");
		expectedOptions.add(question.getQuestion());

		for (Element option : options) {
			String optionName = option.getText();
			log.info("Option name is: "+optionName);
			optionsFromDropdown.add(optionName);
		}

		logger.strongStep("Verify 'Select Item' dropdown should contains options 'Created By','Last Updated By','Create Date','Last Update Date' and question");
		log.info("actual options are:" + optionsFromDropdown);
		log.info("expected options are:" + expectedOptions);
		Assert.assertTrue(optionsFromDropdown.equals(expectedOptions));

		log.info("INFO: Select question from 'Select Item' dropdown");
		logger.strongStep("Select question from 'Select Item' dropdown");
		ui.clickLinkWithJavascript(SurveysUI.selectItemOption("Created By"));
		
		log.info("INFO: Select 'Equals' from 'Choose Operator' dropdown");
		logger.strongStep("Select 'Equals' from 'Choose Operator' dropdown");
		ui.clickLink(SurveysUI.chooseOperator);
		ui.clickLinkWithJavascript(SurveysUI.chooseOperatorOption("Starts with"));
	
		log.info("INFO: Enter "+testUser1.getDisplayName()+"'s survey response "+question.getOptions().get(0).display+" from take survey section");
		logger.strongStep("Enter testUser1's survey response from take survey section");
		ui.typeText(SurveysUI.searchInputBox, testUser.getFirstName());
		
		log.info("INFO: Select 'Search' button");
		logger.strongStep("Select 'Search' button");
		ui.clickLink(SurveysUI.advSearchButton);
		
		log.info("INFO: Verify that responses of user matching with filter criteria should be returned");
		logger.strongStep(" Verify that responses of user matching with filter criteria should be returned");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.responsesOverviewView));
		
		log.info("INFO: Select 'Export Data' button");
		logger.strongStep("Select 'Export Data' button");
		ui.clickLinkWithJavascript(SurveysUI.exportData);
		
		log.info("Verify 'Export Data' dialogue box should be displayed");
		logger.strongStep("Verify 'Export Data' dialogue box should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogBox));
		
		log.info("Verify the different elements present on 'Export Data' dialogue box");
		logger.strongStep("Verify the different elements present on 'Export Data'  dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportResponsesFormatText));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogXMLRadioLabel));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogexcelRadioLabel));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogOpenDocRadioLabel));
		Assert.assertTrue(driver.getSingleElement(SurveysUI.exportDialogXMLRadio).getAttribute("checked").equals("true"));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogBoxNote));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogExportButton));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.exportDialogCancelButton));
		
		log.info("INFO: Select 'Cancel' button on export dialogue box");
		logger.strongStep("Select 'Cancel' button on export dialogue box");
		ui.clickLinkWithJavascript(SurveysUI.exportDialogCancelButton);
		
		log.info("INFO: Verify export dialogue box should be disappeared");
		logger.strongStep("Verify export dialogue box should be disappeared");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementVisible(SurveysUI.id));
		driver.turnOnImplicitWaits();
		
		log.info("INFO: Select survey 'Close' button");
		logger.strongStep("Select survey 'Close' button");
		ui.switchToTopFrame();
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.closeSurveyButton).get(0).getWebElement());
		ui.clickLinkWithJavascript(SurveysUI.closeSurveyButton);
		
		log.info("INFO: Verify user navigated to 'Survey' page");
		logger.strongStep("Verify user navigated to 'Survey' page");
		ui.fluentWaitPresent(SurveysUI.createSurveyButton);
		Assert.assertTrue(ui.isElementPresent(SurveysUI.createSurveyButton));
		
		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Stop Survey</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Create survey</li>
	 *<li><B>Step:</B> Add question to survey</li>
	 *<li><B>Step:</B> Save survey</li>
	 *<li><B>Step:</B> Start survey</li>
	 *<li><B>Step:</B> Take survey</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Click on 'View all' Link from Survey widget and application shall navigate to Surveys</li>
	 *<li><B>Verify:</B> Verify Stop Survey functionalities</li>
	 *<li><B>Step:</B> Returning to Recent Updates navigation page</li>
	 *<li><B>Step:</B> Verify the Embedded Experience for Stopped Survey on Recent Updates page</li>
	 *<li><B>Verify:</B> Verify the EE (embedded experience) pop-up box displays for the stopped survey entry</li>
	 *<li><B>Step:</B> Returning to Surveys page</li>
	 *<li><B>Step:</B> Verify the message 'This survey has now ended.' is displayed'</li>
	 *<li><B>Step:</B> Delete community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void stopSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder("comm" + testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		// Create Survey to Take survey common navigation
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		logger.strongStep("Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		log.info("INFO:Click on 'View all' Link from Survey widget and application shall navigate to Surveys");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		driver.getSingleElement(SurveysUI.viewAllLink).click();

		logger.strongStep("Verify Stop Survey functionalities");
		log.info("Verify Stop Survey functionalities");
		ui.stopSurveyFromSurveys(survey, MoreLink.EDIT);
		
		logger.strongStep("INFO Returning to Recent Updates navigation page");
		log.info("INFO Returning to Recent Updates navigation page");
		driver.getSingleElement(SurveysUI.recentUpdatesLink).click();

		log.info("INFO: Launch the Embedded Experience for Stopped Survey on Recent Updates page");
		logger.strongStep("Open the EE for Stopped Survey " + testUser.getDisplayName() + " added to");
		String newsStory = hui.replaceNewsStory(Data.STOP_SURVEY, survey.getName(), community.getName(),
				testUser.getDisplayName());
		HomepageValid.verifyItemsInAS(hui, driver, new String[] { newsStory }, null, true);

		log.info("INFO: Verify the EE (embedded experience) pop-up box displays for the stopped survey entry");
		logger.strongStep("Verify the EE (embedded experience) pop-up box displays for the stopped survey entry");
		openAndVerifyEE(newsStory);

		logger.strongStep("INFO Returning to Surveys page");
		log.info("INFO Returning to Surveys page");
		Community_TabbedNav_Menu.SURVEYS.select(ui,1);
		ui.clickMoreLink(survey);

		logger.strongStep(" Verify the message 'This survey has now ended.' is displayed");
		log.info("INFO:Verify the message 'This survey has now ended.' is displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.endSurveyTitle).getText(),"Thank you for participating. This survey has now ended.");

		// Cleanup
		logger.strongStep("Delete Community");
		log.info("INFO:Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}
	
	private void openAndVerifyEE(String newsStory) {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		log.info("INFO: Open the EE");
		logger.strongStep(" Open the EE");
		hui.filterNewsItemOpenEE(newsStory);
		log.info("INFO: Verify First Survey Title link should appear on the EE box");
		logger.strongStep("Verify First Survey Title link should appear on the EE box");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.surveyNameEE), "ERROR: Survey name is not Displayed");
		log.info("INFO: Verify Second Survey Title link should appear on the EE box");
		logger.strongStep(" Verify Second Survey Title link should appear on the EE box");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.surveyName2EE), "ERROR: Survey name is not Displayed");
		HomepageValid.verifyItemsInAS(hui, driver, new String[] { newsStory }, null, true);
		hui.filterNewsItemOpenEE(newsStory);
		hui.switchToTopFrame();
		ui.fluentWaitElementVisible(SurveysUI.closeEEpopUP);
		ui.clickLinkWait(SurveysUI.closeEEpopUP);
    }
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Featured Surveys</li>
	 *<li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Click on 'Select a Survey to Display' Link from Featured Survey widget</li>
	 *<li><B>Verify:</B> Verify Feature Survey drop down</li>
	 *<li><B>Step:</B> Select dropdown'Select Featured Survey'</li>
	 *<li><B>Verify:</B> Verify drop down should contains options 'Active Surveys','Closed Surveys' and 'No Survey Displayed'</li>
	 *<li><B>Verify:</B> Verify selected active Survey is displayed</li>
	 *<li><B>Verify:</B> Verify 'Display Survey' button is displayed on dialog box</li>
	 *<li><B>Verify:</B> Verify 'Cancel' button is displayed on dialog box</li>
	 *<li><B>Verify:</B> Verify and Click 'Creating'a survey link from Featured Survey Widget</li>
	 *<li><B>Verify:</B> Verify and Click 'Cancel' survey button from Create Surveys page</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Verify:</B> Verify and Click 'editing' a draft link from Featured Survey Widget</li>
	 *<li><B>Verify:</B> Verify 'Drafts' drop down on Surveys page</li>
	 *<li><B>Verify:</B> Verify the message 'There are no draft surveys for this community.' is displayed</li>
	 *<li><B>Step:</B> Returning to Overview page</li>
	 *<li><B>Step:</B> Click on 'Select a Survey to Display' Link from Featured Survey widget</li>
	 *<li><B>Verify:</B> Verify 'Display Survey' button is displayed on dialog box</li>
	 *<li><B>Verify:</B> Verify Response Table is displayed</li>
	 *<li><B>Step:</B> Delete Community</li>
	 *</ul>
	 */
	@Test(groups = { "level2" })
	public void featuredSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Question A",
				BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
						.addOption(new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
						.addOption(new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + randvalue).description("Test for Survey" + testName)
				.questions(questions).anonResponse(false).build();

		// Create Survey to Take survey common navigation
		fromCreateSurveyToTakeSurveyNavigation(survey, question, community);

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		logger.strongStep("Click on 'Select a Survey to Display' Link from Featured Survey widget");
		log.info("INFO:Click on 'Select a Survey to Display' Link from Featured Survey widget");
		ui.clickLink(SurveysUI.selectSurveyForFeaturedLink);

		log.info("INFO: Verify Feature Survey dropdown");
		logger.strongStep("Verify Feature Survey dropdown");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.selectSurveyTypeDropdown),"ERROR: Survey dropdown is not Displayed");

		log.info("Select dropdown'Select Featured Survey'");
		logger.strongStep("Select dropdown 'Select Featured Survey'");
		ui.clickLink(SurveysUI.selectSurveyTypeDropdown);
		List<Element> options = driver.getVisibleElements(SurveysUI.selectSurveyTypeDropdownvalues);
		List<String> optionsFromDropdown = new ArrayList<>();
		List<String> expectedOptions = new ArrayList<>();
		expectedOptions.add("Active Surveys");
		expectedOptions.add("Closed Surveys");
		expectedOptions.add("No Survey Displayed");

		for (Element option : options) {
			String optionName = option.getText();
			log.info("Option name is: " + optionName);
			optionsFromDropdown.add(optionName);
		}

		logger.strongStep(
				"Verify dropdown should contains options 'Active Surveys','Closed Surveys' and 'No Survey Displayed'");
		log.info("actual options are:" + optionsFromDropdown);
		log.info("expected options are:" + expectedOptions);
		Assert.assertTrue(optionsFromDropdown.equals(expectedOptions));

		log.info("INFO: Verify selected active Survey is displayed");
		logger.strongStep("Verify selected active Survey is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.activeSurveyradioBtn),
				"ERROR: Selected Survey is not Displayed");

		log.info("INFO: Verify 'Display Survey' button is displayed on dialog box");
		logger.strongStep("Verify 'Display Survey' button is displayed on dialog box");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.displaySurveyBtn),
				"ERROR: 'Display Survey' button is not Displayed");

		log.info("INFO: Verify 'Cancel' button is displayed on dialog box");
		logger.strongStep("Verify 'Cancel' button is displayed on dialog box");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.cancelSurveyBtn), "ERROR: 'Cancel' button is not Displayed");
		driver.getSingleElement(SurveysUI.cancelSurveyBtn).click();

		log.info("INFO: Verify and Click 'Creating'a survey link from Featured Survey Widget");
		logger.strongStep("Verify and Click 'Creating'a survey link from Featured Survey Widget");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.creatingSurveyLink),
				"ERROR: 'Creating'a survey link is not displayed on Featured Survey Widget");
		driver.getSingleElement(SurveysUI.creatingSurveyLink).click();

		log.info("INFO: Verify and Click 'Cancel' survey button from Create Surveys page");
		logger.strongStep("Verify and Click 'Cancel' survey button from Create Surveys page");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.cancelSurveyfromSurveys),
				"ERROR: 'Cancel' survey buttonis not displayed on Create Surveys page");
		driver.getSingleElement(SurveysUI.cancelSurveyfromSurveys).click();

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();

		log.info("INFO: Verify and Click 'editing' a draft link from Featured Survey Widget");
		logger.strongStep("Verify and Click 'editing' a draft link from Featured Survey Widget");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.editingDraft),"ERROR: 'editing' a draft link is not displayed on Featured Survey Widget");
		driver.getSingleElement(SurveysUI.editingDraft).click();

		log.info("INFO: Verify 'Drafts' dropdown on Surveys page");
		logger.strongStep("Verify 'Drafts' dropdown on Surveys page");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.selectSurveyTypeDropdown),"ERROR: Drafte dropdown is not Displayed");
		
		logger.strongStep(" Verify the message 'There are no draft surveys for this community.' is displayed");
		log.info("INFO:Verify the message 'There are no draft surveys for this community.' is displayed");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.noDraftsSurveyMsg).getText(),
				"There are no draft surveys for this community.");

		logger.strongStep("INFO Returning to Overview page");
		log.info("INFO Returning to Overview page");
		driver.getSingleElement(SurveysUI.overviewLink).click();
		driver.navigate().refresh();

		logger.strongStep("Click on 'Select a Survey to Display' Link from Featured Survey widget");
		log.info("INFO:Click on 'Select a Survey to Display' Link from Featured Survey widget");
		ui.clickLink(SurveysUI.selectSurveyForFeaturedLink);
		
		log.info("INFO: Verify 'Display Survey' button is displayed on dialog box");
		logger.strongStep("Verify 'Display Survey' button is displayed on dialog box");
		Assert.assertTrue(ui.fluentWaitPresent(SurveysUI.displaySurveyBtn),"ERROR: 'Display Survey' button is not Displayed");
		driver.getSingleElement(SurveysUI.displaySurveyBtn).click();
		
		log.info("INFO: Verify Response Table is displayed");
		logger.strongStep("Verify Response Table is displayed");
		ui.switchToFrame(SurveysUI.featruredSurveyWidget, SurveysUI.responsetable);
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(SurveysUI.responsetable).get(0).getWebElement());
		Assert.assertTrue(ui.isElementPresent(SurveysUI.responsetable), "ERROR: 'Response Table' is not Displayed");
		
		// Cleanup
		logger.strongStep("Delete Community");
		log.info("INFO:Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
		
		}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify delete featured survey functionality</li>
	 * <li><B>Prerequisite: </B> Server should have 'Surveys' running on it</li>
	 * <li><B>Step:</B>create community using API</li>
	 * <li><B>Step: </B>Add SURVEYS and FEATUREDSURVEYS widget with API</li>
	 * <li><B>Step: </B>Load component and login with testUser</li>
	 * <li><B>Step: </B>Navigate to community</li>
	 * <li><B>Step: </B>Select Surveys summary widget action menu i.e. 3 dots(...)</li>
	 * <li><B>Step: </B>Select 'Delete' option from action menu</li>
	 * <li><B>Verify: </B>Verify 'Delete Surveys' dialogue box should be displayed</li>
	 * <li><B>Verify: </B>Verify OK button on 'Delete Surveys' dialogue box should be greyed out</li>
	 * <li><B>Step: </B>Select 'Cancel' button on 'Delete Surveys' dialogue box</li>
	 * <li><B>Verify: </B>Verify 'Delete Surveys' dialogue box should be no longer displayed on page and user is back on overview page</li>
	 * <li><B>Step: </B>Select survey's action menu again</li>
	 * <li><B>Step: </B>Select 'Delete' from action menu</li>
	 * <li><B>Step: </B>Enter text 'Surveys' in confirm application name input field</li>
	 * <li><B>Step: </B>Enter user's name in sign with your name input field</li>
	 * <li><B>Verify: </B>Verify that 'OK' button on delete dialogue box should no longer greyed out</li>
	 * <li><B>Step: </B>Select 'OK' button on delete dialogue box</li>
	 * <li><B>Verify: </B>Verify that summary widget on overview page should no longer displayed</li>
	 * <li><B>Verify: </B>Verify that summary widget should no longer appears on top nav bar</li>
	 * <li><B>Verify: </B>Verify that inactive warning text 'This widget is no longer functional because Surveys widget has been removed from this community.' should be displayed on Featured Survey widget</li>
	 * <li><B>Verify: </B>Verify that inactive delete text 'Remove this widget or customize your community by adding Surveys widget again.' should be displayed on Featured Survey widget</li>
	 * <li><B>Step: </B>Select Featured Survey's action menu</li>
	 * <li><B>Step: </B>Select 'Remove' option from action menu</li>
	 * <li><B>Verify: </B>Verify Featured Survey remove dialogue box</li>
	 * <li><B>Step: </B>Select 'Cancel' option from dialogue box</li>
	 * <li><B>Verify: </B>Verify featured survey remove dialogue box should no longer displayed</li>
	 * <li><B>Step: </B>Select Featured Survey action menu again</li>
	 * <li><B>Step: </B>Select 'Remove' option from action menu</li>
	 * <li><B>Step: </B>Select 'OK' button from featured survey remove dialogue box</li>
	 * <li><B>Verify: </B>Verify featured survey summary widget should no longer available</li>	 
	 * </ul>
	 */
	
	@Test(groups = { "level2" })
	public void deleteFeaturedSurveys() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String randvalue = Helper.genDateBasedRandVal();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + randvalue)
				.tags(Data.getData().commonTag + randvalue).access(Access.PUBLIC)
				.description("Test check Survey Lifecycle" + testName).addMember(member).build();

		// create community
		logger.strongStep("create community using API");
		log.info("INFO: create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// add widget SURVEYS
		log.info("INFO: Add SURVEYS and FEATUREDSURVEYS widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS);

		// Load component and login
		logger.strongStep("Load communities and Login:" + testUser.getDisplayName());
		commui.loadComponent(Data.getData().ComponentCommunities);
		commui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		commui.changingCommunityLandingPage(apiOwner, comAPI);

		// Navigate to the API community
		logger.strongStep("Navigate to community");
		log.info("INFO: Navvigate to community using UUID");
		community.navViaUUID(commui);
		
		logger.strongStep("Select surveys summary widget action menu i.e. 3 dots(...)");
		log.info("INFO: Select surveys summary widget action menu i.e. 3 dots(...)");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(SurveysUI.createSurveyFromWidget).get(0).getWebElement());
		ui.fluentWaitElementVisible(SurveysUI.surveyWidgetActionMenu);
		ui.clickLinkWithJavascript(SurveysUI.surveyWidgetActionMenu);
		
		logger.strongStep("Select 'Delete' option from action menu");
		log.info("INFO: Select 'Delete' option from action menu");
		ui.fluentWaitElementVisible(SurveysUI.surveyDeleteOption);
		ui.clickLinkWithJavascript(SurveysUI.surveyDeleteOption);
		
		logger.strongStep("Verify 'Delete Surveys' dialogue box should be displayed");
		log.info("INFO: Verify 'Delete Surveys' dialogue box should be displayed");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.deleteConfirmWidgetDialogueBox));
		
		logger.strongStep("Verify OK button on 'Delete Surveys' dialogue box should be greyed out");
		log.info("INFO: Verify OK button on 'Delete Surveys' dialogue box should be greyed out");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.deleteConfirmWidgetOKButton).getAttribute("aria-disabled").equals("true"));
		
		logger.strongStep("Select 'Cancel' button on 'Delete Surveys' dialogue box");
		log.info("INFO: Select 'Cancel' button on 'Delete Surveys' dialogue box");
		ui.clickLinkWithJavascript(SurveysUI.deleteConfirmWidgetCancelButton);
		
		logger.strongStep("Verify 'Delete Surveys' dialogue box should be no longer displayed on page and user is back on overview page");
		log.info("INFO: Verify 'Delete Surveys' dialogue box should be no longer displayed on page and user is back on overview page");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementPresent(SurveysUI.deleteConfirmWidgetDialogueBox));
		driver.turnOnImplicitWaits();
		Assert.assertTrue(ui.isElementVisible(CommunitiesUIConstants.surveysSummaryWidget));
		
		logger.strongStep("Select survey's action menu again");
		log.info("INFO: Select survey's action menu again");
		ui.fluentWaitElementVisible(SurveysUI.surveyWidgetActionMenu);
		ui.clickLinkWithJavascript(SurveysUI.surveyWidgetActionMenu);
		
		logger.strongStep("Select 'Delete' from action menu");
		log.info("INFO: Select 'Delete' from action menu");
		ui.fluentWaitElementVisible(SurveysUI.surveyDeleteOption);
		ui.clickLinkWithJavascript(SurveysUI.surveyDeleteOption);
		ui.fluentWaitElementVisible(SurveysUI.deleteConfirmWidgetDialogueBox);
		
		logger.strongStep("Enter text 'Surveys' in confirm application name input field");
		log.info("INFO: Enter text 'Surveys' in confirm application name input field");
		ui.typeText(SurveysUI.confirmApplicationNameInputField, "Surveys");
		
		logger.strongStep("Enter user's name in sign with your name input field");
		log.info("INFO: Enter user's name in sign with your name input field");
		ui.typeText(SurveysUI.signWithYourNameField, testUser.getDisplayName());
		
		logger.strongStep("Verify that 'OK' button on delete dialogue box should no longer greyed out");
		log.info("INFO:Verify that 'OK' button on delete dialogue box should no longer greyed out");
		Assert.assertTrue(driver.getSingleElement(SurveysUI.deleteConfirmWidgetOKButton).getAttribute("aria-disabled").equals("false"));
		
		logger.strongStep("Select 'OK' button on delete dialogue box");
		log.info("INFO: Select 'OK' button on delete dialogue box");
		ui.clickLinkWithJavascript(SurveysUI.deleteConfirmWidgetOKButton);
		
		logger.strongStep("Verify that summary widget on overiew page should no longer displayed");
		log.info("INFO: Verify that summary widget on overiew page should no longer displayed");
		Element targetItem = driver.getSingleElement(CommunitiesUIConstants.surveysSummaryWidget);
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
		wait.until(ExpectedConditions.invisibilityOf(targetItem.getWebElement()));
		Assert.assertFalse(ui.isElementPresent(SurveysUI.surveysSummaryWidget));
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Verify that summary widget should no longer appears on top nav bar");
		log.info("INFO: Verify that summary widget should no longer appears on top nav bar");
		List<String> widgets = commui.getTopNavItems(false);
		boolean found = false;
		for (String widgetTitle : widgets) {
			log.info("Widget title is:" + widgetTitle);
			if (widgetTitle.equals("surveys")) {
				found = true;
				break;
			}
		}
		Assert.assertFalse(found);
		
		logger.strongStep("Verify that inactive warning text 'This widget is no longer functional because Surveys widget has been removed from this community.' should be displayed on Featured Survey widget");
		log.info("INFO: Verify that inactive warning text 'This widget is no longer functional because Surveys widget has been removed from this community.' should be displayed on Featured Survey widget");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.featuredSurveysInactiveWarningText).getText(),"This widget is no longer functional because Surveys widget has been removed from this community.");
		
		logger.strongStep("Verify that inactive delete text 'Remove this widget or customize your community by adding Surveys widget again.' should be displayed on Featured Survey widget");
		log.info("INFO: Verify that inactive delete text 'Remove this widget or customize your community by adding Surveys widget again.' should be displayed on Featured Survey widget");
		Assert.assertEquals(driver.getSingleElement(SurveysUI.featuredSurveysInactiveDeleteText).getText(),"Remove this widget or customize your community by adding Surveys widget again.");
		
		logger.strongStep("Select Featured Survey's action menu");
		log.info("INFO: Select Featured Survey's action menu");
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyWidgetActionMenu);
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyWidgetActionMenu);
		
		logger.strongStep("Select 'Remove' option from action menu");
		log.info("INFO: Select 'Remove' option from action menu");
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyRemoveOption);
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyRemoveOption);
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyRemoveDialogueBox);
		
		logger.strongStep("Verify Featured Survey remove dialogue box");
		log.info("INFO: Verify Featured Survey remove dialogue box");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.featuredSurveyRemoveDialogueBox));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.featuredSurveyRemoveDialogueBoxHeader));
		Assert.assertEquals(driver.getSingleElement(SurveysUI.featuredSurveyRemoveDialogueBoxHeader).getText(),"Remove Application");
		Assert.assertTrue(ui.isElementPresent(SurveysUI.featuredSurveyRemoveDialogueBoxOK));
		Assert.assertTrue(ui.isElementPresent(SurveysUI.featuredSurveyRemoveDialogueBoxCancel));
		Assert.assertEquals(getMessage(SurveysUI.featuredSurveyRemoveDialogueText),"Are you sure you want to remove this application? You can restore this application later through the Community Actions menu. Any settings for displaying the application are lost, but the application's data remains intact. ");
		
		logger.strongStep("Select 'Cancel' option from dialogue box");
		log.info("INFO: Select 'Cancel' option from dialogue box");
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyRemoveDialogueBoxCancel);
		
		logger.strongStep("Verify featured survey remove dialogue box should no longer displayed");
		log.info("INFO: Verify featured survey remove dialogue box should no longer displayed");
		driver.turnOffImplicitWaits();
		Element targetItem1 = driver.getSingleElement(SurveysUI.featuredSurveyRemoveDialogueBox);
		wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
		wait.until(ExpectedConditions.invisibilityOf(targetItem1.getWebElement()));
		Assert.assertFalse(ui.isElementVisible(SurveysUI.featuredSurveyRemoveDialogueBox));
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Select Featured Survey action menu again");
		log.info("INFO: Select Featured Survey action menu again");
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyWidgetActionMenu);
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyWidgetActionMenu);
		
		logger.strongStep("Select 'Remove' option from action menu");
		log.info("INFO: Select 'Remove' option from action menu");
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyRemoveOption);
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyRemoveOption);
		
		logger.strongStep("Select 'OK' button from featured survey remove dialogue box");
		log.info("INFO: Select 'OK' button from featured survey remove dialogue box");
		ui.fluentWaitElementVisible(SurveysUI.featuredSurveyRemoveDialogueBoxOK);
		ui.clickLinkWithJavascript(SurveysUI.featuredSurveyRemoveDialogueBoxOK);
		
		logger.strongStep("Verify featured survey summary widget should no longer available");
		log.info("INFO: Verify featured survey summary widget should no longer available");
		targetItem = driver.getSingleElement(CommunitiesUIConstants.featuredSummarySurveyWidget);
		driver.turnOffImplicitWaits();
		wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
		wait.until(ExpectedConditions.invisibilityOf(targetItem.getWebElement()));
		Assert.assertFalse(ui.isElementPresent(SurveysUI.featuredSummarySurveyWidget));
		driver.turnOnImplicitWaits();
		
		// Cleanup
		logger.strongStep("Delete Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();
	}

}
