package com.ibm.conn.auto.tests.icec.xcc;

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
import com.ibm.conn.auto.webui.IcecUI;

public class BVT_Level_2_Icec_AdminPanel extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Icec_AdminPanel.class);
	private IcecUI ui;
	private User testUser;
	private TestConfigCustom cfg;
	private boolean isIcecLight;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		testUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Using test user:" + testUser.getDisplayName());
		isIcecLight = ui.isIcecLight();
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

	    // initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Create, delete and permanently remove Widget from Admin Panel.
	*<li><B>Step:</B> Login to xcc/main.
	*<li><B>Step:</B> Open Admin Panel.
	*<li><B>Step:</B> Open Widgets Tab.
	*<li><B>Step:</B> Click Create Widget.
	**<li><B>Step:</B> Edit widget and Enter Title.
	*<li><B>Step:</B> Click Save to update widget.
	*<li><B>Verify:</B> Check widget is created successfully.
	*<li><B>Step:</B> Remove widget.
	*<li><B>Verify:</B> Check widget is removed successfully.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void adminPanelWidgetsCreateDelete() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String widgetType = "My Communities";
		String myCommunitiesId = "my-communities-" + Helper.genDateBasedRandVal();

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Login to ICEC and navigate to Admin Panel Widgets tab");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentIcec);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);
		
		logger.strongStep("Click on Customize button");
		log.info("INFO: Click on Customize button");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		
		logger.strongStep("Navigate to the widgets tab");
		log.info("INFO: Navigate to the widgets tab");
		ui.clickLink(IcecUI.widgetsTab);
		
		// Create Widget
		logger.strongStep("Create Widget by clicking on New Widget and select My communities widget");
		log.info("INFO: Create Widget by clicking on New Widget and select My communities widget");
		ui.clickLink(IcecUI.createWidgetLink);
		ui.selectType("xccMyCommunities");
		
		logger.strongStep("Edit My communities widget");
		log.info("INFO: Edit My communities widget");
		ui.editWidget(widgetType);
		
		logger.strongStep("Clear and enter title");
		log.info("INFO: Clear and enter title");
		ui.clearText(IcecUI.idTextField);
		ui.typeText(IcecUI.idTextField, myCommunitiesId);
		
		logger.strongStep("Click on save button");
		log.info("INFO: Click on save button");
		ui.clickLink(IcecUI.widgetDialogCreateButton);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(myCommunitiesId)),
				myCommunitiesId + " widget not found. Failed to create?");
		
		// Delete widget
		logger.strongStep("Remove widget");
		log.info("INFO: Remove widget");
		ui.removeWidget(myCommunitiesId);
		
		String deletionMessage = "Widget " + myCommunitiesId + " was removed. "
				+ "The configuration of this Widget will be saved and will apply the next time you use this widget.";
		
		logger.strongStep("Verify deletion messege appears and the widget is removed successfully");
		log.info("INFO: Verify deletion messege appears and the widget is removed successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(deletionMessage),
				myCommunitiesId + " widget deletion message didn't display. Failed to delete?");
		Assert.assertFalse(ui.isElementPresent(IcecUI.getWidget(myCommunitiesId)),
				myCommunitiesId + " widget found. Failed to delete?");
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Admin Panel Page Settings - BVT.
	*<li><B>Step:</B> Login to xcc/main.
	*<li><B>Step:</B> Open Admin Panel.
	*<li><B>Step:</B> Open Page Settings Tab.
	*<li><B>Step:</B> Click Restricted option.
	*<li><B>Verify:</B> Check expected info message appears.
	*<li><B>Verify:</B> Check Page Reader label appears.
	*<li><B>Step:</B> Click Public option.
	*<li><B>Verify:</B> Check expected info message appears.
	*<li><B>Step:</B> Click Anonymous option.
	*<li><B>Verify:</B> Check expected info message appears.
  	*<li><B>Step:</B> Click Page Editor text field.
  	*<li><B>Step:</B> Enter user into text field.
  	*<li><B>Verify:</B> Check user dropdown appears.
  	*<li><B>Step:</B> Click Navigation Checkbox.
  	*<li><B>Verify:</B> Check expected labels and options appear.
  	*<li><B>Step:</B> Click Activity Stream and Right-Column Checkboxes.
	*<li><B>Step:</B> Change ID and Title.
	*<li><B>Step:</B> Click Grid dropdown .
	*<li><B>Verify:</B> Ensure grid options appear.
 	*<li><B>Step:</B> Click 'Is Template' Checkbox.
  	*<li><B>Step:</B> Click save.
  	*<li><B>Verify:</B> Check Title, Connections Elements and template exists.
  	*<li><B>Step:</B> Delete template.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void adminPanelPageSettings() {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		} 
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Login to ICEC and navigate to Admin Panel Page Settings tab");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentIcec);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);
		ui.clickLink(IcecUI.customizeButton);
		ui.clickLink(IcecUI.pageSettingsTab);
		
		// Page Access
		logger.strongStep("Check restricted option");
		log.info("INFO: Check restricted option");
		ui.clickLink(IcecUI.advancedSettingsButton);
		ui.clickLink(IcecUI.restrictedButton);
		String restrictedMessage = "All Administrators, explicitly named Page Editors and named Page Readers can see the page";
		Assert.assertTrue(ui.fluentWaitTextPresent(restrictedMessage),
				"Restricted message not found.");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.pageReaderLabel),
				"Page Reader label not found.");
		
		logger.strongStep("Check anonymous option");
		log.info("INFO: Check anonymous option");
		ui.clickLink(IcecUI.anonymousButton);
		String anonymousMessage = "Everyone including unauthenticated users can see the page";
		Assert.assertTrue(ui.fluentWaitTextPresent(anonymousMessage),
				"Anonymous message not found.");
		
		logger.strongStep("Check public option");
		log.info("INFO: Check public option");
		ui.clickLink(IcecUI.publicButton);
		String publicMessage = "Authenticated users can see the page";
		Assert.assertTrue(ui.fluentWaitTextPresent(publicMessage),
				"Public message not found.");
		
		logger.strongStep("Check page editor user search");
		log.info("INFO: Check page editor user search");
		ui.typeText(IcecUI.pageEditorTextField, testUser.getDisplayName());
		Assert.assertTrue(ui.fluentWaitElementVisible("css=ul[id='ui-id-8']"),
				"User dropdown not found.");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName()),
				testUser.getDisplayName() + " not found.");
		
		// Navigation
		logger.strongStep("Click navigation checkbox and check options appear");
		log.info("INFO: Click navigation checkbox and check options appear");
		ui.clickLink(IcecUI.navigationCheckbox);
		String[] navLabels = {"Orientation", "Personalize", "Source"};
		checkPageSettingsLabelsPresent(navLabels);
		ui.clickLink(IcecUI.navigationCheckbox);
		
		// Set values and enable checkboxes
		logger.strongStep("Enter text and select options to test");
		log.info("INFO: Enter text and select options to test");
		String id = "xcc" + Helper.genDateBasedRandVal();
		String title = "xcc" + Helper.genDateBasedRandVal();
		ui.clearText(IcecUI.idTextField);
		ui.typeText(IcecUI.idTextField, id);
		ui.clearText(IcecUI.titleTextField);
		ui.typeText(IcecUI.titleTextField, title);
		ui.selectCheckbox(IcecUI.activityStreamCheckbox);
		ui.selectCheckbox(IcecUI.rightColumnCheckbox);
		ui.selectCheckbox(IcecUI.isTemplateCheckbox);
		ui.clickLink(IcecUI.pageSettingsSave);
		
		// Verify set values
		Assert.assertTrue(ui.getElementText(IcecUI.title).equals(title),
				"Title doesn't match expected title: " + title);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.activityStream),
				"Activity not found.");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.rightColumn),
				"Right column not found.");
		
		logger.strongStep("Check templates view");
		log.info("INFO: Check templates view");
		ui.clickLink(IcecUI.customizeButton);
		ui.clickLink(IcecUI.pageMgmtTab);
		ui.clickLink(IcecUI.advancedSettingsButton);
		ui.selectCheckbox(IcecUI.showTemplatesCheckbox);
		Assert.assertTrue(ui.fluentWaitTextPresent(id),
				id + " not found in list of templates.");

		logger.strongStep("Delete page");
		log.info("INFO: Delete page");
		ui.deletePageMgmtTemplate(id);
		
		cleanUpPageSettings();
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Admin Panel Page Settings - BVT.
	*<li><B>Step:</B> Login to xcc/main.
	*<li><B>Step:</B> Open Admin Panel.
	*<li><B>Step:</B> Open Page Management Tab.
	*<li><B>Step:</B> Create Page.
	*<li><B>Step:</B> Type text into filter that doesn't match.
	*<li><B>Verify:</B> Check 0 results are displayed.
	*<li><B>Step:</B> Type text into filter that does match.
	*<li><B>Verify:</B> Check results are displayed.
	*<li><B>Step:</B> Copy page.
	*<li><B>Verify:</B> Check page copied successfully.
	*<li><B>Step:</B> Delete page.
	*<li><B>Verify:</B> Check page deleted successfully.
	*<li><B>Step:</B> Set page as template.
	*<li><B>Step:</B> Show templates view.
	*<li><B>Verify:</B> Check expected pageId appears in templates view.
	*<li><B>Verify:</B> Check modified and modifier sections appear as expected.
	*<li><B>Step:</B> Click import button.
	*<li><B>Verify:</B> Check import button displays expected dialog.
	*<li><B>Verify:</B> Check export button is available.
	*<li><B>Step:</B> Delete created page.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void adminPanelPageManagement() {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Login to ICEC and navigate to Admin Panel Page Management tab");
		log.info("INFO: Logging in with user: " + testUser.getEmail());
		ui.loadComponent(Data.getData().ComponentIcec);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);
		ui.clickLink(IcecUI.customizeButton);
		ui.clickLink(IcecUI.pageMgmtTab);
		
		logger.strongStep("Create page in page management");
		log.info("INFO: Create page in page management");
		String pageId = "ICEC-" + Helper.genDateBasedRandVal();
		createPageInPageMgmt(pageId);
		
		//Test Filter
		logger.strongStep("Type text into filter that doesn't match");
		log.info("INFO: Type text into filter that doesn't match");
		ui.clearAndTypeText(IcecUI.searchTextField, "test" + Helper.genDateBasedRandVal());
		Assert.assertTrue(ui.fluentWaitTextPresent(IcecUI.FILTER_ZERO_RESULTS),
				"'" + IcecUI.FILTER_ZERO_RESULTS + "' text wasn't found. Is filtering working as expected?");
		
		logger.strongStep("Type text into filter that does match");
		log.info("INFO: Type text into filter that does match");
		ui.clearAndTypeText(IcecUI.searchTextField, pageId);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getPageMgmtPageIdLabel(pageId)),
				"ID doesn't contain expected text: " + pageId);
		
		// Check copy
		logger.strongStep("Copy page");
		log.info("INFO: Copy page");
		ui.clickLink(IcecUI.getPageMgmtCopyPage(pageId));
		ui.clickLink(IcecUI.modalYesButton);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getPageMgmtPageIdLabel(pageId + "-copy")),
				"ID doesn't contain expected text: " + pageId + "-copy");
		ui.deletePageMgmtTemplate(pageId + "-copy");
		Assert.assertFalse(ui.isElementPresent(IcecUI.getPageMgmtPageIdLabel(pageId + "-copy")),
				pageId + "-copy ID found. Failed to delete?");
		
		// Check is Template
		logger.strongStep("Set page as template");
		log.info("INFO: Set page as template");
		ui.clickLink(IcecUI.getPageMgmtEditPage(pageId));
		ui.selectCheckbox(IcecUI.isTemplateCheckbox);
		ui.clickLink(IcecUI.modalYesButton);
		
		ui.clickLink(IcecUI.advancedSettingsButton);
		ui.selectCheckbox(IcecUI.showTemplatesCheckbox);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getPageMgmtPageIdLabel(pageId)),
				"ID doesn't contain expected text: " + pageId);
		
		// Modifier and Modified labels check
		logger.strongStep("Check modified and modifier sections appear as expected");
		log.info("INFO: Check modified and modifier sections appear as expected");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName()),
				"Modifier not visible");
		
		logger.strongStep("Click import button");
		log.info("INFO: Click import button");
		ui.clickLink(IcecUI.importPagesButton);
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.selectFilesDropzone),
				"Select Files area not found");
		ui.clickLink(IcecUI.modalYesButton);
		
		logger.strongStep("Check export all button");
		log.info("INFO: Check export all button");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.exportAllPagesButton),
				"Export All Pages Button not found");
		
		logger.strongStep("Delete page");
		log.info("INFO: Delete page");
		ui.deletePageMgmtTemplate(pageId);
		
		ui.endTest();
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
	
	private void createPageInPageMgmt(String pageId) {
		ui.clickLink(IcecUI.createPageButton);
		ui.typeText(IcecUI.createPageIdField, pageId);
		ui.typeText(IcecUI.createPageTitleField, pageId);
		ui.clickLink(IcecUI.modalYesButton);
		ui.clickLink(IcecUI.modalYesButton);
	}
	
	private void cleanUpPageSettings() {
		ui.clickLink(IcecUI.pageSettingsTab);
		ui.clickLink(IcecUI.advancedSettingsButton);
		ui.clearText(IcecUI.titleTextField);
		ui.typeText(IcecUI.titleTextField, "xcc");
		ui.unselectCheckbox(IcecUI.activityStreamCheckbox);
		ui.unselectCheckbox(IcecUI.rightColumnCheckbox);
		ui.unselectCheckbox(IcecUI.isTemplateCheckbox);
		ui.clickLink(IcecUI.pageSettingsSave);
	}
}
