package com.ibm.conn.auto.tests.icec.highlights;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Icec_Highlights extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Icec_Highlights.class);
	private IcecUI ui;
	private CommunitiesUI communitiesUi;
	private BaseCommunity.Access defaultAccess;
	private TestConfigCustom cfg;
	private String serverURL;
	private APICommunitiesHandler apiOwner;
	private ArrayList<Community> communitiesToDelete;
	private User testUser;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		communitiesToDelete = new ArrayList<Community>();
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

	    // initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		communitiesUi = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Add/Remove Highlights App to/from a community.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create Community.
	*<li><B>Step:</B> Add Highlights widget to the community if it is not already a part of the default layout.
	*<li><B>Step:</B> Navigate to Highlights app.
	*<li><B>Verify:</B> The ICEC Content appears.
	*<li><B>Step:</B> Remove the Highlights app.
	*<li><B>Verify:</B> Check Highlights has successfully been removed from community.
	*</ul>
	*/
	@Test(groups = {"regression", "bvt", "smokeonprem"})
	public void addRemoveHighlightsApp() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String appName = "Highlights";
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				   .access(Access.PUBLIC)
				   .description("Test Community " + communityName)
				   .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		communitiesToDelete.add(comAPI);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = communitiesUi.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {

		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}
		
		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(communitiesUi);

		logger.strongStep("Add " + appName + " app to the community if it is not already added to it");
		log.info("INFO: Add " + appName + " app to the community if it is not already added to it");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) 
			communitiesUi.addWidget(BaseWidget.HIGHLIGHTS);
		
		logger.strongStep("Navigate to " + appName + " app");
		log.info("INFO: Navigate to " + appName + " app");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);
		Assert.assertTrue(ui.fluentWaitPresent("css=#xccContent"),
				"ICEC content not found on " + appName + " page. Was navigation to " + appName + " successful?");
		
		logger.strongStep("Remove " + appName + " app from Community");
		log.info("INFO: Remove " + appName + " app from Community");
		removeHighlightsFromCommunity(testUser);
		ui.fluentWaitTextNotPresent("Removing Application");
		
		log.info("INFO: Refreshing page");
		driver.navigate().refresh();
		Assert.assertFalse(ui.isElementPresent(IcecUI.getOverviewActionsMenu(appName)),
				appName + " app unsuccessfully deleted?");
		
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scrollTo(250, 0);");
		
		logger.strongStep("Check " + appName + " is available to add again after removal");
		log.info("INFO: Check " + appName + " is available to add again after removal");
		ui.clickLink(IcecUI.communityActionsLink);
		driver.executeScript("scrollTo(250, 0);");
		ui.clickLink(IcecUI.addAppsLink);
		Assert.assertTrue(ui.isHighlightsInAppPalette(),
				"Option to add " + appName + " in Add Apps not found.");

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B> Create, delete and permanently remove Widget from Customize menu.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create Community.
	*<li><B>Step:</B> Remove default Community Description widget.
	*<li><B>Step:</B> Click on Customize button and add the Community Description widget.
	**<li><B>Step:</B> Edit widget and change the description.
	*<li><B>Step:</B> Click Save to update widget.
	*<li><B>Verify:</B> Check widget is updated successfully.
	*<li><B>Step:</B> Remove widget.
	*<li><B>Verify:</B> Check widget is removed successfully.
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "smokeonprem"})
	public void customizeCreateDeleteWidgets() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community Description for: " + communityName;
		String newCommunityDescriptionText = "New Community Description" + Helper.genDateBasedRandVal();
		String widgetType = "description";
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				   .access(Access.PUBLIC)
				   .description(description)
				   .build();

		logger.strongStep("Login, Create a community and navigate to the community");
		log.info("INFO: Login, Create a community and navigate to the community");
		Community comAPI = createLoginAndNavigateToCommunity(testUser, communityName, description);
		communitiesToDelete.add(comAPI);

		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = communitiesUi.isHighlightDefaultCommunityLandingPage();
		
		if (!flag) {
			
			if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) {
	        	
				community.addWidgetAPI(comAPI, apiOwner, BaseWidget.HIGHLIGHTS);
				driver.navigate().refresh();
				ui.waitForPageLoaded(driver);

			}
			
			logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
			log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
			Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);
			
		}	

		logger.strongStep("Remove default Community Description widget");
		log.info("INFO: Remove default Community Description widget");
		ui.removeWidget(widgetType);
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		// Create Widget
		logger.strongStep("Click on Customize button and Create Community Description Widget");
		log.info("INFO: Click on Customize button and Create Community Description Widget");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.addWidgetsFromHiddenApps(widgetType);
		ui.waitForJQueryToLoad(driver);
		communitiesUi.waitForCommunityLoaded();
		
		logger.strongStep("Edit Community Description widget");
		log.info("INFO: Edit Community Description widget");
		ui.editWidget(false, widgetType);
		
		logger.strongStep("Clear the text in Community Description widget and enter: " + newCommunityDescriptionText);
		log.info("INFO: Clear the text in Community Description widget and enter: " + newCommunityDescriptionText);
		ui.typeInCkEditor(newCommunityDescriptionText);
		
		logger.strongStep("Click on the 'Save and Close' button");
		log.info("INFO: Click on the 'Save and Close' button");
		ui.clickLinkWithJavascript(IcecUI.actionsSaveAndClose);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetType)),
				"Community Description widget not found. Failed to create?");
		
		logger.strongStep("Validate that the Community Description widget now has the text '" + newCommunityDescriptionText + "'");
		log.info("INFO: Validate that the Community Description widget now has the text '" + newCommunityDescriptionText + "'");
		Assert.assertTrue(ui.getElementText(IcecUI.getCommunityDescriptionText("description")).equals(newCommunityDescriptionText),
			"The Community Description widget does not have the text '" + newCommunityDescriptionText + "'");

		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		// Delete widget
		logger.strongStep("Remove the Community Description widget");
		log.info("INFO: Remove the Community Description widget");
		ui.removeWidget(widgetType);
		
		logger.strongStep("Verify the deletion message appears and the widget is no longer visible");
		log.info("INFO: Verify the deletion message appears and the widget is no longer visible");
		String deletionMessage = "Widget Community Description was removed. "
				+ "The configuration of this widget will be saved and will apply the next time you use this widget.";
		Assert.assertTrue(ui.fluentWaitTextPresent(deletionMessage),
				"Community Description widget deletion message didn't display. Failed to delete?");
		Assert.assertFalse(ui.isElementPresent(IcecUI.getWidget(widgetType)),
				"Community Description widget found. Failed to delete?");

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Customize Page Settings - BVT.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create Community.
	*<li><B>Step:</B> Add the Highlights widget to the community if it is not a part of the default layout.
	*<li><B>Step:</B> Open Customize menu.
  	*<li><B>Step:</B> Click Page Editor text field.
  	*<li><B>Step:</B> Enter user into text field.
  	*<li><B>Verify:</B> Check user dropdown appears.
  	*<li><B>Step:</B> Click Navigation Checkbox.
  	*<li><B>Verify:</B> Check expected labels and options appear.
	*<li><B>Step:</B> Click Layout dropdown .
  	*<li><B>Verify:</B> Ensure Layout options appear.
 	*<li><B>Step:</B> Click 'Is Template' Checkbox.
  	*<li><B>Step:</B> Click save.
  	*<li><B>Verify:</B> Check template exists.
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "smokeonprem"})
	public void customizePageSettings() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community " + communityName;
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description)
				   .build();

		logger.strongStep("Login, Create a community and navigate to the community");
		log.info("INFO: Login, Create a community and navigate to the community");
		Community comAPI = createLoginAndNavigateToCommunity(testUser, communityName, description);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		log.info("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = communitiesUi.isHighlightDefaultCommunityLandingPage();
		
		if (!flag) {
			
			if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) 
				community.addWidget(communitiesUi, BaseWidget.HIGHLIGHTS);
			
			logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
			log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
			Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);
		}	

		//Open Customize Menu
		logger.strongStep("Click on Customize button and Go to Highlights page settings and Click on Advanced settings button");
		log.info("INFO: Click on Customize button and Go to Highlights page settings and Click on Advanced settings button");		
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.clickLink(IcecUI.highlightsPageSettingsTab);
		ui.clickLink(IcecUI.advancedSettingsButton);
		
		logger.strongStep("Check page editor user search");
		log.info("INFO: Check page editor user search");
		ui.typeText(IcecUI.pageEditorTextField, testUser.getDisplayName());
		String emailCheck = ui.getFirstVisibleElement("css=ul[class*='ui-menu'][class*='ui-autocomplete'] li[class='ui-menu-item']")
				.getAttribute("title");
		Assert.assertEquals(testUser.getEmail().toLowerCase(), emailCheck.toLowerCase(),"User in dropdown not found.");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName()),
				testUser.getDisplayName() + " not found.");
		
		// Navigation
		logger.strongStep("Click navigation checkbox and check options appear");
		log.info("INFO: Click navigation checkbox and check options appear");
		ui.clickLink(IcecUI.navigationCheckbox);
		String[] navLabels = {"Orientation", "Source"};
		checkPageSettingsLabelsPresent(navLabels);
		ui.clickLink(IcecUI.navigationCheckbox);
		
		// Layout
		logger.strongStep("Click Layout dropdown");
		log.info("INFO: Click Layout dropdown");
		ui.clickLink(IcecUI.layoutDropdown);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getLayoutDropdownOption("-- Default --")),
				"-- Default -- not found");
		
		// Set values and enable checkboxes
		logger.strongStep("Select options to test");
		log.info("INFO: Select options to test");
		ui.selectCheckbox(IcecUI.isTemplateCheckbox);
		ui.clickLink(IcecUI.pageSettingsSave);
		
		ui.endTest();
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanUp() {
		for (Community community : communitiesToDelete) {
			log.info("INFO: Deleting community: " + community.getTitle());
			apiOwner.deleteCommunity(community);
		}
	}
	
	private void removeHighlightsFromCommunity(User testUser) {
		Community_TabbedNav_Menu.OVERVIEW.select(ui);
		ui.clickLinkWait(IcecUI.getOverviewActionsMenu("Highlights"));
		// A temporary conditional, so that this test will support both 
		// 'Delete'(new) and 'Remove'(old) in the Highlights action menu
		String menuId = driver.getSingleElement("css=div[id$=_dropdown] table[class~=dijitMenuSelected]").getAttribute("id");
		if(ui.isElementPresent(IcecUI.getActiveOverviewMenuAction(menuId, "Delete"))) {
			ui.clickLink(IcecUI.getActiveOverviewMenuAction(menuId, "Delete"));
		} else {
		  ui.clickLink(IcecUI.actionsMenuRemove);
		}
		ui.clearAndTypeText(IcecUI.deleteAppNameTextField, "Highlights");
		ui.clearAndTypeText(IcecUI.deleteUserNameTextField, testUser.getDisplayName());
		ui.clickLinkWithJavascript(IcecUI.deleteOKButton);
	}
	
	private Community createLoginAndNavigateToCommunity(User testUser, String communityName, String description) {
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//GUI
		//Login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(communitiesUi);
		
		return comAPI;
	}
	
	private void checkPageSettingsLabelsPresent(String[] labels) {
		for (String label : labels) {
			String selector = IcecUI.getPageSettingsLabel(label);
			DefectLogger logger = dlog.get(Thread.currentThread().getId());
			logger.strongStep("Check label " + label + " is present");
			log.info("INFO: Check label " + label + " is present");
			Assert.assertTrue(ui.fluentWaitPresent(selector),
					label + " label not found");
		}	
	}
}
