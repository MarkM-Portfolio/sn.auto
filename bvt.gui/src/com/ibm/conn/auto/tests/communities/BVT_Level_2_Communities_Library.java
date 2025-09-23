package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Executor.Alert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.appobjects.common.PreviewDialog;
import com.ibm.conn.auto.appobjects.library.FileThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.FolderThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.LibraryWidget;
import com.ibm.conn.auto.appobjects.library.SortArea;
import com.ibm.conn.auto.appobjects.library.SortArea.SortKey;
import com.ibm.conn.auto.appobjects.library.ViewSelector;
import com.ibm.conn.auto.appobjects.library.ThumbnailWidget;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Communities_Library extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities_Library.class);
	private CommunitiesUI ui;
	private FilesUI filesUI;
	private TestConfigCustom cfg;
	private User testUser, adminUser;
	private APICommunitiesHandler apiOwner;
	private BaseFile file1, file2;
	private boolean isOnPrem;
	private GatekeeperConfig gkc;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());	
				
		file1 = new BaseFile.Builder(Data.getData().file1)
							.extension(".jpg")
							.build();
				
		file2 = new BaseFile.Builder(Data.getData().file2)
							.extension(".jpg")						
							.build();
		
		// for checking Gatekeeper settings
		isOnPrem = cfg.getProductName().equalsIgnoreCase("onprem");
		adminUser = cfg.getUserAllocator().getAdminUser();
		if (isOnPrem) {
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else {
			gkc = GatekeeperConfig.getInstance(driver);
		}
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Upload a file into the library widget</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Load the Library widget</li>
	*<li><B>Step:</B>Upload a file into the library widget</li>
	*<li><B>Step:</B>Change to the Details view</li>
	*<li><B>Verify:</B>Check that the file has being uploaded and all options are present</li>
	*</ul>
	*/
	@Test(groups = { "level2", "bvt" })
	public void AddLibraryCommunity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Create a community base state object
		logger.strongStep("Create a new Community");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags(Data.getData().commonTag)
									 .description("Test Library inside community")
									 .build();
		
		
		//create community
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the Owned Communities view");
		log.info("INFO: Navigate to the Owned Communities view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Customize community - Add the Library widget
		logger.strongStep("Add the Library widget to the Community");
		log.info("INFO: Adding the " + BaseWidget.LIBRARY.getTitle() + " widget to Community: "+ community.getName());
		ui.addWidget(BaseWidget.LIBRARY);
			
		//Choose Library link in the left nav
		logger.strongStep("Choose the Library link in the left navigation menu");
		log.info("INFO: Select Library from left nav menu");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();

		//Click on the Library link to open the library widget
		logger.strongStep("Click on the Library link to open the Library widget");
		log.info("INFO: Click on the Library link to open the Library widget");
		filesUI.libraryFileUpload(file1.getName());
		
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		
		logger.strongStep("Change to the Details View");
		log.info("INFO: Change to the Details View");
		viewSelector.switchToView(ViewSelector.View.DETAILS_VIEW);
		
		//Verify different options for the file
		logger.weakStep("Verify the different options for the file");
		log.info("INFO: Verify the different options for the file");
		verifyFileUploadAndOptions(file1.getName(), 0);
		
		apiOwner.deleteCommunity(comAPI);
				
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>Upload a file into a folder</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add the Library widget</li>
	*<li><B>Step:</B>Open the library widget</li>
	*<li><B>Step:</B>Add a new folder</li>
	*<li><B>Step:</B>Change to the Details view</li>
	*<li><B>Step:</B>Upload a file into the folder</li>
	*<li><B>Verify:</B>Check that the file is uploaded and all options are available</li>
	*</ul>
	*/
	@Test(groups = { "level2", "bvt" })
	public void UploadFileToNewFolderInLibrary() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Create a community base state object
		logger.strongStep("Create a new Community");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags(Data.getData().commonTag)
									 .description("Test Library inside community")
									 .build();
		
		//create community
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add the Library widget to the Community");
		log.info("INFO: Add the Library widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the Owned Communities view");
		log.info("INFO: Navigate to the Owned Communities view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
	
		//Choose Library link in the left nav
		logger.strongStep("Choose the Library link in the left navigation menu");
		log.info("INFO: Select Library from left nav menu");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		
		//Create a new Folder
		logger.strongStep("Create a new Folder");
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);
		
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
      
		logger.strongStep("Change to the Details view");
		log.info("INFO: Change to the Details View");
		viewSelector.switchToView(ViewSelector.View.DETAILS_VIEW);
				
		//Open the folder just created perform a file upload
		logger.strongStep("Upload a file to the new folder");
		log.info("INFO: Upload a file to the new folder");
		filesUI.openFolderAndUploadFile(Data.getData().FolderName, file1.getName());
			
		//Verify different options for the file
		logger.weakStep("Verify the different options for the file");
		log.info("INFO: Verify the different options for the file");
		verifyFileUploadAndOptions(file1.getName(), 1);
		
		apiOwner.deleteCommunity(comAPI);
				
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Add a tag to a file in the library</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add the Library widget</li>
	*<li><B>Step:</B>Open the library widget</li>
	*<li><B>Step:</B>Add a new folder</li>
	*<li><B>Step:</B>Change to the Details view</li>
	*<li><B>Step:</B>Upload a file into the folder</li>
	*<li><B>Step:</B>Add a tag to the file</li>
	*<li><B>Verify:</B>Check that the file is uploaded</li>
	*<li><B>Verify:</B>Check that the file is associated with a tag</li>
	*<li><B>Verify:</B>Check that all the edit properties are available</li>
	*</ul>
	*/
	@Test(groups = { "level2", "bvt" })
	public void UploadFileToLibraryAndPerformActionsOnFile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String Tagname = "TagforLibrarytest" + Helper.genDateBasedRand();
		String testName = ui.startTest();

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags(Data.getData().commonTag)
									 .description("Test Library inside community")
									 .build();
		
		//create community
		logger.strongStep("Create a new Community");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add the Library widget to the Community");
		log.info("INFO: Add the Library widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the Owned Communities view");
		log.info("INFO: Navigate to the Owned Communities view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link in the left nav
		logger.strongStep("Choose the Library link in the left navigation menu");
		log.info("INFO: Select Library from left nav menu");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		
		//Create a new Folder	
		logger.strongStep("Create a new Folder");
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);

		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
      
		logger.strongStep("Change to the Details View");
        log.info("INFO: Change to the Details View");
        viewSelector.switchToView(ViewSelector.View.DETAILS_VIEW);
	
		//Now upload a file and Verify different options for the file
        logger.strongStep("Upload a file to the folder");
		log.info("INFO: Upload a file to the folder");
		filesUI.libraryFileUpload(file1.getName());
		
		logger.weakStep("Verify that the file uploaded and that the file options are present");
		log.info("INFO: Verify that the file uploaded and that the file options are present");
		verifyFileUploadAndOptions(file1.getName(), 2);
			
		//Add a tag	
		logger.strongStep("Add a tag to a the uploaded file");
		log.info("INFO: Add a tag to a the uploaded file");
		ui.clickLink(CommunitiesUIConstants.LibraryAddTags);
		driver.getSingleElement(CommunitiesUIConstants.LibraryAddTagsTextfield).type(Tagname);

		logger.strongStep("Save the new tag");
		log.info("INFO: Save the new tag");
		ui.clickButton("Save");
		
		//Validation
		logger.weakStep("Verify that 'Add Remove' tag is not present");
		log.info("INFO: Verify that 'Add Remove' tag is not present");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryAddRemoveTags),
						  "ERROR: 'Add Remove' Tag is not present");
		
		logger.weakStep("Verify that the tag name is present");
		log.info("INFO: Verify that the tag name is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(Tagname),
						   "ERROR: Tag name is not present");

		//CheckOut and CheckIn
		logger.strongStep("CheckOut and CheckIn the File");
		log.info("INFO: Checkout and CheckIn the File");
		ui.checkInCheckOutFile(4, 6);
		
		//Upload a new version
		logger.strongStep("Upload a new version of the File");
		log.info("INFO: Upload a new version of the File");
		ui.uploadNewVersionOfFile(file1.getName(), file2.getName());

		//expand with more link
		logger.strongStep("Expand with the 'More' link");
		log.info("INFO: Expand with the 'More' link");
		ui.clickLink(CommunitiesUIConstants.LibraryMoreLink + 8);

		
		//More Actions - Share/Copy & Move to Folder/Move to trash
		
		//Verify the option in More Actions
		logger.strongStep("Click on 'More Actions' and verify that the options are present");
		log.info("INFO: Click on 'More Actions' and verify that the options are present");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryMoreActions);
		
		log.info("INFO: Verify that the 'Edit Properties' option for the File exists");
		logger.weakStep("Verify that the 'Edit Properties' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Edit Properties)"),
							"ERROR: Unable to find the 'Edit Properties' option");

		log.info("INFO: Verify that the 'Share' option for the File exists");
		logger.weakStep("Verify that the 'Share' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Share)"),
							"ERROR: Unable to find the 'Share' option");
		
		log.info("INFO: Verify that the 'Stop Following' option for the File exists");
		logger.weakStep("Verify that the 'Stop Following' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Stop Following)"),
							"ERROR: Unable to find the 'Stop Following' option");
		
		log.info("INFO: Verify that the 'Copy to Folder' option for the File exists");
		logger.weakStep("Verify that the 'Copy to Folder' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Copy to Folder)"),
							"ERROR: Unable to find the 'Copy to Folder' option");
		
		log.info("INFO: Verify that the 'Move to Folder' option for the File exists");
		logger.weakStep("Verify that the 'Move to Folder' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Move to Folder)"),
							"ERROR: Unable to find the 'Move to Folder' option");
		
		log.info("INFO: Verify that the 'Move to Trash' option for the File exists");
		logger.weakStep("Verify that the 'Move to Trash' option for the File exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActionOption + ":contains(Move to Trash)"),
							"ERROR: Unable to find the 'Move to Trash' option");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>Explore several parts of the Grid View and it's associated functionality</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add the Library widget</li>
	*<li><B>Step:</B>Open the library widget</li>
	*<li><B>Step:</B>Add a new folder</li>
	*<li><B>Step:</B>Upload a file</li>
	*<li><B>Verify:</B>Check that the Grid View is selected by default</li>
	*<li><B>Step:</B>Change to the Details View</li>
	*<li><B>Verify:</B>Check that the Details View is selected</li>
	*<li><B>Step:</B>Change to the Grid View</li>
	*<li><B>Verify:</B>Check that the Grid View is selected</li>
	*<li><B>Verify:</B>Check that a FolderThumbnailWidget is present with uniqueId ending in 2</li>
	*<li><B>Step:</B>Click the back-side action of the FolderThumbnailWidget to view that folder's contents</li>
	*<li><B>Step:</B>Click the Library breadcrumb to go back to viewing the Library's contents</li>
	*<li><B>Verify:</B>Check that a FileThumbnailWidget is present with uniqueId ending in 1</li>
	*<li><B>Step:</B>Click the "Download" action of the FileThumbnailWidget to initiate the download of a file</li>
	*<li><B>Step:</B>Click the "Preview" action of the FileThumbnailWidget to make the Preview Dialog for that file appear</li>
	*<li><B>Step:</B>Click the "Summary" action of the FileThumbnailWidget to navigate to the Document Summary page</li>
	*<li><B>Verify:</B>Check that we are on a Document Summary Page viewing a file's details</li>
	*<li><B>Step:</B>Click the Library breadcrumb to go back to viewing the Library's contents</li>
	*<li><B>Verify:</B>Check that the sorting header is present</li>
	*<li><B>Verify:</B>Check that the current sort method is the key \"Updated\" with the order \"descending\"</li>
	*<li><B>Step:</B>Click the link to update the sort method for \"Updated\"</li>
	*<li><B>Verify:</B>Check that the new sort method is the key \"Updated\" with the order \"ascending\"</li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvt" })
	public void exploreGridView() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags(Data.getData().commonTag)
									 .description("Test Library inside community")
									 .build();
		
		//create community
		logger.strongStep("Create a new Community");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add the Library widget to the Community");
		log.info("INFO: Add the Library widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the Owned Communities view");
		log.info("INFO: Navigate to the Owned Communities view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link in the left nav
		logger.strongStep("Choose the Library link in the left navigation menu");
		log.info("INFO: Select Library from left nav menu");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		
		//Create a new Folder
		logger.strongStep("Create a new Folder");
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);
	
		//Upload a file
		logger.strongStep("Upload a file and verify that the options for the file are present");
		log.info("INFO: Upload a file and verify that the options for the file are present");
		filesUI.libraryFileUpload(file1.getName());
		
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		
		//Now that we have some library contents, check that the grid view is now the default view (assumes ccmMediaView is enabled on the server)
		logger.weakStep("Verify that the Grid View selector is present and active by default");
		log.info("INFO: Verify that the Grid View selector is present and active by default");
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
							"ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");
		
		logger.strongStep("Change to the Details View");
		log.info("INFO: Change to the Details View");
		viewSelector.switchToView(ViewSelector.View.DETAILS_VIEW);
		
		logger.weakStep("Verify that the Details View selector is present and active (1st time)");
		log.info("INFO: Verify that the Details View selector is present and active");
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.DETAILS_VIEW),
							"ERROR: Unable to find the Library Details View Selector or it is not the currently active view");
		
		logger.strongStep("Change to the Grid View");
		log.info("INFO: Change to the Grid View");
		viewSelector.switchToView(ViewSelector.View.GRID_VIEW);
		
		logger.weakStep("Verify that the Grid View selector is present and active (2nd time)");
		log.info("INFO: Verify that the Grid View selector is present and active again");
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
							"ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");

		FolderThumbnailWidget folderTW = libraryWidget.getDocMain().getFolderThumbnailWidgetByName(Data.getData().FolderName);
		
		verifyGridItemPresence(folderTW);
		
		logger.weakStep("Verify that the 'Open' action on a Folder Thumbnail Widget works by clicking the back-side action");
		log.info("INFO: Verify that the 'Open' action on a Folder Thumbnail Widget works by clicking the back-side action");
		folderTW.getBackSideActionLink().click();
		
		logger.strongStep("Go back to the Document Main page");
		log.info("INFO: Going back to the Document Main page");
		libraryWidget.getDocMain().getBreadcrumbs().goBackToLibrary();
		
		FileThumbnailWidget fileTW = libraryWidget.getDocMain().getFileThumbnailWidgetByName(file1.getName());
		verifyGridItemPresence(fileTW);
		
		logger.weakStep("Verify that the 'Download' action on a File Thumbnail Widget works by simply clicking it and not verifying that the file is actually downloaded");
		log.info("INFO: Verify that the 'Download' action on a File Thumbnail Widget works by simply clicking it and not verifying that the file is actually downloaded");
		fileTW.getDownloadActionLink().click();
		
		//TODO: Update this part of the test with item 129041.
		//TODO: Change log message accordingly, add assert that the Preview Dialog comes up, remove enter key line, and close the dialog.
		logger.weakStep("Verify that the 'Preview' action on a File Thumbnail Widget works by opening the Preview Dialog");
		log.info("INFO: Verify that the 'Preview' action on a File Thumbnail Widget works by opening the Preview Dialog");
		fileTW.getPreviewActionLink().click();
		PreviewDialog previewDialog = new PreviewDialog(driver);
		previewDialog.close();
		
		logger.weakStep("Verify that the 'Summary' action on a File Thumbnail Widget works and takes us to the Document Summary page");
		log.info("INFO: Verify that the 'Summary' action on a File Thumbnail Widget works and takes us to the Document Summary page");
		fileTW.getSummaryActionLink().click();
		
		verifyOnFileDocSummaryPage(file1.getName());
		
		logger.strongStep("Go back to the Document Main page");
		log.info("INFO: Going back to the Document Main page");
		libraryWidget.getDocSummary().getBreadcrumbs().goBackToLibrary();
		
		SortArea sortArea = libraryWidget.getDocMain().getSortArea();
		
		logger.weakStep("Verify that the sorting header is present");
		log.info("INFO: Verify that the sorting header is present");
		Assert.assertNotNull(sortArea.getSortArea());
		
		logger.weakStep("Verify that the current sort method is the key 'Updated' with the order 'Descending;");
		log.info("INFO: Verify that the current sort method is the key 'Updated' with the order 'Descending'");
		Assert.assertTrue(sortArea.isDescending(SortKey.UPDATED));
		
		logger.strongStep("Click the link to update the sort method for 'Updated'");
		log.info("INFO: Click the link to update the sort method for 'Updated'");
		sortArea.updateSort(SortKey.UPDATED);
		
		logger.weakStep("Verify that the new sort method is the key 'Updated' with the order 'Ascending'");
		log.info("INFO: Verify that the new sort method is the key 'Updated' with the order 'Ascending'");
		Assert.assertTrue(sortArea.isAscending(SortKey.UPDATED));

		// TODO: Once the Thumbnail widgets have their listitem ordering set so they know which file they are, compare that the widgets are sorted in the correct order. 
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	private LibraryWidget getLibraryWidgetOnFullWidgetPage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Get the Library widget on the full widget page");
		//Check if the LibraryTitle is not present and refreshes the page.
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer).isElementPresent(CommunitiesUIConstants.LibraryTitle))
			ui.fluentWaitPresentWithRefresh(CommunitiesUIConstants.LibraryTitle);
		return new LibraryWidget(driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer));
	}
	
	private void verifyGridItemPresence(ThumbnailWidget tw) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.weakStep("Verify that the grid item is present in the grid view");
		
		log.info("INFO: Verify that " + tw.getId() + " is present");
		Assert.assertNotNull(tw.getWidgetElement(),
				"ERROR: Unable to find " + tw.getId() + " in the Grid View");
	}

	private void verifyFileUploadAndOptions(String FileUploadName, int NoOfMoreLink) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.weakStep("Verify that the File is uploaded and present in the view");
		log.info("INFO: Verify that the File is upload and present in the view");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.UploadedFileNameInView+"('"+FileUploadName+"')").getText().contains(FileUploadName),
							"ERROR: Unable to find the File name in the view");

		logger.strongStep("Click on the 'More link and verify that the options are present");
		log.info("INFO: Click on the 'More' link and verify that the options are present");
		ui.clickLink(CommunitiesUIConstants.LibraryMoreLink+NoOfMoreLink);

		logger.weakStep("Verify that 'Add Tags' is present");
		log.info("INFO: Verify that 'Add Tags' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryAddTags),
							"ERROR: Unable to find 'Add Tags'");

		logger.weakStep("Verify that 'Download' is present");
		log.info("INFO: Verify that 'Download' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryDownload),
							"ERROR: Unable to find 'Download'");

		
		log.info("INFO: Verify that 'New Version' is present");
		logger.weakStep("Verify that 'New Version' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryNewVersion),
							"ERROR: Unable to find 'New Version'");

		log.info("INFO: Verify that 'Checkout' is present");
		logger.weakStep("Verify that 'Checkout' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryCheckOut),
							"ERROR: Unable to find 'Checkout'");

		log.info("INFO: Verify that 'More Actions' is present");
		logger.weakStep("Verify that 'More Actions' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryMoreActions),
							"ERROR: Unable to find 'More Actions'");

		log.info("INFO: Verify that 'View Trash' is present");
		logger.weakStep("Verify that 'View Trash' is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.LibraryViewTrash),
							"ERROR: Unable to find 'View Trash'");
	}

	private void verifyOnFileDocSummaryPage(String fileName){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.weakStep("Verify that we are on the Document Summary page viewing the File's details");
		
		log.info("INFO: Verify that we are on the Document Summary page viewing the '" + fileName + "' file's details");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.LibraryDocSummaryFileTitleWithTextSuffix+"('"+fileName+"')").getText().contains(fileName),
							"ERROR: Unable to find the file's title on a Document Summary page");
	}

	private void closeAlertBox(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Close Alert Box");
		
		Alert alert = driver.switchToAlert();
		alert.accept();
	}
}