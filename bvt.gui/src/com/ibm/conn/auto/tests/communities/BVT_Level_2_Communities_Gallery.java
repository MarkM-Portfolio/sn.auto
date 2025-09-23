package com.ibm.conn.auto.tests.communities;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_Communities_Gallery extends SetUpMethods2 {

	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Communities_Gallery.class);

	private CommunitiesUI ui;
	private FilesUI fUI;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private User testUser;
	private String serverURL;
	private boolean isOnPrem;
	
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		// for checking Gatekeeper settings
		isOnPrem = cfg.getProductName().equalsIgnoreCase("onprem");
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Add the gallery widget to a community</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the gallery widget</li>
	 * <li><B>Verify: </B>Check the gallery widget was added</li>
	 * @throws Exception
	 */
	
	@Test(groups = { "regression", "bvt", "regressioncloud"})
	public void addGalleryWidget() throws Exception {	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		boolean found = false;
		
		
		ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName() + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);	
			
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities views");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
				
		logger.strongStep("Add the gallery (title of widget) to the community");
		log.info("INFO: Adding the " + BaseWidget.GALLERY.getTitle()
				 + " widget to community: " + community.getName());
		ui.addWidget(BaseWidget.GALLERY);


		log.info("INFO: Validate that the Gallery Widget was added");
        logger.strongStep("Validate that the Gallery Widget was added");
		List<Element> elements = driver.getElements(CommunitiesUIConstants.rightsideGalleryWidget);
		for (Element element : elements){
			log.info("INFO: Right side Widget" + element.getText());
			if(element.getText().contentEquals("Gallery")){
				found = true;
				break;
			}
		}
		logger.weakStep("Text displays- Error: Unable find 'Gallery' widget on right side widget container");
		Assert.assertTrue(found, 
						 "ERROR: Unable find 'Gallery' widget on right side widget container");
		
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
			
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Add files to the gallery widget</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the Gallery widget</li>
	 * <li><B>Step: </B>Upload a file into Community Files</li>
	 * <li><B>Step: </B>Setup the gallery widget to point to all community files</li>
	 * <li><B>Verify: </B>Gallery widget title reflects the name of the community</li>
	 * <li><B>Verify: </B>Uploaded file appears in the gallery widget</li>
	 * <li><B>Step: </B>Click the thumbnail of the uploaded file</li>
	 * <li><B>Verify: </B>The lightbox containing the thumbnail appears</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression", "bvt", "regressioncloud"})
	public void showAllFiles() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName() + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
									 .comFile(true)
									 .extension(".jpg")
									 .rename(Helper.genDateBasedRand())
									 .build();
		
		ui.startTest();

		//create community
		logger.strongStep("create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("add widget");
		log.info("INFO: Add media gallery widget to community");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		logger.strongStep("Upload file via the API");
		log.info("INFO: Upload file via the API");
		FileViewerUI uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		uiViewer.upload(fileA, testConfig, testUser, commAPI);
		fileA.setName(fileA.getRename()+ fileA.getExtension());
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities views");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Files from menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}

		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community left navigation menu");
			log.info("INFO: Select Overview from community left navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		// point Gallery to show all Community files
		logger.strongStep("Point gallery to show all community files");
		pointToAllCommunityFiles();
		
		logger.weakStep("Verify the gallery is in the community with the community title");
		Assert.assertEquals(getGalleryTitle(commAPI), community.getName(),
				"INFO: Verified the gallery is in the Community with the Community title ");
				

		// Verify the Gallery has thumbnail for every file uploaded
		logger.weakStep("Verify the gallery has a thumbnail for every file uploaded");
		Assert.assertTrue(
				driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"INFO: verified thumbnail for fileA is present in Gallery ");

		// click of the fileA thumbnail
		logger.weakStep("Click on the fileA thumbnail");
		ui.scrollToWidget(BaseWidget.GALLERY);
		ui.clickLink(CommunitiesUI.getFileThumbnail(fileA));

		assertPreviewShowing(fileA);
		
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Add and change names of folders in the Gallery</li>
	 * <li><B>Step: </B>Create a community
	 * <li><B>Step: </B>Add the Gallery widget</li>
	 * <li><B>Step: </B>Create a folder named BVT Folder</li>
	 * <li><B>Step: </B>Configure Gallery to Point to BVT Folder</li>
	 * <li><B>Verify: </B>Gallery has folder name</li>
	 * <li><B>Step: </B>Change Folder name to BVT Folder New</li>
	 * <li><B>Verify: </B>Gallery reflects folder name change</li>
	 * <li><B>Step: </B>Delete the folder</li>
	 * <li><B>Verify: </B>Correct missing folder message appears in the gallery</li> 
	 * </ul>
	 * 
	 * @throws Exception
	 */
	// Disabling this test until it is fixed  - CNXTEST-2807 ticket to enable it
	@Test(groups = { "level2", "bvt", "regressioncloud"}, enabled=false)
	public void configureWithEmptyFolders() throws Exception {

		String gk_dnd_flag = "files-tree-dnd";
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName() + Helper.genDateBasedRand())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag)
									.description("Test Gallery inside community")
									.build();

		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add widget");
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Files from menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		

		BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName)
				.description(Data.getData().FolderDescription).build();
        
		logger.strongStep("Create a folder");
		log.info("INFO: Creating a folder");
		folder.add(fUI);

		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community left navigation menu");
			log.info("INFO: Select Overview from community left navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		// Set up the Gallery. Open the Folder Picker and select the folder
		logger.strongStep("Set up the Gallery. Open the Folder Picker");
		ui.scrollToWidget(BaseWidget.FILES);
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);
		clickFolder(CommunitiesUIConstants.radioFirstFolder);
		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();
        
		logger.weakStep("Text displays- ERROR: Gallery does not have correct title, which is the name of the folder");
		Assert.assertEquals(getGalleryTitle(commAPI), Data.getData().FolderName,
						  "ERROR: Gallery does not have correct title, which is the name of the folder");

		logger.weakStep("Text displays- ERROR: Gallery does not show appropriate no files in folder message");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.noFilesToDisplay),
						  "ERROR: Gallery does not show appropriate no files in folder message");

		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			driver.executeScript("window.scrollTo(0, 0)");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		
		//Click on My Folder to see your folder
	    logger.strongStep("Click on Community Folders ");
		log.info("INFO: Click on Community Folders");
		if ( ui.checkGKSetting(gk_dnd_flag))
			ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
		else
			ui.clickLinkWait(FilesUIConstants.ComFolderTab);
		//Click on display list to show folders in list
        logger.strongStep("Switch to List view");
        log.info("INFO: Switch to List view");
        ui.clickLinkWait(FilesUIConstants.DisplayList);
         
		//Click on your folder 
        logger.strongStep("Open your folder");
		log.info("INFO: Open your folder");
		ui.clickLinkWait(FilesUI.selectMyFolder(folder));
		
		logger.strongStep("Update folder name using Edit Properties");
		ui.changeFolderNameInFileWidget(Data.getData().EditedFolderName, true);
		Assert.assertTrue(ui.isTextPresent(Data.getData().EditedFolderName + " was saved successfully"),
				"Error: Folder name update confirmation not found.");
		
		logger.strongStep("Go back to Overview");
		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community navigation menu");
			log.info("INFO: Select Overview from community navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}

		logger.strongStep("Check the title of the Gallery widget is updated");
		log.info("Check the title of the Gallery widget is updated");
		// sometimes the old title flash for a sec before it's updated so wait for the widget to load first
		ui.fluentWaitElementVisible(CommunitiesUIConstants.GalleryWidget);
		Assert.assertEquals(getGalleryTitle(commAPI), Data.getData().EditedFolderName,
						  "ERROR: Gallery title not updated ");

		BaseFolder folderNew = new BaseFolder.Builder(Data.getData().EditedFolderName)
		.description(Data.getData().EditedFolderName).build();

		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		
		//Click on My Folder to see your folder
	    logger.strongStep("Click on Community Folders ");
		log.info("INFO: Click on Community Folders");
		if ( ui.checkGKSetting(gk_dnd_flag))
			ui.clickLinkWait(FilesUIConstants.navCommunityFolders);
		else
			ui.clickLinkWait(FilesUIConstants.ComFolderTab);
		
		//Click on display list to show folders in list
        logger.strongStep("Switch to List view");
        log.info("INFO: Switch to List view");
        ui.clickLinkWait(FilesUIConstants.DisplayList);
         
		//Click on your folder
        logger.strongStep("Open your folder");
		log.info("INFO: Open your folder");
		ui.clickLinkWait(FilesUI.selectMyFolder(folderNew));
		
		logger.strongStep("Delete the folder using Edit Properties");
		ui.deleteFolderInFileWidget(Data.getData().EditedFolderName, true);
		Assert.assertTrue(ui.isTextPresent("The folder was deleted"),
				"Error: Folder deletion confirmation not found.");
        
		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community left navigation menu");
			log.info("INFO: Select Overview from community left navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		logger.weakStep("Verify Gallery displays correct deleted folder information");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.GalleryWidget);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.deletedFolder),
				"INFO: Verified Gallery displayes correct deleted folder information");
		
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Check thumbnails of folders work</li>
	 * <li><B>Step: </B>Create a community
	 * <li><B>Step: </B>Add the Gallery widget</li>
	 * <li><B>Step: </B>Upload a file into Community Files</li>
	 * <li><B>Step: </B>Create new Folder called 'BVT Folder'</li>
	 * <li><B>Step: </B>Add File to 'BVT Folder'</li>
	 * <li><B>Step: </B>Setup the gallery widget to point to 'BVT Folder' files</li>
	 * <li><B>Verify: </B>Uploaded file appears in the gallery widget</li>
	 * <li><B>Step: </B>Click the thumbnail of the uploaded file</li>
	 * <li><B>Verify: </B>The lightbox containing the thumbnail appears</li>
	 * <li><B>Step: </B>Close out of the lightbox</li>
	 * <li><B>Step: </B>Click on View All link</li>
	 * <li><B>Verify: </B>User is taken to the folder files page</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt", "regressioncloud"})
	public void viewAllLinkWithFolderFiles() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName() + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file2)
									 .comFile(true)
									 .extension(".jpg")
									 .build();

		BaseFolder folderA = new BaseFolder.Builder(Data.getData().FolderName)
										   .description(Data.getData().FolderDescription)
										   .build();
		
		ui.startTest();

		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add widget");
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Files from menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}

		logger.strongStep("Upload file");
		log.info("INFO: Upload file");
		fileA.upload(fUI);

		logger.strongStep("Add Folder");
		log.info("INFO: Add Folder");
		folderA.add(fUI);

		//add file to folder
		logger.strongStep("Add file to folder");
		log.info("INFO: Add file to folder");
		fileA.addToFolder(fUI, folderA);

		// go back to Overview
		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community left navigation menu");
			log.info("INFO: Select Overview from community left navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		logger.strongStep("Set up a Gallery with Folder picker");
		log.info("INFO: Setting up a Gallery with Folder picker");
		// open the Folder Picker a
		ui.clickLink(CommunitiesUIConstants.setupGalleryLink);

		// Choose the 1st Folder
		logger.strongStep("Choose the 1st Folder");
		clickFolder(CommunitiesUIConstants.radioFirstFolder);

		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();

		logger.weakStep("Verify thumbnail element is present in Gallery");
		Assert.assertTrue(
				driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"INFO: verified thumbnail element is present in Gallery ");

		// click on Thumbnail
		logger.strongStep("Click on Thumbnail");
		ui.scrollToWidget(BaseWidget.GALLERY);
		ui.clickLink(CommunitiesUI.getFileThumbnail(fileA));

		logger.weakStep("Assert Preview fileA");
		assertPreviewShowing(fileA);
		
		// close Thumbnail
		logger.weakStep("Close Thumbnail");
		closePreview();

		log.info("INFO: Clicking Gallery View All link");
		logger.strongStep("Clicking Gallery View All link");
		// Click View All Link
		ui.clickLink(CommunitiesUI.getViewAllLink(1));

		logger.strongStep("User is taken to Folder File page by clicking on View All Link");
		log.info("INFO: User is taken to Folder File page by clicking on View All Link");

		// Verify it take us to Folder Files page
		logger.weakStep("Verify that it takes us to Folder Files page");
		Assert.assertTrue(
				driver.isElementPresent(CommunitiesUI.getFolderHeading(Data
						.getData().FolderName)),
				"INFO: Verified Folder files page View All Link took us to has correct heading ");
		
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Add a file to a folder, edit the gallery to point to the folder</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the Gallery widget</li>
	 * <li><B>Step: </B>Create new Folder called 'BVT Folder'</li>
	 * <li><B>Step: </B>Upload a file into Community Files</li>
	 * <li><B>Step: </B>Add File to 'BVT Folder'</li>
	 * <li><B>Step: </B>Setup the gallery widget to All Files</li>
	 * <li><B>Verify: </B>Gallery widget title reflects the name of the community</li>
	 * <li><B>Step: </B>Click "Edit" Gallery</li>
	 * <li><B>Step: </B>Re-configure gallery to point to the empty folder</li>
	 * <li><B>Verify: </B>Gallery re-configured correctly and has folder name title</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "level2", "bvt", "regressioncloud"})
	public void galleryEdit() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName() + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file2)
									 .comFile(true)
									 .extension(".jpg")
									 .rename(Helper.genDateBasedRand())
									 .build();

		ui.startTest();
		
		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add widget");
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		logger.strongStep("Upload file via the API");
		log.info("INFO: Upload file via the API");
		FileViewerUI uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		uiViewer.upload(fileA, testConfig, testUser, commAPI);
		fileA.setName(fileA.getRename()+ fileA.getExtension());
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Select Files from menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed nav menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Files from left navigation menu");
			log.info("INFO: Select Files from left nav menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}

		logger.strongStep("Add Folder");
		log.info("INFO: Add Folder");
		BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName)
				.description(Data.getData().FolderDescription).build();

		logger.strongStep("Create a folder");
		log.info("INFO: Creating a folder");
		folder.add(fUI);
		
		// Add file to folder
		logger.strongStep("Add file to folder");
		log.info("INFO: Add file to folder");		
		fileA.addToFolder(fUI, folder);

		if (value)
		{
			logger.strongStep("Select overview from the tabbed navigation menu");
			log.info("INFO: Select Overview from the tabbed nav menu");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			logger.strongStep("Select overview from community left navigation menu");
			log.info("INFO: Select Overview from community left navigation menu");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		// point Gallery to show all community files
		logger.weakStep("Point Gallery to show all community files");
		log.info("INFO: Configure Gallery to show All Community Files");
		pointToAllCommunityFiles();
		Assert.assertEquals(getGalleryTitle(commAPI), community.getName() ,
				"INFO: Verified the gallery is in the community with title ");

		// click on Gallery menu, and click Edit
		logger.strongStep("Click on gallery menu, and click edit");
		log.info("INFO: Edit Gallery Configuration");
		ui.waitForPageLoaded(driver);
		ui.clickLinkWithJavascript(CommunitiesUIConstants.galleryConfig);
		ui.clickLinkWithJavascript(CommunitiesUIConstants.menuOption + ":contains(Edit)");

		// re-configure Gallery to show Folder
		logger.strongStep("Re-configure gallery to point to 1st folder");
		log.info("INFO: Re-configuring Gallery to point to 1st folder");
		
		ui.waitForPageLoaded(driver);
		driver.turnOffImplicitWaits();

		if (!clickFolder(CommunitiesUIConstants.folderSecondTime)) {
			clickFolder(CommunitiesUIConstants.radioFirstFolder);
		}
		driver.turnOnImplicitWaits();

		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();

		logger.strongStep("Successfully re-configured Gallery to show folder files");
		log.info("INFO: Successfully re-configured Gallery to show folder files");
		
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}
	
	/**
	* <ul>
	* <li><B>Info: </B>Redirect the Gallery Widget to All Files</li>
	* <li><B>Step: </B>Navigate to personal files and upload a file</li>
	* <li><B>Step: </B>Navigate back to the communities page</li>
	* <li><B>Step: </B>Create a community
	* <li><B>Step: </B>Add the Gallery widget</li>
	* <li><B>Step: </B>Configure the gallery widget so that it points to All Files</li>
	* <li><B>Step: </B>Upload a file into Community Files</li>
	* <li><B>Verify: </B>Gallery widget refreshes with the new file</li>
	* <li><B>Step: </B>Add the file that was uploaded to personal files to the community</li>
	* <li><B>Verify: </B>The gallery widget refreshes with the new file</li>
	* <li><B>Step: </B>Add a folder Files</li>
	* <li><B>Verify: </B>Gallery widget does not refresh following the addition of the folder</li>
	* <li><B>Step: </B>Click on View All link</li>
	* <li><B>Verify: </B>The user is redirected to the folder files page</li>
	* </ul>
	*@throws Exception
	*/
	// Disabled temporarily which causing pipeline failure
	//To-do: Enable once fixed (CNXTEST-2715)
	@Test(groups = { "level2", "bvt", "regressioncloud"}, enabled=false)
	public void automaticRefreshOnFileUpload() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Thread.currentThread().getName()+ Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags(Data.getData().commonTag)
		 							.description("Test Gallery inside community")
		 							.build();

		BaseFile personalFile = new BaseFile.Builder(Data.getData().file1)
				.comFile(false)
				.performFromOverview(true)
				.extension(".jpg")
				.build();
		
		BaseFile communityFile = new BaseFile.Builder(Data.getData().file2)
				.comFile(true)
				.performFromOverview(true)
				.extension(".jpg")
				.build();
									  
		ui.startTest();

		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add widget");
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		//Check Gatekeeper value for Catalog UI Updated [6.0 CR3]
		String gk_flag_catalog_ui_updated =  Data.getData().gk_catalog_ui_updated_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_catalog_ui_updated + " setting is enabled");
		boolean catalogUiUpdated = ui.checkGKSetting(gk_flag_catalog_ui_updated);
		
		//Check Gatekeeper value for Card View[6.0 CR4]
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");


		// Upload a file to Personal Files
		logger.strongStep("Upload a file to personal files");
		log.info("INFO: Uploading a file");
		personalFile.upload(fUI);
		
		log.info("INFO: Close guided tour if obscurs page element");
		driver.executeScript(ui.getCloseTourScript());
		
		// In order to support Cloud, 6.0 CR2, and the new Community Catalog UI in 6.0 CR3
		// we have to handle three different scenarios to get to the "I'm an Owner" view.
		
		//Click on Communities from the top nav. In cloud this will open the Community Catalog
		// On prem, this will open the Community dropdown megamenu
		logger.strongStep("Select Communities link from the mega-menu");
		log.info("INFO: Select Communities link from the mega-menu");
		ui.clickLinkWait(ui.getCommunitiesMegaMenu());
		
		// Click the new "My Communities" megamenu item and then select "I'm an Owner" from the left nav
		if(isOnPrem && catalogUiUpdated){
			//Select My Communities from the Communities Mega-Menu dropdown menu
			log.info("INFO: Select My Communities from the Communities mega-menu dropdown");
			logger.strongStep("Select My Communities from the Communities mega-menu dropdown");
			driver.getFirstElement(ui.getCommunitiesMegaMenuMyCommunities()).click();
			
			//Select I'm an Owner from the left nav bar
			log.info("INFO: Select I'm an Owner from the Community Catalog left nav bar");
			logger.strongStep("Select I'm an Owner from the Community Catalog left nav bar");
			if (isCardView){
				ui.goToOwnerCardView();
			}
			else {
			   Community_View_Menu.IM_AN_OWNER.select(ui);
			}
			
		// Click on "I'm an Owner" from the megamenu
		} else if(isOnPrem){
			//Select I'm an Owner from the Communities Mega-Menu dropdown menu
			log.info("INFO: Select I'm an Owner from the Communities mega-menu dropdown");
			logger.strongStep("Select I'm an Owner from the Communities mega-menu dropdown");
			driver.getFirstElement(ui.getCommunitiesMegaMenuOwner()).click();
			
		// For Cloud, click on "I'm an Owner" from the left nav
		} else {
			//Select I'm an Owner from the left nav bar
			log.info("INFO: Select I'm an Owner from the Community Catalog left nav bar");
			logger.strongStep("Select I'm an Owner from the Community Catalog left nav bar");
			Community_View_Menu.IM_AN_OWNER.select(ui);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Configuring Gallery to point to All Files");
		log.info("INFO: Configuring Gallery to point to All Files");
		pointToAllCommunityFiles();
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		// Upload a file to the community
		logger.strongStep("Upload a file to the community");
		log.info("INFO: Uploading a file to the community");
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			Community_TabbedNav_Menu.FILES.select(ui);
			communityFile.upload(fUI);
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			Community_LeftNav_Menu.FILES.select(ui);
			communityFile.upload(fUI);
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		// Make sure the Gallery is refreshed with the new thumbnail
		logger.weakStep("Make sure the Gallery is refreshed with the new thumbnail");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(communityFile)),
				"INFO: Verified Gallery has refreshed with the new file " + communityFile.getName());
		
		// Share the file from Personal Files with the community
		logger.strongStep("Share the file from Personal Files with the community");
		log.info("INFO: Adding the personal file to the community");
		personalFile.setComFile(true);
		personalFile.share(fUI);
		
		// Make sure the Gallery is refreshed with the new thumbnail
		logger.weakStep("Make sure the Gallery is refreshed with the new thumbnail");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(personalFile)),
				"INFO: Verified Gallery has refreshed with the new file " + personalFile.getName());
		
		// Store the current gallery widget id.
		logger.strongStep("Store the current gallery widget id.");
		String initialGalleryWidgetId = ui.getFirstVisibleElement(CommunitiesUIConstants.GalleryWidget).getAttribute("id");

		log.info("INFO: Creating a folder");
		logger.strongStep("Create a folder");
		BaseFolder folder = new BaseFolder.Builder("test" + Helper.genDateBasedRand())
									      .description(Data.getData().FolderDescription)
									      .performFromOverview(true)
									      .build();
		folder.add(fUI);
		
		// Allow time for the Gallery widget to refresh after the folder has been added
		logger.strongStep("Allow time for the Gallery widget to refresh after the folder has been added");
		log.info("INFO: Waiting to allow time for the gallery widget to refresh");

		
		// Verify that the Gallery widget did not actually refresh
		// (If it had refreshed, the number at the end of the ID will have changed)
		logger.strongStep("Checking that the gallery has not refreshed since adding the folder");
		log.info("INFO: Checking that the gallery has not refreshed since adding the folder");
		logger.weakStep("Verified that gallery widget ID has not changed since adding the folder");
		String currentGalleryWidgetId = ui.getFirstVisibleElement(CommunitiesUIConstants.GalleryWidget).getAttribute("id");
		Assert.assertEquals(initialGalleryWidgetId, currentGalleryWidgetId,
				"INFO: Verified that gallery widget ID has not changed since adding the folder");
		
		// Click View All Link
		logger.strongStep("Click View All Link");
		ui.scrollToWidget(BaseWidget.GALLERY);
		log.info("INFO: Clicking Gallery View All link");
		ui.clickLink(CommunitiesUI.getViewAllLink(2));

		logger.strongStep("User is taken to the folder file by clicking on the View All Link");
		log.info("INFO: User is taken to Folder File page by clicking on View All Link");

		// Verify it takes us to the main Files page
		logger.weakStep("Verify that 'folder files page view all link' took us to the correct heading");
		Assert.assertTrue(
				driver.isElementPresent(CommunitiesUI.getFolderHeading(Data
						.getData().RootName)),
				"INFO: Verified Folder files page View All Link took us to has correct heading");
		
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}
	
	private String getGalleryTitle(Community commAPI) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
        String commUUID = apiOwner.getCommunityUUID(commAPI);
		log.info("INFO: commUID is " + commUUID);
		
		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Gallery");
		log.info("INFO: Gallery id is " + widgetID);

		ui.fluentWaitElementVisible(CommunitiesUI.getWidgetTitle(widgetID));
		String galleryName = driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetID)).getText();
		log.info("INFO: Gallery name is " + galleryName);
		
		return galleryName;	
	}

	private void pointToAllCommunityFiles() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		// Open the Folder Picker
		logger.strongStep("Open the Folder Picker");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.setupGalleryLink);
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		// configure to show All Files
		logger.strongStep("Configure to show 'All Files' ");
		driver.turnOffImplicitWaits();
		ui.fluentWaitElementVisibleOnce(CommunitiesUIConstants.filePickerStream);
		
		if (driver.getVisibleElements(CommunitiesUIConstants.filePickerAllFiles).size() > 0) {
			ui.getFirstVisibleElement(CommunitiesUIConstants.filePickerAllFiles).click();
		}

		// Accept and close the Folder Picker
		logger.strongStep("Accept and close the Folder Picker");
		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();
		driver.turnOnImplicitWaits();
	}

	private boolean clickFolder(String selector) {
		String oldSelector = selector + " input[type=radio]";
		
		if (driver.getVisibleElements(oldSelector).size() > 0) {
			ui.getFirstVisibleElement(oldSelector).click();
			return true;
		} else if (driver.getVisibleElements(selector).size() > 0) {
			ui.getFirstVisibleElement(selector).click();
			return true;
		}

		return false;
	}

	private void assertPreviewShowing(BaseFile fileA) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		final boolean lightbox = driver.isElementPresent(CommunitiesUI
				.getGalleryLightbox(fileA));
		final boolean viewer = driver.isElementPresent(CommunitiesUIConstants.fileViewer);
		
		logger.weakStep("Verify that lightbox contains Thumbnail in Gallery pops-up once clicked");
		Assert.assertTrue(lightbox || viewer,
				"INFO: Verified lightbox containing Thumbnail in Gallery pops-up once clicked");
	}

	private void closePreview() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String selector = CommunitiesUIConstants.closeViewer;
		
		if (!driver.isElementPresent(selector)) {
			selector = CommunitiesUIConstants.closeThumbnail;
		}
		logger.strongStep("Close Viewer");
		ui.getFirstVisibleElement(selector).click();
	}
}